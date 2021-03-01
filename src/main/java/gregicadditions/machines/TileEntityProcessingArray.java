package gregicadditions.machines;

import gregicadditions.GACapabilities;
import gregicadditions.GAConfig;
import gregicadditions.recipes.*;
import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.capability.*;
import gregtech.api.capability.impl.*;
import gregtech.api.metatileentity.*;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.multiblock.*;
import gregtech.api.multiblock.BlockWorldState;
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
import java.util.function.Predicate;

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
								  .aisle("XMX", "XSX", "XXX")
								  .setAmountAtLeast('L', 12)
								  .setAmountAtMost('M', 1)
								  .where('M', machineHolderPredicate())
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

	public Predicate<BlockWorldState> machineHolderPredicate() {
		return tilePredicate((state, tile) ->
			tile instanceof IMultiblockAbilityPart && ((IMultiblockAbilityPart) tile).getAbility().equals(GACapabilities.PA_MACHINE_CONTAINER)
		);
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

			this.machineItemStack = findMachineStack();
			if(machineItemStack == null) {
				return null;
			}

			MetaTileEntity mte = GregTechAPI.META_TILE_ENTITY_REGISTRY.getObjectById(machineItemStack.getItemDamage());
			if(mte == null) {
				return null;
			}

			//Find the voltage tier of the machine. The machine input bus can only accept ITieredMTEs, so this cast is safe
			this.voltageTier = GTValues.V[((ITieredMetaTileEntity) mte).getTier()];
			//Find the number of machines
			this.numberOfMachines = Math.min(GAConfig.processingArray.processingArrayMachineLimit, machineItemStack.getCount());


			RecipeMap<?> recipeMap = findRecipeMapAndCheckValid(machineItemStack);

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
			Set<ItemStack> countIngredients = new HashSet<>();
			for(int slot = 0; slot < inputs.getSlots(); slot++) {
				ItemStack wholeItemStack = inputs.getStackInSlot(slot);

				// skip empty slots
				if(wholeItemStack.isEmpty())
					continue;

				boolean found = false;
				for(ItemStack i : countIngredients)
					if(ItemStack.areItemsEqual(i, wholeItemStack)) {
						i.setCount(i.getCount() + wholeItemStack.getCount());
						found = true;
						break;
					}

				if(!found)
					countIngredients.add(wholeItemStack.copy());

			}
			return countIngredients;
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

		//Finds the Recipe Map of the passed Machine Stack and checks if it is a valid Recipe Map
		protected RecipeMap findRecipeMapAndCheckValid(ItemStack machineStack) {

			String unlocalizedName = machineStack.getItem().getUnlocalizedNameInefficiently(machineStack);
			String recipeMapName = findRecipeMapName(unlocalizedName);


			//Check the machine against the Config blacklist
			if(!findMachineInBlacklist(recipeMapName)) {

				RecipeMap<?> rmap = RecipeMap.getByName(recipeMapName);

				//Find the RecipeMap of the MTE and ensure that the Processing Array only works on SimpleRecipeBuilders
				//For some reason GTCE has specialized recipe maps for some machines, when it does not need them
				if (rmap != null && (rmap.recipeBuilder() instanceof SimpleRecipeBuilder ||
						rmap.recipeBuilder() instanceof IntCircuitRecipeBuilder ||
						rmap.recipeBuilder() instanceof ArcFurnaceRecipeBuilder ||
						rmap.recipeBuilder() instanceof CutterRecipeBuilder ||
						rmap.recipeBuilder() instanceof UniversalDistillationRecipeBuilder)) {

					return rmap;

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

		public ItemStack findMachineStack() {
			RecipeMapMultiblockController controller = (RecipeMapMultiblockController) this.metaTileEntity;
			List<IMultiblockPart> parts = controller.getMultiblockParts();
			//This should never be null, since it is required for the structure to form.
			MetaTileEntityMachineHolder machineHolder = null;
			for (IMultiblockPart part : parts) {
				if (part instanceof MetaTileEntityMachineHolder) {
					machineHolder = (MetaTileEntityMachineHolder) part;
					break;
				}
			}

			if(machineHolder == null) {
				return null;
			}

			IItemHandlerModifiable machineInventory = machineHolder.getMachineInventory();

			//The machine holder block is always only 1 slot
			ItemStack machine =  machineInventory.getStackInSlot(0);

			if(findRecipeMapAndCheckValid(machine) != null) {
				return machine;
			}

			return null;
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

			ItemStack newMachineStack = findMachineStack();

			boolean dirty = checkRecipeInputsDirty(importInventory, importFluids);
			if(dirty || forceRecipeRecheck) {
				//Check if the machine that the PA is operating on has changed
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
