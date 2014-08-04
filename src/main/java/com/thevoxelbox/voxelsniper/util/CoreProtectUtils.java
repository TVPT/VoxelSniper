package com.thevoxelbox.voxelsniper.util;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

import com.thevoxelbox.voxelsniper.VoxelSniper;

/**
 * 
 * @author thepowderguy
 *
 */

public class CoreProtectUtils
{
	public static CoreProtectAPI instance;
	public static boolean CoreProtectExists = false;
	
	public static CoreProtectAPI getCoreProtect()
	{
		Plugin plugin = VoxelSniper.getInstance().getServer().getPluginManager().getPlugin("CoreProtect");
		if ((plugin == null) || (!(plugin instanceof CoreProtect))) {
			return null;
		}
		CoreProtectAPI CoreProtect = ((CoreProtect)plugin).getAPI();
		if (!CoreProtect.isEnabled()) {
			return null;
		}
		if (CoreProtect.APIVersion() < 2) {
			return null;
		}
		return CoreProtect;
	}

	public static void init()
	{
		CoreProtectUtils.instance = CoreProtectUtils.getCoreProtect();
		if (instance != null)
		{
			CoreProtectUtils.CoreProtectExists = true;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void logBlockPlace(Block b, String uname)
	{
		if (CoreProtectUtils.CoreProtectExists)
		{
			if (b.getType() != Material.AIR)
			{
				CoreProtectUtils.instance.logPlacement(uname, b.getLocation(), b.getTypeId(), b.getData());
			}
			else
			{
				CoreProtectUtils.instance.logRemoval(uname, b.getLocation(), b.getTypeId(), b.getData());
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void logBlockRemove(Block b, String uname)
	{
		if (CoreProtectUtils.CoreProtectExists)
		{
			if (b.getType() != Material.AIR)
			{
				CoreProtectUtils.instance.logRemoval(uname, b.getLocation(), b.getTypeId(), b.getData());
			}
		}
	}
	@SuppressWarnings("deprecation")
	public static void logBlockPlace(BlockState b, String uname)
	{
		if (CoreProtectUtils.CoreProtectExists)
		{
			if (b.getType() != Material.AIR)
			{
				CoreProtectUtils.instance.logPlacement(uname, b.getLocation(), b.getTypeId(), b.getRawData());
			}
			else
			{
				CoreProtectUtils.instance.logRemoval(uname, b.getLocation(), b.getTypeId(), b.getRawData());
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void logBlockRemove(BlockState b, String uname)
	{
		if (CoreProtectUtils.CoreProtectExists)
		{
			if (b.getType() != Material.AIR)
			{
				CoreProtectUtils.instance.logRemoval(uname, b.getLocation(), b.getTypeId(), b.getRawData());
			}
		}
	}
	
}
