package de.philworld.bukkit.compassex.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class HelpManager {

	public static class Entry {
		public final String message;
		public final String permission;

		public Entry(String message, String permission) {
			this.message = message;
			this.permission = permission;
		}

		public String formatMessage(String commandLabel) {
			return message.replace("COMMAND", commandLabel);
		}
	}

	private final List<Entry> messages = new ArrayList<Entry>();

	public void add(String name, String value, String permission) {
		String message = ChatColor.RED + "/COMMAND " + name + ChatColor.BLUE
				+ " " + value;
		messages.add(new Entry(message, permission));
	}

	public Entry get(int index) {
		return messages.get(index);
	}

	public int size() {
		return messages.size();
	}

}
