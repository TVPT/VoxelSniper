package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.block.Block;
import com.thevoxelbox.voxelsniper.util.CoreProtectUtils;

import com.thevoxelbox.voxelsniper.Message;

/**
 * @author Voxel
 */
public class pInkComboNoPhys extends vPerformer
{

    private byte d;
    private byte dr;
    private int ir;

    public pInkComboNoPhys()
    {
        name = "Ink-Combo, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        d = v.getData();
        dr = v.getReplaceData();
        ir = v.getReplaceId();
        p = v.owner().getPlayer().getName();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
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
            b.setData(d, false);
	    CoreProtectUtils.logBlockPlace(b, p);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}
