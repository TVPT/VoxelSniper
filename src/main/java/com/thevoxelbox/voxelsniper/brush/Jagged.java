package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Giltwist
 * 
 */
public class Jagged extends PerformBrush {

    protected Random random = new Random();
    protected boolean first = true;
    protected double[] origincoords = new double[3];
    protected double[] targetcoords = new double[3];
    protected int[] currentcoords = new int[3];
    protected int[] previouscoords = new int[3];
    protected double[] slopevector = new double[3];
    protected int recursion = 3;
    protected int steps = 5;
    protected List blocks = new ArrayList();

    private static int timesUsed = 0;

    public Jagged() {
        this.name = "Jagged Line";
    }

    @Override
    public final void arrow(final vData v) {
        this.origincoords[0] = this.tb.getX() + .5 * this.tb.getX() / Math.abs(this.tb.getX()); // I hate you sometimes, Notch. Really? Every quadrant is
                                                                                                // different?
        this.origincoords[1] = this.tb.getY() + .5;
        this.origincoords[2] = this.tb.getZ() + .5 * this.tb.getZ() / Math.abs(this.tb.getZ());
        this.JaggedA(v);
    }

    @Override
    public final int getTimesUsed() {
        return Jagged.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        // vm.voxel();
    }

    public final void JaggedA(final vData v) {
        v.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
    }

    public final void JaggedP(final vData v) {
        this.w = v.getWorld();
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
            for (int r = 0; r < this.recursion; r++) {
                if (this.currentcoords[0] != this.previouscoords[0] || this.currentcoords[1] != this.previouscoords[1]
                        || this.currentcoords[2] != this.previouscoords[2]) { // Don't double-down

                    this.current.perform(this.clampY(this.currentcoords[0] + this.random.nextInt(3) - 1, this.currentcoords[1] + this.random.nextInt(3) - 1,
                            this.currentcoords[2] + this.random.nextInt(3) - 1));

                }
            }
            this.previouscoords = Arrays.copyOf(this.currentcoords, this.currentcoords.length);

        }

        v.storeUndo(this.current.getUndo());

        // RESET BRUSH
        // origincoords[0] = 0;
        // origincoords[1] = 0;
        // origincoords[2] = 0;

    }

    @Override
    public final void parameters(final String[] par, final vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD
                    + "Jagged Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a jagged line to set the second point.");
            v.sendMessage(ChatColor.AQUA + "/b j r# - sets the number of recursions (default 3, must be 1-10)");
            return;
        }
        if (par[1].startsWith("r")) {

            final int temp = Integer.parseInt(par[1].substring(1));
            if (temp > 0 && temp <= 10) {
                this.recursion = temp;
                v.sendMessage(ChatColor.GREEN + "Recursion set to: " + this.recursion);
            } else {
                v.sendMessage(ChatColor.RED + "ERROR: Deviation must be 1-10.");
            }

            return;
        }

    }

    @Override
    public final void powder(final vData v) {

        if (this.origincoords[0] == 0 && this.origincoords[1] == 0 && this.origincoords[2] == 0) {
            v.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");

        } else {
            this.targetcoords[0] = this.tb.getX() + .5 * this.tb.getX() / Math.abs(this.tb.getX());
            this.targetcoords[1] = this.tb.getY() + .5;
            this.targetcoords[2] = this.tb.getZ() + .5 * this.tb.getZ() / Math.abs(this.tb.getZ());
            this.JaggedP(v);
        }

    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Jagged.timesUsed = tUsed;
    }
}
