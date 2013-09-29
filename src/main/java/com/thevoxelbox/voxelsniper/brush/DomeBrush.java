package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;
import java.util.Set;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Dome_Brush
 *
 * @author Gavjenks
 * @author MikeMatrix
 */
public class DomeBrush extends Brush
{
    private static int timesUsed = 0;

    /**
     *
     */
    public DomeBrush()
    {
        this.setName("Dome");
    }

    @Override
    public final int getTimesUsed()
    {
        return DomeBrush.timesUsed;
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.height();
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        DomeBrush.timesUsed = tUsed;
    }

    /**
     * @param v
     * @param targetBlock
     */
    @SuppressWarnings("deprecation")
	private void generateDome(final SnipeData v, final Block targetBlock)
    {

        if (v.getVoxelHeight() == 0)
        {
            v.sendMessage("VoxelHeight must not be 0.");
            return;
        }

        final int absoluteHeight = Math.abs(v.getVoxelHeight());
        final boolean negative = v.getVoxelHeight() < 0;

        final Set<Vector> changeablePositions = new HashSet<Vector>();

        final Undo undo = new Undo(targetBlock.getWorld().getName());

        final int brushSizeTimesVoxelHeight = v.getBrushSize() * absoluteHeight;
        final double stepScale = ((v.getBrushSize() * v.getBrushSize()) + brushSizeTimesVoxelHeight + brushSizeTimesVoxelHeight) / 3;

        final double stepSize = 1 / stepScale;

        for (double u = 0; u <= Math.PI / 2; u += stepSize)
        {
            final double y = absoluteHeight * Math.sin(u);
            for (double stepV = -Math.PI; stepV <= -(Math.PI / 2); stepV += stepSize)
            {
                final double x = v.getBrushSize() * Math.cos(u) * Math.cos(stepV);
                final double z = v.getBrushSize() * Math.cos(u) * Math.sin(stepV);

                final double targetBlockX = targetBlock.getX() + 0.5;
                final double targetBlockZ = targetBlock.getZ() + 0.5;
                final int targetY = NumberConversions.floor(targetBlock.getY() + (negative ? -y : y));
                final int currentBlockXAdd = NumberConversions.floor(targetBlockX + x);
                final int currentBlockZAdd = NumberConversions.floor(targetBlockZ + z);
                final int currentBlockXSubtract = NumberConversions.floor(targetBlockX - x);
                final int currentBlockZSubtract = NumberConversions.floor(targetBlockZ - z);
                changeablePositions.add(new Vector(currentBlockXAdd, targetY, currentBlockZAdd));
                changeablePositions.add(new Vector(currentBlockXSubtract, targetY, currentBlockZAdd));
                changeablePositions.add(new Vector(currentBlockXAdd, targetY, currentBlockZSubtract));
                changeablePositions.add(new Vector(currentBlockXSubtract, targetY, currentBlockZSubtract));

            }
        }

        for (final Vector vector : changeablePositions)
        {
            final Block currentTargetBlock = vector.toLocation(this.getTargetBlock().getWorld()).getBlock();
            if (currentTargetBlock.getTypeId() != v.getVoxelId() || currentTargetBlock.getData() != v.getData())
            {
                undo.put(currentTargetBlock);
                currentTargetBlock.setTypeIdAndData(v.getVoxelId(), v.getData(), true);
            }
        }

        v.storeUndo(undo);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.generateDome(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.generateDome(v, this.getLastBlock());
    }
}
