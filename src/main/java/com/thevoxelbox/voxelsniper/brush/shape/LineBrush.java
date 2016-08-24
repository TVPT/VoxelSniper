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

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * Creates a line
 */
public class LineBrush extends PerformBrush {

    private boolean continuous = false;
    private Vector3d origin;
    private UUID worldUid;

    public LineBrush() {
        this.setName("Line");
    }

    private void linePowder(final SnipeData v) {
        Vector3d target = this.targetBlock.getBlockPosition().toDouble().add(0.5, 0.5, 0.5);
        Vector3d dir = target.sub(this.origin);
        double dist = target.distance(this.origin);
        this.undo = new Undo((int) (dist + 2));

        BlockRay<World> ray = BlockRay.from(this.world, this.origin).filter(BlockRay.maxDistanceFilter(this.origin, dist)).direction(dir).build();
        perform(v, this.origin.getFloorX(), this.origin.getFloorY(), this.origin.getFloorZ());
        while (ray.hasNext()) {
            Vector3i pos = ray.next().getBlockPosition();
            perform(v, pos.getX(), pos.getY(), pos.getZ());
        }

        if (this.continuous) {
            this.origin = target;
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.worldUid = this.world.getUniqueId();
        this.origin = this.targetBlock.getBlockPosition().toDouble().add(0.5, 0.5, 0.5);
        v.sendMessage(TextColors.DARK_PURPLE, "First point selected.");
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.origin == null || !this.world.getUniqueId().equals(this.worldUid)) {
            v.sendMessage(TextColors.RED, "Warning: You did not select a first coordinate with the arrow");
        } else {
            linePowder(v);
        }
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length == 0 || par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD,
                    "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
            v.sendMessage(TextColors.GOLD + "Line brush Parameters:");
            v.sendMessage(TextColors.AQUA + "/b line continue -- Each line will be drawn from the endpoint of the last line.");
            return;
        }
        this.continuous = false;
        if (par[0].equalsIgnoreCase("continue")) {
            this.continuous = true;
            v.sendMessage(TextColors.AQUA, "Line brush Continuous mode enabled.");
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.line";
    }
}
