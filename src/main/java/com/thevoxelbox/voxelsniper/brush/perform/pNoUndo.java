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
public class pNoUndo extends vPerformer {

    private int i;

    public pNoUndo() {
        name = "BOMB SQUAD";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.vData v) {
        w = v.getWorld();
        i = v.voxelId;
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.voxel();
    }

    @Override
    public void perform(Block b) {
        if (b.getTypeId() != i) {
            b.setTypeId(i);
        }
    }
}