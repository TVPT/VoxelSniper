package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class DiscFace extends PerformBrush {
    private double trueCircle = 0;
    private static int timesUsed = 0;

    public DiscFace() {
        this.setName("Disc Face");
    }

    private final void disc(final SnipeData v) {
        final int _brushSize = v.getBrushSize();

        final double _bpow = Math.pow(_brushSize + this.trueCircle, 2);
        for (int _x = _brushSize; _x >= 0; _x--) {
            final double _xpow = Math.pow(_x, 2);
            for (int _y = _brushSize; _y >= 0; _y--) {
                if ((_xpow + Math.pow(_y, 2)) <= _bpow) {
                    this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y));
                    this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y));
                    this.current.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y));
                    this.current.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _y));
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    private final void discEW(final SnipeData v) {
        final int _brushSize = v.getBrushSize();

        final double _bpow = Math.pow(_brushSize + this.trueCircle, 2);
        for (int _x = _brushSize; _x >= 0; _x--) {
            final double _xpow = Math.pow(_x, 2);
            for (int _y = _brushSize; _y >= 0; _y--) {
                if ((_xpow + Math.pow(_y, 2)) <= _bpow) {
                    this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ()));
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    private final void discNS(final SnipeData v) {
        final int _brushSize = v.getBrushSize();

        final double _bpow = Math.pow(_brushSize + this.trueCircle, 2);
        for (int _x = _brushSize; _x >= 0; _x--) {
            final double _xpow = Math.pow(_x, 2);
            for (int _y = _brushSize; _y >= 0; _y--) {
                if ((_xpow + Math.pow(_y, 2)) <= _bpow) {
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _x, this.getBlockPositionZ() - _y));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - _x, this.getBlockPositionZ() + _y));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - _x, this.getBlockPositionZ() - _y));
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    private void pre(final SnipeData v, final BlockFace _blockFace) {
        if (_blockFace == null) {
            return;
        }
        switch (_blockFace) {
        case NORTH:
        case SOUTH:
            this.discNS(v);
            break;

        case EAST:
        case WEST:
            this.discEW(v);
            break;

        case UP:
        case DOWN:
            this.disc(v);
            break;

        default:
            break;
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.setBlockPositionX(this.getLastBlock().getX());
    	this.setBlockPositionY(this.getLastBlock().getY());
    	this.setBlockPositionZ(this.getLastBlock().getZ());
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GOLD + "Disc Face brush Parameters:");
    		v.sendMessage(ChatColor.AQUA
    				+ "/b df true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
    		return;
    	}
    	for (int _x = 1; _x < par.length; _x++) {
    		if (par[_x].startsWith("true")) {
    			this.trueCircle = 0.5;
    			v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
    			continue;
    		} else if (par[_x].startsWith("false")) {
    			this.trueCircle = 0;
    			v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
    			continue;
    		} else {
    			v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return DiscFace.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	DiscFace.timesUsed = tUsed;
    }
}
