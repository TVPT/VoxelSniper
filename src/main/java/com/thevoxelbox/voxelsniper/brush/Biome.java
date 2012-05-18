/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author Piotr <przerwap@gmail.com>
 */
public class Biome extends Brush {

    protected org.bukkit.block.Biome selected = org.bukkit.block.Biome.PLAINS;

    public Biome() {
        name = "Biome";
    }

    @Override
    protected void arrow(vData v) {
        bio(v);
    }

    @Override
    protected void powder(vData v) {
        bio(v);
    }

    @Override
    public void parameters(String[] par, vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Biome Brush Parameters:");
            String biomes = "";
            boolean first = true;
            for (org.bukkit.block.Biome bio : org.bukkit.block.Biome.values()) {
                if (first) {
                    first = false;
                    biomes = ChatColor.DARK_GREEN + bio.name();
                } else {
                    biomes += ChatColor.RED + ", " + ChatColor.DARK_GREEN + bio.name();
                }
            }
            v.sendMessage(ChatColor.DARK_BLUE + "Available biomes: " + biomes);
        } else {
            for (org.bukkit.block.Biome bio : org.bukkit.block.Biome.values()) {
                if (bio.name().equals(par[1])) {
                    selected = bio;
                    break;
                }
            }
            v.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + selected.name());
        }
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.custom(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + selected.name());
    }

    protected void bio(vData v) {
        int bsize = v.brushSize;
        double bpow = Math.pow(bsize, 2);
        for (int x = -bsize; x <= bsize; x++) {
            double xpow = Math.pow(x, 2);
            for (int z = -bsize; z <= bsize; z++) {
                if ((xpow + Math.pow(z, 2)) <= bpow) {
                    w.setBiome(bx + x, bz + z, selected);
                }
            }
        }

        Block b = w.getBlockAt(bx - bsize, 0, bz - bsize);
        Block bl = w.getBlockAt(bx + bsize, 0, bz + bsize);

        int lowx = (b.getX() <= bl.getX()) ? b.getChunk().getX() : bl.getChunk().getX();
        int lowz = (b.getZ() <= bl.getZ()) ? b.getChunk().getX() : bl.getChunk().getX();
        int highx = (b.getX() >= bl.getX()) ? b.getChunk().getX() : bl.getChunk().getX();
        int highz = (b.getZ() >= bl.getZ()) ? b.getChunk().getX() : bl.getChunk().getX();

        for (int x = lowx; x <= highx; x++) {
            for (int z = lowz; z <= highz; z++) {
                w.refreshChunk(x, z);
            }
        }
    }
}
