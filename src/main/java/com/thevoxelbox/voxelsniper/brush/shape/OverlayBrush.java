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

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Overlays the terrain with blocks.
 */
public class OverlayBrush extends PerformBrush {

    private static final int DEFAULT_DEPTH = 3;
    private int depth = DEFAULT_DEPTH;

    // @Spongify
    public OverlayBrush() {
        this.setName("Overlay (Topsoil Filling)");
    }

    private void overlay(SnipeData v, Location<World> targetBlock, int offset) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;
        
        int tx = this.targetBlock.getBlockX();
        int tz = this.targetBlock.getBlockZ();

        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;

        this.undo = new Undo(GenericMath.floor(Math.PI * (brushSize + 1) * (brushSize + 1)));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (tx - x) * (tx - x);
            outer:
            for (int z = minz; z <= maxz; z++) {
                double zs = (tz - z) * (tz - z);
                if (xs + zs < brushSizeSquared) {
                    int y = Math.min(targetBlock.getBlockY() + offset, WORLD_HEIGHT);
                    for (; y >= 0; y--) {
                        if (this.world.getBlockType(x, y, z) != BlockTypes.AIR) {
                            break;
                        }
                    }
                    if (y == targetBlock.getBlockY() && y < WORLD_HEIGHT) {
                        if (this.world.getBlockType(x, y + 1, z) != BlockTypes.AIR) {
                            // if theres no air above our start block then don't
                            // perform
                            continue outer;
                        }
                    }
                    for (int y0 = Math.min(y + offset, WORLD_HEIGHT); y0 > y - this.depth; y0--) {
                        if(y0 <= y && this.world.getBlockType(x, y0, z) == BlockTypes.AIR) {
                            break;
                        }
                        perform(v, x, y0, z);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;

    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.overlay(v, this.targetBlock, 0);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.overlay(v, this.lastBlock, 1);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(TextColors.AQUA, "Depth set to " + this.depth);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length > 0) {
            if (par[0].equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD, "Overlay brush parameters:");
                v.sendMessage(TextColors.AQUA, "d[number] (ex:  d3) How many blocks deep you want to replace from the surface.");
                return;
            }
            if (par[0].startsWith("d")) {
                try {
                    this.depth = Integer.parseInt(par[0].replace("d", ""));

                    if (this.depth < 1) {
                        this.depth = 1;
                    }

                    v.sendMessage(TextColors.AQUA, "Depth set to " + this.depth);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Depth isn't a number.");
                }
            } else {
                v.sendMessage(TextColors.RED, "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.overlay";
    }
}
