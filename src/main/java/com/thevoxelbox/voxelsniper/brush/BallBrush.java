package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Ball_Brush
 * @author Piotr
 */
public class BallBrush extends PerformBrush {
    private static int timesUsed = 0;
    private double trueCircle = 0;

    /**
     * 
     */
    public BallBrush() {
        this.setName("Ball");
    }

    private final void ball(final SnipeData v) {    	
        final int _bSize = v.getBrushSize();
        final double _bPow = Math.pow(_bSize + this.trueCircle, 2);

        this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()));

        for (int _z = 1; _z <= _bSize; _z++) {
            final double _zPow = Math.pow(_z, 2);
            
            this.current.perform(this.clampY(this.getBlockPositionX() + _z, this.getBlockPositionY(), this.getBlockPositionZ()));
            this.current.perform(this.clampY(this.getBlockPositionX() - _z, this.getBlockPositionY(), this.getBlockPositionZ()));
            this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _z, this.getBlockPositionZ()));
            this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - _z, this.getBlockPositionZ()));
            this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ() + _z));
            this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ() - _z));
            
            for (int _x = 1; _x <= _bSize; _x++) {
                final double _xPow = Math.pow(_x, 2);
                
                if (_zPow + Math.pow(_x, 2) <= _bPow) {
                    this.current.perform(this.clampY(this.getBlockPositionX() + _z, this.getBlockPositionY(), this.getBlockPositionZ() + _x));
                    this.current.perform(this.clampY(this.getBlockPositionX() + _z, this.getBlockPositionY(), this.getBlockPositionZ() - _x));
                    this.current.perform(this.clampY(this.getBlockPositionX() - _z, this.getBlockPositionY(), this.getBlockPositionZ() + _x));
                    this.current.perform(this.clampY(this.getBlockPositionX() - _z, this.getBlockPositionY(), this.getBlockPositionZ() - _x));
                    this.current.perform(this.clampY(this.getBlockPositionX() + _z, this.getBlockPositionY() + _x, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() + _z, this.getBlockPositionY() - _x, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() - _z, this.getBlockPositionY() + _x, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() - _z, this.getBlockPositionY() - _x, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _z, this.getBlockPositionZ() + _x));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _z, this.getBlockPositionZ() - _x));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - _z, this.getBlockPositionZ() + _x));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - _z, this.getBlockPositionZ() - _x));
                }
                
                for (int _y = 1; _y <= _bSize; _y++) {
                    if ((_xPow + Math.pow(_y, 2) + _zPow) <= _bPow) {
                        this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z));
                        this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z));
                        this.current.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z));
                        this.current.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z));
                        this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _z));
                        this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _z));
                        this.current.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _z));
                        this.current.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _z));
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }
    
    @Override
    protected final void arrow(final SnipeData v) {    	
    	this.ball(v);
    }
    
    @Override
    protected final void powder(final SnipeData v) {
    	this.ball(v);
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
        		v.sendMessage(ChatColor.GOLD + "Ball Brush Parameters:");
        		v.sendMessage(ChatColor.AQUA
        				+ "/b b true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
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
        return BallBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        BallBrush.timesUsed = tUsed;
    }
}
