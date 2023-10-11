/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.Inker;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class pInkCombo extends vPerformer
{

    private String i;
    private BlockData rbd;

    public pInkCombo()
    {
        name = "Ink-Combo";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        i = v.getVoxelInk();
        rbd = v.getReplaceData();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.replace();
        vm.voxelInk();
        vm.replaceInk();
    }

    @Override
    public void perform(Block b)
    {
        if (b.getBlockData().matches(rbd))
        {
            h.put(b);
            Inker.ink(b, i);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
