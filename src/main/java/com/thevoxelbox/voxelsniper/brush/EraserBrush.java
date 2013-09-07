package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import java.util.EnumSet;
import java.util.Set;

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

    private static final Set<Material> EXCLUSIVE_MATERIALS = EnumSet.of(
            Material.AIR, Material.STONE, Material.GRASS, Material.DIRT, Material.SAND, Material.GRAVEL, Material.SANDSTONE);
    private static final Set<Material> EXCLUSIVE_LIQUIDS = EnumSet.of(
            Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA);
    private static int timesUsed = 0;

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
        final Undo undo = new Undo(world.getName());

        for (int x = brushSizeDoubled; x >= 0; x--)
        {
            int currentX = this.getBlockPositionX() - brushSize + x;
            for (int y = 0; y <= brushSizeDoubled; y++)
            {
                int currentY = this.getBlockPositionY() - brushSize + y;
                for (int z = brushSizeDoubled; z >= 0; z--)
                {
                    int currentZ = this.getBlockPositionZ() - brushSize + z;
                    Block currentBlock = world.getBlockAt(currentX, currentY, currentZ);
                    if (EXCLUSIVE_MATERIALS.contains(currentBlock.getType())
                            || (keepWater && EXCLUSIVE_LIQUIDS.contains(currentBlock.getType())))
                    {
                        continue;
                    }
                    undo.put(currentBlock);
                    currentBlock.setType(Material.AIR);
                }
            }
        }
        v.storeUndo(undo);
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
    public final int getTimesUsed()
    {
        return EraserBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        EraserBrush.timesUsed = tUsed;
    }
}
