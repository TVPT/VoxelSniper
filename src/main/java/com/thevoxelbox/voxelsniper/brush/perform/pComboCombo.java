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
public class pComboCombo extends vPerformer
{

    private byte d;
    private byte dr;
    private int i;
    private int ir;

    public pComboCombo()
    {
        name = "Combo-Combo";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        d = v.getData();
        dr = v.getReplaceData();
        i = v.getVoxelId();
        ir = v.getReplaceId();
        p = v.owner().getPlayer().getName();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.replace();
        vm.data();
        vm.replaceData();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        if (b.getTypeId() == ir && b.getData() == dr)
        {
            h.put(b);
            CoreProtectUtils.logBlockRemove(b, p);
            b.setTypeIdAndData(i, d, true);
            CoreProtectUtils.logBlockPlace(b, p)
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
