/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;

/**
 *
 * @author Gavjenks
 */
public class ChunkCoords extends Brush {
    
    public ChunkCoords() {
        name = "ChunkCoords";
    }

    @Override
    public void arrow(vSniper v) {
        int x = clampY(tb.getX(), tb.getY(), tb.getZ()).getChunk().getX();
        int z = clampY(tb.getX(), tb.getY(), tb.getZ()).getChunk().getZ();
        v.p.sendMessage("X value of Chunk: " + x);
        v.p.sendMessage("Z value of Chunk: " + z);
    }

    @Override
    public void powder(vSniper v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
    }
}
