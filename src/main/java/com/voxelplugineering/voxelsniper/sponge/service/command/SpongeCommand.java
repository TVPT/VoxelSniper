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

import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.commands.Command;

/**
 * A proxy command which may be registered with sponge but calls the gunsmith
 * event handler.
 */
public class SpongeCommand implements CommandCallable
{

    private Command command;

    /**
     * Creates a new {@link SpongeCommand}.
     * 
     * @param cmd The command to wrap
     */
    public SpongeCommand(Command cmd)
    {
        this.command = cmd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSuggestions(org.spongepowered.api.util.command.CommandSource source, String arguments) throws CommandException
    {
        return Lists.newArrayList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean call(org.spongepowered.api.util.command.CommandSource source, String arguments, List<String> parents) throws CommandException
    {
        String[] args = arguments.split(" ");
        if (source instanceof org.spongepowered.api.entity.player.Player)
        {
            org.spongepowered.api.entity.player.Player player = (org.spongepowered.api.entity.player.Player) source;
            com.voxelplugineering.voxelsniper.api.entity.Player sniper = Gunsmith.getPlayerRegistry().getPlayer(player.getName()).get();
            return this.command.execute(sniper, args);
        } else if (source instanceof org.spongepowered.api.util.command.source.ConsoleSource)
        {
            return this.command.execute(Gunsmith.getPlayerRegistry().getConsoleSniperProxy(), args);
        } else
        {
            //TODO support other types?
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean testPermission(org.spongepowered.api.util.command.CommandSource source)
    {
        //TODO support for other sources?
        if (source instanceof org.spongepowered.api.entity.player.Player)
        {
            org.spongepowered.api.entity.player.Player player = (org.spongepowered.api.entity.player.Player) source;
            for (String permission : this.command.getPermissions())
            {
                if (Gunsmith.getPermissionsProxy().hasPermission(Gunsmith.getPlayerRegistry().getPlayer(player.getName()).get(), permission))
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getShortDescription()
    {
        return Optional.of(this.command.getHelpMsg());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getHelp()
    {
        return Optional.of(this.command.getHelpMsg());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage()
    {
        return this.command.getHelpMsg();
    }

}
