/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
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
package com.voxelplugineering.voxelsniper.sponge.event.handler;

import org.spongepowered.api.entity.EntityInteractionTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.api.entity.Player;
import com.voxelplugineering.voxelsniper.core.Gunsmith;
import com.voxelplugineering.voxelsniper.core.event.SnipeEvent;
import com.voxelplugineering.voxelsniper.core.event.SniperEvent;
import com.voxelplugineering.voxelsniper.core.event.SniperEvent.SniperDestroyEvent;
import com.voxelplugineering.voxelsniper.sponge.VoxelSniperSponge;

/**
 * A proxy for sponge events to post the corresponding Gunsmith events.
 */
public class SpongeEventHandler
{

    private static ItemType TOOL_TYPE;

    static
    {
        Optional<String> id = Gunsmith.getConfiguration().get("arrowMaterial", String.class);
        if (id.isPresent())
        {
            TOOL_TYPE = VoxelSniperSponge.instance.getGame().getRegistry().getType(ItemType.class, id.get()).or(ItemTypes.ARROW);
        } else
        {
            TOOL_TYPE = ItemTypes.ARROW;
        }
    }

    /**
     * An event handler for player join events.
     * 
     * @param event The event
     */
    @org.spongepowered.api.event.Subscribe
    public void onPlayerJoin(org.spongepowered.api.event.entity.player.PlayerJoinEvent event)
    {
        Optional<Player> s = Gunsmith.getPlayerRegistry().getPlayer(event.getPlayer().getName());
        if (s.isPresent())
        {
            SniperEvent.SniperCreateEvent sce = new SniperEvent.SniperCreateEvent(s.get());
            Gunsmith.getEventBus().post(sce);
        }
    }

    /**
     * An event handler for quit events, proxies to Gunsmith's {@link SniperDestroyEvent}.
     * 
     * @param event the event
     */
    @org.spongepowered.api.event.Subscribe
    public void onPlayerLeave(org.spongepowered.api.event.entity.player.PlayerQuitEvent event)
    {
        Optional<Player> s = Gunsmith.getPlayerRegistry().getPlayer(event.getPlayer());
        if (s.isPresent())
        {
            SniperEvent.SniperDestroyEvent sde = new SniperEvent.SniperDestroyEvent(s.get());
            Gunsmith.getEventBus().post(sde);
        }
    }

    /**
     * An event handler for sponge's PlayerInteractEvent events.
     * 
     * @param event The event
     */
    @org.spongepowered.api.event.Subscribe
    public void onPlayerInteractEvent(org.spongepowered.api.event.entity.player.PlayerInteractEvent event)
    {
        org.spongepowered.api.entity.player.Player p = event.getPlayer();
        if (!p.getItemInHand().isPresent()) return;
        if (p.getItemInHand().get().getItem() == TOOL_TYPE && event.getInteractionType() == EntityInteractionTypes.USE)
        {
            Optional<Player> s = Gunsmith.getPlayerRegistry().getPlayer(event.getPlayer());
            if (s.isPresent())
            {
                com.flowpowered.math.vector.Vector3d rotation = p.getRotation(); // {yaw, pitch, roll}
                SnipeEvent se = new SnipeEvent(s.get(), rotation.getX(), rotation.getY());
                Gunsmith.getEventBus().post(se);
            }
        }
    }
}
