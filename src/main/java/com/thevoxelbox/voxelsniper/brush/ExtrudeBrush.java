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
    private double trueCircle;

    /**
     *
     */
    public ExtrudeBrush()
    {
        this.setName("Extrude");
    }

    private void extrudeUpOrDown(final SnipeData v, boolean isUp)
    {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        Undo undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = -brushSize; _x <= brushSize; _x++)
        {
            final double xSquared = Math.pow(_x, 2);
            for (int _z = -brushSize; _z <= brushSize; _z++)
            {
                if ((xSquared + Math.pow(_z, 2)) <= brushSizeSquared)
                {
                    final int direction = (isUp ? 1 : -1);
                    for (int _y = 0; _y < Math.abs(v.getVoxelHeight()); _y++)
                    {
                        final int tempY = _y * direction;
                        undo = this.perform(
                                this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + tempY, this.getBlockPositionZ() + _z),
                                this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + tempY + direction, this.getBlockPositionZ() + _z),
                                v, undo);
                    }
                }
            }
        }

        v.storeUndo(undo);
    }

    private void extrudeNorthOrSouth(final SnipeData v, boolean isSouth)
    {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _x = -brushSize; _x <= brushSize; _x++)
        {
            final double xSquared = Math.pow(_x, 2);
            for (int _y = -brushSize; _y <= brushSize; _y++)
            {
                if ((xSquared + Math.pow(_y, 2)) <= brushSizeSquared)
                {
                    final int direction = (isSouth) ? 1 : -1;
                    for (int _z = 0; _z < Math.abs(v.getVoxelHeight()); _z++)
                    {
                        final int tempZ = _z * direction;
                        _undo = this.perform(
                                this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + tempZ),
                                this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + tempZ + direction),
                                v, _undo);
                    }

                }
            }
        }

        v.storeUndo(_undo);
    }

    private void extrudeEastOrWest(final SnipeData v, boolean isEast)
    {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _y = -brushSize; _y <= brushSize; _y++)
        {
            final double ySquared = Math.pow(_y, 2);
            for (int _z = -brushSize; _z <= brushSize; _z++)
            {
                if ((ySquared + Math.pow(_z, 2)) <= brushSizeSquared)
                {
                    final int direction = (isEast) ? 1 : -1;
                    for (int _x = 0; _x < Math.abs(v.getVoxelHeight()); _x++)
                    {
                        final int tempX = _x * direction;
                        _undo = this.perform(
                                this.clampY(this.getBlockPositionX() + tempX, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z),
                                this.clampY(this.getBlockPositionX() + tempX + direction, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z),
                                v, _undo);
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

    private void selectExtrudeMethod(final SnipeData v, final BlockFace blockFace, final boolean towardsUser)
    {
        if (blockFace == null || v.getVoxelHeight() == 0)
        {
            return;
        }
        boolean tempDirection = towardsUser;
        switch (blockFace)
        {
            case DOWN:
                tempDirection = !towardsUser;
            case UP:
                extrudeUpOrDown(v, tempDirection);
                break;
            case NORTH:
                tempDirection = !towardsUser;
            case SOUTH: 
                extrudeNorthOrSouth(v, tempDirection);
                break;
            case WEST:
                tempDirection = !towardsUser;
            case EAST:
                extrudeEastOrWest(v, tempDirection);
                break;
            default:
                break;
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.selectExtrudeMethod(v, this.getTargetBlock().getFace(this.getLastBlock()), false);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.selectExtrudeMethod(v, this.getTargetBlock().getFace(this.getLastBlock()), true);
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
                } else if (_param.startsWith("true"))
                {
                    this.trueCircle = 0.5;
                    v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                    continue;
                } else if (_param.startsWith("false"))
                {
                    this.trueCircle = 0;
                    v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                    continue;
                } else
                {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                    return;
                }
            } catch (final Exception _e)
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
