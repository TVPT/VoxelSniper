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
package com.thevoxelbox.voxelsniper.brush.terrain;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.util.BlockBuffer;
import com.thevoxelbox.voxelsniper.util.BlockHelper;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Map;

public class ErodeBrush extends Brush {

    private static final Vector3i[] FACES_TO_CHECK = {new Vector3i(0, -1, 0), new Vector3i(0, 1, 0), new Vector3i(0, 0, 1), new Vector3i(0, 0, -1),
            new Vector3i(1, 0, 0), new Vector3i(-1, 0, 0)};

    private ErosionParameters currentPreset = Preset.MELT.getParameters();

    public ErodeBrush() {
        this.setName("Erode");
    }

    protected void erosion(SnipeData v, int erodeFaces, int erodeRec, int fillFaces, int fillRec) {
        int brushSize = (int) Math.ceil(v.getBrushSize());
        double brushSizeSquared = v.getBrushSize() * v.getBrushSize();

        int tx = this.targetBlock.getBlockX();
        int ty = this.targetBlock.getBlockY();
        int tz = this.targetBlock.getBlockZ();

        // @Safety there is no bounds checks done here

        // writes cycle back and fourth between these two buffers to prevent
        // bleeding between iterations
        BlockBuffer buffer1 = new BlockBuffer(new Vector3i(-brushSize - 1, -brushSize - 1, -brushSize - 1),
                new Vector3i(brushSize + 1, brushSize + 1, brushSize + 1));
        BlockBuffer buffer2 = new BlockBuffer(new Vector3i(-brushSize - 1, -brushSize - 1, -brushSize - 1),
                new Vector3i(brushSize + 1, brushSize + 1, brushSize + 1));

        for (int x = -brushSize - 1; x <= brushSize + 1; x++) {
            int x0 = x + tx;
            for (int y = -brushSize - 1; y <= brushSize + 1; y++) {
                int y0 = y + ty;
                for (int z = -brushSize - 1; z <= brushSize + 1; z++) {
                    int z0 = z + tz;
                    BlockState state = this.world.getBlock(x0, y0, z0);
                    buffer1.set(x, y, z, state);
                    buffer2.set(x, y, z, state);
                }
            }
        }
        int swap = 0;
        for (int i = 0; i < erodeRec; ++i) {
            erosionIteration(v, erodeFaces, swap % 2 == 0 ? buffer1 : buffer2, swap % 2 == 1 ? buffer1 : buffer2);
            swap++;
        }

        for (int i = 0; i < fillRec; ++i) {
            fillIteration(v, fillFaces, swap % 2 == 0 ? buffer1 : buffer2, swap % 2 == 1 ? buffer1 : buffer2);
            swap++;
        }
        BlockBuffer finalBuffer = swap % 2 == 0 ? buffer1 : buffer2;
        this.undo = new Undo(finalBuffer.getBlockCount());
        // apply the buffer to the world
        for (int x = -brushSize; x <= brushSize; x++) {
            int x0 = x + tx;
            for (int y = -brushSize; y <= brushSize; y++) {
                int y0 = y + ty;
                for (int z = -brushSize; z <= brushSize; z++) {
                    int z0 = z + tz;
                    if (x * x + y * y + z * z <= brushSizeSquared && finalBuffer.contains(x, y, z)) {
                        setBlockState(x0, y0, z0, finalBuffer.get(x, y, z));
                    }
                }
            }
        }
        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    private void fillIteration(SnipeData v, int fillFaces, BlockBuffer current, BlockBuffer target) {
        int brushSize = (int) v.getBrushSize() + 1;
        double brushSizeSquared = v.getBrushSize() * v.getBrushSize();
        Map<BlockState, Integer> frequency = Maps.newHashMap();

        for (int x = -brushSize; x <= brushSize; x++) {
            for (int y = -brushSize; y <= brushSize; y++) {
                for (int z = -brushSize; z <= brushSize; z++) {
                    target.set(x, y, z, current.get(x, y, z));
                    if (x * x + y * y + z * z >= brushSizeSquared) {
                        continue;
                    }
                    BlockState state = current.get(x, y, z);
                    if (!BlockHelper.isLiquidOrGas(state)) {
                        continue;
                    }
                    int total = 0;
                    int highest = 1;
                    BlockState highestState = state;
                    frequency.clear();
                    for (Vector3i offs : FACES_TO_CHECK) {
                        BlockState next = current.get(x + offs.getX(), y + offs.getY(), z + offs.getZ());
                        if (BlockHelper.isLiquidOrGas(next)) {
                            continue;
                        }
                        total++;
                        Integer count = frequency.get(next);
                        if (count == null) {
                            count = 1;
                        } else {
                            count++;
                        }
                        if (count > highest) {
                            highest = count;
                            highestState = next;
                        }
                        frequency.put(next, count);
                    }
                    if (total >= fillFaces) {
                        target.set(x, y, z, highestState);
                    }
                }
            }
        }
    }

    private void erosionIteration(SnipeData v, int erodeFaces, BlockBuffer current, BlockBuffer target) {
        int brushSize = (int) v.getBrushSize() + 1;
        double brushSizeSquared = v.getBrushSize() * v.getBrushSize();
        Map<BlockType, Integer> frequency = Maps.newHashMap();

        for (int x = -brushSize; x <= brushSize; x++) {
            for (int y = -brushSize; y <= brushSize; y++) {
                for (int z = -brushSize; z <= brushSize; z++) {
                    target.set(x, y, z, current.get(x, y, z));
                    if (x * x + y * y + z * z >= brushSizeSquared) {
                        continue;
                    }
                    BlockState state = current.get(x, y, z);
                    if (BlockHelper.isLiquidOrGas(state)) {
                        continue;
                    }
                    int total = 0;
                    int highest = 1;
                    BlockType highestState = state.getType();
                    frequency.clear();
                    for (Vector3i offs : FACES_TO_CHECK) {
                        BlockState next = current.get(x + offs.getX(), y + offs.getY(), z + offs.getZ());
                        if (!BlockHelper.isLiquidOrGas(next)) {
                            continue;
                        }
                        total++;
                        Integer count = frequency.get(next.getType());
                        if (count == null) {
                            count = 1;
                        } else {
                            count++;
                        }
                        if (count > highest) {
                            highest = count;
                            highestState = next.getType();
                        }
                        frequency.put(next.getType(), count);
                    }
                    if (total >= erodeFaces) {
                        target.set(x, y, z, highestState.getDefaultState());
                    }
                }
            }
        }
    }

    @Override
    protected void arrow(final SnipeData v) {
        this.erosion(v, this.currentPreset.getErosionFaces(), this.currentPreset.getErosionRecursion(), this.currentPreset.getFillFaces(),
                this.currentPreset.getFillRecursion());
    }

    @Override
    protected void powder(final SnipeData v) {
        ErosionParameters i = this.currentPreset.getInverted();
        this.erosion(v, i.getErosionFaces(), i.getErosionRecursion(), i.getFillFaces(), i.getFillRecursion());
    }

    @Override
    public void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(TextColors.AQUA, "Erosion minimum exposed faces set to " + this.currentPreset.getErosionFaces());
        vm.custom(TextColors.BLUE, "Fill minumum touching faces set to " + this.currentPreset.getFillFaces());
        vm.custom(TextColors.DARK_BLUE, "Erosion recursion amount set to " + this.currentPreset.getErosionRecursion());
        vm.custom(TextColors.DARK_GREEN, "Fill recursion amount set to " + this.currentPreset.getFillRecursion());
    }

