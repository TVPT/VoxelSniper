package com.voxelplugineering.voxelsniper.logging;

import java.util.logging.Level;

import com.voxelplugineering.voxelsniper.VoxelSniperBukkit;
import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.api.ILogger;

public class BukkitLogger implements ILogger
{
    
    public BukkitLogger()
    {
        
    }

    @Override
    public void debug(String msg)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.FINE, msg);
    }

    @Override
    public void info(String msg)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.INFO, msg);
    }

    @Override
    public void warning(String msg)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.WARNING, msg);
    }

    @Override
    public void error(String msg)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.SEVERE, msg);
    }

    @Override
    public void error(Exception e)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.SEVERE, e.getMessage(), e);
    }

    @Override
    public void error(Exception e, String msg)
    {
        ((VoxelSniperBukkit) Gunsmith.getVoxelSniper()).getLogger().log(Level.SEVERE, msg, e);
        
    }

}
