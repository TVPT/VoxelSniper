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
public class pInkInk extends vPerformer {

    private byte d;
    private byte dr;

    public pInkInk() {
        name = "Ink-Ink";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.vData v) {
        w = v.getWorld();
        d = v.data;
        dr = v.replaceData;
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.data();
        vm.replaceData();
    }

    @Override
    public void perform(Block b) {
        if (b.getData() == dr) {
            h.put(b);
            b.setData(d, true);
        }
    }
}
