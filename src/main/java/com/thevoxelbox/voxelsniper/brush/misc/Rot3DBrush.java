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

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.util.BlockBuffer;
import com.thevoxelbox.voxelsniper.util.Rot3d;

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@Brush.BrushInfo(
    name = "3D Rotation",
    aliases = {"rot3", "rotation3d", "rot", "rotation"},
    permission = "voxelsniper.brush.rot3d",
    category = Brush.BrushCategory.MISC
)
public class Rot3DBrush extends Brush {

    private double yaw;
    private double pitch;
    private double roll;

    private Rot3d rotUtil;

    public Rot3DBrush() {
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(info.name());
        vm.brushMessage("Rotates a spherical area by a given yaw, pitch, and roll");
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        boolean changed = false;
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];
            // which way is clockwise is less obvious for roll and pitch...
            // should probably fix that / make it clear
            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD, "Rotate brush Parameters:");
                v.sendMessage(TextColors.AQUA, "p[0-359] -- set degrees of pitch rotation (rotation about the Z axis).");
                v.sendMessage(TextColors.BLUE, "r[0-359] -- set degrees of roll rotation (rotation about the X axis).");
                v.sendMessage(TextColors.LIGHT_PURPLE, "y[0-359] -- set degrees of yaw rotation (Rotation about the Y axis).");

                return;
            } else if (parameter.startsWith("p")) {
                try {
                    if (this.pitch < 0 || this.pitch > 359) {
                        v.sendMessage(TextColors.RED + "Invalid brush parameters! Angles must be from 1-359");
                    } else {
                        this.pitch = Math.toRadians(Double.parseDouble(parameter.replace("p", "")));
                        v.sendMessage(TextColors.AQUA + "Around Z-axis degrees set to " + this.pitch);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid pitch given.");
                }
            } else if (parameter.startsWith("r")) {
                try {
                    if (this.roll < 0 || this.roll > 359) {
                        v.sendMessage(TextColors.RED + "Invalid brush parameters! Angles must be from 1-359");
                    } else {
                        this.roll = Math.toRadians(Double.parseDouble(parameter.replace("r", "")));
                        v.sendMessage(TextColors.AQUA + "Around X-axis degrees set to " + this.roll);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid roll given.");
                }
            } else if (parameter.startsWith("y")) {
                try {
                    if (this.yaw < 0 || this.yaw > 359) {
                        v.sendMessage(TextColors.RED + "Invalid brush parameters! Angles must be from 1-359");
                    } else {
                        this.yaw = Math.toRadians(Double.parseDouble(parameter.replace("y", "")));
                        v.sendMessage(TextColors.AQUA + "Around Y-axis degrees set to " + this.yaw);
                    }
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid yaw given.");
                }
            }
        }
        if (this.rotUtil == null || changed) {
            this.rotUtil = new Rot3d(this.yaw, this.pitch, this.roll);
        }
    }

    private void rotate(final SnipeData v, Location<World> targetBlock) {
        int brushSize = GenericMath.floor(v.getBrushSize()) + 1;
        double brushSizeSquared = brushSize * brushSize;

        int tx = targetBlock.getBlockX();
        int ty = targetBlock.getBlockY();
        int tz = targetBlock.getBlockZ();

        BlockBuffer buffer = new BlockBuffer(new Vector3i(-brushSize, -brushSize, -brushSize), new Vector3i(brushSize, brushSize, brushSize));

        for (int x = -brushSize; x <= brushSize; x++) {
            int x0 = tx + x;
            for (int y = -brushSize; y <= brushSize; y++) {
                int y0 = ty + y;
                for (int z = -brushSize; z <= brushSize; z++) {
                    int z0 = tz + z;
                    if (x * x + y * y + z * z >= brushSizeSquared) {
                        continue;
                    }
                    Vector3d rot = this.rotUtil.rotate(x, y, z);
                    buffer.set(x + rot.getFloorX(), y + rot.getFloorY(), z + rot.getFloorZ(), this.world.getBlock(x0, y0, z0));
                }
            }
        }

        this.undo = new Undo(buffer.getBlockCount());
        // apply the buffer to the world
        for (int x = -brushSize; x <= brushSize; x++) {
            int x0 = x + tx;
            for (int y = -brushSize; y <= brushSize; y++) {
                int y0 = y + ty;
                for (int z = -brushSize; z <= brushSize; z++) {
                    int z0 = z + tz;
                    if (buffer.contains(x, y, z)) {
                        setBlockState(x0, y0, z0, buffer.get(x, y, z));
                    }
                }
            }
        }
        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        rotate(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        rotate(v, this.lastBlock);
    }
}
