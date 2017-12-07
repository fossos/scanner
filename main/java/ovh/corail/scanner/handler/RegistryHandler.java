package ovh.corail.scanner.handler;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ovh.corail.scanner.core.Helper;
import ovh.corail.scanner.core.Main;

@Mod.EventBusSubscriber
public class RegistryHandler {
	private static Item[] items = {
		Main.scanner,
		Main.battery
	};
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(items);
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void renderItems(ModelRegistryEvent event) {
		for (Item item : items) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		Helper.registerEncodedRecipes();
	}
}
