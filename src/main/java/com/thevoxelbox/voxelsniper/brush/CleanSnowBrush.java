package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author psanker
 */
public class CleanSnowBrush extends Brush {
	private static int timesUsed = 0;
    private double trueCircle = 0;

    public CleanSnowBrush() {
        this.setName("Clean Snow");
    }

    public final void cleanSnow(final SnipeData v) {
        final int _bSize = v.getBrushSize();
        final double _bPow = Math.pow(_bSize + this.trueCircle, 2);
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _y = (_bSize + 1) * 2; _y >= 0; _y--) {
            final double _yPow = Math.pow(_y - _bSize, 2);
            
            for (int _x = (_bSize + 1) * 2; _x >= 0; _x--) {
            	final double _xPow = Math.pow(_x - _bSize, 2);
            	
                for (int _z = (_bSize + 1) * 2; _z >= 0; _z--) {
                    if ((_xPow + Math.pow(_z - _bSize, 2) + _yPow) <= _bPow) {
                        if ((this.clampY(this.getBlockPositionX() + _x - _bSize, this.getBlockPositionY() + _z - _bSize, this.getBlockPositionZ() + _y - _bSize).getType() == Material.SNOW)
                                && ((this.clampY(this.getBlockPositionX() + _x - _bSize, this.getBlockPositionY() + _z - _bSize - 1, this.getBlockPositionZ() + _y - _bSize).getType() == Material.SNOW) || (this
                                        .clampY(this.getBlockPositionX() + _x - _bSize, this.getBlockPositionY() + _z - _bSize - 1, this.getBlockPositionZ() + _y - _bSize).getType() == Material.AIR))) {
                            _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _z, this.getBlockPositionZ() + _y));
                            this.setBlockIdAt(0, this.getBlockPositionX() + _x - _bSize, this.getBlockPositionY() + _z - _bSize, this.getBlockPositionZ() + _y - _bSize);
                        }

                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.cleanSnow(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.cleanSnow(v);
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
    			v.sendMessage(ChatColor.GOLD + "Clean Snow Brush Parameters:");
    			v.sendMessage(ChatColor.AQUA
    					+ "/b cls true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b cls false will switch back. (false is default)");
    			return;
			} else if (_param.startsWith("true")) {
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
    	return CleanSnowBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	CleanSnowBrush.timesUsed = tUsed;
    }
}
