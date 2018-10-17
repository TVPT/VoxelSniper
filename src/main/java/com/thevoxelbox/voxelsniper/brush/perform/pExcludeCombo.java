/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.VoxelList;

/**
 * @author Voxel
 */
public class pExcludeCombo extends vPerformer
{

    private VoxelList excludeList;
    private BlockData bd;

    public pExcludeCombo()
    {
        name = "Exclude Combo";
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelList();
        vm.voxel();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        bd = v.getVoxelData();
        excludeList = v.getVoxelList();
    }

	@Override
    public void perform(Block b)
    {
    	if(!excludeList.contains(b.getBlockData())) {
			h.put(b);
			b.setBlockData(bd, true);
		}
    }
}
