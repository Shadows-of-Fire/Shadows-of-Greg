package gregicadditions.recipes;

import forestry.core.ModuleCore;
import forestry.core.items.EnumElectronTube;
import gregicadditions.GAConfig;
import gregicadditions.item.GAMetaItems;
import gregicadditions.item.GATransparentCasing;
import gregicadditions.machines.GATileEntities;
import gregtech.api.items.ToolDictNames;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.Recipe;
import gregtech.api.unification.material.type.FluidMaterial;
import gregtech.api.unification.material.type.IngotMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.material.type.SolidMaterial;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.common.blocks.BlockMultiblockCasing.MultiblockCasingType;
import gregtech.common.items.MetaItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static gregicadditions.GAMaterials.*;
import static gregicadditions.item.GAMultiblockCasing.CasingType.*;
import static gregicadditions.item.GAOreDictNames.*;
import static gregicadditions.recipes.GACraftingComponents.*;
import static gregicadditions.recipes.GARecipeMaps.*;
import static gregtech.api.GTValues.*;
import static gregtech.api.recipes.CountableIngredient.from;
import static gregtech.api.recipes.ModHandler.Substitution.sub;
import static gregtech.api.recipes.RecipeMaps.*;
import static gregtech.api.unification.OreDictUnifier.get;
import static gregtech.api.unification.material.MarkerMaterials.Tier.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.blocks.BlockMachineCasing.*;
import static gregtech.common.blocks.BlockMultiblockCasing.MultiblockCasingType.*;
import static gregtech.common.blocks.BlockWireCoil.CoilType.*;
import static gregtech.loaders.recipe.MetaTileEntityLoader.*;

public class GARecipeAddition {

	private static final MaterialStack[] cableFluids = {
		new MaterialStack(Rubber, 144),
		new MaterialStack(StyreneButadieneRubber, 108),
		new MaterialStack(SiliconeRubber, 72)
	};

	private static final MaterialStack[] cableDusts = {
		new MaterialStack(Polydimethylsiloxane, 1),
		new MaterialStack(PolyvinylChloride, 1)
	};

	private static final MaterialStack[] firstMetal = {
		new MaterialStack(Iron, 1),
		new MaterialStack(Nickel, 1),
		new MaterialStack(Invar, 2),
		new MaterialStack(Steel, 2),
		new MaterialStack(StainlessSteel, 3),
		new MaterialStack(Titanium, 3),
		new MaterialStack(Tungsten, 4),
		new MaterialStack(TungstenSteel, 5)
	};

	private static final MaterialStack[] lastMetal = {
		new MaterialStack(Tin, 0),
		new MaterialStack(Zinc, 0),
		new MaterialStack(Aluminium, 1)
	};

	private static final MaterialStack[] ironOres = {
		new MaterialStack(Pyrite, 1),
		new MaterialStack(BrownLimonite, 1),
		new MaterialStack(YellowLimonite, 1),
		new MaterialStack(Magnetite, 1),
		new MaterialStack(Iron, 1)
	};

	/** Materials to skip when handling {@link GAConfig.GT6#BendingRings} */
	private static final Material[] ringExclusions = {
		Rubber,
		StyreneButadieneRubber,
		SiliconeRubber
	};


	/** Materials excluded from {@link GAConfig.GT5U#CablesGT5U} recipes */
	private static final IngotMaterial[] cableExclusions5u = {
		RedAlloy,
		Cobalt,
		Zinc,
		SolderingAlloy,
		Tin,
		Lead
	};

	/** Materials relevant to {@link GAConfig.GT5U#CablesGT5U} assembler recipes */
	private static final IngotMaterial[] assembler5u = {
		Tungsten,
		Osmium,
		Platinum,
		TungstenSteel,
		Graphene,
		VanadiumGallium,
		HSSG,
		YttriumBariumCuprate,
		NiobiumTitanium,
		Naquadah,
		NaquadahEnriched,
		Duranium,
		NaquadahAlloy
	};

	private static final OrePrefix[] cableThicknesses = {
		cableGtSingle,
		cableGtDouble,
		cableGtQuadruple,
		cableGtOctal,
		cableGtHex
	};

	private static final OrePrefix[] wireThicknesses = {
		wireGtSingle,
		wireGtDouble,
		wireGtQuadruple,
		wireGtOctal,
		wireGtHex
	};

	public static void init() {

		FLUID_SOLIDFICATION_RECIPES
			.recipeBuilder()
			.output(MetaItems.GLASS_TUBE)
			.notConsumable(MetaItems.SHAPE_MOLD_BALL)
			.fluidInputs(Glass.getFluid(144))
			.duration(80).EUt(16)
			.buildAndRegister();

		COMPRESSOR_RECIPES
			.recipeBuilder()
			.output(Blocks.GLOWSTONE)
			.input(Items.GLOWSTONE_DUST, 4)
			.duration(40).EUt(16)
			.buildAndRegister();

		gtnhBricks();

		//GT5U Misc Recipes
		ModHandler.addSmeltingRecipe(new ItemStack(Items.SLIME_BALL), MetaItems.RUBBER_DROP.getStackForm());
		ModHandler.removeRecipeByName(new ResourceLocation("minecraft:bone_meal_from_bone"));
		FORGE_HAMMER_RECIPES
			.recipeBuilder()
			.output(Items.DYE, 4, 15)
			.input(Items.BONE)
			.duration(16).EUt(10)
			.buildAndRegister();

		gt6Bending();
		reinforcedGlass();
		tieredComponents();
		chemReactorCracking();
		fluidRecipes();
		blastFurnaceRecipes();
		minceMeatRecipes();
		ashRecipes();
		assemblyLineRecipes();
	}

	private static void gtnhBricks() {

		//GTNH Bricks
		ModHandler.removeFurnaceSmelting(new ItemStack(Items.CLAY_BALL, 1, OreDictionary.WILDCARD_VALUE));
		ModHandler.removeFurnaceSmelting(MetaItems.COMPRESSED_CLAY.getStackForm());
		ModHandler.addSmeltingRecipe(MetaItems.COMPRESSED_CLAY.getStackForm(), new ItemStack(Items.BRICK));
		ALLOY_SMELTER_RECIPES
			.recipeBuilder()
			.output(Items.BRICK)
			.input(Items.CLAY_BALL)
			.notConsumable(MetaItems.SHAPE_MOLD_INGOT)
			.duration(200).EUt(2)
			.buildAndRegister();

		ModHandler.addShapelessRecipe(
			"clay_brick",
			MetaItems.COMPRESSED_CLAY.getStackForm(),
			new ItemStack(Items.CLAY_BALL),
			MetaItems.WOODEN_FORM_BRICK.getStackForm());

		ModHandler.addShapedRecipe(
			"eight_clay_brick",
			MetaItems.COMPRESSED_CLAY.getStackForm(8),
			new String[] {
				"BBB",
				"BFB",
				"BBB"
			},
			sub('B', Items.CLAY_BALL),
			sub('F', MetaItems.WOODEN_FORM_BRICK));

		ModHandler.addShapedRecipe(
			"coke_brick",
			GAMetaItems.COMPRESSED_COKE_CLAY.getStackForm(3),
			new String[] {
				"BBB",
				"SFS",
				"SSS"
			},
			sub('B', Items.CLAY_BALL),
			sub('S', Blocks.SAND),
			sub('F', MetaItems.WOODEN_FORM_BRICK));

		ModHandler.addSmeltingRecipe(GAMetaItems.COMPRESSED_COKE_CLAY.getStackForm(), MetaItems.COKE_OVEN_BRICK.getStackForm());

	}

