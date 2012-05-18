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

    public liteSniper() {
        myBrushes = liteBrushes.getSniperBrushes();
        brushAlt = liteBrushes.getBrushAlternates();
        vm = new vMessage(data);
        data.vm = vm;
        //defaults
        int[] currentP = new int[8];
        currentP[0] = 0;
        currentP[1] = 0;
        currentP[2] = 0;
        currentP[3] = 3;
        currentP[4] = 1;
        currentP[5] = 0;
        brushPresetsParamsS.put("current@", currentP);
        brushPresetsParamsS.put("previous@", currentP);
        brushPresetsParamsS.put("twoBack@", currentP);
        brushPresetsS.put("current@", myBrushes.get("s"));
        brushPresetsS.put("previous@", myBrushes.get("s"));
        brushPresetsS.put("twoBack@", myBrushes.get("s"));
    }

    @Override
    public void setBrushSize(int size) {
        if (size <= VoxelSniperListener.LITE_MAX_BRUSH && size >= 0) {
            super.setBrushSize(size);
//            data.brushSize = size;
//            vm.size();
        } else {
            p.sendMessage(ChatColor.RED + "You cant use this size of brush!");
        }
    }

    @Override
    public void setVoxel(int voxel) {
        if (!VoxelSniperListener.liteRestricted.contains(voxel)) {
            super.setVoxel(voxel);
//            data.voxelId = voxel;
//            vm.voxel();
        } else {
            p.sendMessage(ChatColor.RED + "You are not allowed to use this block!");
        }
    }

    @Override
    public void setReplace(int replace) {
        if (!VoxelSniperListener.liteRestricted.contains(replace)) {
            super.setReplace(replace);
//            data.replaceId = replace;
//            vm.replace();
        } else {
            p.sendMessage(ChatColor.RED + "You are not allowed to use this block!");
        }
    }

    @Override
    public void setHeigth(int heigth) {
        if (heigth <= (VoxelSniperListener.LITE_MAX_BRUSH * 2 + 1) && heigth >= 0) {
            super.setHeigth(heigth);
//            data.voxelHeight = heigth;
//            vm.height();
        } else {
            p.sendMessage(ChatColor.RED + "You cant use this size of heigth!");
        }
    }

    @Override
    public void setRange(double rng) {
        if (rng > -1) {
            if (rng <= 40) {
                range = rng;
                distRestrict = true;
                vm.toggleRange();
            } else {
                p.sendMessage(ChatColor.GREEN + "liteSnipers are not allowed to use ranges higher than 40.");
            }
        } else {
            distRestrict = !distRestrict;
            vm.toggleRange();
        }
    }

    @Override
    public void toggleLightning() {
        p.sendMessage(ChatColor.GREEN + "liteSnipers are not allowed to use this.");
    }
}
