/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;

/**
 *
 * @author Gavjenks
 */
public class ChunkCoords extends Brush {
    
    public ChunkCoords() {
        name = "ChunkCoords";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        int x = clampY(tb.getX(), tb.getY(), tb.getZ()).getChunk().getX();
        int z = clampY(tb.getX(), tb.getY(), tb.getZ()).getChunk().getZ();
        v.sendMessage("X value of Chunk: " + x);
        v.sendMessage("Z value of Chunk: " + z);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
    }
}
