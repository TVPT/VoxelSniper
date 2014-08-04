/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;

import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pCombo extends vPerformer
{

    private int i;
    private byte d;

    public pCombo()
    {
        name = "Combo";
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.data();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        i = v.getVoxelId();
        d = v.getData();
        p = v.owner().getPlayer().getName();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        h.put(b);
        CoreProtectUtils.logBlockRemove(b, p);
        b.setTypeIdAndData(i, d, true);
	CoreProtectUtils.logBlockPlace(b, p);
    }
}
