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
package com.thevoxelbox.voxelsniper.brush.misc;

import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Measures the length between two positions.
 */
@Brush.BrushInfo(
    name = "Ruler",
    aliases = {"r", "ruler"},
    permission = "voxelsniper.brush.ruler",
    category = Brush.BrushCategory.MISC
)
public class RulerBrush extends Brush {

    private Vector3i pos;

    private int xOff = 0;
    private int yOff = 0;
    private int zOff = 0;

    public RulerBrush() {
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.pos == null) {
            v.sendMessage(TextColors.DARK_PURPLE + "First point selected.");
            this.pos = this.targetBlock.getBlockPosition();
        } else {
            final Undo undo = new Undo(1);
            Location<World> target = this.targetBlock.add(this.xOff, this.yOff, this.zOff);
            undo.put(target);
            target.setBlock(v.getVoxelIdState());
            v.owner().storeUndo(undo);
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.pos == null) {
            v.sendMessage(TextColors.RED + "Select a first point with the arrow.");
            return;
        }
        v.sendMessage(TextColors.AQUA + "X change: " + (this.targetBlock.getX() - this.pos.getX()));
        v.sendMessage(TextColors.AQUA + "Y change: " + (this.targetBlock.getY() - this.pos.getY()));
        v.sendMessage(TextColors.AQUA + "Z change: " + (this.targetBlock.getZ() - this.pos.getZ()));
        final double distance = this.targetBlock.getBlockPosition().sub(this.pos).length();
        v.sendMessage(TextColors.AQUA + "Distance = " + distance);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
        vm.voxel();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD,
                        "Ruler Brush instructions: Right click first point with the arrow. Right click with powder for distances from that block (can repeat without getting a new first block.) For placing blocks, use arrow and input the desired coordinates with parameters.");
                v.sendMessage(TextColors.LIGHT_PURPLE,
                        "/b r x[x value] y[y value] z[z value] -- Will place blocks one at a time of the type you have set with /v at the location you click + this many units away.  If you don't include a value, it will be zero.  Don't include ANY values, and the brush will just measure distance.");
                v.sendMessage(TextColors.BLUE, "/b r ruler -- will reset the tool to just measure distances, not layout blocks.");

                return;
            } else if (parameter.startsWith("x")) {
                this.xOff = Integer.parseInt(parameter.replace("x", ""));
                v.sendMessage(TextColors.AQUA, "X offset set to " + this.xOff);
            } else if (parameter.startsWith("y")) {
                this.yOff = Integer.parseInt(parameter.replace("y", ""));
                v.sendMessage(TextColors.AQUA, "Y offset set to " + this.yOff);
            } else if (parameter.startsWith("z")) {
                this.zOff = Integer.parseInt(parameter.replace("z", ""));
                v.sendMessage(TextColors.AQUA, "Z offset set to " + this.zOff);
            } else if (parameter.startsWith("ruler")) {
                this.zOff = 0;
                this.yOff = 0;
                this.xOff = 0;
                v.sendMessage(TextColors.BLUE, "Ruler mode.");
            } else {
                v.sendMessage(TextColors.RED, "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }
}