	public static void gt6Bending() {
		//GT6 Bending
		if (GAConfig.GT6.BendingCurvedPlates && GAConfig.GT6.BendingCylinders && GAConfig.GT6.addCurvedPlates) {
			ModHandler.removeRecipeByName(new ResourceLocation("gregtech:iron_bucket"));
			ModHandler.addShapedRecipe(
				"bucket",
				new ItemStack(Items.BUCKET),
				new String[] {
					"ChC",
					" P "
				},
				sub('C', plateCurved, Iron),
				sub('P', plate, Iron));

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.output(Items.BUCKET)
				.input(plateCurved, Iron, 2)
				.input(plate, Iron)
				.duration(200).EUt(4)
				.buildAndRegister();

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.output(Items.BUCKET)
				.input(plateCurved, WroughtIron, 2)
				.input(plate, WroughtIron)
				.duration(200).EUt(4)
				.buildAndRegister();

			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:iron_helmet"));
			ModHandler.addShapedRecipe(
				"iron_helmet",
				new ItemStack(Items.IRON_HELMET),
				new String[] {
					"PPP",
					"ChC"
				},
				sub('P', plate, Iron),
				sub('C', plateCurved, Iron));

			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:iron_chestplate"));
			ModHandler.addShapedRecipe(
				"iron_chestplate",
				new ItemStack(Items.IRON_CHESTPLATE),
				new String[] {
					"PhP",
					"CPC",
					"CPC"
				},
				sub('P', plate, Iron),
				sub('C', plateCurved, Iron));

			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:iron_leggings"));
			ModHandler.addShapedRecipe(
				"iron_leggings",
				new ItemStack(Items.IRON_LEGGINGS),
				new String[] {
					"PCP",
					"ChC",
					"C C"
				},
				sub('P', plate, Iron),
				sub('C', plateCurved, Iron));

			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:iron_boots"));
			ModHandler.addShapedRecipe(
				"iron_boots",
				new ItemStack(Items.IRON_BOOTS),
				new String[] {
					"P P",
					"ChC"
				},
				sub('P', plate, Iron),
				sub('C', plateCurved, Iron));

			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:golden_helmet"));
			ModHandler.addShapedRecipe(
				"golden_helmet",
				new ItemStack(Items.GOLDEN_HELMET),
				new String[] {
					"PPP",
					"ChC"
				},
				sub('P', plate, Gold),
				sub('C', plateCurved, Gold));

			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:golden_chestplate"));
			ModHandler.addShapedRecipe(
				"golden_chestplate",
				new ItemStack(Items.GOLDEN_CHESTPLATE),
				new String[] {
					"PhP",
					"CPC",
					"CPC"
				},
				sub('P', plate, Gold),
				sub('C', plateCurved, Gold));

			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:golden_leggings"));
			ModHandler.addShapedRecipe(
				"golden_leggings",
				new ItemStack(Items.GOLDEN_LEGGINGS),
				new String[] {
					"PCP",
					"ChC",
					"C C"
				},
				sub('P', plate, Gold),
				sub('C', plateCurved, Gold));

			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:golden_boots"));
			ModHandler.addShapedRecipe(
				"golden_boots",
				new ItemStack(Items.GOLDEN_BOOTS),
				new String[] {
					"P P",
					"ChC"
				},
				sub('P', plate, Gold),
				sub('C', plateCurved, Gold));

			ModHandler.addShapedRecipe(
				"chain_helmet",
				new ItemStack(Items.CHAINMAIL_HELMET),
				new String[] {
					"RRR",
					"RhR"
				},
				sub('R', ring, Iron));

			ModHandler.addShapedRecipe(
				"chain_chestplate",
				new ItemStack(Items.CHAINMAIL_CHESTPLATE),
				new String[] {
					"RhR",
					"RRR",
					"RRR"
				},
				sub('R', ring, Iron));

			ModHandler.addShapedRecipe(
				"chain_leggings",
				new ItemStack(Items.CHAINMAIL_LEGGINGS),
				new String[] {
					"RRR",
					"RhR",
					"R R"
				},
				sub('R', ring, Iron));

			ModHandler.addShapedRecipe(
				"chain_boots",
				new ItemStack(Items.CHAINMAIL_BOOTS),
				new String[] {
					"R R",
					"RhR"
				},
				sub('R', ring, Iron));
		}

		for (Material m : Material.MATERIAL_REGISTRY) {
			if (GAConfig.GT6.BendingRings && GAConfig.GT6.BendingCylinders &&
				!get(ring, m).isEmpty() &&
				!get(stick, m).isEmpty() &&
				!Arrays.asList(ringExclusions).contains(m))
			{
				ModHandler.removeRecipes(get(ring, m));
				ModHandler.addShapedRecipe(
					"rod_to_ring_" + m,
					get(ring, m),
					new String[] {
						"hS",
						" C"
					},
					sub('S', stick, m),
					sub('C', craftingToolBendingCylinderSmall));
			}

			if (GAConfig.GT6.BendingCurvedPlates && GAConfig.GT6.BendingCylinders &&
				!get(plateCurved, m).isEmpty())
			{
				ModHandler.addShapedRecipe(
					"curved_plate_" + m,
					get(plateCurved, m),
					new String[] {
						"h",
						"P",
						"C"
					},
					sub('P', plate, m),
					sub('C', craftingToolBendingCylinder));

				ModHandler.addShapedRecipe(
					"flatten_plate_" + m,
					get(plate, m),
					new String[]{
						"h",
						"C"
					},
					sub('C', plateCurved, m));

				BENDER_RECIPES
					.recipeBuilder()
					.output(plateCurved, m)
					.input(plate, m)
					.circuitMeta(0)
					.duration((int) m.getMass()).EUt(24)
					.buildAndRegister();
			}

			if (GAConfig.GT6.BendingRotors && GAConfig.GT6.BendingCylinders &&
				!get(rotor, m).isEmpty() &&
				!get(plateCurved, m).isEmpty())
			{
				ModHandler.removeRecipes(get(rotor, m));
				ModHandler.addShapedRecipe(
					"ga_rotor_" + m,
					get(rotor, m),
					new String[] {
						"ChC",
						"SRf",
						"CdC"
					},
					sub('C', plateCurved, m),
					sub('S', screw, m),
					sub('R', ring, m));

				ASSEMBLER_RECIPES
					.recipeBuilder()
					.output(rotor, m)
					.input(plateCurved, m, 4)
					.input(ring, m)
					.fluidInputs(SolderingAlloy.getFluid(32))
					.duration(240).EUt(24)
					.buildAndRegister();
			}

			if (!get(foil, m).isEmpty()) {
				if (GAConfig.GT6.BendingFoils && GAConfig.GT6.BendingCylinders) {
					ModHandler.addShapedRecipe(
						"foil_" + m,
						get(foil, m, 2),
						new String[] {
							"hPC"
						},
						sub('P', plate, m),
						sub('C', craftingToolBendingCylinder));
				}
				if (GAConfig.GT6.BendingFoilsAutomatic && GAConfig.GT6.BendingCylinders) {
					CLUSTER_MILL_RECIPES
						.recipeBuilder()
						.output(foil, m, 4)
						.input(plate, m)
						.duration((int) m.getMass()).EUt(24)
						.buildAndRegister();
				}
				else {
					BENDER_RECIPES
						.recipeBuilder()
						.output(foil, m, 4)
						.input(plate, m)
						.circuitMeta(4)
						.duration((int) m.getMass()).EUt(24)
						.buildAndRegister();
				}
			}

			if (!get(round, m).isEmpty()) {
				ModHandler.addShapedRecipe(
					"round" + m,
					get(round, m),
					new String[] {
						"fN",
						"N "
					},
					sub('N', nugget, m));
				LATHE_RECIPES
					.recipeBuilder()
					.output(round, m)
					.input(nugget, m)
					.duration(100).EUt(8)
					.buildAndRegister();
			}

			// Bundler
			if(!get(wireGtSingle, m).isEmpty())
				for(int startTier = 0; startTier < 4; startTier++)
					for(int tier = 1; tier < 5 - startTier; tier++)
						BUNDLER_RECIPES
							.recipeBuilder()
							.output(wireThicknesses[startTier + tier], m, 1)
							.input(wireThicknesses[startTier], m, 1 << tier)
							.circuitMeta(tier)
							.buildAndRegister();

			//Cables
			if (GAConfig.GT5U.CablesGT5U &&
				m instanceof IngotMaterial &&
				!get(cableGtSingle, m).isEmpty() &&
				!Arrays.asList(cableExclusions5u).contains(m))
			{
				for (MaterialStack stackFluid : cableFluids) {
					IngotMaterial fluid = (IngotMaterial) stackFluid.material;
					if (Arrays.asList(assembler5u).contains(m)) {
						for(int i = 0; i < cableThicknesses.length; i++) {
							int itemCount = (int) Math.pow(2, i);
							int amount = (int) stackFluid.amount * itemCount;

							ASSEMBLER_RECIPES
								.recipeBuilder()
								.output(cableThicknesses[i], m)
								.input(wireThicknesses[i], m)
								.input(foil, m, itemCount)
								.fluidInputs(fluid.getFluid(amount))
								.circuitMeta(24)
								.duration(150).EUt(8)
								.buildAndRegister();

							ASSEMBLER_RECIPES
								.recipeBuilder()
								.output(cableThicknesses[i], m)
								.input(wireThicknesses[i], m)
								.input(foil, PolyphenyleneSulfide, itemCount)
								.fluidInputs(fluid.getFluid(amount))
								.circuitMeta(24)
								.duration(150).EUt(8)
								.buildAndRegister();
						}

						for (MaterialStack stackDust : cableDusts)
							for(int i = 0; i < cableThicknesses.length; i++) {
								int itemCount = (int) Math.pow(2, i);
								int fluidAmount = (int) (stackFluid.amount * itemCount / 2.);

								ASSEMBLER_RECIPES
									.recipeBuilder()
									.output(cableThicknesses[i], m)
									.input(wireThicknesses[i], m)
									.input(foil, m, itemCount)
									.input(dustSmall, stackDust.material, itemCount)
									.fluidInputs(fluid.getFluid(fluidAmount))
									.duration(150).EUt(8)
									.buildAndRegister();

								ASSEMBLER_RECIPES
									.recipeBuilder()
									.output(cableThicknesses[i], m)
									.input(wireThicknesses[i], m)
									.input(foil, PolyphenyleneSulfide, itemCount)
									.input(dustSmall, stackDust.material, itemCount)
									.fluidInputs(fluid.getFluid(fluidAmount))
									.duration(150).EUt(8)
									.buildAndRegister();
						}
					} else {
						for(int i = 0; i < cableThicknesses.length; i++) {
							int fluidAmount = (int) (stackFluid.amount * Math.pow(2, i));
							ASSEMBLER_RECIPES
								.recipeBuilder()
								.output(cableThicknesses[i], m)
								.input(wireThicknesses[i], m)
								.fluidInputs(fluid.getFluid(fluidAmount))
								.circuitMeta(24)
								.duration(150).EUt(8)
								.buildAndRegister();
						}

						for (MaterialStack stackDust : cableDusts)
							for(int i = 0; i < cableThicknesses.length; i++) {
								int itemCount = (int) Math.pow(2, i);
								int fluidAmount = (int) (stackFluid.amount * itemCount / 2.);
								ASSEMBLER_RECIPES
									.recipeBuilder()
									.output(cableThicknesses[i], m)
									.input(wireThicknesses[i], m)
									.input(dustSmall, stackDust.material, itemCount)
									.fluidInputs(fluid.getFluid(fluidAmount))
									.duration(150).EUt(8)
									.buildAndRegister();
							}
					}
				}
			}

			//GT6 Plate Recipe
			if (GAConfig.GT6.PlateDoubleIngot &&
				m instanceof IngotMaterial &&
				!get(plate, m).isEmpty() &&
				!get(ingotDouble, m).isEmpty())
			{
				ModHandler.removeRecipes(get(plate, m));
				ModHandler.addShapedRecipe(
					"ingot_double_" + m,
					get(ingotDouble, m),
					new String[] {
						"h",
						"I",
						"I"
					},
					sub('I', ingot, m));

				ModHandler.addShapedRecipe(
					"double_ingot_to_plate_" + m,
					get(plate, m),
					new String[] {
						"h",
						"I"
					},
					sub('I', ingotDouble, m));
			}

			if (m instanceof IngotMaterial im &&
				!get(toolHeadBuzzSaw, im).isEmpty() &&
				im.toolDurability != 0 &&
				im.hasFlag(SolidMaterial.MatFlags.GENERATE_GEAR))
			{
				LATHE_RECIPES
					.recipeBuilder()
					.output(toolHeadBuzzSaw, im)
					.input(gear, im)
					.duration((int) im.getAverageMass() * 4)
					.EUt(8 * (im.blastFurnaceTemperature > 2800 ? 30 : 7))
					.buildAndRegister();
			}
		}

		// Wooden Pipes
		if (GAConfig.GT6.BendingPipes && GAConfig.GT6.BendingCylinders && GAConfig.GT6.addCurvedPlates) {
			ModHandler.removeRecipes(get(pipeSmall, Wood));
			ModHandler.removeRecipes(get(pipeMedium, Wood));
			ModHandler.addShapedRecipe(
				"pipe_ga_wood",
				get(pipeMedium, Wood, 2),
				new String[] {
					"PPP",
					"sCh",
					"PPP"
				},
				sub('P', plank, Wood),
				sub('C', craftingToolBendingCylinder));

			ModHandler.addShapedRecipe(
				"pipe_ga_large_wood",
				get(pipeLarge, Wood),
				new String[] {
					"PhP",
					"PCP",
					"PsP"
				},
				sub('P', plank, Wood),
				sub('C', craftingToolBendingCylinder));

			ModHandler.addShapedRecipe(
				"pipe_ga_small_wood",
				get(pipeSmall, Wood, 6),
				new String[] {
					"PsP",
					"PCP",
					"PhP"
				},
				sub('P', plank, Wood),
				sub('C', craftingToolBendingCylinder));
		}

		//Pipes
		if(GAConfig.GT6.BendingPipes)
			for (Material m : Material.MATERIAL_REGISTRY)
				if (!get(pipeMedium, m).isEmpty() &&
					!get(plateCurved, m).isEmpty())
				{
					for(String size : arr("small", "medium", "large"))
						ModHandler.removeRecipeByName(new ResourceLocation(String.format("gregtech:%s_%s_pipe", size, m)));

					if (!get(plateCurved, m).isEmpty()) {
						ModHandler.addShapedRecipe(
							"pipe_ga_" + m,
							get(pipeMedium, m, 2),
							new String[] {
								"PPP",
								"wCh",
								"PPP"
							},
							sub('P', plateCurved, m),
							sub('C', craftingToolBendingCylinder));

						ModHandler.addShapedRecipe(
							"pipe_ga_large_" + m,
							get(pipeLarge, m),
							new String[] {
								"PhP",
								"PCP",
								"PwP"
							},
							sub('P', plateCurved, m),
							sub('C', craftingToolBendingCylinder));

						ModHandler.addShapedRecipe(
							"pipe_ga_small_" + m,
							get(pipeSmall, m, 4),
							new String[] {
								"PwP",
								"PCP",
								"PhP"
							},
							sub('P', plateCurved, m),
							sub('C', craftingToolBendingCylinder));
					}
				}

	}

