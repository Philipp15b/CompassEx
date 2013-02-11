package de.philworld.bukkit.compassex;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class LocationsYaml {

	private final CompassEx plugin;
	private final File file;
	private YamlConfiguration config = null;

	public LocationsYaml(CompassEx plugin, String filename) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), filename);
	}

	public YamlConfiguration getConfig() {
		return config;
	}

	public void reload() {
		try {
			config = YamlConfiguration.loadConfiguration(file);
		} catch (Exception e) {
			plugin.getLogger().log(Level.WARNING,
					"Could not load file: " + file + " ", e);
		}
	}

	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE,
					"Could not save to file: " + file + " ", e);
		}
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
		if (plugin.dynmapHelper != null) {
			plugin.dynmapHelper.set(value);
		}
		setLocation(publics(), value);
	}

	public void setPrivateLocation(OwnedLocation value) {
		setLocation(privates(), value);
	}

	public void clearPublicLocation(String id) {
		if (plugin.dynmapHelper != null) {
			plugin.dynmapHelper.remove(id);
		}
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
