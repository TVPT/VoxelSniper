package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Creates an ellipsoid.
 */
public class EllipsoidBrush extends PerformBrush {

    private double xrad = -1;
    private double yrad = -1;
    private double zrad = -1;

    public EllipsoidBrush() {
        this.setName("Ellipsoid");
    }

    private void ellipsoid(final SnipeData v, Location<World> targetBlock) {
        double xrads = this.xrad * this.xrad;
        double yrads = this.yrad * this.yrad;
        double zrads = this.zrad * this.zrad;

        int minx = GenericMath.floor(targetBlock.getBlockX() - this.xrad);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + this.xrad) + 1;
        int miny = Math.max(GenericMath.floor(targetBlock.getBlockY() - this.yrad), 0);
        int maxy = Math.min(GenericMath.floor(targetBlock.getBlockY() + this.yrad) + 1, WORLD_HEIGHT);
        int minz = GenericMath.floor(targetBlock.getBlockZ() - this.zrad);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + this.zrad) + 1;

        this.undo = new Undo(GenericMath.floor(4 * Math.PI * (this.xrad + 1) * (this.yrad + 1) * (this.zrad + 1) / 3));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (minx - x) * (minx - x);
            for (int y = miny; y <= maxy; y++) {
                double ys = (miny - y) * (miny - y);
                for (int z = minz; z <= maxz; z++) {
                    double zs = (minz - z) * (minz - z);
                    if (xs / xrads + ys / yrads + zs / zrads < 1) {
                        perform(v, x, y, z);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.ellipsoid(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.ellipsoid(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.AQUA, "X-radius set to: ", TextColors.DARK_AQUA, this.xrad);
        vm.custom(TextColors.AQUA, "Y-radius set to: ", TextColors.DARK_AQUA, this.yrad);
        vm.custom(TextColors.AQUA, "Z-radius set to: ", TextColors.DARK_AQUA, this.zrad);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD + "Ellipse brush parameters");
                v.sendMessage(TextColors.AQUA + "x[n]: Set X radius to n");
                v.sendMessage(TextColors.AQUA + "y[n]: Set Y radius to n");
                v.sendMessage(TextColors.AQUA + "z[n]: Set Z radius to n");
                return;
            } else if (parameter.startsWith("x")) {
                try {
                    double val = Double.parseDouble(parameter.replace("x", ""));
                    if (val <= 0) {
                        v.sendMessage(TextColors.RED, "X radius must be greater than zero.");
                    } else {
                        this.xrad = val;
                        v.sendMessage(TextColors.GREEN, "X radius  set to " + this.xrad);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid X radius value.");
                }
            } else if (parameter.startsWith("y")) {
                try {
                    double val = Double.parseDouble(parameter.replace("y", ""));
                    if (val <= 0) {
                        v.sendMessage(TextColors.RED, "Y radius must be greater than zero.");
                    } else {
                        this.yrad = val;
                        v.sendMessage(TextColors.GREEN, "Y radius  set to " + this.yrad);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid Y radius value.");
                }
            } else if (parameter.startsWith("z")) {
                try {
                    double val = Double.parseDouble(parameter.replace("z", ""));
                    if (val <= 0) {
                        v.sendMessage(TextColors.RED, "Z radius must be greater than zero.");
                    } else {
                        this.zrad = val;
                        v.sendMessage(TextColors.GREEN, "Z radius  set to " + this.zrad);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid Z radius value.");
                }
            } else {
                v.sendMessage(TextColors.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
            }
        }
        if (this.xrad <= 0) {
            this.xrad = v.getBrushSize();
            v.sendMessage(TextColors.GREEN, "X radius  set to " + this.xrad);
        }
        if (this.yrad <= 0) {
            this.yrad = v.getBrushSize();
            v.sendMessage(TextColors.GREEN, "Y radius  set to " + this.yrad);
        }
        if (this.zrad <= 0) {
            this.zrad = v.getBrushSize();
            v.sendMessage(TextColors.GREEN, "Z radius  set to " + this.zrad);
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ellipsoid";
    }
}
