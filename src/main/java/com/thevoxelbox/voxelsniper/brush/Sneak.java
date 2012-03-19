/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import com.thevoxelbox.voxelsniper.HitBlox;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import net.minecraft.server.Packet53BlockChange;
import org.bukkit.craftbukkit.CraftWorld;

/**
 *
 * @author Piotr
 */
public class Sneak extends Brush {

    public Sneak() {
        name = "Sneak";
    }

    @Override
    public void arrow(vSniper v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void powder(vSniper v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void info(vMessage vm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean perform(Action action, vSniper v, Material heldItem, Block clickedBlock, BlockFace clickedFace) {
        switch (action) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                switch (heldItem) {
                    case ARROW:
                        if (getSilentTarget(v, clickedBlock, clickedFace)) {
                            v.setReplace(tb.getTypeId());
                            return true;
                        } else {                 //Changed due to an excellent member suggestion: when crouch clicking the sky or void, it should just set material or replace material to air.  Just added this and the else{} for left click.  -Gavjenks
                            v.setReplace(0);
                            return true;
                        }

                    case SULPHUR:
                        if (getSilentTarget(v, clickedBlock, clickedFace)) {
                            v.setReplaceData(tb.getData());
                            return true;
                        } else {
                            v.setReplaceData((byte) 0);
                            return true;
                        }

                    case GREEN_RECORD:
                        v.twoBackBrush();
                        v.p.sendMessage(ChatColor.GOLD + "Two Back");
                        return true;

                    case STONE_AXE:
                        v.reset();
                        v.info();
                        return true;

                    case SLIME_BALL: //Cross your fingers - Giltwist
                        HitBlox hb = null;
                        hb = new HitBlox(v.p, v.p.getWorld());
                        tb = hb.getTargetBlock();
                        if(tb == null) {
                            return true;
                        }
                        tb.setTypeId(0, false);
                        ((CraftPlayer) v.p).getHandle().netServerHandler.sendPacket(new Packet53BlockChange(tb.getX(), tb.getY(), tb.getZ(), ((CraftWorld) v.p.getWorld()).getHandle()));
                        return true;

                    default:
                        return false;
                }

            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                switch (heldItem) {
                    case ARROW:
                        if (getSilentTarget(v, clickedBlock, clickedFace)) {
                            v.setVoxel(tb.getTypeId());
                            return true;
                        } else {                 //See above comment for right click -Gavjenks
                            v.setVoxel(0);
                            return true;
                        }

                    case SULPHUR:
                        if (getSilentTarget(v, clickedBlock, clickedFace)) {
                            v.setData(tb.getData());
                            return true;
                        } else {
                            v.setData((byte) 0);
                            return true;
                        }

                    case GREEN_RECORD:
                        v.previousBrush();
                        v.p.sendMessage(ChatColor.GOLD + "One Back");
                        return true;

                    case STONE_AXE:
                        if (getTarget(v, clickedBlock, clickedFace)) {
                            v.setVoxel(tb.getTypeId());
                            v.setData(tb.getData());
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
                v.p.sendMessage(ChatColor.RED + "Something is not right. Report this to przerwap. (Perform Error)");
                return true;
        }
        return false;
    }

    protected boolean getSilentTarget(vSniper v, Block clickedBlock, BlockFace clickedFace) {
        w = v.p.getWorld();
        if (clickedBlock != null) {
            tb = clickedBlock;
            lb = clickedBlock.getRelative(clickedFace);
            if (lb == null) {
                return false;
            }
            if (v.lightning) {
                w.strikeLightning(tb.getLocation());
            }
            return true;
        } else {
            HitBlox hb = null;
            if (v.distRestrict) {
                hb = new HitBlox(v.p, w, v.range);
                tb = hb.getRangeBlock();
            } else {
                hb = new HitBlox(v.p, w);
                tb = hb.getTargetBlock();
            }
            if (tb != null) {
                lb = hb.getLastBlock();
                if (lb == null) {
                    return false;
                }
                if (v.lightning) {
                    w.strikeLightning(tb.getLocation());
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
