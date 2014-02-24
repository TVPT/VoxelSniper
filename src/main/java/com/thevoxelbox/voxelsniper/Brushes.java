package com.thevoxelbox.voxelsniper;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.thevoxelbox.voxelsniper.brush.IBrush;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Brush registration manager.
 */
public class Brushes
{
    private static final Multimap<Class<? extends IBrush>, String> SNIPER_BRUSHES = HashMultimap.create();

    private Brushes()
    {
    }

    /**
     * Register a brush for VoxelSniper to be able to use.
     *
     * @param clazz        Brush implementing IBrush interface.
     * @param handles      Handles under which the brush can be accessed ingame.
     */
    public static void registerSniperBrush(Class<? extends IBrush> clazz, String... handles)
    {
        Preconditions.checkNotNull(clazz, "Cannot register null as a class.");
        for (String handle : handles)
        {
            SNIPER_BRUSHES.put(clazz, handle.toLowerCase());
        }
    }

    /**
     * Retrieve Brush class via handle Lookup.
     *
     * @param handle Case insensitive brush handle
     * @return Brush class
     */
    public static Class<? extends IBrush> getBrushForHandle(String handle)
    {
        Preconditions.checkNotNull(handle, "Brushhandle can not be null.");
        if (!SNIPER_BRUSHES.containsValue(handle.toLowerCase()))
        {
            return null;
        }

        for (Map.Entry<Class<? extends IBrush>, String> entry : SNIPER_BRUSHES.entries())
        {
            if (entry.getValue().equalsIgnoreCase(handle))
            {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * @return Amount of IBrush classes registered with the system under Sniper visibility.
     */
    public static int registeredSniperBrushes()
    {
        return SNIPER_BRUSHES.keySet().size();
    }

    /**
     * @return Amount of handles registered with the system under Sniper visibility.
     */
    public static int registeredSniperBrushHandles()
    {
        return SNIPER_BRUSHES.size();
    }

    /**
     *
     * @param clazz Brush class
     * @return All Sniper registered handles for the brush.
     */
    public static Set<String> getSniperBrushHandles(Class<? extends IBrush> clazz)
    {
        return new HashSet<String>(SNIPER_BRUSHES.get(clazz));
    }

    public static Multimap<Class<?extends IBrush>, String> getRegisteredBrushesMultimap()
    {
        return ImmutableMultimap.copyOf(SNIPER_BRUSHES);
    }
}
