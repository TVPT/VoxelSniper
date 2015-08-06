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
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.GunsmithLogger;
import com.voxelplugineering.voxelsniper.brush.GlobalBrushManager;
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
import com.voxelplugineering.voxelsniper.config.BaseConfiguration;
import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.service.AnnotationScanner;
import com.voxelplugineering.voxelsniper.service.BiomeRegistryService;
import com.voxelplugineering.voxelsniper.service.Builder;
import com.voxelplugineering.voxelsniper.service.CommandHandlerService;
import com.voxelplugineering.voxelsniper.service.InitHook;
import com.voxelplugineering.voxelsniper.service.MaterialRegistryService;
import com.voxelplugineering.voxelsniper.service.PlayerRegistryService;
import com.voxelplugineering.voxelsniper.service.PostInit;
import com.voxelplugineering.voxelsniper.service.WorldRegistryService;
import com.voxelplugineering.voxelsniper.service.command.CommandHandler;
import com.voxelplugineering.voxelsniper.service.command.CommandSender;
import com.voxelplugineering.voxelsniper.service.config.Configuration;
import com.voxelplugineering.voxelsniper.service.eventbus.EventBus;
import com.voxelplugineering.voxelsniper.service.logging.JavaUtilLogger;
import com.voxelplugineering.voxelsniper.service.permission.PermissionProxy;
import com.voxelplugineering.voxelsniper.service.platform.PlatformProxy;
import com.voxelplugineering.voxelsniper.service.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.service.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.service.registry.RegistryProvider;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.service.text.TextFormatParser;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.Pair;
import com.voxelplugineering.voxelsniper.world.World;

/**
 * A provider for bukkit's initialization values.
 */
@SuppressWarnings({ "checkstyle:javadocmethod", "javadoc" })
public class BukkitServiceProvider
{

    private final org.bukkit.plugin.java.JavaPlugin plugin;

    /**
     * Creates a new {@link BukkitServiceProvider}.
     * 
     * @param pl the plugin
     */
    public BukkitServiceProvider(org.bukkit.plugin.java.JavaPlugin pl)
    {
        this.plugin = checkNotNull(pl);
    }

    @InitHook(target = AnnotationScanner.class)
    public void registerScannerExclusions(Context context, AnnotationScanner scanner)
    {
        scanner.addScannerExclusion("com/voxelplugineering/voxelsniper/forge/");
        scanner.addScannerExclusion("com/voxelplugineering/voxelsniper/sponge/");
    }

    @Builder(target = TextFormatParser.class, priority = 0)
    public TextFormatParser getFormatProxy(Context context)
    {
        return new BukkitTextFormatParser(context);
    }

    @Builder(target = PlatformProxy.class, priority = 4000)
    public PlatformProxy getPlatformProxy(Context context)
    {
        return new BukkitPlatformProxyService(context, this.plugin.getDataFolder());
    }

    @InitHook(target = Configuration.class)
    public void registerConfiguration(Context context, Configuration config)
    {
        // We perform configuration overrides here as its before the configuration
        // is loaded from file in the post-init step.
        BaseConfiguration.defaultBiomeName = org.bukkit.block.Biome.PLAINS.name();
    }

    @Builder(target = MaterialRegistry.class, priority = 5000)
    public MaterialRegistry<?> getMaterialRegistry(Context context)
    {
        return new MaterialRegistryService<org.bukkit.Material>(context);
    }

    @InitHook(target = MaterialRegistry.class)
    public void registerMaterials(Context context, MaterialRegistry<?> service)
    {
        @SuppressWarnings("unchecked")
        MaterialRegistry<org.bukkit.Material> registry = (MaterialRegistry<org.bukkit.Material>) service;
        for (org.bukkit.Material m : org.bukkit.Material.values())
        {
            registry.registerMaterial(m.name(), m, new BukkitMaterial(m));
        }
    }

