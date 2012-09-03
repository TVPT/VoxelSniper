package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

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
        this.setName("Canyon Selection");
    }

    @Override
    public final int getTimesUsed() {
        return CanyonSelection.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(ChatColor.GREEN + "Shift Level set to " + this.yLevel);
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        CanyonSelection.timesUsed = tUsed;
    }

    private void selection(final int lowX, final int lowZ, final int highX, final int highZ, final SnipeData v) {
        this.m = new Undo(this.getWorld().getChunkAt(this.getTargetBlock()).getWorld().getName());

        for (int x = lowX; x <= highX; x++) {
            for (int z = lowZ; z <= highZ; z++) {
                this.multiCanyon(this.getWorld().getChunkAt(x, z), v);
            }
        }

        v.storeUndo(this.m);
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.powder(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (this.first) {
            final Chunk c = this.getWorld().getChunkAt(this.getTargetBlock());
            this.fx = c.getX();
            this.fz = c.getZ();
            v.sendMessage(ChatColor.YELLOW + "First point selected!");
            this.first = !this.first;
        } else {
            final Chunk c = this.getWorld().getChunkAt(this.getTargetBlock());
            this.setBlockPositionX(c.getX());
            this.setBlockPositionZ(c.getZ());
            v.sendMessage(ChatColor.YELLOW + "Second point selected!");
            this.selection(this.fx < this.getBlockPositionX() ? this.fx : this.getBlockPositionX(), this.fz < this.getBlockPositionZ() ? this.fz : this.getBlockPositionZ(), this.fx > this.getBlockPositionX() ? this.fx : this.getBlockPositionX(),
                    this.fz > this.getBlockPositionZ() ? this.fz : this.getBlockPositionZ(), v);
            this.first = !this.first;
        }
    }
}
