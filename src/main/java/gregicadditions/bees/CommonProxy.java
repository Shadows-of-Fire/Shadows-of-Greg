package gregicadditions.bees;

import gregicadditions.GregicAdditions;
import gregicadditions.Tags;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Tags.MODID)
public class CommonProxy {

	public void postInit() {
		ForestryMachineRecipes.init();
	}
}