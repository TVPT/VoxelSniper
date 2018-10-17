/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;

/**
 * @author Voxel
 */
public class pNoUndo extends vPerformer
{

    private Material t;

    public pNoUndo()
    {
        name = "BOMB SQUAD";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        t = v.getVoxelData().getMaterial();
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
            b.setType(t);
        }
    }
}
