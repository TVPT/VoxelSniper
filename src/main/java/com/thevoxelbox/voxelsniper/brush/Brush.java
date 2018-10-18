package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeAction;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;

/**
 * Abstract implementation of the {@link IBrush} interface.
 */
public abstract class Brush implements IBrush
{
    protected static final int CHUNK_SIZE = 16;
    /**
     * Targeted Block.
     */
    private Block targetBlock;
    /**
     * Last Block before targeted Block.
     */
    private Block lastBlock;
    /**
     * Brush name.
     */
    private String name = "Undefined";

    /**
     * @param x
     * @param y
     * @param z
     * @return {@link Block}
     */
    public final Block clampY(final int x, final int y, final int z)
    {
        int clampedY = y;
        if (clampedY < 0)
        {
            clampedY = 0;
        }
        else if (clampedY > this.getWorld().getMaxHeight())
        {
            clampedY = this.getWorld().getMaxHeight();
        }

        return this.getWorld().getBlockAt(x, clampedY, z);
    }

    private boolean preparePerform(final SnipeData v, final Block clickedBlock, final BlockFace clickedFace)
    {
        if (this.getTarget(v, clickedBlock, clickedFace))
        {
            if (this instanceof PerformBrush)
            {
                ((PerformBrush) this).initP(v);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean perform(SnipeAction action, SnipeData data, Block targetBlock, Block lastBlock)
    {
        this.setTargetBlock(targetBlock);
        this.setLastBlock(lastBlock);
        switch (action)
        {
            case ARROW:
                this.arrow(data);
                return true;
            case GUNPOWDER:
                this.powder(data);
                return true;
            default:
                return false;
        }
    }

    /**
     * The arrow action. Executed when a player RightClicks with an Arrow
     *
     * @param v Sniper caller
     */
    protected void arrow(final SnipeData v)
    {
    }

    /**
     * The powder action. Executed when a player RightClicks with Gunpowder
     *
     * @param v Sniper caller
     */
    protected void powder(final SnipeData v)
    {
    }

    @Override
    public abstract void info(Message vm);

    @Override
    public void parameters(final String[] par, final SnipeData v)
    {
        v.sendMessage(ChatColor.RED + "This brush does not accept additional parameters.");
    }

    /**
     * Overridable getTarget method.
     *
     * @param v
     * @param clickedBlock
     * @param clickedFace
     * @return boolean
     */
    protected final boolean getTarget(final SnipeData v, final Block clickedBlock, final BlockFace clickedFace)
    {
        if (clickedBlock != null)
        {
            this.setTargetBlock(clickedBlock);
            this.setLastBlock(clickedBlock.getRelative(clickedFace));
            if (this.getLastBlock() == null)
            {
                v.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
                return false;
            }
            if (v.owner().getSnipeData(v.owner().getCurrentToolId()).isLightningEnabled())
            {
                this.getWorld().strikeLightning(this.getTargetBlock().getLocation());
            }
            return true;
        }
        else
        {
            RangeBlockHelper rangeBlockHelper;
            if (v.owner().getSnipeData(v.owner().getCurrentToolId()).isRanged())
            {
                rangeBlockHelper = new RangeBlockHelper(v.owner().getPlayer(), v.owner().getPlayer().getWorld(), (double) v.owner().getSnipeData(v.owner().getCurrentToolId()).getRange());
                this.setTargetBlock(rangeBlockHelper.getRangeBlock());
            }
            else
            {
                rangeBlockHelper = new RangeBlockHelper(v.owner().getPlayer(), v.owner().getPlayer().getWorld());
                this.setTargetBlock(rangeBlockHelper.getTargetBlock());
            }
            if (this.getTargetBlock() != null)
            {
                this.setLastBlock(rangeBlockHelper.getLastBlock());
                if (this.getLastBlock() == null)
                {
                    v.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
                    return false;
                }
                if (v.owner().getSnipeData(v.owner().getCurrentToolId()).isLightningEnabled())
                {
                    this.getWorld().strikeLightning(this.getTargetBlock().getLocation());
                }
                return true;
            }
            else
            {
                v.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
                return false;
            }
        }
    }

    @Override
    public final String getName()
    {
        return this.name;
    }

    @Override
    public final void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public String getBrushCategory()
    {
        return "General";
    }

    /**
     * @return the targetBlock
     */
    protected final Block getTargetBlock()
    {
        return this.targetBlock;
    }

    /**
     * @param targetBlock the targetBlock to set
     */
    protected final void setTargetBlock(final Block targetBlock)
    {
        this.targetBlock = targetBlock;
    }

    /**
     * @return the world
     */
    protected final World getWorld()
    {
        return targetBlock.getWorld();
    }

    /**
     * Looks up Type ID of Block at given coordinates in the world of the targeted Block.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Type ID of Block at given coordinates in the world of the targeted Block.
     */
    protected Material getBlockTypeAt(int x, int y, int z)
    {
        return getWorld().getBlockAt(x, y, z).getType();
    }

    /**
     * Looks up Block Data Value of Block at given coordinates in the world of the targeted Block.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Block Data Value of Block at given coordinates in the world of the targeted Block.
     */
    protected BlockData getBlockDataAt(int x, int y, int z)
    {
        return this.getWorld().getBlockAt(x, y, z).getBlockData();
    }

    /**
     * @return Block before target Block.
     */
    protected final Block getLastBlock()
    {
        return this.lastBlock;
    }

    /**
     * @param lastBlock Last Block before target Block.
     */
    protected final void setLastBlock(Block lastBlock)
    {
        this.lastBlock = lastBlock;
    }

    /**
     * Set block data with supplied data over BlockWrapper.
     *
     * @param blockWrapper Block data wrapper
     */
    @Deprecated
    protected final void setBlock(BlockWrapper blockWrapper)
    {
        this.getWorld().getBlockAt(blockWrapper.getX(), blockWrapper.getY(), blockWrapper.getZ()).setBlockData(blockWrapper.getBlockData());
    }

    /**
     * Sets the Id of the block at the passed coordinate.
     *
     * @param z  Z coordinate
     * @param x  X coordinate
     * @param y  Y coordinate
     * @param type The type the block will be set to
     */
    protected final void setBlockTypeAt(int z, int x, int y, Material type)
    {
        this.getWorld().getBlockAt(x, y, z).setType(type);
    }

    /**
     * Sets the id and data value of the block at the passed coordinate.
     *
     * @param x    X coordinate
     * @param y    Y coordinate
     * @param z    Z coordinate
     * @param blockData The block data the block will be set to
     */
    protected final void setBlockDataAt(int x, int y, int z, BlockData blockData)
    {
        this.getWorld().getBlockAt(x, y, z).setBlockData(blockData, true);
    }
}