    @Builder(target = WorldRegistry.class, priority = 6000)
    public WorldRegistry<?> getWorldRegistry(Context context)
    {
        return new WorldRegistryService<org.bukkit.World>(context, new WorldRegistryProvider(context));
    }

    @Builder(target = PermissionProxy.class, priority = 7000)
    public PermissionProxy getPermissionProxy(Context context)
    {
        org.bukkit.plugin.Plugin vault = org.bukkit.Bukkit.getPluginManager().getPlugin("Vault");
        if (vault != null)
        {
            return new VaultPermissionService(context);
        }
        return new SuperPermsPermissionService(context);
    }

    @Builder(target = PlayerRegistry.class, priority = 8000)
    public PlayerRegistry<?> getPlayerRegistry(Context context)
    {
        CommandSender console = new BukkitConsoleSender(org.bukkit.Bukkit.getConsoleSender());
        return new PlayerRegistryService<org.bukkit.entity.Player>(context, new PlayerRegistryProvider(context), console);
    }

    @InitHook(target = EventBus.class)
    public void registerEventProxies(Context context, EventBus service)
    {
        org.bukkit.Bukkit.getPluginManager().registerEvents(new BukkitEventHandler(context), this.plugin);
    }

    @InitHook(target = CommandHandler.class)
    public void registerCommands(Context context, CommandHandler service)
    {
        CommandHandlerService cmd = (CommandHandlerService) service;
        cmd.setRegistrar(new BukkitCommandRegistrar(context));
    }

    @Builder(target = Scheduler.class, priority = 11000)
    public Scheduler getSchedulerProxy(Context context)
    {
        return new BukkitSchedulerService(context, this.plugin);
    }

    @Builder(target = BiomeRegistry.class, priority = 12000)
    public BiomeRegistry<?> getBiomeRegistry(Context context)
    {
        return new BiomeRegistryService<org.bukkit.block.Biome>(context);
    }

    @InitHook(target = BiomeRegistry.class)
    public void registerBiomes(Context context, BiomeRegistry<?> service)
    {
        @SuppressWarnings("unchecked")
        BiomeRegistry<org.bukkit.block.Biome> reg = (BiomeRegistry<org.bukkit.block.Biome>) service;
        for (org.bukkit.block.Biome b : org.bukkit.block.Biome.values())
        {
            reg.registerBiome(b.name(), b, new BukkitBiome(b));
        }
    }

    @PostInit
    public void postInit(Context c)
    {
        GunsmithLogger.getLogger().registerLogger("bukkit", new JavaUtilLogger(this.plugin.getLogger()));
    }

}

/**
 * A provider for {@link BukkitWorld}s.
 */
class WorldRegistryProvider implements RegistryProvider<org.bukkit.World, World>
{

    private final Context context;

    public WorldRegistryProvider(Context context)
    {
        this.context = context;
    }

    @Override
    public Optional<Pair<org.bukkit.World, World>> get(String name)
    {
        org.bukkit.World world = org.bukkit.Bukkit.getWorld(name);
        if (world == null)
        {
            return Optional.absent();
        }
        return Optional.of(new Pair<org.bukkit.World, World>(world, new BukkitWorld(this.context, world, Gunsmith.getMainThread())));
    }

}

class PlayerRegistryProvider implements RegistryProvider<org.bukkit.entity.Player, Player>
{

    private final Context context;
    private final GlobalBrushManager bm;

    public PlayerRegistryProvider(Context context)
    {
        this.context = context;
        this.bm = context.getRequired(GlobalBrushManager.class);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Optional<Pair<org.bukkit.entity.Player, Player>> get(String name)
    {
        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(name);
        if (player == null)
        {
            return Optional.absent();
        }
        BukkitPlayer bp = new BukkitPlayer(player, this.bm, this.context);
        bp.init(this.context);
        return Optional.of(new Pair<org.bukkit.entity.Player, Player>(player, bp));
    }

}
