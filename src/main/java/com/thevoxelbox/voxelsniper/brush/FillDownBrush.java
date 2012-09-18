package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class FillDownBrush extends PerformBrush {
	private static int timesUsed = 0;
    private double trueCircle = 0;

    public FillDownBrush() {
        this.setName("Fill Down");
    }

    private void fillDown(final SnipeData v, final Block b) {
    	final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + this.trueCircle, 2);
        
        for (int _x = 0 - _brushSize; _x <= _brushSize; _x++) {
            final double _xPow = Math.pow(_x, 2);
            
            for (int _z = 0 - _brushSize; _z <= _brushSize; _z++) {
                if (_xPow + Math.pow(_z, 2) <= _bPow) {
                	for(int _y = this.getBlockPositionY(); _y >= 0; --_y) {
                		final Block _block = this.clampY(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z);                		
                		if(_block.getType().equals(Material.AIR)) {
                			this.current.perform(_block);
                		}
                	}
                }
            }
        }
        
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.fillDown(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.fillDown(v, this.getLastBlock());        
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {    	
    	for (int _x = 1; _x < par.length; _x++) {
    		if (par[_x].equalsIgnoreCase("info")) {
        		v.sendMessage(ChatColor.GOLD + "Fill Down Parameters:");
        		v.sendMessage(ChatColor.AQUA
        				+ "/b fd true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b fd false will switch back. (false is default)");
        		return;
        	} else if (par[_x].startsWith("true")) {
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
    	return FillDownBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	FillDownBrush.timesUsed = tUsed;
    }
}
