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

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.util.BlockHelper;

import com.flowpowered.math.GenericMath;
import com.flowpowered.noise.NoiseQuality;
import com.flowpowered.noise.module.source.Perlin;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * Total destruction.
 */
public class HeatRayBrush extends Brush {

    private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
    private static final double REQUIRED_COBBLE_DENSITY = 0.45;
    private static final double REQUIRED_FIRE_DENSITY = 0.1;

    private static final Set<BlockType> FLAMABLE_BLOCKS = new HashSet<BlockType>();

    static {
        FLAMABLE_BLOCKS.add(BlockTypes.WALL_SIGN);
        FLAMABLE_BLOCKS.add(BlockTypes.STANDING_SIGN);
        FLAMABLE_BLOCKS.add(BlockTypes.WOODEN_BUTTON);
        FLAMABLE_BLOCKS.add(BlockTypes.WOODEN_DOOR);
        FLAMABLE_BLOCKS.add(BlockTypes.RED_FLOWER);
        FLAMABLE_BLOCKS.add(BlockTypes.YELLOW_FLOWER);
        FLAMABLE_BLOCKS.add(BlockTypes.WOODEN_SLAB);
        FLAMABLE_BLOCKS.add(BlockTypes.DOUBLE_WOODEN_SLAB);
        FLAMABLE_BLOCKS.add(BlockTypes.TALLGRASS);
        FLAMABLE_BLOCKS.add(BlockTypes.PLANKS);
        FLAMABLE_BLOCKS.add(BlockTypes.ACACIA_DOOR);
        FLAMABLE_BLOCKS.add(BlockTypes.ACACIA_FENCE);
        FLAMABLE_BLOCKS.add(BlockTypes.ACACIA_FENCE_GATE);
        FLAMABLE_BLOCKS.add(BlockTypes.ACACIA_STAIRS);
        FLAMABLE_BLOCKS.add(BlockTypes.BIRCH_DOOR);
        FLAMABLE_BLOCKS.add(BlockTypes.BIRCH_FENCE);
        FLAMABLE_BLOCKS.add(BlockTypes.BIRCH_FENCE_GATE);
        FLAMABLE_BLOCKS.add(BlockTypes.BIRCH_STAIRS);
        FLAMABLE_BLOCKS.add(BlockTypes.FENCE);
        FLAMABLE_BLOCKS.add(BlockTypes.FENCE_GATE);
        FLAMABLE_BLOCKS.add(BlockTypes.OAK_STAIRS);
        FLAMABLE_BLOCKS.add(BlockTypes.BOOKSHELF);
        FLAMABLE_BLOCKS.add(BlockTypes.CARPET);
        FLAMABLE_BLOCKS.add(BlockTypes.COAL_BLOCK);
        FLAMABLE_BLOCKS.add(BlockTypes.CRAFTING_TABLE);
        FLAMABLE_BLOCKS.add(BlockTypes.DARK_OAK_DOOR);
        FLAMABLE_BLOCKS.add(BlockTypes.DARK_OAK_FENCE);
        FLAMABLE_BLOCKS.add(BlockTypes.DARK_OAK_FENCE_GATE);
        FLAMABLE_BLOCKS.add(BlockTypes.DARK_OAK_STAIRS);
        FLAMABLE_BLOCKS.add(BlockTypes.DEADBUSH);
        FLAMABLE_BLOCKS.add(BlockTypes.JUNGLE_DOOR);
        FLAMABLE_BLOCKS.add(BlockTypes.JUNGLE_FENCE);
        FLAMABLE_BLOCKS.add(BlockTypes.JUNGLE_FENCE_GATE);
        FLAMABLE_BLOCKS.add(BlockTypes.JUNGLE_STAIRS);
        FLAMABLE_BLOCKS.add(BlockTypes.LEAVES);
        FLAMABLE_BLOCKS.add(BlockTypes.LEAVES2);
        FLAMABLE_BLOCKS.add(BlockTypes.LOG);
        FLAMABLE_BLOCKS.add(BlockTypes.LOG2);
        FLAMABLE_BLOCKS.add(BlockTypes.TRAPDOOR);
        FLAMABLE_BLOCKS.add(BlockTypes.WOOL);
        FLAMABLE_BLOCKS.add(BlockTypes.SPRUCE_DOOR);
        FLAMABLE_BLOCKS.add(BlockTypes.SPRUCE_FENCE);
        FLAMABLE_BLOCKS.add(BlockTypes.SPRUCE_FENCE_GATE);
        FLAMABLE_BLOCKS.add(BlockTypes.SPRUCE_STAIRS);
    }

