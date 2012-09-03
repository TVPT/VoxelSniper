/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Sniper;
import net.minecraft.server.Packet53BlockChange;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;

/**
 * 
 * @author Voxel
 */
public class pComboUpdate extends vPerformer {

    private byte d;
    private int i;
    private Sniper s;

    public pComboUpdate() {
        name = "Combo-Update";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
        w = v.getWorld();
        d = v.getData();
        i = v.getVoxelId();
        s = v.owner();
    }

    @Override
    public void info(Message vm) {
        vm.performerName(name);
        vm.custom(ChatColor.RED + "USE WITH CAUTION");
        vm.voxel();
        vm.data();
    }

    @Override
    public void perform(Block b) {
        h.put(b);
        b.setTypeIdAndData(i, d, true);
        ((CraftPlayer) s.getPlayer()).getHandle().netServerHandler.sendPacket(new Packet53BlockChange(b.getX(), b.getY(), b.getZ(), ((CraftWorld) s.getPlayer()
                .getWorld()).getHandle()));
    }
}
