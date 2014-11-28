package com.voxelplugineering.voxelsniper.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.voxelplugineering.voxelsniper.VoxelSniperBukkit;
import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.api.ILogger;

/**
 * A wrapper for bukkit's {@link Logger}.
 * <p>
 * TODO: this should be reworked as a more general wrapper for a java.util.Logger.
 */
public class BukkitLogger implements ILogger
{

    /**
     * Creates a new {@link BukkitLogger}.
     */
    public BukkitLogger()
    {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(String msg)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.FINE, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String msg)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.INFO, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(String msg)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.WARNING, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(String msg)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.SEVERE, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(Exception e)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.SEVERE, e.getMessage(), e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(Exception e, String msg)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.SEVERE, msg, e);

    }

}
