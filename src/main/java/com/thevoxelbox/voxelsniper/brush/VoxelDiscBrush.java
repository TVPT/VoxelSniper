package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Voxel_Disc_Brush
 *
 * @author Voxel
 */
public class VoxelDiscBrush extends PerformBrush
{
    private static int timesUsed = 0;

    /**
     *
     */
    public VoxelDiscBrush()
    {
        this.setName("Voxel Disc");
    }

    private void disc(final SnipeData v, Block targetBlock)
    {
        final int _bSize = v.getBrushSize();

        for (int _x = _bSize; _x >= -_bSize; _x--)
        {
            for (int _z = _bSize; _z >= -_bSize; _z--)
            {
                current.perform(targetBlock.getRelative(_x, 0, _z));
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.disc(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.disc(v, this.getLastBlock());
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
        // voxelMessage.voxel();
    }

    @Override
    public final int getTimesUsed()
    {
        return VoxelDiscBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        VoxelDiscBrush.timesUsed = tUsed;
    }
}
