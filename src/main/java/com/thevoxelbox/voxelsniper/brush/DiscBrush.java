package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.flowpowered.math.GenericMath;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A 1-block tall cylinder.
 */
public class DiscBrush extends PerformBrush {

    public DiscBrush() {
        this.setName("Disc");
    }

    private void disc(final SnipeData v, final Location<World> targetBlock) {
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
                    perform(v, x, targetBlock.getBlockY(), z);
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.disc(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.disc(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.disc";
    }
}
