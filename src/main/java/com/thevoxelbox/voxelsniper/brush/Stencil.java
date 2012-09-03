package com.thevoxelbox.voxelsniper.brush;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Gavjenks This is paste only currently. Assumes files exist, and thus has no usefulness until I add in saving stencils later. Uses sniper-exclusive
 *         stencil format: 3 shorts for X,Z,Y size of cuboid 3 shorts for X,Z,Y offsets from the -X,-Z,-Y corner. This is the reference point for pasting,
 *         corresponding to where you click your brush. 1 long integer saying how many runs of blocks are in the schematic (data is compressed into runs) 1
 * 
 *         per run: ( 1 boolean: true = compressed line ahead, false = locally unique block ahead. This wastes a bit instead of a byte, and overall saves space,
 *         as long as at least 1/8 of all RUNS are going to be size 1, which in Minecraft is almost definitely true. IF boolean was true, next unsigned byte
 *         stores the number of consecutive blocks of the same type, up to 256. IF boolean was false, there is no byte here, goes straight to ID and data
 *         instead, which applies to just one block. 2 bytes to identify type of block. First byte is ID, second is data. This applies to every one of the line
 *         of consecutive blocks if boolean was true. )
 * 
 */
// LOVE THIS -psanker
public class Stencil extends Brush {

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
    protected int jspecial;
    protected byte point = 1;

    private static int timesUsed = 0;

    public Stencil() {
        this.setName("Stencil");
    }

    @Override
    public final int getTimesUsed() {
        return Stencil.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.custom("File loaded: " + this.Filename);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Stencil brush Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b schem [optional: 'full' 'fill' or 'replace', with fill as default] [name] -- Loads the specified schematic.  Allowed size of schematic is based on rank.  Full/fill/replace must come first.  Full = paste all blocks, fill = paste only into air blocks, replace = paste full blocks in only, but replace anything in their way.");
            v.sendMessage(ChatColor.BLUE + "Size of the stencils you are allowed to paste depends on rank (member / lite, sniper, curator, admin)");
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
            final File f = new File("plugins/VoxelSniper/stencils/" + this.Filename + ".vstencil");
            if (f.exists()) {
                v.sendMessage(ChatColor.RED + "Stencil '" + this.Filename
                        + "' exists and was loaded.  Make sure you are using powder if you do not want any chance of overwriting the file.");
            } else {
                v.sendMessage(ChatColor.AQUA + "Stencil '" + this.Filename + "' does not exist.  Ready to be saved to, but cannot be pasted.");
            }
        } catch (final Exception e) {
            v.sendMessage(ChatColor.RED + "You need to type a stencil name.");
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Stencil.timesUsed = tUsed;
    }

