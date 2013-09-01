package com.thevoxelbox.voxelsniper;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.thevoxelbox.voxelsniper.brush.Brush;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author MikeMatrix
 */
public class Brushes
{
    private static final Multimap<Class<? extends Brush>, String> sniperBrushes = HashMultimap.create();
    private static final Multimap<Class<? extends Brush>, String> liteSniperBrushes = HashMultimap.create();

    public static void registerSniperBrush(Class<? extends Brush> clazz, BrushAvailability availability, String... handles)
    {
        Preconditions.checkNotNull(clazz, "Cannot register null as a class.");
        for (String handle : handles)
        {
            switch (availability)
            {
                case SNIPER_ONLY:
                    sniperBrushes.put(clazz, handle);
                    break;
                case LITESNIPER_ONLY:
                    liteSniperBrushes.put(clazz, handle);
                    break;
                case ALL:
                    sniperBrushes.put(clazz, handle);
                    liteSniperBrushes.put(clazz, handle);
                    break;
                default:
                    break;
            }
        }
    }

    public static int registeredSniperBrushes()
    {
        return sniperBrushes.keySet().size();
    }

    public static int registeredLiteSniperBrushes()
    {
        return liteSniperBrushes.keySet().size();
    }

    public static int registeredSniperBrushHandles()
    {
        return sniperBrushes.size();
    }

    public static int registeredLiteSniperBrushHandles()
    {
        return liteSniperBrushes.size();
    }

    private static Brush getNewBrushInstance(Class<? extends Brush> clazz)
    {
        Preconditions.checkNotNull(clazz, "Null cannot be instanciated.");
        Brush brushInstance;
        try
        {
            brushInstance = clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            Logger.getLogger("Minecraft").severe(clazz.getName() + " could not be instanciated: " + e.getMessage());
            return null;
        }
        catch (IllegalAccessException e)
        {
            Logger.getLogger("Minecraft").severe(clazz.getName() + " could not be instanciated: " + e.getMessage());
            return null;
        }
        return brushInstance;
    }

    private static Brush getNewBrushInstance(Multimap<Class<? extends Brush>, String> brushes, String handle)
    {
        for (Class<? extends Brush> clazz : brushes.keySet())
        {
            for (String brushHandle : brushes.get(clazz))
            {
                if (handle.equalsIgnoreCase(brushHandle))
                {
                    return getNewBrushInstance(clazz);
                }
            }
        }
        return null;
    }

    public static Brush getNewSniperBrushInstance(String handle)
    {
        return getNewBrushInstance(sniperBrushes, handle);
    }

    public static Brush getnewLiteSniperBrushInstance(String handle)
    {
        return getNewBrushInstance(liteSniperBrushes, handle);
    }

    public static Map<String, Brush> getNewSniperBrushInstances()
    {
        return getNewBrushInstances(sniperBrushes);
    }

    private static Map<String, Brush> getNewBrushInstances(Multimap<Class<? extends Brush>, String> brushes)
    {
        HashMap<String, Brush> result = new HashMap<String, Brush>();

        for (Class<? extends Brush> clazz : brushes.keySet())
        {
            Brush brushInstance = getNewBrushInstance(clazz);

            if (brushInstance == null)
            {
                continue;
            }

            for (String handle : brushes.get(clazz))
            {
                result.put(handle, brushInstance);
            }
        }

        return result;
    }

    public static Map<String, Brush> getNewLiteSniperBrushInstances()
    {
        return getNewBrushInstances(liteSniperBrushes);
    }

    public enum BrushAvailability
    {
        LITESNIPER_ONLY, SNIPER_ONLY, ALL;
    }
}
