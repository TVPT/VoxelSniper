package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;

/**
 * @author Voxel
 */
public class pInkInkNoPhys extends vPerformer
{

    private byte d;
    private byte dr;

    public pInkInkNoPhys()
    {
        name = "Ink-Ink, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        d = v.getData();
        dr = v.getReplaceData();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.data();
        vm.replaceData();
    }

    @Override
    public void perform(Block b)
    {
        if (b.getData() == dr)
        {
            h.put(b);
            b.setData(d, false);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}