/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;

/**
 *
 * @author Gavjenks
 */
public class Ruler extends Brush {

    protected boolean first = true;
    protected double[] coords = new double[3];

    public Ruler() {
        name = "Ruler";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        rulerA(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        rulerP(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.voxel();
    }
    private int xOff = 0;
    private int yOff = 0;
    private int zOff = 0;

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Ruler Brush instructions: Right click first point with the arrow. Right click with powder for distances from that block (can repeat without getting a new first block.) For placing blocks, use arrow and input the desired coordinates with parameters.");
            v.sendMessage(ChatColor.LIGHT_PURPLE + "/b r x[x value] y[y value] z[z value] -- Will place blocks one at a time of the type you have set with /v at the location you click + this many units away.  If you don't include a value, it will be zero.  Don't include ANY values, and the brush will just measure distance.");
            v.sendMessage(ChatColor.BLUE + "/b r ruler -- will reset the tool to just measure distances, not layout blocks.");

            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("x")) {
                xOff = Integer.parseInt(par[x].replace("x", ""));
                v.sendMessage(ChatColor.AQUA + "X offset set to " + xOff);
                continue;
            } else if (par[x].startsWith("y")) {
                yOff = Integer.parseInt(par[x].replace("y", ""));
                v.sendMessage(ChatColor.AQUA + "Y offset set to " + yOff);
                continue;
            } else if (par[x].startsWith("z")) {
                zOff = Integer.parseInt(par[x].replace("z", ""));
                v.sendMessage(ChatColor.AQUA + "Z offset set to " + zOff);
                continue;
            } else if (par[x].startsWith("ruler")) {
                zOff = 0;
                yOff = 0;
                xOff = 0;
                v.sendMessage(ChatColor.BLUE + "Ruler mode.");
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }

    }

    public void rulerA(vData v) {
        int bId = v.voxelId;
        //tb = tb;
        if (xOff == 0 && yOff == 0 && zOff == 0) {

            coords[0] = tb.getX();
            coords[1] = tb.getY();
            coords[2] = tb.getZ();
            v.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
            first = !first;

        } else {
            vUndo h = new vUndo(tb.getWorld().getName());

            h.put(clampY(bx + xOff, by + yOff, bz + zOff));
            setBlockIdAt(bId, bx + xOff, by + yOff, bz + zOff);
            v.storeUndo(h);
        }
    }

    public void rulerP(vData v) {
        if (coords[0] == 0 && coords[1] == 0 && coords[2] == 0) {
            v.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow.  Comparing to point 0,0,0 instead.");
        }

        v.sendMessage(ChatColor.BLUE + "Format = (second coord - first coord)");
        v.sendMessage(ChatColor.AQUA + "X change: " + (tb.getX() - coords[0]));
        v.sendMessage(ChatColor.AQUA + "Y change: " + (tb.getY() - coords[1]));
        v.sendMessage(ChatColor.AQUA + "Z change: " + (tb.getZ() - coords[2]));
        double distance = Math.sqrt(Math.pow((coords[0] - tb.getX()), 2) + Math.pow((coords[1] - tb.getY()), 2) + Math.pow((coords[2] - tb.getZ()), 2));
        distance = roundTwoDecimals(distance);
        double blockdistance = Math.abs(Math.max(Math.max(Math.abs(tb.getX() - coords[0]), Math.abs(tb.getY() - coords[1])), Math.abs(tb.getZ() - coords[2]))) + 1;
        blockdistance = roundTwoDecimals(blockdistance);
        v.sendMessage(ChatColor.AQUA + "Euclidean distance = " + distance);
        v.sendMessage(ChatColor.AQUA + "Block distance = " + blockdistance); //more what people would expect - Gilt
        //}
    }

    double roundTwoDecimals(double d) {
        java.text.DecimalFormat twoDForm = new java.text.DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
