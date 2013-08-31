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

    private static final Set<Material> exclusiveMaterials = EnumSet.of(
            Material.AIR, Material.STONE, Material.GRASS, Material.DIRT, Material.SAND, Material.GRAVEL, Material.SANDSTONE);
    private static final Set<Material> exclusiveLiquids = EnumSet.of(
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
        final int _brushSize = v.getBrushSize();
        final int _twoBrushSize = 2 * _brushSize;
        World world = this.getTargetBlock().getWorld();
        final Undo _undo = new Undo(world.getName());

        for (int _x = _twoBrushSize; _x >= 0; _x--)
        {
            int currentX = this.getBlockPositionX() - _brushSize + _x;
            for (int _y = 0; _y <= _twoBrushSize; _y++)
            {
                int currentY = this.getBlockPositionY() - _brushSize + _y;
                for (int _z = _twoBrushSize; _z >= 0; _z--)
                {
                    int currentZ = this.getBlockPositionZ() - _brushSize + _z;
                    Block currentBlock = world.getBlockAt(currentX, currentY, currentZ);
                    if (exclusiveMaterials.contains(currentBlock.getType())
                            || (keepWater && exclusiveLiquids.contains(currentBlock.getType())))
                    {
                        continue;
                    }
                    _undo.put(currentBlock);
                    currentBlock.setType(Material.AIR);
                }
            }
        }
        v.storeUndo(_undo);
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
