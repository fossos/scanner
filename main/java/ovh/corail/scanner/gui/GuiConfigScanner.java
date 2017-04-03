package ovh.corail.scanner.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import ovh.corail.scanner.core.Main;
import ovh.corail.scanner.handler.ConfigurationHandler;

public class GuiConfigScanner extends GuiConfig {
	public GuiConfigScanner(GuiScreen parentScreen) {
		super(parentScreen, new ConfigElement(ConfigurationHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Main.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
	}
}
