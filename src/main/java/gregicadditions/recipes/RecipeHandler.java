package gregicadditions.recipes;


import gregicadditions.GAConfig;
import gregicadditions.GAEnums;
import gregicadditions.item.GAMetaItems;
import gregtech.api.GTValues;
import gregtech.api.items.toolitem.ToolMetaItem;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.*;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.util.GTUtility;
import gregtech.common.items.MetaItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.stream.IntStream;

import static gregicadditions.GAEnums.GAOrePrefix.ingotDouble;
import static gregicadditions.GAEnums.GAOrePrefix.plateCurved;
import static gregicadditions.GregicAdditions.MODID;
import static gregicadditions.item.GAMetaItems.BENDING_CYLINDER;
import static gregicadditions.item.GAMetaItems.SMALL_BENDING_CYLINDER;
import static gregicadditions.recipes.helpers.HelperMethods.*;
import static gregicadditions.recipes.GARecipeMaps.CLUSTER_MILL_RECIPES;
import static gregtech.api.GTValues.L;
import static gregtech.api.GTValues.M;
import static gregtech.api.recipes.RecipeMaps.*;
import static gregtech.api.recipes.ingredients.IntCircuitIngredient.getIntegratedCircuit;
import static gregtech.api.unification.material.type.DustMaterial.MatFlags.NO_SMASHING;
import static gregtech.loaders.oreprocessing.WireRecipeHandler.INSULATION_MATERIALS;
import static gregtech.api.unification.ore.OrePrefix.*;


/**
 * Primary Recipe Registration Class
 */
public class RecipeHandler {

    private static final MaterialStack[] cableDusts = { new MaterialStack(Materials.Polydimethylsiloxane, 1), new MaterialStack(Materials.PolyvinylChloride, 1) };


    public static void register() {
        ingot.addProcessingHandler(IngotMaterial.class, RecipeHandler::processIngot);
        foil.addProcessingHandler(IngotMaterial.class, RecipeHandler::processFoil);
        GAEnums.GAOrePrefix.round.addProcessingHandler(IngotMaterial.class, RecipeHandler::processRound);
        gem.addProcessingHandler(GemMaterial.class, RecipeHandler::processGem);

        if(GAConfig.Misc.PackagerDustRecipes) {
            dustSmall.addProcessingHandler(DustMaterial.class, RecipeHandler::processSmallDust);
            dustTiny.addProcessingHandler(DustMaterial.class, RecipeHandler::processTinyDust);
            //TODO, this one needs looking at, don't see where this is registered
            nugget.addProcessingHandler(IngotMaterial.class, RecipeHandler::processNugget);
        }

        plate.addProcessingHandler(IngotMaterial.class, RecipeHandler::processPlate);

        if(GAConfig.GT6.PlateDoubleIngot && GAConfig.GT6.addDoubleIngots) {
            plate.addProcessingHandler(IngotMaterial.class, RecipeHandler::processDoubleIngot);
        }
        if(GAConfig.GT6.BendingCurvedPlates && GAConfig.GT6.BendingCylinders && GAConfig.GT6.addCurvedPlates) {
            plateCurved.addProcessingHandler(IngotMaterial.class, RecipeHandler::processPlateCurved);
        }
        if(GAConfig.GT6.BendingRings && GAConfig.GT6.BendingCylinders) {
            ring.addProcessingHandler(IngotMaterial.class, RecipeHandler::processRing);
        }

        for(OrePrefix wirePrefix : GAEnums.WIRE_DOUBLING_ORDER) {
            wirePrefix.addProcessingHandler(IngotMaterial.class, RecipeHandler::processWireGt);
        }
    }

