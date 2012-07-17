package com.thevoxelbox.voxelsniper;

import org.bukkit.ChatColor;

/**
 * 
 * @author Voxel
 */
public class liteSniper extends vSniper {

    /**
     * Default Constructor.
     */
    public liteSniper() {
        this.setMyBrushes(liteBrushes.getSniperBrushes());
        this.setBrushAlt(liteBrushes.getBrushAlternates());
        this.setVoxelMessage(new vMessage(this.getData()));
        this.getData().vm = this.getVoxelMessage();
        // defaults
        final int[] _currentP = new int[8];
        _currentP[0] = 0;
        _currentP[1] = 0;
        _currentP[2] = 0;
        _currentP[3] = 3;
        _currentP[4] = 1;
        _currentP[5] = 0;
        this.getBrushPresetsParamsS().put("current@", _currentP);
        this.getBrushPresetsParamsS().put("previous@", _currentP);
        this.getBrushPresetsParamsS().put("twoBack@", _currentP);
        this.getBrushPresetsS().put("current@", this.getMyBrushes().get("s"));
        this.getBrushPresetsS().put("previous@", this.getMyBrushes().get("s"));
        this.getBrushPresetsS().put("twoBack@", this.getMyBrushes().get("s"));
    }

    @Override
    public final void setBrushSize(final int size) {
        if (size <= VoxelSniperListener.getLiteMaxBrush() && size >= 0) {
            super.setBrushSize(size);
        } else {
            this.getPlayer().sendMessage(ChatColor.RED + "You cant use this size of brush!");
        }
    }

    @Override
    public final void setHeigth(final int heigth) {
        if (heigth <= (VoxelSniperListener.getLiteMaxBrush() * 2 + 1) && heigth >= 0) {
            super.setHeigth(heigth);
        } else {
            this.getPlayer().sendMessage(ChatColor.RED + "You cant use this size of heigth!");
        }
    }

    @Override
    public final void setRange(final double rng) {
        if (rng > -1) {
            if (rng <= 40) {
                super.setRange(rng);
                this.setDistRestrict(true);
                this.getVoxelMessage().toggleRange();
            } else {
                this.getPlayer().sendMessage(ChatColor.GREEN + "liteSnipers are not allowed to use ranges higher than 40.");
            }
        } else {
            this.setDistRestrict(!this.isDistRestrict());
            this.getVoxelMessage().toggleRange();
        }
    }

    @Override
    public final void setReplace(final int replace) {
        if (!VoxelSniperListener.getLiteRestricted().contains(replace)) {
            super.setReplace(replace);
        } else {
            this.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to use this block!");
        }
    }

    @Override
    public final void setVoxel(final int voxel) {
        if (!VoxelSniperListener.getLiteRestricted().contains(voxel)) {
            super.setVoxel(voxel);
        } else {
            this.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to use this block!");
        }
    }

    @Override
    public final void toggleLightning() {
        this.getPlayer().sendMessage(ChatColor.GREEN + "liteSnipers are not allowed to use this.");
    }
}
