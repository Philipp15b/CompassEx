package de.philworld.bukkit.compassex.persistence;


public class PersisterTask implements Runnable {

	private final Persistable[] persistables;

	public PersisterTask(Persistable... persistables) {
		this.persistables = persistables;
	}

	@Override
	public void run() {
		for (Persistable p : persistables) {
			p.save();
		}
	}

}
