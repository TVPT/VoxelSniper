package com.thevoxelbox.voxelsniper.brush;


import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Snow_cone_brush
 *
 * @author Voxel
 */
public class SnowConeBrush extends Brush
{
    private static int timesUsed = 0;

    private void addSnow(final SnipeData v, Block targetBlock)
    {
        int _brushSize;
        int blockPositionX = targetBlock.getX();
        int blockPositionY = targetBlock.getY();
        int blockPositionZ = targetBlock.getZ();
        if (this.getBlockIdAt(blockPositionX, blockPositionY, blockPositionZ) == Material.AIR.getId())
        {
            _brushSize = 0;
        }
        else
        {
            _brushSize = this.clampY(blockPositionX, blockPositionY, blockPositionZ).getData() + 1;
        }

        final int _twoBrushSize = 2 * _brushSize;
        final int[][] _snowcone = new int[_twoBrushSize + 1][_twoBrushSize + 1]; // Will hold block IDs
        final int[][] _snowconedata = new int[_twoBrushSize + 1][_twoBrushSize + 1]; // Will hold data values for snowcone
        final int[][] _yoffset = new int[_twoBrushSize + 1][_twoBrushSize + 1];
        // prime the arrays

        for (int _x = 0; _x <= _twoBrushSize; _x++)
        {
            for (int _z = 0; _z <= _twoBrushSize; _z++)
            {
                boolean _flag = true;

                for (int _i = 0; _i < 10; _i++)
                { // overlay
                    if (_flag)
                    {
                        if ((this.getBlockIdAt(blockPositionX - _brushSize + _x, blockPositionY - _i, blockPositionZ - _brushSize + _z) == 0 || this.getBlockIdAt(blockPositionX - _brushSize + _x, blockPositionY - _i, blockPositionZ - _brushSize + _z) == Material.SNOW.getId()) && this.getBlockIdAt(blockPositionX - _brushSize + _x, blockPositionY - _i - 1, blockPositionZ - _brushSize + _z) != Material.AIR.getId() && this.getBlockIdAt(blockPositionX - _brushSize + _x, blockPositionY - _i - 1, blockPositionZ - _brushSize + _z) != Material.SNOW.getId())
                        {
                            _flag = false;
                            _yoffset[_x][_z] = _i;
                        }
                    }
                }
                _snowcone[_x][_z] = this.getBlockIdAt(blockPositionX - _brushSize + _x, blockPositionY - _yoffset[_x][_z], blockPositionZ - _brushSize + _z);
                _snowconedata[_x][_z] = this.clampY(blockPositionX - _brushSize + _x, blockPositionY - _yoffset[_x][_z], blockPositionZ - _brushSize + _z).getData();
            }
        }

        // figure out new snowheights
        for (int _x = 0; _x <= _twoBrushSize; _x++)
        {
            final double _xPow = Math.pow(_x - _brushSize, 2);

            for (int _z = 0; _z <= 2 * _brushSize; _z++)
            {
                final double _zPow = Math.pow(_z - _brushSize, 2);
                final double _dist = Math.pow(_xPow + _zPow, .5); // distance from center of array
                final int _snowData = _brushSize - (int) Math.ceil(_dist);

                if (_snowData >= 0)
                { // no funny business
                    switch (_snowData)
                    {
                        case 0:
                            if (_snowcone[_x][_z] == Material.AIR.getId())
                            {
                                _snowcone[_x][_z] = Material.SNOW.getId();
                                _snowconedata[_x][_z] = 0;
                            }
                            break;
                        case 7: // Turn largest snowtile into snowblock
                            if (_snowcone[_x][_z] == Material.SNOW.getId())
                            {
                                _snowcone[_x][_z] = Material.SNOW_BLOCK.getId();
                                _snowconedata[_x][_z] = 0;
                            }
                            break;
                        default: // Increase snowtile size, if smaller than target

                            if (_snowData > _snowconedata[_x][_z])
                            {
                                switch (_snowcone[_x][_z])
                                {
                                    case 0:
                                        _snowconedata[_x][_z] = _snowData;
                                        _snowcone[_x][_z] = Material.SNOW.getId();
                                    case 78:
                                        _snowconedata[_x][_z] = _snowData;
                                        break;
                                    default:
                                        break;

                                }
                            }
                            else if (_yoffset[_x][_z] > 0 && _snowcone[_x][_z] == Material.SNOW.getId())
                            {
                                _snowconedata[_x][_z]++;
                                if (_snowconedata[_x][_z] == 7)
                                {
                                    _snowconedata[_x][_z] = 0;
                                    _snowcone[_x][_z] = Material.SNOW_BLOCK.getId();
                                }
                            }
                            break;
                    }
                }
            }
        }

        final Undo _undo = new Undo(targetBlock.getWorld().getName());

        for (int _x = 0; _x <= _twoBrushSize; _x++)
        {
            for (int _z = 0; _z <= _twoBrushSize; _z++)
            {

                if (this.getBlockIdAt(blockPositionX - _brushSize + _x, blockPositionY - _yoffset[_x][_z], blockPositionZ - _brushSize + _z) != _snowcone[_x][_z] || this.clampY(blockPositionX - _brushSize + _x, blockPositionY - _yoffset[_x][_z], blockPositionZ - _brushSize + _z).getData() != _snowconedata[_x][_z])
                {
                    _undo.put(this.clampY(blockPositionX - _brushSize + _x, blockPositionY - _yoffset[_x][_z], blockPositionZ - _brushSize + _z));
                }
                this.setBlockIdAt(blockPositionZ - _brushSize + _z, blockPositionX - _brushSize + _x, blockPositionY - _yoffset[_x][_z], _snowcone[_x][_z]);
                this.clampY(blockPositionX - _brushSize + _x, blockPositionY - _yoffset[_x][_z], blockPositionZ - _brushSize + _z).setData((byte) _snowconedata[_x][_z]);

            }
        }
        v.storeUndo(_undo);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        switch (getTargetBlock().getType())
        {
            case SNOW:
                this.addSnow(v, this.getTargetBlock());
                break;
            default:
                Block blockAbove = getTargetBlock().getRelative(BlockFace.UP);
                if (blockAbove != null && blockAbove.getType() == Material.AIR)
                {
                    addSnow(v, blockAbove);
                }
                else
                {
                    v.owner().getPlayer().sendMessage(ChatColor.RED + "Error: Center block neither snow nor air.");
                }
                break;
        }
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName("Snow Cone");
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        if (par[1].equalsIgnoreCase("info"))
        {
            v.sendMessage(ChatColor.GOLD + "Snow Cone Parameters:");
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return SnowConeBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        SnowConeBrush.timesUsed = tUsed;
    }
}
