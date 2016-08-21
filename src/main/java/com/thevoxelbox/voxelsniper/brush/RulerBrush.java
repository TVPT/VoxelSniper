package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Measures the length between two positions.
 */
public class RulerBrush extends Brush {

    private Vector3i pos;

    private int xOff = 0;
    private int yOff = 0;
    private int zOff = 0;

    public RulerBrush() {
        this.setName("Ruler");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.pos == null) {
            v.sendMessage(TextColors.DARK_PURPLE + "First point selected.");
            this.pos = this.targetBlock.getBlockPosition();
        } else {
            final Undo undo = new Undo(1);
            Location<World> target = this.targetBlock.add(this.xOff, this.yOff, this.zOff);
            undo.put(target);
            target.setBlock(v.getVoxelIdState(), this.cause);
            v.owner().storeUndo(undo);
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.pos == null) {
            v.sendMessage(TextColors.RED + "Select a first point with the arrow.");
            return;
        }
        v.sendMessage(TextColors.AQUA + "X change: " + (this.targetBlock.getX() - this.pos.getX()));
        v.sendMessage(TextColors.AQUA + "Y change: " + (this.targetBlock.getY() - this.pos.getY()));
        v.sendMessage(TextColors.AQUA + "Z change: " + (this.targetBlock.getZ() - this.pos.getZ()));
        final double distance = this.targetBlock.getBlockPosition().sub(this.pos).length();
        v.sendMessage(TextColors.AQUA + "Distance = " + distance);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.voxel();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD,
                        "Ruler Brush instructions: Right click first point with the arrow. Right click with powder for distances from that block (can repeat without getting a new first block.) For placing blocks, use arrow and input the desired coordinates with parameters.");
                v.sendMessage(TextColors.LIGHT_PURPLE,
                        "/b r x[x value] y[y value] z[z value] -- Will place blocks one at a time of the type you have set with /v at the location you click + this many units away.  If you don't include a value, it will be zero.  Don't include ANY values, and the brush will just measure distance.");
                v.sendMessage(TextColors.BLUE, "/b r ruler -- will reset the tool to just measure distances, not layout blocks.");

                return;
            } else if (parameter.startsWith("x")) {
                this.xOff = Integer.parseInt(parameter.replace("x", ""));
                v.sendMessage(TextColors.AQUA, "X offset set to " + this.xOff);
            } else if (parameter.startsWith("y")) {
                this.yOff = Integer.parseInt(parameter.replace("y", ""));
                v.sendMessage(TextColors.AQUA, "Y offset set to " + this.yOff);
            } else if (parameter.startsWith("z")) {
                this.zOff = Integer.parseInt(parameter.replace("z", ""));
                v.sendMessage(TextColors.AQUA, "Z offset set to " + this.zOff);
            } else if (parameter.startsWith("ruler")) {
                this.zOff = 0;
                this.yOff = 0;
                this.xOff = 0;
                v.sendMessage(TextColors.BLUE, "Ruler mode.");
            } else {
                v.sendMessage(TextColors.RED, "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ruler";
    }
}
