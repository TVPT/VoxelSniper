package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Drain_Brush
 * @author Gavjenks
 * @author psanker
 */
public class DrainBrush extends Brush {
	private static int timesUsed = 0;
    private double trueCircle = 0;
    private boolean disc = false;

    /**
     * 
     */
    public DrainBrush() {
        this.setName("Drain");
    }

    private final void drain(final SnipeData v) {
        final int _bSize = v.getBrushSize();
        final double _bPow = Math.pow(_bSize + this.trueCircle, 2);
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        
        if (this.disc) {
            for (int _x = _bSize; _x >= 0; _x--) {
                final double _xPow = Math.pow(_x, 2);
                
                for (int _y = _bSize; _y >= 0; _y--) {
                    if ((_xPow + Math.pow(_y, 2)) <= _bPow) {
                        if (this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == Material.WATER.getId() || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == Material.STATIONARY_WATER.getId()
                                || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == Material.LAVA.getId() || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == Material.STATIONARY_LAVA.getId()) {
                            _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y));
                            this.setBlockIdAt(Material.AIR.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y);
                        }

                        if (this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == Material.WATER.getId() || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == Material.STATIONARY_WATER.getId()
                                || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == Material.LAVA.getId() || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == Material.STATIONARY_LAVA.getId()) {
                            _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y));
                            this.setBlockIdAt(Material.AIR.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y);
                        }

                        if (this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == Material.WATER.getId() || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == Material.STATIONARY_WATER.getId()
                                || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == Material.LAVA.getId() || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == Material.STATIONARY_LAVA.getId()) {
                            _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y));
                            this.setBlockIdAt(Material.AIR.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y);
                        }

                        if (this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == Material.WATER.getId() || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == Material.STATIONARY_WATER.getId()
                                || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == Material.LAVA.getId() || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == Material.STATIONARY_LAVA.getId()) {
                            _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y));
                            this.setBlockIdAt(Material.AIR.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y);
                        }
                    }
                }
            }
        } else {
            for (int _y = (_bSize + 1) * 2; _y >= 0; _y--) {
                final double _yPow = Math.pow(_y - _bSize, 2);
                
                for (int _x = (_bSize + 1) * 2; _x >= 0; _x--) {
                    final double _xPow = Math.pow(_x - _bSize, 2);
                    
                    for (int _z = (_bSize + 1) * 2; _z >= 0; _z--) {
                        if ((_xPow + Math.pow(_z - _bSize, 2) + _yPow) <= _bPow) {
                            if (this.getBlockIdAt(this.getBlockPositionX() + _x - _bSize, this.getBlockPositionY() + _z - _bSize, this.getBlockPositionZ() + _y - _bSize) == Material.WATER.getId()
                                    || this.getBlockIdAt(this.getBlockPositionX() + _x - _bSize, this.getBlockPositionY() + _z - _bSize, this.getBlockPositionZ() + _y - _bSize) == Material.STATIONARY_WATER.getId()
                                    || this.getBlockIdAt(this.getBlockPositionX() + _x - _bSize, this.getBlockPositionY() + _z - _bSize, this.getBlockPositionZ() + _y - _bSize) == Material.LAVA.getId()
                                    || this.getBlockIdAt(this.getBlockPositionX() + _x - _bSize, this.getBlockPositionY() + _z - _bSize, this.getBlockPositionZ() + _y - _bSize) == Material.STATIONARY_LAVA.getId()) {
                                _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _z, this.getBlockPositionZ() + _y));
                                this.setBlockIdAt(Material.AIR.getId(), this.getBlockPositionX() + _x - _bSize, this.getBlockPositionY() + _z - _bSize, this.getBlockPositionZ() + _y - _bSize);
                            }
                        }
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.drain(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.drain(v);
     }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    	
    	vm.custom(ChatColor.AQUA + ((this.trueCircle == 0.5) ? "True circle mode ON" : "True circle mode OFF"));
    	vm.custom(ChatColor.AQUA + ((this.disc) ? "Disc drain mode ON" : "Disc drain mode OFF"));
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {    	
    	for (int _i = 1; _i < par.length; _i++) {
    		final String _param = par[_i];
    		
    		if (_param.equalsIgnoreCase("info")) {
        		v.sendMessage(ChatColor.GOLD + "Drain Brush Parameters:");
        		v.sendMessage(ChatColor.AQUA
        				+ "/b drain true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b drain false will switch back. (false is default)");
        		v.sendMessage(ChatColor.AQUA + "/b drain d -- toggles disc drain mode, as opposed to a ball drain mode");
        		return;
        	} else if (_param.startsWith("true")) {
    			this.trueCircle = 0.5;
    			v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
    			continue;
    		} else if (_param.startsWith("false")) {
    			this.trueCircle = 0;
    			v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
    			continue;
    		} else if (_param.equalsIgnoreCase("d")) {
    			if (this.disc) {
    				this.disc = false;
    				v.sendMessage(ChatColor.AQUA + "Disc drain mode OFF");
    			} else {
    				this.disc = true;
    				v.sendMessage(ChatColor.AQUA + "Disc drain mode ON");
    			}
    		} else {
    			v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return DrainBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	DrainBrush.timesUsed = tUsed;
    }
}
