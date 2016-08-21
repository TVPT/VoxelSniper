package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Creates an ellipse.
 */
public class EllipseBrush extends PerformBrush {

    private double xrad = -1;
    private double yrad = -1;

    public EllipseBrush() {
        this.setName("Ellipse");
    }

    private void ellipse(final SnipeData v, Location<World> targetBlock, Direction axis) {
        double xrads = this.xrad * this.xrad;
        double yrads = this.yrad * this.yrad;
        int tx = targetBlock.getBlockX();
        int tz = targetBlock.getBlockZ();
        int minx = GenericMath.floor(targetBlock.getBlockX() - this.xrad);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + this.xrad) + 1;
        int minz = GenericMath.floor(targetBlock.getBlockZ() - this.yrad);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + this.yrad) + 1;

        this.undo = new Undo(GenericMath.floor(Math.PI * (this.xrad + 1) * (this.yrad + 1)));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (tx - x) * (tx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (tz - z) * (tz - z);
                if (xs / xrads + zs / yrads < 1) {
                    if (axis == Direction.UP) {
                        perform(v, x, targetBlock.getBlockY(), z);
                    } else if (axis == Direction.NORTH) {
                        perform(v, x, z, targetBlock.getBlockZ());
                    } else if (axis == Direction.EAST) {
                        perform(v, targetBlock.getBlockX(), x, z);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    private void pre(final SnipeData v, Location<World> target) {
        if (this.lastBlock.getBlockY() != this.targetBlock.getBlockY()) {
            ellipse(v, target, Direction.UP);
        } else if (this.lastBlock.getBlockX() != this.targetBlock.getBlockX()) {
            ellipse(v, target, Direction.EAST);
        } else if (this.lastBlock.getBlockZ() != this.targetBlock.getBlockZ()) {
            ellipse(v, target, Direction.NORTH);
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.pre(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.pre(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.AQUA, "X-radius set to: ", TextColors.DARK_AQUA, this.xrad);
        vm.custom(TextColors.AQUA, "Y-radius set to: ", TextColors.DARK_AQUA, this.yrad);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD + "Ellipse brush parameters");
                v.sendMessage(TextColors.AQUA + "x[n]: Set X radius to n");
                v.sendMessage(TextColors.AQUA + "y[n]: Set Y radius to n");
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
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ellipse";
    }
}
