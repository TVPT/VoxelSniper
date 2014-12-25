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
package com.voxelplugineering.voxelsniper.entity.living;

import java.io.File;

import org.bukkit.entity.Player;

import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.entity.EntityType;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.brushes.FileBrushLoader;
import com.voxelplugineering.voxelsniper.util.BukkitUtility;

/**
 * A wrapper for bukkit's {@link Player}s.
 */
public class BukkitPlayer extends AbstractPlayer<Player>
{

    /**
     * Creates a new {@link BukkitPlayer}.
     * 
     * @param player the player to wrap, cannot be null
     */
    public BukkitPlayer(Player player)
    {
        super(player);
        //TODO: Change the call to getDataFolder() to a configuration value for the Gunsmith folder
        File personalFolder = new File(Gunsmith.getDataFolder(), "brushes" + File.separator + this.getName());
        this.getPersonalBrushManager().addLoader(new FileBrushLoader(personalFolder));
    }

    @Override
    public void sendMessage(String msg)
    {
        getThis().sendMessage(msg);
    }

    @Override
    public void sendMessage(String format, Object... args)
    {
        sendMessage(String.format(format, args));
    }

    @Override
    public World getWorld()
    {
        return Gunsmith.getWorldRegistry().getWorld(getThis().getWorld().getName()).get();
    }

    @Override
    public String getName()
    {
        return getThis().getName();
    }

    @Override
    public EntityType getType()
    {
        return null;
    }

    @Override
    public com.voxelplugineering.voxelsniper.api.world.Location getLocation()
    {
        return BukkitUtility.getGunsmithLocation(getThis().getLocation());
    }

    @Override
    public void setLocation(com.voxelplugineering.voxelsniper.api.world.Location newLocation)
    {
        getThis().teleport(BukkitUtility.getBukkitLocation(newLocation));
    }

    @Override
    public boolean isPlayer()
    {
        return true;
    }

    @Override
    public double getHealth()
    {
        return getThis().getHealth();
    }

}
