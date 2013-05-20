package de.philworld.bukkit.compassex;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
		if (!p.hasPermission(permission))
			throw new PermissionException();
	}

	static void sendMessage(Player p, String message) {
		p.sendMessage(GREEN + "[CompassEx]" + WHITE + " " + message);
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
