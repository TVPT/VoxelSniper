package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Giltwist
 */
public class BlobBrush extends PerformBrush {
	private static final int GROW_PERCENT_DEFAULT = 1000;
	private static final int GROW_PERCENT_MIN = 1;
	private static final int GROW_PERCENT_MAX = 9999;
	
	private static int timesUsed = 0;
    private int growPercent = GROW_PERCENT_DEFAULT; // chance block on recursion pass is made active
    private Random randomGenerator = new Random();

    /**
     * 
     */
    public BlobBrush() {
        this.setName("Blob");
    }
    
    private final void checkValidGrowPercent(final SnipeData v) {
    	if (this.growPercent < GROW_PERCENT_MIN || this.growPercent > GROW_PERCENT_MAX) {
    		if(v != null) {
    			v.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
    		}
    		this.growPercent = GROW_PERCENT_DEFAULT;
    	}    	
    }

    private final void digBlob(final SnipeData v) {
    	final int _bSize = v.getBrushSize();
    	final int _twoBrushSize = 2 * _bSize;
    	final double _rPow = Math.pow(_bSize + 1, 2);
    	final int[][][] _splat = new int[_twoBrushSize + 1][_twoBrushSize + 1][_twoBrushSize + 1];
    	final int[][][] _tempSplat = new int[_twoBrushSize + 1][_twoBrushSize + 1][_twoBrushSize + 1];
    	int _growCheck = 0;
    	double _xPow = 0;
    	double _yPow = 0;
    	
    	this.checkValidGrowPercent(v);

        // Seed the array
        for (int _x = _twoBrushSize; _x >= 0; _x--) {
            for (int _y = _twoBrushSize; _y >= 0; _y--) {
                for (int _z = _twoBrushSize; _z >= 0; _z--) {
                    if ((_x == 0 || _y == 0 | _z == 0 || _x == 2 * _bSize || _y == 2 * _bSize || _z == 2 * _bSize) && this.randomGenerator.nextInt(10000) <= this.growPercent) {
                        _splat[_x][_y][_z] = 0;
                    } else {
                        _splat[_x][_y][_z] = 1;
                    }
                }
            }
        }

        // Grow the seed
        for (int _r = 0; _r < _bSize; _r++) {
        	
            for (int _x = _twoBrushSize; _x >= 0; _x--) {
                for (int _y = _twoBrushSize; _y >= 0; _y--) {
                    for (int _z = _twoBrushSize; _z >= 0; _z--) {
                        _tempSplat[_x][_y][_z] = _splat[_x][_y][_z];
                        _growCheck = 0;
                        if (_splat[_x][_y][_z] == 1) {
                            if (_x != 0 && _splat[_x - 1][_y][_z] == 0) {
                                _growCheck++;
                            }
                            if (_y != 0 && _splat[_x][_y - 1][_z] == 0) {
                                _growCheck++;
                            }
                            if (_z != 0 && _splat[_x][_y][_z - 1] == 0) {
                                _growCheck++;
                            }
                            if (_x != 2 * _bSize && _splat[_x + 1][_y][_z] == 0) {
                                _growCheck++;
                            }
                            if (_y != 2 * _bSize && _splat[_x][_y + 1][_z] == 0) {
                                _growCheck++;
                            }
                            if (_z != 2 * _bSize && _splat[_x][_y][_z + 1] == 0) {
                                _growCheck++;
                            }
                        }

                        if (_growCheck >= 1 && this.randomGenerator.nextInt(10000) <= this.growPercent) {
                            _tempSplat[_x][_y][_z] = 0; // prevent bleed into splat
                        }
                    }
                }
            }

            // shouldn't this just be splat = tempsplat;? -Gavjenks
            // integrate tempsplat back into splat at end of iteration
            for (int _x = _twoBrushSize; _x >= 0; _x--) {
                for (int _y = _twoBrushSize; _y >= 0; _y--) {
                    for (int _z = _twoBrushSize; _z >= 0; _z--) {
                        _splat[_x][_y][_z] = _tempSplat[_x][_y][_z];
                    }
                }
            }
        }

        // Make the changes        
        for (int _x = _twoBrushSize; _x >= 0; _x--) {
            _xPow = Math.pow(_x - _bSize - 1, 2);
            
            for (int _y = _twoBrushSize; _y >= 0; _y--) {
                _yPow = Math.pow(_y - _bSize - 1, 2);
                
                for (int z = _twoBrushSize; z >= 0; z--) {
                    if (_splat[_x][_y][z] == 1 && _xPow + _yPow + Math.pow(z - _bSize - 1, 2) <= _rPow) {
                        this.current.perform(this.clampY(this.getBlockPositionX() - _bSize + _x, this.getBlockPositionY() - _bSize + z, this.getBlockPositionZ() - _bSize + _y));
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }   

    private final void growBlob(final SnipeData v) {
    	final int _bSize = v.getBrushSize();
    	final int _twoBrushSize = 2 * _bSize;
    	final int[][][] _splat = new int[_twoBrushSize + 1][_twoBrushSize + 1][_twoBrushSize + 1];
    	final int[][][] _tempSplat = new int[2 * _bSize + 1][2 * _bSize + 1][2 * _bSize + 1];
    	final double _rPow = Math.pow(_bSize + 1, 2);
    	int _growCheck = 0;
    	double _xPow = 0;
    	double _yPow = 0;
    	
        this.checkValidGrowPercent(v);
        
        // Seed the array
        _splat[_bSize][_bSize][_bSize] = 1;

        // Grow the seed
        for (int _r = 0; _r < _bSize; _r++) {

            for (int _x = _twoBrushSize; _x >= 0; _x--) {
                for (int _y = _twoBrushSize; _y >= 0; _y--) {
                    for (int _z = _twoBrushSize; _z >= 0; _z--) {
                        _tempSplat[_x][_y][_z] = _splat[_x][_y][_z]; 
                        _growCheck = 0;
                        if (_splat[_x][_y][_z] == 0) {
                            if (_x != 0 && _splat[_x - 1][_y][_z] == 1) {
                                _growCheck++;
                            }
                            if (_y != 0 && _splat[_x][_y - 1][_z] == 1) {
                                _growCheck++;
                            }
                            if (_z != 0 && _splat[_x][_y][_z - 1] == 1) {
                                _growCheck++;
                            }
                            if (_x != 2 * _bSize && _splat[_x + 1][_y][_z] == 1) {
                                _growCheck++;
                            }
                            if (_y != 2 * _bSize && _splat[_x][_y + 1][_z] == 1) {
                                _growCheck++;
                            }
                            if (_z != 2 * _bSize && _splat[_x][_y][_z + 1] == 1) {
                                _growCheck++;
                            }
                        }

                        if (_growCheck >= 1 && this.randomGenerator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
                            _tempSplat[_x][_y][_z] = 1; // prevent bleed into splat
                        }
                    }
                }
            }

            // integrate tempsplat back into splat at end of iteration
            for (int _x = _twoBrushSize; _x >= 0; _x--) {
                for (int _y = _twoBrushSize; _y >= 0; _y--) {
                    for (int _z = _twoBrushSize; _z >= 0; _z--) {
                        _splat[_x][_y][_z] = _tempSplat[_x][_y][_z];
                    }
                }
            }
        }

        // Make the changes
        for (int _x = _twoBrushSize; _x >= 0; _x--) {
            _xPow = Math.pow(_x - _bSize - 1, 2);
            
            for (int _y = _twoBrushSize; _y >= 0; _y--) {
                _yPow = Math.pow(_y - _bSize - 1, 2);
                
                for (int z = _twoBrushSize; z >= 0; z--) {
                    if (_splat[_x][_y][z] == 1 && _xPow + _yPow + Math.pow(z - _bSize - 1, 2) <= _rPow) {
                        this.current.perform(this.clampY(this.getBlockPositionX() - _bSize + _x, this.getBlockPositionY() - _bSize + z, this.getBlockPositionZ() - _bSize + _y));
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
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
        this.checkValidGrowPercent(null);
        
        vm.brushName(this.getName());
        vm.size();
        vm.custom(ChatColor.BLUE + "Growth percent set to: " + this.growPercent / 100 + "%");
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Blob brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b blob g[int] -- set a growth percentage (1-9999).  Default is 1000");
            return;
        }
        for (int _x = 1; _x < par.length; _x++) {
            if (par[_x].startsWith("g")) {
                final double _temp = Integer.parseInt(par[_x].replace("g", ""));
                if (_temp >= GROW_PERCENT_MIN && _temp <= GROW_PERCENT_MAX) {
                    v.sendMessage(ChatColor.AQUA + "Growth percent set to: " + _temp / 100 + "%");
                    this.growPercent = (int) _temp;
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
    public final int getTimesUsed() {
        return BlobBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        BlobBrush.timesUsed = tUsed;
    }
}
