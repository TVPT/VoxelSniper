/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.util.Random;
import org.bukkit.ChatColor;

/**
 *
 * @author Voxel
 */
public class SplatterDisc extends PerformBrush {

    protected int seedpercent; // Chance block on first pass is made active
    protected int growpercent; // chance block on recursion pass is made active
    protected int splatterrecursions; // How many times you grow the seeds
    protected Random generator = new Random();

    public SplatterDisc() {
        name = "Splatter Disc";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        splatterdisc(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        splatterdisc(v);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Splatter Disc brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b sd s[int] -- set a seed percentage (1-9999). 100 = 1% Default is 1000");
            v.sendMessage(ChatColor.AQUA + "/b sd g[int] -- set a growth percentage (1-9999).  Default is 1000");
            v.sendMessage(ChatColor.AQUA + "/b sd r[int] -- set a recursion (1-10).  Default is 3");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("s")) {
                double temp = Integer.parseInt(par[x].replace("s", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.sendMessage(ChatColor.AQUA + "Seed percent set to: " + temp / 100 + "%");
                    seedpercent = (int) temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Seed percent must be an integer 1-9999!");
                }
                continue;
            } else if (par[x].startsWith("g")) {
                double temp = Integer.parseInt(par[x].replace("g", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.sendMessage(ChatColor.AQUA + "Growth percent set to: " + temp / 100 + "%");
                    growpercent = (int) temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Growth percent must be an integer 1-9999!");
                }
                continue;
            } else if (par[x].startsWith("r")) {
                int temp = Integer.parseInt(par[x].replace("r", ""));
                if (temp >= 1 && temp <= 10) {
                    v.sendMessage(ChatColor.AQUA + "Recursions set to: " + temp);
                    splatterrecursions = temp;
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
    public void info(vMessage vm) {
        if (seedpercent < 1 || seedpercent > 9999) {
            seedpercent = 1000;
        }
        if (growpercent < 1 || growpercent > 9999) {
            growpercent = 1000;
        }
        if (splatterrecursions < 1 || splatterrecursions > 10) {
            splatterrecursions = 3;
        }
        vm.brushName("Splatter Disc");
        vm.size();
        //vm.voxel();
        vm.custom(ChatColor.BLUE + "Seed percent set to: " + seedpercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Growth percent set to: " + growpercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Recursions set to: " + splatterrecursions);
    }

    public void splatterdisc(vData v) {
        if (seedpercent < 1 || seedpercent > 9999) {
            v.sendMessage(ChatColor.BLUE + "Seed percent set to: 10%");
            seedpercent = 1000;
        }
        if (growpercent < 1 || growpercent > 9999) {
            v.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
            growpercent = 1000;
        }
        if (splatterrecursions < 1 || splatterrecursions > 10) {
            v.sendMessage(ChatColor.BLUE + "Recursions set to: 3");
            splatterrecursions = 3;
        }
        int bsize = v.brushSize;
        int[][] splat = new int[2 * bsize + 1][2 * bsize + 1];

        // Seed the array
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 2 * bsize; y >= 0; y--) {

                if (generator.nextInt(10000) <= seedpercent) {
                    splat[x][y] = 1;

                }
            }
        }
        // Grow the seeds
        int gref = growpercent;
        int growcheck;
        int[][] tempsplat = new int[2 * bsize + 1][2 * bsize + 1];
        for (int r = 0; r < splatterrecursions; r++) {

            growpercent = (int) (gref - ((gref / splatterrecursions) * (r)));
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {

                    tempsplat[x][y] = splat[x][y]; //prime tempsplat

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

                    if (growcheck >= 1 && generator.nextInt(10000) <= growpercent) {
                        tempsplat[x][y] = 1; //prevent bleed into splat
                    }

                }

            }
            //integrate tempsplat back into splat at end of iteration
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {

                    splat[x][y] = tempsplat[x][y];

                }
            }
        }
        growpercent = gref;
        // Fill 1x1 holes
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 2 * bsize; y >= 0; y--) {

                if (splat[Math.max(x - 1, 0)][y] == 1 && splat[Math.min(x + 1, 2 * bsize)][y] == 1 && splat[x][Math.max(0, y - 1)] == 1 && splat[x][Math.min(2 * bsize, y + 1)] == 1) {
                    splat[x][y] = 1;
                }

            }
        }

        // Make the changes
        double rpow = Math.pow(bsize + 1, 2);
        for (int x = 2 * bsize; x >= 0; x--) {
            double xpow = Math.pow(x - bsize - 1, 2);
            for (int y = 2 * bsize; y >= 0; y--) {
                if (splat[x][y] == 1 && xpow + Math.pow(y - bsize - 1, 2) <= rpow) {
                    current.perform(clampY(bx - bsize + x, by, bz - bsize + y));
                }
            }
        }
        v.storeUndo(current.getUndo());
    }
    
    private static int timesUsed = 0;
	
    @Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
