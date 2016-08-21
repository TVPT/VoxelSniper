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
import org.spongepowered.api.text.format.TextColors;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Ring_Brush
 *
 * @author Voxel
 */
public class RingBrush extends PerformBrush {

    private double trueCircle = 0;
    private double innerSize = 0;

    public RingBrush() {
        this.setName("Ring");
    }

    // @Spongify
//    private void ring(final SnipeData v, Block targetBlock)
//    {
//        final int brushSize = v.getBrushSize();
//        final double outerSquared = Math.pow(brushSize + this.trueCircle, 2);
//        final double innerSquared = Math.pow(this.innerSize, 2);
//
//        for (int x = brushSize; x >= 0; x--)
//        {
//            final double xSquared = Math.pow(x, 2);
//            for (int z = brushSize; z >= 0; z--)
//            {
//                final double ySquared = Math.pow(z, 2);
//                if ((xSquared + ySquared) <= outerSquared && (xSquared + ySquared) >= innerSquared)
//                {
//                    current.perform(targetBlock.getRelative(x, 0, z));
//                    current.perform(targetBlock.getRelative(x, 0, -z));
//                    current.perform(targetBlock.getRelative(-x, 0, z));
//                    current.perform(targetBlock.getRelative(-x, 0, -z));
//                }
//            }
//        }
//
//        v.owner().storeUndo(this.current.getUndo());
//    }

    @Override
    protected final void arrow(final SnipeData v) {
//        this.ring(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
//        this.ring(v, this.getLastBlock());
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(TextColors.AQUA, "The inner radius is ", TextColors.RED, this.innerSize);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 1; i < par.length; i++) {
            if (par[i].equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD, "Ring Brush Parameters:");
                v.sendMessage(TextColors.AQUA,
                        "/b ri true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ri false will switch back. (false is default)");
                v.sendMessage(TextColors.AQUA, "/b ri ir2.5 -- will set the inner radius to 2.5 units");
                return;
            } else if (par[i].startsWith("true")) {
                this.trueCircle = 0.5;
                v.sendMessage(TextColors.AQUA, "True circle mode ON.");
            } else if (par[i].startsWith("false")) {
                this.trueCircle = 0;
                v.sendMessage(TextColors.AQUA, "True circle mode OFF.");
            } else if (par[i].startsWith("ir")) {
                try {
                    final double d = Double.parseDouble(par[i].replace("ir", ""));
                    this.innerSize = d;
                    v.sendMessage(TextColors.AQUA, "The inner radius has been set to ", TextColors.RED, this.innerSize);
                } catch (final Exception exception) {
                    v.sendMessage(TextColors.RED, "The parameters included are invalid.");
                }
            } else {
                v.sendMessage(TextColors.RED, "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ring";
    }
}
