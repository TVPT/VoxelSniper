package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 *
 */
public class BlendVoxelDisc extends Brush {
    private boolean excludeAir = true;
    private boolean excludeWater = true;

    private static int timesUsed = 0;

    public BlendVoxelDisc() {
        this.setName("Blend Voxel Disc");
    }     

    private final void vdblend(final SnipeData v) {
        final int _brushSize = v.getBrushSize();
        final int[][] _oldMaterials = new int[2 * (_brushSize + 1) + 1][2 * (_brushSize + 1) + 1]; // Array that holds the original materials plus a buffer
        final int[][] _newMaterials = new int[2 * _brushSize + 1][2 * _brushSize + 1]; // Array that holds the blended materials
        int _maxMaterialId = 0; // What is the highest material ID that is a block?

        // Log current materials into oldmats
        for (int _x = 0; _x <= 2 * (_brushSize + 1); _x++) {
            for (int _z = 0; _z <= 2 * (_brushSize + 1); _z++) {
                _oldMaterials[_x][_z] = this.getBlockIdAt(this.getBlockPositionX() - _brushSize - 1 + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _brushSize - 1 + _z);
            }
        }

        // Log current materials into newmats
        for (int _x = 0; _x <= 2 * _brushSize; _x++) {
            for (int _z = 0; _z <= 2 * _brushSize; _z++) {
                _newMaterials[_x][_z] = _oldMaterials[_x + 1][_z + 1];
            }
        }

        // Find highest placeable block ID
        for (int _i = 0; _i < Material.values().length; _i++) {
            if (Material.values()[_i].isBlock() && Material.values()[_i].getId() > _maxMaterialId) {
                _maxMaterialId = Material.values()[_i].getId();
            }
        }

        // Blend materials
        for (int _x = 0; _x <= 2 * _brushSize; _x++) {
            for (int _z = 0; _z <= 2 * _brushSize; _z++) {
                final int[] _materialFrequency = new int[_maxMaterialId + 1]; // Array that tracks frequency of materials neighboring given block
                int _modeMatCount = 0;
                int _modeMatId = 0;
                boolean _tiecheck = true;

                for (int _m = -1; _m <= 1; _m++) {
                    for (int _n = -1; _n <= 1; _n++) {
                        if (!(_m == 0 && _n == 0)) {
                            _materialFrequency[_oldMaterials[_x + 1 + _m][_z + 1 + _n]]++;
                        }
                    }
                }

                // Find most common neighboring material.
                for (int _i = 0; _i <= _maxMaterialId; _i++) {
                    if (_materialFrequency[_i] > _modeMatCount && !(this.excludeAir && _i == 0)
                            && !(this.excludeWater && (_i == 8 || _i == 9))) {
                        _modeMatCount = _materialFrequency[_i];
                        _modeMatId = _i;
                    }
                }
                // Make sure there'world not a tie for most common
                for (int _i = 0; _i < _modeMatId; _i++) {
                    if (_materialFrequency[_i] == _modeMatCount && !(this.excludeAir && _i == 0)
                            && !(this.excludeWater && (_i == 8 || _i == 9))) {
                        _tiecheck = false;
                    }
                }

                // Record most common neighbor material for this block
                if (_tiecheck) {
                    _newMaterials[_x][_z] = _modeMatId;
                }
            }
        }

        // Make the changes
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = 2 * _brushSize; _x >= 0; _x--) {
            for (int _z = 2 * _brushSize; _z >= 0; _z--) {

                if (!(this.excludeAir && _newMaterials[_x][_z] == 0)
                        && !(this.excludeWater && (_newMaterials[_x][_z] == 8 || _newMaterials[_x][_z] == 9))) {
                    if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _brushSize + _z) != _newMaterials[_x][_z]) {
                        _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _brushSize + _z));
                    }
                    this.setBlockIdAt(_newMaterials[_x][_z], this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _brushSize + _z);

                }
            }
        }
        v.storeUndo(_undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.excludeAir = false;
        this.vdblend(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.excludeAir = true;
        this.vdblend(v);
    }
    
    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.custom(ChatColor.BLUE + "Water Mode: " + this.excludeWater);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Blend Voxel Disc Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b bvd water -- toggle include or exclude (default) water");
            return;
        }  
        
        if (par[1].equalsIgnoreCase("water")) {
            this.excludeWater = !this.excludeWater;
            v.sendMessage(ChatColor.AQUA + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
        }
    }
    
    @Override
    public final int getTimesUsed() {
        return BlendVoxelDisc.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        BlendVoxelDisc.timesUsed = tUsed;
    }
}
