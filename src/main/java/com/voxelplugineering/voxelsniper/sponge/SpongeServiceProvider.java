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
package com.voxelplugineering.voxelsniper.sponge;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.api.config.Configuration;
import com.voxelplugineering.voxelsniper.api.entity.Player;
import com.voxelplugineering.voxelsniper.api.logging.LoggingDistributor;
import com.voxelplugineering.voxelsniper.api.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.api.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.api.registry.RegistryProvider;
import com.voxelplugineering.voxelsniper.api.service.Service;
import com.voxelplugineering.voxelsniper.api.service.ServiceManager;
import com.voxelplugineering.voxelsniper.api.service.ServiceProvider;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.core.Gunsmith;
import com.voxelplugineering.voxelsniper.core.service.BiomeRegistryService;
import com.voxelplugineering.voxelsniper.core.service.CommandHandlerService;
import com.voxelplugineering.voxelsniper.core.service.MaterialRegistryService;
import com.voxelplugineering.voxelsniper.core.service.PlayerRegistryService;
import com.voxelplugineering.voxelsniper.core.service.WorldRegistryService;
import com.voxelplugineering.voxelsniper.core.util.Pair;
import com.voxelplugineering.voxelsniper.sponge.config.SpongeConfiguration;
import com.voxelplugineering.voxelsniper.sponge.entity.SpongePlayer;
import com.voxelplugineering.voxelsniper.sponge.event.handler.SpongeEventHandler;
import com.voxelplugineering.voxelsniper.sponge.service.SpongePermissionProxyService;
import com.voxelplugineering.voxelsniper.sponge.service.SpongePlatformProxyService;
import com.voxelplugineering.voxelsniper.sponge.service.SpongeSchedulerService;
import com.voxelplugineering.voxelsniper.sponge.service.SpongeTextFormatService;
import com.voxelplugineering.voxelsniper.sponge.service.command.SpongeCommandRegistrar;
import com.voxelplugineering.voxelsniper.sponge.service.command.SpongeConsoleProxy;
import com.voxelplugineering.voxelsniper.sponge.service.logging.Slf4jLogger;
import com.voxelplugineering.voxelsniper.sponge.world.SpongeWorld;
import com.voxelplugineering.voxelsniper.sponge.world.biome.SpongeBiome;
import com.voxelplugineering.voxelsniper.sponge.world.material.SpongeMaterial;

/**
 * A provider for bukkit's initialization values.
 */
public class SpongeServiceProvider extends ServiceProvider
{

    private org.spongepowered.api.Game game;

