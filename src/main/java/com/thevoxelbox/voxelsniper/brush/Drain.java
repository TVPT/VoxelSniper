package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Gavjenks
 * @author psanker
 */
public class Drain extends Brush {
    private double trueCircle = 0;
    private boolean disc = false;
    private static int timesUsed = 0;

    public Drain() {
        this.setName("Drain");
    }

    private final void drain(final SnipeData v) {
        final int _brushSize = v.getBrushSize();
        final double _bpow = Math.pow(_brushSize + this.trueCircle, 2);
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        if (this.disc) {
            for (int _x = _brushSize; _x >= 0; _x--) {
                final double _xpow = Math.pow(_x, 2);
                for (int _y = _brushSize; _y >= 0; _y--) {
                    if ((_xpow + Math.pow(_y, 2)) <= _bpow) {
                        if (this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == 8 || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == 9
                                || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == 10 || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == 11) {
                            _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y));
                            this.setBlockIdAt(0, this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y);
                        }

                        if (this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == 8 || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == 9
                                || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == 10 || this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == 11) {
                            _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y));
                            this.setBlockIdAt(0, this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y);
                        }

                        if (this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == 8 || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == 9
                                || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == 10 || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y) == 11) {
                            _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y));
                            this.setBlockIdAt(0, this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y);
                        }

                        if (this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == 8 || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == 9
                                || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == 10 || this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y) == 11) {
                            _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y));
                            this.setBlockIdAt(0, this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y);
                        }
                    }
                }
            }
        } else {
            for (int _y = (_brushSize + 1) * 2; _y >= 0; _y--) {
                final double _ypow = Math.pow(_y - _brushSize, 2);
                for (int _x = (_brushSize + 1) * 2; _x >= 0; _x--) {
                    final double _xpow = Math.pow(_x - _brushSize, 2);
                    for (int _z = (_brushSize + 1) * 2; _z >= 0; _z--) {
                        if ((_xpow + Math.pow(_z - _brushSize, 2) + _ypow) <= _bpow) {
                            if (this.getBlockIdAt(this.getBlockPositionX() + _x - _brushSize, this.getBlockPositionY() + _z - _brushSize, this.getBlockPositionZ() + _y - _brushSize) == 8
                                    || this.getBlockIdAt(this.getBlockPositionX() + _x - _brushSize, this.getBlockPositionY() + _z - _brushSize, this.getBlockPositionZ() + _y - _brushSize) == 9
                                    || this.getBlockIdAt(this.getBlockPositionX() + _x - _brushSize, this.getBlockPositionY() + _z - _brushSize, this.getBlockPositionZ() + _y - _brushSize) == 10
                                    || this.getBlockIdAt(this.getBlockPositionX() + _x - _brushSize, this.getBlockPositionY() + _z - _brushSize, this.getBlockPositionZ() + _y - _brushSize) == 11) {
                                _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _z, this.getBlockPositionZ() + _y));
                                this.setBlockIdAt(0, this.getBlockPositionX() + _x - _brushSize, this.getBlockPositionY() + _z - _brushSize, this.getBlockPositionZ() + _y - _brushSize);
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
    	
    	if (this.trueCircle == 0.5) {
    		vm.custom(ChatColor.AQUA + "True circle mode ON");
    	} else {
    		vm.custom(ChatColor.AQUA + "True circle mode OFF");
    	}
    	
    	if (this.disc) {
    		vm.custom(ChatColor.AQUA + "Disc drain mode ON");
    	} else {
    		vm.custom(ChatColor.AQUA + "Disc drain mode OFF");
    	}
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GOLD + "Drain Brush Parameters:");
    		v.sendMessage(ChatColor.AQUA
    				+ "/b drain true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b drain false will switch back. (false is default)");
    		v.sendMessage(ChatColor.AQUA + "/b drain d -- toggles disc drain mode, as opposed to a ball drain mode");
    		return;
    	}
    	for (int x = 1; x < par.length; x++) {
    		if (par[x].startsWith("true")) {
    			this.trueCircle = 0.5;
    			v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
    			continue;
    		} else if (par[x].startsWith("false")) {
    			this.trueCircle = 0;
    			v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
    			continue;
    		} else if (par[x].equalsIgnoreCase("d")) {
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
    	return Drain.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Drain.timesUsed = tUsed;
    }
}
