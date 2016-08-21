package com.thevoxelbox.voxelsniper;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Message {

    private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;
    private final SnipeData snipeData;

    /**
     * @param snipeData
     */
    public Message(SnipeData snipeData) {
        this.snipeData = snipeData;
    }

    /**
     * Send a brush message styled message to the player.
     *
     * @param brushMessage
     */
    public void brushMessage(String brushMessage) {
        this.snipeData.sendMessage(TextColors.LIGHT_PURPLE, brushMessage);
    }

    /**
     * Display Brush Name.
     *
     * @param brushName
     */
    public void brushName(String brushName) {
        this.snipeData.sendMessage(TextColors.AQUA, "Brush Type: ", TextColors.LIGHT_PURPLE, brushName);
    }

    /**
     * Display Center Parameter.
     */
    public void center() {
        this.snipeData.sendMessage(TextColors.DARK_BLUE, "Brush Center: ", TextColors.DARK_RED, this.snipeData.getcCen());
    }

    /**
     * Display custom message.
     *
     * @param message
     */
    public void custom(Text message) {
        this.snipeData.sendMessage(message);
    }
    
    public void custom(Object... args) {
        this.snipeData.sendMessage(args);
    }

    /**
     * Display voxel height.
     */
    public void height() {
        this.snipeData.sendMessage(TextColors.DARK_AQUA, "Brush Height: ", TextColors.DARK_RED, this.snipeData.getVoxelHeight());
    }

    /**
     * Display performer.
     *
     * @param performerName
     */
    public void performerName(String performerName) {
        this.snipeData.sendMessage(TextColors.DARK_PURPLE, "Performer: ", TextColors.DARK_GREEN, performerName);
    }

    /**
     * Display replace material.
     */
    public void replace() {
        this.snipeData.sendMessage(TextColors.AQUA, "Replace Material: ", TextColors.RED, this.snipeData.getReplaceId(), TextColors.GRAY, " (",
                this.snipeData.getReplaceId(), ")");
    }

    /**
     * Display brush size.
     */
    public void size() {
        this.snipeData.sendMessage(TextColors.GREEN, "Brush Size: ", TextColors.DARK_RED, this.snipeData.getBrushSize());
        if (this.snipeData.getBrushSize() >= BRUSH_SIZE_WARNING_THRESHOLD) {
            this.snipeData.sendMessage(TextColors.RED, "WARNING: Large brush size selected!");
        }
    }

    /**
     * Display toggle lightning message.
     */
    public void toggleLightning() {
        this.snipeData.sendMessage(TextColors.GOLD, "Lightning mode has been toggled ", TextColors.DARK_RED,
                ((this.snipeData.owner().getSnipeData(this.snipeData.owner().getCurrentToolId()).isLightningEnabled()) ? "on" : "off"));
    }

    /**
     * Display toggle printout message.
     */
    public final void togglePrintout() {
        this.snipeData.sendMessage(TextColors.GOLD, "Brush info printout mode has been toggled ", TextColors.DARK_RED,
                ((this.snipeData.owner().getSnipeData(this.snipeData.owner().getCurrentToolId()).isLightningEnabled()) ? "on" : "off"));
    }

    /**
     * Display toggle range message.
     */
    public void toggleRange() {
        this.snipeData.sendMessage(TextColors.GOLD, "Distance Restriction toggled ", TextColors.DARK_RED,
                ((this.snipeData.owner().getSnipeData(this.snipeData.owner().getCurrentToolId()).isRanged()) ? "on" : "off"), TextColors.GOLD,
                ". Range is ", TextColors.LIGHT_PURPLE,
                (double) this.snipeData.owner().getSnipeData(this.snipeData.owner().getCurrentToolId()).getRange());
    }

    /**
     * Display voxel type.
     */
    public void voxel() {
        this.snipeData.sendMessage(TextColors.GOLD, "Voxel: ", TextColors.RED, this.snipeData.getVoxelId());
    }

    /**
     * Display voxel list.
     */
    public void voxelList() {
        if (this.snipeData.getVoxelList().isEmpty()) {
            this.snipeData.sendMessage(TextColors.DARK_GREEN, "No blocks selected!");
        } else {
            Text.Builder returnValueBuilder = Text.builder();
            returnValueBuilder.append(Text.of(TextColors.DARK_GREEN, "Block Types Selected: "));

            StringBuilder vl = new StringBuilder();
            for (BlockType type : this.snipeData.getVoxelList().getWildcardTypes()) {
                vl.append(type.getId());
                vl.append(" ");
            }
            for (BlockState type : this.snipeData.getVoxelList().getSpecificTypes()) {
                vl.append(type.getId());
                vl.append(" ");
            }
            returnValueBuilder.append(Text.of(TextColors.AQUA, vl.toString().trim()));

            this.snipeData.sendMessage(returnValueBuilder.toText());
        }
    }
}
