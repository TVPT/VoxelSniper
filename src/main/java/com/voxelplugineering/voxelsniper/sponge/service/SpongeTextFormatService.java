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
package com.voxelplugineering.voxelsniper.sponge.service;

import com.voxelplugineering.voxelsniper.service.AbstractService;
import com.voxelplugineering.voxelsniper.service.text.TextFormat;
import com.voxelplugineering.voxelsniper.service.text.TextFormatParser;
import com.voxelplugineering.voxelsniper.util.Context;

/**
 * A converter for text formatting codes.
 */
public class SpongeTextFormatService extends AbstractService implements TextFormatParser
{

    /**
     * Creates a new {@link SpongeTextFormatService}.
     */
    public SpongeTextFormatService(Context context)
    {
        super(context);
    }

    @Override
    public String getFormat(TextFormat format)
    {

        switch (format)
        {
        case BLACK:
            return "\u00A70";
        case DARK_BLUE:
            return "\u00A71";
        case DARK_GREEN:
            return "\u00A72";
        case DARK_AQUA:
            return "\u00A73";
        case DARK_RED:
            return "\u00A74";
        case DARK_PURPLE:
            return "\u00A75";
        case GOLD:
            return "\u00A76";
        case GRAY:
            return "\u00A77";
        case DARK_GRAY:
            return "\u00A78";
        case BLUE:
            return "\u00A79";
        case GREEN:
            return "\u00A7a";
        case AQUA:
            return "\u00A70b";
        case RED:
            return "\u00A7c";
        case LIGHT_PURPLE:
            return "\u00A7d";
        case YELLOW:
            return "\u00A7e";
        case WHITE:
            return "\u00A7f";
        case BOLD:
            return "\u00A7l";
        case STRIKETHROUGH:
            return "\u00A7m";
        case UNDERLINE:
            return "\u00A7n";
        case ITALIC:
            return "\u00A7o";
        case RESET:
            return "\u00A7r";
        default:
            return "\u00A7k";
        }
    }

    @Override
    protected void _init()
    {
    }

    @Override
    protected void _shutdown()
    {
    }

}
