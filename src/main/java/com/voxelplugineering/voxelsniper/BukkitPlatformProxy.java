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

import com.voxelplugineering.voxelsniper.api.platform.PlatformProxy;

/**
 * A proxy for Bukkit's platform.
 */
public class BukkitPlatformProxy implements PlatformProxy
{

    private Thread mainThread;
    private File dataFolder;
    private File metrics;

    /**
     * Creates a new {@link BukkitPlatformProxy}.
     * 
     * @param thread The main thread
     * @param data The data folder
     * @param cl The classloader
     */
    protected BukkitPlatformProxy(Thread thread, File data)
    {
        this.mainThread = thread;
        this.dataFolder = data;
        this.metrics = new File(data.getParentFile(), "PluginMetrics/config.yml");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return Bukkit.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion()
    {
        return Bukkit.getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullVersion()
    {
        return Bukkit.getBukkitVersion();
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
    public File getDataFolder()
    {
        return this.dataFolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getMetricsFile()
    {
        return this.metrics;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfPlayersOnline()
    {
        return Bukkit.getOnlinePlayers().size();
    }

}
