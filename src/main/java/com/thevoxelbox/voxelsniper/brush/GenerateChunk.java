package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.Chunk;

/**
 *
 * @author Mick
 */
public class GenerateChunk extends Brush {

    private Chunk ch;

    public GenerateChunk() {
        name = "Chunk Generator 40k";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        generateChunk(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.brushMessage("Tread lightly.");
        vm.brushMessage("This brush will melt your spleen and sell your kidneys.");
    }

    public void generateChunk(vData v) {
        ch = tb.getChunk();

        vUndo h = new vUndo(tb.getWorld().getName());

        for (int z = 16; z >= 0; z--) {
            for (int x = 16; x >= 0; x--) {
                for (int y = 128; y >= 0; y--) {
                    h.put(ch.getBlock(x, y, z));
                }
            }
        }
        v.storeUndo(h);

        // !!! Very dangerous! Do not touch! It will melt your spleen and sell your kidneys.  <- disregard DivineRage he knows not what he speaks of... -prz
        v.owner().p.sendMessage("Generate that chunk! " + ch.getX() + " " + ch.getZ());
        w.regenerateChunk(ch.getX(), ch.getZ());
        w.refreshChunk(ch.getX(), ch.getZ());
        //s.regenerateChunk((int) Math.floor(ch.getX() / 16), (int) Math.floor(ch.getZ() / 16));
    }
}
