package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.VTags;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 *
 */
public class StampBrush extends Brush
{
    /**
     * @author Voxel
     */
    protected class BlockWrapper
    {
        public BlockData bd;
        public int x;
        public int y;
        public int z;

        /**
         * @param b
         * @param blx
         * @param bly
         * @param blz
         */
        public BlockWrapper(final Block b, final int blx, final int bly, final int blz)
        {
            this.bd = b.getBlockData();
            this.x = blx;
            this.y = bly;
            this.z = blz;
        }
    }

    /**
     * @author Monofraps
     */
    protected enum StampType
    {
        NO_AIR, FILL, DEFAULT
    }

    protected HashSet<BlockWrapper> clone = new HashSet<BlockWrapper>();
    protected HashSet<BlockWrapper> fall = new HashSet<BlockWrapper>();
    protected HashSet<BlockWrapper> drop = new HashSet<BlockWrapper>();
    protected HashSet<BlockWrapper> solid = new HashSet<BlockWrapper>();
    protected Undo undo;
    protected boolean sorted = false;

    protected StampType stamp = StampType.DEFAULT;

    /**
     *
     */
    public StampBrush()
    {
        this.setName("Stamp");
    }

    /**
     *
     */
    public final void reSort()
    {
        this.sorted = false;
    }

    /**
     * @param cb
     */
    protected final void setBlock(final BlockWrapper cb)
    {
        final Block block = this.clampY(this.getTargetBlock().getX() + cb.x, this.getTargetBlock().getY() + cb.y, this.getTargetBlock().getZ() + cb.z);
        this.undo.put(block);
        block.setBlockData(cb.bd);
    }

    /**
     * @param cb
     */
    protected final void setBlockFill(final BlockWrapper cb)
    {
        final Block block = this.clampY(this.getTargetBlock().getX() + cb.x, this.getTargetBlock().getY() + cb.y, this.getTargetBlock().getZ() + cb.z);
        if (block.getType() == Material.AIR)
        {
            this.undo.put(block);
            block.setBlockData(cb.bd);
        }
    }

    /**
     * @param type
     */
    protected final void setStamp(final StampType type)
    {
        this.stamp = type;
    }

    /**
     * @param v
     */
    protected final void stamp(final SnipeData v)
    {
        this.undo = new Undo();

        if (this.sorted)
        {
            for (final BlockWrapper block : this.solid)
            {
                this.setBlock(block);
            }
            for (final BlockWrapper block : this.drop)
            {
                this.setBlock(block);
            }
            for (final BlockWrapper block : this.fall)
            {
                this.setBlock(block);
            }
        }
        else
        {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (final BlockWrapper block : this.clone)
            {
                if (VTags.POP_OFF.isTagged(block.bd.getMaterial()))
                {
                    this.fall.add(block);
                }
                else if (VTags.FALLING.isTagged(block.bd.getMaterial()))
                {
                    this.drop.add(block);
                }
                else
                {
                    this.solid.add(block);
                    this.setBlock(block);
                }
            }
            for (final BlockWrapper block : this.drop)
            {
                this.setBlock(block);
            }
            for (final BlockWrapper block : this.fall)
            {
                this.setBlock(block);
            }
            this.sorted = true;
        }

        v.owner().storeUndo(this.undo);
    }

    /**
     * @param v
     */
    protected final void stampFill(final SnipeData v)
    {

        this.undo = new Undo();

        if (this.sorted)
        {
            for (final BlockWrapper block : this.solid)
            {
                this.setBlockFill(block);
            }
            for (final BlockWrapper block : this.drop)
            {
                this.setBlockFill(block);
            }
            for (final BlockWrapper block : this.fall)
            {
                this.setBlockFill(block);
            }
        }
        else
        {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (final BlockWrapper block : this.clone)
            {
                if (VTags.POP_OFF.isTagged(block.bd.getMaterial()))
                {
                    this.fall.add(block);
                }
                else if (VTags.FALLING.isTagged(block.bd.getMaterial()))
                {
                    this.drop.add(block);
                }
                else if (block.bd.getMaterial() != Material.AIR)
                {
                    this.solid.add(block);
                    this.setBlockFill(block);
                }
            }
            for (final BlockWrapper block : this.drop)
            {
                this.setBlockFill(block);
            }
            for (final BlockWrapper block : this.fall)
            {
                this.setBlockFill(block);
            }
            this.sorted = true;
        }

        v.owner().storeUndo(this.undo);
    }

    /**
     * @param v
     */
    protected final void stampNoAir(final SnipeData v)
    {

        this.undo = new Undo();

        if (this.sorted)
        {
            for (final BlockWrapper block : this.solid)
            {
                this.setBlock(block);
            }
            for (final BlockWrapper block : this.drop)
            {
                this.setBlock(block);
            }
            for (final BlockWrapper block : this.fall)
            {
                this.setBlock(block);
            }
        }
        else
        {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (final BlockWrapper block : this.clone)
            {
                if (VTags.POP_OFF.isTagged(block.bd.getMaterial()))
                {
                    this.fall.add(block);
                }
                else if (VTags.FALLING.isTagged(block.bd.getMaterial()))
                {
                    this.drop.add(block);
                }
                else if (block.bd.getMaterial() != Material.AIR)
                {
                    this.solid.add(block);
                    this.setBlock(block);
                }
            }
            for (final BlockWrapper block : this.drop)
            {
                this.setBlock(block);
            }
            for (final BlockWrapper block : this.fall)
            {
                this.setBlock(block);
            }
            this.sorted = true;
        }

        v.owner().storeUndo(this.undo);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        switch (this.stamp)
        {
            case DEFAULT:
                this.stamp(v);
                break;

            case NO_AIR:
                this.stampNoAir(v);
                break;

            case FILL:
                this.stampFill(v);
                break;

            default:
                v.sendMessage(ChatColor.DARK_RED + "Error while stamping! Report");
                break;
        }
    }

    @Override
    protected void powder(final SnipeData v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void info(final Message vm)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.stamp";
    }
}
