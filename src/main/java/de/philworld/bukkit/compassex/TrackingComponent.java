package de.philworld.bukkit.compassex;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.WHITE;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.philworld.bukkit.compassex.command.Command;
import de.philworld.bukkit.compassex.command.CommandContext;

public class TrackingComponent extends Component implements Listener {

	/**
	 * Map watcher => watched
	 */
	private final Map<String, String> watchMap = new LinkedHashMap<String, String>(2);
	private final long updateRate;
	private final CompassUpdaterTask updater;

	public TrackingComponent(CompassEx plugin) {
		super(plugin);
		updateRate = plugin.getConfig().getInt("live-update-rate", 200);
		updater = new CompassUpdaterTask();
		updater.start();
	}

	@Command(aliases = { "live" }, permission = "compassex.live")
	public void live(CommandContext context, Player p) {
		List<Player> foundPlayers = plugin.getServer().matchPlayer(context.arg1);

		if (foundPlayers.size() != 1) {
			sendMessage(p, "Player cannot be found.");
			return;
		}

		Player target = foundPlayers.get(0);

		if (plugin.hiding.isHidden(p) && !p.hasPermission("compassex.admin")) {
			sendMessage(p, "Player cannot be found.");
			return;
		}

		try {
			setWatcher(p, target);
		} catch (IllegalArgumentException e) {
			sendMessage(p, e.getMessage());
			return;
		}

		sendMessage(p, "Your compass is now pointing live to " + BLUE + target.getDisplayName() + WHITE + ".");
	}

	/**
	 * Sets a watcher and the watched player, starts the task if not running.
	 * 
	 * @throws IllegalArgumentException
	 *             If both players are the same.
	 */
	private void setWatcher(Player watcher, Player watched) {
		if (watcher.equals(watched))
			throw new IllegalArgumentException("Watcher and watched player may not be the same!");

		watchMap.put(watcher.getName(), watched.getName());
		updater.start();
	}

	boolean removeWatcher(Player player) {
		String name = player.getName();
		boolean success = watchMap.remove(name) != null;
		if (watchMap.isEmpty())
			updater.stop();
		return success;
	}

	private void removePlayer(Player p, String reason) {
		if (watchMap.isEmpty())
			return;

		String name = p.getName();
		Iterator<Entry<String, String>> it = watchMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> pair = it.next();
			if (pair.getValue().equals(name)) {
				Player watcher = Bukkit.getServer().getPlayer(pair.getKey());
				if (watcher != null)
					sendMessage(watcher, "Your watched player, " + BLUE + name + WHITE + ", " + reason);
			} else if (pair.getKey().equals(name)) {
			} else {
				continue;
			}

			it.remove();
		}

		if (watchMap.isEmpty())
			updater.stop();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		if (watchMap.containsKey(event.getPlayer().getName()))
			sendMessage(event.getPlayer(), "You're now in a different world. Stopped tracking.");
		removePlayer(event.getPlayer(), "is now in a different world. Stopped tracking.");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		removePlayer(event.getPlayer(), "has left the server. Stopped tracking.");
	}

	private class CompassUpdaterTask implements Runnable {

		private int taskId = -2;

		public void start() {
			if (isRunning())
				return;
			taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 40L, updateRate);
		}

		public boolean isRunning() {
			return taskId > 0;
		}

		public void stop() {
			if (!isRunning())
				return;
			plugin.getServer().getScheduler().cancelTask(taskId);
			taskId = -2;
		}

		@Override
		public void run() {
			Server server = plugin.getServer();
			for (Entry<String, String> entry : watchMap.entrySet()) {
				Player watcher = server.getPlayer(entry.getKey());
				Player watched = server.getPlayer(entry.getValue());

				// it seems that sometimes the task is run after a player has
				// left the server, but before the appropriate events are thrown
				// and the player can be removed from the watchMap. In this
				// case, we just ignore it.
				if (watched == null || watcher == null)
					continue;

				watcher.setCompassTarget(watched.getLocation());
				watcher.saveData();
			}
		}
	}

}
