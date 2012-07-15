/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.util.Arrays;
import org.bukkit.ChatColor;

/**
 *
 * @author Gavjenks
 * Heavily revamped from ruler brush by Giltwist
 */
public class Line extends PerformBrush {

    protected boolean first = true;
    protected double[] origincoords = new double[3];
    protected double[] targetcoords = new double[3];
    protected int[] currentcoords = new int[3];
    protected int[] previouscoords = new int[3];
    protected double[] slopevector = new double[3];

    public Line() {
        name = "Line";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        origincoords[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX()); //I hate you sometimes, Notch.  Really? Every quadrant is different?
        origincoords[1] = tb.getY() + .5;
        origincoords[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());
        LineA(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        if (origincoords[0] == 0 && origincoords[1] == 0 && origincoords[2] == 0) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
        } else {
            targetcoords[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX());
            targetcoords[1] = tb.getY() + .5;
            targetcoords[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());
            LineP(v);
        }
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        //vm.voxel();
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
        }
    }

    public void LineA(vData v) {
        v.owner().getPlayer().sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
    }

    public void LineP(vData v) {
        w = v.owner().getPlayer().getWorld();
        int bId = v.voxelId;
        double linelength = 0;

        //Calculate slope vector
        for (int i = 0; i < 3; i++) {
            slopevector[i] = targetcoords[i] - origincoords[i];
        }
        //Calculate line length in 
        linelength = Math.pow((Math.pow(slopevector[0], 2) + Math.pow(slopevector[1], 2) + Math.pow(slopevector[2], 2)), .5);

        //Unitize slope vector
        for (int i = 0; i < 3; i++) {
            slopevector[i] = slopevector[i] / linelength;
        }
        //Make the Changes

        for (int t = 0; t <= linelength; t++) {

            //Update current coords
            for (int i = 0; i < 3; i++) {
                currentcoords[i] = (int) (origincoords[i] + t * slopevector[i]);
            }

            if (currentcoords[0] != previouscoords[0] || currentcoords[1] != previouscoords[1] || currentcoords[2] != previouscoords[2]) {  // Don't double-down
                current.perform(clampY(currentcoords[0], currentcoords[1], currentcoords[2]));
            }

            previouscoords = Arrays.copyOf(currentcoords, currentcoords.length);
        }

        //Line might be a block short, check target block
        //current.perform(clampY((int) Math.floor(targetcoords[0] - .5 * targetcoords[0] / Math.abs(targetcoords[0])), (int) Math.floor(targetcoords[1] - .5), (int) Math.floor(targetcoords[2] - .5 * targetcoords[2] / Math.abs(targetcoords[2]))));

        v.storeUndo(current.getUndo());

        //RESET BRUSH
        //origincoords[0] = 0;
        //origincoords[1] = 0;
        //origincoords[2] = 0;
    }
}
