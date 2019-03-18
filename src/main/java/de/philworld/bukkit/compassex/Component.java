package de.philworld.bukkit.compassex;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.philworld.bukkit.compassex.util.BlockLocation;
import de.philworld.bukkit.compassex.util.PermissionException;

abstract class Component {

	protected final CompassEx plugin;

	public Component(CompassEx plugin) {
		this.plugin = plugin;
	}

	public CompassEx getPlugin() {
		return plugin;
	}

	protected void help(String syntax, String description, String permission) {
		plugin.helpManager.add(syntax, description, permission);
	}

	protected static void requirePermission(Player p, String permission) throws PermissionException {
		if (!p.hasPermission("compassex." + permission))
			throw new PermissionException();
	}

	static void sendMessage(Player p, String message) {
		p.sendMessage(GREEN + "[CompassEx]" + WHITE + " " + message);
	}

	protected static void sendCoords(Player p, BlockLocation loc) {
		sendMessage(p, "(X: " + BLUE + loc.x + WHITE + " Y: " + BLUE + loc.y + WHITE + " Z: " + BLUE + loc.z + WHITE
				+ ")");
	}

	protected static void sendCoords(Player p, Location loc) {
		sendMessage(p, "(X: " + BLUE + loc.getBlockX() + WHITE + " Y: " + BLUE + loc.getBlockY() + WHITE + " Z: "
				+ BLUE + loc.getBlockZ() + WHITE + ")");
	}

	protected void setTarget(Player p, Location loc) {
		if (plugin.tracking != null)
			plugin.tracking.removeWatcher(p);
		p.setCompassTarget(loc);
		p.saveData();
	}

	protected boolean withdraw(Player p, double amount) {
		if (amount != 0 && CompassEx.economy != null) {
			if (!CompassEx.economy.bankWithdraw(p.getName(), amount).transactionSuccess()) {
				p.sendMessage(RED + "You don't have " + CompassEx.economy.format(amount) + " to pay this action!");
				return false;
			}
		}
		return true;
	}

}
