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
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Random;

@Brush.BrushInfo(
    name = "Splatter Overlay",
    aliases = {"sover", "splatteroverlay"},
    permission = "voxelsniper.brush.splatteroverlay",
    category = Brush.BrushCategory.TERRAIN
)
public class SplatterOverlayBrush extends PerformBrush {

    private static final double GROW_PERCENT_DEFAULT = 0.1;
    private static final double SEED_PERCENT_DEFAULT = 0.1;
    private static final int RECURSIONS_MIN = 1;
    private static final int RECURSIONS_DEFAULT = 3;
    private static final int RECURSIONS_MAX = 20;
    private double seedPercent = SEED_PERCENT_DEFAULT;
    private double growPercent = GROW_PERCENT_DEFAULT;
    private int splatterRecursions = RECURSIONS_DEFAULT;
    private Random generator = new Random();
    private int depth = 3;

    public SplatterOverlayBrush() {
    }

    private void splatterOverlay(final SnipeData v, Location<World> targetBlock) {
        int size = GenericMath.floor(v.getBrushSize()) + 1;
        double sizeSquared = v.getBrushSize() * v.getBrushSize();

        int tx = targetBlock.getBlockX();
        int ty = targetBlock.getBlockY();
        int tz = targetBlock.getBlockZ();

        // 0 = not part of the overlay area
        // 1 = part of the overlay area but not marked
        // 2 = part of the overlay area and marked
        final byte[][][] splat = new byte[2 * size + 1][ty][2 * size + 1];

        // Seed the array
        int miny = WORLD_HEIGHT;
        for (int x = -size; x <= size; x++) {
            int x0 = tx + x;
            for (int z = -size; z <= size; z++) {
                int z0 = tx + z;
                if (x * x + z * z >= sizeSquared) {
                    continue;
                }
                y_search: for (int y = ty; y >= 0; y--) {
                    if (this.world.getBlockType(x0, y, z0) != BlockTypes.AIR) {
                        if (y == ty) {
                            break;
                        }
                        for (int y0 = y; y0 >= y - this.depth; y0--) {
                            if (this.world.getBlockType(x0, y, z0) == BlockTypes.AIR) {
                                break y_search;
                            }
                            if (this.generator.nextDouble() < this.growPercent) {
                                splat[x + size][y0][z + size] = 2;
                            } else {
                                splat[x + size][y0][z + size] = 1;
                            }
                            if (y0 < miny) {
                                miny = y0;
                            }
                        }
                    }
                }
            }
        }
        int y_range = ty - miny + 1;
        // Grow the seeds
        final byte[][][] tempSplat = new byte[2 * size + 1][y_range][2 * size + 1];
        int growcheck;

        for (int r = 0; r < this.splatterRecursions; r++) {
            double grow = this.growPercent - ((this.growPercent / this.splatterRecursions) * (r));
            for (int x = -size; x <= size; x++) {
                int xi = x + size;
                for (int z = -size; z <= size; z++) {
                    int zi = z + size;
                    for (int y = 0; y <= y_range; y++) {
                        int y0 = miny + y;
                        tempSplat[xi][y][zi] = splat[xi][y0][zi]; // prime
                                                                  // tempsplat
                        growcheck = 0;
                        if (splat[xi][y0][zi] != 1) {
                            continue;
                        }
                        if (xi != 0 && splat[xi - 1][y0][zi] == 2) {
                            growcheck++;
                        }
                        if (y != 0 && splat[xi][y0 - 1][zi] == 2) {
                            growcheck++;
                        }
                        if (zi != 0 && splat[xi][y0][zi - 1] == 2) {
                            growcheck++;
                        }
                        if (xi != 2 * size && splat[xi + 1][y0][zi] == 2) {
                            growcheck++;
                        }
                        if (y0 != ty && splat[xi][y0 + 1][zi] == 2) {
                            growcheck++;
                        }
                        if (zi != 2 * size && splat[xi][y0][zi + 1] == 2) {
                            growcheck++;
                        }

                        if (growcheck >= 0 && this.generator.nextDouble() <= grow) {
                            tempSplat[xi][y][zi] = 2;
                        }

                    }
                }
            }
            // integrate tempsplat back into splat at end of iterationfor (int x
            // = -size; x <= size; x++) {
            for (int x = -size; x <= size; x++) {
                int xi = x + size;
                for (int z = -size; z <= size; z++) {
                    int zi = z + size;
                    for (int y = 0; y <= y_range; y++) {
                        int y0 = miny + y;
                        splat[xi][y0][zi] = tempSplat[xi][y][zi];
                    }
                }
            }
        }
        // Fill 1x1x1 holes
        for (int x = -size; x <= size; x++) {
            int xi = x + size;
            for (int z = -size; z <= size; z++) {
                int zi = z + size;
                for (int y = 0; y <= y_range; y++) {
                    int y0 = miny + y;
                    if (splat[xi - 1][y0][zi] == 2 && splat[xi + 1][y0][zi] == 2 && splat[xi][y0 - 1][zi] == 2 && splat[xi][y0 + 1][zi] == 2
                            && splat[xi][y0][zi - 1] == 2 && splat[xi][y0][zi + 1] == 2) {
                        splat[xi][y0][zi] = 2;
                    }
                }
            }
        }

        this.undo = new Undo(GenericMath.floor(4 * Math.PI * (v.getBrushSize() + 1) * (v.getBrushSize() + 1) * (v.getBrushSize() + 1) / 3));
        // Make the changes
        for (int x = -size; x <= size; x++) {
            int xi = x + size;
            int x0 = x + tx;
            for (int z = -size; z <= size; z++) {
                int zi = z + size;
                int z0 = z + tz;
                for (int y = 0; y <= y_range; y++) {
                    int y0 = miny + y;
                    if (splat[xi][y0][zi] == 2) {
                        perform(v, x0, y0, z0);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.splatterOverlay(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        // @Usability this should also splatter into the block above the terrain
        this.splatterOverlay(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(info.name());
        vm.size();
        vm.custom(TextColors.BLUE, "Seed percent set to: " + this.seedPercent / 100 + "%");
        vm.custom(TextColors.BLUE, "Growth percent set to: " + this.growPercent / 100 + "%");
        vm.custom(TextColors.BLUE, "Recursions set to: " + this.splatterRecursions);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];
            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD, "Splatter Overlay brush parameters:");
                v.sendMessage(TextColors.AQUA, "d[int] (ex:  d3) How many blocks deep you want to replace from the surface.");
                v.sendMessage(TextColors.AQUA, "/b sover s[float] -- set a seed percentage (0 - 1). Default is 0.1");
                v.sendMessage(TextColors.AQUA, "/b sover g[float] -- set a growth percentage (0 - 1).  Default is 0.1");
                v.sendMessage(TextColors.AQUA, "/b sover r[int] -- set a recursion (1 - 10).  Default is 3");
                return;
            } else if (parameter.startsWith("d")) {
                try {
                    this.depth = Integer.parseInt(parameter.replace("d", ""));
                    if (this.depth < 1) {
                        this.depth = 1;
                    }
                    v.sendMessage(TextColors.AQUA, "Depth set to " + this.depth);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid depth provided.");
                }
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
