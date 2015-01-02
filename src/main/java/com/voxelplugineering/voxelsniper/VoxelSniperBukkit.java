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
package com.voxelplugineering.voxelsniper;

import org.bukkit.plugin.java.JavaPlugin;

import com.voxelplugineering.voxelsniper.util.vsl.DefaultBrushBuilder;

/**
 * The Main class for the bukkit specific implementation.
 */
public class VoxelSniperBukkit extends JavaPlugin
{

    /**
     * The main plugin instance.
     */
    public static VoxelSniperBukkit voxelsniper;

    /**
     * The main enabling sequence.
     */
    @Override
    public void onEnable()
    {
        Gunsmith.beginInit(getDataFolder(), new BukkitPlatformProvider(this));

        DefaultBrushBuilder.buildBrushes();
        DefaultBrushBuilder.loadAll(Gunsmith.getGlobalBrushManager());
    }

    /**
     * The disabling sequence
     */
    @Override
    public void onDisable()
    {
        if (Gunsmith.isEnabled())
        {
            Gunsmith.stop();
        }
    }

}
