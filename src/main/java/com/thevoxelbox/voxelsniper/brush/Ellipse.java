package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author psanker
 */
public class Ellipse extends PerformBrush {
    private int xscl;
    private int yscl;
    private int steps;
    private boolean fill;
    private static int timesUsed = 0;

    public Ellipse() {
        this.setName("Ellipse");
    }

    private final void ellipse(final SnipeData v) {
        final double _stepSize = ((2 * Math.PI) / this.steps);

        if (_stepSize <= 0) {
            v.sendMessage("Failed: Step size is <= 0");
            return;
        }

        try {
            for (double _steps = 0; (_steps <= (2 * Math.PI)); _steps += _stepSize) {
                final int _x = (int) Math.round(this.xscl * Math.cos(_steps));
                final int _y = (int) Math.round(this.yscl * Math.sin(_steps));

                switch (this.getTargetBlock().getFace(this.getLastBlock())) {
                case NORTH:
                case SOUTH:
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y));
                    break;
                case EAST:
                case WEST:
                    this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ()));
                    break;
                case UP:
                case DOWN:
                    this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y));
                default:
                    break;
                }

                if (_steps >= (2 * Math.PI)) {
                    break;
                }
            }
        } catch (final Exception e) {
            v.sendMessage(ChatColor.RED + "Invalid target.");
        }

        v.storeUndo(this.current.getUndo());
    }

    private final void ellipsefill(final SnipeData v) {
        this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()));

        final double _stepSize = ((2 * Math.PI) / this.steps);
        int _ix = this.xscl;
        int _iy = this.yscl;

        if (_stepSize <= 0) {
            v.sendMessage("Failed: Step size is <= 0");
            return;
        }

        try {
            if (_ix >= _iy) { // Need this unless you want weird holes
                for (_iy = this.yscl; _iy > 0; _iy--) {
                    for (double _steps = 0; (_steps <= (2 * Math.PI)); _steps += _stepSize) {
                        final int _x =  (int) Math.round(_ix * Math.cos(_steps));
                        final int _y =  (int) Math.round(_iy * Math.sin(_steps));

                        switch (this.getTargetBlock().getFace(this.getLastBlock())) {
                        case NORTH:
                        case SOUTH:
                            this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y));
                            break;
                        case EAST:
                        case WEST:
                            this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ()));
                            break;
                        case UP:
                        case DOWN:
                            this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y));
                        default:
                            break;
                        }

                        if (_steps >= (2 * Math.PI)) {
                            break;
                        }
                    }
                    _ix--;
                }
            } else {
                for (_ix = this.xscl; _ix > 0; _ix--) {
                    for (double _steps = 0; (_steps <= (2 * Math.PI)); _steps += _stepSize) {
                        final int _x = (int) Math.round(_ix * Math.cos(_steps));
                        final int _y = (int) Math.round(_iy * Math.sin(_steps));

                        switch (this.getTargetBlock().getFace(this.getLastBlock())) {
                        case NORTH:
                        case SOUTH:
                            this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y));
                            break;
                        case EAST:
                        case WEST:
                            this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ()));
                            break;
                        case UP:
                        case DOWN:
                            this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y));
                        default:
                            break;
                        }

                        if (_steps >= (2 * Math.PI)) {
                            break;
                        }
                    }
                    _iy--;
                }
            }
        } catch (final Exception e) {
            v.sendMessage(ChatColor.RED + "Invalid target.");
        }

        v.storeUndo(this.current.getUndo());
    }

    private void execute(final SnipeData v) {
        if (this.fill) {
            this.ellipsefill(v);
        } else {
            this.ellipse(v);
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.execute(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
    	this.setBlockPositionX(this.getLastBlock().getX());
    	this.setBlockPositionY(this.getLastBlock().getY());
    	this.setBlockPositionZ(this.getLastBlock().getZ());
        this.execute(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	if (this.xscl < 1 || this.xscl > 9999) {
    		this.xscl = 10;
    	}
    	
    	if (this.yscl < 1 || this.yscl > 9999) {
    		this.yscl = 10;
    	}
    	
    	if (this.steps < 1 || this.steps > 2000) {
    		this.steps = 200;
    	}
    	
    	vm.brushName(this.getName());
    	vm.custom(ChatColor.AQUA + "X-size set to: " + ChatColor.DARK_AQUA + this.xscl);
    	vm.custom(ChatColor.AQUA + "Y-size set to: " + ChatColor.DARK_AQUA + this.yscl);
    	vm.custom(ChatColor.AQUA + "Render step number set to: " + ChatColor.DARK_AQUA + this.steps);
    	if (this.fill == true) {
    		vm.custom(ChatColor.AQUA + "Fill mode is enabled");
    	} else {
    		vm.custom(ChatColor.AQUA + "Fill mode is disabled");
    	}
    }
    
    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GOLD + "Ellipse brush parameters");
    		v.sendMessage(ChatColor.AQUA + "x[n]: Set X size modifier to n");
    		v.sendMessage(ChatColor.AQUA + "y[n]: Set Y size modifier to n");
    		v.sendMessage(ChatColor.AQUA + "t[n]: Set the amount of time steps");
    		v.sendMessage(ChatColor.AQUA + "fill: Toggles fill mode");
    		return;
    	}
    	
    	for (int _i = 1; _i < par.length; _i++) {
    		try {
    			if (par[_i].startsWith("x")) {
    				this.xscl = Integer.parseInt(par[_i].replace("x", ""));
    				v.sendMessage(ChatColor.AQUA + "X-scale modifier set to: " + this.xscl);
    				continue;
    			} else if (par[_i].startsWith("y")) {
    				this.yscl = Integer.parseInt(par[_i].replace("y", ""));
    				v.sendMessage(ChatColor.AQUA + "Y-scale modifier set to: " + this.yscl);
    				continue;
    			} else if (par[_i].startsWith("t")) {
    				this.steps = Integer.parseInt(par[_i].replace("t", ""));
    				v.sendMessage(ChatColor.AQUA + "Render step number set to: " + this.steps);
    				continue;
    			} else if (par[_i].equalsIgnoreCase("fill")) {
    				if (this.fill == true) {
    					this.fill = false;
    					v.sendMessage(ChatColor.AQUA + "Fill mode is disabled");
    					continue;
    				} else {
    					this.fill = true;
    					v.sendMessage(ChatColor.AQUA + "Fill mode is enabled");
    					continue;
    				}
    			} else {
    				v.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
    			}
    			
    		} catch (final Exception e) {
    			v.sendMessage(ChatColor.RED + "Incorrect parameter \"" + par[_i] + "\"; use the \"info\" parameter.");
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return Ellipse.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Ellipse.timesUsed = tUsed;
    }
}
