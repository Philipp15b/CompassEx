package de.philworld.bukkit.compassex;

import java.util.logging.Logger;

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
		if(plugin.stopLiveTask(event.getPlayer())) {
			Logger log = Logger.getLogger("Minecraft");
			log.info("[CompassEx] Stopped live task of player " + event.getPlayer().getDisplayName());
		}
	}
	
}
