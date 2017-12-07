package ovh.corail.scanner.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import ovh.corail.scanner.core.Helper;
import ovh.corail.scanner.core.ModProps;

public class ConfigurationHandler {
	public static Configuration config;
	private static File configDir;
	public static int batteryEnergy, scanRange;
	public static boolean highlightBlocks, enableScannerSound;
	
	private ConfigurationHandler() {
		
	}
	
	public static void refreshConfig() {
		batteryEnergy=config.getInt("batteryEnergy", config.CATEGORY_GENERAL, 5000, 1000, 50000, Helper.getTranslation("config.batteryEnergy"));
		scanRange=config.getInt("scanRange", config.CATEGORY_GENERAL, 10, 1, 30, Helper.getTranslation("config.scanRange"));
		highlightBlocks = config.getBoolean("highlightBlocks", config.CATEGORY_CLIENT, false, Helper.getTranslation("config.highlightBlocks"));
		enableScannerSound = config.getBoolean("enableScannerSound", config.CATEGORY_CLIENT, true, Helper.getTranslation("config.enableScannerSound"));
		if (config.hasChanged()) {
			config.save();
		}
	}

	public static void loadConfig(File configDir) {
		ConfigurationHandler.configDir= configDir;
		if (!configDir.exists()) {
			configDir.mkdir();
		}
		config = new Configuration(new File(configDir, ModProps.MOD_ID + ".cfg"), ModProps.MOD_VER);
		config.load();
		ConfigurationHandler.refreshConfig();
	}

	public static File getConfigDir() {
		return configDir;
	}
	
	public static void updateClient(int scanRange, int batteryEnergy) {
		ConfigurationHandler.scanRange = scanRange;
		ConfigurationHandler.batteryEnergy= batteryEnergy;
	}
	
}
