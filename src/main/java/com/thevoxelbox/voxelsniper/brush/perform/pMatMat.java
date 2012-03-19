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
public class pMatMat extends vPerformer{

    private int i;
    private int r;

    public pMatMat() {
        name = "Mat-Mat";
    }

    @Override
    public void init(vSniper v) {
        w = v.p.getWorld();
        i = v.voxelId;
        r = v.replaceId;
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.voxel();
        vm.replace();
    }

    @Override
    public void perform(Block b) {
        if(b.getTypeId() == r) {
            h.put(b);
            b.setTypeId(i);
        }
    }
}
