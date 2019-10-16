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
 * A square disc.
 */
@Brush.BrushInfo(
    name = "Voxel Disc",
    aliases = {"vd", "voxeldisc"},
    permission = "voxelsniper.brush.voxeldisc",
    category = Brush.BrushCategory.SHAPE
)
public class VoxelDiscBrush extends PerformBrush {

    public VoxelDiscBrush() {
    }

    private void disc(final SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();

        int minx = (int) Math.round(targetBlock.getBlockX() - brushSize);
        int maxx = (int) Math.round(targetBlock.getBlockX() + brushSize);
        int minz = (int) Math.round(targetBlock.getBlockZ() - brushSize);
        int maxz = (int) Math.round(targetBlock.getBlockZ() + brushSize);

        this.undo = new Undo(GenericMath.floor(4 * brushSize * brushSize));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            for (int z = minz; z <= maxz; z++) {
                perform(v, x, targetBlock.getBlockY(), z);
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.disc(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.disc(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(info.name());
        vm.size();
    }
}
