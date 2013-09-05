package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Disc_Brush
 *
 * @author Voxel
 */
public class DiscBrush extends PerformBrush
{
    private static int timesUsed = 0;
    private double trueCircle = 0;

    /**
     * Default Constructor.
     */
    public DiscBrush()
    {
        this.setName("Disc");
    }

    /**
     * Disc executor.
     *
     * @param v
     */
    private void disc(final SnipeData v, final Block targetBlock)
    {
        final double _radiusSquared = (v.getBrushSize() + this.trueCircle) * (v.getBrushSize() + this.trueCircle);
        final Vector _centerPoint = targetBlock.getLocation().toVector();
        final Vector _currentPoint = _centerPoint.clone();

        for (int _x = -v.getBrushSize(); _x <= v.getBrushSize(); _x++)
        {
            _currentPoint.setX(_centerPoint.getX() + _x);
            for (int _z = -v.getBrushSize(); _z <= v.getBrushSize(); _z++)
            {
                _currentPoint.setZ(_centerPoint.getZ() + _z);
                if (_centerPoint.distanceSquared(_currentPoint) <= _radiusSquared)
                {
                    this.current.perform(this.clampY(_currentPoint.getBlockX(), _currentPoint.getBlockY(), _currentPoint.getBlockZ()));
                }
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
    public final int getTimesUsed()
    {
        return DiscBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        DiscBrush.timesUsed = tUsed;
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
        // voxelMessage.voxel();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int _i = 1; _i < par.length; _i++)
        {
            final String _param = par[_i].toLowerCase();

            if (_param.equalsIgnoreCase("info"))
            {
                v.sendMessage(ChatColor.GOLD + "Disc Brush Parameters:");
                v.sendMessage(ChatColor.AQUA + "/b d true|false" + " -- toggles useing the true circle algorithm instead of the skinnier version with classic sniper nubs. (false is default)");
                return;
            }
            else if (_param.startsWith("true"))
            {
                this.trueCircle = 0.5;
                v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            }
            else if (_param.startsWith("false"))
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
}
