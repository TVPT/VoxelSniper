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

import com.thevoxelbox.voxelsniper.util.VoxelList;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class SnipeData {

    public static final int DEFAULT_CYLINDER_CENTER = 0;
    public static final int DEFAULT_VOXEL_HEIGHT = 1;
    public static final double DEFAULT_BRUSH_SIZE = 3.5;
    public static final String DEFAULT_REPLACE_ID = "air";
    public static final String DEFAULT_VOXEL_ID = "air";

    private Sniper owner;
    private Message voxelMessage;

    private double brushSize = SnipeData.DEFAULT_BRUSH_SIZE;
    private BlockState voxelId = Sponge.getRegistry().getType(BlockState.class, SnipeData.DEFAULT_VOXEL_ID).orElse(BlockTypes.AIR.getDefaultState());
    private BlockState replaceId =
            Sponge.getRegistry().getType(BlockState.class, SnipeData.DEFAULT_REPLACE_ID).orElse(BlockTypes.AIR.getDefaultState());
    private VoxelList voxelList = new VoxelList();
    private Key<?> voxelInkKey = null;
    private Object voxelInkValue = null;
    private Key<?> replaceInkKey = null;
    private Object replaceInkValue = null;

    private int voxelHeight = SnipeData.DEFAULT_VOXEL_HEIGHT;
    private int cCen = SnipeData.DEFAULT_CYLINDER_CENTER;
    private int range = 0;
    private boolean ranged = false;
    private boolean lightning = false;

    public SnipeData(Sniper vs) {
        this.owner = vs;
    }

    // @Cleanup these method names are all over the place

    public double getBrushSize() {
        return this.brushSize;
    }

    public void setBrushSize(double brushSize) {
        this.brushSize = brushSize;
    }

    public int getcCen() {
        return this.cCen;
    }

    public void setcCen(int cCen) {
        this.cCen = cCen;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getReplaceId() {
        return this.replaceId.getId();
    }

    public BlockState getReplaceIdState() {
        return this.replaceId;
    }

    public boolean setReplaceId(String replaceId) {
        Optional<BlockState> state = Sponge.getRegistry().getType(BlockState.class, replaceId);
        if (state.isPresent()) {
            this.replaceId = state.get();
            return true;
        }
        return false;
    }

    public void setReplaceId(BlockState state) {
        this.replaceId = state;
    }

    public int getVoxelHeight() {
        return this.voxelHeight;
    }

    public void setVoxelHeight(int voxelHeight) {
        this.voxelHeight = voxelHeight;
    }

    public String getVoxelId() {
        return this.voxelId.getId();
    }

    public BlockState getVoxelIdState() {
        return this.voxelId;
    }

    public boolean setVoxelId(String voxelId) {
        Optional<BlockState> state = Sponge.getRegistry().getType(BlockState.class, voxelId);
        if (state.isPresent()) {
            this.voxelId = state.get();
            return true;
        }
        return false;
    }

    public void setVoxelId(BlockState state) {
        this.voxelId = state;
    }

    public VoxelList getVoxelList() {
        return this.voxelList;
    }

    public void setVoxelList(VoxelList voxelList) {
        this.voxelList = voxelList;
    }

    public Message getVoxelMessage() {
        return this.voxelMessage;
    }

    public void setVoxelMessage(Message voxelMessage) {
        this.voxelMessage = voxelMessage;
    }

    public World getWorld() {
        return this.owner.getPlayer().getWorld();
    }

    public boolean isLightningEnabled() {
        return this.lightning;
    }

    public void setLightningEnabled(boolean lightning) {
        this.lightning = lightning;
    }

    public boolean isRanged() {
        return this.ranged;
    }

    public void setRanged(boolean ranged) {
        this.ranged = ranged;
    }

    public Key<? extends BaseValue<?>> getVoxelInkKey() {
        return this.voxelInkKey;
    }

    public Object getVoxelInkValue() {
        return this.voxelInkValue;
    }

    public <V extends BaseValue<T>, T> void setVoxelInk(Key<V> key, T value) {
        this.voxelInkKey = key;
        this.voxelInkValue = value;
    }

    public Key<? extends BaseValue<?>> getReplaceInkKey() {
        return this.replaceInkKey;
    }

    public Object getReplaceInkValue() {
        return this.replaceInkValue;
    }

    public <V extends BaseValue<T>, T> void setReplaceInk(Key<V> key, T value) {
        this.replaceInkKey = key;
        this.replaceInkValue = value;
    }

    public Sniper owner() {
        return this.owner;
    }

    /**
     * Reset to default values.
     */
    public void reset() {
        this.voxelId = Sponge.getRegistry().getType(BlockState.class, SnipeData.DEFAULT_VOXEL_ID).orElse(BlockTypes.AIR.getDefaultState());
        this.replaceId = Sponge.getRegistry().getType(BlockState.class, SnipeData.DEFAULT_REPLACE_ID).orElse(BlockTypes.AIR.getDefaultState());
        this.brushSize = SnipeData.DEFAULT_BRUSH_SIZE;
        this.voxelHeight = SnipeData.DEFAULT_VOXEL_HEIGHT;
        this.cCen = SnipeData.DEFAULT_CYLINDER_CENTER;
        this.voxelList = new VoxelList();

        this.voxelInkKey = null;
        this.voxelInkValue = null;
        this.replaceInkKey = null;
        this.replaceInkValue = null;
    }

    public void sendMessage(Object... args) {
        this.owner.getPlayer().sendMessage(Text.of(args));
    }

    public void sendMessage(Text msg) {
        this.owner.getPlayer().sendMessage(msg);
    }
}
