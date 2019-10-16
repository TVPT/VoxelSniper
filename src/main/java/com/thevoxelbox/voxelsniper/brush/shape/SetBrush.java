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
package com.thevoxelbox.voxelsniper.brush.shape;

import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * Fills in a cuboid area between two selected points.
 */
@Brush.BrushInfo(
    name = "Set",
    aliases = {"set"},
    permission = "voxelsniper.brush.set",
    category = Brush.BrushCategory.SHAPE
)
public class SetBrush extends PerformBrush {

    private static final int SELECTION_SIZE_MAX = 5000000;
    private Vector3i block = null;
    private UUID worldUid;

    public SetBrush() {
    }

    private void set(final SnipeData v, final Location<World> bl) {
        if (this.block == null || !bl.getExtent().getUniqueId().equals(this.worldUid)) {
            this.block = bl.getBlockPosition();
            this.worldUid = bl.getExtent().getUniqueId();
            v.sendMessage(TextColors.GRAY, "Point one");
            return;
        }
        final int lowX = (this.block.getX() <= bl.getBlockX()) ? this.block.getX() : bl.getBlockX();
        final int lowY = (this.block.getY() <= bl.getBlockY()) ? this.block.getY() : bl.getBlockY();
        final int lowZ = (this.block.getZ() <= bl.getBlockZ()) ? this.block.getZ() : bl.getBlockZ();
        final int highX = (this.block.getX() >= bl.getBlockX()) ? this.block.getX() : bl.getBlockX();
        final int highY = (this.block.getY() >= bl.getBlockY()) ? this.block.getY() : bl.getBlockY();
        final int highZ = (this.block.getZ() >= bl.getBlockZ()) ? this.block.getZ() : bl.getBlockZ();

        int size = Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY);
        if (size > SELECTION_SIZE_MAX) {
            v.sendMessage(TextColors.RED + "Selection size above hardcoded limit, please use a smaller selection.");
            return;
        }
        this.undo = new Undo(size);
        for (int y = lowY; y <= highY; y++) {
            for (int x = lowX; x <= highX; x++) {
                for (int z = lowZ; z <= highZ; z++) {
                    perform(v, x, y, z);
                }
            }
        }
        v.owner().storeUndo(this.undo);
        this.undo = null;

        this.block = null;
        this.worldUid = null;
        return;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.set(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.set(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(info.name());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        super.parameters(par, v);
    }
}
