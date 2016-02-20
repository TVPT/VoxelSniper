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
package com.voxelplugineering.voxelsniper.bukkit.entity;

import com.voxelplugineering.voxelsniper.bukkit.util.BukkitUtilities;
import com.voxelplugineering.voxelsniper.bukkit.world.BukkitWorld;
import com.voxelplugineering.voxelsniper.entity.AbstractEntity;
import com.voxelplugineering.voxelsniper.entity.EntityType;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.util.math.Vector3d;
import com.voxelplugineering.voxelsniper.world.Location;
import com.voxelplugineering.voxelsniper.world.World;

import java.util.UUID;

/**
 * Represents a Bukkit entity.
 */
public class BukkitEntity extends AbstractEntity<org.bukkit.entity.Entity>
{

    private final EntityType type;
    private final WorldRegistry<org.bukkit.World> worldReg;

    /**
     * Creates a new entity wrapper.
     *
     * @param entity The entity to wrap
     */
    public BukkitEntity(org.bukkit.entity.Entity entity, WorldRegistry<org.bukkit.World> worldReg)
    {
        super(entity);
        this.type = BukkitUtilities.getEntityType(entity.getType());
        this.worldReg = worldReg;
    }

    @Override
    public World getWorld()
    {
        return this.worldReg.getWorld(getThis().getWorld().getName()).get();
    }

    @Override
    public String getName()
    {
        return getThis().getCustomName();
    }

    @Override
    public EntityType getType()
    {
        return this.type;
    }

    @Override
    public Location getLocation()
    {
        return BukkitUtilities.getGunsmithLocation(getThis().getLocation(), this.worldReg);
    }

    @Override
    public void setLocation(World world, double x, double y, double z)
    {
        getThis().teleport(new org.bukkit.Location(((BukkitWorld) world).getThis(), x, y, z));
    }

    @Override
    public UUID getUniqueId()
    {
        return getThis().getUniqueId();
    }

    @Override
    public Vector3d getRotation()
    {
        org.bukkit.Location location = getThis().getLocation();
        return new Vector3d(location.getPitch(), location.getYaw(), 0);
    }

    @Override
    public void setRotation(double pitch, double yaw, double roll)
    {
        getThis().getLocation().setYaw((float) yaw);
        getThis().getLocation().setPitch((float) pitch);
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
        return getThis().getLocation().getYaw();
    }

    @Override
    public double getPitch()
    {
        return getThis().getLocation().getPitch();
    }

    @Override
    public double getRoll()
    {
        return 0;// TODO where does bukkit expose armour stands...or do they not
    }
}
