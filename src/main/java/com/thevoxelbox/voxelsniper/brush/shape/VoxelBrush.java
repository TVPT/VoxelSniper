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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A cuboid shape.
 */
@Brush.BrushInfo(
    name = "Voxel",
    aliases = {"v", "voxel"},
    permission = "voxelsniper.brush.voxel",
    category = Brush.BrushCategory.SHAPE
)
public class VoxelBrush extends PerformBrush {

    public VoxelBrush() {
    }

    private void voxel(final SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();

        int minx = (int) Math.round(targetBlock.getBlockX() - brushSize);
        int maxx = (int) Math.round(targetBlock.getBlockX() + brushSize);
        int miny = Math.max((int) Math.round(targetBlock.getBlockY() - brushSize), 0);
        int maxy = Math.min((int) Math.round(targetBlock.getBlockY() + brushSize), WORLD_HEIGHT);
        int minz = (int) Math.round(targetBlock.getBlockZ() - brushSize);
        int maxz = (int) Math.round(targetBlock.getBlockZ() + brushSize);

        this.undo = new Undo(GenericMath.floor(8 * (brushSize + 1) * (brushSize + 1) * (brushSize + 1)));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            for (int y = miny; y <= maxy; y++) {
                for (int z = minz; z <= maxz; z++) {
                    perform(v, x, y, z);
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.voxel(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.voxel(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
        vm.size();
    }
}
