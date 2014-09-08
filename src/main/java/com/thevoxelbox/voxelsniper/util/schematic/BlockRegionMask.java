package com.thevoxelbox.voxelsniper.util.schematic;

import com.thevoxelbox.voxelsniper.util.VoxelList;

/**
 * Mask used to determine which blocks to replace when performing an operation.
 * 
 * @author Deamon
 */
public class BlockRegionMask extends VoxelList
{
    public static final BlockRegionMask NONE = new BlockRegionMask(BlockRegionMaskType.NONE);

    private BlockRegionMaskType         type;

    public BlockRegionMask(BlockRegionMaskType t)
    {
        this.type = t;
    }

    public BlockRegionMaskType getType()
    {
        return type;
    }

    public void setType(BlockRegionMaskType type)
    {
        this.type = type;
        if (this.type == BlockRegionMaskType.NONE)
        {
            this.clear();
        }
    }
}
