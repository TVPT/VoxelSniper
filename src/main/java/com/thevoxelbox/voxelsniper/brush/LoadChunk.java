/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import net.minecraft.server.Packet51MapChunk;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 *
 * @author giltwist
 *
 */
public class LoadChunk extends Brush {

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        dochunkload(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName("Load Chunk Brush");
        vm.custom(ChatColor.AQUA + "This brush refreshes chunks to fix chunk errors.");
    }

    public void dochunkload(vData v) {
//        int j = v.owner().p.getWorld().getChunkAt(v.owner().p.getLocation()).getX();
//        int k = v.owner().p.getWorld().getChunkAt(v.owner().p.getLocation()).getZ();
        //byte[] data = new byte[81920];
        ((CraftPlayer) v.owner().p).getHandle().netServerHandler.sendPacket(new Packet51MapChunk(((CraftChunk) v.owner().p.getWorld().getChunkAt(v.owner().p.getLocation().getBlock())).getHandle(), true, 0));

        // ... why the hack method when there is a built-in Bukkit protocol for this? That I added? -psa
    }
}
