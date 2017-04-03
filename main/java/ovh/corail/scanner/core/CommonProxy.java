package ovh.corail.scanner.core;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import ovh.corail.scanner.handler.ConfigurationHandler;
import ovh.corail.scanner.handler.PacketHandler;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		/** config */
		ConfigurationHandler.config = new Configuration(event.getSuggestedConfigurationFile(), Main.MOD_VER);
		ConfigurationHandler.config.load();
		ConfigurationHandler.refreshConfig();
		/** register items and blocks */
		Helper.register();
		/** new recipes */
		/** battery */
		GameRegistry.addRecipe(new ShapedOreRecipe(Main.battery, new Object[] { "1", "0", "1", 
				Character.valueOf('0'),	"nuggetIron", 
				Character.valueOf('1'),	Items.REDSTONE, 
		}));
		/** scanner */
		GameRegistry.addRecipe(new ShapedOreRecipe(Main.scanner, new Object[] { "2 2", "010", "000", 
				Character.valueOf('0'),	"ingotIron", 
				Character.valueOf('1'),	Main.battery, 
				Character.valueOf('2'),	Blocks.REDSTONE_TORCH, 
		}));
		/** recharge battery */
		class RechargeScannerRecipe extends ShapelessOreRecipe {
			public RechargeScannerRecipe(ItemStack res, Object... recipe) {
				super(res, recipe);
			}

			@Override
			public ItemStack getCraftingResult(InventoryCrafting inv) {
				ItemStack scanner = ItemStack.EMPTY;
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					if (!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getItem() == Main.scanner && inv.getStackInSlot(i).getItemDamage() > 0) {
						scanner = inv.getStackInSlot(i);
						break;
					}
				}
				if (scanner.isEmpty())
					return ItemStack.EMPTY;
				ItemStack res = scanner.copy();
				res.setItemDamage(0);
				return res;
			}
		}
		RecipeSorter.register("scanner:recharge", RechargeScannerRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
		GameRegistry.addRecipe(new RechargeScannerRecipe(new ItemStack(Main.scanner, 1, 0), new Object[] {
			new ItemStack(Main.scanner, 1, OreDictionary.WILDCARD_VALUE), Main.battery
		}));
		/** packet handler */
		PacketHandler.init();
	}
	
	public void init(FMLInitializationEvent event) {
		
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public Side getSide() {
		return Side.SERVER;
	}

}
