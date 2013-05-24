package de.philworld.bukkit.compassex;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

class DynmapHelper {

	public static DynmapHelper init(CompassEx plugin) {
		try {
			Plugin dynmap = Bukkit.getPluginManager().getPlugin("dynmap");
			if (dynmap == null)
				return null;
			DynmapCommonAPI dynmapAPI = (DynmapCommonAPI) dynmap;
			DynmapHelper dynmapHelper = new DynmapHelper(plugin, dynmapAPI);
			dynmapHelper.setup();
			return dynmapHelper;
		} catch (NoClassDefFoundError e) {
			return null;
		}
	}

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
			set = marker.createMarkerSet("compassex.saves", "Location", null, false);
			set.setLabelShow(true);
		}

		for (OwnedLocation loc : plugin.saving.publicLocations.getLocations()) {
			set(loc);
		}
	}

	public void cleanup() {
		if (set != null) {
			set.deleteMarkerSet();
			set = null;
		}
	}

	public void set(OwnedLocation loc) {
		remove(loc.id);
		set.createMarker(loc.id, loc.id, loc.world, loc.x, loc.y, loc.z, marker.getMarkerIcon(plugin.markerIcon), false);
	}

	public void remove(String id) {
		Marker m = set.findMarker(id);
		if (m != null)
			m.deleteMarker();
	}
}