    public final void stencilpaste(final vData v) {
        if (this.Filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename.  This is required.");
            return;
        }

        final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());
        final File f = new File("plugins/VoxelSniper/stencils/" + this.Filename + ".vstencil");

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
                v.sendMessage(ChatColor.RED + "Something went wrong.");
                // v.sendMessage("jspecial: " + jspecial);
                e.printStackTrace();
            }
        } else {
            v.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    public final void stencilsave(final vData v, final boolean reference) {

        final File f = new File("plugins/VoxelSniper/stencils/" + this.Filename + ".vstencil");
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            this.X = (short) (Math.abs((this.firstpoint[0] - this.secondpoint[0])) + 1);
            this.Z = (short) (Math.abs((this.firstpoint[1] - this.secondpoint[1])) + 1);
            this.Y = (short) (Math.abs((this.firstpoint[2] - this.secondpoint[2])) + 1);
            this.Xref = (short) ((this.firstpoint[0] > this.secondpoint[0]) ? (this.pastepoint[0] - this.secondpoint[0])
                    : (this.pastepoint[0] - this.firstpoint[0]));
            this.Zref = (short) ((this.firstpoint[1] > this.secondpoint[1]) ? (this.pastepoint[1] - this.secondpoint[1])
                    : (this.pastepoint[1] - this.firstpoint[1]));
            this.Yref = (short) ((this.firstpoint[2] > this.secondpoint[2]) ? (this.pastepoint[2] - this.secondpoint[2])
                    : (this.pastepoint[2] - this.firstpoint[2]));
            this.setBlockPositionX((this.firstpoint[0] > this.secondpoint[0]) ? this.secondpoint[0] : this.firstpoint[0]);
            this.setBlockPositionZ((this.firstpoint[1] > this.secondpoint[1]) ? this.secondpoint[1] : this.firstpoint[1]);
            this.setBlockPositionY((this.firstpoint[2] > this.secondpoint[2]) ? this.secondpoint[2] : this.firstpoint[2]);
            out.writeShort(this.X);
            out.writeShort(this.Z);
            out.writeShort(this.Y);
            out.writeShort(this.Xref);
            out.writeShort(this.Zref);
            out.writeShort(this.Yref);

            v.sendMessage(ChatColor.AQUA + "Volume: " + this.X * this.Z * this.Y + " blockPositionX:" + this.getBlockPositionX() + " blockPositionZ:" + this.getBlockPositionZ() + " blockPositionY:" + this.getBlockPositionY());

            this.blockArray = new byte[this.X * this.Z * this.Y];
            this.dataArray = new byte[this.X * this.Z * this.Y];
            this.runSizeArray = new byte[this.X * this.Z * this.Y];

            byte lastid = (byte) (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()) - 128);
            byte lastdata = (byte) (this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getData() - 128);
            byte thisid;
            byte thisdata;
            int counter = 0;
            int arrayIndex = 0;
            for (int y = 0; y < this.Y; y++) {
                for (int z = 0; z < this.Z; z++) {
                    for (int x = 0; x < this.X; x++) {
                        // v.sendMessage("should be checking a block at " + (blockPositionX + x) + " " + (blockPositionY + y) + " " + (blockPositionZ + z));
                        thisid = (byte) (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z) - 128);
                        // if (thisid > 0){v.sendMessage("high id saved. coords:" + x + "," + y + "," + z);}
                        thisdata = (byte) (this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z).getData() - 128);
                        if (thisid != lastid || thisdata != lastdata || counter == 255) {
                            // v.sendMessage("ended a run" + " thisid:" + thisid + " thisdata:" + thisdata + " lastid:" + lastid + " lastdata:" + lastdata);
                            this.blockArray[arrayIndex] = lastid;
                            this.dataArray[arrayIndex] = lastdata;
                            this.runSizeArray[arrayIndex] = (byte) (counter - 128);
                            arrayIndex++;
                            counter = 1;
                            lastid = thisid;
                            lastdata = thisdata;
                        } else {
                            counter++;
                            lastid = thisid;
                            lastdata = thisdata;
                        }
                    }
                }
            }
            this.blockArray[arrayIndex] = lastid; // saving last run, which will always be left over.
            this.dataArray[arrayIndex] = lastdata;
            this.runSizeArray[arrayIndex] = (byte) (counter - 128);

            out.writeInt(arrayIndex);
            // v.sendMessage("number of runs = " + arrayIndex);
            for (int i = 0; i < arrayIndex + 1; i++) {
                if (this.runSizeArray[i] > -127) {
                    out.writeBoolean(true);
                    out.writeByte(this.runSizeArray[i]);
                    out.writeByte(this.blockArray[i]);
                    out.writeByte(this.dataArray[i]);
                } else {
                    out.writeBoolean(false);
                    out.writeByte(this.blockArray[i]);
                    out.writeByte(this.dataArray[i]);
                }
            }

            v.sendMessage(ChatColor.BLUE + "Saved as '" + this.Filename + "'.");
            out.close();

        } catch (final Exception e) {
            v.sendMessage(ChatColor.RED + "Something went wrong.");
            e.printStackTrace();
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) { // will be used to copy/save later on?
        if (this.point == 1) {
            // if (v.p.isSneaking()) {
            // v.sendMessage(ChatColor.RED + "Must select at least two corners (reference point optional).");
            // } else {
            this.firstpoint[0] = this.getTargetBlock().getX();
            this.firstpoint[1] = this.getTargetBlock().getZ();
            this.firstpoint[2] = this.getTargetBlock().getY();
            v.sendMessage(ChatColor.GRAY + "First point");
            v.sendMessage("X:" + this.firstpoint[0] + " Z:" + this.firstpoint[1] + " Y:" + this.firstpoint[2]);
            this.point = 2;
            // }
        } else if (this.point == 2) {
            // if (v.p.isSneaking()) {
            // v.sendMessage(ChatColor.RED + "Must select at least two corners (reference point optional).");
            // } else {
            this.secondpoint[0] = this.getTargetBlock().getX();
            this.secondpoint[1] = this.getTargetBlock().getZ();
            this.secondpoint[2] = this.getTargetBlock().getY();
            if ((Math.abs(this.firstpoint[0] - this.secondpoint[0]) * Math.abs(this.firstpoint[1] - this.secondpoint[1]) * Math.abs(this.firstpoint[2]
                    - this.secondpoint[2])) > 5000000) {
                v.sendMessage(ChatColor.DARK_RED + "Area selected is too large. (Limit is 5,000,000 blocks)");
                this.point = 1;
            } else {
                v.sendMessage(ChatColor.GRAY + "Second point");
                v.sendMessage("X:" + this.secondpoint[0] + " Z:" + this.secondpoint[1] + " Y:" + this.secondpoint[2]);
                this.point = 3;
            }

            // }
        } else if (this.point == 3) {
            // if (v.p.isSneaking()) {

            // } else {
            this.pastepoint[0] = this.getTargetBlock().getX();
            this.pastepoint[1] = this.getTargetBlock().getZ();
            this.pastepoint[2] = this.getTargetBlock().getY();
            v.sendMessage(ChatColor.GRAY + "Paste Reference point");
            v.sendMessage("X:" + this.pastepoint[0] + " Z:" + this.pastepoint[1] + " Y:" + this.pastepoint[2]);
            this.point = 1;

            this.stencilsave(v, false);
            // }
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) { // will be used to paste later on
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.stencilpaste(v);
    }
}
