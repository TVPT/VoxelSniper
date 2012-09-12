package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author psanker
 */
public class EllipseBrush extends PerformBrush {
	private static final double TWO_PI = (2 * Math.PI);
	private static final int SCL_MIN = 1;
	private static final int SCL_MAX = 9999;
	private static final int SCL_DEFAULT = 10;
	private static final int STEPS_MIN = 1;
	private static final int STEPS_MAX = 2000;
	private static final int STEPS_DEFAULT = 200;
	
    private int xscl;
    private int yscl;
    private int steps;
    private double stepSize;
    private boolean fill;
    private static int timesUsed = 0;

    public EllipseBrush() {
        this.setName("Ellipse");
    }

    private final void ellipse(final SnipeData v) {
        try {
            for (double _steps = 0; (_steps <= TWO_PI); _steps += stepSize) {
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

                if (_steps >= TWO_PI) {
                    break;
                }
            }
        } catch (final Exception e) {
            v.sendMessage(ChatColor.RED + "Invalid target.");
        }

        v.storeUndo(this.current.getUndo());
    }

    private final void ellipsefill(final SnipeData v) {
    	int _ix = this.xscl;
    	int _iy = this.yscl;

    	this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()));

        try {
            if (_ix >= _iy) { // Need this unless you want weird holes
                for (_iy = this.yscl; _iy > 0; _iy--) {
                    for (double _steps = 0; (_steps <= TWO_PI); _steps += stepSize) {
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

                        if (_steps >= TWO_PI) {
                            break;
                        }
                    }
                    _ix--;
                }
            } else {
                for (_ix = this.xscl; _ix > 0; _ix--) {
                    for (double _steps = 0; (_steps <= TWO_PI); _steps += stepSize) {
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

                        if (_steps >= TWO_PI) {
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
    	this.stepSize = (TWO_PI / this.steps);
    	
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
    	if (this.xscl < SCL_MIN || this.xscl > SCL_MAX) {
    		this.xscl = SCL_DEFAULT;
    	}
    	
    	if (this.yscl < SCL_MIN || this.yscl > SCL_MAX) {
    		this.yscl = SCL_DEFAULT;
    	}
    	
    	if (this.steps < STEPS_MIN || this.steps > STEPS_MAX) {
    		this.steps = STEPS_DEFAULT;
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
    				int _tmpXScl = Integer.parseInt(par[_i].replace("x", ""));
    				if(_tmpXScl < SCL_MIN || _tmpXScl > SCL_MAX) {
    					v.sendMessage(ChatColor.AQUA + "Invalid X scale (" + SCL_MIN + "-" + SCL_MAX + ")");
    					continue;
    				}
    				this.xscl = _tmpXScl;
    				v.sendMessage(ChatColor.AQUA + "X-scale modifier set to: " + this.xscl);
    				continue;
    			} else if (par[_i].startsWith("y")) {
    				int _tmpYScl = Integer.parseInt(par[_i].replace("y", ""));
    				if(_tmpYScl < SCL_MIN || _tmpYScl > SCL_MAX) {
    					v.sendMessage(ChatColor.AQUA + "Invalid Y scale (" + SCL_MIN + "-" + SCL_MAX + ")");
    					continue;
    				}
    				this.yscl = _tmpYScl;
    				v.sendMessage(ChatColor.AQUA + "Y-scale modifier set to: " + this.yscl);
    				continue;
    			} else if (par[_i].startsWith("t")) {
    				int _tempSteps = Integer.parseInt(par[_i].replace("t", ""));
    				if(_tempSteps < STEPS_MIN || _tempSteps > STEPS_MAX) {
    					v.sendMessage(ChatColor.AQUA + "Invalid step number (" + STEPS_MIN + "-" + STEPS_MAX + ")");
    					continue;
    				}
    				this.steps = _tempSteps;
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
    	return EllipseBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	EllipseBrush.timesUsed = tUsed;
    }
}
