package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class FillDownBrush extends PerformBrush
{
    private static int timesUsed = 0;
    private double trueCircle = 0;

    /**
     *
     */
    public FillDownBrush()
    {
        this.setName("Fill Down");
    }

    private void fillDown(final SnipeData v, final Block b)
    {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);

        for (int currentX = 0 - brushSize; currentX <= brushSize; currentX++)
        {
            final double currentXSquared = Math.pow(currentX, 2);

            for (int currentZ = 0 - brushSize; currentZ <= brushSize; currentZ++)
            {
                if (currentXSquared + Math.pow(currentZ, 2) <= brushSizeSquared)
                {
                    for (int currentY = this.getBlockPositionY(); currentY >= 0; --currentY)
                    {
                        final Block currentBlock = this.clampY(this.getBlockPositionX() + currentX, currentY, this.getBlockPositionZ() + currentZ);
                        if (currentBlock.getType().equals(Material.AIR))
                        {
                            this.current.perform(currentBlock);
                        }
                        else
                        {
                            break;
                        }
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.fillDown(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.fillDown(v, this.getLastBlock());
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int _x = 1; _x < par.length; _x++)
        {
            if (par[_x].equalsIgnoreCase("info"))
            {
                v.sendMessage(ChatColor.GOLD + "Fill Down Parameters:");
                v.sendMessage(ChatColor.AQUA + "/b fd true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b fd false will switch back. (false is default)");
                return;
            }
            else if (par[_x].startsWith("true"))
            {
                this.trueCircle = 0.5;
                v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            }
            else if (par[_x].startsWith("false"))
            {
                this.trueCircle = 0;
                v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                continue;
            }
            else
            {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return FillDownBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        FillDownBrush.timesUsed = tUsed;
    }
}
