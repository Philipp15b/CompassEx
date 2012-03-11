package de.philworld.bukkit.compassex;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompassExCommandExecutor implements CommandExecutor {

	private CompassEx plugin;
	private LocationsYaml locations;

	public CompassExCommandExecutor(CompassEx plugin) {
		this.plugin = plugin;
		this.locations = plugin.getSavedLocations();
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
				
				int page;
				try {
					page = Integer.parseInt(arg1);
				} catch(NumberFormatException e) {
					page = 1;
				}

				Set<Entry<String, String>> entries = CompassEx.helpMessages.entrySet();
				Object[] entryArray = entries.toArray();
				

				int total = entries.size();
				int totalPerPage = 10;
				int totalPages = total / totalPerPage + 1;

				if(page > totalPages) {
					p.sendMessage(ChatColor.RED + "[CompassEx] Help page " + page + " does not exist.");
					return true;
				}

				int startIndex = (page - 1) * totalPerPage;
				int endIndex = startIndex + totalPerPage;
				
				p.sendMessage(ChatColor.GOLD + " ------ CompassEx Help (" + page + "/" + totalPages + ") ------ ");
				
				for (int i = startIndex; i < endIndex && i < entryArray.length; i++) {
					Entry<?, ?> entry = (Entry<?, ?>) entryArray[i];
					String permission = entry.getKey().toString();
					String message = entry.getValue().toString();

					permission = permission.replace("%", "");

					message = message.replace("&blue;", "" + ChatColor.BLUE)
							.replace("&red;", "" + ChatColor.RED)
							.replace("&command;", commandLabel);

					if (p.hasPermission(permission))
						p.sendMessage(message);
				}
				
				if(page < totalPages) {
					p.sendMessage(ChatColor.RED + "To see the next page, type: " + ChatColor.WHITE + "/" + commandLabel + " help " + (page + 1));
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
		else if (base.equalsIgnoreCase("reset") || base.equalsIgnoreCase("spawn")) {
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
		else if (base.equalsIgnoreCase("here")) {

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
		// BED COMMAND
		// ------------------
		else if (base.equalsIgnoreCase("bed")) {

			if (p.hasPermission("compassex.bed")) {


				if (p.getBedSpawnLocation() != null) {
					CompassTrackerUpdater.removeWatcher(p);

					p.setCompassTarget(p.getBedSpawnLocation());

					p.saveData();

					p.sendMessage(ChatColor.RED
							+ "[CompassEx] Your compass has been set to your bed.");
				} else {
					p.sendMessage(ChatColor.RED
							+ "You haven't slept in a bed before, didnt you?");
				}

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
		else if (base.equalsIgnoreCase("north") || base.equalsIgnoreCase("n")) {

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
		else if (base.equalsIgnoreCase("east") || base.equalsIgnoreCase("e")) {

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
		else if (base.equalsIgnoreCase("south") || base.equalsIgnoreCase("s")) {

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
		else if (base.equalsIgnoreCase("west") || base.equalsIgnoreCase("w")) {

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
		else if (base.equalsIgnoreCase("live")) {
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
		else if (base.equalsIgnoreCase("h") || base.equalsIgnoreCase("height")) {
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
		else if (base.equalsIgnoreCase("hide")) {

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
		else if (base.equalsIgnoreCase("hidden")) {
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
		else if (base.equalsIgnoreCase("deathpoint") || base.equalsIgnoreCase("dp")
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
		// SAVE COMMAND
		// ------------------
		else if (base.equalsIgnoreCase("save")) {

			if (p.hasPermission("compassex.save")) {


				boolean here = false;
				String id;
				if(arg1.equalsIgnoreCase("here")) {
					// executed /compass save here <id>
					here = true;
					id = arg2;
				}
				else if(arg1.equalsIgnoreCase("compass")) {
					// executed /compass save compass <id>
					here = false;
					id = arg1;
				}
				else {
					// executed /compass save <id>
					here = false;
					id = arg1;
				}

				if(id.isEmpty()) {
					p.sendMessage(ChatColor.RED
							+ "[CompassEx] Expected an ID: ");
					p.sendMessage("/compass save <id>");
					p.sendMessage("/compass save here <id>");
					return true;
				}

				if(locations.hasPrivateLocation(id) || locations.hasPublicLocation(id)) {
					// already exists
					p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \"" + id + "\" already exists.");
					return true;
				}

				Location loc;
				if(here) {
					// save current location
					loc = p.getLocation();

					p.sendMessage(ChatColor.RED
							+ "[CompassEx] Your current location saved as " + ChatColor.WHITE + "\"" + id + "\"" + ChatColor.RED + ".");
					p.sendMessage(ChatColor.RED + "To set your compass to this location, ");
					p.sendMessage(ChatColor.RED + "type: " + ChatColor.WHITE + "/" + commandLabel + " load " + id);
				}
				else {
					// save current compass target
					loc = p.getCompassTarget();

					p.sendMessage(ChatColor.RED
							+ "[CompassEx] Your current compass target has been saved as " + ChatColor.WHITE + "\"" + id + "\"" + ChatColor.RED + ".");
					p.sendMessage(ChatColor.RED + "To set your compass to that location again later, ");
					p.sendMessage(ChatColor.RED + "type: " + ChatColor.WHITE + "/" + commandLabel + " load " + id);
				}
				OwnedLocation result = new OwnedLocation(id, p.getName(), loc);
				locations.setPrivateLocation(result);
				locations.save();


			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}


		// ------------------
		// LOAD COMMAND
		// ------------------
		else if (base.equalsIgnoreCase("load")) {

			if (p.hasPermission("compassex.load")) {

				if(arg1.isEmpty()) {
					p.sendMessage(ChatColor.RED + "[CompassEx] Expected an ID: /compass load <id>");
					return true;
				}

				// get private location from file
				OwnedLocation location = locations.getPrivateLocation(arg1);
				
				if(location == null) {
					// private not exist,
					// get public location from file
					location = locations.getPublicLocation(arg1);
				}
				else if(!location.getPlayerName().equals(p.getName())) {
					// private does exist
					// but is not owner
					p.sendMessage(ChatColor.RED + "Compass target \"" + location.getId() + "\" is private.");
					return true;
				}

				if(location == null) {
					// specified target id does not exist
					p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \"" + arg1 + "\" does not exist.");
					return true;
				}

				// private/public location found

				// remove watcher because compass-target is about to change
				CompassTrackerUpdater.removeWatcher(p);
				// change compass target
				Location loc = location.getLocation();
				p.setCompassTarget(loc);
				p.saveData();


				p.sendMessage(ChatColor.RED
						+ "[CompassEx] Your compass has been set to " + ChatColor.WHITE + "\"" + arg1 + "\"" + ChatColor.RED + ".");
				p.sendMessage(ChatColor.RED + "(X: " + ChatColor.WHITE + loc.getBlockX() +
						ChatColor.RED + " Y: " + ChatColor.WHITE + loc.getBlockY() + ChatColor.RED + " Z: " + ChatColor.WHITE + loc.getBlockZ() + ChatColor.RED +  ")");
			} else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}

			return true;
		}


		// ------------------
		// REMOVE COMMAND
		// ------------------
		else if (base.equalsIgnoreCase("remove")) {

			boolean hasPublicPerm = p.hasPermission("compassex.remove.public");
			boolean hasPrivatePerm = p.hasPermission("compassex.remove.private");

			// check permissions early
			if(!hasPublicPerm && !hasPrivatePerm) {
				p.sendMessage(ChatColor.RED
						+ "You don't have permission to remove public/private compass targets.");
				return true;
			}

			if(arg1.isEmpty()) {
				p.sendMessage(ChatColor.RED + "[CompassEx] Expected an ID: /compass remove <id>");
				return true;
			}

			OwnedLocation location = locations.getPublicLocation(arg1);
			// is it a public location?
			if(location != null) {
				if(location.getPlayerName().equals(p.getName())) {
					if(!hasPublicPerm) {
						p.sendMessage(ChatColor.RED
								+ "You don't have permission to remove public compass targets.");
						return true;
					}
				}
				else {
					if(!p.hasPermission("compassex.remove.public.any")) {
						p.sendMessage(ChatColor.RED + "You don't have permission to remove other players' public compass targets.");
						return true;
					}
				}
				locations.clearPublicLocation(arg1);
				locations.save();
				p.sendMessage(ChatColor.RED + "[CompassEx] Public compass target \"" + arg1 + "\" removed.");
			}
			else {
				location = locations.getPrivateLocation(arg1);

				// is it a private location?
				if(location != null) {
					if(location.getPlayerName().equals(p.getName())) {
						if(!hasPrivatePerm) {
							p.sendMessage(ChatColor.RED + "You don't have permission to remove private compass targets.");
							return true;
						}
					}
					else {
						if(!p.hasPermission("compassex.remove.private.any")) {
							p.sendMessage(ChatColor.RED + "You don't have permission to remove other players' private compass targets.");
							return true;
						}
					}
					locations.clearPrivateLocation(arg1);
					locations.save();
					p.sendMessage(ChatColor.RED + "[CompassEx] Private compass target \"" + arg1 + "\" removed.");
				}
				else {
					// does not exist
					p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \"" + arg1 + "\" does not exist.");
				}
			}

			return true;
		}

		// ------------------
		// PRIVATE COMMAND
		// ------------------
		else if (base.equalsIgnoreCase("private") || base.equalsIgnoreCase("privatize")) {
			if (p.hasPermission("compassex.privatize")) {

				if(arg1.isEmpty()) {
					p.sendMessage(ChatColor.RED + "[CompassEx] Expected an ID: /compass privatize <id>");
					return true;
				}

				OwnedLocation location = locations.getPublicLocation(arg1);
				if(location != null) {
					if(!location.getPlayerName().equals(p.getName()) && !p.hasPermission("compassex.privatize.any")) {
						p.sendMessage(ChatColor.RED + "You don't have permission to privatize other players' compass targets.");
						return true;
					}
					locations.makePrivate(arg1);
					locations.save();
					p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \"" + arg1 + "\" is now private!");
				}
				else {
					p.sendMessage(ChatColor.RED + "[CompassEx] Public compass target \"" + arg1 + "\" does not exist.");
				}
			}
			else {
				// no permission
				p.sendMessage(ChatColor.RED
						+ "You don't have permission to privatize public compass targets.");
			}
		}


		// ------------------
		// PUBLIC COMMAND
		// ------------------
		else if (base.equalsIgnoreCase("public") || base.equalsIgnoreCase("publicize")) {
			if (p.hasPermission("compassex.publicize")) {

				if(arg1.isEmpty()) {
					p.sendMessage(ChatColor.RED + "[CompassEx] Expected an ID: /compass publicize <id>");
					return true;
				}

				OwnedLocation location = locations.getPrivateLocation(arg1);
				if(location != null) {
					if(!location.getPlayerName().equals(p.getName()) && !p.hasPermission("compassex.publicize.any")) {
						p.sendMessage(ChatColor.RED + "You don't have permission to publicize other players' compass targets.");
						return true;
					}
					locations.makePublic(arg1);
					locations.save();
					p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \"" + arg1 + "\" is now public!");
				}
				else {
					p.sendMessage(ChatColor.RED + "[CompassEx] Private compass target \"" + arg1 + "\" does not exist.");
				}
			}
			else {
				// no permission
				p.sendMessage(ChatColor.RED
						+ "You don't have permission to publicize public compass targets.");
			}
		}

		// ------------------
		// INFO COMMAND
		// ------------------
		else if (base.equalsIgnoreCase("info")) {

			if (p.hasPermission("compassex.info")) {

				OwnedLocation location;
				Location loc;
				boolean isPublic = false;
				if(arg1.isEmpty()) {

					loc = p.getCompassTarget();
					p.sendMessage(ChatColor.RED + "[CompassEx] Current compass target info:");
				}
				else {
					// get private location from file
					location = locations.getPrivateLocation(arg1);
					
					if(location == null) {
						// private not exist,
						// get public location from file
						location = locations.getPublicLocation(arg1);
						if(location != null) {
							isPublic = true;
							p.sendMessage(ChatColor.RED + "[CompassEx] Public compass target \"" + location.getId() + "\" info:");
						}
					}
					else {
						p.sendMessage(ChatColor.RED + "[CompassEx] Private compass target \"" + location.getId() + "\" info:");
					}

					if(location == null) {
						// specified target id does not exist
						p.sendMessage(ChatColor.RED + "[CompassEx] Compass target \"" + arg1 + "\" does not exist.");
						return true;
					}
					
					loc = location.getLocation();
					
					p.sendMessage(ChatColor.RED + "Owned by: " + ChatColor.WHITE + location.getPlayerName());
					if(!isPublic && !location.getPlayerName().equals(p.getName()) && !p.hasPermission("compassex.info.any")) {
						return false;
					}
				}

				

				
				// private/public/compass-target location found
				// show info
				p.sendMessage(ChatColor.RED + loc.getWorld().getName() + " (X: " + ChatColor.WHITE + loc.getBlockX() + ChatColor.RED
						+ " Y: " + ChatColor.WHITE + loc.getBlockY() + ChatColor.RED + " Z: " + ChatColor.WHITE + loc.getBlockZ() + ChatColor.RED + ")");

			}
			else {
				p.sendMessage(ChatColor.RED
						+ "You don't have any permission to do that.");
			}
		}


		// ------------------
		// LIST COMMAND
		// ------------------
		else if (base.equalsIgnoreCase("list")) {

			if (p.hasPermission("compassex.list")) {

				boolean showPublic = false;
				String pageArg;
				if(arg1.equalsIgnoreCase("public")) {
					// show public locations
					showPublic = true;
					pageArg = arg2;
				}
				else if(arg1.equalsIgnoreCase("private")) {
					// show private locations
					showPublic = false;
					pageArg = arg2;
				}
				else {
					// default, show public locations
					showPublic = true;
					pageArg = arg1;
				}


				int page;
				try {
					page = Integer.parseInt(pageArg);
				}
				catch(NumberFormatException e) {
					page = 1;
				}

				Set<String> locSet;
				if(showPublic) {
					locSet = locations.getPublicLocationIds();
				}
				else {
					if(p.hasPermission("compassex.list.any")) {
						locSet = locations.getPrivateLocationIds();
					}
					else {
						locSet = locations.getPrivateOwnedLocationIds(p.getName());
					}
					
				}

				int total = locSet.size();
				int totalPerPage = 10;
				int totalPages = total / totalPerPage + 1;

				if(page > totalPages) {
					p.sendMessage(ChatColor.RED + "[CompassEx] Page " + page + " doesn't exist in list of " + (showPublic?"public":"private") + " locations.");
					return true;
				}

				int startIndex = (page - 1) * totalPerPage;
				int endIndex = startIndex + totalPerPage;

				String[] locArray = locSet.toArray(new String[locSet.size()]);
				p.sendMessage(ChatColor.RED + "[CompassEx] " + (showPublic?"Public":"Private") + " compass target list (page " + page + "/" + totalPages + ")");

				if(total == 0) {
					p.sendMessage(ChatColor.RED + "(none)");
				}
				for (int i = startIndex; i < endIndex && i < locArray.length; i++) {
					p.sendMessage(ChatColor.RED + " " + (i + 1) + ": " + ChatColor.WHITE + locArray[i]);
					//TODO maybe also show X,Y,Z world
				}
				p.sendMessage(ChatColor.RED + "See " + (showPublic?"private":"public") + " compass target list: " + ChatColor.WHITE + "/" + commandLabel + " list " + (showPublic?"private":"public"));
				if(page < totalPages) {
					p.sendMessage(ChatColor.RED + "See the next page: " + ChatColor.WHITE + "/" + commandLabel + " list " + (showPublic?"public":"private") + " " + (page + 1));
				}
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
		else if (base.equalsIgnoreCase("pos") || arg2 != "") {

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
		else {
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
