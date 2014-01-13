package de.philworld.bukkit.compassex;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import de.philworld.bukkit.compassex.command.Command;
import de.philworld.bukkit.compassex.command.CommandContext;
import de.philworld.bukkit.compassex.persistence.Persistable;
import de.philworld.bukkit.compassex.util.BlockLocation;
import de.philworld.bukkit.compassex.util.PermissionException;

public class DeathpointComponent extends Component implements Listener, Persistable {

	private final Map<String, BlockLocation> deathPoints = new HashMap<String, BlockLocation>();

	public DeathpointComponent(CompassEx plugin) {
		super(plugin);

		plugin.getServer().getPluginManager().registerEvents(this, plugin);

		help("deathpoint", "Set to your latest death point", "compassex.deathpoint");

		load();
	}

	private void load() {
		File f = new File(plugin.getDataFolder(), "deathpoints.db.yml");
		if (!f.exists())
			return;
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		for (String player : config.getKeys(false)) {
			deathPoints.put(player, (BlockLocation) config.get(player));
		}
	}

	@Override
	public void save() {
		YamlConfiguration config = new YamlConfiguration();
		synchronized (deathPoints) {
			for (Entry<String, BlockLocation> point : deathPoints.entrySet()) {
				config.set(point.getKey(), point.getValue());
			}
		}
		try {
			config.save(new File(plugin.getDataFolder(), "deathpoints.db.yml"));
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to save death points!", e);
		}
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "deathpoint", "dp", "death" }, permission = "compassex.deathpoint")
	public void deathpoint(CommandContext context, Player p) throws PermissionException {
		BlockLocation dp = deathPoints.get(p.getName());
		if (dp == null) {
			sendMessage(p, "Could not find your last death point.");
			return;
		}
		Location loc = dp.toLocation();
		if (loc == null || !loc.getWorld().equals(p.getWorld())) {
			sendMessage(p, "Your deathpoint is in another world!");
			return;
		}
		setTarget(p, dp.toLocation());
		sendMessage(p, "Set your compass to your last death point.");
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player p = (Player) entity;
			synchronized (deathPoints) {
				deathPoints.put(p.getName(), new BlockLocation(entity.getLocation()));
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (event.getPlayer().hasPermission("compassex.giveondeath")) {
			synchronized (deathPoints) {
				if (!deathPoints.containsKey(event.getPlayer().getName()))
					return;
			}
			givePlayerDPCompass(event.getPlayer());
		}
	}

	/**
	 * Gives a player a compass pointed to his/her death point.
	 */
	private void givePlayerDPCompass(Player p) {
		p.getInventory().addItem(new ItemStack(Material.COMPASS));

		BlockLocation dp = deathPoints.get(p.getName());
		Location loc = dp.toLocation();
		if (loc == null || !loc.getWorld().equals(p.getWorld()))
			return; // other world

		setTarget(p, loc);
	}

}
