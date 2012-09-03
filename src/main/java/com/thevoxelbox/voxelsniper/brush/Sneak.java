package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import com.thevoxelbox.voxelsniper.HitBlox;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;

/**
 * 
 * @author Piotr
 */
public class Sneak extends Brush {

    private static int timesUsed = 0;

    public Sneak() {
        this.name = "Sneak";
    }

    @Override
    public final int getTimesUsed() {
        return Sneak.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public final boolean perform(final Action action, final com.thevoxelbox.voxelsniper.vData v, final Material heldItem, final Block clickedBlock,
            final BlockFace clickedFace) {
        switch (action) {
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            switch (heldItem) {
            case ARROW:
                if (this.getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                    v.owner().setReplace(this.tb.getTypeId());
                    return true;
                } else { // Changed due to an excellent member suggestion: when crouch clicking the sky or void, it should just set material or replace material
                         // to air. Just added this and the else{} for left click. -Gavjenks
                    v.owner().setReplace(0);
                    return true;
                }

            case SULPHUR:
                if (this.getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                    v.owner().setReplaceData(this.tb.getData());
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
                    v.owner().setVoxel(this.tb.getTypeId());
                    return true;
                } else { // See above comment for right click -Gavjenks
                    v.owner().setVoxel(0);
                    return true;
                }

            case SULPHUR:
                if (this.getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                    v.owner().setData(this.tb.getData());
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
                    v.owner().setVoxel(this.tb.getTypeId());
                    v.owner().setData(this.tb.getData());
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

    @Override
    public final void setTimesUsed(final int tUsed) {
        Sneak.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected final boolean getSilentTarget(final vSniper v, final Block clickedBlock, final BlockFace clickedFace) {
        this.w = v.getPlayer().getWorld();
        if (clickedBlock != null) {
            this.tb = clickedBlock;
            this.lb = clickedBlock.getRelative(clickedFace);
            if (this.lb == null) {
                return false;
            }
            if (v.isLightning()) {
                this.w.strikeLightning(this.tb.getLocation());
            }
            return true;
        } else {
            HitBlox hb = null;
            if (v.isDistRestrict()) {
                hb = new HitBlox(v.getPlayer(), this.w, v.getRange());
                this.tb = hb.getRangeBlock();
            } else {
                hb = new HitBlox(v.getPlayer(), this.w);
                this.tb = hb.getTargetBlock();
            }
            if (this.tb != null) {
                this.lb = hb.getLastBlock();
                if (this.lb == null) {
                    return false;
                }
                if (v.isLightning()) {
                    this.w.strikeLightning(this.tb.getLocation());
                }
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
