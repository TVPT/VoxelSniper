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

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
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

    private void splatterOverlay(final SnipeData v, Location<World> targetBlock, boolean isOffset) {
        int size = GenericMath.floor(v.getBrushSize()) + 1;
        double sizeSquared = v.getBrushSize() * v.getBrushSize();

        int targetX = targetBlock.getBlockX();
        int targetY = targetBlock.getBlockY();
        int targetZ = targetBlock.getBlockZ();

        // 0 = not part of the overlay area
        // 1 = part of the overlay area but not marked
        // 2 = part of the overlay area and marked
        final byte[][][] splatSeeds = new byte[2 * size + 1][targetY + 2][2 * size + 1];

        // Seed the array
        int miny = WORLD_HEIGHT;
        for (int x = -size; x <= size; x++) {
            int x0 = targetX + x;
            for (int z = -size; z <= size; z++) {
                int z0 = targetZ + z;
                // If we are outside the circle based on the target block or the block at the top of the current column
                // of blocks does not have air above it, don't do anything with this column
                if (x * x + z * z >= sizeSquared ||
                    this.world.getBlockType(x0, targetY, z0) != BlockTypes.AIR &&
                    this.world.getBlockType(x0, targetY + 1, z0) != BlockTypes.AIR) {
                    continue;
                }

                int columnTop = targetY;
                while (columnTop >= 0 && this.world.getBlockType(x0, columnTop, z0) == BlockTypes.AIR) {
                    columnTop--;
                }

                if (columnTop > -1) {
                    if (isOffset) {
                        columnTop += 1;
                    }
                    int maxDepth = Math.max(columnTop - this.depth + 1, 0);
                    for (int y0 = columnTop; y0 >= maxDepth; y0--) {
                        if ((!isOffset || y0 != columnTop) && this.world.getBlockType(x0, y0, z0) == BlockTypes.AIR) {
                            break;
                        }

                        if (this.generator.nextDouble() < this.seedPercent) {
                            splatSeeds[x + size][y0][z + size] = 2;
                        } else {
                            splatSeeds[x + size][y0][z + size] = 1;
                        }

                        if (y0 < miny) {
                            miny = y0;
                        }
                    }
                }
            }
        }
        // Grow the seeds
        List<Integer[]> newSeedPoints = new ArrayList<>();

        for (int r = 0; r < this.splatterRecursions; r++) {
            double grow = this.growPercent - ((this.growPercent / this.splatterRecursions) * (r));
            for (int xi = 0; xi < size * 2 + 1; xi++) {
                for (int zi = 0; zi < size * 2 + 1; zi++) {
                    for (int y = miny; y <= targetY; y++) {
                        // If the current block is in the overlay area and one of the adjacent blocks is seeded, see
                        // if we should seed the current block as well
                        if (splatSeeds[xi][y][zi] == 1 &&
                                adjacentBlockIsSeeded(splatSeeds, xi, y, zi) &&
                                this.generator.nextDouble() <= grow) {
                            newSeedPoints.add(
                                    new Integer[]{xi, y, zi}
                            );
                        }
                    }
                }
            }

            for (Integer[] coords : newSeedPoints) {
                int x = coords[0], y = coords[1], z = coords[2];
                splatSeeds[x][y][z] = 2;
            }

            newSeedPoints.clear();
        }
        // Fill 1x1x1 holes
        for (int xi = 0; xi < 2 * size + 1; xi++) {
            for (int zi = 0; zi < 2 * size + 1; zi++) {
                for (int yi = miny; yi <= targetY; yi++) {
                    if (splatSeeds[xi][yi][zi] == 1 && allAdjacentBlocksAreSeeded(splatSeeds, xi, yi, zi)) {
                        splatSeeds[xi][yi][zi] = 2;
                    }
                }
            }
        }

        this.undo = new Undo(GenericMath.floor(Math.PI * 4 * size * size * depth));
        // Make the changes
        for (int x = -size; x <= size; x++) {
            int xi = x + size;
            int x0 = x + targetX;
            for (int z = -size; z <= size; z++) {
                int zi = z + size;
                int z0 = z + targetZ;
                for (int y = miny; y <= targetY; y++) {
                    if (splatSeeds[xi][y][zi] == 2) {
                        perform(v, x0, y, z0);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    private boolean adjacentBlockIsSeeded(final byte[][][] seeds, int x, int y, int z) {
        return x > 0 && seeds[x - 1][y][z] == 2 ||
                y > 0 && seeds[x][y - 1][z] == 2 ||
                z > 0 && seeds[x][y][z - 1] == 2 ||
                x < seeds.length - 1 && seeds[x + 1][y][z] == 2 ||
                y < seeds[0].length - 1 && seeds[x][y + 1][z] == 2 ||
                z < seeds[0][0].length - 1 && seeds[x][y][z + 1] == 2;
    }

    private boolean allAdjacentBlocksAreSeeded(final byte[][][] seeds, int x, int y, int z) {
        return (x < 0 || seeds[x - 1][y][z] == 2) &&
                (y < 0 || seeds[x][y - 1][z] == 2) &&
                (z < 0 || seeds[x][y][z - 1] == 2) &&
                (x >= seeds.length - 1 || seeds[x + 1][y][z] == 2) &&
                (y >= seeds[0].length - 1 || seeds[x][y + 1][z] == 2) &&
                (z >= seeds[0][0].length || seeds[x][y][z + 1] == 2);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.splatterOverlay(v, this.targetBlock, false);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.splatterOverlay(v, this.lastBlock, true);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
        vm.size();
        vm.custom(TextColors.BLUE, "Seed percent set to: " + this.seedPercent * 100 + "%");
        vm.custom(TextColors.BLUE, "Growth percent set to: " + this.growPercent * 100 + "%");
        vm.custom(TextColors.BLUE, "Recursions set to: " + this.splatterRecursions);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (String parameter : par) {
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
