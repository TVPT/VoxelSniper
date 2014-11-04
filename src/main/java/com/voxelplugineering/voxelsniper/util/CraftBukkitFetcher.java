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
package com.voxelplugineering.voxelsniper.util;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

/**
 * A utility class to fetch the current craftbukkit package for this version.
 * 
 * TODO: replace with a cleaner way, here as a placeholder
 * 
 * @author Deamon
 */
public class CraftBukkitFetcher
{

    /**
     * The current craftbukkit package.
     */
    public static String    CRAFTBUKKIT_PACKAGE;

    static
    {
        CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit";
        Pattern pattern = Pattern.compile("v[0-9]+_[0-9]+_R?[0-9]+");
        try
        {
            Method getPackages = Bukkit.class.getClassLoader().getClass().getMethod("getPackages");
            getPackages.setAccessible(true);
            Object o = getPackages.invoke(Bukkit.class.getClassLoader());
            Package[] packages = (Package[]) o;
            for (Package p: packages)
            {
                if (p.getName().startsWith("org.bukkit.craftbukkit."))
                {
                    String sub = p.getName().substring(23);
                    Matcher match = pattern.matcher(sub);
                    if (match.find())
                    {
                        CRAFTBUKKIT_PACKAGE = p.getName();
                        System.out.println("Located craftbukkit package as " + CRAFTBUKKIT_PACKAGE);
                        break;
                    }
                }
            }
        } catch (Exception e)
        {
            System.out.println("Error determining CraftBukkit package name: " + e.getMessage());
        }
    }
}
