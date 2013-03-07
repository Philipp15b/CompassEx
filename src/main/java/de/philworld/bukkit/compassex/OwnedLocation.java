package de.philworld.bukkit.compassex;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

public class OwnedLocation implements ConfigurationSerializable {

	private final String id;
	private final String playerName;
	private final Location location;

	public OwnedLocation(String id, String playerName, Location location) {
		this.id = id;
		this.playerName = playerName;
		this.location = location;
	}

	public OwnedLocation(Map<String, Object> map) {
		this.id = map.get("id").toString();
		this.playerName = map.get("playerName").toString();

		Map<?, ?> locationSection = (Map<?, ?>) map.get("location");
		Vector vec = (Vector) locationSection.get("vector");
		String world = locationSection.get("world").toString();

		this.location = new Location(Bukkit.getServer().getWorld(world),
				vec.getX(), vec.getY(), vec.getZ());
	}

	public static OwnedLocation deserialize(Map<String, Object> map) {
		return new OwnedLocation(map);
	}

	public Map<String, Object> serialize() {
		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put("id", id);
		result.put("playerName", playerName);

		HashMap<String, Object> locationSection = new HashMap<String, Object>();
		locationSection.put("vector", location.toVector());
		locationSection.put("world", location.getWorld().getName());

		result.put("location", locationSection);

		return result;
	}

	public String getId() {
		return id;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Location getLocation() {
		return location;
	}

}
