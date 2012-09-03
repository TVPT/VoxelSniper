package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Giltwist
 */
public class Blob extends PerformBrush {

    protected int growpercent; // chance block on recursion pass is made active
    protected Random generator = new Random();

    private static int timesUsed = 0;

    public Blob() {
        this.name = "Blob";
    }

    public final void digblob(final vData v) {
        if (this.growpercent < 1 || this.growpercent > 9999) {
            v.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
            this.growpercent = 1000;
        }

        final int bsize = v.brushSize;
        final int[][][] splat = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1];

        // Seed the array
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 2 * bsize; y >= 0; y--) {
                for (int z = 2 * bsize; z >= 0; z--) {
                    if ((x == 0 || y == 0 | z == 0 || x == 2 * bsize || y == 2 * bsize || z == 2 * bsize) && this.generator.nextInt(10000) <= this.growpercent) {
                        splat[x][y][z] = 0;
                    } else {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }
        final int[][][] tempsplat = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1];

        // Grow the seed
        int growcheck;
        for (int r = 0; r < bsize; r++) {

            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {
                    for (int z = 2 * bsize; z >= 0; z--) {
                        tempsplat[x][y][z] = splat[x][y][z]; // prime tempsplat
                        growcheck = 0;
                        if (splat[x][y][z] == 1) {
                            if (x != 0 && splat[x - 1][y][z] == 0) {
                                growcheck++;
                            }
                            if (y != 0 && splat[x][y - 1][z] == 0) {
                                growcheck++;
                            }
                            if (z != 0 && splat[x][y][z - 1] == 0) {
                                growcheck++;
                            }
                            if (x != 2 * bsize && splat[x + 1][y][z] == 0) {
                                growcheck++;
                            }
                            if (y != 2 * bsize && splat[x][y + 1][z] == 0) {
                                growcheck++;
                            }
                            if (z != 2 * bsize && splat[x][y][z + 1] == 0) {
                                growcheck++;
                            }
                        }

                        if (growcheck >= 1 && this.generator.nextInt(10000) <= this.growpercent) {
                            tempsplat[x][y][z] = 0; // prevent bleed into splat
                        }
                    }
                }
            }

            // shouldn't this just be splat = tempsplat;? -Gavjenks
            // integrate tempsplat back into splat at end of iteration
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {
                    for (int z = 2 * bsize; z >= 0; z--) {
                        splat[x][y][z] = tempsplat[x][y][z];
                    }
                }
            }
        }

        // Make the changes
        final double rpow = Math.pow(bsize + 1, 2);
        for (int x = 2 * bsize; x >= 0; x--) {
            final double xpow = Math.pow(x - bsize - 1, 2);
            for (int y = 2 * bsize; y >= 0; y--) {
                final double ypow = Math.pow(y - bsize - 1, 2);
                for (int z = 2 * bsize; z >= 0; z--) {
                    if (splat[x][y][z] == 1 && xpow + ypow + Math.pow(z - bsize - 1, 2) <= rpow) {
                        this.current.perform(this.clampY(this.bx - bsize + x, this.by - bsize + z, this.bz - bsize + y));
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final int getTimesUsed() {
        return Blob.timesUsed;
    }

    public final void growblob(final vData v) {
        if (this.growpercent < 1 || this.growpercent > 9999) {
            v.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
            this.growpercent = 1500;
        }
        final int bsize = v.brushSize;
        final int[][][] splat = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1];

        // Seed the array
        splat[bsize][bsize][bsize] = 1;
        final int[][][] tempsplat = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1];

        // Grow the seed
        int growcheck;
        for (int r = 0; r < bsize; r++) {

            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {
                    for (int z = 2 * bsize; z >= 0; z--) {
                        tempsplat[x][y][z] = splat[x][y][z]; // prime tempsplat
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
                            if (x != 2 * bsize && splat[x + 1][y][z] == 1) {
                                growcheck++;
                            }
                            if (y != 2 * bsize && splat[x][y + 1][z] == 1) {
                                growcheck++;
                            }
                            if (z != 2 * bsize && splat[x][y][z + 1] == 1) {
                                growcheck++;
                            }
                        }

                        if (growcheck >= 1 && this.generator.nextInt(10000) <= this.growpercent) {
                            tempsplat[x][y][z] = 1; // prevent bleed into splat
                        }
                    }
                }
            }

            // integrate tempsplat back into splat at end of iteration
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {
                    for (int z = 2 * bsize; z >= 0; z--) {
                        splat[x][y][z] = tempsplat[x][y][z];
                    }
                }
            }
        }

        // Make the changes
        final double rpow = Math.pow(bsize + 1, 2);
        for (int x = 2 * bsize; x >= 0; x--) {
            final double xpow = Math.pow(x - bsize - 1, 2);
            for (int y = 2 * bsize; y >= 0; y--) {
                final double ypow = Math.pow(y - bsize - 1, 2);
                for (int z = 2 * bsize; z >= 0; z--) {
                    if (splat[x][y][z] == 1 && xpow + ypow + Math.pow(z - bsize - 1, 2) <= rpow) {
                        this.current.perform(this.clampY(this.bx - bsize + x, this.by - bsize + z, this.bz - bsize + y));
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final void info(final vMessage vm) {
        if (this.growpercent < 1 || this.growpercent > 9999) {
            this.growpercent = 1500;
        }
        vm.brushName(this.name);
        vm.size();
        // vm.voxel();
        vm.custom(ChatColor.BLUE + "Growth percent set to: " + this.growpercent / 100 + "%");
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Blob brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b blob g[int] -- set a growth percentage (1-9999).  Default is 1500");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("g")) {
                final double temp = Integer.parseInt(par[x].replace("g", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.sendMessage(ChatColor.AQUA + "Growth percent set to: " + temp / 100 + "%");
                    this.growpercent = (int) temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Growth percent must be an integer 1-9999!");
                }
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Blob.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.growblob(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.digblob(v);
    }
}
