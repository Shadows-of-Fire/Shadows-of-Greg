package gregicadditions.recipes.helpers;

import gregicadditions.GregicAdditions;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.machines.FuelRecipeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains various helper methods used to see if a recipe modification is working correctly.
 * Methods in this class namely wrap GTCE methods to provide logging for recipe modification failure or success.
 */
public class HelperMethods {

    public static void removeRecipeByName(String recipeName) {
        ResourceLocation recipe = new ResourceLocation(recipeName);

        if (ForgeRegistries.RECIPES.containsKey(recipe)) {
            GregicAdditions.LOGGER.info("Successfully Removed Recipe with Name: {}", recipeName);
        } else {
            GregicAdditions.LOGGER.warn("Failed to Remove Recipe with Name: {}", recipeName);
        }

        ModHandler.removeRecipeByName(new ResourceLocation(recipeName));
    }

    /**
     * Wrapper method for removing Crafting Table Recipes.
     * Wrapped to provide logging on if the removal failed or succeeds.
     *
     * @param output The ItemStack output of the Crafting Table recipe
     */
    public static void removeCraftingRecipes(ItemStack output) {
        int removedRecipes = ModHandler.removeRecipes(output);

        if (removedRecipes != 0) {
            GregicAdditions.LOGGER.info("Successfully Removed {} Recipe(s) with Output: {}", removedRecipes, output.getDisplayName());
        } else {
            GregicAdditions.LOGGER.warn("Failed to Remove Crafting Recipe with Output: {}", output.getDisplayName());
        }
    }

    /**
     * Wrapper method for removing Furnace Smelting Recipes.
     * Wrapped for easy logging info.
     *
     * @param input The ItemStack input of the Furnace Recipe to remove.
     */
    public static void removeFurnaceRecipe(ItemStack input) {
        if (ModHandler.removeFurnaceSmelting(input)) {
            GregicAdditions.LOGGER.info("Removed Smelting Recipe for Input: {}", input.getDisplayName());
        }
        else {
            GregicAdditions.LOGGER.warn("Failed to Remove Smelting Recipe for Input: {}", input.getDisplayName());
        }
    }

    public static <R extends RecipeBuilder<R>> void removeRecipesByInputs(RecipeMap<R> map, ItemStack... itemInputs) {
        List<ItemStack> inputs = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (ItemStack s : itemInputs) {
            inputs.add(s);
            names.add(s.getDisplayName() + " x " + s.getCount());
        }

        if(map.removeRecipe(map.findRecipe(Long.MAX_VALUE, inputs, Collections.emptyList(), Integer.MAX_VALUE))) {
            GregicAdditions.LOGGER.info("Removed Recipe for Item Input(s): " + names);
        }
        else {
            GregicAdditions.LOGGER.warn("Failed to Remove Recipe for Item Input(s): " + names);
        }
    }

    public static <R extends RecipeBuilder<R>> void removeRecipesByInputs(RecipeMap<R> map, FluidStack... fluidInputs) {
        List<FluidStack> inputs = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (FluidStack s : fluidInputs) {
            inputs.add(s);
            names.add(s.getFluid().getName() + " x " + s.amount);
        }

        if(map.removeRecipe(map.findRecipe(Long.MAX_VALUE, Collections.emptyList(), inputs, Integer.MAX_VALUE))) {
            GregicAdditions.LOGGER.info("Removed Recipe for Fluid Input(s): " + names);
        }
        else {
            GregicAdditions.LOGGER.warn("Failed to Remove Recipe for Fluid Input(s): " + names);
        }
    }

    public static <R extends RecipeBuilder<R>> void removeRecipesByInputs(RecipeMap<R> map, ItemStack[] itemInputs, FluidStack[] fluidInputs) {
        List<ItemStack> itemIn = new ArrayList<>();
        List<String> fluidNames = new ArrayList<>();
        List<String> itemNames = new ArrayList<>();
        for (ItemStack s : itemInputs) {
            itemIn.add(s);
            itemNames.add(s.getDisplayName() + " x " + s.getCount());
        }

        List<FluidStack> fluidIn = new ArrayList<>();
        for (FluidStack s : fluidInputs) {
            fluidIn.add(s);
            fluidNames.add(s.getFluid().getName() + " x " + s.amount);
        }

        if(map.removeRecipe(map.findRecipe(Long.MAX_VALUE, itemIn, fluidIn, Integer.MAX_VALUE))) {
            GregicAdditions.LOGGER.info("Removed Recipe for inputs: Items: " + itemNames + " Fluids: " + fluidNames);
        }
        else {
            GregicAdditions.LOGGER.warn("Failed to Remove Recipe for inputs: Items: " + itemNames + " Fluids: " + fluidNames);
        }
    }

    /**
     * Removes all recipes from the provided Recipe Map
     *
     * @param map The Recipe Map for which all recipes should be removed
     *
     */
    public static <R extends RecipeBuilder<R>> void removeAllRecipes(RecipeMap<R> map) {

        List<Recipe> recipes = new ArrayList<>(map.getRecipeList());

        for (Recipe r : recipes) {
            map.removeRecipe(r);
        }

        GregicAdditions.LOGGER.debug("Removed all recipes for Recipe Map {}", map.getUnlocalizedName());
    }

    public static void removeFuelRecipe(FuelRecipeMap map, FluidStack fluidStack) {
        if(map.removeRecipe(map.findRecipe(Integer.MAX_VALUE, fluidStack))) {
            GregicAdditions.LOGGER.debug("Removed Generator Recipe for {} for Fluid: {}", map.getUnlocalizedName(), fluidStack.getLocalizedName());
        }
        else {
            GregicAdditions.LOGGER.warn("Failed to remove Generator Recipe for {} for Fluid: {}", map.getUnlocalizedName(), fluidStack.getLocalizedName());
        }
    }
}

