/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vSniper;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import java.util.Random;

/**
 *
 * @author Voxel
 */
public class SplatterVoxel extends PerformBrush {

    protected int seedpercent; // Chance block on first pass is made active
    protected int growpercent; // chance block on recursion pass is made active
    protected int splatterrecursions; // How many times you grow the seeds
    protected Random generator = new Random();
    
    public SplatterVoxel() {
        name = "Splatter Voxel";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        vsplatterball(v);
    }

    @Override
    public void powder(vSniper v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        vsplatterball(v);
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Splatter Voxel brush Parameters:");
            v.p.sendMessage(ChatColor.AQUA + "/b sv s[int] -- set a seed percentage (1-9999). 100 = 1% Default is 1000");
            v.p.sendMessage(ChatColor.AQUA + "/b sv g[int] -- set a growth percentage (1-9999).  Default is 1000");
            v.p.sendMessage(ChatColor.AQUA + "/b sv r[int] -- set a recursion (1-10).  Default is 3");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("s")) {
                double temp = Integer.parseInt(par[x].replace("s", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.p.sendMessage(ChatColor.AQUA + "Seed percent set to: " + temp / 100 + "%");
                    seedpercent = (int) temp;
                } else {
                    v.p.sendMessage(ChatColor.RED + "Seed percent must be an integer 1-9999!");
                }
                continue;
            } else if (par[x].startsWith("g")) {
                double temp = Integer.parseInt(par[x].replace("g", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.p.sendMessage(ChatColor.AQUA + "Growth percent set to: " + temp / 100 + "%");
                    growpercent = (int) temp;
                } else {
                    v.p.sendMessage(ChatColor.RED + "Growth percent must be an integer 1-9999!");
                }
                continue;
            } else if (par[x].startsWith("r")) {
                int temp = Integer.parseInt(par[x].replace("r", ""));
                if (temp >= 1 && temp <= 10) {
                    v.p.sendMessage(ChatColor.AQUA + "Recursions set to: " + temp);
                    splatterrecursions = temp;
                } else {
                    v.p.sendMessage(ChatColor.RED + "Recursions must be an integer 1-10!");
                }
                continue;
            } else {
                v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
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
        vm.brushName("Splatter Voxel");
        vm.size();
        //vm.voxel();
        vm.custom(ChatColor.BLUE + "Seed percent set to: " + seedpercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Growth percent set to: " + growpercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Recursions set to: " + splatterrecursions);

    }

    public void vsplatterball(vSniper v) {
        if (seedpercent < 1 || seedpercent > 9999) {
            v.p.sendMessage(ChatColor.BLUE + "Seed percent set to: 10%");
            seedpercent = 1000;
        }
        if (growpercent < 1 || growpercent > 9999) {
            v.p.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
            growpercent = 1000;
        }
        if (splatterrecursions < 1 || splatterrecursions > 10) {
            v.p.sendMessage(ChatColor.BLUE + "Recursions set to: 3");
            splatterrecursions = 3;
        }
        int bsize = v.brushSize;
        int bId = v.voxelId;
        int[][][] splat = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1];

        // Seed the array
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 2 * bsize; y >= 0; y--) {
                for (int z = 2 * bsize; z >= 0; z--) {
                    if (generator.nextInt(10000) <= seedpercent) {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }
        // Grow the seeds
        int gref = growpercent;
        int growcheck;
         int[][][] tempsplat = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1];
        for (int r = 0; r < splatterrecursions; r++) {
            
            growpercent = (int) (gref - ((gref / splatterrecursions) * (r)));
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {
                    for (int z = 2 * bsize; z >= 0; z--) {
                        tempsplat[x][y][z] = splat[x][y][z]; //prime tempsplat
                        
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

                        if (growcheck >= 1 && generator.nextInt(10000) <= growpercent) {
                            tempsplat[x][y][z] = 1; //prevent bleed into splat
                        }
                        
                    }
                }
            }
            //integrate tempsplat back into splat at end of iteration
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {
                    for (int z = 2 * bsize; z >= 0; z--) {
                        splat[x][y][z] = tempsplat[x][y][z];
                    }
                }
            }
        }
        growpercent = gref;
        // Fill 1x1x1 holes
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 2 * bsize; y >= 0; y--) {
                for (int z = 2 * bsize; z >= 0; z--) {
                    if (splat[Math.max(x - 1, 0)][y][z] == 1 && splat[Math.min(x + 1, 2 * bsize)][y][z] == 1 && splat[x][Math.max(0, y - 1)][z] == 1 && splat[x][Math.min(2 * bsize, y + 1)][z] == 1) {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }



        // Make the changes
        
        for (int x = 2 * bsize; x >= 0; x--) {
            
            for (int y = 2 * bsize; y >= 0; y--) {
                
                for (int z = 2 * bsize; z >= 0; z--) {
                    if (splat[x][y][z] == 1) {

                        current.perform(clampY( bx - bsize + x, by - bsize + z, bz - bsize + y));

                    }
                }
            }
        }
        if (current.getUndo().getSize() > 0) {
            v.hashUndo.put(v.hashEn, current.getUndo());
            v.hashEn++;
        }
    }
}
