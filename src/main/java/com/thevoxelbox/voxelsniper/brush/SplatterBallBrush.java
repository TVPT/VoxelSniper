package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Random;

/**
 * Places a number of seeds in the area and then grows them over a number of
 * passes.
 */
public class SplatterBallBrush extends PerformBrush {

    private static final double GROW_PERCENT_DEFAULT = 0.1;
    private static final double SEED_PERCENT_DEFAULT = 0.1;
    private static final int RECURSIONS_MIN = 1;
    private static final int RECURSIONS_DEFAULT = 3;
    private static final int RECURSIONS_MAX = 20;
    private double seedPercent = SEED_PERCENT_DEFAULT;
    private double growPercent = GROW_PERCENT_DEFAULT;
    private int splatterRecursions = RECURSIONS_DEFAULT;
    private Random generator = new Random();

    public SplatterBallBrush() {
        this.setName("Splatter Ball");
    }

    private void splatterBall(final SnipeData v, Location<World> targetBlock) {
        int size = GenericMath.floor(v.getBrushSize()) + 1;
        final int[][][] splat = new int[2 * size][2 * size][2 * size];

        // Seed the array
        for (int x = 2 * size; x >= 0; x--) {
            for (int y = 2 * size; y >= 0; y--) {
                for (int z = 2 * size; z >= 0; z--) {
                    if (this.generator.nextDouble() <= this.seedPercent) {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }
        // Grow the seeds
        final int[][][] tempSplat = new int[2 * size][2 * size][2 * size];
        int growcheck;

        for (int r = 0; r < this.splatterRecursions; r++) {
            double grow = this.growPercent - ((this.growPercent / this.splatterRecursions) * (r));
            for (int x = 2 * size; x >= 0; x--) {
                for (int y = 2 * size; y >= 0; y--) {
                    for (int z = 2 * size; z >= 0; z--) {
                        tempSplat[x][y][z] = splat[x][y][z]; // prime tempsplat

                        growcheck = 0;
                        if (splat[x][y][z] == 0) {
                            if (x != 0 && splat[x - 1][y][z] == 1) {
                                growcheck++;
                            }
                            if (y != 0 && splat[x][y - 1][z] == 1) {
                                growcheck++;
                            }
                            if (z != 0 && splat[x][y][z - 1] == 1) {
                                growcheck++;
                            }
                            if (x != 2 * v.getBrushSize() && splat[x + 1][y][z] == 1) {
                                growcheck++;
                            }
                            if (y != 2 * v.getBrushSize() && splat[x][y + 1][z] == 1) {
                                growcheck++;
                            }
                            if (z != 2 * v.getBrushSize() && splat[x][y][z + 1] == 1) {
                                growcheck++;
                            }
                        }

                        if (growcheck >= 0 && this.generator.nextDouble() <= grow) {
                            tempSplat[x][y][z] = 1;
                        }

                    }
                }
            }
            // integrate tempsplat back into splat at end of iteration
            for (int x = 2 * size; x >= 0; x--) {
                for (int y = 2 * size; y >= 0; y--) {
                    for (int z = 2 * size; z >= 0; z--) {
                        splat[x][y][z] = tempSplat[x][y][z];
                    }
                }
            }
        }
        // Fill 1x1x1 holes
        for (int x = 2 * size; x >= 0; x--) {
            for (int y = size; y >= 0; y--) {
                for (int z = size; z >= 0; z--) {
                    if (splat[Math.max(x - 1, 0)][y][z] == 1 && splat[Math.min(x + 1, 2 * size)][y][z] == 1
                            && splat[x][Math.max(0, y - 1)][z] == 1 && splat[x][Math.min(2 * size, y + 1)][z] == 1) {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }

        // Make the changes
        final double rSquared = Math.pow(v.getBrushSize() + 1, 2);

        for (int x = 2 * size; x >= 0; x--) {
            final double xSquared = (x - size) * (x - size);
            int x0 = x - size + this.targetBlock.getBlockX();
            for (int y = 2 * size; y >= 0; y--) {
                final double ySquared = (y - size) * (y - size);
                int y0 = y - size + this.targetBlock.getBlockY();

                for (int z = 2 * size; z >= 0; z--) {
                    final double zSquared = (z - size) * (z - size);
                    int z0 = z - size + this.targetBlock.getBlockZ();
                    if (splat[x][y][z] == 1 && xSquared + ySquared + zSquared <= rSquared) {
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
        this.splatterBall(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.splatterBall(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName("Splatter Ball");
        vm.size();
        vm.custom(TextColors.BLUE, "Seed percent set to: " + this.seedPercent / 100 + "%");
        vm.custom(TextColors.BLUE, "Growth percent set to: " + this.growPercent / 100 + "%");
        vm.custom(TextColors.BLUE, "Recursions set to: " + this.splatterRecursions);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 1; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD, "Splatter Ball brush Parameters:");
                v.sendMessage(TextColors.AQUA, "/b sb s[int] -- set a seed percentage (1-9999). 100 = 1% Default is 1000");
                v.sendMessage(TextColors.AQUA, "/b sb g[int] -- set a growth percentage (1-9999).  Default is 1000");
                v.sendMessage(TextColors.AQUA, "/b sb r[int] -- set a recursion (1-10).  Default is 3");
                return;
            } else if (parameter.startsWith("s")) {
                try {
                    final double temp = Double.parseDouble(parameter.replace("s", ""));

                    if (temp >= 0 && temp <= 1) {
                        v.sendMessage(TextColors.AQUA, "Seed percent set to: " + temp * 100 + "%");
                        this.seedPercent = (int) temp;
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
                        this.growPercent = (int) temp;
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

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.splatterball";
    }
}
