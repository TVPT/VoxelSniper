package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Drain_Brush
 *
 * @author Gavjenks
 * @author psanker
 */
public class DrainBrush extends Brush
{
    private double trueCircle = 0;
    private boolean disc = false;

    /**
     *
     */
    public DrainBrush()
    {
        this.setName("Drain");
    }

	private void drain(final SnipeData v)
    {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        final Undo undo = new Undo();

		int tx = this.getTargetBlock().getX();
		int ty = this.getTargetBlock().getY();
		int tz = this.getTargetBlock().getZ();
		if (this.disc)
        {
            for (int x = brushSize; x >= 0; x--)
            {
                final double xSquared = Math.pow(x, 2);

                for (int y = brushSize; y >= 0; y--)
                {
                    if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared)
                    {
						if (this.getBlockTypeAt(tx + x, ty, tz + y) == Material.WATER || this.getBlockTypeAt(tx + x, ty, tz + y) == Material.LAVA)
                        {
                            undo.put(this.clampY(tx + x, ty, tz + y));
                            this.setBlockTypeAt(tz + y, tx + x, ty, Material.AIR);
                        }

                        if (this.getBlockTypeAt(tx + x, ty, tz - y) == Material.WATER|| this.getBlockTypeAt(tx + x, ty, tz - y) == Material.LAVA)
                        {
                            undo.put(this.clampY(tx + x, ty, tz - y));
                            this.setBlockTypeAt(tz - y, tx + x, ty, Material.AIR);
                        }

                        if (this.getBlockTypeAt(tx - x, ty, tz + y) == Material.WATER || this.getBlockTypeAt(tx - x, ty, tz + y) == Material.LAVA)
                        {
                            undo.put(this.clampY(tx - x, ty, tz + y));
                            this.setBlockTypeAt(tz + y, tx - x, ty, Material.AIR);
                        }

                        if (this.getBlockTypeAt(tx - x, ty, tz - y) == Material.WATER ||  this.getBlockTypeAt(tx - x, ty, tz - y) == Material.LAVA)
                        {
                            undo.put(this.clampY(tx - x, ty, tz - y));
                            this.setBlockTypeAt(tz - y, tx - x, ty, Material.AIR);
                        }
                    }
                }
            }
        }
        else
        {
            for (int y = (brushSize + 1) * 2; y >= 0; y--)
            {
                final double ySquared = Math.pow(y - brushSize, 2);

                for (int x = (brushSize + 1) * 2; x >= 0; x--)
                {
                    final double xSquared = Math.pow(x - brushSize, 2);

                    for (int z = (brushSize + 1) * 2; z >= 0; z--)
                    {
                        if ((xSquared + Math.pow(z - brushSize, 2) + ySquared) <= brushSizeSquared)
                        {
							int bsx = tx + x - brushSize;
							int bsy = ty + z - brushSize;
							int bsz = tz + y - brushSize;
							if (this.getBlockTypeAt(bsx, bsy, bsz) == Material.WATER || this.getBlockTypeAt(bsx, bsy, bsz) == Material.LAVA)
                            {
                                undo.put(this.clampY(
										tx + x, ty + z, tz + y));
                                this.setBlockTypeAt(bsz, bsx, bsy, Material.AIR);
                            }
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(undo);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.drain(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.drain(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();

        vm.custom(ChatColor.AQUA + ((this.trueCircle == 0.5) ? "True circle mode ON" : "True circle mode OFF"));
        vm.custom(ChatColor.AQUA + ((this.disc) ? "Disc drain mode ON" : "Disc drain mode OFF"));
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int i = 1; i < par.length; i++)
        {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info"))
            {
                v.sendMessage(ChatColor.GOLD + "Drain Brush Parameters:");
                v.sendMessage(ChatColor.AQUA + "/b drain true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b drain false will switch back. (false is default)");
                v.sendMessage(ChatColor.AQUA + "/b drain d -- toggles disc drain mode, as opposed to a ball drain mode");
                return;
            }
            else if (parameter.startsWith("true"))
            {
                this.trueCircle = 0.5;
                v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
            }
            else if (parameter.startsWith("false"))
            {
                this.trueCircle = 0;
                v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
            }
            else if (parameter.equalsIgnoreCase("d"))
            {
                if (this.disc)
                {
                    this.disc = false;
                    v.sendMessage(ChatColor.AQUA + "Disc drain mode OFF");
                }
                else
                {
                    this.disc = true;
                    v.sendMessage(ChatColor.AQUA + "Disc drain mode ON");
                }
            }
            else
            {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.drain";
    }
}
