package com.thevoxelbox.voxelsniper.brush;

import java.util.Queue;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import com.thevoxelbox.voxelsniper.HitBlox;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.undo.vBlock;

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

    public final Block clampY(final int x, int y, final int z) {
        if (y < 0) {
            y = 0;
        } else if (y > this.w.getMaxHeight()) {
            y = this.w.getMaxHeight();
        }

        return this.w.getBlockAt(x, y, z);
    }

    public final int getPiece() {
        return this.currentPiece;
    }

    public final int[] getPieceNumbers(final com.thevoxelbox.voxelsniper.vData v, final int piece, final int numPieces) { // method for determinine the loop
                                                                                                                          // starting
        // and stoppping
        // numbers for partial pieces of a 3d snipe. Currently only 3d
        // brush shapes supported (discs can be hundreds in size and
        // still solve without crashing, aside from shadows)
        final int bsize = v.brushSize;
        final int pieceSize = v.owner().pieceSize;
        final int nextBlock = (piece - 1) * pieceSize + 1;

        final int innerLoop = (nextBlock % (bsize * bsize)) % bsize; // hmm, + 1 or something?
        final int middleLoop = ((nextBlock % (bsize * bsize)) - innerLoop) / bsize + 1;
        final int outerLoop = (nextBlock - innerLoop - middleLoop * bsize) / (bsize * bsize) + 1;

        double endBlock;
        if (piece < numPieces) {
            endBlock = nextBlock + pieceSize;
        } else {
            endBlock = bsize * bsize * bsize;
        }
        final double innerDone = (endBlock % (bsize * bsize)) % bsize; // hmm, + 1 or something?
        final double middleDone = ((endBlock % (bsize * bsize)) - innerLoop) / bsize + 1;
        final double outerDone = (endBlock - innerLoop - middleLoop * bsize) / (bsize * bsize) + 1;

        final int[] returnNumbers = new int[6];
        returnNumbers[0] = innerLoop;
        returnNumbers[1] = middleLoop;
        returnNumbers[2] = outerLoop;
        returnNumbers[3] = (int) innerDone;
        returnNumbers[4] = (int) middleDone;
        returnNumbers[5] = (int) outerDone;
        return returnNumbers;
    }

    public abstract int getTimesUsed();

    /**
     * 
     * @param vm
     */
    public abstract void info(vMessage vm);

    /**
     * A Brush's custom command handler.
     * 
     * @param par
     *            Array of string containing parameters
     * @param v
     *            vSniper caller
     */
    public void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        v.sendMessage(ChatColor.DARK_GREEN + "This brush doesn't take any extra parameters.");
    }

    /**
     * 
     * @param action
     * @param v
     * @param heldItem
     * @param clickedBlock
     * @param clickedFace
     */
    public boolean perform(final Action action, final com.thevoxelbox.voxelsniper.vData v, final Material heldItem, final Block clickedBlock,
            final BlockFace clickedFace) {
        switch (action) {
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            switch (heldItem) {
            case ARROW:
                this.setTimesUsed(this.getTimesUsed() + 1);
                if (this.getTarget(v, clickedBlock, clickedFace)) {
                    this.updateScale();
                    if (this instanceof PerformBrush) {
                        ((PerformBrush) this).initP(v);
                    }
                    this.arrow(v);
                    return true;
                }
                break;

            case SULPHUR:
                this.setTimesUsed(this.getTimesUsed() + 1);
                if (this.getTarget(v, clickedBlock, clickedFace)) {
                    this.updateScale();
                    if (this instanceof PerformBrush) {
                        ((PerformBrush) this).initP(v);
                    }
                    this.powder(v);
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

    public final void setPiece(final int piece) {
        this.currentPiece = piece;
    }

    public abstract void setTimesUsed(int timesUsed);

    public void ThrottledRun(final com.thevoxelbox.voxelsniper.vData v, final int[] pieceNumbers) { // to be overriden by individual brushes
    }

    public void updateScale() {
    }

    /**
     * The arrow action. Executed when a player RightClicks with an Arrow
     * 
     * @param v
     *            vSniper caller
     */
    protected void arrow(final com.thevoxelbox.voxelsniper.vData v) {
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
    protected final int getBlockIdAt(final int ax, final int ay, final int az) {
        return this.w.getBlockAt(ax, ay, az).getTypeId();
    }

    /**
     * Overridable getTarget method.
     * 
     * @param v
     * @param clickedBlock
     * @param clickedFace
     * @return
     */
    protected final boolean getTarget(final com.thevoxelbox.voxelsniper.vData v, final Block clickedBlock, final BlockFace clickedFace) {
        this.w = v.getWorld();
        if (clickedBlock != null) {
            this.tb = clickedBlock;
            this.lb = clickedBlock.getRelative(clickedFace);
            if (this.lb == null) {
                v.sendMessage(ChatColor.RED + "You clicked outside of your sniping range.");
                return false;
            }
            if (v.owner().isLightning()) {
                this.w.strikeLightning(this.tb.getLocation());
            }
            return true;
        } else {
            HitBlox hb = null;
            if (v.owner().isDistRestrict()) {
                hb = new HitBlox(v.owner().getPlayer(), this.w, v.owner().getRange());
                this.tb = hb.getRangeBlock();
            } else {
                hb = new HitBlox(v.owner().getPlayer(), this.w);
                this.tb = hb.getTargetBlock();
            }
            if (this.tb != null) {
                this.lb = hb.getLastBlock();
                if (this.lb == null) {
                    v.sendMessage(ChatColor.RED + "You clicked outside of your sniping range.");
                    return false;
                }
                if (v.owner().isLightning()) {
                    this.w.strikeLightning(this.tb.getLocation());
                }
                return true;
            } else {
                v.sendMessage(ChatColor.RED + "You clicked outside of your sniping range.");
                return false;
            }
        }
    }

    /**
     * The powder action. Executed when a player RightClicks with Gunpowder
     * 
     * @param v
     *            vSniper caller
     */
    protected void powder(final com.thevoxelbox.voxelsniper.vData v) {
    }

    /**
     * 
     * @param v
     */
    protected final void setBlock(final vBlock v) {
        this.w.getBlockAt(v.x, v.y, v.z).setTypeId(v.id);
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
    protected final void setBlockIdAt(final int t, final int ax, final int ay, final int az) {
        this.w.getBlockAt(ax, ay, az).setTypeId(t);
    }
}
