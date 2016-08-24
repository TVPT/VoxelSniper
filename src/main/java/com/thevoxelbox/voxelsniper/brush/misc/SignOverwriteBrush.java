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

import com.flowpowered.math.GenericMath;
import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

/**
 * Overwrites signs.
 */
public class SignOverwriteBrush extends Brush {

    private List<Text> signTextLines = Lists.newArrayList(Text.of(), Text.of(), Text.of(), Text.of());

    public SignOverwriteBrush() {
        this.setName("Sign Overwrite Brush");
    }

    private void setRanged(final SnipeData v, Location<World> targetBlock) {
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

        boolean signFound = false;
        for (int x = minx; x <= maxx; x++) {
            double xs = (tx - x) * (tx - x);
            for (int y = miny; y <= maxy; y++) {
                double ys = (ty - y) * (ty - y);
                for (int z = minz; z <= maxz; z++) {
                    double zs = (tz - z) * (tz - z);
                    if (xs + ys + zs < brushSizeSquared) {
                        BlockState block = this.world.getBlock(x, y, z);
                        if (block.getType() == BlockTypes.STANDING_SIGN || block.getType() == BlockTypes.WALL_SIGN) {
                            Optional<TileEntity> te = this.world.getTileEntity(x, y, z);
                            if (te.isPresent()) {
                                Sign sign = (Sign) te.get();
                                SignData data = sign.getSignData();
                                data.set(Keys.SIGN_LINES, this.signTextLines);
                                sign.offer(data);
                                signFound = true;
                            }
                        }
                    }
                }
            }
        }

        if (!signFound) {
            v.sendMessage(TextColors.RED + "Did not found any sign in selection box.");
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        setRanged(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        BlockState block = this.targetBlock.getBlock();
        if (block.getType() == BlockTypes.STANDING_SIGN || block.getType() == BlockTypes.WALL_SIGN) {
            Optional<TileEntity> te = this.targetBlock.getTileEntity();
            if (te.isPresent()) {
                Sign sign = (Sign) te.get();
                SignData data = sign.getSignData();
                this.signTextLines = data.asList();
            }
            displayBuffer(v);
        } else {
            v.sendMessage(TextColors.RED + "Target block is not a sign.");
        }
    }

    private void displayBuffer(final SnipeData v) {
        v.sendMessage(TextColors.BLUE + "Buffer text set to: ");
        for (int i = 0; i < this.signTextLines.size(); i++) {
            v.sendMessage(i + ": ", this.signTextLines.get(i));
        }
    }

    @Override
    public final void info(final Message vm) {
        vm.custom(TextColors.AQUA + "Sign Overwrite Brush Powder/Arrow:");
        vm.custom(TextColors.BLUE + "The arrow writes the internal line buffer to the tearget sign.");
        vm.custom(TextColors.BLUE + "The powder reads the text of the target sign into the internal buffer.");
        vm.custom(TextColors.BLUE + "Buffer text set to: ");
        for (int i = 0; i < this.signTextLines.size(); i++) {
            vm.custom(i + ": ", this.signTextLines.get(i));
        }
        vm.size();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.signoverwrite";
    }
}
