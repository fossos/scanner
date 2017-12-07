package ovh.corail.scanner.core;

import static ovh.corail.scanner.core.ModProps.MC_ACCEPT;
import static ovh.corail.scanner.core.ModProps.MOD_ID;
import static ovh.corail.scanner.core.ModProps.MOD_NAME;
import static ovh.corail.scanner.core.ModProps.MOD_UPDATE;
import static ovh.corail.scanner.core.ModProps.MOD_VER;
import static ovh.corail.scanner.core.ModProps.ROOT;

import java.io.File;

import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ovh.corail.scanner.handler.ConfigurationHandler;
import ovh.corail.scanner.handler.EventHandler;
import ovh.corail.scanner.handler.PacketHandler;
import ovh.corail.scanner.item.ItemBattery;
import ovh.corail.scanner.item.ItemScanner;

@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VER, acceptedMinecraftVersions = MC_ACCEPT, updateJSON = MOD_UPDATE, guiFactory = ROOT + ".gui.GuiFactory")
public class Main {
	
	@Instance(MOD_ID)
	public static Main instance;
	
	@SidedProxy(clientSide = ROOT +".core.ClientProxy", serverSide = ROOT + ".core.CommonProxy")
	public static CommonProxy proxy;
	public static Logger logger;
	public static CreativeTabs tabScanner = new CreativeTabs(MOD_ID) {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Main.scanner);
		}

		@Override
		public String getTranslatedTabLabel() {
			return MOD_NAME;
		}
	};
	public static ItemScanner scanner = new ItemScanner();
	public static ItemBattery battery = new ItemBattery();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		PacketHandler.init();
		/** config */
		ConfigurationHandler.loadConfig(new File(event.getModConfigurationDirectory(), ModProps.MOD_ID));
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
}
