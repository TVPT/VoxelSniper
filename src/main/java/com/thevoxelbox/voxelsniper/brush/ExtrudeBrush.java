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
 * Extrude
 */
public class ExtrudeBrush extends Brush {

    public ExtrudeBrush() {
        this.setName("Extrude");
    }
    // @Spongify

//    private void extrudeUpOrDown(final SnipeData v, boolean isUp)
//    {
//        final int brushSize = v.getBrushSize();
//        final double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
//        Undo undo = new Undo();
//
//        for (int x = -brushSize; x <= brushSize; x++)
//        {
//            final double xSquared = Math.pow(x, 2);
//            for (int z = -brushSize; z <= brushSize; z++)
//            {
//                if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared)
//                {
//                    final int direction = (isUp ? 1 : -1);
//                    for (int y = 0; y < Math.abs(v.getVoxelHeight()); y++)
//                    {
//                        final int tempY = y * direction;
//                        undo = this.perform(
//                                this.clampY(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + tempY, this.getTargetBlock().getZ() + z),
//                                this.clampY(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + tempY + direction, this.getTargetBlock().getZ() + z),
//                                v, undo);
//                    }
//                }
//            }
//        }
//
//        v.owner().storeUndo(undo);
//    }
//
//    private void extrudeNorthOrSouth(final SnipeData v, boolean isSouth)
//    {
//        final int brushSize = v.getBrushSize();
//        final double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
//        Undo undo = new Undo();
//
//        for (int x = -brushSize; x <= brushSize; x++)
//        {
//            final double xSquared = Math.pow(x, 2);
//            for (int y = -brushSize; y <= brushSize; y++)
//            {
//                if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared)
//                {
//                    final int direction = (isSouth) ? 1 : -1;
//                    for (int z = 0; z < Math.abs(v.getVoxelHeight()); z++)
//                    {
//                        final int tempZ = z * direction;
//                        undo = this.perform(
//                                this.clampY(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + tempZ),
//                                this.clampY(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + tempZ + direction),
//                                v, undo);
//                    }
//
//                }
//            }
//        }
//
//        v.owner().storeUndo(undo);
//    }
//
//    private void extrudeEastOrWest(final SnipeData v, boolean isEast)
//    {
//        final int brushSize = v.getBrushSize();
//        final double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
//        Undo undo = new Undo();
//
//        for (int y = -brushSize; y <= brushSize; y++)
//        {
//            final double ySquared = Math.pow(y, 2);
//            for (int z = -brushSize; z <= brushSize; z++)
//            {
//                if ((ySquared + Math.pow(z, 2)) <= brushSizeSquared)
//                {
//                    final int direction = (isEast) ? 1 : -1;
//                    for (int x = 0; x < Math.abs(v.getVoxelHeight()); x++)
//                    {
//                        final int tempX = x * direction;
//                        undo = this.perform(
//                                this.clampY(this.getTargetBlock().getX() + tempX, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z),
//                                this.clampY(this.getTargetBlock().getX() + tempX + direction, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z),
//                                v, undo);
//                    }
//
//                }
//            }
//        }
//        v.owner().storeUndo(undo);
//    }
//
//    @SuppressWarnings("deprecation")
//	private Undo perform(final Block b1, final Block b2, final SnipeData v, final Undo undo)
//    {
//        if (v.getVoxelList().contains(new int[]{this.getBlockIdAt(b1.getX(), b1.getY(), b1.getZ()), this.getBlockDataAt(b1.getX(), b1.getY(), b1.getZ())}))
//        {
//            undo.put(b2);
//            this.setBlockIdAt(b2.getZ(), b2.getX(), b2.getY(), this.getBlockIdAt(b1.getX(), b1.getY(), b1.getZ()));
//            this.clampY(b2.getX(), b2.getY(), b2.getZ()).setData(this.clampY(b1.getX(), b1.getY(), b1.getZ()).getData());
//        }
//
//        return undo;
//    }
//
//    private void selectExtrudeMethod(final SnipeData v, final BlockFace blockFace, final boolean towardsUser)
//    {
//        if (blockFace == null || v.getVoxelHeight() == 0)
//        {
//            return;
//        }
//        boolean tempDirection = towardsUser;
//        switch (blockFace)
//        {
//            case DOWN:
//                tempDirection = !towardsUser;
//            case UP:
//                extrudeUpOrDown(v, tempDirection);
//                break;
//            case NORTH:
//                tempDirection = !towardsUser;
//            case SOUTH:
//                extrudeNorthOrSouth(v, tempDirection);
//                break;
//            case WEST:
//                tempDirection = !towardsUser;
//            case EAST:
//                extrudeEastOrWest(v, tempDirection);
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    protected final void arrow(final SnipeData v) {
//        this.selectExtrudeMethod(v, this.getTargetBlock().getFace(this.getLastBlock()), false);
    }

    @Override
    protected final void powder(final SnipeData v) {
//        this.selectExtrudeMethod(v, this.getTargetBlock().getFace(this.getLastBlock()), true);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.voxelList();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.extrude";
    }
}
