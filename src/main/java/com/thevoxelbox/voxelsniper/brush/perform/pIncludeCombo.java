/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.VoxelList;

import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pIncludeCombo extends vPerformer
{

    private VoxelList includeList;
    private int id;
    private byte data;

    public pIncludeCombo()
    {
        name = "Include Combo";
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelList();
        vm.voxel();
        vm.data();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        id = v.getVoxelId();
        data = v.getData();
        includeList = v.getVoxelList();
        p = v.owner().getPlayer().getName();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        if (includeList.contains(new int[] {b.getTypeId(), b.getData()}))
        {
            h.put(b);
	    CoreProtectUtils.logBlockRemove(b, p);
            b.setTypeIdAndData(id, data, true);
	    CoreProtectUtils.logBlockPlace(b, p);
        }
    }
}
