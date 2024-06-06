package gregicadditions.machines;

import static gregtech.api.multiblock.BlockPattern.RelativeDirection.BACK;
import static gregtech.api.multiblock.BlockPattern.RelativeDirection.DOWN;
import static gregtech.api.multiblock.BlockPattern.RelativeDirection.LEFT;

import java.util.List;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicadditions.client.ClientHandler;
import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.multiblock.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.recipeproperties.FusionEUToStartProperty;
import gregtech.api.render.ICubeRenderer;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TileEntityFusionReactor extends RecipeMapMultiblockController {
	private final int tier;
	private EnergyContainerList inputEnergyContainers;
	private long heat = 0; // defined in TileEntityFusionReactor but serialized in FusionRecipeLogic
	private long recipeHeat = 0;

	public TileEntityFusionReactor(ResourceLocation metaTileEntityId, int tier) {
		super(metaTileEntityId, RecipeMaps.FUSION_RECIPES);
		this.recipeMapWorkable = new FusionRecipeLogic(this);
		this.tier = tier;
		this.reinitializeStructurePattern();
		this.energyContainer = new EnergyContainerInternal(this, Integer.MAX_VALUE, 0, 0, 0, 0);
	}

	private static class EnergyContainerInternal extends EnergyContainerHandler {

		public EnergyContainerInternal(MetaTileEntity tileEntity,
									   long maxCapacity,
									   long maxInputVoltage,
									   long maxInputAmperage,
									   long maxOutputVoltage, long maxOutputAmperage)
		{
			super(tileEntity, maxCapacity, maxInputVoltage, maxInputAmperage, maxOutputVoltage, maxOutputAmperage);
		}

		@Override
		public String getName() {
			return "EnergyContainerInternal";
		}
	}

	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntityFusionReactor(metaTileEntityId, tier);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		FactoryBlockPattern.start();
		return FactoryBlockPattern.start(LEFT, DOWN, BACK)
				.aisle("###############", "######OCO######", "###############")
				.aisle("######ICI######", "####CCcccCC####", "######ICI######")
				.aisle("####CC###CC####", "###EccOCOccE###", "####CC###CC####")
				.aisle("###C#######C###", "##EcEC###CEcE##", "###C#######C###")
				.aisle("##C#########C##", "#CcE#######EcC#", "##C#########C##")
				.aisle("##C#########C##", "#CcC#######CcC#", "##C#########C##")
				.aisle("#I###########I#", "OcO#########OcO", "#I###########I#")
				.aisle("#C###########C#", "CcC#########CcC", "#C###########C#")
				.aisle("#I###########I#", "OcO#########OcO", "#I###########I#")
				.aisle("##C#########C##", "#CcC#######CcC#", "##C#########C##")
				.aisle("##C#########C##", "#CcE#######EcC#", "##C#########C##")
				.aisle("###C#######C###", "##EcEC###CEcE##", "###C#######C###")
				.aisle("####CC###CC####", "###EccOCOccE###", "####CC###CC####")
				.aisle("######ICI######", "####CCcccCC####", "######ICI######")
				.aisle("###############", "######OSO######", "###############")
				.where('S', selfPredicate())
				.where('C', statePredicate(getCasingState()))
				.where('c', statePredicate(getCoilState()))
				.where('O', statePredicate(getCasingState()).or(abilityPartPredicate(MultiblockAbility.EXPORT_FLUIDS)))
				.where('E', statePredicate(getCasingState()).or(tilePredicate((state, tile) -> {
					for (int i = tier; i < GTValues.V.length; i++) {
						if (tile.metaTileEntityId.equals(MetaTileEntities.ENERGY_INPUT_HATCH[i].metaTileEntityId)) return true;
					}
					return false;
				})))
				.where('I', statePredicate(getCasingState()).or(abilityPartPredicate(MultiblockAbility.IMPORT_FLUIDS)))
				.where('#', (tile) -> true)
				.build();
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
		return ClientHandler.FUSION_TEXTURE;
	}

	private IBlockState getCasingState() {
		switch (tier) {
		case 6:
			return MetaBlocks.MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.LuV);
		case 7:
			return MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.FUSION_CASING);
		case 8:
		default:
			return MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.FUSION_CASING_MK2);
		}
	}

	private IBlockState getCoilState() {
		switch (tier) {
		case 6:
			return MetaBlocks.WIRE_COIL.getState(BlockWireCoil.CoilType.SUPERCONDUCTOR);
		case 7:
		case 8:
		default:
			return MetaBlocks.WIRE_COIL.getState(BlockWireCoil.CoilType.FUSION_COIL);
		}
	}

	@Override
	public boolean checkRecipe(Recipe recipe, boolean consumeIfSuccess) {

		long requiredHeat;

		// if the recipe produces energy then we don't need to worry about heat mechanics
		if(recipeMapWorkable.getRecipeEUt() < 0)
			return true;

		// The recipe may be null on world reload. Use recipeHeat instead.
		if(recipe == null)
			requiredHeat = recipeHeat;
		else
			// Ensure sufficient heat for the recipe to be running
			requiredHeat = recipe.getRecipePropertyStorage()
								 .getRecipePropertyValue(FusionEUToStartProperty.getInstance(), 0L);

		// can't run that hot
		if(requiredHeat > energyContainer.getEnergyCapacity())
			return false;

		// already hot enough
		if(requiredHeat <= heat) return true;

		long heatDiff = requiredHeat - heat;
		// not hot enough, but is there enough energy to heat back up?
		if(energyContainer.getEnergyStored() >= heatDiff) {
			// Don't modify heat unless asked
			if(consumeIfSuccess) {
				// reheat the reactor and continue
				long energyRemoved = energyContainer.removeEnergy(heatDiff);
				// add to heat what was removed from the buffer (the resulting value is negative)
				heat -= energyRemoved;
			}
			return true;
		}

		// Can't reach required temperature. Jammed.
		return false;
	}

	@Override
	public void update() {
		// check to reduce heat every tick, even when unformed, except on the first game tick
		if(!getWorld().isRemote && heat > 0 && getTimer() > 0)
			// if the structure isn't formed, or is formed and not operational
			if(!isStructureFormed() ||
			   (recipeMapWorkable.isJammed() || !recipeMapWorkable.isActive()))
				// reduce the heat
				heat = heat <= 10_000 ? 0 : (heat - 10_000);

		// then do the usual stuff
		super.update();
	}

	@Override
	public void invalidateStructure() {
		// superclass deletes this, so cache it before that happens
		IEnergyContainer temp = this.energyContainer;
		super.invalidateStructure();
		// restore it
		this.energyContainer = temp;
	}

	@Override
	protected void formStructure(PatternMatchContext context) {
		long energyStored = this.energyContainer.getEnergyStored();
		super.formStructure(context);
		this.initializeAbilities();

		// refill buffered energy, voiding any excess
		if(this.energyContainer instanceof EnergyContainerHandler c)
			c.setEnergyStored(Math.min(c.getEnergyCapacity(), energyStored));
	}

	private void initializeAbilities() {
		this.inputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
		this.inputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
		this.outputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
		this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
		List<IEnergyContainer> energyInputs = getAbilities(MultiblockAbility.INPUT_ENERGY);
		this.inputEnergyContainers = new EnergyContainerList(energyInputs);
		long euCapacity = energyInputs.size() * 10_000_000L * (long) Math.pow(2, tier - 6);
		this.energyContainer = new EnergyContainerInternal(this, euCapacity, GTValues.V[tier], 0, 0, 0);
	}

	@Override
	protected void updateFormedValid() {
		if (!getWorld().isRemote) {
			if (this.inputEnergyContainers.getEnergyStored() > 0) {
				long energyAdded = this.energyContainer.addEnergy(this.inputEnergyContainers.getEnergyStored());
				if (energyAdded > 0) this.inputEnergyContainers.removeEnergy(energyAdded);
			}
			super.updateFormedValid();
		}
	}

	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		if (!this.isStructureFormed()) {
			textList.add(new TextComponentTranslation("gregtech.multiblock.invalid_structure").setStyle(new Style().setColor(TextFormatting.RED)));
		}
		if (this.isStructureFormed()) {
			if (!this.recipeMapWorkable.isWorkingEnabled()) {
				textList.add(new TextComponentTranslation("gregtech.multiblock.work_paused"));
			} else if (this.recipeMapWorkable.isActive()) {
				if(!this.recipeMapWorkable.isJammed())
					textList.add(new TextComponentTranslation("gregtech.multiblock.running"));
				else
					textList.add(new TextComponentTranslation("gregtech.multiblock.jammed"));
				int currentProgress;
				if (energyContainer.getEnergyCapacity() > 0) {
					currentProgress = (int) (this.recipeMapWorkable.getProgressPercent() * 100.0D);
					textList.add(new TextComponentTranslation("gregtech.multiblock.progress", currentProgress));
				} else {
					currentProgress = -this.recipeMapWorkable.getRecipeEUt();
					textList.add(new TextComponentTranslation("gregtech.multiblock.generation_eu", currentProgress));
				}
			} else {
				textList.add(new TextComponentTranslation("gregtech.multiblock.idling"));
			}

			if (this.recipeMapWorkable.isHasNotEnoughEnergy()) {
				textList.add(new TextComponentTranslation("gregtech.multiblock.not_enough_energy").setStyle(new Style().setColor(TextFormatting.RED)));
			}
		}

		textList.add(new TextComponentString("EU: " + this.energyContainer.getEnergyStored() + " / " + this.energyContainer.getEnergyCapacity()));
		textList.add(new TextComponentTranslation("gregtech.multiblock.fusion_reactor.heat", heat));
	}

	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		this.getBaseTexture(null).render(renderState, translation, pipeline);
		ClientHandler.FUSION_REACTOR_OVERLAY.render(renderState, translation, pipeline, this.getFrontFacing(), this.recipeMapWorkable.isActive());
	}

	private class FusionRecipeLogic extends MultiblockRecipeLogic {
		public FusionRecipeLogic(TileEntityFusionReactor tileEntity) {
			super(tileEntity);
			this.allowOverclocking = false;
		}

		@Override
		public void updateWorkable() {
			super.updateWorkable();
		}

		@Override
		protected Recipe findRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs) {
			Recipe recipe = super.findRecipe(maxVoltage, inputs, fluidInputs);
			return (recipe != null && recipe.getRecipePropertyStorage().getRecipePropertyValue(FusionEUToStartProperty.getInstance(), 0L) <= energyContainer.getEnergyCapacity()) ? recipe : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = super.serializeNBT();
			tag.setLong("Heat", heat);
			tag.setLong("RecipeHeat", recipeHeat);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound compound) {
			super.deserializeNBT(compound);
			heat = compound.getLong("Heat");
			recipeHeat = compound.getLong("RecipeHeat");
		}
	}
}
