package de.philworld.bukkit.compassex;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
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
	
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player) {
			String name = ((Player) entity).getName();
			plugin.deathPoints.put(name, entity.getLocation());
		}
	}
	
}
