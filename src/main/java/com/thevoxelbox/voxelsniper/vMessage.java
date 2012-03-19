/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.util.VoxelList.VoxIterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 *
 * @author Voxel
 */
public class vMessage {

    private vSniper v;

    public vMessage(vSniper vs) {
        v = vs;
    }

    public void size() {
        v.p.sendMessage(ChatColor.GREEN + "Brush size set to " + ChatColor.DARK_RED + v.brushSize);
    }

    public void voxel() {
        if (v.printout) {
            v.p.sendMessage(ChatColor.GOLD + "Voxel set to " + ChatColor.DARK_RED + v.voxelId + ChatColor.AQUA + " (" + Material.getMaterial(v.voxelId).toString() + ")");
        }
    }

    public void replace() {
        if (v.printout) {
            v.p.sendMessage(ChatColor.AQUA + "Replace material set to " + ChatColor.DARK_RED + v.replaceId + ChatColor.AQUA + " (" + Material.getMaterial(v.replaceId).toString() + ")");
        }
    }

    public void data() {
        if (v.printout) {
            v.p.sendMessage(ChatColor.BLUE + "Data variable set to " + ChatColor.DARK_RED + v.data);
        }
    }

    public void replaceData() {
        if (v.printout) {
            v.p.sendMessage(ChatColor.DARK_GRAY + "Replace data variable set to " + ChatColor.DARK_RED + v.replaceData);
        }
    }

    public void voxelList() {
        if (v.printout) {
            if (v.voxelList.isEmpty()) {
                v.p.sendMessage(ChatColor.DARK_GREEN + "No selected blocks!");
            } else {
                VoxIterator it = v.voxelList.getIterator();
                String pre = ChatColor.DARK_GREEN + "Block types selected: " + ChatColor.AQUA;
                while (it.hasNext()) {
                    pre = pre + it.next() + " ";
                }

                v.p.sendMessage(pre);
            }
        }
    }

    public void height() {
        if (v.printout) {
            v.p.sendMessage(ChatColor.DARK_AQUA + "Brush height " + ChatColor.DARK_RED + v.voxelHeight);
        }
    }

    public void center() {
        if (v.printout) {
            v.p.sendMessage(ChatColor.DARK_BLUE + "Center set to " + ChatColor.DARK_RED + v.cCen);
        }
    }

    public void brushMessage(String brushMessage) {
        if (v.printout) {
            v.p.sendMessage(ChatColor.LIGHT_PURPLE + brushMessage);
        }
    }

    public void brushName(String brushName) {
        v.p.sendMessage(ChatColor.LIGHT_PURPLE + "Brush set to " + brushName);
    }

    public void performerName(String performerName) {
        v.p.sendMessage(ChatColor.DARK_GREEN + performerName + ChatColor.LIGHT_PURPLE + " Performer selected");
    }

    public void toggleRange() {
        v.p.sendMessage(ChatColor.GOLD + "Distance Restriction toggled " + ChatColor.DARK_RED + ((v.distRestrict) ? "on" : "off") + ChatColor.GOLD + ". Range is " + ChatColor.LIGHT_PURPLE + v.range);
    }

    public void toggleLightning() {
        v.p.sendMessage(ChatColor.GOLD + "Lightning mode has been toggled " + ChatColor.DARK_RED + ((v.lightning) ? "on" : "off"));
    }

    public void togglePrintout() {
        v.p.sendMessage(ChatColor.GOLD + "Brush info printout mode has been toggled " + ChatColor.DARK_RED + ((v.printout) ? "on" : "off"));
    }

    public void custom(String message) {
        v.p.sendMessage(message);
    }
}
