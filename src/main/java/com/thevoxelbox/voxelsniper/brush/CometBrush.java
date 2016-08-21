package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.vector.Vector3d;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.explosive.fireball.LargeFireball;
import org.spongepowered.api.entity.projectile.explosive.fireball.SmallFireball;

public class CometBrush extends Brush {

    public CometBrush() {
        this.setName("Comet");
    }

    private void doFireball(final SnipeData v) {
        Player pl = v.owner().getPlayer();
        Vector3d target = this.targetBlock.getPosition().sub(pl.getLocation().getPosition().add(0, 1.72, 0));
        pl.launchProjectile(SmallFireball.class, target.normalize());
    }

    private void doLargeFireball(final SnipeData v) {
        Player pl = v.owner().getPlayer();
        Vector3d target = this.targetBlock.getPosition().sub(pl.getLocation().getPosition().add(0, 1.72, 0));
        pl.launchProjectile(LargeFireball.class, target.normalize());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.doLargeFireball(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.doFireball(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.comet";
    }
}
