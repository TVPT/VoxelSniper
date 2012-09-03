package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * @author Piotr <przerwap@gmail.com>
 */
public class Biome extends Brush {

    protected org.bukkit.block.Biome selected = org.bukkit.block.Biome.PLAINS;

    private static int timesUsed = 0;

    public Biome() {
        this.setName("Biome");
    }

    @Override
    public final int getTimesUsed() {
        return Biome.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selected.name());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Biome Brush Parameters:");
            String biomes = "";
            boolean first = true;
            for (final org.bukkit.block.Biome bio : org.bukkit.block.Biome.values()) {
                if (first) {
                    first = false;
                    biomes = ChatColor.DARK_GREEN + bio.name();
                } else {
                    biomes += ChatColor.RED + ", " + ChatColor.DARK_GREEN + bio.name();
                }
            }
            v.sendMessage(ChatColor.DARK_BLUE + "Available biomes: " + biomes);
        } else {
            for (final org.bukkit.block.Biome bio : org.bukkit.block.Biome.values()) {
                if (bio.name().equals(par[1])) {
                    this.selected = bio;
                    break;
                }
            }
            v.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selected.name());
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Biome.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.bio(v);
    }

    protected final void bio(final SnipeData v) {
        final int bsize = v.getBrushSize();
        final double bpow = Math.pow(bsize, 2);
        for (int x = -bsize; x <= bsize; x++) {
            final double xpow = Math.pow(x, 2);
            for (int z = -bsize; z <= bsize; z++) {
                if ((xpow + Math.pow(z, 2)) <= bpow) {
                    this.getWorld().setBiome(this.getBlockPositionX() + x, this.getBlockPositionZ() + z, this.selected);
                }
            }
        }

        final Block b = this.getWorld().getBlockAt(this.getBlockPositionX() - bsize, 0, this.getBlockPositionZ() - bsize);
        final Block bl = this.getWorld().getBlockAt(this.getBlockPositionX() + bsize, 0, this.getBlockPositionZ() + bsize);

        final int lowx = (b.getX() <= bl.getX()) ? b.getChunk().getX() : bl.getChunk().getX();
        final int lowz = (b.getZ() <= bl.getZ()) ? b.getChunk().getX() : bl.getChunk().getX();
        final int highx = (b.getX() >= bl.getX()) ? b.getChunk().getX() : bl.getChunk().getX();
        final int highz = (b.getZ() >= bl.getZ()) ? b.getChunk().getX() : bl.getChunk().getX();

        for (int x = lowx; x <= highx; x++) {
            for (int z = lowz; z <= highz; z++) {
                this.getWorld().refreshChunk(x, z);
            }
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.bio(v);
    }
}
