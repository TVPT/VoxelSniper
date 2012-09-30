package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Jagged_Line_Brush
 * @author Giltwist
 * 
 */
public class JaggedLineBrush extends PerformBrush {
	private static int timesUsed = 0;

	private static final int RECURSION_MIN = 1;
	private static final int RECURSION_DEFAULT = 3;
	private static final int RECURSION_MAX = 10;
	private static final int RANDOM_MAX = 3;	
	
    private Random random = new Random();
    private Vector originCoords = null;
    private Vector targetCoords = new Vector();
    private int recursion = RECURSION_DEFAULT;

    /**
     * 
     */
    public JaggedLineBrush() {
        this.setName("Jagged Line");
    }

    private final void jaggedP(final SnipeData v) {
        final Vector _slope = targetCoords.subtract(originCoords).normalize();
    	Vector _previousCoords = new Vector();
        Vector _currentCoords = null;

        for (int _t = 0; _t <= _slope.length(); _t++) {
        	_currentCoords = _slope.multiply(_t).add(originCoords);
        	
            for (int _r = 0; _r < this.recursion; _r++) {
				if (_currentCoords != _previousCoords) {
					this.current.perform(this.clampY((int) Math.round(_currentCoords.getX() + this.random.nextInt(RANDOM_MAX) - 1),
							(int) Math.round(_currentCoords.getY() + this.random.nextInt(RANDOM_MAX) - 1),
							(int) Math.round(_currentCoords.getZ() + this.random.nextInt(RANDOM_MAX) - 1)));
				}
            }
            _previousCoords = _currentCoords;
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final void arrow(final SnipeData v) {
    	if (originCoords == null) {
			originCoords = new Vector();
		}
    	originCoords.setX(this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()));
    	originCoords.setY(this.getTargetBlock().getY() + .5);
    	originCoords.setZ(this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ()));
        v.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
    }

    @Override
    public final void powder(final SnipeData v) {
        if (originCoords == null) {
            v.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
            return;
        } else {
        	targetCoords.setX(this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()));
        	targetCoords.setY(this.getTargetBlock().getY() + .5);
        	targetCoords.setZ(this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ()));
            this.jaggedP(v);
        }

    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	final String _param = par[1];
    	
        if (_param.equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD
                    + "Jagged Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a jagged line to set the second point.");
            v.sendMessage(ChatColor.AQUA + "/b j r# - sets the number of recursions (default 3, must be 1-10)");
            return;
        }
        if (_param.startsWith("r")) {
            final int _temp = Integer.parseInt(_param.substring(1));
            if (_temp >= RECURSION_MIN && _temp <= RECURSION_MAX) {
                this.recursion = _temp;
                v.sendMessage(ChatColor.GREEN + "Recursion set to: " + this.recursion);
            } else {
                v.sendMessage(ChatColor.RED + "ERROR: Deviation must be " + RECURSION_MIN + "-" + RECURSION_MAX);
            }

            return;
        }

    }

    @Override
    public final int getTimesUsed() {
        return JaggedLineBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        JaggedLineBrush.timesUsed = tUsed;
    }
}
