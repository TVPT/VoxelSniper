package com.thevoxelbox.voxelsniper.util;

import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class for multiple ID/Datavalue pairs.
 */
public class VoxelList
{

    private List<BlockData> blocks = new ArrayList<BlockData>();
    private List<Tag<Material>> tags = new ArrayList<Tag<Material>>();

    /**
     * Adds the specified block data the VoxelList.
     * 
     * @param i
     */
    public void addBlock(BlockData i)
    {
        if (!blocks.contains(i))
        {
            blocks.add(i);
        }
    }

    /**
     * Adds the specified tag to the VoxelList.
     */
    public void addTag(Tag<Material> tag)
    {
        if (!tags.contains(tag))
        {
            tags.add(tag);
        }
    }

    /**
     * Removes the specified block data from the VoxelList.
     *
     * @param i
     * @return true if this list contained the specified element
     */
    public boolean removeBlock(final BlockData i)
    {
        if (blocks.isEmpty())
        {
            return false;
        }
        else
        {
            return blocks.remove(i);
        }
    }

    /**
     * Removes the specified tag from the VoxelList.
     *
     * @return true if this list contained the specified element
     */
    public boolean removeTag(final Tag<Material> tag)
    {
        if (tags.isEmpty())
        {
            return false;
        }
        else
        {
            return tags.remove(tag);
        }
    }

    /**
     * @param i
     * @return true if this list contains the specified element
     */
    public boolean contains(final BlockData i)
    {
        for (BlockData in : blocks)
        {
            if (i.matches(in)) {
                return true;
            }
        }

        for (Tag<Material> tag : tags)
        {
            if (tag.isTagged(i.getMaterial())) {
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
        blocks.clear();
        tags.clear();
    }

    /**
     * Returns true if this list contains no elements.
     *
     * @return true if this list contains no elements
     */
    public boolean isEmpty()
    {
        return blocks.isEmpty() || tags.isEmpty();
    }

    /**
     * Returns a defensive copy of the List.
     *
     * @return defensive copy of the List
     */
    public List<BlockData> getBlockList()
    {
        return ImmutableList.copyOf(blocks);
    }

    /**
     * Returns a defensive copy of the List.
     *
     * @return defensive copy of the List
     */
    public List<Tag<Material>> getTagList()
    {
        return ImmutableList.copyOf(tags);
    }

}
