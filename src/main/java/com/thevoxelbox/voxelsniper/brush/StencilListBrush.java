package com.thevoxelbox.voxelsniper.brush;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Gavjenks
 */
public class StencilListBrush extends Brush {

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

    public StencilListBrush() {
        this.setName("StencilList");
    }
    
    protected final String readRandomStencil(final SnipeData v) {
        double _rand = Math.random();
        _rand = _rand * (this.stencilList.size());
        final int _choice = (int) (_rand);
        return this.stencilList.get(_choice);
    }

    protected final void readStencilList(final String listname, final SnipeData v) {
        final File _file = new File("plugins/VoxelSniper/stencilLists/" + this.Filename + ".txt");
        if (_file.exists()) {
            try {
                final Scanner _snr = new Scanner(_file);
                int _counter = 0;
                while (_snr.hasNext()) {
                    this.stencilList.put(_counter, _snr.nextLine());
                    _counter++;
                }
                _snr.close();
            } catch (final Exception _e) {
                _e.printStackTrace();
            }
        }
    }

    protected final void stencilPaste(final SnipeData v) {
        if (this.Filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String _stencilName = this.readRandomStencil(v);
        v.sendMessage(_stencilName);

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final File _file = new File("plugins/VoxelSniper/stencils/" + _stencilName + ".vstencil");

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
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                final int _volume = this.X * this.Y * this.Z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + this.Filename + " pasted.  Volume is " + _volume + " blocks.");

                int _currX = -this.Xref; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int _currZ = -this.Zref;
                int _currY = -this.Yref;
                int _id;
                int _data;
                if (this.pasteoption == 0) {
                    for (int i = 1; i < _numRuns + 1; i++) {
                        if (_in.readBoolean()) {
                            final int numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
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
                            final int numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
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
                            for (int _j = 0; _j < (numLoops); _j++) {
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

            } catch (final Exception e) {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    protected final void stencilPaste180(final SnipeData v) {
        if (this.Filename.matches("NoFileLoaded")) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String _stencilName = this.readRandomStencil(v);

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final File _file = new File("plugins/VoxelSniper/stencils/" + _stencilName + ".vstencil");

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
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                final int _volume = this.X * this.Y * this.Z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + this.Filename + " pasted.  Volume is " + _volume + " blocks.");

                int _currX = +this.Xref; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int _currZ = +this.Zref;
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
                                _currX--;
                                if (_currX == -this.X + this.Xref) {
                                    _currX = this.Xref;
                                    _currZ--;
                                    if (_currZ == -this.Z + this.Zref) {
                                        _currZ = +this.Zref;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                            this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData((_in.readByte() + 128),
                                    (byte) (_in.readByte() + 128), false);
                            _currX--;
                            if (_currX == -this.X + this.Xref) {
                                _currX = this.Xref;
                                _currZ--;
                                if (_currZ == -this.Z + this.Zref) {
                                    _currZ = +this.Zref;
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
                                _currX--;
                                if (_currX == -this.X + this.Xref) {
                                    _currX = this.Xref;
                                    _currZ--;
                                    if (_currZ == -this.Z + this.Zref) {
                                        _currZ = +this.Zref;
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
                            _currX--;
                            if (_currX == -this.X + this.Xref) {
                                _currX = this.Xref;
                                _currZ--;
                                if (_currZ == -this.Z + this.Zref) {
                                    _currZ = +this.Zref;
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
                            for (int _j = 0; _j < (numLoops); _j++) {
                                if (_id != 0) {
                                    _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                    this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) _data, false);
                                }
                                _currX--;
                                if (_currX == -this.X + this.Xref) {
                                    _currX = this.Xref;
                                    _currZ--;
                                    if (_currZ == -this.Z + this.Zref) {
                                        _currZ = +this.Zref;
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
                            _currX--;
                            if (_currX == -this.X + this.Xref) {
                                _currX = this.Xref;
                                _currZ--;
                                if (_currZ == -this.Z + this.Zref) {
                                    _currZ = +this.Zref;
                                    _currY++;
                                }
                            }
                        }
                    }
                }
                _in.close();
                v.storeUndo(_undo);

            } catch (final Exception e) {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    protected final void stencilPaste270(final SnipeData v) {
        if (this.Filename.matches("NoFileLoaded")) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String _stencilName = this.readRandomStencil(v);

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final File _file = new File("plugins/VoxelSniper/stencils/" + _stencilName + ".vstencil");

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
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                final int _volume = this.X * this.Y * this.Z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + this.Filename + " pasted.  Volume is " + _volume + " blocks.");

                int _currX = +this.Zref; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int _currZ = -this.Xref;
                int _currY = -this.Yref;
                int _id;
                int _data;
                if (this.pasteoption == 0) {
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int j = 0; j < numLoops; j++) {
                                _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) _data, false);
                                _currZ++;
                                if (_currZ == this.X - this.Xref) {
                                    _currZ = -this.Xref;
                                    _currX--;
                                    if (_currX == -this.Z + this.Zref) {
                                        _currX = +this.Zref;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                            this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData((_in.readByte() + 128),
                                    (byte) (_in.readByte() + 128), false);
                            _currZ++;
                            _currZ++;
                            if (_currZ == this.X - this.Xref) {
                                _currZ = -this.Xref;
                                _currX--;
                                if (_currX == -this.Z + this.Zref) {
                                    _currX = +this.Zref;
                                    _currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteoption == 1) {
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int _j = 0; _j < numLoops; _j++) {
                                if (_id != 0 && this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                                  // air, and it prevents us
                                                                                                                                  // most of the time from
                                                                                                                                  // having to even check the
                                                                                                                                  // block.
                                    _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                    this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                                }
                                _currZ++;
                                if (_currZ == this.X - this.Xref) {
                                    _currZ = -this.Xref;
                                    _currX--;
                                    if (_currX == -this.Z + this.Zref) {
                                        _currX = +this.Zref;
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
                                this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                            }
                            _currZ++;
                            if (_currZ == this.X - this.Xref) {
                                _currZ = -this.Xref;
                                _currX--;
                                if (_currX == -this.Z + this.Zref) {
                                    _currX = +this.Zref;
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
                            for (int _j = 0; _j < (numLoops); _j++) {
                                if (_id != 0) {
                                    _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                    this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) _data, false);
                                }
                                _currZ++;
                                if (_currZ == this.X - this.Xref) {
                                    _currZ = -this.Xref;
                                    _currX--;
                                    if (_currX == -this.Z + this.Zref) {
                                        _currX = +this.Zref;
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
                            _currZ++;
                            if (_currZ == this.X - this.Xref) {
                                _currZ = -this.Xref;
                                _currX--;
                                if (_currX == -this.Z + this.Zref) {
                                    _currX = +this.Zref;
                                    _currY++;
                                }
                            }
                        }
                    }
                }
                _in.close();
                v.storeUndo(_undo);

            } catch (final Exception e) {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    protected final void stencilPaste90(final SnipeData v) {
        if (this.Filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String _stencilName = this.readRandomStencil(v);

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final File _file = new File("plugins/VoxelSniper/stencils/" + _stencilName + ".vstencil");

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
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                final int _volume = this.X * this.Y * this.Z;
                v.sendMessage(ChatColor.AQUA + this.Filename + " pasted.  Volume is " + _volume + " blocks.");

                int _currX = -this.Zref; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int _currZ = +this.Xref;
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
                                _currZ--;
                                if (_currZ == -this.X + this.Xref) {
                                    _currZ = this.Xref;
                                    _currX++;
                                    if (_currX == this.Z - this.Zref) {
                                        _currX = -this.Zref;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                            this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData((_in.readByte() + 128),
                                    (byte) (_in.readByte() + 128), false);
                            _currZ--;
                            if (_currZ == -this.X + this.Xref) {
                                _currZ = this.Xref;
                                _currX++;
                                if (_currX == this.Z - this.Zref) {
                                    _currX = -this.Zref;
                                    _currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteoption == 1) {
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int _j = 0; _j < numLoops; _j++) {
                                if (_id != 0 && this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                                  // air, and it prevents us
                                                                                                                                  // most of the time from
                                                                                                                                  // having to even check the
                                                                                                                                  // block.
                                    _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                    this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                                }
                                _currZ--;
                                if (_currZ == -this.X + this.Xref) {
                                    _currZ = this.Xref;
                                    _currX++;
                                    if (_currX == this.Z - this.Zref) {
                                        _currX = -this.Zref;
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
                            _currZ--;
                            if (_currZ == -this.X + this.Xref) {
                                _currZ = this.Xref;
                                _currX++;
                                if (_currX == this.Z - this.Zref) {
                                    _currX = -this.Zref;
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
                                _currZ--;
                                if (_currZ == -this.X + this.Xref) {
                                    _currZ = this.Xref;
                                    _currX++;
                                    if (_currX == this.Z - this.Zref) {
                                        _currX = -this.Zref;
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
                            _currZ--;
                            if (_currZ == -this.X + this.Xref) {
                                _currZ = this.Xref;
                                _currX++;
                                if (_currX == this.Z - this.Zref) {
                                    _currX = -this.Zref;
                                    _currY++;
                                }
                            }
                        }
                    }
                }
                _in.close();
                v.storeUndo(_undo);

            } catch (final Exception e) {
                v.sendMessage(ChatColor.RED + "Something went wrong.");
                e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    protected final void stencilPasteRotation(final SnipeData v) {
        // just randomly chooses a rotation and then calls stencilPaste.
        this.readStencilList(this.Filename, v);
        final double _rand = Math.random();
        if (_rand < 0.26) {
            this.stencilPaste(v);
        } else if (_rand < 0.51) {
            this.stencilPaste90(v);
        } else if (_rand < 0.76) {
            this.stencilPaste180(v);
        } else {
            this.stencilPaste270(v);
        }

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
        vm.custom("File loaded: " + this.Filename);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
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
            final File _file = new File("plugins/VoxelSniper/stencilLists/" + this.Filename + ".txt");
            if (_file.exists()) {
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

    @Override
    public final int getTimesUsed() {
        return StencilListBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        StencilListBrush.timesUsed = tUsed;
    }
}
