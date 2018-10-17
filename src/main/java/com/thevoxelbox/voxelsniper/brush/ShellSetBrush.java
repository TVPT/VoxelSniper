package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Shell_Brushes
 *
 * @author Piotr
 */
public class ShellSetBrush extends Brush
{
    private static final int MAX_SIZE = 5000000;
    private Block block = null;

    /**
     *
     */
    public ShellSetBrush()
    {
        this.setName("Shell Set");
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

            final int lowX = (this.block.getX() <= bl.getX()) ? this.block.getX() : bl.getX();
            final int lowY = (this.block.getY() <= bl.getY()) ? this.block.getY() : bl.getY();
            final int lowZ = (this.block.getZ() <= bl.getZ()) ? this.block.getZ() : bl.getZ();
            final int highX = (this.block.getX() >= bl.getX()) ? this.block.getX() : bl.getX();
            final int highY = (this.block.getY() >= bl.getY()) ? this.block.getY() : bl.getY();
            final int highZ = (this.block.getZ() >= bl.getZ()) ? this.block.getZ() : bl.getZ();

            if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > MAX_SIZE)
            {
                v.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
            }
            else
            {
                final ArrayList<Block> blocks = new ArrayList<Block>(((Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY)) / 2));
                for (int y = lowY; y <= highY; y++)
                {
                    for (int x = lowX; x <= highX; x++)
                    {
                        for (int z = lowZ; z <= highZ; z++)
                        {
                            if (this.getWorld().getBlockAt(x, y, z).getType() == v.getReplaceData().getMaterial())
                            {
                                continue;
                            }
                            else if (this.getWorld().getBlockAt(x + 1, y, z).getType() == v.getReplaceData().getMaterial())
                            {
                                continue;
                            }
                            else if (this.getWorld().getBlockAt(x - 1, y, z).getType() == v.getReplaceData().getMaterial())
                            {
                                continue;
                            }
                            else if (this.getWorld().getBlockAt(x, y, z + 1).getType() == v.getReplaceData().getMaterial())
                            {
                                continue;
                            }
                            else if (this.getWorld().getBlockAt(x, y, z - 1).getType() == v.getReplaceData().getMaterial())
                            {
                                continue;
                            }
                            else if (this.getWorld().getBlockAt(x, y + 1, z).getType() == v.getReplaceData().getMaterial())
                            {
                                continue;
                            }
                            else if (this.getWorld().getBlockAt(x, y - 1, z).getType() == v.getReplaceData().getMaterial())
                            {
                                continue;
                            }
                            else
                            {
                                blocks.add(this.getWorld().getBlockAt(x, y, z));
                            }
                        }
                    }
                }

                final Undo undo = new Undo();
                for (final Block currentBlock : blocks)
                {
                    if (currentBlock.getType() != v.getVoxelData().getMaterial())
                    {
                        undo.put(currentBlock);
                        currentBlock.setType(v.getVoxelData().getMaterial());
                    }
                }
                v.owner().storeUndo(undo);
                v.sendMessage(ChatColor.AQUA + "Shell complete.");
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
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        if (this.set(this.getLastBlock(), v))
        {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.replace();
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.shellset";
    }
}
