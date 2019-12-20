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
import com.thevoxelbox.voxelsniper.VoxelSniperConfiguration;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@Brush.BrushInfo(
    name = "Fill Down",
    aliases = {"fd", "filldown"},
    permission = "voxelsniper.brush.filldown",
    category = Brush.BrushCategory.SHAPE
)
public class FillDownBrush extends PerformBrush {

    private boolean fillLiquid = true;
    private boolean fromExisting = false;
    private int verticalLimit;

    public FillDownBrush() {
        this.verticalLimit = VoxelSniperConfiguration.DEFAULT_VOXEL_HEIGHT;
    }

    private void fillDown(SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int tx = targetBlock.getBlockX();
        int tz = targetBlock.getBlockZ();
        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;

        this.undo = new Undo(GenericMath.floor(4 * Math.PI * (brushSize + 1) * (brushSize + 1) * (brushSize + 1) / 3));

        for (int x = minx; x <= maxx; x++) {
            double xs = (tx - x) * (tx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (tz - z) * (tz - z);
                if (xs + zs < brushSizeSquared) {
                    int y = targetBlock.getBlockY();
                    if (this.fromExisting) {
                        for (int y0 = -this.verticalLimit; y0 < this.verticalLimit; y0++) {
                            if (this.world.getBlock(x, y + y0, z) != v.getReplaceState()) {
                                y += y0 - 1;
                                break;
                            }
                        }
                    }
                    for (; y >= 0; y--) {
                        if (this.replace != PerformerType.NONE) {
                            if (!perform(v, x, y, z)) {
                                break;
                            }
                        } else {
                            BlockState current = this.world.getBlock(x, y, z);
                            if (current.getType() == BlockTypes.AIR) {
                                perform(v, x, y, z);
                            } else if (this.fillLiquid) {
                                if (BlockHelper.isLiquid(current)) {
                                    perform(v, x, y, z);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.fillDown(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.fillDown(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (String parameter : par) {
            parameter = parameter.toLowerCase();
            if (parameter.equals("info")) {
                v.sendMessage(TextColors.GOLD, "Fill Down Parameters:");
                v.sendMessage(TextColors.AQUA, "/b fd some -- Fills only into air.");
                v.sendMessage(TextColors.AQUA, "/b fd all -- Fills into liquids as well. (Default)");
                v.sendMessage(TextColors.AQUA, "/b fd -e -- Fills down only from existing blocks. (Toggle)");
                v.sendMessage(TextColors.AQUA, "/b fd r[number] -- Vertical range for checking where to fill down when using -e.  Default 5.");
                return;

            } else if (parameter.equals("all")) {
                this.fillLiquid = true;
                v.sendMessage(TextColors.AQUA, "Now filling liquids as well as air.");

            } else if (parameter.equals("some")) {
                this.fillLiquid = false;
                v.sendMessage(TextColors.AQUA, "Now only filling air.");

            } else if (parameter.equals("-e")) {
                this.fromExisting = !this.fromExisting;
                v.sendMessage(TextColors.AQUA, "Now filling down from " + ((this.fromExisting) ? "existing" : "all") + " blocks.");

            } else if (parameter.startsWith("r")) {
                try {
                    this.verticalLimit = (int) Double.parseDouble(parameter.substring(1));
                    v.sendMessage(TextColors.AQUA, "Vertical range set to ", this.verticalLimit);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid limit given");
                }
            } else {
                v.sendMessage(TextColors.RED, "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }
}
