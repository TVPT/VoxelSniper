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
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.voxelplugineering.voxelsniper.api.IRegistry;
import com.voxelplugineering.voxelsniper.api.ISniper;
import com.voxelplugineering.voxelsniper.api.ISniperRegistry;
import com.voxelplugineering.voxelsniper.api.IVoxelSniper;
import com.voxelplugineering.voxelsniper.bukkit.BukkitConsoleSniper;
import com.voxelplugineering.voxelsniper.bukkit.BukkitSniper;
import com.voxelplugineering.voxelsniper.bukkit.BukkitWorld;
import com.voxelplugineering.voxelsniper.common.CommonPlayer;
import com.voxelplugineering.voxelsniper.common.factory.ProvidedWeakRegistry;
import com.voxelplugineering.voxelsniper.common.factory.RegistryProvider;
import com.voxelplugineering.voxelsniper.util.Pair;

/**
 * The sniper manager for the bukkit specific implementation.
 */
public class SniperManagerBukkit extends ProvidedWeakRegistry<Player, CommonPlayer<Player>> implements ISniperRegistry<Player>
{
    private int blockChangesPerSecond;

    public SniperManagerBukkit(final IRegistry<World, BukkitWorld> world, int changesPerSecond, final IVoxelSniper access)
    {
        super(new RegistryProvider<Player, CommonPlayer<Player>>()
        {

            @Override
            public Pair<Player, CommonPlayer<Player>> get(String name)
            {
                @SuppressWarnings("deprecation")
                Player player = Bukkit.getPlayer(name);
                if (player == null)
                {
                    return null;
                }
                BukkitSniper sniper = new BukkitSniper(player, world, access.getGlobalBrushManager(), access.getDefaultBrushLoader(), (Integer) access.getGunsmith().getConfiguration().get("UNDO_HISTORY_SIZE"), access.getDataFolder());
                //TODO set sniper settings to initial values
                return new Pair<Player, CommonPlayer<Player>>(player, sniper);
            }
        });
        this.blockChangesPerSecond = changesPerSecond;
    }

    /**
     * A special {@link ISniper} to represent the console in operations.
     */
    private BukkitConsoleSniper console = new BukkitConsoleSniper(Bukkit.getConsoleSender());
    /**
     * A task to tick all player change queues 5x per second.
     */
    private BukkitTask worldTick;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init()
    {
        this.worldTick = VoxelSniperBukkit.voxelsniper.getServer().getScheduler().runTaskTimer(VoxelSniperBukkit.voxelsniper, new Runnable()
        {

            @Override
            public void run()
            {
                long start = System.currentTimeMillis();
                int n = 0;
                for (CommonPlayer<Player> p : getRegisteredValues())
                {
                    if (p.hasPendingChanges())
                    {
                        n++;
                    }
                }
                if (n == 0)
                {
                    return;
                }
                int remaining = blockChangesPerSecond;
                remaining /= 10;
                for (CommonPlayer<Player> p : getRegisteredValues())
                {
                    if (!p.hasPendingChanges())
                    {
                        continue;
                    }
                    int allocation = remaining / (n--);
                    int actual = 0;
                    while (p.hasPendingChanges() && actual < allocation)
                    {
                        actual += p.getNextPendingChange().perform(allocation);
                        if (p.getNextPendingChange().isFinished())
                        {
                            p.clearNextPending();
                        }
                    }
                    remaining -= actual;
                    if (remaining <= 0)
                    {
                        break;
                    }
                }
                Gunsmith.getLogger().info("Change queue tick length: " + (System.currentTimeMillis() - start) + " ms");
            }

        }, 0, 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop()
    {
        this.worldTick.cancel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restart()
    {
        stop();
        init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISniper getConsoleSniperProxy()
    {
        return this.console;
    }

}
