package gregicadditions.recipes;

import static gregicadditions.recipes.GACraftingComponents.BETTER_CIRCUIT;
import static gregicadditions.recipes.GACraftingComponents.CABLE;
import static gregicadditions.recipes.GACraftingComponents.CABLE_QUAD;
import static gregicadditions.recipes.GACraftingComponents.CIRCUIT;
import static gregicadditions.recipes.GACraftingComponents.COIL_ELECTRIC;
import static gregicadditions.recipes.GACraftingComponents.COIL_HEATING;
import static gregicadditions.recipes.GACraftingComponents.COIL_HEATING_DOUBLE;
import static gregicadditions.recipes.GACraftingComponents.CONVEYOR;
import static gregicadditions.recipes.GACraftingComponents.DIAMOND;
import static gregicadditions.recipes.GACraftingComponents.EMITTER;
import static gregicadditions.recipes.GACraftingComponents.FIELD_GENERATOR;
import static gregicadditions.recipes.GACraftingComponents.GLASS;
import static gregicadditions.recipes.GACraftingComponents.GRINDER;
import static gregicadditions.recipes.GACraftingComponents.HULL;
import static gregicadditions.recipes.GACraftingComponents.MOTOR;
import static gregicadditions.recipes.GACraftingComponents.PIPE;
import static gregicadditions.recipes.GACraftingComponents.PISTON;
import static gregicadditions.recipes.GACraftingComponents.PLATE;
import static gregicadditions.recipes.GACraftingComponents.PUMP;
import static gregicadditions.recipes.GACraftingComponents.ROBOT_ARM;
import static gregicadditions.recipes.GACraftingComponents.ROTOR;
import static gregicadditions.recipes.GACraftingComponents.STICK_DISTILLATION;
import static gregicadditions.recipes.GACraftingComponents.STICK_ELECTROMAGNETIC;
import static gregicadditions.recipes.GACraftingComponents.STICK_RADIOACTIVE;
import static gregicadditions.recipes.GACraftingComponents.WIRE;
import static gregicadditions.recipes.helpers.HelperMethods.removeRecipeByName;

import java.util.Arrays;

import gregicadditions.GAConfig;
import gregicadditions.machines.GATileEntities;
import gregtech.api.GTValues;
import gregtech.api.items.OreDictNames;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.MarkerMaterials.Tier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.init.Blocks;

/**
 * This class contains modifications to the hand crafting recipes of both GTCE and SoG singleblock and multiblock machines
 */
public class MachineCraftingRecipes {

	private static final String[] tiers = { "lv", "mv", "hv", "ev" };

