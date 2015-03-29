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
package com.voxelplugineering.voxelsniper.bukkit.service;

import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.util.text.TextFormat;
import com.voxelplugineering.voxelsniper.api.util.text.TextFormatParser;
import com.voxelplugineering.voxelsniper.service.AbstractService;

/**
 * A proxy for bukkit's formatting codes.
 */
public class BukkitTextFormatParser extends AbstractService implements TextFormatParser
{

    /**
     * Creates a new {@link BukkitTextFormatParser}.
     */
    public BukkitTextFormatParser()
    {
        super(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormat(TextFormat format)
    {
        switch (format)
        {
        case BLACK:
            return org.bukkit.ChatColor.BLACK.toString();
        case DARK_BLUE:
            return org.bukkit.ChatColor.DARK_BLUE.toString();
        case DARK_GREEN:
            return org.bukkit.ChatColor.DARK_GREEN.toString();
        case DARK_AQUA:
            return org.bukkit.ChatColor.DARK_AQUA.toString();
        case DARK_RED:
            return org.bukkit.ChatColor.DARK_RED.toString();
        case DARK_PURPLE:
            return org.bukkit.ChatColor.DARK_PURPLE.toString();
        case GOLD:
            return org.bukkit.ChatColor.GOLD.toString();
        case GRAY:
            return org.bukkit.ChatColor.GRAY.toString();
        case DARK_GRAY:
            return org.bukkit.ChatColor.DARK_GRAY.toString();
        case BLUE:
            return org.bukkit.ChatColor.BLUE.toString();
        case GREEN:
            return org.bukkit.ChatColor.GREEN.toString();
        case AQUA:
            return org.bukkit.ChatColor.AQUA.toString();
        case RED:
            return org.bukkit.ChatColor.RED.toString();
        case LIGHT_PURPLE:
            return org.bukkit.ChatColor.LIGHT_PURPLE.toString();
        case YELLOW:
            return org.bukkit.ChatColor.YELLOW.toString();
        case WHITE:
            return org.bukkit.ChatColor.WHITE.toString();
        case BOLD:
            return org.bukkit.ChatColor.BOLD.toString();
        case STRIKETHROUGH:
            return org.bukkit.ChatColor.STRIKETHROUGH.toString();
        case UNDERLINE:
            return org.bukkit.ChatColor.UNDERLINE.toString();
        case ITALIC:
            return org.bukkit.ChatColor.ITALIC.toString();
        default:
            return org.bukkit.ChatColor.RESET.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return "formatProxy";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        Gunsmith.getLogger().info("Initialized BukkitTextFormat service");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void destroy()
    {
        Gunsmith.getLogger().info("Stopped BukkitTextFormat service");
    }

}
