package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Shell_Brushes
 * @author Voxel
 */
public class ShellVoxelBrush extends Brush {
    private static int timesUsed = 0;

    /**
     * 
     */
    public ShellVoxelBrush() {
        this.setName("Shell Voxel");
    }

    private void vShell(final SnipeData v) {
        final int _brushSize = v.getBrushSize();
        final int _twoBrushSize = 2 * _brushSize;
        final int _voxelMaterialId = v.getVoxelId();
        final int _voxelReplaceMaterialId = v.getReplaceId();
        final int[][][] _oldmats = new int[2 * (_brushSize + 1) + 1][2 * (_brushSize + 1) + 1][2 * (_brushSize + 1) + 1]; // Array that holds the original materials plus a  buffer
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
        for (int _x = 0; _x <= _twoBrushSize; _x++) {
            for (int _y = 0; _y <= _twoBrushSize; _y++) {
                for (int _z = 0; _z <= _twoBrushSize; _z++) {
                    _newmats[_x][_y][_z] = _oldmats[_x + 1][_y + 1][_z + 1];
                }
            }
        }
        int _temp;

        // Hollow Brush Area
        for (int _x = 0; _x <= _twoBrushSize; _x++) {
            for (int _y = 0; _y <= _twoBrushSize; _y++) {
                for (int _z = 0; _z <= _twoBrushSize; _z++) {
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

        for (int _x = _twoBrushSize; _x >= 0; _x--) {
            for (int _y = 0; _y <= _twoBrushSize; _y++) {
                for (int _z = _twoBrushSize; _z >= 0; _z--) {

                    if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _brushSize + _y, this.getBlockPositionZ() - _brushSize + _z) != _newmats[_x][_y][_z]) {
                        _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _brushSize + _y, this.getBlockPositionZ() - _brushSize + _z));
                    }
                    this.setBlockIdAt(_newmats[_x][_y][_z], this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _brushSize + _y, this.getBlockPositionZ() - _brushSize + _z);
                }
            }
        }
        v.storeUndo(_undo);

        v.owner().getPlayer().sendMessage(ChatColor.AQUA + "Shell complete.");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.vShell(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.vShell(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    	vm.voxel();
    	vm.replace();
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GOLD + "Shell Voxel Parameters:");
    	} else {
    		v.sendMessage(ChatColor.RED + "Invalid parameter - see the info message for help.");
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return ShellVoxelBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	ShellVoxelBrush.timesUsed = tUsed;
    }
}
