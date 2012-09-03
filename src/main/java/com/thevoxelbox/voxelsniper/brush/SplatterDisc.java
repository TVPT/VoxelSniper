package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class SplatterDisc extends PerformBrush {

    protected int seedpercent; // Chance block on first pass is made active
    protected int growpercent; // chance block on recursion pass is made active
    protected int splatterrecursions; // How many times you grow the seeds
    protected Random generator = new Random();

    private static int timesUsed = 0;

    public SplatterDisc() {
        this.setName("Splatter Disc");
    }

    @Override
    public final int getTimesUsed() {
        return SplatterDisc.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        if (this.seedpercent < 1 || this.seedpercent > 9999) {
            this.seedpercent = 1000;
        }
        if (this.growpercent < 1 || this.growpercent > 9999) {
            this.growpercent = 1000;
        }
        if (this.splatterrecursions < 1 || this.splatterrecursions > 10) {
            this.splatterrecursions = 3;
        }
        vm.brushName("Splatter Disc");
        vm.size();
        // vm.voxel();
        vm.custom(ChatColor.BLUE + "Seed percent set to: " + this.seedpercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Growth percent set to: " + this.growpercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Recursions set to: " + this.splatterrecursions);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Splatter Disc brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b sd s[int] -- set a seed percentage (1-9999). 100 = 1% Default is 1000");
            v.sendMessage(ChatColor.AQUA + "/b sd g[int] -- set a growth percentage (1-9999).  Default is 1000");
            v.sendMessage(ChatColor.AQUA + "/b sd r[int] -- set a recursion (1-10).  Default is 3");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("s")) {
                final double temp = Integer.parseInt(par[x].replace("s", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.sendMessage(ChatColor.AQUA + "Seed percent set to: " + temp / 100 + "%");
                    this.seedpercent = (int) temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Seed percent must be an integer 1-9999!");
                }
                continue;
            } else if (par[x].startsWith("g")) {
                final double temp = Integer.parseInt(par[x].replace("g", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.sendMessage(ChatColor.AQUA + "Growth percent set to: " + temp / 100 + "%");
                    this.growpercent = (int) temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Growth percent must be an integer 1-9999!");
                }
                continue;
            } else if (par[x].startsWith("r")) {
                final int temp = Integer.parseInt(par[x].replace("r", ""));
                if (temp >= 1 && temp <= 10) {
                    v.sendMessage(ChatColor.AQUA + "Recursions set to: " + temp);
                    this.splatterrecursions = temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Recursions must be an integer 1-10!");
                }
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        SplatterDisc.timesUsed = tUsed;
    }

    public final void splatterdisc(final vData v) {
        if (this.seedpercent < 1 || this.seedpercent > 9999) {
            v.sendMessage(ChatColor.BLUE + "Seed percent set to: 10%");
            this.seedpercent = 1000;
        }
        if (this.growpercent < 1 || this.growpercent > 9999) {
            v.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
            this.growpercent = 1000;
        }
        if (this.splatterrecursions < 1 || this.splatterrecursions > 10) {
            v.sendMessage(ChatColor.BLUE + "Recursions set to: 3");
            this.splatterrecursions = 3;
        }
        final int bsize = v.brushSize;
        final int[][] splat = new int[2 * bsize + 1][2 * bsize + 1];

        // Seed the array
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 2 * bsize; y >= 0; y--) {

                if (this.generator.nextInt(10000) <= this.seedpercent) {
                    splat[x][y] = 1;

                }
            }
        }
        // Grow the seeds
        final int gref = this.growpercent;
        int growcheck;
        final int[][] tempsplat = new int[2 * bsize + 1][2 * bsize + 1];
        for (int r = 0; r < this.splatterrecursions; r++) {

            this.growpercent = gref - ((gref / this.splatterrecursions) * (r));
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {

                    tempsplat[x][y] = splat[x][y]; // prime tempsplat

                    growcheck = 0;
                    if (splat[x][y] == 0) {
                        if (x != 0 && splat[x - 1][y] == 1) {
                            growcheck++;
                        }
                        if (y != 0 && splat[x][y - 1] == 1) {
                            growcheck++;
                        }
                        if (x != 2 * bsize && splat[x + 1][y] == 1) {
                            growcheck++;
                        }
                        if (y != 2 * bsize && splat[x][y + 1] == 1) {
                            growcheck++;
                        }

                    }

                    if (growcheck >= 1 && this.generator.nextInt(10000) <= this.growpercent) {
                        tempsplat[x][y] = 1; // prevent bleed into splat
                    }

                }

            }
            // integrate tempsplat back into splat at end of iteration
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {

                    splat[x][y] = tempsplat[x][y];

                }
            }
        }
        this.growpercent = gref;
        // Fill 1x1 holes
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 2 * bsize; y >= 0; y--) {

                if (splat[Math.max(x - 1, 0)][y] == 1 && splat[Math.min(x + 1, 2 * bsize)][y] == 1 && splat[x][Math.max(0, y - 1)] == 1
                        && splat[x][Math.min(2 * bsize, y + 1)] == 1) {
                    splat[x][y] = 1;
                }

            }
        }

        // Make the changes
        final double rpow = Math.pow(bsize + 1, 2);
        for (int x = 2 * bsize; x >= 0; x--) {
            final double xpow = Math.pow(x - bsize - 1, 2);
            for (int y = 2 * bsize; y >= 0; y--) {
                if (splat[x][y] == 1 && xpow + Math.pow(y - bsize - 1, 2) <= rpow) {
                    this.current.perform(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY(), this.getBlockPositionZ() - bsize + y));
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.splatterdisc(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.splatterdisc(v);
    }
}
