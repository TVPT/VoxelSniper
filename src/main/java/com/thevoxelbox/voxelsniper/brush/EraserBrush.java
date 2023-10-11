package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.VTags;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Eraser_Brush
 *
 * @author Voxel
 */
public class EraserBrush extends Brush
{

    /**
     *
     */
    public EraserBrush()
    {
        this.setName("Eraser");
    }

    private void doErase(final SnipeData v, final boolean keepWater)
    {
        final int brushSize = v.getBrushSize();
        final int brushSizeDoubled = 2 * brushSize;
        World world = this.getTargetBlock().getWorld();
        final Undo undo = new Undo();

        for (int x = brushSizeDoubled; x >= 0; x--)
        {
            int currentX = this.getTargetBlock().getX() - brushSize + x;
            for (int y = 0; y <= brushSizeDoubled; y++)
            {
                int currentY = this.getTargetBlock().getY() - brushSize + y;
                for (int z = brushSizeDoubled; z >= 0; z--)
                {
                    int currentZ = this.getTargetBlock().getZ() - brushSize + z;
                    Block currentBlock = world.getBlockAt(currentX, currentY, currentZ);
                    if (VTags.ERASER_EXCLUSIVE.isTagged(currentBlock.getType()) || (keepWater && VTags.LIQUID.isTagged(currentBlock.getType())))
                    {
                        continue;
                    }
                    undo.put(currentBlock);
                    currentBlock.setType(Material.AIR);
                }
            }
        }
        v.owner().storeUndo(undo);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.doErase(v, false);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.doErase(v, true);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.eraser";
    }
}
