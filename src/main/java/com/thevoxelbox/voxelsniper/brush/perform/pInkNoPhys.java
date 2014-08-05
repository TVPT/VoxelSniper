package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.block.Block;
import com.thevoxelbox.voxelsniper.util.CoreProtectUtils;

import com.thevoxelbox.voxelsniper.Message;

/**
 * @author Voxel
 */
public class pInkNoPhys extends vPerformer
{

    private byte d;

    public pInkNoPhys()
    {
        name = "Ink, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        d = v.getData();
        p = v.owner().getPlayer().getName();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.data();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        h.put(b);
        CoreProtectUtils.logBlockRemove(b, p);
        b.setData(d, false);
        CoreProtectUtils.logBlockPlace(b, p);
    }
}
