package gregicadditions.recipes;

import gregicadditions.GAMaterials;
import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.IngotMaterial;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.common.items.MetaItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static gregicadditions.recipes.helpers.HelperMethods.removeCraftingRecipes;
import static gregicadditions.recipes.helpers.HelperMethods.removeRecipesByInputs;


/**
 * This class registers recipes for Components, such as Robot Arms, etc, for all tiers
 */
public class ComponentRegistration {

    private static final MaterialStack[] cableFluids = { new MaterialStack(Materials.Rubber, 144), new MaterialStack(Materials.StyreneButadieneRubber, 108), new MaterialStack(Materials.SiliconeRubber, 72) };

    public static void init() {

        //Crafting Recipe Removals---------------------------------------------------------------------------------

        removeCraftingRecipes(MetaItems.ROBOT_ARM_IV.getStackForm());

        removeCraftingRecipes(MetaItems.ELECTRIC_PUMP_LV.getStackForm());
        removeCraftingRecipes(MetaItems.ELECTRIC_PUMP_MV.getStackForm());
        removeCraftingRecipes(MetaItems.ELECTRIC_PUMP_HV.getStackForm());
        removeCraftingRecipes(MetaItems.ELECTRIC_PUMP_EV.getStackForm());
        removeCraftingRecipes(MetaItems.ELECTRIC_PUMP_IV.getStackForm());

        //Crafting Recipe Additions-----------------------------------------------------------------------

        //Robot Arm
        //Replace the circuit in IV robot arm with tier appropriate circuit
        ModHandler.addShapedRecipe("ga_iv_robot_arm", MetaItems.ROBOT_ARM_IV.getStackForm(),
                "CCC", "MRM", "PSR",
                'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tungsten),
                'M', MetaItems.ELECTRIC_MOTOR_IV.getStackForm(),
                'R', OreDictUnifier.get(OrePrefix.stick, Materials.TungstenSteel),
                'P', MetaItems.ELECTRIC_PISTON_IV.getStackForm(),
                'S', "circuitElite");

        //Pump
        //Replace plates with tools, add pipe

        ModHandler.addShapedRecipe("lv_electric_pump_paper", MetaItems.ELECTRIC_PUMP_LV.getStackForm(),
                "SRH", "dPw", "HMC",
                'S', OreDictUnifier.get(OrePrefix.screw, Materials.Tin),
                'R', OreDictUnifier.get(OrePrefix.rotor, Materials.Tin),
                'H', OreDictUnifier.get(OrePrefix.ring, Materials.Paper),
                'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Bronze),
                'M', MetaItems.ELECTRIC_MOTOR_LV.getStackForm(),
                'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tin));

        for(MaterialStack stackFluid : cableFluids) {
            IngotMaterial m = (IngotMaterial) stackFluid.material;
            ModHandler.addShapedRecipe(String.format("lv_electric_pump_%s", m.toString()), MetaItems.ELECTRIC_PUMP_LV.getStackForm(),
                    "SRH", "dPw", "HMC",
                    'S', OreDictUnifier.get(OrePrefix.screw, Materials.Tin),
                    'R', OreDictUnifier.get(OrePrefix.rotor, Materials.Tin),
                    'H', OreDictUnifier.get(OrePrefix.ring, m),
                    'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Bronze),
                    'M', MetaItems.ELECTRIC_MOTOR_LV.getStackForm(),
                    'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tin));

            ModHandler.addShapedRecipe(String.format("mv_electric_pump_%s", m.toString()), MetaItems.ELECTRIC_PUMP_MV.getStackForm(),
                    "SRH", "dPw", "HMC",
                    'S', OreDictUnifier.get(OrePrefix.screw, Materials.Bronze),
                    'R', OreDictUnifier.get(OrePrefix.rotor, Materials.Bronze),
                    'H', OreDictUnifier.get(OrePrefix.ring, m),
                    'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Steel),
                    'M', MetaItems.ELECTRIC_MOTOR_MV.getStackForm(),
                    'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Copper));

            ModHandler.addShapedRecipe(String.format("hv_electric_pump_%s", m.toString()), MetaItems.ELECTRIC_PUMP_HV.getStackForm(),
                    "SRH", "dPw", "HMC",
                    'S', OreDictUnifier.get(OrePrefix.screw, Materials.Steel),
                    'R', OreDictUnifier.get(OrePrefix.rotor, Materials.Steel),
                    'H', OreDictUnifier.get(OrePrefix.ring, m),
                    'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.StainlessSteel),
                    'M', MetaItems.ELECTRIC_MOTOR_HV.getStackForm(),
                    'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Gold));

            ModHandler.addShapedRecipe(String.format("ev_electric_pump_%s", m.toString()), MetaItems.ELECTRIC_PUMP_EV.getStackForm(),
                    "SRH", "dPw", "HMC",
                    'S', OreDictUnifier.get(OrePrefix.screw, Materials.StainlessSteel),
                    'R', OreDictUnifier.get(OrePrefix.rotor, Materials.StainlessSteel),
                    'H', OreDictUnifier.get(OrePrefix.ring, m),
                    'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Titanium),
                    'M', MetaItems.ELECTRIC_MOTOR_EV.getStackForm(),
                    'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Aluminium));

            ModHandler.addShapedRecipe(String.format("iv_electric_pump_%s", m.toString()), MetaItems.ELECTRIC_PUMP_IV.getStackForm(),
                    "SRH", "dPw", "HMC",
                    'S', OreDictUnifier.get(OrePrefix.screw, Materials.TungstenSteel),
                    'R', OreDictUnifier.get(OrePrefix.rotor, Materials.TungstenSteel),
                    'H', OreDictUnifier.get(OrePrefix.ring, m),
                    'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.TungstenSteel),
                    'M', MetaItems.ELECTRIC_MOTOR_IV.getStackForm(),
                    'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tungsten));
        }

        //Machine Recipe Additions--------------------------------------------------------------------------

        //Field Generators
        //Adjusting the time requirement
        removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES,
                new ItemStack[] {OreDictUnifier.get(OrePrefix.circuit, MarkerMaterials.Tier.Basic, 4), OreDictUnifier.get(OrePrefix.dust, Materials.EnderPearl)},
                new FluidStack[]{Materials.Osmium.getFluid(288)});
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(CountableIngredient.from(OrePrefix.circuit, MarkerMaterials.Tier.Basic, 4))
                .inputs(CountableIngredient.from(OrePrefix.dust, Materials.EnderPearl))
                .fluidInputs(Materials.Osmium.getFluid(288))
                .outputs(MetaItems.FIELD_GENERATOR_LV.getStackForm())
                .duration(100).EUt(30).buildAndRegister();

        //Adjusting the time requirement
        removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES,
                new ItemStack[] {OreDictUnifier.get(OrePrefix.circuit, MarkerMaterials.Tier.Good, 4), OreDictUnifier.get(OrePrefix.dust, Materials.EnderEye)},
                new FluidStack[]{Materials.Osmium.getFluid(576)});
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(CountableIngredient.from(OrePrefix.circuit, MarkerMaterials.Tier.Good, 4), CountableIngredient.from(OrePrefix.dust, Materials.EnderEye))
                .fluidInputs(Materials.Osmium.getFluid(576))
                .outputs(MetaItems.FIELD_GENERATOR_MV.getStackForm())
                .duration(100).EUt(120).buildAndRegister();

        //Adjusting the time requirement
        removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES,
                new ItemStack[] {OreDictUnifier.get(OrePrefix.circuit, MarkerMaterials.Tier.Advanced, 4), MetaItems.QUANTUM_EYE.getStackForm()},
                new FluidStack[]{Materials.Osmium.getFluid(1152)});
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(CountableIngredient.from(OrePrefix.circuit, MarkerMaterials.Tier.Advanced, 4), CountableIngredient.from(MetaItems.QUANTUM_EYE.getStackForm()))
                .fluidInputs(Materials.Osmium.getFluid(1152))
                .outputs(MetaItems.FIELD_GENERATOR_HV.getStackForm())
                .duration(100).EUt(480).buildAndRegister();

        //Adjusting the time requirement, Fixing the circuit requirement
        removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES,
                new ItemStack[] {OreDictUnifier.get(OrePrefix.circuit, MarkerMaterials.Tier.Elite, 4), OreDictUnifier.get(OrePrefix.dust, Materials.NetherStar)},
                new FluidStack[]{Materials.Osmium.getFluid(2304)});
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(CountableIngredient.from(OrePrefix.circuit, MarkerMaterials.Tier.Extreme, 4), CountableIngredient.from(OrePrefix.dust, Materials.NetherStar))
                .fluidInputs(Materials.Osmium.getFluid(2304))
                .outputs(MetaItems.FIELD_GENERATOR_EV.getStackForm())
                .duration(100).EUt(1920).buildAndRegister();

        //Adjusting the time requirement, Fixing the circuit requirement
        removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES,
                new ItemStack[] {OreDictUnifier.get(OrePrefix.circuit, MarkerMaterials.Tier.Master, 4), MetaItems.QUANTUM_STAR.getStackForm()},
                new FluidStack[]{Materials.Osmium.getFluid(4608)});
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(CountableIngredient.from(OrePrefix.circuit, MarkerMaterials.Tier.Elite, 4), CountableIngredient.from(MetaItems.QUANTUM_STAR.getStackForm()))
                .fluidInputs(Materials.Osmium.getFluid(4608))
                .outputs(MetaItems.FIELD_GENERATOR_IV.getStackForm())
                .duration(100).EUt(7680).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, Materials.HSSG)
                .inputs(MetaItems.QUANTUM_STAR.getStackForm(), MetaItems.EMITTER_LUV.getStackForm(4))
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.cableGtOctal, Materials.YttriumBariumCuprate, 4)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Master, 16)
                .fluidInputs(Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.FIELD_GENERATOR_LUV.getStackForm())
                .duration(600).EUt(30720).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, Materials.HSSE)
                .inputs(MetaItems.QUANTUM_STAR.getStackForm(), MetaItems.EMITTER_ZPM.getStackForm(4))
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.cableGtOctal, Materials.YttriumBariumCuprate, 4)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Ultimate, 16)
                .fluidInputs(Materials.SolderingAlloy.getFluid(1152))
                .outputs(MetaItems.FIELD_GENERATOR_ZPM.getStackForm())
                .duration(600).EUt(245760).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, GAMaterials.NEUTRONIUM)
                .inputs(MetaItems.GRAVI_STAR.getStackForm(), MetaItems.EMITTER_UV.getStackForm(4))
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.wireFine, Materials.Osmium, 64)
                .input(OrePrefix.cableGtOctal, Materials.YttriumBariumCuprate, 4)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Superconductor, 16)
                .fluidInputs(Materials.SolderingAlloy.getFluid(2304))
                .outputs(MetaItems.FIELD_GENERATOR_UV.getStackForm())
                .duration(600).EUt(491520).buildAndRegister();

        //Electric Motors
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Tin, 2)
                .input(OrePrefix.stick, Materials.Iron, 2)
                .input(OrePrefix.stick, Materials.IronMagnetic)
                .fluidInputs(Materials.Copper.getFluid(288))
                .outputs(MetaItems.ELECTRIC_MOTOR_LV.getStackForm())
                .duration(100).EUt(10).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Tin, 2)
                .input(OrePrefix.stick, Materials.Steel, 2)
                .input(OrePrefix.stick, Materials.SteelMagnetic)
                .fluidInputs(Materials.Copper.getFluid(288))
                .outputs(MetaItems.ELECTRIC_MOTOR_LV.getStackForm())
                .duration(100).EUt(10).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Copper, 2)
                .input(OrePrefix.stick, Materials.Aluminium, 2)
                .input(OrePrefix.stick, Materials.SteelMagnetic)
                .fluidInputs(Materials.Copper.getFluid(576))
                .outputs(MetaItems.ELECTRIC_MOTOR_MV.getStackForm())
                .duration(100).EUt(40).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Gold, 2)
                .input(OrePrefix.stick, Materials.StainlessSteel, 2)
                .input(OrePrefix.stick, Materials.SteelMagnetic)
                .fluidInputs(Materials.Copper.getFluid(1152))
                .outputs(MetaItems.ELECTRIC_MOTOR_HV.getStackForm())
                .duration(100).EUt(160).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Aluminium, 2)
                .input(OrePrefix.stick, Materials.Titanium, 2)
                .input(OrePrefix.stick, Materials.NeodymiumMagnetic)
                .fluidInputs(Materials.AnnealedCopper.getFluid(2304))
                .outputs(MetaItems.ELECTRIC_MOTOR_EV.getStackForm())
                .duration(600).EUt(640).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Tungsten, 2)
                .input(OrePrefix.stick, Materials.TungstenSteel, 2)
                .input(OrePrefix.stick, Materials.NeodymiumMagnetic)
                .fluidInputs(Materials.AnnealedCopper.getFluid(4608))
                .outputs(MetaItems.ELECTRIC_MOTOR_IV.getStackForm())
                .duration(100).EUt(2560).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.stickLong, Materials.NeodymiumMagnetic)
                .input(OrePrefix.stickLong, Materials.HSSG, 2)
                .input(OrePrefix.ring, Materials.HSSG, 4)
                .input(OrePrefix.valueOf("round"), Materials.HSSG, 16)
                .input(OrePrefix.wireFine, Materials.AnnealedCopper, 64)
                .input(OrePrefix.wireFine, Materials.AnnealedCopper, 64)
                .input(OrePrefix.wireFine, Materials.AnnealedCopper, 64)
                .input(OrePrefix.wireFine, Materials.AnnealedCopper, 64)
                .input(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144), Materials.Lubricant.getFluid(250))
                .outputs(MetaItems.ELECTRIC_MOTOR_LUV.getStackForm())
                .duration(600).EUt(10240).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.stickLong, Materials.NeodymiumMagnetic)
                .input(OrePrefix.stickLong, Materials.HSSE, 2)
                .input(OrePrefix.ring, Materials.HSSE, 4)
                .input(OrePrefix.valueOf("round"), Materials.HSSE, 16)
                .input(OrePrefix.wireFine, Materials.Platinum, 64)
                .input(OrePrefix.wireFine, Materials.Platinum, 64)
                .input(OrePrefix.wireFine, Materials.Platinum, 64)
                .input(OrePrefix.wireFine, Materials.Platinum, 64)
                .input(OrePrefix.cableGtQuadruple, Materials.VanadiumGallium, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(288), Materials.Lubricant.getFluid(750))
                .outputs(MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm())
                .duration(600).EUt(40960).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.block, Materials.NeodymiumMagnetic)
                .input(OrePrefix.stickLong, GAMaterials.NEUTRONIUM, 2)
                .input(OrePrefix.ring, GAMaterials.NEUTRONIUM, 4)
                .input(OrePrefix.valueOf("round"), GAMaterials.NEUTRONIUM, 16)
                .input(OrePrefix.wireGtSingle, MarkerMaterials.Tier.Superconductor, 64)
                .input(OrePrefix.wireGtSingle, MarkerMaterials.Tier.Superconductor, 64)
                .input(OrePrefix.wireGtSingle, MarkerMaterials.Tier.Superconductor, 64)
                .input(OrePrefix.wireGtSingle, MarkerMaterials.Tier.Superconductor, 64)
                .input(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(1296), Materials.Lubricant.getFluid(2000))
                .outputs(MetaItems.ELECTRIC_MOTOR_UV.getStackForm())
                .duration(600).EUt(163840).buildAndRegister();

        //Conveyors
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Tin)
                .inputs(MetaItems.ELECTRIC_MOTOR_LV.getStackForm(2))
                .fluidInputs(Materials.Rubber.getFluid(864))
                .outputs(MetaItems.CONVEYOR_MODULE_LV.getStackForm())
                .duration(100).EUt(15).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Copper)
                .inputs(MetaItems.ELECTRIC_MOTOR_MV.getStackForm(2))
                .fluidInputs(Materials.Rubber.getFluid(864))
                .outputs(MetaItems.CONVEYOR_MODULE_MV.getStackForm())
                .duration(100).EUt(60).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Gold)
                .inputs(MetaItems.ELECTRIC_MOTOR_HV.getStackForm(2))
                .fluidInputs(Materials.Rubber.getFluid(864))
                .outputs(MetaItems.CONVEYOR_MODULE_HV.getStackForm())
                .duration(100).EUt(240).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Aluminium)
                .inputs(MetaItems.ELECTRIC_MOTOR_EV.getStackForm(2))
                .fluidInputs(Materials.Rubber.getFluid(864))
                .outputs(MetaItems.CONVEYOR_MODULE_EV.getStackForm())
                .duration(100).EUt(960).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtSingle, Materials.Tungsten)
                .inputs(MetaItems.ELECTRIC_MOTOR_IV.getStackForm(2))
                .fluidInputs(Materials.Rubber.getFluid(864))
                .outputs(MetaItems.CONVEYOR_MODULE_IV.getStackForm())
                .duration(100).EUt(3840).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaItems.ELECTRIC_MOTOR_LUV.getStackForm(2))
                .input(OrePrefix.plate, Materials.HSSG, 8)
                .input(OrePrefix.gear, Materials.HSSG, 4)
                .input(OrePrefix.stick, Materials.HSSG, 4)
                .input(OrePrefix.ingot, Materials.HSSG, 2)
                .input(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 2)
                .fluidInputs(Materials.StyreneButadieneRubber.getFluid(1440), Materials.Lubricant.getFluid(250))
                .outputs(MetaItems.CONVEYOR_MODULE_LUV.getStackForm())
                .duration(600).EUt(15360).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm(2))
                .input(OrePrefix.plate, Materials.HSSE, 8)
                .input(OrePrefix.gear, Materials.HSSE, 4)
                .input(OrePrefix.stick, Materials.HSSE, 4)
                .input(OrePrefix.ingot, Materials.HSSE, 2)
                .input(OrePrefix.cableGtSingle, Materials.VanadiumGallium, 2)
                .fluidInputs(Materials.StyreneButadieneRubber.getFluid(2880), Materials.Lubricant.getFluid(750))
                .outputs(MetaItems.CONVEYOR_MODULE_ZPM.getStackForm())
                .duration(600).EUt(61440).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaItems.ELECTRIC_MOTOR_UV.getStackForm(2))
                .input(OrePrefix.plate, GAMaterials.NEUTRONIUM, 8)
                .input(OrePrefix.gear, GAMaterials.NEUTRONIUM, 4)
                .input(OrePrefix.stick, GAMaterials.NEUTRONIUM, 4)
                .input(OrePrefix.ingot, GAMaterials.NEUTRONIUM, 2)
                .input(OrePrefix.cableGtSingle, Materials.NiobiumTitanium, 2)
                .fluidInputs(Materials.StyreneButadieneRubber.getFluid(2880), Materials.Lubricant.getFluid(2000))
                .outputs(MetaItems.CONVEYOR_MODULE_UV.getStackForm())
                .duration(600).EUt(245760).buildAndRegister();

        //Emitters
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Basic, 2)
                .input(OrePrefix.cableGtSingle, Materials.Tin, 2)
                .input(OrePrefix.gem, Materials.Quartzite)
                .fluidInputs(Materials.Brass.getFluid(288))
                .outputs(MetaItems.EMITTER_LV.getStackForm())
                .duration(100).EUt(15).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Good, 2)
                .input(OrePrefix.cableGtSingle, Materials.Copper, 2)
                .input(OrePrefix.gem, Materials.NetherQuartz)
                .fluidInputs(Materials.Electrum.getFluid(288))
                .outputs(MetaItems.EMITTER_MV.getStackForm())
                .duration(100).EUt(60).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Advanced, 2)
                .input(OrePrefix.cableGtSingle, Materials.Gold, 2)
                .input(OrePrefix.gem, Materials.Emerald)
                .fluidInputs(Materials.Chrome.getFluid(288))
                .outputs(MetaItems.EMITTER_HV.getStackForm())
                .duration(100).EUt(240).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Extreme, 2)
                .input(OrePrefix.cableGtSingle, Materials.Aluminium, 2)
                .input(OrePrefix.gem, Materials.EnderPearl)
                .fluidInputs(Materials.Platinum.getFluid(288))
                .outputs(MetaItems.EMITTER_EV.getStackForm())
                .duration(100).EUt(960).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Elite, 2)
                .input(OrePrefix.cableGtSingle, Materials.Tungsten, 2)
                .input(OrePrefix.gem, Materials.EnderEye)
                .fluidInputs(Materials.Osmium.getFluid(288))
                .outputs(MetaItems.EMITTER_IV.getStackForm())
                .duration(100).EUt(3840).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, Materials.HSSG)
                .inputs(MetaItems.EMITTER_IV.getStackForm(2))
                .input(OrePrefix.foil, Materials.Electrum, 64)
                .input(OrePrefix.foil, Materials.Electrum, 64)
                .input(OrePrefix.foil, Materials.Electrum, 64)
                .input(OrePrefix.wireGtDouble, Materials.YttriumBariumCuprate, 8)
                .input(OrePrefix.gemExquisite, Materials.Ruby, 2)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Extreme, 8)
                .fluidInputs(Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.EMITTER_LUV.getStackForm())
                .duration(600).EUt(15360).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, Materials.HSSE)
                .inputs(MetaItems.EMITTER_LUV.getStackForm(2))
                .input(OrePrefix.foil, Materials.Platinum, 64)
                .input(OrePrefix.foil, Materials.Platinum, 64)
                .input(OrePrefix.foil, Materials.Platinum, 64)
                .input(OrePrefix.wireGtDouble, Materials.VanadiumGallium, 8)
                .input(OrePrefix.gemExquisite, Materials.Emerald, 2)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Elite, 8)
                .fluidInputs(Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.EMITTER_ZPM.getStackForm())
                .duration(600).EUt(61440).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, GAMaterials.NEUTRONIUM)
                .inputs(MetaItems.EMITTER_ZPM.getStackForm(2))
                .input(OrePrefix.foil, Materials.Osmiridium, 64)
                .input(OrePrefix.foil, Materials.Osmiridium, 64)
                .input(OrePrefix.foil, Materials.Osmiridium, 64)
                .input(OrePrefix.wireGtDouble, Materials.NiobiumTitanium, 8)
                .input(OrePrefix.gemExquisite, Materials.Diamond, 2)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Master, 8)
                .fluidInputs(Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.EMITTER_UV.getStackForm())
                .duration(600).EUt(245760).buildAndRegister();

        //Electric Pump
        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaItems.ELECTRIC_MOTOR_LUV.getStackForm())
                .input(OrePrefix.pipeSmall, Materials.Ultimet, 2)
                .input(OrePrefix.screw, Materials.HSSG, 8)
                .input(OrePrefix.ring, Materials.SiliconeRubber, 16)
                .input(OrePrefix.rotor, Materials.HSSG, 2)
                .input(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144), Materials.Lubricant.getFluid(250))
                .outputs(MetaItems.ELECTRIC_PUMP_LUV.getStackForm())
                .duration(600).EUt(15360).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm())
                .input(OrePrefix.pipeMedium, Materials.Ultimet, 2)
                .input(OrePrefix.screw, Materials.HSSE, 8)
                .input(OrePrefix.ring, Materials.SiliconeRubber, 16)
                .input(OrePrefix.rotor, Materials.HSSE, 2)
                .input(OrePrefix.cableGtSingle, Materials.VanadiumGallium, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(288), Materials.Lubricant.getFluid(750))
                .outputs(MetaItems.ELECTRIC_PUMP_ZPM.getStackForm())
                .duration(600).EUt(61440).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaItems.ELECTRIC_MOTOR_UV.getStackForm())
                .input(OrePrefix.pipeLarge, Materials.Ultimet, 2)
                .input(OrePrefix.screw, GAMaterials.NEUTRONIUM, 8)
                .input(OrePrefix.ring, Materials.SiliconeRubber, 16)
                .input(OrePrefix.rotor, GAMaterials.NEUTRONIUM, 2)
                .input(OrePrefix.cableGtSingle, Materials.NiobiumTitanium, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(1296), Materials.Lubricant.getFluid(2000))
                .outputs(MetaItems.ELECTRIC_PUMP_UV.getStackForm())
                .duration(600).EUt(245760).buildAndRegister();

        //Electric Piston
        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaItems.ELECTRIC_MOTOR_LUV.getStackForm())
                .input(OrePrefix.plate, Materials.HSSG, 8)
                .input(OrePrefix.gearSmall, Materials.HSSG, 8)
                .input(OrePrefix.stick, Materials.HSSG, 4)
                .input(OrePrefix.ingot, Materials.HSSG, 2)
                .input(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144), Materials.Lubricant.getFluid(250))
                .outputs(MetaItems.ELECTRIC_PISTON_LUV.getStackForm())
                .duration(600).EUt(15360).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm())
                .input(OrePrefix.plate, Materials.HSSE, 8)
                .input(OrePrefix.gearSmall, Materials.HSSE, 8)
                .input(OrePrefix.stick, Materials.HSSE, 4)
                .input(OrePrefix.ingot, Materials.HSSE, 2)
                .input(OrePrefix.cableGtSingle, Materials.VanadiumGallium, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(288), Materials.Lubricant.getFluid(750))
                .outputs(MetaItems.ELECTRIC_PISTON_ZPM.getStackForm())
                .duration(600).EUt(61440).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaItems.ELECTRIC_MOTOR_UV.getStackForm())
                .input(OrePrefix.plate, GAMaterials.NEUTRONIUM, 8)
                .input(OrePrefix.gearSmall, GAMaterials.NEUTRONIUM, 8)
                .input(OrePrefix.stick, GAMaterials.NEUTRONIUM, 4)
                .input(OrePrefix.ingot, GAMaterials.NEUTRONIUM, 2)
                .input(OrePrefix.cableGtSingle, Materials.NiobiumTitanium, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(1296), Materials.Lubricant.getFluid(2000))
                .outputs(MetaItems.ELECTRIC_PISTON_UV.getStackForm())
                .duration(600).EUt(245760).buildAndRegister();

        //Robot Arm
        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtDouble, Materials.YttriumBariumCuprate, 16)
                .input(OrePrefix.screw, Materials.HSSG, 16)
                .input(OrePrefix.stick, Materials.HSSG, 16)
                .input(OrePrefix.ingot, Materials.HSSG)
                .inputs(MetaItems.ELECTRIC_MOTOR_LUV.getStackForm(2))
                .inputs(MetaItems.ELECTRIC_PISTON_LUV.getStackForm())
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Extreme, 8)
                .fluidInputs(Materials.SolderingAlloy.getFluid(576), Materials.Lubricant.getFluid(250))
                .outputs(MetaItems.ROBOT_ARM_LUV.getStackForm())
                .duration(600).EUt(20480).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtDouble, Materials.VanadiumGallium, 16)
                .input(OrePrefix.screw, Materials.HSSE, 16)
                .input(OrePrefix.stick, Materials.HSSE, 16)
                .input(OrePrefix.ingot, Materials.HSSE)
                .inputs(MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm(2))
                .inputs(MetaItems.ELECTRIC_PISTON_ZPM.getStackForm())
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Elite, 8)
                .fluidInputs(Materials.SolderingAlloy.getFluid(1152), Materials.Lubricant.getFluid(750))
                .outputs(MetaItems.ROBOT_ARM_ZPM.getStackForm())
                .duration(600).EUt(81920).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.cableGtDouble, Materials.NiobiumTitanium, 16)
                .input(OrePrefix.screw, GAMaterials.NEUTRONIUM, 16)
                .input(OrePrefix.stick, GAMaterials.NEUTRONIUM, 16)
                .input(OrePrefix.ingot, GAMaterials.NEUTRONIUM)
                .inputs(MetaItems.ELECTRIC_MOTOR_UV.getStackForm(2))
                .inputs(MetaItems.ELECTRIC_PISTON_UV.getStackForm())
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Master, 8)
                .fluidInputs(Materials.SolderingAlloy.getFluid(2304), Materials.Lubricant.getFluid(2000))
                .outputs(MetaItems.ROBOT_ARM_UV.getStackForm())
                .duration(600).EUt(327680).buildAndRegister();

        //Sensor
        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, Materials.HSSG)
                .inputs(MetaItems.SENSOR_IV.getStackForm(2))
                .input(OrePrefix.foil, Materials.Electrum, 64)
                .input(OrePrefix.foil, Materials.Electrum, 64)
                .input(OrePrefix.foil, Materials.Electrum, 64)
                .input(OrePrefix.wireGtDouble, Materials.YttriumBariumCuprate, 8)
                .input(OrePrefix.gemExquisite, Materials.Ruby, 2)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Extreme, 8)
                .fluidInputs(Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.SENSOR_LUV.getStackForm())
                .duration(600).EUt(15360).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, Materials.HSSE)
                .inputs(MetaItems.SENSOR_LUV.getStackForm(2))
                .input(OrePrefix.foil, Materials.Platinum, 64)
                .input(OrePrefix.foil, Materials.Platinum, 64)
                .input(OrePrefix.foil, Materials.Platinum, 64)
                .input(OrePrefix.wireGtDouble, Materials.VanadiumGallium, 8)
                .input(OrePrefix.gemExquisite, Materials.Emerald, 2)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Elite, 8)
                .fluidInputs(Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.SENSOR_ZPM.getStackForm())
                .duration(600).EUt(61440).buildAndRegister();

        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, GAMaterials.NEUTRONIUM)
                .inputs(MetaItems.SENSOR_ZPM.getStackForm(2))
                .input(OrePrefix.foil, Materials.Osmiridium, 64)
                .input(OrePrefix.foil, Materials.Osmiridium, 64)
                .input(OrePrefix.foil, Materials.Osmiridium, 64)
                .input(OrePrefix.wireGtDouble, Materials.NiobiumTitanium, 8)
                .input(OrePrefix.gemExquisite, Materials.Diamond, 2)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.Master, 8)
                .fluidInputs(Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.SENSOR_UV.getStackForm())
                .duration(600).EUt(245760).buildAndRegister();

    }
}
