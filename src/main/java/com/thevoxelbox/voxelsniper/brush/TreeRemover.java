package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vMessage;

public class TreeRemover extends Brush {
	
	private static final int[] treeIds = { 17, 18 };
	private int dx, dy, dz;
	
	public TreeRemover() {
		name = "treeremover";
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
					
					Block tempBlock = v.getWorld().getBlockAt(new Location(tb.getWorld(), tb.getX() + x - (Integer) (dx / 2), tb.getY() + y - (Integer) (dy / 2), tb.getZ() + z - (Integer) (dz / 2)));
					
					for (Integer i : treeIds) {
						if (tempBlock.getTypeId() == i)
							tempBlock.setTypeId(0);
						
						Block blockBelow = v.getWorld().getBlockAt(new Location(v.getWorld(), tempBlock.getLocation().getX(), tempBlock.getLocation().getY() - 1, tempBlock.getLocation().getZ()));
						if (blockBelow.getTypeId() == 3)
							blockBelow.setTypeId(2);	
					}
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
            v.sendMessage(ChatColor.GOLD + "Tree Remover Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b tr dim [x] [y] [z] -- set dimension to remove trees size by x, y, and z.");
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
