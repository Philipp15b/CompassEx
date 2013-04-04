package de.philworld.bukkit.compassex.command;

import org.bukkit.command.Command;

public class CommandContext {

	public final Command command;
	public final String label;
	/**
	 * Lower-cased first argument to the command.
	 */
	public final String base;
	public final String arg1;
	public final String arg2;
	public final String arg3;

	public CommandContext(org.bukkit.command.Command command, String label,
			String[] args) {
		this.command = command;
		this.label = label;
		base = args.length > 0 ? args[0].toLowerCase() : "";
		arg1 = args.length > 1 ? args[1].toLowerCase() : "";
		arg2 = args.length > 2 ? args[2].toLowerCase() : "";
		arg3 = args.length > 3 ? args[3].toLowerCase() : "";
	}

}
