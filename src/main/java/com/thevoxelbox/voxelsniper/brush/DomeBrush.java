package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

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
    public final void setTimesUsed(final int tUsed) {
        DomeBrush.timesUsed = tUsed;
    }

    /**
     * @param v
     * @param targetBlock
     */
    private void generateDome(final SnipeData v, final Block targetBlock, final boolean fill) {

        if (v.getVoxelHeight() == 0) {
            v.sendMessage("VoxelHeight must not be 0.");
            return;
        }

        final int _absoluteHeight = Math.abs(v.getVoxelHeight());
        final boolean _negative = v.getVoxelHeight() < 0;

        final Set<Vector> _changeablePositions = new HashSet<Vector>();

        final Undo _undo = new Undo(targetBlock.getWorld().getName());

        final int _brushSizeTimesVoxelHeight = v.getBrushSize() * _absoluteHeight;
        final double _stepScale = ((v.getBrushSize() * v.getBrushSize()) + _brushSizeTimesVoxelHeight + _brushSizeTimesVoxelHeight) / 3;

        final double _stepSize = 1 / _stepScale;

        for (double _u = 0; _u <= Math.PI / 2; _u += _stepSize) {
            final double _y = _absoluteHeight * Math.sin(_u);
            for (double _v = -Math.PI; _v <= Math.PI; _v += _stepSize) {
                final double _x = v.getBrushSize() * Math.cos(_u) * Math.cos(_v);
                final double _z = v.getBrushSize() * Math.cos(_u) * Math.sin(_v);

                final int _targetX = NumberConversions.floor(targetBlock.getX() + _x);
                final int _targetY = NumberConversions.floor(targetBlock.getY() + (_negative ? -_y : _y));
                final int _targetZ = NumberConversions.floor(targetBlock.getZ() + _z);

                _changeablePositions.add(new Vector(_targetX, _targetY, _targetZ));
            }
        }

        for (final Vector _vector : _changeablePositions) {
            final Block _targetBlock = _vector.toLocation(this.getTargetBlock().getWorld()).getBlock();
            if (_targetBlock.getTypeId() != v.getVoxelId() || _targetBlock.getData() != v.getData()) {
                _undo.put(_targetBlock);
                _targetBlock.setTypeIdAndData(v.getVoxelId(), v.getData(), true);
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
