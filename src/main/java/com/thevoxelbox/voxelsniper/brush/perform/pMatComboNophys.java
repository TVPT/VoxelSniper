/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class pMatComboNophys extends vPerformer
{

    private BlockData rd;
    private Material t;

    public pMatComboNophys()
    {
        name = "Mat-Combo, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        rd = v.getReplaceData();
        t = v.getVoxelMat();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.replace();
        vm.replaceInk();
    }

    @Override
    public void perform(Block b)
    {
        if (b.getBlockData().matches(rd))
        {
            h.put(b);
            b.setType(t, false);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
