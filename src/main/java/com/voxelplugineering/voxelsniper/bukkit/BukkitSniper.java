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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.common.CommonLocation;
import com.voxelplugineering.voxelsniper.common.CommonPlayer;
import com.voxelplugineering.voxelsniper.common.CommonWorld;
import com.voxelplugineering.voxelsniper.common.FileBrushLoader;
import com.voxelplugineering.voxelsniper.world.ChangeQueue;

/**
 * A wrapper for bukkit's {@link Player}s.
 */
public class BukkitSniper extends CommonPlayer<Player>
{

    /**
     * Creates a new {@link BukkitSniper}.
     * 
     * @param player the player to wrap, cannot be null
     */
    public BukkitSniper(Player player)
    {
        super(player);
        //TODO: Change the call to getDataFolder() to a configuration value for the Gunsmith folder
        File personalFolder = new File(((JavaPlugin) Gunsmith.getVoxelSniper()).getDataFolder(), "brushes" + File.separator + this.getName());
        this.getPersonalBrushManager().addLoader(new FileBrushLoader(personalFolder));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return getPlayerReference().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String msg)
    {
        getPlayerReference().sendMessage(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonLocation getLocation()
    {
        Location location = getPlayerReference().getLocation();
        CommonWorld world = Gunsmith.getWorldFactory().getWorld(location.getWorld().getName());
        return new CommonLocation(world, location.getX(), location.getY(), location.getZ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonWorld getWorld()
    {
        return Gunsmith.getWorldFactory().getWorld(getPlayerReference().getWorld().getName());
    }
    
}
