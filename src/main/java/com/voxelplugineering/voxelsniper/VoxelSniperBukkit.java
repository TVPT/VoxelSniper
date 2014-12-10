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
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.api.IMaterialRegistry;
import com.voxelplugineering.voxelsniper.api.IPermissionProxy;
import com.voxelplugineering.voxelsniper.api.IRegistry;
import com.voxelplugineering.voxelsniper.api.ISniperRegistry;
import com.voxelplugineering.voxelsniper.api.IVoxelSniper;
import com.voxelplugineering.voxelsniper.bukkit.BukkitMaterial;
import com.voxelplugineering.voxelsniper.bukkit.BukkitWorld;
import com.voxelplugineering.voxelsniper.command.BukkitCommandRegistrar;
import com.voxelplugineering.voxelsniper.common.CommonBrushManager;
import com.voxelplugineering.voxelsniper.common.CommonWorld;
import com.voxelplugineering.voxelsniper.common.FileBrushLoader;
import com.voxelplugineering.voxelsniper.common.command.CommandHandler;
import com.voxelplugineering.voxelsniper.common.commands.BrushCommand;
import com.voxelplugineering.voxelsniper.common.commands.MaskMaterialCommand;
import com.voxelplugineering.voxelsniper.common.commands.MaterialCommand;
import com.voxelplugineering.voxelsniper.common.commands.VSCommand;
import com.voxelplugineering.voxelsniper.common.factory.CommonMaterialRegistry;
import com.voxelplugineering.voxelsniper.common.factory.ProvidedWeakRegistry;
import com.voxelplugineering.voxelsniper.common.factory.RegistryProvider;
import com.voxelplugineering.voxelsniper.config.BukkitConfiguration;
import com.voxelplugineering.voxelsniper.logging.JavaUtilLogger;
import com.voxelplugineering.voxelsniper.perms.VaultPermissionProxy;
import com.voxelplugineering.voxelsniper.util.Pair;
import com.voxelplugineering.voxelsniper.util.TemporaryBrushBuilder;

/**
 * The Main class for the bukkit specific implementation.
 */
public class VoxelSniperBukkit extends JavaPlugin implements IVoxelSniper
{

    /**
     * The main plugin instance.
     */
    public static VoxelSniperBukkit voxelsniper;

    private IRegistry<World, BukkitWorld> worldRegistry;
    private IMaterialRegistry<Material> materialRegistry;
    private IPermissionProxy permissions = null;
    private SniperManagerBukkit sniperManager;
    private BukkitEventHandler bukkitEvents;
    private FileBrushLoader brushLoader;
    private CommonBrushManager brushManager;
    private CommandHandler commandHandler;

    /**
     * The main server thread to check synchronous accesses.
     */
    private Thread mainThread;

    /**
     * The main enabling sequence.
     */
    @Override
    public void onEnable()
    {
        mainThread = Thread.currentThread();
        getLogger().setLevel(Level.FINE);
        getDataFolder().mkdirs();

        Gunsmith.beginInit(getDataFolder());

        voxelsniper = this;
        Gunsmith.setPlugin(this);

        Gunsmith.getLoggingDistributor().registerLogger(new JavaUtilLogger(this.getLogger()), "bukkit");

        Gunsmith.getConfiguration().registerContainer(BukkitConfiguration.class);

        this.materialRegistry = new CommonMaterialRegistry<Material>();
        setupMaterials();

        worldRegistry = new ProvidedWeakRegistry<World, BukkitWorld>(new WorldRegistryProvider(this.materialRegistry));

        setupPermissions();

        this.sniperManager = new SniperManagerBukkit();
        this.sniperManager.init();

        this.bukkitEvents = new BukkitEventHandler();
        Bukkit.getPluginManager().registerEvents(this.bukkitEvents, this);

        this.brushLoader = new FileBrushLoader(new File(this.getDataFolder(), "brushes"));
        Gunsmith.setDefaultBrushLoader(this.brushLoader);

        this.brushManager = new CommonBrushManager();
        this.brushManager.init();
        Gunsmith.setGlobalBrushManager(this.brushManager);

        this.commandHandler = new CommandHandler();
        Gunsmith.setCommandHandler(this.commandHandler);
        Gunsmith.getCommandHandler().setRegistrar(new BukkitCommandRegistrar());
        setupCommands();

        Gunsmith.finish();

        TemporaryBrushBuilder.buildBrushes();
        TemporaryBrushBuilder.saveAll(new File(getDataFolder(), "brushes"));

    }

    /**
     * Sets up the permissions.
     */
    private void setupPermissions()
    {
        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
        if (vault != null)
        {
            this.permissions = new VaultPermissionProxy();
        }//TODO if vault not found need to create trivial permission proxy
    }

    /**
     * Registers commands.
     */
    public void setupCommands()
    {
        Gunsmith.getCommandHandler().registerCommand(new BrushCommand());
        Gunsmith.getCommandHandler().registerCommand(new MaterialCommand());
        Gunsmith.getCommandHandler().registerCommand(new MaskMaterialCommand());
        Gunsmith.getCommandHandler().registerCommand(new VSCommand());
    }

    /**
     * Registers the materials into the material factory.
     */
    public void setupMaterials()
    {
        for (Material m : Material.values())
        {
            this.materialRegistry.register(m.name(), m, new BukkitMaterial(m));
        }
    }

    /**
     * The disabling sequence for Gunsmith.
     */
    @Override
    public void onDisable()
    {
        if (Gunsmith.isEnabled())
        {
            Gunsmith.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassLoader getGunsmithClassLoader()
    {
        return this.getClassLoader();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Thread getMainThread()
    {
        return this.mainThread;
    }

    @Override
    public IPermissionProxy getPermissionProxy()
    {
        return this.permissions;
    }

    @Override
    public IRegistry<?, ? extends CommonWorld<?>> getWorldRegistry()
    {
        return this.worldRegistry;
    }

    @Override
    public ISniperRegistry<?> getPlayerRegistry()
    {
        return this.sniperManager;
    }

}

class WorldRegistryProvider implements RegistryProvider<World, BukkitWorld>
{

    IMaterialRegistry<Material> materials;

    public WorldRegistryProvider(IMaterialRegistry<Material> materials)
    {
        this.materials = materials;
    }

    @Override
    public Optional<Pair<World, BukkitWorld>> get(String name)
    {
        World world = Bukkit.getWorld(name);
        if (world == null)
        {
            return Optional.absent();
        }
        return Optional.of(new Pair<World, BukkitWorld>(world, new BukkitWorld(world, materials)));
    }

}
