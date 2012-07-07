package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

public class Pole extends PerformBrush {

	public Pole() {
		name="pole";
	}

	@Override
	protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
		bx = tb.getX();
		by = tb.getY();
		bz = tb.getZ();
		pole(v);
	}

	@Override
	protected void powder(com.thevoxelbox.voxelsniper.vData v) {
		bx = lb.getX();
		by = lb.getY();
		bz = lb.getZ();
		pole(v);
	}

	@Override
	public void info(vMessage vm) {
		vm.brushName(name);		
	}
	
	private void pole(com.thevoxelbox.voxelsniper.vData v) {
		int bsize = v.brushSize;
		
		for (int i = 0; i <bsize; i++) {
			current.perform(clampY(bx, by + i, bz));
		}
	}

}
