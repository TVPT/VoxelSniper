package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

import org.bukkit.ChatColor;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Splatter_Brushes
 *
 * @author Voxel
 */
public class SplatterDiscBrush extends PerformBrush
{
    private static final int GROW_PERCENT_MIN = 1;
    private static final int GROW_PERCENT_DEFAULT = 1000;
    private static final int GROW_PERCENT_MAX = 9999;

    private static final int SEED_PERCENT_MIN = 1;
    private static final int SEED_PERCENT_DEFAULT = 1000;
    private static final int SEED_PERCENT_MAX = 9999;

    private static final int SPLATREC_PERCENT_MIN = 1;
    private static final int SPLATREC_PERCENT_DEFAULT = 3;
    private static final int SPLATREC_PERCENT_MAX = 10;

    private static int timesUsed = 0;

    private int seedPercent; // Chance block on first pass is made active
    private int growPercent; // chance block on recursion pass is made active
    private int splatterRecursions; // How many times you grow the seeds
    private Random generator = new Random();

    /**
     *
     */
    public SplatterDiscBrush()
    {
        this.setName("Splatter Disc");
    }

    private void splatterDisc(final SnipeData v)
    {
        if (this.seedPercent < SEED_PERCENT_MIN || this.seedPercent > SEED_PERCENT_MAX)
        {
            v.sendMessage(ChatColor.BLUE + "Seed percent set to: 10%");
            this.seedPercent = SEED_PERCENT_DEFAULT;
        }
        if (this.growPercent < GROW_PERCENT_MIN || this.growPercent > GROW_PERCENT_MAX)
        {
            v.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
            this.growPercent = GROW_PERCENT_DEFAULT;
        }
        if (this.splatterRecursions < SPLATREC_PERCENT_MIN || this.splatterRecursions > SPLATREC_PERCENT_MAX)
        {
            v.sendMessage(ChatColor.BLUE + "Recursions set to: 3");
            this.splatterRecursions = SPLATREC_PERCENT_DEFAULT;
        }
        final int _brushSize = v.getBrushSize();
        final int _twoBrushSize = 2 * _brushSize;
        final int[][] _splat = new int[_twoBrushSize + 1][_twoBrushSize + 1];

        // Seed the array
        for (int _x = _twoBrushSize; _x >= 0; _x--)
        {
            for (int _y = _twoBrushSize; _y >= 0; _y--)
            {

                if (this.generator.nextInt(SEED_PERCENT_MAX + 1) <= this.seedPercent)
                {
                    _splat[_x][_y] = 1;

                }
            }
        }
        // Grow the seeds
        final int _gref = this.growPercent;
        int _growcheck;
        final int[][] _tempsplat = new int[_twoBrushSize + 1][_twoBrushSize + 1];
        for (int _r = 0; _r < this.splatterRecursions; _r++)
        {

            this.growPercent = _gref - ((_gref / this.splatterRecursions) * (_r));
            for (int _x = _twoBrushSize; _x >= 0; _x--)
            {
                for (int _y = _twoBrushSize; _y >= 0; _y--)
                {

                    _tempsplat[_x][_y] = _splat[_x][_y]; // prime tempsplat

                    _growcheck = 0;
                    if (_splat[_x][_y] == 0)
                    {
                        if (_x != 0 && _splat[_x - 1][_y] == 1)
                        {
                            _growcheck++;
                        }
                        if (_y != 0 && _splat[_x][_y - 1] == 1)
                        {
                            _growcheck++;
                        }
                        if (_x != 2 * _brushSize && _splat[_x + 1][_y] == 1)
                        {
                            _growcheck++;
                        }
                        if (_y != 2 * _brushSize && _splat[_x][_y + 1] == 1)
                        {
                            _growcheck++;
                        }

                    }

                    if (_growcheck >= 1 && this.generator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent)
                    {
                        _tempsplat[_x][_y] = 1; // prevent bleed into splat
                    }

                }

            }
            // integrate tempsplat back into splat at end of iteration
            for (int _x = _twoBrushSize; _x >= 0; _x--)
            {
                for (int _y = _twoBrushSize; _y >= 0; _y--)
                {

                    _splat[_x][_y] = _tempsplat[_x][_y];

                }
            }
        }
        this.growPercent = _gref;
        // Fill 1x1 holes
        for (int _x = _twoBrushSize; _x >= 0; _x--)
        {
            for (int _y = _twoBrushSize; _y >= 0; _y--)
            {

                if (_splat[Math.max(_x - 1, 0)][_y] == 1 && _splat[Math.min(_x + 1, _twoBrushSize)][_y] == 1 && _splat[_x][Math.max(0, _y - 1)] == 1 && _splat[_x][Math.min(_twoBrushSize, _y + 1)] == 1)
                {
                    _splat[_x][_y] = 1;
                }

            }
        }

        // Make the changes
        final double _rPow = Math.pow(_brushSize + 1, 2);
        for (int _x = _twoBrushSize; _x >= 0; _x--)
        {
            final double _xPow = Math.pow(_x - _brushSize - 1, 2);
            for (int _y = _twoBrushSize; _y >= 0; _y--)
            {
                if (_splat[_x][_y] == 1 && _xPow + Math.pow(_y - _brushSize - 1, 2) <= _rPow)
                {
                    this.current.perform(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _brushSize + _y));
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.splatterDisc(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.splatterDisc(v);
    }

    @Override
    public final void info(final Message vm)
    {
        if (this.seedPercent < SEED_PERCENT_MIN || this.seedPercent > SEED_PERCENT_MAX)
        {
            this.seedPercent = SEED_PERCENT_DEFAULT;
        }
        if (this.growPercent < GROW_PERCENT_MIN || this.growPercent > GROW_PERCENT_MAX)
        {
            this.growPercent = GROW_PERCENT_DEFAULT;
        }
        if (this.splatterRecursions < SPLATREC_PERCENT_MIN || this.splatterRecursions > SPLATREC_PERCENT_MAX)
        {
            this.splatterRecursions = SPLATREC_PERCENT_DEFAULT;
        }
        vm.brushName("Splatter Disc");
        vm.size();
        vm.custom(ChatColor.BLUE + "Seed percent set to: " + this.seedPercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Growth percent set to: " + this.growPercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Recursions set to: " + this.splatterRecursions);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int _i = 1; _i < par.length; _i++)
        {
            final String _param = par[_i];

            if (_param.equalsIgnoreCase("info"))
            {
                v.sendMessage(ChatColor.GOLD + "Splatter Disc brush Parameters:");
                v.sendMessage(ChatColor.AQUA + "/b sd s[int] -- set a seed percentage (1-9999). 100 = 1% Default is 1000");
                v.sendMessage(ChatColor.AQUA + "/b sd g[int] -- set a growth percentage (1-9999).  Default is 1000");
                v.sendMessage(ChatColor.AQUA + "/b sd r[int] -- set a recursion (1-10).  Default is 3");
                return;
            }
            else if (_param.startsWith("s"))
            {
                final double _temp = Integer.parseInt(_param.replace("s", ""));
                if (_temp >= SEED_PERCENT_MIN && _temp <= SEED_PERCENT_MAX)
                {
                    v.sendMessage(ChatColor.AQUA + "Seed percent set to: " + _temp / 100 + "%");
                    this.seedPercent = (int) _temp;
                }
                else
                {
                    v.sendMessage(ChatColor.RED + "Seed percent must be an integer 1-9999!");
                }
                continue;
            }
            else if (_param.startsWith("g"))
            {
                final double _temp = Integer.parseInt(_param.replace("g", ""));
                if (_temp >= GROW_PERCENT_MIN && _temp <= GROW_PERCENT_MAX)
                {
                    v.sendMessage(ChatColor.AQUA + "Growth percent set to: " + _temp / 100 + "%");
                    this.growPercent = (int) _temp;
                }
                else
                {
                    v.sendMessage(ChatColor.RED + "Growth percent must be an integer 1-9999!");
                }
                continue;
            }
            else if (_param.startsWith("r"))
            {
                final int _temp = Integer.parseInt(_param.replace("r", ""));
                if (_temp >= SPLATREC_PERCENT_MIN && _temp <= SPLATREC_PERCENT_MAX)
                {
                    v.sendMessage(ChatColor.AQUA + "Recursions set to: " + _temp);
                    this.splatterRecursions = _temp;
                }
                else
                {
                    v.sendMessage(ChatColor.RED + "Recursions must be an integer 1-10!");
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
        return SplatterDiscBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        SplatterDiscBrush.timesUsed = tUsed;
    }
}
