package ovh.corail.scanner.core;

import java.io.File;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import ovh.corail.scanner.handler.AchievementHandler;
import ovh.corail.scanner.handler.ConfigurationHandler;
import ovh.corail.scanner.handler.PacketHandler;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		/** config */
		ConfigurationHandler.loadConfig(new File(event.getModConfigurationDirectory(), ModProps.MOD_ID));
		/** register items and blocks */
		Helper.register();
		/** new recipes */
		/** battery */
		GameRegistry.addRecipe(new ShapedOreRecipe(Main.battery, new Object[] { "1", "0", "1", 
				Character.valueOf('0'),	"nuggetGold", 
				Character.valueOf('1'),	Items.REDSTONE, 
		}));
		/** scanner */
		GameRegistry.addRecipe(new ShapedOreRecipe(Main.scanner, new Object[] { "2 2", "010", "000", 
				Character.valueOf('0'),	"ingotIron", 
				Character.valueOf('1'),	Main.battery, 
				Character.valueOf('2'),	Blocks.REDSTONE_TORCH, 
		}));
		/** recharge battery */
		RecipeSorter.register("scanner:recharge", RechargeScannerRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
		GameRegistry.addRecipe(new RechargeScannerRecipe(new ItemStack(Main.scanner, 1, 0), new Object[] {
			new ItemStack(Main.scanner, 1, OreDictionary.WILDCARD_VALUE), Main.battery
		}));
		/** packet handler */
		PacketHandler.init();
		/** init ScannerManager */
		ScannerManager.getInstance().init();
	}
	
	public void init(FMLInitializationEvent event) {
		/** achievements */
		AchievementHandler.registerAchievements();
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public Side getSide() {
		return Side.SERVER;
	}

}
