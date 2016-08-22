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

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Removes floating snow tiles.
 */
public class CleanSnowBrush extends Brush {

    public CleanSnowBrush() {
        this.setName("Clean Snow");
    }

    private void cleanSnow(SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int tx = targetBlock.getBlockX();
        int ty = targetBlock.getBlockY();
        int tz = targetBlock.getBlockZ();
        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int miny = Math.max(GenericMath.floor(targetBlock.getBlockY() - brushSize), 0);
        int maxy = Math.min(GenericMath.floor(targetBlock.getBlockY() + brushSize) + 1, WORLD_HEIGHT);
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;
        if (miny <= 0) {
            miny = 1;
        }
        // Approximate the size of the undo
        this.undo = new Undo(GenericMath.floor(Math.PI * (brushSize + 1) * (brushSize + 1) * (brushSize + 1) / 3));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (tx - x) * (tx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (tz - z) * (tz - z);
                for (int y = maxy; y >= miny; y--) {
                    double ys = (ty - y) * (ty - y);
                    if (xs + ys + zs < brushSizeSquared) {
                        if(this.world.getBlockType(x, y, z) == BlockTypes.SNOW_LAYER) {
                            BlockType below = this.world.getBlockType(x, y - 1, z);
                            if (below == BlockTypes.SNOW_LAYER || below == BlockTypes.AIR) {
                                setBlockType(x, y, z, BlockTypes.AIR);
                            }
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.cleanSnow(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.cleanSnow(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.cleansnow";
    }
}
