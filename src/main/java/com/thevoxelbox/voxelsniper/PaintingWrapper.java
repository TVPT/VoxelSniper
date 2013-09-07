package com.thevoxelbox.voxelsniper;

import net.minecraft.server.v1_6_R2.AxisAlignedBB;
import net.minecraft.server.v1_6_R2.EntityPainting;
import net.minecraft.server.v1_6_R2.EnumArt;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        final Location location = p.getTargetBlock(null, 4).getLocation();
        final Location location2 = p.getLocation();
        final CraftWorld craftWorld = (CraftWorld) p.getWorld();
        final double x1 = location.getX() + 0.4D;
        final double y1 = location.getY() + 0.4D;
        final double z1 = location.getZ() + 0.4D;
        final double x2 = location2.getX();
        final double y2 = location.getY() + 0.6D;
        final double z2 = location2.getZ();

        final AxisAlignedBB bb = AxisAlignedBB.a(Math.min(x1, x2), y1, Math.min(z1, z2), Math.max(x1, x2), y2, Math.max(z1, z2));

        final List<?> entities = craftWorld.getHandle().getEntities(((CraftPlayer) p).getHandle(), bb);
        if ((entities.size() == 1) && ((entities.get(0) instanceof EntityPainting)))
        {
            final EntityPainting oldPainting = (EntityPainting) entities.get(0);
            final EntityPainting newPainting = new EntityPainting(craftWorld.getHandle(), oldPainting.x, oldPainting.y, oldPainting.z, oldPainting.direction % 4);

            newPainting.art = oldPainting.art;
            oldPainting.dead = true;

            if (auto)
            {
                final int i = (PaintingWrapper.PAINTINGS.indexOf(newPainting.art) + (back ? -1 : 1) + PaintingWrapper.PAINTINGS.size()) % PaintingWrapper.PAINTINGS.size();
                newPainting.art = (PaintingWrapper.PAINTINGS.get(i));
                newPainting.setDirection(newPainting.direction);
                newPainting.world.addEntity(newPainting);
                p.sendMessage(ChatColor.GREEN + "Painting set to ID: " + (i));
            }
            else
            {
                try
                {
                    newPainting.art = (PaintingWrapper.PAINTINGS.get(choice));
                    newPainting.setDirection(newPainting.direction);
                    newPainting.world.addEntity(newPainting);
                    p.sendMessage(ChatColor.GREEN + "Painting set to ID: " + choice);
                }
                catch (final Exception exception)
                {
                    p.sendMessage(ChatColor.RED + "Your input was invalid somewhere.");
                }
            }
        }
    }
}
