package com.thevoxelbox.voxelsniper.util;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author MikeMatrix
 */
public class BlockWrapper
{

    private BlockData data;
    private int x;
    private int y;
    private int z;
    private World world;

    /**
     * @param block
     */
    public BlockWrapper(final Block block)
    {
        this.setBlockData(block.getBlockData());
        this.setX(block.getX());
        this.setY(block.getY());
        this.setZ(block.getZ());
        this.setWorld(block.getWorld());
    }

    public final BlockData getBlockData()
    {
        return this.data;
    }

    /**
     * @return the world
     */
    public final World getWorld()
    {
        return this.world;
    }

    /**
     * @return the x
     */
    public final int getX()
    {
        return this.x;
    }

    /**
     * @return the y
     */
    public final int getY()
    {
        return this.y;
    }

    /**
     * @return the z
     */
    public final int getZ()
    {
        return this.z;
    }

    public final void setBlockData(final BlockData data)
    {
        this.data = data;
    }

    /**
     * @param world
     *         the world to set
     */
    public final void setWorld(final World world)
    {
        this.world = world;
    }

    /**
     * @param x
     *         the x to set
     */
    public final void setX(final int x)
    {
        this.x = x;
    }

    /**
     * @param y
     *         the y to set
     */
    public final void setY(final int y)
    {
        this.y = y;
    }

    /**
     * @param z
     *         the z to set
     */
    public final void setZ(final int z)
    {
        this.z = z;
    }
}
