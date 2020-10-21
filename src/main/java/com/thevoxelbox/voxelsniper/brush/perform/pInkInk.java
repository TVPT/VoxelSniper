/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.Inker;

import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pInkInk extends vPerformer
{

    private String i;
    private String ri;

    public pInkInk()
    {
        name = "Ink-Ink";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        i = v.getVoxelInk();
        ri = v.getReplaceInk();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelInk();
        vm.replaceInk();
    }

    @Override
    public void perform(Block b)
    {
        if (Inker.matches(b, ri))
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
