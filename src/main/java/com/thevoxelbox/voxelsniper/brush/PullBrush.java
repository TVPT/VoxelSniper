package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * @author Piotr
 */
public class PullBrush extends Brush
{

    /**
     * @author Piotr
     */
    private final class BlockWrapper
    {

        private final int id;
        private final byte d;
        private final double str;
        private final int x;
        private final int y;
        private final int z;

        /**
         * @param block
         * @param st
         */
        public BlockWrapper(final Block block, final double st)
        {
            this.id = block.getTypeId();
            this.d = block.getData();
            this.x = block.getX();
            this.y = block.getY();
            this.z = block.getZ();
            this.str = st;
        }

        /**
         * @return the d
         */
        public byte getD()
        {
            return this.d;
        }

        /**
         * @return the id
         */
        public int getId()
        {
            return this.id;
        }

        /**
         * @return the str
         */
        public double getStr()
        {
            return this.str;
        }

        /**
         * @return the x
         */
        public int getX()
        {
            return this.x;
        }

        /**
         * @return the y
         */
        public int getY()
        {
            return this.y;
        }

        /**
         * @return the z
         */
        public int getZ()
        {
            return this.z;
        }
    }

    private int vh;

    private static int timesUsed = 0;

    private final HashSet<BlockWrapper> surface = new HashSet<BlockWrapper>();
    private double c1 = 1;

    private double c2 = 0;

    /**
     * Default Constructor.
     */
    public PullBrush()
    {
        this.setName("Soft Selection");
    }

