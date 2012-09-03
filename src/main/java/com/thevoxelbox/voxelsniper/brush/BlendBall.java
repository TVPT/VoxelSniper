package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 *
 */
public class BlendBall extends Brush {
    private boolean excludeAir = true;
    private boolean excludeWater = true;

    private static int timesUsed = 0;

    public BlendBall() {
        this.setName("Blend Ball");
    }

    private final void bblend(final SnipeData v) {    	
        final int _bSize = v.getBrushSize();
        final int[][][] _oldMaterials = new int[2 * (_bSize + 1) + 1][2 * (_bSize + 1) + 1][2 * (_bSize + 1) + 1]; // Array that holds the original materials plus a buffer
        final int[][][] _newMaterials = new int[2 * _bSize + 1][2 * _bSize + 1][2 * _bSize + 1]; // Array that holds the blended materials
        int _maxMaterialID = 0; // What is the highest material ID that is a block?

        // Log current materials into oldmats
        for (int _x = 0; _x <= 2 * (_bSize + 1); _x++) {
            for (int _y = 0; _y <= 2 * (_bSize + 1); _y++) {
                for (int _z = 0; _z <= 2 * (_bSize + 1); _z++) {
                    _oldMaterials[_x][_y][_z] = this.getBlockIdAt(this.getBlockPositionX() - _bSize - 1 + _x, this.getBlockPositionY() - _bSize - 1 + _y, this.getBlockPositionZ() - _bSize - 1 + _z);
                }
            }
        }

        // Log current materials into newmats
        for (int _x = 0; _x <= 2 * _bSize; _x++) {
            for (int _y = 0; _y <= 2 * _bSize; _y++) {
                for (int _z = 0; _z <= 2 * _bSize; _z++) {
                    _newMaterials[_x][_y][_z] = _oldMaterials[_x + 1][_y + 1][_z + 1];
                }
            }
        }

        // Find highest placeable block ID
        for (int _i = 0; _i < Material.values().length; _i++) {
            if (Material.values()[_i].isBlock() && Material.values()[_i].getId() > _maxMaterialID) {
                _maxMaterialID = Material.values()[_i].getId();
            }
        }

        // Blend materials
        for (int _x = 0; _x <= 2 * _bSize; _x++) {
            for (int _y = 0; _y <= 2 * _bSize; _y++) {
                for (int _z = 0; _z <= 2 * _bSize; _z++) {
                    final int[] _materialFrequency = new int[_maxMaterialID + 1]; // Array that tracks frequency of materials neighboring given block
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
                    for (int _i = 0; _i <= _maxMaterialID; _i++) {
                        if (_materialFrequency[_i] > _modeMatCount && !(this.excludeAir && _i == 0)
                                && !(this.excludeWater && (_i == 8 || _i == 9))) {
                            _modeMatCount = _materialFrequency[_i];
                            _modeMatId = _i;
                        }
                    }
                    // Make sure there'world not a tie for most common
                    for (int i = 0; i < _modeMatId; i++) {
                        if (_materialFrequency[i] == _modeMatCount && !(this.excludeAir && i == 0)
                                && !(this.excludeWater && (i == 8 || i == 9))) {
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

        // Make the changes
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final double _rPow = Math.pow(_bSize + 1, 2);
        for (int _x = 2 * _bSize; _x >= 0; _x--) {
        	final double _xPow = Math.pow(_x - _bSize - 1, 2);
            for (int _y = 0; _y <= 2 * _bSize; _y++) {
                final double yPow = Math.pow(_y - _bSize - 1, 2);
                for (int _z = 2 * _bSize; _z >= 0; _z--) {
                    if (_xPow + yPow + Math.pow(_z - _bSize - 1, 2) <= _rPow) {
                        if (!(this.excludeAir && _newMaterials[_x][_y][_z] == 0)
                                && !(this.excludeWater && (_newMaterials[_x][_y][_z] == 8 || _newMaterials[_x][_y][_z] == 9))) {
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
    protected final void arrow(final SnipeData v) {    	
    	this.excludeAir = false;
    	this.bblend(v);
    }
    
    @Override
    protected final void powder(final SnipeData v) {    	
    	this.excludeAir = true;
    	this.bblend(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.custom(ChatColor.BLUE + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Blend Ball Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b bb water -- toggle include or exclude (default) water");
            return;
        }
        if (par[1].equalsIgnoreCase("water")) {
            this.excludeWater = !this.excludeWater;
            v.sendMessage(ChatColor.AQUA + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
        }
    }
    
    @Override
    public final int getTimesUsed() {
        return BlendBall.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	BlendBall.timesUsed = tUsed;
    }
}
