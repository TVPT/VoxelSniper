package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;

import java.util.Optional;
import java.util.UUID;

public class CanyonSelectionBrush extends CanyonBrush {

    private Vector3i pos;
    private UUID worldUid;

    public CanyonSelectionBrush() {
        this.setName("Canyon Selection");
    }

    private void execute(final SnipeData v) {
        if (this.pos == null || this.worldUid == null || !this.worldUid.equals(this.targetBlock.getExtent().getUniqueId())) {
            this.worldUid = this.targetBlock.getExtent().getUniqueId();
            this.pos = this.targetBlock.getChunkPosition();
            v.sendMessage(TextColors.YELLOW + "First point selected!");
        } else {
            Vector3i other = this.targetBlock.getChunkPosition();
            v.sendMessage(TextColors.YELLOW + "Second point selected!");
            Vector3i min = other.min(this.pos);
            Vector3i max = other.max(this.pos);
            this.undo = new Undo((max.getX() - min.getX()) * (max.getZ() - min.getZ()) * 16 * 4 * 256);
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    Optional<Chunk> chunk = this.world.getChunk(x, 0, z);
                    if (chunk.isPresent()) {
                        canyon(v, chunk.get());
                    }
                }
            }
            v.owner().storeUndo(this.undo);
            this.undo = null;
            this.worldUid = null;
            this.pos = null;
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        execute(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        execute(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.GREEN + "Shift Level set to " + this.yLevel);
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.canyonselection";
    }
}
