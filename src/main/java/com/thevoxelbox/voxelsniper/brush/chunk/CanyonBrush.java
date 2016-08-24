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
package com.thevoxelbox.voxelsniper.brush.chunk;

import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;

import java.util.Optional;

/**
 * Shifts terrain vertically chunk by chunk.
 */
public class CanyonBrush extends ChunkBrush {

    private static final int SHIFT_LEVEL_MIN = -255;
    private static final int SHIFT_LEVEL_MAX = 255;
    protected int yLevel = -10;

    public CanyonBrush() {
        this.setName("Canyon");
    }

    @Override
    protected void createUndo(int chunks) {
        this.undo = new Undo(chunks * 16384);
    }

    @Override
    protected void storeUndo(SnipeData v) {
        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected void operate(SnipeData v, Chunk chunk) {
        int minx = chunk.getBlockMin().getX();
        int minz = chunk.getBlockMin().getZ();
        BlockState fillBlock = v.getVoxelIdState();
        if (fillBlock.getType() == BlockTypes.AIR) {
            fillBlock = BlockTypes.STONE.getDefaultState();
        }
        int sy = this.yLevel < 0 ? 0 : WORLD_HEIGHT;
        int ey = this.yLevel < 0 ? WORLD_HEIGHT : 0;
        int dir = this.yLevel < 0 ? 1 : -1;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = sy; y != ey; y += dir) {
                    int srcy = y - this.yLevel;
                    if (srcy < 0) {
                        setBlockState(minx + x, y, minz + z, fillBlock);
                    } else if (srcy > WORLD_HEIGHT) {
                        setBlockType(minx + x, y, minz + z, BlockTypes.AIR);
                    } else {
                        setBlockState(minx + x, y, minz + z, this.world.getBlock(minx + x, srcy, minz + z));
                    }
                }
            }
        }
    }

    @Override
    public void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.GREEN + "Shift Level set to " + this.yLevel);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length == 0 || par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GREEN + "y[number] to set the Level to which the land will be shifted down");
        }
        if (par[0].startsWith("y")) {
            int _i = Integer.parseInt(par[0].replace("y", ""));
            if (_i < SHIFT_LEVEL_MIN) {
                _i = SHIFT_LEVEL_MIN;
            } else if (_i > SHIFT_LEVEL_MAX) {
                _i = SHIFT_LEVEL_MAX;
            }
            this.yLevel = _i;
            v.sendMessage(TextColors.GREEN, "Shift Level set to " + this.yLevel);
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.canyon";
    }
}
