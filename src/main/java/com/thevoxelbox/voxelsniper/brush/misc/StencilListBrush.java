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
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class StencilListBrush extends Brush {

    private byte pasteOption = 1; // 0 = full, 1 = fill, 2 = replace
    private String filename = "NoFileLoaded";
    private short x;
    private short z;
    private short y;
    private short xRef;
    private short zRef;
    private short yRef;
    private byte pasteParam = 0;
    private HashMap<Integer, String> stencilList = new HashMap<Integer, String>();

    public StencilListBrush() {
        this.setName("StencilList");
    }

    private String readRandomStencil(final SnipeData v) {
        double rand = Math.random() * (this.stencilList.size());
        final int choice = (int) rand;
        return this.stencilList.get(choice);
    }

    private void readStencilList(final String listname, final SnipeData v) {
        final File file = new File("plugins/VoxelSniper/stencilLists/" + this.filename + ".txt");
        if (file.exists()) {
            try {
                final Scanner scanner = new Scanner(file);
                int counter = 0;
                while (scanner.hasNext()) {
                    this.stencilList.put(counter, scanner.nextLine());
                    counter++;
                }
                scanner.close();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void stencilPaste(final SnipeData v) {
        // @Spongify
//        if (this.filename.matches("NoFileLoaded"))
//        {
//            v.sendMessage(TextColors.RED + "You did not specify a filename for the list.  This is required.");
//            return;
//        }
//
//        final String stencilName = this.readRandomStencil(v);
//        v.sendMessage(stencilName);
//
//        final Undo undo = new Undo();
//        final File file = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");
//
//        if (file.exists())
//        {
//            try
//            {
//                final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
//
//                this.x = in.readShort();
//                this.z = in.readShort();
//                this.y = in.readShort();
//
//                this.xRef = in.readShort();
//                this.zRef = in.readShort();
//                this.yRef = in.readShort();
//
//                final int numRuns = in.readInt();
//                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
//                final int volume = this.x * this.y * this.z;
//                v.owner().getPlayer().sendMessage(TextColors.AQUA + this.filename + " pasted.  Volume is " + volume + " blocks.");
//
//                int currX = -this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
//                // corner, for example.
//                int currZ = -this.zRef;
//                int currY = -this.yRef;
//                int id;
//                int data;
//                if (this.pasteOption == 0)
//                {
//                    for (int i = 1; i < numRuns + 1; i++)
//                    {
//                        if (in.readBoolean())
//                        {
//                            final int numLoops = in.readByte() + 128;
//                            id = (in.readByte() + 128);
//                            data = (in.readByte() + 128);
//                            for (int j = 0; j < numLoops; j++)
//                            {
//                                undo.put(this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ));
//                                this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ).setTypeIdAndData(id, (byte) data, false);
//                                currX++;
//                                if (currX == this.x - this.xRef)
//                                {
//                                    currX = -this.xRef;
//                                    currZ++;
//                                    if (currZ == this.z - this.zRef)
//                                    {
//                                        currZ = -this.zRef;
//                                        currY++;
//                                    }
//                                }
//                            }
//                        }
//                        else
//                        {
//                            undo.put(this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ));
//                            this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ).setTypeIdAndData((in.readByte() + 128), (byte) (in.readByte() + 128), false);
//                            currX++;
//                            if (currX == this.x - this.xRef)
//                            {
//                                currX = -this.xRef;
//                                currZ++;
//                                if (currZ == this.z - this.zRef)
//                                {
//                                    currZ = -this.zRef;
//                                    currY++;
//                                }
//                            }
//                        }
//                    }
//                }
//                else if (this.pasteOption == 1)
//                {
//                    for (int i = 1; i < numRuns + 1; i++)
//                    {
//                        if (in.readBoolean())
//                        {
//                            final int numLoops = in.readByte() + 128;
//                            id = (in.readByte() + 128);
//                            data = (in.readByte() + 128);
//                            for (int j = 0; j < numLoops; j++)
//                            {
//                                if (id != 0 && this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ).getTypeId() == 0)
//                                {
//                                    undo.put(this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ));
//                                    this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ).setTypeIdAndData(id, (byte) (data), false);
//                                }
//                                currX++;
//                                if (currX == this.x - this.xRef)
//                                {
//                                    currX = -this.xRef;
//                                    currZ++;
//                                    if (currZ == this.z - this.zRef)
//                                    {
//                                        currZ = -this.zRef;
//                                        currY++;
//                                    }
//                                }
//                            }
//                        }
//                        else
//                        {
//                            id = (in.readByte() + 128);
//                            data = (in.readByte() + 128);
//                            if (id != 0 && this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ).getTypeId() == 0)
//                            {
//                                undo.put(this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ));
//                                this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ).setTypeIdAndData(id, (byte) (data), false);
//                            }
//                            currX++;
//                            if (currX == this.x - this.xRef)
//                            {
//                                currX = -this.xRef;
//                                currZ++;
//                                if (currZ == this.z - this.zRef)
//                                {
//                                    currZ = -this.zRef;
//                                    currY++;
//                                }
//                            }
//                        }
//                    }
//                }
//                else
//                { // replace
//                    for (int i = 1; i < numRuns + 1; i++)
//                    {
//                        if (in.readBoolean())
//                        {
//                            final int numLoops = in.readByte() + 128;
//                            id = (in.readByte() + 128);
//                            data = (in.readByte() + 128);
//                            for (int j = 0; j < (numLoops); j++)
//                            {
//                                if (id != 0)
//                                {
//                                    undo.put(this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ));
//                                    this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ).setTypeIdAndData(id, (byte) data, false);
//                                }
//                                currX++;
//                                if (currX == this.x - this.xRef)
//                                {
//                                    currX = -this.xRef;
//                                    currZ++;
//                                    if (currZ == this.z - this.zRef)
//                                    {
//                                        currZ = -this.zRef;
//                                        currY++;
//                                    }
//                                }
//                            }
//                        }
//                        else
//                        {
//                            id = (in.readByte() + 128);
//                            data = (in.readByte() + 128);
//                            if (id != 0)
//                            {
//                                undo.put(this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ));
//                                this.clampY(this.getTargetBlock().getX() + currX, this.getTargetBlock().getY() + currY, this.getTargetBlock().getZ() + currZ).setTypeIdAndData(id, (byte) data, false);
//                            }
//                            currX++;
//                            if (currX == this.x)
//                            {
//                                currX = 0;
//                                currZ++;
//                                if (currZ == this.z)
//                                {
//                                    currZ = 0;
//                                    currY++;
//                                }
//                            }
//                        }
//                    }
//                }
//                in.close();
//                v.owner().storeUndo(undo);
//
//            }
//            catch (final Exception exception)
//            {
//                v.owner().getPlayer().sendMessage(TextColors.RED + "Something went wrong.");
//                exception.printStackTrace();
//            }
//        }
//        else
//        {
//            v.owner().getPlayer().sendMessage(TextColors.RED + "You need to type a stencil name / your specified stencil does not exist.");
//        }
    }

    private void stencilPasteRotation(final SnipeData v) {
//        // just randomly chooses a rotation and then calls stencilPaste.
//        this.readStencilList(this.filename, v);
//        final double random = Math.random();
//        if (random < 0.26)
//        {
//            this.stencilPaste(v);
//        }
//        else if (random < 0.51)
//        {
//            this.stencilPaste90(v);
//        }
//        else if (random < 0.76)
//        {
//            this.stencilPaste180(v);
//        }
//        else
//        {
//            this.stencilPaste270(v);
//        }

    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.stencilPaste(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.stencilPasteRotation(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom("File loaded: " + this.filename);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD, "Stencil List brush Parameters:");
            v.sendMessage(TextColors.AQUA,
                    "/b schem [optional: 'full' 'fill' or 'replace', with fill as default] [name] -- Loads the specified stencil list.  Full/fill/replace must come first.  Full = paste all blocks, fill = paste only into air blocks, replace = paste full blocks in only, but replace anything in their way.");
            return;
        } else if (par[1].equalsIgnoreCase("full")) {
            this.pasteOption = 0;
            this.pasteParam = 1;
        } else if (par[1].equalsIgnoreCase("fill")) {
            this.pasteOption = 1;
            this.pasteParam = 1;
        } else if (par[1].equalsIgnoreCase("replace")) {
            this.pasteOption = 2;
            this.pasteParam = 1;
        }
        try {
            this.filename = par[1 + this.pasteParam];
            final File file = new File("plugins/VoxelSniper/stencilLists/" + this.filename + ".txt");
            if (file.exists()) {
                v.sendMessage(TextColors.RED, "Stencil List '" + this.filename + "' exists and was loaded.");
                this.readStencilList(this.filename, v);
            } else {
                v.sendMessage(TextColors.AQUA,
                        "Stencil List '" + this.filename + "' does not exist.  This brush will not function without a valid stencil list.");
                this.filename = "NoFileLoaded";
            }
        } catch (final Exception exception) {
            v.sendMessage(TextColors.RED, "You need to type a stencil name.");
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.stencillist";
    }
}
