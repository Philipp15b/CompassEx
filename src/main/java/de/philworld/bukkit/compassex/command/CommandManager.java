package de.philworld.bukkit.compassex.command;

import static org.bukkit.ChatColor.RED;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.philworld.bukkit.compassex.util.PermissionException;

public class CommandManager implements CommandExecutor {

	private final Map<String, Method> commands = new HashMap<String, Method>();
	private final Map<Method, Object> instances = new HashMap<Method, Object>();
	private final Logger logger;

	public CommandManager(Logger logger) {
		this.logger = logger;
	}

	public void registerMethod(Object instance, Method method) {
		Class<?>[] params = method.getParameterTypes();
		if (params.length != 2 || !params[0].equals(CommandContext.class) || !params[1].equals(Player.class))
			throw new IllegalArgumentException(
					"Command methods must have the signature method(CommandContext context, Player p)!");

		instances.put(method, instance);

		Command annotation = method.getAnnotation(Command.class);
		for (String alias : annotation.aliases()) {
			commands.put(alias, method);
		}
	}

	public void register(Object obj) {
		Class<? extends Object> clazz = obj.getClass();
		for (Method method : clazz.getMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				registerMethod(obj, method);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Please use only in game!");
			return true;
		}
		Player p = (Player) sender;

		String base = args.length > 0 ? args[0].toLowerCase() : "";
		if (!commands.containsKey(base))
			return false;
		Method method = commands.get(base);
		Command annotation = method.getAnnotation(Command.class);

		if (!annotation.permission().equals("") && !p.hasPermission(annotation.permission())) {
			p.sendMessage(RED + PermissionException.DEFAULT_MESSAGE);
			return true;
		}

		try {
			method.invoke(instances.get(method), new CommandContext(command, label, args), p);
			return true;
		} catch (IllegalAccessException e) {
			logger.log(Level.SEVERE, "Failed to execute command", e);
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "Failed to execute command", e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof PermissionException) {
				((PermissionException) e.getCause()).send(p);
				return true;
			}
			logger.log(Level.SEVERE, "Failed to execute command", e.getCause());
		}
		p.sendMessage(RED + "An error occured while performing this command.");
		return true;
	}

}
