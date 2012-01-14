package de.philworld.bukkit.compassex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CompassTrackerUpdater implements Runnable {
	
	/**
	 * Hashmap  watcher => watched
	 */
	private static HashMap<String, String> watchList = new HashMap<String, String>();
	
	private static int taskId = -2;
	
	private static JavaPlugin plugin;
	
	private static long updateRate = 2000;

	
	public static void setPlugin(JavaPlugin pplugin) {
		plugin = pplugin;
	}
	
	public static void setUpdateRate(long gupdateRate) {
		updateRate = gupdateRate;
	}
	
	/**
	 * Start the scheduled task to update the compasses
	 * @return if the task is now running
	 */
	public static boolean start() {
		if(!isRunning()) {
			taskId = plugin.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(
						plugin,
						new CompassTrackerUpdater(),
						40L,
						updateRate
				);
			if(isRunning()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if a task is running.
	 * @return if its running
	 */
	public static boolean isRunning() {
		return (taskId > 0);
	}
	
	/**
	 * Stops a task if one is active
	 */
	public static void stop() {
		if(isRunning()) {
			plugin.getServer().getScheduler().cancelTask(taskId);
		}
		taskId = -2;
	}
	
	/**
	 * Sets a watcher and the watched player, starts the task if not running.
	 * @param watcher The watcher player
	 * @param watched The watched player
	 */
	public static void setWatcher(Player watcher, Player watched) {
		watchList.put(watcher.getName(), watched.getName());
		start();
	}
	
	/**
	 * Removes a player from the watchList, watchers as well as watched
	 * @param player
	 */
	public static void removePlayer(Player player) {
		String name = player.getName();
		// remove watched players
		// TODO: send notification to watcher when the watched leaves
		if(watchList.containsValue(name)) {
			Iterator<String> iterator = watchList.values().iterator();
			while(iterator.hasNext()) {
				if(iterator.next() == name) {
					iterator.remove();
				}
			}
		}
		
		removeWatcher(player);
	}
	
	/**
	 * Removes a watcher from the watchList
	 * @param player
	 */
	public static void removeWatcher(Player player) {
		String name = player.getName();
		
		// remove watcher player
		if(watchList.containsKey(name)) {
			watchList.remove(name);
		}

		
		if(watchList.isEmpty()) {
			stop();
		}
	}

	@Override
	public void run() {
		Server server = plugin.getServer();
		for( Map.Entry<String, String> entry : watchList.entrySet() )
		{
			Player watcher = server.getPlayer(entry.getKey());
			Player watched = server.getPlayer(entry.getValue());
			watcher.setCompassTarget(watched.getLocation());
			watcher.saveData();
		}	
	}
	
}
