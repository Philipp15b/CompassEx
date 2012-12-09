package de.philworld.bukkit.compassex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LocationsYaml {

	private JavaPlugin plugin;
	private YamlConfiguration config = null;
	private String name = "locations";
	private File file;

	/**
	 * Constructor
	 * 
	 * @param plugin
	 *            The plugin
	 */
	public LocationsYaml(JavaPlugin plugin) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), name + ".yml");
	}

	/**
	 * Returns the name of the file
	 * 
	 * @return The name of the file
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the currently loaded configuration
	 * 
	 * @return currently loaded config
	 */
	public YamlConfiguration getConfig() {
		return config;
	}

	/**
	 * Loads the yml file
	 */
	public void reload() {
		try {
			config = YamlConfiguration.loadConfiguration(file);
			setDefaults(this.name);
		} catch (Exception e) {
			plugin.getLogger().log(Level.WARNING,
					"Could not load file: " + file + " ", e);
		}
	}

	public void setDefaults(String fileName) {

		InputStream defConfigStream = plugin.getResource(fileName + ".yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			config.options().copyDefaults(true);
			config.setDefaults(defConfig);
			save();
		}
	}

	/**
	 * Saves the yml file
	 */
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE,
					"Could not save to file: " + file + " ", e);
		}
	}

	/**
	 * @return the plugin
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}

	public OwnedLocation getPublicLocation(String id) {
		return getLocation(publics(), id);
	}

	public OwnedLocation getPrivateLocation(String id) {
		return getLocation(privates(), id);
	}

	public boolean hasPublicLocation(String id) {
		return hasLocation(publics(), id);
	}

	public boolean hasPrivateLocation(String id) {
		return hasLocation(privates(), id);
	}

	public void setPublicLocation(OwnedLocation value) {
		setLocation(publics(), value);
	}

	public void setPrivateLocation(OwnedLocation value) {
		setLocation(privates(), value);
	}

	public void clearPublicLocation(String id) {
		clearLocation(publics(), id);
	}

	public void clearPrivateLocation(String id) {
		clearLocation(privates(), id);
	}

	public Set<String> getPublicLocationIds() {
		return getLocationIds(publics());
	}

	public Set<String> getPrivateLocationIds() {
		return getLocationIds(privates());
	}

	public Set<String> getAllLocationIds() {
		HashSet<String> result = new HashSet<String>();
		result.addAll(getPrivateLocationIds());
		result.addAll(getPublicLocationIds());
		return result;
	}

	public Set<String> getPrivateOwnedLocationIds(String playerName) {
		return getOwnedLocationIds(privates(), playerName);
	}

	public Set<String> getPublicOwnedLocationIds(String playerName) {
		return getOwnedLocationIds(publics(), playerName);
	}

	public Set<String> getAllOwnedLocationIds(String playerName) {
		HashSet<String> result = new HashSet<String>();
		result.addAll(getPrivateOwnedLocationIds(playerName));
		result.addAll(getPublicOwnedLocationIds(playerName));
		return result;
	}

	public void makePrivate(String id) {
		OwnedLocation loc = getPublicLocation(id);
		if (loc != null) {
			setPrivateLocation(loc);
			clearPublicLocation(loc.getId());
		}
	}

	public void makePublic(String id) {
		OwnedLocation loc = getPrivateLocation(id);
		if (loc != null) {
			setPublicLocation(loc);
			clearPrivateLocation(loc.getId());
		}
	}

	protected ConfigurationSection publics() {
		if (!config.contains("publics")) {
			return config.createSection("publics");
		}
		return config.getConfigurationSection("publics");
	}

	protected ConfigurationSection privates() {
		if (!config.contains("privates")) {
			return config.createSection("privates");
		}
		return config.getConfigurationSection("privates");
	}

	protected Set<String> getLocationIds(ConfigurationSection section) {
		return section.getKeys(false);
	}

	protected Set<String> getOwnedLocationIds(ConfigurationSection section,
			String playerName) {
		Set<String> keys = getLocationIds(section);
		HashSet<String> result = new HashSet<String>();
		for (String id : keys) {
			OwnedLocation loc = getLocation(section, id);
			if (loc.getPlayerName().equals(playerName)) {
				result.add(loc.getId());
			}
		}
		return result;
	}

	protected OwnedLocation getLocation(ConfigurationSection section, String id) {
		return (OwnedLocation) section.get(id.toLowerCase().replace(" ", "_"));
	}

	protected void setLocation(ConfigurationSection section, OwnedLocation value) {
		section.set(value.getId().toLowerCase().replace(" ", "_"), value);
	}

	protected boolean hasLocation(ConfigurationSection section, String id) {
		return section.contains(id.toLowerCase());
	}

	protected void clearLocation(ConfigurationSection section, String id) {
		section.set(id.toLowerCase(), null);
	}
}
