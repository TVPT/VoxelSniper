package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * @author Gavjenks
 */
public class AntiFreeze extends Brush {
    private static int timesUsed = 0;

    public AntiFreeze() {
        this.setName("AntiFreeze");
    }

    public final void antiFreeze(final SnipeData v, final boolean bool) {
        final int _bSize = v.getBrushSize();

        final double _bPow = Math.pow(_bSize + 0.5, 2);
        for (int _x = _bSize; _x >= 0; _x--) {
            final double _xPow = Math.pow(_x, 2);
            for (int _z = _bSize; _z >= 0; _z--) {
                final double _zPow = Math.pow(_z, 2);
                if (_xPow + _zPow <= _bPow) {
                    for (int _y = 1; _y < 127; _y++) {
                        if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z) == 79 && this.getBlockIdAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z) == 0) {
                            if (bool) {
                                this.setBlockIdAt(53, this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z);
                                if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z) == 53) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z);
                                }
                                this.setBlockIdAt(53, this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() - _z);
                                if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z) == 53) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z);
                                }
                                this.setBlockIdAt(53, this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() + _z);
                                if (this.getBlockIdAt(this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z) == 53) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z);
                                }
                                this.setBlockIdAt(53, this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() - _z);
                                if (this.getBlockIdAt(this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z) == 53) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z);
                                }
                                this.getWorld().getBlockAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() - _z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() + _z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() - _z).setData((byte) 6);
                                this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z);
                            } else {

                                this.setBlockIdAt(67, this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z);
                                if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z) == 67) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z);
                                }
                                this.setBlockIdAt(67, this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() - _z);
                                if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z) == 67) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z);
                                }
                                this.setBlockIdAt(67, this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() + _z);
                                if (this.getBlockIdAt(this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z) == 67) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z);
                                }
                                this.setBlockIdAt(67, this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() - _z);
                                if (this.getBlockIdAt(this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z) == 67) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z);
                                }
                                this.getWorld().getBlockAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() - _z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() + _z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() - _z).setData((byte) 6);
                                
                                this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z);
                                
                                this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z); 
                                this.setBlockIdAt(9, this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z);

                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected final void arrow(final SnipeData v) {
    	this.antiFreeze(v, true);
    }
    
    @Override
    protected final void powder(final SnipeData v) {    	
    	this.antiFreeze(v, false);
    }

    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
        vm.brushMessage(ChatColor.GOLD
                + "Arrow overlays insible wood stairs, powder is cobble stairs.  Use whichever one you have less of in your build for easier undoing later.  This may ruin builds with ice as a structural component.  DOES NOT UNDO DIRECTLY.");
        vm.size();
    }
    
    @Override
    public final int getTimesUsed() {
        return AntiFreeze.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	AntiFreeze.timesUsed = tUsed;
    }
}
