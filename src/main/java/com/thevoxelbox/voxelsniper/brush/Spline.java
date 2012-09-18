package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * FOR ANY BRUSH THAT USES A SPLINE, EXTEND THAT BRUSH FROM THIS BRUSH!!! That way, the spline calculations are already there. Also, the UI for
 * the splines will be included.
 * @author psanker 
 * 
 */
public class Spline extends PerformBrush {

    // Vector class for splines
    protected class Point {
        int x;
        int y;
        int z;

        public Point(final Block b) {
            this.x = b.getX();
            this.y = b.getY();
            this.z = b.getZ();
        }

        public Point(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public final Point add(final Point p) {
            return new Point(this.x + p.x, this.y + p.y, this.z + p.z);
        }

        public final Point multiply(final int scalar) {
            return new Point(this.x * scalar, this.y * scalar, this.z * scalar);
        }

        public final Point subtract(final Point p) {
            return new Point(this.x - p.x, this.y - p.y, this.z - p.z);
        }
    }

    private final ArrayList<Block> endPts = new ArrayList<Block>();
    private final ArrayList<Block> ctrlPts = new ArrayList<Block>();
    protected ArrayList<Point> spline = new ArrayList<Point>();
    protected boolean set;
    protected boolean ctrl;

    protected String[] sparams = { "ss", "sc", "clear" };

    private static int timesUsed = 0;

    public Spline() {
        this.setName("Spline");
    }

    public final void addToSet(final SnipeData v, final boolean ep) {
        if (ep) {
            if (this.endPts.contains(this.getTargetBlock()) || this.endPts.size() == 2) {
                return;
            }

            this.endPts.add(this.getTargetBlock());
            v.sendMessage(ChatColor.GRAY + "Added block " + ChatColor.RED + "(" + this.getBlockPositionX() + ", " + this.getBlockPositionY() + ", " + this.getBlockPositionZ() + ") " + ChatColor.GRAY
                    + "to endpoint selection");
            return;
        }

        if (this.ctrlPts.contains(this.getTargetBlock()) || this.ctrlPts.size() == 2) {
            return;
        }

        this.ctrlPts.add(this.getTargetBlock());
        v.sendMessage(ChatColor.GRAY + "Added block " + ChatColor.RED + "(" + this.getBlockPositionX() + ", " + this.getBlockPositionY() + ", " + this.getBlockPositionZ() + ") " + ChatColor.GRAY
                + "to control point selection");
    }

    public final void removeFromSet(final SnipeData v, final boolean ep) {
        if (ep) {
            if (this.endPts.contains(this.getTargetBlock()) == false) {
                v.sendMessage(ChatColor.RED + "That block is not in the endpoint selection set.");
                return;
            }

            this.endPts.add(this.getTargetBlock());
            v.sendMessage(ChatColor.GRAY + "Removed block " + ChatColor.RED + "(" + this.getBlockPositionX() + ", " + this.getBlockPositionY() + ", " + this.getBlockPositionZ() + ") " + ChatColor.GRAY
                    + "from endpoint selection");
            return;
        }

        if (this.ctrlPts.contains(this.getTargetBlock()) == false) {
            v.sendMessage(ChatColor.RED + "That block is not in the control point selection set.");
            return;
        }

        this.ctrlPts.remove(this.getTargetBlock());
        v.sendMessage(ChatColor.GRAY + "Removed block " + ChatColor.RED + "(" + this.getBlockPositionX() + ", " + this.getBlockPositionY() + ", " + this.getBlockPositionZ() + ") " + ChatColor.GRAY
                + "from control point selection");
    }

