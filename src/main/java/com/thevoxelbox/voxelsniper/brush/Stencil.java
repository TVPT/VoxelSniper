package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.io.*;
import org.bukkit.ChatColor;

/**
 *
 * @author Gavjenks
 * This is paste only currently. Assumes files exist, and thus has no usefulness until I add in saving stencils later.
 * Uses sniper-exclusive stencil format:
 * 3 shorts for X,Z,Y size of cuboid
 * 3 shorts for X,Z,Y offsets from the -X,-Z,-Y corner. This is the reference point for pasting, corresponding to where you click your brush.
 * 1 long integer saying how many runs of blocks are in the schematic (data is compressed into runs)
 * 1
 *
 * per run:
 * (
 * 1 boolean: true = compressed line ahead, false = locally unique block ahead. This wastes a bit instead of a byte, and overall saves space, as long as at least 1/8 of all RUNS are going to be size 1, which in Minecraft is almost definitely true.
 * IF boolean was true, next unsigned byte stores the number of consecutive blocks of the same type, up to 256.
 * IF boolean was false, there is no byte here, goes straight to ID and data instead, which applies to just one block.
 * 2 bytes to identify type of block. First byte is ID, second is data. This applies to every one of the line of consecutive blocks if boolean was true.
 * )
 *
 */
// LOVE THIS -psanker
public class Stencil extends Brush {

    protected byte pasteoption = 1; //0 = full, 1 = fill, 2 = replace
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

