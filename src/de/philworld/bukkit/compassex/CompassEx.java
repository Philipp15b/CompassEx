package de.philworld.bukkit.compassex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompassEx extends JavaPlugin {

    Logger log = Logger.getLogger("Minecraft"); 
    FileConfiguration config;
    
    // save all hidden players in a list
    List<String> hiddenPlayers = new ArrayList<String>();
    
    HashMap<String, Location> deathPoints = new HashMap<String, Location>();
    
    
    @Override
    public void onEnable() {
        loadConfiguration();
        
        PluginManager pm = getServer().getPluginManager();
        
        // set up listener
        CompassExPlayerListener listener = new CompassExPlayerListener(this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, listener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, listener, Priority.Low, this);
       
        // setup compass tracker
        CompassTrackerUpdater.setPlugin(this);
        CompassTrackerUpdater.setUpdateRate((long) getConfig().getInt("live-update-rate"));
        
        // set command executor
        getCommand("compass").setExecutor(new CompassExCommandExecutor(this));
        
        // done.
        PluginDescriptionFile pff = this.getDescription();
        log.info(pff.getName() +  " " + pff.getVersion() + " is enabled.");
    }

    @Override
    public void onDisable() {
    	CompassTrackerUpdater.stop(); // stop tasks
    	
        PluginDescriptionFile pff = this.getDescription();
        log.info(pff.getName() +  " " + pff.getVersion() + " is disabled.");
    }

    /**
     * Loads the configuration and inserts the defaults.
     */
    public void loadConfiguration() {
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
    }

}
