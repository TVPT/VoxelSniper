package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Extrude_Brush
 *
 * @author psanker
 */
public class ExtrudeBrush extends Brush
{
    private static int timesUsed = 0;
    private int level;
    private double trueCircle;
    private boolean awto;

    /**
     *
     */
    public ExtrudeBrush()
    {
        this.setName("Extrude");
    }

    private void extrudeD(final SnipeData v)
    {
        final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + this.trueCircle, 2);
        Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = _brushSize; _x >= 0; _x--)
        {
            final double _xPow = Math.pow(_x, 2);

            for (int _y = _brushSize; _y >= 0; _y--)
            {
                if ((_xPow + Math.pow(_y, 2)) <= _bPow)
                {
                    if (this.awto)
                    {
                        for (int _i = 0; _i <= this.level - 1; _i++)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _i - 1, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _i - 1, this.getBlockPositionZ() - _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _i - 1, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _i - 1, this.getBlockPositionZ() - _y), v, _undo);
                        }
                    }
                    else
                    {
                        for (int _i = 0; _i >= this.level + 1; _i--)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _i + 1, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _i + 1, this.getBlockPositionZ() - _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _i + 1, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _i, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _i + 1, this.getBlockPositionZ() - _y), v, _undo);
                        }
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    private void extrudeE(final SnipeData v)
    {
        final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + this.trueCircle, 2);
        Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = _brushSize; _x >= 0; _x--)
        {
            final double _xPow = Math.pow(_x, 2);

            for (int _y = _brushSize; _y >= 0; _y--)
            {
                if ((_xPow + Math.pow(_y, 2)) <= _bPow)
                {
                    if (this.awto)
                    {
                        for (int _i = 0; _i <= this.level - 1; _i++)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _i), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _i - 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _i), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _i - 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _i), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _i - 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _i), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _i - 1), v, _undo);
                        }
                    }
                    else
                    {
                        for (int _i = 0; _i >= this.level + 1; _i--)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _i), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _i + 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _i), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _i + 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _i), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _i + 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _i), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _i + 1), v, _undo);
                        }
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    private void extrudeN(final SnipeData v)
    {
        final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + this.trueCircle, 2);
        Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = _brushSize; _x >= 0; _x--)
        {
            final double _xPow = Math.pow(_x, 2);

            for (int _y = _brushSize; _y >= 0; _y--)
            {
                if ((_xPow + Math.pow(_y, 2)) <= _bPow)
                {
                    if (this.awto)
                    {
                        for (int _i = 0; _i <= this.level - 1; _i++)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _i, this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() - _i - 1, this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _i, this.getBlockPositionY() + _x, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() - _i - 1, this.getBlockPositionY() + _x, this.getBlockPositionZ() - _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _i, this.getBlockPositionY() - _x, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() - _i - 1, this.getBlockPositionY() - _x, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _i, this.getBlockPositionY() - _x, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() - _i - 1, this.getBlockPositionY() - _x, this.getBlockPositionZ() - _y), v, _undo);
                        }
                    }
                    else
                    {
                        for (int _i = 0; _i >= this.level + 1; _i--)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _i, this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() - _i + 1, this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _i, this.getBlockPositionY() + _x, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() - _i + 1, this.getBlockPositionY() + _x, this.getBlockPositionZ() - _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _i, this.getBlockPositionY() - _x, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() - _i + 1, this.getBlockPositionY() - _x, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _i, this.getBlockPositionY() - _x, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() - _i + 1, this.getBlockPositionY() - _x, this.getBlockPositionZ() - _y), v, _undo);
                        }
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    private void extrudeS(final SnipeData v)
    {
        final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + this.trueCircle, 2);
        Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = _brushSize; _x >= 0; _x--)
        {
            final double _xPow = Math.pow(_x, 2);

            for (int _y = _brushSize; _y >= 0; _y--)
            {
                if ((_xPow + Math.pow(_y, 2)) <= _bPow)
                {
                    if (this.awto)
                    {
                        for (int _i = 0; _i <= this.level - 1; _i++)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _i, this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() + _i + 1, this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _i, this.getBlockPositionY() + _x, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() + _i + 1, this.getBlockPositionY() + _x, this.getBlockPositionZ() - _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _i, this.getBlockPositionY() - _x, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() + _i + 1, this.getBlockPositionY() - _x, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _i, this.getBlockPositionY() - _x, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() + _i + 1, this.getBlockPositionY() - _x, this.getBlockPositionZ() - _y), v, _undo);
                        }
                    }
                    else
                    {
                        for (int _i = 0; _i >= this.level + 1; _i--)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _i, this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() + _i - 1, this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _i, this.getBlockPositionY() + _x, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() + _i - 1, this.getBlockPositionY() + _x, this.getBlockPositionZ() - _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _i, this.getBlockPositionY() - _x, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() + _i - 1, this.getBlockPositionY() - _x, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _i, this.getBlockPositionY() - _x, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() + _i - 1, this.getBlockPositionY() - _x, this.getBlockPositionZ() - _y), v, _undo);
                        }
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    private void extrudeU(final SnipeData v)
    {
        final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + this.trueCircle, 2);
        Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = _brushSize; _x >= 0; _x--)
        {
            final double _xPow = Math.pow(_x, 2);

            for (int _y = _brushSize; _y >= 0; _y--)
            {
                if ((_xPow + Math.pow(_y, 2)) <= _bPow)
                {
                    if (this.awto)
                    {
                        for (int _i = 0; _i <= this.level - 1; _i++)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i + 1, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i + 1, this.getBlockPositionZ() - _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i + 1, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i + 1, this.getBlockPositionZ() - _y), v, _undo);
                        }
                    }
                    else
                    {
                        for (int _i = 0; _i >= this.level + 1; _i--)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i - 1, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _i - 1, this.getBlockPositionZ() - _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() + _y), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i - 1, this.getBlockPositionZ() + _y), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i, this.getBlockPositionZ() - _y), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _i - 1, this.getBlockPositionZ() - _y), v, _undo);
                        }
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    private void extrudeW(final SnipeData v)
    {
        final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + this.trueCircle, 2);
        Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = _brushSize; _x >= 0; _x--)
        {
            final double _xpow = Math.pow(_x, 2);

            for (int _y = _brushSize; _y >= 0; _y--)
            {
                if ((_xpow + Math.pow(_y, 2)) <= _bPow)
                {
                    if (this.awto)
                    {
                        for (int _i = 0; _i <= this.level - 1; _i++)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _i), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _i + 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _i), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _i + 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _i), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _i + 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _i), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _i + 1), v, _undo);
                        }
                    }
                    else
                    {
                        for (int _i = 0; _i >= this.level + 1; _i--)
                        {
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _i), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _i - 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _i), this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _i - 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _i), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _i - 1), v, _undo);
                            _undo = this.perform(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _i), this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _i - 1), v, _undo);
                        }
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    private Undo perform(final Block b1, final Block b2, final SnipeData v, final Undo undo)
    {
        if (v.getVoxelList().contains(this.getBlockIdAt(b1.getX(), b1.getY(), b1.getZ())))
        {
            undo.put(b2);
            this.setBlockIdAt(this.getBlockIdAt(b1.getX(), b1.getY(), b1.getZ()), b2.getX(), b2.getY(), b2.getZ());
            this.clampY(b2.getX(), b2.getY(), b2.getZ()).setData(this.clampY(b1.getX(), b1.getY(), b1.getZ()).getData());
        }

        return undo;
    }

    private void pre(final SnipeData v, final BlockFace blockFace)
    {
        if (blockFace == null)
        {
            return;
        }

        this.level = v.getVoxelHeight();

        if (this.level == 0)
        {
            return;
        }
        else if (!this.awto)
        {
            if (this.level > 0)
            {
                this.level = -this.level;
            }
        }
        else if (this.awto)
        {
            if (this.level < 0)
            {
                this.level = -this.level;
            }
        }

        switch (blockFace)
        {
            case NORTH:
                this.extrudeN(v);
                break;

            case SOUTH:
                this.extrudeS(v);
                break;

            case EAST:
                this.extrudeE(v);
                break;

            case WEST:
                this.extrudeW(v);
                break;

            case UP:
                this.extrudeU(v);
                break;

            case DOWN:
                this.extrudeD(v);
                break;

            default:
                break;
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.awto = false;
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.awto = true;
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }


    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.voxelList();

        vm.custom(ChatColor.AQUA + ((this.trueCircle == 0.5) ? "True circle mode ON" : "True circle mode OFF"));
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v)
    {
        for (int _i = 1; _i < par.length; _i++)
        {
            final String _param = par[_i];

            try
            {
                if (_param.equalsIgnoreCase("info"))
                {
                    v.sendMessage(ChatColor.GOLD + "Extrude brush Parameters:");
                    v.sendMessage(ChatColor.AQUA + "/b ex true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ex false will switch back. (false is default)");
                    return;
                }
                else if (_param.startsWith("true"))
                {
                    this.trueCircle = 0.5;
                    v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                    continue;
                }
                else if (_param.startsWith("false"))
                {
                    this.trueCircle = 0;
                    v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                    continue;
                }
                else
                {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                    return;
                }
            }
            catch (final Exception _e)
            {
                v.sendMessage(ChatColor.RED + "Incorrect parameter \"" + _param + "\"; use the \"info\" parameter.");
            }
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return ExtrudeBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        ExtrudeBrush.timesUsed = tUsed;
    }
}
