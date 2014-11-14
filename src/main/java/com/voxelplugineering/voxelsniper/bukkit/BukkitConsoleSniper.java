package com.voxelplugineering.voxelsniper.bukkit;

import org.bukkit.command.CommandSender;

import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.api.IBrushManager;
import com.voxelplugineering.voxelsniper.api.ISniper;
import com.voxelplugineering.voxelsniper.common.CommonLocation;

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
    public String getCurrentToolId()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getToolId(Object object)
    {
        throw new UnsupportedOperationException();
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

}
