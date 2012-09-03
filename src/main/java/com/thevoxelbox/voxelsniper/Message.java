package com.thevoxelbox.voxelsniper;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.util.VoxelList.VoxIterator;

/**
 * 
 * @author Voxel
 */
public class Message {

    private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;
    private final SnipeData v;

    /**
     * @param vs
     */
    public Message(final SnipeData vs) {
        this.v = vs;
    }

    /**
     * Send a brush message styled message to the player.
     * 
     * @param brushMessage
     */
    public final void brushMessage(final String brushMessage) {
        if (this.v.owner().isPrintout()) {
            this.v.sendMessage(ChatColor.LIGHT_PURPLE + brushMessage);
        }
    }

    /**
     * Display Brush Name.
     * 
     * @param brushName
     */
    public final void brushName(final String brushName) {
        this.v.sendMessage(ChatColor.LIGHT_PURPLE + "Brush set to " + brushName);
    }

    /**
     * Display Center Parameter.
     */
    public final void center() {
        if (this.v.owner().isPrintout()) {
            this.v.sendMessage(ChatColor.DARK_BLUE + "Center set to " + ChatColor.DARK_RED + this.v.getcCen());
        }
    }

    /**
     * Display custom message.
     * 
     * @param message
     */
    public final void custom(final String message) {
        this.v.sendMessage(message);
    }

    /**
     * Display data value.
     */
    public final void data() {
        if (this.v.owner().isPrintout()) {
            this.v.sendMessage(ChatColor.BLUE + "Data variable set to " + ChatColor.DARK_RED + this.v.getData());
        }
    }

    /**
     * Display voxel height.
     */
    public final void height() {
        if (this.v.owner().isPrintout()) {
            this.v.sendMessage(ChatColor.DARK_AQUA + "Brush height " + ChatColor.DARK_RED + this.v.getVoxelHeight());
        }
    }

    /**
     * Display performer.
     * 
     * @param performerName
     */
    public final void performerName(final String performerName) {
        this.v.sendMessage(ChatColor.DARK_GREEN + performerName + ChatColor.LIGHT_PURPLE + " Performer selected");
    }

    /**
     * Displaye replace material.
     */
    public final void replace() {
        if (this.v.owner().isPrintout()) {
            this.v.sendMessage(ChatColor.AQUA + "Replace material set to " + ChatColor.DARK_RED + this.v.getReplaceId() + ChatColor.AQUA + " ("
                    + Material.getMaterial(this.v.getReplaceId()).toString() + ")");
        }
    }

    /**
     * Display replace data value.
     */
    public final void replaceData() {
        if (this.v.owner().isPrintout()) {
            this.v.sendMessage(ChatColor.DARK_GRAY + "Replace data variable set to " + ChatColor.DARK_RED + this.v.getReplaceData());
        }
    }

    /**
     * Display brush size.
     */
    public final void size() {
        this.v.sendMessage(ChatColor.GREEN + "Brush size set to " + ChatColor.DARK_RED + this.v.getBrushSize());
        if (this.v.getBrushSize() >= BRUSH_SIZE_WARNING_THRESHOLD) {
            this.v.sendMessage(ChatColor.RED + "WARNING: Large brush size selected!");
        }
    }

    /**
     * Display toggle lightning message.
     */
    public final void toggleLightning() {
        this.v.sendMessage(ChatColor.GOLD + "Lightning mode has been toggled " + ChatColor.DARK_RED + ((this.v.owner().isLightning()) ? "on" : "off"));
    }

    /**
     * Display toggle printout message.
     */
    public final void togglePrintout() {
        this.v.sendMessage(ChatColor.GOLD + "Brush info printout mode has been toggled " + ChatColor.DARK_RED + ((this.v.owner().isLightning()) ? "on" : "off"));
    }

    /**
     * Display toggle range message.
     */
    public final void toggleRange() {
        this.v.sendMessage(ChatColor.GOLD + "Distance Restriction toggled " + ChatColor.DARK_RED + ((this.v.owner().isDistRestrict()) ? "on" : "off")
                + ChatColor.GOLD + ". Range is " + ChatColor.LIGHT_PURPLE + this.v.owner().getRange());
    }

    /**
     * Display voxel type.
     */
    public final void voxel() {
        if (this.v.owner().isPrintout()) {
            this.v.sendMessage(ChatColor.GOLD + "Voxel set to " + ChatColor.DARK_RED + this.v.getVoxelId() + ChatColor.AQUA + " ("
                    + Material.getMaterial(this.v.getVoxelId()).toString() + ")");
        }
    }

    /**
     * Display voxel list.
     */
    public final void voxelList() {
        if (this.v.owner().isPrintout()) {
            if (this.v.getVoxelList().isEmpty()) {
                this.v.sendMessage(ChatColor.DARK_GREEN + "No selected blocks!");
            } else {
                final VoxIterator _it = this.v.getVoxelList().getIterator();
                String _pre = ChatColor.DARK_GREEN + "Block types selected: " + ChatColor.AQUA;
                while (_it.hasNext()) {
                    _pre = _pre + _it.next() + " ";
                }

                this.v.sendMessage(_pre);
            }
        }
    }
}
