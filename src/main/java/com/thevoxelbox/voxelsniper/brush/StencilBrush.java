package com.thevoxelbox.voxelsniper.brush;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;


//LOVE THIS -psanker
/**
 * This is paste only currently. Assumes files exist, and thus has no usefulness until I add in saving stencils later. Uses sniper-exclusive
 * stencil format: 3 shorts for X,Z,Y size of cuboid 3 shorts for X,Z,Y offsets from the -X,-Z,-Y corner. This is the reference point for pasting,
 * corresponding to where you click your brush. 1 long integer saying how many runs of blocks are in the schematic (data is compressed into runs) 1
 * per run: ( 1 boolean: true = compressed line ahead, false = locally unique block ahead. This wastes a bit instead of a byte, and overall saves space,
 * as long as at least 1/8 of all RUNS are going to be size 1, which in Minecraft is almost definitely true. IF boolean was true, next unsigned byte
 * stores the number of consecutive blocks of the same type, up to 256. IF boolean was false, there is no byte here, goes straight to ID and data
 * instead, which applies to just one block. 2 bytes to identify type of block. First byte is ID, second is data. This applies to every one of the line
 * of consecutive blocks if boolean was true. )
 * @author Gavjenks 
 * 
 */
public class StencilBrush extends Brush {

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

    public StencilBrush() {
        this.setName("Stencil");
    }

