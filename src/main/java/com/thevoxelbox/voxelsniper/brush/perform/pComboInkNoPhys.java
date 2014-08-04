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
public class pComboInkNoPhys extends vPerformer
{

    private byte d;
    private byte dr;
    private int i;

    public pComboInkNoPhys()
    {
        name = "Combo-Ink, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        d = v.getData();
        dr = v.getReplaceData();
        i = v.getVoxelId();
        p = v.owner().getPlayer().getName();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.data();
        vm.replaceData();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        if (b.getData() == dr)
        {
            h.put(b);
	    CoreProtectUtils.logBlockRemove(b, p);
            b.setTypeIdAndData(i, d, false);
	    CoreProtectUtils.logBlockPlace(b, p);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
