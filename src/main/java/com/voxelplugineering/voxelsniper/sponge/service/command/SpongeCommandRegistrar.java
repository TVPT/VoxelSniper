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

import com.voxelplugineering.voxelsniper.api.commands.CommandRegistrar;
import com.voxelplugineering.voxelsniper.core.Gunsmith;
import com.voxelplugineering.voxelsniper.core.commands.Command;
import com.voxelplugineering.voxelsniper.sponge.VoxelSniperSponge;
import com.voxelplugineering.voxelsniper.sponge.service.SpongePlatformProxyService;

/**
 * A command registrar for sponge commands.
 */
public class SpongeCommandRegistrar implements CommandRegistrar
{

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerCommand(Command command)
    {
        SpongeCommand wrapper = new SpongeCommand(command);
        ((SpongePlatformProxyService) Gunsmith.getPlatformProxy()).getGame().getCommandDispatcher()
                .register(VoxelSniperSponge.instance, wrapper, command.getAllAliases());
    }
}
