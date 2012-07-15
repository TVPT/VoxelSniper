/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.HitBlox;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.undo.vBlock;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.event.block.Action;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import java.util.Queue;
import org.bukkit.World;

/**
 * The abstract class Brush Base of all the brushes
 * 
 * @author Piotr
 */
public abstract class Brush {

    protected int currentPiece; // for throttled execution of large brushes so as not to crash the server, holds info about which piece is currently in need of
                                // processing.
    public int currentTimerID = -1;
    public int currentOneOffID = -1;
    public Queue<int[]> throttleQueue;
    /**
     * Pointer to the world the current action is being executed
     */
    protected World w;
    /**
     * Targeted reference point X
     */
    protected int bx;
    /**
     * Targeted reference point Y
     */
    protected int by;
    /**
     * Targeted reference point Z
     */
    protected int bz;
    /**
     * Brush'w Target Block Derived from getTarget()
     */
    protected Block tb;
    /**
     * Brush'w Target 'Last' Block Block at the face of the block clicked ColDerived from getTarget()
     */
    protected Block lb;
    /**
     * Brush'w private name.
     */
    public String name = "Undefined";
    protected int undoScale = 1000;

    /**
     * 
     * @param v
     */
    protected void setBlock(vBlock v) {
        w.getBlockAt(v.x, v.y, v.z).setTypeId(v.id);
    }

    public int getPiece() {
        return currentPiece;
    }

    public void setPiece(int piece) {
        currentPiece = piece;
    }

    public void ThrottledRun(com.thevoxelbox.voxelsniper.vData v, int[] pieceNumbers) { // to be overriden by individual brushes
    }

    public int[] getPieceNumbers(com.thevoxelbox.voxelsniper.vData v, int piece, int numPieces) { // method for determinine the loop starting and stoppping
                                                                                                  // numbers for partial pieces of a 3d snipe. Currently only 3d
                                                                                                  // brush shapes supported (discs can be hundreds in size and
                                                                                                  // still solve without crashing, aside from shadows)
        int bsize = v.brushSize;
        int pieceSize = v.owner().pieceSize;
        int nextBlock = (piece - 1) * pieceSize + 1;

        int innerLoop = (nextBlock % (bsize * bsize)) % bsize; // hmm, + 1 or something?
        int middleLoop = ((nextBlock % (bsize * bsize)) - innerLoop) / bsize + 1;
        int outerLoop = (nextBlock - innerLoop - middleLoop * bsize) / (bsize * bsize) + 1;

        double endBlock;
        if (piece < numPieces) {
            endBlock = nextBlock + pieceSize;
        } else {
            endBlock = bsize * bsize * bsize;
        }
        double innerDone = (endBlock % (bsize * bsize)) % bsize; // hmm, + 1 or something?
        double middleDone = ((endBlock % (bsize * bsize)) - innerLoop) / bsize + 1;
        double outerDone = (endBlock - innerLoop - middleLoop * bsize) / (bsize * bsize) + 1;

        int[] returnNumbers = new int[6];
        returnNumbers[0] = innerLoop;
        returnNumbers[1] = middleLoop;
        returnNumbers[2] = outerLoop;
        returnNumbers[3] = (int) innerDone;
        returnNumbers[4] = (int) middleDone;
        returnNumbers[5] = (int) outerDone;
        return returnNumbers;
    }

    /**
     * Sets the Id of the block at the passed coordinate
     * 
     * @param t
     *            The id the block will be set to
     * @param ax
     *            X coordinate
     * @param ay
     *            Y coordinate
     * @param az
     *            Z coordinate
     */
    protected void setBlockIdAt(int t, int ax, int ay, int az) {
        w.getBlockAt(ax, ay, az).setTypeId(t);
    }

    /**
     * Returns the block at the passed coordinates
     * 
     * @param ax
     *            X coordinate
     * @param ay
     *            Y coordinate
     * @param az
     *            Z coordinate
     * @return
     */
    protected int getBlockIdAt(int ax, int ay, int az) {
        return w.getBlockAt(ax, ay, az).getTypeId();
    }

    /**
     * The arrow action. Executed when a player RightClicks with an Arrow
     * 
     * @param v
     *            vSniper caller
     */
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
    }

    /**
     * The powder action. Executed when a player RightClicks with Gunpowder
     * 
     * @param v
     *            vSniper caller
     */
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
    }

    /**
     * 
     * @param vm
     */
    public abstract void info(vMessage vm);

    public void updateScale() {
    }

    public Block clampY(int x, int y, int z) {
        if (y < 0) {
            y = 0;
        } else if (y > w.getMaxHeight()) {
            y = w.getMaxHeight();
        }

        return w.getBlockAt(x, y, z);
    }

    /**
     * 
     * @param action
     * @param v
     * @param heldItem
     * @param clickedBlock
     * @param clickedFace
     */
    public boolean perform(Action action, com.thevoxelbox.voxelsniper.vData v, Material heldItem, Block clickedBlock, BlockFace clickedFace) {
        switch (action) {
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            switch (heldItem) {
            case ARROW:
                if (getTarget(v, clickedBlock, clickedFace)) {
                    updateScale();
                    if (this instanceof PerformBrush) {
                        ((PerformBrush) this).initP(v);
                    }
                    arrow(v);
                    return true;
                }
                break;

            case SULPHUR:
                if (getTarget(v, clickedBlock, clickedFace)) {
                    updateScale();
                    if (this instanceof PerformBrush) {
                        ((PerformBrush) this).initP(v);
                    }
                    powder(v);
                    return true;
                }
                break;

            default:
                return false;
            }
            break;

        case LEFT_CLICK_AIR:

            break;

        case LEFT_CLICK_BLOCK:

            break;

        case PHYSICAL:
            break;

        default:
            v.sendMessage(ChatColor.RED + "Something is not right. Report this to przerwap. (Perform Error)");
            return true;
        }
        return false;
    }

    /**
     * Overridable getTarget method.
     * 
     * @param v
     * @param clickedBlock
     * @param clickedFace
     * @return
     */
    protected boolean getTarget(com.thevoxelbox.voxelsniper.vData v, Block clickedBlock, BlockFace clickedFace) {
        w = v.getWorld();
        if (clickedBlock != null) {
            tb = clickedBlock;
            lb = clickedBlock.getRelative(clickedFace);
            if (lb == null) {
                v.sendMessage(ChatColor.RED + "You clicked outside of your sniping range.");
                return false;
            }
            if (v.owner().isLightning()) {
                w.strikeLightning(tb.getLocation());
            }
            return true;
        } else {
            HitBlox hb = null;
            if (v.owner().isDistRestrict()) {
                hb = new HitBlox(v.owner().getPlayer(), w, v.owner().getRange());
                tb = hb.getRangeBlock();
            } else {
                hb = new HitBlox(v.owner().getPlayer(), w);
                tb = hb.getTargetBlock();
            }
            if (tb != null) {
                lb = hb.getLastBlock();
                if (lb == null) {
                    v.sendMessage(ChatColor.RED + "You clicked outside of your sniping range.");
                    return false;
                }
                if (v.owner().isLightning()) {
                    w.strikeLightning(tb.getLocation());
                }
                return true;
            } else {
                v.sendMessage(ChatColor.RED + "You clicked outside of your sniping range.");
                return false;
            }
        }
    }

    /**
     * A Brush's custom command handler.
     * 
     * @param par
     *            Array of string containing parameters
     * @param v
     *            vSniper caller
     */
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        v.sendMessage(ChatColor.DARK_GREEN + "This brush doesn't take any extra parameters.");
    }
}
