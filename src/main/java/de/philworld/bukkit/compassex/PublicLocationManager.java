package de.philworld.bukkit.compassex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * A thread-safe manager for public owned locations.
 */
public class PublicLocationManager implements ConfigurationSerializable {

	private final Map<String, OwnedLocation> locations = new HashMap<String, OwnedLocation>();

	public PublicLocationManager() {
	}

	public synchronized void add(OwnedLocation loc) {
		if (loc.id.equals(ConfigurationSerialization.SERIALIZED_TYPE_KEY))
			throw new IllegalArgumentException(ConfigurationSerialization.SERIALIZED_TYPE_KEY
					+ " may not be used as a location id!");
		locations.put(loc.id, loc);
	}

	public synchronized OwnedLocation get(String id) {
		return locations.get(id);
	}

	public synchronized boolean remove(String id) {
		return locations.remove(id) != null;
	}

	public synchronized List<OwnedLocation> getLocations() {
		return new ArrayList<OwnedLocation>(locations.values());
	}

	public PublicLocationManager(Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			// this is just stupid
			if (!entry.getKey().equals(ConfigurationSerialization.SERIALIZED_TYPE_KEY))
				locations.put(entry.getKey(), (OwnedLocation) entry.getValue());
		}
	}

	@Override
	public synchronized Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>(locations.size());
		for (Entry<String, OwnedLocation> entry : locations.entrySet()) {
			serialized.put(entry.getKey(), entry.getValue());
		}
		return serialized;
	}

}
