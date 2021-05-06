package gregicadditions.recipes.helpers;

import gregicadditions.GregicAdditions;
import gregtech.api.recipes.ModHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * This class contains various helper methods used to see if a recipe modification is working correctly.
 * Methods in this class namely wrap GTCE methods to provide logging for recipe modification failure or success
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
     * @param input The input of the Furnace Recipe to remove.
     */
    public static void removeFurnaceRecipe(ItemStack input) {
        if (ModHandler.removeFurnaceSmelting(input)) {
            GregicAdditions.LOGGER.info("Removed Smelting Recipe for Input: {}", input.getDisplayName());
        }
        else {
            GregicAdditions.LOGGER.warn("Failed to Remove Smelting Recipe for Input: {}", input.getDisplayName());
        }
    }
}

