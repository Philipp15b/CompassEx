package de.philworld.bukkit.compassex;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompassExCommandExecutor implements CommandExecutor {

	private final CompassEx plugin;
	private final LocationsYaml locations;

	public CompassExCommandExecutor(CompassEx plugin) {
		this.plugin = plugin;
		this.locations = plugin.getSavedLocations();
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		boolean isPlayer = (sender instanceof Player);
		Player p = (isPlayer) ? (Player) sender : null;

		if (!isPlayer) {
			sender.sendMessage("Please only use in game!");
			return true;
		}

		String base = (args.length > 0) ? args[0].toLowerCase() : "";
		String arg1 = (args.length > 1) ? args[1].toLowerCase() : "";
		String arg2 = (args.length > 2) ? args[2].toLowerCase() : "";
		String arg3 = (args.length > 3) ? args[3].toLowerCase() : "";

		try {
			if (base.equals("") || base.equals("help")) {
				help(p, commandLabel, arg1);
			} else if (base.equals("reset") || base.equals("spawn")) {
				reset(p);
			} else if (base.equals("here")) {
				here(p);
			} else if (base.equals("bed")) {
				bed(p);
			} else if (base.equals("north") || base.equals("n")) {
				direction(p, Direction.NORTH);
			} else if (base.equals("east") || base.equals("e")) {
				direction(p, Direction.EAST);
			} else if (base.equals("south") || base.equals("s")) {
				direction(p, Direction.SOUTH);
			} else if (base.equals("west") || base.equals("w")) {
				direction(p, Direction.WEST);
			} else if (base.equals("live")) {
				live(p, arg1);
			} else if (base.equals("h") || base.equals("height")) {
				height(p);
			} else if (base.equals("hide")) {
				hide(p);
			} else if (base.equals("hidden")) {
				hidden(p);
			} else if (base.equals("deathpoint") || base.equals("dp")
					|| base.equals("death")) {
				deathpoint(p);
			} else if (base.equals("save")) {
				save(p, commandLabel, arg1, arg2);
			} else if (base.equals("load")) {
				load(p, arg1);
			} else if (base.equals("remove")) {
				remove(p, arg1);
			} else if (base.equals("private") || base.equals("privatize")) {
				privatize(p, arg1);
			} else if (base.equals("public") || base.equals("publicize")) {
				publicize(p, arg1);
			} else if (base.equals("info")) {
				info(p, arg1);
			} else if (base.equals("list")) {
				list(p, commandLabel, arg1, arg2);
			}
			// in addition to /compass pos <x> <y> <z>, it also allows
			// /compass <x> <y> <z>
			else if (base.equals("pos") || !arg2.isEmpty()) {
				position(p, base, arg1, arg2, arg3);
			}
			// if no special command is used, it must be a player
			// like in /compass Philipp15b
			// /compass player Philipp15b is also allowed, in case somebody's
			// name is one of the commands.
			else {
				return player(p, base, arg1);
			}
		} catch (PermissionException e) {
			p.sendMessage(ChatColor.RED + e.getMessage());
		}
		return true;
	}

	private void help(Player p, String commandLabel, String arg1)
			throws PermissionException {
		if (!p.hasPermission("compassex.help"))
			throw new PermissionException();

		int page;
		try {
			page = Integer.parseInt(arg1);
		} catch (NumberFormatException e) {
			page = 1;
		}

		int total = CompassEx.helpMessages.size();
		int totalPages = total / plugin.helpPageNumCommands + 1;

		if (page > totalPages) {
			p.sendMessage(ChatColor.RED + "[CompassEx] Help page " + page
					+ " does not exist.");
			return;
		}

		int startIndex = (page - 1) * plugin.helpPageNumCommands;
		int endIndex = startIndex + plugin.helpPageNumCommands;

		p.sendMessage(ChatColor.GOLD + " ------ CompassEx Help (" + page + "/"
				+ totalPages + ") ------ ");

		for (int i = startIndex; i < endIndex && i < total; i++) {
			CommandHelpProvider.Entry entry = CompassEx.helpMessages.get(i);
			String message = entry.formatMessage(commandLabel);

			if (p.hasPermission(entry.permission))
				p.sendMessage(message);
		}

		if (page < totalPages) {
			p.sendMessage(ChatColor.RED + "To see the next page, type: "
					+ ChatColor.WHITE + "/" + commandLabel + " help "
					+ (page + 1));
		}
	}

	private void reset(Player p) throws PermissionException {
		if (!p.hasPermission("compassex.reset"))
			throw new PermissionException();
		setTarget(p, p.getWorld().getSpawnLocation());
		p.sendMessage(ChatColor.RED
				+ "[CompassEx] Your compass has been reset to spawn.");
	}

	private void here(Player p) throws PermissionException {
		if (!p.hasPermission("compassex.here"))
			throw new PermissionException();
		setTarget(p, p.getLocation());
		p.sendMessage(ChatColor.RED
				+ "[CompassEx] Your compass has been set to your current location.");
	}

	private void bed(Player p) throws PermissionException {
		if (!p.hasPermission("compassex.bed"))
			throw new PermissionException();

		if (p.getBedSpawnLocation() != null) {
			setTarget(p, p.getBedSpawnLocation());
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Your compass has been set to your bed.");
		} else {
			p.sendMessage(ChatColor.RED
					+ "You haven't slept in a bed before, have you?");
		}
	}

	private void direction(Player p, Direction dir) throws PermissionException {
		if (!p.hasPermission("compassex.direction"))
			throw new PermissionException();
		setTarget(p, dir.getVector().toLocation(p.getWorld()));
		p.sendMessage(ChatColor.RED + "[CompassEx] Your compass has been set "
				+ dir.getName() + ".");
	}

	private void live(Player p, String arg1) throws PermissionException {
		if (!p.hasPermission("compassex.live"))
			throw new PermissionException();
		List<Player> foundPlayers = plugin.getServer().matchPlayer(arg1);

		if (foundPlayers.size() == 1) {
			Player target = foundPlayers.get(0);

			if (plugin.isHidden(target)
					&& !(p.hasPermission("compassex.admin"))) {
				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Player cannot be found.");
				return;
			}

			try {
				plugin.trackerUpdater.setWatcher(p, target);
			} catch (IllegalArgumentException e) {
				p.sendMessage(ChatColor.RED + "[CompassEx] " + e.getMessage());
				return;
			}

			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Your compass is now pointing live to "
					+ target.getDisplayName() + ".");
		} else {
			p.sendMessage(ChatColor.RED + "[CompassEx] Player cannot be found.");
		}
	}

	private void height(Player p) throws PermissionException {
		if (!p.hasPermission("compassex.height"))
			throw new PermissionException();
		int diff = (int) Math.ceil(p.getCompassTarget().getBlockY()
				- p.getLocation().getY());

		p.sendMessage(ChatColor.RED
				+ "[CompassEx] Height difference between you and your compass target: "
				+ diff + " blocks.");
	}

	private void hide(Player p) throws PermissionException {
		if (!p.hasPermission("compassex.hide"))
			throw new PermissionException();
		if (!plugin.isHidden(p)) {
			plugin.hide(p);
			p.sendMessage(ChatColor.RED + "[CompassEx] You are now hidden.");
		} else {
			plugin.unhide(p);
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] You are now visible again.");
		}
	}

	private void hidden(Player p) throws PermissionException {
		if (!p.hasPermission("compassex.hide"))
			throw new PermissionException();
		if (plugin.isHidden(p)) {
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] You are hidden right now.");
		} else {
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] You are trackable right now.");
		}
	}

	private void deathpoint(Player p) throws PermissionException {
		if (!p.hasPermission("compassex.deathpoint"))
			throw new PermissionException();
		Location deathPoint = plugin.deathPoints.get(p.getName());
		if (deathPoint == null) {
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Could not find your latest death point.");
			return;
		}
		setTarget(p, deathPoint);
	}

	private void save(Player p, String commandLabel, String arg1, String arg2)
			throws PermissionException {
		if (!p.hasPermission("compassex.save"))
			throw new PermissionException();
		boolean here = false;
		String id;
		if (arg1.equals("here")) {
			// executed /compass save here <id>
			here = true;
			id = arg2;
		} else {
			// executed /compass save <id>
			id = arg1;
		}

		if (id.isEmpty()) {
			p.sendMessage(ChatColor.RED + "[CompassEx] Expected an ID: ");
			p.sendMessage("/compass save <id>");
			p.sendMessage("/compass save here <id>");
			return;
		}

		if (locations.hasPrivateLocation(id) || locations.hasPublicLocation(id)) {
			p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \"" + id
					+ "\" already exists.");
			return;
		}

		if (!withdraw(p, plugin.saveCost))
			return;

		Location loc;
		if (here) {
			// save current location
			loc = p.getLocation();

			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Your current location saved as "
					+ ChatColor.WHITE + "\"" + id + "\"" + ChatColor.RED + ".");
			p.sendMessage(ChatColor.RED
					+ "To set your compass to this location, ");
			p.sendMessage(ChatColor.RED + "type: " + ChatColor.WHITE + "/"
					+ commandLabel + " load " + id);
		} else {
			// save current compass target
			loc = p.getCompassTarget();

			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Your current compass target has been saved as "
					+ ChatColor.WHITE + "\"" + id + "\"" + ChatColor.RED + ".");
			p.sendMessage(ChatColor.RED
					+ "To set your compass to that location again later, ");
			p.sendMessage(ChatColor.RED + "type: " + ChatColor.WHITE + "/"
					+ commandLabel + " load " + id);
		}
		locations.setPrivateLocation(new OwnedLocation(id, p.getName(), loc));
		locations.save();
	}

	private void load(Player p, String arg1) throws PermissionException {
		if (!p.hasPermission("compassex.load"))
			throw new PermissionException();

		if (arg1.isEmpty()) {
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Expected an ID: /compass load <id>");
			return;
		}

		OwnedLocation location = locations.getPrivateLocation(arg1);
		if (location == null) {
			location = locations.getPublicLocation(arg1);
		} else if (!location.getPlayerName().equals(p.getName())) {
			p.sendMessage(ChatColor.RED + "Compass target \""
					+ location.getId() + "\" is private.");
			return;
		}
		if (location == null) {
			p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \""
					+ arg1 + "\" does not exist.");
			return;
		}

		Location loc = location.getLocation();
		setTarget(p, loc);
		p.sendMessage(ChatColor.RED
				+ "[CompassEx] Your compass has been set to " + ChatColor.WHITE
				+ "\"" + arg1 + "\"" + ChatColor.RED + ".");
		p.sendMessage(ChatColor.RED + "(X: " + ChatColor.WHITE
				+ loc.getBlockX() + ChatColor.RED + " Y: " + ChatColor.WHITE
				+ loc.getBlockY() + ChatColor.RED + " Z: " + ChatColor.WHITE
				+ loc.getBlockZ() + ChatColor.RED + ")");
	}

	private void remove(Player p, String arg1) throws PermissionException {
		boolean hasPublicPerm = p.hasPermission("compassex.remove.public");
		boolean hasPrivatePerm = p.hasPermission("compassex.remove.private");

		if (!hasPublicPerm && !hasPrivatePerm)
			throw new PermissionException();

		if (arg1.isEmpty()) {
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Expected an ID: /compass remove <id>");
			return;
		}

		OwnedLocation location = locations.getPublicLocation(arg1);
		if (location != null) {
			if (location.getPlayerName().equals(p.getName()) && !hasPublicPerm) {
				throw new PermissionException(
						"You don't have permission to remove public compass targets.");
			}
			if (!p.hasPermission("compassex.remove.public.any")) {
				throw new PermissionException(
						"You don't have permission to remove other players' public compass targets.");
			}
			locations.clearPublicLocation(arg1);
			locations.save();
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Public compass target \"" + arg1
					+ "\" removed.");
		} else {
			location = locations.getPrivateLocation(arg1);
			if (location != null) {
				if (location.getPlayerName().equals(p.getName())
						&& !hasPrivatePerm) {
					throw new PermissionException(
							"You don't have permission to remove private compass targets.");
				}
				if (!p.hasPermission("compassex.remove.private.any")) {
					throw new PermissionException(
							"You don't have permission to remove other players' private compass targets.");
				}
				locations.clearPrivateLocation(arg1);
				locations.save();
				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Private compass target \"" + arg1
						+ "\" removed.");
			} else {
				p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \""
						+ arg1 + "\" does not exist.");
			}
		}
	}

	private void privatize(Player p, String arg1) throws PermissionException {
		if (!p.hasPermission("compassex.privatize"))
			throw new PermissionException();

		if (arg1.isEmpty()) {
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Expected an ID: /compass privatize <id>");
			return;
		}

		OwnedLocation location = locations.getPublicLocation(arg1);
		if (location != null) {
			if (!location.getPlayerName().equals(p.getName())
					&& !p.hasPermission("compassex.privatize.any")) {
				throw new PermissionException(
						"You don't have permission to privatize other players' compass targets.");
			}

			if (!withdraw(p, plugin.privatizeCost))
				return;

			locations.makePrivate(arg1);
			locations.save();
			p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \""
					+ arg1 + "\" is now private!");
		} else {
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Public compass target \"" + arg1
					+ "\" does not exist.");
		}
	}

	private void publicize(Player p, String arg1) throws PermissionException {
		if (!p.hasPermission("compassex.publicize"))
			throw new PermissionException();

		if (arg1.isEmpty()) {
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Expected an ID: /compass publicize <id>");
			return;
		}

		OwnedLocation location = locations.getPrivateLocation(arg1);
		if (location != null) {
			if (!location.getPlayerName().equals(p.getName())
					&& !p.hasPermission("compassex.publicize.any")) {
				throw new PermissionException(
						"You don't have permission to publicize other players' compass targets.");
			}

			if (!withdraw(p, plugin.publicizeCost))
				return;

			locations.makePublic(arg1);
			locations.save();
			p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \""
					+ arg1 + "\" is now public!");
		} else {
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Private compass target \"" + arg1
					+ "\" does not exist.");
		}
	}

	private void info(Player p, String arg1) throws PermissionException {
		if (!p.hasPermission("compassex.info"))
			throw new PermissionException();

		OwnedLocation location;
		Location loc;
		boolean isPublic = false;
		if (arg1.isEmpty()) {
			loc = p.getCompassTarget();
			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Current compass target info:");
		} else {
			location = locations.getPrivateLocation(arg1);
			if (location == null) {
				location = locations.getPublicLocation(arg1);
				if (location != null) {
					isPublic = true;
					p.sendMessage(ChatColor.RED
							+ "[CompassEx] Public compass target \""
							+ location.getId() + "\" info:");
				}
			} else {
				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Private compass target \""
						+ location.getId() + "\" info:");
			}

			if (location == null) {
				// specified target id does not exist
				p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \""
						+ arg1 + "\" does not exist.");
				return;
			}

			loc = location.getLocation();

			p.sendMessage(ChatColor.RED + "Owned by: " + ChatColor.WHITE
					+ location.getPlayerName());
			if (!isPublic && !location.getPlayerName().equals(p.getName())
					&& !p.hasPermission("compassex.info.any")) {
				return;
			}
		}

		// private/public/compass-target location found
		// show info
		p.sendMessage(ChatColor.RED + loc.getWorld().getName() + " (X: "
				+ ChatColor.WHITE + loc.getBlockX() + ChatColor.RED + " Y: "
				+ ChatColor.WHITE + loc.getBlockY() + ChatColor.RED + " Z: "
				+ ChatColor.WHITE + loc.getBlockZ() + ChatColor.RED + ")");
	}

	private void list(Player p, String commandLabel, String arg1, String arg2)
			throws PermissionException {
		if (!p.hasPermission("compassex.list"))
			throw new PermissionException();

		boolean showPublic;
		String pageArg;
		if (arg1.equalsIgnoreCase("public")) {
			// show public locations
			showPublic = true;
			pageArg = arg2;
		} else if (arg1.equalsIgnoreCase("private")) {
			// show private locations
			showPublic = false;
			pageArg = arg2;
		} else {
			// default, show public locations
			showPublic = true;
			pageArg = arg1;
		}

		int page;
		try {
			page = Integer.parseInt(pageArg);
		} catch (NumberFormatException e) {
			page = 1;
		}

		Set<String> locSet;
		if (showPublic) {
			locSet = locations.getPublicLocationIds();
		} else {
			if (p.hasPermission("compassex.list.any")) {
				locSet = locations.getPrivateLocationIds();
			} else {
				locSet = locations.getPrivateOwnedLocationIds(p.getName());
			}
		}

		int total = locSet.size();
		int totalPerPage = 10;
		int totalPages = total / totalPerPage + 1;

		if (page > totalPages) {
			p.sendMessage(ChatColor.RED + "[CompassEx] Page " + page
					+ " doesn't exist in list of "
					+ (showPublic ? "public" : "private") + " locations.");
			return;
		}

		int startIndex = (page - 1) * totalPerPage;
		int endIndex = startIndex + totalPerPage;

		String[] locArray = locSet.toArray(new String[locSet.size()]);
		p.sendMessage(ChatColor.RED + "[CompassEx] "
				+ (showPublic ? "Public" : "Private")
				+ " compass target list (page " + page + "/" + totalPages + ")");

		if (total == 0) {
			p.sendMessage(ChatColor.RED + "(none)");
		}
		for (int i = startIndex; i < endIndex && i < locArray.length; i++) {
			p.sendMessage(ChatColor.RED + " " + (i + 1) + ": "
					+ ChatColor.WHITE + locArray[i]);
			// TODO maybe also show X,Y,Z world
		}
		p.sendMessage(ChatColor.RED + "See "
				+ (showPublic ? "private" : "public")
				+ " compass target list: " + ChatColor.WHITE + "/"
				+ commandLabel + " list " + (showPublic ? "private" : "public"));
		if (page < totalPages) {
			p.sendMessage(ChatColor.RED + "See the next page: "
					+ ChatColor.WHITE + "/" + commandLabel + " list "
					+ (showPublic ? "public" : "private") + " " + (page + 1));
		}
	}

	private void position(Player p, String base, String arg1, String arg2,
			String arg3) {
		if (p.hasPermission("compassex.pos")) {

			int x, y, z;
			try {
				if (base.equalsIgnoreCase("pos")) {

					if (arg3.isEmpty()) {
						p.sendMessage(ChatColor.RED
								+ "[CompassEx] Wrong arguments: /compass pos <x> <y> <z>.");
						return;
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
				return;
			}

			setTarget(p, new Location(p.getWorld(), x, y, z));

			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Your compass has been set to X: " + x
					+ " Y: " + y + " Z: " + z + ".");

		} else {
			p.sendMessage(ChatColor.RED
					+ "You don't have any permission to do that.");
		}
	}

	private boolean player(Player p, String base, String arg1)
			throws PermissionException {
		if (!p.hasPermission("compassex.player")) {
			// this is also called for /compass <playername>
			// so if the player does not have permission for this
			// show him that the command does not exist instead of
			// throwing a PermissionException.
			if (base.equals("player") && !arg1.isEmpty())
				throw new PermissionException();
			return false;
		}

		// fallback for /compass player <playername>
		String name = base.equals("player") ? arg1 : base;
		List<Player> foundPlayers = plugin.getServer().matchPlayer(name);

		if (foundPlayers.size() == 1
				&& (!plugin.isHidden(foundPlayers.get(0)) || p
						.hasPermission("compassex.admin"))) {
			Player target = foundPlayers.get(0);

			setTarget(p, target.getLocation());

			p.sendMessage(ChatColor.RED
					+ "[CompassEx] Your compass is now pointing to "
					+ target.getDisplayName() + ".");

		} else {
			p.sendMessage(ChatColor.RED + "[CompassEx] Player cannot be found.");
		}
		return true;
	}

	private void setTarget(Player p, Location loc) {
		plugin.trackerUpdater.removeWatcher(p);
		p.setCompassTarget(loc);
		p.saveData();
	}

	private boolean withdraw(Player p, double amount) {
		if (amount != 0 && CompassEx.economy != null
				&& !p.hasPermission("compassex.save.free")) {
			if (!CompassEx.economy.bankWithdraw(p.getName(), plugin.saveCost)
					.transactionSuccess()) {
				p.sendMessage(ChatColor.RED + "You don't have "
						+ CompassEx.economy.format(plugin.saveCost)
						+ " to pay this action!");
				return false;
			}
		}
		return true;
	}
}
