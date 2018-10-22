/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.Inker;
import com.thevoxelbox.voxelsniper.util.VoxelList;

import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pExcludeInk extends vPerformer
{

    private VoxelList excludeList;
    private String ink;

    public pExcludeInk()
    {
        name = "Exclude Ink";
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelList();
        vm.voxelInk();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        ink = v.getVoxelInk();
        excludeList = v.getVoxelList();
    }

    @Override
    public void perform(Block b)
    {
        if (!excludeList.contains(b.getBlockData()))
        {
            h.put(b);
            Inker.ink(b, ink);
        }
    }
}
