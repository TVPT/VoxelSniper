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

import net.minecraft.server.MinecraftServer;

import com.voxelplugineering.voxelsniper.api.entity.Player;
import com.voxelplugineering.voxelsniper.api.permissions.PermissionProxy;
import com.voxelplugineering.voxelsniper.core.Gunsmith;
import com.voxelplugineering.voxelsniper.core.service.AbstractService;
import com.voxelplugineering.voxelsniper.forge.entity.ForgePlayer;

/**
 * A proxy for forge permissions.
 */
public class ForgePermissionProxyService extends AbstractService implements PermissionProxy
{

    /**
     * Creates a new {@link ForgePermissionProxyService}.
     */
    public ForgePermissionProxyService()
    {
        super(PermissionProxy.class, 7);
    }

    @Override
    public String getName()
    {
        return "permissionProxy";
    }

    @Override
    protected void init()
    {
        Gunsmith.getLogger().info("Initialized ForgePermissionProxy service");
    }

    @Override
    protected void destroy()
    {
        Gunsmith.getLogger().info("Stopped ForgePermissionProxy service");
    }

    @Override
    public boolean isOp(Player sniper)
    {
        return sniper instanceof ForgePlayer
                && MinecraftServer.getServer().getConfigurationManager().getOppedPlayers()
                        .getEntry(((ForgePlayer) sniper).getThis().getGameProfile()) != null;
    }

    @Override
    public boolean hasPermission(Player sniper, String permission)
    {
        return isOp(sniper);
    }

}
