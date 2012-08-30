/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;

/**
 * 
 * @author Voxel
 */
public class DiscFace extends PerformBrush {

	public DiscFace() {
		name = "Disc Face";
	}

	@Override
	protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
		bx = tb.getX();
		by = tb.getY();
		bz = tb.getZ();
		pre(v, tb.getFace(lb));
	}

	@Override
	protected void powder(com.thevoxelbox.voxelsniper.vData v) {
		bx = lb.getX();
		by = lb.getY();
		bz = lb.getZ();
		pre(v, tb.getFace(lb));
	}

	@Override
	public void info(vMessage vm) {
		vm.brushName(name);
		vm.size();
		// vm.voxel();
	}

	double trueCircle = 0;

	@Override
	public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
		if (par[1].equalsIgnoreCase("info")) {
			v.sendMessage(ChatColor.GOLD + "Disc Face brush Parameters:");
			v.sendMessage(ChatColor.AQUA
					+ "/b df true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
			return;
		}
		for (int x = 1; x < par.length; x++) {
			if (par[x].startsWith("true")) {
				trueCircle = 0.5;
				v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
				continue;
			} else if (par[x].startsWith("false")) {
				trueCircle = 0;
				v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
				continue;
			} else {
				v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	private void pre(vData v, BlockFace bf) {
		if (bf == null) {
			return;
		}
		switch (bf) {
		case NORTH:
		case SOUTH:
			discNS(v);
			break;

		case EAST:
		case WEST:
			discEW(v);
			break;

		case UP:
		case DOWN:
			disc(v);
			break;

		default:
			break;
		}
	}

	public void disc(vData v) {
		int bsize = v.brushSize;

		double bpow = Math.pow(bsize + trueCircle, 2);
		for (int x = bsize; x >= 0; x--) {
			double xpow = Math.pow(x, 2);
			for (int y = bsize; y >= 0; y--) {
				if ((xpow + Math.pow(y, 2)) <= bpow) {
					current.perform(clampY(bx + x, by, bz + y));
					current.perform(clampY(bx + x, by, bz - y));
					current.perform(clampY(bx - x, by, bz + y));
					current.perform(clampY(bx - x, by, bz - y));
				}
			}
		}

		v.storeUndo(current.getUndo());
	}

	public void discEW(vData v) {
		int bsize = v.brushSize;

		double bpow = Math.pow(bsize + trueCircle, 2);
		for (int x = bsize; x >= 0; x--) {
			double xpow = Math.pow(x, 2);
			for (int y = bsize; y >= 0; y--) {
				if ((xpow + Math.pow(y, 2)) <= bpow) {
					current.perform(clampY(bx + x, by + y, bz));
					current.perform(clampY(bx + x, by - y, bz));
					current.perform(clampY(bx - x, by + y, bz));
					current.perform(clampY(bx - x, by - y, bz));
				}
			}
		}

		v.storeUndo(current.getUndo());
	}

	public void discNS(vData v) {
		int bsize = v.brushSize;

		double bpow = Math.pow(bsize + trueCircle, 2);
		for (int x = bsize; x >= 0; x--) {
			double xpow = Math.pow(x, 2);
			for (int y = bsize; y >= 0; y--) {
				if ((xpow + Math.pow(y, 2)) <= bpow) {
					current.perform(clampY(bx, by + x, bz + y));
					current.perform(clampY(bx, by + x, bz - y));
					current.perform(clampY(bx, by - x, bz + y));
					current.perform(clampY(bx, by - x, bz - y));
				}
			}
		}

		v.storeUndo(current.getUndo());
	}

	private static int timesUsed = 0;

	@Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed;
	}
}
