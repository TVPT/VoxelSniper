package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class WarpBrush extends Brush {

    public WarpBrush() {
        this.setName("Warp");
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        Player player = v.owner().getPlayer();
        player.setLocation(this.lastBlock);
    }

    private void strikeLightning(Location<World> pos, Player player) {
        Entity e = pos.getExtent().createEntity(EntityTypes.LIGHTNING, pos.getBlockPosition());
        pos.getExtent().spawnEntity(e, Cause.of(NamedCause.of("plugin", VoxelSniper.getInstance()), NamedCause.source(player)));
    }

    @Override
    protected final void powder(final SnipeData v) {
        Player player = v.owner().getPlayer();

        strikeLightning(player.getLocation(), player);
        player.setLocation(this.lastBlock);
        strikeLightning(this.lastBlock, player);
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.warp";
    }
}