	public static void init() {
		//Removal of GTCE machine crafting recipes
		for (String tier : tiers) {
			//Plasma Arc Furnace uses Tier + 1 circuits
			removeRecipeByName("gregtech:gregtech.machine.plasma_arc_furnace." + tier);
			//Replaces pistons with Pipes
			removeRecipeByName("gregtech:gregtech.machine.pump." + tier);
			//Replaces Fluid Filters with Item Filters
			removeRecipeByName("gregtech:gregtech.machine.air_collector." + tier);
		}

		//Replaces the entire recipe
		removeRecipeByName("gregtech:bronze_primitive_blast_furnace");
		//Changes circuit to Tier + 1 circuit
		removeRecipeByName("gregtech:diesel_engine");
		//Changes circuit to Tier + 1 circuit
		removeRecipeByName("gregtech:large_plasma_turbine");
		//Changes circuit to Tier + 1 circuit
		removeRecipeByName("gregtech:large_titanium_boiler");
		//Changes circuit to Tier + 1 circuit
		removeRecipeByName("gregtech:large_tungstensteel_boiler");
		//Changes pipe size
		removeRecipeByName("gregtech:steam_turbine_lv");
		removeRecipeByName("gregtech:steam_turbine_mv");
		removeRecipeByName("gregtech:steam_turbine_hv");
		//Changes circuit to Tier + 1 circuit
		removeRecipeByName("gregtech:magic_energy_absorber");
		//Changes to use small coils and other components
		removeRecipeByName("gregtech:transformer_ev");
		removeRecipeByName("gregtech:transformer_iv");
		removeRecipeByName("gregtech:transformer_luv");
		removeRecipeByName("gregtech:transformer_zpm");
		removeRecipeByName("gregtech:transformer_uv");


		ModHandler.addShapedRecipe("ga_transformer_ev", MetaTileEntities.TRANSFORMER[GTValues.EV - 1].getStackForm(), "KBB", "CM ", "KBB", 'M', MetaTileEntities.HULL[GTValues.HV].getStackForm(), 'C', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Aluminium), 'B', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Gold), 'K', MetaItems.SMALL_COIL);
		ModHandler.addShapedRecipe("ga_transformer_iv", MetaTileEntities.TRANSFORMER[GTValues.IV - 1].getStackForm(), "KBB", "CM ", "KBB", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(), 'C', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Tungsten), 'B', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Aluminium), 'K', MetaItems.SMALL_COIL);
		ModHandler.addShapedRecipe("ga_transformer_luv", MetaTileEntities.TRANSFORMER[GTValues.LuV - 1].getStackForm(), "KBB", "CM ", "KBB", 'M', MetaTileEntities.HULL[GTValues.IV].getStackForm(), 'C', new UnificationEntry(OrePrefix.cableGtSingle, Materials.VanadiumGallium), 'B', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Tungsten), 'K', MetaItems.POWER_INTEGRATED_CIRCUIT);
		ModHandler.addShapedRecipe("ga_transformer_zpm", MetaTileEntities.TRANSFORMER[GTValues.ZPM - 1].getStackForm(), "KBB", "CM ", "KBB", 'M', MetaTileEntities.HULL[GTValues.LuV].getStackForm(), 'C', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Naquadah), 'B', new UnificationEntry(OrePrefix.cableGtSingle, Materials.VanadiumGallium), 'K', MetaItems.POWER_INTEGRATED_CIRCUIT);
		ModHandler.addShapedRecipe("ga_transformer_uv", MetaTileEntities.TRANSFORMER[GTValues.UV - 1].getStackForm(), "KBB", "CM ", "KBB", 'M', MetaTileEntities.HULL[GTValues.ZPM].getStackForm(), 'C', new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.NaquadahAlloy), 'B', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Naquadah), 'K', MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT);

		//MultiBlocks
		ModHandler.addShapedRecipe("ga_primitive_blast_furnace", MetaTileEntities.PRIMITIVE_BLAST_FURNACE.getStackForm(), "hRS", "PBR", "dRS", 'R', OreDictUnifier.get(OrePrefix.stick, Materials.Iron), 'S', OreDictUnifier.get(OrePrefix.screw, Materials.Iron), 'P', "plateIron", 'B', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS));
		ModHandler.addShapedRecipe("ga_diesel_engine", MetaTileEntities.DIESEL_ENGINE.getStackForm(), "PCP", "EME", "GWG", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(), 'P', MetaItems.ELECTRIC_PISTON_EV, 'E', MetaItems.ELECTRIC_MOTOR_EV, 'C', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Elite), 'W', new UnificationEntry(OrePrefix.wireGtSingle, Materials.TungstenSteel), 'G', new UnificationEntry(OrePrefix.gear, Materials.Titanium));
		ModHandler.addShapedRecipe("ga_large_plasma_turbine", MetaTileEntities.LARGE_PLASMA_TURBINE.getStackForm(), "PSP", "SAS", "CSC", 'S', new UnificationEntry(OrePrefix.gear, Materials.TungstenSteel), 'P', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Master), 'A', MetaTileEntities.HULL[GTValues.UV].getStackForm(), 'C', OreDictUnifier.get(OrePrefix.pipeLarge, Materials.TungstenSteel));
		ModHandler.addShapedRecipe("ga_large_titanium_boiler", MetaTileEntities.LARGE_TITANIUM_BOILER.getStackForm(), "PSP", "SAS", "PSP", 'P', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Gold), 'S', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Elite), 'A', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.TITANIUM_STABLE));
		ModHandler.addShapedRecipe("ga_large_tungstensteel_boiler", MetaTileEntities.LARGE_TUNGSTENSTEEL_BOILER.getStackForm(), "PSP", "SAS", "PSP", 'P', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Aluminium), 'S', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Master), 'A', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST));
		ModHandler.addShapedRecipe("ga_assline", GATileEntities.ASSEMBLY_LINE.getStackForm(), "CRC", "SAS", "CRC", 'A', MetaTileEntities.HULL[GTValues.IV].getStackForm(), 'R', MetaItems.ROBOT_ARM_IV, 'C', MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLER_CASING), 'S', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Elite));
		ModHandler.addShapedRecipe("ga_processing_array", GATileEntities.PROCESSING_ARRAY.getStackForm(), "CBC", "RHR", "CDC", 'H', MetaTileEntities.HULL[GTValues.IV].getStackForm(), 'R', MetaItems.ROBOT_ARM_IV, 'C', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Elite), 'B', MetaItems.ENERGY_LAPOTRONIC_ORB, 'D', MetaItems.TOOL_DATA_ORB);

		//Storage
		
		if(GAConfig.GT6.registerDrums) {
			ModHandler.addShapedRecipe("wooden_barrel", GATileEntities.WOODEN_DRUM.getStackForm(), "rSs", "PRP", "PRP", 'S', "slimeball", 'P', "plankWood", 'R', "stickLongIron");
			if (GAConfig.GT6.BendingCurvedPlates && GAConfig.GT6.BendingCylinders && GAConfig.GT6.addCurvedPlates) {
				ModHandler.addShapedRecipe("bronze_drum", GATileEntities.BRONZE_DRUM.getStackForm(), " h ", "PRP", "PRP", 'P', "plateCurvedBronze", 'R', "stickLongBronze");
				ModHandler.addShapedRecipe("steel_drum", GATileEntities.STEEL_DRUM.getStackForm(), " h ", "PRP", "PRP", 'P', "plateCurvedSteel", 'R', "stickLongSteel");
				ModHandler.addShapedRecipe("stainless_steel_drum", GATileEntities.STAINLESS_STEEL_DRUM.getStackForm(), " h ", "PRP", "PRP", 'P', "plateCurvedStainlessSteel", 'R', "stickLongStainlessSteel");
				ModHandler.addShapedRecipe("titanium_drum", GATileEntities.TITANIUM_DRUM.getStackForm(), " h ", "PRP", "PRP", 'P', "plateCurvedTitanium", 'R', "stickLongTitanium");
				ModHandler.addShapedRecipe("tungstensteel_drum", GATileEntities.TUNGSTENSTEEL_DRUM.getStackForm(), " h ", "PRP", "PRP", 'P', "plateCurvedTungstenSteel", 'R', "stickLongTungstenSteel");
			} else {
				ModHandler.addShapedRecipe("bronze_drum", GATileEntities.BRONZE_DRUM.getStackForm(), " h ", "PRP", "PRP", 'P', "plateBronze", 'R', "stickLongBronze");
				ModHandler.addShapedRecipe("steel_drum", GATileEntities.STEEL_DRUM.getStackForm(), " h ", "PRP", "PRP", 'P', "plateSteel", 'R', "stickLongSteel");
				ModHandler.addShapedRecipe("stainless_steel_drum", GATileEntities.STAINLESS_STEEL_DRUM.getStackForm(), " h ", "PRP", "PRP", 'P', "plateStainlessSteel", 'R', "stickLongStainlessSteel");
				ModHandler.addShapedRecipe("titanium_drum", GATileEntities.TITANIUM_DRUM.getStackForm(), " h ", "PRP", "PRP", 'P', "plateTitanium", 'R', "stickLongTitanium");
				ModHandler.addShapedRecipe("tungstensteel_drum", GATileEntities.TUNGSTENSTEEL_DRUM.getStackForm(), " h ", "PRP", "PRP", 'P', "plateTungstenSteel", 'R', "stickLongTungstenSteel");
			}
		}
		if(GAConfig.Misc.registerCrates) {
			ModHandler.addShapedRecipe("wooden_crate", GATileEntities.WOODEN_CRATE.getStackForm(), "RPR", "PsP", "RPR", 'P', "plankWood", 'R', "screwIron");
			ModHandler.addShapedRecipe("bronze_crate", GATileEntities.BRONZE_CRATE.getStackForm(), "RPR", "PhP", "RPR", 'P', "plateBronze", 'R', "stickLongBronze");
			ModHandler.addShapedRecipe("steel_crate", GATileEntities.STEEL_CRATE.getStackForm(), "RPR", "PhP", "RPR", 'P', "plateSteel", 'R', "stickLongSteel");
			ModHandler.addShapedRecipe("stainless_steel_crate", GATileEntities.STAINLESS_STEEL_CRATE.getStackForm(), "RPR", "PhP", "RPR", 'P', "plateStainlessSteel", 'R', "stickLongStainlessSteel");
			ModHandler.addShapedRecipe("titanium_crate", GATileEntities.TITANIUM_CRATE.getStackForm(), "RPR", "PhP", "RPR", 'P', "plateTitanium", 'R', "stickLongTitanium");
			ModHandler.addShapedRecipe("tungstensteel_crate", GATileEntities.TUNGSTENSTEEL_CRATE.getStackForm(), "RPR", "PhP", "RPR", 'P', "plateTungstenSteel", 'R', "stickLongTungstenSteel");
		}

		//Generators
		registerMachineRecipe(GATileEntities.NAQUADAH_REACTOR, "RCR", "FMF", "QCQ", 'M', HULL, 'Q', CABLE_QUAD, 'C', BETTER_CIRCUIT, 'F', FIELD_GENERATOR, 'R', STICK_RADIOACTIVE);
		ModHandler.addShapedRecipe("ga_steam_turbine_lv", MetaTileEntities.STEAM_TURBINE[0].getStackForm(), "PCP", "RMR", "EWE", 'M', MetaTileEntities.HULL[GTValues.LV].getStackForm(), 'E', MetaItems.ELECTRIC_MOTOR_LV, 'R', new UnificationEntry(OrePrefix.rotor, Materials.Tin), 'C', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Basic), 'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Tin), 'P', new UnificationEntry(OrePrefix.pipeMedium, Materials.Bronze));
		ModHandler.addShapedRecipe("ga_steam_turbine_mv", MetaTileEntities.STEAM_TURBINE[1].getStackForm(), "PCP", "RMR", "EWE", 'M', MetaTileEntities.HULL[GTValues.MV].getStackForm(), 'E', MetaItems.ELECTRIC_MOTOR_MV, 'R', new UnificationEntry(OrePrefix.rotor, Materials.Bronze), 'C', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Good), 'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Copper), 'P', new UnificationEntry(OrePrefix.pipeMedium, Materials.Steel));
		ModHandler.addShapedRecipe("ga_steam_turbine_hv", MetaTileEntities.STEAM_TURBINE[2].getStackForm(), "PCP", "RMR", "EWE", 'M', MetaTileEntities.HULL[GTValues.HV].getStackForm(), 'E', MetaItems.ELECTRIC_MOTOR_HV, 'R', new UnificationEntry(OrePrefix.rotor, Materials.Steel), 'C', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Advanced), 'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Gold), 'P', new UnificationEntry(OrePrefix.pipeMedium, Materials.StainlessSteel));
		ModHandler.addShapedRecipe("ga_magic_energy_absorber", MetaTileEntities.MAGIC_ENERGY_ABSORBER.getStackForm(), "PCP", "PMP", "PCP", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(), 'P', MetaItems.SENSOR_EV, 'C', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Elite));

		//Lower Tier Machines
		registerMachineRecipe(MetaTileEntities.PLASMA_ARC_FURNACE,
						"WGW",
							   "CMC",
							   "TPT",
							   'M', HULL,
							   'P', PLATE,
							   'C', BETTER_CIRCUIT,
							   'W', CABLE_QUAD,
							   'T', PUMP,
							   'G', new UnificationEntry(OrePrefix.ingot, Materials.Graphite));

		registerMachineRecipe(MetaTileEntities.PUMP,
						"WGW",
							   "GMG",
							   "TGT",
							   'M', HULL,
							   'W', CIRCUIT,
							   'G', PUMP,
							   'T', PIPE);

		registerMachineRecipe(MetaTileEntities.AIR_COLLECTOR,
						"WFW",
							   "PHP",
							   "WCW",
							   'W', Blocks.IRON_BARS,
							   'F', MetaItems.ITEM_FILTER,
							   'P', PUMP,
							   'H', HULL,
							   'C', CIRCUIT);

		//Higher Tier machines
		if(GAConfig.GT5U.highTierPumps) {
			registerMachineRecipe(GATileEntities.PUMP,
						"WGW",
							   "GMG",
							   "TGT",
							   'M', HULL,
							   'W', CIRCUIT,
							   'G', PUMP,
							   'T', PIPE);
		}

		if(GAConfig.GT5U.highTierAlloySmelter) {
			registerMachineRecipe(GATileEntities.ALLOY_SMELTER,
						"ECE",
							   "CMC",
							   "WCW",
							   'M', HULL,
							   'E', CIRCUIT,
							   'W', CABLE,
							   'C', COIL_HEATING_DOUBLE);
		}

		if(GAConfig.GT5U.highTierAssemblers) {
			registerMachineRecipe(GATileEntities.ASSEMBLER,
						"ACA",
							   "VMV",
							   "WCW",
							   'M', HULL,
							   'V', CONVEYOR,
							   'A', ROBOT_ARM,
							   'C', CIRCUIT,
							   'W', CABLE);
		}

		if(GAConfig.GT5U.highTierBenders) {
			registerMachineRecipe(GATileEntities.BENDER,
						"PWP",
							   "CMC",
							   "EWE",
							   'M', HULL,
							   'E', MOTOR,
							   'P', PISTON,
							   'C', CIRCUIT,
							   'W', CABLE);
		}

		if(GAConfig.GT5U.highTierCanners) {
			registerMachineRecipe(GATileEntities.CANNER,
						"WPW",
							   "CMC",
							   "GGG",
							   'M', HULL,
							   'P', PUMP,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierCompressors) {
			registerMachineRecipe(GATileEntities.COMPRESSOR,
						" C ",
							   "PMP",
							   "WCW",
							   'M', HULL,
							   'P', PISTON,
							   'C', CIRCUIT,
							   'W', CABLE);
		}

		if(GAConfig.GT5U.highTierCutters) {
			registerMachineRecipe(GATileEntities.CUTTER,
						"WCG",
							   "VMB",
							   "CWE",
							   'M', HULL,
							   'E', MOTOR,
							   'V', CONVEYOR,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS,
							   'B', OreDictNames.craftingDiamondBlade);
		}

		if(GAConfig.GT5U.highTierElectricFurnace) {
			registerMachineRecipe(GATileEntities.ELECTRIC_FURNACE,
						"ECE",
							   "CMC",
							   "WCW",
							   'M', HULL,
							   'E', CIRCUIT,
							   'W', CABLE,
							   'C', COIL_HEATING);
		}

		if(GAConfig.GT5U.highTierExtractors) {
			registerMachineRecipe(GATileEntities.EXTRACTOR,
						"GCG",
							   "EMP",
							   "WCW",
							   'M', HULL,
							   'E', PISTON,
							   'P', PUMP,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierExtruders) {
			registerMachineRecipe(GATileEntities.EXTRUDER,
						"CCE",
							   "XMP",
							   "CCE",
							   'M', HULL,
							   'X', PISTON,
							   'E', CIRCUIT,
							   'P', PIPE,
							   'C', COIL_HEATING_DOUBLE);
		}

		if(GAConfig.GT5U.highTierLathes) {
			registerMachineRecipe(GATileEntities.LATHE,
						"WCW",
							   "EMD",
							   "CWP",
							   'M', HULL,
							   'E', MOTOR,
							   'P', PISTON,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'D', DIAMOND);
		}

		if(GAConfig.GT5U.highTierMacerators) {
			registerMachineRecipe(GATileEntities.MACERATOR,
						"PEG",
							   "WWM",
							   "CCW",
							   'M', HULL,
							   'E', MOTOR,
							   'P', PISTON,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GRINDER);
		}

		if(GAConfig.GT5U.highTierMicrowaves) {
			registerMachineRecipe(GATileEntities.MICROWAVE,
						"LWC",
							   "LMR",
							   "LEC",
							   'M', HULL,
							   'E', MOTOR,
							   'R', EMITTER,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'L', new UnificationEntry(OrePrefix.plate, Materials.Lead));
		}

		if(GAConfig.GT5U.highTierWiremills) {
			registerMachineRecipe(GATileEntities.WIREMILL,
						"EWE",
							   "CMC",
							   "EWE",
							   'M', HULL,
							   'E', MOTOR,
							   'C', CIRCUIT,
							   'W', CABLE);
		}

		if(GAConfig.GT5U.highTierCentrifuges) {
			registerMachineRecipe(GATileEntities.CENTRIFUGE,
						"CEC",
							   "WMW",
							   "CEC",
							   'M', HULL,
							   'E', MOTOR,
							   'C', CIRCUIT,
							   'W', CABLE);
		}

		if(GAConfig.GT5U.highTierElectrolyzers) {
			registerMachineRecipe(GATileEntities.ELECTROLYZER,
						"IGI",
							   "IMI",
							   "CWC",
							   'M', HULL,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'I', WIRE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierThermalCentrifuges) {
			registerMachineRecipe(GATileEntities.THERMAL_CENTRIFUGE,
						"CEC",
							   "OMO",
							   "WEW",
							   'M', HULL,
							   'E', MOTOR,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'O', COIL_HEATING_DOUBLE);
		}

		if(GAConfig.GT5U.highTierOreWashers) {
			registerMachineRecipe(GATileEntities.ORE_WASHER,
						"RGR",
							   "CEC",
							   "WMW",
							   'M', HULL,
							   'R', ROTOR,
							   'E', MOTOR,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierPackers) {
			registerMachineRecipe(GATileEntities.PACKER,
						"BCB",
							   "RMV",
							   "WCW",
							   'M', HULL,
							   'R', ROBOT_ARM,
							   'V', CONVEYOR,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'B', OreDictNames.chestWood);
		}

		if(GAConfig.GT5U.highTierUnpackers) {
			registerMachineRecipe(GATileEntities.UNPACKER,
						"BCB",
							   "VMR",
							   "WCW",
							   'M', HULL,
							   'R', ROBOT_ARM,
							   'V', CONVEYOR,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'B', OreDictNames.chestWood);
		}

		if(GAConfig.GT5U.highTierChemicalReactors) {
			registerMachineRecipe(GATileEntities.CHEMICAL_REACTOR,
						"GRG",
							   "WEW",
							   "CMC",
							   'M', HULL,
							   'R', ROTOR,
							   'E', MOTOR,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierFluidCanners) {
			registerMachineRecipe(GATileEntities.FLUID_CANNER,
						"GCG",
							   "GMG",
							   "WPW",
							   'M', HULL,
							   'P', PUMP,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierBreweries) {
			registerMachineRecipe(GATileEntities.BREWERY,
						"GPG",
							   "WMW",
							   "CBC",
							   'M', HULL,
							   'P', PUMP,
							   'B', STICK_DISTILLATION,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierFermenters) {
			registerMachineRecipe(GATileEntities.FERMENTER,
						"WPW",
							   "GMG",
							   "WCW",
							   'M', HULL,
							   'P', PUMP,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierFluidExtractors) {
			registerMachineRecipe(GATileEntities.FLUID_EXTRACTOR,
						"GCG",
							   "PME",
							   "WCW",
							   'M', HULL,
							   'E', PISTON,
							   'P', PUMP,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierFluidSolidifiers) {
			registerMachineRecipe(GATileEntities.FLUID_SOLIDIFIER,
						"PGP",
							   "WMW",
							   "CBC",
							   'M', HULL,
							   'P', PUMP,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS,
							   'B', OreDictNames.chestWood);
		}

		if(GAConfig.GT5U.highTierDistilleries) {
			registerMachineRecipe(GATileEntities.DISTILLERY,
						"GBG",
							   "CMC",
							   "WPW",
							   'M', HULL,
							   'P', PUMP,
							   'B', STICK_DISTILLATION,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierChemicalBaths) {
			registerMachineRecipe(GATileEntities.CHEMICAL_BATH,
						"VGW",
							   "PGV",
							   "CMC",
							   'M', HULL,
							   'P', PUMP,
							   'V', CONVEYOR,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierPolarizers) {
			registerMachineRecipe(GATileEntities.POLARIZER,
						"ZSZ",
							   "WMW",
							   "ZSZ",
							   'M', HULL,
							   'S', STICK_ELECTROMAGNETIC,
							   'Z', COIL_ELECTRIC,
							   'W', CABLE);
		}

		if(GAConfig.GT5U.highTierElectromagneticSeparators) {
			registerMachineRecipe(GATileEntities.ELECTROMAGNETIC_SEPARATOR,
						"VWZ",
							   "WMS",
							   "CWZ",
							   'M', HULL,
							   'S', STICK_ELECTROMAGNETIC,
							   'Z', COIL_ELECTRIC,
							   'V', CONVEYOR,
							   'C', CIRCUIT,
							   'W', CABLE);
		}

		if(GAConfig.GT5U.highTierAutoclaves) {
			registerMachineRecipe(GATileEntities.AUTOCLAVE,
						"IGI",
							   "IMI",
							   "CPC",
							   'M', HULL,
							   'P', PUMP,
							   'C', CIRCUIT,
							   'I', PLATE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierMixers) {
			registerMachineRecipe(GATileEntities.MIXER,
						"GRG",
							   "GEG",
							   "CMC",
							   'M', HULL,
							   'E', MOTOR,
							   'R', ROTOR,
							   'C', CIRCUIT,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierLaserEngravers) {
			registerMachineRecipe(GATileEntities.LASER_ENGRAVER,
						"PEP",
							   "CMC",
							   "WCW",
							   'M', HULL,
							   'E', EMITTER,
							   'P', PISTON,
							   'C', CIRCUIT,
							   'W', CABLE);
		}

		if(GAConfig.GT5U.highTierFormingPresses) {
			registerMachineRecipe(GATileEntities.FORMING_PRESS,
						"WPW",
							   "CMC",
							   "WPW",
							   'M', HULL,
							   'P', PISTON,
							   'C', CIRCUIT,
							   'W', CABLE);
		}

		if(GAConfig.GT5U.highTierForgeHammers) {
			registerMachineRecipe(GATileEntities.FORGE_HAMMER,
						"WPW",
							   "CMC",
							   "WAW",
							   'M', HULL,
							   'P', PISTON,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'A', OreDictNames.craftingAnvil);
		}

		if(GAConfig.GT5U.highTierFluidHeaters) {
			registerMachineRecipe(GATileEntities.FLUID_HEATER,
						"OGO",
							   "PMP",
							   "WCW",
							   'M', HULL,
							   'P', PUMP,
							   'O', COIL_HEATING_DOUBLE,
							   'C', CIRCUIT,
							   'W', CABLE,
							   'G', GLASS);
		}

		if(GAConfig.GT5U.highTierSifters) {
			registerMachineRecipe(GATileEntities.SIFTER,
						"WFW",
							   "PMP",
							   "CFC",
							   'M', HULL,
							   'P', PISTON,
							   'F', MetaItems.ITEM_FILTER,
							   'C', CIRCUIT,
							   'W', CABLE);
		}

		if(GAConfig.GT5U.highTierArcFurnaces) {
			registerMachineRecipe(GATileEntities.ARC_FURNACE,
						"WGW",
							   "CMC",
							   "PPP",
							   'M', HULL,
							   'P', PLATE,
							   'C', CIRCUIT,
							   'W', CABLE_QUAD,
							   'G', new UnificationEntry(OrePrefix.ingot, Materials.Graphite));
		}

		if(GAConfig.GT5U.highTierPlasmaArcFurnaces) {
			registerMachineRecipe(GATileEntities.PLASMA_ARC_FURNACE,
						"WGW",
							   "CMC",
							   "TPT",
							   'M', HULL,
							   'P', PLATE,
							   'C', BETTER_CIRCUIT,
							   'W', CABLE_QUAD,
							   'T', PUMP,
							   'G', new UnificationEntry(OrePrefix.ingot, Materials.Graphite));
		}

		if(GAConfig.Misc.highTierCollector) {
			registerMachineRecipe(GATileEntities.AIR_COLLECTOR,
						"WFW",
							   "PHP",
							   "WCW",
							   'W', Blocks.IRON_BARS,
							   'F', MetaItems.ITEM_FILTER,
							   'P', PUMP,
							   'H', HULL,
							   'C', CIRCUIT);
		}

		//Completely Shadows of Greg machines
		registerMachineRecipe(GATileEntities.CLUSTERMILL,
							  "MMM",
							  "CHC",
							  "MMM",
							  'M', MOTOR,
							  'C', CIRCUIT,
							  'H', HULL);

		registerMachineRecipe(GATileEntities.MASS_FAB,
							  "CFC",
							  "QMQ",
							  "CFC",
							  'M', HULL,
							  'Q', CABLE_QUAD,
							  'C', BETTER_CIRCUIT,
							  'F', FIELD_GENERATOR);

		registerMachineRecipe(GATileEntities.REPLICATOR,
							  "EFE",
							  "CMC",
							  "EQE",
							  'M', HULL,
							  'Q', CABLE_QUAD,
							  'C', BETTER_CIRCUIT,
							  'F', FIELD_GENERATOR,
							  'E', EMITTER);

		registerMachineRecipe(GATileEntities.BUNDLER,
							  "BCB",
							  "RMV",
							  "WCW",
							  'M', HULL,
							  'R', ROBOT_ARM,
							  'V', CONVEYOR,
							  'C', CIRCUIT,
							  'W', CABLE,
							  'B', OreDictNames.craftingPiston);
	}

	public static <T extends MetaTileEntity> void registerMachineRecipe(T[] metaTileEntities, Object... recipe) {
		for (int i = 0; i < metaTileEntities.length; i++) {
			if (metaTileEntities[i] != null) {
				MetaTileEntity mte = metaTileEntities[i];

				if(mte instanceof ITieredMetaTileEntity) {
					//Use the actual machine tier to catch an edge case where there was no LV machine and everything
					//was getting under-tiered, eg extruder
					ModHandler.addShapedRecipe(String.format("ga_%s", mte.getMetaName()), mte.getStackForm(), prepareRecipe(((ITieredMetaTileEntity) mte).getTier(), Arrays.copyOf(recipe, recipe.length)));
				}
				else {
					ModHandler.addShapedRecipe(String.format("ga_%s", mte.getMetaName()), mte.getStackForm(), prepareRecipe(i + 1, Arrays.copyOf(recipe, recipe.length)));
				}
			}
		}
	}

	private static Object[] prepareRecipe(int tier, Object... recipe) {
		for (int i = 3; i < recipe.length; i++) {
			if (recipe[i] instanceof GACraftingComponents) {
				recipe[i] = ((GACraftingComponents) recipe[i]).getIngredient(tier);
			}
		}
		return recipe;
	}
}
