package de.philworld.bukkit.compassex.migrations;

import java.io.File;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.Vector;

import de.philworld.bukkit.compassex.CompassEx;
import de.philworld.bukkit.compassex.OwnedLocation;
import de.philworld.bukkit.compassex.PrivateLocationManager;
import de.philworld.bukkit.compassex.PublicLocationManager;
import de.philworld.bukkit.compassex.util.BlockLocation;

/**
 * Loads v2-style configs.
 */
public class Migration2 {

	public static boolean should(CompassEx plugin) {
		return new File(plugin.getDataFolder(), "locations.yml").exists();
	}

	private final CompassEx plugin;
	private File file;
	private YamlConfiguration config;

	public Migration2(CompassEx plugin) {
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), "locations.yml");
		ConfigurationSerialization.registerClass(OwnedLocationV2.class, OwnedLocation.class.getName());
		this.config = YamlConfiguration.loadConfiguration(file);
	}

	public PrivateLocationManager loadPrivateLocations() {
		PrivateLocationManager locations = new PrivateLocationManager();
		ConfigurationSection section = config.getConfigurationSection("privates");
		for (String key : section.getKeys(false)) {
			locations.add(((OwnedLocationV2) section.get(key)).loc);
		}
		return locations;
	}

	public PublicLocationManager loadPublicLocations() {
		PublicLocationManager locations = new PublicLocationManager();
		ConfigurationSection section = config.getConfigurationSection("publics");
		for (String key : section.getKeys(false)) {
			OwnedLocation loc = ((OwnedLocationV2) section.get(key)).loc;
			locations.add(loc);
		}
		return locations;
	}

	public void finish() {
		ConfigurationSerialization.unregisterClass(OwnedLocationV2.class);
		config = null;
		file.renameTo(new File(plugin.getDataFolder(), "locations-OLD.yml"));
		file = null;
	}

	public static class OwnedLocationV2 implements ConfigurationSerializable {

		public final OwnedLocation loc;

		public OwnedLocationV2(Map<String, Object> map) {
			String id = (String) map.get("id");
			String owner = (String) map.get("playerName");

			@SuppressWarnings("unchecked")
			Map<String, Object> locationSection = (Map<String, Object>) map.get("location");
			Vector vec = (Vector) locationSection.get("vector");
			String world = (String) locationSection.get("world");

			loc = new OwnedLocation(id, owner, new BlockLocation(world, vec.getBlockX(), vec.getBlockY(),
					vec.getBlockZ()));
		}

		@Override
		public Map<String, Object> serialize() {
			throw new UnsupportedOperationException();
		}

	}

}
