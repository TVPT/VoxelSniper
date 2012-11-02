package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Jagged_Line_Brush
 * 
 * @author Giltwist
 * @author Monofraps
 * 
 */
public class JaggedLineBrush extends PerformBrush {
	private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);
	private static int timesUsed = 0;

	private static final int RECURSION_MIN = 1;
	private static final int RECURSION_DEFAULT = 3;
	private static final int RECURSION_MAX = 10;
	private static final int SPREAD_DEFAULT = 3;

	private Random random = new Random();
	private Vector originCoords = null;
	private Vector targetCoords = new Vector();
	private int recursion = RECURSION_DEFAULT;
	private int spread = SPREAD_DEFAULT;

	/**
     * 
     */
	public JaggedLineBrush() {
		this.setName("Jagged Line");
	}

	private void jaggedP(final SnipeData v) {
		final Vector _originClone = this.originCoords.clone().add(JaggedLineBrush.HALF_BLOCK_OFFSET);
		final Vector _targetClone = this.targetCoords.clone().add(JaggedLineBrush.HALF_BLOCK_OFFSET);

		final Vector _direction = _targetClone.clone().subtract(_originClone);
		final double _length = this.targetCoords.distance(this.originCoords);

		if (_length == 0) {
			this.current.perform(this.targetCoords.toLocation(this.getWorld()).getBlock());
		} else {
			for (final BlockIterator _iterator = new BlockIterator(this.getWorld(), _originClone, _direction, 0, NumberConversions.round(_length)); _iterator
					.hasNext();) {
				final Block _block = _iterator.next();
				for (int _i = 0; _i < recursion; _i++) {
					this.current.perform(this.clampY((int) Math.round(_block.getX() + this.random.nextInt(spread * 2) - spread),
							(int) Math.round(_block.getY() + this.random.nextInt(spread * 2) - spread),
							(int) Math.round(_block.getZ() + this.random.nextInt(spread * 2) - spread)));
				}
			}
		}

		v.storeUndo(this.current.getUndo());
	}

	@Override
	public final void arrow(final SnipeData v) {
		if (originCoords == null) {
			originCoords = new Vector();
		}
		this.originCoords = this.getTargetBlock().getLocation().toVector();
		v.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
	}

	@Override
	public final void powder(final SnipeData v) {
		if (originCoords == null) {
			v.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
			return;
		} else {
			this.targetCoords = this.getTargetBlock().getLocation().toVector();
			this.jaggedP(v);
		}

	}

	@Override
	public final void info(final Message vm) {
		vm.brushName(this.getName());
		vm.custom(ChatColor.GRAY + String.format("Recursion set to: %d", this.recursion));
		vm.custom(ChatColor.GRAY + String.format("Spread set to: %d", this.spread));
	}

	@Override
	public final void parameters(final String[] par, final SnipeData v) {
		for (final String _param : par) {
			try {
				if (_param.equalsIgnoreCase("info")) {
					v.sendMessage(ChatColor.GOLD
							+ "Jagged Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a jagged line to set the second point.");
					v.sendMessage(ChatColor.AQUA + "/b j r# - sets the number of recursions (default 3, must be 1-10)");
					v.sendMessage(ChatColor.AQUA + "/b j s# - sets the spread (default 3, must be 1-10)");
					return;
				}
				if (_param.startsWith("r")) {
					final int _temp = Integer.parseInt(_param.substring(1));
					if (_temp >= RECURSION_MIN && _temp <= RECURSION_MAX) {
						this.recursion = _temp;
						v.sendMessage(ChatColor.GREEN + "Recursion set to: " + this.recursion);
					} else {
						v.sendMessage(ChatColor.RED + "ERROR: Recursion must be " + RECURSION_MIN + "-" + RECURSION_MAX);
					}

					return;
				} else if (_param.startsWith("s")) {
					final int _temp = Integer.parseInt(_param.substring(1));
					this.spread = _temp;
					v.sendMessage(ChatColor.GREEN + "Spread set to: " + this.spread);
				}
			} catch (Exception _e) {
				v.sendMessage(ChatColor.RED + String.format("Exception while parsing parameter: %s", _param));
				_e.printStackTrace();
			}
		}

	}

	@Override
	public final int getTimesUsed() {
		return JaggedLineBrush.timesUsed;
	}

	@Override
	public final void setTimesUsed(final int tUsed) {
		JaggedLineBrush.timesUsed = tUsed;
	}
}
