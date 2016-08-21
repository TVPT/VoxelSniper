package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Creates cylinders.
 */
public class CylinderBrush extends PerformBrush {

    public CylinderBrush() {
        this.setName("Cylinder");
    }

    private void cylinder(SnipeData v, Location<World> targetBlock) {
        int yStartingPoint = targetBlock.getBlockY() + v.getcCen();
        int yEndPoint = targetBlock.getBlockY() + v.getVoxelHeight() + v.getcCen();

        if (yEndPoint < yStartingPoint) {
            yEndPoint = yStartingPoint;
        }
        if (yStartingPoint < 0) {
            yStartingPoint = 0;
        } else if (yStartingPoint > WORLD_HEIGHT) {
            yStartingPoint = WORLD_HEIGHT;
        }
        if (yEndPoint < 0) {
            yEndPoint = 0;
        } else if (yEndPoint > WORLD_HEIGHT) {
            yEndPoint = WORLD_HEIGHT;
        }

        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;

        this.undo = new Undo(GenericMath.floor(Math.PI * (brushSize + 1) * (brushSize + 1)));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (minx - x) * (minx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (minz - z) * (minz - z);
                if (xs + zs < brushSizeSquared) {
                    for (int y = yEndPoint; y >= yStartingPoint; y--) {
                        perform(v, x, targetBlock.getBlockY(), z);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        cylinder(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        cylinder(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.center();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 1; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD + "Cylinder Brush Parameters:");
                v.sendMessage(TextColors.AQUA + "/b c h[number] -- set the cylinder v.voxelHeight.  Default is 1.");
                v.sendMessage(TextColors.DARK_BLUE
                        + "/b c c[number] -- set the origin of the cylinder compared to the target block. Positive numbers will move the cylinder upward, negative will move it downward.");
                return;
            }
            if (parameter.startsWith("h")) {
                try {
                    v.setVoxelHeight((int) Double.parseDouble(parameter.replace("h", "")));
                    v.sendMessage(TextColors.AQUA + "Cylinder v.voxelHeight set to: " + v.getVoxelHeight());
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid height given.");
                }
            } else if (parameter.startsWith("c")) {
                try {
                    v.setcCen((int) Double.parseDouble(parameter.replace("c", "")));
                    v.sendMessage(TextColors.AQUA + "Cylinder origin set to: " + v.getcCen());
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid origin given.");
                }
            } else {
                v.sendMessage(TextColors.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.cylinder";
    }
}
