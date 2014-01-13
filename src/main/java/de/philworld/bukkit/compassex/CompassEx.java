package de.philworld.bukkit.compassex;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import de.philworld.bukkit.compassex.command.HelpManager;
import de.philworld.bukkit.compassex.persistence.PersisterTask;
import net.milkbowl.vault.economy.Economy;

public class CompassEx extends JavaPlugin {

	static Economy economy = null;

	HelpManager helpManager = new HelpManager();

	CompassExCommandExecutor executor;
	PersisterTask persister;

	FileConfiguration config;

	DynmapHelper dynmapHelper;
	String markerIcon;

	VanishHelper vanish;

	TrackingComponent tracking;
	SavingComponent saving;
	HidingComponent hiding;
	DeathpointComponent death;
	GeneralComponent general;
	InfoComponent info;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		loadConfiguration();

		// the order is also the order for help entries
		general = new GeneralComponent(this);
		info = new InfoComponent(this);
		death = new DeathpointComponent(this);
		saving = new SavingComponent(this);
		tracking = new TrackingComponent(this);
		hiding = new HidingComponent(this);

		executor = new CompassExCommandExecutor(this);

		markerIcon = getConfig().getString("dynmap-icon", "compass");

		if (setupEconomy()) {
			getLogger().log(Level.INFO, "Using Vault for payment.");
		} else {
			getLogger().log(Level.INFO, "Vault was not found, all actions will be free!");
		}

		if (getConfig().getBoolean("enable-dynmap", true)) {
			dynmapHelper = DynmapHelper.init(this);
			if (dynmapHelper != null) {
				getLogger().log(Level.INFO, "Dynmap Support is enabled!");
			} else {
				getLogger().log(Level.WARNING, "Dynmap Support could not be enabled: Dynmap not found!");
			}
		}

		if (getConfig().getBoolean("enable-vanish", true)) {
			vanish = VanishHelper.get();
			if (vanish != null) {
				getLogger().log(Level.INFO, "Vanish support is enabled!");
			} else {
				getLogger().log(Level.WARNING, "Vanish support could not be enabled: Vanish not found!");
			}
		}

		persister = new PersisterTask(death, saving, hiding);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, persister, 5 * 20, 5 * 60 * 20);

		try {
			new MetricsLite(this).start();
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Error enabling Metrics:", e);
		}
	}

	@Override
	public void onDisable() {
		economy = null;
		persister.run();
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
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(
					net.milkbowl.vault.economy.Economy.class);

			if (economyProvider == null)
				return false;

			economy = economyProvider.getProvider();

			return true;
		} catch (NoClassDefFoundError e) {
			return false;
		}
	}

}
