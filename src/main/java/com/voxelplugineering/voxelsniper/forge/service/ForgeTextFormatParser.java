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
package com.voxelplugineering.voxelsniper.forge.service;

import com.voxelplugineering.voxelsniper.api.util.text.TextFormat;
import com.voxelplugineering.voxelsniper.api.util.text.TextFormatParser;
import com.voxelplugineering.voxelsniper.core.Gunsmith;
import com.voxelplugineering.voxelsniper.core.service.AbstractService;

/**
 * A converter for standard minecraft colour/formatting codes.
 */
public class ForgeTextFormatParser extends AbstractService implements TextFormatParser
{

    /**
     * Creates a new {@link ForgeTextFormatParser}.
     */
    public ForgeTextFormatParser()
    {
        super(TextFormatParser.class, 0);
    }

    @Override
    public String getFormat(TextFormat format)
    {
        switch (format)
        {
        case BLACK:
            return "&0";
        case DARK_BLUE:
            return "&1";
        case DARK_GREEN:
            return "&2";
        case DARK_AQUA:
            return "&3";
        case DARK_RED:
            return "&4";
        case DARK_PURPLE:
            return "&5";
        case GOLD:
            return "&6";
        case GRAY:
            return "&7";
        case DARK_GRAY:
            return "&8";
        case BLUE:
            return "&9";
        case GREEN:
            return "&a";
        case AQUA:
            return "&0b";
        case RED:
            return "&c";
        case LIGHT_PURPLE:
            return "&d";
        case YELLOW:
            return "&e";
        case WHITE:
            return "&f";
        case BOLD:
            return "&l";
        case STRIKETHROUGH:
            return "&m";
        case UNDERLINE:
            return "&n";
        case ITALIC:
            return "&o";
        case RESET:
            return "&r";
        default:
            return "&k";
        }
    }

    @Override
    public String getName()
    {
        return "formatProxy";
    }

    @Override
    protected void init()
    {
        Gunsmith.getLogger().info("Initialized ForgeTextFormat service");
    }

    @Override
    protected void destroy()
    {
        Gunsmith.getLogger().info("Stopped ForgeTextFormat service");
    }

}
