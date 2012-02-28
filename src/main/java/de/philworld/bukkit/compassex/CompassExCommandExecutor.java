package de.philworld.bukkit.compassex;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompassExCommandExecutor implements CommandExecutor {

	private CompassEx plugin;

	public CompassExCommandExecutor(CompassEx plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {

		// Determine if the sender is a player (and an op), or the console.
		boolean isPlayer = (sender instanceof Player);

		// Cast the sender to Player if possible.
		Player p = (isPlayer) ? (Player) sender : null;

		// no usage from the console cuz we use the player all the time.
		if (!isPlayer) {
			sender.sendMessage("Please only use in game!");
			return true;
		}

		// Grab the command base and any arguments.
		String base = (args.length > 0) ? args[0].toLowerCase() : "";
		String arg1 = (args.length > 1) ? args[1].toLowerCase() : "";
		String arg2 = (args.length > 2) ? args[2].toLowerCase() : "";
		String arg3 = (args.length > 3) ? args[3].toLowerCase() : "";

		// ------------------
		// HELP COMMAND
		// ------------------
		if (base.equals("") || base.equalsIgnoreCase("help")) {
			if (p.hasPermission("compassex.help")) {
				p.sendMessage(ChatColor.GOLD + " ------ CompassEx Help ------ ");

				for (Map.Entry<String, String> entry : CompassEx.helpMessages
						.entrySet()) {
					String permission = entry.getKey();
					String message = entry.getValue();

					permission = permission.replace("%", "");

					message = message.replace("&blue;", "" + ChatColor.BLUE)
							.replace("&red;", "" + ChatColor.RED)
							.replace("&command;", commandLabel);

					if (p.hasPermission(permission))
						p.sendMessage(message);
				}
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// ------------------
		// RESET COMMAND
		// ------------------
		if (base.equalsIgnoreCase("reset") || base.equalsIgnoreCase("spawn")) {
			if (p.hasPermission("compassex.reset")) {

				CompassTrackerUpdater.removeWatcher(p);
				p.setCompassTarget(p.getWorld().getSpawnLocation());
				p.saveData();

				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Your compass has been reset to spawn.");
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// ------------------
		// HERE COMMAND
		// ------------------
		if (base.equalsIgnoreCase("here")) {

			if (p.hasPermission("compassex.here")) {

				CompassTrackerUpdater.removeWatcher(p);
				p.setCompassTarget(p.getLocation());
				p.saveData();

				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Your compass has been set to here.");
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// ------------------
		// DIRECTION COMMANDS
		// ------------------

		// north
		if (base.equalsIgnoreCase("north") || base.equalsIgnoreCase("n")) {

			if (p.hasPermission("compassex.direction")) {

				CompassTrackerUpdater.removeWatcher(p);

				p.setCompassTarget(new Location(p.getWorld(),
						-Double.MAX_VALUE, 0, 0));
				p.saveData();

				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Your compass has been set north.");
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// east
		if (base.equalsIgnoreCase("east") || base.equalsIgnoreCase("e")) {

			if (p.hasPermission("compassex.direction")) {

				CompassTrackerUpdater.removeWatcher(p);

				p.setCompassTarget(new Location(p.getWorld(), 0, 0,
						-Double.MAX_VALUE));
				p.saveData();

				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Your compass has been set east.");
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// south
		if (base.equalsIgnoreCase("south") || base.equalsIgnoreCase("s")) {

			if (p.hasPermission("compassex.direction")) {

				CompassTrackerUpdater.removeWatcher(p);

				p.setCompassTarget(new Location(p.getWorld(), Double.MAX_VALUE,
						0, 0));
				p.saveData();

				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Your compass has been set south.");
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// west
		if (base.equalsIgnoreCase("west") || base.equalsIgnoreCase("w")) {

			if (p.hasPermission("compassex.direction")) {

				CompassTrackerUpdater.removeWatcher(p);

				p.setCompassTarget(new Location(p.getWorld(), 0, 0,
						Double.MAX_VALUE));
				p.saveData();

				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Your compass has been set west.");
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// ------------------
		// LIVE COMMAND
		// ------------------
		if (base.equalsIgnoreCase("live")) {
			if (p.hasPermission("compassex.live")) {

				List<Player> foundPlayers = plugin.getServer()
						.matchPlayer(arg1);

				if (foundPlayers.size() == 1) {
					Player target = foundPlayers.get(0);

					// If the player is hidden dont track it. But only if the
					// user has no admin rights.
					if (isHidden(target)
							&& !(p.hasPermission("compassex.admin"))) {
						p.sendMessage(ChatColor.RED
								+ "[CompassEx] Player cannot be found.");
						return true;
					}

					CompassTrackerUpdater.setWatcher(p, target);

					p.sendMessage(ChatColor.RED
							+ "[CompassEx] Your compass is now pointing live to "
							+ target.getDisplayName() + ".");

				} else {
					p.sendMessage(ChatColor.RED
							+ "[CompassEx] Player cannot be found.");
				}
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// ------------------
		// HEIGHT COMMAND
		// ------------------
		if (base.equalsIgnoreCase("h") || base.equalsIgnoreCase("height")) {
			if (p.hasPermission("compassex.height")) {
				int diff = (int) Math.ceil(p.getCompassTarget().getBlockY()
						- p.getLocation().getY());

				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Height difference between you and your compass target: "
						+ diff + " blocks.");
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}
			return true;
		}

		// ------------------
		// HIDE COMMAND
		// ------------------
		if (base.equalsIgnoreCase("hide")) {

			if (p.hasPermission("compassex.hide")) {
				if (!isHidden(p)) {
					hide(p);
					p.sendMessage(ChatColor.RED
							+ "[CompassEx] You are now hidden.");
				} else {
					unHide(p);
					p.sendMessage(ChatColor.RED
							+ "[CompassEx] You are now visible again.");
				}
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// ------------------
		// HIDDEN COMMAND
		// ------------------
		if (base.equalsIgnoreCase("hidden")) {
			if (p.hasPermission("compassex.hide")) {
				if (isHidden(p)) {
					p.sendMessage(ChatColor.RED
							+ "[CompassEx] You are hidden right now.");
				} else {
					p.sendMessage(ChatColor.RED
							+ "[CompassEx] You are trackable right now.");
				}
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// ------------------
		// DEATH POINT COMMAND
		// ------------------
		if (base.equalsIgnoreCase("deathpoint") || base.equalsIgnoreCase("dp")
				|| base.equalsIgnoreCase("death")) {
			if (p.hasPermission("compassex.deathpoint")) {
				CompassTrackerUpdater.removeWatcher(p);

				Location deathPoint = plugin.deathPoints.get(p.getName());
				if (deathPoint == null) {
					p.sendMessage(ChatColor.RED
							+ "[CompassEx] Could not find your latest death point.");
					return true;
				}
				p.setCompassTarget(deathPoint);
				p.saveData();
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// ------------------
		// POS COMMAND
		// ------------------
		// in addition to /compass pos <x> <y> <z>, it also allows /compass <x>
		// <y> <z>
		if (base.equalsIgnoreCase("pos") || arg2 != "") {

			if (p.hasPermission("compassex.pos")) {

				int x;
				int y;
				int z;
				try {
					if (base.equalsIgnoreCase("pos")) {

						if (arg3 == "") {
							p.sendMessage(ChatColor.RED
									+ "[CompassEx] Wrong arguments: /compass pos <x> <y> <z>.");
							return true;
						}

						x = Integer.parseInt(arg1);
						y = Integer.parseInt(arg2);
						z = Integer.parseInt(arg3);
					} else {
						x = Integer.parseInt(base);
						y = Integer.parseInt(arg1);
						z = Integer.parseInt(arg2);
					}
				} catch (NumberFormatException e) {
					p.sendMessage(ChatColor.RED
							+ "[CompassEx] Wrong argument format: /compass pos <x> <y> <z>.");
					return true;
				}

				// create a new location object from the parameters
				Location point = new Location(p.getWorld(), x, y, z);

				CompassTrackerUpdater.removeWatcher(p);
				p.setCompassTarget(point);
				p.saveData();

				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Your compass has been set to X: " + x
						+ " Y: " + y + " Z: " + z + ".");

			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}

		// ------------------
		// PLAYER COMMAND
		// ------------------
		// if no special command is used, it must be a player
		// like in /compass Philipp15b
		// /compass player Philipp15b is also allowed, in case somebody's
		// name is one of the commands.
		if (p.hasPermission("compassex.player")) {
			String name = base;

			// fallback for /compass player <playername>
			if (base.equalsIgnoreCase("player")) {
				if (arg1 == "") {
					return false;
				}
				name = arg1;
			}

			List<Player> foundPlayers = plugin.getServer().matchPlayer(name);

			if (foundPlayers.size() == 1) {
				Player target = foundPlayers.get(0);

				// If the player is hidden dont track it. But only if the
				// user has no admin rights.
				if (isHidden(target) && !(p.hasPermission("compassex.admin"))) {
					p.sendMessage(ChatColor.RED + "Player cannot be found.");
					return true;
				}

				CompassTrackerUpdater.removeWatcher(p);
				p.setCompassTarget(target.getLocation());
				p.saveData();

				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Your compass is now pointing to "
						+ target.getDisplayName() + ".");

			} else {
				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Player cannot be found.");
			}
		} else {
			if (base == "player" && arg1 != "")
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			else
				return false;
		}

		return true;

	}

	/**
	 * Hide the given player if it is not already hidden.
	 * 
	 * @param player
	 */
	private void hide(Player player) {
		if (!isHidden(player)) {
			plugin.hiddenPlayers.add(player.getName());
		}
	}

	/**
	 * Unhide the given player if it is not already visible.
	 * 
	 * @param player
	 */
	private void unHide(Player player) {
		if (isHidden(player)) {
			plugin.hiddenPlayers.remove(player.getName());
		}
	}

	/**
	 * Returns if the player is hidden.
	 * 
	 * @param player
	 * @return boolean if the player is hidden.
	 */
	private boolean isHidden(Player player) {
		return plugin.hiddenPlayers.contains(player.getName());
	}

}
