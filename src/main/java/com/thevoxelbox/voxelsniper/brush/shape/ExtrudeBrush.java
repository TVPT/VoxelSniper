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
import com.thevoxelbox.voxelsniper.VoxelSniperConfiguration;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.flowpowered.math.GenericMath;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Extrude
 */
@Brush.BrushInfo(
    name = "Extrude",
    aliases = {"ex", "extrude"},
    permission = "voxelsniper.brush.extrude",
    category = Brush.BrushCategory.SHAPE
)
public class ExtrudeBrush extends Brush {

    private int height;

    public ExtrudeBrush() {
        this.height = VoxelSniperConfiguration.DEFAULT_VOXEL_HEIGHT;
    }

    private void extrude(final SnipeData v, Location<World> targetBlock, Direction axis) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;
        // @Safety there is no bounds checks done here
        this.undo = new Undo(GenericMath.floor(Math.PI * (brushSize + 1) * (brushSize + 1) * this.height));
        int size = GenericMath.floor(brushSize) + 1;

        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                if (x * x + z * z < brushSizeSquared) {
                    if (v.getVoxelList().contains(get(x, z, axis, targetBlock))) {
                        for (int y = 0; y < this.height; y++) {
                            set(x, z, axis, targetBlock, v.getVoxelState(), y);
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    private BlockState get(int x, int y, Direction axis, Location<World> target) {
        if (axis == Direction.UP || axis == Direction.DOWN) {
            return this.world.getBlock(x + target.getBlockX(), target.getBlockY(), y + target.getBlockZ());
        } else if (axis == Direction.EAST || axis == Direction.WEST) {
            return this.world.getBlock(target.getBlockX(), x + target.getBlockY(), y + target.getBlockZ());
        }
        return this.world.getBlock(x + target.getBlockX(), y + target.getBlockY(), target.getBlockZ());
    }

    private void set(int x, int y, Direction axis, Location<World> target, BlockState state, int offs) {
        if (axis == Direction.UP || axis == Direction.DOWN) {
            if (axis == Direction.DOWN) {
                offs *= -1;
            }
            setBlockState(x + target.getBlockX(), target.getBlockY() + offs, y + target.getBlockZ(), state);
        } else if (axis == Direction.EAST || axis == Direction.WEST) {
            if (axis == Direction.EAST) {
                offs *= -1;
            }
            setBlockState(target.getBlockX() + offs, x + target.getBlockY(), y + target.getBlockZ(), state);
        } else {
            if (axis == Direction.NORTH) {
                offs *= -1;
            }
            setBlockState(x + target.getBlockX(), y + target.getBlockY(), target.getBlockZ() + offs, state);
        }
    }

    private void pre(final SnipeData v, Location<World> target, boolean towards) {
        if (this.lastBlock.getBlockY() != this.targetBlock.getBlockY()) {
            extrude(v, target, this.lastBlock.getBlockY() > this.targetBlock.getBlockY() ^ towards ? Direction.DOWN : Direction.UP);
        } else if (this.lastBlock.getBlockX() != this.targetBlock.getBlockX()) {
            extrude(v, target, this.lastBlock.getBlockX() > this.targetBlock.getBlockX() ^ towards ? Direction.EAST : Direction.WEST);
        } else if (this.lastBlock.getBlockZ() != this.targetBlock.getBlockZ()) {
            extrude(v, target, this.lastBlock.getBlockZ() > this.targetBlock.getBlockZ() ^ towards ? Direction.NORTH : Direction.SOUTH);
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        pre(v, this.targetBlock, false);
    }

    @Override
    protected final void powder(final SnipeData v) {
        pre(v, this.targetBlock, true);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
        vm.size();
        vm.height(this.height);
        vm.voxelList();
    }

    @Override
    public final void parameters(String[] args, SnipeData v) {
        if (args.length == 1 && args[0].toLowerCase().startsWith("h")) {
            try {
                this.height = Integer.parseInt(args[0].substring(1));
                v.sendMessage(TextColors.AQUA, "Extrude height set to: ", this.height);
            } catch (NumberFormatException e) {
                v.sendMessage(TextColors.RED, "Invalid height given.");
            }
        } else if (args.length > 0) {
            v.sendMessage(TextColors.GOLD, "Extrude Brush Parameters:");
            v.sendMessage(TextColors.AQUA, "/b ex h[number] -- set the extrude height.  Default is 1.");
        }
    }
}
