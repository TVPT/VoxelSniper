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

    private byte pasteOption = 1; // 0 = full, 1 = fill, 2 = replace
    private String filename = "NoFileLoaded";
    private short x;
    private short z;
    private short y;
    private short xRef;
    private short zRef;
    private short yRef;
    private byte pasteParam = 0;
    private byte[] blockArray;
    private byte[] dataArray;
    private byte[] runSizeArray;
    private int[] firstPoint = new int[3];
    private int[] secondPoint = new int[3];
    private int[] pastePoint = new int[3];
    private byte point = 1;

    private static int timesUsed = 0;

    /**
     * 
     */
    public StencilBrush() {
        this.setName("Stencil");
    }

    private final void stencilPaste(final SnipeData v) {
        if (this.filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename.  This is required.");
            return;
        }

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final File _file = new File("plugins/VoxelSniper/stencils/" + this.filename + ".vstencil");

        if (_file.exists()) {
            try {
                final DataInputStream _in = new DataInputStream(new BufferedInputStream(new FileInputStream(_file)));

                this.x = _in.readShort();
                this.z = _in.readShort();
                this.y = _in.readShort();

                this.xRef = _in.readShort();
                this.zRef = _in.readShort();
                this.yRef = _in.readShort();

                final int _numRuns = _in.readInt();

                int _currX = -this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int _currZ = -this.zRef;
                int _currY = -this.yRef;
                int _id;
                int _data;
                if (this.pasteOption == 0) {
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int _numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int _j = 0; _j < _numLoops; _j++) {
                                _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) _data, false);
                                _currX++;
                                if (_currX == this.x - this.xRef) {
                                    _currX = -this.xRef;
                                    _currZ++;
                                    if (_currZ == this.z - this.zRef) {
                                        _currZ = -this.zRef;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                            this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData((_in.readByte() + 128),
                                    (byte) (_in.readByte() + 128), false);
                            _currX++;
                            if (_currX == this.x - this.xRef) {
                                _currX = -this.xRef;
                                _currZ++;
                                if (_currZ == this.z - this.zRef) {
                                    _currZ = -this.zRef;
                                    _currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteOption == 1) {
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int _numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int _j = 0; _j < _numLoops; _j++) {
                                if (_id != 0 && this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).getTypeId() == 0) {
                                    _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                    this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                                }
                                _currX++;
                                if (_currX == this.x - this.xRef) {
                                    _currX = -this.xRef;
                                    _currZ++;
                                    if (_currZ == this.z - this.zRef) {
                                        _currZ = -this.zRef;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            if (_id != 0 && this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).getTypeId() == 0) {
                                _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                // v.sendMessage("currX:" + currX + " currZ:"+currZ + " currY:" + currY + " id:" + id + " data:" + (byte)data);
                                this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                            }
                            _currX++;
                            if (_currX == this.x - this.xRef) {
                                _currX = -this.xRef;
                                _currZ++;
                                if (_currZ == this.z - this.zRef) {
                                    _currZ = -this.zRef;
                                    _currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int _numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int _j = 0; _j < (_numLoops); _j++) {
                                if (_id != 0) {
                                    _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                    this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) _data, false);
                                }
                                _currX++;
                                if (_currX == this.x - this.xRef) {
                                    _currX = -this.xRef;
                                    _currZ++;
                                    if (_currZ == this.z - this.zRef) {
                                        _currZ = -this.zRef;
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
                            if (_currX == this.x) {
                                _currX = 0;
                                _currZ++;
                                if (_currZ == this.z) {
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

    private final void stencilSave(final SnipeData v, final boolean reference) {

        final File _file = new File("plugins/VoxelSniper/stencils/" + this.filename + ".vstencil");
        try {
            _file.getParentFile().mkdirs();
            _file.createNewFile();
            final DataOutputStream _out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(_file)));
            this.x = (short) (Math.abs((this.firstPoint[0] - this.secondPoint[0])) + 1);
            this.z = (short) (Math.abs((this.firstPoint[1] - this.secondPoint[1])) + 1);
            this.y = (short) (Math.abs((this.firstPoint[2] - this.secondPoint[2])) + 1);
            this.xRef = (short) ((this.firstPoint[0] > this.secondPoint[0]) ? (this.pastePoint[0] - this.secondPoint[0])
                    : (this.pastePoint[0] - this.firstPoint[0]));
            this.zRef = (short) ((this.firstPoint[1] > this.secondPoint[1]) ? (this.pastePoint[1] - this.secondPoint[1])
                    : (this.pastePoint[1] - this.firstPoint[1]));
            this.yRef = (short) ((this.firstPoint[2] > this.secondPoint[2]) ? (this.pastePoint[2] - this.secondPoint[2])
                    : (this.pastePoint[2] - this.firstPoint[2]));
            this.setBlockPositionX((this.firstPoint[0] > this.secondPoint[0]) ? this.secondPoint[0] : this.firstPoint[0]);
            this.setBlockPositionZ((this.firstPoint[1] > this.secondPoint[1]) ? this.secondPoint[1] : this.firstPoint[1]);
            this.setBlockPositionY((this.firstPoint[2] > this.secondPoint[2]) ? this.secondPoint[2] : this.firstPoint[2]);
            _out.writeShort(this.x);
            _out.writeShort(this.z);
            _out.writeShort(this.y);
            _out.writeShort(this.xRef);
            _out.writeShort(this.zRef);
            _out.writeShort(this.yRef);

            v.sendMessage(ChatColor.AQUA + "Volume: " + this.x * this.z * this.y + " blockPositionX:" + this.getBlockPositionX() + " blockPositionZ:" + this.getBlockPositionZ() + " blockPositionY:" + this.getBlockPositionY());

            this.blockArray = new byte[this.x * this.z * this.y];
            this.dataArray = new byte[this.x * this.z * this.y];
            this.runSizeArray = new byte[this.x * this.z * this.y];

            byte _lastid = (byte) (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()) - 128);
            byte _lastdata = (byte) (this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getData() - 128);
            byte _thisid;
            byte _thisdata;
            int _counter = 0;
            int _arrayIndex = 0;
            for (int _y = 0; _y < this.y; _y++) {
                for (int _z = 0; _z < this.z; _z++) {
                    for (int _x = 0; _x < this.x; _x++) {
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

            v.sendMessage(ChatColor.BLUE + "Saved as '" + this.filename + "'.");
            _out.close();

        } catch (final Exception _e) {
            v.sendMessage(ChatColor.RED + "Something went wrong.");
            _e.printStackTrace();
        }
    }

    @Override
    protected final void arrow(final SnipeData v) { // will be used to copy/save later on?
        if (this.point == 1) {
            this.firstPoint[0] = this.getTargetBlock().getX();
            this.firstPoint[1] = this.getTargetBlock().getZ();
            this.firstPoint[2] = this.getTargetBlock().getY();
            v.sendMessage(ChatColor.GRAY + "First point");
            v.sendMessage("X:" + this.firstPoint[0] + " Z:" + this.firstPoint[1] + " Y:" + this.firstPoint[2]);
            this.point = 2;
        } else if (this.point == 2) {
            this.secondPoint[0] = this.getTargetBlock().getX();
            this.secondPoint[1] = this.getTargetBlock().getZ();
            this.secondPoint[2] = this.getTargetBlock().getY();
            if ((Math.abs(this.firstPoint[0] - this.secondPoint[0]) * Math.abs(this.firstPoint[1] - this.secondPoint[1]) * Math.abs(this.firstPoint[2]
                    - this.secondPoint[2])) > 5000000) {
                v.sendMessage(ChatColor.DARK_RED + "Area selected is too large. (Limit is 5,000,000 blocks)");
                this.point = 1;
            } else {
                v.sendMessage(ChatColor.GRAY + "Second point");
                v.sendMessage("X:" + this.secondPoint[0] + " Z:" + this.secondPoint[1] + " Y:" + this.secondPoint[2]);
                this.point = 3;
            }
        } else if (this.point == 3) {
            this.pastePoint[0] = this.getTargetBlock().getX();
            this.pastePoint[1] = this.getTargetBlock().getZ();
            this.pastePoint[2] = this.getTargetBlock().getY();
            v.sendMessage(ChatColor.GRAY + "Paste Reference point");
            v.sendMessage("X:" + this.pastePoint[0] + " Z:" + this.pastePoint[1] + " Y:" + this.pastePoint[2]);
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
        vm.custom("File loaded: " + this.filename);
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
            final File _file = new File("plugins/VoxelSniper/stencils/" + this.filename + ".vstencil");
            if (_file.exists()) {
                v.sendMessage(ChatColor.RED + "Stencil '" + this.filename
                        + "' exists and was loaded.  Make sure you are using powder if you do not want any chance of overwriting the file.");
            } else {
                v.sendMessage(ChatColor.AQUA + "Stencil '" + this.filename + "' does not exist.  Ready to be saved to, but cannot be pasted.");
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
