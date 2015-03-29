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

import org.spongepowered.api.text.Texts;

import com.voxelplugineering.voxelsniper.api.entity.EntityType;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.core.Gunsmith;
import com.voxelplugineering.voxelsniper.core.entity.AbstractPlayer;
import com.voxelplugineering.voxelsniper.core.util.math.Vector3d;
import com.voxelplugineering.voxelsniper.core.world.CommonLocation;
import com.voxelplugineering.voxelsniper.sponge.util.SpongeUtilities;

/**
 * Wraps a {@link org.spongepowered.api.entity.player.Player}.
 */
public class SpongePlayer extends AbstractPlayer<org.spongepowered.api.entity.player.Player>
{

    private static final EntityType PLAYER_TYPE = SpongeUtilities.getEntityType(org.spongepowered.api.entity.living.Living.class);

    /**
     * Creates a new {@link SpongePlayer}.
     * 
     * @param player The player to wrap
     */
    public SpongePlayer(org.spongepowered.api.entity.player.Player player)
    {
        super(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPlayer()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String msg)
    {
        getThis().sendMessage(Texts.of(msg));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String format, Object... args)
    {
        sendMessage(String.format(format, args));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getHealth()
    {
        return getThis().getHealth();
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
        return getThis().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getType()
    {
        return PLAYER_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public com.voxelplugineering.voxelsniper.api.world.Location getLocation()
    {
        com.flowpowered.math.vector.Vector3d position = getThis().getLocation().getPosition();
        return new CommonLocation(getWorld(), position.getX(), position.getY(), position.getZ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocation(com.voxelplugineering.voxelsniper.api.world.Location newLocation)
    {
        getThis().setLocation(SpongeUtilities.getSpongeLocation(newLocation));
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
    public void setHealth(double health)
    {
        getThis().setHealth(health);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxHealth()
    {
        return getThis().getMaxHealth();
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
        throw new UnsupportedOperationException("Cannot remove player entities");
    }

}
