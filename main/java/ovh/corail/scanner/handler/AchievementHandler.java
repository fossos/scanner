package ovh.corail.scanner.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import ovh.corail.scanner.core.Main;

public class AchievementHandler {
	private static Map<String, Achievement> achievements = new HashMap<String, Achievement>();
	private static int achievementCount = 0;
	
	public static void initAchievements() {
		addAchievement("buildBattery", 0, 0, Main.battery, null);
		addAchievement("buildScanner", 1, 1, Main.scanner, "buildBattery");
	}
	
	private static void addAchievement(String name, int col, int row, Item icon, String parent) {
		String upperName = name.substring(0,1).toUpperCase()+name.substring(1);
		String lowerName = name.substring(0,1).toLowerCase()+name.substring(1);
		achievements.put(lowerName, new Achievement("achievement."+upperName, upperName, row, col, icon, achievements.get(parent)));
		achievementCount++;
	}
	
	public static Achievement getAchievement(String name) {
		return achievements.get(name);
	}
	
	public static void registerAchievements() {
		Achievement[] pageList = new Achievement[achievementCount];
		int i = 0;
		for (Entry<String, Achievement> entry : achievements.entrySet()) {
		    Achievement achievement = entry.getValue();
		    achievement.registerStat();
		    pageList[i++] = achievement;
		}
		AchievementPage.registerAchievementPage(new AchievementPage(Main.MOD_ID, pageList));
	}
}
