/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;

/**
 *
 * @author psanker
 */

public class Ellipse extends PerformBrush {

    private int xscl;
    private int yscl;
    private int steps;
    private boolean fill;
    
    public Ellipse() {
        name = "Ellipse";
    }
    
    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        sort(v);
    }
    
    @Override
    public void powder(vSniper v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        sort(v);
    }
    
    @Override
    public void info(vMessage vm) {
        if (xscl < 1 || xscl > 9999) {
            xscl = 10;
        }
        
        if (yscl < 1 || yscl > 9999) {
            yscl = 10;
        }
        
        if (steps < 1 || steps > 2000) {
            steps = 200;
        }
        
        vm.brushName(name);
        vm.custom(ChatColor.AQUA+"X-size set to: "+ChatColor.DARK_AQUA+xscl);
        vm.custom(ChatColor.AQUA+"Y-size set to: "+ChatColor.DARK_AQUA+yscl);
        vm.custom(ChatColor.AQUA+"Render step number set to: "+ChatColor.DARK_AQUA+steps);
        if (fill == true)
            vm.custom(ChatColor.AQUA+"Fill mode is enabled");
        else
            vm.custom(ChatColor.AQUA+"Fill mode is disabled");
    }
    
    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD+"Ellipse brush parameters");
            v.p.sendMessage(ChatColor.AQUA+"x[n]: Set X size modifier to n");
            v.p.sendMessage(ChatColor.AQUA+"y[n]: Set Y size modifier to n");
            v.p.sendMessage(ChatColor.AQUA+"t[n]: Set the amount of time steps");
            v.p.sendMessage(ChatColor.AQUA+"fill: Toggles fill mode");
            return;
        }
        
        for (int i = 1; i < par.length; i++) {
            try {
                if (par[i].startsWith("x")) {
                    xscl = Integer.parseInt(par[i].replace("x", ""));
                    v.p.sendMessage(ChatColor.AQUA+"X-scale modifier set to: " + xscl);
                    continue;
                }

                else if (par[i].startsWith("y")) {
                    yscl = Integer.parseInt(par[i].replace("y", ""));
                    v.p.sendMessage(ChatColor.AQUA+"Y-scale modifier set to: " + yscl);
                    continue;
                }

                else if (par[i].startsWith("t")) {
                    steps = Integer.parseInt(par[i].replace("t", ""));
                    v.p.sendMessage(ChatColor.AQUA+"Render step number set to: " + steps);
                    continue;
                }
                
                else if (par[i].equalsIgnoreCase("fill")) {
                    if (fill == true) {
                        fill = false;
                        v.p.sendMessage(ChatColor.AQUA+"Fill mode is disabled");
                        continue;
                    }
                    
                    else {
                        fill = true;
                        v.p.sendMessage(ChatColor.AQUA+"Fill mode is enabled");
                        continue;
                    }
                }

                else {
                    v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                }

            } catch(Exception e) {
                v.p.sendMessage(ChatColor.RED+"Incorrect parameter \""+par[i]+"\"; use the \"info\" parameter.");
            }
        }
    }
    
    private void sort(vSniper v) {
        if (fill)
            ellipsefill(v);
        else
            ellipse(v);
    }
    
    public void ellipse(vSniper v) {
        double stepsize = ((2 * Math.PI) / steps);
        
        if (stepsize <= 0) {
            v.p.sendMessage("Derp");
            return;
        }
        
        try { 
            for (double t = 0; (t <= (2 * Math.PI)); t += stepsize) {
                double _x = (xscl * Math.cos(t));
                double _y = (yscl * Math.sin(t));

                int x = (int) Math.round(_x);
                int y = (int) Math.round(_y);
                
                switch(tb.getFace(lb)) {
                    case NORTH:
                    case SOUTH:
                        current.perform(clampY(bx, by+x, bz+y));
                        break;
                    case EAST:
                    case WEST:
                        current.perform(clampY(bx+x, by+y, bz));
                        break;
                    case UP:
                    case DOWN:
                        current.perform(clampY(bx+x, by, bz+y));
                    default:
                        break;
                }

                if (t == (2 * Math.PI)) {
                    break;
                }
            }
        } catch (Exception e) {
            v.p.sendMessage(ChatColor.RED+"Invalid target.");
        }
        
        if (current.getUndo().getSize() > 0) {
            v.hashUndo.put(v.hashEn, current.getUndo());
            v.hashEn++;
        }
    }
    
    public void ellipsefill(vSniper v) {
        current.perform(clampY(bx, by, bz));
        
        double stepsize = ((2 * Math.PI) / steps);
        int ix = xscl;
        int iy = yscl;
        
        if (stepsize <= 0) {
            v.p.sendMessage("Derp");
            return;
        }
        
        try {
            if (ix >= iy) { // Need this unless you want weird holes
                for (iy = yscl; iy > 0; iy--) {
                    for (double t = 0; (t <= (2 * Math.PI)); t += stepsize) {
                        double _x = (ix * Math.cos(t));
                        double _y = (iy * Math.sin(t));

                        int x = (int) Math.round(_x);
                        int y = (int) Math.round(_y);

                        switch(tb.getFace(lb)) {
                            case NORTH:
                            case SOUTH:
                                current.perform(clampY(bx, by+x, bz+y));
                                break;
                            case EAST:
                            case WEST:
                                current.perform(clampY(bx+x, by+y, bz));
                                break;
                            case UP:
                            case DOWN:
                                current.perform(clampY(bx+x, by, bz+y));
                            default:
                                break;
                        }

                        if (t == (2 * Math.PI)) {
                            break;
                        }
                    }

                    ix--;
                }
            } else {
                for (ix = xscl; ix > 0; ix--) {
                    for (double t = 0; (t <= (2 * Math.PI)); t += stepsize) {
                        double _x = (ix * Math.cos(t));
                        double _y = (iy * Math.sin(t));

                        int x = (int) Math.round(_x);
                        int y = (int) Math.round(_y);

                        switch(tb.getFace(lb)) {
                            case NORTH:
                            case SOUTH:
                                current.perform(clampY(bx, by+x, bz+y));
                                break;
                            case EAST:
                            case WEST:
                                current.perform(clampY(bx+x, by+y, bz));
                                break;
                            case UP:
                            case DOWN:
                                current.perform(clampY(bx+x, by, bz+y));
                            default:
                                break;
                        }

                        if (t == (2 * Math.PI)) {
                            break;
                        }
                    }

                    iy--;
                }
            }
        } catch (Exception e) {
            v.p.sendMessage(ChatColor.RED+"Invalid target.");
        }

        if (current.getUndo().getSize() > 0) {
            v.hashUndo.put(v.hashEn, current.getUndo());
            v.hashEn++;
        }
    }
}