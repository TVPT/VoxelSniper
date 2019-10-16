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
 * A brush that creates a solid ball.
 */
@Brush.BrushInfo(
    name = "Ball",
    aliases = {"b", "ball"},
    permission = "voxelsniper.brush.ball",
    category = Brush.BrushCategory.SHAPE
)
public class BallBrush extends PerformBrush {

    public BallBrush() {
    }

    private void ball(SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int tx = targetBlock.getBlockX();
        int ty = targetBlock.getBlockY();
        int tz = targetBlock.getBlockZ();
        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int miny = Math.max(GenericMath.floor(targetBlock.getBlockY() - brushSize), 0);
        int maxy = Math.min(GenericMath.floor(targetBlock.getBlockY() + brushSize) + 1, WORLD_HEIGHT);
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;

        // Approximate the size of the undo to the volume of a one larger sphere
        this.undo = new Undo(GenericMath.floor(4 * Math.PI * (brushSize + 1) * (brushSize + 1) * (brushSize + 1) / 3));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = maxx; x >= minx; x--) {
            double xs = (tx - x) * (tx - x);
            for (int y = maxy; y >= miny; y--) {
                double ys = (ty - y) * (ty - y);
                for (int z = maxz; z >= minz; z--) {
                    double zs = (tz - z) * (tz - z);
                    if (xs + ys + zs < brushSizeSquared) {
                        perform(v, x, y, z);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected void arrow(SnipeData v) {
        this.ball(v, this.targetBlock);
    }

    @Override
    protected void powder(SnipeData v) {
        this.ball(v, this.lastBlock);
    }

    @Override
    public void info(final Message vm) {
        vm.brushName(info.name());
        vm.size();
    }
}
