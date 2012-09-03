package com.thevoxelbox.voxelsniper.brush;

import java.util.Arrays;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Gavjenks
 * @author giltwist
 */
public class Line extends PerformBrush {
    private double[] origincoords = new double[3];
    private double[] targetcoords = new double[3];
    private int[] currentcoords = new int[3];
    private int[] previouscoords = new int[3];
    private double[] slopevector = new double[3];

    private static int timesUsed = 0;

    public Line() {
        this.setName("Line");
    }

    private final void lineArrow(final SnipeData v) {
        v.owner().getPlayer().sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
    }

    private final void linePowder(final SnipeData v) {
        this.setWorld(v.owner().getPlayer().getWorld());
        double _lineLength = 0;

        // Calculate slope vector
        for (int _i = 0; _i < 3; _i++) {
            this.slopevector[_i] = this.targetcoords[_i] - this.origincoords[_i];
        }
        // Calculate line length in
        _lineLength = Math.pow((Math.pow(this.slopevector[0], 2) + Math.pow(this.slopevector[1], 2) + Math.pow(this.slopevector[2], 2)), .5);

        // Unitize slope vector
        for (int _i = 0; _i < 3; _i++) {
            this.slopevector[_i] = this.slopevector[_i] / _lineLength;
        }
        // Make the Changes

        for (int _t = 0; _t <= _lineLength; _t++) {

            // Update current coords
            for (int _i = 0; _i < 3; _i++) {
                this.currentcoords[_i] = (int) (this.origincoords[_i] + _t * this.slopevector[_i]);
            }

            if (this.currentcoords[0] != this.previouscoords[0] || this.currentcoords[1] != this.previouscoords[1]
                    || this.currentcoords[2] != this.previouscoords[2]) { // Don't double-down
                this.current.perform(this.clampY(this.currentcoords[0], this.currentcoords[1], this.currentcoords[2]));
            }

            this.previouscoords = Arrays.copyOf(this.currentcoords, this.currentcoords.length);
        }
        
        v.storeUndo(this.current.getUndo());
    }
    
    @Override
    protected final void arrow(final SnipeData v) {
    	this.origincoords[0] = this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX());
    	this.origincoords[1] = this.getTargetBlock().getY() + .5;
    	this.origincoords[2] = this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ());
    	this.lineArrow(v);
    }
    
    @Override
    protected final void powder(final SnipeData v) {
    	if (this.origincoords[0] == 0 && this.origincoords[1] == 0 && this.origincoords[2] == 0) {
    		v.owner().getPlayer().sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
    	} else {
    		this.targetcoords[0] = this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX());
    		this.targetcoords[1] = this.getTargetBlock().getY() + .5;
    		this.targetcoords[2] = this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ());
    		this.linePowder(v);
    	}
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD
                    + "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
        }
    }
    
    @Override
    public final int getTimesUsed() {
    	return Line.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Line.timesUsed = tUsed;
    }
}
