/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.Inker;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class pComboInkNoPhys extends vPerformer
{

    private BlockData bd;
    private String i;

    public pComboInkNoPhys()
    {
        name = "Combo-Ink, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        bd = v.getVoxelData();
        i = v.getReplaceInk();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.voxelInk();
        vm.replaceInk();
    }

    @Override
    public void perform(Block b)
    {
        if (Inker.matches(b, i))
        {
            h.put(b);
            b.setBlockData(bd, false);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
