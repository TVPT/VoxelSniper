package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class SplatterDisc extends PerformBrush {
    private int seedpercent; // Chance block on first pass is made active
    private int growpercent; // chance block on recursion pass is made active
    private int splatterrecursions; // How many times you grow the seeds
    private Random generator = new Random();

    private static int timesUsed = 0;

    public SplatterDisc() {
        this.setName("Splatter Disc");
    }

    private final void splatterDisc(final SnipeData v) {
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
        final int _brushSize = v.getBrushSize();
        final int[][] _splat = new int[2 * _brushSize + 1][2 * _brushSize + 1];

        // Seed the array
        for (int _x = 2 * _brushSize; _x >= 0; _x--) {
            for (int y = 2 * _brushSize; y >= 0; y--) {

                if (this.generator.nextInt(10000) <= this.seedpercent) {
                    _splat[_x][y] = 1;

                }
            }
        }
        // Grow the seeds
        final int _gref = this.growpercent;
        int _growcheck;
        final int[][] _tempsplat = new int[2 * _brushSize + 1][2 * _brushSize + 1];
        for (int _r = 0; _r < this.splatterrecursions; _r++) {

            this.growpercent = _gref - ((_gref / this.splatterrecursions) * (_r));
            for (int _x = 2 * _brushSize; _x >= 0; _x--) {
                for (int _y = 2 * _brushSize; _y >= 0; _y--) {

                    _tempsplat[_x][_y] = _splat[_x][_y]; // prime tempsplat

                    _growcheck = 0;
                    if (_splat[_x][_y] == 0) {
                        if (_x != 0 && _splat[_x - 1][_y] == 1) {
                            _growcheck++;
                        }
                        if (_y != 0 && _splat[_x][_y - 1] == 1) {
                            _growcheck++;
                        }
                        if (_x != 2 * _brushSize && _splat[_x + 1][_y] == 1) {
                            _growcheck++;
                        }
                        if (_y != 2 * _brushSize && _splat[_x][_y + 1] == 1) {
                            _growcheck++;
                        }

                    }

                    if (_growcheck >= 1 && this.generator.nextInt(10000) <= this.growpercent) {
                        _tempsplat[_x][_y] = 1; // prevent bleed into splat
                    }

                }

            }
            // integrate tempsplat back into splat at end of iteration
            for (int _x = 2 * _brushSize; _x >= 0; _x--) {
                for (int _y = 2 * _brushSize; _y >= 0; _y--) {

                    _splat[_x][_y] = _tempsplat[_x][_y];

                }
            }
        }
        this.growpercent = _gref;
        // Fill 1x1 holes
        for (int _x = 2 * _brushSize; _x >= 0; _x--) {
            for (int _y = 2 * _brushSize; _y >= 0; _y--) {

                if (_splat[Math.max(_x - 1, 0)][_y] == 1 && _splat[Math.min(_x + 1, 2 * _brushSize)][_y] == 1 && _splat[_x][Math.max(0, _y - 1)] == 1
                        && _splat[_x][Math.min(2 * _brushSize, _y + 1)] == 1) {
                    _splat[_x][_y] = 1;
                }

            }
        }

        // Make the changes
        final double _rpow = Math.pow(_brushSize + 1, 2);
        for (int _x = 2 * _brushSize; _x >= 0; _x--) {
            final double _xpow = Math.pow(_x - _brushSize - 1, 2);
            for (int _y = 2 * _brushSize; _y >= 0; _y--) {
                if (_splat[_x][_y] == 1 && _xpow + Math.pow(_y - _brushSize - 1, 2) <= _rpow) {
                    this.current.perform(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _brushSize + _y));
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.splatterDisc(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.splatterDisc(v);
    }
    
    @Override
    public final void info(final Message vm) {
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
    	vm.custom(ChatColor.BLUE + "Seed percent set to: " + this.seedpercent / 100 + "%");
    	vm.custom(ChatColor.BLUE + "Growth percent set to: " + this.growpercent / 100 + "%");
    	vm.custom(ChatColor.BLUE + "Recursions set to: " + this.splatterrecursions);
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GOLD + "Splatter Disc brush Parameters:");
    		v.sendMessage(ChatColor.AQUA + "/b sd s[int] -- set a seed percentage (1-9999). 100 = 1% Default is 1000");
    		v.sendMessage(ChatColor.AQUA + "/b sd g[int] -- set a growth percentage (1-9999).  Default is 1000");
    		v.sendMessage(ChatColor.AQUA + "/b sd r[int] -- set a recursion (1-10).  Default is 3");
    		return;
    	}
    	for (int _i = 1; _i < par.length; _i++) {
    		if (par[_i].startsWith("s")) {
    			final double temp = Integer.parseInt(par[_i].replace("s", ""));
    			if (temp >= 1 && temp <= 9999) {
    				v.sendMessage(ChatColor.AQUA + "Seed percent set to: " + temp / 100 + "%");
    				this.seedpercent = (int) temp;
    			} else {
    				v.sendMessage(ChatColor.RED + "Seed percent must be an integer 1-9999!");
    			}
    			continue;
    		} else if (par[_i].startsWith("g")) {
    			final double temp = Integer.parseInt(par[_i].replace("g", ""));
    			if (temp >= 1 && temp <= 9999) {
    				v.sendMessage(ChatColor.AQUA + "Growth percent set to: " + temp / 100 + "%");
    				this.growpercent = (int) temp;
    			} else {
    				v.sendMessage(ChatColor.RED + "Growth percent must be an integer 1-9999!");
    			}
    			continue;
    		} else if (par[_i].startsWith("r")) {
    			final int temp = Integer.parseInt(par[_i].replace("r", ""));
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
    public final int getTimesUsed() {
    	return SplatterDisc.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	SplatterDisc.timesUsed = tUsed;
    }
}
