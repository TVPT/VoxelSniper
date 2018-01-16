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

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Chunk;

/**
 * Creates flat oceans.
 */
public class FlatOceanBrush extends ChunkBrush {

    private static final int DEFAULT_WATER_LEVEL = 29;
    private static final int DEFAULT_FLOOR_LEVEL = 8;
    private int waterLevel = DEFAULT_WATER_LEVEL;
    private int floorLevel = DEFAULT_FLOOR_LEVEL;

    public FlatOceanBrush() {
        this.setName("FlatOcean");
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
        int miny = chunk.getBlockMin().getY();
        int minz = chunk.getBlockMin().getZ();
        int maxx = chunk.getBlockMax().getX();
        int maxy = chunk.getBlockMax().getY();
        int maxz = chunk.getBlockMax().getZ();

        for (int x = minx; x <= maxx; x++) {
            for (int z = minz; z <= maxz; z++) {
                for (int y = miny; y <= maxy; y++) {
                    if (y <= this.floorLevel) {
                        setBlockType(x, y, z, BlockTypes.DIRT);
                    } else if (y <= this.waterLevel) {
                        setBlockType(x, y, z, BlockTypes.WATER, BlockChangeFlags.NONE);
                    } else {
                        setBlockType(x, y, z, BlockTypes.AIR);
                    }
                }
            }
        }
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.RED + "THIS BRUSH DOES NOT UNDO");
        vm.custom(TextColors.GREEN + "Water level set to " + this.waterLevel);
        vm.custom(TextColors.GREEN + "Ocean floor level set to " + this.floorLevel);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GREEN + "yo[number] to set the Level to which the water will rise.");
                v.sendMessage(TextColors.GREEN + "yl[number] to set the Level to which the ocean floor will rise.");
            }
            if (parameter.startsWith("yo")) {
                try {
                    int newWaterLevel = Integer.parseInt(parameter.replace("yo", ""));
                    this.waterLevel = newWaterLevel;
                    if (this.waterLevel <= 0) {
                        v.sendMessage(TextColors.RED, "Water level cannot be negative.");
                        continue;
                    }
                    v.sendMessage(TextColors.GREEN + "Water Level set to " + this.waterLevel);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid water level value.");
                }
            } else if (parameter.startsWith("yl")) {
                try {
                    int newFloorLevel = Integer.parseInt(parameter.replace("yl", ""));
                    this.floorLevel = newFloorLevel;
                    if (this.waterLevel <= 0) {
                        v.sendMessage(TextColors.RED, "Ocean floor level cannot be negative.");
                        continue;
                    }
                    v.sendMessage(TextColors.GREEN + "Ocean floor Level set to " + this.floorLevel);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid ocean floor level value.");
                }
            }
        }
        if (this.floorLevel < 0) {
            this.floorLevel = 0;
        }
        if (this.waterLevel <= this.floorLevel) {
            this.waterLevel = this.floorLevel + 1;
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.flatocean";
    }
}
