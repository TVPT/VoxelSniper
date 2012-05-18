/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class pComboNoPhys extends vPerformer {

    private int i;
    private byte d;

    public pComboNoPhys() {
        name = "Combo NoPhysics";
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.voxel();
        vm.data();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.vData v) {
        w = v.getWorld();
        i = v.voxelId;
        d = v.data;
    }

    @Override
    public void perform(Block b) {
        h.put(b);
        b.setTypeIdAndData(i, d, false);
    }
}
