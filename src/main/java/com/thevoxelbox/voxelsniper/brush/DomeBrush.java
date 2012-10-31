package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Dome_Brush
 * 
 * @author Gavjenks
 * @author MikeMatrix
 */
public class DomeBrush extends Brush {
    private static int timesUsed = 0;
    private boolean fill;

    /**
     * 
     */
    public DomeBrush() {
        this.setName("Dome");
    }

    @Override
    public final int getTimesUsed() {
        return DomeBrush.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.height();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + this.getName() + " Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b dome fill -- toggles fill mode.");
        } else {
            for (final String _string : par) {
                final String _lowercaseString = _string.toLowerCase();
                if (_lowercaseString.equals("fill")) {
                    this.fill = !this.fill;
                    v.getVoxelMessage().brushMessage("Fill mode " + (this.fill ? "enabled" : "disabled") + ".");
                }
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        DomeBrush.timesUsed = tUsed;
    }

    /**
     * @param v
     * @param targetBlock
     */
    private void generateDome(final SnipeData v, final Block targetBlock, final boolean fill) {
        final Undo _undo = new Undo(targetBlock.getWorld().getName());

        final boolean _negative = (v.getVoxelHeight() < 0);
        final int _absoluteHeight = Math.abs(v.getVoxelHeight());

        for (int _y = 0; _y <= _absoluteHeight; ++_y) {
            final int _currentY = _negative ? targetBlock.getY() - _y : targetBlock.getY() + _y;
            int _ellipseX = v.getBrushSize();
            int _nextEllipseX = 0;
            if (_absoluteHeight != 0) {
                _ellipseX = NumberConversions.round(v.getBrushSize() * Math.sqrt((_absoluteHeight * _absoluteHeight) - (_y * _y)) / _absoluteHeight);
                final int _nextY = _y + 1;
                _nextEllipseX = (fill || (_y + 1 > _absoluteHeight)) ? 0 : NumberConversions.round(v.getBrushSize()
                        * Math.sqrt((_absoluteHeight * _absoluteHeight) - (_nextY * _nextY)) / _absoluteHeight);
            }

            final double _ellipseRadius = Math.sqrt(_ellipseX * _ellipseX);
            final double _nextEllipseRadius = Math.sqrt((_nextEllipseX) * (_nextEllipseX));

            for (int _x = targetBlock.getX() - v.getBrushSize(); _x <= targetBlock.getX() + v.getBrushSize(); ++_x) {
                for (int _z = targetBlock.getZ() - v.getBrushSize(); _z <= targetBlock.getZ() + v.getBrushSize(); ++_z) {
                    final int _distanceSquared = NumberConversions.round(Math.sqrt(((_x - targetBlock.getX()) * (_x - targetBlock.getX()))
                            + ((_z - targetBlock.getZ()) * (_z - targetBlock.getZ()))));

                    if (_distanceSquared <= _ellipseRadius) {
                        if (_nextEllipseRadius == 0 || _distanceSquared >= _nextEllipseRadius) {
                            final Block _currentBlock = this.clampY(_x, _currentY, _z);
                            _undo.put(_currentBlock);
                            _currentBlock.setTypeIdAndData(v.getVoxelId(), v.getData(), true);
                        }
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.generateDome(v, this.getTargetBlock(), this.fill);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.generateDome(v, this.getLastBlock(), this.fill);
    }
}