    private void printOptions(SnipeData v) {
        Text.Builder builder = Text.builder();
        builder.append(Text.of(TextColors.AQUA, "Available presets are: "));
        for (int i = 0; i < Preset.values().length; i++) {
            Preset preset = Preset.values()[i];
            if (i != Preset.values().length - 1) {
                builder.append(Text.of(TextColors.GOLD, preset.name().toLowerCase(), TextColors.AQUA, ", "));
            } else {
                builder.append(Text.of(TextColors.GOLD, preset.name().toLowerCase()));
            }
        }
        v.sendMessage(builder.toText());
    }

    @Override
    public void parameters(final String[] par, final SnipeData v) {

        ErosionParameters currentPresetBackup = this.currentPreset;

        if (par.length == 0) {
            printOptions(v);
            return;
        }

        Preset selection = Preset.tryValueOf(par[0]);
        if (selection != null) {
            this.currentPreset = selection.getParameters();
        } else {
            printOptions(v);
            return;
        }

        if (!this.currentPreset.equals(currentPresetBackup)) {
            v.sendMessage(TextColors.AQUA, "Erosion minimum exposed faces set to ", TextColors.WHITE, this.currentPreset.getErosionFaces());
            v.sendMessage(TextColors.BLUE, "Fill minumum touching faces set to ", TextColors.WHITE, this.currentPreset.getFillFaces());
            v.sendMessage(TextColors.DARK_BLUE, "Erosion recursion amount set to ", TextColors.WHITE, this.currentPreset.getErosionRecursion());
            v.sendMessage(TextColors.DARK_GREEN, "Fill recursion amount set to ", TextColors.WHITE, this.currentPreset.getFillRecursion());
        }
    }

    private enum Preset {
        MELT(new ErosionParameters(2, 1, 5, 1)),
        FILL(new ErosionParameters(5, 1, 2, 1)),
        SMOOTH(new ErosionParameters(3, 1, 3, 1)),
        LIFT(new ErosionParameters(6, 0, 1, 1)),
        LOWER(new ErosionParameters(1, 1, 6, 0)),
        FLOATCLEAN(new ErosionParameters(6, 1, 6, 1));

        public static Preset tryValueOf(String name) {
            try {
                return Preset.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private ErosionParameters preset;

        Preset(final ErosionParameters preset) {
            this.preset = preset;
        }

        public ErosionParameters getParameters() {
            return this.preset;
        }

    }

    private static final class ErosionParameters {

        private final int erosionFaces;
        private final int erosionRecursion;
        private final int fillFaces;
        private final int fillRecursion;

        private ErosionParameters inverse = null;

        public ErosionParameters(final int erosionFaces, final int erosionRecursion, final int fillFaces, final int fillRecursion) {
            this.erosionFaces = erosionFaces;
            this.erosionRecursion = erosionRecursion;
            this.fillFaces = fillFaces;
            this.fillRecursion = fillRecursion;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.erosionFaces, this.erosionRecursion, this.fillFaces, this.fillRecursion);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof ErosionParameters) {
                ErosionParameters other = (ErosionParameters) obj;
                return Objects.equal(this.erosionFaces, other.erosionFaces) && Objects.equal(this.erosionRecursion, other.erosionRecursion)
                        && Objects.equal(this.fillFaces, other.fillFaces) && Objects.equal(this.fillRecursion, other.fillRecursion);
            }
            return false;
        }

        public int getErosionFaces() {
            return this.erosionFaces;
        }

        public int getErosionRecursion() {
            return this.erosionRecursion;
        }

        public int getFillFaces() {
            return this.fillFaces;
        }

        public int getFillRecursion() {
            return this.fillRecursion;
        }

        public ErosionParameters getInverted() {
            if (this.inverse == null) {
                this.inverse = new ErosionParameters(this.fillFaces, this.fillRecursion, this.erosionFaces, this.erosionRecursion);
            }
            return this.inverse;
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.erode";
    }
}
