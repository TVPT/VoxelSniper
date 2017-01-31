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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SniperManager {

    private static final SniperManager instance = new SniperManager();

    public static SniperManager get() {
        return instance;
    }

    private final Cache<UUID, Sniper> sniperInstances;

    private SniperManager() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        if (VoxelSniperConfiguration.SNIPER_CACHE_EXPIRY > 0) {
            int time = VoxelSniperConfiguration.SNIPER_CACHE_EXPIRY;
            if (VoxelSniperConfiguration.SNIPER_CACHE_EXPIRY < 300) {
                VoxelSniper.getLogger().warn("Sniper cache expiry time is very low, this can cause some thrashing as it checks online players");
                VoxelSniper.getLogger().warn("It is suggested to use a minimal cache expiry time of around 3600 seconds (1 hour)");
                time = 300;
            }
            builder.expireAfterAccess(time, TimeUnit.SECONDS);
            Caffeine<UUID, Sniper> builder2 = builder.removalListener((UUID uid, Sniper sniper, RemovalCause cause) -> {
                // If the player is still online then don't reset their settings
                if (sniper.getPlayer() != null) {
                    this.sniperInstances.put(uid, sniper);
                }
            });
            this.sniperInstances = builder2.build();
        } else {
            this.sniperInstances = builder.<UUID, Sniper>build();
        }
    }

    public Sniper getSniperForPlayer(Player player) {
        Sniper sniper = this.sniperInstances.getIfPresent(player.getUniqueId());
        if (sniper == null) {
            this.sniperInstances.put(player.getUniqueId(), sniper = new Sniper(player));
        }
        return sniper;
    }
}
