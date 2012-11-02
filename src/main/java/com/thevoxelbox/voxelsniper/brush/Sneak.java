package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Sniper;

/**
 * 
 * @author Piotr
 */
public class Sneak extends Brush {
	private static int timesUsed = 0;

	/**
	 * 
	 */
    public Sneak() {
        this.setName("Sneak");
    }

    /**
     * @return
     */
    @Override
    public final boolean perform(final Action action, final SnipeData v, final Material heldItem, final Block clickedBlock, final BlockFace clickedFace) {
        switch (action) {
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            switch (heldItem) {
            case ARROW:
                if (this.getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                    v.owner().setReplace(this.getTargetBlock().getTypeId());
                    return true;
                } else { // Changed due to an excellent member suggestion: when crouch clicking the sky or void, it should just set material or replace material
                         // to air. Just added this and the else{} for left click. -Gavjenks
                    v.owner().setReplace(0);
                    return true;
                }

            case SULPHUR:
                if (this.getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                    v.owner().setReplaceData(this.getTargetBlock().getData());
                    return true;
                } else {
                    v.owner().setReplaceData((byte) 0);
                    return true;
                }

            case GREEN_RECORD:
                v.owner().twoBackBrush();
                v.sendMessage(ChatColor.GOLD + "Two Back");
                return true;

            case STONE_AXE:
                v.reset();
                v.owner().info();
                return true;

            default:
                return false;
            }

        case LEFT_CLICK_AIR:
        case LEFT_CLICK_BLOCK:
            switch (heldItem) {
            case ARROW:
                if (this.getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                    v.owner().setVoxel(this.getTargetBlock().getTypeId());
                    return true;
                } else { // See above comment for right click -Gavjenks
                    v.owner().setVoxel(0);
                    return true;
                }

            case SULPHUR:
                if (this.getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                    v.owner().setData(this.getTargetBlock().getData());
                    return true;
                } else {
                    v.owner().setData((byte) 0);
                    return true;
                }

            case GREEN_RECORD:
                v.owner().previousBrush();
                v.sendMessage(ChatColor.GOLD + "One Back");
                return true;

            case STONE_AXE:
                if (this.getTarget(v, clickedBlock, clickedFace)) {
                    v.owner().setVoxel(this.getTargetBlock().getTypeId());
                    v.owner().setData(this.getTargetBlock().getData());
                    return true;
                }
                break;

            default:
                return false;
            }
            break;

        case PHYSICAL:
            return false;

        default:
            v.sendMessage(ChatColor.RED + "Something is not right. Report this to przerwap. (Perform Error)");
            return true;
        }
        return false;
    }

    /**
     * 
     * @param v
     * @param clickedBlock
     * @param clickedFace
     * @return
     */
    protected final boolean getSilentTarget(final Sniper v, final Block clickedBlock, final BlockFace clickedFace) {
        this.setWorld(v.getPlayer().getWorld());
        if (clickedBlock != null) {
            this.setTargetBlock(clickedBlock);
            this.setLastBlock(clickedBlock.getRelative(clickedFace));
            if (this.getLastBlock() == null) {
                return false;
            }
            if (v.isLightning()) {
                this.getWorld().strikeLightning(this.getTargetBlock().getLocation());
            }
            return true;
        } else {
            RangeBlockHelper _hb = null;
            if (v.isDistRestrict()) {
                _hb = new RangeBlockHelper(v.getPlayer(), this.getWorld(), v.getRange());
                this.setTargetBlock(_hb.getRangeBlock());
            } else {
                _hb = new RangeBlockHelper(v.getPlayer(), this.getWorld());
                this.setTargetBlock(_hb.getTargetBlock());
            }
            if (this.getTargetBlock() != null) {
                this.setLastBlock(_hb.getLastBlock());
                if (this.getLastBlock() == null) {
                    return false;
                }
                if (v.isLightning()) {
                    this.getWorld().strikeLightning(this.getTargetBlock().getLocation());
                }
                return true;
            } else {
                return false;
            }
        }
    }
    
    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }       

    @Override
    public final void info(final Message vm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        Sneak.timesUsed = tUsed;
    }


    @Override
    public final int getTimesUsed() {
        return Sneak.timesUsed;
    }
}