package de.philworld.bukkit.compassex;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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

	static final CommandHelpProvider helpMessages = new CommandHelpProvider() {
		{
			// Basic Setters
			add("reset", "Reset back to spawn", "compassex.reset");
			add("here", "Set to your current position", "compassex.here");
			add("north/east/south/west", "Set to a direction.",
					"compassex.direction");
			add("PLAYERNAME", "Set to a player", "compassex.player");
			add("live", "Set to a player's pos & update", "compassex.live");
			add("bed", "Set to your bed", "compassex.bed");
			add("X Y Z", "Set to coordinates", "compassex.pos");
			add("height", "Height diff between you and the target",
					"compassex.height");
			add("deathpoint", "Set to your latest death point",
					"compassex.deathpoint");
			add("info ID",
					"See the coordinates of your current compass target, or a saved location.",
					"compassex.info");

			// Hiding
			add("hide", "Hide from being tracked", "compassex.hide");
			add("hidden", "Are you hidden?", "compassex.hide");

			// Saving
			add("save ID", "Save your current compass target", "compassex.save");
			add("save here ID", "Save your current location", "compassex.save");
			add("remove ID", "Remove an existing location", "compassex.remove");
			add("load ID", "Set a saved location to your compass",
					"compassex.load");
			add("list private|public", "List saved locations.",
					"compassex.list");
			add("privatize ID", "Make a location private.",
					"compassex.privatize");
			add("publicize ID", "Make a location public.",
					"compassex.publicize");
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

	Set<String> hiddenPlayers = new HashSet<String>(2);

	HashMap<String, Location> deathPoints = new HashMap<String, Location>();

	@Override
	public void onEnable() {
		loadConfiguration();

		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new CompassExListener(this), this);

		trackerUpdater = new CompassTrackerUpdater(this);
		trackerUpdater.setUpdateRate(getConfig()
				.getInt("live-update-rate", 200));

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
			if (setupDynmap()) {
				getLogger().log(Level.INFO, "Dynmap Support is enabled!");
			} else {
				getLogger()
						.log(Level.WARNING,
								"Dynmap Support could not be enabled: Dynmap not found!");
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

	void hide(Player player) {
		if (!isHidden(player)) {
			hiddenPlayers.add(player.getName());
		}
	}

	void unhide(Player player) {
		if (isHidden(player)) {
			hiddenPlayers.remove(player.getName());
		}
	}

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
