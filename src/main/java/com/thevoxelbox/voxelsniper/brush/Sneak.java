/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.HitBlox;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 *
 * @author Piotr
 */
public class Sneak extends Brush {

    public Sneak() {
        name = "Sneak";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void info(vMessage vm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean perform(Action action, com.thevoxelbox.voxelsniper.vData v, Material heldItem, Block clickedBlock, BlockFace clickedFace) {
        switch (action) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                switch (heldItem) {
                    case ARROW:
                        if (getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                            v.owner().setReplace(tb.getTypeId());
                            return true;
                        } else {                 //Changed due to an excellent member suggestion: when crouch clicking the sky or void, it should just set material or replace material to air.  Just added this and the else{} for left click.  -Gavjenks
                            v.owner().setReplace(0);
                            return true;
                        }

                    case SULPHUR:
                        if (getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                            v.owner().setReplaceData(tb.getData());
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
                        if (getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                            v.owner().setVoxel(tb.getTypeId());
                            return true;
                        } else {                 //See above comment for right click -Gavjenks
                            v.owner().setVoxel(0);
                            return true;
                        }

                    case SULPHUR:
                        if (getSilentTarget(v.owner(), clickedBlock, clickedFace)) {
                            v.owner().setData(tb.getData());
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
                        if (getTarget(v, clickedBlock, clickedFace)) {
                            v.owner().setVoxel(tb.getTypeId());
                            v.owner().setData(tb.getData());
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

    protected boolean getSilentTarget(vSniper v, Block clickedBlock, BlockFace clickedFace) {
        w = v.getPlayer().getWorld();
        if (clickedBlock != null) {
            tb = clickedBlock;
            lb = clickedBlock.getRelative(clickedFace);
            if (lb == null) {
                return false;
            }
            if (v.isLightning()) {
                w.strikeLightning(tb.getLocation());
            }
            return true;
        } else {
            HitBlox hb = null;
            if (v.isDistRestrict()) {
                hb = new HitBlox(v.getPlayer(), w, v.getRange());
                tb = hb.getRangeBlock();
            } else {
                hb = new HitBlox(v.getPlayer(), w);
                tb = hb.getTargetBlock();
            }
            if (tb != null) {
                lb = hb.getLastBlock();
                if (lb == null) {
                    return false;
                }
                if (v.isLightning()) {
                    w.strikeLightning(tb.getLocation());
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
