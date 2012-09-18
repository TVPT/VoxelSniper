package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * @author Piotr
 */
public class PullTest extends SoftSelection {
	private static int timesUsed = 0;
    private int vh;

    public PullTest() {
        this.setName("Soft Selection");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.vh = v.getVoxelHeight();
        this.getSurface(v);

        if (this.vh > 0) {
            for (final sBlock _block : this.surface) {
                this.setBlock(_block);
            }
        } else if (this.vh < 0) {
            for (final sBlock _block : this.surface) {
                this.setBlockDown(_block);
            }
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        final int _brushSize = v.getBrushSize();

        this.vh = v.getVoxelHeight();
        this.surface.clear();

        int _lasty;
        int _newy;
        int _laststr;
        double _str;
        final double _bpow = Math.pow(_brushSize + 0.5, 2);

        int _id;

        // Are we pulling up ?
        if (this.vh > 0) {

            // Z - Axis
            for (int _z = -_brushSize; _z <= _brushSize; _z++) {

                final int _zpow = _z * _z;
                final int _zz = this.getBlockPositionZ() + _z;

                // X - Axis
                for (int _x = -_brushSize; _x <= _brushSize; _x++) {

                    final int _xpow = _x * _x;
                    final int _xx = this.getBlockPositionX() + _x;

                    // Down the Y - Axis
                    for (int _y = _brushSize; _y >= -_brushSize; _y--) {

                        final double _pow = _zpow + _xpow + (_y * _y);

                        // Is this in the range of the brush?
                        if (_pow <= _bpow && this.getWorld().getBlockTypeIdAt(_xx, this.getBlockPositionY() + _y, _zz) != 0) {

                            int _yy = this.getBlockPositionY() + _y;

                            // Starting strength and new Position
                            _str = this.getStr(_pow / _bpow);
                            _laststr = (int) (this.vh * _str);
                            _lasty = _yy + _laststr;

                            this.clampY(_xx, _lasty, _zz).setTypeId(this.getWorld().getBlockTypeIdAt(_xx, _yy, _zz));

                            if (_str == 1) {
                                _str = 0.8;
                            }

                            while (_laststr > 0) {
                                if (_yy < this.getBlockPositionY()) {
                                    _str = _str * _str;
                                }
                                _laststr = (int) (this.vh * _str);
                                _newy = _yy + _laststr;
                                _id = this.getWorld().getBlockTypeIdAt(_xx, _yy, _zz);
                                for (int i = _newy; i < _lasty; i++) {
                                    this.clampY(_xx, i, _zz).setTypeId(_id);
                                }
                                _lasty = _newy;
                                _yy--;
                            }
                            break;
                        }
                    }
                    
                }
            }
        } else {
            for (int _z = -_brushSize; _z <= _brushSize; _z++) {
                final double _zpow = Math.pow(_z, 2);
                final int _zz = this.getBlockPositionZ() + _z;
                for (int _x = -_brushSize; _x <= _brushSize; _x++) {
                    final double _xpow = Math.pow(_x, 2);
                    final int _xx = this.getBlockPositionX() + _x;
                    for (int _y = -_brushSize; _y <= _brushSize; _y++) {
                        double _pow = (_xpow + Math.pow(_y, 2) + _zpow);
                        if (_pow <= _bpow && this.getWorld().getBlockTypeIdAt(_xx, this.getBlockPositionY() + _y, _zz) != 0) {
                            final int _byy = this.getBlockPositionY() + _y;
                            _lasty = _byy + (int) (this.vh * this.getStr(_pow / _bpow));
                            this.clampY(_xx, _lasty, _zz).setTypeId(this.getWorld().getBlockTypeIdAt(_xx, _byy, _zz));
                            _y++;
                            _pow = (_xpow + Math.pow(_y, 2) + _zpow);
                            while (_pow <= _bpow) {
                                final int _blY = this.getBlockPositionY() + _y + (int) (this.vh * this.getStr(_pow / _bpow));
                                final int _blId = this.getWorld().getBlockTypeIdAt(_xx, this.getBlockPositionY() + _y, _zz);
                                for (int _i = _blY; _i < _lasty; _i++) {
                                    this.clampY(_xx, _i, _zz).setTypeId(_blId);
                                }
                                _lasty = _blY;
                                _y++;
                                _pow = (_xpow + Math.pow(_y, 2) + _zpow);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private final void setBlock(final sBlock b) {
        final Block _block = this.clampY(b.x, b.y + (int) (this.vh * b.str), b.z);
        if (this.getBlockIdAt(b.x, b.y - 1, b.z) == 0) {
            _block.setTypeId(b.id);
            _block.setData(b.d);
            for (int _y = b.y; _y < _block.getY(); _y++) {
                this.setBlockIdAt(0, b.x, _y, b.z);
            }
        } else {
            _block.setTypeId(b.id);
            _block.setData(b.d);
            for (int _y = b.y - 1; _y < _block.getY(); _y++) {
                final Block _blo = this.clampY(b.x, _y, b.z);
                _blo.setTypeId(b.id);
                _blo.setData(b.d);
            }
        }
    }

    private final void setBlockDown(final sBlock b) {
        final Block _block = this.clampY(b.x, b.y + (int) (this.vh * b.str), b.z);
        _block.setTypeId(b.id);
        _block.setData(b.d);
        for (int _y = b.y; _y > _block.getY(); _y--) {
            this.setBlockIdAt(0, b.x, _y, b.z);
        }
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    	vm.height();
    	vm.custom(ChatColor.AQUA + "Pinch " + (-this.c1 + 1));
    	vm.custom(ChatColor.AQUA + "Bubble " + this.c2);
    }
    
    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
    	try {
    		final double _pinch = Double.parseDouble(par[1]);
    		final double _bubble = Double.parseDouble(par[2]);
    		this.c1 = 1 - _pinch;
    		this.c2 = _bubble;
    	} catch (final Exception ex) {
    		v.sendMessage(ChatColor.RED + "Invalid brush parameters!");
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return PullTest.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	PullTest.timesUsed = tUsed;
    }
}
