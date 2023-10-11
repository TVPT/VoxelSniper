package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.Inker;

/**
 * @author Voxel
 */
public class pInkComboNoPhys extends vPerformer
{

    private String i;
    private BlockData rbd;

    public pInkComboNoPhys()
    {
        name = "Ink-Combo, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        i = v.getVoxelInk();
        rbd = v.getReplaceData();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.replace();
        vm.voxelInk();
        vm.replaceInk();
    }

    @Override
    public void perform(Block b)
    {
        if (b.getBlockData().matches(rbd))
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
