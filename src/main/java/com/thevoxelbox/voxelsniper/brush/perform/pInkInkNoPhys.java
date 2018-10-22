package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.Inker;

import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pInkInkNoPhys extends vPerformer
{

    private String i;
    private String ri;

    public pInkInkNoPhys()
    {
        name = "Ink-Ink, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        i = v.getVoxelInk();
        ri = v.getReplaceInk();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelInk();
        vm.replaceInk();
    }

    @Override
    public void perform(Block b)
    {
        if (Inker.matches(b, ri))
        {
            h.put(b);
            Inker.ink(b, i, false);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
