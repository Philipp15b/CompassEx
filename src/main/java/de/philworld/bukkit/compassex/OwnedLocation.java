package de.philworld.bukkit.compassex;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
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
		this.id = (String) map.get("id");
		this.playerName = (String) map.get("playerName");
		Vector vec = (Vector) map.get("vector");
		String world = (String) map.get("world");
		this.location = new Location(Bukkit.getServer().getWorld(world),
				vec.getX(), vec.getY(), vec.getZ());
	}

	public static OwnedLocation deserialize(Map<String, Object> map) {
		return new OwnedLocation(map);
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("id", id);
		map.put("playerName", playerName);
		map.put("vector", location.toVector());
		map.put("world", location.getWorld().getName());
		return map;
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

	public boolean ownedBy(Player p) {
		return playerName.equals(p.getName());
	}

}
