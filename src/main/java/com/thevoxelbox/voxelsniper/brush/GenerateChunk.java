package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Chunk;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Mick
 */
public class GenerateChunk extends Brush {

    private Chunk ch;

    private static int timesUsed = 0;

    public GenerateChunk() {
        this.setName("Chunk Generator 40k");
    }

    public final void generateChunk(final SnipeData v) {
        this.ch = this.getTargetBlock().getChunk();

        final Undo h = new Undo(this.getTargetBlock().getWorld().getName());

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
        this.getWorld().regenerateChunk(this.ch.getX(), this.ch.getZ());
        this.getWorld().refreshChunk(this.ch.getX(), this.ch.getZ());
        // s.regenerateChunk((int) Math.floor(ch.getX() / 16), (int) Math.floor(ch.getZ() / 16));
    }

    @Override
    public final int getTimesUsed() {
        return GenerateChunk.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.brushMessage("Tread lightly.");
        vm.brushMessage("This brush will melt your spleen and sell your kidneys.");
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        GenerateChunk.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.generateChunk(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.arrow(v);
    }
}
