package de.philworld.bukkit.compassex;

import static org.bukkit.ChatColor.RED;
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

		help("info [ID]",
				"See the coordinates of your current compass target, or a saved location.",
				"compassex.info");
		help("height", "Height diff between you and the target",
				"compassex.height");
		help("distance", "Distance to your target", "compassex.distance");
	}

	@Command(aliases = { "info" }, permission = "compassex.info")
	public void info(CommandContext context, Player p)
			throws PermissionException {
		Location loc;
		if (context.arg1.isEmpty() || plugin.saving == null) {
			loc = p.getCompassTarget();
			sendMessage(p, "Current compass target info:");
		} else {
			boolean isPublic = false;
			OwnedLocation owned = plugin.saving.privateLocations.get(
					p.getName(), context.arg1);
			if (owned == null) {
				owned = plugin.saving.publicLocations.get(context.arg1);
				if (owned != null) {
					isPublic = true;
					sendMessage(p, "Public compass target \"" + owned.getId()
							+ "\" info:");
				}
			} else {
				sendMessage(p, "Private compass target \"" + owned.getId()
						+ "\" info:");
			}

			if (owned == null) {
				// specified target id does not exist
				sendMessage(p, "Compass target \"" + context.arg1
						+ "\" does not exist.");
				return;
			}

			loc = owned.getLocation();

			p.sendMessage(RED + "Owned by: " + WHITE + owned.getPlayerName());
			if (!isPublic && !owned.getPlayerName().equals(p.getName())
					&& !p.hasPermission("compassex.info.any")) {
				return;
			}
		}

		// private/public/compass-target location found
		// show info
		p.sendMessage(RED + loc.getWorld().getName() + " (X: " + WHITE
				+ loc.getBlockX() + RED + " Y: " + WHITE + loc.getBlockY()
				+ RED + " Z: " + WHITE + loc.getBlockZ() + RED + ")");
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "height", "h" }, permission = "compassex.height")
	public void height(CommandContext context, Player p)
			throws PermissionException {
		int diff = (int) Math.ceil(p.getCompassTarget().getBlockY()
				- p.getLocation().getY());

		sendMessage(p,
				"Height difference between you and your compass target: "
						+ diff + " blocks.");
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "distance", "d" }, permission = "compassex.distance")
	public void distance(CommandContext context, Player p)
			throws PermissionException {
		Vector current = p.getLocation().toVector();
		Vector target = p.getCompassTarget().toVector();
		int distance = (int) Math.ceil(current.subtract(target).length());

		sendMessage(p, "Distance between you and your compass target: "
				+ distance + " blocks.");
	}

}
