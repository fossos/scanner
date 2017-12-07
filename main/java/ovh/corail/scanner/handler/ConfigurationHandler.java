package ovh.corail.scanner.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import ovh.corail.scanner.core.Helper;
import ovh.corail.scanner.core.ModProps;

public class ConfigurationHandler {
	public static Configuration config;
	private static File configDir;
	public static int batteryEnergy, scanRange;
	public static boolean highlightBlocks, enableScannerSound;
	public static String favoriteSound;
	
	private ConfigurationHandler() {
		
	}
	
	public static void refreshConfig() {
		batteryEnergy=config.getInt("batteryEnergy", config.CATEGORY_GENERAL, 10000, 1000, 100000, Helper.getTranslation("config.batteryEnergy"));
		scanRange=config.getInt("scanRange", config.CATEGORY_GENERAL, 15, 1, 50, Helper.getTranslation("config.scanRange"));
		highlightBlocks = config.getBoolean("highlightBlocks", config.CATEGORY_CLIENT, false, Helper.getTranslation("config.highlightBlocks"));
		enableScannerSound = config.getBoolean("enableScannerSound", config.CATEGORY_CLIENT, true, Helper.getTranslation("config.enableScannerSound"));
		List<SoundEvent> soundList = ForgeRegistries.SOUND_EVENTS.getValues();
		ArrayList<String> mcSoundList = Lists.newArrayList();
		for (SoundEvent sound : soundList) {
			if (sound.getRegistryName().toString().contains("minecraft:block.note.")) {
				mcSoundList.add(sound.getRegistryName().getResourcePath().toString());
			}
		}
		favoriteSound = config.getString("favoriteSound", Configuration.CATEGORY_CLIENT, SoundEvents.BLOCK_NOTE_HAT.getRegistryName().getResourcePath(), Helper.getTranslation("config.favoriteSound"), mcSoundList.toArray(new String[mcSoundList.size()]));
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
