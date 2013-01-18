/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.VoxelList;

import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pExcludeMat extends vPerformer
{

    private VoxelList el;
    private int i;

    public pExcludeMat()
    {
        name = "Exclude Material"; // This should probably be Material, Exclude Material and have the abbreviation mxm. Then you could have Combo exclude material cxm and material exclude combo mxc - Giltwist
        // I'll worry about the naming scheme when the performer works. ATM, it's very buggy... -psa
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelList();
        vm.voxel();
        vm.data();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        i = v.getVoxelId();
        el = v.getVoxelList();
    }

    @Override
    public void perform(Block b)
    {
        if (el.contains(b.getTypeId()))
        {
            return;
        }
        else
        {
            h.put(b);
            b.setTypeId(i);
        }
    }
}
