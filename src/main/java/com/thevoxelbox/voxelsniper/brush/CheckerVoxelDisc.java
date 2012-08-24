package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * @author MikeMatrix
 * 
 */
public class CheckerVoxelDisc extends PerformBrush {

    private boolean useWorldCoordinates = true;

    /**
     * Default constructor.
     */
    public CheckerVoxelDisc() {
        this.name = "Checker Voxel Disc";
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + this.name + " Parameters:");
            v.sendMessage(ChatColor.AQUA + "true  -- Enables using World Coordinates.");
            v.sendMessage(ChatColor.AQUA + "false -- Disables using World Coordinates.");
            return;
        }
        for (int _x = 1; _x < par.length; _x++) {
            final String _string = par[_x].toLowerCase();
            if (_string.startsWith("true")) {
                this.useWorldCoordinates = true;
                v.sendMessage(ChatColor.AQUA + "Enabled using World Coordinates.");
                continue;
            } else if (_string.startsWith("false")) {
                this.useWorldCoordinates = false;
                v.sendMessage(ChatColor.AQUA + "Disabled using World Coordinates.");
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
                break;
            }
        }
    }

    /**
     * @param v
     * @param target
     */
    private void applyBrush(final vData v, final Block target) {
        for (int _x = v.brushSize; _x >= -v.brushSize; _x--) {
            for (int _y = v.brushSize; _y >= -v.brushSize; _y--) {
                final int _sum = this.useWorldCoordinates ? target.getX() + _x + target.getZ() + _y : _x + _y;
                if (_sum % 2 != 0) {
                    this.current.perform(this.clampY(target.getX() + _x, target.getY(), target.getZ() + _y));
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        final Block _target = this.tb;
        this.applyBrush(v, _target);
    }

    @Override
    protected final void powder(final vData v) {
        this.bx = this.lb.getX();
        this.by = this.lb.getY();
        this.bz = this.lb.getZ();
        final Block _target = this.lb;
        this.applyBrush(v, _target);
    }
}
