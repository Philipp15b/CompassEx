package de.philworld.bukkit.compassex;

@SuppressWarnings("serial")
public class PermissionException extends Exception {
	public PermissionException() {
		super("You don't have any permissions to do that!");
	}
	
	public PermissionException(String message) {
		super(message);
	}
}
