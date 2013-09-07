package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import net.minecraft.server.v1_6_R2.EntityFireball;
import net.minecraft.server.v1_6_R2.EntityLargeFireball;
import net.minecraft.server.v1_6_R2.EntitySmallFireball;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftFireball;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftSmallFireball;
import org.bukkit.util.Vector;

/**
 * @author Gavjenks Heavily revamped from ruler brush blockPositionY
 * @author Giltwist
 * @author Monofraps (Merged Meteor brush)
 */
public class CometBrush extends Brush
{
    private static int timesUsed = 0;
    private boolean useBigBalls = false;

    /**
     *
     */
    public CometBrush()
    {
        this.setName("Comet");
    }

    private void doFireball(final SnipeData v)
    {
        final Vector targetCoords = new Vector(this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()), this.getTargetBlock().getY() + .5, this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ()));
        final Location playerLocation = v.owner().getPlayer().getLocation();
        final Vector slope = targetCoords.subtract(playerLocation.toVector());

        if (useBigBalls)
        {
            final EntityFireball ballEntity = new EntityLargeFireball(((CraftWorld) v.owner().getPlayer().getWorld()).getHandle(), ((CraftPlayer) v.owner().getPlayer()).getHandle(), slope.getX(), slope.getY(), slope.getZ());
            final CraftFireball craftFireball = new CraftFireball((CraftServer) v.owner().getPlayer().getServer(), ballEntity);
            craftFireball.setVelocity(slope.normalize());
            ((CraftWorld) v.owner().getPlayer().getWorld()).getHandle().addEntity(ballEntity);
        }
        else
        {
            final EntitySmallFireball ballEntity = new EntitySmallFireball(((CraftWorld) v.owner().getPlayer().getWorld()).getHandle(), ((CraftPlayer) v.owner().getPlayer()).getHandle(), slope.getX(), slope.getY(), slope.getZ());
            final CraftSmallFireball craftSmallFireball = new CraftSmallFireball((CraftServer) v.owner().getPlayer().getServer(), ballEntity);
            craftSmallFireball.setVelocity(slope.normalize());
            ((CraftWorld) v.owner().getPlayer().getWorld()).getHandle().addEntity(ballEntity);
        }
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int i = 0; i < par.length; ++i)
        {
            String parameter = par[i];

            if (parameter.equalsIgnoreCase("info"))
            {
                v.sendMessage("Parameters:");
                v.sendMessage("balls [big|small]  -- Sets your ball size.");
            }
            if (parameter.equalsIgnoreCase("balls"))
            {
                if (i + 1 >= par.length)
                {
                    v.sendMessage("The balls parameter expects a ball size after it.");
                }

                String newBallSize = par[++i];
                if (newBallSize.equalsIgnoreCase("big"))
                {
                    useBigBalls = true;
                    v.sendMessage("Your balls are " + ChatColor.DARK_RED + ("BIG"));
                }
                else if (newBallSize.equalsIgnoreCase("small"))
                {
                    useBigBalls = false;
                    v.sendMessage("Your balls are " + ChatColor.DARK_RED + ("small"));
                }
                else
                {
                    v.sendMessage("Unknown ball size.");
                }
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.doFireball(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.doFireball(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.voxel();
        vm.custom("Your balls are " + ChatColor.DARK_RED + (useBigBalls ? "BIG" : "small"));
    }

    @Override
    public final int getTimesUsed()
    {
        return CometBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        CometBrush.timesUsed = tUsed;
    }
}