	private static void reinforcedGlass() {
		//Reinforced Glass
		int multiplier2;
		for (MaterialStack metal1 : firstMetal) {
			IngotMaterial material1 = (IngotMaterial) metal1.material;
			int multiplier1 = (int) metal1.amount;
			for (MaterialStack metal2 : lastMetal) {
				IngotMaterial material2 = (IngotMaterial) metal2.material;
				if ((int) metal1.amount == 1) multiplier2 = 0;
				else multiplier2 = (int) metal2.amount;
				ModHandler.addShapedRecipe(
					String.format("mixed_metal_1_%s_%s", material1, material2),
					MetaItems.INGOT_MIXED_METAL.getStackForm(multiplier1 + multiplier2),
					new String[] {
						"F",
						"M",
						"L"
					},
					sub('F', plate, material1),
					sub('M', plate, Bronze),
					sub('L', plate, material2));

				ModHandler.addShapedRecipe(
					String.format("mixed_metal_2_%s_%s", material1, material2),
					MetaItems.INGOT_MIXED_METAL.getStackForm(multiplier1 + multiplier2),
					new String[] {
						"F",
						"M",
						"L"
					},
					sub('F', plate, material1),
					sub('M', plate, Brass),
					sub('L', plate, material2));

				ASSEMBLER_RECIPES
					.recipeBuilder()
					.output(MetaItems.INGOT_MIXED_METAL, multiplier1 + multiplier2)
					.input(plate, material1)
					.input(plank, Bronze)
					.input(plate, material2)
					.duration(40 * multiplier1 + multiplier2 * 40).EUt(8)
					.buildAndRegister();

				ASSEMBLER_RECIPES
					.recipeBuilder()
					.output(MetaItems.INGOT_MIXED_METAL, multiplier1 + multiplier2)
					.input(plate, material1)
					.input(plate, Brass)
					.input(plate, material2)
					.duration(40 * multiplier1 + multiplier2 * 40).EUt(8)
					.buildAndRegister();
			}
		}

		ALLOY_SMELTER_RECIPES
			.recipeBuilder()
			.output(GATransparentCasing.CasingType.REINFORCED_GLASS, 4)
			.input(MetaItems.ADVANCED_ALLOY_PLATE)
			.input(dust, Glass, 3)
			.duration(400).EUt(4)
			.buildAndRegister();

		ALLOY_SMELTER_RECIPES
			.recipeBuilder()
			.output(GATransparentCasing.CasingType.REINFORCED_GLASS,4)
			.input(MetaItems.ADVANCED_ALLOY_PLATE)
			.input(Blocks.GLASS, 3)
			.duration(400).EUt(4)
			.buildAndRegister();
	}

	private static void tieredComponents() {
		//Machine Components - Adjusting the pump recipe to the GT5U recipe
		for(int i = LV; i <= IV; i++)
			ModHandler.removeRecipes(PUMP.getIngredient(i).getStackForm());

		// LV only - paper ring variant
		ModHandler.addShapedRecipe(
			"lv_electric_pump_paper",
			MetaItems.ELECTRIC_PUMP_LV.getStackForm(),
			new String[] {
				"SRH",
				"dPw",
				"HMC"
			},
			resolveComponents(
				LV,
				sub('S', SCREW),
				sub('R', ROTOR),
				sub('H', ring, Paper),
				sub('P', PIPE),
				sub('M', MOTOR),
				sub('C', CABLE)));

		// Create pump recipes for LV through IV with each rubber ring variation
		for(int i = LV - 1; i < IV; i++) {
			var pump = MetaItems.PUMPS[i];
			int tier = i + 1; // index is one lower than tier
			for(MaterialStack stackFluid : cableFluids)
				ModHandler.addShapedRecipe(
					String.format("%s_electric_pump_%s", VN[tier].toLowerCase(), stackFluid.material),
					pump.getStackForm(),
					new String[] {
						"SRH",
						"dPw",
						"HMC"
					},
					resolveComponents(
						tier,
						sub('S', SCREW),
						sub('R', ROTOR),
						sub('H', ring, stackFluid.material),
						sub('P', PIPE),
						sub('M', MOTOR),
						sub('C', CABLE)));
		}

		//Adjust the GTCE Pump Assembler recipe to match our pump recipe
		for (MaterialStack stackFluid : cableFluids) {
			IngotMaterial m = (IngotMaterial) stackFluid.material;
			for(int tier = LV; tier <= IV; tier++)
				ASSEMBLER_RECIPES
					.recipeBuilder()
					.output(tier, PUMP)
					.inputs(tier, ROTOR, CABLE, SCREW, PIPE, MOTOR)
					.fluidInputs(m.getFluid((int) stackFluid.amount))
					.duration(100).EUt(30 << (tier - 1) * 2)
					.buildAndRegister();
		}

		//Pyrolyse Oven Recipes
		PYROLYSE_RECIPES
			.recipeBuilder()
			.output(dust, Charcoal, 12)
			.input(Items.SUGAR, 23)
			.fluidOutputs(Water.getFluid(1500))
			.circuitMeta(1)
			.duration(640).EUt(64)
			.buildAndRegister();

		PYROLYSE_RECIPES
			.recipeBuilder()
			.output(dust, Charcoal, 12)
			.fluidOutputs(Water.getFluid(1500))
			.input(Items.SUGAR, 23)
			.fluidInputs(Nitrogen.getFluid(400))
			.circuitMeta(2)
			.duration(320).EUt(96)
			.buildAndRegister();

	}

	private static void chemReactorCracking() {

		final FluidMaterial[][] cracking = {
			{ Ethane,    HydroCrackedEthane,    SteamCrackedEthane },
			{ Ethylene,  HydroCrackedEthylene,  SteamCrackedEthylene },
			{ Propene,   HydroCrackedPropene,   SteamCrackedPropene },
			{ Propane,   HydroCrackedPropane,   SteamCrackedPropane },
			{ LightFuel, HydroCrackedLightFuel, CrackedLightFuel },
			{ Butane,    HydroCrackedButane,    SteamCrackedButane },
			{ Naphtha,   HydroCrackedNaphtha,   SteamCrackedNaphtha },
			{ HeavyFuel, HydroCrackedHeavyFuel, CrackedHeavyFuel },
			{ Gas,       HydroCrackedGas,       SteamCrackedGas },
			{ Butene,    HydroCrackedButene,    SteamCrackedButene },
			{ Butadiene, HydroCrackedButadiene, SteamCrackedButadiene }
		};

		//Chemical Reactor Cracking
		for(FluidMaterial[] current : cracking) {
			// Hydro Crack
			CHEMICAL_RECIPES
				.recipeBuilder()
				.fluidOutputs(current[1].getFluid(1000))
				.fluidInputs(Hydrogen.getFluid(2000),
				             current[0].getFluid(1000))
				.duration(160).EUt(30)
				.buildAndRegister();

			// Steam Crack
			CHEMICAL_RECIPES
				.recipeBuilder()
				.fluidOutputs(current[2].getFluid(1000))
				.fluidInputs(Steam.getFluid(2000),
				             current[0].getFluid(1000))
				.duration(160).EUt(30)
				.buildAndRegister();
		}
	}

	private static void fluidRecipes() {
		//Distillation Recipes
		DISTILLATION_RECIPES
			.recipeBuilder()
			.fluidOutputs(Lubricant.getFluid(12))
			.fluidInputs(FISH_OIL.getFluid(24))
			.duration(16).EUt(96)
			.buildAndRegister();

		//Fluid Heater Recipes
		FLUID_HEATER_RECIPES
			.recipeBuilder()
			.fluidOutputs(STERILE_GROWTH_MEDIUM.getFluid(500))
			.fluidInputs(RAW_GROWTH_MEDIUM.getFluid(500))
			.circuitMeta(1)
			.duration(30).EUt(24)
			.buildAndRegister();

		//Oil Extractor Recipes
		FLUID_EXTRACTION_RECIPES
			.recipeBuilder()
			.fluidOutputs(FISH_OIL.getFluid(40))
			.input(Items.FISH)
			.duration(160).EUt(4)
			.buildAndRegister();

		FLUID_EXTRACTION_RECIPES
			.recipeBuilder()
			.fluidOutputs(FISH_OIL.getFluid(60))
			.input(Items.FISH, 1, 1)
			.duration(160).EUt(4)
			.buildAndRegister();

		FLUID_EXTRACTION_RECIPES
			.recipeBuilder()
			.fluidOutputs(FISH_OIL.getFluid(70))
			.input(Items.FISH, 1, 2)
			.duration(160).EUt(4)
			.buildAndRegister();

		FLUID_EXTRACTION_RECIPES
			.recipeBuilder()
			.fluidOutputs(FISH_OIL.getFluid(30))
			.input(Items.FISH, 1, 3)
			.duration(160).EUt(4)
			.buildAndRegister();

	}

