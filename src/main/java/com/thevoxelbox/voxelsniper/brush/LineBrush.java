package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Line_Brush
 * 
 * @author Gavjenks
 * @author giltwist
 * @author MikeMatrix
 */
public class LineBrush extends PerformBrush {
    private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);

    private static int timesUsed = 0;

    private Vector originCoords = null;
    private Vector targetCoords = new Vector();

    private World targetWorld;

    /**
	 * 
	 */
    public LineBrush() {
        this.setName("Line");
    }

    @Override
    public final int getTimesUsed() {
        return LineBrush.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD
                    + "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        LineBrush.timesUsed = tUsed;
    }

    private void linePowder(final SnipeData v) {

        final Vector _originClone = this.originCoords.clone().add(LineBrush.HALF_BLOCK_OFFSET);
        final Vector _targetClone = this.targetCoords.clone().add(LineBrush.HALF_BLOCK_OFFSET);

        final Vector _direction = _targetClone.clone().subtract(_originClone);
        final double _length = this.targetCoords.distance(this.originCoords);

        if (_length == 0) {
            this.current.perform(this.targetCoords.toLocation(this.targetWorld).getBlock());
        } else {
            for (final BlockIterator _iterator = new BlockIterator(this.targetWorld, _originClone, _direction, 0, NumberConversions.round(_length)); _iterator
                    .hasNext();) {
                final Block _block = _iterator.next();
                this.current.perform(_block);
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.originCoords = this.getTargetBlock().getLocation().toVector();
        this.targetWorld = this.getTargetBlock().getWorld();
        v.owner().getPlayer().sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.originCoords == null || !this.getTargetBlock().getWorld().equals(this.targetWorld)) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
            return;
        } else {
            this.targetCoords = this.getTargetBlock().getLocation().toVector();
            this.linePowder(v);
        }
    }
}
