package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.vMessage;

public class Mixer extends Brush{
	
	private int dx = 5, dy = 5, dz = 5; // Dimensions to mix in
	private Random rand = new Random();
	
	public Mixer() {
		name = "mixer";
	}
	
	@Override
	public void info(vMessage vm) {
		vm.brushName(name);
        vm.voxel();
	}

	@Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        for (int x = 0; x < dx; x++) {
			for (int y = 0; y < dy; y++) {
				for (int z = 0; z < dz; z++) {
					Location l = new Location(tb.getWorld(), tb.getX() + x - (Integer) (dx / 2), tb.getY() + y - (Integer) (dy - 1), tb.getZ() + z - (Integer) (dz / 2));
					
					int random = rand.nextInt(10);
					if (random <= 3 && l.getWorld().getBlockAt(l).getTypeId() != 0)
						l.getWorld().getBlockAt(l).setTypeId(v.voxelId);
				}
			}
		}
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }
    
    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Mixer Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b mixer dim [x] [y] [z] -- set dimension size by x, y, and z.");
        } else if (par[1].equalsIgnoreCase("dim")) {
        	try {
        		dx = Integer.parseInt(par[2]);
        		dy = Integer.parseInt(par[3]);
        		dz = Integer.parseInt(par[4]);
        		
        		v.sendMessage(ChatColor.GREEN + "Dimensions set.");
        		if (dx >= 100 || dy >= 100 || dz >= 100)
        			v.sendMessage(ChatColor.RED + "Warning: One of the dimensions is very large.");
        		
        	} catch (Exception e) {
        		v.sendMessage(ChatColor.RED + "An error occurred. Make sure the parameters are correct.");
        	}
        } else {
        	v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
        }
    }
	
}
