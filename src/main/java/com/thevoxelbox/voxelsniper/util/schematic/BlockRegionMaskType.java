package com.thevoxelbox.voxelsniper.util.schematic;

/**
 * Mask types, as per old stencil types.
 * 
 * @author Deamon
 *
 */
public enum BlockRegionMaskType
{
    NONE(), // No mask
    REPLACE(), // Replace only these materials
    NEGATIVE_REPLACE(); // Replace only not these materials
}
