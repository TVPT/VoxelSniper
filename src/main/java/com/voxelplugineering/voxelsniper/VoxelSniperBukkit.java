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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.voxelplugineering.voxelsniper.api.IBrushLoader;
import com.voxelplugineering.voxelsniper.api.IBrushManager;
import com.voxelplugineering.voxelsniper.api.IRegistry;
import com.voxelplugineering.voxelsniper.api.IVoxelSniper;
import com.voxelplugineering.voxelsniper.bukkit.BukkitMaterial;
import com.voxelplugineering.voxelsniper.bukkit.BukkitWorld;
import com.voxelplugineering.voxelsniper.command.BukkitCommandRegistrar;
import com.voxelplugineering.voxelsniper.common.CommonBrushManager;
import com.voxelplugineering.voxelsniper.common.FileBrushLoader;
import com.voxelplugineering.voxelsniper.common.command.CommandHandler;
import com.voxelplugineering.voxelsniper.common.commands.BrushCommand;
import com.voxelplugineering.voxelsniper.common.commands.MaskMaterialCommand;
import com.voxelplugineering.voxelsniper.common.commands.MaterialCommand;
import com.voxelplugineering.voxelsniper.common.event.CommonEventHandler;
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

    /**
     * The sniper manager.
     */
    private SniperManagerBukkit sniperManager;
    /**
     * The global brush manager.
     */
    private CommonBrushManager brushManager;
    /**
     * The default brush loader.
     */
    private FileBrushLoader brushLoader;
    /**
     * The standard material registry.
     */
    private CommonMaterialRegistry<Material> materialRegistry;
    /**
     * The command handler.
     */
    private CommandHandler commandHandler;
    /**
     * The permissions proxy.
     */
    private VaultPermissionProxy permissionProxy;
    /**
     * The world registry.
     */
    private IRegistry<World, BukkitWorld> worldRegistry;
    /**
     * The default event handler for Gunsmith events.
     */
    private CommonEventHandler defaultEventHandler;
    /**
     * The bukkit event handler.
     */
    private BukkitEventHandler bukkitEvents;
    
    /**
     * Gunsmith's manager.
     */
    private Gunsmith gunsmith;
    
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
        gunsmith = new Gunsmith();
        
        mainThread = Thread.currentThread();

        gunsmith.beginInit();

        voxelsniper = this;

        gunsmith.getLoggingDistributor().registerLogger(new JavaUtilLogger(this.getLogger()), "bukkit");

        gunsmith.getConfiguration().registerContainer(BukkitConfiguration.class);

        worldRegistry = new ProvidedWeakRegistry<World, BukkitWorld>(new RegistryProvider<World, BukkitWorld>()
        {

            @Override
            public Pair<World, BukkitWorld> get(String name)
            {
                World world = Bukkit.getWorld(name);
                if(world == null)
                {
                    return null;
                }
                return new Pair<World, BukkitWorld>(world, new BukkitWorld(world, materialRegistry, mainThread));
            }
        });
        
        this.permissionProxy = new VaultPermissionProxy();

        this.sniperManager = new SniperManagerBukkit(this.worldRegistry, (Integer) gunsmith.getConfiguration().get("BLOCK_CHANGES_PER_SECOND"), this);
        this.sniperManager.init();

        this.brushLoader = new FileBrushLoader(new File(this.getDataFolder(), "brushes"));

        this.brushManager = new CommonBrushManager(brushLoader, this.getClassLoader(), gunsmith.getCompilerFactory());

        setupPermissions();

        this.materialRegistry = new CommonMaterialRegistry<Material>();
        setupMaterials();
        
        defaultEventHandler = new CommonEventHandler(materialRegistry.getAirMaterial(), gunsmith.getConfiguration());
        gunsmith.getEventBus().register(defaultEventHandler);
        bukkitEvents = new BukkitEventHandler(gunsmith.getEventBus(), this.sniperManager, (Material) gunsmith.getConfiguration().get("ARROW_MATERIAL"));
        Bukkit.getPluginManager().registerEvents(this.bukkitEvents, this);

        this.commandHandler = new CommandHandler(this.permissionProxy, gunsmith.getConfiguration());
        this.commandHandler.setRegistrar(new BukkitCommandRegistrar(this.sniperManager));
        setupCommands();

        gunsmith.finishInit();

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
            this.permissionProxy = new VaultPermissionProxy();
        }
    }

    /**
     * Registers commands.
     */
    public void setupCommands()
    {
        this.commandHandler.registerCommand(new BrushCommand(gunsmith.getConfiguration()));
        this.commandHandler.registerCommand(new MaterialCommand(this.materialRegistry, gunsmith.getConfiguration()));
        this.commandHandler.registerCommand(new MaskMaterialCommand(this.materialRegistry, gunsmith.getConfiguration()));
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
        if (gunsmith.isEnabled())
        {
            gunsmith.stop();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public IBrushManager getGlobalBrushManager()
    {
        return this.brushManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBrushLoader getDefaultBrushLoader()
    {
        return this.brushLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Gunsmith getGunsmith()
    {
        return this.gunsmith;
    }

}
