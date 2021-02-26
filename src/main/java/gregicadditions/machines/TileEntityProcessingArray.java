package gregicadditions.machines;

import gregicadditions.GAConfig;
import gregicadditions.recipes.*;
import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.capability.*;
import gregtech.api.capability.impl.*;
import gregtech.api.metatileentity.*;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.multiblock.*;
import gregtech.api.recipes.*;
import gregtech.api.recipes.Recipe.*;
import gregtech.api.recipes.builders.*;
import gregtech.api.render.*;
import gregtech.api.util.*;
import gregtech.common.blocks.BlockMetalCasing.*;
import gregtech.common.blocks.*;
import net.minecraft.block.state.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.items.*;

import java.util.*;

public class TileEntityProcessingArray extends RecipeMapMultiblockController {

	private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
		MultiblockAbility.IMPORT_ITEMS,
		MultiblockAbility.EXPORT_ITEMS,
		MultiblockAbility.IMPORT_FLUIDS,
		MultiblockAbility.EXPORT_FLUIDS,
		MultiblockAbility.INPUT_ENERGY
	};

	public TileEntityProcessingArray(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, GARecipeMaps.PROCESSING_ARRAY_RECIPES);
		this.recipeMapWorkable = new ProcessingArrayWorkable(this);
	}

	@Override
	protected BlockPattern createStructurePattern() {

		return FactoryBlockPattern.start()
								  .aisle("XXX", "XXX", "XXX")
								  .aisle("XXX", "X#X", "XXX")
								  .aisle("XXX", "XSX", "XXX")
								  .setAmountAtLeast('L', 12)
								  .where('L', statePredicate(getCasingState()))
								  .where('S', selfPredicate())
								  .where('X',
										 statePredicate(getCasingState())
											 .or(abilityPartPredicate(ALLOWED_ABILITIES)))
								  .where('#', isAirPredicate()).build();
	}

	public IBlockState getCasingState() {
		return MetaBlocks.METAL_CASING.getState(MetalCasingType.TUNGSTENSTEEL_ROBUST);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart arg0) {
		return Textures.ROBUST_TUNGSTENSTEEL_CASING;
	}

	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntityProcessingArray(metaTileEntityId);
	}

	protected static class ProcessingArrayWorkable extends MultiblockRecipeLogic {
		long voltageTier;
		int numberOfMachines = 0;
		int numberOfOperations = 0;
		ItemStack machineItemStack = null;

		public ProcessingArrayWorkable(RecipeMapMultiblockController tileEntity) {
			super(tileEntity);
		}

		@Override
		protected Recipe findRecipe(long maxVoltage,
									IItemHandlerModifiable inputs,
									IMultipleTankHandler fluidInputs) {

			RecipeMap<?> recipeMap = findRecipeMap(inputs);

			// No valid recipe map.
			if(recipeMap == null)
				return null;

			Recipe recipe = recipeMap.findRecipe(voltageTier,
												 inputs,
												 fluidInputs,
												 this.getMinTankCapacity(this.getOutputTank()));

			// No matching recipe.
			if(recipe == null)
				return null;

			Set<ItemStack> ingredientStacks = findIngredients(inputs, recipe);
			Map<String, Integer> fluidStacks = findFluid(fluidInputs);

			int itemMultiplier = getMinRatioItem(ingredientStacks, recipe, this.numberOfMachines);
			int fluidMultiplier = getMinRatioFluid(fluidStacks, recipe, this.numberOfMachines);

			int minMultiplier = Math.min(itemMultiplier, fluidMultiplier);

			// No inputs or fluids
			if(minMultiplier == Integer.MAX_VALUE) {
				GTLog.logger.error("Cannot calculate ratio of items for processing array");
				return null;
			}

			List<CountableIngredient> newRecipeInputs = new ArrayList<>();
			List<FluidStack> newFluidInputs = new ArrayList<>();
			List<ItemStack> outputI = new ArrayList<>();
			List<FluidStack> outputF = new ArrayList<>();
			this.multiplyInputsAndOutputs(newRecipeInputs,
										  newFluidInputs,
										  outputI,
										  outputF,
										  recipe,
										  minMultiplier);

			RecipeBuilder<?> newRecipe = recipeMap.recipeBuilder()
												  .inputsIngredients(newRecipeInputs)
												  .fluidInputs(newFluidInputs)
												  .outputs(outputI)
												  .fluidOutputs(outputF)
												  .EUt(recipe.getEUt())
												  .duration(recipe.getDuration());

			copyChancedItemOutputs(newRecipe, recipe, minMultiplier);
			this.numberOfOperations = minMultiplier;
			return newRecipe.build().getResult();
		}

		protected static void copyChancedItemOutputs(RecipeBuilder<?> newRecipe,
													 Recipe oldRecipe,
													 int numberOfOperations) {
			for(ChanceEntry entry : oldRecipe.getChancedOutputs()) {
				int chance = entry.getChance();
				ItemStack itemStack = entry.getItemStack().copy();
				int boost = entry.getBoostPerTier();
				itemStack.setCount(itemStack.getCount() * numberOfOperations);

				newRecipe.chancedOutput(itemStack, chance, boost);
			}
		}

		protected static Set<ItemStack> findIngredients(IItemHandlerModifiable inputs, Recipe recipe) {
			Set<ItemStack> countedIngredients = new HashSet<>();

			//Convert the CountableIngredient recipe Inputs to an ItemStack list. This is safe because at this point, the recipe
			//is already known not to be null
			List<ItemStack> itemStackList = new ArrayList<>();
			recipe.getInputs().forEach(ingredient -> {
				int count = ingredient.getCount();
				ItemStack itemStack = null;
				int meta = 0;
				ItemStack[] matching = ingredient.getIngredient().getMatchingStacks();
				if (matching.length > 0) {
					for (ItemStack stack : matching) {
						if (stack != null) {
							itemStack = stack;
							meta = stack.getMetadata();
							break;
						}
					}
					itemStackList.add(new ItemStack(itemStack.getItem(), count, meta));
				}
			});

			//Iterate over the input inventory, to match items in the input inventory to the recipe items
			for(int slot = 0; slot < inputs.getSlots(); slot++) {
				ItemStack wholeItemStack = inputs.getStackInSlot(slot);

				//Skips empty slots in the input inventory, and slots that don't contain a recipe ingredient
				//This means that the machine stack and non Consumed inputs are not added to the Ingredients list
				if(wholeItemStack.isEmpty() || !itemStackList.stream().anyMatch(stack -> areItemStacksEqual(stack, wholeItemStack))) {
					continue;
				}

				//If there is nothing in the ingredient list, add the current item
				if(countedIngredients.isEmpty()) {
					countedIngredients.add(wholeItemStack.copy());
				}
				//Increment count of current items, or add new items
				else {
					Iterator<ItemStack> ciIterator = countedIngredients.iterator();

					while(ciIterator.hasNext()) {
						ItemStack stack = ciIterator.next();

						if(countedIngredients.contains(stack)) {
							stack.setCount(stack.getCount() + wholeItemStack.getCount());
						}
						else {
							countedIngredients.add(stack.copy());
						}

					}
				}

			}
			return countedIngredients;
		}

		protected int getMinRatioItem(Set<ItemStack> countIngredients,
									  Recipe recipe,
									  int numberOfMachines) {

			int minMultiplier = Integer.MAX_VALUE;
			for(CountableIngredient recipeInputs : recipe.getInputs()) {

				if(recipeInputs.getCount() == 0)
					continue;

				for(ItemStack wholeItemStack : countIngredients) {

					if(recipeInputs.getIngredient().apply(wholeItemStack)) {
						int ratio = Math.min(numberOfMachines, wholeItemStack.getCount() / recipeInputs.getCount());
						if(ratio < minMultiplier)
							minMultiplier = ratio;
						break;
					}

				}
			}
			return minMultiplier;
		}

		protected static Map<String, Integer> findFluid(IMultipleTankHandler fluidInputs) {

			Map<String, Integer> countFluid = new HashMap<>();
			for(IFluidTank tank : fluidInputs)
				if(tank.getFluid() != null) {

					String name = tank.getFluid().getUnlocalizedName();

					if(countFluid.containsKey(name)) {
						int existingValue = countFluid.get(name);
						countFluid.put(name, existingValue + tank.getFluidAmount());
					} else
						countFluid.put(name, tank.getFluidAmount());
				}
			return countFluid;
		}

		protected int getMinRatioFluid(Map<String, Integer> countFluid,
									   Recipe recipe,
									   int numberOfMachines) {

			int minMultiplier = Integer.MAX_VALUE;
			for(FluidStack fs : recipe.getFluidInputs()) {
				String name = fs.getFluid().getUnlocalizedName();
				int ratio = Math.min(numberOfMachines, countFluid.get(name) / fs.amount);

				if(ratio < minMultiplier)
					minMultiplier = ratio;
			}
			return minMultiplier;
		}

		protected static ItemStack copyItemStackWithCount(ItemStack itemStack, int count) {
			ItemStack itemCopy = itemStack.copy();
			itemCopy.setCount(count);
			return itemCopy;
		}

		protected static FluidStack copyFluidStackWithAmount(FluidStack fluidStack, int count) {
			FluidStack fluidCopy = fluidStack.copy();
			fluidCopy.amount = count;
			return fluidCopy;
		}

		protected void multiplyInputsAndOutputs(List<CountableIngredient> newRecipeInputs,
												List<FluidStack> newFluidInputs,
												List<ItemStack> outputItems,
												List<FluidStack> outputFluids,
												Recipe recipe,
												int numberOfOperations) {

			recipe.getInputs().forEach(ci ->
					newRecipeInputs.add(new CountableIngredient(ci.getIngredient(),
																ci.getCount() * numberOfOperations)));

			recipe.getFluidInputs().forEach(fluidStack ->
					newFluidInputs.add(new FluidStack(fluidStack.getFluid(),
													  fluidStack.amount * numberOfOperations)));

			recipe.getOutputs().forEach(itemStack ->
				outputItems.add(copyItemStackWithCount(itemStack,
													   itemStack.getCount() * numberOfOperations)));

			recipe.getFluidOutputs().forEach(fluidStack ->
				outputFluids.add(copyFluidStackWithAmount(fluidStack,
														  fluidStack.amount * numberOfOperations)));
		}

		protected RecipeMap findRecipeMap(IItemHandlerModifiable inputs) {

			for(int slot = 0; slot < inputs.getSlots(); slot++) {

				ItemStack wholeItemStack = inputs.getStackInSlot(slot);
				String unlocalizedName = wholeItemStack.getItem().getUnlocalizedNameInefficiently(wholeItemStack);
				String recipeMapName = findRecipeMapName(unlocalizedName);


				//Use Unlocalized name checks to prevent false positives from any item with metadata
				if((unlocalizedName.contains("gregtech.machine") || unlocalizedName.contains("gtadditions.machine")) &&
						!findMachineInBlacklist(recipeMapName) &&
						GregTechAPI.META_TILE_ENTITY_REGISTRY.getObjectById(wholeItemStack.getItemDamage()) != null) {

					MetaTileEntity mte = GregTechAPI.META_TILE_ENTITY_REGISTRY.getObjectById(wholeItemStack.getItemDamage());

					//All MTEs tested should have tiers, this stops Multiblocks from working in the PA
					if(mte instanceof TieredMetaTileEntity) {

						RecipeMap<?> rmap = RecipeMap.getByName(recipeMapName);

						//Find the RecipeMap of the MTE and ensure that the Processing Array only works on SimpleRecipeBuilders
						//For some reason GTCE has specialized recipe maps for some machines, when it does not need them
						if (rmap != null && (rmap.recipeBuilder() instanceof SimpleRecipeBuilder ||
								rmap.recipeBuilder() instanceof IntCircuitRecipeBuilder ||
								rmap.recipeBuilder() instanceof ArcFurnaceRecipeBuilder ||
								rmap.recipeBuilder() instanceof CutterRecipeBuilder ||
								rmap.recipeBuilder() instanceof UniversalDistillationRecipeBuilder)) {
							//Find the voltage tier of the machine
							this.voltageTier = GTValues.V[((TieredMetaTileEntity) mte).getTier()];
							//Find the number of machines
							this.numberOfMachines = Math.min(GAConfig.processingArray.processingArrayMachineLimit, wholeItemStack.getCount());
							//The machine Item Stack. Is this needed if we remove the machine from being found in the ingredients?
							this.machineItemStack = wholeItemStack;

							return rmap;

						}

					}
				}
			}
			return null;
		}

		//A similar method to findRecipeMap, but instead returns the MTE the PA will be using for recipes.
		//Using for checking if the recipe could have changed.
		protected ItemStack findValidMachine(IItemHandlerModifiable inputs) {

			for(int slot = 0; slot < inputs.getSlots(); slot++) {

				ItemStack wholeItemStack = inputs.getStackInSlot(slot);
				String unlocalizedName = wholeItemStack.getItem().getUnlocalizedNameInefficiently(wholeItemStack);
				String recipeMapName = findRecipeMapName(unlocalizedName);


				//Use Unlocalized name checks to prevent false positives from any item with metadata
				if((unlocalizedName.contains("gregtech.machine") || unlocalizedName.contains("gtadditions.machine")) &&
						!findMachineInBlacklist(recipeMapName) &&
						GregTechAPI.META_TILE_ENTITY_REGISTRY.getObjectById(wholeItemStack.getItemDamage()) != null) {

					MetaTileEntity mte = GregTechAPI.META_TILE_ENTITY_REGISTRY.getObjectById(wholeItemStack.getItemDamage());

					//All MTEs tested should have tiers, this stops Multiblocks from working in the PA
					if(mte instanceof TieredMetaTileEntity) {

						RecipeMap<?> rmap = RecipeMap.getByName(recipeMapName);

						//Find the RecipeMap of the MTE and ensure that the Processing Array only works on SimpleRecipeBuilders
						//For some reason GTCE has specialized recipe maps for some machines, when it does not need them
						if (rmap != null && (rmap.recipeBuilder() instanceof SimpleRecipeBuilder ||
								rmap.recipeBuilder() instanceof IntCircuitRecipeBuilder ||
								rmap.recipeBuilder() instanceof ArcFurnaceRecipeBuilder ||
								rmap.recipeBuilder() instanceof CutterRecipeBuilder ||
								rmap.recipeBuilder() instanceof UniversalDistillationRecipeBuilder)) {

							return wholeItemStack;

						}

					}
				}
			}
			return null;
		}

		public String findRecipeMapName(String unlocalizedName) {

			String trimmedName = unlocalizedName.substring(0, unlocalizedName.lastIndexOf("."));
			trimmedName = trimmedName.substring(trimmedName.lastIndexOf(".") + 1);

			//For some reason, the Cutting saw's machine name does not match the recipe map's unlocalized name, so correct it
			//Same with the Electric Furnace
			if(trimmedName.equals("cutter")) {
				trimmedName = "cutting_saw";
			}
			else if(trimmedName.equals("electric_furnace")) {
				trimmedName = "furnace";
			}

			return trimmedName;
		}

		public boolean findMachineInBlacklist(String unlocalizedName) {

			String[] blacklist = GAConfig.processingArray.machineBlackList;

			return Arrays.asList(blacklist).contains(unlocalizedName);
		}

		@Override
		protected boolean setupAndConsumeRecipeInputs(Recipe recipe) {

			IItemHandlerModifiable importInventory = getInputInventory();
			IItemHandlerModifiable exportInventory = getOutputInventory();
			IMultipleTankHandler importFluids = getInputTank();
			IMultipleTankHandler exportFluids = getOutputTank();

			//Format: EU/t, duration
			int[] resultOverclock = calculateOverclock(recipe.getEUt(), voltageTier, recipe.getDuration());
			int totalEUt = resultOverclock[0] * resultOverclock[1] * this.numberOfOperations;

			boolean enoughPower;
			if(totalEUt >= 0) {
				int capacity;
				if(totalEUt > getEnergyCapacity() / 2)
					capacity = resultOverclock[0];
				else
					capacity = totalEUt;
				enoughPower = getEnergyStored() >= capacity;
			} else {
				int power = resultOverclock[0] * this.numberOfOperations;
				enoughPower = getEnergyStored() - (long) power <= getEnergyCapacity();
			}

			if(!enoughPower)
				return false;

			return MetaTileEntity.addItemsToItemHandler(exportInventory,
														true,
														recipe.getAllItemOutputs(exportInventory.getSlots())) &&
				MetaTileEntity.addFluidsToFluidHandler(exportFluids, true, recipe.getFluidOutputs()) &&
				recipe.matches(true, importInventory, importFluids);
		}

		@Override
		protected void trySearchNewRecipe() {
			long maxVoltage = getMaxVoltage();
			Recipe currentRecipe = null;
			IItemHandlerModifiable importInventory = getInputInventory();
			IMultipleTankHandler importFluids = getInputTank();

			boolean dirty = checkRecipeInputsDirty(importInventory, importFluids);
			if(dirty || forceRecipeRecheck) {
				//Check if the machine that the PA is operating on has changed
				ItemStack newMachineStack = findValidMachine(importInventory);
				if (newMachineStack == null || this.machineItemStack == null || !areItemStacksEqual(machineItemStack, newMachineStack)) {
					previousRecipe = null;
				}
			}

			if(previousRecipe != null &&
					previousRecipe.matches(false, importInventory, importFluids)) {
				currentRecipe = previousRecipe;
			}
			else {
				//If the previous recipe was null, or does not match the current recipe, search for a new recipe
				currentRecipe = findRecipe(maxVoltage, importInventory, importFluids);

				//Update the previous recipe
				if(currentRecipe != null) {
					this.previousRecipe = currentRecipe;
				}

				this.forceRecipeRecheck = false;
			}

			//Attempts to run the current recipe, if it is not null
			if(currentRecipe != null && setupAndConsumeRecipeInputs(currentRecipe)) {
				setupRecipe(currentRecipe);
			}
		}

		@Override
		protected void setupRecipe(Recipe recipe) {
			int[] resultOverclock = calculateOverclock(recipe.getEUt(), voltageTier, recipe.getDuration());
			this.progressTime = 1;
			setMaxProgress(resultOverclock[1]);
			this.recipeEUt = resultOverclock[0] * this.numberOfOperations;
			this.fluidOutputs = GTUtility.copyFluidList(recipe.getFluidOutputs());
			int tier = getMachineTierForRecipe(recipe);
			setActive(true);
			this.itemOutputs = GTUtility.copyStackList(recipe.getResultItemOutputs(getOutputInventory().getSlots(),
																				   random,
																				   tier));

			if(this.wasActiveAndNeedsUpdate)
				this.wasActiveAndNeedsUpdate = false;
			else
				setActive(true);
		}
	}
}
