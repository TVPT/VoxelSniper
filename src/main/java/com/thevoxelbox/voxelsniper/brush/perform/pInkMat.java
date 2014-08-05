/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.CoreProtectUtils;

import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pInkMat extends vPerformer
{

    private byte d;
    private int ir;

    public pInkMat()
    {
        name = "Ink-Mat";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        d = v.getData();
        ir = v.getReplaceId();
        p = v.owner().getPlayer().getName();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.data();
        vm.replace();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        if (b.getTypeId() == ir)
        {
            h.put(b);
            CoreProtectUtils.logBlockRemove(b, p);
            b.setData(d, true);
            CoreProtectUtils.logBlockPlace(b, p);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
