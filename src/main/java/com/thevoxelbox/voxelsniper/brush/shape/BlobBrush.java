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
package com.thevoxelbox.voxelsniper.brush.shape;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.text.format.TextColors;

import java.util.Random;

@Brush.BrushInfo(
    name = "Blob",
    aliases = {"blob", "splatblob"},
    permission = "voxelsniper.brush.blob",
    category = Brush.BrushCategory.SHAPE
)
public class BlobBrush extends PerformBrush {

    private static final double GROW_PERCENT_DEFAULT = 10.0;
    private static final double GROW_PERCENT_MIN = 0.0;
    private static final double GROW_PERCENT_MAX = 100.0;

    private Random randomGenerator = new Random();
    private double growPercent = GROW_PERCENT_DEFAULT;

    public BlobBrush() {
    }

    private void digBlob(final SnipeData v) {
        // @Cleanup change these 3d arrays to BlockBuffers
        final int brushSize = (int) v.getBrushSize();
        final int brushSizeDoubled = 2 * brushSize;
        final int[][][] splat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
        final int[][][] tempSplat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];

        this.undo = new Undo(brushSizeDoubled * brushSizeDoubled * brushSizeDoubled);

        double grow_pct = this.growPercent / 100.0;

        // Seed the array
        for (int x = brushSizeDoubled; x >= 0; x--) {
            for (int y = brushSizeDoubled; y >= 0; y--) {
                for (int z = brushSizeDoubled; z >= 0; z--) {
                    if ((x == 0 || y == 0 | z == 0 || x == brushSizeDoubled || y == brushSizeDoubled || z == brushSizeDoubled)
                            && this.randomGenerator.nextDouble() <= grow_pct) {
                        splat[x][y][z] = 0;
                    } else {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }

        // Grow the seed
        for (int r = 0; r < brushSize; r++) {
            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    for (int z = brushSizeDoubled; z >= 0; z--) {
                        tempSplat[x][y][z] = splat[x][y][z];
                        double growCheck = 0;
                        if (splat[x][y][z] == 1) {
                            if (x != 0 && splat[x - 1][y][z] == 0) {
                                growCheck++;
                            }
                            if (y != 0 && splat[x][y - 1][z] == 0) {
                                growCheck++;
                            }
                            if (z != 0 && splat[x][y][z - 1] == 0) {
                                growCheck++;
                            }
                            if (x != 2 * brushSize && splat[x + 1][y][z] == 0) {
                                growCheck++;
                            }
                            if (y != 2 * brushSize && splat[x][y + 1][z] == 0) {
                                growCheck++;
                            }
                            if (z != 2 * brushSize && splat[x][y][z + 1] == 0) {
                                growCheck++;
                            }
                        }

                        if (growCheck >= 1 && this.randomGenerator.nextDouble() <= grow_pct) {
                            tempSplat[x][y][z] = 0; // prevent bleed into splat
                        }
                    }
                }
            }

            // integrate tempsplat back into splat at end of iteration
            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    for (int z = brushSizeDoubled; z >= 0; z--) {
                        splat[x][y][z] = tempSplat[x][y][z];
                    }
                }
            }
        }

        final double rSquared = Math.pow(brushSize + 1, 2);

        // Make the changes
        for (int x = brushSizeDoubled; x >= 0; x--) {
            final double xSquared = Math.pow(x - brushSize - 1, 2);

            for (int y = brushSizeDoubled; y >= 0; y--) {
                final double ySquared = Math.pow(y - brushSize - 1, 2);

                for (int z = brushSizeDoubled; z >= 0; z--) {
                    if (splat[x][y][z] == 1 && xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
                        perform(v, this.targetBlock.getBlockX() - brushSize + x, this.targetBlock.getBlockY() - brushSize + z,
                                this.targetBlock.getBlockZ() - brushSize + y);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    private void growBlob(final SnipeData v) {
        final int brushSize = (int) v.getBrushSize();
        final int brushSizeDoubled = 2 * brushSize;
        final int[][][] splat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
        final int[][][] tempSplat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];

        this.undo = new Undo(brushSizeDoubled * brushSizeDoubled * brushSizeDoubled);

        // Seed the array
        splat[brushSize][brushSize][brushSize] = 1;

        double grow_pct = this.growPercent / 100.0;
        
        // Grow the seed
        for (int r = 0; r < brushSize; r++) {

            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    for (int z = brushSizeDoubled; z >= 0; z--) {
                        tempSplat[x][y][z] = splat[x][y][z];
                        int growCheck = 0;
                        if (splat[x][y][z] == 0) {
                            if (x != 0 && splat[x - 1][y][z] == 1) {
                                growCheck++;
                            }
                            if (y != 0 && splat[x][y - 1][z] == 1) {
                                growCheck++;
                            }
                            if (z != 0 && splat[x][y][z - 1] == 1) {
                                growCheck++;
                            }
                            if (x != 2 * brushSize && splat[x + 1][y][z] == 1) {
                                growCheck++;
                            }
                            if (y != 2 * brushSize && splat[x][y + 1][z] == 1) {
                                growCheck++;
                            }
                            if (z != 2 * brushSize && splat[x][y][z + 1] == 1) {
                                growCheck++;
                            }
                        }

                        if (growCheck >= 1 && this.randomGenerator.nextDouble() <= grow_pct) {
                            // prevent bleed into splat
                            tempSplat[x][y][z] = 1;
                        }
                    }
                }
            }

            // integrate tempsplat back into splat at end of iteration
            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    for (int z = brushSizeDoubled; z >= 0; z--) {
                        splat[x][y][z] = tempSplat[x][y][z];
                    }
                }
            }
        }

        final double rSquared = Math.pow(brushSize + 1, 2);

        // Make the changes
        for (int x = brushSizeDoubled; x >= 0; x--) {
            final double xSquared = Math.pow(x - brushSize - 1, 2);

            for (int y = brushSizeDoubled; y >= 0; y--) {
                final double ySquared = Math.pow(y - brushSize - 1, 2);

                for (int z = brushSizeDoubled; z >= 0; z--) {
                    if (splat[x][y][z] == 1 && xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
                        perform(v, this.targetBlock.getBlockX() - brushSize + x, this.targetBlock.getBlockY() - brushSize + z,
                                this.targetBlock.getBlockZ() - brushSize + y);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.growBlob(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.digBlob(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
        vm.size();
        vm.custom(TextColors.BLUE, "Growth percent set to: " + this.growPercent + "%");
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {

        if (par.length == 0 || par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD, "Blob brush Parameters:");
            v.sendMessage(TextColors.AQUA, "/b blob g[double] -- set a growth percentage (" + GROW_PERCENT_MIN + "-" + GROW_PERCENT_MAX
                    + "). Default is " + GROW_PERCENT_DEFAULT);
            return;
        }
        if (par[0].startsWith("g")) {
            try {
                final double temp = Double.parseDouble(par[0].replace("g", ""));
                if (temp >= GROW_PERCENT_MIN && temp <= GROW_PERCENT_MAX) {
                    v.sendMessage(TextColors.AQUA, "Growth percent set to: " + temp + "%");
                    this.growPercent = temp;
                } else {
                    v.sendMessage(TextColors.RED, "Growth percent must be a number " + GROW_PERCENT_MIN + "-" + GROW_PERCENT_MAX + "!");
                }
            } catch (NumberFormatException e) {
                v.sendMessage(TextColors.RED, "Growth percent must be a number " + GROW_PERCENT_MIN + "-" + GROW_PERCENT_MAX + "!");
            }
        } else {
            v.sendMessage(TextColors.RED, "Invalid brush parameters! use the info parameter to display parameter info.");
        }
    }
}
