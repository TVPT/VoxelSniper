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
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.flowpowered.math.GenericMath;
import org.spongepowered.api.text.format.TextColors;

import java.util.Random;

@Brush.BrushInfo(
    name = "Random Erode",
    aliases = {"re", "randomerode"},
    permission = "voxelsniper.brush.randomerode",
    category = Brush.BrushCategory.TERRAIN
)
public class RandomErodeBrush extends ErodeBrush {

    private Random rand = new Random();
    
    public RandomErodeBrush() {
    }

    @Override
    protected void arrow(final SnipeData v) {
        int erodeFaces = this.rand.nextInt(5) + 1;
        int erodeRec = this.rand.nextInt(4) + 1;
        int fillFaces = GenericMath.floor(this.rand.nextDouble() * this.rand.nextDouble() * 5) + 1;
        int fillRec = this.rand.nextInt(3) + 1;
        this.erosion(v, erodeFaces, erodeRec, fillFaces, fillRec);
    }

    @Override
    protected void powder(final SnipeData v) {
        int erodeFaces = GenericMath.floor(this.rand.nextDouble() * this.rand.nextDouble() * 5) + 1;
        int erodeRec = this.rand.nextInt(3) + 1;
        int fillFaces = this.rand.nextInt(5) + 1;
        int fillRec = this.rand.nextInt(4) + 1;
        this.erosion(v, erodeFaces, erodeRec, fillFaces, fillRec);
    }

    @Override
    public void info(final Message vm) {
        vm.brushName(info.name());
        vm.size();
    }

    @Override
    public void parameters(final String[] par, final SnipeData v) {
        v.sendMessage(TextColors.RED, "This brush does not accept additional parameters.");
    }
}
