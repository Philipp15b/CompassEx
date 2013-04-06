package de.philworld.bukkit.compassex;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import de.philworld.bukkit.compassex.command.Command;
import de.philworld.bukkit.compassex.command.CommandContext;
import de.philworld.bukkit.compassex.migrations.Migration2;
import de.philworld.bukkit.compassex.util.PermissionException;

public class SavingComponent extends Component {

	private final double saveCost;
	private final double publicizeCost;
	private final double privatizeCost;

	PrivateLocationManager privateLocations;
	Map<String, OwnedLocation> publicLocations;

	public SavingComponent(CompassEx plugin) {
		super(plugin);

		load();

		saveCost = plugin.getConfig().getDouble("save-cost", 0);
		privatizeCost = plugin.getConfig().getDouble("privatize-cost", 0);
		publicizeCost = plugin.getConfig().getDouble("publicize-cost", 0);

		help("save ID", "Save your current compass target", "compassex.save");
		help("save here ID", "Save your current location", "compassex.save");
		help("remove ID", "Remove an existing location", "compassex.remove");
		help("load ID", "Set a saved location to your compass",
				"compassex.load");
		help("list private|public", "List saved locations.", "compassex.list");
		help("privatize ID", "Make a location private.", "compassex.privatize");
		help("publicize ID", "Make a location public.", "compassex.publicize");
	}

	private void load() {
		if (Migration2.should(plugin)) {
			plugin.getLogger()
					.log(Level.INFO,
							"CompassEx v1/v2 config detected. The files will be loaded and then renamed.");
			Migration2 migration = new Migration2(plugin);
			privateLocations = migration.loadPrivateLocations();
			publicLocations = migration.loadPublicLocations();
			migration.finish();
			plugin.getLogger().log(Level.INFO, "Migration finished.");
			return;
		}

		ConfigurationSerialization.registerClass(OwnedLocation.class);
		ConfigurationSerialization.registerClass(PrivateLocationManager.class);

		File f = new File(plugin.getDataFolder(), "locations.db.yml");
		publicLocations = new HashMap<String, OwnedLocation>();
		if (f.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

			privateLocations = (PrivateLocationManager) config.get("private");
			if (privateLocations == null)
				privateLocations = new PrivateLocationManager();

			ConfigurationSection spublic = config
					.getConfigurationSection("public");
			for (String key : spublic.getKeys(false)) {
				publicLocations.put(key, (OwnedLocation) spublic.get(key));
			}
		} else {
			privateLocations = new PrivateLocationManager();
		}
	}

	public void save() throws IOException {
		ConfigurationSerialization.registerClass(OwnedLocation.class);
		ConfigurationSerialization.registerClass(PrivateLocationManager.class);

		YamlConfiguration config = new YamlConfiguration();
		config.set("private", privateLocations);
		config.set("public", publicLocations);
		config.save(new File(plugin.getDataFolder(), "locations.db.yml"));
	}

	@Command(aliases = { "save" }, permission = "compassex.save")
	public void save(CommandContext context, Player p)
			throws PermissionException {
		boolean here = false;
		String id;
		if (context.arg1.equals("here")) {
			// executed /compass save here <id>
			here = true;
			id = context.arg2;
		} else {
			// executed /compass save <id>
			id = context.arg1;
		}

		if (id.isEmpty()) {
			sendMessage(p, "Expected an ID: ");
			p.sendMessage("/compass save <id>");
			p.sendMessage("/compass save here <id>");
			return;
		}

		if (privateLocations.get(p.getName(), id) != null) {
			sendMessage(p, "You already have a compass target named \"" + id
					+ "\".");
			return;
		}

		if (!p.hasPermission("compassex.save.free") && !withdraw(p, saveCost))
			return;

		Location loc = here ? p.getLocation() : p.getCompassTarget();
		sendMessage(p, "Your current " + (here ? "location" : "compass target")
				+ " has been saved as " + WHITE + "\"" + id + "\"" + RED + ".");
		sendMessage(p, "To set your compass to " + (here ? "this" : "that")
				+ " location again later, ");
		sendMessage(p, "type: " + WHITE + "/" + context.label + " load " + id);
		privateLocations.add(new OwnedLocation(id, p.getName(), loc));
	}

