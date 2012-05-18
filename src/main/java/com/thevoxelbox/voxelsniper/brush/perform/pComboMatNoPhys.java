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
public class pComboMatNoPhys extends vPerformer {

    private byte d;
    private int i;
    private int ir;

    public pComboMatNoPhys() {
        name = "Combo-Mat, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.vData v) {
        w = v.getWorld();
        d = v.data;
        i = v.voxelId;
        ir = v.replaceId;
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.voxel();
        vm.replace();
        vm.data();
    }

    @Override
    public void perform(Block b) {
        if (b.getTypeId() == ir) {
            h.put(b);
            b.setTypeIdAndData(i, d, false);
        }
    }
}
