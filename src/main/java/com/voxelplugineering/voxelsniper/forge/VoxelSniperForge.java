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

import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.GunsmithLogger;
import com.voxelplugineering.voxelsniper.forge.service.command.ForgeCommandRegistrar;
import com.voxelplugineering.voxelsniper.forge.util.SpongeDetector;
import com.voxelplugineering.voxelsniper.service.command.CommandHandler;
import com.voxelplugineering.voxelsniper.service.command.CommandRegistrar;
import com.voxelplugineering.voxelsniper.service.logging.Log4jLogger;
import com.voxelplugineering.voxelsniper.util.Context;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Optional;

/**
 * The core class of VoxelSniper for minecraft forge.
 */
@Mod(modid = "voxelsniperforge",
        name = "VoxelSniper-Forge",
        version = "7.1.0",
        acceptableRemoteVersions = "*",
        canBeDeactivated = true)
public class VoxelSniperForge
{

    //@formatter:off
    
    /**
     * The plugin instance.
     */
    @Instance(value = "voxelsniperforge")
    public static VoxelSniperForge voxelsniper;

    @SidedProxy(clientSide = "com.voxelplugineering.voxelsniper.forge.ClientProxy",
                serverSide = "com.voxelplugineering.voxelsniper.forge.ServerProxy")
    private static CommonProxy proxy;

    //@formatter:on

    private Logger logger;
    private File configDir;

    /**
     * The preinitialization event.
     * 
     * @param event The event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        this.configDir = event.getModConfigurationDirectory();
        this.logger = event.getModLog();
    }

    /**
     * The Initialization event.
     * 
     * @param event The event
     */
    @EventHandler
    public void onInit(FMLInitializationEvent event)
    {
    }

    /**
     * About to start, used to initialize gunsmith for the session.
     * 
     * @param event The event
     */
    @EventHandler
    public void serverPreStart(FMLServerAboutToStartEvent event)
    {
        if (!SpongeDetector.isSponge())
        {
            GunsmithLogger.getLogger().registerLogger("forge", new Log4jLogger(this.logger));
            Gunsmith.getServiceManager().register(proxy);
            Gunsmith.getServiceManager().start();
        } else
        {
            this.logger.debug("Detected Sponge: disabling VoxelSniper-Forge in favour of sponge version.");
            // Apparently calling this throws errors as the mod lists are backed
            // by immutable maps
            // Loader.instance().runtimeDisableMod("voxelsniperforge");
        }
    }

    /**
     * Server Starting event, this is the location to register commands.
     * 
     * @param event The event
     */
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        if (!SpongeDetector.isSponge())
        {
            Context context = Gunsmith.getServiceManager().getContext();

            Optional<CommandRegistrar> registrar = context.getRequired(CommandHandler.class).getRegistrar();
            if (registrar.isPresent())
            {
                ((ForgeCommandRegistrar) registrar.get()).flush(event);
            }
        }
    }

    /**
     * If sniper is disabled perform shutdown sequences.
     * 
     * @param event The event
     */
    @EventHandler
    public void onDisabled(FMLModDisabledEvent event)
    {
        if (Gunsmith.getServiceManager().isRunning() && !SpongeDetector.isSponge())
        {
            Gunsmith.getServiceManager().shutdown();
        }
    }

    /**
     * The shutdown event.
     * 
     * @param event The event
     */
    @EventHandler
    public void onShutdown(FMLServerStoppingEvent event)
    {
        if (Gunsmith.getServiceManager().isRunning() && !SpongeDetector.isSponge())
        {
            Gunsmith.getServiceManager().shutdown();
        }
    }

    /**
     * Returns the {@link Logger}.
     * 
     * @return The logger
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Returns the {@link SidedProxy}.
     * 
     * @return The proxy
     */
    public CommonProxy getSidedProxy()
    {
        return proxy;
    }

    /**
     * Gets the config directory.
     * 
     * @return The config directory.
     */
    public File getConfigDir()
    {
        return this.configDir;
    }
}
