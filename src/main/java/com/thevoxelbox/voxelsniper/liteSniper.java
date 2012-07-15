/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
        this.myBrushes = liteBrushes.getSniperBrushes();
        this.brushAlt = liteBrushes.getBrushAlternates();
        this.vm = new vMessage(this.data);
        this.data.vm = this.vm;
        // defaults
        final int[] _currentP = new int[8];
        _currentP[0] = 0;
        _currentP[1] = 0;
        _currentP[2] = 0;
        _currentP[3] = 3;
        _currentP[4] = 1;
        _currentP[5] = 0;
        this.brushPresetsParamsS.put("current@", _currentP);
        this.brushPresetsParamsS.put("previous@", _currentP);
        this.brushPresetsParamsS.put("twoBack@", _currentP);
        this.brushPresetsS.put("current@", this.myBrushes.get("s"));
        this.brushPresetsS.put("previous@", this.myBrushes.get("s"));
        this.brushPresetsS.put("twoBack@", this.myBrushes.get("s"));
    }

    @Override
    public final void setBrushSize(final int size) {
        if (size <= VoxelSniperListener.LITE_MAX_BRUSH && size >= 0) {
            super.setBrushSize(size);
        } else {
            this.p.sendMessage(ChatColor.RED + "You cant use this size of brush!");
        }
    }

    @Override
    public final void setHeigth(final int heigth) {
        if (heigth <= (VoxelSniperListener.LITE_MAX_BRUSH * 2 + 1) && heigth >= 0) {
            super.setHeigth(heigth);
        } else {
            this.p.sendMessage(ChatColor.RED + "You cant use this size of heigth!");
        }
    }

    @Override
    public final void setRange(final double rng) {
        if (rng > -1) {
            if (rng <= 40) {
                this.range = rng;
                this.distRestrict = true;
                this.vm.toggleRange();
            } else {
                this.p.sendMessage(ChatColor.GREEN + "liteSnipers are not allowed to use ranges higher than 40.");
            }
        } else {
            this.distRestrict = !this.distRestrict;
            this.vm.toggleRange();
        }
    }

    @Override
    public final void setReplace(final int replace) {
        if (!VoxelSniperListener.liteRestricted.contains(replace)) {
            super.setReplace(replace);
        } else {
            this.p.sendMessage(ChatColor.RED + "You are not allowed to use this block!");
        }
    }

    @Override
    public final void setVoxel(final int voxel) {
        if (!VoxelSniperListener.liteRestricted.contains(voxel)) {
            super.setVoxel(voxel);
        } else {
            this.p.sendMessage(ChatColor.RED + "You are not allowed to use this block!");
        }
    }

    @Override
    public final void toggleLightning() {
        this.p.sendMessage(ChatColor.GREEN + "liteSnipers are not allowed to use this.");
    }
}
