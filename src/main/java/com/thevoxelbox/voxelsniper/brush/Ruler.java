package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Gavjenks
 */
public class Ruler extends Brush {

    protected boolean first = true;
    protected double[] coords = new double[3];

    private int xOff = 0;

    private int yOff = 0;

    private int zOff = 0;

    private static int timesUsed = 0;

    public Ruler() {
        this.setName("Ruler");
    }

    @Override
    public final int getTimesUsed() {
        return Ruler.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.voxel();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD
                    + "Ruler Brush instructions: Right click first point with the arrow. Right click with powder for distances from that block (can repeat without getting a new first block.) For placing blocks, use arrow and input the desired coordinates with parameters.");
            v.sendMessage(ChatColor.LIGHT_PURPLE
                    + "/b r x[x value] y[y value] z[z value] -- Will place blocks one at a time of the type you have set with /v at the location you click + this many units away.  If you don't include a value, it will be zero.  Don't include ANY values, and the brush will just measure distance.");
            v.sendMessage(ChatColor.BLUE + "/b r ruler -- will reset the tool to just measure distances, not layout blocks.");

            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("x")) {
                this.xOff = Integer.parseInt(par[x].replace("x", ""));
                v.sendMessage(ChatColor.AQUA + "X offset set to " + this.xOff);
                continue;
            } else if (par[x].startsWith("y")) {
                this.yOff = Integer.parseInt(par[x].replace("y", ""));
                v.sendMessage(ChatColor.AQUA + "Y offset set to " + this.yOff);
                continue;
            } else if (par[x].startsWith("z")) {
                this.zOff = Integer.parseInt(par[x].replace("z", ""));
                v.sendMessage(ChatColor.AQUA + "Z offset set to " + this.zOff);
                continue;
            } else if (par[x].startsWith("ruler")) {
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

    public final void rulerA(final vData v) {
        final int bId = v.voxelId;
        // targetBlock = targetBlock;
        if (this.xOff == 0 && this.yOff == 0 && this.zOff == 0) {

            this.coords[0] = this.getTargetBlock().getX();
            this.coords[1] = this.getTargetBlock().getY();
            this.coords[2] = this.getTargetBlock().getZ();
            v.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
            this.first = !this.first;

        } else {
            final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());

            h.put(this.clampY(this.getBlockPositionX() + this.xOff, this.getBlockPositionY() + this.yOff, this.getBlockPositionZ() + this.zOff));
            this.setBlockIdAt(bId, this.getBlockPositionX() + this.xOff, this.getBlockPositionY() + this.yOff, this.getBlockPositionZ() + this.zOff);
            v.storeUndo(h);
        }
    }

    public final void rulerP(final vData v) {
        if (this.coords[0] == 0 && this.coords[1] == 0 && this.coords[2] == 0) {
            v.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow.  Comparing to point 0,0,0 instead.");
        }

        v.sendMessage(ChatColor.BLUE + "Format = (second coord - first coord)");
        v.sendMessage(ChatColor.AQUA + "X change: " + (this.getTargetBlock().getX() - this.coords[0]));
        v.sendMessage(ChatColor.AQUA + "Y change: " + (this.getTargetBlock().getY() - this.coords[1]));
        v.sendMessage(ChatColor.AQUA + "Z change: " + (this.getTargetBlock().getZ() - this.coords[2]));
        double distance = Math.sqrt(Math.pow((this.coords[0] - this.getTargetBlock().getX()), 2) + Math.pow((this.coords[1] - this.getTargetBlock().getY()), 2)
                + Math.pow((this.coords[2] - this.getTargetBlock().getZ()), 2));
        distance = this.roundTwoDecimals(distance);
        double blockdistance = Math.abs(Math.max(Math.max(Math.abs(this.getTargetBlock().getX() - this.coords[0]), Math.abs(this.getTargetBlock().getY() - this.coords[1])),
                Math.abs(this.getTargetBlock().getZ() - this.coords[2]))) + 1;
        blockdistance = this.roundTwoDecimals(blockdistance);
        v.sendMessage(ChatColor.AQUA + "Euclidean distance = " + distance);
        v.sendMessage(ChatColor.AQUA + "Block distance = " + blockdistance); // more what people would expect - Gilt
        // }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Ruler.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.rulerA(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.rulerP(v);
    }

    final double roundTwoDecimals(final double d) {
        final java.text.DecimalFormat twoDForm = new java.text.DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
