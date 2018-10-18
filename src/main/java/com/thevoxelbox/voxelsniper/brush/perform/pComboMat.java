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
public class pComboMat extends vPerformer
{

    private BlockData vd;
    private Material rt;

    public pComboMat()
    {
        name = "Combo-Mat";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        vd = v.getVoxelData();
        rt = v.getReplaceData().getMaterial();
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
        if (b.getType() == rt)
        {
            h.put(b);
            b.setBlockData(vd, true);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
