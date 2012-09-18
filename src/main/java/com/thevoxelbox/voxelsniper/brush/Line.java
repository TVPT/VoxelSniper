package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Gavjenks
 * @author giltwist
 */
public class Line extends PerformBrush {
	private static int timesUsed = 0;

	private Vector originCoords = null;
	private Vector targetCoords = new Vector();

	/**
	 * 
	 */
    public Line() {
        this.setName("Line");
    }

    private final void linePowder(final SnipeData v) {
        Vector _previousCoords = new Vector();
        Vector _currentCoords = null;
        final Vector _slope = targetCoords.subtract(originCoords).normalize();

        for (int _t = 0; _t <= _slope.length(); _t++) {
			_currentCoords = _slope.multiply(_t).add(originCoords);

			if (_currentCoords != _previousCoords) {
				this.current.perform(this.clampY((int) Math.round(_currentCoords.getX()), (int) Math.round(_currentCoords.getY()), (int) Math.round(_currentCoords.getZ())));
			}

			_previousCoords = _currentCoords;
        }

        v.storeUndo(this.current.getUndo());
    }
    
    @Override
    protected final void arrow(final SnipeData v) {
		if (originCoords == null) {
			originCoords = new Vector();
		}
    	originCoords.setX(this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()));
    	originCoords.setY(this.getTargetBlock().getY() + .5);
    	originCoords.setZ(this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ()));
    	v.owner().getPlayer().sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
    }
    
    @Override
    protected final void powder(final SnipeData v) {
    	if (originCoords == null) {
    		v.owner().getPlayer().sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
    		return;
    	} else {
    		targetCoords.setX(this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()));
        	targetCoords.setY(this.getTargetBlock().getY() + .5);
        	targetCoords.setZ(this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ()));
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
