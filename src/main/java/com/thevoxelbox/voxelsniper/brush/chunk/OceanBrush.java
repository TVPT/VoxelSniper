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

import com.google.common.collect.Sets;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;

import java.util.Set;

@Brush.BrushInfo(
    name = "Ocean",
    aliases = {"o", "ocean"},
    permission = "voxelsniper.brush.ocean",
    category = Brush.BrushCategory.CHUNK
)
public class OceanBrush extends ChunkBrush {

    private static final int WATER_LEVEL_DEFAULT = 62;
    private static final int WATER_LEVEL_MIN = 12;
    private static final int WATER_DEPTH_MIN = 6;
    private static final Set<BlockType> EXCLUDED_MATERIALS = Sets.newHashSet();

    static {
        EXCLUDED_MATERIALS.add(BlockTypes.LOG);
        EXCLUDED_MATERIALS.add(BlockTypes.LEAVES);
        EXCLUDED_MATERIALS.add(BlockTypes.LOG2);
        EXCLUDED_MATERIALS.add(BlockTypes.LEAVES2);
        EXCLUDED_MATERIALS.add(BlockTypes.BROWN_MUSHROOM_BLOCK);
        EXCLUDED_MATERIALS.add(BlockTypes.RED_MUSHROOM_BLOCK);
        EXCLUDED_MATERIALS.add(BlockTypes.MELON_BLOCK);
        EXCLUDED_MATERIALS.add(BlockTypes.PUMPKIN);
    }

    private int waterLevel = WATER_LEVEL_DEFAULT;
    private boolean coverFloor = false;

    public OceanBrush() {
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

    private int getActualHeight(final int bx, final int bz) {
        for (int y = WORLD_HEIGHT; y > 0; y--) {
            BlockState state = this.world.getBlock(bx, y, bz);
            if (state.getType() == BlockTypes.AIR) {
                return y;
            }
        }
        return 0;
    }

    private int getHeight(final int bx, final int bz, int start) {
        for (int y = start; y > 0; y--) {
            BlockState state = this.world.getBlock(bx, y, bz);
            if (!BlockHelper.isLiquidOrGas(state)) {
                if (BlockHelper.isSolid(state)) {
                    return y;
                }
                if (EXCLUDED_MATERIALS.contains(state.getType())) {
                    return y;
                }
            }
        }
        return 0;
    }

    @Override
    protected void operate(SnipeData v, Chunk chunk) {
        int minx = chunk.getBlockMin().getX();
        int minz = chunk.getBlockMin().getZ();
        BlockState fillBlock = v.getVoxelIdState();
        if (fillBlock.getType() == BlockTypes.AIR) {
            fillBlock = BlockTypes.DIRT.getDefaultState();
        }
        for (int x = 0; x < 16; x++) {
            int x0 = minx + x;
            for (int z = 0; z < 16; z++) {
                int z0 = minz + z;
                int actualheight = getActualHeight(x0, z0);
                int height = getHeight(x0, z0, actualheight);
                int depth = height;
                if (height > this.waterLevel) {
                    depth = this.waterLevel - (height - this.waterLevel) - WATER_DEPTH_MIN;
                }
                if (depth > this.waterLevel - WATER_DEPTH_MIN) {
                    depth = WATER_DEPTH_MIN;
                }
                if (depth < WATER_LEVEL_MIN) {
                    depth = WATER_LEVEL_MIN;
                }
                int y = actualheight;
                for(; y > this.waterLevel; y--) {
                    setBlockType(x0, y, z0, BlockTypes.AIR);
                }
                for(; y > depth; y--) {
                    setBlockType(x0, y, z0, BlockTypes.WATER);
                }
                if(this.coverFloor) {
                    setBlockState(x0, y, z0, v.getVoxelIdState());
                }
            }
        }
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        this.coverFloor = false;
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.BLUE, "Parameters:");
                v.sendMessage(TextColors.GREEN, "-wlevel #  ", TextColors.BLUE, "--  Sets the water level (e.g. -wlevel 64)");
                v.sendMessage(TextColors.GREEN, "-cfloor    ", TextColors.BLUE,
                        "--  Enables or disables sea floor cover (Cover material will be your voxel material)");
            } else if (parameter.equalsIgnoreCase("-wlevel")) {
                if ((i + 1) >= par.length) {
                    v.sendMessage(TextColors.RED, "Missing parameter. Correct syntax: -wlevel [#] (e.g. -wlevel 64)");
                    continue;
                }
                try {
                    int temp = Integer.parseInt(par[++i]);
                    if (temp <= WATER_LEVEL_MIN) {
                        v.sendMessage(TextColors.RED, "Error: Your specified water level was below 12.");
                        continue;
                    }
                    this.waterLevel = temp;
                    v.sendMessage(TextColors.BLUE, "Water level set to ", TextColors.GREEN, this.waterLevel);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalud water level.");
                }
            } else if (parameter.equalsIgnoreCase("-cfloor") || parameter.equalsIgnoreCase("-coverfloor")) {
                this.coverFloor = true;
                v.sendMessage(TextColors.BLUE, "Floor cover ", TextColors.GREEN, "enabled.");
            }
        }
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(info.name());
        vm.custom(TextColors.BLUE, "Water level set to ", TextColors.GREEN, this.waterLevel);
        vm.custom(TextColors.BLUE, "Floor cover ", TextColors.GREEN, (this.coverFloor ? "enabled" : "disabled") + ".");
    }
}
