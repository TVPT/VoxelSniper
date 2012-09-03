package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Voxel
 */
public class Canyon extends Brush {

    protected int yLevel = 10;
    protected vUndo m;

    private static int timesUsed = 0;

    public Canyon() {
        this.setName("Canyon");
    }

    @Override
    public int getTimesUsed() {
        return Canyon.timesUsed;
    }

    @Override
    public void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.custom(ChatColor.GREEN + "Shift Level set to " + this.yLevel);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GREEN + "y[number] to set the Level to which the land will be shifted down");
        }
        if (par[1].startsWith("y")) {
            int i = Integer.parseInt(par[1].replace("y", ""));
            if (i < 10) {
                i = 10;
            } else if (i > 60) {
                i = 60;
            }
            this.yLevel = i;
            v.sendMessage(ChatColor.GREEN + "Shift Level set to " + this.yLevel);
        }
    }

    @Override
    public void setTimesUsed(final int tUsed) {
        Canyon.timesUsed = tUsed;
    }

    private void canyon(final Chunk c, final vData v) {
        int yy;

        final vUndo h = new vUndo(c.getWorld().getName());

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                yy = this.yLevel;
                for (int y = 63; y < 128; y++) {
                    final Block b = c.getBlock(x, y, z);
                    h.put(b);
                    final Block bb = c.getBlock(x, yy, z);
                    h.put(bb);
                    bb.setTypeId(b.getTypeId(), false);
                    b.setTypeId(0);
                    yy++;
                }
                final Block b = c.getBlock(x, 0, z);
                h.put(b);
                b.setTypeId(7);
                for (int y = 1; y < 10; y++) {
                    final Block bb = c.getBlock(x, y, z);
                    h.put(bb);
                    bb.setTypeId(1);
                }
            }
        }

        v.storeUndo(h);
    }

    @Override
    protected void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.canyon(this.getWorld().getChunkAt(this.getTargetBlock()), v);
    }

    protected final void multiCanyon(final Chunk c, final vData v) {
        int yy;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                yy = this.yLevel;
                for (int y = 63; y < 128; y++) {
                    final Block b = c.getBlock(x, y, z);
                    this.m.put(b);
                    final Block bb = c.getBlock(x, yy, z);
                    this.m.put(bb);
                    bb.setTypeId(b.getTypeId(), false);
                    b.setTypeId(0);
                    yy++;
                }
                final Block b = c.getBlock(x, 0, z);
                this.m.put(b);
                b.setTypeId(7);
                for (int y = 1; y < 10; y++) {
                    final Block bb = c.getBlock(x, y, z);
                    this.m.put(bb);
                    bb.setTypeId(1);
                }
            }
        }
    }

    @Override
    protected void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.m = new vUndo(this.getWorld().getChunkAt(this.getTargetBlock()).getWorld().getName());

        this.multiCanyon(this.getWorld().getChunkAt(this.getTargetBlock()), v);
        this.multiCanyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + 16, 63, this.getBlockPositionZ())), v);
        this.multiCanyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + 16, 63, this.getBlockPositionZ() + 16)), v);
        this.multiCanyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX(), 63, this.getBlockPositionZ() + 16)), v);
        this.multiCanyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - 16, 63, this.getBlockPositionZ() + 16)), v);
        this.multiCanyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - 16, 63, this.getBlockPositionZ())), v);
        this.multiCanyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - 16, 63, this.getBlockPositionZ() - 16)), v);
        this.multiCanyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX(), 63, this.getBlockPositionZ() - 16)), v);
        this.multiCanyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + 16, 63, this.getBlockPositionZ() - 16)), v);

        v.storeUndo(this.m);
    }
}
