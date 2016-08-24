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
import com.thevoxelbox.voxelsniper.util.BlockBuffer;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import org.spongepowered.api.block.BlockState;

import java.util.Map;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Blend_Brushes
 */
public class BlendVoxelBrush extends BlendBrushBase {

    public BlendVoxelBrush() {
        this.setName("Blend Voxel");
    }

    @Override
    protected final void blend(final SnipeData v) {
        final int brushSize = (int) Math.round(v.getBrushSize());
        // all changes are initially performed into a buffer to prevent the
        // results bleeding into each other
        BlockBuffer buffer = new BlockBuffer(new Vector3i(-brushSize, -brushSize, -brushSize), new Vector3i(brushSize, brushSize, brushSize));

        int tx = this.targetBlock.getBlockX();
        int ty = this.targetBlock.getBlockY();
        int tz = this.targetBlock.getBlockZ();

        Map<BlockState, Integer> frequency = Maps.newHashMap();

        for (int x = -brushSize; x <= brushSize; x++) {
            int x0 = x + tx;
            for (int y = -brushSize; y <= brushSize; y++) {
                int y0 = y + ty;
                for (int z = -brushSize; z <= brushSize; z++) {
                    int z0 = z + tz;
                    int highest = 1;
                    BlockState currentState = this.world.getBlock(x0, y0, z0);
                    BlockState highestState = currentState;
                    frequency.clear();
                    boolean tie = false;
                    for (int ox = -1; ox <= 1; ox++) {
                        for (int oz = -1; oz <= 1; oz++) {
                            for (int oy = -1; oy <= 1; oy++) {
                                if (oy + y0 < 0 || oy + y0 > WORLD_HEIGHT) {
                                    continue;
                                }
                                BlockState state = this.world.getBlock(x0 + ox, y0 + oy, z0 + oz);
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
                    }
                    if (!tie && currentState != highestState) {
                        buffer.set(x, y, z, highestState);
                    }
                }
            }
        }

        this.undo = new Undo(buffer.getBlockCount());
        // apply the buffer to the world
        for (int x = -brushSize; x <= brushSize; x++) {
            int x0 = x + tx;
            for (int y = -brushSize; y <= brushSize; y++) {
                int y0 = y + ty;
                for (int z = -brushSize; z <= brushSize; z++) {
                    int z0 = z + tz;
                    if (buffer.contains(x, y, z)) {
                        setBlockState(x0, y0, z0, buffer.get(x, y, z));
                    }
                }
            }
        }
        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.blendvoxel";
    }
}
