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

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.commands.Command;
import com.voxelplugineering.voxelsniper.service.command.CommandRegistrar;
import com.voxelplugineering.voxelsniper.util.Context;

/**
 * A command registrar for registering Gunsmith commands into forge. Commands may only be registered
 * during the {@link FMLServerStartingEvent}.
 */
public class ForgeCommandRegistrar implements CommandRegistrar
{

    private final Context context;
    private List<ForgeCommand> pending = Lists.newArrayList();

    public ForgeCommandRegistrar(Context context)
    {
        this.context = context;
    }

    @Override
    public synchronized void registerCommand(Command command)
    {
        ForgeCommand cmd = new ForgeCommand(command, this.context);
        this.pending.add(cmd);
    }

    /**
     * Sets the server starting event which the
     * {@link FMLServerStartingEvent#registerServerCommand(net.minecraft.command.ICommand)} is
     * called against to register the commands.
     * 
     * @param event the server starting event
     */
    public synchronized void flush(FMLServerStartingEvent event)
    {
        for (ForgeCommand cmd : this.pending)
        {
            event.registerServerCommand(cmd);
        }
        this.pending.clear();
    }

}
