package com.voxelplugineering.voxelsniper.bukkit;

import static com.google.common.base.Preconditions.checkNotNull;
import org.bukkit.command.CommandSender;

import com.thevoxelbox.vsl.api.IVariableScope;
import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.api.IBrush;
import com.voxelplugineering.voxelsniper.api.IBrushManager;
import com.voxelplugineering.voxelsniper.api.ISniper;
import com.voxelplugineering.voxelsniper.common.CommonLocation;
import com.voxelplugineering.voxelsniper.common.CommonWorld;
import com.voxelplugineering.voxelsniper.world.BlockChangeQueue;

/**
 * A stripped out {@link ISniper} implementation to act as a proxy for the console.
 */
public class BukkitConsoleSniper implements ISniper
{

    /**
     * The console's bukkit {@link CommandSender}.
     */
    CommandSender console;

    /**
     * Creates a new console proxy wrapping the given {@link CommandSender}.
     * 
     * @param console the console, cannot be null
     */
    public BukkitConsoleSniper(CommandSender console)
    {
        checkNotNull(console, "Console cannot be null");
        this.console = console;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return this.console.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String msg)
    {
        this.console.sendMessage(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBrushManager getPersonalBrushManager()
    {
        return Gunsmith.getGlobalBrushManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonLocation getLocation()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentBrush(IBrush brush)
    {
        throw new UnsupportedOperationException("Cannot set a brush on the console");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBrush getCurrentBrush()
    {
        throw new UnsupportedOperationException("Cannot get a brush from the console");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IVariableScope getBrushSettings()
    {
        throw new UnsupportedOperationException("Cannot get brush settings from the console");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetSettings()
    {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHistory(BlockChangeQueue invert)
    {
        throw new UnsupportedOperationException("Console has no change queue");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undoHistory(int n)
    {
        throw new UnsupportedOperationException("Console has no change queue");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPersonalQueue()
    {
        throw new UnsupportedOperationException("Console has no change queue");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonWorld getWorld()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockChangeQueue getActiveQueue()
    {
        throw new UnsupportedOperationException("Console has no change queue");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPendingChanges()
    {
        throw new UnsupportedOperationException("Console has no change queue");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockChangeQueue getNextPendingChange()
    {
        throw new UnsupportedOperationException("Console has no change queue");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPending(BlockChangeQueue blockChangeQueue)
    {
        throw new UnsupportedOperationException("Console has no change queue");
    }

}
