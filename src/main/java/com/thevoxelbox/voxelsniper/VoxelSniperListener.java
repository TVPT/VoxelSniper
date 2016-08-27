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

import com.thevoxelbox.voxelsniper.util.SniperStats;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

/**
 * VoxelSniper event listeners.
 */
public class VoxelSniperListener {

    public VoxelSniperListener() {
        SniperStats.setSnipeCounterInitTimeStamp(System.currentTimeMillis());
    }

    @Listener
    public final void onPlayerInteract(InteractBlockEvent.Secondary event, @Root Player player) {

        if (!player.hasPermission(VoxelSniperConfiguration.PERMISSION_SNIPER)) {
            return;
        }

        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        if (sniper.isEnabled() && sniper.snipe(InteractionType.SECONDARY_MAINHAND, player.getItemInHand().orElse(null),
                event.getTargetBlock().getLocation().orElse(null), event.getTargetSide())) {
            SniperStats.increaseSnipeCounter();
            event.setCancelled(true);
        }
    }

    @Listener
    public final void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);

        if (VoxelSniperConfiguration.LOGIN_MESSAGE_ENABLED && player.hasPermission(VoxelSniperConfiguration.PERMISSION_SNIPER)) {
            sniper.displayInfo();
        }
    }
}
