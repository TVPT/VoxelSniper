/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.util.VoxelList;
import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class pIncludeMat extends vPerformer {

    private VoxelList il;
    private int i;

    public pIncludeMat() {
        name = "Include Mat";
    }

    @Override
    public void info(Message vm) {
        vm.performerName(name);
        vm.voxelList();
        vm.voxel();
        vm.data();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
        w = v.getWorld();
        i = v.getVoxelId();
        il = v.getVoxelList();
    }

    @Override
    public void perform(Block b) {
        if (il.contains(b.getTypeId())) {
            h.put(b);
            b.setTypeId(i);
        }
    }
}
