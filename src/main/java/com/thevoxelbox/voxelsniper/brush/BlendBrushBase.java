package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

import org.spongepowered.api.text.format.TextColors;

public abstract class BlendBrushBase extends Brush {

    protected boolean excludeAir = true;
    protected boolean excludeWater = true;

    protected abstract void blend(final SnipeData v);

    @Override
    protected final void arrow(final SnipeData v) {
        this.excludeAir = false;
        this.blend(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.excludeAir = true;
        this.blend(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.custom(TextColors.BLUE + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
    }

    @Override
    public void parameters(final String[] par, final SnipeData v) {
        for (int i = 0; i < par.length; ++i) {
            if (par[i].equalsIgnoreCase("water")) {
                this.excludeWater = !this.excludeWater;
                v.sendMessage(TextColors.AQUA, "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
            }
        }
    }

    protected final boolean isExcludeAir() {
        return this.excludeAir;
    }

    protected final void setExcludeAir(boolean excludeAir) {
        this.excludeAir = excludeAir;
    }

    protected final boolean isExcludeWater() {
        return this.excludeWater;
    }

    protected final void setExcludeWater(boolean excludeWater) {
        this.excludeWater = excludeWater;
    }
}
