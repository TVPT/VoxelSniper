package com.thevoxelbox.voxelsniper.brush;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Gavjenks
 */
public class StencilList extends Brush {

    protected byte pasteoption = 1; // 0 = full, 1 = fill, 2 = replace
    protected String Filename = "NoFileLoaded";
    protected short X;
    protected short Z;
    protected short Y;
    protected short Xref;
    protected short Zref;
    protected short Yref;
    protected byte pasteparam = 0;
    protected byte[] blockArray;
    protected byte[] dataArray;
    protected byte[] runSizeArray;
    protected int[] firstpoint = new int[3];
    protected int[] secondpoint = new int[3];
    protected int[] pastepoint = new int[3];
    protected byte rotation = 0;
    protected HashMap<Integer, String> stencilList = new HashMap<Integer, String>();
    protected byte point = 1;

    private static int timesUsed = 0;

    public StencilList() {
        this.setName("StencilList");
    }

    @Override
    public final int getTimesUsed() {
        return StencilList.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.custom("File loaded: " + this.Filename);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Stencil List brush Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b schem [optional: 'full' 'fill' or 'replace', with fill as default] [name] -- Loads the specified stencil list.  Full/fill/replace must come first.  Full = paste all blocks, fill = paste only into air blocks, replace = paste full blocks in only, but replace anything in their way.");
            return;
        } else if (par[1].equalsIgnoreCase("full")) {
            this.pasteoption = 0;
            this.pasteparam = 1;
        } else if (par[1].equalsIgnoreCase("fill")) {
            this.pasteoption = 1;
            this.pasteparam = 1;
        } else if (par[1].equalsIgnoreCase("replace")) {
            this.pasteoption = 2;
            this.pasteparam = 1;
        }
        try {
            this.Filename = par[1 + this.pasteparam];
            final File f = new File("plugins/VoxelSniper/stencilLists/" + this.Filename + ".txt");
            if (f.exists()) {
                v.sendMessage(ChatColor.RED + "Stencil List '" + this.Filename + "' exists and was loaded.");
                this.readStencilList(this.Filename, v);
            } else {
                v.sendMessage(ChatColor.AQUA + "Stencil List '" + this.Filename
                        + "' does not exist.  This brush will not function without a valid stencil list.");
                this.Filename = "NoFileLoaded";
            }
        } catch (final Exception e) {
            v.sendMessage(ChatColor.RED + "You need to type a stencil name.");
        }
    }

    public final String readRandomStencil(final vData v) {
        double rand = Math.random();
        rand = rand * (this.stencilList.size());
        final int choice = (int) (rand);
        return this.stencilList.get(choice);
    }

    public final void readStencilList(final String listname, final vData v) {
        final File f = new File("plugins/VoxelSniper/stencilLists/" + this.Filename + ".txt");
        if (f.exists()) {
            try {
                final Scanner snr = new Scanner(f);
                int counter = 0;
                while (snr.hasNext()) {
                    this.stencilList.put(counter, snr.nextLine());
                    counter++;
                }
                snr.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        StencilList.timesUsed = tUsed;
    }

    public final void stencilPaste(final vData v) {
        if (this.Filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String stencilName = this.readRandomStencil(v);
        v.sendMessage(stencilName);

        final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());
        final File f = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");

        if (f.exists()) {
            try {
                final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

                this.X = in.readShort();
                this.Z = in.readShort();
                this.Y = in.readShort();

                this.Xref = in.readShort();
                this.Zref = in.readShort();
                this.Yref = in.readShort();

                final int numRuns = in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to vSniper and boots you out with error message if too big.
                final int volume = this.X * this.Y * this.Z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + this.Filename + " pasted.  Volume is " + volume + " blocks.");

                int currX = -this.Xref; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int currZ = -this.Zref;
                int currY = -this.Yref;
                int id;
                int data;
                if (this.pasteoption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                                currX++;
                                if (currX == this.X - this.Xref) {
                                    currX = -this.Xref;
                                    currZ++;
                                    if (currZ == this.Z - this.Zref) {
                                        currZ = -this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                            this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData((in.readByte() + 128),
                                    (byte) (in.readByte() + 128), false);
                            currX++;
                            if (currX == this.X - this.Xref) {
                                currX = -this.Xref;
                                currZ++;
                                if (currZ == this.Z - this.Zref) {
                                    currZ = -this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteoption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                if (id != 0 && this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                                  // air, and it prevents us
                                                                                                                                  // most of the time from
                                                                                                                                  // having to even check the
                                                                                                                                  // block.
                                    h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                    this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) (data), false);
                                }
                                currX++;
                                if (currX == this.X - this.Xref) {
                                    currX = -this.Xref;
                                    currZ++;
                                    if (currZ == this.Z - this.Zref) {
                                        currZ = -this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            if (id != 0 && this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                              // air, and it prevents us most of
                                                                                                                              // the time from having to even
                                                                                                                              // check the block.
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                // v.sendMessage("currX:" + currX + " currZ:"+currZ + " currY:" + currY + " id:" + id + " data:" + (byte)data);
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) (data), false);
                            }
                            currX++;
                            if (currX == this.X - this.Xref) {
                                currX = -this.Xref;
                                currZ++;
                                if (currZ == this.Z - this.Zref) {
                                    currZ = -this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < (numLoops); j++) {
                                if (id != 0) {
                                    h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                    this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                                }
                                currX++;
                                if (currX == this.X - this.Xref) {
                                    currX = -this.Xref;
                                    currZ++;
                                    if (currZ == this.Z - this.Zref) {
                                        currZ = -this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            if (id != 0) {
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                            }
                            currX++;
                            if (currX == this.X) {
                                currX = 0;
                                currZ++;
                                if (currZ == this.Z) {
                                    currZ = 0;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
                v.storeUndo(h);

            } catch (final Exception e) {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                // v.sendMessage("jspecial: " + jspecial);
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    public final void stencilPaste180(final vData v) {
        if (this.Filename.matches("NoFileLoaded")) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String stencilName = this.readRandomStencil(v);

        final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());
        final File f = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");

        if (f.exists()) {
            try {
                final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

                this.X = in.readShort();
                this.Z = in.readShort();
                this.Y = in.readShort();

                this.Xref = in.readShort();
                this.Zref = in.readShort();
                this.Yref = in.readShort();

                final int numRuns = in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to vSniper and boots you out with error message if too big.
                final int volume = this.X * this.Y * this.Z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + this.Filename + " pasted.  Volume is " + volume + " blocks.");

                int currX = +this.Xref; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int currZ = +this.Zref;
                int currY = -this.Yref;
                int id;
                int data;
                if (this.pasteoption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                                currX--;
                                if (currX == -this.X + this.Xref) {
                                    currX = this.Xref;
                                    currZ--;
                                    if (currZ == -this.Z + this.Zref) {
                                        currZ = +this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                            this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData((in.readByte() + 128),
                                    (byte) (in.readByte() + 128), false);
                            currX--;
                            if (currX == -this.X + this.Xref) {
                                currX = this.Xref;
                                currZ--;
                                if (currZ == -this.Z + this.Zref) {
                                    currZ = +this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteoption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                if (id != 0 && this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                                  // air, and it prevents us
                                                                                                                                  // most of the time from
                                                                                                                                  // having to even check the
                                                                                                                                  // block.
                                    h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                    this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) (data), false);
                                }
                                currX--;
                                if (currX == -this.X + this.Xref) {
                                    currX = this.Xref;
                                    currZ--;
                                    if (currZ == -this.Z + this.Zref) {
                                        currZ = +this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            if (id != 0 && this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                              // air, and it prevents us most of
                                                                                                                              // the time from having to even
                                                                                                                              // check the block.
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                // v.sendMessage("currX:" + currX + " currZ:"+currZ + " currY:" + currY + " id:" + id + " data:" + (byte)data);
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) (data), false);
                            }
                            currX--;
                            if (currX == -this.X + this.Xref) {
                                currX = this.Xref;
                                currZ--;
                                if (currZ == -this.Z + this.Zref) {
                                    currZ = +this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < (numLoops); j++) {
                                if (id != 0) {
                                    h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                    this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                                }
                                currX--;
                                if (currX == -this.X + this.Xref) {
                                    currX = this.Xref;
                                    currZ--;
                                    if (currZ == -this.Z + this.Zref) {
                                        currZ = +this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            if (id != 0) {
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                            }
                            currX--;
                            if (currX == -this.X + this.Xref) {
                                currX = this.Xref;
                                currZ--;
                                if (currZ == -this.Z + this.Zref) {
                                    currZ = +this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
                v.storeUndo(h);

            } catch (final Exception e) {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                // v.sendMessage("jspecial: " + jspecial);
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    public final void stencilPaste270(final vData v) {
        if (this.Filename.matches("NoFileLoaded")) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String stencilName = this.readRandomStencil(v);

        final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());
        final File f = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");

        if (f.exists()) {
            try {
                final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

                this.X = in.readShort();
                this.Z = in.readShort();
                this.Y = in.readShort();

                this.Xref = in.readShort();
                this.Zref = in.readShort();
                this.Yref = in.readShort();

                final int numRuns = in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to vSniper and boots you out with error message if too big.
                final int volume = this.X * this.Y * this.Z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + this.Filename + " pasted.  Volume is " + volume + " blocks.");

                int currX = +this.Zref; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int currZ = -this.Xref;
                int currY = -this.Yref;
                int id;
                int data;
                if (this.pasteoption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                                currZ++;
                                if (currZ == this.X - this.Xref) {
                                    currZ = -this.Xref;
                                    currX--;
                                    if (currX == -this.Z + this.Zref) {
                                        currX = +this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                            this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData((in.readByte() + 128),
                                    (byte) (in.readByte() + 128), false);
                            currZ++;
                            currZ++;
                            if (currZ == this.X - this.Xref) {
                                currZ = -this.Xref;
                                currX--;
                                if (currX == -this.Z + this.Zref) {
                                    currX = +this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteoption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                if (id != 0 && this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                                  // air, and it prevents us
                                                                                                                                  // most of the time from
                                                                                                                                  // having to even check the
                                                                                                                                  // block.
                                    h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                    this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) (data), false);
                                }
                                currZ++;
                                if (currZ == this.X - this.Xref) {
                                    currZ = -this.Xref;
                                    currX--;
                                    if (currX == -this.Z + this.Zref) {
                                        currX = +this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            if (id != 0 && this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                              // air, and it prevents us most of
                                                                                                                              // the time from having to even
                                                                                                                              // check the block.
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                // v.sendMessage("currX:" + currX + " currZ:"+currZ + " currY:" + currY + " id:" + id + " data:" + (byte)data);
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) (data), false);
                            }
                            currZ++;
                            if (currZ == this.X - this.Xref) {
                                currZ = -this.Xref;
                                currX--;
                                if (currX == -this.Z + this.Zref) {
                                    currX = +this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < (numLoops); j++) {
                                if (id != 0) {
                                    h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                    this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                                }
                                currZ++;
                                if (currZ == this.X - this.Xref) {
                                    currZ = -this.Xref;
                                    currX--;
                                    if (currX == -this.Z + this.Zref) {
                                        currX = +this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            if (id != 0) {
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                            }
                            currZ++;
                            if (currZ == this.X - this.Xref) {
                                currZ = -this.Xref;
                                currX--;
                                if (currX == -this.Z + this.Zref) {
                                    currX = +this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
                v.storeUndo(h);

            } catch (final Exception e) {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                // v.sendMessage("jspecial: " + jspecial);
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    public final void stencilPaste90(final vData v) {
        if (this.Filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String stencilName = this.readRandomStencil(v);

        final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());
        final File f = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");

        if (f.exists()) {
            try {
                final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

                this.X = in.readShort();
                this.Z = in.readShort();
                this.Y = in.readShort();

                this.Xref = in.readShort();
                this.Zref = in.readShort();
                this.Yref = in.readShort();

                final int numRuns = in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to vSniper and boots you out with error message if too big.
                final int volume = this.X * this.Y * this.Z;
                v.sendMessage(ChatColor.AQUA + this.Filename + " pasted.  Volume is " + volume + " blocks.");

                int currX = -this.Zref; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int currZ = +this.Xref;
                int currY = -this.Yref;
                int id;
                int data;
                if (this.pasteoption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                                currZ--;
                                if (currZ == -this.X + this.Xref) {
                                    currZ = this.Xref;
                                    currX++;
                                    if (currX == this.Z - this.Zref) {
                                        currX = -this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                            this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData((in.readByte() + 128),
                                    (byte) (in.readByte() + 128), false);
                            currZ--;
                            if (currZ == -this.X + this.Xref) {
                                currZ = this.Xref;
                                currX++;
                                if (currX == this.Z - this.Zref) {
                                    currX = -this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteoption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                if (id != 0 && this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                                  // air, and it prevents us
                                                                                                                                  // most of the time from
                                                                                                                                  // having to even check the
                                                                                                                                  // block.
                                    h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                    this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) (data), false);
                                }
                                currZ--;
                                if (currZ == -this.X + this.Xref) {
                                    currZ = this.Xref;
                                    currX++;
                                    if (currX == this.Z - this.Zref) {
                                        currX = -this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            if (id != 0 && this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                              // air, and it prevents us most of
                                                                                                                              // the time from having to even
                                                                                                                              // check the block.
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                // v.sendMessage("currX:" + currX + " currZ:"+currZ + " currY:" + currY + " id:" + id + " data:" + (byte)data);
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) (data), false);
                            }
                            currZ--;
                            if (currZ == -this.X + this.Xref) {
                                currZ = this.Xref;
                                currX++;
                                if (currX == this.Z - this.Zref) {
                                    currX = -this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            final int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < (numLoops); j++) {
                                if (id != 0) {
                                    h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                    this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                                }
                                currZ--;
                                if (currZ == -this.X + this.Xref) {
                                    currZ = this.Xref;
                                    currX++;
                                    if (currX == this.Z - this.Zref) {
                                        currX = -this.Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            if (id != 0) {
                                h.put(this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ));
                                this.clampY(this.getBlockPositionX() + currX, this.getBlockPositionY() + currY, this.getBlockPositionZ() + currZ).setTypeIdAndData(id, (byte) data, false);
                            }
                            currZ--;
                            if (currZ == -this.X + this.Xref) {
                                currZ = this.Xref;
                                currX++;
                                if (currX == this.Z - this.Zref) {
                                    currX = -this.Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
                v.storeUndo(h);

            } catch (final Exception e) {
                v.sendMessage(ChatColor.RED + "Something went wrong.");
                // v.sendMessage("jspecial: " + jspecial);
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    public final void stencilPasteRotation(final vData v) {
        // just randomly chooses a rotation and then calls stencilPaste.
        this.readStencilList(this.Filename, v);
        final double rand = Math.random();
        if (rand < 0.26) {
            this.stencilPaste(v);
        } else if (rand < 0.51) {
            this.stencilPaste90(v);
        } else if (rand < 0.76) {
            this.stencilPaste180(v);
        } else {
            this.stencilPaste270(v);
        }

    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) { // will be used to copy/save later on?
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.stencilPaste(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) { // will be used to paste later on
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.stencilPasteRotation(v);
    }
}
