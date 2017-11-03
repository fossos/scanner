package ovh.corail.scanner.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import ovh.corail.scanner.core.Helper;
import ovh.corail.scanner.core.ModProps;

public class ConfigurationHandler {
	public static Configuration config;
	private static File configDir;
	public static int damageAmount, tickForDamage, scanRange, scanRadius;
	
	private ConfigurationHandler() {
		
	}
	
	public static void refreshConfig() {
		damageAmount=config.getInt("damageAmount", config.CATEGORY_GENERAL, 5, 0, 5000, Helper.getTranslation("config.damageAmount"));
		tickForDamage=config.getInt("tickForDamage", config.CATEGORY_GENERAL, 1000, 100, 60000, Helper.getTranslation("config.tickForDamage"));
		scanRange=config.getInt("scanRange", config.CATEGORY_GENERAL, 10, 1, 1000, Helper.getTranslation("config.scanRange"));
		scanRadius=config.getInt("scanRadius", config.CATEGORY_GENERAL, 3, 1, 9, Helper.getTranslation("config.scanRadius"));
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
	
}
