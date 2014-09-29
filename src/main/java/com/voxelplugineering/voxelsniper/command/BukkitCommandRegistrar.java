package com.voxelplugineering.voxelsniper.command;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import com.voxelplugineering.voxelsniper.api.ICommandRegistrar;
import com.voxelplugineering.voxelsniper.common.command.Command;
import com.voxelplugineering.voxelsniper.util.CraftBukkitFetcher;

public class BukkitCommandRegistrar implements ICommandRegistrar
{
    CommandMap commands;

    public BukkitCommandRegistrar()
    {
        try
        {
            Field cmap = Class.forName(CraftBukkitFetcher.CRAFTBUKKIT_PACKAGE + ".CraftServer").getDeclaredField("commandMap");
            cmap.setAccessible(true);
            commands = (CommandMap) cmap.get(Bukkit.getServer());
        }
        catch (NoSuchFieldException  e)
        {
            System.out.println("Error setting up bukkit command registrar");
            return;
        }
        catch (SecurityException  e)
        {
            System.out.println("Error setting up bukkit command registrar");
            return;
        }
        catch (IllegalArgumentException  e)
        {
            System.out.println("Error setting up bukkit command registrar");
            return;
        }
        catch (IllegalAccessException  e)
        {
            System.out.println("Error setting up bukkit command registrar");
            return;
        }
        catch (ClassNotFoundException  e)
        {
            System.out.println("Error setting up bukkit command registrar");
            return;
        }
    }

    @Override
    public void registerCommand(Command cmd)
    {
        for(String alias: cmd.getAllAliases())
        {
            BukkitCommand bcmd = new BukkitCommand(alias, cmd);
        }
    }

}
