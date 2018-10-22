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
public class pMatInkNoPhys extends vPerformer
{

    private Material m;
    private String ri;

    public pMatInkNoPhys()
    {
        name = "Mat-Ink, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        m = v.getVoxelMat();
        ri = v.getReplaceInk();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.replaceInk();
    }

    @Override
    public void perform(Block b)
    {
        if (Inker.matches(b, ri))
        {
            h.put(b);
            b.setType(m, false);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
