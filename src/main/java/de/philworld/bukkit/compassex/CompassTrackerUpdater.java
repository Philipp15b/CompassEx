package de.philworld.bukkit.compassex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CompassTrackerUpdater implements Runnable {

	/**
	 * Hashmap watcher => watched
	 */
	private HashMap<String, String> watchList = new HashMap<String, String>();
	private int taskId = -2;
	private JavaPlugin plugin;
	private long updateRate = 2000;

	public CompassTrackerUpdater(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Sets how often the compass is updated.
	 */
	public void setUpdateRate(long updateRate) {
		this.updateRate = updateRate;
		if (isRunning()) { // restart if necessary
			stop();
			start();
		}
	}

	/**
	 * Start the scheduled task to update the compasses
	 * 
	 * @return false if scheduling failed or true if it was already scheduled or
	 *         scheduling succeeded.
	 */
	public boolean start() {
		if (isRunning())
			return true;
		taskId = plugin.getServer().getScheduler()
				.scheduleSyncRepeatingTask(plugin, this, 40L, updateRate);
		return isRunning();
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

	/**
	 * Sets a watcher and the watched player, starts the task if not running.
	 * 
	 * @throws IllegalArgumentException
	 *             If both players are the same entity.
	 */
	public void setWatcher(Player watcher, Player watched) {
		if (watcher.getEntityId() == watched.getEntityId())
			throw new IllegalArgumentException(
					"Watcher and watched player may not be the same!");

		watchList.put(watcher.getName(), watched.getName());
		start();
	}

	/**
	 * Removes a player from the watchList, watchers as well as watched
	 */
	public void removePlayer(Player player) {
		String name = player.getName();
		// remove watched players
		// TODO: send notification to watcher when the watched leaves
		if (watchList.containsValue(name)) {
			Iterator<String> iterator = watchList.values().iterator();
			while (iterator.hasNext()) {
				if (iterator.next() == name) {
					iterator.remove();
				}
			}
		}

		removeWatcher(player);
	}

	public void removeWatcher(Player player) {
		String name = player.getName();
		watchList.remove(name);
		if (watchList.isEmpty()) {
			stop();
		}
	}

	@Override
	public void run() {
		Server server = plugin.getServer();
		for (Map.Entry<String, String> entry : watchList.entrySet()) {
			Player watcher = server.getPlayer(entry.getKey());
			Player watched = server.getPlayer(entry.getValue());
			watcher.setCompassTarget(watched.getLocation());
			watcher.saveData();
		}
	}

}
