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
public class pMatInkNoPhys extends vPerformer {

    private int i;
    private byte dr;


    public pMatInkNoPhys() {
        name = "Mat-Ink, No Physics";
    }

    @Override
    public void init(vSniper v) {
        w = v.p.getWorld();
        i = v.voxelId;
        dr = v.replaceData;

    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.voxel();
        vm.replaceData();
    }

    @Override
    public void perform(Block b) {
        if( b.getData() == dr) {
            h.put(b);
            b.setTypeId(i, false);
        }
    }
}
