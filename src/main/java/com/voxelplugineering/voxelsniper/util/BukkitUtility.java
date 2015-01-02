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
package com.voxelplugineering.voxelsniper.util;

import org.bukkit.Location;

import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.world.BukkitWorld;
import com.voxelplugineering.voxelsniper.world.CommonLocation;

/**
 * Bukkit based utilities.
 */
public final class BukkitUtility
{

    /**
     * Switches between Gunsmith's {@link CommonLocation} and bukkit's {@link Location}.
     * 
     * @param location Bukkit's location
     * @return Gunsmith's location
     */
    public static com.voxelplugineering.voxelsniper.api.world.Location getGunsmithLocation(Location location)
    {
        return new CommonLocation(Gunsmith.getWorldRegistry().getWorld(location.getWorld()).get(), location.getX(), location.getY(), location.getZ());
    }

    /**
     * Switches between Gunsmith's {@link CommonLocation} and bukkit's {@link Location}.
     * 
     * @param location Gunsmith's location
     * @return Bukkit's location
     */
    public static Location getBukkitLocation(com.voxelplugineering.voxelsniper.api.world.Location location)
    {
        if (location.getWorld() instanceof BukkitWorld)
        {
            return new Location(((BukkitWorld) location.getWorld()).getThis(), location.getX(), location.getY(), location.getZ());
        }
        return null;
    }

}
