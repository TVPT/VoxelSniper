package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Gavjenks
 */
public class DomeBrush extends Brush {
	private static final int DEFAULT_HEIGHT = 1024;
	private static int timesUsed = 0;
    private boolean fsa = true;

    // should only have the truecircle option this time.
    // param is just height -- will start with the face disc and instead of making the actual disc, will make a dome of that height.
    // defaul height = diameter of the disc. Other heights would make parabolic domes, thus I am developing this in concurrence with parabola brush. It will be
    // able to do everything this can, but would be harder to use.
    // for simplicity for this brush also, arrow will do half block accuracy, powder full block - will work eve for sideways dome, though maybe limited
    // usefulness there. If /v is not a half block, override to full block accuracy. If it is, make full double step stuff for all the blocks underneath the top
    // curve, in same material.
    private double height = DEFAULT_HEIGHT; // just avoiding initiating bsize yet;

    public DomeBrush() {
        this.setName("Dome");
    }

    private final void dome(final SnipeData v, final boolean fillSolid) {
        final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + 0.5, 2);
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        final int _voxelId = v.getVoxelId();
        final double _curvature = this.height / (_brushSize + 0.5);
        final double _powCurvature = Math.pow(_curvature, 2);
        final int[][] _heightMap = new int[_brushSize + 2][_brushSize + 2];
        double _yManip;
        double _centerRef;
        
        if (this.height == DEFAULT_HEIGHT) {
            this.height = _brushSize + 0.5;
        }
        
        if (this.fsa || _voxelId != Material.STEP.getId()) { // override half block accuracy if /v not set to a half block.
            _yManip = 0.5; // whole block accuracy
            _centerRef = 0;
        } else {
            _yManip = 0.5; // half block accuracy
            _centerRef = -0.25;
        }

