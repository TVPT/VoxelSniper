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

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.util.BlockBuffer;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.schematic.BlockPalette;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Blend_Brushes
 */
public class BlendVoxelBrush extends BlendBrushBase {

    public BlendVoxelBrush() {
        this.setName("Blend Voxel");
    }

    @Override
    protected final void blend(final SnipeData v) {
        final int brushSize = (int) v.getBrushSize() + 1;
        // all changes are initially performed into a buffer to prevent the
        // results bleeding into each other
        BlockBuffer buffer = new BlockBuffer(new Vector3i(-brushSize, -brushSize, -brushSize), new Vector3i(brushSize, brushSize, brushSize));

        int tx = this.targetBlock.getBlockX();
        int ty = this.targetBlock.getBlockY();
        int tz = this.targetBlock.getBlockZ();

        BlockPalette palette = BlockPaletteTypes.GLOBAL.create();
        int[] materials = new int[27];

        for (int x = -brushSize; x <= brushSize; x++) {
            int x0 = x + tx;
            for (int y = -brushSize; y <= brushSize; y++) {
                int y0 = y + ty;
                for (int z = -brushSize; z <= brushSize; z++) {
                    int z0 = z + tz;
                    // fill the materials array with the 27 blocks in a 3x3x3
                    // are around this block
                    for (int ox = -1; ox <= 1; ox++) {
                        for (int oz = -1; oz <= 1; oz++) {
                            for (int oy = -1; oy <= 1; oy++) {
                                if (oy + y0 < 0 || oy + y0 > WORLD_HEIGHT) {
                                    materials[(ox + 1) + (oy + 1) * 3 + (oz + 1) * 9] = -1;
                                } else {
                                    materials[(ox + 1) + (oy + 1) * 3 + (oz + 1) * 9] =
                                            palette.getOrAssign(this.world.getBlock(x0 + ox, y0 + oy, z0 + oz));
                                }
                            }
                        }
                    }
                    // find the most common material
                    int highest = 1;
                    // default index is 13 which is the index of the center
                    // block, so if every block is different the highest will be
                    // the center block and no change will occur
                    int highestIndex = 13;
                    for (int i = 0; i < 27; i++) {
                        if (materials[i] == -1) {
                            continue;
                        }
                        int c = 0;
                        for (int j = 0; j < 27; j++) {
                            if (j == i) {
                                continue;
                            }
                            if (materials[i] == materials[j]) {
                                c++;
                            }
                        }
                        if (c > highest) {
                            highest = c;
                            highestIndex = i;
                        }
                    }
                    // if the highest found material was different than what we
                    // had then we set it into the buffer
                    if (materials[highestIndex] != materials[13]) {
                        buffer.set(x, y, z, palette.get(materials[highestIndex]).get());
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
