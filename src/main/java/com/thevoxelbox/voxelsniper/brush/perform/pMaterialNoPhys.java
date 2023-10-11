/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pMaterialNoPhys extends vPerformer
{
    private Material t;

    public pMaterialNoPhys()
    {
        name = "Set, No-Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        t = v.getVoxelMat();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
    }

    @Override
    public void perform(Block b)
    {
        if (b.getType() != t)
        {
            h.put(b);
            b.setType(t, false);
        }
    }
}
