package de.philworld.bukkit.compassex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompassEx extends JavaPlugin {

    Logger log = Logger.getLogger("Minecraft"); 
    FileConfiguration config;
    
    // hashmap to save the ids of the running watch tasks
    private HashMap<String, Integer> watchTasks = new HashMap<String, Integer>();
    
    // save all hidden players in a list
    private List<String> hiddenPlayers = new ArrayList<String>();
    
    
    @Override
    public void onEnable() {
        loadConfiguration();
        
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvent(Event.Type.PLAYER_QUIT, new CompassExPlayerListener(this), Priority.Normal, this);
       
        PluginDescriptionFile pff = this.getDescription();
        log.info(pff.getName() +  " " + pff.getVersion() + " is enabled.");
    }

    @Override
    public void onDisable() {
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
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		// Determine if the sender is a player (and an op), or the console.
        boolean player  = (sender instanceof Player);
        
        // @TODO: remove if not necessary
        // do the bukkit permissions give all rights to ops automatically?
        @SuppressWarnings("unused")
		boolean op      = player && ((Player) sender).isOp();
        
        // Cast the sender to Player if possible.
        Player p = (player) ? (Player)sender : null;
        
        // no usage from the console cuz we use the player all the time.
        if(sender instanceof ConsoleCommandSender) {
        	return false;
        }
        
        
        if (args.length == 0) {
        	sender.sendMessage("You must specify an action! Use /compass <action>.");
        	return false;
        }
        

        // Grab the command base and any arguments.
        String base = args[0].toLowerCase();
        String arg1 = (args.length > 1) ? args[1].toLowerCase() : "";
        String arg2 = (args.length > 2) ? args[2].toLowerCase() : "";
        String arg3 = (args.length > 3) ? args[3].toLowerCase() : "";
        
        
        // ------------------
        // RESET COMMAND
        // ------------------
        if(base.equalsIgnoreCase("reset")) {
        	if(p.hasPermission("compassex.reset")) {
        		
        		stopLiveTask(p);
        		p.setCompassTarget(p.getWorld().getSpawnLocation());
        		p.saveData();
        		
        		p.sendMessage(ChatColor.RED + "[CompassEx] Your compass has been reset to spawn.");
        		return true;
    		}
        }
        
        // ------------------
        // POS COMMAND
        // ------------------
        if(base.equalsIgnoreCase("pos")) {
        	
        	if(p.hasPermission("compassex.pos")) {
        		
        		// Send message if user forgot some arguments
        		if(arg1 == "" || arg2 == "" || arg3 == "") {
            		p.sendMessage(ChatColor.RED + "[CompassEx] Wrong arguments: /compass pos <x> <y> <z>.");
        		
        		} else {
        			
        			// create a new location object from the parameters
        			Location point = new Location(
    					p.getWorld(),
    					Integer.parseInt(arg1),
    					Integer.parseInt(arg2),
    					Integer.parseInt(arg3)
        			);
        			
        			stopLiveTask(p);
        			p.setCompassTarget(point);
        			p.saveData();
        			
        			p.sendMessage(ChatColor.RED + "[CompassEx] Your compass has been set to position(X: " + arg1 + " Y: " + arg2 + " Z: " + arg3 + ").");
        			return true;
        		}
        	}
        	
        }
        
        // ------------------
        // PLAYER COMMAND
        // ------------------
        if(base.equalsIgnoreCase("player")) {
        	if(p.hasPermission("compassex.player")) {
        		
        		List<Player> foundPlayers = getServer().matchPlayer(args[1]);
        		
        		if (foundPlayers.size() == 1)
        		{
        			Player target = foundPlayers.get(0);
        			
        			// If the player is hidden dont track it. But only if the
        			// user has no admin rights.
        			if (isHidden(target) && !(p.hasPermission("compassex.admin"))) {
        				p.sendMessage(ChatColor.RED + "Player cannot be found.");
        				return false;
        			}
        			
        			stopLiveTask(p);
        			p.setCompassTarget(target.getLocation());
        			p.saveData();
        			
        			p.sendMessage(ChatColor.RED + "[CompassEx] Your compass is now pointing to " + target.getDisplayName() + ".");
        			return true;
        		
        		}
        		else
        		{
        			p.sendMessage(ChatColor.RED + "[CompassEx] Player cannot be found.");
        			return false;
        		}
        	}
        }
        
        // ------------------
        // LIVE COMMAND
        // ------------------
        if(base.equalsIgnoreCase("live")) {
        	if(p.hasPermission("compassex.live")) {
        		
        		List<Player> foundPlayers = getServer().matchPlayer(arg1);
        		
        		if (foundPlayers.size() == 1)
        		{
        			Player target = foundPlayers.get(0);
        			
        			// If the player is hidden dont track it. But only if the
        			// user has no admin rights.
        			if (isHidden(target) && !(p.hasPermission("compassex.admin"))) {
        				p.sendMessage(ChatColor.RED + "[CompassEx] Player cannot be found.");
        				return false;
        			}
        			
        			stopLiveTask(p);
        			
        			// Workaround to give arguments to the task (WTH...)
        			class UpdateCompassTask implements Runnable {
    			        Player watcher;
    			        Player target;
    			        
    			        UpdateCompassTask(Player p, Player t) {
    			        	watcher = p;
    			        	target = t;
    			        }
    			        @Override
						public void run() {
    			        	watcher.setCompassTarget(target.getLocation());
    			        	watcher.saveData();
    			        }
    			    }
        			
        			long updateRate = (long) getConfig().getInt("live-update-rate");
                    
        			// set up the task and save it
        			int taskId = getServer()
        					.getScheduler()
        					.scheduleSyncRepeatingTask(
        							this, 
        							new UpdateCompassTask(p, target),
        							40L,
        							updateRate
        					);
        			
        			watchTasks.put(p.getName(), taskId);
        					
        			p.setCompassTarget(target.getLocation());
        			p.saveData();
        			
        			p.sendMessage(ChatColor.RED + "[CompassEx] Your compass is now pointing live to " + target.getDisplayName() + ".");
        			return true;
        		
        		}
        		else
        		{
        			p.sendMessage(ChatColor.RED + "[CompassEx] Player cannot be found.");
        			return false;
        		}
        	}
        }
        
        // ------------------
        // HIDE COMMAND
        // ------------------
        if(base.equalsIgnoreCase("hide")) {
        	if(p.hasPermission("compassex.hide")) {
        		if(isHidden(p)) {
        			hide(p);
            		p.sendMessage(ChatColor.RED + "[CompassEx] You are now hidden.");
        		} else {
        			unHide(p);
        			p.sendMessage(ChatColor.RED + "[CompassEx] You are now visible again.");
        		}
        		return true;
        	}
        }
        
        
        // ------------------
        // HIDDEN COMMAND
        // ------------------
        if(base.equalsIgnoreCase("hidden")) {
        	if(p.hasPermission("compassex.hide")) {
        		if(isHidden(p)) {
        			p.sendMessage(ChatColor.RED + "[CompassEx] You are hidden right now.");
        		} else {
        			p.sendMessage(ChatColor.RED + "[CompassEx] You are trackable right now.");
        		}
        		return true;
        	}
        }

        
        return false;
	}
	
	/**
	 * Stops the LiveCompassTask of a player if it is running.
	 * @param p Player to stop the task of
	 * @return boolean if the live task was found
	 */
	public boolean stopLiveTask(Player p) {
		String playername = p.getName();
		
		// find task, cancel and delete its id if set
		if(watchTasks.containsKey(playername)) {
			getServer().getScheduler().cancelTask(watchTasks.get(playername));
			watchTasks.remove(playername);
			return true;
		}
		return false;
	}
	
	/**
	 * Hide the given player if it is not already hidden.
	 * @param player
	 */
	private void hide(Player player) {
		if(!isHidden(player)) {
			hiddenPlayers.add(player.getName());
		}
	}
	
	/**
	 * Unhide the given player if it is not already visible.
	 * @param player
	 */
	private void unHide(Player player) {
		if (isHidden(player)) {
			hiddenPlayers.remove(player.getName());
		}
	}
	
	/**
	 * Returns if the player is hidden.
	 * @param player
	 * @return boolean if the player is hidden.
	 */
	private boolean isHidden(Player player) {
		return hiddenPlayers.contains(player.getName());		
	}

}
