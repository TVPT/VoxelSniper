package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Scanner;
import org.bukkit.ChatColor;

/**
 *
 * @author Gavjenks
 */
public class StencilList extends Brush {

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
    protected byte rotation = 0;
    protected HashMap<Integer, String> stencilList = new HashMap<Integer, String>();
    protected byte point = 1;

    public StencilList() {
        name = "StencilList";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {  //will be used to copy/save later on?
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        stencilPaste(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {   //will be used to paste later on
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        stencilPasteRotation(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.custom("File loaded: " + Filename);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Stencil List brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b schem [optional: 'full' 'fill' or 'replace', with fill as default] [name] -- Loads the specified stencil list.  Full/fill/replace must come first.  Full = paste all blocks, fill = paste only into air blocks, replace = paste full blocks in only, but replace anything in their way.");
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
            File f = new File("plugins/VoxelSniper/stencilLists/" + Filename + ".txt");
            if (f.exists()) {
                v.sendMessage(ChatColor.RED + "Stencil List '" + Filename + "' exists and was loaded.");
                readStencilList(Filename, v);
            } else {
                v.sendMessage(ChatColor.AQUA + "Stencil List '" + Filename + "' does not exist.  This brush will not function without a valid stencil list.");
                Filename = "NoFileLoaded";
            }
        } catch (Exception e) {
            v.sendMessage(ChatColor.RED + "You need to type a stencil name.");
        }
    }

    public void stencilPaste(vData v) {
        if (Filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        String stencilName = readRandomStencil(v);
        v.sendMessage(stencilName);

        vUndo h = new vUndo(tb.getWorld().getName());
        File f = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");

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
                //Something here that checks ranks using sanker'w thingie he added to vSniper and boots you out with error message if too big.
                int volume = X * Y * Z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + Filename + " pasted.  Volume is " + volume + " blocks.");

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
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                //v.sendMessage("jspecial: " + jspecial);
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    public void stencilPaste90(vData v) {
        if (Filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        String stencilName = readRandomStencil(v);

        vUndo h = new vUndo(tb.getWorld().getName());
        File f = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");

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
                //Something here that checks ranks using sanker'w thingie he added to vSniper and boots you out with error message if too big.
                int volume = X * Y * Z;
                v.sendMessage(ChatColor.AQUA + Filename + " pasted.  Volume is " + volume + " blocks.");

                int currX = -Zref; //so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the corner, for example.
                int currZ = +Xref;
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
                                currZ--;
                                if (currZ == -X + Xref) {
                                    currZ = Xref;
                                    currX++;
                                    if (currX == Z - Zref) {
                                        currX = -Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            h.put(clampY(bx + currX, by + currY, bz + currZ));
                            clampY(bx + currX, by + currY, bz + currZ).setTypeIdAndData((in.readByte() + 128), (byte) (in.readByte() + 128), false);
                            currZ--;
                            if (currZ == -X + Xref) {
                                currZ = Xref;
                                currX++;
                                if (currX == Z - Zref) {
                                    currX = -Zref;
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
                                currZ--;
                                if (currZ == -X + Xref) {
                                    currZ = Xref;
                                    currX++;
                                    if (currX == Z - Zref) {
                                        currX = -Zref;
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
                            currZ--;
                            if (currZ == -X + Xref) {
                                currZ = Xref;
                                currX++;
                                if (currX == Z - Zref) {
                                    currX = -Zref;
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
                                currZ--;
                                if (currZ == -X + Xref) {
                                    currZ = Xref;
                                    currX++;
                                    if (currX == Z - Zref) {
                                        currX = -Zref;
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
                            currZ--;
                            if (currZ == -X + Xref) {
                                currZ = Xref;
                                currX++;
                                if (currX == Z - Zref) {
                                    currX = -Zref;
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
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    public void stencilPaste180(vData v) {
        if (Filename.matches("NoFileLoaded")) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        String stencilName = readRandomStencil(v);

        vUndo h = new vUndo(tb.getWorld().getName());
        File f = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");

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
                //Something here that checks ranks using sanker'w thingie he added to vSniper and boots you out with error message if too big.
                int volume = X * Y * Z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + Filename + " pasted.  Volume is " + volume + " blocks.");

                int currX = +Xref; //so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the corner, for example.
                int currZ = +Zref;
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
                                currX--;
                                if (currX == -X + Xref) {
                                    currX = Xref;
                                    currZ--;
                                    if (currZ == -Z + Zref) {
                                        currZ = +Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            h.put(clampY(bx + currX, by + currY, bz + currZ));
                            clampY(bx + currX, by + currY, bz + currZ).setTypeIdAndData((in.readByte() + 128), (byte) (in.readByte() + 128), false);
                            currX--;
                            if (currX == -X + Xref) {
                                currX = Xref;
                                currZ--;
                                if (currZ == -Z + Zref) {
                                    currZ = +Zref;
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
                                currX--;
                                if (currX == -X + Xref) {
                                    currX = Xref;
                                    currZ--;
                                    if (currZ == -Z + Zref) {
                                        currZ = +Zref;
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
                            currX--;
                            if (currX == -X + Xref) {
                                currX = Xref;
                                currZ--;
                                if (currZ == -Z + Zref) {
                                    currZ = +Zref;
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
                                currX--;
                                if (currX == -X + Xref) {
                                    currX = Xref;
                                    currZ--;
                                    if (currZ == -Z + Zref) {
                                        currZ = +Zref;
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
                            currX--;
                            if (currX == -X + Xref) {
                                currX = Xref;
                                currZ--;
                                if (currZ == -Z + Zref) {
                                    currZ = +Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
                v.storeUndo(h);

            } catch (Exception e) {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                //v.sendMessage("jspecial: " + jspecial);
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    public void stencilPaste270(vData v) {
        if (Filename.matches("NoFileLoaded")) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        String stencilName = readRandomStencil(v);

        vUndo h = new vUndo(tb.getWorld().getName());
        File f = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");

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
                //Something here that checks ranks using sanker'w thingie he added to vSniper and boots you out with error message if too big.
                int volume = X * Y * Z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + Filename + " pasted.  Volume is " + volume + " blocks.");

                int currX = +Zref; //so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the corner, for example.
                int currZ = -Xref;
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
                                currZ++;
                                if (currZ == X - Xref) {
                                    currZ = -Xref;
                                    currX--;
                                    if (currX == -Z + Zref) {
                                        currX = +Zref;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            h.put(clampY(bx + currX, by + currY, bz + currZ));
                            clampY(bx + currX, by + currY, bz + currZ).setTypeIdAndData((in.readByte() + 128), (byte) (in.readByte() + 128), false);
                            currZ++;
                            currZ++;
                            if (currZ == X - Xref) {
                                currZ = -Xref;
                                currX--;
                                if (currX == -Z + Zref) {
                                    currX = +Zref;
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
                                currZ++;
                                if (currZ == X - Xref) {
                                    currZ = -Xref;
                                    currX--;
                                    if (currX == -Z + Zref) {
                                        currX = +Zref;
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
                            currZ++;
                            if (currZ == X - Xref) {
                                currZ = -Xref;
                                currX--;
                                if (currX == -Z + Zref) {
                                    currX = +Zref;
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
                                currZ++;
                                if (currZ == X - Xref) {
                                    currZ = -Xref;
                                    currX--;
                                    if (currX == -Z + Zref) {
                                        currX = +Zref;
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
                            currZ++;
                            if (currZ == X - Xref) {
                                currZ = -Xref;
                                currX--;
                                if (currX == -Z + Zref) {
                                    currX = +Zref;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
                v.storeUndo(h);

            } catch (Exception e) {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                //v.sendMessage("jspecial: " + jspecial);
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    public void stencilPasteRotation(vData v) {
        //just randomly chooses a rotation and then calls stencilPaste.
        readStencilList(Filename, v);
        double rand = Math.random();
        if (rand < 0.26) {
            stencilPaste(v);
        } else if (rand < 0.51) {
            stencilPaste90(v);
        } else if (rand < 0.76) {
            stencilPaste180(v);
        } else {
            stencilPaste270(v);
        }

    }

    public void readStencilList(String listname, vData v) {
        File f = new File("plugins/VoxelSniper/stencilLists/" + Filename + ".txt");
        if (f.exists()) {
            try {
                Scanner snr = new Scanner(f);
                int counter = 0;
                while (snr.hasNext()) {
                    stencilList.put(counter, snr.nextLine());
                    counter++;
                }
                snr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String readRandomStencil(vData v) {
        double rand = Math.random();
        rand = rand * (stencilList.size());
        int choice = (int) (rand);
        return stencilList.get(choice);
    }
}
