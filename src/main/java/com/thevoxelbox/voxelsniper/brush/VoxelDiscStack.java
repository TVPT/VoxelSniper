/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;

/**
 *
 * @author DivineRage
 */
public class VoxelDiscStack extends Brush {

    public VoxelDiscStack() {
        name = "Stack";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Stack brush Parameters:");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("d")) {
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }

    }
}
