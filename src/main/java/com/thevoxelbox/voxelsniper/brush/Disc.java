package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class Disc extends PerformBrush {

    private double trueCircle = 0;

    /**
     * Default Constructor.
     */
    public Disc() {
        this.name = "Disc";
    }

    /**
     * Disc executor.
     * 
     * @param v
     */
    public final void disc(final vData v) {
        final double _radiusSquared = (v.brushSize + this.trueCircle) * (v.brushSize + this.trueCircle);
        final Vector _centerPoint = this.tb.getLocation().toVector();
        final Vector _currentPoint = _centerPoint.clone();

        for (int _x = -v.brushSize; _x <= v.brushSize; _x++) {
            _currentPoint.setX(_centerPoint.getX() + _x);
            for (int _z = -v.brushSize; _z <= v.brushSize; _z++) {
                _currentPoint.setZ(_centerPoint.getZ() + _z);
                if (_centerPoint.distanceSquared(_currentPoint) <= _radiusSquared) {
                    this.current.perform(this.clampY(_currentPoint.getBlockX(), _currentPoint.getBlockY(), _currentPoint.getBlockZ()));
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
        // vm.voxel();
    }

    @Override
    public final void parameters(final String[] par, final vData v) {
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
    protected final void arrow(final vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.disc(v);
    }

    @Override
    protected final void powder(final vData v) {
        this.bx = this.lb.getX();
        this.by = this.lb.getY();
        this.bz = this.lb.getZ();
        this.disc(v);
    }
}
