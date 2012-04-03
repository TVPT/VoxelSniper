/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.EntityPainting;
import net.minecraft.server.EnumArt;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Painting state change handler
 * 
 * @author Piotr
 */
public class vPainting {

    /**
     * ArrayList of Notch's paintings from the EnumArt enum
     * Stored here for easy access, also EnumArt doesn't have a .get
     */
    public static final ArrayList<EnumArt> paintings = new ArrayList<EnumArt>(Arrays.asList(EnumArt.values()));

    /**
     * The paint method used to scroll or set a painting to a specific type
     * 
     * @param p The player executing the method
     * @param _auto Scroll automatically? If false will use 'choice' to try and set the painting
     * @param back Scroll in reverse?
     * @param choice Chosen index to set the painting to
     */
    public static void paint(Player p, boolean _auto, boolean back, int choice) {
        boolean auto = _auto;

        Location loc = p.getTargetBlock(null, 4).getLocation();
        Location loc2 = p.getLocation();
        CraftWorld craftWorld = (CraftWorld) p.getWorld();
        double x1 = loc.getX() + 0.4D;
        double y1 = loc.getY() + 0.4D;
        double z1 = loc.getZ() + 0.4D;
        double x2 = loc2.getX();
        double y2 = loc.getY() + 0.6D;
        double z2 = loc2.getZ();

        AxisAlignedBB bb = AxisAlignedBB.a(Math.min(x1, x2), y1, Math.min(z1, z2), Math.max(x1, x2), y2, Math.max(z1, z2));

        List<?> entities = craftWorld.getHandle().getEntities(((CraftPlayer) p).getHandle(), bb);
        if ((entities.size() == 1) && ((entities.get(0) instanceof EntityPainting))) {
            EntityPainting oldPainting = (EntityPainting) entities.get(0);
            EntityPainting newPainting = new EntityPainting(craftWorld.getHandle(), oldPainting.x, oldPainting.y, oldPainting.z, oldPainting.direction % 4);

            newPainting.art = oldPainting.art;
            oldPainting.dead = true;

            if (auto) {
                int i = (paintings.indexOf(newPainting.art) + (back ? -1 : 1) + paintings.size()) % paintings.size();
                newPainting.art = (paintings.get(i));
                newPainting.setDirection(newPainting.direction);
                newPainting.world.addEntity(newPainting);
                p.sendMessage(ChatColor.GREEN + "Painting set to ID: " + (i));
            } else {
                try {
                    newPainting.art = (paintings.get(choice));
                    newPainting.setDirection(newPainting.direction);
                    newPainting.world.addEntity(newPainting);
                    p.sendMessage(ChatColor.GREEN + "Painting set to ID: " + choice);
                } catch (Exception e) {
                    p.sendMessage(ChatColor.RED + "Your input was invalid somewhere.");
                }
            }
        }
    }
}
