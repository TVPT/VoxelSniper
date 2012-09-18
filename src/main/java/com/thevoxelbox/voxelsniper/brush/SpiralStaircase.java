package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * 
 * @author giltwist
 */
public class SpiralStaircase extends Brush {
	private static int timesUsed = 0;

	private String stairtype = "block"; // "block" 1x1 blocks (default), "step" alternating step double step, "stair" staircase with blocks on corners
    private String sdirect = "c"; // "c" clockwise (default), "cc" counter-clockwise
    private String sopen = "n"; // "n" north (default), "e" east, "world" south, "world" west

    public SpiralStaircase() {
        this.setName("Spiral Staircase");
    }

    private final void buildStairWell(final SnipeData v) {
        final int _brushSize = v.getBrushSize();
        final int _voxelMaterialId = v.getVoxelId();
        final int[][][] _spiral = new int[2 * _brushSize + 1][_height][2 * _brushSize + 1];

        if (v.getVoxelHeight() < 1) {
            v.setVoxelHeight(1);
            v.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
        }
        final int _height = v.getVoxelHeight();

        // locate first block in staircase
        // Note to self, fix these
        int _startx = 0;
        int _startz = 0;
        int _y = 0;
        int _xoffset = 0;
        int _zoffset = 0;
        int _toggle = 0;

        if (this.sdirect.equalsIgnoreCase("cc")) {
            if (this.sopen.equalsIgnoreCase("n")) {
                _startx = 0;
                _startz = 2 * _brushSize;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                _startx = 0;
                _startz = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                _startx = 2 * _brushSize;
                _startz = 0;
            } else {
                _startx = 2 * _brushSize;
                _startz = 2 * _brushSize;
            }
        } else {
            if (this.sopen.equalsIgnoreCase("n")) {
                _startx = 0;
                _startz = 0;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                _startx = 2 * _brushSize;
                _startz = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                _startx = 2 * _brushSize;
                _startz = 2 * _brushSize;
            } else {
                _startx = 0;
                _startz = 2 * _brushSize;
            }
        }

        while (_y < _height) {
            if (this.stairtype.equalsIgnoreCase("block")) {
                // 1x1x1 voxel material steps
                _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                _y++;
            } else if (this.stairtype.equalsIgnoreCase("step")) {
                // alternating step-doublestep, uses data value to determine type
                switch (_toggle) {
                case 0:
                    _toggle = 2;
                    _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    break;
                case 1:
                    _toggle = 2;
                    _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    break;
                case 2:
                    _toggle = 1;
                    _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 2;
                    _y++;
                    break;
                default:
                    break;
                }

            }

            // Adjust horizontal position and do stair-option array stuff
            if (_startx + _xoffset == 0) { // All North
                if (_startz + _zoffset == 0) { // NORTHEAST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        _xoffset++;
                    } else {
                        _zoffset++;
                    }
                } else if (_startz + _zoffset == 2 * _brushSize) { // NORTHWEST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        _zoffset--;
                    } else {
                        _xoffset++;
                    }
                } else { // JUST PLAIN NORTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 5;
                            _y++;
                        }
                        _zoffset--;
                    } else {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 4;
                            _y++;
                        }
                        _zoffset++;
                    }
                }
            } else if (_startx + _xoffset == 2 * _brushSize) { // ALL SOUTH
                if (_startz + _zoffset == 0) { // SOUTHEAST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        _zoffset++;
                    } else {
                        _xoffset--;
                    }
                } else if (_startz + _zoffset == 2 * _brushSize) { // SOUTHWEST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        _xoffset--;
                    } else {
                        _zoffset--;
                    }
                } else { // JUST PLAIN SOUTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 4;
                            _y++;
                        }
                        _zoffset++;
                    } else {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 5;
                            _y++;
                        }
                        _zoffset--;
                    }
                }
            } else if (_startz + _zoffset == 0) { // JUST PLAIN EAST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 2;
                        _y++;
                    }
                    _xoffset++;
                } else {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 3;
                        _y++;
                    }
                    _xoffset--;
                }
            } else { // JUST PLAIN WEST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 3;
                        _y++;
                    }
                    _xoffset--;
                } else {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 2;
                        _y++;
                    }
                    _xoffset++;
                }
            }
        }
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        // Make the changes

        for (int _x = 2 * _brushSize; _x >= 0; _x--) {
            for (int _i = _height - 1; _i >= 0; _i--) {
                for (int _z = 2 * _brushSize; _z >= 0; _z--) {
                    switch (_spiral[_x][_i][_z]) {
                    case 0:
                        if (_i != _height - 1) {
                            if (!((this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) && _spiral[_x][_i + 1][_z] == 1)) {
                                if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z) != 0) {
                                    _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z));
                                }
                                this.setBlockIdAt(0, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z);
                            }

                        } else {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z) != 0) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(0, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z);
                        }

                        break;
                    case 1:
                        if (this.stairtype.equalsIgnoreCase("block")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z) != _voxelMaterialId) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(_voxelMaterialId, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z);
                        } else if (this.stairtype.equalsIgnoreCase("step")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z) != 44) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(44, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z).setData(v.getData());
                        } else if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i - 1, this.getBlockPositionZ() - _brushSize + _z) != _voxelMaterialId) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i - 1, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(_voxelMaterialId, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i - 1, this.getBlockPositionZ() - _brushSize + _z);

                        }
                        break;
                    case 2:
                        if (this.stairtype.equalsIgnoreCase("step")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z) != 43) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(43, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z).setData(v.getData());
                        } else if (this.stairtype.equalsIgnoreCase("woodstair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z) != 53) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(53, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z).setData((byte) 0);
                        } else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z) != 67) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(67, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z).setData((byte) 0);
                        }
                        break;
                    default:
                        if (this.stairtype.equalsIgnoreCase("woodstair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z) != 53) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(53, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z).setData((byte) (_spiral[_x][_i][_z] - 2));
                        } else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z) != 67) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(67, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z).setData((byte) (_spiral[_x][_i][_z] - 2));
                        }
                        break;
                    }
                }
            }
        }
        v.storeUndo(_undo);
    }

    private final void digStairWell(final SnipeData v) {
        final int _brushSize = v.getBrushSize();
        final int _voxelMaterialId = v.getVoxelId();

        if (v.getVoxelHeight() < 1) {
            v.setVoxelHeight(1);
            v.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
        }
        final int _height = v.getVoxelHeight();

        // initialize array
        final int[][][] _spiral = new int[2 * _brushSize + 1][_height][2 * _brushSize + 1];

        // locate first block in staircase
        // Note to self, fix these
        int _startx = 0;
        int _startz = 0;
        int _y = 0;
        int _xoffset = 0;
        int _zoffset = 0;
        int _toggle = 0;

        if (this.sdirect.equalsIgnoreCase("cc")) {
            if (this.sopen.equalsIgnoreCase("n")) {
                _startx = 0;
                _startz = 2 * _brushSize;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                _startx = 0;
                _startz = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                _startx = 2 * _brushSize;
                _startz = 0;
            } else {
                _startx = 2 * _brushSize;
                _startz = 2 * _brushSize;
            }
        } else {
            if (this.sopen.equalsIgnoreCase("n")) {
                _startx = 0;
                _startz = 0;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                _startx = 2 * _brushSize;
                _startz = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                _startx = 2 * _brushSize;
                _startz = 2 * _brushSize;
            } else {
                _startx = 0;
                _startz = 2 * _brushSize;
            }
        }

        while (_y < _height) {
            if (this.stairtype.equalsIgnoreCase("block")) {
                // 1x1x1 voxel material steps
                _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                _y++;
            } else if (this.stairtype.equalsIgnoreCase("step")) {
                // alternating step-doublestep, uses data value to determine type
                switch (_toggle) {
                case 0:
                    _toggle = 2;
                    _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 2;
                    break;
                case 1:
                    _toggle = 2;
                    _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 2;
                    break;
                case 2:
                    _toggle = 1;
                    _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    _y++;
                    break;
                default:
                    break;
                }

            }

            // Adjust horizontal position and do stair-option array stuff
            if (_startx + _xoffset == 0) { // All North
                if (_startz + _zoffset == 0) { // NORTHEAST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        _xoffset++;
                    } else {
                        _zoffset++;
                    }
                } else if (_startz + _zoffset == 2 * _brushSize) { // NORTHWEST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        _zoffset--;
                    } else {
                        _xoffset++;
                    }
                } else { // JUST PLAIN NORTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 4;
                            _y++;
                        }
                        _zoffset--;
                    } else {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 5;
                            _y++;
                        }
                        _zoffset++;
                    }
                }

            } else if (_startx + _xoffset == 2 * _brushSize) { // ALL SOUTH
                if (_startz + _zoffset == 0) { // SOUTHEAST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        _zoffset++;
                    } else {
                        _xoffset--;
                    }
                } else if (_startz + _zoffset == 2 * _brushSize) { // SOUTHWEST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        _xoffset--;
                    } else {
                        _zoffset--;
                    }
                } else { // JUST PLAIN SOUTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 5;
                            _y++;
                        }
                        _zoffset++;
                    } else {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 4;
                            _y++;
                        }
                        _zoffset--;
                    }
                }

            } else if (_startz + _zoffset == 0) { // JUST PLAIN EAST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 3;
                        _y++;
                    }
                    _xoffset++;
                } else {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 2;
                        _y++;
                    }
                    _xoffset--;
                }
            } else { // JUST PLAIN WEST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 2;
                        _y++;
                    }
                    _xoffset--;
                } else {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        _spiral[_startx + _xoffset][_y][_startz + _zoffset] = 3;
                        _y++;
                    }
                    _xoffset++;
                }
            }

        }

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        // Make the changes

        for (int _x = 2 * _brushSize; _x >= 0; _x--) {

            for (int _i = _height - 1; _i >= 0; _i--) {

                for (int _z = 2 * _brushSize; _z >= 0; _z--) {

                    switch (_spiral[_x][_i][_z]) {
                    case 0:
                        if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z) != 0) {
                            _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z));
                        }
                        this.setBlockIdAt(0, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z);
                        break;
                    case 1:
                        if (this.stairtype.equalsIgnoreCase("block")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z) != _voxelMaterialId) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(_voxelMaterialId, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z);
                        } else if (this.stairtype.equalsIgnoreCase("step")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z) != 44) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(44, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z).setData(v.getData());
                        } else if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z) != _voxelMaterialId) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(_voxelMaterialId, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z);

                        }
                        break;
                    case 2:
                        if (this.stairtype.equalsIgnoreCase("step")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z) != 43) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(43, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z).setData(v.getData());
                        } else if (this.stairtype.equalsIgnoreCase("woodstair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z) != 53) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(53, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z).setData((byte) 0);
                        } else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z) != 67) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(67, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z).setData((byte) 0);
                        }
                        break;
                    default:
                        if (this.stairtype.equalsIgnoreCase("woodstair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z) != 53) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(53, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z).setData((byte) (_spiral[_x][_i][_z] - 2));
                        } else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z) != 67) {
                                _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z));
                            }
                            this.setBlockIdAt(67, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z);
                            this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _brushSize + _z).setData((byte) (_spiral[_x][_i][_z] - 2));
                        }
                        break;

                    }

                }
            }
        }
        v.storeUndo(_undo);

    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.digStairWell(v); // make stairwell below target
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.buildStairWell(v); // make stairwell above target
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName("Spiral Staircase");
    	vm.size();
    	vm.voxel();
    	vm.height();
    	vm.data();
    	vm.custom(ChatColor.BLUE + "Staircase type: " + this.stairtype);
    	vm.custom(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
    	vm.custom(ChatColor.BLUE + "Staircase opens: " + this.sopen);
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GOLD + "Spiral Staircase Parameters:");
    		v.sendMessage(ChatColor.AQUA + "/b sstair 'block' (default) | 'step' | 'woodstair' | 'cobblestair' -- set the type of staircase");
    		v.sendMessage(ChatColor.AQUA + "/b sstair 'c' (default) | 'cc' -- set the turning direction of staircase");
    		v.sendMessage(ChatColor.AQUA + "/b sstair 'n' (default) | 'e' | 's' | 'world' -- set the opening direction of staircase");
    		return;
    	}
    	
    	for (int _i = 1; _i < par.length; _i++) {
    		if (par[_i].equalsIgnoreCase("block") || par[_i].equalsIgnoreCase("step") || par[_i].equalsIgnoreCase("woodstair")
    				|| par[_i].equalsIgnoreCase("cobblestair")) {
    			this.stairtype = par[_i];
    			v.sendMessage(ChatColor.BLUE + "Staircase type: " + this.stairtype);
    			continue;
    		} else if (par[_i].equalsIgnoreCase("c") || par[_i].equalsIgnoreCase("cc")) {
    			this.sdirect = par[_i];
    			v.sendMessage(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
    			continue;
    		} else if (par[_i].equalsIgnoreCase("n") || par[_i].equalsIgnoreCase("e") || par[_i].equalsIgnoreCase("s") || par[_i].equalsIgnoreCase("world")) {
    			this.sopen = par[_i];
    			v.sendMessage(ChatColor.BLUE + "Staircase opens: " + this.sopen);
    			continue;
    		} else {
    			v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return SpiralStaircase.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	SpiralStaircase.timesUsed = tUsed;
    }
}
