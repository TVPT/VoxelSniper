package com.thevoxelbox.voxelsniper;

import org.bukkit.Material;

import com.thevoxelbox.voxelgunsmith.MaterialData;

/**
 * Implementation of the VoxelGunsmith MaterialData interface.
 * 
 * @author MikeMatrix
 * 
 */
public class SniperMaterialData implements MaterialData {

    private Material material;

    private byte data;

    /**
     * @param material
     * @param data
     */
    public SniperMaterialData(final Material material, final byte data) {
        this.material = material;
        this.data = data;
    }

    @Override
    public final byte getData() {
        return this.data;
    }

    @Override
    public final Material getMaterial() {
        return this.material;
    }

    @Override
    public final void setData(final byte data) {
        this.data = data;
    }

    @Override
    public final void setMaterial(final Material material) {
        this.material = material;
    }

}
