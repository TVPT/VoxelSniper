/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import com.thevoxelbox.voxelsniper.Message;

/**
 * @author Voxel
 */
public class pMatCombo extends vPerformer
{

    private BlockData rd;
    private Material t;

    public pMatCombo()
    {
        name = "Mat-Combo";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        rd = v.getReplaceData();
        t = v.getVoxelData().getMaterial();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.replace();
    }

    @Override
    public void perform(Block b)
    {
        if (b.getBlockData().matches(rd))
        {
            h.put(b);
            b.setType(t, true);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
