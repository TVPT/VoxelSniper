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
public class pInk extends vPerformer {

    private byte d;

    public pInk() {
        name = "Ink";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.vData v) {
        w = v.getWorld();
        d = v.data;
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.data();
    }

    @Override
    public void perform(Block b) {
        h.put(b);
        b.setData(d);
    }
}
