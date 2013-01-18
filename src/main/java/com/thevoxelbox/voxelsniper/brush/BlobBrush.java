package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

import org.bukkit.ChatColor;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Blob_Brush
 *
 * @author Giltwist
 */
public class BlobBrush extends PerformBrush
{
    private static final int GROW_PERCENT_DEFAULT = 1000;
    private static final int GROW_PERCENT_MIN = 1;
    private static final int GROW_PERCENT_MAX = 9999;

    private static int timesUsed = 0;
    private Random randomGenerator = new Random();
    private int growPercent = GROW_PERCENT_DEFAULT; // chance block on recursion pass is made active

    /**
     *
     */
    public BlobBrush()
    {
        this.setName("Blob");
    }

    private void checkValidGrowPercent(final SnipeData v)
    {
        if (this.growPercent < GROW_PERCENT_MIN || this.growPercent > GROW_PERCENT_MAX)
        {
            this.growPercent = GROW_PERCENT_DEFAULT;
            v.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
        }
    }

    private void digBlob(final SnipeData v)
    {
        final int _bSize = v.getBrushSize();
        final int _twoBrushSize = 2 * _bSize;
        final int[][][] _splat = new int[_twoBrushSize + 1][_twoBrushSize + 1][_twoBrushSize + 1];
        final int[][][] _tempSplat = new int[_twoBrushSize + 1][_twoBrushSize + 1][_twoBrushSize + 1];

        this.checkValidGrowPercent(v);

        // Seed the array
        for (int _x = _twoBrushSize; _x >= 0; _x--)
        {
            for (int _y = _twoBrushSize; _y >= 0; _y--)
            {
                for (int _z = _twoBrushSize; _z >= 0; _z--)
                {
                    if ((_x == 0 || _y == 0 | _z == 0 || _x == _twoBrushSize || _y == _twoBrushSize || _z == _twoBrushSize) && this.randomGenerator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent)
                    {
                        _splat[_x][_y][_z] = 0;
                    }
                    else
                    {
                        _splat[_x][_y][_z] = 1;
                    }
                }
            }
        }

        // Grow the seed
        for (int _r = 0; _r < _bSize; _r++)
        {

            for (int _x = _twoBrushSize; _x >= 0; _x--)
            {
                for (int _y = _twoBrushSize; _y >= 0; _y--)
                {
                    for (int _z = _twoBrushSize; _z >= 0; _z--)
                    {
                        _tempSplat[_x][_y][_z] = _splat[_x][_y][_z];
                        double _growCheck = 0;
                        if (_splat[_x][_y][_z] == 1)
                        {
                            if (_x != 0 && _splat[_x - 1][_y][_z] == 0)
                            {
                                _growCheck++;
                            }
                            if (_y != 0 && _splat[_x][_y - 1][_z] == 0)
                            {
                                _growCheck++;
                            }
                            if (_z != 0 && _splat[_x][_y][_z - 1] == 0)
                            {
                                _growCheck++;
                            }
                            if (_x != 2 * _bSize && _splat[_x + 1][_y][_z] == 0)
                            {
                                _growCheck++;
                            }
                            if (_y != 2 * _bSize && _splat[_x][_y + 1][_z] == 0)
                            {
                                _growCheck++;
                            }
                            if (_z != 2 * _bSize && _splat[_x][_y][_z + 1] == 0)
                            {
                                _growCheck++;
                            }
                        }

                        if (_growCheck >= 1 && this.randomGenerator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent)
                        {
                            _tempSplat[_x][_y][_z] = 0; // prevent bleed into splat
                        }
                    }
                }
            }

            // shouldn't this just be splat = tempsplat;? -Gavjenks
            // integrate tempsplat back into splat at end of iteration
            for (int _x = _twoBrushSize; _x >= 0; _x--)
            {
                for (int _y = _twoBrushSize; _y >= 0; _y--)
                {
                    for (int _z = _twoBrushSize; _z >= 0; _z--)
                    {
                        _splat[_x][_y][_z] = _tempSplat[_x][_y][_z];
                    }
                }
            }
        }

        final double _rPow = Math.pow(_bSize + 1, 2);

        // Make the changes        
        for (int _x = _twoBrushSize; _x >= 0; _x--)
        {
            final double _xPow = Math.pow(_x - _bSize - 1, 2);

            for (int _y = _twoBrushSize; _y >= 0; _y--)
            {
                final double _yPow = Math.pow(_y - _bSize - 1, 2);

                for (int _z = _twoBrushSize; _z >= 0; _z--)
                {
                    if (_splat[_x][_y][_z] == 1 && _xPow + _yPow + Math.pow(_z - _bSize - 1, 2) <= _rPow)
                    {
                        this.current.perform(this.clampY(this.getBlockPositionX() - _bSize + _x, this.getBlockPositionY() - _bSize + _z, this.getBlockPositionZ() - _bSize + _y));
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    private void growBlob(final SnipeData v)
    {
        final int _bSize = v.getBrushSize();
        final int _twoBrushSize = 2 * _bSize;
        final int[][][] _splat = new int[_twoBrushSize + 1][_twoBrushSize + 1][_twoBrushSize + 1];
        final int[][][] _tempSplat = new int[_twoBrushSize + 1][_twoBrushSize + 1][_twoBrushSize + 1];

        this.checkValidGrowPercent(v);

        // Seed the array
        _splat[_bSize][_bSize][_bSize] = 1;

        // Grow the seed
        for (int _r = 0; _r < _bSize; _r++)
        {

            for (int _x = _twoBrushSize; _x >= 0; _x--)
            {
                for (int _y = _twoBrushSize; _y >= 0; _y--)
                {
                    for (int _z = _twoBrushSize; _z >= 0; _z--)
                    {
                        _tempSplat[_x][_y][_z] = _splat[_x][_y][_z];
                        int _growCheck = 0;
                        if (_splat[_x][_y][_z] == 0)
                        {
                            if (_x != 0 && _splat[_x - 1][_y][_z] == 1)
                            {
                                _growCheck++;
                            }
                            if (_y != 0 && _splat[_x][_y - 1][_z] == 1)
                            {
                                _growCheck++;
                            }
                            if (_z != 0 && _splat[_x][_y][_z - 1] == 1)
                            {
                                _growCheck++;
                            }
                            if (_x != 2 * _bSize && _splat[_x + 1][_y][_z] == 1)
                            {
                                _growCheck++;
                            }
                            if (_y != 2 * _bSize && _splat[_x][_y + 1][_z] == 1)
                            {
                                _growCheck++;
                            }
                            if (_z != 2 * _bSize && _splat[_x][_y][_z + 1] == 1)
                            {
                                _growCheck++;
                            }
                        }

                        if (_growCheck >= 1 && this.randomGenerator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent)
                        {
                            _tempSplat[_x][_y][_z] = 1; // prevent bleed into splat
                        }
                    }
                }
            }

            // integrate tempsplat back into splat at end of iteration
            for (int _x = _twoBrushSize; _x >= 0; _x--)
            {
                for (int _y = _twoBrushSize; _y >= 0; _y--)
                {
                    for (int _z = _twoBrushSize; _z >= 0; _z--)
                    {
                        _splat[_x][_y][_z] = _tempSplat[_x][_y][_z];
                    }
                }
            }
        }

        final double _rPow = Math.pow(_bSize + 1, 2);

        // Make the changes
        for (int _x = _twoBrushSize; _x >= 0; _x--)
        {
            final double _xPow = Math.pow(_x - _bSize - 1, 2);

            for (int _y = _twoBrushSize; _y >= 0; _y--)
            {
                final double _yPow = Math.pow(_y - _bSize - 1, 2);

                for (int _z = _twoBrushSize; _z >= 0; _z--)
                {
                    if (_splat[_x][_y][_z] == 1 && _xPow + _yPow + Math.pow(_z - _bSize - 1, 2) <= _rPow)
                    {
                        this.current.perform(this.clampY(this.getBlockPositionX() - _bSize + _x, this.getBlockPositionY() - _bSize + _z, this.getBlockPositionZ() - _bSize + _y));
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.growBlob(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.digBlob(v);
    }

    @Override
    public final void info(final Message vm)
    {
        this.checkValidGrowPercent(null);

        vm.brushName(this.getName());
        vm.size();
        vm.custom(ChatColor.BLUE + "Growth percent set to: " + this.growPercent / 100f + "%");
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int _i = 1; _i < par.length; _i++)
        {
            final String _param = par[_i];

            if (_param.equalsIgnoreCase("info"))
            {
                v.sendMessage(ChatColor.GOLD + "Blob brush Parameters:");
                v.sendMessage(ChatColor.AQUA + "/b blob g[int] -- set a growth percentage (" + GROW_PERCENT_MIN + "-" + GROW_PERCENT_MAX + ").  Default is " + GROW_PERCENT_DEFAULT);
                return;
            }
            if (_param.startsWith("g"))
            {
                final int _temp = Integer.parseInt(_param.replace("g", ""));
                if (_temp >= GROW_PERCENT_MIN && _temp <= GROW_PERCENT_MAX)
                {
                    v.sendMessage(ChatColor.AQUA + "Growth percent set to: " + (float) _temp / 100f + "%");
                    this.growPercent = _temp;
                }
                else
                {
                    v.sendMessage(ChatColor.RED + "Growth percent must be an integer " + GROW_PERCENT_MIN + "-" + GROW_PERCENT_MAX + "!");
                }
                continue;
            }
            else
            {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return BlobBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        BlobBrush.timesUsed = tUsed;
    }
}
