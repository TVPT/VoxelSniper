/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;

/**
 *
 * @author Voxel
 */
public class Relax extends Brush {

    public Relax() {
        name = "Relax";
    }

    @Override
    public void arrow(vSniper v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void powder(vSniper v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        v.p.sendMessage(ChatColor.DARK_GREEN + "This brush doesn't take any extra parameters.");
    }
}
