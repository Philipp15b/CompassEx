package de.philworld.bukkit.compassex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapCommonAPI;
import org.mcstats.Metrics;

public class CompassEx extends JavaPlugin {

	@SuppressWarnings("serial")
	static final Map<String, String> helpMessages = new LinkedHashMap<String, String>() {
		{
			// % will be removed. this is so that 2 help entries can exist with
			// the same permission.
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
			put("compassex.bed", "&red;/&command; bed&blue; Set to your bed");
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
			put("compassex.save",
					"&red;/&command; save ID&blue; Save your current compass target");
			put("compassex.save%",
					"&red;/&command; save here ID&blue; Save your current location");
			put("compassex.remove",
					"&red;/&command; remove ID&blue; Remove an existing location");
			put("compassex.load",
					"&red;/&command; load&blue; Set a saved location to your compass");
			put("compassex.list",
					"&red;/&command; list private|public&blue; List saved locations.");
			put("compassex.info",
					"&red;/&command; info [ID]&blue; See the coordinates of your current compass target, or a saved location.");
			put("compassex.privatize",
					"&red;/&command; private ID&blue; Convert a location to private location.");
			put("compassex.publicize",
					"&red;/&command; public ID&blue; Convert a location to public location.");
		}
	};

	static {
		ConfigurationSerialization.registerClass(OwnedLocation.class);
	}

	static Economy economy = null;

	FileConfiguration config;
	LocationsYaml locations;
	CompassTrackerUpdater trackerUpdater;
	DynmapHelper dynmapHelper;

	int helpPageNumCommands;
	double saveCost;
	double publicizeCost;
	double privatizeCost;
	boolean enableDynmap;
	String markerIcon;

	// save all hidden players in a list
	List<String> hiddenPlayers = new ArrayList<String>();

	HashMap<String, Location> deathPoints = new HashMap<String, Location>();

	@Override
	public void onEnable() {
		loadConfiguration();

		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new CompassExListener(this), this);

		trackerUpdater = new CompassTrackerUpdater(this);
		trackerUpdater.setUpdateRate(getConfig().getInt("live-update-rate", 200));

		locations = new LocationsYaml(this, "locations.yml");
		locations.reload();

		helpPageNumCommands = getConfig().getInt("help-page-num-commands", 10);
		saveCost = getConfig().getDouble("save-cost", 0);
		privatizeCost = getConfig().getDouble("privatize-cost", 0);
		publicizeCost = getConfig().getDouble("publicize-cost", 0);
		
		enableDynmap = getConfig().getBoolean("enable-dynmap", true);
		markerIcon = getConfig().getString("dynmap-icon", "compass");

		if (setupEconomy()) {
			getLogger().log(Level.INFO, "Using Vault for payment.");
		} else {
			getLogger().log(Level.INFO,
					"Vault was not found, all actions will be free!");
		}

		if (enableDynmap) {
			if(setupDynmap()) {
				getLogger().log(Level.INFO, "Dynmap Support is enabled!");
			} else {
				getLogger().log(Level.WARNING, "Dynmap Support could not be enabled: Dynmap not found!");
			}
		}

		getCommand("compass").setExecutor(new CompassExCommandExecutor(this));

		try {
			new Metrics(this).start();
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Error enabling Metrics:", e);
		}
	}

	@Override
	public void onDisable() {
		trackerUpdater.stop();
		trackerUpdater = null;
	}

	LocationsYaml getSavedLocations() {
		return locations;
	}

	/**
	 * Hide the given player if it is not already hidden.
	 */
	void hide(Player player) {
		if (!isHidden(player)) {
			hiddenPlayers.add(player.getName());
		}
	}

	/**
	 * Unhide the given player if it is not already visible.
	 */
	void unHide(Player player) {
		if (isHidden(player)) {
			hiddenPlayers.remove(player.getName());
		}
	}

	/**
	 * Returns if the player is hidden.
	 */
	boolean isHidden(Player player) {
		return hiddenPlayers.contains(player.getName());
	}

	/**
	 * Loads the configuration and inserts the defaults.
	 */
	private void loadConfiguration() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private boolean setupEconomy() {
		try {
			RegisteredServiceProvider<Economy> economyProvider = getServer()
					.getServicesManager().getRegistration(
							net.milkbowl.vault.economy.Economy.class);

			if (economyProvider == null)
				return false;

			economy = economyProvider.getProvider();

			return true;
		} catch (NoClassDefFoundError e) {
			return false;
		}
	}

	private boolean setupDynmap() {
		try {
			Plugin p = Bukkit.getPluginManager().getPlugin("dynmap");
			if (p == null)
				return false;
			DynmapCommonAPI dynmap = (DynmapCommonAPI) p;
			dynmapHelper = new DynmapHelper(this, dynmap);
			dynmapHelper.setup();
			return true;
		} catch (NoClassDefFoundError e) {
			return false;
		}
	}
}
