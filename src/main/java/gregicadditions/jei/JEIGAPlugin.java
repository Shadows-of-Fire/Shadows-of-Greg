package gregicadditions.jei;

import gregtech.integration.jei.multiblock.MultiblockInfoCategory;
import gregtech.integration.jei.multiblock.MultiblockInfoRecipeWrapper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

import static gregicadditions.item.GAMultiblockCasing.CasingType.*;

@JEIPlugin
public class JEIGAPlugin implements IModPlugin {

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		injectMultiblocks();
	}

	private void injectMultiblocks() {
		var map = MultiblockInfoCategory.multiblockRecipes;
		map.put("assembly_line", new MultiblockInfoRecipeWrapper(new AssemblyLineInfo()));
		map.put("fusion_reactor_1", new MultiblockInfoRecipeWrapper(new FusionReactor1Info()));
		map.put("fusion_reactor_2", new MultiblockInfoRecipeWrapper(new FusionReactor2Info()));
		map.put("fusion_reactor_3", new MultiblockInfoRecipeWrapper(new FusionReactor3Info()));
		map.put("processing_array", new MultiblockInfoRecipeWrapper(new ProcessingArrayInfo()));
	}

	@Override
	public void register(IModRegistry registry) {
		// Workaround - hide the Coke Oven blocks until we can remove them without ID shift
		registry.getJeiHelpers()
		        .getIngredientBlacklist()
		        .addIngredientToBlacklist(COKE_OVEN_BRICKS.getStack());
	}
}
