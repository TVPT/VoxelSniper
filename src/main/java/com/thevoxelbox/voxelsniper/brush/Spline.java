/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author psanker
 * FOR ANY BRUSH THAT USES A SPLINE, EXTEND THAT BRUSH FROM THIS BRUSH!!!
 * That way, the spline calculations are already there.
 * Also, the UI for the splines will be included.
 *
 */
public class Spline extends PerformBrush {

    private ArrayList<Block> endPts = new ArrayList<Block>();
    private ArrayList<Block> ctrlPts = new ArrayList<Block>();
    protected ArrayList<Point> spline = new ArrayList<Point>();
    protected boolean set;
    protected boolean ctrl;
    protected String[] sparams = {"ss", "sc", "clear"};

    public Spline() {
        name = "Spline";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        if (set) {
            removeFromSet(v, true);
        } else if (ctrl) {
            removeFromSet(v, false);
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        if (set) {
            addToSet(v, true);
        }
        if (ctrl) {
            addToSet(v, false);
        }
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Spline brush parameters");
            v.sendMessage(ChatColor.AQUA + "ss: Enable endpoint selection mode for desired curve");
            v.sendMessage(ChatColor.AQUA + "sc: Enable control point selection mode for desired curve");
            v.sendMessage(ChatColor.AQUA + "clear: Clear out the curve selection");
            v.sendMessage(ChatColor.AQUA + "ren: Render curve from control points");
            return;
        }

        for (int i = 1; i < par.length; i++) {
            if (par[i].equalsIgnoreCase("sc")) {
                if (!ctrl) {
                    set = false;
                    ctrl = true;
                    v.sendMessage(ChatColor.GRAY + "Control point selection mode ENABLED.");
                    continue;
                } else {
                    ctrl = false;
                    v.sendMessage(ChatColor.AQUA + "Control point selection mode disabled.");
                    continue;
                }

            } else if (par[i].equalsIgnoreCase("ss")) {
                if (!set) {
                    set = true;
                    ctrl = false;
                    v.sendMessage(ChatColor.GRAY + "Endpoint selection mode ENABLED.");
                    continue;
                } else {
                    set = false;
                    v.sendMessage(ChatColor.AQUA + "Endpoint selection mode disabled.");
                    continue;
                }

            } else if (par[i].equalsIgnoreCase("clear")) {
                this.clear(v);

            } else if (par[i].equalsIgnoreCase("ren")) {
                if (spline(new Point(endPts.get(0)), new Point(endPts.get(1)), new Point(ctrlPts.get(0)), new Point(ctrlPts.get(1)), v)) {
                    render(v);
                }
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);

        if (set) {
            vm.custom(ChatColor.GRAY + "Endpoint selection mode ENABLED.");
        } else if (ctrl) {
            vm.custom(ChatColor.GRAY + "Control point selection mode ENABLED.");
        } else {
            vm.custom(ChatColor.AQUA + "No selection mode enabled.");
        }
    }

    public void addToSet(vData v, boolean ep) {
        if (ep) {
            if (endPts.contains(tb) || endPts.size() == 2) {
                return;
            }

            endPts.add(tb);
            v.sendMessage(ChatColor.GRAY + "Added block " + ChatColor.RED + "(" + bx + ", " + by + ", " + bz + ") " + ChatColor.GRAY + "to endpoint selection");
            return;
        }

        if (ctrlPts.contains(tb) || ctrlPts.size() == 2) {
            return;
        }

        ctrlPts.add(tb);
        v.sendMessage(ChatColor.GRAY + "Added block " + ChatColor.RED + "(" + bx + ", " + by + ", " + bz + ") " + ChatColor.GRAY + "to control point selection");
    }

    public void removeFromSet(vData v, boolean ep) {
        if (ep) {
            if (endPts.contains(tb) == false) {
                v.sendMessage(ChatColor.RED + "That block is not in the endpoint selection set.");
                return;
            }

            endPts.add(tb);
            v.sendMessage(ChatColor.GRAY + "Removed block " + ChatColor.RED + "(" + bx + ", " + by + ", " + bz + ") " + ChatColor.GRAY + "from endpoint selection");
            return;
        }

        if (ctrlPts.contains(tb) == false) {
            v.sendMessage(ChatColor.RED + "That block is not in the control point selection set.");
            return;
        }

        ctrlPts.remove(tb);
        v.sendMessage(ChatColor.GRAY + "Removed block " + ChatColor.RED + "(" + bx + ", " + by + ", " + bz + ") " + ChatColor.GRAY + "from control point selection");
    }

    public boolean spline(Point start, Point end, Point c1, Point c2, vData v) {
        spline.clear();

        try {
            Point c = (c1.subtract(start)).multiply(3);
            Point b = ((c2.subtract(c1)).multiply(3)).subtract(c);
            Point a = ((end.subtract(start)).subtract(c)).subtract(b);

            for (double t = 0.0; t < 1.0; t += 0.01) {
                int px = (int) Math.round((a.x * (t * t * t)) + (b.x * (t * t)) + (c.x * t) + endPts.get(0).getX());
                int py = (int) Math.round((a.y * (t * t * t)) + (b.y * (t * t)) + (c.y * t) + endPts.get(0).getY());
                int pz = (int) Math.round((a.z * (t * t * t)) + (b.z * (t * t)) + (c.z * t) + endPts.get(0).getZ());

                if (!spline.contains(new Point(px, py, pz))) {
                    spline.add(new Point(px, py, pz));
                }
            }

            return true;
        } catch (Exception e) {
            v.sendMessage(ChatColor.RED + "Not enough points selected; " + endPts.size() + " endpoints, " + ctrlPts.size() + " control points");
            return false;
        }
    }

    protected void render(vData v) {
        if (spline.isEmpty()) {
            return;
        }

        for (Point pt : spline) {
            current.perform(clampY((int) pt.x, (int) pt.y, (int) pt.z));
        }

        v.storeUndo(current.getUndo());
    }

    protected void clear(vData v) {
        spline.clear();
        ctrlPts.clear();
        endPts.clear();
        v.sendMessage(ChatColor.GRAY + "Bezier curve cleared.");
    }

    // Vector class for splines
    protected class Point {

        int x;
        int y;
        int z;

        public Point(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point(Block b) {
            this.x = b.getX();
            this.y = b.getY();
            this.z = b.getZ();
        }

        public Point add(Point p) {
            return new Point(this.x + p.x, this.y + p.y, this.z + p.z);
        }

        public Point subtract(Point p) {
            return new Point(this.x - p.x, this.y - p.y, this.z - p.z);
        }

        public Point multiply(int scalar) {
            return new Point(this.x * scalar, this.y * scalar, this.z * scalar);
        }
    }
    
    private static int timesUsed = 0;
	
    @Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
