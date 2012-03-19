/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import net.minecraft.server.Packet51MapChunk;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;

/**
 *THIS BRUSH SHOULD NOT USE PERFORMERS
 * @author giltwist
 * 
 */
public class LoadChunk extends Brush {

    @Override
    public void arrow(vSniper v) {
        dochunkload(v);
    }

    @Override
    public void powder(vSniper v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName("Load Chunk Brush");
        vm.custom(ChatColor.AQUA + "This brush refreshes chunks to fix chunk errors.");
    }

    public void dochunkload(vSniper v) {
        int j = v.p.getWorld().getChunkAt(v.p.getLocation()).getX();
        int k = v.p.getWorld().getChunkAt(v.p.getLocation()).getZ();
        byte[] data = new byte[81920];
        ((CraftPlayer) v.p).getHandle().netServerHandler.sendPacket(new Packet51MapChunk(((CraftChunk) v.p.getWorld().getChunkAt(v.p.getLocation().getBlock())).getHandle(), true, 0));
        
        // ... why the hack method when there is a built-in Bukkit protocol for this? That I added? -psa
    }
}
