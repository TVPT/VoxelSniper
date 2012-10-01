package com.thevoxelbox.voxelsniper.brush;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Ruler_Brush
 * @author Gavjenks
 */
public class RulerBrush extends Brush {
	private static int timesUsed = 0;

	private boolean first = true;
	private Vector coords = new Vector(0, 0, 0);

    private int xOff = 0;
    private int yOff = 0;
    private int zOff = 0;

    /**
     * 
     */
    public RulerBrush() {
        this.setName("Ruler");
    }

    private final void rulerA(final SnipeData v) {
        final int _voxelMaterialId = v.getVoxelId();
        coords = this.getTargetBlock().getLocation().toVector();
        
        if (this.xOff == 0 && this.yOff == 0 && this.zOff == 0) {
            v.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
            this.first = !this.first;
        } else {
            final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

            _undo.put(this.clampY(this.getBlockPositionX() + this.xOff, this.getBlockPositionY() + this.yOff, this.getBlockPositionZ() + this.zOff));
            this.setBlockIdAt(_voxelMaterialId, this.getBlockPositionX() + this.xOff, this.getBlockPositionY() + this.yOff, this.getBlockPositionZ() + this.zOff);
            v.storeUndo(_undo);
        }
    }

    private final void rulerP(final SnipeData v) {
        if (this.coords == null) {
            v.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow.  Comparing to point 0,0,0 instead.");
            return;
        }

        v.sendMessage(ChatColor.BLUE + "Format = (second coord - first coord)");
        v.sendMessage(ChatColor.AQUA + "X change: " + (this.getTargetBlock().getX() - this.coords.getX()));
        v.sendMessage(ChatColor.AQUA + "Y change: " + (this.getTargetBlock().getY() - this.coords.getY()));
        v.sendMessage(ChatColor.AQUA + "Z change: " + (this.getTargetBlock().getZ() - this.coords.getZ()));
        final double _distance = this.roundTwoDecimals(this.getTargetBlock().getLocation().toVector().subtract(coords).length());
        final double _blockDistance = this.roundTwoDecimals(Math.abs(this.getTargetBlock().getLocation().toVector().subtract(coords).length()) + 1);

        v.sendMessage(ChatColor.AQUA + "Euclidean distance = " + _distance);
        v.sendMessage(ChatColor.AQUA + "Block distance = " + _blockDistance); 
    }
    
    private final double roundTwoDecimals(final double d) {
    	final DecimalFormat _twoDForm = new DecimalFormat("#.##");
    	return Double.valueOf(_twoDForm.format(d));
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.rulerA(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.rulerP(v);
    }
    
    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.voxel();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int _i = 1; _i < par.length; _i++) {
        	final String _param = par[_i];
        	
        	if (_param.equalsIgnoreCase("info")) {
        		v.sendMessage(ChatColor.GOLD
        				+ "Ruler Brush instructions: Right click first point with the arrow. Right click with powder for distances from that block (can repeat without getting a new first block.) For placing blocks, use arrow and input the desired coordinates with parameters.");
        		v.sendMessage(ChatColor.LIGHT_PURPLE
        				+ "/b r x[x value] y[y value] z[z value] -- Will place blocks one at a time of the type you have set with /v at the location you click + this many units away.  If you don't include a value, it will be zero.  Don't include ANY values, and the brush will just measure distance.");
        		v.sendMessage(ChatColor.BLUE + "/b r ruler -- will reset the tool to just measure distances, not layout blocks.");
        		
        		return;
        	} else if (_param.startsWith("x")) {
                this.xOff = Integer.parseInt(_param.replace("x", ""));
                v.sendMessage(ChatColor.AQUA + "X offset set to " + this.xOff);
                continue;
            } else if (_param.startsWith("y")) {
                this.yOff = Integer.parseInt(_param.replace("y", ""));
                v.sendMessage(ChatColor.AQUA + "Y offset set to " + this.yOff);
                continue;
            } else if (_param.startsWith("z")) {
                this.zOff = Integer.parseInt(_param.replace("z", ""));
                v.sendMessage(ChatColor.AQUA + "Z offset set to " + this.zOff);
                continue;
            } else if (_param.startsWith("ruler")) {
                this.zOff = 0;
                this.yOff = 0;
                this.xOff = 0;
                v.sendMessage(ChatColor.BLUE + "Ruler mode.");
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }

    }

    @Override
    public final int getTimesUsed() {
        return RulerBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        RulerBrush.timesUsed = tUsed;
    }
}
