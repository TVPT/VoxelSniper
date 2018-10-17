/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.VoxelList;

/**
 * @author Voxel
 */
public class pExcludeMat extends vPerformer
{

    private VoxelList excludeList;
    private Material t;

    public pExcludeMat()
    {
        name = "Exclude Material";
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelList();
        vm.voxel();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        t = v.getVoxelData().getMaterial();
        excludeList = v.getVoxelList();
    }

	@Override
    public void perform(Block b)
    {
        if (!excludeList.contains(b.getBlockData()))
        {
            h.put(b);
            b.setType(t);
        }
    }
}
