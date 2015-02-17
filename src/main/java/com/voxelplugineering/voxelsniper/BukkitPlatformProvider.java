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

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.api.brushes.BrushManager;
import com.voxelplugineering.voxelsniper.api.logging.Logger;
import com.voxelplugineering.voxelsniper.api.permissions.PermissionProxy;
import com.voxelplugineering.voxelsniper.api.platform.PlatformProvider;
import com.voxelplugineering.voxelsniper.api.platform.PlatformProxy;
import com.voxelplugineering.voxelsniper.api.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.api.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.api.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.api.registry.RegistryProvider;
import com.voxelplugineering.voxelsniper.api.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.api.service.persistence.DataSourceProvider;
import com.voxelplugineering.voxelsniper.api.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.api.util.text.TextFormatProxy;
import com.voxelplugineering.voxelsniper.brushes.CommonBrushManager;
import com.voxelplugineering.voxelsniper.command.BukkitCommandRegistrar;
import com.voxelplugineering.voxelsniper.command.BukkitConsoleSender;
import com.voxelplugineering.voxelsniper.command.CommandHandler;
import com.voxelplugineering.voxelsniper.commands.AliasCommand;
import com.voxelplugineering.voxelsniper.commands.BrushCommand;
import com.voxelplugineering.voxelsniper.commands.HelpCommand;
import com.voxelplugineering.voxelsniper.commands.MaskMaterialCommand;
import com.voxelplugineering.voxelsniper.commands.MaterialCommand;
import com.voxelplugineering.voxelsniper.commands.RedoCommand;
import com.voxelplugineering.voxelsniper.commands.ResetCommand;
import com.voxelplugineering.voxelsniper.commands.UndoCommand;
import com.voxelplugineering.voxelsniper.commands.VSCommand;
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
import com.voxelplugineering.voxelsniper.service.persistence.DirectoryDataSourceProvider;
import com.voxelplugineering.voxelsniper.service.persistence.NBTDataSource;
import com.voxelplugineering.voxelsniper.util.Pair;
import com.voxelplugineering.voxelsniper.util.text.BukkitTextFormatProxy;
import com.voxelplugineering.voxelsniper.world.BukkitBiome;
import com.voxelplugineering.voxelsniper.world.BukkitWorld;
import com.voxelplugineering.voxelsniper.world.material.BukkitMaterial;

/**
 * A provider for bukkit's initialization values.
 */
public class BukkitPlatformProvider implements PlatformProvider
{

    private JavaPlugin plugin;

    /**
     * Creates a new {@link BukkitPlatformProvider}.
     * 
     * @param pl the plugin
     */
    public BukkitPlatformProvider(JavaPlugin pl)
    {
        check();
        this.plugin = pl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getLogger()
    {
        check();
        return new JavaUtilLogger(this.plugin.getLogger());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLoggerName()
    {
        check();
        return "bukkit";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlatformProxy getPlatformProxy()
    {
        check();
        return new BukkitPlatformProxy(Gunsmith.getMainThread(), Gunsmith.getDataFolder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerConfiguration()
    {
        check();
        Gunsmith.getConfiguration().registerContainer(BukkitConfiguration.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MaterialRegistry<?> getDefaultMaterialRegistry()
    {
        check();
        MaterialRegistry<Material> registry = new CommonMaterialRegistry<Material>();
        for (Material m : Material.values())
        {
            registry.registerMaterial(m.name(), m, new BukkitMaterial(m));
        }
        return registry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldRegistry<?> getWorldRegistry()
    {
        check();
        return new CommonWorldRegistry<World>(new WorldRegistryProvider(Gunsmith.getDefaultMaterialRegistry()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PermissionProxy getPermissionProxy()
    {
        check();
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
     * {@inheritDoc}
     */
    @Override
    public PlayerRegistry<?> getSniperRegistry()
    {
        check();
        return new CommonPlayerRegistry<Player>(new PlayerRegistryProvider(), new BukkitConsoleSender(Bukkit.getConsoleSender()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerEventProxies()
    {
        check();
        Bukkit.getPluginManager().registerEvents(new BukkitEventHandler(), this.plugin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSourceProvider getDefaultBrushLoader()
    {
        check();
        return new DirectoryDataSourceProvider(new File(Gunsmith.getDataFolder(), "brushes"), NBTDataSource.BUILDER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BrushManager getGlobalBrushManager()
    {
        check();
        return new CommonBrushManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandHandler getCommandHandler()
    {
        check();
        CommandHandler cmd = new CommandHandler();
        cmd.setRegistrar(new BukkitCommandRegistrar());

        cmd.registerCommand(new BrushCommand());
        cmd.registerCommand(new MaterialCommand());
        cmd.registerCommand(new MaskMaterialCommand());
        cmd.registerCommand(new VSCommand());
        cmd.registerCommand(new AliasCommand());
        cmd.registerCommand(new HelpCommand());
        cmd.registerCommand(new ResetCommand());
        cmd.registerCommand(new UndoCommand());
        cmd.registerCommand(new RedoCommand());

        return cmd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Scheduler getSchedulerProxy()
    {
        check();
        return new BukkitSchedulerProxy(this.plugin);
    }

    private void check()
    {
        if (Gunsmith.isEnabled())
        {
            throw new IllegalStateException("Cannot fetch from the provider post-initialization");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BiomeRegistry<?> getBiomeRegistry()
    {
        BiomeRegistry<Biome> reg = new CommonBiomeRegistry<Biome>();
        for (Biome b : Biome.values())
        {
            reg.registerBiome(b.name(), b, new BukkitBiome(b));
        }
        return reg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextFormatProxy getFormatProxy()
    {
        return new BukkitTextFormatProxy();
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
