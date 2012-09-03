package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Voxel
 */
public class CanyonSelection extends Canyon {

    private boolean first = true;
    private int fx;
    private int fz;

    private static int timesUsed = 0;

    public CanyonSelection() {
        this.name = "Canyon Selection";
    }

    @Override
    public final int getTimesUsed() {
        return CanyonSelection.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.custom(ChatColor.GREEN + "Shift Level set to " + this.yLevel);
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        CanyonSelection.timesUsed = tUsed;
    }

    private void selection(final int lowX, final int lowZ, final int highX, final int highZ, final vData v) {
        this.m = new vUndo(this.w.getChunkAt(this.tb).getWorld().getName());

        for (int x = lowX; x <= highX; x++) {
            for (int z = lowZ; z <= highZ; z++) {
                this.multiCanyon(this.w.getChunkAt(x, z), v);
            }
        }

        v.storeUndo(this.m);
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.powder(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        if (this.first) {
            final Chunk c = this.w.getChunkAt(this.tb);
            this.fx = c.getX();
            this.fz = c.getZ();
            v.sendMessage(ChatColor.YELLOW + "First point selected!");
            this.first = !this.first;
        } else {
            final Chunk c = this.w.getChunkAt(this.tb);
            this.bx = c.getX();
            this.bz = c.getZ();
            v.sendMessage(ChatColor.YELLOW + "Second point selected!");
            this.selection(this.fx < this.bx ? this.fx : this.bx, this.fz < this.bz ? this.fz : this.bz, this.fx > this.bx ? this.fx : this.bx,
                    this.fz > this.bz ? this.fz : this.bz, v);
            this.first = !this.first;
        }
    }
}
