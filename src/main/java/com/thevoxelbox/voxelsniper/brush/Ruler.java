package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Gavjenks
 */
public class Ruler extends Brush {
    private boolean first = true;
    private double[] coords = new double[3];

    private int xOff = 0;
    private int yOff = 0;
    private int zOff = 0;

    private static int timesUsed = 0;

    public Ruler() {
        this.setName("Ruler");
    }

    private final void rulerA(final SnipeData v) {
        final int _voxelMaterialId = v.getVoxelId();
        if (this.xOff == 0 && this.yOff == 0 && this.zOff == 0) {

            this.coords[0] = this.getTargetBlock().getX();
            this.coords[1] = this.getTargetBlock().getY();
            this.coords[2] = this.getTargetBlock().getZ();
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
        if (this.coords[0] == 0 && this.coords[1] == 0 && this.coords[2] == 0) {
            v.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow.  Comparing to point 0,0,0 instead.");
        }

        v.sendMessage(ChatColor.BLUE + "Format = (second coord - first coord)");
        v.sendMessage(ChatColor.AQUA + "X change: " + (this.getTargetBlock().getX() - this.coords[0]));
        v.sendMessage(ChatColor.AQUA + "Y change: " + (this.getTargetBlock().getY() - this.coords[1]));
        v.sendMessage(ChatColor.AQUA + "Z change: " + (this.getTargetBlock().getZ() - this.coords[2]));
        double _distance = Math.sqrt(Math.pow((this.coords[0] - this.getTargetBlock().getX()), 2) + Math.pow((this.coords[1] - this.getTargetBlock().getY()), 2)
                + Math.pow((this.coords[2] - this.getTargetBlock().getZ()), 2));
        _distance = this.roundTwoDecimals(_distance);
        double _blockdistance = Math.abs(Math.max(Math.max(Math.abs(this.getTargetBlock().getX() - this.coords[0]), Math.abs(this.getTargetBlock().getY() - this.coords[1])),
                Math.abs(this.getTargetBlock().getZ() - this.coords[2]))) + 1;
        _blockdistance = this.roundTwoDecimals(_blockdistance);
        v.sendMessage(ChatColor.AQUA + "Euclidean distance = " + _distance);
        v.sendMessage(ChatColor.AQUA + "Block distance = " + _blockdistance); 
    }
    
    private final double roundTwoDecimals(final double d) {
    	final java.text.DecimalFormat twoDForm = new java.text.DecimalFormat("#.##");
    	return Double.valueOf(twoDForm.format(d));
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
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD
                    + "Ruler Brush instructions: Right click first point with the arrow. Right click with powder for distances from that block (can repeat without getting a new first block.) For placing blocks, use arrow and input the desired coordinates with parameters.");
            v.sendMessage(ChatColor.LIGHT_PURPLE
                    + "/b r x[x value] y[y value] z[z value] -- Will place blocks one at a time of the type you have set with /v at the location you click + this many units away.  If you don't include a value, it will be zero.  Don't include ANY values, and the brush will just measure distance.");
            v.sendMessage(ChatColor.BLUE + "/b r ruler -- will reset the tool to just measure distances, not layout blocks.");

            return;
        }
        for (int _i = 1; _i < par.length; _i++) {
            if (par[_i].startsWith("x")) {
                this.xOff = Integer.parseInt(par[_i].replace("x", ""));
                v.sendMessage(ChatColor.AQUA + "X offset set to " + this.xOff);
                continue;
            } else if (par[_i].startsWith("y")) {
                this.yOff = Integer.parseInt(par[_i].replace("y", ""));
                v.sendMessage(ChatColor.AQUA + "Y offset set to " + this.yOff);
                continue;
            } else if (par[_i].startsWith("z")) {
                this.zOff = Integer.parseInt(par[_i].replace("z", ""));
                v.sendMessage(ChatColor.AQUA + "Z offset set to " + this.zOff);
                continue;
            } else if (par[_i].startsWith("ruler")) {
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
        return Ruler.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Ruler.timesUsed = tUsed;
    }
}