	private static void blastFurnaceRecipes() {

		//Misc Blast Furnace Recipes
		BLAST_RECIPES
			.recipeBuilder()
			.blastFurnaceTemp(1200)
			.output(dust, Garnierite)
			.output(dustTiny, Ash)
			.fluidOutputs(SulfurDioxide.getFluid(1000))
			.input(dust, Pentlandite)
			.fluidInputs(Oxygen.getFluid(3000))
			.duration(120).EUt(120)
			.buildAndRegister();

		BLAST_RECIPES
			.recipeBuilder()
			.blastFurnaceTemp(1200)
			.output(dust, BandedIron)
			.output(dustTiny, Ash)
			.fluidOutputs(SulfurDioxide.getFluid(1000))
			.input(dust, Pyrite)
			.fluidInputs(Oxygen.getFluid(3000))
			.duration(120).EUt(120)
			.buildAndRegister();

		BLAST_RECIPES
			.recipeBuilder()
			.blastFurnaceTemp(1200)
			.output(ingot, Silicon)
			.output(dustTiny, Ash)
			.fluidOutputs(CarbonMonoxde.getFluid(2000))
			.input(dust, SiliconDioxide)
			.input(dust, Carbon, 2)
			.duration(240).EUt(120)
			.buildAndRegister();

		for (MaterialStack current : ironOres) {
			Material materials = current.material;
			BLAST_RECIPES
				.recipeBuilder()
				.blastFurnaceTemp(1500)
				.output(ingot, Iron, 3)
				.output(dustSmall, DarkAsh)
				.input(ore, materials)
				.input(dust, Calcite)
				.duration(500).EUt(120)
				.buildAndRegister();

			BLAST_RECIPES
				.recipeBuilder()
				.blastFurnaceTemp(1500)
				.output(ingot, Iron, 2)
				.output(dustSmall, DarkAsh)
				.input(ore, materials)
				.input(dustTiny, Quicklime, 3)
				.duration(500).EUt(120)
				.buildAndRegister();
		}

	}

	private static void minceMeatRecipes() {
		//Mince Meat Recipes
		for(var item : arr(Items.PORKCHOP, Items.BEEF, Items.RABBIT))
			MACERATOR_RECIPES
				.recipeBuilder()
				.output(dustSmall, MEAT, 6)
				.input(item)
				.duration(60).EUt(16)
				.buildAndRegister();

		for(var item : arr(Items.CHICKEN, Items.MUTTON))
			MACERATOR_RECIPES
				.recipeBuilder()
				.output(dust, MEAT)
				.input(item)
				.duration(40).EUt(16)
				.buildAndRegister();

	}

	private static void ashRecipes() {
		//Ash-Related Recipes
		CENTRIFUGE_RECIPES
			.recipeBuilder()
			.output(dust, Ash)
			.output(dust, Carbon)
			.input(dust, DarkAsh)
			.duration(250).EUt(6)
			.buildAndRegister();

		CENTRIFUGE_RECIPES
			.recipeBuilder()
			.input(dust, Ash)
			.chancedOutput(get(dustSmall, Quicklime, 2), 9900, 0)
			.chancedOutput(get(dustSmall, Potash), 6400, 0)
			.chancedOutput(get(dustSmall, Magnesia), 6000, 0)
			.chancedOutput(get(dustSmall, PhosphorousPentoxide), 500, 0)
			.chancedOutput(get(dustSmall, SodaAsh), 5000, 0)
			.duration(240).EUt(30)
			.buildAndRegister();

	}

