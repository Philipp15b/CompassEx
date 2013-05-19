package de.philworld.bukkit.compassex;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

public class OwnedLocation implements ConfigurationSerializable {

	public final String id;
	public final String owner;
	public final String world;
	public final Vector vector;

	public OwnedLocation(String id, String owner, String world, Vector vector) {
		this.id = Preconditions.checkNotNull(id);
		this.owner = Preconditions.checkNotNull(owner);
		this.world = Preconditions.checkNotNull(world);
		this.vector = Preconditions.checkNotNull(vector);
	}

	public OwnedLocation(String id, String owner, Location location) {
		this(id, owner, location.getWorld().getName(), location.toVector());
	}

	public OwnedLocation(Map<String, Object> map) {
		this((String) map.get("id"), (String) map.get("owner"), (String) map.get("world"), (Vector) map.get("vector"));
	}

	public static OwnedLocation deserialize(Map<String, Object> map) {
		return new OwnedLocation(map);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("id", id);
		map.put("owner", owner);
		map.put("world", world);
		map.put("vector", vector);
		return map;
	}

	public Location toLocation() {
		World w = Bukkit.getServer().getWorld(world);
		if (w == null)
			return null;
		return new Location(w, vector.getX(), vector.getY(), vector.getZ());
	}

	public boolean isOwnedBy(Player p) {
		return owner.equals(p.getName());
	}

}