    /**
     * Ingot Material Handler. Generates:
     *
     * + Bending Cylinder Recipes
     * + GT6 Wrench Recipes (plates over ingots)
     */
    private static void processIngot(OrePrefix ingot, IngotMaterial material) {

        // Tools
        if(!material.hasFlag(NO_SMASHING) && material.toolDurability > 0) {

            // GT6 Expensive Wrenches (Plates over Ingots)
            if(GAConfig.GT6.ExpensiveWrenches) {

                removeRecipeByName(String.format("%s:wrench_%s", MODID, material.toString()));

                ModHandler.addShapedRecipe(String.format("ga_wrench_%s", material.toString()), MetaItems.WRENCH.getStackForm(material),
                        "XhX", "XXX", " X ",
                        'X', new UnificationEntry(plate, material));
            }

            // Bending Cylinders
            if(GAConfig.GT6.BendingCylinders) {

                ModHandler.addShapedRecipe(String.format("cylinder_%s", material.toString()), ((ToolMetaItem<?>.MetaToolValueItem) BENDING_CYLINDER).getStackForm(material),
                        "sfh", "XXX", "XXX",
                        'X', new UnificationEntry(ingot, material));

                ModHandler.addShapedRecipe(String.format("small_cylinder_%s", material.toString()), ((ToolMetaItem<?>.MetaToolValueItem) SMALL_BENDING_CYLINDER).getStackForm(material),
                        "sfh", "XXX",
                        'X', new UnificationEntry(ingot, material));
            }
        }
    }

    /**
     * Tiny Dust Material Handler. Generates:
     * Schematic Recipes in favor of Integrated Circuit Packager Recipes
     */
    private static void processTinyDust(OrePrefix dustTiny, DustMaterial material) {

        removeRecipesByInputs(PACKER_RECIPES, OreDictUnifier.get(dustTiny, material, 9), getIntegratedCircuit(1));

        PACKER_RECIPES.recipeBuilder().duration(100).EUt(4)
                .input(dustTiny, material, 9)
                .notConsumable(GAMetaItems.SCHEMATIC_DUST.getStackForm())
                .output(dust, material)
                .buildAndRegister();
    }

    /**
     * Small Dust Material Handler. Generates:
     * Schematic Recipes in favor of Integrated Circuit Packager Recipes
     */
    private static void processSmallDust(OrePrefix dustSmall, DustMaterial material) {

        removeRecipesByInputs(PACKER_RECIPES, OreDictUnifier.get(dustSmall, material, 4), getIntegratedCircuit(2));

        PACKER_RECIPES.recipeBuilder().duration(100).EUt(4)
                .input(dustSmall, material, 4)
                .notConsumable(GAMetaItems.SCHEMATIC_DUST.getStackForm())
                .output(dust, material)
                .buildAndRegister();
    }

    /**
     * Nugget Material Handler. Generates:
     *
     * + Schematic Packing and Unpacking Recipes instead of Integrated Circuits if enabled
     *
     */
    private static void processNugget(OrePrefix nugget, IngotMaterial material) {

        // Packer
        removeRecipesByInputs(PACKER_RECIPES, OreDictUnifier.get(nugget, material, 9), getIntegratedCircuit(1));

        PACKER_RECIPES.recipeBuilder().duration(100).EUt(4)
                .input(nugget, material, 9)
                .notConsumable(GAMetaItems.SCHEMATIC_3X3.getStackForm())
                .output(ingot, material)
                .buildAndRegister();

        // Unpacker
        removeRecipesByInputs(UNPACKER_RECIPES, OreDictUnifier.get(ingot, material, 1), getIntegratedCircuit(1));

        UNPACKER_RECIPES.recipeBuilder().duration(100).EUt(4)
                .input(ingot, material)
                .notConsumable(GAMetaItems.SCHEMATIC_3X3.getStackForm())
                .output(nugget, material, 9)
                .buildAndRegister();
    }

