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
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@Brush.BrushInfo(
    name = "Checker Voxel Disc",
    aliases = {"cvd", "checkervoxeldissc"},
    permission = "voxelsniper.brush.checkervoxeldisc",
    category = Brush.BrushCategory.SHAPE
)
public class CheckerVoxelDiscBrush extends PerformBrush {

    private boolean useWorldCoordinates = true;

    public CheckerVoxelDiscBrush() {
    }

    private void applyBrush(SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();

        int tx = targetBlock.getBlockX();
        int tz = targetBlock.getBlockZ();
        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;

        this.undo = new Undo(GenericMath.floor(Math.PI * (brushSize + 1) * (brushSize + 1)));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            for (int z = minz; z <= maxz; z++) {
                final int sum = this.useWorldCoordinates ? x + z : x - tx + z - tz;
                if (sum % 2 != 0) {
                    perform(v, x, targetBlock.getBlockY(), z);
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.applyBrush(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.applyBrush(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(info.name());
        vm.size();
    }

    @Override
    public void parameters(final String[] par, final SnipeData v) {
        if (par.length == 0) {
            return;
        }
        if (par[0].equals("info")) {
            v.sendMessage(TextColors.GOLD, info.name() + " Parameters:");
            v.sendMessage(TextColors.AQUA, "true  -- Enables using World Coordinates.");
            v.sendMessage(TextColors.AQUA, "false -- Disables using World Coordinates.");
            return;
        }
        if (par[0].startsWith("true")) {
            this.useWorldCoordinates = true;
            v.sendMessage(TextColors.AQUA, "Enabled using World Coordinates.");
        } else if (par[0].startsWith("false")) {
            this.useWorldCoordinates = false;
            v.sendMessage(TextColors.AQUA, "Disabled using World Coordinates.");
        } else {
            v.sendMessage(TextColors.RED, "Invalid brush parameters! use the info parameter to display parameter info.");
        }
    }
}
