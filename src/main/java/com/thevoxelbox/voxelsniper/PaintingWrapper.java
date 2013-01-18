package com.thevoxelbox.voxelsniper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.v1_4_R1.AxisAlignedBB;
import net.minecraft.server.v1_4_R1.EntityPainting;
import net.minecraft.server.v1_4_R1.EnumArt;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Painting state change handler.
 *
 * @author Piotr
 */
public final class PaintingWrapper
{

    private PaintingWrapper()
    {
    }

    /**
     * ArrayList of Notch's paintings from the EnumArt enum Stored here for easy access, also EnumArt doesn't have a .get.
     */
    public static final ArrayList<EnumArt> PAINTINGS = new ArrayList<EnumArt>(Arrays.asList(EnumArt.values()));

    /**
     * The paint method used to scroll or set a painting to a specific type.
     *
     * @param p
     *         The player executing the method
     * @param auto
     *         Scroll automatically? If false will use 'choice' to try and set the painting
     * @param back
     *         Scroll in reverse?
     * @param choice
     *         Chosen index to set the painting to
     */
    public static void paint(final Player p, final boolean auto, final boolean back, final int choice)
    {
        final boolean _auto = auto;

        final Location _loc = p.getTargetBlock(null, 4).getLocation();
        final Location _loc2 = p.getLocation();
        final CraftWorld _craftWorld = (CraftWorld) p.getWorld();
        final double _x1 = _loc.getX() + 0.4D;
        final double _y1 = _loc.getY() + 0.4D;
        final double _z1 = _loc.getZ() + 0.4D;
        final double _x2 = _loc2.getX();
        final double _y2 = _loc.getY() + 0.6D;
        final double _z2 = _loc2.getZ();

        final AxisAlignedBB _bb = AxisAlignedBB.a(Math.min(_x1, _x2), _y1, Math.min(_z1, _z2), Math.max(_x1, _x2), _y2, Math.max(_z1, _z2));

        final List<?> _entities = _craftWorld.getHandle().getEntities(((CraftPlayer) p).getHandle(), _bb);
        if ((_entities.size() == 1) && ((_entities.get(0) instanceof EntityPainting)))
        {
            final EntityPainting _oldPainting = (EntityPainting) _entities.get(0);
            final EntityPainting _newPainting = new EntityPainting(_craftWorld.getHandle(), _oldPainting.x, _oldPainting.y, _oldPainting.z, _oldPainting.direction % 4);

            _newPainting.art = _oldPainting.art;
            _oldPainting.dead = true;

            if (_auto)
            {
                final int _i = (PaintingWrapper.PAINTINGS.indexOf(_newPainting.art) + (back ? -1 : 1) + PaintingWrapper.PAINTINGS.size()) % PaintingWrapper.PAINTINGS.size();
                _newPainting.art = (PaintingWrapper.PAINTINGS.get(_i));
                _newPainting.setDirection(_newPainting.direction);
                _newPainting.world.addEntity(_newPainting);
                p.sendMessage(ChatColor.GREEN + "Painting set to ID: " + (_i));
            }
            else
            {
                try
                {
                    _newPainting.art = (PaintingWrapper.PAINTINGS.get(choice));
                    _newPainting.setDirection(_newPainting.direction);
                    _newPainting.world.addEntity(_newPainting);
                    p.sendMessage(ChatColor.GREEN + "Painting set to ID: " + choice);
                }
                catch (final Exception _e)
                {
                    p.sendMessage(ChatColor.RED + "Your input was invalid somewhere.");
                }
            }
        }
    }
}
