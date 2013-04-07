package de.philworld.bukkit.compassex;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import de.philworld.bukkit.compassex.command.Command;
import de.philworld.bukkit.compassex.command.CommandContext;
import de.philworld.bukkit.compassex.util.PermissionException;

public class HidingComponent extends Component {

	private final Set<String> hiddenPlayers = new HashSet<String>(2);

	public HidingComponent(CompassEx plugin) {
		super(plugin);

		help("hide", "Hide from being tracked", "compassex.hide");
		help("hidden", "Are you hidden?", "compassex.hide");
	}

	public void hide(Player p) {
		hiddenPlayers.add(p.getName());
	}

	public boolean isHidden(Player p) {
		return hiddenPlayers.contains(p.getName());
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "hide" }, permission = "compassex.hide")
	public void hide(CommandContext context, Player p)
			throws PermissionException {
		if (!hiddenPlayers.contains(p.getName())) {
			hiddenPlayers.add(p.getName());
			sendMessage(p, "You are now hidden.");
		} else {
			hiddenPlayers.remove(p.getName());
			sendMessage(p, "You are now visible again.");
		}
	}

	@SuppressWarnings("unused")
	@Command(aliases = { "hidden" }, permission = "compassex.hide")
	public void hidden(CommandContext context, Player p)
			throws PermissionException {
		if (hiddenPlayers.contains(p.getName())) {
			sendMessage(p, "You are hidden right now.");
		} else {
			sendMessage(p, "You are trackable right now.");
		}
	}

}
