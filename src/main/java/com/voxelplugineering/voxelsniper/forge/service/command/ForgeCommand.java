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
package com.voxelplugineering.voxelsniper.forge.service.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.commands.Command;
import com.voxelplugineering.voxelsniper.config.VoxelSniperConfiguration;
import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.service.permission.PermissionProxy;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.util.Context;

/**
 * A wrapper for gunsmith commands to allow them to be registered into forge.
 */
public class ForgeCommand implements net.minecraft.command.ICommand
{

    private final PlayerRegistry<EntityPlayer> players;
    private final PermissionProxy perms;

    private Command cmd;
    private List<?> aliases;

    /**
     * Creates a new {@link ForgeCommand}.
     * 
     * @param cmd the Gunsmith command to wrap
     */
    @SuppressWarnings({ "unchecked" })
    public ForgeCommand(Command cmd, Context context)
    {
        this.players = context.getRequired(PlayerRegistry.class);
        this.perms = context.getRequired(PermissionProxy.class);
        this.cmd = cmd;
        this.aliases = cmd.getAllAliases().length == 0 ? Lists.newArrayList() : Lists.newArrayList(cmd.getAllAliases());
    }

    @Override
    public int compareTo(Object arg0)
    {
        return 0;
    }

    @Override
    public String getName()
    {
        return this.cmd.getName();
    }

    @Override
    public String getCommandUsage(net.minecraft.command.ICommandSender sender)
    {
        return this.cmd.getHelpMsg();
    }

    @Override
    public List<?> getAliases()
    {
        return this.aliases;
    }

    @Override
    public void execute(net.minecraft.command.ICommandSender sender, String[] args) throws CommandException
    {
        if (sender instanceof net.minecraft.entity.player.EntityPlayer)
        {
            Player player = this.players.getPlayer(sender.getName()).get();
            boolean allowed = false;
            for (String s : this.cmd.getPermissions())
            {
                if (this.perms.hasPermission(player, s))
                {
                    allowed = true;
                    break;
                }
            }

            if (allowed)
            {
                boolean success = this.cmd.execute(player, args);

                if (!success)
                {
                    player.sendMessage(this.cmd.getHelpMsg());
                }
            } else
            {
                player.sendMessage(VoxelSniperConfiguration.permissionsRequiredMessage);
            }
        } else
        {
            if (this.cmd.isPlayerOnly())
            {
                sender.addChatMessage(new net.minecraft.util.ChatComponentText("Sorry this is a player only command."));
            } else
            {
                this.cmd.execute(this.players.getConsoleSniperProxy(), args);
            }
        }
    }

    @Override
    public boolean canCommandSenderUse(net.minecraft.command.ICommandSender sender)
    {
        return true;
    }

    @Override
    public List<?> addTabCompletionOptions(net.minecraft.command.ICommandSender sender, String[] args, net.minecraft.util.BlockPos pos)
    {
        // TODO tab completion
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }

}
