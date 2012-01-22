package de.philworld.bukkit.compassex;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for PlayerQuitEvent and stops the live task of the player and listens to EntityDeathEvent to save death locations.
 */
public class CompassExListener implements Listener  {

	CompassEx plugin;
	
	public CompassExListener (CompassEx p) {
		plugin = p;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		CompassTrackerUpdater.removePlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player) {
			String name = ((Player) entity).getName();
			plugin.deathPoints.put(name, entity.getLocation());
		}
	}
	
}
