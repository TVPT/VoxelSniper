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
package com.voxelplugineering.voxelsniper.service;

import java.io.File;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.platform.PlatformProxy;
import com.voxelplugineering.voxelsniper.api.service.persistence.DataSource;
import com.voxelplugineering.voxelsniper.api.service.persistence.DataSourceProvider;
import com.voxelplugineering.voxelsniper.api.service.persistence.DataSourceReader;
import com.voxelplugineering.voxelsniper.service.AbstractService;
import com.voxelplugineering.voxelsniper.service.persistence.DirectoryDataSourceProvider;
import com.voxelplugineering.voxelsniper.service.persistence.FileDataSource;
import com.voxelplugineering.voxelsniper.service.persistence.JsonDataSourceReader;

/**
 * A proxy for Bukkit's platform.
 */
public class BukkitPlatformProxyService extends AbstractService implements PlatformProxy
{

    private DataSource metrics;
    private DataSourceProvider brushDataSource;
    private DataSourceProvider root;
    private DataSourceReader config;

    private final File rootDir;

    /**
     * Creates a new {@link BukkitPlatformProxyService}.
     * 
     * @param data The data folder
     */
    public BukkitPlatformProxyService(File data)
    {
        super(4);
        this.rootDir = data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        this.rootDir.mkdirs();
        this.root = new DirectoryDataSourceProvider(this.rootDir);
        this.metrics = new FileDataSource(new File(this.rootDir.getParentFile(), "PluginMetrics/config.yml"));
        File brushes = new File(this.rootDir, "brushes");
        brushes.mkdirs();
        this.brushDataSource = new DirectoryDataSourceProvider(brushes);
        this.config = new JsonDataSourceReader(new FileDataSource(new File(this.rootDir, "VoxelSniperConfiguration.json")));
        Gunsmith.getLogger().info("Initialized BukkitPlatformProxy service");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void destroy()
    {
        this.metrics = null;
        this.brushDataSource = null;
        Gunsmith.getLogger().info("Stopped BukkitPlatformProxy service");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return "platformProxy";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPlatformName()
    {
        return org.bukkit.Bukkit.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion()
    {
        return org.bukkit.Bukkit.getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullVersion()
    {
        return org.bukkit.Bukkit.getBukkitVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfPlayersOnline()
    {
        return org.bukkit.Bukkit.getOnlinePlayers().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSourceProvider getBrushDataSource()
    {
        return this.brushDataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSourceProvider getRootDataSourceProvider()
    {
        return this.root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getMetricsFile()
    {
        return this.metrics;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DataSourceReader> getConfigDataSource()
    {
        return Optional.of(this.config);
    }

}
