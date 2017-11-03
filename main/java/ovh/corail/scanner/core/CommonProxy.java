package ovh.corail.scanner.core;

import java.io.File;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import ovh.corail.scanner.handler.ConfigurationHandler;
import ovh.corail.scanner.handler.PacketHandler;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		/** config */
		ConfigurationHandler.loadConfig(new File(event.getModConfigurationDirectory(), ModProps.MOD_ID));
		/** register items and blocks */
		Helper.register();
		/** custom recipes */
		/** recharge battery */
		RecipeSorter.register("scanner:recharge_scanner", RechargeScannerRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
		ForgeRegistries.RECIPES.register(new RechargeScannerRecipe(new ItemStack(Main.scanner, 1, 0), new Object[] {
			new ItemStack(Main.scanner, 1, OreDictionary.WILDCARD_VALUE), Main.battery
		}).setRegistryName(new ResourceLocation(ModProps.MOD_ID, "recharge_scanner")));
		/** packet handler */
		PacketHandler.init();
		/** init ScannerManager */
		ScannerManager.getInstance().init();
	}
	
	public void init(FMLInitializationEvent event) {
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public Side getSide() {
		return Side.SERVER;
	}

}
