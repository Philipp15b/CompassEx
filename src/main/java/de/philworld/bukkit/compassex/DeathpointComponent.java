package de.philworld.bukkit.compassex;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import de.philworld.bukkit.compassex.command.Command;
import de.philworld.bukkit.compassex.command.CommandContext;
import de.philworld.bukkit.compassex.util.PermissionException;

public class DeathpointComponent extends Component implements Listener {

	final Map<String, Location> deathPoints = new HashMap<String, Location>();

	public DeathpointComponent(CompassEx plugin) {
		super(plugin);

		plugin.getServer().getPluginManager().registerEvents(this, plugin);

		help("deathpoint", "Set to your latest death point", "compassex.deathpoint");
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "deathpoint", "dp", "death" }, permission = "compassex.deathpoint")
	public void deathpoint(CommandContext context, Player p) throws PermissionException {
		Location dp = deathPoints.get(p.getName());
		if (dp == null) {
			sendMessage(p, "Could not find your last death point.");
			return;
		}
		setTarget(p, dp);
		sendMessage(p, "Set your compass to your last death point.");
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			String name = ((Player) entity).getName();
			deathPoints.put(name, entity.getLocation());
		}
	}

}
