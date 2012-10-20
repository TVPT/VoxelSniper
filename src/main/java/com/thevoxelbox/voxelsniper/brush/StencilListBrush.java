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
    private static int timesUsed = 0;

    /**
     * 
     */
    public StencilListBrush() {
        this.setName("StencilList");
    }
    
    private final String readRandomStencil(final SnipeData v) {
        double _rand = Math.random();
        _rand = _rand * (this.stencilList.size());
        final int _choice = (int) (_rand);
        return this.stencilList.get(_choice);
    }

    private final void readStencilList(final String listname, final SnipeData v) {
        final File _file = new File("plugins/VoxelSniper/stencilLists/" + this.filename + ".txt");
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

    private final void stencilPaste(final SnipeData v) {
        if (this.filename.matches("NoFileLoaded")) {
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

                this.x = _in.readShort();
                this.z = _in.readShort();
                this.y = _in.readShort();

                this.xRef = _in.readShort();
                this.zRef = _in.readShort();
                this.yRef = _in.readShort();

                final int _numRuns = _in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                final int _volume = this.x * this.y * this.z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + _volume + " blocks.");

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
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                _e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    private final void stencilPaste180(final SnipeData v) {
        if (this.filename.matches("NoFileLoaded")) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String _stencilName = this.readRandomStencil(v);

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final File _file = new File("plugins/VoxelSniper/stencils/" + _stencilName + ".vstencil");

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
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                final int _volume = this.x * this.y * this.z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + _volume + " blocks.");

                int _currX = +this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int _currZ = +this.zRef;
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
                                _currX--;
                                if (_currX == -this.x + this.xRef) {
                                    _currX = this.xRef;
                                    _currZ--;
                                    if (_currZ == -this.z + this.zRef) {
                                        _currZ = +this.zRef;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                            this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData((_in.readByte() + 128),
                                    (byte) (_in.readByte() + 128), false);
                            _currX--;
                            if (_currX == -this.x + this.xRef) {
                                _currX = this.xRef;
                                _currZ--;
                                if (_currZ == -this.z + this.zRef) {
                                    _currZ = +this.zRef;
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
                                _currX--;
                                if (_currX == -this.x + this.xRef) {
                                    _currX = this.xRef;
                                    _currZ--;
                                    if (_currZ == -this.z + this.zRef) {
                                        _currZ = +this.zRef;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            if (_id != 0 && this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).getTypeId() == 0) {
                                _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                            }
                            _currX--;
                            if (_currX == -this.x + this.xRef) {
                                _currX = this.xRef;
                                _currZ--;
                                if (_currZ == -this.z + this.zRef) {
                                    _currZ = +this.zRef;
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
                                _currX--;
                                if (_currX == -this.x + this.xRef) {
                                    _currX = this.xRef;
                                    _currZ--;
                                    if (_currZ == -this.z + this.zRef) {
                                        _currZ = +this.zRef;
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
                            if (_currX == -this.x + this.xRef) {
                                _currX = this.xRef;
                                _currZ--;
                                if (_currZ == -this.z + this.zRef) {
                                    _currZ = +this.zRef;
                                    _currY++;
                                }
                            }
                        }
                    }
                }
                _in.close();
                v.storeUndo(_undo);

            } catch (final Exception _e) {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                _e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    private final void stencilPaste270(final SnipeData v) {
        if (this.filename.matches("NoFileLoaded")) {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String _stencilName = this.readRandomStencil(v);

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final File _file = new File("plugins/VoxelSniper/stencils/" + _stencilName + ".vstencil");

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
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                final int _volume = this.x * this.y * this.z;
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + _volume + " blocks.");

                int _currX = +this.zRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int _currZ = -this.xRef;
                int _currY = -this.yRef;
                int _id;
                int _data;
                if (this.pasteOption == 0) {
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int _numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int j = 0; j < _numLoops; j++) {
                                _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) _data, false);
                                _currZ++;
                                if (_currZ == this.x - this.xRef) {
                                    _currZ = -this.xRef;
                                    _currX--;
                                    if (_currX == -this.z + this.zRef) {
                                        _currX = +this.zRef;
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
                            if (_currZ == this.x - this.xRef) {
                                _currZ = -this.xRef;
                                _currX--;
                                if (_currX == -this.z + this.zRef) {
                                    _currX = +this.zRef;
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
                                if (_id != 0 && this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).getTypeId() == 0) { // no reason to paste air over
                                                                                                                                  // air, and it prevents us
                                                                                                                                  // most of the time from
                                                                                                                                  // having to even check the
                                                                                                                                  // block.
                                    _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                    this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                                }
                                _currZ++;
                                if (_currZ == this.x - this.xRef) {
                                    _currZ = -this.xRef;
                                    _currX--;
                                    if (_currX == -this.z + this.zRef) {
                                        _currX = +this.zRef;
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
                            if (_currZ == this.x - this.xRef) {
                                _currZ = -this.xRef;
                                _currX--;
                                if (_currX == -this.z + this.zRef) {
                                    _currX = +this.zRef;
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
                                _currZ++;
                                if (_currZ == this.x - this.xRef) {
                                    _currZ = -this.xRef;
                                    _currX--;
                                    if (_currX == -this.z + this.zRef) {
                                        _currX = +this.zRef;
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
                            if (_currZ == this.x - this.xRef) {
                                _currZ = -this.xRef;
                                _currX--;
                                if (_currX == -this.z + this.zRef) {
                                    _currX = +this.zRef;
                                    _currY++;
                                }
                            }
                        }
                    }
                }
                _in.close();
                v.storeUndo(_undo);

            } catch (final Exception _e) {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
                _e.printStackTrace();
            }
        } else {
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    private final void stencilPaste90(final SnipeData v) {
        if (this.filename.matches("NoFileLoaded")) {
            v.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }

        final String _stencilName = this.readRandomStencil(v);

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final File _file = new File("plugins/VoxelSniper/stencils/" + _stencilName + ".vstencil");

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
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                final int _volume = this.x * this.y * this.z;
                v.sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + _volume + " blocks.");

                int _currX = -this.zRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                                        // corner, for example.
                int _currZ = +this.xRef;
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
                                _currZ--;
                                if (_currZ == -this.x + this.xRef) {
                                    _currZ = this.xRef;
                                    _currX++;
                                    if (_currX == this.z - this.zRef) {
                                        _currX = -this.zRef;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                            this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData((_in.readByte() + 128),
                                    (byte) (_in.readByte() + 128), false);
                            _currZ--;
                            if (_currZ == -this.x + this.xRef) {
                                _currZ = this.xRef;
                                _currX++;
                                if (_currX == this.z - this.zRef) {
                                    _currX = -this.zRef;
                                    _currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteOption == 1) {
                    for (int _i = 1; _i < _numRuns + 1; _i++) {
                        if (_in.readBoolean()) {
                            final int numLoops = _in.readByte() + 128;
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            for (int _j = 0; _j < numLoops; _j++) {
                                if (_id != 0 && this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).getTypeId() == 0) {
                                    _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                    this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                                }
                                _currZ--;
                                if (_currZ == -this.x + this.xRef) {
                                    _currZ = this.xRef;
                                    _currX++;
                                    if (_currX == this.z - this.zRef) {
                                        _currX = -this.zRef;
                                        _currY++;
                                    }
                                }
                            }
                        } else {
                            _id = (_in.readByte() + 128);
                            _data = (_in.readByte() + 128);
                            if (_id != 0 && this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).getTypeId() == 0) {
                                _undo.put(this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ));
                                this.clampY(this.getBlockPositionX() + _currX, this.getBlockPositionY() + _currY, this.getBlockPositionZ() + _currZ).setTypeIdAndData(_id, (byte) (_data), false);
                            }
                            _currZ--;
                            if (_currZ == -this.x + this.xRef) {
                                _currZ = this.xRef;
                                _currX++;
                                if (_currX == this.z - this.zRef) {
                                    _currX = -this.zRef;
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
                                if (_currZ == -this.x + this.xRef) {
                                    _currZ = this.xRef;
                                    _currX++;
                                    if (_currX == this.z - this.zRef) {
                                        _currX = -this.zRef;
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
                            if (_currZ == -this.x + this.xRef) {
                                _currZ = this.xRef;
                                _currX++;
                                if (_currX == this.z - this.zRef) {
                                    _currX = -this.zRef;
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
            v.owner().getPlayer().sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    private final void stencilPasteRotation(final SnipeData v) {
        // just randomly chooses a rotation and then calls stencilPaste.
        this.readStencilList(this.filename, v);
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
        vm.custom("File loaded: " + this.filename);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Stencil List brush Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b schem [optional: 'full' 'fill' or 'replace', with fill as default] [name] -- Loads the specified stencil list.  Full/fill/replace must come first.  Full = paste all blocks, fill = paste only into air blocks, replace = paste full blocks in only, but replace anything in their way.");
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
            final File _file = new File("plugins/VoxelSniper/stencilLists/" + this.filename + ".txt");
            if (_file.exists()) {
                v.sendMessage(ChatColor.RED + "Stencil List '" + this.filename + "' exists and was loaded.");
                this.readStencilList(this.filename, v);
            } else {
                v.sendMessage(ChatColor.AQUA + "Stencil List '" + this.filename
                        + "' does not exist.  This brush will not function without a valid stencil list.");
                this.filename = "NoFileLoaded";
            }
        } catch (final Exception _e) {
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