	private static void assemblyLineRecipes() {
		//Assembly Line Related Recipes
		ModHandler.addShapedRecipe(
			"assline_casing",
			TUNGSTENSTEEL_GEARBOX_CASING.getStack(2),
			new String[] {
				"PhP",
				"AFA",
				"PwP"
			},
			sub('P', plate, Steel),
			sub('A', IV, ROBOT_ARM),
			sub('F', frameGt, TungstenSteel));

		ModHandler.addShapedRecipe(
			"ga_assmbler_casing",
			ASSEMBLER_CASING.getStack(3),
			new String[] {
				"CCC",
				"CFC",
				"CMC"
			},
			resolveComponents(
				IV,
				sub('C', CIRCUIT),
				sub('F', bind(frameGt, TIER_MATERIAL)),
				sub('M', MOTOR)));

		//Assembly Line Casing
		ASSEMBLER_RECIPES
			.recipeBuilder()
			.output(TUNGSTENSTEEL_GEARBOX_CASING, 2)
			.input(IV, ROBOT_ARM, 2)
			.input(plate, Steel, 4)
			.input(frameGt, TungstenSteel)
			.duration(100).EUt(8000).buildAndRegister();

		//Assline Recipes

		int[] solderMultiplier = { 1, 2, 9};
		final int[] lubeAmt = { 250, 750, 2000 };
		final OrePrefix[] motorWireKinds = {
			cableGtSingle,
			cableGtQuadruple,
			cableGtQuadruple
		};

		// Electric Motors: LuV - UV
		for(int i = LuV; i <= UV; i++)
			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(i, MOTOR)
				.input(i, STICK_MAGNETIC, 1)
				.input(i, AL_STICK_LONG, 2)
				.input(i, AL_RING, 4)
				.input(i, AL_ROUND, 16)
				.input(i, AL_MOTOR_FINE_WIRE, 64)
				.input(i, AL_MOTOR_FINE_WIRE, 64)
				.input(i, AL_MOTOR_FINE_WIRE, 64)
				.input(i, AL_MOTOR_FINE_WIRE, 64)
				.input(i, bind(motorWireKinds[i - LuV], AL_CABLE_MATERIAL), 2)
				.fluidInputs(
					SolderingAlloy.getFluid(144 * solderMultiplier[i - LuV]),
					Lubricant.getFluid(lubeAmt[i - LuV]))
				.duration(600).EUt(10 * (int) Math.pow(4, i - 1))
				.buildAndRegister();

		// Electric Pumps: LuV - UV
		for(int i = LuV; i <= UV; i++)
			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(i, PUMP)
				.input(i, MOTOR, 1)
				.input(i, PIPE, 2)
				.input(i, AL_SCREW, 8)
				.input(ring, SiliconeRubber, 16)
				.input(i, AL_ROTOR, 2)
				.input(i, AL_CABLE, 2)
				.fluidInputs(
					SolderingAlloy.getFluid(144 * solderMultiplier[i - LuV]),
					Lubricant.getFluid(lubeAmt[i - LuV]))
				.duration(600).EUt(15 * (int) Math.pow(4, i - 1))
				.buildAndRegister();

		// Conveyor Modules, LuV - UV
		int[] rubberMult = {1, 2, 2};
		for(int i = LuV; i <= UV; i++)
			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(i, CONVEYOR)
				.input(i, MOTOR, 2)
				.input(i, AL_PLATE, 8)
				.input(i, AL_GEAR, 4)
				.input(i, AL_STICK, 4)
				.input(i, AL_INGOT, 2)
				.input(i, AL_CABLE, 2)
				.fluidInputs(
					StyreneButadieneRubber.getFluid(1440 * rubberMult[i - LuV]),
					Lubricant.getFluid(lubeAmt[i - LuV]))
				.duration(600).EUt(15 * (int) Math.pow(4, i - 1))
				.buildAndRegister();

		// Piston Recipes: LuV - UV
		for(int i = LuV; i <= UV; i++)
			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(i, PISTON)
				.input(i, MOTOR)
				.input(i, AL_PLATE, 8)
				.input(i, AL_GEAR_SMALL, 8)
				.input(i, AL_STICK, 4)
				.input(i, AL_INGOT, 2)
				.input(i, AL_CABLE, 2)
				.fluidInputs(
					SolderingAlloy.getFluid(144 * solderMultiplier[i - LuV]),
					Lubricant.getFluid(lubeAmt[i - LuV]))
				.duration(600).EUt(15 * (int) Math.pow(4, i - 1))
				.buildAndRegister();

		// Robot Arms: LuV - UV
		for(int i = LuV; i <= UV; i++)
			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(i, ROBOT_ARM)
				.input(i, AL_CABLE_2x, 16)
				.input(i, AL_SCREW, 16)
				.input(i, AL_STICK, 16)
				.input(i, AL_INGOT)
				.input(i, MOTOR, 2)
				.input(i, PISTON, 1)
				.input(i - 2, CIRCUIT, 8)
				.fluidInputs(SolderingAlloy.getFluid(576 * (int) Math.pow(2, i - LuV)),
				             Lubricant.getFluid(lubeAmt[i - LuV]))
				.duration(600).EUt(20 * (int) Math.pow(4, i - 1))
				.buildAndRegister();


		// Emitters: LuV - UV
		for(int i = LuV; i <= UV; i++)
			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(i, EMITTER)
				.input(i, AL_FRAME)
				.input(i - 1, EMITTER, 2)
				.input(i, AL_FOIL, 64)
				.input(i, AL_FOIL, 64)
				.input(i, AL_FOIL, 64)
				.input(i, AL_WIRE_2x, 8)
				.input(i, AL_GEM, 2)
				.input(i - 2, CIRCUIT, 8)
				.fluidInputs(SolderingAlloy.getFluid(576))
				.duration(600).EUt(15 * (int) Math.pow(4, i - 1))
				.buildAndRegister();

		// Sensors: LuV - UV
		for(int i = LuV; i <= UV; i++)
			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(i, SENSOR)
				.input(i, AL_FRAME)
				.input(i - 1, SENSOR, 2)
				.input(i, AL_FOIL, 64)
				.input(i, AL_FOIL, 64)
				.input(i, AL_FOIL, 64)
				.input(i, AL_WIRE_2x, 8)
				.input(i, AL_GEM, 2)
				.input(i - 2, CIRCUIT, 8)
				.fluidInputs(SolderingAlloy.getFluid(576))
				.duration(600).EUt(15 * (int) Math.pow(4, i - 1))
				.buildAndRegister();

		// Field Generator: LuV - UV
		for(int i = LuV; i <= UV; i++)
			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(i, FIELD_GENERATOR)
				.input(i, AL_FRAME)
				.input(i, AL_STAR)
				.input(i, EMITTER, 4)
				.input(wireFine, Osmium, 64)
				.input(wireFine, Osmium, 64)
				.input(wireFine, Osmium, 64)
				.input(wireFine, Osmium, 64)
				.input(cableGtOctal, YttriumBariumCuprate, 4)
				.input(i, CIRCUIT, 16)
				.fluidInputs(SolderingAlloy.getFluid(576 * (int) Math.pow(2, i - LuV)))
				.duration(600).EUt(30 * (int) Math.pow(4, i - 1))
				.buildAndRegister();

		// Wetware Circuits

		// Wetware Mainframe
		ASSEMBLY_LINE_RECIPES
			.recipeBuilder()
			.output(MetaItems.WETWARE_MAINFRAME_MAX)
			.input(frameGt, Tritanium, 4)
			.input(MetaItems.WETWARE_SUPER_COMPUTER_UV, 8)
			.input(MetaItems.SMALL_COIL, 4)
			.input(MetaItems.SMD_CAPACITOR, 32)
			.input(MetaItems.SMD_RESISTOR, 32)
			.input(MetaItems.SMD_TRANSISTOR, 32)
			.input(MetaItems.SMD_DIODE, 32)
			.input(MetaItems.RANDOM_ACCESS_MEMORY, 16)
			.input(wireGtDouble, Superconductor, 16)
			.input(foil, SiliconeRubber, 64)
			.fluidInputs(SolderingAlloy.getFluid(2880),
			             Water.getFluid(10000))
			.duration(2000).EUt(300_000)
			.buildAndRegister();

		// Neuro Processor
		ASSEMBLY_LINE_RECIPES
			.recipeBuilder()
			.output(GAMetaItems.NEURO_PROCESSOR, 8)
			.input(MetaItems.WETWARE_BOARD)
			.input(GAMetaItems.STEM_CELLS, 8)
			.input(MetaItems.GLASS_TUBE, 8)
			.input(foil, SiliconeRubber, 64)
			.input(plate, Gold, 8)
			.input(plate, StainlessSteel, 4)
			.fluidInputs(
				STERILE_GROWTH_MEDIUM.getFluid(100),
				UUMatter.getFluid(20),
				DistilledWater.getFluid(4000))
			.duration(200).EUt(20_000)
			.buildAndRegister();

		List<Recipe> recipes = new ArrayList<>();
		for (Recipe recipe : ASSEMBLER_RECIPES.getRecipeList())
			if (recipe.getOutputs().get(0).isItemEqual(MetaItems.WETWARE_PROCESSOR_LUV.getStackForm()) ||
				recipe.getOutputs().get(0).isItemEqual(MetaItems.WETWARE_PROCESSOR_ASSEMBLY_ZPM.getStackForm()))
				recipes.add(recipe);

		recipes.forEach(ASSEMBLER_RECIPES::removeRecipe);

		FluidStack[] solders = {
			SolderingAlloy.getFluid(72),
			Tin.getFluid(144)
		};

		// Wetware Processor
		for(var fluid : solders)
			ASSEMBLER_RECIPES
				.recipeBuilder()
				.output(MetaItems.WETWARE_PROCESSOR_LUV)
				.input(GAMetaItems.NEURO_PROCESSOR)
				.input(MetaItems.CRYSTAL_CENTRAL_PROCESSING_UNIT)
				.input(MetaItems.NANO_CENTRAL_PROCESSING_UNIT)
				.input(MetaItems.SMD_CAPACITOR, 2)
				.input(MetaItems.SMD_TRANSISTOR, 2)
				.input(wireFine, YttriumBariumCuprate, 2)
				.fluidInputs(fluid)
				.duration(200).EUt(28_000)
				.buildAndRegister();

		// Wetware Assembly
		for(var fluid : solders)
			ASSEMBLER_RECIPES
				.recipeBuilder()
				.output(MetaItems.WETWARE_PROCESSOR_ASSEMBLY_ZPM)
				.input(MetaItems.WETWARE_BOARD)
				.input(MetaItems.WETWARE_PROCESSOR_LUV, 2)
				.input(MetaItems.SMALL_COIL, 4)
				.input(MetaItems.SMD_CAPACITOR, 4)
				.input(MetaItems.RANDOM_ACCESS_MEMORY, 4)
				.input(wireFine, YttriumBariumCuprate, 6)
				.fluidInputs(fluid)
				.duration(400).EUt(30_000)
				.buildAndRegister();

		var last_bat = (GAConfig.GT5U.replaceUVwithMAXBat ? GAMetaItems.MAX_BATTERY : MetaItems.ZPM2);

		// GT5U: ZPM and UV tier batteries
		if (GAConfig.GT5U.enableZPMandUVBats) {
			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ENERGY_MODULE)
				.input(plate, Europium, 16)
				.input(MetaItems.WETWARE_SUPER_COMPUTER_UV, 4)
				.input(MetaItems.ENERGY_LAPOTRONIC_ORB2, 8)
				.input(MetaItems.FIELD_GENERATOR_LUV, 2)
				.input(MetaItems.NANO_CENTRAL_PROCESSING_UNIT, 64)
				.input(MetaItems.NANO_CENTRAL_PROCESSING_UNIT, 64)
				.input(MetaItems.SMD_DIODE, 8)
				.input(cableGtSingle, Naquadah, 32)
				.fluidInputs(
					SolderingAlloy.getFluid(2880),
					Water.getFluid(8000))
				.duration(2000).EUt(100_000)
				.buildAndRegister();

			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ENERGY_CLUSTER)
				.input(plate, Americium, 16)
				.input(MetaItems.WETWARE_SUPER_COMPUTER_UV, 4)
				.input(GAMetaItems.ENERGY_MODULE, 8)
				.input(MetaItems.FIELD_GENERATOR_ZPM, 2)
				.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT, 64)
				.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT, 64)
				.input(MetaItems.SMD_DIODE, 16)
				.input(cableGtSingle, NaquadahAlloy, 32)
				.fluidInputs(
					SolderingAlloy.getFluid(2880),
					Water.getFluid(16000))
				.duration(2000).EUt(200_000)
				.buildAndRegister();

			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(last_bat)
				.input(plate, NEUTRONIUM, 16)
				.input(MetaItems.WETWARE_MAINFRAME_MAX, 4)
				.input(GAMetaItems.ENERGY_CLUSTER, 8)
				.input(MetaItems.FIELD_GENERATOR_UV, 2)
				.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT, 64)
				.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT, 64)
				.input(MetaItems.SMD_DIODE, 16)
				.input(wireGtSingle, Superconductor, 32)
				.fluidInputs(
					SolderingAlloy.getFluid(2880),
					Water.getFluid(16000),
					Naquadria.getFluid(1152))
				.duration(2000).EUt(300_000)
				.buildAndRegister();
		}
		else {
			// Override recipe for top tier battery
			ASSEMBLY_LINE_RECIPES
				.recipeBuilder()
				.output(last_bat)
				.input(plate, NEUTRONIUM, 16)
				.input(MetaItems.WETWARE_MAINFRAME_MAX, 4)
				.input(MetaItems.ENERGY_LAPOTRONIC_ORB2, 8)
				.input(MetaItems.FIELD_GENERATOR_UV, 2)
				.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT, 64)
				.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT, 64)
				.input(MetaItems.SMD_DIODE, 16)
				.input(wireGtSingle, Superconductor, 32)
				.fluidInputs(
					SolderingAlloy.getFluid(2880),
					Water.getFluid(16000))
				.duration(2000).EUt(300_000)
				.buildAndRegister();
		}

		// Fusion Reactors
		ASSEMBLY_LINE_RECIPES
			.recipeBuilder()
			.output(GATileEntities.FUSION_REACTOR[0])
			.input(FUSION_COIL)
			.input(plate, Plutonium241)
			.input(plate, NetherStar)
			.input(IV, FIELD_GENERATOR, 2)
			.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT, 32)
			.input(wireGtSingle, Superconductor, 32)
			.input(ZPM, CIRCUIT, 4)
			.fluidInputs(SolderingAlloy.getFluid(2880))
			.duration(1000).EUt(30_000)
			.buildAndRegister();

		ASSEMBLY_LINE_RECIPES
			.recipeBuilder()
			.output(GATileEntities.FUSION_REACTOR[1])
			.input(FUSION_COIL)
			.input(plate, Europium, 4)
			.input(LuV, FIELD_GENERATOR, 2)
			.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT, 48)
			.input(wireGtDouble, Superconductor, 32)
			.input(UV, CIRCUIT, 4)
			.fluidInputs(SolderingAlloy.getFluid(2880))
			.duration(1000).EUt(60_000)
			.buildAndRegister();

		ASSEMBLY_LINE_RECIPES
			.recipeBuilder()
			.output(GATileEntities.FUSION_REACTOR[2])
			.input(FUSION_COIL)
			.input(MAX, CIRCUIT, 4)
			.input(plate, Americium, 4)
			.input(ZPM, FIELD_GENERATOR, 2)
			.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT, 64)
			.input(wireGtQuadruple, Superconductor, 32)
			.fluidInputs(SolderingAlloy.getFluid(2880))
			.duration(1000).EUt(90_000)
			.buildAndRegister();
	}

	public static void init2() {

		MIXER_RECIPES
			.recipeBuilder()
			.fluidOutputs(RAW_GROWTH_MEDIUM.getFluid(4000))
			.input(Items.SUGAR, 4)
			.input(dust, MEAT)
			.input(dustTiny, Salt)
			.fluidInputs(DistilledWater.getFluid(4000))
			.duration(160).EUt(16)
			.buildAndRegister();


		//Diesel
		MIXER_RECIPES
			.recipeBuilder()
			.fluidOutputs(Fuel.getFluid(6000))
			.fluidInputs(
				LightFuel.getFluid(5000),
				HeavyFuel.getFluid(1000))
			.duration(16).EUt(120)
			.buildAndRegister();

		//UU-Matter
		MIXER_RECIPES
			.recipeBuilder()
			.fluidOutputs(UUMatter.getFluid(20))
			.fluidInputs(
				POSITIVE_MATTER.getFluid(10),
				NEUTRAL_MATTER.getFluid(10))
			.duration(30).EUt(480)
			.buildAndRegister();

		//Stem Cells
		EXTRACTOR_RECIPES
			.recipeBuilder()
			.input(Items.EGG)
			.chancedOutput(GAMetaItems.STEM_CELLS.getStackForm(), 1500, 500)
			.duration(600).EUt(512)
			.buildAndRegister();

		//Star Recipes
		CHEMICAL_RECIPES.
			recipeBuilder()
			.output(dust, Plutonium, 3)
			.fluidOutputs(Radon.getFluid(50))
			.input(ingot, Plutonium, 3)
			.duration(60_000).EUt(8)
			.buildAndRegister();

		AUTOCLAVE_RECIPES
			.recipeBuilder()
			.output(MetaItems.GRAVI_STAR)
			.input(Items.NETHER_STAR)
			.fluidInputs(NEUTRONIUM.getFluid(288))
			.duration(480).EUt(7680)
			.buildAndRegister();

		fusionRecipes();
	}

	private static void fusionRecipes() {

		//Fusion Recipes
		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(40_000_000)
			.fluidOutputs(Helium.getPlasma(125))
			.fluidInputs(
				Deuterium.getFluid(125),
				Tritium.getFluid(125))
			.duration(16).EUt(4096)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(60_000_000)
			.fluidOutputs(Helium.getPlasma(125))
			.fluidInputs(
				Deuterium.getFluid(125),
				Helium3.getFluid(125))
			.duration(16).EUt(2048)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(80_000_000)
			.fluidOutputs(Oxygen.getPlasma(125))
			.fluidInputs(
				Carbon.getFluid(125),
				Helium3.getFluid(125))
			.duration(32).EUt(4096)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(180_000_000)
			.fluidOutputs(Nitrogen.getPlasma(175))
			.fluidInputs(
				Beryllium.getFluid(16),
				Deuterium.getFluid(375))
			.duration(16).EUt(16384)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(360_000_000)
			.fluidOutputs(Iron.getPlasma(125))
			.fluidInputs(
				Silicon.getFluid(16),
				Magnesium.getFluid(16))
			.duration(32).EUt(8192)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(480_000_000)
			.fluidOutputs(Nickel.getPlasma(125))
			.fluidInputs(
				Potassium.getFluid(16),
				Fluorine.getFluid(125))
			.duration(16).EUt(32768)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(150_000_000)
			.fluidOutputs(Platinum.getFluid(16))
			.fluidInputs(
				Beryllium.getFluid(16),
				Tungsten.getFluid(16))
			.duration(32).EUt(32768)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(150_000_000)
			.fluidOutputs(Europium.getFluid(16))
			.fluidInputs(
				Neodymium.getFluid(16),
				Hydrogen.getFluid(48))
			.duration(64).EUt(24576)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(200_000_000)
			.fluidOutputs(Americium.getFluid(16))
			.fluidInputs(
				Lutetium.getFluid(16),
				Chrome.getFluid(16))
			.duration(96).EUt(49152)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(300_000_000)
			.fluidOutputs(Naquadah.getFluid(16))
			.fluidInputs(
				Plutonium.getFluid(16),
				Thorium.getFluid(16))
			.duration(64).EUt(32768)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(600_000_000)
			.fluidOutputs(NEUTRONIUM.getFluid(2))
			.fluidInputs(
				Americium.getFluid(16),
				Naquadria.getFluid(16))
			.duration(200).EUt(98304)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(150_000_000)
			.fluidOutputs(Osmium.getFluid(16))
			.fluidInputs(
				Tungsten.getFluid(16),
				Helium.getFluid(16))
			.duration(64).EUt(24578)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(120_000_000)
			.fluidOutputs(Iron.getFluid(16))
			.fluidInputs(
				Manganese.getFluid(16),
				Hydrogen.getFluid(16))
			.duration(64).EUt(8192)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(240_000_000)
			.fluidOutputs(Uranium.getFluid(16))
			.fluidInputs(
				Mercury.getFluid(16),
				Magnesium.getFluid(16))
			.duration(64).EUt(49152)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(240_000_000)
			.fluidOutputs(Uranium.getFluid(16))
			.fluidInputs(
				Gold.getFluid(16),
				Aluminium.getFluid(16))
			.duration(64).EUt(49152)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(480_000_000)
			.fluidOutputs(Plutonium.getFluid(16))
			.fluidInputs(
				Uranium.getFluid(16),
				Helium.getFluid(16))
			.duration(128).EUt(49152)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(140_000_000)
			.fluidOutputs(Chrome.getFluid(16))
			.fluidInputs(
				Vanadium.getFluid(16),
				Hydrogen.getFluid(125))
			.duration(64).EUt(24576)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(140_000_000)
			.fluidOutputs(Duranium.getFluid(16))
			.fluidInputs(
				Gallium.getFluid(16),
				Radon.getFluid(125))
			.duration(64).EUt(16384)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(200_000_000)
			.fluidOutputs(Tritanium.getFluid(16))
			.fluidInputs(
				Titanium.getFluid(48),
				Duranium.getFluid(32))
			.duration(64).EUt(32768)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(200_000_000)
			.fluidOutputs(Radon.getFluid(125))
			.fluidInputs(
				Gold.getFluid(16),
				Mercury.getFluid(16))
			.duration(64).EUt(32768)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(200_000_000)
			.fluidOutputs(Tungsten.getFluid(16))
			.fluidInputs(
				Tantalum.getFluid(16),
				Tritium.getFluid(16))
			.duration(16).EUt(24576)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(380_000_000)
			.fluidOutputs(Indium.getFluid(16))
			.fluidInputs(
				Silver.getFluid(16),
				Lithium.getFluid(16))
			.duration(32).EUt(24576)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(400_000_000)
			.fluidOutputs(Naquadria.getFluid(3))
			.fluidInputs(
				NaquadahEnriched.getFluid(15),
				Radon.getFluid(125))
			.duration(64).EUt(49152)
			.buildAndRegister();

		FUSION_RECIPES
			.recipeBuilder()
			.EUToStart(80_000_000)
			.fluidOutputs(Lutetium.getFluid(16))
			.fluidInputs(
				Lanthanum.getFluid(16),
				Silicon.getFluid(16))
			.duration(16).EUt(8192)
			.buildAndRegister();

		//Fusion Casing Recipes
		ModHandler.addShapedRecipe(
			"fusion_casing_1",
			MultiblockCasingType.FUSION_CASING.getStack(),
			new String[] {
				"PhP",
				"PHP",
				"PwP"
			},
			sub('P', plate, TungstenSteel),
			sub('H', TIER_CASING.getIngredient(LuV)));

		ModHandler.addShapedRecipe(
			"fusion_casing_2",
			MultiblockCasingType.FUSION_CASING_MK2.getStack(),
			new String[] {
				"PhP",
				"PHP",
				"PwP"
			},
			sub('P', plate, Americium),
			sub('H', MultiblockCasingType.FUSION_CASING.getStack()));

		ASSEMBLER_RECIPES
			.recipeBuilder()
			.output(MultiblockCasingType.FUSION_CASING_MK2)
			.input(MultiblockCasingType.FUSION_CASING)
			.input(plate, Americium, 6)
			.duration(50).EUt(16)
			.buildAndRegister();

		ModHandler.addShapedRecipe(
			"fusion_coil",
			FUSION_COIL.getStack(),
			new String[] {
				"CRC",
				"FSF",
				"CRC"
			},
			sub('C', CIRCUIT.getIngredient(LuV)),
			sub('R', MetaItems.NEUTRON_REFLECTOR),
			sub('F', FIELD_GENERATOR.getIngredient(MV)),
			sub('S', SUPERCONDUCTOR.getStack()));

		//Explosive Recipes
		ModHandler.removeRecipes(new ItemStack(Blocks.TNT));
		ModHandler.removeRecipes(MetaItems.DYNAMITE.getStackForm());
		CHEMICAL_RECIPES
			.recipeBuilder()
			.output(MetaItems.DYNAMITE)
			.input(Items.PAPER)
			.input(Items.STRING)
			.fluidInputs(Glyceryl.getFluid(500))
			.duration(160).EUt(4)
			.buildAndRegister();

		//Dust Packing
		for (Material m : Material.MATERIAL_REGISTRY) {
			if (GAConfig.Misc.PackagerDustRecipes && !get(dust, m).isEmpty()) {
				PACKER_RECIPES
					.recipeBuilder()
					.output(dust, m)
					.input(dustSmall, m, 4)
					.notConsumable(GAMetaItems.SCHEMATIC_DUST)
					.duration(100).EUt(4)
					.buildAndRegister();

				PACKER_RECIPES
					.recipeBuilder()
					.output(dust, m)
					.input(dustTiny, m, 9)
					.notConsumable(GAMetaItems.SCHEMATIC_DUST)
					.duration(100).EUt(4)
					.buildAndRegister();
			}
		}

		//Schematic Recipes
		ASSEMBLER_RECIPES
			.recipeBuilder()
			.output(GAMetaItems.SCHEMATIC)
			.input(MV, CIRCUIT, 4)
			.input(plate, StainlessSteel, 2)
			.duration(3200).EUt(4)
			.buildAndRegister();

		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:schematic/schematic_1"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:schematic/schematic_c"));

		//Configuration Circuit
		ModHandler.removeRecipes(MetaItems.INTEGRATED_CIRCUIT.getStackForm());
		ModHandler.addShapelessRecipe(
			"basic_to_configurable_circuit",
			MetaItems.INTEGRATED_CIRCUIT.getStackForm(),
			CIRCUIT.getIngredient(LV));

		//MAX Machine Hull
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:casing_max"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:hull_max"));

		ModHandler.addShapedRecipe(
			"ga_casing_max",
			MachineCasingType.MAX.getStack(),
			new String[] {
				"PPP",
				"PwP",
				"PPP"
			},
			sub('P', MAX, PLATE));

		ModHandler.addShapedRecipe(
			"ga_hull_max",
			HULL.getIngredient(MAX),
			new String[] {
				"PHP",
				"CMC"
			},
			resolveComponents(
				MAX,
				sub('M', TIER_CASING),
				sub('C', CABLE),
				sub('H', PLATE),
				sub('P', HULL_PLATE_2)));

		ASSEMBLER_RECIPES
			.recipeBuilder()
			.output(MAX, TIER_CASING)
			.input(plate, NEUTRONIUM, 8)
			.circuitMeta(8)
			.duration(50).EUt(16)
			.buildAndRegister();

		ASSEMBLER_RECIPES
			.recipeBuilder()
			.output(MAX, HULL)
			.input(MAX, TIER_CASING)
			.input(MAX, CABLE, 2)
			.fluidInputs(Polytetrafluoroethylene.getFluid(288))
			.duration(50).EUt(16)
			.buildAndRegister();

		//Redstone and glowstone melting
		for(var stone : arr(Redstone, Glowstone))
			FLUID_EXTRACTION_RECIPES
				.recipeBuilder()
				.fluidOutputs(stone.getFluid(144))
				.input(dust, stone)
				.duration(80).EUt(32)
				.buildAndRegister();

		//Gem Tool Part Fixes
		for (Material material : Material.MATERIAL_REGISTRY) {
			if (!get(gem, material).isEmpty() &&
				!get(toolHeadHammer, material).isEmpty() &&
				material != Flint)
			{
				ModHandler.removeRecipes(get(toolHeadAxe, material));
				ModHandler.addShapedRecipe(
					"axe_head_" + material,
					get(toolHeadAxe, material),
					new String[] {
						"GG",
						"Gf"
					},
					sub('G', gem, material));

				ModHandler.removeRecipes(get(toolHeadFile, material));
				ModHandler.addShapedRecipe(
					"file_head_" + material,
					get(toolHeadFile, material),
					new String[] {
						"G",
						"G",
						"f"
					},
					sub('G', gem, material));

				ModHandler.removeRecipes(get(toolHeadHammer, material));
				ModHandler.addShapedRecipe(
					"hammer_head_" + material.toString(),
					get(toolHeadHammer, material),
					new String[] {
						"GG ",
						"GGf",
						"GG "
					},
					sub('G', gem, material));

				ModHandler.removeRecipes(get(toolHeadHoe, material));
				ModHandler.addShapedRecipe(
					"hoe_head_" + material,
					get(toolHeadHoe, material),
					new String[] {
						"GGf"
					},
					sub('G', gem, material));

				ModHandler.removeRecipes(get(toolHeadPickaxe, material));
				ModHandler.addShapedRecipe(
					"pickaxe_head_" + material,
					get(toolHeadPickaxe, material),
					new String[] {
						"GGG",
						"f  "
					},
					sub('G', gem, material));

				ModHandler.removeRecipes(get(toolHeadSaw, material));
				ModHandler.addShapedRecipe(
					"saw_head_" + material,
					get(toolHeadSaw, material),
					new String[] {
						"GG",
						"f "
					},
					sub('G', gem, material));

				ModHandler.removeRecipes(get(toolHeadSense, material));
				ModHandler.addShapedRecipe(
					"sense_head_" + material,
					get(toolHeadSense, material),
					new String[] {
						"GGG",
						" f "
					},
					sub('G', gem, material));

				ModHandler.removeRecipes(get(toolHeadShovel, material));
				ModHandler.addShapedRecipe(
					"shovel_head_" + material,
					get(toolHeadShovel, material),
					new String[] {
						"fG"
					},
					sub('G', gem, material));

				ModHandler.removeRecipes(get(toolHeadSword, material));
				ModHandler.addShapedRecipe(
					"sword_head_" + material,
					get(toolHeadSword, material),
					new String[] {
						" G",
						"fG"
					},
					sub('G', gem, material));

				ModHandler.removeRecipes(get(toolHeadUniversalSpade, material));
				ModHandler.addShapedRecipe(
					"universal_spade_head_" + material,
					get(toolHeadUniversalSpade, material),
					new String[] {
						"GGG",
						"GfG",
						" G "
					},
					sub('G', gem, material));
			}
		}

		//Misc Recipe Patches
		for(var mat : arr(NetherQuartz, CertusQuartz))
			COMPRESSOR_RECIPES
				.recipeBuilder()
				.output(plate, mat)
				.input(dust, mat)
				.duration(400).EUt(2)
				.buildAndRegister();

		ModHandler.addShapedRecipe(
			"3x3_schematic",
			GAMetaItems.SCHEMATIC_3X3.getStackForm(),
			new String[] {
				"  d",
				" S ",
				"   "
			},
			sub('S', GAMetaItems.SCHEMATIC));

		ModHandler.addShapedRecipe(
			"2x2_schematic",
			GAMetaItems.SCHEMATIC_2X2.getStackForm(),
			new String[] {
				" d ",
				" S ",
				"   "
			},
			sub('S', GAMetaItems.SCHEMATIC));

		ModHandler.addShapedRecipe(
			"dust_schematic",
			GAMetaItems.SCHEMATIC_DUST.getStackForm(),
			new String[] {
				"   ",
				" S ",
				"  d"
			},
			sub('S', GAMetaItems.SCHEMATIC));

		// Recipes for LuV-UV Fluid Regulators
		for(int tier = LuV; tier <= UV; tier++) {
			ASSEMBLER_RECIPES
				.recipeBuilder()
				.output(tier, FLUID_REGULATOR)
				.input(tier, PUMP)
				.input(tier, CIRCUIT, 2)
				.duration(100).EUt((int) (V[tier] * 30 / 32))
				.buildAndRegister();
		}

	}

	public static void forestrySupport() {

		//Bio Diesel via Fish Oil
		CHEMICAL_RECIPES
			.recipeBuilder()
			.fluidOutputs(
				Glycerol.getFluid(1000),
				BioDiesel.getFluid(6000))
			.input(dustTiny, SodiumHydroxide)
			.fluidInputs(
				FISH_OIL.getFluid(6000),
				Methanol.getFluid(1000))
			.duration(600).EUt(30)
			.buildAndRegister();

		CHEMICAL_RECIPES
			.recipeBuilder()
			.fluidOutputs(
				Glycerol.getFluid(1000),
				BioDiesel.getFluid(6000))
			.input(dustTiny, SodiumHydroxide)
			.fluidInputs(
				FISH_OIL.getFluid(6000),
				Ethanol.getFluid(1000))
			.duration(600).EUt(30)
			.buildAndRegister();

		//Electrode Recipes
		if (GAConfig.GT6.electrodes && Loader.isModLoaded("forestry")) {
			ASSEMBLER_RECIPES
				.recipeBuilder()
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.APATITE, 1))
				.input(GAMetaItems.ELECTRODE_APATITE)
				.input(plate, Glass)
				.duration(150).EUt(16)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_APATITE)
				.input(stick, Apatite, 2)
				.input(bolt, Apatite)
				.input(dustSmall, Redstone, 2)
				.duration(100).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_APATITE, 2)
				.input(stick, Apatite, 4)
				.input(bolt, Apatite, 2)
				.input(dust, Redstone)
				.duration(200).EUt(24)
				.buildAndRegister();

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.BLAZE, 1))
				.input(GAMetaItems.ELECTRODE_BLAZE)
				.input(plate, Glass)
				.duration(150).EUt(16)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_BLAZE, 2)
				.input(dust, Blaze, 2)
				.input(dustSmall, Blaze, 2)
				.input(dust, Redstone)
				.duration(200).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_BLAZE, 4)
				.input(dust, Blaze, 5)
				.input(dust, Redstone, 2)
				.duration(400).EUt(24)
				.buildAndRegister();

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.BRONZE, 1))
				.input(GAMetaItems.ELECTRODE_BRONZE)
				.input(plate, Glass)
				.duration(150).EUt(16)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_BRONZE)
				.input(stick, Bronze, 2)
				.input(bolt, Bronze)
				.input(dustSmall, Redstone, 2)
				.duration(100).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_BRONZE, 2)
				.input(stick, Bronze, 4)
				.input(bolt, Bronze, 2)
				.input(dust, Redstone)
				.duration(200).EUt(24)
				.buildAndRegister();

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.COPPER, 1))
				.input(GAMetaItems.ELECTRODE_COPPER)
				.input(plate, Glass)
				.duration(150).EUt(16)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_COPPER)
				.input(stick, Copper, 2)
				.input(bolt, Copper)
				.input(dustSmall, Redstone, 2)
				.duration(100).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_COPPER, 2)
				.input(stick, Copper, 4)
				.input(bolt, Copper, 2)
				.input(dust, Redstone)
				.duration(200).EUt(24)
				.buildAndRegister();

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.DIAMOND, 1))
				.input(GAMetaItems.ELECTRODE_DIAMOND)
				.input(plate, Glass)
				.duration(150).EUt(16)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_DIAMOND)
				.input(stick, Diamond, 2)
				.input(bolt, Diamond)
				.input(dustSmall, Redstone, 2)
				.duration(100).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_DIAMOND, 2)
				.input(stick, Diamond, 4)
				.input(bolt, Diamond, 2)
				.input(dust, Redstone)
				.duration(200).EUt(24)
				.buildAndRegister();

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.duration(150).EUt(16)
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.EMERALD, 1))
				.input(GAMetaItems.ELECTRODE_EMERALD)
				.input(plate, Glass)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_EMERALD)
				.input(stick, Emerald, 2)
				.input(bolt, Emerald)
				.input(dustSmall, Redstone, 2)
				.duration(100).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_EMERALD, 2)
				.input(stick, Emerald, 4)
				.input(bolt, Emerald, 2)
				.input(dust, Redstone)
				.duration(200).EUt(24)
				.buildAndRegister();

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.ENDER, 1))
				.input(GAMetaItems.ELECTRODE_ENDER)
				.input(plate, Glass)
				.duration(150).EUt(16)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_ENDER, 2)
				.input(dust, Endstone, 2)
				.input(dustSmall, Endstone, 2)
				.input(dust, EnderEye)
				.duration(200).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_ENDER, 4)
				.input(dust, Endstone, 5)
				.input(dust, EnderEye, 2)
				.duration(400).EUt(24)
				.buildAndRegister();

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.GOLD, 1))
				.input(GAMetaItems.ELECTRODE_GOLD)
				.input(plate, Glass)
				.duration(150).EUt(16)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_GOLD)
				.input(stick, Gold, 2)
				.input(bolt, Gold)
				.input(dustSmall, Redstone, 2)
				.duration(100).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_GOLD, 2)
				.input(stick, Gold, 4)
				.input(bolt, Gold, 2)
				.input(dust, Redstone)
				.duration(200).EUt(24)
				.buildAndRegister();

			if (Loader.isModLoaded("ic2") || Loader.isModLoaded("binniecore")) {
				ASSEMBLER_RECIPES
					.recipeBuilder()
					.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.IRON, 1))
					.input(GAMetaItems.ELECTRODE_IRON)
					.input(plate, Glass)
					.duration(150).EUt(16)
					.buildAndRegister();

				FORMING_PRESS_RECIPES
					.recipeBuilder()
					.output(GAMetaItems.ELECTRODE_IRON)
					.input(stick, Iron, 2)
					.input(bolt, Iron)
					.input(dustSmall, Redstone, 2)
					.duration(100).EUt(24)
					.buildAndRegister();

				FORMING_PRESS_RECIPES
					.recipeBuilder()
					.output(GAMetaItems.ELECTRODE_IRON, 2)
					.input(stick, Iron, 4)
					.input(bolt, Iron, 2)
					.input(dust, Redstone)
					.duration(200).EUt(24)
					.buildAndRegister();
			}

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.LAPIS, 1))
				.input(GAMetaItems.ELECTRODE_LAPIS)
				.input(plate, Glass)
				.duration(150).EUt(16)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_LAPIS)
				.input(stick, Lapis, 2)
				.input(bolt, Lapis)
				.input(dustSmall, Redstone, 2)
				.duration(100).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_LAPIS, 2)
				.input(stick, Lapis, 4)
				.input(bolt, Lapis, 2)
				.input(dust, Redstone)
				.duration(200).EUt(24)
				.buildAndRegister();

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.OBSIDIAN, 1))
				.input(GAMetaItems.ELECTRODE_OBSIDIAN)
				.input(plate, Glass)
				.duration(150).EUt(16)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_OBSIDIAN, 2)
				.input(dust, Obsidian, 2)
				.input(dustSmall, Obsidian, 2)
				.input(dust, Redstone)
				.duration(200).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_OBSIDIAN, 4)
				.input(dust, Obsidian, 5)
				.input(dust, Redstone, 2)
				.duration(400).EUt(24)
				.buildAndRegister();

			if (Loader.isModLoaded("extrautils2")) {
				ASSEMBLER_RECIPES
					.recipeBuilder()
					.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.ORCHID, 1))
					.input(GAMetaItems.ELECTRODE_ORCHID)
					.input(plate, Glass)
					.duration(150).EUt(16)
					.buildAndRegister();

				FORMING_PRESS_RECIPES
					.recipeBuilder()
					.output(GAMetaItems.ELECTRODE_ORCHID, 4)
					.input(Blocks.REDSTONE_ORE, 5)
					.input(dust, Redstone)
					.duration(400).EUt(24)
					.buildAndRegister();
			}

			if (Loader.isModLoaded("ic2") || Loader.isModLoaded("techreborn")) {
				ASSEMBLER_RECIPES
					.recipeBuilder()
					.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.RUBBER, 1))
					.input(GAMetaItems.ELECTRODE_RUBBER)
					.input(plate, Glass)
					.duration(150).EUt(16)
					.buildAndRegister();

				FORMING_PRESS_RECIPES
					.recipeBuilder()
					.output(GAMetaItems.ELECTRODE_RUBBER)
					.input(stick, Rubber, 2)
					.input(bolt, Rubber)
					.input(dustSmall, Redstone, 2)
					.duration(100).EUt(24)
					.buildAndRegister();

				FORMING_PRESS_RECIPES
					.recipeBuilder()
					.output(GAMetaItems.ELECTRODE_RUBBER, 2)
					.input(stick, Rubber, 4)
					.input(bolt, Rubber, 2)
					.input(dust, Redstone)
					.duration(200).EUt(24)
					.buildAndRegister();
			}

			ASSEMBLER_RECIPES
				.recipeBuilder()
				.outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.TIN, 1))
				.input(GAMetaItems.ELECTRODE_TIN)
				.input(plate, Glass)
				.duration(150).EUt(16)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_TIN)
				.input(stick, Tin, 2)
				.input(bolt, Tin)
				.input(dustSmall, Redstone, 2)
				.duration(100).EUt(24)
				.buildAndRegister();

			FORMING_PRESS_RECIPES
				.recipeBuilder()
				.output(GAMetaItems.ELECTRODE_TIN, 2)
				.input(stick, Tin, 4)
				.input(bolt, Tin, 2)
				.input(dust, Redstone)
				.duration(200).EUt(24)
				.buildAndRegister();
		}

	}

	public static void generatedRecipes() {
		List<ResourceLocation> recipesToRemove = new ArrayList<>();

		for (IRecipe recipe : CraftingManager.REGISTRY) {
			final var ingredients = recipe.getIngredients();
			final Function<Integer, ItemStack[]> matching = i -> ingredients.get(i).getMatchingStacks();
			final var output = recipe.getRecipeOutput();
			if(ingredients.isEmpty())
				continue;

			final var firstMatching = matching.apply(0);

			if (ingredients.size() == 9) {
				if (firstMatching.length > 0 && Block.getBlockFromItem(output.getItem()) != Blocks.AIR) {
					boolean match = true;
					for (int i = 1; i < ingredients.size(); i++) {
						var currentMatching = matching.apply(i);
						if (currentMatching.length == 0 || !firstMatching[0].isItemEqual(currentMatching[0])) {
							match = false;
							break;
						}
					}
					if (match) {
						if (GAConfig.GT5U.Remove3x3BlockRecipes)
							recipesToRemove.add(recipe.getRegistryName());
						if (GAConfig.GT5U.GenerateCompressorRecipes)
							COMPRESSOR_RECIPES
								.recipeBuilder()
								.outputs(output)
								.inputs(from(firstMatching[0], ingredients.size()))
								.duration(400).EUt(2)
								.buildAndRegister();
					}
				}
			}

			if (ingredients.size() == 9) {
				if (firstMatching.length > 0 && Block.getBlockFromItem(output.getItem()) == Blocks.AIR) {
					boolean match = true;
					for (int i = 1; i < ingredients.size(); i++) {
						var currentMatching = matching.apply(i);
						if (currentMatching.length == 0 || !firstMatching[0].isItemEqual(currentMatching[0])) {
							match = false;
							break;
						}
					}
					if (match &&
						GAConfig.Misc.Packager3x3Recipes &&
						output.getCount() == 1 &&
						!recipesToRemove.contains(recipe.getRegistryName()) &&
						!GAMetaItems.hasPrefix(output, dust.name(), dustTiny.name()))
					{
						PACKER_RECIPES
							.recipeBuilder()
							.outputs(output)
							.inputs(from(firstMatching[0], ingredients.size()))
							.notConsumable(GAMetaItems.SCHEMATIC_3X3)
							.duration(100).EUt(4)
							.buildAndRegister();
					}
				}
			}

			if (ingredients.size() == 4) {
				if (firstMatching.length > 0 && Block.getBlockFromItem(output.getItem()) != Blocks.QUARTZ_BLOCK) {
					boolean match = true;
					for (int i = 1; i < ingredients.size(); i++) {
						var currentMatching = matching.apply(i);
						if (currentMatching.length == 0 || !firstMatching[0].isItemEqual(currentMatching[0])) {
							match = false;
							break;
						}
					}
					if (match &&
						GAConfig.Misc.Packager2x2Recipes &&
						output.getCount() == 1 &&
						!recipesToRemove.contains(recipe.getRegistryName()) &&
						!GAMetaItems.hasPrefix(output, dust.name(), dustSmall.name()))
					{
						PACKER_RECIPES
							.recipeBuilder()
							.outputs(output)
							.inputs(from(firstMatching[0], ingredients.size()))
							.notConsumable(GAMetaItems.SCHEMATIC_2X2)
							.duration(100).EUt(4)
							.buildAndRegister();
					}
				}
			}

			if (ingredients.size() == 1 && firstMatching.length > 0 && output.getCount() == 9 &&
				Block.getBlockFromItem(firstMatching[0].getItem()) != Blocks.AIR &&
				Block.getBlockFromItem(firstMatching[0].getItem()) != Blocks.SLIME_BLOCK)
			{
				boolean isIngot = false;
				for (int i : OreDictionary.getOreIDs(output)) {
					if (OreDictionary.getOreName(i).startsWith(ingot.name())) {
						isIngot = true;
						break;
					}
				}
				if (GAConfig.GT5U.RemoveBlockUncraftingRecipes)
					recipesToRemove.add(recipe.getRegistryName());
				if (!isIngot) {
					FORGE_HAMMER_RECIPES
						.recipeBuilder()
						.outputs(output)
						.inputs(firstMatching[0])
						.duration(100).EUt(24)
						.buildAndRegister();
				}
			}

			if (ingredients.size() == 1 && firstMatching.length > 0 && output.getCount() == 9) {
				if (GAConfig.Misc.Unpackager3x3Recipes && !recipesToRemove.contains(recipe.getRegistryName())) {
					UNPACKER_RECIPES
						.recipeBuilder()
						.outputs(output)
						.inputs(firstMatching[0])
						.circuitMeta(1)
						.duration(100).EUt(8)
						.buildAndRegister();
				}
			}
		}

		for (ResourceLocation r : recipesToRemove)
			ModHandler.removeRecipeByName(r);
		recipesToRemove.clear();

		if (GAConfig.GT5U.GenerateCompressorRecipes) {
			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:glowstone"));
			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:quartz_block"));
			ModHandler.removeRecipeByName(new ResourceLocation("gregtech:nether_quartz_block_to_nether_quartz"));
			FORGE_HAMMER_RECIPES
				.recipeBuilder()
				.output(gem, NetherQuartz, 4)
				.input(block, NetherQuartz)
				.duration(100).EUt(24)
				.buildAndRegister();
		}

		//Generate Plank Recipes
		for (IRecipe recipe : CraftingManager.REGISTRY) {
			if (recipe.getRecipeOutput().isEmpty())
				continue;

			for (int i : OreDictionary.getOreIDs(recipe.getRecipeOutput())) {

				// First, skip all recipes we don't care about
				final String odName = OreDictionary.getOreName(i);
				final boolean isPlank = odName.equals("plankWood");
				final boolean isSlab = odName.equals("slabWood");
				if (!(isPlank || isSlab)) {
					continue;
				}

				// Skip cursed recipes:

				List<Ingredient> ingredients = recipe.getIngredients();
				if (ingredients.isEmpty()) {
					GTLog.logger.warn("Skipping plank/slab recipe with no ingredients: {}", recipe.getRegistryName());
					continue;
				}

				ItemStack[] matchingStacks = ingredients.get(0).getMatchingStacks();
				if (matchingStacks.length == 0) {
					GTLog.logger.warn("Skipping plank/slab recipe whose own inputs were rejected: {}}",
					                  recipe.getRegistryName());
					continue;
				}

				ItemStack matchingStack = matchingStacks[0];

				if (isPlank && recipe.getIngredients().size() == 1 && recipe.getRecipeOutput().getCount() == 4) {
					if (GAConfig.GT5U.GeneratedSawingRecipes) {
						ModHandler.removeRecipeByName(recipe.getRegistryName());
						ItemStack output = recipe.getRecipeOutput();

						ModHandler.addShapelessRecipe(
							String.format("log_to_4_%s", output),
							GTUtility.copyAmount(4, output),
							matchingStack,
							ToolDictNames.craftingToolSaw);

						ModHandler.addShapelessRecipe(
							String.format("log_to_2_%s", output),
							GTUtility.copyAmount(2, output),
							matchingStack);
					}

					CUTTER_RECIPES
						.recipeBuilder()
						.outputs(GTUtility.copyAmount(6, recipe.getRecipeOutput()))
						.output(dust, Wood, 2)
						.inputs(matchingStack)
						.fluidInputs(Lubricant.getFluid(1))
						.duration(200).EUt(8).buildAndRegister();
				}
				if (isSlab && recipe.getRecipeOutput().getCount() == 6) {
					CUTTER_RECIPES
						.recipeBuilder()
						.outputs(GTUtility.copyAmount(2, recipe.getRecipeOutput()))
						.inputs(matchingStack)
						.duration(50).EUt(4).buildAndRegister();
				}
			}
		}

		//Disable Wood To Charcoal Recipes
		if(GAConfig.GT5U.DisableLogToCharcoalSmelting) {
			List<ItemStack> allWoodLogs = new ArrayList<>();
			for(ItemStack itemStack : OreDictionary.getOres("logWood"))
				allWoodLogs.addAll(ModHandler.getAllSubItems(itemStack));

			for(ItemStack stack : allWoodLogs) {
				ItemStack smeltingOutput = ModHandler.getSmeltingOutput(stack);
				if (!smeltingOutput.isEmpty()
					&& smeltingOutput.getItem() == Items.COAL
					&& smeltingOutput.getMetadata() == 1)
				{
					ItemStack woodStack = stack.copy();
					woodStack.setItemDamage(OreDictionary.WILDCARD_VALUE);
					ModHandler.removeFurnaceSmelting(woodStack);
				}
			}
		}
	}
}
