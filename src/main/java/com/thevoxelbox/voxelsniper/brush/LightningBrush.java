package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;

public class LightningBrush extends Brush {

    public LightningBrush() {
        this.setName("Lightning");
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.brushMessage("Lightning Brush!  Please use in moderation.");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        Entity e = this.world.createEntity(EntityTypes.LIGHTNING, this.targetBlock.getBlockPosition());
        this.world.spawnEntity(e, this.cause);
    }

    @Override
    protected final void powder(final SnipeData v) {
        arrow(v);
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.lightning";
    }
}
