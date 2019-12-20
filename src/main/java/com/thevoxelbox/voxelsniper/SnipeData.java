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

public class SnipeData {

    private Sniper owner;
    private Message voxelMessage;

    private double brushSize;
    private BlockState voxelState;
    private BlockState replaceState;
    private VoxelList voxelList;
    private Key<?> voxelInkKey;
    private Object voxelInkValue;
    private Key<?> replaceInkKey;
    private Object replaceInkValue;

    private int range;
    private boolean ranged;
    private boolean lightning;

    public SnipeData(Sniper vs) {
        this.owner = vs;

        this.voxelState = Sponge.getRegistry().getType(BlockState.class, VoxelSniperConfiguration.DEFAULT_VOXEL_ID)
                .orElse(BlockTypes.AIR.getDefaultState());
        this.replaceState = Sponge.getRegistry().getType(BlockState.class, VoxelSniperConfiguration.DEFAULT_REPLACE_ID)
                .orElse(BlockTypes.AIR.getDefaultState());

        this.brushSize = VoxelSniperConfiguration.DEFAULT_BRUSH_SIZE;
        this.voxelList = new VoxelList();
        this.range = 0;

        this.ranged = false;
        this.lightning = false;

        this.voxelInkKey = null;
        this.voxelInkValue = null;
        this.replaceInkKey = null;
        this.replaceInkValue = null;
    }

    public double getBrushSize() {
        return this.brushSize;
    }

    public void setBrushSize(double brushSize) {
        this.brushSize = brushSize;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getReplaceId() {
        return this.replaceState.getId();
    }

    public BlockState getReplaceState() {
        return this.replaceState;
    }

    public void setReplaceState(BlockState state) {
        this.replaceState = state;
    }

    public String getVoxelId() {
        return voxelState.getId();
    }

    public BlockState getVoxelState() {
        return this.voxelState;
    }

    public void setVoxelState(BlockState state) {
        this.voxelState = state;
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


    public void sendMessage(Object... args) {
        this.owner.getPlayer().sendMessage(Text.of(args));
    }

    public void sendMessage(Text msg) {
        this.owner.getPlayer().sendMessage(msg);
    }
}
