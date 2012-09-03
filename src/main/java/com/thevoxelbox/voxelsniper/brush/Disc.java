package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class Disc extends PerformBrush {
    private double trueCircle = 0;

    private static int timesUsed = 0;

    /**
     * Default Constructor.
     */
    public Disc() {
        this.setName("Disc");
    }

    /**
     * Disc executor.
     * 
     * @param v
     */
    public final void disc(final SnipeData v, final Block targetBlock) {
        final double _radiusSquared = (v.getBrushSize() + this.trueCircle) * (v.getBrushSize() + this.trueCircle);
        final Vector _centerPoint = targetBlock.getLocation().toVector();
        final Vector _currentPoint = _centerPoint.clone();

        for (int _x = -v.getBrushSize(); _x <= v.getBrushSize(); _x++) {
            _currentPoint.setX(_centerPoint.getX() + _x);
            for (int _z = -v.getBrushSize(); _z <= v.getBrushSize(); _z++) {
                _currentPoint.setZ(_centerPoint.getZ() + _z);
                if (_centerPoint.distanceSquared(_currentPoint) <= _radiusSquared) {
                    this.current.perform(this.clampY(_currentPoint.getBlockX(), _currentPoint.getBlockY(), _currentPoint.getBlockZ()));
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final int getTimesUsed() {
        return Disc.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        // voxelMessage.voxel();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Disc Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b d true|false"
                    + " -- toggles useing the true circle algorithm instead of the skinnier version with classic sniper nubs. (false is default)");
            return;
        }
        for (int _x = 1; _x < par.length; _x++) {
            final String _string = par[_x].toLowerCase();
            if (_string.startsWith("true")) {
                this.trueCircle = 0.5;
                v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            } else if (_string.startsWith("false")) {
                this.trueCircle = 0;
                v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Disc.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.disc(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.disc(v, this.getLastBlock());
    }
}
