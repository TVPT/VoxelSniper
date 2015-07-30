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

import org.spongepowered.api.data.manipulator.DisplayNameData;
import org.spongepowered.api.text.Texts;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.entity.AbstractEntity;
import com.voxelplugineering.voxelsniper.entity.EntityType;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.sponge.util.SpongeUtilities;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.math.Vector3d;
import com.voxelplugineering.voxelsniper.world.Location;
import com.voxelplugineering.voxelsniper.world.World;

/**
 * A wrapper for a sponge entity.
 */
public class SpongeEntity extends AbstractEntity<org.spongepowered.api.entity.Entity>
{

    private final WorldRegistry<org.spongepowered.api.world.World> worlds;
    private final EntityType type;

    /**
     * Creates a new {@link SpongeEntity}.
     * 
     * @param entity The entity to wrap
     */
    @SuppressWarnings("unchecked")
    public SpongeEntity(Context context, org.spongepowered.api.entity.Entity entity)
    {
        super(entity);
        this.worlds = context.getRequired(WorldRegistry.class);
        this.type = SpongeUtilities.getEntityType(entity.getClass());
    }

    @Override
    public World getWorld()
    {
        return this.worlds.getWorld(getThis().getWorld().getName()).get();
    }

    @Override
    public String getName()
    {
        if (getThis() instanceof org.spongepowered.api.entity.player.Player)
        {
            return ((org.spongepowered.api.entity.player.Player) getThis()).getName();
        }
        Optional<DisplayNameData> name = getThis().getData(DisplayNameData.class);
        if (name.isPresent())
        {
            return Texts.toPlain(name.get().getDisplayName());
        }
        return this.type.getName();
    }

    @Override
    public EntityType getType()
    {
        return this.type;
    }

    @Override
    public Location getLocation()
    {
        // TODO change to exception on fail
        return SpongeUtilities.fromSpongeLocation(getThis().getLocation(), this.worlds).orNull();
    }

    @Override
    public void setLocation(Location loc)
    {
        getThis().setLocation(SpongeUtilities.getSpongeLocation(loc));
    }

    @Override
    public UUID getUniqueId()
    {
        return getThis().getUniqueId();
    }

    @Override
    public Vector3d getRotation()
    {
        return SpongeUtilities.getGunsmithVector(getThis().getRotation());
    }

    @Override
    public void setRotation(Vector3d rotation)
    {
        getThis().setRotation(SpongeUtilities.getSpongeVector(rotation));
    }

    @Override
    public boolean remove()
    {
        getThis().remove();
        return true;
    }

    @Override
    public double getYaw()
    {
        return getThis().getRotation().getY();
    }

    @Override
    public double getPitch()
    {
        return getThis().getRotation().getX();
    }

    @Override
    public double getRoll()
    {
        return getThis().getRotation().getZ();
    }
}
