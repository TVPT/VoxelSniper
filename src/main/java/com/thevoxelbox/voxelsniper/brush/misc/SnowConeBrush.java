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
package com.thevoxelbox.voxelsniper.brush.misc;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Creates mounds of snow tiles.
 */
public class SnowConeBrush extends Brush {

    private void addSnow(final SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int tx = targetBlock.getBlockX();
        int tz = targetBlock.getBlockZ();
        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int miny = Math.max(GenericMath.floor(targetBlock.getBlockY() - brushSize), 0);
        int maxy = Math.min(GenericMath.floor(targetBlock.getBlockY() + brushSize) + 1, WORLD_HEIGHT - 1);
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;

        this.undo = new Undo(GenericMath.floor((brushSize + 1) * (brushSize + 1)));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (tx - x) * (tx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (tz - z) * (tz - z);
                if (xs + zs < brushSizeSquared) {
                    int y = maxy;
                    boolean topFound = false;
                    for (; y >= miny; y--) {
                        if (this.world.getBlockType(x, y, z) != BlockTypes.AIR) {
                            topFound = true;
                            break;
                        }
                    }
                    if (topFound) {
                        if (y == maxy) {
                            BlockType above = this.world.getBlock(x, y + 1, z).getType();
                            if (above != BlockTypes.AIR) {
                                continue;
                            }
                        }
                        BlockState block = this.world.getBlock(x, y, z);
                        if (block.getType() != BlockTypes.SNOW_LAYER) {
                            setBlockType(x, y + 1, z, BlockTypes.SNOW_LAYER);
                        } else {
                            Optional<Integer> height = block.get(Keys.LAYER);
                            if (!height.isPresent()) {
                                BlockState newSnow = BlockTypes.SNOW_LAYER.getDefaultState().with(Keys.LAYER, 2).get();
                                setBlockState(x, y, z, newSnow);
                            } else {
                                int sheight = height.get();
                                if (sheight == block.getValue(Keys.LAYER).get().getMaxValue()) {
                                    setBlockType(x, y, z, BlockTypes.SNOW);
                                    setBlockType(x, y + 1, z, BlockTypes.SNOW_LAYER);
                                } else {
                                    BlockState newSnow = BlockTypes.SNOW_LAYER.getDefaultState().with(Keys.LAYER, sheight + 1).get();
                                    setBlockState(x, y, z, newSnow);
                                }
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
        addSnow(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        addSnow(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName("Snow Cone");
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.snowcone";
    }
}
