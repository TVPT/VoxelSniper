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
public class pMatUpdate extends vPerformer {

    private int i;
    private vSniper s;

    public pMatUpdate() {
        name = "Mat-Update";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.vData v) {
        w = v.getWorld();
        i = v.voxelId;
        s = v.owner();
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.custom(ChatColor.RED + "USE WITH CAUTION");
        vm.voxel();
    }

    @Override
    public void perform(Block b) {
        h.put(b);
        b.setTypeId(i);
        ((CraftPlayer) s.p).getHandle().netServerHandler.sendPacket(new Packet53BlockChange(b.getX(), b.getY(), b.getZ(), ((CraftWorld) s.p.getWorld()).getHandle()));
    }
}
