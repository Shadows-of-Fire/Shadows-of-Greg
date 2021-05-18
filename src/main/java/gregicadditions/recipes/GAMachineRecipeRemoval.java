package gregicadditions.recipes;

import gregicadditions.GAConfig;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static gregicadditions.recipes.helpers.HelperMethods.removeRecipesByInputs;

public class GAMachineRecipeRemoval {

	private static final FluidStack[] cableFluids = { Materials.Rubber.getFluid(144), Materials.StyreneButadieneRubber.getFluid(108), Materials.SiliconeRubber.getFluid(72) };


	public static void init() {

		//Remove Old Bucket Recipe
		if (GAConfig.GT6.BendingCurvedPlates && GAConfig.GT6.addCurvedPlates) {
			removeRecipesByInputs(RecipeMaps.BENDER_RECIPES, OreDictUnifier.get(OrePrefix.plate, Materials.Iron, 12), IntCircuitIngredient.getIntegratedCircuit(1));
			removeRecipesByInputs(RecipeMaps.BENDER_RECIPES, OreDictUnifier.get(OrePrefix.plate, Materials.WroughtIron, 12), IntCircuitIngredient.getIntegratedCircuit(1));
		}

		//Fix Brick Exploit
		removeRecipesByInputs(RecipeMaps.MACERATOR_RECIPES, new ItemStack(Items.BRICK));

		removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, MetaItems.ENERGY_LAPOTRONIC_ORB2.getStackForm(8), OreDictUnifier.get(OrePrefix.plate, Materials.Darmstadtium, 16));

		//Star Recipes
		removeRecipesByInputs(RecipeMaps.AUTOCLAVE_RECIPES, new ItemStack[] { new ItemStack(Items.NETHER_STAR) }, new FluidStack[] { Materials.Darmstadtium.getFluid(288) });

		//MAX Hull Recipes
		removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, OreDictUnifier.get(OrePrefix.plate, Materials.Darmstadtium, 8), IntCircuitIngredient.getIntegratedCircuit(8));
		removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] { MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX), OreDictUnifier.get(OrePrefix.wireGtSingle, MarkerMaterials.Tier.Superconductor, 2) }, new FluidStack[] { Materials.Polytetrafluoroethylene.getFluid(288) });

		//Electrolyzing Fixes
		removeRecipesByInputs(RecipeMaps.ELECTROLYZER_RECIPES, OreDictUnifier.get(OrePrefix.dust, Materials.Sphalerite, 2));

		//Remove duplicate seed oil recipes if we are generating our own
		if(GAConfig.GTBees.GenerateExtractorRecipes) {
			removeRecipesByInputs(RecipeMaps.FLUID_EXTRACTION_RECIPES, new ItemStack(Items.WHEAT_SEEDS));
			removeRecipesByInputs(RecipeMaps.FLUID_EXTRACTION_RECIPES, new ItemStack(Items.MELON_SEEDS));
			removeRecipesByInputs(RecipeMaps.FLUID_EXTRACTION_RECIPES, new ItemStack(Items.PUMPKIN_SEEDS));
		}

		//Remove Conflicting Redstone Plate Recipe
		removeRecipesByInputs(RecipeMaps.COMPRESSOR_RECIPES, OreDictUnifier.get(OrePrefix.dust, Materials.Redstone));

		//Remove Incorrect Quartz Plate Recipes
		removeRecipesByInputs(RecipeMaps.CUTTER_RECIPES, new ItemStack[] { new ItemStack(Blocks.QUARTZ_BLOCK) }, new FluidStack[] { Materials.Water.getFluid(73) });
		removeRecipesByInputs(RecipeMaps.CUTTER_RECIPES, new ItemStack[] { OreDictUnifier.get(OrePrefix.block, Materials.CertusQuartz) }, new FluidStack[] { Materials.Water.getFluid(73) });
		removeRecipesByInputs(RecipeMaps.CUTTER_RECIPES, new ItemStack[] { new ItemStack(Blocks.QUARTZ_BLOCK) }, new FluidStack[] { Materials.DistilledWater.getFluid(55) });
		removeRecipesByInputs(RecipeMaps.CUTTER_RECIPES, new ItemStack[] { OreDictUnifier.get(OrePrefix.block, Materials.CertusQuartz) }, new FluidStack[] { Materials.DistilledWater.getFluid(55) });
		removeRecipesByInputs(RecipeMaps.CUTTER_RECIPES, new ItemStack[] { new ItemStack(Blocks.QUARTZ_BLOCK) }, new FluidStack[] { Materials.Lubricant.getFluid(18) });
		removeRecipesByInputs(RecipeMaps.CUTTER_RECIPES, new ItemStack[] { OreDictUnifier.get(OrePrefix.block, Materials.CertusQuartz) }, new FluidStack[] { Materials.Lubricant.getFluid(18) });


		//Remove the GTCE Pump assembler recipes to match our table recipes
		for(FluidStack stackFluid : cableFluids) {
			removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] {OreDictUnifier.get(OrePrefix.plate, Materials.Tin, 2), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tin), OreDictUnifier.get(OrePrefix.screw, Materials.Tin), OreDictUnifier.get(OrePrefix.rotor, Materials.Tin), MetaItems.ELECTRIC_MOTOR_LV.getStackForm()}, new FluidStack[] {stackFluid});
			removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] {OreDictUnifier.get(OrePrefix.plate, Materials.Bronze, 2), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Copper), OreDictUnifier.get(OrePrefix.screw, Materials.Bronze), OreDictUnifier.get(OrePrefix.rotor, Materials.Bronze), MetaItems.ELECTRIC_MOTOR_MV.getStackForm()}, new FluidStack[] {stackFluid});
			removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] {OreDictUnifier.get(OrePrefix.plate, Materials.Steel, 2), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Gold), OreDictUnifier.get(OrePrefix.screw, Materials.Steel), OreDictUnifier.get(OrePrefix.rotor, Materials.Steel), MetaItems.ELECTRIC_MOTOR_HV.getStackForm()}, new FluidStack[] {stackFluid});
			removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] {OreDictUnifier.get(OrePrefix.plate, Materials.StainlessSteel, 2), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Aluminium), OreDictUnifier.get(OrePrefix.screw, Materials.StainlessSteel), OreDictUnifier.get(OrePrefix.rotor, Materials.StainlessSteel), MetaItems.ELECTRIC_MOTOR_EV.getStackForm()}, new FluidStack[] {stackFluid});
			removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] {OreDictUnifier.get(OrePrefix.plate, Materials.TungstenSteel, 2), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tungsten), OreDictUnifier.get(OrePrefix.screw, Materials.TungstenSteel), OreDictUnifier.get(OrePrefix.rotor, Materials.TungstenSteel), MetaItems.ELECTRIC_MOTOR_IV.getStackForm()}, new FluidStack[] {stackFluid});
		}


	}


}
