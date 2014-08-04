package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;

/**
 * @author Voxel
 */
public class pInkMatNoPhys extends vPerformer
{

    private byte d;
    private int ir;

    public pInkMatNoPhys()
    {
        name = "Ink-Mat, No Physics";
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
