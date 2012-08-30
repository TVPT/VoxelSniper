/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;

/**
 *
 * @author Voxel
 */
public class Ring extends PerformBrush {

    private double trueCircle = 0;
    private double innerSize = 0;

    public Ring() {
        name = "Ring";
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.custom(ChatColor.AQUA + "The inner radius is " + ChatColor.RED + innerSize);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Ring Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b ri true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ri false will switch back. (false is default)");
            v.sendMessage(ChatColor.AQUA + "/b ri ir2.5 -- will set the inner radius to 2.5 units");
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
            } else if (par[x].startsWith("ir")) {
                try {
                    double d = Double.parseDouble(par[x].replace("ir", ""));
                    innerSize = d;
                    v.sendMessage(ChatColor.AQUA + "The inner radius has been set to " + ChatColor.RED + innerSize);
                } catch (Exception e) {
                    v.sendMessage(ChatColor.RED + "The parameters included are invalid");
                }
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        ring(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        ring(v);
    }

    public void ring(vData v) {
        int bsize = v.brushSize;
        double outerpow = Math.pow(bsize + trueCircle, 2);
        double innerpow = Math.pow(innerSize, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                double ypow = Math.pow(y, 2);
                if ((xpow + ypow) <= outerpow && (xpow + ypow) >= innerpow) {
                    current.perform(clampY(bx + x, by, bz + y));
                    current.perform(clampY(bx + x, by, bz - y));
                    current.perform(clampY(bx - x, by, bz + y));
                    current.perform(clampY(bx - x, by, bz - y));
                }
            }
        }

        v.storeUndo(current.getUndo());
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
