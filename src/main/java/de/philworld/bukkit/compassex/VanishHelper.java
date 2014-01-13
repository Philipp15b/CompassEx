package de.philworld.bukkit.compassex;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPlugin;

public class VanishHelper {

	public static VanishHelper get() {
		try {
			VanishPlugin plugin = (VanishPlugin) Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket");
			if (plugin == null)
				return null;
			return new VanishHelper(plugin);
		} catch (NoClassDefFoundError e) {
			return null;
		}
	}

	private final VanishManager vanish;

	private VanishHelper(VanishPlugin plugin) {
		this.vanish = plugin.getManager();
	}

	public boolean isVanished(Player p) {
		return vanish.isVanished(p);
	}

}