    /**
     * Foil Material Handler. Generates:
     *
     * + Bending Cylinder Foil Recipes if enabled
     * + Cluster Mill Foil Recipes if enabled
     *
     * - Removes Bender Foils if Cluster Mill is enabled
     */
    private static void processFoil(OrePrefix foil, IngotMaterial material) {
        if (!OreDictUnifier.get(foil, material).isEmpty()) {

            // Handcrafting foils
            if(GAConfig.GT6.BendingFoils && GAConfig.GT6.BendingCylinders) {
                ModHandler.addShapedRecipe(String.format("foil_%s", material.toString()), OreDictUnifier.get(foil, material, 2),
                        "hPC",
                        'P', new UnificationEntry(plate, material),
                        'C', "craftingToolBendingCylinder");
            }
            else {
                ModHandler.addShapedRecipe(String.format("foil_%s", material.toString()), OreDictUnifier.get(foil, material, 2),
                        "hP ",
                        'P', new UnificationEntry(plate, material));
            }

            // Cluster Mill foils
            if(GAConfig.GT6.BendingFoilsAutomatic) {

                removeRecipesByInputs(BENDER_RECIPES, OreDictUnifier.get(plate, material), getIntegratedCircuit(0));

                CLUSTER_MILL_RECIPES.recipeBuilder().EUt(24).duration((int) material.getMass())
                        .input(plate, material)
                        .output(foil, material, 4)
                        .buildAndRegister();
            }
        }
    }

    /**
     * Ring Material Handler. Generates:
     *
     * + Bending Cylinder Ring Recipes if enabled
     *
     * - Removes old Handcrafting Ring Recipes if enabled
     */
    private static void processRing(OrePrefix ring, IngotMaterial material) {
        if(!material.hasFlag(NO_SMASHING)) {

            removeCraftingRecipes(OreDictUnifier.get(ring, material));

            ModHandler.addShapedRecipe(String.format("rod_to_ring_%s", material.toString()), OreDictUnifier.get(ring, material),
                    "hS", " C",
                    'S', new UnificationEntry(stick, material),
                    'C', "craftingToolBendingCylinderSmall");
        }
    }

    /**
     * Round Material Handler. Generates:
     *
     * + Round Handcrafting Recipes
     * + Round Lathe Recipes
     */
    private static void processRound(OrePrefix round, IngotMaterial material) {
        if(!material.hasFlag(NO_SMASHING) && !OreDictUnifier.get(round, material).isEmpty()) {

            ModHandler.addShapedRecipe(String.format("round_%s", material.toString()), OreDictUnifier.get(round, material),
                    "fN", "N ",
                    'N', new UnificationEntry(nugget, material));
        }

        LATHE_RECIPES.recipeBuilder().EUt(8).duration(100)
                .input(nugget, material)
                .output(round, material)
                .buildAndRegister();
    }

