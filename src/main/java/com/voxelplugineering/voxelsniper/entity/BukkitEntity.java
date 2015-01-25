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
package com.voxelplugineering.voxelsniper.entity;

import org.bukkit.entity.Entity;

import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.entity.EntityType;
import com.voxelplugineering.voxelsniper.api.world.Location;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.util.BukkitUtility;

/**
 * Represents a Bukkit entity.
 */
public class BukkitEntity extends AbstractEntity<Entity>
{
    /**
     * Creates a new entity wrapper.
     *
     * @param value The entity to wrap
     */
    public BukkitEntity(Entity value)
    {
        super(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public World getWorld()
    {
        return Gunsmith.getWorldRegistry().getWorld(getThis().getWorld().getName()).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return getThis().getCustomName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getType()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getLocation()
    {
        return BukkitUtility.getGunsmithLocation(getThis().getLocation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocation(Location newLocation)
    {
        getThis().teleport(BukkitUtility.getBukkitLocation(newLocation));
    }
}
