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
package com.thevoxelbox.voxelsniper.player;

import com.google.common.collect.Maps;
import com.thevoxelbox.voxelsniper.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.BrushManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private static final Map<UUID, PlayerData> data = Maps.newHashMap();

    public static void create(UUID uid) {
        data.put(uid, new PlayerData(uid));
    }

    public static PlayerData get(UUID uid) {
        return data.get(uid);
    }

    public static void clear(UUID uid) {
        data.remove(uid);
    }

    private final UUID uid;
    private Brush<?>   currentBrush = BrushManager.get().getBrush(VoxelSniperConfig.default_brush);
    private BlockState material     = BlockTypes.AIR.getDefaultState();
    private double     size         = 3.5;
    private long       lastSnipe    = 0;

    public PlayerData(UUID uid) {
        this.uid = uid;
    }

    public Player getPlayer() {
        return Sponge.getServer().getPlayer(this.uid).get();
    }

    public Brush<?> getCurrentBrush() {
        return this.currentBrush;
    }

    public void setCurrentBrush(Brush<?> brush) {
        this.currentBrush = brush;
    }

    public BlockState getMaterial() {
        return this.material;
    }

    public void setMaterial(BlockState material) {
        this.material = material;
    }

    public double getBrushSize() {
        return this.size;
    }

    public void setBrushSize(double size) {
        this.size = size;
    }

    public long getLastSnipeTime() {
        return this.lastSnipe;
    }

    public void updateLastSnipeTime() {
        this.lastSnipe = System.currentTimeMillis();
    }

}
