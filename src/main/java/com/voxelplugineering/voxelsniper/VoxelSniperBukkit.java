/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelplugineering.voxelsniper;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.api.IVoxelSniper;
import com.voxelplugineering.voxelsniper.bukkit.BukkitWorldFactory;
import com.voxelplugineering.voxelsniper.command.BukkitCommandRegistrar;
import com.voxelplugineering.voxelsniper.common.command.CommandHandler;
import com.voxelplugineering.voxelsniper.common.factory.CommonWorldFactory;
import com.voxelplugineering.voxelsniper.perms.VaultPermissionProxy;

public class VoxelSniperBukkit extends JavaPlugin implements IVoxelSniper
{
    public static VoxelSniperBukkit voxelsniper;

    SniperManagerBukkit sniperManager;
    BrushManagerBukkit brushManager;

    @Override
    public void onEnable()
    {
        voxelsniper = this;
        Gunsmith.setPlugin(this);
        CommonWorldFactory.setFactory(new BukkitWorldFactory(this.getServer()));
        this.sniperManager = new SniperManagerBukkit();
        this.sniperManager.init();
        this.brushManager = new BrushManagerBukkit();
        Gunsmith.setBrushManager(this.brushManager);
        this.brushManager.init();
        setupPermissions();
        CommandHandler.create();
        CommandHandler.COMMAND_HANDLER.setRegistrar(new BukkitCommandRegistrar());
        Gunsmith.finish();
        
    }

    private void setupPermissions()
    {
        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
        if (vault != null) {
            Gunsmith.setPermissionProxy(new VaultPermissionProxy());
        }
    }

    @Override
    public void onDisable()
    {
        this.sniperManager.stop();
        this.brushManager.stop();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
         
        
        return false;
    }

    @Override
    public SniperManagerBukkit getSniperManager()
    {
        return this.sniperManager;
    }

    @Override
    public BrushManagerBukkit getBrushManager()
    {
        return this.brushManager;
    }

}
