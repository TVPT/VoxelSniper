package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Piotr
 */
public class ShellSetBrush extends Brush {
	private static final int MAX_SIZE = 5000000;
	
    private Block block = null;
    private static int timesUsed = 0;

    public ShellSetBrush() {
        this.setName("Shell Set");
    }

    private boolean set(final Block bl, final SnipeData v) {
        if (this.block == null) {
            this.block = bl;
            return true;
        } else {
            if (!this.block.getWorld().getName().equals(bl.getWorld().getName())) {
                v.sendMessage(ChatColor.RED + "You selected points in different worlds!");
                this.block = null;
                return true;
            }

            final int _voxelMaterialId = v.getVoxelId();
            final int _voxelReplaceMaterialId = v.getReplaceId();
            
            final int _lowx = (this.block.getX() <= bl.getX()) ? this.block.getX() : bl.getX();
            final int _lowy = (this.block.getY() <= bl.getY()) ? this.block.getY() : bl.getY();
            final int _lowz = (this.block.getZ() <= bl.getZ()) ? this.block.getZ() : bl.getZ();
            final int _highx = (this.block.getX() >= bl.getX()) ? this.block.getX() : bl.getX();
            final int _highy = (this.block.getY() >= bl.getY()) ? this.block.getY() : bl.getY();
            final int _highz = (this.block.getZ() >= bl.getZ()) ? this.block.getZ() : bl.getZ();
            
            if (Math.abs(_highx - _lowx) * Math.abs(_highz - _lowz) * Math.abs(_highy - _lowy) > MAX_SIZE) {
                v.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
            } else {
                final ArrayList<Block> blocks = new ArrayList<Block>(((Math.abs(_highx - _lowx) * Math.abs(_highz - _lowz) * Math.abs(_highy - _lowy)) / 2));
                for (int _y = _lowy; _y <= _highy; _y++) {
                    for (int _x = _lowx; _x <= _highx; _x++) {
                        for (int _z = _lowz; _z <= _highz; _z++) {
                            if (this.getWorld().getBlockTypeIdAt(_x, _y, _z) == _voxelReplaceMaterialId) {
                                continue;
                            } else if (this.getWorld().getBlockTypeIdAt(_x + 1, _y, _z) == _voxelReplaceMaterialId) {
                                continue;
                            } else if (this.getWorld().getBlockTypeIdAt(_x - 1, _y, _z) == _voxelReplaceMaterialId) {
                                continue;
                            } else if (this.getWorld().getBlockTypeIdAt(_x, _y, _z + 1) == _voxelReplaceMaterialId) {
                                continue;
                            } else if (this.getWorld().getBlockTypeIdAt(_x, _y, _z - 1) == _voxelReplaceMaterialId) {
                                continue;
                            } else if (this.getWorld().getBlockTypeIdAt(_x, _y + 1, _z) == _voxelReplaceMaterialId) {
                                continue;
                            } else if (this.getWorld().getBlockTypeIdAt(_x, _y - 1, _z) == _voxelReplaceMaterialId) {
                                continue;
                            } else {
                                blocks.add(this.getWorld().getBlockAt(_x, _y, _z));
                            }
                        }
                    }
                }

                final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
                for (final Block _block : blocks) {
                    if (_block.getTypeId() != _voxelMaterialId) {
                        _undo.put(_block);
                        _block.setTypeId(_voxelMaterialId);
                    }
                }
                v.storeUndo(_undo);
                v.sendMessage(ChatColor.AQUA + "Shell complete.");
            }

            this.block = null;
            return false;
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.set(this.getTargetBlock(), v)) {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.set(this.getLastBlock(), v)) {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        }
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
    	return ShellSetBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	ShellSetBrush.timesUsed = tUsed;
    }
}
