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

import java.util.UUID;

import com.voxelplugineering.voxelsniper.brush.BrushManager;
import com.voxelplugineering.voxelsniper.bukkit.util.BukkitUtilities;
import com.voxelplugineering.voxelsniper.entity.AbstractPlayer;
import com.voxelplugineering.voxelsniper.entity.EntityType;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.math.Vector3d;
import com.voxelplugineering.voxelsniper.world.World;

/**
 * A wrapper for bukkit's {@link org.bukkit.entity.Player}s.
 */
public class BukkitPlayer extends AbstractPlayer<org.bukkit.entity.Player>
{

    private final WorldRegistry<org.bukkit.World> worldReg;

    /**
     * Creates a new {@link BukkitPlayer}.
     * 
     * @param player the player to wrap, cannot be null
     */
    @SuppressWarnings({ "unchecked" })
    public BukkitPlayer(org.bukkit.entity.Player player, BrushManager bm, Context context)
    {
        super(player, bm, context);
        this.worldReg = context.getRequired(WorldRegistry.class);
        // TODO persistence
        // File personalFolder = new File(Gunsmith.getDataFolder(), "brushes" +
        // File.separator + this.getName());
        // this.getPersonalBrushManager().addLoader(new
        // DirectoryDataSourceProvider(personalFolder, NBTDataSource.BUILDER));
    }

    @Override
    public void sendMessage(String msg)
    {
        for (String message : msg.split("\n"))
        {
            getThis().sendMessage(message);
        }
    }

    @Override
    public World getWorld()
    {
        return this.worldReg.getWorld(getThis().getWorld().getName()).get();
    }

    @Override
    public String getName()
    {
        return getThis().getName();
    }

    @Override
    public EntityType getType()
    {
        return BukkitUtilities.getEntityType(org.bukkit.entity.EntityType.PLAYER);
    }

    @Override
    public com.voxelplugineering.voxelsniper.world.Location getLocation()
    {
        return BukkitUtilities.getGunsmithLocation(getThis().getLocation(), this.worldReg);
    }

    @Override
    public void setLocation(com.voxelplugineering.voxelsniper.world.Location newLocation)
    {
        getThis().teleport(BukkitUtilities.getBukkitLocation(newLocation));
    }

    @Override
    public double getHealth()
    {
        return getThis().getHealth();
    }

    @Override
    public UUID getUniqueId()
    {
        return getThis().getUniqueId();
    }

    @Override
    public void setHealth(double health)
    {
        getThis().setHealth(health);
    }

    @Override
    public double getMaxHealth()
    {
        return getThis().getMaxHealth();
    }

    @Override
    public Vector3d getRotation()
    {
        org.bukkit.Location location = getThis().getLocation();
        return new Vector3d(location.getYaw(), location.getPitch(), 0);
    }

    @Override
    public void setRotation(Vector3d rotation)
    {
        getThis().getLocation().setYaw((float) rotation.getX());
        getThis().getLocation().setYaw((float) rotation.getY());
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
        return 0;
    }

}
