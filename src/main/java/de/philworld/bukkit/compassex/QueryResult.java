package de.philworld.bukkit.compassex;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

public interface QueryResult {

	public OwnedLocation get();

	public boolean isPublic();

	public boolean notifyIfNotFound(Player p);

	public static class NotFoundResult implements QueryResult {

		private final String owner;
		private final String id;

		NotFoundResult(String owner, String id) {
			this.owner = owner;
			this.id = Preconditions.checkNotNull(id);
		}

		@Override
		public OwnedLocation get() {
			return null;
		}

		@Override
		public boolean isPublic() {
			return false;
		}

		@Override
		public boolean notifyIfNotFound(Player p) {
			if (owner == null) {
				Component.sendMessage(p, "Could not find public location with id " + BLUE + id + WHITE + "!");
			} else if (owner.equals(p.getName())) {
				Component.sendMessage(p, "Could not find a private location with id " + BLUE + id + WHITE
						+ " owned by you!");
			} else {
				Component.sendMessage(p, "Could not find a private location with id " + BLUE + id + WHITE
						+ " owned by " + BLUE + owner + WHITE + "!");
			}
			return true;
		}

	}

	public static class FoundResult implements QueryResult {

		private final OwnedLocation location;
		private final boolean isPublic;

		FoundResult(OwnedLocation location, boolean isPublic) {
			this.location = location;
			this.isPublic = isPublic;
		}

		@Override
		public OwnedLocation get() {
			return location;
		}

		@Override
		public boolean isPublic() {
			return isPublic;
		}

		@Override
		public boolean notifyIfNotFound(Player p) {
			return false;
		}

	}

}
