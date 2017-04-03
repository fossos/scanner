package ovh.corail.scanner.core;

import java.io.File;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.scanner.handler.ConfigurationHandler;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		/** config */
		ConfigurationHandler.config = new Configuration(event.getSuggestedConfigurationFile(), Main.MOD_VER);
		ConfigurationHandler.config.load();
		ConfigurationHandler.refreshConfig();
		/** register items and blocks */
		Helper.register();
		/** new recipes */
		/** TODO oreDict for nugget */
		GameRegistry.addRecipe(new ItemStack(Main.scanner, 1), new Object[] { "1 1", "010", "000", 
				Character.valueOf('0'),	new ItemStack(Items.field_191525_da, 1, 0), 
				Character.valueOf('1'),	new ItemStack(Items.REDSTONE, 1), 
		});
	}
	
	public void init(FMLInitializationEvent event) {
		
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}

}
