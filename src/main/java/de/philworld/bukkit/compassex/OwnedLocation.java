package de.philworld.bukkit.compassex;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import de.philworld.bukkit.compassex.util.BlockLocation;

public class OwnedLocation extends BlockLocation implements ConfigurationSerializable {

	public final String id;
	public final String owner;

	public OwnedLocation(String id, String owner, BlockLocation loc) {
		super(loc);
		this.id = Preconditions.checkNotNull(id);
		this.owner = Preconditions.checkNotNull(owner);
	}

	public OwnedLocation(String id, String owner, Location loc) {
		super(loc);
		this.id = Preconditions.checkNotNull(id);
		this.owner = Preconditions.checkNotNull(owner);
	}

	public OwnedLocation(Map<String, Object> map) {
		super(map);
		id = (String) map.get("id");
		owner = (String) map.get("owner");
	}

	public static OwnedLocation deserialize(Map<String, Object> map) {
		return new OwnedLocation(map);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>(6);
		map.put("id", id);
		map.put("owner", owner);
		map.putAll(super.serialize());
		return map;
	}

	public boolean isOwnedBy(Player p) {
		return owner.equals(p.getName());
	}

}
