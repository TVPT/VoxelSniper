/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import net.minecraft.server.Packet53BlockChange;
import org.bukkit.craftbukkit.CraftWorld;

/**
 *
 * @author Voxel
 */
public class pComboUpdate extends vPerformer {

    private byte d;
    private int i;
    private vSniper s;

    public pComboUpdate() {
        name = "Combo-Update";
    }

    @Override
    public void init(vSniper v) {
        w = v.p.getWorld();
        d = v.data;
        i = v.voxelId;
        s = v;
    }

    @Override
    public void info(vMessage vm) {
        vm.performerName(name);
        vm.custom(ChatColor.RED+"USE WITH CAUTION");
        vm.voxel();
        vm.data();
    }

    @Override
    public void perform(Block b) {
        h.put(b);
        b.setTypeIdAndData(i, d, true);
        ((CraftPlayer) s.p).getHandle().netServerHandler.sendPacket(new Packet53BlockChange(b.getX(),b.getY(),b.getZ(),((CraftWorld) s.p.getWorld()).getHandle()));
    }
}
