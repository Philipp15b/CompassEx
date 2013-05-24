package de.philworld.bukkit.compassex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class PrivateLocationManager implements ConfigurationSerializable {

	private final Map<String, List<OwnedLocation>> locations = new HashMap<String, List<OwnedLocation>>();

	public PrivateLocationManager() {
	}

	public synchronized void add(OwnedLocation loc) {
		if (loc.id.equals(ConfigurationSerialization.SERIALIZED_TYPE_KEY))
			throw new IllegalArgumentException(ConfigurationSerialization.SERIALIZED_TYPE_KEY
					+ " may not be used as a location id!");
		if (!locations.containsKey(loc.owner)) {
			locations.put(loc.owner, new ArrayList<OwnedLocation>(2));
		}
		locations.get(loc.owner).add(loc);
	}

	public synchronized List<OwnedLocation> getLocations(String player) {
		return Collections.unmodifiableList(locations.get(player));
	}

	public synchronized OwnedLocation get(String player, String id) {
		List<OwnedLocation> l = locations.get(player);
		if (l == null)
			return null;
		for (OwnedLocation loc : l) {
			if (loc.id.equals(id))
				return loc;
		}
		return null;
	}

	public synchronized boolean remove(String player, String id) {
		List<OwnedLocation> l = locations.get(player);
		if (l == null)
			return false;
		Iterator<OwnedLocation> iterator = l.iterator();
		while (iterator.hasNext()) {
			OwnedLocation loc = iterator.next();
			if (loc.id.equals(id)) {
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
			// this is just stupid
			if (!entry.getKey().equals(ConfigurationSerialization.SERIALIZED_TYPE_KEY))
				locations.put(entry.getKey(), (List<OwnedLocation>) entry.getValue());
		}
	}

	@Override
	public synchronized Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>(locations.size());
		for (Entry<String, List<OwnedLocation>> entry : locations.entrySet()) {
			serialized.put(entry.getKey(), entry.getValue());
		}
		return serialized;
	}
}
