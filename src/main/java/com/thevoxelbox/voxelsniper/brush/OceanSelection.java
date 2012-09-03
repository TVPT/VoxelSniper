package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Voxel
 */
public class OceanSelection extends Ocean {

    protected boolean sel = true;

    private static int timesUsed = 0;

    public OceanSelection() {
        this.setName("Ocean Selection");
    }

    @Override
    public int getTimesUsed() {
        return OceanSelection.timesUsed;
    }

    @Override
    public void info(final Message vm) {
        vm.brushName(this.getName());
    }

    public void oceanate(final SnipeData v, final int lowx, final int highx, final int lowz, final int highz) {
        this.h = new Undo(this.getTargetBlock().getWorld().getName());
        for (int x = lowx; x <= highx; x += 16) {
            this.setTargetBlock(this.setX(this.getTargetBlock(), x));
            for (int z = lowz; z <= highz; z += 16) {
                this.setTargetBlock(this.setZ(this.getTargetBlock(), z));
                this.oceanator(v);
            }
        }
        v.storeUndo(this.h);
    }

    public void oceanSelection(final SnipeData v) {
        if (this.sel) {
            this.h = new Undo(this.getTargetBlock().getWorld().getName());
            this.oceanator(v);
            v.storeUndo(this.h);
            this.s1x = this.getTargetBlock().getX();
            this.s1z = this.getTargetBlock().getZ();
            v.sendMessage(ChatColor.DARK_PURPLE + "Chunk one selected");
            this.sel = !this.sel;
        } else {
            v.sendMessage(ChatColor.DARK_PURPLE + "Chunk two selected");
            this.h = new Undo(this.getTargetBlock().getWorld().getName());
            this.oceanator(v);
            v.storeUndo(this.h);
            this.s2x = this.getTargetBlock().getX();
            this.s2z = this.getTargetBlock().getZ();
            this.oceanate(v, ((this.s1x <= this.s2x) ? this.s1x : this.s2x), ((this.s2x >= this.s1x) ? this.s2x : this.s1x), ((this.s1z <= this.s2z) ? this.s1z
                    : this.s2z), ((this.s2z >= this.s1z) ? this.s2z : this.s1z));
            this.sel = !this.sel;
        }
    }

    @Override
    public void setTimesUsed(final int tUsed) {
        OceanSelection.timesUsed = tUsed;
    }

    @Override
    protected void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.oceanSelection(v);
    }

    @Override
    protected void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.oceanSelection(v);
    }
}
