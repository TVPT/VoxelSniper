package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author psanker
 */
public class Ellipse extends PerformBrush {

    private int xscl;
    private int yscl;
    private int steps;
    private boolean fill;

    private static int timesUsed = 0;

    public Ellipse() {
        this.name = "Ellipse";
    }

    public final void ellipse(final vData v) {
        final double stepsize = ((2 * Math.PI) / this.steps);

        if (stepsize <= 0) {
            v.sendMessage("Derp");
            return;
        }

        try {
            for (double t = 0; (t <= (2 * Math.PI)); t += stepsize) {
                final double _x = (this.xscl * Math.cos(t));
                final double _y = (this.yscl * Math.sin(t));

                final int x = (int) Math.round(_x);
                final int y = (int) Math.round(_y);

                switch (this.tb.getFace(this.lb)) {
                case NORTH:
                case SOUTH:
                    this.current.perform(this.clampY(this.bx, this.by + x, this.bz + y));
                    break;
                case EAST:
                case WEST:
                    this.current.perform(this.clampY(this.bx + x, this.by + y, this.bz));
                    break;
                case UP:
                case DOWN:
                    this.current.perform(this.clampY(this.bx + x, this.by, this.bz + y));
                default:
                    break;
                }

                if (t >= (2 * Math.PI)) {
                    break;
                }
            }
        } catch (final Exception e) {
            v.sendMessage(ChatColor.RED + "Invalid target.");
        }

        v.storeUndo(this.current.getUndo());
    }

    public final void ellipsefill(final vData v) {
        this.current.perform(this.clampY(this.bx, this.by, this.bz));

        final double stepsize = ((2 * Math.PI) / this.steps);
        int ix = this.xscl;
        int iy = this.yscl;

        if (stepsize <= 0) {
            v.sendMessage("Derp");
            return;
        }

        try {
            if (ix >= iy) { // Need this unless you want weird holes
                for (iy = this.yscl; iy > 0; iy--) {
                    for (double t = 0; (t <= (2 * Math.PI)); t += stepsize) {
                        final double _x = (ix * Math.cos(t));
                        final double _y = (iy * Math.sin(t));

                        final int x = (int) Math.round(_x);
                        final int y = (int) Math.round(_y);

                        switch (this.tb.getFace(this.lb)) {
                        case NORTH:
                        case SOUTH:
                            this.current.perform(this.clampY(this.bx, this.by + x, this.bz + y));
                            break;
                        case EAST:
                        case WEST:
                            this.current.perform(this.clampY(this.bx + x, this.by + y, this.bz));
                            break;
                        case UP:
                        case DOWN:
                            this.current.perform(this.clampY(this.bx + x, this.by, this.bz + y));
                        default:
                            break;
                        }

                        if (t >= (2 * Math.PI)) {
                            break;
                        }
                    }
                    ix--;
                }
            } else {
                for (ix = this.xscl; ix > 0; ix--) {
                    for (double t = 0; (t <= (2 * Math.PI)); t += stepsize) {
                        final double _x = (ix * Math.cos(t));
                        final double _y = (iy * Math.sin(t));

                        final int x = (int) Math.round(_x);
                        final int y = (int) Math.round(_y);

                        switch (this.tb.getFace(this.lb)) {
                        case NORTH:
                        case SOUTH:
                            this.current.perform(this.clampY(this.bx, this.by + x, this.bz + y));
                            break;
                        case EAST:
                        case WEST:
                            this.current.perform(this.clampY(this.bx + x, this.by + y, this.bz));
                            break;
                        case UP:
                        case DOWN:
                            this.current.perform(this.clampY(this.bx + x, this.by, this.bz + y));
                        default:
                            break;
                        }

                        if (t >= (2 * Math.PI)) {
                            break;
                        }
                    }
                    iy--;
                }
            }
        } catch (final Exception e) {
            v.sendMessage(ChatColor.RED + "Invalid target.");
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final int getTimesUsed() {
        return Ellipse.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        if (this.xscl < 1 || this.xscl > 9999) {
            this.xscl = 10;
        }

        if (this.yscl < 1 || this.yscl > 9999) {
            this.yscl = 10;
        }

        if (this.steps < 1 || this.steps > 2000) {
            this.steps = 200;
        }

        vm.brushName(this.name);
        vm.custom(ChatColor.AQUA + "X-size set to: " + ChatColor.DARK_AQUA + this.xscl);
        vm.custom(ChatColor.AQUA + "Y-size set to: " + ChatColor.DARK_AQUA + this.yscl);
        vm.custom(ChatColor.AQUA + "Render step number set to: " + ChatColor.DARK_AQUA + this.steps);
        if (this.fill == true) {
            vm.custom(ChatColor.AQUA + "Fill mode is enabled");
        } else {
            vm.custom(ChatColor.AQUA + "Fill mode is disabled");
        }
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Ellipse brush parameters");
            v.sendMessage(ChatColor.AQUA + "x[n]: Set X size modifier to n");
            v.sendMessage(ChatColor.AQUA + "y[n]: Set Y size modifier to n");
            v.sendMessage(ChatColor.AQUA + "t[n]: Set the amount of time steps");
            v.sendMessage(ChatColor.AQUA + "fill: Toggles fill mode");
            return;
        }

        for (int i = 1; i < par.length; i++) {
            try {
                if (par[i].startsWith("x")) {
                    this.xscl = Integer.parseInt(par[i].replace("x", ""));
                    v.sendMessage(ChatColor.AQUA + "X-scale modifier set to: " + this.xscl);
                    continue;
                } else if (par[i].startsWith("y")) {
                    this.yscl = Integer.parseInt(par[i].replace("y", ""));
                    v.sendMessage(ChatColor.AQUA + "Y-scale modifier set to: " + this.yscl);
                    continue;
                } else if (par[i].startsWith("t")) {
                    this.steps = Integer.parseInt(par[i].replace("t", ""));
                    v.sendMessage(ChatColor.AQUA + "Render step number set to: " + this.steps);
                    continue;
                } else if (par[i].equalsIgnoreCase("fill")) {
                    if (this.fill == true) {
                        this.fill = false;
                        v.sendMessage(ChatColor.AQUA + "Fill mode is disabled");
                        continue;
                    } else {
                        this.fill = true;
                        v.sendMessage(ChatColor.AQUA + "Fill mode is enabled");
                        continue;
                    }
                } else {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                }

            } catch (final Exception e) {
                v.sendMessage(ChatColor.RED + "Incorrect parameter \"" + par[i] + "\"; use the \"info\" parameter.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Ellipse.timesUsed = tUsed;
    }

    private void sort(final vData v) {
        if (this.fill) {
            this.ellipsefill(v);
        } else {
            this.ellipse(v);
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.sort(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.lb.getX();
        this.by = this.lb.getY();
        this.bz = this.lb.getZ();
        this.sort(v);
    }
}