	@Command(aliases = { "load" }, permission = "compassex.load")
	public void load(CommandContext context, Player p)
			throws PermissionException {
		String id = context.arg1;

		if (id.isEmpty()) {
			sendMessage(p, "Expected an ID: /compass load <id>");
			return;
		}

		OwnedLocation location = privateLocations.get(p.getName(), id);
		if (location == null) {
			location = publicLocations.get(id);
		}
		if (location == null) {
			sendMessage(p, "Compass target \"" + id + "\" does not exist.");
			return;
		}

		Location loc = location.getLocation();
		setTarget(p, loc);
		sendMessage(p, "Your compass has been set to " + WHITE + "\"" + id
				+ "\"" + RED + ".");
		p.sendMessage(RED + "(X: " + WHITE + loc.getBlockX() + RED + " Y: "
				+ WHITE + loc.getBlockY() + RED + " Z: " + WHITE
				+ loc.getBlockZ() + RED + ")");
	}

	@Command(aliases = { "remove" })
	public void remove(CommandContext context, Player p)
			throws PermissionException {
		boolean hasPublicPerm = p.hasPermission("compassex.remove.public");
		boolean hasPrivatePerm = p.hasPermission("compassex.remove.private");
		if (!hasPublicPerm && !hasPrivatePerm)
			throw new PermissionException();

		if (context.arg1.isEmpty()) {
			sendMessage(p, "Expected an ID: /compass remove <id>");
			if (p.hasPermission("compassex.remove.private.any"))
				sendMessage(p, "Or: /compass remove <owner> <id>");
			return;
		}

		// /compassex remove <id>
		if (context.arg2.isEmpty()) {
			String id = context.arg1;
			OwnedLocation loc = privateLocations.get(p.getName(), id);
			if (loc == null) {
				sendMessage(p, "Could not find a private location named '" + id
						+ "' owned by you!");
				if (p.hasPermission("compassex.remove.public")
						|| p.hasPermission("compassex.public.any"))
					sendMessage(p,
							"If you want to remove a public location, type "
									+ WHITE + "/compass remove public <id>");
				return;
			}

			if (!p.hasPermission("compassex.remove.private.any")
					&& !(loc.ownedBy(p) && p
							.hasPermission("compassex.remove.private")))
				throw new PermissionException(
						"You're not allowed to remove your locations!");

			privateLocations.remove(p.getName(), id);
			sendMessage(p, "Removed the private location '" + id + "'.");

		} else {
			// /compassex remove public <id>
			if (context.arg2.equalsIgnoreCase("public")) {
				String id = context.arg2;
				OwnedLocation loc = publicLocations.get(id);
				if (loc == null) {
					sendMessage(p, "Could not find public location!");
					return;
				}

				if (!p.hasPermission("compassex.remove.public.any")
						&& !(loc.ownedBy(p) && p
								.hasPermission("compassex.public.private")))
					throw new PermissionException(
							"You're not allowed to remove that location!");

				publicLocations.remove(id);
				sendMessage(p, "Removed the public location '" + id + "'.");
			} else {
				// /compassex remove <owner> <id> or
				// /compassex remove private <owner> <id>

				String owner = context.arg1;
				String id = context.arg2;
				if (context.arg2.equalsIgnoreCase("private")) {
					owner = context.arg2;
					id = context.arg3;
				}

				OwnedLocation loc = privateLocations.get(owner, id);

				if (loc == null) {
					sendMessage(p, "Could not find private location '" + id
							+ "' owned by '" + owner + "'!");
					return;
				}

				if (!p.hasPermission("compassex.remove.private.any")
						&& !(loc.ownedBy(p) && p
								.hasPermission("compassex.remove.private")))
					throw new PermissionException(
							"You may not remove the location!");

				privateLocations.remove(owner, id);
				sendMessage(p, "Removed the private location '" + id + "'.");
			}
		}

	}

