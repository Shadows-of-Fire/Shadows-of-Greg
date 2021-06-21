package gregicadditions.recipes;

import gregicadditions.GAEnums;
import gregicadditions.GAMaterials;
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
        //Replace plates with tools, add pipe. Basically change to the GT5U recipe
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

        //Machine Recipe Removals

        //Pumps
        //Remove the GTCE Pump assembler recipes to match our changed recipes
        for(MaterialStack stackFluid : cableFluids) {
            IngotMaterial material = (IngotMaterial) stackFluid.material;
            removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] {OreDictUnifier.get(OrePrefix.plate, Materials.Tin, 2), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tin), OreDictUnifier.get(OrePrefix.screw, Materials.Tin), OreDictUnifier.get(OrePrefix.rotor, Materials.Tin), MetaItems.ELECTRIC_MOTOR_LV.getStackForm()}, new FluidStack[] {material.getFluid((int) stackFluid.amount)});
            removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] {OreDictUnifier.get(OrePrefix.plate, Materials.Bronze, 2), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Copper), OreDictUnifier.get(OrePrefix.screw, Materials.Bronze), OreDictUnifier.get(OrePrefix.rotor, Materials.Bronze), MetaItems.ELECTRIC_MOTOR_MV.getStackForm()}, new FluidStack[] {material.getFluid((int) stackFluid.amount)});
            //TODO, fix cable type once it is fixed in GTCE (Copper -> Gold)
            removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] {OreDictUnifier.get(OrePrefix.plate, Materials.Steel, 2), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Copper), OreDictUnifier.get(OrePrefix.screw, Materials.Steel), OreDictUnifier.get(OrePrefix.rotor, Materials.Steel), MetaItems.ELECTRIC_MOTOR_HV.getStackForm()}, new FluidStack[] {material.getFluid((int) stackFluid.amount)});
            removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] {OreDictUnifier.get(OrePrefix.plate, Materials.StainlessSteel, 2), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Aluminium), OreDictUnifier.get(OrePrefix.screw, Materials.StainlessSteel), OreDictUnifier.get(OrePrefix.rotor, Materials.StainlessSteel), MetaItems.ELECTRIC_MOTOR_EV.getStackForm()}, new FluidStack[] {material.getFluid((int) stackFluid.amount)});
            removeRecipesByInputs(RecipeMaps.ASSEMBLER_RECIPES, new ItemStack[] {OreDictUnifier.get(OrePrefix.plate, Materials.TungstenSteel, 2), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tungsten), OreDictUnifier.get(OrePrefix.screw, Materials.TungstenSteel), OreDictUnifier.get(OrePrefix.rotor, Materials.TungstenSteel), MetaItems.ELECTRIC_MOTOR_IV.getStackForm()}, new FluidStack[] {material.getFluid((int) stackFluid.amount)});
        }

        //Machine Recipe Additions--------------------------------------------------------------------------

        //Field Generators
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
        GARecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(OrePrefix.stickLong, Materials.NeodymiumMagnetic)
                .input(OrePrefix.stickLong, Materials.HSSG, 2)
                .input(OrePrefix.ring, Materials.HSSG, 4)
                .input(GAEnums.GAOrePrefix.round, Materials.HSSG, 16)
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
                .input(GAEnums.GAOrePrefix.round, Materials.HSSE, 16)
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
                .input(GAEnums.GAOrePrefix.round, GAMaterials.NEUTRONIUM, 16)
                .input(OrePrefix.wireGtSingle, MarkerMaterials.Tier.Superconductor, 64)
                .input(OrePrefix.wireGtSingle, MarkerMaterials.Tier.Superconductor, 64)
                .input(OrePrefix.wireGtSingle, MarkerMaterials.Tier.Superconductor, 64)
                .input(OrePrefix.wireGtSingle, MarkerMaterials.Tier.Superconductor, 64)
                .input(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(1296), Materials.Lubricant.getFluid(2000))
                .outputs(MetaItems.ELECTRIC_MOTOR_UV.getStackForm())
                .duration(600).EUt(163840).buildAndRegister();

        //Conveyors
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
        //Adjust the GTCE Pump Assembler recipe to match our pump recipe
        for (MaterialStack stackFluid : cableFluids) {
            IngotMaterial m = (IngotMaterial) stackFluid.material;
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .input(OrePrefix.rotor, Materials.Tin)
                    .input(OrePrefix.cableGtSingle, Materials.Tin)
                    .input(OrePrefix.screw, Materials.Tin)
                    .input(OrePrefix.pipeMedium, Materials.Bronze)
                    .inputs(MetaItems.ELECTRIC_MOTOR_LV.getStackForm())
                    .fluidInputs(m.getFluid((int) stackFluid.amount))
                    .outputs(MetaItems.ELECTRIC_PUMP_LV.getStackForm())
                    .duration(100).EUt(30).buildAndRegister();

            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .input(OrePrefix.rotor, Materials.Bronze)
                    .input(OrePrefix.cableGtSingle, Materials.Copper)
                    .input(OrePrefix.screw, Materials.Bronze)
                    .input(OrePrefix.pipeMedium, Materials.Steel)
                    .inputs(MetaItems.ELECTRIC_MOTOR_MV.getStackForm())
                    .fluidInputs(m.getFluid((int) stackFluid.amount))
                    .outputs(MetaItems.ELECTRIC_PUMP_MV.getStackForm())
                    .duration(100).EUt(120).buildAndRegister();

            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .input(OrePrefix.rotor, Materials.Steel)
                    .input(OrePrefix.cableGtSingle, Materials.Gold)
                    .input(OrePrefix.screw, Materials.Steel)
                    .input(OrePrefix.pipeMedium, Materials.StainlessSteel)
                    .inputs(MetaItems.ELECTRIC_MOTOR_HV.getStackForm())
                    .fluidInputs(m.getFluid((int) stackFluid.amount))
                    .outputs(MetaItems.ELECTRIC_PUMP_HV.getStackForm())
                    .duration(100).EUt(480).buildAndRegister();

            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .input(OrePrefix.rotor, Materials.StainlessSteel)
                    .input(OrePrefix.cableGtSingle, Materials.Aluminium)
                    .input(OrePrefix.screw, Materials.StainlessSteel)
                    .input(OrePrefix.pipeMedium, Materials.Titanium)
                    .inputs(MetaItems.ELECTRIC_MOTOR_EV.getStackForm())
                    .fluidInputs(m.getFluid((int) stackFluid.amount))
                    .outputs(MetaItems.ELECTRIC_PUMP_EV.getStackForm())
                    .duration(100).EUt(1920).buildAndRegister();

            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .input(OrePrefix.rotor, Materials.TungstenSteel)
                    .input(OrePrefix.cableGtSingle, Materials.Tungsten)
                    .input(OrePrefix.screw, Materials.TungstenSteel)
                    .input(OrePrefix.pipeMedium, Materials.TungstenSteel)
                    .inputs(MetaItems.ELECTRIC_MOTOR_IV.getStackForm())
                    .fluidInputs(m.getFluid((int) stackFluid.amount))
                    .outputs(MetaItems.ELECTRIC_PUMP_IV.getStackForm())
                    .duration(100).EUt(7680).buildAndRegister();
        }

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
