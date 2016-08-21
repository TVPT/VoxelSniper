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
 * THIS BRUSH SHOULD NOT USE PERFORMERS.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Shell_Brushes
 *
 * @author Voxel
 */
public class ShellVoxelBrush extends Brush {

    // @Spongify
    public ShellVoxelBrush() {
        this.setName("Shell Voxel");
    }

//    private void vShell(final SnipeData v, Block targetBlock)
//    {
//        final int brushSize = v.getBrushSize();
//        final int brushSizeSquared = 2 * brushSize;
//        final int[][][] oldMaterials = new int[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1]; // Array that holds the original materials plus a  buffer
//        final int[][][] newMaterials = new int[2 * brushSize + 1][2 * brushSize + 1][2 * brushSize + 1]; // Array that holds the hollowed materials
//
//        int blockPositionX = targetBlock.getX();
//        int blockPositionY = targetBlock.getY();
//        int blockPositionZ = targetBlock.getZ();
//        // Log current materials into oldmats
//        for (int x = 0; x <= 2 * (brushSize + 1); x++)
//        {
//            for (int y = 0; y <= 2 * (brushSize + 1); y++)
//            {
//                for (int z = 0; z <= 2 * (brushSize + 1); z++)
//                {
//                    oldMaterials[x][y][z] = this.getBlockIdAt(blockPositionX - brushSize - 1 + x, blockPositionY - brushSize - 1 + y, blockPositionZ - brushSize - 1 + z);
//                }
//            }
//        }
//
//        // Log current materials into newmats
//        for (int x = 0; x <= brushSizeSquared; x++)
//        {
//            for (int y = 0; y <= brushSizeSquared; y++)
//            {
//                for (int z = 0; z <= brushSizeSquared; z++)
//                {
//                    newMaterials[x][y][z] = oldMaterials[x + 1][y + 1][z + 1];
//                }
//            }
//        }
//        int temp;
//
//        // Hollow Brush Area
//        for (int x = 0; x <= brushSizeSquared; x++)
//        {
//            for (int z = 0; z <= brushSizeSquared; z++)
//            {
//                for (int y = 0; y <= brushSizeSquared; y++)
//                {
//                    temp = 0;
//
//                    if (oldMaterials[x + 1 + 1][z + 1][y + 1] == v.getReplaceId())
//                    {
//                        temp++;
//                    }
//                    if (oldMaterials[x + 1 - 1][z + 1][y + 1] == v.getReplaceId())
//                    {
//                        temp++;
//                    }
//                    if (oldMaterials[x + 1][z + 1 + 1][y + 1] == v.getReplaceId())
//                    {
//                        temp++;
//                    }
//                    if (oldMaterials[x + 1][z + 1 - 1][y + 1] == v.getReplaceId())
//                    {
//                        temp++;
//                    }
//                    if (oldMaterials[x + 1][z + 1][y + 1 + 1] == v.getReplaceId())
//                    {
//                        temp++;
//                    }
//                    if (oldMaterials[x + 1][z + 1][y + 1 - 1] == v.getReplaceId())
//                    {
//                        temp++;
//                    }
//
//                    if (temp == 0)
//                    {
//                        newMaterials[x][z][y] = v.getVoxelId();
//                    }
//                }
//            }
//        }
//
//        // Make the changes
//        final Undo undo = new Undo();
//
//        for (int x = brushSizeSquared; x >= 0; x--)
//        {
//            for (int y = 0; y <= brushSizeSquared; y++)
//            {
//                for (int z = brushSizeSquared; z >= 0; z--)
//                {
//                    if (this.getBlockIdAt(blockPositionX - brushSize + x, blockPositionY - brushSize + y, blockPositionZ - brushSize + z) != newMaterials[x][y][z])
//                    {
//                        undo.put(this.clampY(blockPositionX - brushSize + x, blockPositionY - brushSize + y, blockPositionZ - brushSize + z));
//                    }
//                    this.setBlockIdAt(blockPositionZ - brushSize + z, blockPositionX - brushSize + x, blockPositionY - brushSize + y, newMaterials[x][y][z]);
//                }
//            }
//        }
//        v.owner().storeUndo(undo);
//
//        v.owner().getPlayer().sendMessage(TextColors.AQUA + "Shell complete.");
//    }

    @Override
    protected final void arrow(final SnipeData v) {
//        this.vShell(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
//        this.vShell(v, this.getLastBlock());
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
        return "voxelsniper.brush.shellvoxel";
    }
}
