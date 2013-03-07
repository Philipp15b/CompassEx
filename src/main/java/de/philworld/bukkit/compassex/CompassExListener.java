package de.philworld.bukkit.compassex;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CompassExListener implements Listener {

	private final CompassEx plugin;

	public CompassExListener(CompassEx plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.trackerUpdater.removePlayer(event.getPlayer());
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			String name = ((Player) entity).getName();
			plugin.deathPoints.put(name, entity.getLocation());
		}
	}

}