        for (int _x = _brushSize; _x >= 0; _x--) {
            final double _xPow = Math.pow(_x, 2);
            for (int _z = _brushSize; _z >= 0; _z--) {
                final double _zPow = Math.pow(_z, 2);
                for (int _y = (int) this.height; _y >= 0; _y--) {
                    final double _yPowMinus = Math.pow(_y - _yManip + _centerRef, 2);
                    final double _yPow = Math.pow(_y + _centerRef, 2);
                    if ((_xPow + (_yPowMinus / Math.pow(_curvature, 2)) + _zPow) <= _bPow) { // If within the ellipse
                        final double _yPowPlus = Math.pow(_y + _yManip + _centerRef, 2);
                        if ((_xPow + (_yPowPlus / _powCurvature) + _zPow) > _bPow) { // If nothing else further out (i.e. if on the surface)
                            _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z));
                            _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z));
                            _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z)); // only want top of dome. So only 4 of these.
                            _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z));

                            _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() + _z));
                            _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() - _z)); // blocks right underneath each
                            _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() + _z));
                            _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() - _z));

                            if (!this.fsa && ((_xPow + (_yPow / _powCurvature) + _zPow) > _bPow)) { // if half block accuracy is being used AND this is a
                                                                                                         // portion of the curve that is closer to matching a
                                                                                                         // half block than a full block...
                                this.setBlockIdAt(Material.STEP.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z); // set to a half block
                                this.setBlockIdAt(Material.STEP.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z);
                                this.setBlockIdAt(Material.STEP.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                                this.setBlockIdAt(Material.STEP.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z);
                                // AND place a full double step underneath to prevent gaps (might be slightly bulkier than could be possible... but much simpler
                                // to code)
                                _heightMap[_x][_z] = _y - 1;
                                this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() + _z);
                                this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() - _z);
                                this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() + _z);
                                this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() - _z);
                            } else {
                                if (_voxelId == Material.STEP.getId()) { // if half block accuracy, but this particular position conforms better to a full block
                                    _heightMap[_x][_z] = _y;
                                    this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z); // set to a full double step
                                    this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z);
                                    this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                                    this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z);
                                } else { // if full block accuracy
                                    _heightMap[_x][_z] = _y;
                                    this.setBlockIdAt(_voxelId, this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z); // set to a full block of whatever /v is.
                                    this.setBlockIdAt(_voxelId, this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z);
                                    this.setBlockIdAt(_voxelId, this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                                    this.setBlockIdAt(_voxelId, this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int _x = 0; _x <= _brushSize; _x++) {
            for (int _z = 0; _z <= _brushSize; _z++) {
                for (int _i = _heightMap[_x][_z] - 1; _i >= 0; _i--) {
                    if (!fillSolid && _heightMap[_x][_z + 1] < _i || _heightMap[_x + 1][_z] < _i) { // if annoying air gap in wall in x or z direction
                        _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z));
                        _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z));
                        _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z));
                        _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z));

                        if (_voxelId == Material.STEP.getId()) {
                            this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z);
                            this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z);
                            this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z);
                            this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z);
                        } else {
                            this.setBlockIdAt(_voxelId, this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z);
                            this.setBlockIdAt(_voxelId, this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z);
                            this.setBlockIdAt(_voxelId, this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z);
                            this.setBlockIdAt(_voxelId, this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z);
                        }
                    }
                    if (fillSolid) { // fill in solid.
                        _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z));
                        _undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z));
                        _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z));
                        _undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z));

                        if (_voxelId == Material.STEP.getId()) {
                            this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z);
                            this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z);
                            this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z);
                            this.setBlockIdAt(Material.DOUBLE_STEP.getId(), this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z);
                        } else {
                            this.setBlockIdAt(_voxelId, this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z);
                            this.setBlockIdAt(_voxelId, this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z);
                            this.setBlockIdAt(_voxelId, this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _z);
                            this.setBlockIdAt(_voxelId, this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _z);
                        }
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    private void pre(final SnipeData v, final BlockFace bf, final boolean fillSolid) {
        if (bf == null) {
            return;
        }
        switch (bf) {
        case NORTH:
        case SOUTH:
            this.dome(v, fillSolid); // would be domeNS later
            break;

        case EAST:
        case WEST:
            this.dome(v, fillSolid); // would be domeEW later
            break;

        case UP:
        case DOWN:
            this.dome(v, fillSolid);
            break;

        default:
            break;
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()), false);
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.setBlockPositionX(this.getLastBlock().getX());
    	this.setBlockPositionY(this.getLastBlock().getY());
    	this.setBlockPositionZ(this.getLastBlock().getZ());
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()), true);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    	vm.voxel();
    	vm.height();
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	for (int _i = 1; _i < par.length; _i++) {
    		final String _param = par[_i];
    		if (_param.equalsIgnoreCase("info")) {
    			v.sendMessage(ChatColor.GOLD + "Dome brush Parameters:");
    			v.sendMessage(ChatColor.AQUA
    					+ "/b dome h[number] -- set a custom dome height.  Default is the radius of the brush.  Anything else will make it a parabolic dome with circular base.  Cannot be negative.");
    			v.sendMessage(ChatColor.BLUE
    					+ "/b dome acc [or inacc] -- set brush to half (acc) or full step (inacc) accuracy.  if /v is anything other than 44, will override you and force full step accuracy.");
				return;
			} else if (_param.startsWith("h")) {
				this.height = Double.parseDouble(par[_i].replace("h", ""));
    			v.sendMessage(ChatColor.AQUA + "Dome height set to: " + this.height);
    			continue;
    		} else if (_param.startsWith("inacc")) {
    			this.fsa = true;
    			v.sendMessage(ChatColor.BLUE + "Full step accuracy.");
    			continue;
    		} else if (_param.startsWith("acc")) {
    			if (v.getVoxelId() != 44) {
    				this.fsa = true;
    				v.sendMessage(ChatColor.BLUE + "Full step accuracy. (overridden since you don't have half steps selected)");
    			} else {
    				this.fsa = false;
    				v.sendMessage(ChatColor.BLUE + "Half step accuracy.");
    			}
    			continue;
    		} else {
    			v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return DomeBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	DomeBrush.timesUsed = tUsed;
    }
}
