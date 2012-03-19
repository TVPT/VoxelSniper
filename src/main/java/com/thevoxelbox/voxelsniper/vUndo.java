package com.thevoxelbox.voxelsniper;

import java.util.HashSet;
import org.bukkit.World;

import org.bukkit.block.Block;

/**
 * VoxelUndo class holds block data in form of vBlock objects
 *
 * @author Voxel
 */
public class vUndo {

    private HashSet<vBlock> hm = new HashSet<vBlock>();
    private HashSet<vBlock> fall = new HashSet<vBlock>();
    private HashSet<vBlock> drop = new HashSet<vBlock>();
    private String worldName;
    private World w;

    /**
     * Default constructor of a vUndo container
     *
     * @param wName name of the world the blocks reside in
     */
    public vUndo(String wName) {
        worldName = wName;
    }

    /**
     * Adds a Block to the collection
     *
     * @param b Block to be added
     */
    public void put(Block b) {
        this.hm.add(new vBlock(b));
    }

    /**
     * Adds a vBlock to the collection
     *
     * @param b vBlock to be added
     */
    public void put(vBlock b) {
        hm.add(b);
    }

    /**
     * Get the number of blocks in the collection
     *
     * @return size of the vUndo collection
     */
    public int getSize() {
        return hm.size();
    }

    /**
     * This method begins the process of replacing the blocks stored in this
     * collection
     */
    public void undo() {
        w = VoxelSniper.s.getWorld(worldName);
        for (vBlock vb : hm) {
            if (fallsOff(vb.id)) {
                fall.add(vb);
            } else if (falling(vb.id)) {
                drop.add(vb);
            } else {
                setBlock(vb);
            }
        }
        for (vBlock vb : drop) {
            setBlock(vb);
        }
        for (vBlock vb : fall) {
            setBlock(vb);
        }
    }

    private void setBlock(vBlock vb) {
        Block b = w.getBlockAt(vb.x, vb.y, vb.z);
        b.setTypeIdAndData(vb.id, vb.d, false);
    }

    /**
     * Checks whether a block falls off. Doesn't stay in mid air.
     * 
     * @param id The id to be checked.
     * @return true if the block falls off. Otherwise false
     */
    public static boolean fallsOff(int id) {
        switch (id) {
            // 6, 37, 38, 39, 40, 50, 51, 55, 59, 63, 64, 69, 70, 71, 72, 75, 76, 77, 83
            case (6):
                return true;

            case (37):
                return true;

            case (38):
                return true;

            case (39):
                return true;

            case (40):
                return true;

            case (50):
                return true;

            case (51):
                return true;

            case (59):
                return true;

            case (63):
                return true;

            case (64):
                return true;

            case (69):
                return true;

            case (70):
                return true;

            case (71):
                return true;

            case (72):
                return true;

            case (75):
                return true;

            case (76):
                return true;

            case (77):
                return true;

            case (83):
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks whether a block falls. Water, lava, sand, gravel etc.
     * 
     * @param id The id to be checked.
     * @return true if the block falls. Otherwise false
     */
    public static boolean falling(int id) {
        if (id > 7 && id < 14) {
            return true;
        } else {
            return false;
        }
    }
}
