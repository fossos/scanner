package ovh.corail.scanner.handler;

import net.minecraftforge.common.config.Configuration;
import ovh.corail.scanner.core.Helper;

public class ConfigurationHandler {
	public static Configuration config;
	public static int damageAmount, timeForDamage;
	
	private ConfigurationHandler() {
		
	}
	
	public static void refreshConfig() {
		damageAmount=config.getInt("damageAmount", config.CATEGORY_GENERAL, 5, 0, 5000, Helper.getTranslation("config.damageAmount"));
		timeForDamage=config.getInt("timeForDamage", config.CATEGORY_GENERAL, 1000, 100, 60000, Helper.getTranslation("config.timeForDamage"));
		if (config.hasChanged()) {
			config.save();
		}
	}
	
}
