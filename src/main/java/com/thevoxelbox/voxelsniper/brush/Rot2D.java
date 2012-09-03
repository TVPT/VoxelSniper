package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;

/**
 * 
 * @author Piotr
 */
public class Rot2D extends Brush {
    private int mode = 0;
    private int bsize;
    private int brushSize;
    private BlockWrapper[][][] snap;
    private double se;

    private static int timesUsed = 0;

    public Rot2D() {
        this.setName("2D Rotation");
    }

    private void getMatrix() {
        this.brushSize = (this.bsize * 2) + 1;

        this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];

        final int derp = this.bsize;
        int sx = this.getBlockPositionX() - this.bsize;
        int sy = this.getBlockPositionY() - this.bsize;
        int sz = this.getBlockPositionZ() - this.bsize;
        final double bpow = Math.pow(this.bsize + 0.5, 2);
        for (int x = 0; x < this.snap.length; x++) {
            sz = this.getBlockPositionZ() - derp;
            final double xpow = Math.pow(x - this.bsize, 2);
            for (int z = 0; z < this.snap.length; z++) {
                sy = this.getBlockPositionY() - derp;
                if (xpow + Math.pow(z - this.bsize, 2) <= bpow) {
                    for (int y = 0; y < this.snap.length; y++) {
                        final Block b = this.clampY(sx, sy, sz); // why is this not sx + x, sy + y sz + z?
                        this.snap[x][y][z] = new BlockWrapper(b);
                        b.setTypeId(0);
                        sy++;
                    }
                }
                sz++;
            }
            sx++;
        }
    }

    private void rotate(final SnipeData v) {
        int _xx;
        int _zz;
        int _yy;
        double _newx;
        double _newz;
        final double _bpow = Math.pow(this.bsize + 0.5, 2);
        final double _cos = Math.cos(this.se);
        final double _sin = Math.sin(this.se);
        final boolean[][] _doNotFill = new boolean[this.snap.length][this.snap.length];
        // I put y in the inside loop, since it doesn't have any power functions, should be much faster.
        // Also, new array keeps track of which x and z coords are being assigned in the rotated space so that we can
        // do a targeted filling of only those columns later that were left out.

        for (int _x = 0; _x < this.snap.length; _x++) {
            _xx = _x - this.bsize;
            final double _xpow = Math.pow(_xx, 2);
            for (int _z = 0; _z < this.snap.length; _z++) {
                _zz = _z - this.bsize;
                if (_xpow + Math.pow(_zz, 2) <= _bpow) {
                    _newx = (_xx * _cos) - (_zz * _sin);
                    _newz = (_xx * _sin) + (_zz * _cos);

                    _doNotFill[(int) _newx + this.bsize][(int) _newz + this.bsize] = true;

                    for (int _y = 0; _y < this.snap.length; _y++) {
                        _yy = _y - this.bsize;

                        final BlockWrapper _vb = this.snap[_x][_y][_z];
                        if (_vb.id == 0) {
                            continue;
                        }
                        this.setBlockIdAt(_vb.id, this.getBlockPositionX() + (int) _newx, this.getBlockPositionY() + _yy, this.getBlockPositionZ() + (int) _newz);
                    }
                }
            }
        }
        int _A, _B, _C, _D, _fx, _fy, _fz, _winner;
        for (int _x = 0; _x < this.snap.length; _x++) {
            final double _xpow = Math.pow(_x - this.bsize, 2);
            _fx = _x + this.getBlockPositionX() - this.bsize;
            for (int _z = 0; _z < this.snap.length; _z++) {
                if (_xpow + Math.pow(_z - this.bsize, 2) <= _bpow) {
                    _fz = _z + this.getBlockPositionZ() - this.bsize;
                    if (!_doNotFill[_x][_z]) {
                        // smart fill stuff

                        for (int y = 0; y < this.snap.length; y++) {
                            _fy = y + this.getBlockPositionY() - this.bsize;
                            _A = this.getBlockIdAt(_fx + 1, _fy, _fz);
                            _D = this.getBlockIdAt(_fx - 1, _fy, _fz);
                            _C = this.getBlockIdAt(_fx, _fy, _fz + 1);
                            _B = this.getBlockIdAt(_fx, _fy, _fz - 1);
                            if (_A == _B || _A == _C || _A == _D) { // I figure that since we are already narrowing it down to ONLY the holes left behind, it should
                                                              // be fine to do all 5 checks needed to be legit about it.
                                _winner = _A;
                            } else if (_B == _D || _C == _D) {
                                _winner = _D;
                            } else {
                                _winner = _B; // blockPositionY making this default, it will also automatically cover situations where B = C;
                            }

                            this.setBlockIdAt(_winner, _fx, _fy, _fz);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
    	this.bsize = v.getBrushSize();

        switch (this.mode) {
        case 0:
            this.getMatrix();
            this.rotate(v);
            break;

        default:
            v.sendMessage(ChatColor.RED + "Something went wrong.");
            break;
        }
    }

    @Override
    protected void powder(final SnipeData v) {
    	this.bsize = v.getBrushSize();

        switch (this.mode) {
        case 0:
            this.getMatrix();
            this.rotate(v);
            break;

        default:
            v.sendMessage(ChatColor.RED + "Something went wrong.");
            break;
        }
    }
    

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        this.se = Math.toRadians(Double.parseDouble(par[1]));
        v.sendMessage(ChatColor.GREEN + "Angle set to " + this.se);
    }    

    @Override
    public final int getTimesUsed() {
        return Rot2D.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Rot2D.timesUsed = tUsed;
    }
}
