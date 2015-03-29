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
package com.voxelplugineering.voxelsniper.sponge.entity;

import java.util.UUID;

import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.entity.EntityType;
import com.voxelplugineering.voxelsniper.api.world.Location;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.entity.AbstractEntity;
import com.voxelplugineering.voxelsniper.sponge.util.SpongeUtilities;
import com.voxelplugineering.voxelsniper.util.math.Vector3d;

/**
 * A wrapper for a sponge entity.
 */
public class SpongeEntity extends AbstractEntity<org.spongepowered.api.entity.Entity>
{

    private final EntityType type;

    /**
     * Creates a new {@link SpongeEntity}.
     * 
     * @param entity The entity to wrap
     */
    public SpongeEntity(org.spongepowered.api.entity.Entity entity)
    {
        super(entity);
        this.type = SpongeUtilities.getEntityType(entity.getClass());
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
        if (getThis() instanceof org.spongepowered.api.entity.player.Player)
        {
            return ((org.spongepowered.api.entity.player.Player) getThis()).getName();
        } else if (getThis() instanceof org.spongepowered.api.entity.living.Agent)
        {
            return ((org.spongepowered.api.entity.living.Agent) getThis()).getCustomName();
        }
        return this.type.getName();
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
        return SpongeUtilities.fromSpongeLocation(getThis().getLocation()).orNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocation(Location loc)
    {
        getThis().setLocation(SpongeUtilities.getSpongeLocation(loc));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getUniqueId()
    {
        return getThis().getUniqueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector3d getRotation()
    {
        return SpongeUtilities.getGunsmithVector(getThis().getRotation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRotation(Vector3d rotation)
    {
        getThis().setRotation(SpongeUtilities.getSpongeVector(rotation));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove()
    {
        getThis().remove();
        return true;
    }
}