    public Stencil() {
        name = "Stencil";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {  //will be used to copy/save later on?
        if (point == 1) {
            //if (v.p.isSneaking()) {
            //    v.sendMessage(ChatColor.RED + "Must select at least two corners (reference point optional).");
            //} else {
            firstpoint[0] = tb.getX();
            firstpoint[1] = tb.getZ();
            firstpoint[2] = tb.getY();
            v.sendMessage(ChatColor.GRAY + "First point");
            v.sendMessage("X:" + firstpoint[0] + " Z:" + firstpoint[1] + " Y:" + firstpoint[2]);
            point = 2;
            //}
        } else if (point == 2) {
            //if (v.p.isSneaking()) {
            //    v.sendMessage(ChatColor.RED + "Must select at least two corners (reference point optional).");
            //} else {
            secondpoint[0] = tb.getX();
            secondpoint[1] = tb.getZ();
            secondpoint[2] = tb.getY();
            if((Math.abs(firstpoint[0]-secondpoint[0]) * Math.abs(firstpoint[1]-secondpoint[1]) * Math.abs(firstpoint[2]-secondpoint[2]) ) > 5000000) {
            	v.sendMessage(ChatColor.DARK_RED + "Area selected is too large. (Limit is 5,000,000 blocks)");
            	point = 1;
            } else {
            	v.sendMessage(ChatColor.GRAY + "Second point");
	            v.sendMessage("X:" + secondpoint[0] + " Z:" + secondpoint[1] + " Y:" + secondpoint[2]);
	            point = 3;
            }
	           
            //}
        } else if (point == 3) {
            //if (v.p.isSneaking()) {

            //} else {
            pastepoint[0] = tb.getX();
            pastepoint[1] = tb.getZ();
            pastepoint[2] = tb.getY();
            v.sendMessage(ChatColor.GRAY + "Paste Reference point");
            v.sendMessage("X:" + pastepoint[0] + " Z:" + pastepoint[1] + " Y:" + pastepoint[2]);
            point = 1;

            stencilsave(v, false);
            //}
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {   //will be used to paste later on
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        stencilpaste(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.custom("File loaded: " + Filename);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Stencil brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b schem [optional: 'full' 'fill' or 'replace', with fill as default] [name] -- Loads the specified schematic.  Allowed size of schematic is based on rank.  Full/fill/replace must come first.  Full = paste all blocks, fill = paste only into air blocks, replace = paste full blocks in only, but replace anything in their way.");
            v.sendMessage(ChatColor.BLUE + "Size of the stencils you are allowed to paste depends on rank (member / lite, sniper, curator, admin)");
            return;
        } else if (par[1].equalsIgnoreCase("full")) {
            pasteoption = 0;
            pasteparam = 1;
        } else if (par[1].equalsIgnoreCase("fill")) {
            pasteoption = 1;
            pasteparam = 1;
        } else if (par[1].equalsIgnoreCase("replace")) {
            pasteoption = 2;
            pasteparam = 1;
        }
        try {
            Filename = par[1 + pasteparam];
            File f = new File("plugins/VoxelSniper/stencils/" + Filename + ".vstencil");
            if (f.exists()) {
                v.sendMessage(ChatColor.RED + "Stencil '" + Filename + "' exists and was loaded.  Make sure you are using powder if you do not want any chance of overwriting the file.");
            } else {
                v.sendMessage(ChatColor.AQUA + "Stencil '" + Filename + "' does not exist.  Ready to be saved to, but cannot be pasted.");
            }
        } catch (Exception e) {
            v.sendMessage(ChatColor.RED + "You need to type a stencil name.");
        }
    }

    public void stencilpaste(vData v) {
        if (Filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename.  This is required.");
            return;
        }

        vUndo h = new vUndo(tb.getWorld().getName());
        File f = new File("plugins/VoxelSniper/stencils/" + Filename + ".vstencil");

        if (f.exists()) {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

                X = in.readShort();
                Z = in.readShort();
                Y = in.readShort();

                Xref = in.readShort();
                Zref = in.readShort();
                Yref = in.readShort();

                int numRuns = in.readInt();

                int currX = -Xref; //so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the corner, for example.
                int currZ = -Zref;
                int currY = -Yref;
                int id;
                int data;
                if (pasteoption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                h.put(clampY(bx + currX, by + currY, bz + currZ));
                                clampY(bx + currX, by + currY, bz + currZ).setTypeIdAndData(id, (byte) data, false);
                                currX++;
                                if (currX == X - Xref) {
                                    currX = -Xref;
                                    currZ++;
                                    if (currZ == Z - Zref) {
                                        currZ = -Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            h.put(clampY(bx + currX, by + currY, bz + currZ));
                            clampY(bx + currX, by + currY, bz + currZ).setTypeIdAndData((in.readByte() + 128), (byte) (in.readByte() + 128), false);
                            currX++;
                            if (currX == X - Xref) {
                                currX = -Xref;
                                currZ++;
                                if (currZ == Z - Zref) {
                                    currZ = -Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (pasteoption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                if (id != 0 && clampY(bx + currX, by + currY, bz + currZ).getTypeId() == 0) { //no reason to paste air over air, and it prevents us most of the time from having to even check the block.
                                    h.put(clampY(bx + currX, by + currY, bz + currZ));
                                    clampY(bx + currX, by + currY, bz + currZ).setTypeIdAndData(id, (byte) (data), false);
                                }
                                currX++;
                                if (currX == X - Xref) {
                                    currX = -Xref;
                                    currZ++;
                                    if (currZ == Z - Zref) {
                                        currZ = -Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            if (id != 0 && clampY(bx + currX, by + currY, bz + currZ).getTypeId() == 0) { //no reason to paste air over air, and it prevents us most of the time from having to even check the block.
                                h.put(clampY(bx + currX, by + currY, bz + currZ));
                                //v.sendMessage("currX:" + currX + " currZ:"+currZ + " currY:" + currY + " id:" + id + " data:" + (byte)data);
                                clampY(bx + currX, by + currY, bz + currZ).setTypeIdAndData(id, (byte) (data), false);
                            }
                            currX++;
                            if (currX == X - Xref) {
                                currX = -Xref;
                                currZ++;
                                if (currZ == Z - Zref) {
                                    currZ = -Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { //replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            for (int j = 0; j < (numLoops); j++) {
                                if (id != 0) {
                                    h.put(clampY(bx + currX, by + currY, bz + currZ));
                                    clampY(bx + currX, by + currY, bz + currZ).setTypeIdAndData(id, (byte) data, false);
                                }
                                currX++;
                                if (currX == X - Xref) {
                                    currX = -Xref;
                                    currZ++;
                                    if (currZ == Z - Zref) {
                                        currZ = -Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            id = (in.readByte() + 128);
                            data = (in.readByte() + 128);
                            if (id != 0) {
                                h.put(clampY(bx + currX, by + currY, bz + currZ));
                                clampY(bx + currX, by + currY, bz + currZ).setTypeIdAndData(id, (byte) data, false);
                            }
                            currX++;
                            if (currX == X) {
                                currX = 0;
                                currZ++;
                                if (currZ == Z) {
                                    currZ = 0;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
                v.storeUndo(h);

            } catch (Exception e) {
                v.sendMessage(ChatColor.RED + "Something went wrong.");
                //v.sendMessage("jspecial: " + jspecial);
                e.printStackTrace();
            }
        } else {
            v.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    public void stencilsave(vData v, boolean reference) {

        File f = new File("plugins/VoxelSniper/stencils/" + Filename + ".vstencil");
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            X = (short) (Math.abs((firstpoint[0] - secondpoint[0])) + 1);
            Z = (short) (Math.abs((firstpoint[1] - secondpoint[1])) + 1);
            Y = (short) (Math.abs((firstpoint[2] - secondpoint[2])) + 1);
            Xref = (short) ((firstpoint[0] > secondpoint[0]) ? (pastepoint[0] - secondpoint[0]) : (pastepoint[0] - firstpoint[0]));
            Zref = (short) ((firstpoint[1] > secondpoint[1]) ? (pastepoint[1] - secondpoint[1]) : (pastepoint[1] - firstpoint[1]));
            Yref = (short) ((firstpoint[2] > secondpoint[2]) ? (pastepoint[2] - secondpoint[2]) : (pastepoint[2] - firstpoint[2]));
            bx = (firstpoint[0] > secondpoint[0]) ? secondpoint[0] : firstpoint[0];
            bz = (firstpoint[1] > secondpoint[1]) ? secondpoint[1] : firstpoint[1];
            by = (firstpoint[2] > secondpoint[2]) ? secondpoint[2] : firstpoint[2];
            out.writeShort(X);
            out.writeShort(Z);
            out.writeShort(Y);
            out.writeShort(Xref);
            out.writeShort(Zref);
            out.writeShort(Yref);

            v.sendMessage(ChatColor.AQUA + "Volume: " + X * Z * Y + " bx:" + bx + " bz:" + bz + " by:" + by);

            blockArray = new byte[X * Z * Y];
            dataArray = new byte[X * Z * Y];
            runSizeArray = new byte[X * Z * Y];

            byte lastid = (byte) (w.getBlockTypeIdAt(bx, by, bz) - 128);
            byte lastdata = (byte) (clampY(bx, by, bz).getData() - 128);
            byte thisid;
            byte thisdata;
            int counter = 0;
            int arrayIndex = 0;
            for (int y = 0; y < Y; y++) {
                for (int z = 0; z < Z; z++) {
                    for (int x = 0; x < X; x++) {
                        //v.sendMessage("should be checking a block at " + (bx + x) + " " + (by + y) + " " + (bz + z));
                        thisid = (byte) (w.getBlockTypeIdAt(bx + x, by + y, bz + z) - 128);
                        //if (thisid > 0){v.sendMessage("high id saved. coords:" + x + "," + y + "," + z);}
                        thisdata = (byte) (clampY(bx + x, by + y, bz + z).getData() - 128);
                        if (thisid != lastid || thisdata != lastdata || counter == 255) {
                            //v.sendMessage("ended a run" + " thisid:" + thisid + " thisdata:" + thisdata + " lastid:" + lastid + " lastdata:" + lastdata);
                            blockArray[arrayIndex] = lastid;
                            dataArray[arrayIndex] = lastdata;
                            runSizeArray[arrayIndex] = (byte) (counter - 128);
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
            blockArray[arrayIndex] = lastid; //saving last run, which will always be left over.
            dataArray[arrayIndex] = lastdata;
            runSizeArray[arrayIndex] = (byte) (counter - 128);

            out.writeInt(arrayIndex);
            //v.sendMessage("number of runs = " + arrayIndex);
            for (int i = 0; i < arrayIndex + 1; i++) {
                if (runSizeArray[i] > -127) {
                    out.writeBoolean(true);
                    out.writeByte(runSizeArray[i]);
                    out.writeByte(blockArray[i]);
                    out.writeByte(dataArray[i]);
                } else {
                    out.writeBoolean(false);
                    out.writeByte(blockArray[i]);
                    out.writeByte(dataArray[i]);
                }
            }


            v.sendMessage(ChatColor.BLUE + "Saved as '" + Filename + "'.");
            out.close();

        } catch (Exception e) {
            v.sendMessage(ChatColor.RED + "Something went wrong.");
            e.printStackTrace();
        }
    }
}
