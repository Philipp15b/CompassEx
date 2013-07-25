package de.philworld.bukkit.compassex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.philworld.bukkit.compassex.command.Command;
import de.philworld.bukkit.compassex.command.CommandContext;
import de.philworld.bukkit.compassex.persistence.Persistable;
import de.philworld.bukkit.compassex.util.PermissionException;

public class HidingComponent extends Component implements Persistable, Listener {

	private final Set<String> hiddenPlayers = new HashSet<String>(2);

	public HidingComponent(CompassEx plugin) {
		super(plugin);

		help("hide", "Hide from being tracked", "compassex.hide");
		help("hidden", "Are you hidden?", "compassex.hide");

		load();

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	private void load() {
		File f = new File(plugin.getDataFolder(), "hidden.db.txt");
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line;
			while ((line = in.readLine()) != null) {
				hiddenPlayers.add(line);
			}
			in.close();
		} catch (FileNotFoundException e) {
			// if the file doesn't exist yet, just don't read it
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not read hidden players database!", e);
		}
	}

	@Override
	public void save() {
		File f = new File(plugin.getDataFolder(), "hidden.db.txt");
		List<String> hp;
		synchronized (hiddenPlayers) {
			hp = new ArrayList<String>(hiddenPlayers);
		}
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			for (String player : hp) {
				out.write(player + "\n");
			}
			out.close();
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save hidden players database!", e);
		}
	}

	public boolean isHidden(Player p) {
		return hiddenPlayers.contains(p.getName());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("compassex.autohide") && !isHidden(event.getPlayer())) {
			synchronized (hiddenPlayers) {
				hiddenPlayers.add(event.getPlayer().getName());
			}
		}
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "hide" }, permission = "compassex.hide")
	public void hide(CommandContext context, Player p) throws PermissionException {
		if (context.arg1.equalsIgnoreCase("on")) {
			hideOn(context, p);
		} else if (context.arg1.equalsIgnoreCase("off")) {
			hideOff(context, p);
		} else {
			if (!hiddenPlayers.contains(p.getName())) {
				synchronized (hiddenPlayers) {
					hiddenPlayers.add(p.getName());
				}
				sendMessage(p, "You are now hidden.");
			} else {
				synchronized (hiddenPlayers) {
					hiddenPlayers.remove(p.getName());
				}
				sendMessage(p, "You are now visible again.");
			}
		}
	}

	@SuppressWarnings("unused")
	@Command(aliases = "hon", permission = "compassex.hide")
	public void hideOn(CommandContext context, Player p) {
		if (hiddenPlayers.contains(p.getName())) {
			sendMessage(p, "You are already hidden!");
			return;
		}
		synchronized (hiddenPlayers) {
			hiddenPlayers.add(p.getName());
		}
		sendMessage(p, "You are now hidden.");
	}

	@SuppressWarnings("unused")
	@Command(aliases = "hoff", permission = "compassex.hide")
	public void hideOff(CommandContext context, Player p) {
		if (!hiddenPlayers.contains(p.getName())) {
			sendMessage(p, "You are already trackable!");
			return;
		}
		synchronized (hiddenPlayers) {
			hiddenPlayers.add(p.getName());
		}
		sendMessage(p, "You are now trackable.");
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "hidden" }, permission = "compassex.hide")
	public void hidden(CommandContext context, Player p) throws PermissionException {
		if (hiddenPlayers.contains(p.getName())) {
			sendMessage(p, "You are hidden right now.");
		} else {
			sendMessage(p, "You are trackable right now.");
		}
	}

}
