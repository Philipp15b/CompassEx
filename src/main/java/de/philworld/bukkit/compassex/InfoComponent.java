package de.philworld.bukkit.compassex;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.philworld.bukkit.compassex.command.Command;
import de.philworld.bukkit.compassex.command.CommandContext;
import de.philworld.bukkit.compassex.util.PermissionException;

public class InfoComponent extends Component {

	public InfoComponent(CompassEx plugin) {
		super(plugin);

		help("info [ID]", "See the coordinates of your current compass target, or a saved location.", "compassex.info");
		help("height [ID]", "Height diff between you and the target", "compassex.height");
		help("distance [ID]", "Distance to your target", "compassex.distance");
	}

	@Command(aliases = { "info" }, permission = "compassex.info")
	public void info(CommandContext context, Player p) throws PermissionException {
		Location loc = null;
		if (!context.arg1.isEmpty() && plugin.saving != null) {
			QueryResult result = plugin.saving.queryLocation(p, context.arg1, context.arg2);

			if (result != null) {
				if (result.notifyIfNotFound(p))
					return;

				sendMessage(p, "Private compass target " + BLUE + result.get().id + WHITE + " info:");

				loc = result.get().toLocation();

				if (!result.get().isOwnedBy(p))
					sendMessage(p, "Owned by: " + BLUE + result.get().id);
			}
		}

		if (loc == null) {
			loc = p.getCompassTarget();
			sendMessage(p, "Current compass target info:");
		}

		sendMessage(p, BLUE + loc.getWorld().getName() + WHITE + " (X: " + BLUE + loc.getBlockX() + WHITE + " Y: "
				+ BLUE + loc.getBlockY() + WHITE + " Z: " + BLUE + loc.getBlockZ() + WHITE + ")");

		Vector current = p.getLocation().toVector();
		if (p.hasPermission("compassex.distance"))
			sendMessage(p, "Distance: " + BLUE + (int) Math.ceil(current.subtract(loc.toVector()).length()) + WHITE
					+ " blocks.");
	}

	@Command(aliases = { "height", "h" }, permission = "compassex.height")
	public void height(CommandContext context, Player p) throws PermissionException {
		Location loc = queryLocationEx(p, context.arg1, context.arg2);
		if (loc == null)
			return;
		int diff = (int) Math.ceil(p.getCompassTarget().getY() - p.getLocation().getY());
		sendMessage(p, "Height difference: " + diff + " blocks.");
	}

	@Command(aliases = { "distance", "d" }, permission = "compassex.distance")
	public void distance(CommandContext context, Player p) throws PermissionException {
		Location loc = queryLocationEx(p, context.arg1, context.arg2);
		if (loc == null)
			return;
		Vector current = p.getLocation().toVector();
		Vector target = loc.toVector();
		int distance = (int) Math.ceil(current.distance(target));
		sendMessage(p, "Distance: " + distance + " blocks.");
	}

	private Location queryLocationEx(Player p, String arg1, String arg2) throws PermissionException {
		if (!arg1.isEmpty() && plugin.saving != null) {
			QueryResult result = plugin.saving.queryLocation(p, arg1, arg2);
			if (result != null) {
				if (result.notifyIfNotFound(p))
					return null;
				return result.get().toLocation();
			}
		}
		return p.getCompassTarget();
	}

}
