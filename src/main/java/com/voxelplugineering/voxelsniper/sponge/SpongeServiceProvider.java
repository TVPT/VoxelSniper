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

import java.io.File;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.biome.BiomeType;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.GunsmithLogger;
import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.service.BiomeRegistryService;
import com.voxelplugineering.voxelsniper.service.Builder;
import com.voxelplugineering.voxelsniper.service.CommandHandlerService;
import com.voxelplugineering.voxelsniper.service.InitHook;
import com.voxelplugineering.voxelsniper.service.MaterialRegistryService;
import com.voxelplugineering.voxelsniper.service.PlayerRegistryService;
import com.voxelplugineering.voxelsniper.service.PostInit;
import com.voxelplugineering.voxelsniper.service.WorldRegistryService;
import com.voxelplugineering.voxelsniper.service.command.CommandHandler;
import com.voxelplugineering.voxelsniper.service.config.Configuration;
import com.voxelplugineering.voxelsniper.service.eventbus.EventBus;
import com.voxelplugineering.voxelsniper.service.permission.PermissionProxy;
import com.voxelplugineering.voxelsniper.service.platform.PlatformProxy;
import com.voxelplugineering.voxelsniper.service.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.service.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.service.registry.RegistryProvider;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.service.text.TextFormatParser;
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
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.Pair;
import com.voxelplugineering.voxelsniper.world.World;

/**
 * A provider for bukkit's initialization values.
 */
public class SpongeServiceProvider
{

    private final org.spongepowered.api.Game game;
    private final PluginContainer plugin;
    private final org.slf4j.Logger logger;
    private final File root;

    /**
     * Creates a new {@link SpongeServiceProvider}.
     * 
     * @param game the game instance
     * @param plugin The plugin container
     * @param logger The logger
     */
    public SpongeServiceProvider(org.spongepowered.api.Game game, PluginContainer plugin, org.slf4j.Logger logger, File root)
    {
        this.game = game;
        this.plugin = plugin;
        this.logger = logger;
        this.root = root;
    }

    @PostInit
    public void postInit(Context c) {
        GunsmithLogger.getLogger().registerLogger(new Slf4jLogger(this.logger), "sponge");
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = TextFormatParser.class, priority = 0)
    public TextFormatParser getFormatProxy(Context context)
    {
        return new SpongeTextFormatService(context);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = PlatformProxy.class, priority = 4000)
    public PlatformProxy getPlatformProxy(Context context)
    {
        return new SpongePlatformProxyService(context, this.game, this.root);
    }

    /**
     * Init hook
     * 
     * @param config The service
     */
    @InitHook(target = Configuration.class)
    public void registerConfiguration(Context context, Configuration config)
    {
        config.registerContainer(SpongeConfiguration.class);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = MaterialRegistry.class, priority = 5000)
    public MaterialRegistry<?> getMaterialRegistry(Context context)
    {
        return new MaterialRegistryService<org.spongepowered.api.block.BlockType>(context);
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook(target = MaterialRegistry.class)
    public void registerMaterials(Context context, MaterialRegistry<?> service)
    {
        @SuppressWarnings("unchecked") MaterialRegistry<org.spongepowered.api.block.BlockType> registry =
                (MaterialRegistry<org.spongepowered.api.block.BlockType>) service;
        for (org.spongepowered.api.block.BlockType m : this.game.getRegistry().getAllOf(BlockType.class))
        {
            System.out.println(m.getId());
            registry.registerMaterial(m.getId().replace("minecraft:", ""), m, new SpongeMaterial(m));
        }
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = WorldRegistry.class, priority = 6000)
    public WorldRegistry<?> getWorldRegistry(Context context)
    {
        WorldRegistryProvider provider = new WorldRegistryProvider(context, this.game);
        return new WorldRegistryService<org.spongepowered.api.world.World>(context, provider);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = PermissionProxy.class, priority = 7000)
    public PermissionProxy getPermissionProxy(Context context)
    {
        return new SpongePermissionProxyService(context);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = PlayerRegistry.class, priority = 8000)
    public PlayerRegistry<?> getPlayerRegistry(Context context)
    {
        return new PlayerRegistryService<org.spongepowered.api.entity.player.Player>(context, new PlayerRegistryProvider(context, this.game),
                new SpongeConsoleProxy());
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook(target = EventBus.class)
    public void registerEventProxies(Context context, EventBus service)
    {
        this.game.getEventManager().register(this.plugin, new SpongeEventHandler(context));
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook(target = CommandHandler.class)
    public void registerCommands(Context context, CommandHandler service)
    {
        CommandHandlerService cmd = (CommandHandlerService) service;
        cmd.setRegistrar(new SpongeCommandRegistrar(context));
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = Scheduler.class, priority = 11000)
    public Scheduler getSchedulerProxy(Context context)
    {
        return new SpongeSchedulerService(context, this.plugin, this.game);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = BiomeRegistry.class, priority = 12000)
    public BiomeRegistry<?> getBiomeRegistry(Context context)
    {
        return new BiomeRegistryService<org.spongepowered.api.world.biome.BiomeType>(context);
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook(target = BiomeRegistry.class)
    public void registerBiomes(Context context, BiomeRegistry<?> service)
    {
        @SuppressWarnings("unchecked") BiomeRegistry<org.spongepowered.api.world.biome.BiomeType> reg =
                (BiomeRegistry<org.spongepowered.api.world.biome.BiomeType>) service;
        for (org.spongepowered.api.world.biome.BiomeType b : this.game.getRegistry().getAllOf(BiomeType.class))
        {
            reg.registerBiome(b.getName(), b, new SpongeBiome(b));
        }
    }

    /**
     * A world registry provider for sponge.
     */
    public static class WorldRegistryProvider implements RegistryProvider<org.spongepowered.api.world.World, World>
    {

        private final Context context;
        private final org.spongepowered.api.Game game;

        /**
         * Creates a new {@link com.voxelplugineering.voxelsniper.service.registry.RegistryProvider}
         *
         * @param game The game instance
         */
        public WorldRegistryProvider(Context context, org.spongepowered.api.Game game)
        {
            this.context = context;
            this.game = game;
        }

        @Override
        public Optional<Pair<org.spongepowered.api.world.World, World>> get(String name)
        {
            Optional<org.spongepowered.api.world.World> world = this.game.getServer().getWorld(name);
            if (world.isPresent())
            {
                SpongeWorld spongeWorld = new SpongeWorld(this.context, world.get(), Gunsmith.getMainThread());
                return Optional.of(new Pair<org.spongepowered.api.world.World, World>(world.get(), spongeWorld));
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
        private final Context context;

        /**
         * Creates a new {@link PlayerRegistryProvider}
         *
         * @param g The game instance
         */
        public PlayerRegistryProvider(Context context, org.spongepowered.api.Game g)
        {
            this.game = g;
            this.context = context;
        }

        @Override
        public Optional<Pair<org.spongepowered.api.entity.player.Player, Player>> get(String name)
        {
            Optional<org.spongepowered.api.entity.player.Player> player = this.game.getServer().getPlayer(name);
            if (player.isPresent())
            {
                SpongePlayer splayer = new SpongePlayer(this.context, player.get());
                splayer.init();
                return Optional.of(new Pair<org.spongepowered.api.entity.player.Player, Player>(player.get(), splayer));
            }
            return Optional.absent();
        }
    }
}
