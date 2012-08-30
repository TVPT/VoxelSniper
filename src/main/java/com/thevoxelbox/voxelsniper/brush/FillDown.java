/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class FillDown extends PerformBrush {

    private int bsize;

    public FillDown() {
        name = "Fill Down";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bsize = v.brushSize;
        fillDown(tb);
        v.storeUndo(current.getUndo());
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bsize = v.brushSize;
        fillDown(lb);
        v.storeUndo(current.getUndo());
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        //vm.voxel();
    }
    double trueCircle = 0;

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Fill Down Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b fd true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("true")) {
                trueCircle = 0.5;
                v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            } else if (par[x].startsWith("false")) {
                trueCircle = 0;
                v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    private void fillDown(Block b) {
        bx = b.getX();
        by = b.getY();
        bz = b.getZ();

        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int x = 0 - bsize; x <= bsize; x++) {
            double xpow = Math.pow(x, 2);
            for (int z = 0 - bsize; z <= bsize; z++) {
                if (xpow + Math.pow(z, 2) <= bpow) {
                    if (w.getBlockTypeIdAt(bx + x, by, bz + z) == 0) { //why is this if statement here?  You don't want to fill anything in the whole column if there is a single block at the level of your disc?  Are you sure? -gavjenks
                        int y = by;
                        while (--y >= 0) {
                            if (w.getBlockTypeIdAt(bx + x, y, bz + z) != 0) {
                                break;
                            }
                        }
                        for (int yy = y; yy <= by; yy++) {
                            Block bl = clampY(bx + x, yy, bz + z);
                            current.perform(bl);
                        }
                    }
                }
            }
        }
    }
    
    private static int timesUsed = 0;
	
    @Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
