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
package com.voxelplugineering.voxelsniper.bukkit;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.common.CommonLocation;
import com.voxelplugineering.voxelsniper.common.CommonPlayer;
import com.voxelplugineering.voxelsniper.common.FileBrushLoader;

public class BukkitSniper extends CommonPlayer<Player>
{

    public BukkitSniper(Player player)
    {
        super(player);
        this.getPersonalBrushManager().addLoader(
                new FileBrushLoader(new File(((JavaPlugin) Gunsmith.getVoxelSniper()).getDataFolder(), "brushes" + File.separator + this.getName())));
    }

    @Override
    public String getName()
    {
        return getPlayerReference().getName();
    }

    @Override
    public void sendMessage(String msg)
    {
        getPlayerReference().sendMessage(msg);
    }

    @Override
    public CommonLocation getLocation()
    {
        return new CommonLocation(Gunsmith.getWorldFactory().getWorld(getPlayerReference().getLocation().getWorld().getName()), getPlayerReference()
                .getLocation().getX(), getPlayerReference().getLocation().getY(), getPlayerReference().getLocation().getZ());
    }

}
