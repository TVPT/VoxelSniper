package com.thevoxelbox.voxelsniper.brush;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * @author Mick
 */
public class Pointless extends Brush {
    private boolean broadcastIt = false;
    private static HashMap<String, String[]> printouts = new HashMap<String, String[]>();
    private String selection = "voxelbox";

    static {
    	Pointless.printouts.put("dachshund", new String[] { "             .--.", " (_______(]6 `-,", " (   ____    /''\"`", " //\\\\   //\\\\",
    	" \"\"  \"\"  \"\"  \"\"" });
    	Pointless.printouts.put("sunset", new String[] { "                           ~,  ^^                       |          ",
    			"                           /|    ^^                  \\ _ /        ", "                          / |\\                    -=  ( )  =-     ",
    	" ~^~ ^ ^~^~ ~^~ ~=====^~^~-~^~~^~^-=~=~=-~^~^~^~" });
    	Pointless.printouts.put("poof", new String[] { "      .( * .", "    . *  .  ) .", "   . . POOF .* .", "    ' * . (  .) '", "     ` ( . *" });
    	Pointless.printouts.put("dog", new String[] { "   |\\_/|", "   |^ ^|      /}", "   ( 0 )\"\"\"\\'", "  8===8     |", "   ||_/=\\\\__|" });
    	Pointless.printouts.put("voxelbox", new String[] { "                             §c_ _               ",
    			"§a /\\   /\\§b___§6__  __§c___| | |__   _____  __", "§a \\ \\ / /§b  _ \\§6 \\/ /§c _ \\ | '_ \\ / _ \\ \\/ /",
    			"§a  \\ V /§b  (_) §6>   <§c  __/ |   |_) | (_)  >   < ", "§a   \\_/ §b\\___/§6_/\\_\\§c__|_|_.__/ \\___/_/\\_\\" });
    }

    private static int timesUsed = 0;

    public Pointless() {
        this.setName("Mind-Numbingly Pointless");
    }
    
    /**
     * 
     * @param v
     */
    private final void printIt(final SnipeData v) {
        if (Pointless.printouts.containsKey(this.selection)) {
            for (final String _str : Pointless.printouts.get(this.selection)) {
                this.printLine(v, _str);
            }
        } else {
            v.sendMessage(ChatColor.RED + "Sorry, this printout does not exist.");
            for (final String _str : Pointless.printouts.keySet()) {
                Bukkit.getServer().broadcastMessage("--- " + _str);
            }
        }
    }

    private final void printLine(final SnipeData v, final String line) {
        // This will eventually parse everything for the colors before printing. Now I just want to get it out there.
        if (this.broadcastIt) {
        	Bukkit.getServer().broadcastMessage(line);
        } else {
            v.sendMessage(line);
        }
    }

    @Override
	protected final void arrow(final SnipeData v) {
    	this.printIt(v);
	}

    @Override
    protected final void powder(final SnipeData v) {
    	this.printIt(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GOLD + "Pointless brushiness! :D :");
    		return;
    	}
    	for (int _i = 1; _i < par.length; _i++) {
    		if (par[_i].equals("broadcast")) {
    			this.broadcastIt = !this.broadcastIt;
    			v.sendMessage("Broadcast mode: " + this.broadcastIt);
    			break;
    		} else {
    			this.selection = par[_i];
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return Pointless.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Pointless.timesUsed = tUsed;
    }
}
