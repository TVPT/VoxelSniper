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
package com.voxelplugineering.voxelsniper.forge.service;

import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.forge.entity.ForgePlayer;
import com.voxelplugineering.voxelsniper.service.AbstractService;
import com.voxelplugineering.voxelsniper.service.permission.PermissionProxy;
import com.voxelplugineering.voxelsniper.util.Context;

import net.minecraft.server.MinecraftServer;

/**
 * A proxy for forge permissions.
 */
public class ForgePermissionProxyService extends AbstractService implements PermissionProxy
{

    /**
     * Creates a new {@link ForgePermissionProxyService}.
     */
    public ForgePermissionProxyService(Context context)
    {
        super(context);
    }

    @Override
    protected void _init()
    {
    }

    @Override
    protected void _shutdown()
    {
    }

    @Override
    public boolean hasPermission(Player sniper, String permission)
    {
        return sniper instanceof ForgePlayer && MinecraftServer.getServer().getConfigurationManager().getOppedPlayers()
                .getEntry(((ForgePlayer) sniper).getThis().getGameProfile()) != null;
    }

}
