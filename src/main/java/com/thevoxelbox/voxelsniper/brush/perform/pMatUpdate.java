/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Sniper;
import net.minecraft.server.v1_4_5.Packet53BlockChange;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_5.CraftWorld;
import org.bukkit.craftbukkit.v1_4_5.entity.CraftPlayer;

/**
 *
 * @author Voxel
 */
public class pMatUpdate extends vPerformer {

    private int i;
    private Sniper s;

    public pMatUpdate() {
        name = "Mat-Update";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
        w = v.getWorld();
        i = v.getVoxelId();
        s = v.owner();
    }

    @Override
    public void info(Message vm) {
        vm.performerName(name);
        vm.custom(ChatColor.RED + "USE WITH CAUTION");
        vm.voxel();
    }

    @Override
    public void perform(Block b) {
        h.put(b);
        b.setTypeId(i);
        ((CraftPlayer) s.getPlayer()).getHandle().netServerHandler.sendPacket(new Packet53BlockChange(b.getX(), b.getY(), b.getZ(), ((CraftWorld) s.getPlayer().getWorld()).getHandle()));
    }
}