    @Override
    public final int getTimesUsed()
    {
        return PullBrush.timesUsed;
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.custom(ChatColor.AQUA + "Pinch " + (-this.c1 + 1));
        vm.custom(ChatColor.AQUA + "Bubble " + this.c2);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        try
        {
            final double _pinch = Double.parseDouble(par[1]);
            final double _bubble = Double.parseDouble(par[2]);
            this.c1 = 1 - _pinch;
            this.c2 = _bubble;
        }
        catch (final Exception _ex)
        {
            v.sendMessage(ChatColor.RED + "Invalid brush parameters!");
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        PullBrush.timesUsed = tUsed;
    }

    /**
     * @param t
     *
     * @return double
     */
    private double getStr(final double t)
    {
        final double _lt = 1 - t;
        return (_lt * _lt * _lt) + 3 * (_lt * _lt) * t * this.c1 + 3 * _lt * (t * t) * this.c2; // My + (t * ((By + (t * ((c2 + (t * (0 - c2))) - By))) - My));
    }

    /**
     * @param v
     */
    private void getSurface(final SnipeData v)
    {
        this.surface.clear();

        final double _bpow = Math.pow(v.getBrushSize() + 0.5, 2);
        for (int _z = -v.getBrushSize(); _z <= v.getBrushSize(); _z++)
        {
            final double _zpow = Math.pow(_z, 2);
            final int _zz = this.getBlockPositionZ() + _z;
            for (int _x = -v.getBrushSize(); _x <= v.getBrushSize(); _x++)
            {
                final double _xpow = Math.pow(_x, 2);
                final int _xx = this.getBlockPositionX() + _x;
                for (int _y = -v.getBrushSize(); _y <= v.getBrushSize(); _y++)
                {
                    final double _pow = (_xpow + Math.pow(_y, 2) + _zpow);
                    if (_pow <= _bpow)
                    {
                        if (this.isSurface(_xx, this.getBlockPositionY() + _y, _zz))
                        {
                            this.surface.add(new BlockWrapper(this.clampY(_xx, this.getBlockPositionY() + _y, _zz), this.getStr(((_pow / _bpow)))));
                        }
                    }
                }
            }
        }
    }

    /**
     * @param x
     * @param y
     * @param z
     *
     * @return boolean
     */
    private boolean isSurface(final int x, final int y, final int z)
    {
        if (this.getBlockIdAt(x, y, z) == 0)
        {
            return false;
        }

        return ((this.getBlockIdAt(x, y - 1, z) == 0) || (this.getBlockIdAt(x, y + 1, z) == 0) || (this.getBlockIdAt(x + 1, y, z) == 0) || (this.getBlockIdAt(x - 1, y, z) == 0) || (this.getBlockIdAt(x, y, z + 1) == 0) || (this.getBlockIdAt(x, y, z - 1) == 0));
    }

    private void setBlock(final BlockWrapper block)
    {
        final Block _bl = this.clampY(block.getX(), block.getY() + (int) (this.vh * block.getStr()), block.getZ());
        if (this.getBlockIdAt(block.getX(), block.getY() - 1, block.getZ()) == 0)
        {
            _bl.setTypeId(block.getId());
            _bl.setData(block.getD());
            for (int _y = block.getY(); _y < _bl.getY(); _y++)
            {
                this.setBlockIdAt(block.getZ(), block.getX(), _y, 0);
            }
        }
        else
        {
            _bl.setTypeId(block.getId());
            _bl.setData(block.getD());
            for (int _y = block.getY() - 1; _y < _bl.getY(); _y++)
            {
                final Block _block = this.clampY(block.getX(), _y, block.getZ());
                _block.setTypeId(block.getId());
                _block.setData(block.getD());
            }
        }
    }

    private void setBlockDown(final BlockWrapper block)
    {
        final Block _block = this.clampY(block.getX(), block.getY() + (int) (this.vh * block.getStr()), block.getZ());
        _block.setTypeId(block.getId());
        _block.setData(block.getD());
        for (int _y = block.getY(); _y > _block.getY(); _y--)
        {
            this.setBlockIdAt(block.getZ(), block.getX(), _y, 0);
        }
        // }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.vh = v.getVoxelHeight();
        this.getSurface(v);

        if (this.vh > 0)
        {
            for (final BlockWrapper _block : this.surface)
            {
                this.setBlock(_block);
            }
        }
        else if (this.vh < 0)
        {
            for (final BlockWrapper _block : this.surface)
            {
                this.setBlockDown(_block);
            }
        }
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.vh = v.getVoxelHeight();

        this.surface.clear();

        int _lastY;
        int _newY;
        int _lastStr;
        double _str;
        final double _bPow = Math.pow(v.getBrushSize() + 0.5, 2);

        int _id;

        // Are we pulling up ?
        if (this.vh > 0)
        {

            // Z - Axis
            for (int _z = -v.getBrushSize(); _z <= v.getBrushSize(); _z++)
            {

                final int _zPow = _z * _z;
                final int _zz = this.getBlockPositionZ() + _z;

                // X - Axis
                for (int _x = -v.getBrushSize(); _x <= v.getBrushSize(); _x++)
                {

                    final int _xPow = _x * _x;
                    final int _xx = this.getBlockPositionX() + _x;

                    // Down the Y - Axis
                    for (int _y = v.getBrushSize(); _y >= -v.getBrushSize(); _y--)
                    {

                        final double _pow = _zPow + _xPow + (_y * _y);

                        // Is this in the range of the brush?
                        if (_pow <= _bPow && this.getWorld().getBlockTypeIdAt(_xx, this.getBlockPositionY() + _y, _zz) != 0)
                        {

                            int _yy = this.getBlockPositionY() + _y;

                            // Starting strength and new Position
                            _str = this.getStr(_pow / _bPow);
                            _lastStr = (int) (this.vh * _str);
                            _lastY = _yy + _lastStr;

                            this.clampY(_xx, _lastY, _zz).setTypeId(this.getWorld().getBlockTypeIdAt(_xx, _yy, _zz));

                            if (_str == 1)
                            {
                                _str = 0.8;
                            }

                            while (_lastStr > 0)
                            {
                                if (_yy < this.getBlockPositionY())
                                {
                                    _str = _str * _str;
                                }
                                _lastStr = (int) (this.vh * _str);
                                _newY = _yy + _lastStr;
                                _id = this.getWorld().getBlockTypeIdAt(_xx, _yy, _zz);
                                for (int _i = _newY; _i < _lastY; _i++)
                                {
                                    this.clampY(_xx, _i, _zz).setTypeId(_id);
                                }
                                _lastY = _newY;
                                _yy--;
                            }
                            break;
                        }
                    }
                }
            }
        }
        else
        {
            for (int _z = -v.getBrushSize(); _z <= v.getBrushSize(); _z++)
            {
                final double _zPow = Math.pow(_z, 2);
                final int _zz = this.getBlockPositionZ() + _z;
                for (int _x = -v.getBrushSize(); _x <= v.getBrushSize(); _x++)
                {
                    final double _xpow = Math.pow(_x, 2);
                    final int _xx = this.getBlockPositionX() + _x;
                    for (int _y = -v.getBrushSize(); _y <= v.getBrushSize(); _y++)
                    {
                        double _pow = (_xpow + Math.pow(_y, 2) + _zPow);
                        if (_pow <= _bPow && this.getWorld().getBlockTypeIdAt(_xx, this.getBlockPositionY() + _y, _zz) != 0)
                        {
                            final int _byy = this.getBlockPositionY() + _y;
                            _lastY = _byy + (int) (this.vh * this.getStr(_pow / _bPow));
                            this.clampY(_xx, _lastY, _zz).setTypeId(this.getWorld().getBlockTypeIdAt(_xx, _byy, _zz));
                            _y++;
                            _pow = (_xpow + Math.pow(_y, 2) + _zPow);
                            while (_pow <= _bPow)
                            {
                                final int _blY = this.getBlockPositionY() + _y + (int) (this.vh * this.getStr(_pow / _bPow));
                                final int _blId = this.getWorld().getBlockTypeIdAt(_xx, this.getBlockPositionY() + _y, _zz);
                                for (int _i = _blY; _i < _lastY; _i++)
                                {
                                    this.clampY(_xx, _i, _zz).setTypeId(_blId);
                                }
                                _lastY = _blY;
                                _y++;
                                _pow = (_xpow + Math.pow(_y, 2) + _zPow);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
