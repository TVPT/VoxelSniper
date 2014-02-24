package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author MikeMatrix
 */
public class WarpBrush extends Brush
{
    /**
     *
     */
    public WarpBrush()
    {
        this.setName("Warp");
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        Player player = v.owner().getPlayer();
        Location location = this.getLastBlock().getLocation();
        Location playerLocation = player.getLocation();
        location.setPitch(playerLocation.getPitch());
        location.setYaw(playerLocation.getYaw());

        player.teleport(location);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        Player player = v.owner().getPlayer();
        Location location = this.getLastBlock().getLocation();
        Location playerLocation = player.getLocation();
        location.setPitch(playerLocation.getPitch());
        location.setYaw(playerLocation.getYaw());

        getWorld().strikeLightning(location);
        player.teleport(location);
        getWorld().strikeLightning(location);
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.warp";
    }
}
