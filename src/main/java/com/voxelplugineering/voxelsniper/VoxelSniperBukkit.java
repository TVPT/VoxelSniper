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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.api.IBrushManager;
import com.voxelplugineering.voxelsniper.api.IVoxelSniper;
import com.voxelplugineering.voxelsniper.bukkit.BukkitMaterial;
import com.voxelplugineering.voxelsniper.bukkit.BukkitWorldFactory;
import com.voxelplugineering.voxelsniper.command.BukkitCommandRegistrar;
import com.voxelplugineering.voxelsniper.common.CommonBrushManager;
import com.voxelplugineering.voxelsniper.common.CommonMaterialFactory;
import com.voxelplugineering.voxelsniper.common.FileBrushLoader;
import com.voxelplugineering.voxelsniper.common.command.CommandHandler;
import com.voxelplugineering.voxelsniper.common.commands.BrushCommand;
import com.voxelplugineering.voxelsniper.common.commands.MaterialCommand;
import com.voxelplugineering.voxelsniper.config.BukkitConfiguration;
import com.voxelplugineering.voxelsniper.logging.BukkitLogger;
import com.voxelplugineering.voxelsniper.perms.VaultPermissionProxy;
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

    private SniperManagerBukkit sniperManager;
    private CommonBrushManager brushManager;
    private FileBrushLoader brushLoader;
    private CommonMaterialFactory<Material> materialFactory;
    private CommandHandler commandHandler;
    private BukkitWorldFactory worldFactory;
    private VaultPermissionProxy permissionProxy;
    
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

        Gunsmith.beginInit();

        voxelsniper = this;
        Gunsmith.setPlugin(this);

        Gunsmith.getLoggingDistributor().registerLogger(new BukkitLogger(), "bukkit");

        Gunsmith.getConfiguration().registerContainer(BukkitConfiguration.class);

        this.worldFactory = new BukkitWorldFactory(this.getServer());
        Gunsmith.setWorldFactory(this.worldFactory);

        this.permissionProxy = new VaultPermissionProxy();
        Gunsmith.setPermissionProxy(this.permissionProxy);

        this.sniperManager = new SniperManagerBukkit();
        this.sniperManager.init();
        Bukkit.getPluginManager().registerEvents(this.sniperManager, this);
        Gunsmith.setSniperManager(this.sniperManager);

        this.brushLoader = new FileBrushLoader(new File(this.getDataFolder(), "brushes"));
        Gunsmith.setDefaultBrushLoader(this.brushLoader);

        this.brushManager = new CommonBrushManager();
        this.brushManager.init();
        Gunsmith.setGlobalBrushManager(this.brushManager);

        setupPermissions();

        this.materialFactory = new CommonMaterialFactory<Material>();
        this.materialFactory.init();
        Gunsmith.setMaterialFactory(this.materialFactory);
        setupMaterials();

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
            Gunsmith.setPermissionProxy(new VaultPermissionProxy());
        }
    }

    /**
     * Registers commands.
     */
    public void setupCommands()
    {
        Gunsmith.getCommandHandler().registerCommand(new BrushCommand());
        Gunsmith.getCommandHandler().registerCommand(new MaterialCommand());
    }

    /**
     * Registers the materials into the material factory.
     */
    public void setupMaterials()
    {
        for (Material m : Material.values())
        {
            this.materialFactory.registerMaterial(m.name(), new BukkitMaterial(m));
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
    public SniperManagerBukkit getSniperManager()
    {
        return this.sniperManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBrushManager getBrushManager()
    {
        return this.brushManager;
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

}
