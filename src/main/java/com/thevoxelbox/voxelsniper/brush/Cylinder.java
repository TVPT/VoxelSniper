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
 * @author Kavutop with exerpts frankenpasted from other brushes
 */
public class Cylinder extends PerformBrush {

    /**
     * The starring Y position
     * At the bottom of the cylinder
     */
    protected int st;
    /**
     * End Y position
     * At the top of the cylinder
     */
    protected int en;

    public Cylinder() {
        name = "Cylinder";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        cylinder(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        cylinder(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.height();
        vm.center();
        //vm.voxel();
    }
    double trueCircle = 0;

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Cylinder Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b c h[number] -- set the cylinder v.voxelHeight.  Default is 1.");
            v.sendMessage(ChatColor.DARK_AQUA + "/b c true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
            v.sendMessage(ChatColor.DARK_BLUE + "/b c c[number] -- set the origin of the cylinder compared to the target block. Positive numbers will move the cylinder upward, negative will move it downward.");
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
            } else if (par[x].startsWith("h")) {
                v.voxelHeight = (int) Double.parseDouble(par[x].replace("h", ""));
                v.sendMessage(ChatColor.AQUA + "Cylinder v.voxelHeight set to: " + v.voxelHeight);
                continue;
            } else if (par[x].startsWith("c")) {
                v.cCen = (int) Double.parseDouble(par[x].replace("c", ""));
                v.sendMessage(ChatColor.AQUA + "Cylinder origin set to: " + v.cCen);
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    public void cylinder(vData v) {
        st = by + (int) v.cCen;
        en = by + (int) v.voxelHeight + (int) v.cCen;
        if (en < st) {
            en = st;
        }
        if (st < 0) {
            st = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        } else if (st > 127) {
            st = 127;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        }
        if (en < 0) {
            en = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        } else if (en > 127) {
            en = 127;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        }
        int bsize = v.brushSize;

        double bpow = Math.pow(bsize + trueCircle, 2);

        for (int z = en; z >= st; z--) {
            for (int x = bsize; x >= 0; x--) {
                double xpow = Math.pow(x, 2);
                for (int y = bsize; y >= 0; y--) {
                    if ((xpow + Math.pow(y, 2)) <= bpow) {
                        current.perform(clampY(bx + x, by, bz + y));
                        current.perform(clampY(bx + x, by, bz - y));
                        current.perform(clampY(bx - x, by, bz + y));
                        current.perform(clampY(bx - x, by, bz - y));
                    }
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