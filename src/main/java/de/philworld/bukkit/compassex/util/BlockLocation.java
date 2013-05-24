package de.philworld.bukkit.compassex.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

/**
 * A block location with the world name instead of a world instance.
 */
public class BlockLocation implements ConfigurationSerializable {

	public final String world;
	public final int x;
	public final int y;
	public final int z;

	public BlockLocation(BlockLocation o) {
		world = o.world;
		x = o.x;
		y = o.y;
		z = o.z;
	}

	public BlockLocation(String world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockLocation(Location loc) {
		this(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public Location toLocation() {
		World w = Bukkit.getServer().getWorld(world);
		if (w == null)
			return null;
		return new Location(w, x, y, z);
	}

	public BlockLocation(Map<String, Object> map) {
		this((String) map.get("world"), (Integer) map.get("x"), (Integer) map.get("y"), (Integer) map.get("z"));
	}

	public Vector toVector() {
		return new Vector(x, y, z);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>(4);
		map.put("world", world);
		map.put("x", x);
		map.put("y", y);
		map.put("z", z);
		return map;
	}

}
