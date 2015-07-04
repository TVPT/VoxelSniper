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

import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.DefaultConfig;

import com.google.inject.Inject;
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.brush.GlobalBrushManager;
import com.voxelplugineering.voxelsniper.expansion.Expansion;
import com.voxelplugineering.voxelsniper.forge.util.SpongeDetector;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.defaults.DefaultBrushBuilder;

/**
 * The main plugin class for Sponge.
 */
@Plugin(id = "voxelsnipersponge", name = "VoxelSniper-Sponge", version = "7.0.0")
public class VoxelSniperSponge implements Expansion
{

    /**
     * The plugin instance.
     */
    public static VoxelSniperSponge instance = null;

    @Inject private org.spongepowered.api.Game game;
    @Inject private org.slf4j.Logger logger;
    @Inject private PluginContainer plugin;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    /**
     * Marks the server as having sponge.
     * 
     * @param event The event
     */
    @Subscribe
    public void onInit(org.spongepowered.api.event.state.PreInitializationEvent event)
    {
        SpongeDetector.sponge();
    }

    /**
     * The init event handler.
     * 
     * @param event The event
     */
    @Subscribe
    public void onServerStarted(org.spongepowered.api.event.state.ServerAboutToStartEvent event)
    {
        instance = this;

        Gunsmith.getServiceManager().register(new SpongeServiceProvider(this.game, this.plugin, this.logger, this.defaultConfig.getParentFile()));
        Gunsmith.getServiceManager().start();

        Context context = Gunsmith.getServiceManager().getContext();

        DefaultBrushBuilder.buildBrushes();
        DefaultBrushBuilder.loadAll(context.getRequired(GlobalBrushManager.class));
    }

    @Override
    public void init()
    {
    }

    @Override
    public void stop()
    {

    }

    /**
     * The stop event handler.
     * 
     * @param event The event
     */
    @Subscribe
    public void onServerStop(org.spongepowered.api.event.state.ServerStoppingEvent event)
    {
        if (Gunsmith.getServiceManager().isRunning())
        {
            Gunsmith.getServiceManager().shutdown();
        }
    }

    /**
     * Gets the slf4j logger for Sponge.
     * 
     * @return The logger
     */
    public org.slf4j.Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Gets the game instance.
     * 
     * @return The game
     */
    public org.spongepowered.api.Game getGame()
    {
        return this.game;
    }
}