    /**
     * Creates a new {@link SpongeServiceProvider}.
     * 
     * @param game the game instance
     */
    public SpongeServiceProvider(org.spongepowered.api.Game game)
    {
        super(ServiceProvider.Type.PLATFORM);
        this.game = game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerNewServices(ServiceManager manager)
    {

    }

    /**
     * Init hook
     * 
     * @param logger The service
     */
    @InitHook("logger")
    public void getLogger(Service logger)
    {
        org.slf4j.Logger log = VoxelSniperSponge.instance.getLogger();
        ((LoggingDistributor) logger).registerLogger(new Slf4jLogger(log), "sponge");
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("formatProxy")
    public Service getFormatProxy()
    {
        return new SpongeTextFormatService();
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("platformProxy")
    public Service getPlatformProxy()
    {
        return new SpongePlatformProxyService(this.game);
    }

    /**
     * Init hook
     * 
     * @param config The service
     */
    @InitHook("config")
    public void registerConfiguration(Service config)
    {
        ((Configuration) config).registerContainer(SpongeConfiguration.class);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("materialRegistry")
    public Service getMaterialRegistry()
    {
        return new MaterialRegistryService<org.spongepowered.api.block.BlockType>();
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook("materialRegistry")
    public void registerMaterials(Service service)
    {
        @SuppressWarnings("unchecked")
        MaterialRegistry<org.spongepowered.api.block.BlockType> registry = (MaterialRegistry<org.spongepowered.api.block.BlockType>) service;
        for (org.spongepowered.api.block.BlockType m : this.game.getRegistry().getBlocks())
        {
            registry.registerMaterial(m.getId().replace("minecraft:", ""), m, new SpongeMaterial(m));
        }
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("worldRegistry")
    public Service getWorldRegistry()
    {
        WorldRegistryProvider provider = new WorldRegistryProvider(Gunsmith.<org.spongepowered.api.block.BlockType>getMaterialRegistry(), this.game);
        return new WorldRegistryService<org.spongepowered.api.world.World>(provider);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("permissionProxy")
    public Service getPermissionProxy()
    {
        return new SpongePermissionProxyService();
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("playerRegistry")
    public Service getPlayerRegistry()
    {
        return new PlayerRegistryService<org.spongepowered.api.entity.player.Player>(new PlayerRegistryProvider(this.game), new SpongeConsoleProxy());
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook("eventBus")
    public void registerEventProxies(Service service)
    {
        this.game.getEventManager().register(VoxelSniperSponge.instance, new SpongeEventHandler());
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook("commandHandler")
    public void registerCommands(Service service)
    {
        CommandHandlerService cmd = (CommandHandlerService) service;
        cmd.setRegistrar(new SpongeCommandRegistrar());
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("scheduler")
    public Service getSchedulerProxy()
    {
        return new SpongeSchedulerService();
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("biomeRegistry")
    public Service getBiomeRegistry()
    {
        return new BiomeRegistryService<org.spongepowered.api.world.biome.BiomeType>();
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook("biomeRegistry")
    public void registerBiomes(Service service)
    {
        @SuppressWarnings("unchecked")
        BiomeRegistry<org.spongepowered.api.world.biome.BiomeType> reg = (BiomeRegistry<org.spongepowered.api.world.biome.BiomeType>) service;
        for (org.spongepowered.api.world.biome.BiomeType b : this.game.getRegistry().getBiomes())
        {
            reg.registerBiome(b.getName(), b, new SpongeBiome(b));
        }
    }

    /**
     * A world registry provider for sponge.
     */
    public static class WorldRegistryProvider implements RegistryProvider<org.spongepowered.api.world.World, World>
    {

        private MaterialRegistry<org.spongepowered.api.block.BlockType> materials;
        private org.spongepowered.api.Game game;

        /**
         * Creates a new
         * {@link com.voxelplugineering.voxelsniper.api.registry.RegistryProvider}
         *
         * @param mat The material registry
         * @param game The game instance
         */
        public WorldRegistryProvider(MaterialRegistry<org.spongepowered.api.block.BlockType> mat, org.spongepowered.api.Game game)
        {
            this.materials = mat;
            this.game = game;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<Pair<org.spongepowered.api.world.World, World>> get(String name)
        {
            Optional<org.spongepowered.api.Server> server = this.game.getServer();
            if (server.isPresent())
            {
                Optional<org.spongepowered.api.world.World> world = server.get().getWorld(name);
                if (world.isPresent())
                {
                    SpongeWorld spongeWorld = new SpongeWorld(world.get(), this.materials, Gunsmith.getMainThread());
                    return Optional.of(new Pair<org.spongepowered.api.world.World, World>(world.get(), spongeWorld));
                }
            }
            return Optional.absent();
        }
    }

    /**
     * A player registry provider for forge.
     */
    public static class PlayerRegistryProvider implements RegistryProvider<org.spongepowered.api.entity.player.Player, Player>
    {

        private final org.spongepowered.api.Game game;

        /**
         * Creates a new {@link PlayerRegistryProvider}
         *
         * @param g The game instance
         */
        public PlayerRegistryProvider(org.spongepowered.api.Game g)
        {
            this.game = g;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<Pair<org.spongepowered.api.entity.player.Player, Player>> get(String name)
        {
            Optional<org.spongepowered.api.Server> server = this.game.getServer();
            if (server.isPresent())
            {
                Optional<org.spongepowered.api.entity.player.Player> player = server.get().getPlayer(name);
                if (player.isPresent())
                {
                    SpongePlayer splayer = new SpongePlayer(player.get());
                    return Optional.of(new Pair<org.spongepowered.api.entity.player.Player, Player>(player.get(), splayer));
                }
            }
            return Optional.absent();
        }
    }
}
