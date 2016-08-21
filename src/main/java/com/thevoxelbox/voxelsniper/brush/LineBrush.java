package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.spongepowered.api.text.format.TextColors;

/**
 * Creates a line
 */
public class LineBrush extends PerformBrush {
//    private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);
//    private Vector originCoords = null;
//    private Vector targetCoords = new Vector();
//    private World targetWorld;

    /**
     *
     */
    public LineBrush() {
        this.setName("Line");
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length == 0 || par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD,
                    "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
        }
    }
    // @Spongify

    private void linePowder(final SnipeData v) {
//        final Vector originClone = this.originCoords.clone().add(LineBrush.HALF_BLOCK_OFFSET);
//        final Vector targetClone = this.targetCoords.clone().add(LineBrush.HALF_BLOCK_OFFSET);
//
//        final Vector direction = targetClone.clone().subtract(originClone);
//        final double length = this.targetCoords.distance(this.originCoords);
//
//        if (length == 0) {
//            this.current.perform(this.targetCoords.toLocation(this.targetWorld).getBlock());
//        } else {
//            for (final BlockIterator blockIterator =
//                    new BlockIterator(this.targetWorld, originClone, direction, 0, NumberConversions.round(length)); blockIterator.hasNext();) {
//                final Block currentBlock = blockIterator.next();
//                this.current.perform(currentBlock);
//            }
//        }
//
//        v.owner().storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
//        this.originCoords = this.getTargetBlock().getLocation().toVector();
//        this.targetWorld = this.getTargetBlock().getWorld();
//        v.owner().getPlayer().sendMessage(TextColors.DARK_PURPLE + "First point selected.");
    }

    @Override
    protected final void powder(final SnipeData v) {
//        if (this.originCoords == null || !this.getTargetBlock().getWorld().equals(this.targetWorld)) {
//            v.owner().getPlayer().sendMessage(TextColors.RED + "Warning: You did not select a first coordinate with the arrow");
//        } else {
//            this.targetCoords = this.getTargetBlock().getLocation().toVector();
//            this.linePowder(v);
//        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.line";
    }
}
