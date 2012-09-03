package com.thevoxelbox.voxelsniper.brush;

import java.util.Arrays;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Gavjenks
 * @author giltwist
 */
public class Line extends PerformBrush {

    protected boolean first = true;
    protected double[] origincoords = new double[3];
    protected double[] targetcoords = new double[3];
    protected int[] currentcoords = new int[3];
    protected int[] previouscoords = new int[3];
    protected double[] slopevector = new double[3];

    private static int timesUsed = 0;

    public Line() {
        this.name = "Line";
    }

    @Override
    public final int getTimesUsed() {
        return Line.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        // vm.voxel();
    }

    public final void LineA(final vData v) {
        v.owner().getPlayer().sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
    }

    public final void LineP(final vData v) {
        this.w = v.owner().getPlayer().getWorld();
        double linelength = 0;

        // Calculate slope vector
        for (int i = 0; i < 3; i++) {
            this.slopevector[i] = this.targetcoords[i] - this.origincoords[i];
        }
        // Calculate line length in
        linelength = Math.pow((Math.pow(this.slopevector[0], 2) + Math.pow(this.slopevector[1], 2) + Math.pow(this.slopevector[2], 2)), .5);

        // Unitize slope vector
        for (int i = 0; i < 3; i++) {
            this.slopevector[i] = this.slopevector[i] / linelength;
        }
        // Make the Changes

        for (int t = 0; t <= linelength; t++) {

            // Update current coords
            for (int i = 0; i < 3; i++) {
                this.currentcoords[i] = (int) (this.origincoords[i] + t * this.slopevector[i]);
            }

            if (this.currentcoords[0] != this.previouscoords[0] || this.currentcoords[1] != this.previouscoords[1]
                    || this.currentcoords[2] != this.previouscoords[2]) { // Don't double-down
                this.current.perform(this.clampY(this.currentcoords[0], this.currentcoords[1], this.currentcoords[2]));
            }

            this.previouscoords = Arrays.copyOf(this.currentcoords, this.currentcoords.length);
        }

        // Line might be a block short, check target block
        // current.perform(clampY((int) Math.floor(targetcoords[0] - .5 * targetcoords[0] / Math.abs(targetcoords[0])), (int) Math.floor(targetcoords[1] - .5),
        // (int) Math.floor(targetcoords[2] - .5 * targetcoords[2] / Math.abs(targetcoords[2]))));

        v.storeUndo(this.current.getUndo());

        // RESET BRUSH
        // origincoords[0] = 0;
        // origincoords[1] = 0;
        // origincoords[2] = 0;
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD
                    + "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Line.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.origincoords[0] = this.tb.getX() + .5 * this.tb.getX() / Math.abs(this.tb.getX()); // I hate you sometimes, Notch. Really? Every quadrant is
                                                                                                // different?
        this.origincoords[1] = this.tb.getY() + .5;
        this.origincoords[2] = this.tb.getZ() + .5 * this.tb.getZ() / Math.abs(this.tb.getZ());
        this.LineA(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        if (this.origincoords[0] == 0 && this.origincoords[1] == 0 && this.origincoords[2] == 0) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
        } else {
            this.targetcoords[0] = this.tb.getX() + .5 * this.tb.getX() / Math.abs(this.tb.getX());
            this.targetcoords[1] = this.tb.getY() + .5;
            this.targetcoords[2] = this.tb.getZ() + .5 * this.tb.getZ() / Math.abs(this.tb.getZ());
            this.LineP(v);
        }
    }
}
