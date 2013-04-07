package de.philworld.bukkit.compassex.command;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.WHITE;

import java.util.ArrayList;
import java.util.List;

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
		String message = GRAY + "/COMMAND " + name + WHITE + " " + value;
		messages.add(new Entry(message, permission));
	}

	public Entry get(int index) {
		return messages.get(index);
	}

	public int size() {
		return messages.size();
	}

}
