/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import net.minecraft.server.Packet53BlockChange;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;

/**
 *
 * @author Voxel
 */
public class pInkUpdate extends vPerformer {

    private byte d;
    private vSniper s;

    public pInkUpdate() {
        name = "Ink-Update";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.vData v) {
        w = v.getWorld();
        d = v.data;
        s = v.owner();
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.custom(ChatColor.RED + "USE WITH CAUTION");
        vm.data();
    }

    @Override
    public void perform(Block b) {
        h.put(b);
        b.setData(d);
        ((CraftPlayer) s.p).getHandle().netServerHandler.sendPacket(new Packet53BlockChange(b.getX(), b.getY(), b.getZ(), ((CraftWorld) s.p.getWorld()).getHandle()));
    }
}
