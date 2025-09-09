package gregicadditions.recipes;

import gregicadditions.GAConfig;
import gregicadditions.machines.GATileEntities;
import gregtech.api.items.OreDictNames;
import gregtech.api.metatileentity.ITiered;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static gregicadditions.recipes.GACraftingComponents.*;
import static gregtech.api.GTValues.*;
import static gregtech.api.recipes.ModHandler.Substitution;
import static gregtech.api.recipes.ModHandler.Substitution.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.BRONZE_HULL;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.STEEL_HULL;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.BRONZE_BRICKS_HULL;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.STEEL_BRICKS_HULL;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.*;
import static gregtech.common.blocks.BlockMultiblockCasing.MultiblockCasingType.*;
import static gregtech.loaders.recipe.MetaTileEntityLoader.*;

public class MachineCraftingRecipes {

	public static void init() {
		//Removal

		// Machines that go up to EV
		for(String machineName :
			arr("alloy_smelter", "bender", "canner", "compressor", "cutter", "electric_furnace", "extractor",
			    "extruder", "lathe", "macerator", "microwave", "wiremill", "centrifuge", "electrolyzer",
			    "ore_washer", "packer", "unpacker", "chemical_reactor", "fluid_canner", "brewery", "fermenter",
			    "fluid_extractor", "fluid_solidifier", "distillery", "chemical_bath", "polarizer",
			    "electromagnetic_separator", "mixer", "forming_press", "forge_hammer", "fluid_heater", "sifter",
			    "arc_furnace", "plasma_arc_furnace", "pump", "air_collector"))
			for (String tier : arr("lv", "mv", "hv", "ev"))
				ModHandler.removeRecipeByName(
					new ResourceLocation(String.format("gregtech:gregtech.machine.%s.%s", machineName, tier)));

		// Machines that go up to IV
		for(String machineName : arr("assembler", "autoclave", "laser_engraver"))
			for (String tier : arr("lv", "mv", "hv", "ev", "iv"))
				ModHandler.removeRecipeByName(
					new ResourceLocation(String.format("gregtech:gregtech.machine.%s.%s", machineName, tier)));

		// Misc Machines
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:bronze_primitive_blast_furnace"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:diesel_engine"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:electric_blast_furnace"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:engine_intake_casing"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:implosion_compressor"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:large_bronze_boiler"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:large_gas_turbine"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:large_plasma_turbine"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:large_steam_turbine"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:large_steel_boiler"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:large_titanium_boiler"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:large_tungstensteel_boiler"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:magic_energy_absorber"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:multi_furnace"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:pyrolyse_oven"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_alloy_smelter_bronze"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_alloy_smelter_steel"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_boiler_solar_bronze"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_compressor_bronze"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_compressor_steel"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_extractor_bronze"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_extractor_steel"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_furnace_bronze"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_furnace_steel"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_hammer_bronze"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_hammer_steel"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_macerator_bronze"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_macerator_steel"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech:vacuum_freezer"));

		// Tiered Single-block Generators
		for(int i = LV; i <= HV; i++) {
			String tierName = VN[i].toLowerCase();
			for(String fmt : arr("gregtech:diesel_generator_%s",
			                     "gregtech:gas_turbine_%s",
			                     "gregtech:steam_turbine_%s"))
				ModHandler.removeRecipeByName(new ResourceLocation(String.format(fmt, tierName)));
		}

		// Chargers
		for(String vn : VN)
			ModHandler.removeRecipeByName(new ResourceLocation(String.format("gregtech:charger_%s", vn.toLowerCase())));

		// Transformers
		for(int i = EV - 1; i < MetaTileEntities.TRANSFORMER.length; i++)
			ModHandler.removeRecipeByName(
				new ResourceLocation(
					String.format("gregtech:transformer_%s",
					              VN[MetaTileEntities.TRANSFORMER[i].getTier()].toLowerCase())));

		// --- Replacement Recipes ---

		//Power Manipulation Machines
		registerTieredShapedRecipes(
			"ga_charger_",
			MetaTileEntities.CHARGER,
			new String[] {
				"WTW",
				"WMW",
				"BCB"
			},
			sub('M', HULL),
			sub('W', bind(wireGtHex, XF_CABLE_MATERIAL)),
			sub('T', OreDictNames.chestWood),
			sub('B', GA_BATTERY),
			sub('C', CIRCUIT));

		// Replace circuits in EV+ transformers with coil/pic/hpic
		registerTieredShapedRecipes(
			"ga_transformer_",
			Arrays.copyOfRange(MetaTileEntities.TRANSFORMER, EV - 1, MetaTileEntities.TRANSFORMER.length),
			new String[] {
				"KBB",
				"CM ",
				"KBB"
			},
			sub('M', WORSE_HULL),
			sub('C', XF_CABLE),
			sub('B', XF_CABLE_WORSE),
			sub('K', XF_ITEM));

		//Steam Machines
		ModHandler.addShapedRecipe(
			"ga_steam_boiler_solar_bronze",
			MetaTileEntities.STEAM_BOILER_SOLAR_BRONZE.getStackForm(),
			new String[] {
				"GGG",
				"SSS",
				"PMP"
			},
			sub('M', BRONZE_BRICKS_HULL),
			sub('P', pipeSmall, Bronze),
			sub('S', plate, Silver),
			sub('G', new ItemStack(Blocks.GLASS)));

		ModHandler.addShapedRecipe(
			"ga_steam_furnace_bronze",
			MetaTileEntities.STEAM_FURNACE_BRONZE.getStackForm(),
			new String[] {
				"XXX",
				"XMX",
				"XFX"
			},
			sub('M', BRONZE_BRICKS_HULL),
			sub('X', pipeSmall, Bronze),
			sub('F', OreDictNames.craftingFurnace));

		ModHandler.addShapedRecipe(
			"ga_steam_furnace_steel",
			MetaTileEntities.STEAM_FURNACE_STEEL.getStackForm(),
			new String[] {
				"XXX",
				"XMX",
				"XFX"
			},
			sub('M', STEEL_BRICKS_HULL),
			sub('X', pipeSmall, Steel),
			sub('F', OreDictNames.craftingFurnace));

		ModHandler.addShapedRecipe(
			"ga_steam_macerator_bronze",
			MetaTileEntities.STEAM_MACERATOR_BRONZE.getStackForm(),
			new String[] {
				"DXD",
				"XMX",
				"PXP"
			},
			sub('M', BRONZE_HULL),
			sub('X', pipeSmall, Bronze),
			sub('P', OreDictNames.craftingPiston),
			sub('D', new ItemStack(Items.FLINT)));

		ModHandler.addShapedRecipe(
			"ga_steam_macerator_steel",
			MetaTileEntities.STEAM_MACERATOR_STEEL.getStackForm(),
			new String[] {
				"DXD",
				"XMX",
				"PXP"
			},
			sub('M', STEEL_HULL),
			sub('X', pipeSmall, Steel),
			sub('P', OreDictNames.craftingPiston),
			sub('D', new ItemStack(Items.FLINT)));

		ModHandler.addShapedRecipe(
			"ga_steam_extractor_bronze",
			MetaTileEntities.STEAM_EXTRACTOR_BRONZE.getStackForm(),
			new String[] {
				"XXX",
				"PMG",
				"XXX"
			},
			sub('M', BRONZE_HULL),
			sub('X', pipeSmall, Bronze),
			sub('P', OreDictNames.craftingPiston),
			sub('G', new ItemStack(Blocks.GLASS)));

		ModHandler.addShapedRecipe(
			"ga_steam_extractor_steel",
			MetaTileEntities.STEAM_EXTRACTOR_STEEL.getStackForm(),
			new String[] {
				"XXX",
				"PMG",
				"XXX"
			},
			sub('M', STEEL_HULL),
			sub('X', pipeSmall, Steel),
			sub('P', OreDictNames.craftingPiston),
			sub('G', new ItemStack(Blocks.GLASS)));

		ModHandler.addShapedRecipe(
			"ga_steam_hammer_bronze",
			MetaTileEntities.STEAM_HAMMER_BRONZE.getStackForm(),
			new String[] {
				"XPX",
				"XMX",
				"XAX"
			},
			sub('M', BRONZE_HULL),
			sub('X', pipeSmall, Bronze),
			sub('P', OreDictNames.craftingPiston),
			sub('A', OreDictNames.craftingAnvil));

		ModHandler.addShapedRecipe(
			"ga_steam_hammer_steel",
			MetaTileEntities.STEAM_HAMMER_STEEL.getStackForm(),
			new String[] {
				"XPX",
				"XMX",
				"XAX"
			},
			sub('M', STEEL_HULL),
			sub('X', pipeSmall, Steel),
			sub('P', OreDictNames.craftingPiston),
			sub('A', OreDictNames.craftingAnvil));

		ModHandler.addShapedRecipe(
			"ga_steam_compressor_bronze",
			MetaTileEntities.STEAM_COMPRESSOR_BRONZE.getStackForm(),
			new String[] {
				"XXX",
				"PMP",
				"XXX"
			},
			sub('M', BRONZE_HULL),
			sub('X', pipeSmall, Bronze),
			sub('P', OreDictNames.craftingPiston));

		ModHandler.addShapedRecipe(
			"ga_steam_compressor_steel",
			MetaTileEntities.STEAM_COMPRESSOR_STEEL.getStackForm(),
			new String[] {
				"XXX",
				"PMP",
				"XXX"
			},
			sub('M', STEEL_HULL),
			sub('X', pipeSmall, Steel),
			sub('P', OreDictNames.craftingPiston));

		ModHandler.addShapedRecipe(
			"ga_steam_alloy_smelter_bronze",
			MetaTileEntities.STEAM_ALLOY_SMELTER_BRONZE.getStackForm(),
			new String[] {
				"XXX",
				"FMF",
				"XXX"
			},
			sub('M', BRONZE_BRICKS_HULL),
			sub('X', pipeSmall, Bronze),
			sub('F', OreDictNames.craftingFurnace));

		ModHandler.addShapedRecipe(
			"ga_steam_alloy_smelter_steel",
			MetaTileEntities.STEAM_ALLOY_SMELTER_STEEL.getStackForm(),
			new String[] {
				"XXX",
				"FMF",
				"XXX"
			},
			sub('M', STEEL_BRICKS_HULL),
			sub('X', pipeSmall, Steel),
			sub('F', OreDictNames.craftingFurnace));

		//MultiBlocks
		ModHandler.addShapedRecipe(
			"ga_primitive_blast_furnace",
			MetaTileEntities.PRIMITIVE_BLAST_FURNACE.getStackForm(),
			new String[] {
				"hRS",
				"PBR",
				"dRS"
			},
			sub('R', stick, Iron),
			sub('S', screw, Iron),
			sub('P', plate, Iron),
			sub('B', PRIMITIVE_BRICKS));

		ModHandler.addShapedRecipe(
			"ga_electric_blast_furnace",
			MetaTileEntities.ELECTRIC_BLAST_FURNACE.getStackForm(),
			new String[] {
				"FFF",
				"CMC",
				"WCW"
			},
			resolveComponents(
				LV,
				sub('M', INVAR_HEATPROOF),
				sub('F', OreDictNames.craftingFurnace),
				sub('C', CIRCUIT),
				sub('W', CABLE)));

		ModHandler.addShapedRecipe(
			"ga_vacuum_freezer",
			MetaTileEntities.VACUUM_FREEZER.getStackForm(),
			new String[] {
				"PPP",
				"CMC",
				"WCW"
			},
			resolveComponents(
				HV,
				sub('M', ALUMINIUM_FROSTPROOF),
				sub('P', PUMP),
				sub('C', BETTER_CIRCUIT),
				sub('W', CABLE)));

		ModHandler.addShapedRecipe(
			"ga_implosion_compressor",
			MetaTileEntities.IMPLOSION_COMPRESSOR.getStackForm(),
			new String[] {
				"OOO",
				"CMC",
				"WCW"
			},
			sub('M', STEEL_SOLID),
			sub('O', stone, Obsidian),
			sub('C', HV, CIRCUIT),
			sub('W', EV, CABLE));

		ModHandler.addShapedRecipe(
			"ga_pyrolyse_oven",
			MetaTileEntities.PYROLYSE_OVEN.getStackForm(),
			new String[] {
				"WEP",
				"EME",
				"WCP"
			},
			resolveComponents(
				MV,
				sub('M', HULL),
				sub('W', PISTON),
				sub('P', COIL_HEATING_DOUBLE),
				sub('E', CIRCUIT),
				sub('C', PUMP)));

		ModHandler.addShapedRecipe(
			"ga_diesel_engine",
			MetaTileEntities.DIESEL_ENGINE.getStackForm(),
			new String[] {
				"PCP",
				"EME",
				"GWG"
			},
			resolveComponents(
				EV,
				sub('M', HULL),
				sub('P', PISTON),
				sub('E', MOTOR),
				sub('C', BETTER_CIRCUIT),
				sub('W', wireGtSingle, TungstenSteel),
				sub('G', GEAR)));

		ModHandler.addShapedRecipe(
			"ga_engine_intake_casing",
			ENGINE_INTAKE_CASING.getStack(),
			new String[] {
				"PhP",
				"RFR",
				"PwP"
			},
			sub('R', EV, PIPE),
			sub('F', TITANIUM_STABLE),
			sub('P', rotor, Titanium));

		ModHandler.addShapedRecipe(
			"ga_multi_furnace",
			MetaTileEntities.MULTI_FURNACE.getStackForm(),
			new String[] {
				"PPP",
				"ASA",
				"CAC"
			},
			sub('P', Blocks.FURNACE),
			sub('A', HV, CIRCUIT),
			sub('S', INVAR_HEATPROOF),
			sub('C', cableGtSingle, AnnealedCopper));


		ModHandler.addShapedRecipe(
			"ga_large_steam_turbine",
			MetaTileEntities.LARGE_STEAM_TURBINE.getStackForm(),
			new String[] {
				"PSP",
				"SAS",
				"CSC"
			},
			sub('S', LV, GEAR),
			sub('P', HV, CIRCUIT),
			sub('A', HV, HULL),
			sub('C', LV, PIPE_LARGE));

		ModHandler.addShapedRecipe(
			"ga_large_gas_turbine",
			MetaTileEntities.LARGE_GAS_TURBINE.getStackForm(),
			new String[] {
				"PSP",
				"SAS",
				"CSC"
			},
			sub('S', HV, GEAR),
			sub('P', EV, CIRCUIT),
			sub('A', EV, HULL),
			sub('C', HV, PIPE_LARGE));

		ModHandler.addShapedRecipe(
			"ga_large_plasma_turbine",
			MetaTileEntities.LARGE_PLASMA_TURBINE.getStackForm(),
			new String[] {
				"PSP",
				"SAS",
				"CSC"
			},
			sub('S', gear, TungstenSteel),
			sub('P', LuV, CIRCUIT),
			sub('A', UV, HULL),
			sub('C', pipeLarge, TungstenSteel));

		ModHandler.addShapedRecipe(
			"ga_large_bronze_boiler",
			MetaTileEntities.LARGE_BRONZE_BOILER.getStackForm(),
			new String[] {
				"PSP",
				"SAS",
				"PSP"
			},
			resolveComponents(
				LV,
				sub('P', CABLE),
				sub('S', CIRCUIT),
				sub('A', BRONZE_BRICKS)));

		ModHandler.addShapedRecipe(
			"ga_large_steel_boiler",
			MetaTileEntities.LARGE_STEEL_BOILER.getStackForm(),
			new String[] {
				"PSP",
				"SAS",
				"PSP"
			},
			resolveComponents(
				MV,
				sub('P', CABLE),
				sub('S', BETTER_CIRCUIT),
				sub('A', STEEL_SOLID)));

		ModHandler.addShapedRecipe(
			"ga_large_titanium_boiler",
			MetaTileEntities.LARGE_TITANIUM_BOILER.getStackForm(),
			new String[] {
				"PSP",
				"SAS",
				"PSP"
			},
			sub('P', HV, CABLE),
			sub('S', IV, CIRCUIT),
			sub('A', TITANIUM_STABLE));

		ModHandler.addShapedRecipe(
			"ga_large_tungstensteel_boiler",
			MetaTileEntities.LARGE_TUNGSTENSTEEL_BOILER.getStackForm(),
			new String[] {
				"PSP",
				"SAS",
				"PSP"
			},
			sub('P', EV, CABLE),
			sub('S', LuV, CIRCUIT),
			sub('A', TUNGSTENSTEEL_ROBUST));

		ModHandler.addShapedRecipe(
			"ga_assline",
			GATileEntities.ASSEMBLY_LINE.getStackForm(),
			new String[] {
				"CRC",
				"SAS",
				"CRC"
			},
			resolveComponents(
				IV,
				sub('A', HULL),
				sub('R', ROBOT_ARM),
				sub('C', ASSEMBLER_CASING),
				sub('S', CIRCUIT)));

		ModHandler.addShapedRecipe(
			"ga_processing_array",
			GATileEntities.PROCESSING_ARRAY.getStackForm(),
			new String[] {
				"CBC",
				"RHR",
				"CDC"
			},
			resolveComponents(
				IV,
				sub('H', HULL),
				sub('R', ROBOT_ARM),
				sub('C', CIRCUIT),
				sub('B', BATTERY),
				sub('D', MetaItems.TOOL_DATA_ORB)));

		List<Recipe> removals = new ArrayList<>();

		for (Recipe r : RecipeMaps.ASSEMBLER_RECIPES.getRecipeList()) {
			for (ItemStack s : r.getOutputs()) {
				if (s.getItem().getUnlocalizedNameInefficiently(s).contains("large_boiler")) {
					removals.add(r);
					break;
				}
			}
		}

		RecipeMaps.ASSEMBLER_RECIPES
			.recipeBuilder()
			.output(MetaTileEntities.LARGE_STEEL_BOILER)
			.inputs(MetaTileEntities.LARGE_BRONZE_BOILER.getStackForm())
			.input(plate, Steel, 2)
			.input(HV, CIRCUIT, 2)
			.EUt(120).duration(600)
			.buildAndRegister();

		RecipeMaps.ASSEMBLER_RECIPES
			.recipeBuilder()
			.output(MetaTileEntities.LARGE_TITANIUM_BOILER)
			.inputs(MetaTileEntities.LARGE_STEEL_BOILER.getStackForm())
			.input(plate, Titanium, 2)
			.input(HV, CIRCUIT, 2)
			.EUt(500).duration(600)
			.buildAndRegister();

		RecipeMaps.ASSEMBLER_RECIPES
			.recipeBuilder()
			.output(MetaTileEntities.LARGE_TUNGSTENSTEEL_BOILER)
			.inputs(MetaTileEntities.LARGE_TITANIUM_BOILER.getStackForm())
			.input(plate, TungstenSteel, 2)
			.input(HV, CIRCUIT, 2)
			.EUt(2000).duration(600)
			.buildAndRegister();

		//Storage

		if (GAConfig.GT6.registerDrums) {
			// Wood Drum
			ModHandler.addShapedRecipe(
				"wooden_barrel",
				GATileEntities.WOODEN_DRUM.getStackForm(),
				new String[] {
					"rSs",
					"PRP",
					"PRP"
				},
				sub('S', "slimeball"),
				sub('P', plank, Wood),
				sub('R', stickLong, Iron));

			// Metal Drums
			Function<Material, UnificationEntry> plateFn;
			// use curved plates if all bending-related configs are enabled
			if (GAConfig.GT6.BendingCurvedPlates && GAConfig.GT6.BendingCylinders && GAConfig.GT6.addCurvedPlates) {
				plateFn = x -> new UnificationEntry(plateCurved, x);
			}else // use normal ones
				plateFn = x -> new UnificationEntry(plate, x);

			for(var drum : GATileEntities.DRUMS)
				if(drum.getMaterial() != Wood)
					ModHandler.addShapedRecipe(
						String.format("%s_drum", drum.getMaterial()),
						drum.getStackForm(),
						new String[] {
							" h ",
							"PRP",
							"PRP"
						},
						sub('P', plateFn.apply(drum.getMaterial())),
						sub('R', stickLong, drum.getMaterial())
					);
		}

		if (GAConfig.Misc.registerCrates) {
			ModHandler.addShapedRecipe(
				"wooden_crate",
				GATileEntities.WOODEN_CRATE.getStackForm(),
				new String[] {
					"RPR",
					"PsP",
					"RPR"
				},
				sub('P', plank, Wood),
				sub('R', screw, Iron));

			// Metal Crates
			for(var crate : GATileEntities.CRATES)
				if(crate.getMaterial() != Wood)
					ModHandler.addShapedRecipe(
						String.format("%s_crate", crate.getMaterial()),
						crate.getStackForm(),
						new String[] {
							"RPR",
							"PhP",
							"RPR"
						},
						sub('P', plate, crate.getMaterial()),
						sub('R', stickLong, crate.getMaterial()));
		}

		//Generators
		registerMachineRecipe(
			GATileEntities.NAQUADAH_REACTOR,
			new String[] {
				"RCR",
				"FMF",
				"QCQ"
			},
			sub('M', HULL),
			sub('Q', CABLE_QUAD),
			sub('C', BETTER_CIRCUIT),
			sub('F', FIELD_GENERATOR),
			sub('R', STICK_RADIOACTIVE));

		registerTieredShapedRecipes(
			"ga_diesel_generator_",
			MetaTileEntities.DIESEL_GENERATOR,
			new String[] {
				"PCP",
				"EME",
				"GWG"
			},
			sub('M', HULL),
			sub('P', PISTON),
			sub('E', MOTOR),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GEAR));

