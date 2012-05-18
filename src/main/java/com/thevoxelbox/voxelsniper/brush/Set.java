/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class Set extends PerformBrush {

    protected int i;
    protected Block b = null;

    public Set() {
        name = "Set";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) { // Derp
        i = v.voxelId;
        if (set(tb, v)) {
            v.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(current.getUndo());
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        i = v.voxelId;
        if (set(lb, v)) {
            v.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(current.getUndo());
        }
    }

    @Override
    public void info(vMessage vm) {
        b = null;
        vm.brushName(name);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        super.parameters(par, v);
    }

    private boolean set(Block bl, vData v) {
        if (b == null) {
            b = bl;
            return true;
        } else {
            if (!b.getWorld().getName().equals(bl.getWorld().getName())) {
                v.sendMessage(ChatColor.RED + "You selected points in different worlds!");
                b = null;
                return true;
            }
            int lowx = (b.getX() <= bl.getX()) ? b.getX() : bl.getX();
            int lowy = (b.getY() <= bl.getY()) ? b.getY() : bl.getY();
            int lowz = (b.getZ() <= bl.getZ()) ? b.getZ() : bl.getZ();
            int highx = (b.getX() >= bl.getX()) ? b.getX() : bl.getX();
            int highy = (b.getY() >= bl.getY()) ? b.getY() : bl.getY();
            int highz = (b.getZ() >= bl.getZ()) ? b.getZ() : bl.getZ();
            if (Math.abs(highx - lowx) * Math.abs(highz - lowz) * Math.abs(highy - lowy) > 5000000) {
                v.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
            } else {
                for (int y = lowy; y <= highy; y++) {
                    for (int x = lowx; x <= highx; x++) {
                        for (int z = lowz; z <= highz; z++) {
                            current.perform(clampY(x, y, z));
                        }
                    }
                }
            }

            b = null;
            return false;
        }
    }
}
