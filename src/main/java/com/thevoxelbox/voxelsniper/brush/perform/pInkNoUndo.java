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
public class pInkNoUndo extends vPerformer
{

    private String i;

    public pInkNoUndo()
    {
        name = "Ink, No-Undo"; // made name more descriptive - Giltwist
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        i = v.getVoxelInk();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelInk();
    }

    @Override
    public void perform(Block b)
    {
        if (Inker.matches(b, i))
        {
            Inker.ink(b, i);
        }
    }
}
