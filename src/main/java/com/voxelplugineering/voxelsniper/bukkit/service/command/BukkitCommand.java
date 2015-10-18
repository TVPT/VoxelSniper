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
package com.voxelplugineering.voxelsniper.bukkit.service.command;

import static com.google.common.base.Preconditions.checkNotNull;

import com.voxelplugineering.voxelsniper.commands.Command;
import com.voxelplugineering.voxelsniper.config.VoxelSniperConfiguration;
import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.service.permission.PermissionProxy;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.util.Context;

/**
 * A wrapper for Gunsmith's commands which may be registered into the bukkit command handler.
 */
public class BukkitCommand extends org.bukkit.command.Command
{

    private final PlayerRegistry<org.bukkit.entity.Player> pr;
    private final PermissionProxy perms;
    private final Command cmd;

    /**
     * Creates a new {@link BukkitCommand}.
     * 
     * @param name the command name, cannot be null or empty
     * @param cmd the command, cannot be null
     */
    @SuppressWarnings("unchecked")
    protected BukkitCommand(String name, Command cmd, Context context)
    {
        super(name);
        this.cmd = checkNotNull(cmd, "Command cannot be null");
        this.pr = context.getRequired(PlayerRegistry.class);
        this.perms = context.getRequired(PermissionProxy.class);
    }

    @Override
    public boolean execute(org.bukkit.command.CommandSender cs, String command, String[] args)
    {
        if (cs instanceof org.bukkit.entity.Player)
        {
            Player sender = this.pr.getPlayer(cs.getName()).get();
            boolean allowed = false;
            for (String s : this.cmd.getPermissions())
            {
                if (this.perms.hasPermission(sender, s))
                {
                    allowed = true;
                    break;
                }
            }

            if (allowed)
            {
                boolean success = this.cmd.execute(sender, args);

                if (!success)
                {
                    sender.sendMessage(this.cmd.getHelpMsg());
                }
            } else
            {
                sender.sendMessage(VoxelSniperConfiguration.permissionsRequiredMessage);
            }
            return true;
        } else if (cs instanceof org.bukkit.command.ConsoleCommandSender)
        {
            if (this.cmd.isPlayerOnly())
            {
                cs.sendMessage(VoxelSniperConfiguration.commandPlayerOnly);
                return true;
            }
            return this.cmd.execute(this.pr.getConsoleSniperProxy(), args);
        } else
        {
            // Could support other senders here if necessary
            return false;
        }
    }

}
