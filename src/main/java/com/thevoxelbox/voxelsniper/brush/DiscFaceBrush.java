package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Disc_Face_Brush
 * @author Voxel
 */
public class DiscFaceBrush extends PerformBrush {
    private double trueCircle = 0;
    private static int timesUsed = 0;

    /**
     * 
     */
    public DiscFaceBrush() {
        this.setName("Disc Face");
    }

    private final void discUD(final SnipeData v) {
        final int _brushSize = v.getBrushSize();        
        final double _bPow = Math.pow(_brushSize + this.trueCircle, 2);
        
        for (int _x = _brushSize; _x >= 0; _x--) {
            final double _xPow = Math.pow(_x, 2);
            
            for (int _y = _brushSize; _y >= 0; _y--) {
                if ((_xPow + Math.pow(_y, 2)) <= _bPow) {
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
        final double _bPow = Math.pow(_brushSize + this.trueCircle, 2);

        for (int _x = _brushSize; _x >= 0; _x--) {
            final double _xPow = Math.pow(_x, 2);
            for (int _y = _brushSize; _y >= 0; _y--) {
                if ((_xPow + Math.pow(_y, 2)) <= _bPow) {
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
    	final double _bPow = Math.pow(_brushSize + this.trueCircle, 2);

        for (int _x = _brushSize; _x >= 0; _x--) {
            final double _xPow = Math.pow(_x, 2);
            for (int _y = _brushSize; _y >= 0; _y--) {
                if ((_xPow + Math.pow(_y, 2)) <= _bPow) {
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _x, this.getBlockPositionZ() - _y));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - _x, this.getBlockPositionZ() + _y));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - _x, this.getBlockPositionZ() - _y));
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    private void pre(final SnipeData v, final BlockFace blockFace) {
        if (blockFace == null) {
            return;
        }
        switch (blockFace) {
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
            this.discUD(v);
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
    	for (int _i = 1; _i < par.length; _i++) {
    		final String _param = par[_i];
    		
    		if (_param.equalsIgnoreCase("info")) {
    			v.sendMessage(ChatColor.GOLD + "Disc Face brush Parameters:");
    			v.sendMessage(ChatColor.AQUA
    					+ "/b df true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
    			return;
    		}
    		if (_param.startsWith("true")) {
    			this.trueCircle = 0.5;
    			v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
    			continue;
    		} else if (_param.startsWith("false")) {
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
    	return DiscFaceBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	DiscFaceBrush.timesUsed = tUsed;
    }
}
