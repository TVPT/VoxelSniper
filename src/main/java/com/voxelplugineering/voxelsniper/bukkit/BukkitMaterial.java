package com.voxelplugineering.voxelsniper.bukkit;

import org.bukkit.Material;

import com.voxelplugineering.voxelsniper.common.CommonMaterial;

public class BukkitMaterial extends CommonMaterial<Material>
{

    protected BukkitMaterial(Material value)
    {
        super(value);
    }

    @Override
    public String toString()
    {
        return this.getValue().name();
    }

}
