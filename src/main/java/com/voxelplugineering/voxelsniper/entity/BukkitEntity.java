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

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Entity;

import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.entity.EntityType;
import com.voxelplugineering.voxelsniper.api.world.Location;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.util.BukkitUtilities;

/**
 * Represents a Bukkit entity.
 */
public class BukkitEntity extends AbstractEntity<Entity>
{

    private static final Map<org.bukkit.entity.EntityType, EntityType> entityTypes = new EnumMap<org.bukkit.entity.EntityType, EntityType>(
            org.bukkit.entity.EntityType.class);

    /**
     * Returns the Gunsmith {@link EntityType} for the given bukkit EntityType.
     * 
     * @param type The bukkit entity type
     * @return The gunsmith entity type
     */
    public static EntityType getEntityType(org.bukkit.entity.EntityType type)
    {
        EntityType gType;
        if (!entityTypes.containsKey(type))
        {
            gType = new BukkitEntityType(type);
            entityTypes.put(type, gType);
        } else
        {
            gType = entityTypes.get(type);
        }
        return gType;
    }

    private final EntityType type;

    /**
     * Creates a new entity wrapper.
     *
     * @param entity The entity to wrap
     */
    public BukkitEntity(Entity entity)
    {
        super(entity);
        this.type = getEntityType(entity.getType());
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
        return this.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getLocation()
    {
        return BukkitUtilities.getGunsmithLocation(getThis().getLocation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocation(Location newLocation)
    {
        getThis().teleport(BukkitUtilities.getBukkitLocation(newLocation));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getUniqueId()
    {
        return getThis().getUniqueId();
    }
}
