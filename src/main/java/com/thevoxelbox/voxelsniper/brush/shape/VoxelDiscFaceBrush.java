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

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import com.flowpowered.math.GenericMath;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Places a disc aligned to the face of the block that you target.
 */
@Brush.BrushInfo(
    name = "Voxel Disc Face",
    aliases = {"vdf", "voxeldiscface"},
    permission = "voxelsniper.brush.voxeldiscface",
    category = Brush.BrushCategory.SHAPE
)
public class VoxelDiscFaceBrush extends PerformBrush {

    public VoxelDiscFaceBrush() {
    }

    private void disc(final SnipeData v, Location<World> targetBlock, Direction axis) {
        int brushSize = GenericMath.floor(v.getBrushSize());

        int tx = targetBlock.getBlockX();
        int ty = targetBlock.getBlockY();
        int tz = targetBlock.getBlockZ();

        this.undo = new Undo((brushSize * 2 + 1) * (brushSize * 2 + 1));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = -brushSize; x <= brushSize; x++) {
            for (int z = -brushSize; z <= brushSize; z++) {
                if (axis == Direction.UP) {
                    perform(v, tx + x, ty, tz + z);
                } else if (axis == Direction.NORTH) {
                    perform(v, tx + x, ty + z, tz);
                } else if (axis == Direction.EAST) {
                    perform(v, tx, ty + x, tz + z);
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    private void pre(final SnipeData v, Location<World> target) {
        if (this.lastBlock.getBlockY() != this.targetBlock.getBlockY()) {
            disc(v, target, Direction.UP);
        } else if (this.lastBlock.getBlockX() != this.targetBlock.getBlockX()) {
            disc(v, target, Direction.EAST);
        } else if (this.lastBlock.getBlockZ() != this.targetBlock.getBlockZ()) {
            disc(v, target, Direction.NORTH);
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.pre(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.pre(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
        vm.size();
    }
}
