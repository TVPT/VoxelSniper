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
package com.voxelplugineering.voxelsniper.bukkit.service.command;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;

import com.voxelplugineering.voxelsniper.api.service.command.CommandRegistrar;
import com.voxelplugineering.voxelsniper.api.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.bukkit.util.CraftBukkitFetcher;
import com.voxelplugineering.voxelsniper.core.GunsmithLogger;
import com.voxelplugineering.voxelsniper.core.commands.Command;
import com.voxelplugineering.voxelsniper.core.util.Context;

/**
 * A registrar for registering Gunsmith command within the bukkit command handler.
 */
public class BukkitCommandRegistrar implements CommandRegistrar
{

    private final PlayerRegistry<org.bukkit.entity.Player> pr;
    private org.bukkit.command.CommandMap commands;

    /**
     * Creates a new {@link BukkitCommandRegistrar}. This fetches bukkit's CommandMap via reflection
     * for use to register commands.
     */
    @SuppressWarnings("unchecked")
    public BukkitCommandRegistrar(Context context)
    {
        this.pr = context.getRequired(PlayerRegistry.class);
        try
        {
            Field cmap = Class.forName(CraftBukkitFetcher.CRAFTBUKKIT_PACKAGE + ".CraftServer").getDeclaredField("commandMap");
            cmap.setAccessible(true);
            this.commands = (org.bukkit.command.CommandMap) cmap.get(org.bukkit.Bukkit.getServer());
        } catch (Exception e)
        {
            GunsmithLogger.getLogger().error(e, "Error setting up bukkit command registrar");
        }
    }

    @Override
    public void registerCommand(Command cmd)
    {
        checkNotNull(cmd);
        BukkitCommand bcmd = new BukkitCommand(cmd.getName(), cmd, this.pr);
        this.commands.register("voxelsniper", bcmd);
        for (String alias : cmd.getAllAliases())
        {
            bcmd = new BukkitCommand(alias, cmd, this.pr);
            this.commands.register("voxelsniper", bcmd);
        }
    }

}
