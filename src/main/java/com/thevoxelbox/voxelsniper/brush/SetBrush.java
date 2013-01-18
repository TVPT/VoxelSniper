package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Set_Brush
 *
 * @author Voxel
 */
public class SetBrush extends PerformBrush
{
    private static final int SELECTION_SIZE_MAX = 5000000;

    private static int timesUsed = 0;
    private Block block = null;

    /**
     *
     */
    public SetBrush()
    {
        this.setName("Set");
    }

    private boolean set(final Block bl, final SnipeData v)
    {
        if (this.block == null)
        {
            this.block = bl;
            return true;
        }
        else
        {
            if (!this.block.getWorld().getName().equals(bl.getWorld().getName()))
            {
                v.sendMessage(ChatColor.RED + "You selected points in different worlds!");
                this.block = null;
                return true;
            }
            final int _lowx = (this.block.getX() <= bl.getX()) ? this.block.getX() : bl.getX();
            final int _lowy = (this.block.getY() <= bl.getY()) ? this.block.getY() : bl.getY();
            final int _lowz = (this.block.getZ() <= bl.getZ()) ? this.block.getZ() : bl.getZ();
            final int _highx = (this.block.getX() >= bl.getX()) ? this.block.getX() : bl.getX();
            final int _highy = (this.block.getY() >= bl.getY()) ? this.block.getY() : bl.getY();
            final int _highz = (this.block.getZ() >= bl.getZ()) ? this.block.getZ() : bl.getZ();

            if (Math.abs(_highx - _lowx) * Math.abs(_highz - _lowz) * Math.abs(_highy - _lowy) > SELECTION_SIZE_MAX)
            {
                v.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
            }
            else
            {
                for (int _y = _lowy; _y <= _highy; _y++)
                {
                    for (int _x = _lowx; _x <= _highx; _x++)
                    {
                        for (int _z = _lowz; _z <= _highz; _z++)
                        {
                            this.current.perform(this.clampY(_x, _y, _z));
                        }
                    }
                }
            }

            this.block = null;
            return false;
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        if (this.set(this.getTargetBlock(), v))
        {
            v.sendMessage(ChatColor.GRAY + "Point one");
        }
        else
        {
            v.storeUndo(this.current.getUndo());
        }
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        if (this.set(this.getLastBlock(), v))
        {
            v.sendMessage(ChatColor.GRAY + "Point one");
        }
        else
        {
            v.storeUndo(this.current.getUndo());
        }
    }

    @Override
    public final void info(final Message vm)
    {
        this.block = null;
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        super.parameters(par, v);
    }

    @Override
    public final int getTimesUsed()
    {
        return SetBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        SetBrush.timesUsed = tUsed;
    }
}