    /**
     * Curved Plate Material Handler. Generates:
     *
     * + Curved Plate Recipes if enabled, Handcrafting and Machine
     * + Curved Plate Rotor Recipes if enabled
     * + Curved Plate Pipe Recipes if enabled
     *
     * + Adds back old rotor recipes if Curved Plate Rotors are disabled
     */
    private static void processPlateCurved(OrePrefix plateCurved, IngotMaterial material) {

        // Register Curved Plate recipes
        ModHandler.addShapedRecipe(String.format("curved_plate_%s", material.toString()), OreDictUnifier.get(plateCurved, material),
                "h", "P", "C",
                'P', new UnificationEntry(plate, material),
                'C', "craftingToolBendingCylinder");

        ModHandler.addShapedRecipe(String.format("flatten_plate_%s", material.toString()), OreDictUnifier.get(plate, material),
                "h", "C",
                'C', new UnificationEntry(plateCurved, material));

        BENDER_RECIPES.recipeBuilder().EUt(24).duration((int) material.getMass())
                .input(plate, material)
                .circuitMeta(0)
                .output(plateCurved, material)
                .buildAndRegister();

        BENDER_RECIPES.recipeBuilder().EUt(24).duration((int) material.getMass())
                .input(plateCurved, material)
                .circuitMeta(1)
                .output(plate, material)
                .buildAndRegister();

        // Register Curved Plate Pipe Recipes
        if(GAConfig.GT6.BendingPipes) {

            if(!OreDictUnifier.get(pipeMedium, material).isEmpty() && !OreDictUnifier.get(plateCurved, material).isEmpty()) {

                removeCraftingRecipes(OreDictUnifier.get(pipeSmall, material, 4));
                removeCraftingRecipes(OreDictUnifier.get(pipeMedium, material, 2));

                ModHandler.addShapedRecipe(String.format("pipe_ga_tiny_%s", material.toString()), OreDictUnifier.get(pipeTiny, material, 8),
                        "PPP", "hCw", "PPP",
                        'P', new UnificationEntry(plateCurved, material),
                        'C', "craftingToolBendingCylinder");

                ModHandler.addShapedRecipe(String.format("pipe_ga_small_%s", material.toString()), OreDictUnifier.get(pipeSmall, material, 4),
                        "PwP", "PCP", "PhP",
                        'P', new UnificationEntry(plateCurved, material),
                        'C', "craftingToolBendingCylinder");

                ModHandler.addShapedRecipe(String.format("pipe_ga_%s", material.toString()), OreDictUnifier.get(pipeMedium, material, 2),
                        "PPP", "wCh", "PPP",
                        'P', new UnificationEntry(plateCurved, material),
                        'C', "craftingToolBendingCylinder");

                ModHandler.addShapedRecipe(String.format("pipe_ga_large_%s", material.toString()), OreDictUnifier.get(pipeLarge, material),
                        "PhP", "PCP", "PwP",
                        'P', new UnificationEntry(plateCurved, material),
                        'C', "craftingToolBendingCylinder");
            }
        }

        if(GAConfig.GT6.BendingRotors) {

            if(!OreDictUnifier.get(rotor, material).isEmpty() && !OreDictUnifier.get(plateCurved, material).isEmpty()) {
                removeCraftingRecipes(OreDictUnifier.get(rotor, material));
                ModHandler.addShapedRecipe(String.format("ga_rotor_%s", material.toString()), OreDictUnifier.get(rotor, material),
                        "ChC", "SRf", "CdC",
                        'C', OreDictUnifier.get(plateCurved, material),
                        'S', OreDictUnifier.get(screw, material),
                        'R', OreDictUnifier.get(ring, material));

                RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                        .input(plateCurved, material, 4)
                        .input(ring, material)
                        .fluidInputs(Materials.SolderingAlloy.getFluid(32))
                        .outputs(OreDictUnifier.get(rotor, material))
                        .duration(240).EUt(24).buildAndRegister();
            }
        }
        //Re-add rotor assembler recipes removed by GTCE
        else {
            if(!OreDictUnifier.get(rotor, material).isEmpty() && !OreDictUnifier.get(plate, material).isEmpty()) {
                RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                        .input(plate, material, 4)
                        .input(ring, material)
                        .fluidInputs(Materials.SolderingAlloy.getFluid(32))
                        .outputs(OreDictUnifier.get(rotor, material))
                        .duration(240).EUt(24).buildAndRegister();
            }
        }
    }

    /**
     * Double Ingot Material Handler. Generates:
     *
     * + Double Ingot Recipes if enabled
     * + Plates from Double Ingots if enabled
     */
    private static void processDoubleIngot(OrePrefix plate, IngotMaterial material) {
        //TODO, check if this removed Fiber-Reinforced Expoy Resin sheets: It will

        removeCraftingRecipes(OreDictUnifier.get(plate, material));

        ModHandler.addShapedRecipe(String.format("ingot_double_%s", material.toString()), OreDictUnifier.get(ingotDouble, material),
                "h", "I", "I",
                'I', new UnificationEntry(ingot, material));

        ModHandler.addShapedRecipe(String.format("double_ingot_to_plate_%s", material.toString()), OreDictUnifier.get(plate, material),
                "h", "I",
                'I', new UnificationEntry(ingotDouble, material));
    }

