package gregicadditions.machines;

import gregicadditions.*;
import gregicadditions.recipes.*;
import gregtech.api.*;
import gregtech.api.block.machines.*;
import gregtech.api.capability.*;
import gregtech.api.capability.impl.*;
import gregtech.api.gui.*;
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
import gregtech.common.metatileentities.electric.*;
import gregtech.common.sound.GTSoundEvents;
import it.unimi.dsi.fastutil.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.state.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.items.*;

import javax.annotation.*;
import java.util.Arrays;
import java.util.*;
import java.util.function.Function;
import java.util.function.*;
import java.util.stream.*;

import static gregtech.api.gui.widgets.AdvancedTextWidget.*;
import static gregtech.api.util.Predicates.*;

public class TileEntityProcessingArray extends RecipeMapMultiblockController {
	/**
	 * Indicates whether the machine stack has been changed.
	 * Will remain true if it happens during a recipe run, otherwise cleared during the next recipe setup.
	 */
	private boolean machineChanged = true;

	/**
	 * Cached details about the machines used in the active recipe. Persists until the recipe is completed,
	 * even if the structure is deformed and reformed, or the machine stack is modified.
 	 */
	private MachineStats activeRecipeMachineStats;

	private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
		MultiblockAbility.IMPORT_ITEMS,
		MultiblockAbility.EXPORT_ITEMS,
		MultiblockAbility.IMPORT_FLUIDS,
		MultiblockAbility.EXPORT_FLUIDS,
		MultiblockAbility.INPUT_ENERGY,
		GACapabilities.PA_MACHINE_CONTAINER
	};

	protected boolean isDistinctInputBusMode = false;

	public TileEntityProcessingArray(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, GARecipeMaps.PROCESSING_ARRAY_RECIPES);
		this.recipeMapWorkable = new ProcessingArrayWorkable(this);
	}

	/** Payload for recipe map packet when the map is null */
	private static final String NONE = "NONE";

	/** Max length of Recipe Map name payload in custom data packet */
	private static final int MAP_NAME_LENGTH = 512;

	@Override
	@Nullable
	public SoundEvent getSound() {
		return ((ProcessingArrayWorkable)this.recipeMapWorkable).getSound();
	}

	@Override
	protected void updateFormedValid() {
		super.updateFormedValid();
		((ProcessingArrayWorkable) recipeMapWorkable).findMachineStack();
	}

	@Override
	protected BlockPattern createStructurePattern() {

		return FactoryBlockPattern.start()
		                          .aisle("XXX", "XXX", "XXX")
		                          .aisle("XXX", "X#X", "XXX")
		                          .aisle("XXX", "XSX", "XXX")
		                          .setAmountAtLeast('L', 12)
		                          .setAmountLimit('M', 1, 1)
		                          .where('M', abilityPartPredicate(GACapabilities.PA_MACHINE_CONTAINER))
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

	protected MultiblockRecipeLogic getWorkable() {
		return recipeMapWorkable;
	}

	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);

		// if the structure isn't formed, no need to add extra text
		if(!isStructureFormed())
			return;

		// If a recipe is running
		if(activeRecipeMachineStats != null) {
			// display the recipe's stats
			textList.add(new TextComponentTranslation("gtadditions.multiblock.processing_array.recipe",
						 new TextComponentTranslation("recipemap." + activeRecipeMachineStats.recipeMap.unlocalizedName + ".name"),
						 GTValues.VN[activeRecipeMachineStats.machineTier],
						 activeRecipeMachineStats.parallels));

			// If jammed, display the detected machine (if any)
			if(getWorkable().isJammed()) {
				MachineStats detectedStats = getDetectedMachineStats();

				// Only show if machines are the issue, otherwise it would be confusing
				if(detectedStats == null)
					// "N/A"
					textList.add(new TextComponentTranslation("gtadditions.multiblock.processing_array.detected.no"));
				else if(!detectedStats.satisfies(activeRecipeMachineStats))
					textList.add(new TextComponentTranslation("gtadditions.multiblock.processing_array.detected.yes",
								 new TextComponentTranslation("recipemap." + detectedStats.recipeMap.unlocalizedName + ".name"),
								 GTValues.VN[detectedStats.machineTier],
								 detectedStats.parallels));
			}
		}

		final boolean isDistinctModeAvailable = inputInventory.getSlots() > 0;

		// Display a clickable toggle button with accompanying hint text
		if(isDistinctModeAvailable) {
			final String modeTranslationKey = "gtadditions.multiblock.processing_array.distinct." +
				(isDistinctInputBusMode ? "yes" : "no");
			textList.add(makeDistinctModeToggleButton(modeTranslationKey));
			textList.add(new TextComponentTranslation("gtadditions.multiblock.processing_array.distinct2",
			                                          new TextComponentTranslation(modeTranslationKey)));
		}
		else
			textList.add(makeDistinctModeUnavailableTextComponent());
	}

	/**
	 * @return information about the contents of the machine holder, or {@code null} if the holder is empty.
	 */
	@Nullable
	private MachineStats getDetectedMachineStats() {
		ItemStack stack = getAbilities(GACapabilities.PA_MACHINE_CONTAINER).get(0).getStackInSlot(0);

		if(stack == ItemStack.EMPTY)
			return null;

		RecipeMap<?> rmap = ProcessingArrayWorkable.findRecipeMapAndCheckValid(stack);
		if(rmap == null)
			return null;
		MetaTileEntity mte = MachineItemBlock.getMetaTileEntity(stack);
		//Find the voltage tier of the machine.
		int machineTier = 0;
		if(mte instanceof ITieredMetaTileEntity tmte)
			machineTier = tmte.getTier();

		return new MachineStats(machineTier, stack.getCount(), rmap);
	}

	private ITextComponent makeDistinctModeToggleButton(String translationKey) {
		ITextComponent label = new TextComponentTranslation("gtadditions.multiblock.processing_array.distinct");
		ITextComponent modeButton = withButton(new TextComponentTranslation(translationKey), "distinct");
		withHoverTextTranslate(modeButton, "gtadditions.multiblock.processing_array.distinct.info");
		return label.appendText(" ").appendSibling(modeButton);
	}

	private ITextComponent makeDistinctModeUnavailableTextComponent() {
		ITextComponent label = new TextComponentTranslation("gtadditions.multiblock.processing_array.distinct");
		ITextComponent modeText = new TextComponentTranslation("gtadditions.multiblock.processing_array.distinct.disabled");
		withHoverTextTranslate(modeText, "gtadditions.multiblock.processing_array.distinct.no_bus");
		return label.appendText(" ").appendSibling(modeText);
	}

	@Override
	protected void handleDisplayClick(String componentData, Widget.ClickData clickData) {
		super.handleDisplayClick(componentData, clickData);
		isDistinctInputBusMode = !isDistinctInputBusMode;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setBoolean("Distinct", isDistinctInputBusMode);

		// Serialize the MachineStats details so it works across reloads
		if(activeRecipeMachineStats != null) {
			NBTTagCompound activeRecipeTag = new NBTTagCompound();
			activeRecipeTag.setString("RecipeMap", activeRecipeMachineStats.recipeMap.unlocalizedName);
			activeRecipeTag.setInteger("Tier", activeRecipeMachineStats.machineTier);
			activeRecipeTag.setInteger("Parallels", activeRecipeMachineStats.parallels);
			data.setTag("ActiveRecipe", activeRecipeTag);
		}
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		isDistinctInputBusMode = data.getBoolean("Distinct");

		// Deserialize active recipe if present
		if(data.hasKey("ActiveRecipe")) {
			NBTTagCompound activeRecipeTag = data.getCompoundTag("ActiveRecipe");

			RecipeMap<?> recipeMap = RecipeMap.getByName(activeRecipeTag.getString("RecipeMap"));
			if(recipeMap != null)
				activeRecipeMachineStats = new MachineStats(activeRecipeTag.getInteger("Tier"),
															activeRecipeTag.getInteger("Parallels"),
															recipeMap);
		}
	}

	@Override
	public void writeInitialSyncData(PacketBuffer buf) {
		super.writeInitialSyncData(buf);
		buf.writeBoolean(isDistinctInputBusMode);

		if(recipeMapWorkable instanceof ProcessingArrayWorkable paw && paw.recipeMap != null)
			buf.writeString(paw.recipeMap.unlocalizedName);
		else
			buf.writeString(NONE);
	}

	@Override
	public void receiveInitialSyncData(PacketBuffer buf) {
		super.receiveInitialSyncData(buf);
		this.isDistinctInputBusMode = buf.readBoolean();
		if(this.recipeMapWorkable instanceof ProcessingArrayWorkable paw)
			paw.recipeMap = RecipeMap.getByName(buf.readString(MAP_NAME_LENGTH));
	}

	@Override
	public void invalidateStructure() {
		super.invalidateStructure();
		((ProcessingArrayWorkable) this.recipeMapWorkable).invalidate();
	}

	public void notifyMachineChanged() {
		getWorkable().invalidate();
	}

	@Override
	public boolean checkRecipe(Recipe recipe, boolean consumeIfSuccess) {
		// if stats are null here, this is a recipe started in SoG 3.0.0 or earlier.
		// Let it proceed to completion for now, and the next run will have the data available.
		if(activeRecipeMachineStats == null)
			return true;

		// if the structure is intact we don't need to do any additional checks
		if (!machineChanged)
			return true;

		// If the structure was broken and reformed, we need to see if the current machines are suitable
		MachineStats stats = getDetectedMachineStats();

		// No machine, can't proceed
		if(stats == null)
			return false;

		// Proceed if the detected stack is sufficient to complete the ongoing recipe
		return stats.satisfies(activeRecipeMachineStats);
	}

	protected class ProcessingArrayWorkable extends MultiblockRecipeLogic {
		/** The voltage this machine operates at */
		long machineVoltage;
		/** The GTValues.V tier ordinal for the machine's tier */
		int machineTier;
		int numberOfMachines = 0;
		int numberOfOperations = 0;
		ItemStack machineItemStack = ItemStack.EMPTY;
		RecipeMap<?> recipeMap = null;

		/** dataID for recipe map update packets, to sync with client */
		private static final int RECIPEMAP_CHANGED = -999;

		// Stuff for Distinct Mode
		/** Index of the last bus used for distinct mode */
		int lastRecipeIndex = 0;
		/** Records invalidated inputs for Distinct Mode logic */
		List<IItemHandlerModifiable> invalidatedInputList = new ArrayList<>();

		public ProcessingArrayWorkable(RecipeMapMultiblockController tileEntity) {
			super(tileEntity);
		}

		@Override
		@Nullable
		public SoundEvent getSound() {
			if (isActive && (isJammed || hasNotEnoughEnergy))
				return GTSoundEvents.INTERRUPTED;
			if (this.recipeMap != null)
				return this.recipeMap.getSound();
			return null;
		}

		@Override
		public void invalidate() {
			super.invalidate();
			isOutputsFull = false;
			lastRecipeIndex = 0;
			invalidatedInputList.clear();
			machineItemStack = ItemStack.EMPTY;
			machineChanged = true;
			machineTier = 0;
			machineVoltage = 0L;
			recipeMap = null;
		}

		@Override
		protected boolean shouldSearchForRecipes() {
			return canWorkWithMachines() && super.shouldSearchForRecipes();
		}

		/**
		 * Determines whether the machines in the holder are usable.<br />
		 * If the machine stack is dirty, it will perform validation first.
		 *
		 * @return {@code true} if the current machine stack is usable.
		 */
		public boolean canWorkWithMachines() {
			if(machineChanged) {
				findMachineStack();
				machineChanged = false;
				previousRecipe = null;
				if(isDistinctInputBusMode) {
					invalidatedInputList.clear();
				} else {
					invalidInputsForRecipes = false;
				}
			}

			return (!machineItemStack.isEmpty() && this.recipeMap != null);
		}

		/*
		  Overridden solely to update the machine stack and the recipe map at an early point.
		  Recipe multiplication will come at a later time.
		*/
		@Override
		protected Recipe findRecipe(long maxVoltage,
		                            IItemHandlerModifiable inputs,
		                            IMultipleTankHandler fluidInputs) {

			// Avoid crashing during load, when GTCE initializes its multiblock previews
			if(machineItemStack.isEmpty() || this.recipeMap == null) {
				return null;
			}

			return this.recipeMap.findRecipe(Math.min(this.machineVoltage, maxVoltage),
			                                 inputs,
			                                 fluidInputs,
			                                 this.getMinTankCapacity(this.getOutputTank()));
		}


		protected Recipe multiplyRecipe(IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs, Recipe recipe, ItemStack machineStack, RecipeMap<?> rmap) {
			//Check if passed a null recipemap or machine stack
			if(rmap == null || machineStack == null) {
				return null;
			}

			//Find the number of machines
			this.numberOfMachines = Math.min(GAConfig.processingArray.processingArrayMachineLimit, machineStack.getCount());

			Set<ItemStack> ingredientStacks = findAllItemsInInputs(inputs);
			Map<String, Integer> fluidStacks = findAllFluidsInInputs(fluidInputs);

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

			RecipeBuilder<?> newRecipe = rmap.recipeBuilder()
			                                 .inputsIngredients(newRecipeInputs)
			                                 .fluidInputs(newFluidInputs)
			                                 .outputs(outputI)
			                                 .fluidOutputs(outputF)
			                                 .EUt(recipe.getEUt())
			                                 .duration(recipe.getDuration());

			//Don't allow MV or LV macerators to have chanced outputs, because they do not have the slots for chanced
			MetaTileEntity mte = MachineItemBlock.getMetaTileEntity(machineStack);
			if(!(mte instanceof MetaTileEntityMacerator && this.machineTier < GTValues.HV))
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

		protected List<IItemHandlerModifiable> getInputBuses() {
			RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
			return controller.getAbilities(MultiblockAbility.IMPORT_ITEMS);
		}

		protected static Set<ItemStack> findAllItemsInInputs(IItemHandlerModifiable inputs) {
			Hash.Strategy<ItemStack> strategy = ItemStackHashStrategy.comparingAllButCount();
			final Supplier<Map<ItemStack, Integer>> mapSupplier =
				() -> new Object2IntOpenCustomHashMap<>(strategy);

			final Set<ItemStack> result = new ObjectOpenCustomHashSet<>(strategy);

			StreamUtils.streamFrom(inputs)
			           // keep only non-empty item stacks
			           .filter(not(ItemStack::isEmpty))
			           // Track the number of identical items
			           .collect(Collectors.toMap(Function.identity(),
			                                     ItemStack::getCount,
			                                     Math::addExact,
			                                     mapSupplier))
			           // Create a single stack of the combined count for each item
			           .entrySet().stream()
			           .map(entry -> {
				           ItemStack combined = entry.getKey().copy();
				           combined.setCount(entry.getValue());
				           return combined;
			           })
			           .forEach(result::add);
			return result;
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

		protected static Map<String, Integer> findAllFluidsInInputs(IMultipleTankHandler fluidInputs) {

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
		public static RecipeMap<?> findRecipeMapAndCheckValid(ItemStack machineStack) {

			if(machineStack == null || machineStack.isEmpty()) {
				return null;
			}

			String unlocalizedName = machineStack.getItem().getUnlocalizedNameInefficiently(machineStack);
			String recipeMapName = findRecipeMapName(unlocalizedName);


			//Check the machine against the Config blacklist
			if(!findMachineInBlacklist(recipeMapName)) {

				RecipeMap<?> rmap = RecipeMap.getByName(recipeMapName);

				if(rmap == null) {
					return null;
				}

				RecipeBuilder<?> rbuilder = rmap.recipeBuilder();

				//Find the RecipeMap of the MTE and ensure that the Processing Array only works on SimpleRecipeBuilders
				//For some reason GTCE has specialized recipe maps for some machines, when it does not need them
				if (rbuilder instanceof SimpleRecipeBuilder ||
					rbuilder instanceof IntCircuitRecipeBuilder ||
					rbuilder instanceof ArcFurnaceRecipeBuilder ||
					rbuilder instanceof CutterRecipeBuilder ||
					rbuilder instanceof UniversalDistillationRecipeBuilder) {

					return rmap;
				}
			}
			return null;
		}

		protected static String findRecipeMapName(String unlocalizedName) {

			String trimmedName = unlocalizedName.substring(0, unlocalizedName.lastIndexOf("."));
			trimmedName = trimmedName.substring(trimmedName.lastIndexOf(".") + 1);

			//Catch some cases where the machine's name is not the same as its recipe map's name
			trimmedName = switch(trimmedName) {
				case "cutter" -> "cutting_saw";
				case "electric_furnace" -> "furnace";
				case "ore_washer" -> "orewasher";
				case "brewery" -> "brewer";
				default -> trimmedName;
			};

			return trimmedName;
		}

		protected static boolean findMachineInBlacklist(String unlocalizedName) {

			String[] blacklist = GAConfig.processingArray.machineBlackList;

			return Arrays.asList(blacklist).contains(unlocalizedName);
		}

		@Override
		public void receiveCustomData(int dataId, PacketBuffer buf) {
			super.receiveCustomData(dataId, buf);
			if(dataId == RECIPEMAP_CHANGED)
				recipeMap = RecipeMap.getByName(buf.readString(MAP_NAME_LENGTH));
		}

		public void findMachineStack() {
			RecipeMapMultiblockController controller = (RecipeMapMultiblockController) this.metaTileEntity;

			//The Processing Array is limited to 1 Machine Interface per multiblock, and only has 1 slot
			ItemStack currentMachine = controller.getAbilities(GACapabilities.PA_MACHINE_CONTAINER).get(0).getStackInSlot(0);

			if (currentMachine.isEmpty()) {
				invalidate();
				return;
			}

			if (!ItemStack.areItemStacksEqual(this.machineItemStack, currentMachine)) {
				RecipeMap<?> rmap = findRecipeMapAndCheckValid(currentMachine);

				MetaTileEntity mte = MachineItemBlock.getMetaTileEntity(currentMachine);

				//Find the voltage tier of the machine.
				this.machineTier = mte instanceof ITieredMetaTileEntity tmte ? tmte.getTier() : 0;

				this.machineVoltage = GTValues.V[this.machineTier];

				// invalidate the previous recipe
				previousRecipe = null;

				//we make a copy here to account for changes in the amount of machines in the hatch
				this.machineItemStack = currentMachine.copy();
				this.recipeMap = rmap;

				// Send packet to client, so it knows what recipe map is loaded
				writeCustomData(RECIPEMAP_CHANGED, buf -> buf.writeString(rmap.unlocalizedName));
			}
		}

		/**
		 * Sets up and consumes recipe inputs, considering all inputs. Used when Distinct Bus Mode is disabled.
		 * @param recipe    the recipe to prepare to run
		 * @return {@code true} if the recipe was successfully set up and ingredients consumed, or
		 *         {@code false} if the recipe could not be configured and no work was done.
		 * @see #setupAndConsumeRecipeInputs(Recipe recipe, IItemHandlerModifiable importInventory)
		 */
		@Override
		protected boolean setupAndConsumeRecipeInputs(Recipe recipe) {
			// use all input buses
			return setupAndConsumeRecipeInputs(recipe, getInputInventory());
		}

		/**
		 * Sets up and consumes recipe inputs targeting a predetermined input bus. Used for Distinct Bus mode.
		 * @param recipe    the recipe to prepare to run
		 * @param index     the index of the predetermined index bus
		 * @return {@code true} if the recipe was successfully set up and ingredients consumed, or
		 *         {@code false} if the recipe could not be configured and no work was done.
		 * @see #setupAndConsumeRecipeInputs(Recipe recipe, IItemHandlerModifiable importInventory)
		 */
		protected boolean setupAndConsumeRecipeInputs(Recipe recipe, int index) {
			// use the specified input bus
			return setupAndConsumeRecipeInputs(recipe, getInputBuses().get(index));
		}

		/**
		 * If possible, consumes the ingredients for a recipe from the target inventory in preparation for starting the
		 * craft.
		 *
		 * @param recipe          the recipe to prepare to run
		 * @param importInventory the inventory to check for ingredients
		 * @return {@code true} if the recipe was successfully set up and ingredients consumed, or
		 *         {@code false} if the recipe could not be configured and no work was done.
		 */
		protected boolean setupAndConsumeRecipeInputs(Recipe recipe, IItemHandlerModifiable importInventory) {
			IItemHandlerModifiable exportInventory = getOutputInventory();
			IMultipleTankHandler importFluids = getInputTank();
			IMultipleTankHandler exportFluids = getOutputTank();

			// Check if there's enough energy to even start this recipe
			if(!haveEnoughPowerToProceed(recipe, machineVoltage, this.numberOfOperations))
				return false;

			// Ensure there's enough room for items, otherwise mark outputs read and bail out
			if(!MetaTileEntity.addItemsToItemHandler(exportInventory, true, recipe.getAllItemOutputs(Integer.MAX_VALUE))) {
				this.isOutputsFull = true;
				return false;
			}

			// Ensure there's enough room for fluids, otherwise mark outputs read and bail out
			if(!MetaTileEntity.addFluidsToFluidHandler(exportFluids, true, recipe.getFluidOutputs())) {
				this.isOutputsFull = true;
				return false;
			}

			this.isOutputsFull = false;
			return recipe.matches(true, importInventory, importFluids);
		}

		/**
		 * Determines if there is sufficient energy buffer to proceed with running a parallelized recipe overclocked to
		 * a given tier.
		 *
		 * @param recipe        the Recipe to perform
		 * @param voltageTier   the voltage tier to overclock to in the computations
		 * @param numOperations the number of times this recipe is to be multiplied by
		 * @return {@code true} if there is enough energy to proceed, {@code false} otherwise.
		 */
		protected boolean haveEnoughPowerToProceed(Recipe recipe, long voltageTier, int numOperations) {
			//Format: EU/t, duration
			int[] resultOverclock = calculateOverclock(recipe.getEUt(), voltageTier, recipe.getDuration());
			int totalEU = resultOverclock[0] * resultOverclock[1] * numOperations;
			int EUt = resultOverclock[0] * numOperations;

			boolean enoughPower;
			if(totalEU >= 0) {
				int capacity;
				if(totalEU > getEnergyCapacity() / 2)
					capacity = EUt;
				else
					capacity = totalEU;
				enoughPower = getEnergyStored() >= capacity;
			} else {
				int power = EUt * numOperations;
				enoughPower = getEnergyStored() - (long) power <= getEnergyCapacity();
			}

			return enoughPower;
		}

		private boolean useDistinctLogic() {
			return metaTileEntity instanceof TileEntityProcessingArray tepa &&
				   tepa.isDistinctInputBusMode &&
				   getInputInventory().getSlots() > 0;
		}

		@Override
		protected boolean canWorkWithInputs() {
			return useDistinctLogic() ? canWorkWithDistinctInputs() : super.canWorkWithInputs();
		}

		/**
		 * Determines if any input buses have changed since the last distinct mode recipe check.
		 * All such buses are marked valid and the change notification cleared.
		 *
		 * @return {@code true} if any buses were validated during this check.
		 */
		private boolean validateChangedInputBuses() {
			boolean anyValidated = false;

			// Iterate over all input buses that have reported a change
			Iterator<IItemHandlerModifiable> notifiedIter = metaTileEntity.getNotifiedItemInputList().iterator();
			while (notifiedIter.hasNext()) {
				IItemHandlerModifiable bus = notifiedIter.next();

				// Find and validate this bus, if it is currently listed as invalid
				Iterator<IItemHandlerModifiable> invalidatedIter = invalidatedInputList.iterator();
				while (invalidatedIter.hasNext()) {
					IItemHandler invalidatedHandler = invalidatedIter.next();

					// recurse into handler lists; we need the specific handler
					if (invalidatedHandler instanceof ItemHandlerList ihl) {
						for (IItemHandler ih : ihl.getBackingHandlers()) {
							if (ih == bus) {
								anyValidated = true;
								invalidatedIter.remove();
								break;
							}
						}
					} else if (invalidatedHandler == bus) {
						anyValidated = true;
						invalidatedIter.remove();
					}
				}
				notifiedIter.remove();
			}

			return anyValidated;
		}

		/**
		 * Handles the logic for determining whether work can be done in distinct mode
		 */
		protected boolean canWorkWithDistinctInputs() {
			// if we haven't marked any buses invalid, proceed
			if (invalidatedInputList.isEmpty())
				return true;

			// If a fluid input changed, clear notifications/invalidations and proceed from scratch.
			if (!metaTileEntity.getNotifiedFluidInputList().isEmpty()) {
				invalidatedInputList.clear();
				metaTileEntity.getNotifiedFluidInputList().clear();
				metaTileEntity.getNotifiedItemInputList().clear();
				return true;
			}

			// If any of the invalid buses changed since last check, proceed.
			if(validateChangedInputBuses())
				return true;

			// If we're here, no inputs have changed since the last check but at least one is invalid.
			// Proceed if at least one item handler hasn't been marked invalid.

			// Collect all item handlers
			ArrayList<IItemHandlerModifiable> flattenedHandlers = new ArrayList<>();
			for(IItemHandler ih : getInputBuses()) {
				if (ih instanceof ItemHandlerList ihl) {
					for(IItemHandler backingHandler : ihl.getBackingHandlers())
						if(backingHandler instanceof IItemHandlerModifiable bhm)
							flattenedHandlers.add(bhm);
				}
				if (ih instanceof IItemHandlerModifiable ihl)
					flattenedHandlers.add(ihl);
			}

			return (!invalidatedInputList.containsAll(flattenedHandlers));
		}

		@Override
		protected void trySearchNewRecipe() {
			if(useDistinctLogic()) {
				trySearchNewRecipeDistinct();
			} else {
				trySearchNewRecipeCombined();
			}
		}

		private void trySearchNewRecipeCombined() {
			long maxVoltage = getMaxVoltage();
			Recipe currentRecipe = null;
			IItemHandlerModifiable importInventory = getInputInventory();
			IMultipleTankHandler importFluids = getInputTank();

			// see if the last recipe we used still works
			if (this.previousRecipe != null &&
				this.previousRecipe.matches(false, importInventory, importFluids))
				currentRecipe = this.previousRecipe;
				// If there is no active recipe, then we need to find one.
			else
				currentRecipe = findRecipe(maxVoltage, importInventory, importFluids);

			// If a recipe was found, then inputs were valid. Cache found recipe.
			if (currentRecipe != null)
				this.previousRecipe = currentRecipe;

			this.invalidInputsForRecipes = (currentRecipe == null);

			// proceed if we have a usable recipe.
			if (currentRecipe != null) {
				Recipe multipliedRecipe = multiplyRecipe(importInventory, importFluids, currentRecipe, machineItemStack, recipeMap);

				//Attempts to run the current recipe, if it is not null
				if (multipliedRecipe != null && setupAndConsumeRecipeInputs(multipliedRecipe))
					setupRecipe(multipliedRecipe);
			}

			// Inputs have been inspected.
			metaTileEntity.getNotifiedItemInputList().clear();
			metaTileEntity.getNotifiedFluidInputList().clear();
		}

		private void trySearchNewRecipeDistinct() {
			long maxVoltage = getMaxVoltage();
			Recipe currentRecipe = null;
			List<IItemHandlerModifiable> importInventory = getInputBuses();
			IMultipleTankHandler importFluids = getInputTank();

			// Can we reuse the cached recipe?
			if (previousRecipe != null &&
				previousRecipe.matches(false, importInventory.get(lastRecipeIndex), importFluids)) {

				currentRecipe = previousRecipe;
				Recipe multipliedRecipe = multiplyRecipe(importInventory.get(lastRecipeIndex), importFluids, currentRecipe, machineItemStack, recipeMap);
				if(setupAndConsumeRecipeInputs(multipliedRecipe, lastRecipeIndex)) {
					setupRecipe(multipliedRecipe);
				}
				//if the recipe matches return true we HAVE enough of the input to proceed, but are not proceeding due to either lack of energy or output space.
				return;
			}

			//If the previous recipe is null, check for a new recipe
			for(int i = 0; i < importInventory.size(); i++) {
				IItemHandlerModifiable bus = importInventory.get(i);

				// skip this bus if nothing's changed since last check
				if (invalidatedInputList.contains(bus))
					continue;

				// see if another recipe can be run
				currentRecipe = findRecipe(maxVoltage, bus, importFluids);

				// if no valid recipe was found, mark this bus invalid and try the next one.
				if(currentRecipe == null) {
					invalidatedInputList.add(bus);
					continue;
				}

				// Cache the base recipe
				this.previousRecipe = currentRecipe;

				// Scale the recipe
				Recipe multipliedRecipe = multiplyRecipe(bus, importFluids, currentRecipe, machineItemStack, recipeMap);

				// Got a usable multiplied recipe, proceed with that.
				if(multipliedRecipe != null && setupAndConsumeRecipeInputs(multipliedRecipe, i)) {
					setupRecipe(multipliedRecipe);
					lastRecipeIndex = i;
					return; // success, stop here.
				}
			}
		}

		// ------------------------------- End Distinct Bus Logic ------------------------------------------------

		@Override
		protected void setupRecipe(Recipe recipe) {
			int[] resultOverclock = calculateOverclock(recipe.getEUt(), machineVoltage, recipe.getDuration());
			this.progressTime = 1;
			setMaxProgress(resultOverclock[1]);
			this.recipeEUt = resultOverclock[0] * this.numberOfOperations;
			this.fluidOutputs = GTUtility.copyFluidList(recipe.getFluidOutputs());
			int tier = Math.min(getMachineTierForRecipe(recipe), machineTier);
			int overclocks = tier - recipe.getBaseTier();
			this.itemOutputs = GTUtility.copyStackList(recipe.getResultItemOutputs(getOutputInventory().getSlots(),
			                                                                       random,
			                                                                       overclocks));

			if(this.wasActiveAndNeedsUpdate)
				this.wasActiveAndNeedsUpdate = false;
			else
				setActive(true);

			if(metaTileEntity instanceof TileEntityProcessingArray tepa)
				tepa.activeRecipeMachineStats = new MachineStats(tier, numberOfOperations, recipeMap);
		}

		@Override
		protected void completeRecipe() {
			super.completeRecipe();

			// if the recipe has actually finished (i.e. not jammed), clear the cached MachineStats
			if(!isJammed)
				activeRecipeMachineStats = null;
		}
	}

	/**
	 * Container for caching information about a machine stack. Used for display text and Jammed state checking,
	 * namely when the structure has been broken and reformed (which nukes much of the active recipe information).
 	 */
	private static class MachineStats {
		/** The tier the recipe is being run at as a GTValues ordinal */
		public final int machineTier;
		/** The number of parallel operations being performed */
		public final int parallels;
		/** The RecipeMap the recipe originated from */
		public final RecipeMap<?> recipeMap;

		public MachineStats(int tier, int parallels, @Nonnull RecipeMap<?> recipeMap) {
			assert tier >= 0;
			assert parallels > 0;

			this.machineTier = tier;
			this.parallels = parallels;
			this.recipeMap = recipeMap;
		}

		/**
		 * @param other the basis of comparison
		 * @return {@code true} if the recipe map is the same and the tier and parallels are at least that of {@code other},
		 * {@code false} otherwise.
		 */
		public boolean satisfies(@Nonnull MachineStats other) {
			return this.recipeMap == other.recipeMap
				   && this.machineTier >= other.machineTier
				   && this.parallels >= other.parallels;
		}
	}
}
