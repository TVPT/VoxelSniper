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
package com.voxelplugineering.voxelsniper.forge.entity;

import java.util.UUID;

import com.voxelplugineering.voxelsniper.entity.AbstractPlayer;
import com.voxelplugineering.voxelsniper.entity.EntityType;
import com.voxelplugineering.voxelsniper.forge.util.ForgeUtilities;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.service.text.TextFormat;
import com.voxelplugineering.voxelsniper.service.text.TextFormatParser;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.math.Vector3d;
import com.voxelplugineering.voxelsniper.world.CommonLocation;
import com.voxelplugineering.voxelsniper.world.Location;
import com.voxelplugineering.voxelsniper.world.World;

/**
 * A wrapper for forge's {@link net.minecraft.entity.player.EntityPlayer}.
 */
public class ForgePlayer extends AbstractPlayer<net.minecraft.entity.player.EntityPlayer>
{

    private static final int MAX_MESSAGE_LENGTH = 32768;
    private static final EntityType PLAYER_TYPE = ForgeUtilities.getEntityType(net.minecraft.entity.player.EntityPlayer.class);

    private final WorldRegistry<org.bukkit.World> worldReg;
    private final TextFormatParser textFormat;

    /**
     * Creates a new {@link ForgePlayer}.
     * 
     * @param player The player to wrap
     */
    @SuppressWarnings("unchecked")
    public ForgePlayer(net.minecraft.entity.player.EntityPlayer player, Context context)
    {
        super(player, context);
        this.worldReg = context.getRequired(WorldRegistry.class);
        this.textFormat = context.getRequired(TextFormatParser.class);
    }

    @Override
    public String getName()
    {
        return getThis().getName();
    }

    @Override
    public void sendMessage(String msg)
    {
        if (msg.indexOf('\n') != -1)
        {
            for (String message : msg.split("\n"))
            {
                sendMessage(message);
            }
            return;
        }
        if (msg.length() > MAX_MESSAGE_LENGTH)
        {
            sendMessage(msg.substring(0, MAX_MESSAGE_LENGTH));
            sendMessage(msg.substring(MAX_MESSAGE_LENGTH));
            return;
        }
        getThis().addChatMessage(new net.minecraft.util.ChatComponentText(formatMessage(msg)));
    }

    private String formatMessage(String msg)
    {
        for (TextFormat format : TextFormat.values())
        {
            msg = msg.replaceAll(format.toString(), this.textFormat.getFormat(format));
        }
        return msg;
    }

    @Override
    public void sendMessage(String format, Object... args)
    {
        sendMessage(String.format(format, args));
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

    @Override
    public World getWorld()
    {
        return this.worldReg.getWorld(getThis().worldObj.getWorldInfo().getWorldName()).orElse(null);
    }

    @Override
    public Location getLocation()
    {
        return new CommonLocation(this.getWorld(), getThis().posX, getThis().posY, getThis().posZ);
    }

    @Override
    public void setLocation(World world, double x, double y, double z)
    {
        if (!world.equals(this.getWorld()))
        {
            getThis().travelToDimension(getThis().worldObj.provider.getDimensionId());
        }
        getThis().setPositionAndUpdate(x, y, z);
    }

    @Override
    public UUID getUniqueId()
    {
        return getThis().getUniqueID();
    }

    @Override
    public EntityType getType()
    {
        return PLAYER_TYPE;
    }

    @Override
    public void setHealth(double health)
    {
        getThis().setHealth((float) health);
    }

    @Override
    public double getMaxHealth()
    {
        return getThis().getMaxHealth();
    }

    @Override
    public Vector3d getRotation()
    {
        return new Vector3d(getThis().rotationYaw, getThis().rotationPitch, 0);
    }

    @Override
    public void setRotation(double pitch, double yaw, double roll)
    {
        getThis().setRotationYawHead((float) yaw);
        getThis().rotationPitch = (float) pitch;
    }

    @Override
    public boolean remove()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getYaw()
    {
        return getThis().rotationYaw;
    }

    @Override
    public double getPitch()
    {
        return getThis().rotationPitch;
    }

    @Override
    public double getRoll()
    {
        return 0;
    }
}
