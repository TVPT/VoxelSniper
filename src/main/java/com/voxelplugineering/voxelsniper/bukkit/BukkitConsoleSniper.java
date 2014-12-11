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
package com.voxelplugineering.voxelsniper.bukkit;

import static com.google.common.base.Preconditions.checkNotNull;

import org.bukkit.command.CommandSender;

import com.google.common.base.Optional;
import com.thevoxelbox.vsl.api.IVariableScope;
import com.voxelplugineering.voxelsniper.api.IBrush;
import com.voxelplugineering.voxelsniper.api.IBrushManager;
import com.voxelplugineering.voxelsniper.api.ISniper;
import com.voxelplugineering.voxelsniper.common.CommonLocation;
import com.voxelplugineering.voxelsniper.common.CommonWorld;
import com.voxelplugineering.voxelsniper.world.ChangeQueue;

/**
 * A stripped out {@link ISniper} implementation to act as a proxy for the console.
 */
public class BukkitConsoleSniper implements ISniper
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return this.console.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String msg)
    {
        this.console.sendMessage(msg);
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
    public IBrushManager getPersonalBrushManager()
    {
        throw new UnsupportedOperationException("Console has no brush manager.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonLocation getLocation()
    {
        throw new UnsupportedOperationException("Console has no location.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentBrush(IBrush brush)
    {
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBrush getCurrentBrush()
    {
        throw new UnsupportedOperationException("Cannot get a brush from the console");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IVariableScope getBrushSettings()
    {
        throw new UnsupportedOperationException("Cannot get brush settings from the console");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undoHistory(int n)
    {
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonWorld<?> getWorld()
    {
        throw new UnsupportedOperationException("Console has no world");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPendingChanges()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ChangeQueue> getNextPendingChange()
    {
        throw new UnsupportedOperationException("Console has no change queue");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearNextPending()
    {
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHistory(ChangeQueue invert)
    {
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPending(ChangeQueue blockChangeQueue)
    {
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetSettings()
    {
        return;
    }

}
