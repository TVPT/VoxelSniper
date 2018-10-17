package com.thevoxelbox.voxelsniper;

import org.bukkit.ChatColor;
import org.bukkit.block.data.BlockData;

/**
 *
 */
public class Message
{
    private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;
    private final SnipeData snipeData;

    /**
     * @param snipeData
     */
    public Message(SnipeData snipeData)
    {
        this.snipeData = snipeData;
    }

    /**
     * Send a brush message styled message to the player.
     *
     * @param brushMessage
     */
    public void brushMessage(String brushMessage)
    {
        snipeData.sendMessage(ChatColor.LIGHT_PURPLE + brushMessage);
    }

    /**
     * Display Brush Name.
     *
     * @param brushName
     */
    public void brushName(String brushName)
    {
        snipeData.sendMessage(ChatColor.AQUA + "Brush Type: " + ChatColor.LIGHT_PURPLE + brushName);
    }

    /**
     * Display Center Parameter.
     */
    public void center()
    {
        snipeData.sendMessage(ChatColor.DARK_BLUE + "Brush Center: " + ChatColor.DARK_RED + snipeData.getcCen());
    }

    /**
     * Display custom message.
     *
     * @param message
     */
    public void custom(String message)
    {
        snipeData.sendMessage(message);
    }

    /**
     * Display voxel height.
     */
    public void height()
    {
        snipeData.sendMessage(ChatColor.DARK_AQUA + "Brush Height: " + ChatColor.DARK_RED + snipeData.getVoxelHeight());
    }

    /**
     * Display performer.
     *
     * @param performerName
     */
    public void performerName(String performerName)
    {
        this.snipeData.sendMessage(ChatColor.DARK_PURPLE + "Performer: " + ChatColor.DARK_GREEN + performerName);
    }

    /**
     * Displaye replace material.
     */
    public void replace()
    {
        snipeData.sendMessage(ChatColor.AQUA + "Replace Material: " + ChatColor.RED + snipeData.getReplaceData().getAsString());
    }

    /**
     * Display brush size.
     */
    public void size()
    {
        snipeData.sendMessage(ChatColor.GREEN + "Brush Size: " + ChatColor.DARK_RED + snipeData.getBrushSize());
        if (snipeData.getBrushSize() >= BRUSH_SIZE_WARNING_THRESHOLD)
        {
            snipeData.sendMessage(ChatColor.RED + "WARNING: Large brush size selected!");
        }
    }

    /**
     * Display toggle lightning message.
     */
    public void toggleLightning()
    {
        snipeData.sendMessage(ChatColor.GOLD + "Lightning mode has been toggled " + ChatColor.DARK_RED + ((snipeData.owner().getSnipeData(snipeData.owner().getCurrentToolId()).isLightningEnabled()) ? "on" : "off"));
    }

    /**
     * Display toggle printout message.
     */
    public final void togglePrintout()
    {
        snipeData.sendMessage(ChatColor.GOLD + "Brush info printout mode has been toggled " + ChatColor.DARK_RED + ((snipeData.owner().getSnipeData(snipeData.owner().getCurrentToolId()).isLightningEnabled()) ? "on" : "off"));
    }

    /**
     * Display toggle range message.
     */
    public void toggleRange()
    {
        snipeData.sendMessage(ChatColor.GOLD + "Distance Restriction toggled " + ChatColor.DARK_RED + ((snipeData.owner().getSnipeData(snipeData.owner().getCurrentToolId()).isRanged()) ? "on" : "off") + ChatColor.GOLD + ". Range is " + ChatColor.LIGHT_PURPLE + (double) snipeData.owner().getSnipeData(snipeData.owner().getCurrentToolId()).getRange());
    }

    /**
     * Display voxel type.
     */
    public void voxel()
    {
        snipeData.sendMessage(ChatColor.GOLD + "Voxel: " + ChatColor.RED + snipeData.getVoxelData().getAsString());
    }

    /**
     * Display voxel list.
     */
    public void voxelList()
    {
        if (snipeData.getVoxelList().isEmpty())
        {
            snipeData.sendMessage(ChatColor.DARK_GREEN + "No blocks selected!");
        }
        else
        {
            StringBuilder returnValueBuilder = new StringBuilder();
            returnValueBuilder.append(ChatColor.DARK_GREEN);
            returnValueBuilder.append("Block Types Selected: ");
            returnValueBuilder.append(ChatColor.AQUA);

            for (BlockData data : snipeData.getVoxelList().getList())
            {
                returnValueBuilder.append(data.getAsString());
                returnValueBuilder.append(" ");
            }

            snipeData.sendMessage(returnValueBuilder.toString());
        }
    }
}
