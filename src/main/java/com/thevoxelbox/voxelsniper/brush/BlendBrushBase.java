package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * @author Monofraps
 *
 */
public abstract class BlendBrushBase extends Brush {
	protected static int maxBlockMaterialID;
	protected boolean excludeAir = true;
	protected boolean excludeWater = true;

	static {
		// Find highest placeable block ID
		for (Material _mat : Material.values()) {
			maxBlockMaterialID = ((_mat.isBlock() && (_mat.getId() > maxBlockMaterialID)) ? _mat.getId() : maxBlockMaterialID);
		}
	}

	/**
	 * 
	 * @param v
	 */
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
		vm.custom(ChatColor.BLUE + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
	}

	@Override
	public void parameters(final String[] par, final SnipeData v) {
		for (int _i = 1; _i < par.length; ++_i) {
			if (par[_i].equalsIgnoreCase("water")) {
				this.excludeWater = !this.excludeWater;
				v.sendMessage(ChatColor.AQUA + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
			}
		}
	}
}
