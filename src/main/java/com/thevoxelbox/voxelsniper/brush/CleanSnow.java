/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 *
 * @author psanker
 */
public class CleanSnow extends Brush {

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        cleanSnow(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName("Clean Snow");
        vm.size();
    }
    double trueCircle = 0;

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Clean Snow Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b cls true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b cls false will switch back. (false is default)");
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

    public void cleanSnow(vData v) {
        int bsize = v.brushSize;

        vUndo h = new vUndo(tb.getWorld().getName());

        double bpow = Math.pow(bsize + trueCircle, 2);

        for (int y = (bsize + 1) * 2; y >= 0; y--) {
            double ypow = Math.pow(y - bsize, 2);
            for (int x = (bsize + 1) * 2; x >= 0; x--) {
                double xpow = Math.pow(x - bsize, 2);
                for (int z = (bsize + 1) * 2; z >= 0; z--) {
                    if ((xpow + Math.pow(z - bsize, 2) + ypow) <= bpow) {
                        if ((clampY(bx + x - bsize, by + z - bsize, bz + y - bsize).getType() == Material.SNOW) && ((clampY(bx + x - bsize, by + z - bsize - 1, bz + y - bsize).getType() == Material.SNOW) || (clampY(bx + x - bsize, by + z - bsize - 1, bz + y - bsize).getType() == Material.AIR))) {
                            h.put(clampY(bx + x, by + z, bz + y));
                            setBlockIdAt(0, bx + x - bsize, by + z - bsize, bz + y - bsize);
                        }

                    }
                }
            }
        }

        v.storeUndo(h);
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
