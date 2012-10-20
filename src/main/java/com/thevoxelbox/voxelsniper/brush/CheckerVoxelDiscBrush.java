package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * @author MikeMatrix
 * 
 */
public class CheckerVoxelDiscBrush extends PerformBrush {
	private static int timesUsed = 0;
    private boolean useWorldCoordinates = true;

    /**
     * Default constructor.
     */
    public CheckerVoxelDiscBrush() {
        this.setName("Checker Voxel Disc");
    }

    /**
     * @param v
     * @param target
     */
    private final void applyBrush(final SnipeData v, final Block target) {
        for (int _x = v.getBrushSize(); _x >= -v.getBrushSize(); _x--) {
            for (int _y = v.getBrushSize(); _y >= -v.getBrushSize(); _y--) {
                final int _sum = this.useWorldCoordinates ? target.getX() + _x + target.getZ() + _y : _x + _y;
                if (_sum % 2 != 0) {
                    this.current.perform(this.clampY(target.getX() + _x, target.getY(), target.getZ() + _y));
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.applyBrush(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.applyBrush(v, this.getLastBlock());
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	for (int _x = 1; _x < par.length; _x++) {
    		final String _param = par[_x].toLowerCase();

    		if (_param.equals("info")) {
    			v.sendMessage(ChatColor.GOLD + this.getName() + " Parameters:");
    			v.sendMessage(ChatColor.AQUA + "true  -- Enables using World Coordinates.");
    			v.sendMessage(ChatColor.AQUA + "false -- Disables using World Coordinates.");
    			return;
    		}
    		if (_param.startsWith("true")) {
    			this.useWorldCoordinates = true;
    			v.sendMessage(ChatColor.AQUA + "Enabled using World Coordinates.");
    			continue;
    		} else if (_param.startsWith("false")) {
    			this.useWorldCoordinates = false;
    			v.sendMessage(ChatColor.AQUA + "Disabled using World Coordinates.");
    			continue;
    		} else {
    			v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
    			break;
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return CheckerVoxelDiscBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	CheckerVoxelDiscBrush.timesUsed = tUsed;
    }
}
