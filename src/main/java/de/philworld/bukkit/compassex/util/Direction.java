package de.philworld.bukkit.compassex.util;

import org.bukkit.util.Vector;

public enum Direction {
	NORTH(new Vector(0, 0, -12550820)),
	EAST(new Vector(12550820, 0, 0)),
	SOUTH(new Vector(0, 0, 12550820)),
	WEST(new Vector(-12550820, 0, 0));
	
	private Vector vec;
	private String name;
	
	Direction(Vector vec) {
		this.vec = vec;
		this.name = name().toLowerCase();
	}
	
	public Vector getVector() {
		return vec;
	}
	
	public String getName() {
		return name;
	}
}
