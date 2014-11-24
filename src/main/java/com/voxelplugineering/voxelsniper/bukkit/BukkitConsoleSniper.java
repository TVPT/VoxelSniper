package com.voxelplugineering.voxelsniper.bukkit;

import org.bukkit.command.CommandSender;

import com.thevoxelbox.vsl.api.IVariableScope;
import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.api.IBrush;
import com.voxelplugineering.voxelsniper.api.IBrushManager;
import com.voxelplugineering.voxelsniper.api.ISniper;
import com.voxelplugineering.voxelsniper.common.CommonLocation;
import com.voxelplugineering.voxelsniper.common.CommonWorld;
import com.voxelplugineering.voxelsniper.world.BlockChangeQueue;

public class BukkitConsoleSniper implements ISniper
{

    CommandSender console;

    public BukkitConsoleSniper(CommandSender c)
    {
        this.console = c;
    }

    @Override
    public String getName()
    {
        return this.console.getName();
    }

    @Override
    public void sendMessage(String msg)
    {
        this.console.sendMessage(msg);
    }

    @Override
    public IBrushManager getPersonalBrushManager()
    {
        return Gunsmith.getGlobalBrushManager();
    }

    @Override
    public CommonLocation getLocation()
    {
        return null;
    }

    @Override
    public void setCurrentBrush(IBrush brush)
    {
        throw new UnsupportedOperationException("Cannot set a brush on the console");
    }

    @Override
    public IBrush getCurrentBrush()
    {
        throw new UnsupportedOperationException("Cannot get a brush from the console");
    }

    @Override
    public IVariableScope getBrushSettings()
    {
        throw new UnsupportedOperationException("Cannot get brush settings from the console");
    }

    @Override
    public void resetSettings()
    {

    }

    @Override
    public void addHistory(BlockChangeQueue invert)
    {
        throw new UnsupportedOperationException("Console has no change queue");
    }

    @Override
    public void resetPersonalQueue()
    {
        throw new UnsupportedOperationException("Console has no change queue");
    }

    @Override
    public CommonWorld getWorld()
    {
        return null;
    }

    @Override
    public BlockChangeQueue getPersonalQueue()
    {
        return null;
    }

}
