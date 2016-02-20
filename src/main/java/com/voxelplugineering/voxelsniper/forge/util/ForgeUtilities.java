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
package com.voxelplugineering.voxelsniper.forge.util;

import com.google.common.collect.MapMaker;
import com.voxelplugineering.voxelsniper.entity.EntityType;
import com.voxelplugineering.voxelsniper.forge.entity.ForgeEntityType;

import java.util.Map;

/**
 * A set of utility functions for forge.
 */
public class ForgeUtilities
{

    private static final Map<Class<? extends net.minecraft.entity.Entity>, ForgeEntityType> entityTypeCache = new MapMaker().weakKeys().makeMap();

    /**
     * Gets the Gunsmith {@link EntityType} for the given forge entity class.
     * 
     * @param cls The entity class
     * @return The gunsmith entity
     */
    public static EntityType getEntityType(Class<? extends net.minecraft.entity.Entity> cls)
    {
        if (entityTypeCache.containsKey(cls))
        {
            return entityTypeCache.get(cls);
        }
        ForgeEntityType type = new ForgeEntityType(cls);
        entityTypeCache.put(cls, type);
        return type;
    }

}
