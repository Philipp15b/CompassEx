package de.philworld.bukkit.compassex;

import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

class DynmapHelper {

	private final CompassEx plugin;
	private final MarkerAPI marker;
	private MarkerSet set;

	public DynmapHelper(CompassEx plugin, DynmapCommonAPI dynmap) {
		this.plugin = plugin;
		this.marker = dynmap.getMarkerAPI();
	}

	public void setup() throws IllegalStateException {
		set = marker.getMarkerSet("compassex.saves");
		if (set == null) {
			set = marker.createMarkerSet("compassex.saves", "Location", null,
					false);
			set.setLabelShow(true);
		}

		for (String id : plugin.locations.getPublicLocationIds()) {
			set(plugin.locations.getPublicLocation(id));
		}
	}

	public void cleanup() {
		if (set != null) {
			set.deleteMarkerSet();
			set = null;
		}
	}

	public void set(OwnedLocation loc) {
		remove(loc.getId());
		set.createMarker(loc.getId(), loc.getId(), loc.getLocation().getWorld()
				.getName(), loc.getLocation().getX(), loc.getLocation().getY(),
				loc.getLocation().getZ(), marker.getMarkerIcon(plugin.markerIcon), false);
	}

	public void remove(String id) {
		Marker m = set.findMarker(id);
		if (m != null)
			m.deleteMarker();
	}
}
