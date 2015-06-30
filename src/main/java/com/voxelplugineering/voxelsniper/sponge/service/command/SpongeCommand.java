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
package com.voxelplugineering.voxelsniper.sponge.service.command;

import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.commands.Command;
import com.voxelplugineering.voxelsniper.service.permission.PermissionProxy;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.util.Context;

/**
 * A proxy command which may be registered with sponge but calls the gunsmith event handler.
 */
public class SpongeCommand implements CommandCallable
{

    private final PlayerRegistry<org.spongepowered.api.entity.player.Player> players;
    private final PermissionProxy perms;
    private Command command;

    /**
     * Creates a new {@link SpongeCommand}.
     * 
     * @param cmd The command to wrap
     */
    @SuppressWarnings("unchecked")
    public SpongeCommand(Context context, Command cmd)
    {
        this.players = context.getRequired(PlayerRegistry.class);
        this.perms = context.getRequired(PermissionProxy.class);
        this.command = cmd;
    }

    @Override
    public List<String> getSuggestions(org.spongepowered.api.util.command.CommandSource source, String arguments) throws CommandException
    {
        return Lists.newArrayList();
    }

    @Override
    public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException
    {
        String[] args = arguments.split(" ");
        if (source instanceof org.spongepowered.api.entity.player.Player)
        {
            org.spongepowered.api.entity.player.Player player = (org.spongepowered.api.entity.player.Player) source;
            com.voxelplugineering.voxelsniper.entity.Player sniper = this.players.getPlayer(player.getName()).get();
            boolean success = this.command.execute(sniper, args);
            if (success)
            {
                return Optional.of(CommandResult.success());
            }
        } else if (source instanceof org.spongepowered.api.util.command.source.ConsoleSource)
        {
            boolean success = this.command.execute(this.players.getConsoleSniperProxy(), args);
            if (success)
            {
                return Optional.of(CommandResult.success());
            }
        }
        // TODO support other types?
        return Optional.of(CommandResult.empty());
    }

    @Override
    public boolean testPermission(org.spongepowered.api.util.command.CommandSource source)
    {
        // TODO support for other sources?
        if (source instanceof org.spongepowered.api.entity.player.Player)
        {
            org.spongepowered.api.entity.player.Player player = (org.spongepowered.api.entity.player.Player) source;
            for (String permission : this.command.getPermissions())
            {
                if (this.perms.hasPermission(this.players.getPlayer(player.getName()).get(), permission))
                {
                    return true;
                }
            }
        } else
        {
            if (!this.command.isPlayerOnly())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source)
    {
        return Optional.<Text>of(Texts.of(this.command.getHelpMsg()));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source)
    {
        return Optional.<Text>of(Texts.of(this.command.getHelpMsg()));
    }

    @Override
    public Text getUsage(CommandSource source)
    {
        return Texts.of(this.command.getHelpMsg());
    }

}
