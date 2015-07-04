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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import com.voxelplugineering.voxelsniper.CommonPlatformProxyService;
import com.voxelplugineering.voxelsniper.util.Context;

/**
 * A proxy for sponge-specific runtime values.
 */
public class SpongePlatformProxyService extends CommonPlatformProxyService
{

    private org.spongepowered.api.Game game;

    /**
     * Creates a new {@link SpongePlatformProxyService}.
     * 
     * @param game The game instance
     */
    public SpongePlatformProxyService(Context context, org.spongepowered.api.Game game, File root)
    {
        super(context, root);
        this.game = checkNotNull(game);
    }

    @Override
    public String getPlatformName()
    {
        return "Sponge";
    }

    @Override
    public String getVersion()
    {
        return String.format("%s %s", "Sponge", this.game.getPlatform().getMinecraftVersion());
    }

    @Override
    public String getFullVersion()
    {
        return "Sponge version " + this.game.getPlatform().getVersion() + " implementing api version " + this.game.getPlatform().getApiVersion();
    }

    @Override
    public int getNumberOfPlayersOnline()
    {
        return this.game.getServer().getOnlinePlayers().size();
    }

}
