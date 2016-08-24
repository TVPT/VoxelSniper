/*
 * This file is part of VoxelSniper, licensed under the MIT License (MIT).
 *
 * Copyright (c) The VoxelBox <http://thevoxelbox.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.thevoxelbox.voxelsniper.brush.chunk;

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
            v.sendMessage(TextColors.YELLOW, "First point selected!");
        } else {
            Vector3i other = this.targetBlock.getChunkPosition();
            v.sendMessage(TextColors.YELLOW, "Second point selected!");
            Vector3i min = other.min(this.pos);
            Vector3i max = other.max(this.pos);
            this.undo = new Undo((max.getX() - min.getX()) * (max.getZ() - min.getZ()) * 16 * 4 * 256);
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    Optional<Chunk> chunk = this.world.getChunk(x, 0, z);
                    if (chunk.isPresent()) {
                        operate(v, chunk.get());
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
