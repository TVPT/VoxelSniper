package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 * 
 * @author Voxel
 */
public class ShellBall extends Brush {
    private static int timesUsed = 0;

    public ShellBall() {
        this.setName("Shell Ball");
    }

    // parameters isn't an abstract method, gilt. You can just leave it out if there are none.
    private final void bShell(final SnipeData v) {
        final int _brushSize = v.getBrushSize();
        final int _voxelMaterialId = v.getVoxelId();
        final int _voxelReplaceMaterialId = v.getReplaceId();
        final int[][][] _oldmats = new int[2 * (_brushSize + 1) + 1][2 * (_brushSize + 1) + 1][2 * (_brushSize + 1) + 1]; // Array that holds the original materials plus a
                                                                                                          // buffer
        final int[][][] _newmats = new int[2 * _brushSize + 1][2 * _brushSize + 1][2 * _brushSize + 1]; // Array that holds the hollowed materials

        // Log current materials into oldmats
        for (int _x = 0; _x <= 2 * (_brushSize + 1); _x++) {
            for (int _y = 0; _y <= 2 * (_brushSize + 1); _y++) {
                for (int _z = 0; _z <= 2 * (_brushSize + 1); _z++) {
                    _oldmats[_x][_y][_z] = this.getBlockIdAt(this.getBlockPositionX() - _brushSize - 1 + _x, this.getBlockPositionY() - _brushSize - 1 + _y, this.getBlockPositionZ() - _brushSize - 1 + _z);
                }
            }
        }

        // Log current materials into newmats
        for (int _x = 0; _x <= 2 * _brushSize; _x++) {
            for (int _y = 0; _y <= 2 * _brushSize; _y++) {
                for (int _z = 0; _z <= 2 * _brushSize; _z++) {
                    _newmats[_x][_y][_z] = _oldmats[_x + 1][_y + 1][_z + 1];
                }
            }
        }
        
        int _temp;

        // Hollow Brush Area
        for (int _x = 0; _x <= 2 * _brushSize; _x++) {
            for (int _y = 0; _y <= 2 * _brushSize; _y++) {
                for (int _z = 0; _z <= 2 * _brushSize; _z++) {
                    _temp = 0;

                    if (_oldmats[_x + 1 + 1][_y + 1][_z + 1] == _voxelReplaceMaterialId) {
                        _temp++;
                    }
                    if (_oldmats[_x + 1 - 1][_y + 1][_z + 1] == _voxelReplaceMaterialId) {
                        _temp++;
                    }
                    if (_oldmats[_x + 1][_y + 1 + 1][_z + 1] == _voxelReplaceMaterialId) {
                        _temp++;
                    }
                    if (_oldmats[_x + 1][_y + 1 - 1][_z + 1] == _voxelReplaceMaterialId) {
                        _temp++;
                    }
                    if (_oldmats[_x + 1][_y + 1][_z + 1 + 1] == _voxelReplaceMaterialId) {
                        _temp++;
                    }
                    if (_oldmats[_x + 1][_y + 1][_z + 1 - 1] == _voxelReplaceMaterialId) {
                        _temp++;
                    }

                    if (_temp == 0) {
                        _newmats[_x][_y][_z] = _voxelMaterialId;
                    }
                }
            }
        }

        // Make the changes
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final double _rpow = Math.pow(_brushSize + 0.5, 2);
        for (int _x = 2 * _brushSize; _x >= 0; _x--) {
            final double _xpow = Math.pow(_x - _brushSize, 2);
            for (int _y = 0; _y <= 2 * _brushSize; _y++) {
                final double _ypow = Math.pow(_y - _brushSize, 2);
                for (int _z = 2 * _brushSize; _z >= 0; _z--) {
                    if (_xpow + _ypow + Math.pow(_z - _brushSize, 2) <= _rpow) {

                        if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _brushSize + _y, this.getBlockPositionZ() - _brushSize + _z) != _newmats[_x][_y][_z]) {
                            _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _brushSize + _y, this.getBlockPositionZ() - _brushSize + _z));
                        }
                        this.setBlockIdAt(_newmats[_x][_y][_z], this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _brushSize + _y, this.getBlockPositionZ() - _brushSize + _z);
                    }
                }
            }
        }
        v.storeUndo(_undo);

        v.owner().getPlayer().sendMessage(ChatColor.AQUA + "Shell complete."); // This is needed because most uses of this brush will not be sible to the
                                                                               // sniper.
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.bShell(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.bShell(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    	vm.voxel();
    	vm.replace();
    	
    }
    
    @Override
    public final int getTimesUsed() {
    	return ShellBall.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	ShellBall.timesUsed = tUsed;
    }
}
