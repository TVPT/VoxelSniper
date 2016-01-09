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

import com.voxelplugineering.voxelsniper.brush.BrushAction;
import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.event.SnipeEvent;
import com.voxelplugineering.voxelsniper.event.SniperEvent;
import com.voxelplugineering.voxelsniper.event.SniperEvent.SniperDestroyEvent;
import com.voxelplugineering.voxelsniper.service.eventbus.EventBus;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.sponge.config.SpongeConfiguration;
import com.voxelplugineering.voxelsniper.util.Context;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.util.Optional;

/**
 * A proxy for sponge events to post the corresponding Gunsmith events.
 */
public class SpongeEventHandler
{

    private final PlayerRegistry<org.spongepowered.api.entity.living.player.Player> players;
    private final EventBus bus;
    private final ItemType primaryMaterial;
    private final ItemType altMaterial;

    /**
     * Creates a new {@link SpongeEventHandler}.
     * 
     * @param context The context
     */
    @SuppressWarnings("unchecked")
    public SpongeEventHandler(Context context)
    {
        this.players = context.getRequired(PlayerRegistry.class);
        this.bus = context.getRequired(EventBus.class);
        this.primaryMaterial = Sponge.getRegistry().getType(ItemType.class, SpongeConfiguration.primaryMaterial)
                .orElse(ItemTypes.ARROW);
        this.altMaterial = Sponge.getRegistry().getType(ItemType.class, SpongeConfiguration.altMaterial)
                .orElse(ItemTypes.GUNPOWDER);
    }

    /**
     * An event handler for player join events.
     * 
     * @param event The event
     */
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event)
    {
        Optional<Player> s = this.players.getPlayer(event.getTargetEntity().getName());
        if (s.isPresent())
        {
            SniperEvent.SniperCreateEvent sce = new SniperEvent.SniperCreateEvent(s.get());
            this.bus.post(sce);
        }
    }

    /**
     * An event handler for quit events, proxies to Gunsmith's {@link SniperDestroyEvent}.
     * 
     * @param event The event
     */
    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event)
    {
        Optional<Player> s = this.players.getPlayer(event.getTargetEntity().getName());
        if (s.isPresent())
        {
            SniperEvent.SniperDestroyEvent sde = new SniperEvent.SniperDestroyEvent(s.get());
            this.bus.post(sde);
        }
    }

    /**
     * An event handler for sponge's PlayerInteractEvent events.
     * 
     * @param event The event
     */
    @Listener
    public void onPlayerInteractEvent(InteractBlockEvent.Secondary event)
    {
        org.spongepowered.api.entity.living.player.Player p = event.getCause().first(org.spongepowered.api.entity.living.player.Player.class).get();
        if (!p.getItemInHand().isPresent())
        {
            return;
        }
        BrushAction action;
        if (p.getItemInHand().get().getItem().equals(this.primaryMaterial))
        {
            action = BrushAction.PRIMARY;
        } else if (p.getItemInHand().get().getItem().equals(this.altMaterial))
        {
            action = BrushAction.ALTERNATE;
        } else
        {
            return;
        }
        Optional<Player> s = this.players.getPlayer(p);
        if (s.isPresent())
        {
            SnipeEvent se = new SnipeEvent(s.get(), s.get().getYaw(), s.get().getPitch(), action);
            this.bus.post(se);
        }
    }
}
