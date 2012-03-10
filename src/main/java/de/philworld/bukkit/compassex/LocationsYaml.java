package de.philworld.bukkit.compassex;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class LocationsYaml {

	private JavaPlugin plugin;
	private YamlConfiguration config = null;
	private String name;
	private File file;

	/**
	 * Constructor
	 * @param plugin The plugin
	 * @param name the filename without .yml extension
	 */
	public LocationsYaml(JavaPlugin plugin) {
		if (plugin == null) {
			throw new NullPointerException("Parameter plugin must be non-null.");
		}
		this.plugin = plugin;
		this.name = "locations";
		
		file = new File(plugin.getDataFolder(), this.name + ".yml");
	}
	
	/**
	 * Returns the name of the file
	 * @return The name of the file
	 */
	public String getName() { return name; }

	/**
	 * Returns the currently loaded configuration
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
		}
		catch(Exception e) {
			plugin.getLogger().log(Level.WARNING, plugin.getDescription().getFullName() + " could not load file: " + file + " ", e);
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
					plugin.getDescription().getFullName() + " could not save to file: " + file + " ", e);
		}
	}

	/**
	 * @return the plugin
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	
	
	
	
	protected ConfigurationSection privates() {
		if(!config.contains("privates")) {
			return config.createSection("privates");
		}
		return config.getConfigurationSection("privates");
	}
	
	protected ConfigurationSection publics() {
		if(!config.contains("publics")) {
			return config.createSection("publics");
		}
		return config.getConfigurationSection("publics");
	}
	
	public Set<String> getPrivateLocations(String playerName) {
		try {
			return privates().getConfigurationSection(playerName).getKeys(false);
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	public Set<String> getPublicLocations() {
		try {
			return publics().getKeys(false);
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	public boolean hasLocation(String playerName, String id) {
		return privates().contains(playerName + "." + id.toLowerCase());
	}
	
	public void saveLocation(String playerName, Location location, String id) {
		
		String path = playerName + "." + id.toLowerCase();
		privates().set(path + ".world", location.getWorld().getName());
		privates().set(path + ".vector", location.toVector());
		save();
	}
	
	public Location getLocation(String playerName, String id) {
		String path = playerName + "." + id.toLowerCase();
		Vector vec = privates().getVector(path + ".vector", null);
		if(vec == null) {
			return null;
		}
		String worldName = privates().getString(path + ".world");
		return new Location(plugin.getServer().getWorld(worldName), vec.getX(), vec.getY(), vec.getZ());
	}
	
	public boolean hasPublicLocation(String id) {
		return publics().contains(id.toLowerCase());
	}
	
	public Location getPublicLocation(String id) {
		String path = id.toLowerCase();
		Vector vec = publics().getVector(path + ".vector", null);
		if(vec == null) {
			return null;
		}
		String worldName = publics().getString(path + ".world");
		return new Location(plugin.getServer().getWorld(worldName), vec.getX(), vec.getY(), vec.getZ());
	}
	
	public void makePublic(String playerName, String id) {
		String lowerId = id.toLowerCase();
		if(!privates().contains(playerName + "." + lowerId)) {
			throw new NullPointerException("Player \"" + playerName + "\" doesn't have a private location called \"" + id + "\".");
		}
		publics().set(lowerId, privates().get(playerName + "." + lowerId));
		privates().set(playerName + "." + lowerId, null);
		save();
	}
	
	public void makePrivate(String playerName, String id) {
		String lowerId = id.toLowerCase();
		if(!publics().contains(lowerId)) {
			throw new NullPointerException("Public location \"" + id + "\" does not exist.");
		}
		privates().set(playerName + "." + lowerId, publics().get(lowerId));
		publics().set(lowerId, null);
		save();
	}
	
	public void removePrivate(String playerName, String id) {
		privates().set(playerName + "." + id.toLowerCase(), null);
		save();
	}
	
	public void removePublic(String id) {
		publics().set(id.toLowerCase(), null);
		save();
	}
}
