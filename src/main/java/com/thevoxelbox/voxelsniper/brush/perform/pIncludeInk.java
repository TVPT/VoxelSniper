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
public class pIncludeInk extends vPerformer
{

    private VoxelList includeList;
    private String ink;

    public pIncludeInk()
    {
        name = "Include Ink";
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
        includeList = v.getVoxelList();
    }

    @Override
    public void perform(Block b)
    {
        if (includeList.contains(b.getBlockData()))
        {
            h.put(b);
            Inker.ink(b, ink);
        }
    }
}
