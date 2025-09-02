package gregicadditions.jei;

import gregtech.integration.jei.multiblock.MultiblockInfoPage;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

import static gregicadditions.item.GAMultiblockCasing.CasingType.*;

@JEIPlugin
public class JEIGAPlugin implements IModPlugin {

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new GAMultiblockInfoCategory(registry.getJeiHelpers()));
	}

	@Override
	public void register(IModRegistry registry) {
		GAMultiblockInfoCategory.registerRecipes(registry);
		// Workaround - hide the Coke Oven blocks until we can remove them without ID shift
		registry.getJeiHelpers()
		        .getIngredientBlacklist()
		        .addIngredientToBlacklist(COKE_OVEN_BRICKS.getStack());


		//Multiblock info page registration
		GAMultiblockInfoCategory.multiblockRecipes.values().forEach(v -> {
			MultiblockInfoPage infoPage = v.getInfoPage();
			registry.addIngredientInfo(infoPage.getController().getStackForm(),
					VanillaTypes.ITEM,
					infoPage.getDescription());
		});

	}
}
