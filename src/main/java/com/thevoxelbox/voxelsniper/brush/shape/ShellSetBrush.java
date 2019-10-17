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

import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.util.BlockBuffer;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class ShellSetBrush extends Brush {

    private Vector3i pos1;
    private UUID worldUid;

    public ShellSetBrush() {
        this.setName("Shell Set");
    }

    private void sShell(final SnipeData v, Vector3i pos1, Vector3i pos2) {
        int minx = Math.min(pos1.getX(), pos2.getX());
        int miny = Math.min(pos1.getY(), pos2.getY());
        int minz = Math.min(pos1.getZ(), pos2.getZ());
        int maxx = Math.max(pos1.getX(), pos2.getX());
        int maxy = Math.max(pos1.getY(), pos2.getY());
        int maxz = Math.max(pos1.getZ(), pos2.getZ());

        BlockBuffer buffer = new BlockBuffer(new Vector3i(minx, miny, minz), new Vector3i(maxx, maxy, maxz));

        for (int x = minx; x <= maxx; x++) {
            for (int y = miny; y <= maxy; y++) {
                for (int z = minz; z <= maxz; z++) {
                    if (y <= 0 || y >= WORLD_HEIGHT) {
                        continue;
                    }
                    if (this.world.getBlock(x, y, z) != v.getReplaceState()) {
                        continue;
                    }
                    int blocks = 0;
                    if (this.world.getBlock(x + 1, y, z) == v.getReplaceState()) {
                        blocks++;
                    }
                    if (this.world.getBlock(x - 1, y, z) == v.getReplaceState()) {
                        blocks++;
                    }
                    if (this.world.getBlock(x, y + 1, z) == v.getReplaceState()) {
                        blocks++;
                    }
                    if (this.world.getBlock(x, y - 1, z) == v.getReplaceState()) {
                        blocks++;
                    }
                    if (this.world.getBlock(x, y, z + 1) == v.getReplaceState()) {
                        blocks++;
                    }
                    if (this.world.getBlock(x, y, z - 1) == v.getReplaceState()) {
                        blocks++;
                    }
                    if (blocks == 6) {
                        buffer.set(x, y, z, v.getVoxelState());
                    }
                }
            }
        }

        this.undo = new Undo(buffer.getBlockCount());
        // apply the buffer to the world
        for (int x = minx; x <= maxx; x++) {
            for (int y = miny; y <= maxy; y++) {
                for (int z = minz; z <= maxz; z++) {
                    if (buffer.contains(x, y, z)) {
                        setBlockState(x, y, z, buffer.get(x, y, z));
                    }
                }
            }
        }
        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    private void pos(SnipeData v, Location<World> target) {
        if (this.worldUid == null || !target.getExtent().getUniqueId().equals(this.worldUid)) {
            this.pos1 = target.getBlockPosition();
            v.sendMessage(TextColors.GRAY, "First point set.");
        } else {
            sShell(v, target.getBlockPosition(), this.pos1);
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        pos(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        pos(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.replace();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.shellset";
    }
}