    private Perlin perlin = new Perlin();
    private int octaves = 5;
    private double frequency = 1;
    private double persistence = 0.5;

    public HeatRayBrush() {
        this.setName("Heat Ray");

        this.perlin.setLacunarity(2);
        this.perlin.setNoiseQuality(NoiseQuality.STANDARD);
    }

    public void heatRay(SnipeData v, Location<World> targetBlock) {
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

        // Approximate the size of the undo to the volume of a one larger sphere
        this.undo = new Undo(GenericMath.floor(4 * Math.PI * (brushSize + 1) * (brushSize + 1) * (brushSize + 1) / 3));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (tx - x) * (tx - x);
            for (int y = miny; y <= maxy; y++) {
                double ys = (ty - y) * (ty - y);
                for (int z = minz; z <= maxz; z++) {
                    double zs = (tz - z) * (tz - z);
                    if (xs + ys + zs < brushSizeSquared) {
                        BlockState current = this.world.getBlock(x, y, z);
                        if (current.getType() == BlockTypes.AIR) {
                            continue;
                        }
                        if (BlockHelper.isLiquid(current)) {
                            setBlockType(x, y, z, BlockTypes.AIR);
                            continue;
                        }
                        if (FLAMABLE_BLOCKS.contains(current.getType())) {
                            setBlockType(x, y, z, BlockTypes.FIRE);
                            continue;
                        }
                        double noise = this.perlin.getValue(x, y, z);
                        if (noise >= REQUIRED_OBSIDIAN_DENSITY) {
                            setBlockType(x, y, z, BlockTypes.OBSIDIAN);
                        } else if (noise >= REQUIRED_COBBLE_DENSITY) {
                            setBlockType(x, y, z, BlockTypes.COBBLESTONE);
                        } else if (noise >= REQUIRED_FIRE_DENSITY) {
                            setBlockType(x, y, z, BlockTypes.FIRE);
                        } else {
                            setBlockType(x, y, z, BlockTypes.AIR);
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
        this.heatRay(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.heatRay(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.GREEN, "Octaves: " + this.octaves);
        vm.custom(TextColors.GREEN, "Persistence: " + this.persistence);
        vm.custom(TextColors.GREEN, "Frequency: " + this.frequency);
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 1; i < par.length; i++) {
            final String parameter = par[i].toLowerCase();

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD + "Heat Ray brush Parameters:");
                v.sendMessage(TextColors.AQUA + "/b hr oct[int] -- Octaves parameter for the noise generator.");
                v.sendMessage(TextColors.AQUA + "/b hr pers[float] -- Persistence parameter for the noise generator.");
                v.sendMessage(TextColors.AQUA + "/b hr freq[float] -- Frequency parameter for the noise generator.");
            }
            if (parameter.startsWith("oct")) {
                try {
                    this.octaves = Integer.valueOf(parameter.replace("oct", ""));
                    this.perlin.setOctaveCount(this.octaves);
                    v.getVoxelMessage().custom(TextColors.GREEN + "Octaves: " + this.octaves);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid octave value.");
                }
            } else if (parameter.startsWith("pers")) {
                try {
                    this.persistence = Double.valueOf(parameter.replace("pers", ""));
                    this.perlin.setPersistence(this.persistence);
                    v.getVoxelMessage().custom(TextColors.GREEN + "Amplitude: " + this.persistence);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid amplitude value.");
                }
            } else if (parameter.startsWith("freq")) {
                try {
                    this.frequency = Double.valueOf(parameter.replace("freq", ""));
                    this.perlin.setFrequency(this.frequency);
                    v.getVoxelMessage().custom(TextColors.GREEN + "Frequency: " + this.frequency);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid frequency value.");
                }
            }
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.heatray";
    }
}
