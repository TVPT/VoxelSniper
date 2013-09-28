/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;

import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pComboInkNoPhys extends vPerformer
{

    private byte d;
    private byte dr;
    private int i;

    public pComboInkNoPhys()
    {
        name = "Combo-Ink, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        d = v.getData();
        dr = v.getReplaceData();
        i = v.getVoxelId();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.data();
        vm.replaceData();
    }

    @Override
    public void perform(Block b)
    {
        if (b.getData() == dr)
        {
            h.put(b);
            b.setTypeIdAndData(i, d, false);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
