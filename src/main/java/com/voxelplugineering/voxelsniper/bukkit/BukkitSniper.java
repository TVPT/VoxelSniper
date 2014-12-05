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

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.voxelplugineering.voxelsniper.api.IBrushLoader;
import com.voxelplugineering.voxelsniper.api.IBrushManager;
import com.voxelplugineering.voxelsniper.api.IRegistry;
import com.voxelplugineering.voxelsniper.common.CommonLocation;
import com.voxelplugineering.voxelsniper.common.CommonPlayer;
import com.voxelplugineering.voxelsniper.common.CommonWorld;
import com.voxelplugineering.voxelsniper.common.FileBrushLoader;

/**
 * A wrapper for bukkit's {@link Player}s.
 */
public class BukkitSniper extends CommonPlayer<Player>
{
    
    private IRegistry<World, BukkitWorld> worldRegistry;

    /**
     * Creates a new {@link BukkitSniper}.
     * 
     * @param player the player to wrap, cannot be null
     */
    public BukkitSniper(Player player, IRegistry<World, BukkitWorld> world, IBrushManager parentManager, IBrushLoader loader, int history, File dataFolder)
    {
        super(player, parentManager, loader, history);
        //TODO: Change the call to getDataFolder() to a configuration value for the Gunsmith folder
        File personalFolder = new File(dataFolder, "brushes" + File.separator + this.getName());
        this.getPersonalBrushManager().addLoader(new FileBrushLoader(personalFolder));
        this.worldRegistry = world;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return getThis().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String msg)
    {
        getThis().sendMessage(msg);
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
    public CommonLocation getLocation()
    {
        Location location = getThis().getLocation();
        CommonWorld<?> world = worldRegistry.get(location.getWorld());
        return new CommonLocation(world, location.getX(), location.getY(), location.getZ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonWorld<?> getWorld()
    {
        return worldRegistry.get(getThis().getWorld());
    }
    
}
