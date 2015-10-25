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
package com.voxelplugineering.voxelsniper.forge;

import java.util.Optional;

import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.forge.entity.ForgePlayer;
import com.voxelplugineering.voxelsniper.forge.service.command.ForgeConsoleProxy;
import com.voxelplugineering.voxelsniper.forge.world.ForgeWorld;
import com.voxelplugineering.voxelsniper.service.Builder;
import com.voxelplugineering.voxelsniper.service.PlayerRegistryService;
import com.voxelplugineering.voxelsniper.service.ServicePriorities;
import com.voxelplugineering.voxelsniper.service.WorldRegistryService;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.service.registry.RegistryProvider;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.Pair;
import com.voxelplugineering.voxelsniper.world.World;

/**
 * The proxy for operations to only be executed in a client-side environment.
 */
public class ClientProxy extends CommonProxy
{

    @Override
    public int getOnlinePlayerCount()
    {
        return net.minecraft.client.Minecraft.getMinecraft().getIntegratedServer().getConfigurationManager().getCurrentPlayerCount();
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = WorldRegistry.class,
            priority = ServicePriorities.WORLD_REGISTRY_PRIORITY)
    public WorldRegistry<?> getWorldRegistry(Context context)
    {
        return new WorldRegistryService<net.minecraft.world.World>(context, new WorldRegistryProviderClient(context));
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = PlayerRegistry.class,
            priority = ServicePriorities.PLAYER_REGISTRY_PRIORITY)
    public PlayerRegistry<?> getPlayerRegistry(Context context)
    {
        return new PlayerRegistryService<net.minecraft.entity.player.EntityPlayer>(context, new SniperRegistryProviderClient(context),
                new ForgeConsoleProxy());
    }

    /**
     * A world registry provider for forge.
     */
    private class WorldRegistryProviderClient implements RegistryProvider<net.minecraft.world.World, World>
    {

        private final Context context;

        private WorldRegistryProviderClient(Context context)
        {
            this.context = context;
        }

        @Override
        public Optional<Pair<net.minecraft.world.World, World>> get(String name)
        {
            net.minecraft.world.WorldServer w = null;
            for (net.minecraft.world.WorldServer ws : net.minecraft.client.Minecraft.getMinecraft().getIntegratedServer().worldServers)
            {
                if (ws.getWorldInfo().getWorldName().equals(name))
                {
                    w = ws;
                    break;
                }
            }
            if (w == null)
            {
                return Optional.empty();
            }
            return Optional.of(new Pair<net.minecraft.world.World, World>(w, new ForgeWorld(this.context, w)));
        }

    }

    /**
     * A player registry provider for forge.
     */
    private class SniperRegistryProviderClient implements RegistryProvider<net.minecraft.entity.player.EntityPlayer, Player>
    {

        private final Context context;

        public SniperRegistryProviderClient(Context context)
        {
            this.context = context;
        }

        @Override
        public Optional<Pair<net.minecraft.entity.player.EntityPlayer, Player>> get(String name)
        {
            net.minecraft.entity.player.EntityPlayer player = null;
            for (Object e : net.minecraft.client.Minecraft.getMinecraft().getIntegratedServer().getConfigurationManager().playerEntityList)
            {
                net.minecraft.entity.player.EntityPlayer entity = (net.minecraft.entity.player.EntityPlayer) e;
                if (entity.getCommandSenderName().equals(name))
                {
                    player = entity;
                    break;
                }
            }
            if (player == null)
            {
                return Optional.empty();
            }
            return Optional.of(new Pair<net.minecraft.entity.player.EntityPlayer, Player>(player, new ForgePlayer(player, this.context)));
        }

    }

}
