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
package com.voxelplugineering.voxelsniper.util.text;

import org.bukkit.ChatColor;

import com.voxelplugineering.voxelsniper.api.util.text.TextFormat;
import com.voxelplugineering.voxelsniper.api.util.text.TextFormatProxy;

/**
 * A proxy for bukkit's formatting codes.
 */
public class BukkitTextFormatProxy implements TextFormatProxy
{

    @Override
    public String getFormat(TextFormat format)
    {
        switch(format)
        {
        case BLACK:
            return ChatColor.BLACK.toString();
        case DARK_BLUE:
            return ChatColor.DARK_BLUE.toString();
        case DARK_GREEN:
            return ChatColor.DARK_GREEN.toString();
        case DARK_AQUA:
            return ChatColor.DARK_AQUA.toString();
        case DARK_RED:
            return ChatColor.DARK_RED.toString();
        case DARK_PURPLE:
            return ChatColor.DARK_PURPLE.toString();
        case GOLD:
            return ChatColor.GOLD.toString();
        case GRAY:
            return ChatColor.GRAY.toString();
        case DARK_GRAY:
            return ChatColor.DARK_GRAY.toString();
        case BLUE:
            return ChatColor.BLUE.toString();
        case GREEN:
            return ChatColor.GREEN.toString();
        case AQUA:
            return ChatColor.AQUA.toString();
        case RED:
            return ChatColor.RED.toString();
        case LIGHT_PURPLE:
            return ChatColor.LIGHT_PURPLE.toString();
        case YELLOW:
            return ChatColor.YELLOW.toString();
        case WHITE:
            return ChatColor.WHITE.toString();
        case BOLD:
            return ChatColor.BOLD.toString();
        case STRIKETHROUGH:
            return ChatColor.STRIKETHROUGH.toString();
        case UNDERLINE:
            return ChatColor.UNDERLINE.toString();
        case ITALIC:
            return ChatColor.ITALIC.toString();
        default:
            return ChatColor.RESET.toString();
        }
    }

}