    public final boolean spline(final Point start, final Point end, final Point c1, final Point c2, final SnipeData v) {
        this.spline.clear();

        try {
            final Point _c = (c1.subtract(start)).multiply(3);
            final Point _b = ((c2.subtract(c1)).multiply(3)).subtract(_c);
            final Point _a = ((end.subtract(start)).subtract(_c)).subtract(_b);

            for (double _t = 0.0; _t < 1.0; _t += 0.01) {
                final int _px = (int) Math.round((_a.x * (_t * _t * _t)) + (_b.x * (_t * _t)) + (_c.x * _t) + this.endPts.get(0).getX());
                final int _py = (int) Math.round((_a.y * (_t * _t * _t)) + (_b.y * (_t * _t)) + (_c.y * _t) + this.endPts.get(0).getY());
                final int _pz = (int) Math.round((_a.z * (_t * _t * _t)) + (_b.z * (_t * _t)) + (_c.z * _t) + this.endPts.get(0).getZ());

                if (!this.spline.contains(new Point(_px, _py, _pz))) {
                    this.spline.add(new Point(_px, _py, _pz));
                }
            }

            return true;
        } catch (final Exception _e) {
            v.sendMessage(ChatColor.RED + "Not enough points selected; " + this.endPts.size() + " endpoints, " + this.ctrlPts.size() + " control points");
            return false;
        }
    }
    
    protected final void render(final SnipeData v) {
    	if (this.spline.isEmpty()) {
    		return;
    	}
    	
    	for (final Point _pt : this.spline) {
    		this.current.perform(this.clampY(_pt.x, _pt.y, _pt.z));
    	}
    	
    	v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        if (this.set) {
            this.removeFromSet(v, true);
        } else if (this.ctrl) {
            this.removeFromSet(v, false);
        }
    }

    protected final void clear(final SnipeData v) {
        this.spline.clear();
        this.ctrlPts.clear();
        this.endPts.clear();
        v.sendMessage(ChatColor.GRAY + "Bezier curve cleared.");
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        if (this.set) {
            this.addToSet(v, true);
        }
        if (this.ctrl) {
            this.addToSet(v, false);
        }
    }
    

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());

        if (this.set) {
            vm.custom(ChatColor.GRAY + "Endpoint selection mode ENABLED.");
        } else if (this.ctrl) {
            vm.custom(ChatColor.GRAY + "Control point selection mode ENABLED.");
        } else {
            vm.custom(ChatColor.AQUA + "No selection mode enabled.");
        }
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        for (int _i = 1; _i < par.length; _i++) {
        	if (par[_i].equalsIgnoreCase("info")) {
        		v.sendMessage(ChatColor.GOLD + "Spline brush parameters");
        		v.sendMessage(ChatColor.AQUA + "ss: Enable endpoint selection mode for desired curve");
        		v.sendMessage(ChatColor.AQUA + "sc: Enable control point selection mode for desired curve");
        		v.sendMessage(ChatColor.AQUA + "clear: Clear out the curve selection");
        		v.sendMessage(ChatColor.AQUA + "ren: Render curve from control points");
        		return;
        	}
            if (par[_i].equalsIgnoreCase("sc")) {
                if (!this.ctrl) {
                    this.set = false;
                    this.ctrl = true;
                    v.sendMessage(ChatColor.GRAY + "Control point selection mode ENABLED.");
                    continue;
                } else {
                    this.ctrl = false;
                    v.sendMessage(ChatColor.AQUA + "Control point selection mode disabled.");
                    continue;
                }

            } else if (par[_i].equalsIgnoreCase("ss")) {
                if (!this.set) {
                    this.set = true;
                    this.ctrl = false;
                    v.sendMessage(ChatColor.GRAY + "Endpoint selection mode ENABLED.");
                    continue;
                } else {
                    this.set = false;
                    v.sendMessage(ChatColor.AQUA + "Endpoint selection mode disabled.");
                    continue;
                }

            } else if (par[_i].equalsIgnoreCase("clear")) {
                this.clear(v);

            } else if (par[_i].equalsIgnoreCase("ren")) {
                if (this.spline(new Point(this.endPts.get(0)), new Point(this.endPts.get(1)), new Point(this.ctrlPts.get(0)), new Point(this.ctrlPts.get(1)), v)) {
                    this.render(v);
                }
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }
    
    @Override
    public final int getTimesUsed() {
        return Spline.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        Spline.timesUsed = tUsed;
    }
}
