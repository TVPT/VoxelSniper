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
package com.thevoxelbox.voxelsniper.brush.insn;

import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.brush.BrushInfo;
import com.thevoxelbox.voxelsniper.brush.ModeBrush;
import com.thevoxelbox.voxelsniper.player.PlayerData;

import com.flowpowered.math.GenericMath;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@BrushInfo(alias = { "ball", "b" })
public class BallBrush extends ModeBrush<BallBrush> {

    public BallBrush() {

    }

    @Override
    public String getName() {
        return "Ball";
    }

    @Override
    public BallBrush consumeArgs(String args) {
        return new BallBrush();
    }

    @Override
    public void execute(PlayerData data, Location<World> target) {
        double radius = data.getBrushSize();
        double radiusSquared = radius * radius;
        Cause cause = Cause.of(VoxelSniper.plugin_cause, NamedCause.source(data.getPlayer()));
        for (int x = -GenericMath.floor(radius); x <= GenericMath.floor(radius) + 1; x++) {
            int ox = target.getBlockX() + x;
            for (int y = -GenericMath.floor(radius); y <= GenericMath.floor(radius) + 1; y++) {
                int oy = target.getBlockY() + y;
                for (int z = -GenericMath.floor(radius); z <= GenericMath.floor(radius) + 1; z++) {
                    int oz = target.getBlockZ() + z;
                    if (x * x + y * y + z * z < radiusSquared) {
                        perform(data, target.getExtent(), ox, oy, oz, cause);
                    }
                }
            }
        }
    }

}