		registerTieredShapedRecipes(
			"ga_gas_turbine_",
			MetaTileEntities.GAS_TURBINE,
			new String[] {
				"CRC",
				"RMR",
				"EWE"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('R', ROTOR),
			sub('C', CIRCUIT),
			sub('W', CABLE));

		registerTieredShapedRecipes(
			"ga_steam_turbine_",
			MetaTileEntities.STEAM_TURBINE,
			new String[]{
				"PCP",
				"RMR",
				"EWE"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('R', ROTOR),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('P', PIPE));

		ModHandler.addShapedRecipe(
			"ga_magic_energy_absorber",
			MetaTileEntities.MAGIC_ENERGY_ABSORBER.getStackForm(),
			new String[] {
				"PCP",
				"PMP",
				"PCP"
			},
			resolveComponents(EV,
			                  sub('M', HULL),
			                  sub('P', SENSOR),
			                  sub('C', BETTER_CIRCUIT)));

		//Machines
		registerMachineRecipe(
			GATileEntities.CLUSTERMILL,
			new String[] {
				"MMM",
				"CHC",
				"MMM"
			},
			sub('M', MOTOR),
			sub('C', CIRCUIT),
			sub('H', HULL));

		registerMachineRecipe(
			MetaTileEntities.ALLOY_SMELTER,
			new String[] {
				"ECE",
				"CMC",
				"WCW"
			},
			sub('M', HULL),
			sub('E', CIRCUIT),
			sub('W', CABLE),
			sub('C', COIL_HEATING_DOUBLE));

		registerMachineRecipe(
			MetaTileEntities.ASSEMBLER,
			new String[] {
				"ACA",
				"VMV",
				"WCW"
			},
			sub('M', HULL),
			sub('V', CONVEYOR),
			sub('A', ROBOT_ARM),
			sub('C', CIRCUIT),
			sub('W', CABLE));

		registerMachineRecipe(
			MetaTileEntities.BENDER,
			new String[] {
				"PwP",
				"CMC",
				"EWE"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('P', PISTON),
			sub('C', CIRCUIT),
			sub('W', CABLE));

		registerMachineRecipe(
			MetaTileEntities.CANNER,
			new String[] {
				"WPW",
				"CMC",
				"GGG"
			},
			sub('M', HULL),
			sub('P', PUMP),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.COMPRESSOR,
			new String[] {
				" C ",
				"PMP",
				"WCW"
			},
			sub('M', HULL),
			sub('P', PISTON),
			sub('C', CIRCUIT),
			sub('W', CABLE));

		registerMachineRecipe(
			MetaTileEntities.CUTTER,
			new String[] {
				"WCG",
				"VMB",
				"CWE"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('V', CONVEYOR),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS),
			sub('B', OreDictNames.craftingDiamondBlade));

		registerMachineRecipe(
			MetaTileEntities.ELECTRIC_FURNACE,
			new String[] {
				"ECE",
				"CMC",
				"WCW"
			},
			sub('M', HULL),
			sub('E', CIRCUIT),
			sub('W', CABLE),
			sub('C', COIL_HEATING));

		registerMachineRecipe(
			MetaTileEntities.EXTRACTOR,
			new String[] {
				"GCG",
				"EMP",
				"WCW"
			},
			sub('M', HULL),
			sub('E', PISTON),
			sub('P', PUMP),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.EXTRUDER,
			new String[] {
				"CCE",
				"XMP",
				"CCE"
			},
			sub('M', HULL),
			sub('X', PISTON),
			sub('E', CIRCUIT),
			sub('P', PIPE),
			sub('C', COIL_HEATING_DOUBLE));

		registerMachineRecipe(
			MetaTileEntities.LATHE,
			new String[] {
				"WCW",
				"EMD",
				"CWP"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('P', PISTON),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('D', DIAMOND));

		registerMachineRecipe(
			MetaTileEntities.MACERATOR,
			new String[] {
				"PEG",
				"WWM",
				"CCW"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('P', PISTON),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GRINDER));

		registerMachineRecipe(
			MetaTileEntities.MICROWAVE,
			new String[] {
				"LWC",
				"LMR",
				"LEC"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('R', EMITTER),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('L', plate, Lead));

		registerMachineRecipe(
			MetaTileEntities.WIREMILL,
			new String[] {
				"EWE",
				"CMC",
				"EWE"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('C', CIRCUIT),
			sub('W', CABLE));

		registerMachineRecipe(
			MetaTileEntities.CENTRIFUGE,
			new String[] {
				"CEC",
				"WMW",
				"CEC"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('C', CIRCUIT),
			sub('W', CABLE));

		registerMachineRecipe(
			MetaTileEntities.ELECTROLYZER,
			new String[] {
				"IGI",
				"IMI",
				"CWC"
			},
			sub('M', HULL),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('I', WIRE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.THERMAL_CENTRIFUGE,
			new String[] {
				"CEC",
				"OMO",
				"WEW"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('O', COIL_HEATING_DOUBLE));

		registerMachineRecipe(
			MetaTileEntities.ORE_WASHER,
			new String[] {
				"RGR",
				"CEC",
				"WMW"
			},
			sub('M', HULL),
			sub('R', ROTOR),
			sub('E', MOTOR),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.PACKER,
			new String[] {
				"BCB",
				"RMV",
				"WCW"
			},
			sub('M', HULL),
			sub('R', ROBOT_ARM),
			sub('V', CONVEYOR),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('B', OreDictNames.chestWood));

		registerMachineRecipe(
			MetaTileEntities.UNPACKER,
			new String[] {
				"BCB",
				"VMR",
				"WCW"
			},
			sub('M', HULL),
			sub('R', ROBOT_ARM),
			sub('V', CONVEYOR),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('B', OreDictNames.chestWood));

		registerMachineRecipe(
			MetaTileEntities.CHEMICAL_REACTOR,
			new String[] {
				"GRG",
				"WEW",
				"CMC"
			},
			sub('M', HULL),
			sub('R', ROTOR),
			sub('E', MOTOR),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.FLUID_CANNER,
			new String[] {
				"GCG",
				"GMG",
				"WPW"
			},
			sub('M', HULL),
			sub('P', PUMP),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.BREWERY,
			new String[] {
				"GPG",
				"WMW",
				"CBC"
			},
			sub('M', HULL),
			sub('P', PUMP),
			sub('B', STICK_DISTILLATION),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.FERMENTER,
			new String[] {
				"WPW",
				"GMG",
				"WCW"
			},
			sub('M', HULL),
			sub('P', PUMP),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.FLUID_EXTRACTOR,
			new String[] {
				"GCG",
				"PME",
				"WCW"
			},
			sub('M', HULL),
			sub('E', PISTON),
			sub('P', PUMP),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.FLUID_SOLIDIFIER,
			new String[] {
				"PGP",
				"WMW",
				"CBC"
			},
			sub('M', HULL),
			sub('P', PUMP),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS),
			sub('B', OreDictNames.chestWood));

		registerMachineRecipe(
			MetaTileEntities.DISTILLERY,
			new String[] {
				"GBG",
				"CMC",
				"WPW"
			},
			sub('M', HULL),
			sub('P', PUMP),
			sub('B', STICK_DISTILLATION),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.CHEMICAL_BATH,
			new String[] {
				"VGW",
				"PGV",
				"CMC"
			},
			sub('M', HULL),
			sub('P', PUMP),
			sub('V', CONVEYOR),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.POLARIZER,
			new String[] {
				"ZSZ",
				"WMW",
				"ZSZ"
			},
			sub('M', HULL),
			sub('S', STICK_ELECTROMAGNETIC),
			sub('Z', COIL_ELECTRIC),
			sub('W', CABLE));

		registerMachineRecipe(
			MetaTileEntities.ELECTROMAGNETIC_SEPARATOR,
			new String[] {
				"VWZ",
				"WMS",
				"CWZ"
			},
			sub('M', HULL),
			sub('S', STICK_ELECTROMAGNETIC),
			sub('Z', COIL_ELECTRIC),
			sub('V', CONVEYOR),
			sub('C', CIRCUIT),
			sub('W', CABLE));

		registerMachineRecipe(
			MetaTileEntities.AUTOCLAVE,
			new String[] {
				"IGI",
				"IMI",
				"CPC"
			},
			sub('M', HULL),
			sub('P', PUMP),
			sub('C', CIRCUIT),
			sub('I', PLATE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.MIXER,
			new String[] {
				"GRG",
				"GEG",
				"CMC"
			},
			sub('M', HULL),
			sub('E', MOTOR),
			sub('R', ROTOR),
			sub('C', CIRCUIT),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.LASER_ENGRAVER,
			new String[] {
				"PEP",
				"CMC",
				"WCW"
			},
			sub('M', HULL),
			sub('E', EMITTER),
			sub('P', PISTON),
			sub('C', CIRCUIT),
			sub('W', CABLE));

		registerMachineRecipe(
			MetaTileEntities.FORMING_PRESS,
			new String[] {
				"WPW",
				"CMC",
				"WPW"
			},
			sub('M', HULL),
			sub('P', PISTON),
			sub('C', CIRCUIT),
			sub('W', CABLE));

		registerMachineRecipe(
			MetaTileEntities.FORGE_HAMMER,
			new String[] {
				"WPW",
				"CMC",
				"WAW"
			},
			sub('M', HULL),
			sub('P', PISTON),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('A', OreDictNames.craftingAnvil));

		registerMachineRecipe(
			MetaTileEntities.FLUID_HEATER,
			new String[] {
				"OGO",
				"PMP",
				"WCW"
			},
			sub('M', HULL),
			sub('P', PUMP),
			sub('O', COIL_HEATING_DOUBLE),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('G', GLASS));

		registerMachineRecipe(
			MetaTileEntities.SIFTER,
			new String[] {
				"WFW",
				"PMP",
				"CFC"
			},
			sub('M', HULL),
			sub('P', PISTON),
			sub('F', MetaItems.ITEM_FILTER),
			sub('C', CIRCUIT),
			sub('W', CABLE));

		registerMachineRecipe(
			MetaTileEntities.ARC_FURNACE,
			new String[] {
				"WGW",
				"CMC",
				"PPP"
			},
			sub('M', HULL),
			sub('P', PLATE),
			sub('C', CIRCUIT),
			sub('W', CABLE_QUAD),
			sub('G', ingot, Graphite));

		registerMachineRecipe(
			MetaTileEntities.PLASMA_ARC_FURNACE,
			new String[] {
				"WGW",
				"CMC",
				"TPT"
			},
			sub('M', HULL),
			sub('P', PLATE),
			sub('C', BETTER_CIRCUIT),
			sub('W', CABLE_QUAD),
			sub('T', PUMP),
			sub('G', ingot, Graphite));

		registerMachineRecipe(
			MetaTileEntities.PUMP,
			new String[] {
				"WGW",
				"GMG",
				"TGT"
			},
			sub('M', HULL),
			sub('W', CIRCUIT),
			sub('G', PUMP),
			sub('T', PIPE));

		registerMachineRecipe(
			MetaTileEntities.AIR_COLLECTOR,
			new String[] {
				"WFW",
				"PHP",
				"WCW"
			},
			sub('W', Blocks.IRON_BARS),
			sub('F', MetaItems.ITEM_FILTER),
			sub('P', PUMP),
			sub('H', HULL),
			sub('C', CIRCUIT));

		if (GAConfig.GT5U.highTierPumps)
			registerMachineRecipe(
				GATileEntities.PUMP,
				new String[] {
					"WGW",
					"GMG",
					"TGT"
				},
				sub('M', HULL),
				sub('W', CIRCUIT),
				sub('G', PUMP),
				sub('T', PIPE));

		if (GAConfig.GT5U.highTierAlloySmelter)
			registerMachineRecipe(
				GATileEntities.ALLOY_SMELTER,
				new String[] {
					"ECE",
					"CMC",
					"WCW"
				},
				sub('M', HULL),
				sub('E', CIRCUIT),
				sub('W', CABLE),
				sub('C', COIL_HEATING_DOUBLE));

		if (GAConfig.GT5U.highTierAssemblers)
			registerMachineRecipe(
				GATileEntities.ASSEMBLER,
				new String[] {
					"ACA",
					"VMV",
					"WCW"
				},
				sub('M', HULL),
				sub('V', CONVEYOR),
				sub('A', ROBOT_ARM),
				sub('C', CIRCUIT),
				sub('W', CABLE));

		if (GAConfig.GT5U.highTierBenders)
			registerMachineRecipe(
				GATileEntities.BENDER,
				new String[] {
					"PWP",
					"CMC",
					"EWE"
				},
				sub('M', HULL),
				sub('E', MOTOR),
				sub('P', PISTON),
				sub('C', CIRCUIT),
				sub('W', CABLE));

		if (GAConfig.GT5U.highTierCanners)
			registerMachineRecipe(
				GATileEntities.CANNER,
				new String[] {
					"WPW",
					"CMC",
					"GGG"
				},
				sub('M', HULL),
				sub('P', PUMP),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierCompressors)
			registerMachineRecipe(
				GATileEntities.COMPRESSOR,
				new String[] {
					" C ",
					"PMP",
					"WCW"
				},
				sub('M', HULL),
				sub('P', PISTON),
				sub('C', CIRCUIT),
				sub('W', CABLE));

		if (GAConfig.GT5U.highTierCutters)
			registerMachineRecipe(
				GATileEntities.CUTTER,
				new String[] {
					"WCG",
					"VMB",
					"CWE"
				},
				sub('M', HULL),
				sub('E', MOTOR),
				sub('V', CONVEYOR),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS),
				sub('B', OreDictNames.craftingDiamondBlade));

		if (GAConfig.GT5U.highTierElectricFurnace)
			registerMachineRecipe(
				GATileEntities.ELECTRIC_FURNACE,
				new String[] {
					"ECE",
					"CMC",
					"WCW"
				},
				sub('M', HULL),
				sub('E', CIRCUIT),
				sub('W', CABLE),
				sub('C', COIL_HEATING));

		if (GAConfig.GT5U.highTierExtractors)
			registerMachineRecipe(
				GATileEntities.EXTRACTOR,
				new String[] {
					"GCG",
					"EMP",
					"WCW"
				},
				sub('M', HULL),
				sub('E', PISTON),
				sub('P', PUMP),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierExtruders)
			registerMachineRecipe(
				GATileEntities.EXTRUDER,
				new String[] {
					"CCE",
					"XMP",
					"CCE"
				},
				sub('M', HULL),
				sub('X', PISTON),
				sub('E', CIRCUIT),
				sub('P', PIPE),
				sub('C', COIL_HEATING_DOUBLE));

		if (GAConfig.GT5U.highTierLathes)
			registerMachineRecipe(
				GATileEntities.LATHE,
				new String[] {
					"WCW",
					"EMD",
					"CWP"
				},
				sub('M', HULL),
				sub('E', MOTOR),
				sub('P', PISTON),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('D', DIAMOND));

		if (GAConfig.GT5U.highTierMacerators)
			registerMachineRecipe(
				GATileEntities.MACERATOR,
				new String[] {
					"PEG",
					"WWM",
					"CCW"
				},
				sub('M', HULL),
				sub('E', MOTOR),
				sub('P', PISTON),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GRINDER));

		if (GAConfig.GT5U.highTierMicrowaves)
			registerMachineRecipe(
				GATileEntities.MICROWAVE,
				new String[] {
					"LWC",
					"LMR",
					"LEC"
				},
				sub('M', HULL),
				sub('E', MOTOR),
				sub('R', EMITTER),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('L', plate, Lead));

		if (GAConfig.GT5U.highTierWiremills)
			registerMachineRecipe(
				GATileEntities.WIREMILL,
				new String[] {
					"EWE",
					"CMC",
					"EWE"
				},
				sub('M', HULL),
				sub('E', MOTOR),
				sub('C', CIRCUIT),
				sub('W', CABLE));

		if (GAConfig.GT5U.highTierCentrifuges)
			registerMachineRecipe(
				GATileEntities.CENTRIFUGE,
				new String[] {
					"CEC",
					"WMW",
					"CEC"
				},
				sub('M', HULL),
				sub('E', MOTOR),
				sub('C', CIRCUIT),
				sub('W', CABLE));

		if (GAConfig.GT5U.highTierElectrolyzers)
			registerMachineRecipe(
				GATileEntities.ELECTROLYZER,
				new String[] {
					"IGI",
					"IMI",
					"CWC"
				},
				sub('M', HULL),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('I', WIRE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierThermalCentrifuges)
			registerMachineRecipe(
				GATileEntities.THERMAL_CENTRIFUGE,
				new String[] {
					"CEC",
					"OMO",
					"WEW"
				},
				sub('M', HULL),
				sub('E', MOTOR),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('O', COIL_HEATING_DOUBLE));

		if (GAConfig.GT5U.highTierOreWashers)
			registerMachineRecipe(
				GATileEntities.ORE_WASHER,
				new String[] {
					"RGR",
					"CEC",
					"WMW"
				},
				sub('M', HULL),
				sub('R', ROTOR),
				sub('E', MOTOR),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierPackers)
			registerMachineRecipe(
				GATileEntities.PACKER,
				new String[] {
					"BCB",
					"RMV",
					"WCW"
				},
				sub('M', HULL),
				sub('R', ROBOT_ARM),
				sub('V', CONVEYOR),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('B', OreDictNames.chestWood));

		if (GAConfig.GT5U.highTierUnpackers)
			registerMachineRecipe(
				GATileEntities.UNPACKER,
				new String[] {
					"BCB",
					"VMR",
					"WCW"
				},
				sub('M', HULL),
				sub('R', ROBOT_ARM),
				sub('V', CONVEYOR),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('B', OreDictNames.chestWood));

		if (GAConfig.GT5U.highTierChemicalReactors)
			registerMachineRecipe(
				GATileEntities.CHEMICAL_REACTOR,
				new String[] {
					"GRG",
					"WEW",
					"CMC"
				},
				sub('M', HULL),
				sub('R', ROTOR),
				sub('E', MOTOR),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierFluidCanners)
			registerMachineRecipe(
				GATileEntities.FLUID_CANNER,
				new String[] {
					"GCG",
					"GMG",
					"WPW"
				},
				sub('M', HULL),
				sub('P', PUMP),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierBreweries)
			registerMachineRecipe(
				GATileEntities.BREWERY,
				new String[] {
					"GPG",
					"WMW",
					"CBC"
				},
				sub('M', HULL),
				sub('P', PUMP),
				sub('B', STICK_DISTILLATION),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierFermenters)
			registerMachineRecipe(
				GATileEntities.FERMENTER,
				new String[] {
					"WPW",
					"GMG",
					"WCW"
				},
				sub('M', HULL),
				sub('P', PUMP),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierFluidExtractors)
			registerMachineRecipe(
				GATileEntities.FLUID_EXTRACTOR,
				new String[] {
					"GCG",
					"PME",
					"WCW"
				},
				sub('M', HULL),
				sub('E', PISTON),
				sub('P', PUMP),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierFluidSolidifiers)
			registerMachineRecipe(
				GATileEntities.FLUID_SOLIDIFIER,
				new String[] {
					"PGP",
					"WMW",
					"CBC"
				},
				sub('M', HULL),
				sub('P', PUMP),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS),
				sub('B', OreDictNames.chestWood));

		if (GAConfig.GT5U.highTierDistilleries)
			registerMachineRecipe(
				GATileEntities.DISTILLERY,
				new String[] {
					"GBG",
					"CMC",
					"WPW"
				},
				sub('M', HULL),
				sub('P', PUMP),
				sub('B', STICK_DISTILLATION),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierChemicalBaths)
			registerMachineRecipe(
				GATileEntities.CHEMICAL_BATH,
				new String[] {
					"VGW",
					"PGV",
					"CMC"
				},
				sub('M', HULL),
				sub('P', PUMP),
				sub('V', CONVEYOR),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierPolarizers)
			registerMachineRecipe(
				GATileEntities.POLARIZER,
				new String[] {
					"ZSZ",
					"WMW",
					"ZSZ"
				},
				sub('M', HULL),
				sub('S', STICK_ELECTROMAGNETIC),
				sub('Z', COIL_ELECTRIC),
				sub('W', CABLE));

		if (GAConfig.GT5U.highTierElectromagneticSeparators)
			registerMachineRecipe(
				GATileEntities.ELECTROMAGNETIC_SEPARATOR,
				new String[] {
					"VWZ",
					"WMS",
					"CWZ"
				},
				sub('M', HULL),
				sub('S', STICK_ELECTROMAGNETIC),
				sub('Z', COIL_ELECTRIC),
				sub('V', CONVEYOR),
				sub('C', CIRCUIT),
				sub('W', CABLE));

		if (GAConfig.GT5U.highTierAutoclaves)
			registerMachineRecipe(
				GATileEntities.AUTOCLAVE,
				new String[] {
					"IGI",
					"IMI",
					"CPC"
				},
				sub('M', HULL),
				sub('P', PUMP),
				sub('C', CIRCUIT),
				sub('I', PLATE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierMixers)
			registerMachineRecipe(
				GATileEntities.MIXER,
				new String[] {
					"GRG",
					"GEG",
					"CMC"
				},
				sub('M', HULL),
				sub('E', MOTOR),
				sub('R', ROTOR),
				sub('C', CIRCUIT),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierLaserEngravers)
			registerMachineRecipe(
				GATileEntities.LASER_ENGRAVER,
				new String[] {
					"PEP",
					"CMC",
					"WCW"
				},
				sub('M', HULL),
				sub('E', EMITTER),
				sub('P', PISTON),
				sub('C', CIRCUIT),
				sub('W', CABLE));

		if (GAConfig.GT5U.highTierFormingPresses)
			registerMachineRecipe(
				GATileEntities.FORMING_PRESS,
				new String[] {
					"WPW",
					"CMC",
					"WPW"
				},
				sub('M', HULL),
				sub('P', PISTON),
				sub('C', CIRCUIT),
				sub('W', CABLE));

		if (GAConfig.GT5U.highTierForgeHammers)
			registerMachineRecipe(
				GATileEntities.FORGE_HAMMER,
				new String[] {
					"WPW",
					"CMC",
					"WAW"
				},
				sub('M', HULL),
				sub('P', PISTON),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('A', OreDictNames.craftingAnvil));

		if (GAConfig.GT5U.highTierFluidHeaters)
			registerMachineRecipe(
				GATileEntities.FLUID_HEATER,
				new String[] {
					"OGO",
					"PMP",
					"WCW"
				},
				sub('M', HULL),
				sub('P', PUMP),
				sub('O', COIL_HEATING_DOUBLE),
				sub('C', CIRCUIT),
				sub('W', CABLE),
				sub('G', GLASS));

		if (GAConfig.GT5U.highTierSifters)
			registerMachineRecipe(
				GATileEntities.SIFTER,
				new String[] {
					"WFW",
					"PMP",
					"CFC"
				},
				sub('M', HULL),
				sub('P', PISTON),
				sub('F', MetaItems.ITEM_FILTER),
				sub('C', CIRCUIT),
				sub('W', CABLE));

		if (GAConfig.GT5U.highTierArcFurnaces)
			registerMachineRecipe(
				GATileEntities.ARC_FURNACE,
				new String[] {
					"WGW",
					"CMC",
					"PPP"
				},
				sub('M', HULL),
				sub('P', PLATE),
				sub('C', CIRCUIT),
				sub('W', CABLE_QUAD),
				sub('G', ingot, Graphite));

		if (GAConfig.GT5U.highTierPlasmaArcFurnaces)
			registerMachineRecipe(
				GATileEntities.PLASMA_ARC_FURNACE,
				new String[] {
					"WGW",
					"CMC",
					"TPT"
				},
				sub('M', HULL),
				sub('P', PLATE),
				sub('C', BETTER_CIRCUIT),
				sub('W', CABLE_QUAD),
				sub('T', PUMP),
				sub('G', ingot, Graphite));

		registerMachineRecipe(
			Arrays.stream(GATileEntities.MASS_FAB)
			      .filter(Objects::nonNull)
			      .toArray(SimpleMachineMetaTileEntity[]::new),
			new String[] {
				"CFC",
				"QMQ",
				"CFC"
			},
			sub('M', HULL),
			sub('Q', CABLE_QUAD),
			sub('C', BETTER_CIRCUIT),
			sub('F', FIELD_GENERATOR));

		registerMachineRecipe(
			Arrays.stream(GATileEntities.REPLICATOR)
			      .filter(Objects::nonNull)
			      .toArray(SimpleMachineMetaTileEntity[]::new),
			new String[] {
				"EFE",
				"CMC",
				"EQE"
			},
			sub('M', HULL),
			sub('Q', CABLE_QUAD),
			sub('C', BETTER_CIRCUIT),
			sub('F', FIELD_GENERATOR),
			sub('E', EMITTER));

		if (GAConfig.Misc.highTierCollector)
			registerMachineRecipe(
				GATileEntities.AIR_COLLECTOR,
				new String[] {
					"WFW",
					"PHP",
					"WCW"
				},
				sub('W', Blocks.IRON_BARS),
				sub('F', MetaItems.ITEM_FILTER),
				sub('P', PUMP),
				sub('H', HULL),
				sub('C', CIRCUIT));

		ModHandler.addShapedRecipe(
			"machine_access_interface",
			GATileEntities.MACHINE_ACCESS_INTERFACE.getStackForm(),
			new String[] {
				"C",
				"H"
			},
			sub('C', IV, CIRCUIT),
			sub('H', MetaTileEntities.ITEM_IMPORT_BUS[ULV].getStackForm()));

		registerMachineRecipe(
			GATileEntities.BUNDLER,
			new String[] {
				"BCB",
				"RMV",
				"WCW"
			},
			sub('M', HULL),
			sub('R', ROBOT_ARM),
			sub('V', CONVEYOR),
			sub('C', CIRCUIT),
			sub('W', CABLE),
			sub('B', OreDictNames.craftingPiston));
	}

	public static <T extends MetaTileEntity & ITiered>
	void registerMachineRecipe(T[] metaTileEntities,
	                           String[] definition,
	                           Substitution<?>... subs)
	{
		registerTieredShapedRecipes(metaTileEntities,
		                            x -> String.format("ga_%s", x.getMetaName()),
		                            ITiered::getTier,
		                            MetaTileEntity::getStackForm,
		                            definition,
		                            subs);
	}
}
