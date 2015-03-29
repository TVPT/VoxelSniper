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

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.MapMaker;
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.entity.EntityType;
import com.voxelplugineering.voxelsniper.api.world.Location;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.entity.SpongeEntityType;
import com.voxelplugineering.voxelsniper.util.math.Vector3d;
import com.voxelplugineering.voxelsniper.world.CommonLocation;
import com.voxelplugineering.voxelsniper.world.SpongeWorld;

/**
 * A set of utilities for the sponge implementation.
 */
public class SpongeUtilities
{

    private static final Map<Class<? extends org.spongepowered.api.entity.Entity>, SpongeEntityType> entityTypeCache;

    static
    {
        entityTypeCache = new MapMaker().weakKeys().makeMap();
    }

    /**
     * Converts a {@link com.voxelplugineering.voxelsniper.api.world.Location}
     * into a {@link org.spongepowered.api.world.Location}.
     * 
     * @param location The location to convert
     * @return The new location
     */
    public static org.spongepowered.api.world.Location getSpongeLocation(Location location)
    {
        return new org.spongepowered.api.world.Location(((SpongeWorld) location.getWorld()).getThis(), getSpongeVector(location.toVector()));
    }

    /**
     * Converts a {@link com.voxelplugineering.voxelsniper.util.math.Vector3d}
     * into a {@link com.flowpowered.math.vector.Vector3d}.
     * 
     * @param vector The vector to convert
     * @return The new vector
     */
    public static com.flowpowered.math.vector.Vector3d getSpongeVector(Vector3d vector)
    {
        return new com.flowpowered.math.vector.Vector3d(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Converts a {@link com.flowpowered.math.vector.Vector3d} into a
     * {@link com.voxelplugineering.voxelsniper.util.math.Vector3d}.
     * 
     * @param vector The vector to convert
     * @return The new vector
     */

    public static Vector3d getGunsmithVector(com.flowpowered.math.vector.Vector3d vector)
    {
        return new Vector3d(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Gets the Gunsmith {@link EntityType} corresponding to the given entity
     * class.
     * 
     * @param cls The entity class
     * @return The gunsmith entity type
     */
    public static EntityType getEntityType(Class<? extends org.spongepowered.api.entity.Entity> cls)
    {
        if (entityTypeCache.containsKey(cls))
        {
            return entityTypeCache.get(cls);
        } else
        {
            SpongeEntityType type = new SpongeEntityType(cls);
            entityTypeCache.put(cls, type);
            return type;
        }
    }

    /**
     * Gets an instance of a Gunsmith {@link Location} corresponding to the
     * given sponge location.
     * 
     * @param location The location
     * @return The gunsmith location
     */
    public static Optional<Location> fromSpongeLocation(org.spongepowered.api.world.Location location)
    {
        if (location.getExtent() instanceof World)
        {
            Optional<World> world = Gunsmith.getWorldRegistry().getWorld(((org.spongepowered.api.world.World) location.getExtent()).getName());
            if (!world.isPresent())
            {
                return Optional.absent();
            }
            com.flowpowered.math.vector.Vector3d position = location.getPosition();
            return Optional.<Location>of(new CommonLocation(world.get(), position.getX(), position.getY(), position.getZ()));
        }
        return Optional.absent();
    }

}
