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
package com.voxelplugineering.voxelsniper.perms;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.voxelplugineering.voxelsniper.api.IPermissionProxy;
import com.voxelplugineering.voxelsniper.api.ISniper;
import com.voxelplugineering.voxelsniper.bukkit.BukkitSniper;

/**
 * A permission proxy for Vault permissions.
 */
public class VaultPermissionProxy implements IPermissionProxy
{

    /**
     * A reference to Vault's permission service.
     */
    private static Permission permissionService = null;

    /**
     * Creates a new {@link VaultPermissionProxy}.
     */
    public VaultPermissionProxy()
    {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (rsp != null)
        {
            permissionService = rsp.getProvider();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOp(ISniper sniper)
    {
        checkNotNull(sniper, "Sniper cannot be null");
        return sniper instanceof BukkitSniper && ((BukkitSniper) sniper).getThis().isOp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(ISniper sniper, String permission)
    {
        checkNotNull(sniper, "Sniper cannot be null");
        checkNotNull(permission, "Permission cannot be null!");
        checkArgument(!permission.isEmpty(), "Permission cannot be empty");
        return sniper instanceof BukkitSniper && permissionService.playerHas(((BukkitSniper) sniper).getThis(), permission);
    }
}