    /**
     * Adds by hand recipes for Tiny and Large Pipes, when curved plates are disabled.
     */
    private static void processPlate(OrePrefix plate, IngotMaterial material) {

        if(!OreDictUnifier.get(pipeTiny, material).isEmpty() && !(GAConfig.GT6.BendingPipes && GAConfig.GT6.addCurvedPlates)) {

            ModHandler.addShapedRecipe(String.format("pipe_ga_tiny_%s", material.toString()), OreDictUnifier.get(pipeTiny, material, 8),
                    "PfP", "P P", "PhP",
                    'P', new UnificationEntry(plate, material));

            ModHandler.addShapedRecipe(String.format("pipe_ga_large_%s", material.toString()), OreDictUnifier.get(pipeLarge, material),
                    "PhP", "P P", "PfP",
                    'P', new UnificationEntry(plate, material));
        }
    }

    private static void processWireGt(OrePrefix wireGt, IngotMaterial material) {

        //Bundler Recipes
        for(int startTier = 0; startTier < 4; startTier++) {
            final int current = startTier; // yay lambdas
            IntStream.range(1, 5 - startTier).forEach(tier -> {
                GARecipeMaps.BUNDLER_RECIPES.recipeBuilder()
                        .inputs(OreDictUnifier.get(GAEnums.WIRE_DOUBLING_ORDER.get(current), material, 1 << tier))
                        .notConsumable(new IntCircuitIngredient(tier))
                        .outputs(OreDictUnifier.get(GAEnums.WIRE_DOUBLING_ORDER.get(current + tier), material, 1))
                        .buildAndRegister();
            });
        }


        //Check done here instead of in the register method so that Bundler Recipes can also be included
        if(GAConfig.GT5U.CablesGT5U) {

            //Only Process recipes on applicable cables with voltages greater than LV
            if(material.cableProperties == null || material.cableProperties.voltage <= GTValues.V[GTValues.LV]) {
                return;
            }

            OrePrefix cablePrefix = valueOf("cable" + wireGt.name().substring(4));
            ItemStack cableStack = OreDictUnifier.get(cablePrefix, material);


            //Removing the existing Recipes
            //TODO, this is not removing the 16 -> 1 on circuit 28
            HashMap<FluidMaterial, Integer> trimmedInsulationMap = findRelevantInsulation(material.cableProperties.voltage);
            for(FluidMaterial insulationMaterial : trimmedInsulationMap.keySet()) {
                int cableTier = GTUtility.getTierByVoltage(material.cableProperties.voltage);
                int insulationTier = INSULATION_MATERIALS.get(insulationMaterial);
                if(cableTier > insulationTier) {
                    continue;
                }

                int fluidAmount = Math.max(36, 144 / (1 + (insulationTier - cableTier) / 2));


                // Try to remove 1 recipe with the Fluid type. If it fails, then exit
                boolean wasRemoved = removeRecipesByInputs(ASSEMBLER_RECIPES, new ItemStack[]{OreDictUnifier.get(wireGt, material), getIntegratedCircuit(24)}, new FluidStack[]{insulationMaterial.getFluid(fluidAmount * (int) (wireGt.materialAmount / M) * 2)});
                if(!wasRemoved) {
                    continue;
                }

                boolean temp = removeRecipesByInputs(ASSEMBLER_RECIPES,
                        new ItemStack[]{OreDictUnifier.get(wireGtSingle, material, (int) (wireGt.materialAmount / M) * 2), getIntegratedCircuit(24 + GAEnums.WIRE_DOUBLING_ORDER.indexOf(wireGt))},
                        new FluidStack[]{insulationMaterial.getFluid(fluidAmount * (int) (wireGt.materialAmount / M) * 2)});

                System.out.println(temp);
            }

            //Adding the new Recipes
            for(FluidMaterial insulationMaterial : trimmedInsulationMap.keySet()) {
                int cableTier = GTUtility.getTierByVoltage(material.cableProperties.voltage);
                int insulationTier = INSULATION_MATERIALS.get(insulationMaterial);
                if(cableTier > insulationTier) {
                    continue;
                }

                int fluidAmount = Math.max(36, 144 / (1 + (insulationTier - cableTier) / 2));

                //If the cable is IV tier or above, Add additional recipes with material foils
                if(material.cableProperties.voltage >= GTValues.V[GTValues.IV]) {

                    //TODO Check Fluid amounts against pre-refactor amounts
                    ASSEMBLER_RECIPES.recipeBuilder()
                            .input(wireGt, material)
                            .input(foil, material, (int) (wireGt.materialAmount / M) * 2)
                            .fluidInputs(insulationMaterial.getFluid((int) (fluidAmount * (wireGt.materialAmount / M) * 2)))
                            .circuitMeta(24)
                            .outputs(cableStack)
                            .duration(150).EUt(8).buildAndRegister();

                    ASSEMBLER_RECIPES.recipeBuilder()
                            .input(wireGt, material)
                            .input(foil, Materials.PolyphenyleneSulfide, (int) (wireGt.materialAmount / M) * 2)
                            .fluidInputs(insulationMaterial.getFluid((int) (fluidAmount * (wireGt.materialAmount / M) * 2)))
                            .circuitMeta(24)
                            .outputs(cableStack)
                            .duration(150).EUt(8).buildAndRegister();

                    for(MaterialStack dustStack : cableDusts) {

                        ASSEMBLER_RECIPES.recipeBuilder()
                                .input(wireGt, material)
                                .input(foil, material, (int) (wireGt.materialAmount / M) * 2)
                                .input(dustSmall, dustStack.material, (int) (wireGt.materialAmount / M) * 2)
                                .fluidInputs(insulationMaterial.getFluid((int) (fluidAmount * wireGt.materialAmount / M)))
                                .outputs(cableStack)
                                .duration(150).EUt(8).buildAndRegister();

                        ASSEMBLER_RECIPES.recipeBuilder()
                                .input(wireGt, material)
                                .input(dustSmall, dustStack.material, (int) (wireGt.materialAmount / M) * 2)
                                .input(foil, Materials.PolyphenyleneSulfide, (int) (wireGt.materialAmount / M) * 2)
                                .fluidInputs(insulationMaterial.getFluid((int) (fluidAmount * wireGt.materialAmount / M)))
                                .outputs(cableStack)
                                .duration(150).EUt(8).buildAndRegister();

                    }
                }
                //MV to EV cable, no material foil recipes
                else {

                    ASSEMBLER_RECIPES.recipeBuilder()
                            .input(wireGt, material)
                            .fluidInputs(insulationMaterial.getFluid((int) (fluidAmount * (wireGt.materialAmount / M) * 2)))
                            .circuitMeta(24)
                            .outputs(cableStack)
                            .duration(150).EUt(8).buildAndRegister();

                    for(MaterialStack dustStack : cableDusts) {

                        ASSEMBLER_RECIPES.recipeBuilder()
                                .input(wireGt, material)
                                .input(dustSmall, dustStack.material, (int) (wireGt.materialAmount / M) * 2)
                                .fluidInputs(insulationMaterial.getFluid((int) (fluidAmount * wireGt.materialAmount / M)))
                                .outputs(cableStack)
                                .duration(150).EUt(8).buildAndRegister();
                    }
                }
            }
        }
    }

