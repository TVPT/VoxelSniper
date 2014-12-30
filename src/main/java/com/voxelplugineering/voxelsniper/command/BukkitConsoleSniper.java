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

import static com.google.common.base.Preconditions.checkNotNull;

import org.bukkit.command.CommandSender;

import com.voxelplugineering.voxelsniper.api.entity.living.Player;

/**
 * A stripped out {@link Player} implementation to act as a proxy for the console.
 */
public class BukkitConsoleSniper implements com.voxelplugineering.voxelsniper.api.commands.CommandSender
{

    /**
     * The console's bukkit {@link CommandSender}.
     */
    CommandSender console;

    /**
     * Creates a new console proxy wrapping the given {@link CommandSender}.
     * 
     * @param console the console, cannot be null
     */
    public BukkitConsoleSniper(CommandSender console)
    {
        checkNotNull(console, "Console cannot be null");
        this.console = console;
    }

    @Override
    public void sendMessage(String msg)
    {
        for(String message: msg.split("\n"))
        {
            this.console.sendMessage(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sendMessage(String format, Object... args)
    {
        sendMessage(String.format(format, args));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPlayer()
    {
        return false;
    }

}
