package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;

import com.thevoxelbox.voxelsniper.vMessage;

/**
 * 
 * @author GavJenks
 */
public class FlatOcean extends Brush {

    protected int yoLevel = 29;
    protected int ylLevel = 8;

    private static int timesUsed = 0;

    public FlatOcean() {
        this.name = "FlatOcean";
    }

    @Override
    public final int getTimesUsed() {
        return FlatOcean.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.custom(ChatColor.RED + "THIS BRUSH DOES NOT UNDO");
        vm.custom(ChatColor.GREEN + "Water level set to " + this.yoLevel);
        vm.custom(ChatColor.GREEN + "Ocean floor level set to " + this.ylLevel);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {

        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GREEN + "yo[number] to set the Level to which the water will rise.");
            v.sendMessage(ChatColor.GREEN + "yl[number] to set the Level to which the ocean floor will rise.");
        }
        for (int x = 1; x < par.length; x++) {

            if (par[x].startsWith("yo")) {
                int i = Integer.parseInt(par[x].replace("yo", ""));
                if (i < this.ylLevel) {
                    i = this.ylLevel + 1;
                }
                this.yoLevel = i;
                v.sendMessage(ChatColor.GREEN + "Water Level set to " + this.yoLevel);
                continue;
            } else if (par[x].startsWith("yl")) {
                int i = Integer.parseInt(par[x].replace("yl", ""));
                if (i > this.yoLevel) {
                    i = this.yoLevel - 1;
                    if (i == 0) {
                        i = 1;
                        this.yoLevel = 2;
                    }
                }
                this.ylLevel = i;
                v.sendMessage(ChatColor.GREEN + "Ocean floor Level set to " + this.ylLevel);
                continue;
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        FlatOcean.timesUsed = tUsed;
    }

    private void flatOcean(final Chunk c) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    if (y <= this.ylLevel) {
                        c.getBlock(x, y, z).setTypeId(3, true);
                    } else if (y <= this.yoLevel) {
                        c.getBlock(x, y, z).setTypeId(9, false);
                    } else {
                        c.getBlock(x, y, z).setTypeId(0, false);
                    }
                }
            }
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.bz = this.tb.getZ();

        this.flatOcean(this.w.getChunkAt(this.tb));
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.bz = this.tb.getZ();

        this.flatOcean(this.w.getChunkAt(this.tb));
        this.flatOcean(this.w.getChunkAt(this.clampY(this.bx + 16, 63, this.bz)));
        this.flatOcean(this.w.getChunkAt(this.clampY(this.bx + 16, 63, this.bz + 16)));
        this.flatOcean(this.w.getChunkAt(this.clampY(this.bx, 63, this.bz + 16)));
        this.flatOcean(this.w.getChunkAt(this.clampY(this.bx - 16, 63, this.bz + 16)));
        this.flatOcean(this.w.getChunkAt(this.clampY(this.bx - 16, 63, this.bz)));
        this.flatOcean(this.w.getChunkAt(this.clampY(this.bx - 16, 63, this.bz - 16)));
        this.flatOcean(this.w.getChunkAt(this.clampY(this.bx, 63, this.bz - 16)));
        this.flatOcean(this.w.getChunkAt(this.clampY(this.bx + 16, 63, this.bz - 16)));
    }
}
