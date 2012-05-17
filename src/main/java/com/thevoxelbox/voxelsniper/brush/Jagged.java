/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;

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

    public Jagged() {
        name = "Jagged Line";
    }

    @Override
    public void arrow(vSniper v) {
        origincoords[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX()); //I hate you sometimes, Notch.  Really? Every quadrant is different?
        origincoords[1] = tb.getY() + .5;
        origincoords[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());
        JaggedA(v);
    }

    @Override
    public void powder(vSniper v) {

        if (origincoords[0] == 0 && origincoords[1] == 0 && origincoords[2] == 0) {
            v.p.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");

        } else {
            targetcoords[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX());
            targetcoords[1] = tb.getY() + .5;
            targetcoords[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());
            JaggedP(v);
        }

    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        //vm.voxel();
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Jagged Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a jagged line to set the second point.");
            v.p.sendMessage(ChatColor.AQUA + "/b j r# - sets the number of recursions (default 3, must be 1-10)");
            return;
        }
        if (par[1].startsWith("r")) {

            int temp = Integer.parseInt(par[1].substring(1));
            if (temp > 0 && temp <= 10) {
                recursion = temp;
                v.p.sendMessage(ChatColor.GREEN + "Recursion set to: " + recursion);
            } else {
                v.p.sendMessage(ChatColor.RED + "ERROR: Deviation must be 1-10.");
            }



            return;
        }





    }

    public void JaggedA(vSniper v) {
        v.p.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
    }

    public void JaggedP(vSniper v) {
        w = v.p.getWorld();
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
            for (int r = 0; r < recursion; r++) {
                if (currentcoords[0] != previouscoords[0] || currentcoords[1] != previouscoords[1] || currentcoords[2] != previouscoords[2]) {  // Don't double-down

                    current.perform(clampY(currentcoords[0] + random.nextInt(3) - 1, currentcoords[1] + random.nextInt(3) - 1, currentcoords[2] + random.nextInt(3) - 1));

                }
            }
            previouscoords = Arrays.copyOf(currentcoords, currentcoords.length);

        }


        if (current.getUndo().getSize() > 0) {
            v.hashUndo.put(v.hashEn, current.getUndo());
            v.hashEn++;
        }

        //RESET BRUSH
        //origincoords[0] = 0;
        //origincoords[1] = 0;
        //origincoords[2] = 0;


    }
}
