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
public class pIncludeMat extends vPerformer
{

    private VoxelList includeList;
    private Material t;

    public pIncludeMat()
    {
        name = "Include Material";
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
        includeList = v.getVoxelList();
    }

	@Override
    public void perform(Block b)
    {
        if (includeList.contains(b.getBlockData()))
        {
            h.put(b);
            b.setType(t);
        }
    }
}
