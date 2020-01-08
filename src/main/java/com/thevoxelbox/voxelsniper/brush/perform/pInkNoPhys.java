package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.Inker;

/**
 * @author Voxel
 */
public class pInkNoPhys extends vPerformer
{

    private String i;

    public pInkNoPhys()
    {
        name = "Ink, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        i = v.getVoxelInk();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelInk();
    }

    @Override
    public void perform(Block b)
    {
        h.put(b);
        Inker.ink(b, i, false);
    }
}
