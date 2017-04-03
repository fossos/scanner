package ovh.corail.scanner.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import ovh.corail.scanner.handler.CommandHandler;
import ovh.corail.scanner.handler.EventHandler;
import ovh.corail.scanner.item.ItemBattery;
import ovh.corail.scanner.item.ItemScanner;

@Mod(modid = Main.MOD_ID, name = Main.MOD_NAME, version = Main.MOD_VER, guiFactory = "ovh.corail." + Main.MOD_ID + ".gui.GuiFactory")
public class Main {
	public static final String MOD_ID = "scanner";
	public static final String MOD_NAME = "Corail Scanner";
	public static final String MOD_VER = "1.0.0";
	
	@Instance(Main.MOD_ID)
	public static Main instance;
	
	@SidedProxy(clientSide = "ovh.corail."+ MOD_ID +".core.ClientProxy", serverSide = "ovh.corail." + MOD_ID + ".core.CommonProxy")
	public static CommonProxy proxy;
	public static CreativeTabs tabScanner = new CreativeTabs(Main.MOD_ID) {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Main.scanner, 1);
		}

		@Override
		public String getTranslatedTabLabel() {
			return Main.MOD_NAME;
		}
	};
	public static ItemScanner scanner = new ItemScanner();
	public static ItemBattery battery = new ItemBattery();
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	
	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandHandler());
	}
}