    private static HashMap<FluidMaterial, Integer> findRelevantInsulation(long voltage) {

        HashMap<FluidMaterial, Integer> modifiedInsulationMap = new HashMap<>();

        if(voltage >= GTValues.V[GTValues.ZPM]) {
            modifiedInsulationMap.put(Materials.SiliconeRubber, 4);
        }
        else if(voltage >= GTValues.V[GTValues.EV]) {
            modifiedInsulationMap.put(Materials.StyreneButadieneRubber, 3);
            modifiedInsulationMap.put(Materials.SiliconeRubber, 4);
        }
        else {
            modifiedInsulationMap.put(Materials.Rubber, 1);
            modifiedInsulationMap.put(Materials.StyreneButadieneRubber, 3);
            modifiedInsulationMap.put(Materials.SiliconeRubber, 4);
        }

        return modifiedInsulationMap;

    }


    private static void processGem(OrePrefix gem, GemMaterial material) {

        //Adds mirrored recipes for the hammer tool. Why only using gems I have no idea. TODO, is this needed?
        if (!OreDictUnifier.get(toolHeadHammer, material).isEmpty()) {
            ModHandler.addMirroredShapedRecipe(String.format("gem_hammer_%s", material.toString()), MetaItems.HARD_HAMMER.getStackForm(material, 1),
                    "GG ", "GGS", "GG ",
                    'G', new UnificationEntry(gem, material), 'S', new UnificationEntry(stick, Materials.Wood));
        }

        //Replace Hammers in gem recipes with files
        //TODO is this really needed? All it does is remove the hammer from gem tool recipes
        if (GAConfig.Misc.gemToolsNeedFiles && !OreDictUnifier.get(toolHeadHammer, material).isEmpty()) {
            removeCraftingRecipes(OreDictUnifier.get(toolHeadAxe, material));
            ModHandler.addShapedRecipe(String.format("axe_head_%s", material.toString()), OreDictUnifier.get(toolHeadAxe, material),
                    "GG", "Gf", 'G', new UnificationEntry(gem, material));

            removeCraftingRecipes(OreDictUnifier.get(toolHeadFile, material));
            ModHandler.addShapedRecipe(String.format("file_head_%s", material.toString()), OreDictUnifier.get(toolHeadFile, material),
                    "G", "G", "f", 'G', new UnificationEntry(gem, material));

            removeCraftingRecipes(OreDictUnifier.get(toolHeadHammer, material));
            ModHandler.addShapedRecipe(String.format("hammer_head_%s", material.toString()), OreDictUnifier.get(toolHeadHammer, material),
                    "GG ", "GGf", "GG ", 'G', new UnificationEntry(gem, material));

            removeCraftingRecipes(OreDictUnifier.get(toolHeadHoe, material));
            ModHandler.addShapedRecipe(String.format("hoe_head_%s", material.toString()), OreDictUnifier.get(toolHeadHoe, material),
                    "GGf", 'G', new UnificationEntry(gem, material));

            removeCraftingRecipes(OreDictUnifier.get(toolHeadPickaxe, material));
            ModHandler.addShapedRecipe(String.format("pickaxe_head_%s", material.toString()), OreDictUnifier.get(toolHeadPickaxe, material),
                    "GGG", "f  ", 'G', new UnificationEntry(gem, material));

            removeCraftingRecipes(OreDictUnifier.get(toolHeadSaw, material));
            ModHandler.addShapedRecipe(String.format("saw_head_%s", material.toString()), OreDictUnifier.get(toolHeadSaw, material),
                    "GG", "f ", 'G', new UnificationEntry(gem, material));

            removeCraftingRecipes(OreDictUnifier.get(toolHeadSense, material));
            ModHandler.addShapedRecipe(String.format("sense_head_%s", material.toString()), OreDictUnifier.get(toolHeadSense, material),
                    "GGG", " f ", 'G', new UnificationEntry(gem, material));

            removeCraftingRecipes(OreDictUnifier.get(toolHeadShovel, material));
            ModHandler.addShapedRecipe(String.format("shovel_head_%s", material.toString()), OreDictUnifier.get(toolHeadShovel, material),
                    "fG", 'G', new UnificationEntry(gem, material));

            removeCraftingRecipes(OreDictUnifier.get(toolHeadSword, material));
            ModHandler.addShapedRecipe(String.format("sword_head_%s", material.toString()), OreDictUnifier.get(toolHeadSword, material),
                    " G", "fG", 'G', new UnificationEntry(gem, material));

            removeCraftingRecipes(OreDictUnifier.get(toolHeadUniversalSpade, material));
            ModHandler.addShapedRecipe(String.format("universal_spade_head_%s", material.toString()), OreDictUnifier.get(toolHeadUniversalSpade, material),
                    "GGG", "GfG", " G ", 'G', new UnificationEntry(gem, material));
            }

    }
}

