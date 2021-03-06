package de.philworld.bukkit.compassex;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.WHITE;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.philworld.bukkit.compassex.command.Command;
import de.philworld.bukkit.compassex.command.CommandContext;
import de.philworld.bukkit.compassex.command.HelpManager.Entry;
import de.philworld.bukkit.compassex.util.Direction;
import de.philworld.bukkit.compassex.util.PermissionException;

public class GeneralComponent extends Component {

	private final int helpPageNumCommands;

	public GeneralComponent(CompassEx plugin) {
		super(plugin);

		helpPageNumCommands = plugin.getConfig().getInt("help-page-num-commands", 7);

		help("reset", "Reset back to spawn", "compassex.reset");
		help("here", "Set to your current position", "compassex.here");
		help("bed", "Set to your bed", "compassex.bed");
		help("north/east/south/west", "Set to a direction.", "compassex.direction");
		help("X Y Z", "Set to coordinates", "compassex.pos");
		help("PLAYERNAME", "Set to a player", "compassex.player");
		help("live", "Set to a player's pos & update", "compassex.live");
	}

	@Command(aliases = { "help", "" }, permission = "compassex.help")
	public void help(CommandContext context, Player p) {
		int page;
		if (!context.arg1.isEmpty()) {
			try {
				page = Integer.parseInt(context.arg1);
			} catch (NumberFormatException e) {
				sendMessage(p, "Could not parse help page. Please enter a valid number.");
				return;
			}
		} else {
			page = 1;
		}

		int total = plugin.helpManager.size();
		int totalPages = total / helpPageNumCommands + 1;

		if (page > totalPages) {
			sendMessage(p, "Help page " + BLUE + page + WHITE + " does not exist.");
			return;
		}

		int startIndex = (page - 1) * helpPageNumCommands;
		int endIndex = startIndex + helpPageNumCommands;

		p.sendMessage(GOLD + " ------ CompassEx Help (" + BLUE + page + GOLD + "/" + totalPages + ") ------ ");

		for (int i = startIndex; i < endIndex && i < total; i++) {
			Entry entry = plugin.helpManager.get(i);
			if (p.hasPermission(entry.permission))
				p.sendMessage(entry.formatMessage(context.label));
		}

		if (page < totalPages) {
			p.sendMessage("To see the next page, type: " + GRAY + "/" + context.label + " help " + (page + 1));
		}
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "reset" }, permission = "compassex.reset")
	public void reset(CommandContext context, Player p) throws PermissionException {
		setTarget(p, p.getWorld().getSpawnLocation());
		sendMessage(p, "Your compass has been reset to spawn.");
		sendCoords(p, p.getWorld().getSpawnLocation());
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "here" }, permission = "compassex.here")
	public void here(CommandContext context, Player p) throws PermissionException {
		setTarget(p, p.getLocation());
		sendMessage(p, "Your compass has been set to your current location.");
		sendCoords(p, p.getLocation());
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "bed" }, permission = "compassex.bed")
	public void bed(CommandContext context, Player p) throws PermissionException {
		if (p.getBedSpawnLocation() != null) {
			setTarget(p, p.getBedSpawnLocation());
			sendMessage(p, "Your compass has been set to your bed.");
			sendCoords(p, p.getBedSpawnLocation());
		} else {
			sendMessage(p, "Could not find your bed!");
		}
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "north", "n" }, permission = "compassex.dir")
	public void north(CommandContext context, Player p) throws PermissionException {
		direction(p, Direction.NORTH);
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "south", "s" }, permission = "compassex.dir")
	public void south(CommandContext context, Player p) throws PermissionException {
		direction(p, Direction.SOUTH);
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "west", "w" }, permission = "compassex.dir")
	public void west(CommandContext context, Player p) throws PermissionException {
		direction(p, Direction.WEST);
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "east", "e" }, permission = "compassex.dir")
	public void east(CommandContext context, Player p) throws PermissionException {
		direction(p, Direction.EAST);
	}

	private void direction(Player p, Direction dir) {
		setTarget(p, dir.getVector().toLocation(p.getWorld()));
		sendMessage(p, "Your compass has been set " + BLUE + dir.getName().toLowerCase() + WHITE + ".");
	}

	@Command(aliases = { "position", "pos" }, permission = "compassex.pos")
	public void position(CommandContext context, Player p) {
		int x, y, z;
		try {
			if (context.base.equals("pos")) {
				if (context.arg2.isEmpty()) {
					sendMessage(p, "Wrong arguments:" + GRAY + "/" + context.label + " pos <x> [y=64] <z>" + WHITE
							+ ".");
					return;
				}

				x = Integer.parseInt(context.arg1);
				y = !context.arg3.isEmpty() ? Integer.parseInt(context.arg2) : 64;
				z = !context.arg3.isEmpty() ? Integer.parseInt(context.arg3) : Integer.parseInt(context.arg2);
			} else {
				x = Integer.parseInt(context.base);
				y = !context.arg2.isEmpty() ? Integer.parseInt(context.arg1) : 64;
				z = !context.arg2.isEmpty() ? Integer.parseInt(context.arg2) : Integer.parseInt(context.arg1);
			}
		} catch (NumberFormatException e) {
			sendMessage(p, "Wrong argument format: " + GRAY + "/" + context.label + " pos <x> [y=64] <z>" + WHITE + ".");
			return;
		}

		Location loc = new Location(p.getWorld(), x, y, z);
		setTarget(p, loc);
		sendMessage(p, "Your compass has been set to:");
		sendCoords(p, loc);
	}

	@Command(aliases = { "player" }, permission = "compassex.player")
	public void player(CommandContext context, Player p) {
		final String name = context.base.equals("player") ? context.arg1 : context.base;

		List<Player> foundPlayers = plugin.getServer().matchPlayer(name);

		if (foundPlayers.size() == 1
				&& (!plugin.hiding.isHidden(foundPlayers.get(0)) || p.hasPermission("compassex.admin"))) {
			Player target = foundPlayers.get(0);

			setTarget(p, target.getLocation());
			sendMessage(p, "Your compass is now pointing to " + BLUE + target.getDisplayName() + WHITE + ".");
			sendCoords(p, target.getLocation());
		} else {
			sendMessage(p, "Player cannot be found.");
		}
	}

}
