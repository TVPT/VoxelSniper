/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.VoxelList;
import com.thevoxelbox.voxelsniper.util.CoreProtectUtils;

import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pIncludeMat extends vPerformer
{

    private VoxelList includeList;
    private int id;

    public pIncludeMat()
    {
        name = "Include Material";
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
        id = v.getVoxelId();
        includeList = v.getVoxelList();
        p = v.owner().getPlayer().getName();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        if (includeList.contains(new int[]{b.getTypeId(), b.getData()}))
        {
            h.put(b);
	    CoreProtectUtils.logBlockRemove(b, p);
            b.setTypeId(id);
	    CoreProtectUtils.logBlockPlace(b, p);
        }
    }
}