    protected final void stencilPaste(final SnipeData v) {
        if (this.Filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename.  This is required.");
            return;
        }

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final File _file = new File("plugins/VoxelSniper/stencils/" + this.Filename + ".vstencil");

        if (_file.exists()) {
            try {
                final DataInputStream _in = new DataInputStream(new BufferedInputStream(new FileInputStream(_file)));

                this.X = _in.readShort();
                this.Z = _in.readShort();
                this.Y = _in.readShort();

                this.Xref = _in.readShort();
                this.Zref = _in.readShort();
                this.Yref = _in.readShort();

                final int _numRuns = _in.readInt();

                int _currX = -this.Xref; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int _currZ = -this.Zref;
                int _currY = -this.Yref;
                int _id;
                int _data;
                if (this.pasteoption == 0) {
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int _numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int _j = 0; _j < _numLoops; _j++) {
                                _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) _data, false);
                                _currX++;
                                if (_currX == this.X - this.Xref) {
                                    _currX = -this.Xref;
                                    _currZ++;
                                    if (_currZ == this.Z - this.Zref) {
                                        _currZ = -this.Zref;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                            this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData((_in.readByte() + 128),
                                    (byte) (_in.readByte() + 128), false);
                            _currX++;
                            if (_currX == this.X - this.Xref) {
                                _currX = -this.Xref;
                                _currZ++;
                                if (_currZ == this.Z - this.Zref) {
                                    _currZ = -this.Zref;
                                    _currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteoption == 1) {
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int _numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int _j = 0; _j < _numLoops; _j++) {
                                if (_id != 0 && this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                                  // air, and it prevents us
                                                                                                                                  // most of the time from
                                                                                                                                  // having to even check the
                                                                                                                                  // block.
                                    _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                    this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                                }
                                _currX++;
                                if (_currX == this.X - this.Xref) {
                                    _currX = -this.Xref;
                                    _currZ++;
                                    if (_currZ == this.Z - this.Zref) {
                                        _currZ = -this.Zref;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            if (_id != 0 && this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                              // air, and it prevents us most of
                                                                                                                              // the time from having to even
                                                                                                                              // check the block.
                                _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                // v.sendMessage("currX:" + currX + " currZ:"+currZ + " currY:" + currY + " id:" + id + " data:" + (byte)data);
                                this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                            }
                            _currX++;
                            if (_currX == this.X - this.Xref) {
                                _currX = -this.Xref;
                                _currZ++;
                                if (_currZ == this.Z - this.Zref) {
                                    _currZ = -this.Zref;
                                    _currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int j = 0; j < (numLoops); j++) {
                                if (_id != 0) {
                                    _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                    this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) _data, false);
                                }
                                _currX++;
                                if (_currX == this.X - this.Xref) {
                                    _currX = -this.Xref;
                                    _currZ++;
                                    if (_currZ == this.Z - this.Zref) {
                                        _currZ = -this.Zref;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            if (_id != 0) {
                                _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) _data, false);
                            }
                            _currX++;
                            if (_currX == this.X) {
                                _currX = 0;
                                _currZ++;
                                if (_currZ == this.Z) {
                                    _currZ = 0;
                                    _currY++;
                                }
                            }
                        }
                    }
                }
                _in.close();
                v.storeUndo(_undo);

            } catch (final Exception _e) {
                v.sendMessage(ChatColor.RED + "Something went wrong.");
                _e.printStackTrace();
            }
        } else {
            v.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    protected final void stencilSave(final SnipeData v, final boolean reference) {

        final File _file = new File("plugins/VoxelSniper/stencils/" + this.Filename + ".vstencil");
        try {
            _file.getParentFile().mkdirs();
            _file.createNewFile();
            final DataOutputStream _out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(_file)));
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
            _out.writeShort(this.X);
            _out.writeShort(this.Z);
            _out.writeShort(this.Y);
            _out.writeShort(this.Xref);
            _out.writeShort(this.Zref);
            _out.writeShort(this.Yref);

            v.sendMessage(ChatColor.AQUA + "Volume: " + this.X * this.Z * this.Y + " blockPositionX:" + this.getBlockPositionX() + " blockPositionZ:" + this.getBlockPositionZ() + " blockPositionY:" + this.getBlockPositionY());

            this.blockArray = new byte[this.X * this.Z * this.Y];
            this.dataArray = new byte[this.X * this.Z * this.Y];
            this.runSizeArray = new byte[this.X * this.Z * this.Y];

            byte _lastid = (byte) (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()) - 128);
            byte _lastdata = (byte) (this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getData() - 128);
            byte _thisid;
            byte _thisdata;
            int _counter = 0;
            int _arrayIndex = 0;
            for (int _y = 0; _y < this.Y; _y++) {
                for (int _z = 0; _z < this.Z; _z++) {
                    for (int _x = 0; _x < this.X; _x++) {
                        _thisid = (byte) (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z) - 128);
                        _thisdata = (byte) (this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z).getData() - 128);
                        if (_thisid != _lastid || _thisdata != _lastdata || _counter == 255) {
                            this.blockArray[_arrayIndex] = _lastid;
                            this.dataArray[_arrayIndex] = _lastdata;
                            this.runSizeArray[_arrayIndex] = (byte) (_counter - 128);
                            _arrayIndex++;
                            _counter = 1;
                            _lastid = _thisid;
                            _lastdata = _thisdata;
                        } else {
                            _counter++;
                            _lastid = _thisid;
                            _lastdata = _thisdata;
                        }
                    }
                }
            }
            this.blockArray[_arrayIndex] = _lastid; // saving last run, which will always be left over.
            this.dataArray[_arrayIndex] = _lastdata;
            this.runSizeArray[_arrayIndex] = (byte) (_counter - 128);

            _out.writeInt(_arrayIndex);
            // v.sendMessage("number of runs = " + arrayIndex);
            for (int _i = 0; _i < _arrayIndex + 1; _i++) {
                if (this.runSizeArray[_i] > -127) {
                    _out.writeBoolean(true);
                    _out.writeByte(this.runSizeArray[_i]);
                    _out.writeByte(this.blockArray[_i]);
                    _out.writeByte(this.dataArray[_i]);
                } else {
                    _out.writeBoolean(false);
                    _out.writeByte(this.blockArray[_i]);
                    _out.writeByte(this.dataArray[_i]);
                }
            }

            v.sendMessage(ChatColor.BLUE + "Saved as '" + this.Filename + "'.");
            _out.close();

        } catch (final Exception _e) {
            v.sendMessage(ChatColor.RED + "Something went wrong.");
            _e.printStackTrace();
        }
    }

    @Override
    protected final void arrow(final SnipeData v) { // will be used to copy/save later on?
        if (this.point == 1) {
            this.firstpoint[0] = this.getTargetBlock().getX();
            this.firstpoint[1] = this.getTargetBlock().getZ();
            this.firstpoint[2] = this.getTargetBlock().getY();
            v.sendMessage(ChatColor.GRAY + "First point");
            v.sendMessage("X:" + this.firstpoint[0] + " Z:" + this.firstpoint[1] + " Y:" + this.firstpoint[2]);
            this.point = 2;
        } else if (this.point == 2) {
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
        } else if (this.point == 3) {
            this.pastepoint[0] = this.getTargetBlock().getX();
            this.pastepoint[1] = this.getTargetBlock().getZ();
            this.pastepoint[2] = this.getTargetBlock().getY();
            v.sendMessage(ChatColor.GRAY + "Paste Reference point");
            v.sendMessage("X:" + this.pastepoint[0] + " Z:" + this.pastepoint[1] + " Y:" + this.pastepoint[2]);
            this.point = 1;

            this.stencilSave(v, false);
        }
    }

    @Override
    protected final void powder(final SnipeData v) { // will be used to paste later on
        this.stencilPaste(v);
    }
    

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom("File loaded: " + this.Filename);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
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
            final File _file = new File("plugins/VoxelSniper/stencils/" + this.Filename + ".vstencil");
            if (_file.exists()) {
                v.sendMessage(ChatColor.RED + "Stencil '" + this.Filename
                        + "' exists and was loaded.  Make sure you are using powder if you do not want any chance of overwriting the file.");
            } else {
                v.sendMessage(ChatColor.AQUA + "Stencil '" + this.Filename + "' does not exist.  Ready to be saved to, but cannot be pasted.");
            }
        } catch (final Exception _ex) {
            v.sendMessage(ChatColor.RED + "You need to type a stencil name.");
        }
    }
    
    @Override
    public final int getTimesUsed() {
    	return StencilBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        StencilBrush.timesUsed = tUsed;
    }

}
