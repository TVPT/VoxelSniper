/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.Inker;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pInkMat extends vPerformer
{

    private String i;
    private Material rm;

    public pInkMat()
    {
        name = "Ink-Mat";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        i = v.getVoxelInk();
        rm = v.getReplaceMat();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelInk();
        vm.replace();
    }

    @Override
    public void perform(Block b)
    {
        if (b.getType() == rm)
        {
            h.put(b);
            Inker.ink(b, i, true);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
