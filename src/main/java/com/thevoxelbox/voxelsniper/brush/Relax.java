/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
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
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        v.sendMessage(ChatColor.DARK_GREEN + "This brush doesn't take any extra parameters.");
    }
}
