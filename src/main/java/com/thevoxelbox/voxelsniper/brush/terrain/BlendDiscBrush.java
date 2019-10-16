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
package com.thevoxelbox.voxelsniper.brush.terrain;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.util.BlockBuffer;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import org.spongepowered.api.block.BlockState;

import java.util.Map;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Blend_Brushes
 */
@Brush.BrushInfo(
    name = "Blend Dissc",
    aliases = {"bd", "blenddisc"},
    permission = "voxelsniper.brush.blenddisc",
    category = Brush.BrushCategory.TERRAIN
)
public class BlendDiscBrush extends BlendBrushBase {

    /**
     *
     */
    public BlendDiscBrush() {
    }

    @Override
    protected final void blend(final SnipeData v) {
        final int brushSize = (int) v.getBrushSize() + 1;
        double brushSizeSquared = v.getBrushSize() * v.getBrushSize();
        // all changes are initially performed into a buffer to prevent the
        // results bleeding into each other
        BlockBuffer buffer = new BlockBuffer(new Vector3i(-brushSize, 0, -brushSize), new Vector3i(brushSize, 0, brushSize));

        int tx = this.targetBlock.getBlockX();
        int ty = this.targetBlock.getBlockY();
        int tz = this.targetBlock.getBlockZ();

        Map<BlockState, Integer> frequency = Maps.newHashMap();

        for (int x = -brushSize; x <= brushSize; x++) {
            int x0 = x + tx;
            for (int z = -brushSize; z <= brushSize; z++) {
                if (x * x + z * z >= brushSizeSquared) {
                    continue;
                }
                int z0 = z + tz;
                int highest = 1;
                BlockState currentState = this.world.getBlock(x0, ty, z0);
                BlockState highestState = currentState;
                frequency.clear();
                boolean tie = false;
                for (int ox = -1; ox <= 1; ox++) {
                    for (int oz = -1; oz <= 1; oz++) {
                        BlockState state = this.world.getBlock(x0 + ox, ty, z0 + oz);
                        Integer count = frequency.get(state);
                        if (count == null) {
                            count = 1;
                        } else {
                            count++;
                        }
                        if (count > highest) {
                            highest = count;
                            highestState = state;
                            tie = false;
                        } else if (count == highest) {
                            tie = true;
                        }
                        frequency.put(state, count);
                    }
                }
                if (!tie && currentState != highestState) {
                    buffer.set(x, 0, z, highestState);
                }
            }
        }

        this.undo = new Undo(buffer.getBlockCount());
        // apply the buffer to the world
        for (int x = -brushSize; x <= brushSize; x++) {
            int x0 = x + tx;
            for (int z = -brushSize; z <= brushSize; z++) {
                int z0 = z + tz;
                if (buffer.contains(x, 0, z)) {
                    setBlockState(x0, ty, z0, buffer.get(x, ty, z));
                }
            }
        }
        v.owner().storeUndo(this.undo);
        this.undo = null;
    }
}
