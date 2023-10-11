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
public class pMatMatNoPhys extends vPerformer
{

    private Material vt;
    private Material rt;

    public pMatMatNoPhys()
    {
        name = "Mat-Mat No-Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        vt = v.getVoxelMat();
        rt = v.getReplaceMat();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.replace();
    }

    @Override
    public void perform(Block b)
    {
        if (b.getType() == rt)
        {
            h.put(b);
            b.setType(vt, false);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
