package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * @author Gavjenks
 */
public class AntiFreezeBrush extends Brush {
	private static final int INVISIBLE_DATA_VALUE = 6;
	
    private static int timesUsed = 0;

    /**
     * 
     */
    public AntiFreezeBrush() {
        this.setName("Anti Freeze");
    }

    private final void antiFreeze(final SnipeData v, final int invisibleOverlayMaterialId) {
        final int _bSize = v.getBrushSize();
        final double _bPow = Math.pow(_bSize + 0.5, 2);
        double _xPow = 0;
        double _zPow = 0;
        
        for (int _x = _bSize; _x >= 0; _x--) {
            _xPow = Math.pow(_x, 2);
            
            for (int _z = _bSize; _z >= 0; _z--) {
                _zPow = Math.pow(_z, 2);

				if (_xPow + _zPow <= _bPow) {
					for (int _y = 1; _y < v.getWorld().getMaxHeight(); _y++) {
						if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z) == Material.ICE.getId()
								&& this.getBlockIdAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z) == Material.AIR.getId()) {
							
							this.setBlockIdAt(invisibleOverlayMaterialId, this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z);
							if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z) == invisibleOverlayMaterialId) {
								this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z);
							}
							
							this.setBlockIdAt(invisibleOverlayMaterialId, this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() - _z);
							if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z) == invisibleOverlayMaterialId) {
								this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z);
							}
							
							this.setBlockIdAt(invisibleOverlayMaterialId, this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() + _z);
							if (this.getBlockIdAt(this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z) == invisibleOverlayMaterialId) {
								this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z);
							}
							
							this.setBlockIdAt(invisibleOverlayMaterialId, this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() - _z);
							if (this.getBlockIdAt(this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z) == invisibleOverlayMaterialId) {
								this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z);
							}
							
							this.getWorld().getBlockAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z).setData((byte) INVISIBLE_DATA_VALUE);
							this.getWorld().getBlockAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() - _z).setData((byte) INVISIBLE_DATA_VALUE);
							this.getWorld().getBlockAt(this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() + _z).setData((byte) INVISIBLE_DATA_VALUE);
							this.getWorld().getBlockAt(this.getBlockPositionX() - _x, _y + 1, this.getBlockPositionZ() - _z).setData((byte) INVISIBLE_DATA_VALUE);

							this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z);
							this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z);
							this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z);
							this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z);

							this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z);
							this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() - _z);
							this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() + _z);
							this.setBlockIdAt(Material.STATIONARY_WATER.getId(), this.getBlockPositionX() - _x, _y, this.getBlockPositionZ() - _z);

						}
					}
				}
            }
        }
    }
    
    @Override
    protected final void arrow(final SnipeData v) {
    	this.antiFreeze(v, Material.WOOD_STAIRS.getId());
    }
    
    @Override
    protected final void powder(final SnipeData v) {    	
    	this.antiFreeze(v, Material.COBBLESTONE_STAIRS.getId());
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
        return AntiFreezeBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	AntiFreezeBrush.timesUsed = tUsed;
    }
}
