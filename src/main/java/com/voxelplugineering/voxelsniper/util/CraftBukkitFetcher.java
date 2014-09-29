package com.voxelplugineering.voxelsniper.util;

import java.lang.reflect.InvocationTargetException;
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
	public static String	CRAFTBUKKIT_PACKAGE;

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
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
		    System.out.println("Error determining CraftBukkit package name " + e.getMessage());
		}
	}
}
