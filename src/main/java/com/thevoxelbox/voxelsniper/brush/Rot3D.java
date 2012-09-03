package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;

/**
 * 
 * @author Gavjenks (from Gavjenks & Piotr'world 2D brush)
 */
public class Rot3D extends Brush {
    private int mode = 0;
    private int bsize;
    private int brushSize;
    private BlockWrapper[][][] snap;
    private double seYaw;
    private double sePitch;
    private double seRoll;

    private static int timesUsed = 0;

    public Rot3D() {
        this.setName("3D Rotation");
    }


    private void getMatrix() {// only need to do once. But y needs to change + sphere
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
                final double zpow = Math.pow(z - this.bsize, 2);
                for (int y = 0; y < this.snap.length; y++) {
                    if (xpow + zpow + Math.pow(y - this.bsize, 2) <= bpow) {
                        final Block b = this.clampY(sx, sy, sz);
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
        // basically 1) make it a sphere we are rotating in, not a cylinder
        // 2) do three rotations in a row, one in each dimension, unless some dimensions are set to zero or udnefined or whatever, then skip those.
        // --> Why not utilize Sniper'world new oportunities and have arrow rotate all 3, powder rotate x, goldsisc y, otherdisc z. Or something like that. Or we
        // could just use arrow and powder and just differenciate between left and right click that gis 4 different situations
        // --> Well, there would be 7 different possibilities... X, Y, Z, XY, XZ, YZ, XYZ, and different numbers of parameters for each, so I think each having
        // and item is too confusing. How about this: arrow = rotate one dimension, based on the face you click, and takes 1 param... powder: rotates all three
        // at once, and takes 3 params.
        int _xx;
        int _zz;
        int _yy;
        double _newxzX;
        double _newxzZ;
        double _newxyY;
        double _newxyX;
        double _newyzY;
        double _newyzZ;
        final double _bpow = Math.pow(this.bsize + 0.5, 2);
        final double _cosYaw = Math.cos(this.seYaw);
        final double _sinYaw = Math.sin(this.seYaw);
        final double _cosPitch = Math.cos(this.sePitch);
        final double _sinPitch = Math.sin(this.sePitch);
        final double _cosRoll = Math.cos(this.seRoll);
        final double _sinRoll = Math.sin(this.seRoll);
        final boolean[][][] _doNotFill = new boolean[this.snap.length][this.snap.length][this.snap.length];
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = 0; _x < this.snap.length; _x++) {
            _xx = _x - this.bsize;
            final double _xpow = Math.pow(_xx, 2);
            for (int _z = 0; _z < this.snap.length; _z++) {
                _zz = _z - this.bsize;
                final double _zpow = Math.pow(_zz, 2);
                _newxzX = (_xx * _cosYaw) - (_zz * _sinYaw);
                _newxzZ = (_xx * _sinYaw) + (_zz * _cosYaw);
                for (int _y = 0; _y < this.snap.length; _y++) {
                    _yy = _y - this.bsize;
                    if (_xpow + _zpow + Math.pow(_yy, 2) <= _bpow) {
                        _undo.put(this.clampY(this.getBlockPositionX() + _xx, this.getBlockPositionY() + _yy, this.getBlockPositionZ() + _zz)); // just store whole sphere in undo, too complicated otherwise, since this
                                                                                      // brush both adds and remos things unpredictably.

                        _newxyX = (_newxzX * _cosPitch) - (_yy * _sinPitch);
                        _newxyY = (_newxzX * _sinPitch) + (_yy * _cosPitch); // calculates all three in succession in precise math space
                        _newyzY = (_newxyY * _cosRoll) - (_newxzZ * _sinRoll);
                        _newyzZ = (_newxyY * _sinRoll) + (_newxzZ * _cosRoll);

                        _doNotFill[(int) _newxyX + this.bsize][(int) _newyzY + this.bsize][(int) _newyzZ + this.bsize] = true; // only rounds off to nearest block
                                                                                                                           // after all three, though.

                        final BlockWrapper vb = this.snap[_x][_y][_z];
                        if (vb.id == 0) {
                            continue;
                        }
                        this.setBlockIdAt(vb.id, this.getBlockPositionX() + (int) _newxyX, this.getBlockPositionY() + (int) _newyzY, this.getBlockPositionZ() + (int) _newyzZ);
                    }
                }
            }
        }
        int _A, _B, _C, _D, _fx, _fy, _fz, _winner;
        for (int _x = 0; _x < this.snap.length; _x++) {
            final double _xpow = Math.pow(_x - this.bsize, 2);
            _fx = _x + this.getBlockPositionX() - this.bsize;
            for (int _z = 0; _z < this.snap.length; _z++) {
                final double _zpow = Math.pow(_z - this.bsize, 2);
                _fz = _z + this.getBlockPositionZ() - this.bsize;
                for (int _y = 0; _y < this.snap.length; _y++) {
                    if (_xpow + _zpow + Math.pow(_y - this.bsize, 2) <= _bpow) {
                        if (!_doNotFill[_x][_y][_z]) {
                            // smart fill stuff
                            _fy = _y + this.getBlockPositionY() - this.bsize;
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
        v.storeUndo(_undo);
    }

    // after all rotations, compare snapshot to new state of world, and store changed blocks to undo?
    // --> agreed. Do what erode does and store one snapshot with Block pointers and int id of what the block started with, afterwards simply go thru that
    // matrix and compare Block.getId with 'id' if different undo.add( new BlockWrapper ( Block, oldId ) )

    @Override
    protected final void arrow(final SnipeData v) {
    	this.bsize = v.getBrushSize();

        switch (this.mode) {
        case 0:
            this.getMatrix();
            this.rotate(v);
            break;

        default:
            v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
            break;
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.bsize = v.getBrushSize();

        switch (this.mode) {
        case 0:
            this.getMatrix();
            this.rotate(v);
            break;

        default:
            v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
            break;
        }
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.brushMessage("Rotates Yaw (XZ), then Pitch(XY), then Roll(ZY), in order.");
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GOLD + "Rotate brush Parameters:");
    		v.sendMessage(ChatColor.AQUA + "p[0-359] -- set degrees of pitch rotation (rotation about the Z axis).");
    		v.sendMessage(ChatColor.BLUE + "r[0-359] -- set degrees of roll rotation (rotation about the X axis).");
    		v.sendMessage(ChatColor.LIGHT_PURPLE + "y[0-359] -- set degrees of yaw rotation (Rotation about the Y axis).");
    		
    		return;
    	}
    	for (int _i = 1; _i < par.length; _i++) {
    		// which way is clockwise is less obvious for roll and pitch... should probably fix that / make it clear
    		if (par[_i].startsWith("p")) {
    			this.sePitch = Math.toRadians(Double.parseDouble(par[_i].replace("p", "")));
    			v.sendMessage(ChatColor.AQUA + "Around Z-axis degrees set to " + this.sePitch);
    			if (this.sePitch < 0 || this.sePitch > 359) {
    				v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
    			}
    			continue;
    		} else if (par[_i].startsWith("r")) {
    			this.seRoll = Math.toRadians(Double.parseDouble(par[_i].replace("r", "")));
    			v.sendMessage(ChatColor.AQUA + "Around X-axis degrees set to " + this.seRoll);
    			if (this.seRoll < 0 || this.seRoll > 359) {
    				v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
    			}
    			continue;
    		} else if (par[_i].startsWith("y")) {
    			this.seYaw = Math.toRadians(Double.parseDouble(par[_i].replace("y", "")));
    			v.sendMessage(ChatColor.AQUA + "Around Y-axis degrees set to " + this.seYaw);
    			if (this.seYaw < 0 || this.seYaw > 359) {
    				v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
    			}
    			continue;
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return Rot3D.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Rot3D.timesUsed = tUsed;
    }
}
