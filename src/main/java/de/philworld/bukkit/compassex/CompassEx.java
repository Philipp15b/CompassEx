package de.philworld.bukkit.compassex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompassEx extends JavaPlugin {

	Logger log = Logger.getLogger("Minecraft");
	FileConfiguration config;

	@SuppressWarnings("serial")
	static Map<String, String> helpMessages = new HashMap<String, String>() {{
		put("compassex.reset", ChatColor.RED + "/compass reset"
				+ ChatColor.BLUE + " Resets your compass to spawn.");
		put("compassex.player", ChatColor.RED + "/compass PLAYERNAME"
				+ ChatColor.BLUE + " Points your compass to a player");
		put("compassex.pos", ChatColor.RED + "/compass X Y Z"
				+ ChatColor.BLUE + " Points your compass to coordinates");
		put("compassex.live", ChatColor.RED + "/compass live PLAYERNAME"
				+ ChatColor.BLUE
				+ " Points your compass to a player and updates it.");
		put("compassex.deathpoint", ChatColor.RED + "/compass dp"
				+ ChatColor.BLUE
				+ " Points your compass to your latest death point.");
	}
	};

	// save all hidden players in a list
	List<String> hiddenPlayers = new ArrayList<String>();

	HashMap<String, Location> deathPoints = new HashMap<String, Location>();


	@Override
	public void onEnable() {
		loadConfiguration();

		PluginManager pm = getServer().getPluginManager();

		// set up listener
		pm.registerEvents(new CompassExListener(this), this);

		// setup compass tracker
		CompassTrackerUpdater.setPlugin(this);
		CompassTrackerUpdater.setUpdateRate(getConfig().getInt("live-update-rate"));

		// set command executor
		getCommand("compass").setExecutor(new CompassExCommandExecutor(this));

		// done.
		PluginDescriptionFile pff = this.getDescription();
		log.info(pff.getName() +  " " + pff.getVersion() + " is enabled.");
	}

	@Override
	public void onDisable() {
		CompassTrackerUpdater.stop(); // stop tasks

		PluginDescriptionFile pff = this.getDescription();
		log.info(pff.getName() +  " " + pff.getVersion() + " is disabled.");
	}

	/**
	 * Loads the configuration and inserts the defaults.
	 */
	public void loadConfiguration() {
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
	}

}
