package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * Fills in a cuboid area between two selected points.
 */
public class SetBrush extends PerformBrush {

    private static final int SELECTION_SIZE_MAX = 5000000;
    private Vector3i block = null;
    private UUID worldUid;

    public SetBrush() {
        this.setName("Set");
    }

    private void set(final SnipeData v, final Location<World> bl) {
        if (this.block == null || !bl.getExtent().getUniqueId().equals(this.worldUid)) {
            this.block = bl.getBlockPosition();
            this.worldUid = bl.getExtent().getUniqueId();
            v.sendMessage(TextColors.GRAY, "Point one");
            return;
        }
        final int lowX = (this.block.getX() <= bl.getBlockX()) ? this.block.getX() : bl.getBlockX();
        final int lowY = (this.block.getY() <= bl.getBlockY()) ? this.block.getY() : bl.getBlockY();
        final int lowZ = (this.block.getZ() <= bl.getBlockZ()) ? this.block.getZ() : bl.getBlockZ();
        final int highX = (this.block.getX() >= bl.getBlockX()) ? this.block.getX() : bl.getBlockX();
        final int highY = (this.block.getY() >= bl.getBlockY()) ? this.block.getY() : bl.getBlockY();
        final int highZ = (this.block.getZ() >= bl.getBlockZ()) ? this.block.getZ() : bl.getBlockZ();

        int size = Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY);
        if (size > SELECTION_SIZE_MAX) {
            v.sendMessage(TextColors.RED + "Selection size above hardcoded limit, please use a smaller selection.");
            return;
        }
        this.undo = new Undo(size);
        for (int y = lowY; y <= highY; y++) {
            for (int x = lowX; x <= highX; x++) {
                for (int z = lowZ; z <= highZ; z++) {
                    perform(v, x, y, z);
                }
            }
        }
        v.owner().storeUndo(this.undo);
        this.undo = null;

        this.block = null;
        this.worldUid = null;
        return;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.set(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.set(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        super.parameters(par, v);
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.set";
    }
}
