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
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Same as overlay but for the bottom of blocks.
 */
@Brush.BrushInfo(
    name = "Underlay",
    aliases = {"under", "underlay"},
    permission = "voxelsniper.brush.underlay",
    category = Brush.BrushCategory.SHAPE
)
public class UnderlayBrush extends PerformBrush {

    // @Cleanup move to config
    private static final int DEFAULT_DEPTH = 3;
    private int depth = DEFAULT_DEPTH;

    public UnderlayBrush() {
    }

    private void underlay(SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;

        this.undo = new Undo(GenericMath.floor(Math.PI * (brushSize + 1) * (brushSize + 1)));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (minx - x) * (minx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (minz - z) * (minz - z);
                if (xs + zs < brushSizeSquared) {
                    int y = targetBlock.getBlockY();
                    for (; y <= Brush.WORLD_HEIGHT; y++) {
                        if (this.world.getBlockType(x, y, z) != BlockTypes.AIR) {
                            break;
                        }
                    }
                    if (y == targetBlock.getBlockY() && y > 0) {
                        if (this.world.getBlockType(x, y - 1, z) != BlockTypes.AIR) {
                            // if theres no air below our start block then don't
                            // perform
                            continue;
                        }
                    }
                    for (int y0 = y; y0 < y + this.depth; y0++) {
                        if (this.world.getBlockType(x, y0, z) != BlockTypes.AIR) {
                            perform(v, x, y0, z);
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;

    }

    @Override
    public final void arrow(final SnipeData v) {
        this.underlay(v, this.targetBlock);
    }

    @Override
    public final void powder(final SnipeData v) {
        this.underlay(v, this.lastBlock);
        // @Feature the gunpowder should stack one block higher than the
        // existing land
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(info.name());
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length < 0) {
            v.sendMessage(TextColors.AQUA, "Usage: /b reover d[#]");
            return;
        }
        if (par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD, "Reverse Overlay brush parameters:");
            v.sendMessage(TextColors.AQUA, "d[number] (ex: d3) The number of blocks thick to change.");
            return;
        }
        if (par[0].startsWith("d")) {
            this.depth = Integer.parseInt(par[0].replace("d", ""));
            if (this.depth < 1) {
                this.depth = 1;
            }
            v.sendMessage(TextColors.AQUA, "Depth set to " + this.depth);
        } else {
            v.sendMessage(TextColors.RED, "Invalid parameter '" + par[0] + "'");
            v.sendMessage(TextColors.AQUA, "Usage: /b reover d[#]");
        }
    }
}
