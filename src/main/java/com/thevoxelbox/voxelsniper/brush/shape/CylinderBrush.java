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
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.VoxelSniperConfiguration;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Creates cylinders.
 */
@Brush.BrushInfo(
    name = "Cylinder",
    aliases = {"c", "cylinder"},
    permission = "voxelsniper.brush.cylinder",
    category = Brush.BrushCategory.SHAPE
)
public class CylinderBrush extends PerformBrush {

    private int height;
    private int offset;

    public CylinderBrush() {
        this.height = VoxelSniperConfiguration.DEFAULT_VOXEL_HEIGHT;
        this.offset = VoxelSniperConfiguration.DEFAULT_CYLINDER_CENTER;
    }

    private void cylinder(SnipeData v, Location<World> targetBlock) {
        int yStartingPoint = targetBlock.getBlockY() + this.offset;
        int yEndPoint = yStartingPoint + this.height - 1;

        if (yEndPoint < yStartingPoint) {
            yEndPoint = yStartingPoint;
        }
        if (yStartingPoint < 0) {
            yStartingPoint = 0;
        } else if (yStartingPoint > WORLD_HEIGHT) {
            yStartingPoint = WORLD_HEIGHT;
        }
        if (yEndPoint < 0) {
            yEndPoint = 0;
        } else if (yEndPoint > WORLD_HEIGHT) {
            yEndPoint = WORLD_HEIGHT;
        }

        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

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
            double xs = (tx - x) * (tx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (tz - z) * (tz - z);
                if (xs + zs < brushSizeSquared) {
                    for (int y = yEndPoint; y >= yStartingPoint; y--) {
                        perform(v, x, y, z);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        cylinder(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        cylinder(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
        vm.size();
        vm.height(this.height);
        vm.custom(TextColors.DARK_BLUE, "Brush Offset: ", TextColors.DARK_RED, offset);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD, "Cylinder Brush Parameters:");
                v.sendMessage(TextColors.AQUA, "/b c h[number] -- set the cylinder height.  Default is 1.");
                v.sendMessage(TextColors.DARK_BLUE,
                                "/b c o[number] -- set the vertical offset of the cylinder compared to the target ",
                                "block. Positive numbers will move the cylinder upward, negative will move it ",
                                "downward. Default is 0.");
                return;
            }
            if (parameter.startsWith("h")) {
                try {
                    this.height = Integer.parseInt(parameter.substring(1));
                    v.sendMessage(TextColors.AQUA, "Cylinder height set to: ", this.height);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid height given.");
                }
            } else if (parameter.startsWith("o")) {
                try {
                    this.offset = (int) Double.parseDouble(parameter.substring(1));
                    v.sendMessage(TextColors.AQUA, "Cylinder origin set to: " + this.offset);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid origin given.");
                }
            } else {
                v.sendMessage(TextColors.RED, "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }
}
