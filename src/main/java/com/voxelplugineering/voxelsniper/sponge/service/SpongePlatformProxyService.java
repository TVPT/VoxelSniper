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
package com.voxelplugineering.voxelsniper.sponge.service;

import com.voxelplugineering.voxelsniper.CommonPlatformProxyService;
import com.voxelplugineering.voxelsniper.sponge.config.SpongeConfiguration;
import com.voxelplugineering.voxelsniper.util.Context;
import org.spongepowered.api.Sponge;

import java.io.File;

/**
 * A proxy for sponge-specific runtime values.
 */
public class SpongePlatformProxyService extends CommonPlatformProxyService
{

    private File metricsConf;

    /**
     * Creates a new {@link SpongePlatformProxyService}.
     * 
     * @param game The game instance
     */
    public SpongePlatformProxyService(Context context, File root)
    {
        super(context, root);
        this.metricsConf = new File(this.rootDir.getParentFile(), SpongeConfiguration.metricsConf);
    }

    @Override
    public String getPlatformName()
    {
        return "Sponge";
    }

    @Override
    public String getVersion()
    {
        return String.format("%s %s", "Sponge", Sponge.getPlatform().getMinecraftVersion());
    }

    @Override
    public String getFullVersion()
    {
        return "Sponge version " + Sponge.getPlatform().getImplementation().getVersion() + " implementing api version " + Sponge.getPlatform().getApi().getVersion();
    }

    @Override
    public int getNumberOfPlayersOnline()
    {
        return Sponge.getServer().getOnlinePlayers().size();
    }

    @Override
    public File getMetricsFile()
    {
        return this.metricsConf;
    }

}
