/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;

/**
 *
 * @author Piotr
 */
public class Ball extends PerformBrush {

    private double trueCircle = 0;

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        ball(v);
    }

    @Override
    public void powder(vSniper v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName("Ball");
        vm.size();
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Ball Brush Parameters:");
            v.p.sendMessage(ChatColor.AQUA + "/b b true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("true")) {
                trueCircle = 0.5;
                v.p.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            } else if (par[x].startsWith("false")) {
                trueCircle = 0;
                v.p.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                continue;
            } else {
                v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    public void ball(vSniper v) {
        int bsize = v.brushSize;

        double bpow = Math.pow(bsize + trueCircle, 2);
        double zpow;
        double xpow;

        current.perform(clampY(bx, by, bz));

        for (int i = 1; i <= bsize; i++) {
            current.perform(clampY(bx + i, by, bz));
            current.perform(clampY(bx - i, by, bz));
            current.perform(clampY(bx, by + i, bz));
            current.perform(clampY(bx, by - i, bz));
            current.perform(clampY(bx, by, bz + i));
            current.perform(clampY(bx, by, bz - i));
        }

        for (int i = 1; i <= bsize; i++) {
            zpow = Math.pow(i, 2);
            for (int j = 1; j <= bsize; j++) {
                if (zpow + Math.pow(j, 2) <= bpow) {
                    current.perform(clampY(bx + i, by, bz + j));
                    current.perform(clampY(bx + i, by, bz - j));
                    current.perform(clampY(bx - i, by, bz + j));
                    current.perform(clampY(bx - i, by, bz - j));
                    current.perform(clampY(bx + i, by + j, bz));
                    current.perform(clampY(bx + i, by - j, bz));
                    current.perform(clampY(bx - i, by + j, bz));
                    current.perform(clampY(bx - i, by - j, bz));
                    current.perform(clampY(bx, by + i, bz + j));
                    current.perform(clampY(bx, by + i, bz - j));
                    current.perform(clampY(bx, by - i, bz + j));
                    current.perform(clampY(bx, by - i, bz - j));
                }
            }
        }

        for (int z = 1; z <= bsize; z++) {
            zpow = Math.pow(z, 2);
            for (int x = 1; x <= bsize; x++) {
                xpow = Math.pow(x, 2);
                for (int y = 1; y <= bsize; y++) {
                    if ((xpow + Math.pow(y, 2) + zpow) <= bpow) {
                        current.perform(clampY(bx + x, by + y, bz + z));
                        current.perform(clampY(bx + x, by + y, bz - z));
                        current.perform(clampY(bx - x, by + y, bz + z));
                        current.perform(clampY(bx - x, by + y, bz - z));
                        current.perform(clampY(bx + x, by - y, bz + z));
                        current.perform(clampY(bx + x, by - y, bz - z));
                        current.perform(clampY(bx - x, by - y, bz + z));
                        current.perform(clampY(bx - x, by - y, bz - z));
                    }
                }
            }
        }

        if (current.getUndo().getSize() > 0) {
            v.hashUndo.put(v.hashEn, current.getUndo());
            v.hashEn++;
        }
    }
}
