package com.thevoxelbox.voxelsniper;

import org.bukkit.ChatColor;

/**
 * @author Voxel
 */
public class LiteSniper extends Sniper
{

    /**
     * Default Constructor.
     */
    public LiteSniper()
    {
        this.setMyBrushes(Brushes.getNewLiteSniperBrushInstances());
        this.setVoxelMessage(new Message(this.getData()));
        this.getData().setVoxelMessage(this.getVoxelMessage());
        // defaults
        final int[] currentP = new int[8];
        currentP[0] = 0;
        currentP[1] = 0;
        currentP[2] = 0;
        currentP[3] = 3;
        currentP[4] = 1;
        currentP[5] = 0;
        this.getBrushPresetsParamsS().put("current@", currentP);
        this.getBrushPresetsParamsS().put("previous@", currentP);
        this.getBrushPresetsParamsS().put("twoBack@", currentP);
        this.getBrushPresetsS().put("current@", this.getMyBrushes().get("s"));
        this.getBrushPresetsS().put("previous@", this.getMyBrushes().get("s"));
        this.getBrushPresetsS().put("twoBack@", this.getMyBrushes().get("s"));
    }

    @Override
    public final void setBrushSize(final int size)
    {
        if (size <= VoxelSniper.getInstance().getLiteMaxBrush() && size >= 0)
        {
            super.setBrushSize(size);
        }
        else
        {
            this.getPlayer().sendMessage(ChatColor.RED + "You cannot use this brush size.");
        }
    }

    @Override
    public final void setHeigth(final int heigth)
    {
        if (heigth <= (VoxelSniper.getInstance().getLiteMaxBrush() * 2 + 1) && heigth >= 0)
        {
            super.setHeigth(heigth);
        }
        else
        {
            this.getPlayer().sendMessage(ChatColor.RED + "You cannot use this brush height.");
        }
    }

    @Override
    public final void setRange(final double rng)
    {
        if (rng > -1)
        {
            if (rng <= 40)
            {
                super.setRange(rng);
                this.setDistRestrict(true);
                this.getVoxelMessage().toggleRange();
            }
            else
            {
                this.getPlayer().sendMessage(ChatColor.GREEN + "LiteSnipers are not allowed to use ranges higher than 40.");
            }
        }
        else
        {
            this.setDistRestrict(!this.isDistRestrict());
            this.getVoxelMessage().toggleRange();
        }
    }

    @Override
    public final void setReplace(final int replace)
    {
        if (!VoxelSniper.getInstance().getLiteRestricted().contains(replace))
        {
            super.setReplace(replace);
        }
        else
        {
            this.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to use this block.");
        }
    }

    @Override
    public final void setVoxel(final int voxel)
    {
        if (!VoxelSniper.getInstance().getLiteRestricted().contains(voxel))
        {
            super.setVoxel(voxel);
        }
        else
        {
            this.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to use this block.");
        }
    }

    @Override
    public final void toggleLightning()
    {
        this.getPlayer().sendMessage(ChatColor.GREEN + "LiteSnipers are not allowed to use this.");
    }
}
