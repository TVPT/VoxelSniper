/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import com.thevoxelbox.voxelsniper.Message;

/**
 * @author Voxel
 */
public class pComboNoPhys extends vPerformer
{

	private BlockData bd;

    public pComboNoPhys()
    {
        name = "Combo NoPhysics";
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
		w = v.getWorld();
		bd = v.getVoxelData();
    }

	@Override
    public void perform(Block b)
    {
        h.put(b);
        b.setBlockData(bd, false);
    }
}
