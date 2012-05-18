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
public class pInkMat extends vPerformer {

    private byte d;
    private int ir;

    public pInkMat() {
        name = "Ink-Mat";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.vData v) {
        w = v.getWorld();
        d = v.data;
        ir = v.replaceId;
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.data();
        vm.replace();
    }

    @Override
    public void perform(Block b) {
        if (b.getTypeId() == ir) {
            h.put(b);
            b.setData(d, true);
        }
    }
}
