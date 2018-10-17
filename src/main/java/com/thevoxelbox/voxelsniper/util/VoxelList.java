package com.thevoxelbox.voxelsniper.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.data.BlockData;

import com.google.common.collect.ImmutableList;

/**
 * Container class for multiple ID/Datavalue pairs.
 */
public class VoxelList
{

    private List<BlockData> values = new ArrayList<BlockData>();

    /**
     * Adds the specified block data the VoxelList.
     * 
     * @param i
     */
    public void add(BlockData i)
    {
		if (!values.contains(i))
		{
			values.add(i);
		}
    }

    /**
     * Removes the specified block data from the VoxelList.
     * 
     * @param i
     * @return true if this list contained the specified element
     */
    public boolean removeValue(final BlockData i)
    {
        if (values.isEmpty())
        {
            return false;
        }
        else
        {
            return values.remove(i);
        }
    }

    /**
     * @param i
     * @return true if this list contains the specified element
     */
    public boolean contains(final BlockData i)
    {
        for (BlockData in : values)
        {
        	if(i.matches(in)) {
        		return true;
			}
        }
        return false;
    }

    /**
     * Clears the VoxelList.
     */
    public void clear()
    {
        values.clear();
    }

    /**
     * Returns true if this list contains no elements.
     *
     * @return true if this list contains no elements
     */
    public boolean isEmpty()
    {
        return values.isEmpty();
    }

    /**
     * Returns a defensive copy of the List with pairs.
     *
     * @return defensive copy of the List with pairs
     */
    public List<BlockData> getList()
    {
        return ImmutableList.copyOf(values);
    }


}
