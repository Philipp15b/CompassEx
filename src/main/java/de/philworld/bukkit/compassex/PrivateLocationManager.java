package de.philworld.bukkit.compassex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class PrivateLocationManager implements ConfigurationSerializable {

	private final Map<String, List<OwnedLocation>> locations = new HashMap<String, List<OwnedLocation>>();

	public PrivateLocationManager() {
	}

	public void add(OwnedLocation loc) {
		if (!locations.containsKey(loc.getPlayerName())) {
			locations.put(loc.getPlayerName(), new ArrayList<OwnedLocation>(2));
		}
		locations.get(loc.getPlayerName()).add(loc);
	}

	public List<OwnedLocation> getLocations(Player player) {
		return locations.get(player.getName());
	}

	public OwnedLocation get(String player, String id) {
		List<OwnedLocation> l = locations.get(player);
		if (l == null)
			return null;
		for (OwnedLocation loc : l) {
			if (loc.getId().equals(id))
				return loc;
		}
		return null;
	}

	public boolean remove(String player, String id) {
		List<OwnedLocation> l = locations.get(player);
		if (l == null)
			return false;
		Iterator<OwnedLocation> iterator = l.iterator();
		while (iterator.hasNext()) {
			OwnedLocation loc = iterator.next();
			if (loc.getId().equals(id)) {
				if (l.size() == 1)
					locations.remove(player);
				else
					iterator.remove();
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public PrivateLocationManager(Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			locations.put(entry.getKey(),
					(List<OwnedLocation>) entry.getValue());
		}
	}

	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>(
				locations.size());
		for (Entry<String, List<OwnedLocation>> entry : locations.entrySet()) {
			serialized.put(entry.getKey(), entry.getValue());
		}
		return serialized;
	}
}
