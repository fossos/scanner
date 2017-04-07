package ovh.corail.scanner.core;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.reflect.TypeToken;

import ovh.corail.scanner.handler.ConfigurationHandler;

public class ScannerManager {
	private static final ScannerManager instance = new ScannerManager();
	private File blacklistFile = new File(ConfigurationHandler.getConfigDir(), "blacklist_blocks.json");
	public Set<String> blacklist = new HashSet<String>();
	
	private ScannerManager() {
		
	}
	
	public static ScannerManager getInstance() {
		return instance;
	}
	
	public void init() {
		loadBlacklist();
	}

	private void loadBlacklist() {
		if (!blacklistFile.exists()) {
			/** default list */
			blacklist.add("minecraft:grass:0");
			blacklist.add("minecraft:dirt:0");
			blacklist.add("minecraft:dirt:1");
			blacklist.add("minecraft:dirt:2");
			Helper.saveAsJson(blacklistFile, blacklist);
		} else {
			Type token = new TypeToken<Set<String>>() {}.getType();
			blacklist = (Set<String>) Helper.loadAsJson(blacklistFile, token);
		}
	}
	
	public boolean canSelectBlock(String stringBloc) {
		for (String currentString : blacklist) {
			if (currentString.equals(stringBloc)) { return false; }
		}
		return true;
	}
		
}
