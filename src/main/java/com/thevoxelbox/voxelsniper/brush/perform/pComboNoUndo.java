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
public class pComboNoUndo extends vPerformer
{

    private int i;
    private byte d;

    public pComboNoUndo()
    {
        name = "Combo, No-Undo"; // made name more descriptive - Giltwist
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        i = v.getVoxelId();
        d = v.getData();
        p = v.owner().getPlayer().getName();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxel();
        vm.data();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        if (b.getTypeId() != i || b.getData() != d)
        {
	    CoreProtectUtils.logBlockRemove(b, p);
            b.setTypeIdAndData(i, d, true);
	    CoreProtectUtils.logBlockPlace(b, p);
        }
    }
}
