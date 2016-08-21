package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.util.Rot3d;
import org.spongepowered.api.text.format.TextColors;

public class Rot3DBrush extends Brush {

    private double yaw;
    private double pitch;
    private double roll;

    private Rot3d rotUtil;

    public Rot3DBrush() {
        this.setName("Rotation");
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.brushMessage("Rotates a spherical area by a given yaw, pitch, and roll");
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        boolean changed = false;
        for (int i = 1; i < par.length; i++) {
            final String parameter = par[i];
            // which way is clockwise is less obvious for roll and pitch...
            // should probably fix that / make it clear
            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD, "Rotate brush Parameters:");
                v.sendMessage(TextColors.AQUA, "p[0-359] -- set degrees of pitch rotation (rotation about the Z axis).");
                v.sendMessage(TextColors.BLUE, "r[0-359] -- set degrees of roll rotation (rotation about the X axis).");
                v.sendMessage(TextColors.LIGHT_PURPLE, "y[0-359] -- set degrees of yaw rotation (Rotation about the Y axis).");

                return;
            } else if (parameter.startsWith("p")) {
                try {
                    if (this.pitch < 0 || this.pitch > 359) {
                        v.sendMessage(TextColors.RED + "Invalid brush parameters! Angles must be from 1-359");
                    } else {
                        this.pitch = Math.toRadians(Double.parseDouble(parameter.replace("p", "")));
                        v.sendMessage(TextColors.AQUA + "Around Z-axis degrees set to " + this.pitch);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid pitch given.");
                }
            } else if (parameter.startsWith("r")) {
                try {
                    if (this.roll < 0 || this.roll > 359) {
                        v.sendMessage(TextColors.RED + "Invalid brush parameters! Angles must be from 1-359");
                    } else {
                        this.roll = Math.toRadians(Double.parseDouble(parameter.replace("r", "")));
                        v.sendMessage(TextColors.AQUA + "Around X-axis degrees set to " + this.roll);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid roll given.");
                }
            } else if (parameter.startsWith("y")) {
                try {
                    if (this.yaw < 0 || this.yaw > 359) {
                        v.sendMessage(TextColors.RED + "Invalid brush parameters! Angles must be from 1-359");
                    } else {
                        this.yaw = Math.toRadians(Double.parseDouble(parameter.replace("y", "")));
                        v.sendMessage(TextColors.AQUA + "Around Y-axis degrees set to " + this.yaw);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid yaw given.");
                }
            }
        }
        if (this.rotUtil == null || changed) {
            this.rotUtil = new Rot3d(this.yaw, this.pitch, this.roll);
        }
    }

    private void rotate(final SnipeData v) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int minx = GenericMath.floor(this.targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(this.targetBlock.getBlockX() + brushSize) + 1;
        int miny = Math.max(GenericMath.floor(this.targetBlock.getBlockY() - brushSize), 0);
        int maxy = Math.min(GenericMath.floor(this.targetBlock.getBlockY() + brushSize) + 1, WORLD_HEIGHT);
        int minz = GenericMath.floor(this.targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(this.targetBlock.getBlockZ() + brushSize) + 1;

        // @Spongify need a block buffer
        
        // Approximate the size of the undo to the volume of a one larger sphere
        this.undo = new Undo(GenericMath.floor(4 * Math.PI * (brushSize + 1) * (brushSize + 1) * (brushSize + 1) / 3));

        for (int x = minx; x <= maxx; x++) {
            double xs = (minx - x) * (minx - x);
            for (int y = miny; y <= maxy; y++) {
                double ys = (miny - y) * (miny - y);
                for (int z = minz; z <= maxz; z++) {
                    double zs = (minz - z) * (minz - z);
                    if (xs + ys + zs < brushSizeSquared) {

                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        rotate(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        rotate(v);
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.rot3d";
    }
}
