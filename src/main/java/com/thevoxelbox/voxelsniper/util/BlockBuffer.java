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
package com.thevoxelbox.voxelsniper.util;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.schematic.BlockPalette;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;

import java.util.Arrays;

public class BlockBuffer {

    private BlockPalette palette = BlockPaletteTypes.LOCAL.create();

    private final Vector3i min;
    private final Vector3i max;
    private final Vector3i size;
    private char[] buffer;
    private int count;

    public BlockBuffer(Vector3i min, Vector3i max) {
        this.min = min;
        this.max = max;
        this.size = this.max.sub(this.min).add(1, 1, 1);
        this.buffer = new char[this.size.getX() * this.size.getY() * this.size.getZ()];
        Arrays.fill(this.buffer, Character.MAX_VALUE);
    }

    private int index(int x, int y, int z) {
        x -= this.min.getX();
        y -= this.min.getY();
        z -= this.min.getZ();
        return x + y * this.size.getX() + z * this.size.getX() * this.size.getY();
    }

    public Vector3i getMin() {
        return this.min;
    }

    public Vector3i getMax() {
        return this.max;
    }

    public Vector3i getSize() {
        return this.size;
    }

    public boolean contains(int x, int y, int z) {
        if (x < this.min.getX() || x > this.max.getX() || y < this.min.getY() || y > this.max.getY() || z < this.min.getZ() || z > this.max.getZ()) {
            return false;
        }
        return this.buffer[index(x, y, z)] != Character.MAX_VALUE;
    }

    public BlockState get(int x, int y, int z) {
        if (x < this.min.getX() || x > this.max.getX() || y < this.min.getY() || y > this.max.getY() || z < this.min.getZ() || z > this.max.getZ()) {
            throw new IllegalArgumentException(
                    "Expected block buffer position in range " + this.min + " to " + this.max + " but was (" + x + ", " + y + ", " + z + ")");
        }
        char id = this.buffer[index(x, y, z)];
        if (id == Character.MAX_VALUE) {
            return null;
        }
        return this.palette.get(id).get();
    }

    public void set(int x, int y, int z, BlockState state) {
        if (x < this.min.getX() || x > this.max.getX() || y < this.min.getY() || y > this.max.getY() || z < this.min.getZ() || z > this.max.getZ()) {
            throw new IllegalArgumentException(
                    "Expected block buffer position in range " + this.min + " to " + this.max + " but was (" + x + ", " + y + ", " + z + ")");
        }
        if (state == null) {
            if (this.buffer[index(x, y, z)] != Character.MAX_VALUE) {
                this.count--;
                this.buffer[index(x, y, z)] = Character.MAX_VALUE;
            }
            return;
        }
        if (this.buffer[index(x, y, z)] == Character.MAX_VALUE) {
            this.count++;
        }
        int id = this.palette.getOrAssign(state);
        this.buffer[index(x, y, z)] = (char) id;
    }

    public int getBlockCount() {
        return this.count;
    }

}
