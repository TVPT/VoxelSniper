/*
 * This file is part of VoxelSniper, licensed under the MIT License (MIT).
 *
 * Copyright (c) The VoxelBox <http://thevoxelbox.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Message {

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
        this.snipeData.sendMessage(TextColors.DARK_BLUE, "Brush Center: ",
                                    TextColors.DARK_RED, this.snipeData.getCylinderCenter());
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
     * Display performer data.
     *
     * @param placeMethod
     * @param replaceMethod
     * @param usePhysics
     */
    public void performerData(PerformBrush.PerformerType placeMethod,
                              PerformBrush.PerformerType replaceMethod,
                              boolean usePhysics) {
        String physics = usePhysics ? "On" : "Off";
        this.snipeData.sendMessage(
                TextColors.DARK_PURPLE,
                "Performers:",
                TextColors.GREEN,
                " place=",
                TextColors.AQUA,
                placeMethod,
                TextColors.GREEN,
                " replace=",
                TextColors.AQUA,
                replaceMethod,
                TextColors.GREEN,
                " physics=",
                TextColors.AQUA,
                physics);
    }

    /**
     * Display replace material.
     */
    public void replace() {
        this.snipeData.sendMessage(TextColors.DARK_BLUE, "Replace Material: ", TextColors.RED, this.snipeData.getReplaceId());
    }

    /**
     * Display brush size.
     */
    public void size() {
        this.snipeData.sendMessage(TextColors.GREEN, "Brush Size: ", TextColors.DARK_RED, this.snipeData.getBrushSize());
        if (this.snipeData.getBrushSize() >= VoxelSniperConfiguration.BRUSH_SIZE_WARNING_THRESHOLD) {
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
        this.snipeData.sendMessage(Text.of(TextColors.DARK_GREEN, this.snipeData.getVoxelList()));
    }
}
