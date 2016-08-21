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
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * A tool to assist with redstone creation.
 */
public class VoltMeterBrush extends Brush {

    public VoltMeterBrush() {
        this.setName("VoltMeter");
    }

    // @spongify need to update for block data
    private void data(final SnipeData v) {
//        final Block block = this.clampY(this.getTargetBlock().getX(), this.getTargetBlock().getY(), this.getTargetBlock().getZ());
//        final byte data = block.getData();
//        v.sendMessage(TextColors.AQUA + "Blocks until repeater needed: " + data);
    }

    private void volt(final SnipeData v) {
//        final Block block = this.clampY(this.getTargetBlock().getX(), this.getTargetBlock().getY(), this.getTargetBlock().getZ());
//        final boolean indirect = block.isBlockIndirectlyPowered();
//        final boolean direct = block.isBlockPowered();
//        v.sendMessage(TextColors.AQUA + "Direct Power? " + direct + " Indirect Power? " + indirect);
//        v.sendMessage(TextColors.BLUE + "Top Direct? " + block.isBlockFacePowered(BlockFace.UP) + " Top Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.UP));
//        v.sendMessage(TextColors.BLUE + "Bottom Direct? " + block.isBlockFacePowered(BlockFace.DOWN) + " Bottom Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.DOWN));
//        v.sendMessage(TextColors.BLUE + "East Direct? " + block.isBlockFacePowered(BlockFace.EAST) + " East Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.EAST));
//        v.sendMessage(TextColors.BLUE + "West Direct? " + block.isBlockFacePowered(BlockFace.WEST) + " West Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.WEST));
//        v.sendMessage(TextColors.BLUE + "North Direct? " + block.isBlockFacePowered(BlockFace.NORTH) + " North Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.NORTH));
//        v.sendMessage(TextColors.BLUE + "South Direct? " + block.isBlockFacePowered(BlockFace.SOUTH) + " South Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH));
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.volt(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.data(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.brushMessage("Right click with arrow to see if blocks/faces are powered. Powder measures wire current.");
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.voltmeter";
    }
}
