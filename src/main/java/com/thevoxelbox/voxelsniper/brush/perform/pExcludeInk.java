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
public class pExcludeInk extends vPerformer
{

    private VoxelList excludeList;
    private byte data;

    public pExcludeInk()
    {
        name = "Exclude Ink";
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelList();
        vm.data();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        data = v.getData();
        excludeList = v.getVoxelList();
        p = v.owner().getPlayer().getName();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        if (!excludeList.contains(new int[] {b.getTypeId(), b.getData()}))
        {
            h.put(b);
	    CoreProtectUtils.logBlockRemove(b, p);
            b.setData(data);
	    CoreProtectUtils.logBlockPlace(b, p);
        }
    }
}
