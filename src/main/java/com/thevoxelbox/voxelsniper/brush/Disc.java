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
public class Disc extends PerformBrush {

    public Disc() {
        name = "Disc";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        disc(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        disc(v);
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
            v.sendMessage(ChatColor.GOLD + "Disc Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b d true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
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

    public void disc(vData v) {
        int bsize = v.brushSize;
        double bpow = Math.pow(bsize + trueCircle, 2);
        current.perform(clampY(bx, by, bz));
        for (int x = bsize; x >= 0; x--) {
            current.perform(clampY(bx + x, by, bz));
            current.perform(clampY(bx - x, by, bz));
            current.perform(clampY(bx, by, bz + x));
            current.perform(clampY(bx, by, bz - x));
        }
        for (int x = bsize; x >= 1; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 1; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    current.perform(clampY(bx + x, by, bz + y));
                    current.perform(clampY(bx + x, by, bz - y));
                    current.perform(clampY(bx - x, by, bz + y));
                    current.perform(clampY(bx - x, by, bz - y));
                }
            }
        }

        v.storeUndo(current.getUndo());
    }
}
