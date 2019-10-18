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
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Creates an ellipse.
 */
@Brush.BrushInfo(
    name = "Ellipse",
    aliases = {"el", "ellipse"},
    permission = "voxelsniper.brush.ellipse",
    category = Brush.BrushCategory.SHAPE
)
public class EllipseBrush extends PerformBrush {

    private double xrad = -1;
    private double yrad = -1;

    public EllipseBrush() {
    }

    private void ellipse(final SnipeData v, Location<World> targetBlock, Direction axis) {
        double xrads = this.xrad * this.xrad;
        double yrads = this.yrad * this.yrad;
        int tx = targetBlock.getBlockX();
        int tz = targetBlock.getBlockZ();
        int minx = GenericMath.floor(targetBlock.getBlockX() - this.xrad);
        int maxx = (int) Math.ceil(targetBlock.getBlockX() + this.xrad);
        int minz = GenericMath.floor(targetBlock.getBlockZ() - this.yrad);
        int maxz = (int) Math.ceil(targetBlock.getBlockZ() + this.yrad);

        this.undo = new Undo(GenericMath.floor(Math.PI * (this.xrad + 1) * (this.yrad + 1)));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (tx - x) * (tx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (tz - z) * (tz - z);
                if (xs / xrads + zs / yrads < 1) {
                    if (axis == Direction.UP) {
                        perform(v, x, targetBlock.getBlockY(), z);
                    } else if (axis == Direction.NORTH) {
                        perform(v, x, z, targetBlock.getBlockZ());
                    } else if (axis == Direction.EAST) {
                        perform(v, targetBlock.getBlockX(), x, z);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    private void pre(final SnipeData v, Location<World> target) {
        if (this.lastBlock.getBlockY() != this.targetBlock.getBlockY()) {
            ellipse(v, target, Direction.UP);
        } else if (this.lastBlock.getBlockX() != this.targetBlock.getBlockX()) {
            ellipse(v, target, Direction.EAST);
        } else if (this.lastBlock.getBlockZ() != this.targetBlock.getBlockZ()) {
            ellipse(v, target, Direction.NORTH);
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
        vm.custom(TextColors.AQUA, "X-radius set to: ", TextColors.DARK_AQUA, this.xrad);
        vm.custom(TextColors.AQUA, "Y-radius set to: ", TextColors.DARK_AQUA, this.yrad);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD, "Ellipse brush parameters");
                v.sendMessage(TextColors.AQUA, "  x[n]: Set X radius to n");
                v.sendMessage(TextColors.AQUA, "  y[n]: Set Y radius to n");
                return;
            } else if (parameter.startsWith("x")) {
                try {
                    double val = Double.parseDouble(parameter.replace("x", ""));
                    if (val <= 0) {
                        v.sendMessage(TextColors.RED, "X radius must be greater than zero.");
                    } else {
                        this.xrad = val;
                        v.sendMessage(TextColors.GREEN, "X radius  set to " + this.xrad);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid X radius value.");
                }
            } else if (parameter.startsWith("y")) {
                try {
                    double val = Double.parseDouble(parameter.replace("y", ""));
                    if (val <= 0) {
                        v.sendMessage(TextColors.RED, "Y radius must be greater than zero.");
                    } else {
                        this.yrad = val;
                        v.sendMessage(TextColors.GREEN, "Y radius  set to " + this.yrad);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid Y radius value.");
                }
            } else {
                v.sendMessage(TextColors.RED, "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
            }
        }
        if (this.xrad <= 0) {
            this.xrad = v.getBrushSize();
            v.sendMessage(TextColors.GREEN, "X radius  set to " + this.xrad);
        }
        if (this.yrad <= 0) {
            this.yrad = v.getBrushSize();
            v.sendMessage(TextColors.GREEN, "Y radius  set to " + this.yrad);
        }
    }
}
