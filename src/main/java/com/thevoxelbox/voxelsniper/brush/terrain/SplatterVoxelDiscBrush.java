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

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import com.flowpowered.math.GenericMath;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Random;

@Brush.BrushInfo(
    name = "Splatter Voxel Disc",
    aliases = {"svd", "splattervoxeldisc"},
    permission = "voxelsniper.brush.splattervoxeldisc",
    category = Brush.BrushCategory.TERRAIN
)
public class SplatterVoxelDiscBrush extends PerformBrush {

    private static final double GROW_PERCENT_DEFAULT = 0.1;
    private static final double SEED_PERCENT_DEFAULT = 0.1;
    private static final int RECURSIONS_MIN = 1;
    private static final int RECURSIONS_DEFAULT = 3;
    private static final int RECURSIONS_MAX = 20;
    private double seedPercent = SEED_PERCENT_DEFAULT;
    private double growPercent = GROW_PERCENT_DEFAULT;
    private int splatterRecursions = RECURSIONS_DEFAULT;
    private Random generator = new Random();

    public SplatterVoxelDiscBrush() {
    }

    private void splatterVoxelDisc(final SnipeData v, Location<World> targetBlock) {
        int size = (int) Math.round(v.getBrushSize());
        final boolean[][] splat = new boolean[2 * size + 1][2 * size + 1];

        // @Cleanup: a 3d bitset would make this a lot smaller in memory
        // footprint

        // Seed the array
        for (int x = 2 * size; x >= 0; x--) {
            for (int z = 2 * size; z >= 0; z--) {
                if (this.generator.nextDouble() <= this.seedPercent) {
                    splat[x][z] = true;
                }
            }
        }
        // Grow the seeds
        final boolean[][] tempSplat = new boolean[2 * size + 1][2 * size + 1];
        int growcheck;

        for (int r = 0; r < this.splatterRecursions; r++) {
            double grow = this.growPercent - ((this.growPercent / this.splatterRecursions) * (r));
            for (int x = 2 * size; x >= 0; x--) {
                for (int z = 2 * size; z >= 0; z--) {
                    tempSplat[x][z] = splat[x][z]; // prime tempsplat

                    growcheck = 0;
                    if (!splat[x][z]) {
                        if (x != 0 && splat[x - 1][z]) {
                            growcheck++;
                        }
                        if (z != 0 && splat[x][z - 1]) {
                            growcheck++;
                        }
                        if (x != 2 * size && splat[x + 1][z]) {
                            growcheck++;
                        }
                        if (z != 2 * size && splat[x][z + 1]) {
                            growcheck++;
                        }
                    }

                    if (growcheck >= 0 && this.generator.nextDouble() <= grow) {
                        tempSplat[x][z] = true;
                    }
                }
            }
            // integrate tempsplat back into splat at end of iteration
            for (int x = 2 * size; x >= 0; x--) {
                for (int z = 2 * size; z >= 0; z--) {
                    splat[x][z] = tempSplat[x][z];
                }
            }
        }
        // Fill 1x1 holes
        for (int x = 2 * size - 1; x >= 1; x--) {
            for (int z = size - 1; z >= 1; z--) {
                if (splat[x - 1][z] && splat[x + 1][z] && splat[x][z - 1] && splat[x][z + 1]) {
                    splat[x][z] = true;
                }
            }
        }

        this.undo = new Undo(GenericMath.floor(2 * Math.PI * (v.getBrushSize() + 1) * (v.getBrushSize() + 1)));

        // Make the changes
        for (int x = 2 * size; x >= 0; x--) {
            int x0 = x - size + targetBlock.getBlockX();
            for (int z = 2 * size; z >= 0; z--) {
                int z0 = z - size + targetBlock.getBlockZ();
                if (splat[x][z]) {
                    perform(v, x0, targetBlock.getBlockY(), z0);
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;

    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.splatterVoxelDisc(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.splatterVoxelDisc(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName("Splatter VoxelDisc");
        vm.size();
        vm.custom(TextColors.BLUE, "Seed percent set to: " + this.seedPercent * 100 + "%");
        vm.custom(TextColors.BLUE, "Growth percent set to: " + this.growPercent * 100 + "%");
        vm.custom(TextColors.BLUE, "Recursions set to: " + this.splatterRecursions);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD, "Splatter VoxelDisc brush Parameters:");
                v.sendMessage(TextColors.AQUA, "/b svd s[float] -- set a seed percentage (0 - 1). Default is 0.1");
                v.sendMessage(TextColors.AQUA, "/b svd g[float] -- set a growth percentage (0 - 1). Default is 0.1");
                v.sendMessage(TextColors.AQUA, "/b svd r[int] -- set a recursion (1-10).  Default is 3");
                return;
            } else if (parameter.startsWith("s")) {
                try {
                    final double temp = Double.parseDouble(parameter.replace("s", ""));

                    if (temp >= 0 && temp <= 1) {
                        v.sendMessage(TextColors.AQUA, "Seed percent set to: " + temp * 100 + "%");
                        this.seedPercent = temp;
                    } else {
                        v.sendMessage(TextColors.RED, "Seed percent must be a decimal between 0 and 1!");
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid seed percent.");
                }
            } else if (parameter.startsWith("g")) {
                try {
                    final double temp = Double.parseDouble(parameter.replace("g", ""));

                    if (temp >= 0 && temp <= 1) {
                        v.sendMessage(TextColors.AQUA, "Growth percent set to: " + temp * 100 + "%");
                        this.growPercent = temp;
                    } else {
                        v.sendMessage(TextColors.RED, "Growth percent must be a decimal between 0 and 1!");
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid growth percent.");
                }
            } else if (parameter.startsWith("r")) {
                try {
                    final int temp = Integer.parseInt(parameter.replace("r", ""));
                    if (temp >= RECURSIONS_MIN && temp <= RECURSIONS_MAX) {
                        v.sendMessage(TextColors.AQUA, "Recursions set to: " + temp);
                        this.splatterRecursions = temp;
                    } else {
                        v.sendMessage(TextColors.RED, "Recursions must be an integer " + RECURSIONS_MIN + "-" + RECURSIONS_MAX);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid recursion count.");
                }
            } else {
                v.sendMessage(TextColors.RED, "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }
}
