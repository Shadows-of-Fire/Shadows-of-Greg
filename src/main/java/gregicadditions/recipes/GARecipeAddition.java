package gregicadditions.recipes;

import java.util.ArrayList;
import java.util.List;

import forestry.core.ModuleCore;
import forestry.core.items.EnumElectronTube;
import gregicadditions.GAConfig;
import gregicadditions.GAEnums;
import gregicadditions.GAMaterials;
import gregicadditions.item.GAMetaBlocks;
import gregicadditions.item.GAMetaItems;
import gregicadditions.item.GAMultiblockCasing;
import gregicadditions.item.GATransparentCasing;
import gregicadditions.machines.GATileEntities;
import gregtech.api.GTValues;
import gregtech.api.items.ToolDictNames;
import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.MarkerMaterials.Tier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.IngotMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.util.GTUtility;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.*;
import gregtech.common.blocks.BlockMultiblockCasing.MultiblockCasingType;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

import static gregicadditions.recipes.helpers.HelperMethods.*;
import static gregtech.api.GTValues.M;
import static gregtech.api.recipes.RecipeMaps.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.loaders.oreprocessing.WireRecipeHandler.INSULATION_MATERIALS;

public class GARecipeAddition {

	private static final MaterialStack[] cableFluids = { new MaterialStack(Materials.Rubber, 144), new MaterialStack(Materials.StyreneButadieneRubber, 108), new MaterialStack(Materials.SiliconeRubber, 72) };

	private static final MaterialStack[] firstMetal = { new MaterialStack(Materials.Iron, 1), new MaterialStack(Materials.Nickel, 1), new MaterialStack(Materials.Invar, 2), new MaterialStack(Materials.Steel, 2), new MaterialStack(Materials.StainlessSteel, 3), new MaterialStack(Materials.Titanium, 3), new MaterialStack(Materials.Tungsten, 4), new MaterialStack(Materials.TungstenSteel, 5) };

	private static final MaterialStack[] lastMetal = { new MaterialStack(Materials.Tin, 0), new MaterialStack(Materials.Zinc, 0), new MaterialStack(Materials.Aluminium, 1) };

	private static final MaterialStack[] ironOres = { new MaterialStack(Materials.Pyrite, 1), new MaterialStack(Materials.BrownLimonite, 1), new MaterialStack(Materials.YellowLimonite, 1), new MaterialStack(Materials.Magnetite, 1), new MaterialStack(Materials.Iron, 1) };

	private static final Material[] circuitTiers = new Material[] {Tier.Master, Tier.Ultimate, Tier.Superconductor };

