package gregicadditions.machines;

import static gregtech.api.multiblock.BlockPattern.RelativeDirection.*;

import gregicadditions.item.GAMetaBlocks;
import gregicadditions.item.GAMultiblockCasing;
import gregicadditions.item.GATransparentCasing;
import gregicadditions.recipes.GARecipeMaps;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.multiblock.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class TileEntityAssemblyLine extends RecipeMapMultiblockController {

	/** Number of input-type slices detected on structure formation */
	private int inputSlices = 0;

	/** Number of slices required to run the active recipe */
	private int recipeSlices = 0;

	public TileEntityAssemblyLine(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, GARecipeMaps.ASSEMBLY_LINE_RECIPES);
	}

	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntityAssemblyLine(metaTileEntityId);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start(LEFT, DOWN, FRONT)
					.aisle("#Y#", "GSG", "RTR", "FIF")
					.aisle("#Y#", "GAG", "RTR", "FIF").setRepeatable(3, 15)
					.aisle("#Y#", "GAG", "RTR", "COC")
					.where('S', selfPredicate())
					.where('C', statePredicate(getCasingState()))
					.where('F', statePredicate(getCasingState()).or(abilityPartPredicate(MultiblockAbility.IMPORT_FLUIDS)))
					.where('O', statePredicate(getCasingState()).or(abilityPartPredicate(MultiblockAbility.EXPORT_ITEMS)))
					.where('Y', statePredicate(getCasingState()).or(abilityPartPredicate(MultiblockAbility.INPUT_ENERGY)))
					.where('I', tilePredicate((state, tile) -> {
						return tile.metaTileEntityId.equals(MetaTileEntities.ITEM_IMPORT_BUS[0].metaTileEntityId); }))
					.where('G', statePredicate(MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING)))
					.where('A', statePredicate(MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLER_CASING)))
					.where('R', statePredicate(GAMetaBlocks.TRANSPARENT_CASING.getState(GATransparentCasing.CasingType.REINFORCED_GLASS)))
					.where('T', statePredicate(GAMetaBlocks.MUTLIBLOCK_CASING.getState(GAMultiblockCasing.CasingType.TUNGSTENSTEEL_GEARBOX_CASING)))
					.where('#', (tile) -> {
						return true; })
					.build();

	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
		return Textures.SOLID_STEEL_CASING;
	}

	protected IBlockState getCasingState() {
		return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
	}

	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		if(isStructureFormed()) {
			textList.add(new TextComponentTranslation("gtadditions.machine.assembly_line.slices", inputSlices + 1));
			if(getRecipeMapWorkable().isJammed() && inputSlices < recipeSlices)
				textList.add(new TextComponentTranslation("gtadditions.machine.assembly_line.recipe_slices", recipeSlices + 1));
		}
	}

	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		inputSlices = getInputInventory().getSlots();
	}

	@Override
	public boolean checkRecipe(Recipe recipe, boolean consumeIfSuccess) {
		if(recipe != null)
			recipeSlices = recipe.getInputs().size();

		return inputSlices >= recipeSlices
			   && super.checkRecipe(recipe, consumeIfSuccess);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		if(getRecipeMapWorkable().isActive())
			data.setInteger("Slices", recipeSlices);
		return super.writeToNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		recipeSlices = data.getInteger("Slices");
		super.readFromNBT(data);
	}
}