	@Command(aliases = { "list", "l" }, permission = "compassex.list")
	public void list(CommandContext context, Player p)
			throws PermissionException {

		boolean showPublic;
		String pageArg;
		if (context.arg1.equalsIgnoreCase("public")) {
			// show public locations
			showPublic = true;
			pageArg = context.arg2;
		} else if (context.arg1.equalsIgnoreCase("private")) {
			// show private locations
			showPublic = false;
			pageArg = context.arg2;
		} else {
			// default, show public locations
			showPublic = true;
			pageArg = context.arg1;
		}

		int page;
		try {
			page = Integer.parseInt(pageArg);
		} catch (NumberFormatException e) {
			page = 1;
		}

		List<String> locations = new ArrayList<String>();
		if (showPublic) {
			for (OwnedLocation loc : publicLocations.values()) {
				locations.add(loc.getId());
			}
		} else {
			boolean any = p.hasPermission("compassex.list.any");
			for (OwnedLocation loc : privateLocations.getLocations(p)) {
				if (any || loc.ownedBy(p))
					locations.add(loc.getId());
			}
		}

		int totalPerPage = 10;
		int totalPages = locations.size() / totalPerPage + 1;

		if (page > totalPages) {
			sendMessage(p, "Page " + page + " doesn't exist in list of "
					+ (showPublic ? "public" : "private") + " locations.");
			return;
		}

		int startIndex = (page - 1) * totalPerPage;
		int endIndex = startIndex + totalPerPage;

		sendMessage(p, (showPublic ? "Public" : "Private")
				+ " compass target list (page " + page + "/" + totalPages + ")");

		if (locations.size() == 0) {
			p.sendMessage(ChatColor.RED + "(none)");
		}
		for (int i = startIndex; i < endIndex && i < locations.size(); i++) {
			p.sendMessage(ChatColor.RED + " " + (i + 1) + ": "
					+ ChatColor.WHITE + locations.get(i));
		}
		p.sendMessage(ChatColor.RED + "See "
				+ (showPublic ? "private" : "public")
				+ " compass target list: " + ChatColor.WHITE + "/"
				+ context.label + " list "
				+ (showPublic ? "private" : "public"));
		if (page < totalPages) {
			p.sendMessage(ChatColor.RED + "See the next page: "
					+ ChatColor.WHITE + "/" + context.label + " list "
					+ (showPublic ? "public" : "private") + " " + (page + 1));
		}
	}

	@Command(
			aliases = { "privatize", "private" },
			permission = "compassex.privatize")
	public void privatize(CommandContext context, Player p)
			throws PermissionException {
		String id = context.arg1;
		if (id.isEmpty()) {
			sendMessage(p, "Expected an ID: /compass privatize <id>");
			return;
		}

		OwnedLocation loc = publicLocations.get(id);
		if (loc == null) {
			sendMessage(p, "Public compass target \"" + id
					+ "\" does not exist.");
			return;
		}

		if (!loc.ownedBy(p) && !p.hasPermission("compassex.privatize.any")) {
			throw new PermissionException(
					"You don't have permission to privatize other players' compass targets.");
		}

		if (!p.hasPermission("compassex.privatize.free")
				&& !withdraw(p, privatizeCost))
			return;

		privateLocations.remove(loc.getPlayerName(), loc.getId());
		publicLocations.put(loc.getId(), loc);
		sendMessage(p, "Compass target \"" + id + "\" is now private!");
	}

	@Command(
			aliases = { "publicize", "public" },
			permission = "compassex.publicize")
	public void publicize(CommandContext context, Player p)
			throws PermissionException {
		String id = context.arg1;
		if (id.isEmpty()) {
			sendMessage(p, "Expected an ID: /compass publicize <id>");
			return;
		}

		OwnedLocation loc = privateLocations.get(p.getName(), id);
		if (loc == null) {
			sendMessage(p, "Private compass target \"" + id
					+ "\" does not exist.");
			return;
		}

		if (!loc.ownedBy(p) && !p.hasPermission("compassex.publicize.any")) {
			throw new PermissionException(
					"You don't have permission to publicize other players' compass targets.");
		}

		if (publicLocations.containsKey(id)) {
			sendMessage(p,
					"A public location with this name already exists. Delete it first!");
			return;
		}

		if (!p.hasPermission("compassex.publicize.free")
				&& !withdraw(p, publicizeCost))
			return;

		privateLocations.remove(loc.getPlayerName(), loc.getId());
		publicLocations.put(loc.getId(), loc);
		sendMessage(p, "Compass target \"" + id + "\" is now public!");
	}

}
