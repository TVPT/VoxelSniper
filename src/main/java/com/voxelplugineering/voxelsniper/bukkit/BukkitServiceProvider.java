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
package com.voxelplugineering.voxelsniper.bukkit;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.api.commands.CommandSender;
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
import com.voxelplugineering.voxelsniper.bukkit.config.BukkitConfiguration;
import com.voxelplugineering.voxelsniper.bukkit.entity.BukkitPlayer;
import com.voxelplugineering.voxelsniper.bukkit.event.handler.BukkitEventHandler;
import com.voxelplugineering.voxelsniper.bukkit.service.BukkitPlatformProxyService;
import com.voxelplugineering.voxelsniper.bukkit.service.BukkitSchedulerService;
import com.voxelplugineering.voxelsniper.bukkit.service.BukkitTextFormatParser;
import com.voxelplugineering.voxelsniper.bukkit.service.SuperPermsPermissionService;
import com.voxelplugineering.voxelsniper.bukkit.service.VaultPermissionService;
import com.voxelplugineering.voxelsniper.bukkit.service.command.BukkitCommandRegistrar;
import com.voxelplugineering.voxelsniper.bukkit.service.command.BukkitConsoleSender;
import com.voxelplugineering.voxelsniper.bukkit.world.BukkitWorld;
import com.voxelplugineering.voxelsniper.bukkit.world.biome.BukkitBiome;
import com.voxelplugineering.voxelsniper.bukkit.world.material.BukkitMaterial;
import com.voxelplugineering.voxelsniper.core.Gunsmith;
import com.voxelplugineering.voxelsniper.core.service.BiomeRegistryService;
import com.voxelplugineering.voxelsniper.core.service.CommandHandlerService;
import com.voxelplugineering.voxelsniper.core.service.MaterialRegistryService;
import com.voxelplugineering.voxelsniper.core.service.PlayerRegistryService;
import com.voxelplugineering.voxelsniper.core.service.WorldRegistryService;
import com.voxelplugineering.voxelsniper.core.service.logging.JavaUtilLogger;
import com.voxelplugineering.voxelsniper.core.util.Pair;

/**
 * A provider for bukkit's initialization values.
 */
public class BukkitServiceProvider extends ServiceProvider
{

    private final org.bukkit.plugin.java.JavaPlugin plugin;

    /**
     * Creates a new {@link BukkitServiceProvider}.
     * 
     * @param pl the plugin
     */
    public BukkitServiceProvider(org.bukkit.plugin.java.JavaPlugin pl)
    {
        super(ServiceProvider.Type.PLATFORM);
        this.plugin = checkNotNull(pl);
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
        ((LoggingDistributor) logger).registerLogger(new JavaUtilLogger(this.plugin.getLogger()), "bukkit");
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("formatProxy")
    public Service getFormatProxy()
    {
        return new BukkitTextFormatParser();
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("platformProxy")
    public Service getPlatformProxy()
    {
        return new BukkitPlatformProxyService(this.plugin.getDataFolder());
    }

    /**
     * Init hook
     * 
     * @param config The service
     */
    @InitHook("config")
    public void registerConfiguration(Service config)
    {
        ((Configuration) config).registerContainer(BukkitConfiguration.class);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("materialRegistry")
    public Service getMaterialRegistry()
    {
        return new MaterialRegistryService<org.bukkit.Material>();
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook("materialRegistry")
    public void registerMaterials(Service service)
    {
        @SuppressWarnings("unchecked") MaterialRegistry<org.bukkit.Material> registry = (MaterialRegistry<org.bukkit.Material>) service;
        for (org.bukkit.Material m : org.bukkit.Material.values())
        {
            registry.registerMaterial(m.name(), m, new BukkitMaterial(m));
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
        return new WorldRegistryService<org.bukkit.World>(new WorldRegistryProvider(Gunsmith.getMaterialRegistry()));
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("permissionProxy")
    public Service getPermissionProxy()
    {
        org.bukkit.plugin.Plugin vault = org.bukkit.Bukkit.getPluginManager().getPlugin("Vault");
        if (vault != null)
        {
            return new VaultPermissionService();
        }
        return new SuperPermsPermissionService();
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("playerRegistry")
    public Service getPlayerRegistry()
    {
        CommandSender console = new BukkitConsoleSender(org.bukkit.Bukkit.getConsoleSender());
        return new PlayerRegistryService<org.bukkit.entity.Player>(new PlayerRegistryProvider(), console);
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook("eventBus")
    public void registerEventProxies(Service service)
    {
        org.bukkit.Bukkit.getPluginManager().registerEvents(new BukkitEventHandler(), this.plugin);
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
        cmd.setRegistrar(new BukkitCommandRegistrar());
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("scheduler")
    public Service getSchedulerProxy()
    {
        return new BukkitSchedulerService(this.plugin);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("biomeRegistry")
    public Service getBiomeRegistry()
    {
        return new BiomeRegistryService<org.bukkit.block.Biome>();
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook("biomeRegistry")
    public void registerBiomes(Service service)
    {
        @SuppressWarnings("unchecked") BiomeRegistry<org.bukkit.block.Biome> reg = (BiomeRegistry<org.bukkit.block.Biome>) service;
        for (org.bukkit.block.Biome b : org.bukkit.block.Biome.values())
        {
            reg.registerBiome(b.name(), b, new BukkitBiome(b));
        }
    }

}

/**
 * A provider for {@link BukkitWorld}s.
 */
class WorldRegistryProvider implements RegistryProvider<org.bukkit.World, World>
{

    MaterialRegistry<?> materials;

    /**
     * Creates a new WorldRegistryProvider.
     * 
     * @param materials the material registry
     */
    public WorldRegistryProvider(MaterialRegistry<?> materials)
    {
        this.materials = materials;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<Pair<org.bukkit.World, World>> get(String name)
    {
        org.bukkit.World world = org.bukkit.Bukkit.getWorld(name);
        if (world == null)
        {
            return Optional.absent();
        }
        return Optional.of(new Pair<org.bukkit.World, World>(world, new BukkitWorld(world, (MaterialRegistry<org.bukkit.Material>) this.materials,
                Thread.currentThread())));
    }

}

class PlayerRegistryProvider implements RegistryProvider<org.bukkit.entity.Player, Player>
{

    @SuppressWarnings("deprecation")
    @Override
    public Optional<Pair<org.bukkit.entity.Player, Player>> get(String name)
    {
        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(name);
        if (player == null)
        {
            return Optional.absent();
        }
        return Optional.of(new Pair<org.bukkit.entity.Player, Player>(player, new BukkitPlayer(player)));
    }

}
