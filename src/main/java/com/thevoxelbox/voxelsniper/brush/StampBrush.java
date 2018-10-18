package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

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
     * @param id
     *
     * @return
     */
    protected final boolean falling(final BlockData id)
    {
        switch(id.getMaterial())
        {
            case WATER:
            case LAVA:
            case SAND:
            case GRAVEL:
                return true;
            default:
                return false;
        }
    }

    /**
     * @param id
     *
     * @return
     */
    protected final boolean fallsOff(final BlockData id)
    {
        switch (id.getMaterial())
        {
            case OAK_SAPLING:
            case SPRUCE_SAPLING:
            case BIRCH_SAPLING:
            case ACACIA_SAPLING:
            case JUNGLE_SAPLING:
            case DARK_OAK_SAPLING:
            case DANDELION:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case PINK_TULIP:
            case WHITE_TULIP:
            case ORANGE_TULIP:
            case OXEYE_DAISY:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case TORCH:
            case WALL_TORCH:
            case FIRE:
            case REDSTONE_WIRE:
            case WHEAT:
            case SIGN:
            case WALL_SIGN:
            case OAK_DOOR:
            case IRON_DOOR:
            case BIRCH_DOOR:
            case ACACIA_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            case DARK_OAK_DOOR:
            case LADDER:
            case RAIL:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
            case ACTIVATOR_RAIL:
            case LEVER:
            case OAK_PRESSURE_PLATE:
            case BIRCH_PRESSURE_PLATE:
            case STONE_PRESSURE_PLATE:
            case ACACIA_PRESSURE_PLATE:
            case JUNGLE_PRESSURE_PLATE:
            case SPRUCE_PRESSURE_PLATE:
            case DARK_OAK_PRESSURE_PLATE:
            case HEAVY_WEIGHTED_PRESSURE_PLATE:
            case LIGHT_WEIGHTED_PRESSURE_PLATE:
            case REDSTONE_TORCH:
            case REDSTONE_WALL_TORCH:
            case OAK_BUTTON:
            case BIRCH_BUTTON:
            case STONE_BUTTON:
            case ACACIA_BUTTON:
            case JUNGLE_BUTTON:
            case SPRUCE_BUTTON:
            case DARK_OAK_BUTTON:
            case SUGAR_CANE:
            case REPEATER:
            case COMPARATOR:
                return true;
            default:
                return false;
        }
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
                if (this.fallsOff(block.bd))
                {
                    this.fall.add(block);
                }
                else if (this.falling(block.bd))
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
                if (this.fallsOff(block.bd))
                {
                    this.fall.add(block);
                }
                else if (this.falling(block.bd))
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
                if (this.fallsOff(block.bd))
                {
                    this.fall.add(block);
                }
                else if (this.falling(block.bd))
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
