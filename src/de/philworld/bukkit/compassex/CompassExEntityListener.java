package de.philworld.bukkit.compassex;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Saves player's death points.
 */
public class CompassExEntityListener extends PlayerListener  {

	CompassEx plugin;
	
	public CompassExEntityListener (CompassEx p) {
		plugin = p;
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player) {
			String name = ((Player) entity).getName();
			plugin.deathPoints.put(name, entity.getLocation());
		}
	}
	
}
