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
 * @author Giltwist
 */
public class Blob extends PerformBrush {

    protected int growpercent; // chance block on recursion pass is made active
    protected Random generator = new Random();

    public Blob() {
        name = "Blob";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        growblob(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        digblob(v);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Blob brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b blob g[int] -- set a growth percentage (1-9999).  Default is 1500");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("g")) {
                double temp = Integer.parseInt(par[x].replace("g", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.sendMessage(ChatColor.AQUA + "Growth percent set to: " + temp / 100 + "%");
                    growpercent = (int) temp;
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
    public void info(vMessage vm) {
        if (growpercent < 1 || growpercent > 9999) {
            growpercent = 1500;
        }
        vm.brushName(name);
        vm.size();
        //vm.voxel();
        vm.custom(ChatColor.BLUE + "Growth percent set to: " + growpercent / 100 + "%");
    }

    public void growblob(vData v) {
        if (growpercent < 1 || growpercent > 9999) {
            v.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
            growpercent = 1500;
        }
        int bsize = v.brushSize;
        int[][][] splat = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1];

        // Seed the array
        splat[bsize][bsize][bsize] = 1;
        int[][][] tempsplat = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1];

        // Grow the seed
        int growcheck;
        for (int r = 0; r < bsize; r++) {

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

        // Make the changes
        double rpow = Math.pow(bsize + 1, 2);
        for (int x = 2 * bsize; x >= 0; x--) {
            double xpow = Math.pow(x - bsize - 1, 2);
            for (int y = 2 * bsize; y >= 0; y--) {
                double ypow = Math.pow(y - bsize - 1, 2);
                for (int z = 2 * bsize; z >= 0; z--) {
                    if (splat[x][y][z] == 1 && xpow + ypow + Math.pow(z - bsize - 1, 2) <= rpow) {
                        current.perform(clampY(bx - bsize + x, by - bsize + z, bz - bsize + y));
                    }
                }
            }
        }

        v.storeUndo(current.getUndo());
    }

    public void digblob(vData v) {
        if (growpercent < 1 || growpercent > 9999) {
            v.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
            growpercent = 1000;
        }

        int bsize = v.brushSize;
        int[][][] splat = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1];

        // Seed the array
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 2 * bsize; y >= 0; y--) {
                for (int z = 2 * bsize; z >= 0; z--) {
                    if ((x == 0 || y == 0 | z == 0 || x == 2 * bsize || y == 2 * bsize || z == 2 * bsize) && generator.nextInt(10000) <= growpercent) {
                        splat[x][y][z] = 0;
                    } else {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }
        int[][][] tempsplat = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1];

        // Grow the seed
        int growcheck;
        for (int r = 0; r < bsize; r++) {

            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {
                    for (int z = 2 * bsize; z >= 0; z--) {
                        tempsplat[x][y][z] = splat[x][y][z]; //prime tempsplat
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

                        if (growcheck >= 1 && generator.nextInt(10000) <= growpercent) {
                            tempsplat[x][y][z] = 0; //prevent bleed into splat
                        }
                    }
                }
            }

            //shouldn't this just be splat = tempsplat;? -Gavjenks
            //integrate tempsplat back into splat at end of iteration
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {
                    for (int z = 2 * bsize; z >= 0; z--) {
                        splat[x][y][z] = tempsplat[x][y][z];
                    }
                }
            }
        }

        // Make the changes
        double rpow = Math.pow(bsize + 1, 2);
        for (int x = 2 * bsize; x >= 0; x--) {
            double xpow = Math.pow(x - bsize - 1, 2);
            for (int y = 2 * bsize; y >= 0; y--) {
                double ypow = Math.pow(y - bsize - 1, 2);
                for (int z = 2 * bsize; z >= 0; z--) {
                    if (splat[x][y][z] == 1 && xpow + ypow + Math.pow(z - bsize - 1, 2) <= rpow) {
                        current.perform(clampY(bx - bsize + x, by - bsize + z, bz - bsize + y));
                    }
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
