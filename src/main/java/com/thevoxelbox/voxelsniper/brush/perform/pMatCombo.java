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
public class pMatCombo extends vPerformer {

    private byte dr;
    private int i;
    private int ir;

    public pMatCombo() {
        name = "Mat-Combo";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.vData v) {
        w = v.getWorld();
        dr = v.replaceData;
        i = v.voxelId;
        ir = v.replaceId;
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.voxel();
        vm.replace();
        vm.replaceData();
    }

    @Override
    public void perform(Block b) {
        if (b.getTypeId() == ir && b.getData() == dr) {
            h.put(b);
            b.setTypeId(i, true);
        }
    }
}
