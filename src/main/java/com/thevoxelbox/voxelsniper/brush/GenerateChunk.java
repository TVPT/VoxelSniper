package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Chunk;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Mick
 */
public class GenerateChunk extends Brush {

    private Chunk ch;

    private static int timesUsed = 0;

    public GenerateChunk() {
        this.name = "Chunk Generator 40k";
    }

    public final void generateChunk(final vData v) {
        this.ch = this.tb.getChunk();

        final vUndo h = new vUndo(this.tb.getWorld().getName());

        for (int z = 16; z >= 0; z--) {
            for (int x = 16; x >= 0; x--) {
                for (int y = 128; y >= 0; y--) {
                    h.put(this.ch.getBlock(x, y, z));
                }
            }
        }
        v.storeUndo(h);

        // !!! Very dangerous! Do not touch! It will melt your spleen and sell your kidneys. <- disregard DivineRage he knows not what he speaks of... -prz
        v.owner().getPlayer().sendMessage("Generate that chunk! " + this.ch.getX() + " " + this.ch.getZ());
        this.w.regenerateChunk(this.ch.getX(), this.ch.getZ());
        this.w.refreshChunk(this.ch.getX(), this.ch.getZ());
        // s.regenerateChunk((int) Math.floor(ch.getX() / 16), (int) Math.floor(ch.getZ() / 16));
    }

    @Override
    public final int getTimesUsed() {
        return GenerateChunk.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.brushMessage("Tread lightly.");
        vm.brushMessage("This brush will melt your spleen and sell your kidneys.");
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        GenerateChunk.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.generateChunk(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.arrow(v);
    }
}
