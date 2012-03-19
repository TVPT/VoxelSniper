/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class pMatComboNophys extends vPerformer {

    private byte dr;
    private int i;
    private int ir;

    public pMatComboNophys() {
        name = "Mat-Combo, No Physics";
    }

    @Override
    public void init(vSniper v) {
        w = v.p.getWorld();
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
        if(b.getTypeId() == ir && b.getData() == dr) {
            h.put(b);
            b.setTypeId(i, false);
        }
    }
}
