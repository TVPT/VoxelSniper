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
package com.voxelplugineering.voxelsniper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.api.config.Configuration;
import com.voxelplugineering.voxelsniper.api.logging.LoggingDistributor;
import com.voxelplugineering.voxelsniper.api.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.api.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.api.registry.RegistryProvider;
import com.voxelplugineering.voxelsniper.api.service.Service;
import com.voxelplugineering.voxelsniper.api.service.ServiceManager;
import com.voxelplugineering.voxelsniper.api.service.ServiceProvider;
import com.voxelplugineering.voxelsniper.command.BukkitCommandRegistrar;
import com.voxelplugineering.voxelsniper.command.BukkitConsoleSender;
import com.voxelplugineering.voxelsniper.command.CommandHandler;
import com.voxelplugineering.voxelsniper.config.BukkitConfiguration;
import com.voxelplugineering.voxelsniper.entity.living.BukkitPlayer;
import com.voxelplugineering.voxelsniper.logging.JavaUtilLogger;
import com.voxelplugineering.voxelsniper.perms.SuperPermsPermissionProxy;
import com.voxelplugineering.voxelsniper.perms.VaultPermissionProxy;
import com.voxelplugineering.voxelsniper.registry.CommonBiomeRegistry;
import com.voxelplugineering.voxelsniper.registry.CommonMaterialRegistry;
import com.voxelplugineering.voxelsniper.registry.CommonPlayerRegistry;
import com.voxelplugineering.voxelsniper.registry.CommonWorldRegistry;
import com.voxelplugineering.voxelsniper.scheduler.BukkitSchedulerProxy;
import com.voxelplugineering.voxelsniper.util.Pair;
import com.voxelplugineering.voxelsniper.util.text.BukkitTextFormatProxy;
import com.voxelplugineering.voxelsniper.world.BukkitBiome;
import com.voxelplugineering.voxelsniper.world.BukkitWorld;
import com.voxelplugineering.voxelsniper.world.material.BukkitMaterial;

/**
 * A provider for bukkit's initialization values.
 */
public class BukkitServiceProvider extends ServiceProvider
{

    private JavaPlugin plugin;

    /**
     * Creates a new {@link BukkitServiceProvider}.
     * 
     * @param pl the plugin
     */
    public BukkitServiceProvider(JavaPlugin pl)
    {
        super(ServiceProvider.Type.PLATFORM);
        this.plugin = pl;
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
        return new BukkitTextFormatProxy();
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("platformProxy")
    public Service getPlatformProxy()
    {
        return new BukkitPlatformProxy();
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
        return new CommonMaterialRegistry<Material>();
    }

    /**
     * Init hook
     * 
     * @param config The service
     */
    @InitHook("materialRegistry")
    public void registerMaterials(Service config)
    {
        @SuppressWarnings("unchecked")
        MaterialRegistry<Material> registry = (MaterialRegistry<Material>) config;
        for (Material m : Material.values())
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
        return new CommonWorldRegistry<World>(new WorldRegistryProvider(Gunsmith.getMaterialRegistry()));
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("permissionProxy")
    public Service getPermissionProxy()
    {
        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
        if (vault != null)
        {
            return new VaultPermissionProxy();
        } else
        {
            return new SuperPermsPermissionProxy();
        }
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("playerRegistry")
    public Service getPlayerRegistry()
    {
        return new CommonPlayerRegistry<Player>(new PlayerRegistryProvider(), new BukkitConsoleSender(Bukkit.getConsoleSender()));
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook("eventBus")
    public void registerEventProxies(Service service)
    {
        Bukkit.getPluginManager().registerEvents(new BukkitEventHandler(), this.plugin);
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook("commandHandler")
    public void registerCommands(Service service)
    {
        CommandHandler cmd = (CommandHandler) service;
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
        return new BukkitSchedulerProxy(this.plugin);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder("biomeRegistry")
    public Service getBiomeRegistry()
    {
        return new CommonBiomeRegistry<Biome>();
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
        BiomeRegistry<Biome> reg = (BiomeRegistry<Biome>) service;
        for (Biome b : Biome.values())
        {
            reg.registerBiome(b.name(), b, new BukkitBiome(b));
        }
    }

}

/**
 * A provider for {@link BukkitWorld}s.
 */
class WorldRegistryProvider implements RegistryProvider<World, com.voxelplugineering.voxelsniper.api.world.World>
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
    public Optional<Pair<World, com.voxelplugineering.voxelsniper.api.world.World>> get(String name)
    {
        World world = Bukkit.getWorld(name);
        if (world == null)
        {
            return Optional.absent();
        }
        return Optional.of(new Pair<World, com.voxelplugineering.voxelsniper.api.world.World>(world, new BukkitWorld(world,
                (MaterialRegistry<Material>) this.materials, Thread.currentThread())));
    }

}

class PlayerRegistryProvider implements RegistryProvider<Player, com.voxelplugineering.voxelsniper.api.entity.living.Player>
{

    @SuppressWarnings("deprecation")
    @Override
    public Optional<Pair<Player, com.voxelplugineering.voxelsniper.api.entity.living.Player>> get(String name)
    {
        Player player = Bukkit.getPlayer(name);
        if (player == null)
        {
            return Optional.absent();
        }
        return Optional.of(new Pair<Player, com.voxelplugineering.voxelsniper.api.entity.living.Player>(player, new BukkitPlayer(player)));
    }

}
