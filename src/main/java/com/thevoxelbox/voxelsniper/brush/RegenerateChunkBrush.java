/*
 * This file is part of VoxelSniper, licensed under the MIT License (MIT).
 *
 * Copyright (c) The VoxelBox <http://thevoxelbox.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * Regenerates the target chunk.
 */
public class RegenerateChunkBrush extends Brush {

    public RegenerateChunkBrush() {
        this.setName("Chunk Generator 40k");
    }

    private void generateChunk(final SnipeData v) {
        // @Spongify pending regenerate chunk method
//        final Chunk chunk = this.getTargetBlock().getChunk();
//        final Undo undo = new Undo();
//
//        for (int z = CHUNK_SIZE; z >= 0; z--)
//        {
//            for (int x = CHUNK_SIZE; x >= 0; x--)
//            {
//                for (int y = this.getWorld().getMaxHeight(); y >= 0; y--)
//                {
//                    undo.put(chunk.getBlock(x, y, z));
//                }
//            }
//        }
//        v.owner().storeUndo(undo);
//
//        v.sendMessage("Generate that chunk! " + chunk.getX() + " " + chunk.getZ());
//        this.getWorld().regenerateChunk(chunk.getX(), chunk.getZ());
//        this.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.generateChunk(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.generateChunk(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.brushMessage("Tread lightly.");
        vm.brushMessage("This brush will melt your spleen and sell your kidneys.");
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.regeneratechunk";
    }
}
