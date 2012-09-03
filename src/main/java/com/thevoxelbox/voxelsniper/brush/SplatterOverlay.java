package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Gavjenks Splatterized blockPositionY Giltwist
 */
public class SplatterOverlay extends PerformBrush {

    private int seedpercent; // Chance block on first pass is made active
    private int growpercent; // chance block on recursion pass is made active
    private int splatterrecursions; // How many times you grow the seeds
    private Random generator = new Random();

    private int depth = 3;
    private boolean allBlocks = false;

    private static int timesUsed = 0;

    public SplatterOverlay() {
        this.setName("Splatter Overlay");
    }

    private final void sOverlay(final SnipeData v) {
        final int _brushSize = v.getBrushSize();

        // Splatter Time
        final int[][] _splat = new int[2 * _brushSize + 1][2 * _brushSize + 1];
        // Seed the array
        for (int _x = 2 * _brushSize; _x >= 0; _x--) {
            for (int _y = 2 * _brushSize; _y >= 0; _y--) {

                if (this.generator.nextInt(10000) <= this.seedpercent) {
                    _splat[_x][_y] = 1;

                }
            }
        }
        // Grow the seeds
        final int _gref = this.growpercent;
        int _growcheck;
        final int[][] _tempsplat = new int[2 * _brushSize + 1][2 * _brushSize + 1];
        for (int _r = 0; _r < this.splatterrecursions; _r++) {

            this.growpercent = _gref - ((_gref / this.splatterrecursions) * (_r));
            for (int _x = 2 * _brushSize; _x >= 0; _x--) {
                for (int _y = 2 * _brushSize; _y >= 0; _y--) {

                    _tempsplat[_x][_y] = _splat[_x][_y]; // prime tempsplat

                    _growcheck = 0;
                    if (_splat[_x][_y] == 0) {
                        if (_x != 0 && _splat[_x - 1][_y] == 1) {
                            _growcheck++;
                        }
                        if (_y != 0 && _splat[_x][_y - 1] == 1) {
                            _growcheck++;
                        }
                        if (_x != 2 * _brushSize && _splat[_x + 1][_y] == 1) {
                            _growcheck++;
                        }
                        if (_y != 2 * _brushSize && _splat[_x][_y + 1] == 1) {
                            _growcheck++;
                        }

                    }

                    if (_growcheck >= 1 && this.generator.nextInt(10000) <= this.growpercent) {
                        _tempsplat[_x][_y] = 1; // prevent bleed into splat
                    }

                }

            }
            // integrate tempsplat back into splat at end of iteration
            for (int _x = 2 * _brushSize; _x >= 0; _x--) {
                for (int _y = 2 * _brushSize; _y >= 0; _y--) {

                    _splat[_x][_y] = _tempsplat[_x][_y];

                }
            }
        }
        this.growpercent = _gref;

        final int[][] _memory = new int[_brushSize * 2 + 1][_brushSize * 2 + 1];
        final double _bpow = Math.pow(_brushSize + 0.5, 2);
        for (int _z = _brushSize; _z >= -_brushSize; _z--) {
            for (int _x = _brushSize; _x >= -_brushSize; _x--) {
                for (int _y = this.getBlockPositionY(); _y > 0; _y--) { // start scanning from the height you clicked at
                    if (_memory[_x + _brushSize][_z + _brushSize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(_x, 2) + Math.pow(_z, 2)) <= _bpow && _splat[_x + _brushSize][_z + _brushSize] == 1) { // if inside of the column && if to be splattered
                            final int _check = this.getBlockIdAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z);
                            if (_check == 0 || _check == 8 || _check == 9) { // must start at surface... this prevents it filling stuff in if you click in a wall
                                                                          // and it starts out below surface.
                                if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                    switch (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z)) {
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 12:
                                    case 13:
                                    case 24:// These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to mess
                                            // with.
                                    case 48:
                                    case 82:
                                    case 49:
                                    case 78:
                                        for (int _d = 0; (_d < this.depth); _d++) {
                                            if (this.clampY(this.getBlockPositionX() + _x, _y - _d, this.getBlockPositionZ() + _z).getTypeId() != 0) {
                                                this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y - _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify
                                                                                                                    // in parameters
                                                _memory[_x + _brushSize][_z + _brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                            }
                                        }
                                        break;

                                    default:
                                        break;
                                    }
                                } else {
                                    for (int _d = 0; (_d < this.depth); _d++) {
                                        if (this.clampY(this.getBlockPositionX() + _x, _y - _d, this.getBlockPositionZ() + _z).getTypeId() != 0) {
                                            this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y - _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify in
                                                                                                                // parameters
                                            _memory[_x + _brushSize][_z + _brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    private final void soverlayTwo(final SnipeData v) {
        final int _brushSize = v.getBrushSize();

        // Splatter Time
        final int[][] _splat = new int[2 * _brushSize + 1][2 * _brushSize + 1];
        // Seed the array
        for (int _x = 2 * _brushSize; _x >= 0; _x--) {
            for (int _y = 2 * _brushSize; _y >= 0; _y--) {

                if (this.generator.nextInt(10000) <= this.seedpercent) {
                    _splat[_x][_y] = 1;

                }
            }
        }
        // Grow the seeds
        final int _gref = this.growpercent;
        int _growcheck;
        final int[][] _tempsplat = new int[2 * _brushSize + 1][2 * _brushSize + 1];
        for (int _r = 0; _r < this.splatterrecursions; _r++) {

            this.growpercent = _gref - ((_gref / this.splatterrecursions) * (_r));
            for (int _x = 2 * _brushSize; _x >= 0; _x--) {
                for (int _y = 2 * _brushSize; _y >= 0; _y--) {

                    _tempsplat[_x][_y] = _splat[_x][_y]; // prime tempsplat

                    _growcheck = 0;
                    if (_splat[_x][_y] == 0) {
                        if (_x != 0 && _splat[_x - 1][_y] == 1) {
                            _growcheck++;
                        }
                        if (_y != 0 && _splat[_x][_y - 1] == 1) {
                            _growcheck++;
                        }
                        if (_x != 2 * _brushSize && _splat[_x + 1][_y] == 1) {
                            _growcheck++;
                        }
                        if (_y != 2 * _brushSize && _splat[_x][_y + 1] == 1) {
                            _growcheck++;
                        }

                    }

                    if (_growcheck >= 1 && this.generator.nextInt(10000) <= this.growpercent) {
                        _tempsplat[_x][_y] = 1; // prevent bleed into splat
                    }

                }

            }
            // integrate tempsplat back into splat at end of iteration
            for (int _x = 2 * _brushSize; _x >= 0; _x--) {
                for (int _y = 2 * _brushSize; _y >= 0; _y--) {

                    _splat[_x][_y] = _tempsplat[_x][_y];

                }
            }
        }
        this.growpercent = _gref;

        final int[][] _memory = new int[_brushSize * 2 + 1][_brushSize * 2 + 1];
        final double _bpow = Math.pow(_brushSize + 0.5, 2);
        for (int _z = _brushSize; _z >= -_brushSize; _z--) {
            for (int _x = _brushSize; _x >= -_brushSize; _x--) {
                for (int _y = this.getBlockPositionY(); _y > 0; _y--) { // start scanning from the height you clicked at
                    if (_memory[_x + _brushSize][_z + _brushSize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(_x, 2) + Math.pow(_z, 2)) <= _bpow && _splat[_x + _brushSize][_z + _brushSize] == 1) { // if inside of the column...&& if to be splattered
                            if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y - 1, this.getBlockPositionZ() + _z) != 0) { // if not a floating block (like one of Notch'world pools)
                                if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z) == 0) { // must start at surface... this prevents it filling stuff in if
                                                                                               // you click in a wall and it starts out below surface.
                                    if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.

                                        switch (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z)) {
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 12:
                                        case 13:
                                        case 14: // These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to
                                                 // mess with.
                                        case 15:
                                        case 16:
                                        case 24:
                                        case 48:
                                        case 82:
                                        case 49:
                                        case 78:
                                            for (int _d = 1; (_d < this.depth + 1); _d++) {
                                                this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y + _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify
                                                                                                                    // in parameters
                                                _memory[_x + _brushSize][_z + _brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                            }
                                            break;

                                        default:
                                            break;
                                        }
                                    } else {
                                        for (int _d = 1; (_d < this.depth + 1); _d++) {
                                            this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y + _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify in
                                                                                                                // parameters
                                            _memory[_x + _brushSize][_z + _brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.sOverlay(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.soverlayTwo(v);
    }
    

    @Override
    public final void info(final Message vm) {
        if (this.seedpercent < 1 || this.seedpercent > 9999) {
            this.seedpercent = 1000;
        }
        if (this.growpercent < 1 || this.growpercent > 9999) {
            this.growpercent = 1000;
        }
        if (this.splatterrecursions < 1 || this.splatterrecursions > 10) {
            this.splatterrecursions = 3;
        }
        vm.brushName(this.getName());
        vm.size();
        vm.custom(ChatColor.BLUE + "Seed percent set to: " + this.seedpercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Growth percent set to: " + this.growpercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Recursions set to: " + this.splatterrecursions);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Splatter Overlay brush parameters:");
            v.sendMessage(ChatColor.AQUA + "d[number] (ex:  d3) How many blocks deep you want to replace from the surface.");
            v.sendMessage(ChatColor.BLUE
                    + "all (ex:  /b over all) Sets the brush to overlay over ALL materials, not just natural surface ones (will no longer ignore trees and buildings).  The parameter /some will set it back to default.");
            v.sendMessage(ChatColor.AQUA + "/b sover s[int] -- set a seed percentage (1-9999). 100 = 1% Default is 1000");
            v.sendMessage(ChatColor.AQUA + "/b sover g[int] -- set a growth percentage (1-9999).  Default is 1000");
            v.sendMessage(ChatColor.AQUA + "/b sover r[int] -- set a recursion (1-10).  Default is 3");
            return;
        }
        for (int _i = 1; _i < par.length; _i++) {
            if (par[_i].startsWith("d")) {
                this.depth = Integer.parseInt(par[_i].replace("d", ""));
                v.sendMessage(ChatColor.AQUA + "Depth set to " + this.depth);
                if (this.depth < 1) {
                    this.depth = 1;
                }
                continue;
            } else if (par[_i].startsWith("all")) {
                this.allBlocks = true;
                v.sendMessage(ChatColor.BLUE + "Will overlay over any block." + this.depth);
                continue;
            } else if (par[_i].startsWith("some")) {
                this.allBlocks = false;
                v.sendMessage(ChatColor.BLUE + "Will overlay only natural block types." + this.depth);
                continue;
            } else if (par[_i].startsWith("s")) {
                final double temp = Integer.parseInt(par[_i].replace("s", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.sendMessage(ChatColor.AQUA + "Seed percent set to: " + temp / 100 + "%");
                    this.seedpercent = (int) temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Seed percent must be an integer 1-9999!");
                }
                continue;
            } else if (par[_i].startsWith("g")) {
                final double temp = Integer.parseInt(par[_i].replace("g", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.sendMessage(ChatColor.AQUA + "Growth percent set to: " + temp / 100 + "%");
                    this.growpercent = (int) temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Growth percent must be an integer 1-9999!");
                }
                continue;
            } else if (par[_i].startsWith("r")) {
                final int temp = Integer.parseInt(par[_i].replace("r", ""));
                if (temp >= 1 && temp <= 10) {
                    v.sendMessage(ChatColor.AQUA + "Recursions set to: " + temp);
                    this.splatterrecursions = temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Recursions must be an integer 1-10!");
                }
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final int getTimesUsed() {
        return SplatterOverlay.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        SplatterOverlay.timesUsed = tUsed;
    }

}
