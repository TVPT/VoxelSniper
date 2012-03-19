/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class newBallReplFill extends newBall{
    
    protected int replId;

    public newBallReplFill() {
        name = "Ball Replace/Fill";
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.voxel();
        vm.replace();
    }

    @Override
    protected void initArrow(vSniper v) {
        voxelId = v.voxelId;
        replId = v.replaceId;
    }

    @Override
    protected void initPowder(vSniper v) {
        voxelId = v.voxelId;
    }

    @Override
    protected void performArrow(Block b) {
        if (b.getTypeId() == replId && b.getTypeId() != voxelId) {
            h.put(b);
            b.setTypeId(voxelId);
        }
    }

    @Override
    protected void performPowder(Block b) {
        if (b.getTypeId() == 0 && b.getTypeId() != voxelId) {
            h.put(b);
            b.setTypeId(voxelId);
        }
    }
}
