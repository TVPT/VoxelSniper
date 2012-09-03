package com.thevoxelbox.voxelsniper.brush;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Giltwist
 * 
 */
public class Jagged extends PerformBrush {
    private Random random = new Random();
    private double[] origincoords = new double[3];
    private double[] targetcoords = new double[3];
    private int[] currentcoords = new int[3];
    private int[] previouscoords = new int[3];
    private double[] slopevector = new double[3];
    private int recursion = 3;

    private static int timesUsed = 0;

    public Jagged() {
        this.setName("Jagged Line");
    }

    private final void jaggedA(final SnipeData v) {
        v.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
    }

    private final void jaggedP(final SnipeData v) {
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
            for (int _r = 0; _r < this.recursion; _r++) {
                if (this.currentcoords[0] != this.previouscoords[0] || this.currentcoords[1] != this.previouscoords[1]
                        || this.currentcoords[2] != this.previouscoords[2]) { // Don't double-down

                    this.current.perform(this.clampY(this.currentcoords[0] + this.random.nextInt(3) - 1, this.currentcoords[1] + this.random.nextInt(3) - 1,
                            this.currentcoords[2] + this.random.nextInt(3) - 1));

                }
            }
            this.previouscoords = Arrays.copyOf(this.currentcoords, this.currentcoords.length);

        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final void arrow(final SnipeData v) {
        this.origincoords[0] = this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX());
        this.origincoords[1] = this.getTargetBlock().getY() + .5;
        this.origincoords[2] = this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ());
        this.jaggedA(v);
    }

    @Override
    public final void powder(final SnipeData v) {
        if (this.origincoords[0] == 0 && this.origincoords[1] == 0 && this.origincoords[2] == 0) {
            v.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");

        } else {
            this.targetcoords[0] = this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX());
            this.targetcoords[1] = this.getTargetBlock().getY() + .5;
            this.targetcoords[2] = this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ());
            this.jaggedP(v);
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
                    + "Jagged Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a jagged line to set the second point.");
            v.sendMessage(ChatColor.AQUA + "/b j r# - sets the number of recursions (default 3, must be 1-10)");
            return;
        }
        if (par[1].startsWith("r")) {

            final int _temp = Integer.parseInt(par[1].substring(1));
            if (_temp > 0 && _temp <= 10) {
                this.recursion = _temp;
                v.sendMessage(ChatColor.GREEN + "Recursion set to: " + this.recursion);
            } else {
                v.sendMessage(ChatColor.RED + "ERROR: Deviation must be 1-10.");
            }

            return;
        }

    }

    @Override
    public final int getTimesUsed() {
        return Jagged.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Jagged.timesUsed = tUsed;
    }
}
