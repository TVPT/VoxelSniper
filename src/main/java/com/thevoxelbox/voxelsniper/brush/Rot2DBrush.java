package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Piotr
 */
public class Rot2DBrush extends Brush
{
    private int mode = 0;
    private int bSize;
    private int brushSize;
    private BlockWrapper[][][] snap;
    private double se;

    /**
     *
     */
    public Rot2DBrush()
    {
        this.setName("2D Rotation");
    }

    private void getMatrix()
    {
        this.brushSize = (this.bSize * 2) + 1;

        this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];

        final double brushSizeSquared = Math.pow(this.bSize + 0.5, 2);
        int sx = this.getTargetBlock().getX() - this.bSize;
        int sy = this.getTargetBlock().getY() - this.bSize;
        int sz = this.getTargetBlock().getZ() - this.bSize;

        for (int x = 0; x < this.snap.length; x++)
        {
            sz = this.getTargetBlock().getZ() - this.bSize;
            final double xSquared = Math.pow(x - this.bSize, 2);
            for (int y = 0; y < this.snap.length; y++)
            {
                sy = this.getTargetBlock().getY() - this.bSize;
                if (xSquared + Math.pow(y - this.bSize, 2) <= brushSizeSquared)
                {
                    for (int z = 0; z < this.snap.length; z++)
                    {
                        final Block block = this.clampY(sx, sy, sz); // why is this not sx + x, sy + y sz + z?
                        this.snap[x][z][y] = new BlockWrapper(block);
                        block.setType(Material.AIR);
                        sy++;
                    }
                }
                sz++;
            }
            sx++;
        }
    }

    private void rotate(final SnipeData v)
    {
        final double brushSiyeSquared = Math.pow(this.bSize + 0.5, 2);
        final double cos = Math.cos(this.se);
        final double sin = Math.sin(this.se);
        final boolean[][] doNotFill = new boolean[this.snap.length][this.snap.length];
        // I put y in the inside loop, since it doesn't have any power functions, should be much faster.
        // Also, new array keeps track of which x and z coords are being assigned in the rotated space so that we can
        // do a targeted filling of only those columns later that were left out.

        for (int x = 0; x < this.snap.length; x++)
        {
            final int xx = x - this.bSize;
            final double xSquared = Math.pow(xx, 2);

            for (int y = 0; y < this.snap.length; y++)
            {
                final int zz = y - this.bSize;

                if (xSquared + Math.pow(zz, 2) <= brushSiyeSquared)
                {
                    final double newX = (xx * cos) - (zz * sin);
                    final double newZ = (xx * sin) + (zz * cos);

                    doNotFill[(int) newX + this.bSize][(int) newZ + this.bSize] = true;

                    for (int currentY = 0; currentY < this.snap.length; currentY++)
                    {
                        final int yy = currentY - this.bSize;
                        final BlockWrapper block = this.snap[x][currentY][y];

                        if (block.getBlockData().getMaterial() == Material.AIR)
                        {
                            continue;
                        }
                        this.setBlockDataAt(this.getTargetBlock().getX() + (int) newX, this.getTargetBlock().getY() + yy, this.getTargetBlock().getZ() + (int) newZ, block.getBlockData());
                    }
                }
            }
        }
        for (int x = 0; x < this.snap.length; x++)
        {
            final double xSquared = Math.pow(x - this.bSize, 2);
            final int fx = x + this.getTargetBlock().getX() - this.bSize;

            for (int z = 0; z < this.snap.length; z++)
            {
                if (xSquared + Math.pow(z - this.bSize, 2) <= brushSiyeSquared)
                {
                    final int fz = z + this.getTargetBlock().getZ() - this.bSize;

                    if (!doNotFill[x][z])
                    {
                        // smart fill stuff

                        for (int y = 0; y < this.snap.length; y++)
                        {
                            final int fy = y + this.getTargetBlock().getY() - this.bSize;

                            final BlockData a = this.getBlockDataAt(fx + 1, fy, fz);
                            final BlockData d = this.getBlockDataAt(fx - 1, fy, fz);
                            final BlockData c = this.getBlockDataAt(fx, fy, fz + 1);
                            final BlockData b = this.getBlockDataAt(fx, fy, fz - 1);

                            BlockData winner;

                            if (a == b || a == c || a == d)
                            { // I figure that since we are already narrowing it down to ONLY the holes left behind, it
                                // should
                                // be fine to do all 5 checks needed to be legit about it.
                                winner = a;
                            }
                            else if (b == d || c == d)
                            {
                                winner = d;
                            }
                            else
                            {
                                winner = b; // blockPositionY making this default, it will also automatically cover situations where B = C;
                            }

                            this.setBlockDataAt(fx, fy, fz, winner);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.bSize = v.getBrushSize();

        switch (this.mode)
        {
            case 0:
                this.getMatrix();
                this.rotate(v);
                break;

            default:
                v.sendMessage(ChatColor.RED + "Something went wrong.");
                break;
        }
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.bSize = v.getBrushSize();

        switch (this.mode)
        {
            case 0:
                this.getMatrix();
                this.rotate(v);
                break;

            default:
                v.sendMessage(ChatColor.RED + "Something went wrong.");
                break;
        }
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        this.se = Math.toRadians(Double.parseDouble(par[1]));
        v.sendMessage(ChatColor.GREEN + "Angle set to " + this.se);
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.rot2d";
    }
}
