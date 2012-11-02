package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Blend_Brushes
 */
public class BlendBallBrush extends BlendBrushBase {
	private static int timesUsed = 0;    

	/**
	 * 
	 */
    public BlendBallBrush() {
        this.setName("Blend Ball");
    }

    @Override
    protected final void blend(final SnipeData v) {    	
        final int _bSize = v.getBrushSize();
        final int _twoBrushSize = 2 * _bSize;
        final int[][][] _oldMaterials = new int[2 * (_bSize + 1) + 1][2 * (_bSize + 1) + 1][2 * (_bSize + 1) + 1]; // Array that holds the original materials plus a buffer
        final int[][][] _newMaterials = new int[_twoBrushSize + 1][_twoBrushSize + 1][_twoBrushSize + 1]; // Array that holds the blended materials
        
        // Log current materials into oldmats
        for (int _x = 0; _x <= 2 * (_bSize + 1); _x++) {
            for (int _y = 0; _y <= 2 * (_bSize + 1); _y++) {
                for (int _z = 0; _z <= 2 * (_bSize + 1); _z++) {
                    _oldMaterials[_x][_y][_z] = this.getBlockIdAt(this.getBlockPositionX() - _bSize - 1 + _x, this.getBlockPositionY() - _bSize - 1 + _y, this.getBlockPositionZ() - _bSize - 1 + _z);
                }
            }
        }

        // Log current materials into newmats
        for (int _x = 0; _x <= _twoBrushSize; _x++) {
            for (int _y = 0; _y <= _twoBrushSize; _y++) {
                for (int _z = 0; _z <= _twoBrushSize; _z++) {
                    _newMaterials[_x][_y][_z] = _oldMaterials[_x + 1][_y + 1][_z + 1];
                }
            }
        }

        // Blend materials
        for (int _x = 0; _x <= _twoBrushSize; _x++) {
            for (int _y = 0; _y <= _twoBrushSize; _y++) {
                for (int _z = 0; _z <= _twoBrushSize; _z++) {
                    final int[] _materialFrequency = new int[maxBlockMaterialID + 1]; // Array that tracks frequency of materials neighboring given block
                    int _modeMatCount = 0;
                    int _modeMatId = 0;
                    boolean _tiecheck = true;

                    for (int _m = -1; _m <= 1; _m++) {
                        for (int _n = -1; _n <= 1; _n++) {
                            for (int _o = -1; _o <= 1; _o++) {
                                if (!(_m == 0 && _n == 0 && _o == 0)) {
                                    _materialFrequency[_oldMaterials[_x + 1 + _m][_y + 1 + _n][_z + 1 + _o]]++;
                                }
                            }
                        }
                    }

                    // Find most common neighboring material.
                    for (int _i = 0; _i <= maxBlockMaterialID; _i++) {
                        if (_materialFrequency[_i] > _modeMatCount && !(this.excludeAir && _i == Material.AIR.getId())
                                && !(this.excludeWater && (_i == Material.WATER.getId() || _i == Material.STATIONARY_WATER.getId()))) {
                            _modeMatCount = _materialFrequency[_i];
                            _modeMatId = _i;
                        }
                    }
                    // Make sure there'world not a tie for most common
                    for (int _i = 0; _i < _modeMatId; _i++) {
                    	if (_materialFrequency[_i] == _modeMatCount && !(this.excludeAir && _i == Material.AIR.getId())
                                && !(this.excludeWater && (_i == Material.WATER.getId() || _i == Material.STATIONARY_WATER.getId()))) {
                    		_tiecheck = false;
                    	}
                    }

                    // Record most common neighbor material for this block
                    if (_tiecheck) {
                        _newMaterials[_x][_y][_z] = _modeMatId;
                    }
                }
            }
        }

        final Undo _undo = new Undo(this.getWorld().getName());
        final double _rPow = Math.pow(_bSize + 1, 2);
        
        // Make the changes  
        for (int _x = _twoBrushSize; _x >= 0; _x--) {
        	final double _xPow = Math.pow(_x - _bSize - 1, 2);
        	
        	for (int _y = 0; _y <= _twoBrushSize; _y++) {
        		final double _yPow = Math.pow(_y - _bSize - 1, 2);
        		
        		for (int _z = _twoBrushSize; _z >= 0; _z--) {
        			if (_xPow + _yPow + Math.pow(_z - _bSize - 1, 2) <= _rPow) {
        				if (!(this.excludeAir && _newMaterials[_x][_y][_z] == Material.AIR.getId())
        						&& !(this.excludeWater && (_newMaterials[_x][_y][_z] == Material.WATER.getId() || _newMaterials[_x][_y][_z] == Material.STATIONARY_WATER.getId()))) {
        					if (this.getBlockIdAt(this.getBlockPositionX() - _bSize + _x, this.getBlockPositionY() - _bSize + _y, this.getBlockPositionZ() - _bSize + _z) != _newMaterials[_x][_y][_z]) {
        						_undo.put(this.clampY(this.getBlockPositionX() - _bSize + _x, this.getBlockPositionY() - _bSize + _y, this.getBlockPositionZ() - _bSize + _z));
        					}
        					this.setBlockIdAt(_newMaterials[_x][_y][_z], this.getBlockPositionX() - _bSize + _x, this.getBlockPositionY() - _bSize + _y, this.getBlockPositionZ() - _bSize + _z);
        				}
        			}
        		}
        	}
        }
        v.storeUndo(_undo);    	
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Blend Ball Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b bb water -- toggle include or exclude (default: exclude) water");
            return;
        }
       
        super.parameters(par, v);
    }
    
    @Override
    public final int getTimesUsed() {
        return BlendBallBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	BlendBallBrush.timesUsed = tUsed;
    }
}
