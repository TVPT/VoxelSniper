package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Entity_Brush
 *
 * @author Piotr
 */
public class EntityBrush extends Brush
{
    private static int timesUsed = 0;
    private EntityType entityType = EntityType.ZOMBIE;

    /**
     *
     */
    public EntityBrush()
    {
        this.setName("Entity");
    }

    private void spawn(final SnipeData v)
    {
        for (int _x = 0; _x < v.getBrushSize(); _x++)
        {
            try
            {
                this.getWorld().spawn(this.getLastBlock().getLocation(), this.entityType.getEntityClass());
            }
            catch (final IllegalArgumentException _ex)
            {
                v.sendMessage(ChatColor.RED + "Cannot spawn entity!");
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.spawn(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.spawn(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushMessage(ChatColor.LIGHT_PURPLE + "Entity brush" + " (" + this.entityType.getName() + ")");
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        if (par[1].equalsIgnoreCase("info"))
        {
            String _names = "";

            v.sendMessage(ChatColor.BLUE + "The available entity types are as follows:");
            for (final EntityType _cre : EntityType.values())
            {

                _names += ChatColor.AQUA + " | " + ChatColor.DARK_GREEN + _cre.getName();
            }
            _names += ChatColor.AQUA + " |";
            v.sendMessage(_names);
        }
        else
        {
            final EntityType _cre = EntityType.fromName(par[1]);
            if (_cre != null)
            {
                this.entityType = _cre;
                v.sendMessage(ChatColor.GREEN + "Entity type set to " + this.entityType.getName());
            }
            else
            {
                v.sendMessage(ChatColor.RED + "This is not a valid entity!");
            }
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return EntityBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        EntityBrush.timesUsed = tUsed;
    }
}
