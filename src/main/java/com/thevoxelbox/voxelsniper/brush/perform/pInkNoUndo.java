/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class pInkNoUndo extends vPerformer {

    private byte d;

    public pInkNoUndo() {
        name = "Ink, No-Undo"; // made name more descriptive - Giltwist
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
        w = v.getWorld();
        d = v.getData();
    }

    @Override
    public void info(Message vm) {
        vm.performerName(name);
        vm.data();
    }

    @Override
    public void perform(Block b) {
        if (b.getData() != d) {
            b.setData(d);
        }
    }
}