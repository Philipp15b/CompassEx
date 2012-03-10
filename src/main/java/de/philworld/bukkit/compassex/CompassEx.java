package de.philworld.bukkit.compassex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompassEx extends JavaPlugin {

	Logger log = Logger.getLogger("Minecraft");
	FileConfiguration config;
	LocationsYaml locations;

	@SuppressWarnings("serial")
	static final Map<String, String> helpMessages = new HashMap<String, String>() {
		{
			put("compassex.reset",
					"&red;/&command; reset&blue; Reset back to spawn");

			put("compassex.here",
					"&red;/&command; here&blue; Set to your current position");

			put("compassex.direction",
					"&red;/&command; north/east/south/west&blue; Set to a direction.");

			put("compassex.player",
					"&red;/&command; PLAYERNAME&blue; Set to a player");

			put("compassex.live",
					"&red;/&command; live PLAYERNAME&blue; Set to a player's pos & update");

			put("compassex.bed",
					"&red;/&command; bed&blue; Set to your bed");

			put("compassex.pos",
					"&red;/&command; X Y Z&blue; Set to coordinates");

			put("compassex.height",
					"&red;/&command; height&blue; Height diff between you and the target");

			put("compassex.deathpoint",
					"&red;/&command; dp&blue; Set to your latest death point");

			put("compassex.hide",
					"&red;/&command; hide&blue; Hide from being tracked");

			put("compassex.hide%",
					"&red;/&command; hidden&blue; Are you hidden?");
			// % will be removed. this is so that 2 help entries can exist with
			// the same permission.

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
		CompassTrackerUpdater.setUpdateRate(getConfig().getInt(
				"live-update-rate"));
		
		locations = new LocationsYaml(this);
		locations.reload();

		// set command executor
		getCommand("compass").setExecutor(new CompassExCommandExecutor(this));

		// done.
		PluginDescriptionFile pff = this.getDescription();
		log.info(pff.getName() + " " + pff.getVersion() + " is enabled.");
	}

	@Override
	public void onDisable() {
		CompassTrackerUpdater.stop(); // stop tasks

		PluginDescriptionFile pff = this.getDescription();
		log.info(pff.getName() + " " + pff.getVersion() + " is disabled.");
	}

	/**
	 * Loads the configuration and inserts the defaults.
	 */
	public void loadConfiguration() {
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
	}

	public LocationsYaml getSavedLocations() {
		return locations;
	}
}
