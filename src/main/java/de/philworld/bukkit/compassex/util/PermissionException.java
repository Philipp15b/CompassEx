package de.philworld.bukkit.compassex.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("serial")
public class PermissionException extends Exception {

	public static final String DEFAULT_MESSAGE = "You don't have any permissions to do that!";

	public PermissionException() {
		super(DEFAULT_MESSAGE);
	}

	public PermissionException(String message) {
		super(message);
	}

	public void send(Player p) {
		p.sendMessage(ChatColor.RED + getMessage());
	}
}
