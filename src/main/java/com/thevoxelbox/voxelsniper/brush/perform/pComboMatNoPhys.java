/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class pComboMatNoPhys extends vPerformer
{

    private BlockData vd;
    private Material rt;

    public pComboMatNoPhys()
    {
        name = "Combo-Mat, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        vd = v.getVoxelData();
        rt = v.getReplaceMat();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.voxelInk();
        vm.replace();
    }

    @Override
    public void perform(Block b)
    {
        if (b.getType() == rt)
        {
            h.put(b);
            b.setBlockData(vd, false);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
