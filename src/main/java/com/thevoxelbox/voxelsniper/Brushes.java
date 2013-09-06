package com.thevoxelbox.voxelsniper;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.thevoxelbox.voxelsniper.brush.IBrush;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Brush registration manager.
 */
public class Brushes
{
    private static final Multimap<Class<? extends IBrush>, String> SNIPER_BRUSHES = HashMultimap.create();
    private static final Multimap<Class<? extends IBrush>, String> LITE_SNIPER_BRUSHES = HashMultimap.create();

    private Brushes()
    {
    }

    /**
     * Register a brush for VoxelSniper to be able to use.
     *
     * @param clazz        Brush implementing IBrush interface.
     * @param availability Availability for Snipers/LiteSnipers.
     * @param handles      Handles under which the brush can be accessed ingame.
     */
    public static void registerSniperBrush(Class<? extends IBrush> clazz, BrushAvailability availability, String... handles)
    {
        Preconditions.checkNotNull(clazz, "Cannot register null as a class.");
        for (String handle : handles)
        {
            switch (availability)
            {
                case SNIPER_ONLY:
                    SNIPER_BRUSHES.put(clazz, handle);
                    break;
                case LITESNIPER_ONLY:
                    LITE_SNIPER_BRUSHES.put(clazz, handle);
                    break;
                case ALL:
                    SNIPER_BRUSHES.put(clazz, handle);
                    LITE_SNIPER_BRUSHES.put(clazz, handle);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @return Amount of IBrush classes registered with the system under Sniper visibility.
     */
    public static int registeredSniperBrushes()
    {
        return SNIPER_BRUSHES.keySet().size();
    }

    /**
     * @return Amount of IBrush classes registered with the system under LiteSniper visibility.
     */
    public static int registeredLiteSniperBrushes()
    {
        return LITE_SNIPER_BRUSHES.keySet().size();
    }

    /**
     * @return Amount of handles registered with the system under Sniper visibility.
     */
    public static int registeredSniperBrushHandles()
    {
        return SNIPER_BRUSHES.size();
    }

    /**
     * @return Amount of handles registered with the system under LiteSniper visibility.
     */
    public static int registeredLiteSniperBrushHandles()
    {
        return LITE_SNIPER_BRUSHES.size();
    }

    /**
     * Create a new IBrush instance.
     *
     * @param clazz Class implementing IBrush to be instanciated.
     * @return The newly created IBrush instance or null.
     */
    private static IBrush getNewBrushInstance(Class<? extends IBrush> clazz)
    {
        Preconditions.checkNotNull(clazz, "Null cannot be instanciated.");
        IBrush brushInstance;
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

    /**
     * Find correct IBrush class by handle and create a new Instance of it.
     *
     * @param brushes Multimap containing IBrush to handle mapping.
     * @param handle  Handle to look up and instanciate.
     * @return The newly created IBrush instance or null if not found.
     */
    private static IBrush getNewBrushInstance(Multimap<Class<? extends IBrush>, String> brushes, String handle)
    {
        for (Class<? extends IBrush> clazz : brushes.keySet())
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

    /**
     * Convinience Method for Sniper brushes around {@link #getNewBrushInstance(com.google.common.collect.Multimap, String)}.
     *
     * @param handle Handle to look up and instanciate.
     * @return The newly created IBrush instance or null if not found.
     */
    public static IBrush getNewSniperBrushInstance(String handle)
    {
        return getNewBrushInstance(SNIPER_BRUSHES, handle);
    }

    /**
     * Convinience Method for LiteSniper brushes around {@link #getNewBrushInstance(com.google.common.collect.Multimap, String)}.
     *
     * @param handle Handle to look up and instanciate.
     * @return The newly created IBrush instance or null if not found.
     */
    public static IBrush getNewLiteSniperBrushInstance(String handle)
    {
        return getNewBrushInstance(LITE_SNIPER_BRUSHES, handle);
    }

    /**
     * Create and return a mapping of handles to {@link IBrush} instaces for Snipers.
     *
     * @return Mapping of handles to newly created {@link IBrush} instances.
     */
    public static Map<String, IBrush> getNewSniperBrushInstances()
    {
        return getNewBrushInstances(SNIPER_BRUSHES);
    }

    /**
     * Create and return a mapping of handles to {@link IBrush} instaces for LiteSnipers.
     *
     * @return Mapping of handles to newly created {@link IBrush} instances.
     */
    public static Map<String, IBrush> getNewLiteSniperBrushInstances()
    {
        return getNewBrushInstances(LITE_SNIPER_BRUSHES);
    }

    /**
     * Create and return a mapping of handles to {@link IBrush} instances.
     *
     * @param brushes Mappings between IBrush classes and handles.
     * @return Mapping of handles to newly created {@link IBrush} instances.
     */
    private static Map<String, IBrush> getNewBrushInstances(Multimap<Class<? extends IBrush>, String> brushes)
    {
        HashMap<String, IBrush> result = new HashMap<String, IBrush>();

        for (Class<? extends IBrush> clazz : brushes.keySet())
        {
            IBrush brushInstance = getNewBrushInstance(clazz);

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

    /**
     * Brush Availability Ranks.
     */
    public enum BrushAvailability
    {
        LITESNIPER_ONLY, SNIPER_ONLY, ALL
    }
}
