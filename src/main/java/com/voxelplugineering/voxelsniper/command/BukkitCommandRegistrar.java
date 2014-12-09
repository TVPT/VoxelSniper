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
package com.voxelplugineering.voxelsniper.command;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.ICommandRegistrar;
import com.voxelplugineering.voxelsniper.api.ISniperRegistry;
import com.voxelplugineering.voxelsniper.common.command.Command;
import com.voxelplugineering.voxelsniper.util.CraftBukkitFetcher;

/**
 * A registrar for registering Gunsmith command within the bukkit command handler.
 */
public class BukkitCommandRegistrar implements ICommandRegistrar
{
    /**
     * A reference to bukkit's {@link CommandMap}.
     * <p>
     * TODO: possible memory leak if bukkit attempts to recreate this map, perhaps across reloads.
     */
    private CommandMap commands;

    /**
     * Creates a new {@link BukkitCommandRegistrar}. This fetches bukkit's {@link CommandMap} via reflection for use to register commands.
     * 
     * @param playerRegistry the player registry
     */
    public BukkitCommandRegistrar()
    {
        try
        {
            Field cmap = Class.forName(CraftBukkitFetcher.CRAFTBUKKIT_PACKAGE + ".CraftServer").getDeclaredField("commandMap");
            cmap.setAccessible(true);
            this.commands = (CommandMap) cmap.get(Bukkit.getServer());
        } catch (Exception e)
        {
            Gunsmith.getLogger().error(e, "Error setting up bukkit command registrar");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerCommand(Command cmd)
    {
        for (String alias : cmd.getAllAliases())
        {
            BukkitCommand bcmd = new BukkitCommand(alias, cmd);
            commands.register("voxelsniper", bcmd);
        }
    }

}
