package de.philworld.bukkit.compassex;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for PlayerQuitEvent and stops the live task of the player.
 * 
 */
public class CompassExPlayerListener extends PlayerListener  {

	CompassEx plugin;
	
	public CompassExPlayerListener (CompassEx p) {
		plugin = p;
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		CompassTrackerUpdater.removePlayer(event.getPlayer());
	}
	
}
