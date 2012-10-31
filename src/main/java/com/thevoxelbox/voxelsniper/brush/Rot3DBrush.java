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
public class Rot3DBrush extends Brush {
	private static int timesUsed = 0;

	private int mode = 0;
    private int bSize;
    private int brushSize;
    private BlockWrapper[][][] snap;
    private double seYaw;
    private double sePitch;
    private double seRoll;

    /**
     * 
     */
    public Rot3DBrush() {
        this.setName("3D Rotation");
    }


    private void getMatrix() { // only need to do once. But y needs to change + sphere
    	final double _bpow = Math.pow(this.bSize + 0.5, 2);
        this.brushSize = (this.bSize * 2) + 1;

        this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];

        int _sx = this.getBlockPositionX() - this.bSize;
        int _sy = this.getBlockPositionY() - this.bSize;
        int _sz = this.getBlockPositionZ() - this.bSize;

        for (int _x = 0; _x < this.snap.length; _x++) {
        	final double _xPow = Math.pow(_x - this.bSize, 2);
            _sz = this.getBlockPositionZ() - this.bSize;

            for (int _z = 0; _z < this.snap.length; _z++) {
            	final double _zPow = Math.pow(_z - this.bSize, 2);
                _sy = this.getBlockPositionY() - this.bSize;

                for (int _y = 0; _y < this.snap.length; _y++) {
                    if (_xPow + _zPow + Math.pow(_y - this.bSize, 2) <= _bpow) {
                        final Block _b = this.clampY(_sx, _sy, _sz);
                        this.snap[_x][_y][_z] = new BlockWrapper(_b);
                        _b.setTypeId(0);
                        _sy++;
                    }
                }

                _sz++;
            }
            _sx++;
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
        final double _bpow = Math.pow(this.bSize + 0.5, 2);
        final double _cosYaw = Math.cos(this.seYaw);
        final double _sinYaw = Math.sin(this.seYaw);
        final double _cosPitch = Math.cos(this.sePitch);
        final double _sinPitch = Math.sin(this.sePitch);
        final double _cosRoll = Math.cos(this.seRoll);
        final double _sinRoll = Math.sin(this.seRoll);
        final boolean[][][] _doNotFill = new boolean[this.snap.length][this.snap.length][this.snap.length];
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = 0; _x < this.snap.length; _x++) {
            final int _xx = _x - this.bSize;
            final double _xPow = Math.pow(_xx, 2);
            
            for (int _z = 0; _z < this.snap.length; _z++) {
                final int _zz = _z - this.bSize;
                final double _zPow = Math.pow(_zz, 2);
                final double _newxzX = (_xx * _cosYaw) - (_zz * _sinYaw);
                final double _newxzZ = (_xx * _sinYaw) + (_zz * _cosYaw);
                
                for (int _y = 0; _y < this.snap.length; _y++) {
                    final int _yy = _y - this.bSize;
                    if (_xPow + _zPow + Math.pow(_yy, 2) <= _bpow) {
                        _undo.put(this.clampY(this.getBlockPositionX() + _xx, this.getBlockPositionY() + _yy, this.getBlockPositionZ() + _zz)); // just store whole sphere in undo, too complicated otherwise, since this
                                                                                      // brush both adds and remos things unpredictably.

                        final double _newxyX = (_newxzX * _cosPitch) - (_yy * _sinPitch);
                        final double _newxyY = (_newxzX * _sinPitch) + (_yy * _cosPitch); // calculates all three in succession in precise math space
                        final double _newyzY = (_newxyY * _cosRoll) - (_newxzZ * _sinRoll);
                        final double _newyzZ = (_newxyY * _sinRoll) + (_newxzZ * _cosRoll);

                        _doNotFill[(int) _newxyX + this.bSize][(int) _newyzY + this.bSize][(int) _newyzZ + this.bSize] = true; // only rounds off to nearest block
                                                                                                                           // after all three, though.

                        final BlockWrapper _vb = this.snap[_x][_y][_z];
                        if (_vb.getId() == 0) {
                            continue;
                        }
                        this.setBlockIdAt(_vb.getId(), this.getBlockPositionX() + (int) _newxyX, this.getBlockPositionY() + (int) _newyzY, this.getBlockPositionZ() + (int) _newyzZ);
                    }
                }
            }
        }

        for (int _x = 0; _x < this.snap.length; _x++) {
            final double _xPow = Math.pow(_x - this.bSize, 2);
            final int _fx = _x + this.getBlockPositionX() - this.bSize;
            
            for (int _z = 0; _z < this.snap.length; _z++) {
                final double _zPow = Math.pow(_z - this.bSize, 2);
                final int _fz = _z + this.getBlockPositionZ() - this.bSize;
                
                for (int _y = 0; _y < this.snap.length; _y++) {
                    if (_xPow + _zPow + Math.pow(_y - this.bSize, 2) <= _bpow) {
                        if (!_doNotFill[_x][_y][_z]) {
                            // smart fill stuff
                            final int _fy = _y + this.getBlockPositionY() - this.bSize;
                            final int _a = this.getBlockIdAt(_fx + 1, _fy, _fz);
                            final int _d = this.getBlockIdAt(_fx - 1, _fy, _fz);
                            final int _c = this.getBlockIdAt(_fx, _fy, _fz + 1);
                            final int _b = this.getBlockIdAt(_fx, _fy, _fz - 1);
                            
                            int _winner;
                            
                            if (_a == _b || _a == _c || _a == _d) { // I figure that since we are already narrowing it down to ONLY the holes left behind, it should
                                                              // be fine to do all 5 checks needed to be legit about it.
                                _winner = _a;
                            } else if (_b == _d || _c == _d) {
                                _winner = _d;
                            } else {
                                _winner = _b; // blockPositionY making this default, it will also automatically cover situations where B = C;
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
    	this.bSize = v.getBrushSize();

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
    	this.bSize = v.getBrushSize();

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
    	for (int _i = 1; _i < par.length; _i++) {
    		final String _param = par[_i];
    		// which way is clockwise is less obvious for roll and pitch... should probably fix that / make it clear
    		if (_param.equalsIgnoreCase("info")) {
    			v.sendMessage(ChatColor.GOLD + "Rotate brush Parameters:");
    			v.sendMessage(ChatColor.AQUA + "p[0-359] -- set degrees of pitch rotation (rotation about the Z axis).");
    			v.sendMessage(ChatColor.BLUE + "r[0-359] -- set degrees of roll rotation (rotation about the X axis).");
    			v.sendMessage(ChatColor.LIGHT_PURPLE + "y[0-359] -- set degrees of yaw rotation (Rotation about the Y axis).");
    			
    			return;
    		} else if (_param.startsWith("p")) {
    			this.sePitch = Math.toRadians(Double.parseDouble(_param.replace("p", "")));
    			v.sendMessage(ChatColor.AQUA + "Around Z-axis degrees set to " + this.sePitch);
    			if (this.sePitch < 0 || this.sePitch > 359) {
    				v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
    			}
    			continue;
    		} else if (_param.startsWith("r")) {
    			this.seRoll = Math.toRadians(Double.parseDouble(_param.replace("r", "")));
    			v.sendMessage(ChatColor.AQUA + "Around X-axis degrees set to " + this.seRoll);
    			if (this.seRoll < 0 || this.seRoll > 359) {
    				v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
    			}
    			continue;
    		} else if (_param.startsWith("y")) {
    			this.seYaw = Math.toRadians(Double.parseDouble(_param.replace("y", "")));
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
    	return Rot3DBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Rot3DBrush.timesUsed = tUsed;
    }
}