	/**
	 * Where all Miscellaneous Crafting Recipe manipulation is done. These are the recipes that do not fit in other initialization classes.
	 */
	public static void miscCraftingRecipes() {

		//GTNH Bricks
		removeFurnaceRecipe(new ItemStack(Items.CLAY_BALL, 1, OreDictionary.WILDCARD_VALUE));
		removeFurnaceRecipe(MetaItems.COMPRESSED_CLAY.getStackForm());
		ModHandler.addSmeltingRecipe(MetaItems.COMPRESSED_CLAY.getStackForm(), new ItemStack(Items.BRICK));
		ALLOY_SMELTER_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.CLAY_BALL))
				.notConsumable(MetaItems.SHAPE_MOLD_INGOT)
				.outputs(new ItemStack(Items.BRICK))
				.duration(200).EUt(2).buildAndRegister();

		OreDictionary.registerOre("formWood", MetaItems.WOODEN_FORM_BRICK.getStackForm());
		ModHandler.addShapelessRecipe("clay_brick", MetaItems.COMPRESSED_CLAY.getStackForm(), new ItemStack(Items.CLAY_BALL), "formWood");

		ModHandler.addShapedRecipe("eight_clay_brick", MetaItems.COMPRESSED_CLAY.getStackForm(8),
				"BBB", "BFB", "BBB",
				'B', new ItemStack(Items.CLAY_BALL),
				'F', "formWood");

		ModHandler.addShapedRecipe("coke_brick", GAMetaItems.COMPRESSED_COKE_CLAY.getStackForm(3),
				"BBB", "SFS", "SSS",
				'B', new ItemStack(Items.CLAY_BALL),
				'S', new ItemStack(Blocks.SAND),
				'F', "formWood");

		ModHandler.addSmeltingRecipe(GAMetaItems.COMPRESSED_COKE_CLAY.getStackForm(), MetaItems.COKE_OVEN_BRICK.getStackForm());

		//GT5U Misc Recipes
		ModHandler.addSmeltingRecipe(new ItemStack(Items.SLIME_BALL), MetaItems.RUBBER_DROP.getStackForm());
		removeRecipeByName("minecraft:bone_meal_from_bone");
		FORGE_HAMMER_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.BONE))
				.outputs(new ItemStack(Items.DYE, 4, 15))
				.duration(16).EUt(10).buildAndRegister();

		//Misc Vanilla recipe modification using the GT6 Bending
		if (GAConfig.GT6.BendingCurvedPlates && GAConfig.GT6.BendingCylinders && GAConfig.GT6.addCurvedPlates) {

			//Bucket Recipe Modification
			removeRecipeByName("gregtech:iron_bucket");

			ModHandler.addShapedRecipe("bucket", new ItemStack(Items.BUCKET),
					"ChC", " P ",
					'C', "plateCurvedIron",
					'P', "plateIron");

			ASSEMBLER_RECIPES.recipeBuilder()
					.input(valueOf("plateCurved"), Materials.Iron, 2)
					.input(plate, Materials.Iron)
					.outputs(new ItemStack(Items.BUCKET))
					.duration(200).EUt(4).buildAndRegister();

			ASSEMBLER_RECIPES.recipeBuilder()
					.input(valueOf("plateCurved"), Materials.WroughtIron, 2)
					.input(plate, Materials.WroughtIron)
					.outputs(new ItemStack(Items.BUCKET))
					.duration(200).EUt(4).buildAndRegister();

			//Vanilla Armor modification
			removeRecipeByName("minecraft:iron_helmet");
			ModHandler.addShapedRecipe("iron_helmet", new ItemStack(Items.IRON_HELMET),
					"PPP", "ChC",
					'P', "plateIron",
					'C', "plateCurvedIron");

			removeRecipeByName("minecraft:iron_chestplate");
			ModHandler.addShapedRecipe("iron_chestplate", new ItemStack(Items.IRON_CHESTPLATE),
					"PhP", "CPC", "CPC",
					'P', "plateIron",
					'C', "plateCurvedIron");

			removeRecipeByName("minecraft:iron_leggings");
			ModHandler.addShapedRecipe("iron_leggings", new ItemStack(Items.IRON_LEGGINGS),
					"PCP", "ChC", "C C",
					'P', "plateIron",
					'C', "plateCurvedIron");

			removeRecipeByName("minecraft:iron_boots");
			ModHandler.addShapedRecipe("iron_boots", new ItemStack(Items.IRON_BOOTS),
					"P P", "ChC",
					'P', "plateIron",
					'C', "plateCurvedIron");

			removeRecipeByName("minecraft:golden_helmet");
			ModHandler.addShapedRecipe("golden_helmet", new ItemStack(Items.GOLDEN_HELMET),
					"PPP", "ChC",
					'P', "plateGold",
					'C', "plateCurvedGold");

			removeRecipeByName("minecraft:golden_chestplate");
			ModHandler.addShapedRecipe("golden_chestplate", new ItemStack(Items.GOLDEN_CHESTPLATE),
					"PhP", "CPC", "CPC",
					'P', "plateGold",
					'C', "plateCurvedGold");

			removeRecipeByName("minecraft:golden_leggings");
			ModHandler.addShapedRecipe("golden_leggings", new ItemStack(Items.GOLDEN_LEGGINGS),
					"PCP", "ChC", "C C",
					'P', "plateGold",
					'C', "plateCurvedGold");

			removeRecipeByName("minecraft:golden_boots");
			ModHandler.addShapedRecipe("golden_boots", new ItemStack(Items.GOLDEN_BOOTS),
					"P P", "ChC",
					'P', "plateGold",
					'C', "plateCurvedGold");

			ModHandler.addShapedRecipe("chain_helmet", new ItemStack(Items.CHAINMAIL_HELMET),
					"RRR", "RhR",
					'R', "ringIron");

			ModHandler.addShapedRecipe("chain_chestplate", new ItemStack(Items.CHAINMAIL_CHESTPLATE),
					"RhR", "RRR", "RRR",
					'R', "ringIron");

			ModHandler.addShapedRecipe("chain_leggings", new ItemStack(Items.CHAINMAIL_LEGGINGS),
					"RRR", "RhR", "R R",
					'R', "ringIron");

			ModHandler.addShapedRecipe("chain_boots", new ItemStack(Items.CHAINMAIL_BOOTS),
					"R R", "RhR",
					'R', "ringIron");

		}

		//Adjust the recipes for wooden pipes if the config is enabled.
		//This cannot be done in the Recipe Handler because there is no wood curved plate
		if(GAConfig.GT6.BendingPipes && GAConfig.GT6.BendingCylinders) {
			removeRecipeByName("gregtech:medium_wooden_pipe");
			removeRecipeByName("gregtech:small_wooden_pipe");

			ModHandler.addShapedRecipe("pipe_ga_tiny_wood", OreDictUnifier.get(pipeTiny, Materials.Wood, 8),
					"PPP", "hCs", "PPP",
					'P', "plankWood",
					'C', "craftingToolBendingCylinder");

			ModHandler.addShapedRecipe("pipe_ga_small_wood", OreDictUnifier.get(pipeSmall, Materials.Wood, 6),
					"PsP", "PCP", "PhP",
					'P', "plankWood",
					'C', "craftingToolBendingCylinder");

			ModHandler.addShapedRecipe("pipe_ga_wood", OreDictUnifier.get(pipeMedium, Materials.Wood, 2),
					"PPP", "sCh", "PPP",
					'P', "plankWood",
					'C', "craftingToolBendingCylinder");

			ModHandler.addShapedRecipe("pipe_ga_large_wood", OreDictUnifier.get(pipeLarge, Materials.Wood),
					"PhP", "PCP", "PsP",
					'P', "plankWood",
					'C', "craftingToolBendingCylinder");
		}

		//Schematic Recipes
		ASSEMBLER_RECIPES.recipeBuilder()
				.input(circuit, Tier.Good, 4)
				.input(plate, Materials.StainlessSteel, 2)
				.outputs(GAMetaItems.SCHEMATIC.getStackForm())
				.duration(3200).EUt(4).buildAndRegister();

		ModHandler.addShapedRecipe("3x3_schematic", GAMetaItems.SCHEMATIC_3X3.getStackForm(),
				"  d", " S ", "   ",
				'S', GAMetaItems.SCHEMATIC.getStackForm());

		ModHandler.addShapedRecipe("2x2_schematic", GAMetaItems.SCHEMATIC_2X2.getStackForm(),
				" d ", " S ", "   ",
				'S', GAMetaItems.SCHEMATIC.getStackForm());

		ModHandler.addShapedRecipe("dust_schematic", GAMetaItems.SCHEMATIC_DUST.getStackForm(),
				"   ", " S ", "  d",
				'S', GAMetaItems.SCHEMATIC.getStackForm());


		//Adjusting Recipes for the MAX Machine Hull and MAX Machine Casing
		removeRecipeByName("gregtech:casing_max");
		removeRecipeByName("gregtech:hull_max");

		ModHandler.addShapedRecipe("ga_casing_max", MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX),
				"PPP", "PwP", "PPP",
				'P', new UnificationEntry(plate, GAMaterials.NEUTRONIUM));

		ASSEMBLER_RECIPES.recipeBuilder().duration(50).EUt(16)
				.input(plate, GAMaterials.NEUTRONIUM, 8)
				.circuitMeta(8)
				.outputs(MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX))
				.duration(50).EUt(16).buildAndRegister();

		//Add MAX hull recipes depending upon the GTCE config option
		if(ConfigHolder.harderMachineHulls) {
			ModHandler.addShapedRecipe("ga_hull_max", MetaTileEntities.HULL[GTValues.MAX].getStackForm(),
					"PHP", "CMC",
					'M', MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX),
					'C', new UnificationEntry(wireGtSingle, Tier.Superconductor),
					'H', new UnificationEntry(plate, GAMaterials.NEUTRONIUM),
					'P', new UnificationEntry(plate, Materials.Polytetrafluoroethylene));

			ASSEMBLER_RECIPES.recipeBuilder()
					.inputs(MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX))
					.input(wireGtSingle, Tier.Superconductor, 2)
					.fluidInputs(Materials.Polytetrafluoroethylene.getFluid(288))
					.outputs(MetaTileEntities.HULL[GTValues.MAX].getStackForm())
					.duration(50).EUt(16).buildAndRegister();
		}
		else {
			ModHandler.addShapedRecipe("ga_hull_max", MetaTileEntities.HULL[GTValues.MAX].getStackForm(),
					"CMC",
					'M', MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX),
					'C', new UnificationEntry(wireGtSingle, Tier.Superconductor));

			ASSEMBLER_RECIPES.recipeBuilder()
					.inputs(MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX))
					.input(wireGtSingle, Tier.Superconductor, 2)
					.outputs(MetaTileEntities.HULL[GTValues.MAX].getStackForm())
					.duration(50).EUt(16).buildAndRegister();
		}
	}

	/**
	 * This method deals with Miscellaneous recipe removals and additions from Single-block machines
	 */
	public static void miscSingleblockMachineRecipes() {

		//Covering Superconductor cables
		int cableTier = GTUtility.getTierByVoltage(GTValues.UV);
		int insulationTier = INSULATION_MATERIALS.get(Materials.SiliconeRubber);

		int fluidAmount = Math.max(36, 144 / (1 + (insulationTier - cableTier) / 2));

		for(OrePrefix wirePrefix : GAEnums.WIRE_DOUBLING_ORDER) {

			int cableAmount = (int) (wirePrefix.materialAmount * 2 / M);


			OrePrefix cablePrefix = valueOf("cable" + wirePrefix.name().substring(4));
			ItemStack cableStack = OreDictUnifier.get(cablePrefix, Tier.Superconductor);

			//Register everything under circuit 24
			ASSEMBLER_RECIPES.recipeBuilder()
					.input(wirePrefix, Tier.Superconductor)
					.fluidInputs(Materials.SiliconeRubber.getFluid(fluidAmount * cableAmount))
					.circuitMeta(24)
					.outputs(cableStack)
					.duration(150).EUt(8).buildAndRegister();

			//Register unique circuit configurations
			if(wirePrefix != wireGtSingle) {
				ASSEMBLER_RECIPES.recipeBuilder()
						.input(wireGtSingle, Tier.Superconductor, cableAmount)
						.fluidInputs(Materials.SiliconeRubber.getFluid(fluidAmount * cableAmount))
						.circuitMeta(24 + GAEnums.WIRE_DOUBLING_ORDER.indexOf(wirePrefix))
						.outputs(cableStack)
						.duration(150).EUt(8).buildAndRegister();
			}
		}


		//Boiler Casings Recipes
		ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.STEEL_SOLID))
				.input(cableGtSingle, Materials.Copper, 4)
				.input(circuit, Tier.Advanced, 4)
				.outputs(MetaTileEntities.LARGE_STEEL_BOILER.getStackForm())
				.EUt(120).duration(600).buildAndRegister();

		ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.TITANIUM_STABLE))
				.input(cableGtSingle, Materials.Gold, 4)
				.input(circuit, Tier.Elite, 4)
				.outputs(MetaTileEntities.LARGE_TITANIUM_BOILER.getStackForm())
				.EUt(500).duration(600).buildAndRegister();

		ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST))
				.input(cableGtSingle, Materials.Aluminium, 4)
				.input(circuit, Tier.Master, 4)
				.outputs(MetaTileEntities.LARGE_TUNGSTENSTEEL_BOILER.getStackForm()).EUt(2000).duration(600).buildAndRegister();

		//Glass Tube
		FLUID_SOLIDFICATION_RECIPES.recipeBuilder()
				.fluidInputs(Materials.Glass.getFluid(144))
				.notConsumable(MetaItems.SHAPE_MOLD_BALL.getStackForm())
				.outputs(MetaItems.GLASS_TUBE.getStackForm())
				.EUt(16).duration(80).buildAndRegister();

		COMPRESSOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.GLOWSTONE_DUST, 4))
				.outputs(new ItemStack(Blocks.GLOWSTONE))
				.EUt(16).duration(40).buildAndRegister();

		//Reinforced Glass
		int multiplier2;
		for (MaterialStack metal1 : firstMetal) {
			IngotMaterial material1 = (IngotMaterial) metal1.material;
			int multiplier1 = (int) metal1.amount;
			for(MaterialStack metal2 : lastMetal) {
				IngotMaterial material2 = (IngotMaterial) metal2.material;
				if((int) metal1.amount == 1) {
					multiplier2 = 0;
				}
				else {
					multiplier2 = (int) metal2.amount;
				}

				ModHandler.addShapedRecipe(String.format("mixed_metal_1_%s_%s", material1.toString(), material2.toString()), MetaItems.INGOT_MIXED_METAL.getStackForm(multiplier1 + multiplier2),
						"F", "M", "L",
						'F', new UnificationEntry(plate, material1),
						'M', "plateBronze",
						'L', OreDictUnifier.get(plate, material2));

				ModHandler.addShapedRecipe(String.format("mixed_metal_2_%s_%s", material1.toString(), material2.toString()), MetaItems.INGOT_MIXED_METAL.getStackForm(multiplier1 + multiplier2),
						"F", "M", "L",
						'F', new UnificationEntry(plate, material1),
						'M', "plateBrass",
						'L', OreDictUnifier.get(plate, material2));

				ASSEMBLER_RECIPES.recipeBuilder()
						.input(plate, material1)
						.input(plank, Materials.Bronze)
						.input(plate, material2)
						.outputs(MetaItems.INGOT_MIXED_METAL.getStackForm(multiplier1 + multiplier2))
						.duration(40 * multiplier1 + multiplier2 * 40).EUt(8).buildAndRegister();

				ASSEMBLER_RECIPES.recipeBuilder()
						.input(plate, material1)
						.input(plate, Materials.Brass)
						.input(plate, material2)
						.outputs(MetaItems.INGOT_MIXED_METAL.getStackForm(multiplier1 + multiplier2))
						.duration(40 * multiplier1 + multiplier2 * 40).EUt(8).buildAndRegister();
			}
		}

		ALLOY_SMELTER_RECIPES.recipeBuilder()
				.inputs(MetaItems.ADVANCED_ALLOY_PLATE.getStackForm())
				.input(dust, Materials.Glass, 3)
				.outputs(GAMetaBlocks.TRANSPARENT_CASING.getItemVariant(GATransparentCasing.CasingType.REINFORCED_GLASS, 4))
				.duration(400).EUt(4).buildAndRegister();

		ALLOY_SMELTER_RECIPES.recipeBuilder()
				.inputs(MetaItems.ADVANCED_ALLOY_PLATE.getStackForm())
				.inputs(new ItemStack(Blocks.GLASS, 3))
				.outputs(GAMetaBlocks.TRANSPARENT_CASING.getItemVariant(GATransparentCasing.CasingType.REINFORCED_GLASS, 4))
				.duration(400).EUt(4).buildAndRegister();

		//Machine Components - Adjusting the pump recipe to the GT5U recipe
		removeCraftingRecipes(MetaItems.ELECTRIC_PUMP_LV.getStackForm());
		removeCraftingRecipes(MetaItems.ELECTRIC_PUMP_MV.getStackForm());
		removeCraftingRecipes(MetaItems.ELECTRIC_PUMP_HV.getStackForm());
		removeCraftingRecipes(MetaItems.ELECTRIC_PUMP_EV.getStackForm());
		removeCraftingRecipes(MetaItems.ELECTRIC_PUMP_IV.getStackForm());

		ModHandler.addShapedRecipe("lv_electric_pump_paper", MetaItems.ELECTRIC_PUMP_LV.getStackForm(), "SRH", "dPw", "HMC", 'S', OreDictUnifier.get(screw, Materials.Tin), 'R', OreDictUnifier.get(rotor, Materials.Tin), 'H', OreDictUnifier.get(ring, Materials.Paper), 'P', OreDictUnifier.get(pipeMedium, Materials.Bronze), 'M', MetaItems.ELECTRIC_MOTOR_LV.getStackForm(), 'C', OreDictUnifier.get(cableGtSingle, Materials.Tin));
		for (MaterialStack stackFluid : cableFluids) {
			IngotMaterial m = (IngotMaterial) stackFluid.material;
			ModHandler.addShapedRecipe(String.format("lv_electric_pump_%s", m.toString()), MetaItems.ELECTRIC_PUMP_LV.getStackForm(), "SRH", "dPw", "HMC", 'S', OreDictUnifier.get(screw, Materials.Tin), 'R', OreDictUnifier.get(rotor, Materials.Tin), 'H', OreDictUnifier.get(ring, m), 'P', OreDictUnifier.get(pipeMedium, Materials.Bronze), 'M', MetaItems.ELECTRIC_MOTOR_LV.getStackForm(), 'C', OreDictUnifier.get(cableGtSingle, Materials.Tin));
			ModHandler.addShapedRecipe(String.format("mv_electric_pump_%s", m.toString()), MetaItems.ELECTRIC_PUMP_MV.getStackForm(), "SRH", "dPw", "HMC", 'S', OreDictUnifier.get(screw, Materials.Bronze), 'R', OreDictUnifier.get(rotor, Materials.Bronze), 'H', OreDictUnifier.get(ring, m), 'P', OreDictUnifier.get(pipeMedium, Materials.Steel), 'M', MetaItems.ELECTRIC_MOTOR_MV.getStackForm(), 'C', OreDictUnifier.get(cableGtSingle, Materials.Copper));
			ModHandler.addShapedRecipe(String.format("hv_electric_pump_%s", m.toString()), MetaItems.ELECTRIC_PUMP_HV.getStackForm(), "SRH", "dPw", "HMC", 'S', OreDictUnifier.get(screw, Materials.Steel), 'R', OreDictUnifier.get(rotor, Materials.Steel), 'H', OreDictUnifier.get(ring, m), 'P', OreDictUnifier.get(pipeMedium, Materials.StainlessSteel), 'M', MetaItems.ELECTRIC_MOTOR_HV.getStackForm(), 'C', OreDictUnifier.get(cableGtSingle, Materials.Gold));
			ModHandler.addShapedRecipe(String.format("ev_electric_pump_%s", m.toString()), MetaItems.ELECTRIC_PUMP_EV.getStackForm(), "SRH", "dPw", "HMC", 'S', OreDictUnifier.get(screw, Materials.StainlessSteel), 'R', OreDictUnifier.get(rotor, Materials.StainlessSteel), 'H', OreDictUnifier.get(ring, m), 'P', OreDictUnifier.get(pipeMedium, Materials.Titanium), 'M', MetaItems.ELECTRIC_MOTOR_EV.getStackForm(), 'C', OreDictUnifier.get(cableGtSingle, Materials.Aluminium));
			ModHandler.addShapedRecipe(String.format("iv_electric_pump_%s", m.toString()), MetaItems.ELECTRIC_PUMP_IV.getStackForm(), "SRH", "dPw", "HMC", 'S', OreDictUnifier.get(screw, Materials.TungstenSteel), 'R', OreDictUnifier.get(rotor, Materials.TungstenSteel), 'H', OreDictUnifier.get(ring, m), 'P', OreDictUnifier.get(pipeMedium, Materials.TungstenSteel), 'M', MetaItems.ELECTRIC_MOTOR_IV.getStackForm(), 'C', OreDictUnifier.get(cableGtSingle, Materials.Tungsten));
		}

		//Adjust the GTCE Pump Assembler recipe to match our pump recipe
		for (MaterialStack stackFluid : cableFluids) {
			IngotMaterial m = (IngotMaterial) stackFluid.material;
			ASSEMBLER_RECIPES.recipeBuilder().duration(100).EUt(30).input(rotor, Materials.Tin).input(cableGtSingle, Materials.Tin).input(screw, Materials.Tin).input(pipeMedium, Materials.Bronze).inputs(MetaItems.ELECTRIC_MOTOR_LV.getStackForm()).fluidInputs(m.getFluid((int) stackFluid.amount)).outputs(MetaItems.ELECTRIC_PUMP_LV.getStackForm()).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(100).EUt(120).input(rotor, Materials.Bronze).input(cableGtSingle, Materials.Copper).input(screw, Materials.Bronze).input(pipeMedium, Materials.Steel).inputs(MetaItems.ELECTRIC_MOTOR_MV.getStackForm()).fluidInputs(m.getFluid((int) stackFluid.amount)).outputs(MetaItems.ELECTRIC_PUMP_MV.getStackForm()).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(100).EUt(480).input(rotor, Materials.Steel).input(cableGtSingle, Materials.Gold).input(screw, Materials.Steel).input(pipeMedium, Materials.StainlessSteel).inputs(MetaItems.ELECTRIC_MOTOR_HV.getStackForm()).fluidInputs(m.getFluid((int) stackFluid.amount)).outputs(MetaItems.ELECTRIC_PUMP_HV.getStackForm()).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(100).EUt(1920).input(rotor, Materials.StainlessSteel).input(cableGtSingle, Materials.Aluminium).input(screw, Materials.StainlessSteel).input(pipeMedium, Materials.Titanium).inputs(MetaItems.ELECTRIC_MOTOR_EV.getStackForm()).fluidInputs(m.getFluid((int) stackFluid.amount)).outputs(MetaItems.ELECTRIC_PUMP_EV.getStackForm()).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(100).EUt(7680).input(rotor, Materials.TungstenSteel).input(cableGtSingle, Materials.Tungsten).input(screw, Materials.TungstenSteel).input(pipeMedium, Materials.TungstenSteel).inputs(MetaItems.ELECTRIC_MOTOR_IV.getStackForm()).fluidInputs(m.getFluid((int) stackFluid.amount)).outputs(MetaItems.ELECTRIC_PUMP_IV.getStackForm()).buildAndRegister();
		}

		//Chemical Reactor Cracking
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Ethane.getFluid(1000)).fluidOutputs(Materials.HydroCrackedEthane.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Ethylene.getFluid(1000)).fluidOutputs(Materials.HydroCrackedEthylene.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Propene.getFluid(1000)).fluidOutputs(Materials.HydroCrackedPropene.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Propane.getFluid(1000)).fluidOutputs(Materials.HydroCrackedPropane.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.LightFuel.getFluid(1000)).fluidOutputs(Materials.HydroCrackedLightFuel.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Butane.getFluid(1000)).fluidOutputs(Materials.HydroCrackedButane.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Naphtha.getFluid(1000)).fluidOutputs(Materials.HydroCrackedNaphtha.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.HeavyFuel.getFluid(1000)).fluidOutputs(Materials.HydroCrackedHeavyFuel.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Gas.getFluid(1000)).fluidOutputs(Materials.HydroCrackedGas.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Butene.getFluid(1000)).fluidOutputs(Materials.HydroCrackedButene.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Butadiene.getFluid(1000)).fluidOutputs(Materials.HydroCrackedButadiene.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Ethane.getFluid(1000)).fluidOutputs(Materials.SteamCrackedEthane.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Ethylene.getFluid(1000)).fluidOutputs(Materials.SteamCrackedEthylene.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Propene.getFluid(1000)).fluidOutputs(Materials.SteamCrackedPropene.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Propane.getFluid(1000)).fluidOutputs(Materials.SteamCrackedPropane.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.LightFuel.getFluid(1000)).fluidOutputs(Materials.CrackedLightFuel.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Butane.getFluid(1000)).fluidOutputs(Materials.SteamCrackedButane.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Naphtha.getFluid(1000)).fluidOutputs(Materials.SteamCrackedNaphtha.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.HeavyFuel.getFluid(1000)).fluidOutputs(Materials.CrackedHeavyFuel.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Gas.getFluid(1000)).fluidOutputs(Materials.SteamCrackedGas.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Butene.getFluid(1000)).fluidOutputs(Materials.SteamCrackedButene.getFluid(1000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Butadiene.getFluid(1000)).fluidOutputs(Materials.SteamCrackedButadiene.getFluid(1000)).buildAndRegister();

		//Lubricant Distillation Recipes
		DISTILLATION_RECIPES.recipeBuilder()
				.fluidInputs(GAMaterials.FISH_OIL.getFluid(24))
				.fluidOutputs(Materials.Lubricant.getFluid(12))
				.duration(16).EUt(96).buildAndRegister();

		//Fish Oil Extractor Recipes
		FLUID_EXTRACTION_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.FISH))
				.fluidOutputs(GAMaterials.FISH_OIL.getFluid(40))
				.duration(160).EUt(4).buildAndRegister();

		FLUID_EXTRACTION_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.FISH, 1, 1))
				.fluidOutputs(GAMaterials.FISH_OIL.getFluid(60))
				.duration(160).EUt(4).buildAndRegister();

		FLUID_EXTRACTION_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.FISH, 1, 2))
				.fluidOutputs(GAMaterials.FISH_OIL.getFluid(70))
				.duration(160).EUt(4).buildAndRegister();

		FLUID_EXTRACTION_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.FISH, 1, 3))
				.fluidOutputs(GAMaterials.FISH_OIL.getFluid(30))
				.duration(160).EUt(4).buildAndRegister();

		//Mince Meat Recipes
		MACERATOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.PORKCHOP))
				.output(dustSmall, GAMaterials.MEAT, 6)
				.duration(60).EUt(16).buildAndRegister();

		MACERATOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.BEEF))
				.output(dustSmall, GAMaterials.MEAT, 6)
				.duration(60).EUt(16).buildAndRegister();

		MACERATOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.RABBIT))
				.output(dustSmall, GAMaterials.MEAT, 6)
				.duration(60).EUt(16).buildAndRegister();

		MACERATOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.CHICKEN))
				.output(dust, GAMaterials.MEAT)
				.duration(40).EUt(16).buildAndRegister();

		MACERATOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.MUTTON))
				.output(dust, GAMaterials.MEAT)
				.duration(40).EUt(16).buildAndRegister();

		//Raw Growth Medium
		MIXER_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.SUGAR, 4))
				.input(dust, GAMaterials.MEAT)
				.input(dustTiny, Materials.Salt)
				.fluidInputs(Materials.DistilledWater.getFluid(4000))
				.fluidOutputs(GAMaterials.RAW_GROWTH_MEDIUM.getFluid(4000))
				.duration(160).EUt(16).buildAndRegister();

		//Sterile Growth Medium
		FLUID_HEATER_RECIPES.recipeBuilder()
				.fluidInputs(GAMaterials.RAW_GROWTH_MEDIUM.getFluid(500))
				.circuitMeta(1)
				.fluidOutputs(GAMaterials.STERILE_GROWTH_MEDIUM.getFluid(500))
				.duration(30).EUt(24).buildAndRegister();

		//UU-Matter
		MIXER_RECIPES.recipeBuilder()
				.fluidInputs(GAMaterials.POSITIVE_MATTER.getFluid(10))
				.fluidInputs(GAMaterials.NEUTRAL_MATTER.getFluid(10))
				.fluidOutputs(Materials.UUMatter.getFluid(20))
				.duration(30).EUt(480).buildAndRegister();

		//Stem Cells
		EXTRACTOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.EGG))
				.chancedOutput(GAMetaItems.STEM_CELLS.getStackForm(), 1500, 500)
				.duration(600).EUt(512).buildAndRegister();

		//Star Recipes
		CHEMICAL_RECIPES.recipeBuilder()
				.input(ingot, Materials.Plutonium, 3)
				.outputs(OreDictUnifier.get(dust, Materials.Plutonium, 3))
				.fluidOutputs(Materials.Radon.getFluid(50))
				.duration(60000).EUt(8).buildAndRegister();

		AUTOCLAVE_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.NETHER_STAR))
				.fluidInputs(GAMaterials.NEUTRONIUM.getFluid(288))
				.outputs(MetaItems.GRAVI_STAR.getStackForm())
				.duration(480).EUt(7680).buildAndRegister();

		//Explosive Recipes
		removeCraftingRecipes(new ItemStack(Blocks.TNT));
		removeCraftingRecipes(MetaItems.DYNAMITE.getStackForm());
		CHEMICAL_RECIPES.recipeBuilder()
				.inputs(new ItemStack(Items.PAPER))
				.inputs(new ItemStack(Items.STRING))
				.fluidInputs(Materials.Glyceryl.getFluid(500))
				.outputs(MetaItems.DYNAMITE.getStackForm())
				.duration(160).EUt(4).buildAndRegister();

		//Redstone and glowstone melting
		FLUID_EXTRACTION_RECIPES.recipeBuilder()
				.input(dust, Materials.Redstone)
				.fluidOutputs(Materials.Redstone.getFluid(144))
				.duration(80).EUt(32).buildAndRegister();

		FLUID_EXTRACTION_RECIPES.recipeBuilder()
				.input(dust, Materials.Glowstone)
				.fluidOutputs(Materials.Glowstone.getFluid(144))
				.duration(80).EUt(32).buildAndRegister();

		//Misc Recipe Patches
		COMPRESSOR_RECIPES.recipeBuilder()
				.input(dust, Materials.NetherQuartz)
				.outputs(OreDictUnifier.get(plate, Materials.NetherQuartz))
				.duration(400).EUt(2).buildAndRegister();

		COMPRESSOR_RECIPES.recipeBuilder()
				.input(dust, Materials.CertusQuartz)
				.outputs(OreDictUnifier.get(plate, Materials.CertusQuartz))
				.duration(400).EUt(2).buildAndRegister();

	}

	/**
	 * Deals with modifications of recipes from mutliblocks, or changing the recipes of multiblock components
	 */
	public static void miscMultiblockRecipes() {

		//Pyrolyse Oven Recipes
		PYROLYSE_RECIPES.recipeBuilder().inputs(new ItemStack(Items.SUGAR, 23)).circuitMeta(1).outputs(OreDictUnifier.get(dust, Materials.Charcoal, 12)).fluidOutputs(Materials.Water.getFluid(1500)).duration(640).EUt(64).buildAndRegister();
		PYROLYSE_RECIPES.recipeBuilder().inputs(new ItemStack(Items.SUGAR, 23)).circuitMeta(2).fluidInputs(Materials.Nitrogen.getFluid(400)).outputs(OreDictUnifier.get(dust, Materials.Charcoal, 12)).fluidOutputs(Materials.Water.getFluid(1500)).duration(320).EUt(96).buildAndRegister();


		GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(OreDictUnifier.get(frameGt, Materials.Tritanium, 4), MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(8), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(32), MetaItems.SMD_RESISTOR.getStackForm(32), MetaItems.SMD_TRANSISTOR.getStackForm(32), MetaItems.SMD_DIODE.getStackForm(32), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(16), OreDictUnifier.get(wireGtDouble, Tier.Superconductor, 16), OreDictUnifier.get(foil, Materials.SiliconeRubber, 64)).fluidInputs(Materials.SolderingAlloy.getFluid(2880), Materials.Water.getFluid(10000)).outputs(MetaItems.WETWARE_MAINFRAME_MAX.getStackForm()).duration(2000).EUt(300000).buildAndRegister();

		GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(MetaItems.WETWARE_BOARD.getStackForm(), GAMetaItems.STEM_CELLS.getStackForm(8), MetaItems.GLASS_TUBE.getStackForm(8), OreDictUnifier.get(foil, Materials.SiliconeRubber, 64)).input(plate, Materials.Gold, 8).input(plate, Materials.StainlessSteel, 4).fluidInputs(GAMaterials.STERILE_GROWTH_MEDIUM.getFluid(100), Materials.UUMatter.getFluid(20), Materials.DistilledWater.getFluid(4000)).outputs(GAMetaItems.NEURO_PROCESSOR.getStackForm(8)).duration(200).EUt(20000).buildAndRegister();

		//Assembly Line Related Recipes
		ModHandler.addShapedRecipe("assline_casing", GAMetaBlocks.MUTLIBLOCK_CASING.getItemVariant(GAMultiblockCasing.CasingType.TUNGSTENSTEEL_GEARBOX_CASING, 2), "PhP", "AFA", "PwP", 'P', "plateSteel", 'A', MetaItems.ROBOT_ARM_IV.getStackForm(), 'F', OreDictUnifier.get(frameGt, Materials.TungstenSteel));
		removeRecipeByName("gregtech:casing_assembler_casing");
		ModHandler.addShapedRecipe("ga_assembler_casing", MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.ASSEMBLER_CASING, 3), "CCC", "CFC", "CMC", 'C', "circuitElite", 'F', "frameGtTungstenSteel", 'M', MetaItems.ELECTRIC_MOTOR_IV.getStackForm());
		//Assembly Line Casing
		ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(MetaItems.ROBOT_ARM_IV.getStackForm(2), OreDictUnifier.get(plate, Materials.Steel, 4), OreDictUnifier.get(frameGt, Materials.TungstenSteel))
				.outputs(GAMetaBlocks.MUTLIBLOCK_CASING.getItemVariant(GAMultiblockCasing.CasingType.TUNGSTENSTEEL_GEARBOX_CASING, 2))
				.duration(100).EUt(8000).buildAndRegister();


		//Should this be replaced with direct removals?
		List<Recipe> recipes = new ArrayList<Recipe>();
		for (Recipe recipe : ASSEMBLER_RECIPES.getRecipeList()) {
			if (recipe.getOutputs().get(0).isItemEqual(MetaItems.WETWARE_PROCESSOR_LUV.getStackForm()) || recipe.getOutputs().get(0).isItemEqual(MetaItems.WETWARE_PROCESSOR_ASSEMBLY_ZPM.getStackForm())) {
				recipes.add(recipe);
			}
		}
		recipes.forEach(ASSEMBLER_RECIPES::removeRecipe);

		ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(28000).inputs(GAMetaItems.NEURO_PROCESSOR.getStackForm(), MetaItems.CRYSTAL_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.SMD_CAPACITOR.getStackForm(2), MetaItems.SMD_TRANSISTOR.getStackForm(2)).input(wireFine, Materials.YttriumBariumCuprate, 2).fluidInputs(Materials.SolderingAlloy.getFluid(72)).outputs(MetaItems.WETWARE_PROCESSOR_LUV.getStackForm()).buildAndRegister();
		ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(28000).inputs(GAMetaItems.NEURO_PROCESSOR.getStackForm(), MetaItems.CRYSTAL_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.SMD_CAPACITOR.getStackForm(2), MetaItems.SMD_TRANSISTOR.getStackForm(2)).input(wireFine, Materials.YttriumBariumCuprate, 2).fluidInputs(Materials.Tin.getFluid(144)).outputs(MetaItems.WETWARE_PROCESSOR_LUV.getStackForm()).buildAndRegister();

		ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(30000).inputs(MetaItems.WETWARE_BOARD.getStackForm(), MetaItems.WETWARE_PROCESSOR_LUV.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4)).input(wireFine, Materials.YttriumBariumCuprate, 6).fluidInputs(Materials.SolderingAlloy.getFluid(72)).outputs(MetaItems.WETWARE_PROCESSOR_ASSEMBLY_ZPM.getStackForm()).buildAndRegister();
		ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(30000).inputs(MetaItems.WETWARE_BOARD.getStackForm(), MetaItems.WETWARE_PROCESSOR_LUV.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4)).input(wireFine, Materials.YttriumBariumCuprate, 6).fluidInputs(Materials.Tin.getFluid(144)).outputs(MetaItems.WETWARE_PROCESSOR_ASSEMBLY_ZPM.getStackForm()).buildAndRegister();

		ItemStack last_bat = (GAConfig.GT5U.replaceUVwithMAXBat ? GAMetaItems.MAX_BATTERY : MetaItems.ZPM2).getStackForm();

		if (GAConfig.GT5U.enableZPMandUVBats) {
			GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(OreDictUnifier.get(plate, Materials.Europium, 16), MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(4), MetaItems.ENERGY_LAPOTRONIC_ORB2.getStackForm(8), MetaItems.FIELD_GENERATOR_LUV.getStackForm(2), MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(64), MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(64), MetaItems.SMD_DIODE.getStackForm(8), OreDictUnifier.get(cableGtSingle, Materials.Naquadah, 32)).fluidInputs(Materials.SolderingAlloy.getFluid(2880), Materials.Water.getFluid(8000)).outputs(GAMetaItems.ENERGY_MODULE.getStackForm()).duration(2000).EUt(100000).buildAndRegister();

			GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(OreDictUnifier.get(plate, Materials.Americium, 16), MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(4), GAMetaItems.ENERGY_MODULE.getStackForm(8), MetaItems.FIELD_GENERATOR_ZPM.getStackForm(2), MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64), MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64), MetaItems.SMD_DIODE.getStackForm(16), OreDictUnifier.get(cableGtSingle, Materials.NaquadahAlloy, 32)).fluidInputs(Materials.SolderingAlloy.getFluid(2880), Materials.Water.getFluid(16000)).outputs(GAMetaItems.ENERGY_CLUSTER.getStackForm()).duration(2000).EUt(200000).buildAndRegister();

			GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(OreDictUnifier.get(plate, GAMaterials.NEUTRONIUM, 16), MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(4), GAMetaItems.ENERGY_CLUSTER.getStackForm(8), MetaItems.FIELD_GENERATOR_UV.getStackForm(2), MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64), MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64), MetaItems.SMD_DIODE.getStackForm(16), OreDictUnifier.get(wireGtSingle, Tier.Superconductor, 32)).fluidInputs(Materials.SolderingAlloy.getFluid(2880), Materials.Water.getFluid(16000), Materials.Naquadria.getFluid(1152)).outputs(last_bat).duration(2000).EUt(300000).buildAndRegister();
		}
		else {
			GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(OreDictUnifier.get(plate, GAMaterials.NEUTRONIUM, 16), MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(4), MetaItems.ENERGY_LAPOTRONIC_ORB2.getStackForm(8), MetaItems.FIELD_GENERATOR_UV.getStackForm(2), MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64), MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64), MetaItems.SMD_DIODE.getStackForm(16), OreDictUnifier.get(wireGtSingle, Tier.Superconductor, 32)).fluidInputs(Materials.SolderingAlloy.getFluid(2880), Materials.Water.getFluid(16000)).outputs(last_bat).duration(2000).EUt(300000).buildAndRegister();
		}

		GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.FUSION_COIL), OreDictUnifier.get(plate, Materials.Plutonium241), OreDictUnifier.get(plate, Materials.NetherStar), MetaItems.FIELD_GENERATOR_IV.getStackForm(2), MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(32), OreDictUnifier.get(wireGtSingle, Tier.Superconductor, 32)).input(circuit, Tier.Ultimate, 4).fluidInputs(Materials.SolderingAlloy.getFluid(2880)).outputs(GATileEntities.FUSION_REACTOR[0].getStackForm()).duration(1000).EUt(30000).buildAndRegister();

		GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.FUSION_COIL), OreDictUnifier.get(plate, Materials.Europium, 4), MetaItems.FIELD_GENERATOR_LUV.getStackForm(2), MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(48), OreDictUnifier.get(wireGtDouble, Tier.Superconductor, 32)).input(circuit, Tier.Superconductor, 4).fluidInputs(Materials.SolderingAlloy.getFluid(2880)).outputs(GATileEntities.FUSION_REACTOR[1].getStackForm()).duration(1000).EUt(60000).buildAndRegister();

		GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.FUSION_COIL), MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(4), OreDictUnifier.get(plate, Materials.Americium, 4), MetaItems.FIELD_GENERATOR_ZPM.getStackForm(2), MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64), OreDictUnifier.get(wireGtQuadruple, Tier.Superconductor, 32)).fluidInputs(Materials.SolderingAlloy.getFluid(2880)).outputs(GATileEntities.FUSION_REACTOR[2].getStackForm()).duration(1000).EUt(90000).buildAndRegister();


		//Fusion Recipes
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Deuterium.getFluid(125), Materials.Tritium.getFluid(125)).fluidOutputs(Materials.Helium.getPlasma(125)).duration(16).EUt(4096).EUToStart(40000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Deuterium.getFluid(125), Materials.Helium3.getFluid(125)).fluidOutputs(Materials.Helium.getPlasma(125)).duration(16).EUt(2048).EUToStart(60000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Carbon.getFluid(125), Materials.Helium3.getFluid(125)).fluidOutputs(Materials.Oxygen.getPlasma(125)).duration(32).EUt(4096).EUToStart(80000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Beryllium.getFluid(16), Materials.Deuterium.getFluid(375)).fluidOutputs(Materials.Nitrogen.getPlasma(175)).duration(16).EUt(16384).EUToStart(180000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Silicon.getFluid(16), Materials.Magnesium.getFluid(16)).fluidOutputs(Materials.Iron.getPlasma(125)).duration(32).EUt(8192).EUToStart(360000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Potassium.getFluid(16), Materials.Fluorine.getFluid(125)).fluidOutputs(Materials.Nickel.getPlasma(125)).duration(16).EUt(32768).EUToStart(480000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Beryllium.getFluid(16), Materials.Tungsten.getFluid(16)).fluidOutputs(Materials.Platinum.getFluid(16)).duration(32).EUt(32768).EUToStart(150000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Neodymium.getFluid(16), Materials.Hydrogen.getFluid(48)).fluidOutputs(Materials.Europium.getFluid(16)).duration(64).EUt(24576).EUToStart(150000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Lutetium.getFluid(16), Materials.Chrome.getFluid(16)).fluidOutputs(Materials.Americium.getFluid(16)).duration(96).EUt(49152).EUToStart(200000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Plutonium.getFluid(16), Materials.Thorium.getFluid(16)).fluidOutputs(Materials.Naquadah.getFluid(16)).duration(64).EUt(32768).EUToStart(300000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Americium.getFluid(16), Materials.Naquadria.getFluid(16)).fluidOutputs(GAMaterials.NEUTRONIUM.getFluid(2)).duration(200).EUt(98304).EUToStart(600000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Tungsten.getFluid(16), Materials.Helium.getFluid(16)).fluidOutputs(Materials.Osmium.getFluid(16)).duration(64).EUt(24578).EUToStart(150000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Manganese.getFluid(16), Materials.Hydrogen.getFluid(16)).fluidOutputs(Materials.Iron.getFluid(16)).duration(64).EUt(8192).EUToStart(120000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Mercury.getFluid(16), Materials.Magnesium.getFluid(16)).fluidOutputs(Materials.Uranium.getFluid(16)).duration(64).EUt(49152).EUToStart(240000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Gold.getFluid(16), Materials.Aluminium.getFluid(16)).fluidOutputs(Materials.Uranium.getFluid(16)).duration(64).EUt(49152).EUToStart(240000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Uranium.getFluid(16), Materials.Helium.getFluid(16)).fluidOutputs(Materials.Plutonium.getFluid(16)).duration(128).EUt(49152).EUToStart(480000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Vanadium.getFluid(16), Materials.Hydrogen.getFluid(125)).fluidOutputs(Materials.Chrome.getFluid(16)).duration(64).EUt(24576).EUToStart(140000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Gallium.getFluid(16), Materials.Radon.getFluid(125)).fluidOutputs(Materials.Duranium.getFluid(16)).duration(64).EUt(16384).EUToStart(140000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Titanium.getFluid(48), Materials.Duranium.getFluid(32)).fluidOutputs(Materials.Tritanium.getFluid(16)).duration(64).EUt(32768).EUToStart(200000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Gold.getFluid(16), Materials.Mercury.getFluid(16)).fluidOutputs(Materials.Radon.getFluid(125)).duration(64).EUt(32768).EUToStart(200000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Tantalum.getFluid(16), Materials.Tritium.getFluid(16)).fluidOutputs(Materials.Tungsten.getFluid(16)).duration(16).EUt(24576).EUToStart(200000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Silver.getFluid(16), Materials.Lithium.getFluid(16)).fluidOutputs(Materials.Indium.getFluid(16)).duration(32).EUt(24576).EUToStart(380000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.NaquadahEnriched.getFluid(15), Materials.Radon.getFluid(125)).fluidOutputs(Materials.Naquadria.getFluid(3)).duration(64).EUt(49152).EUToStart(400000000).buildAndRegister();
		FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Lanthanum.getFluid(16), Materials.Silicon.getFluid(16)).fluidOutputs(Materials.Lutetium.getFluid(16)).duration(16).EUt(8192).EUToStart(80000000).buildAndRegister();

		//Fusion Casing Recipes
		ModHandler.addShapedRecipe("fusion_casing_1", MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.FUSION_CASING), "PhP", "PHP", "PwP", 'P', "plateTungstenSteel", 'H', MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.LuV));
		ModHandler.addShapedRecipe("fusion_casing_2", MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.FUSION_CASING_MK2), "PhP", "PHP", "PwP", 'P', "plateAmericium", 'H', MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.FUSION_CASING));
		ASSEMBLER_RECIPES.recipeBuilder().EUt(16).inputs(MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.FUSION_CASING)).input(plate, Materials.Americium, 6).outputs(MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.FUSION_CASING_MK2)).duration(50).buildAndRegister();

		ModHandler.addShapedRecipe("fusion_coil", MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.FUSION_COIL), "CRC", "FSF", "CRC", 'C', "circuitMaster", 'R', MetaItems.NEUTRON_REFLECTOR.getStackForm(), 'F', MetaItems.FIELD_GENERATOR_MV.getStackForm(), 'S', MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.SUPERCONDUCTOR));


		//Misc Blast Furnace Recipes
		BLAST_RECIPES.recipeBuilder().duration(120).EUt(120).blastFurnaceTemp(1200).input(dust, Materials.Pentlandite).fluidInputs(Materials.Oxygen.getFluid(3000)).outputs(OreDictUnifier.get(dust, Materials.Garnierite), OreDictUnifier.get(dustTiny, Materials.Ash)).fluidOutputs(Materials.SulfurDioxide.getFluid(1000)).buildAndRegister();
		BLAST_RECIPES.recipeBuilder().duration(120).EUt(120).blastFurnaceTemp(1200).input(dust, Materials.Pyrite).fluidInputs(Materials.Oxygen.getFluid(3000)).outputs(OreDictUnifier.get(dust, Materials.BandedIron), OreDictUnifier.get(dustTiny, Materials.Ash)).fluidOutputs(Materials.SulfurDioxide.getFluid(1000)).buildAndRegister();

		BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(dust, Materials.SiliconDioxide).input(dust, Materials.Carbon, 2).outputs(OreDictUnifier.get(ingot, Materials.Silicon), OreDictUnifier.get(dustTiny, Materials.Ash)).fluidOutputs(Materials.CarbonMonoxde.getFluid(2000)).buildAndRegister();

		for (MaterialStack oreBlock : ironOres) {
			Material materials = oreBlock.material;
			BLAST_RECIPES.recipeBuilder().duration(500).EUt(120).blastFurnaceTemp(1500).input(ore, materials).input(dust, Materials.Calcite).outputs(OreDictUnifier.get(ingot, Materials.Iron, 3), OreDictUnifier.get(dustSmall, Materials.DarkAsh)).buildAndRegister();
			BLAST_RECIPES.recipeBuilder().duration(500).EUt(120).blastFurnaceTemp(1500).input(ore, materials).input(dustTiny, Materials.Quicklime, 3).outputs(OreDictUnifier.get(ingot, Materials.Iron, 2), OreDictUnifier.get(dustSmall, Materials.DarkAsh)).buildAndRegister();
		}

		/*
		Fluid Regulators
		Note, although this loop starts at IV, it registers recipes for LuV-UV Fluid Regulators
		This is because the PUMPS array starts at LV, while GTValues starts at ULV, so we have to move back
		an index in PUMPs to match up
		 */
		for(int i = GTValues.IV; i <= GTValues.ZPM; i++) {
			ASSEMBLER_RECIPES.recipeBuilder()
					.inputs(MetaItems.PUMPS[i].getStackForm())
					.input(circuit, circuitTiers[i - GTValues.IV], 2)
					.outputs(MetaItems.FLUID_REGULATORS[i].getStackForm())
					.EUt((int) (GTValues.V[i + 1] * 30 / 32)).duration(100).buildAndRegister();
		}

	}

	public static void forestrySupport() {

		//Bio Diesel via Fish Oil
		CHEMICAL_RECIPES.recipeBuilder().duration(600).EUt(30).input(dustTiny, Materials.SodiumHydroxide).fluidInputs(GAMaterials.FISH_OIL.getFluid(6000), Materials.Methanol.getFluid(1000)).fluidOutputs(Materials.Glycerol.getFluid(1000), Materials.BioDiesel.getFluid(6000)).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder().duration(600).EUt(30).input(dustTiny, Materials.SodiumHydroxide).fluidInputs(GAMaterials.FISH_OIL.getFluid(6000), Materials.Ethanol.getFluid(1000)).fluidOutputs(Materials.Glycerol.getFluid(1000), Materials.BioDiesel.getFluid(6000)).buildAndRegister();

		//Electrode Recipes
		if (Loader.isModLoaded("forestry") && GAConfig.GT6.electrodes) {
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_APATITE.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.APATITE, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(stick, Materials.Apatite, 2).input(bolt, Materials.Apatite).input(dustSmall, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_APATITE.getStackForm()).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(stick, Materials.Apatite, 4).input(bolt, Materials.Apatite, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_APATITE.getStackForm(2)).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_BLAZE.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.BLAZE, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(dust, Materials.Blaze, 2).input(dustSmall, Materials.Blaze, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_BLAZE.getStackForm(2)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(400).EUt(24).input(dust, Materials.Blaze, 5).input(dust, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_BLAZE.getStackForm(4)).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_BRONZE.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.BRONZE, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(stick, Materials.Bronze, 2).input(bolt, Materials.Bronze).input(dustSmall, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_BRONZE.getStackForm()).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(stick, Materials.Bronze, 4).input(bolt, Materials.Bronze, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_BRONZE.getStackForm(2)).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_COPPER.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.COPPER, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(stick, Materials.Copper, 2).input(bolt, Materials.Copper).input(dustSmall, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_COPPER.getStackForm()).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(stick, Materials.Copper, 4).input(bolt, Materials.Copper, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_COPPER.getStackForm(2)).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_DIAMOND.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.DIAMOND, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(stick, Materials.Diamond, 2).input(bolt, Materials.Diamond).input(dustSmall, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_DIAMOND.getStackForm()).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(stick, Materials.Diamond, 4).input(bolt, Materials.Diamond, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_DIAMOND.getStackForm(2)).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_EMERALD.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.EMERALD, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(stick, Materials.Emerald, 2).input(bolt, Materials.Emerald).input(dustSmall, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_EMERALD.getStackForm()).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(stick, Materials.Emerald, 4).input(bolt, Materials.Emerald, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_EMERALD.getStackForm(2)).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_ENDER.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.ENDER, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(dust, Materials.Endstone, 2).input(dustSmall, Materials.Endstone, 2).input(dust, Materials.EnderEye).outputs(GAMetaItems.ELECTRODE_ENDER.getStackForm(2)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(400).EUt(24).input(dust, Materials.Endstone, 5).input(dust, Materials.EnderEye, 2).outputs(GAMetaItems.ELECTRODE_ENDER.getStackForm(4)).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_GOLD.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.GOLD, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(stick, Materials.Gold, 2).input(bolt, Materials.Gold).input(dustSmall, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_GOLD.getStackForm()).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(stick, Materials.Gold, 4).input(bolt, Materials.Gold, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_GOLD.getStackForm(2)).buildAndRegister();
			if (Loader.isModLoaded("ic2") || Loader.isModLoaded("binniecore")) {
				ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_IRON.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.IRON, 1)).buildAndRegister();
				FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(stick, Materials.Iron, 2).input(bolt, Materials.Iron).input(dustSmall, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_IRON.getStackForm()).buildAndRegister();
				FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(stick, Materials.Iron, 4).input(bolt, Materials.Iron, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_IRON.getStackForm(2)).buildAndRegister();
			}
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_LAPIS.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.LAPIS, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(stick, Materials.Lapis, 2).input(bolt, Materials.Lapis).input(dustSmall, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_LAPIS.getStackForm()).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(stick, Materials.Lapis, 4).input(bolt, Materials.Lapis, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_LAPIS.getStackForm(2)).buildAndRegister();
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_OBSIDIAN.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.OBSIDIAN, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(dust, Materials.Obsidian, 2).input(dustSmall, Materials.Obsidian, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_OBSIDIAN.getStackForm(2)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(400).EUt(24).input(dust, Materials.Obsidian, 5).input(dust, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_OBSIDIAN.getStackForm(4)).buildAndRegister();
			if (Loader.isModLoaded("extrautils2")) {
				ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_ORCHID.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.ORCHID, 1)).buildAndRegister();
				FORMING_PRESS_RECIPES.recipeBuilder().duration(400).EUt(24).inputs(new ItemStack(Blocks.REDSTONE_ORE, 5), OreDictUnifier.get(dust, Materials.Redstone)).outputs(GAMetaItems.ELECTRODE_ORCHID.getStackForm(4)).buildAndRegister();
			}
			if (Loader.isModLoaded("ic2") || Loader.isModLoaded("techreborn")) {
				ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_RUBBER.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.RUBBER, 1)).buildAndRegister();
				FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(stick, Materials.Rubber, 2).input(bolt, Materials.Rubber).input(dustSmall, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_RUBBER.getStackForm()).buildAndRegister();
				FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(stick, Materials.Rubber, 4).input(bolt, Materials.Rubber, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_RUBBER.getStackForm(2)).buildAndRegister();
			}
			ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(GAMetaItems.ELECTRODE_TIN.getStackForm(), OreDictUnifier.get(plate, Materials.Glass)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.TIN, 1)).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(stick, Materials.Tin, 2).input(bolt, Materials.Tin).input(dustSmall, Materials.Redstone, 2).outputs(GAMetaItems.ELECTRODE_TIN.getStackForm()).buildAndRegister();
			FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(stick, Materials.Tin, 4).input(bolt, Materials.Tin, 2).input(dust, Materials.Redstone).outputs(GAMetaItems.ELECTRODE_TIN.getStackForm(2)).buildAndRegister();
		}

	}

	//TODO, see if this can be broken down at all
	public static void generatedRecipes() {
		List<ResourceLocation> recipesToRemove = new ArrayList<>();

		for (IRecipe recipe : CraftingManager.REGISTRY) {
			if (recipe.getIngredients().size() == 9) {
				if (recipe.getIngredients().get(0).getMatchingStacks().length > 0 && Block.getBlockFromItem(recipe.getRecipeOutput().getItem()) != Blocks.AIR) {
					boolean match = true;
					for (int i = 1; i < recipe.getIngredients().size(); i++) {
						if (recipe.getIngredients().get(i).getMatchingStacks().length == 0 || !recipe.getIngredients().get(0).getMatchingStacks()[0].isItemEqual(recipe.getIngredients().get(i).getMatchingStacks()[0])) {
							match = false;
							break;
						}
					}
					if(match) {
						if(GAConfig.GT5U.Remove3x3BlockRecipes) {
							recipesToRemove.add(recipe.getRegistryName());
						}
						else if(GAConfig.Misc.Packager3x3Recipes && !GAMetaItems.hasPrefix(recipe.getRecipeOutput(), "dust", "dustTiny") && recipe.getRecipeOutput().getCount() == 1) {
							PACKER_RECIPES.recipeBuilder()
									.inputs(CountableIngredient.from(recipe.getIngredients().get(0).getMatchingStacks()[0], recipe.getIngredients().size()))
									.notConsumable(GAMetaItems.SCHEMATIC_3X3.getStackForm())
									.outputs(recipe.getRecipeOutput())
									.duration(100).EUt(4).buildAndRegister();
						}

						if(GAConfig.GT5U.GenerateCompressorRecipes) {
							COMPRESSOR_RECIPES.recipeBuilder()
									.inputs(CountableIngredient.from(recipe.getIngredients().get(0).getMatchingStacks()[0], recipe.getIngredients().size()))
									.outputs(recipe.getRecipeOutput())
									.duration(400).EUt(2).buildAndRegister();
						}
					}
				}
			}
			//The initial section here is exactly the same as above, so the sections were combined to avoid iterating twice
			/*if (recipe.getIngredients().size() == 9) {
				if (recipe.getIngredients().get(0).getMatchingStacks().length > 0 && Block.getBlockFromItem(recipe.getRecipeOutput().getItem()) == Blocks.AIR) {
					boolean match = true;
					for (int i = 1; i < recipe.getIngredients().size(); i++) {
						if (recipe.getIngredients().get(i).getMatchingStacks().length == 0 || !recipe.getIngredients().get(0).getMatchingStacks()[0].isItemEqual(recipe.getIngredients().get(i).getMatchingStacks()[0])) {
							match = false;
							break;
						}
					}
					if (match && !recipesToRemove.contains(String.format("%s:%s", recipe.getRegistryName().getNamespace(), recipe.getRegistryName().getPath())) && !GAMetaItems.hasPrefix(recipe.getRecipeOutput(), "dust", "dustTiny") && recipe.getRecipeOutput().getCount() == 1 && GAConfig.Misc.Packager3x3Recipes) {
						PACKER_RECIPES.recipeBuilder().duration(100).EUt(4).inputs(CountableIngredient.from(recipe.getIngredients().get(0).getMatchingStacks()[0], recipe.getIngredients().size())).notConsumable(GAMetaItems.SCHEMATIC_3X3.getStackForm()).outputs(recipe.getRecipeOutput()).buildAndRegister();
					}
				}
			} */
			if (recipe.getIngredients().size() == 4) {
				if (recipe.getIngredients().get(0).getMatchingStacks().length > 0 && Block.getBlockFromItem(recipe.getRecipeOutput().getItem()) != Blocks.QUARTZ_BLOCK) {
					boolean match = true;
					for (int i = 1; i < recipe.getIngredients().size(); i++) {
						if (recipe.getIngredients().get(i).getMatchingStacks().length == 0 || !recipe.getIngredients().get(0).getMatchingStacks()[0].isItemEqual(recipe.getIngredients().get(i).getMatchingStacks()[0])) {
							match = false;
							break;
						}
					}
					if (match && GAConfig.Misc.Packager2x2Recipes && !recipesToRemove.contains(recipe.getRegistryName()) && !GAMetaItems.hasPrefix(recipe.getRecipeOutput(), "dust", "dustSmall") && recipe.getRecipeOutput().getCount() == 1) {
						PACKER_RECIPES.recipeBuilder()
								.inputs(CountableIngredient.from(recipe.getIngredients().get(0).getMatchingStacks()[0], recipe.getIngredients().size()))
								.notConsumable(GAMetaItems.SCHEMATIC_2X2.getStackForm())
								.outputs(recipe.getRecipeOutput())
								.duration(100).EUt(4).buildAndRegister();
					}
				}
			}
			if(recipe.getIngredients().size() == 1 && recipe.getIngredients().get(0).getMatchingStacks().length > 0 && recipe.getRecipeOutput().getCount() == 9) {

				if(GAConfig.Misc.Unpackager3x3Recipes && !recipesToRemove.contains(recipe.getRegistryName())) {
					UNPACKER_RECIPES.recipeBuilder()
							.inputs(recipe.getIngredients().get(0).getMatchingStacks()[0])
							.inputs(new CountableIngredient(new IntCircuitIngredient(1), 0))
							.outputs(recipe.getRecipeOutput())
							.duration(100).EUt(8).buildAndRegister();
				}

				if(Block.getBlockFromItem(recipe.getIngredients().get(0).getMatchingStacks()[0].getItem()) != Blocks.AIR && Block.getBlockFromItem(recipe.getIngredients().get(0).getMatchingStacks()[0].getItem()) != Blocks.SLIME_BLOCK) {

					boolean isIngot = false;
					for (int i : OreDictionary.getOreIDs(recipe.getRecipeOutput())) {
						if (OreDictionary.getOreName(i).startsWith("ingot")) {
							isIngot = true;
							break;
						}
					}

					if(GAConfig.GT5U.RemoveBlockUncraftingRecipes) {
						recipesToRemove.add(recipe.getRegistryName());
					}
					if(!isIngot) {
						FORGE_HAMMER_RECIPES.recipeBuilder()
								.inputs(recipe.getIngredients().get(0).getMatchingStacks()[0])
								.outputs(recipe.getRecipeOutput())
								.duration(100).EUt(24).buildAndRegister();
					}
				}
			/*if (recipe.getIngredients().size() == 1 && recipe.getIngredients().get(0).getMatchingStacks().length > 0 && recipe.getRecipeOutput().getCount() == 9) {
				if (!recipesToRemove.contains(String.format("%s:%s", recipe.getRegistryName().getNamespace(), recipe.getRegistryName().getPath())) && GAConfig.Misc.Unpackager3x3Recipes) {
					UNPACKER_RECIPES.recipeBuilder().duration(100).EUt(8).inputs(recipe.getIngredients().get(0).getMatchingStacks()[0]).inputs(new CountableIngredient(new IntCircuitIngredient(1), 0)).outputs(recipe.getRecipeOutput()).buildAndRegister();
				} */
			}
		}

		for (ResourceLocation r : recipesToRemove) {
			removeRecipeByName(r);
		}
		recipesToRemove.clear();

		if (GAConfig.GT5U.GenerateCompressorRecipes) {
			removeRecipeByName("minecraft:glowstone");
			removeRecipeByName("minecraft:quartz_block");
			removeRecipeByName("gregtech:nether_quartz_block_to_nether_quartz");
			FORGE_HAMMER_RECIPES.recipeBuilder()
					.inputs(OreDictUnifier.get(block, Materials.NetherQuartz))
					.outputs(OreDictUnifier.get(gem, Materials.NetherQuartz, 4))
					.duration(100).EUt(24).buildAndRegister();
		}

		//Generate Plank Recipes, TODO check against the config option in GTCE
		for(IRecipe recipe : CraftingManager.REGISTRY) {
			if(recipe.getRecipeOutput().isEmpty()) {
				continue;
			}
			//for(int i : OreDictionary.getOreIDs(recipe.getRecipeOutput())) {
			if(GAMetaItems.hasPrefix(recipe.getRecipeOutput(), "plankWood") && recipe.getIngredients().size() == 1 && recipe.getRecipeOutput().getCount() == 4) {
				if(GAConfig.GT5U.GeneratedSawingRecipes) {
					removeRecipeByName(recipe.getRegistryName());
					ModHandler.addShapelessRecipe("log_to_4_" + recipe.getRecipeOutput().toString(), GTUtility.copyAmount(4, recipe.getRecipeOutput()), recipe.getIngredients().get(0).getMatchingStacks()[0], ToolDictNames.craftingToolSaw);
					ModHandler.addShapelessRecipe("log_to_2_" + recipe.getRecipeOutput().toString(), GTUtility.copyAmount(2, recipe.getRecipeOutput()), recipe.getIngredients().get(0).getMatchingStacks()[0]);
				}
				CUTTER_RECIPES.recipeBuilder()
						.inputs(recipe.getIngredients().get(0).getMatchingStacks()[0])
						.fluidInputs(Materials.Lubricant.getFluid(1))
						.outputs(GTUtility.copyAmount(6, recipe.getRecipeOutput()), OreDictUnifier.get(dust, Materials.Wood, 2))
						.duration(200).EUt(8).buildAndRegister();
			}

			if(GAMetaItems.hasPrefix(recipe.getRecipeOutput(), "slabWood") && recipe.getRecipeOutput().getCount() == 6) {
				CUTTER_RECIPES.recipeBuilder()
						.inputs(recipe.getIngredients().get(0).getMatchingStacks()[0])
						.outputs(GTUtility.copyAmount(2, recipe.getRecipeOutput()))
						.duration(50).EUt(4).buildAndRegister();
			}
			//}
		}

		//Disable Wood To Charcoal Recipes
		//TODO check if there is any difference between the two formats
		List<ItemStack> allWoodLogs = OreDictionary.getOres("logWood");
		//List<ItemStack> allWoodLogs = OreDictionary.getOres("logWood").stream().flatMap(stack -> ModHandler.getAllSubItems(stack).stream()).collect(Collectors.toList());

		for (ItemStack stack : allWoodLogs) {
			ItemStack smeltingOutput = ModHandler.getSmeltingOutput(stack);
			if (!smeltingOutput.isEmpty() && smeltingOutput.getItem() == Items.COAL && smeltingOutput.getMetadata() == 1 && GAConfig.GT5U.DisableLogToCharcoalSmelting) {
				ItemStack woodStack = stack.copy();
				woodStack.setItemDamage(OreDictionary.WILDCARD_VALUE);
				removeFurnaceRecipe(woodStack);
			}
		}
	}
}
