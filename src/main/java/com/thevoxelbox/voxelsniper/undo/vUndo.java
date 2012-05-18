package com.thevoxelbox.voxelsniper.undo;

import com.thevoxelbox.voxelsniper.VoxelSniper;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * VoxelUndo class holds block data in form of vBlock objects
 *
 * @author Voxel
 */
public class vUndo {

    private uCollection all;
    private uCollection falloff;
    private uCollection dropdown;
    private String worldName;
    private World w;

    /**
     * Default constructor of a vUndo container
     *
     * @param wName name of the world the blocks reside in
     */
    public vUndo(String wName) {
        worldName = wName;
        all = new uCollection();
        falloff = new uCollection();
        dropdown = new uCollection();
    }

    public vUndo(String wName, int scale) {
        worldName = wName;
        all = new uCollection(scale);
        falloff = new uCollection(scale);
        dropdown = new uCollection(scale);
    }

    /**
     * Adds a Block to the collection
     *
     * @param b Block to be added
     */
    public void put(Block b) { // 63 68 
        if (b.getTypeId() == 63 || b.getTypeId() == 68) {
            all.add(new uBlockSign(b));
        } else if (b.getTypeId() == 25) {
            all.add(new uBlockNote(b));
        } else {
            all.add(new uBlock(b));
        }
    }

    /**
     * Adds a vBlock to the collection
     *
     * @param b vBlock to be added
     */
//    public void put(vBlock b) {
//        hm.add(b);
//    }
    public void put(uBlock b) {
        all.add(b);
    }

    /**
     * Get the number of blocks in the collection
     *
     * @return size of the vUndo collection
     */
    public int getSize() {
        return all.getSize();
    }

    /**
     * This method begins the process of replacing the blocks stored in this
     * collection
     */
    public void undo() {
        w = VoxelSniper.s.getWorld(worldName);
        if(all.getSize() == 0) {
            return;
        }
        uIterator itr = all.getIterator();
        do {
            sort(itr.getNext());
        } while (itr.hasNext());

        falloff.setAll(w);
        dropdown.setAll(w);
    }

    private void sort(uBlock b) {
        if (fallsOff(b.id)) {
            falloff.add(b);
        } else if (falling(b.id)) {
            dropdown.add(b);
        } else {
            b.set(w);
        }
    }

    private void setBlock(vBlock vb) {
        w.getBlockAt(vb.x, vb.y, vb.z).setTypeIdAndData(vb.id, vb.d, false);
    }

    /**
     * Checks whether a block falls off. Doesn't stay in mid air.
     * 
     * @param id The id to be checked.
     * @return true if the block falls off. Otherwise false
     */
    public static boolean fallsOff(int id) {  // Converted to binary tree. Will really only make a difference when Undo'ing things 100+'ish (But will make ClonePaint faster)

        // 6-
        // 26  27  28-
        // 31  32-
        // 34-
        // 37  38  39  40-
        // 50  51-
        // 59-
        // 63  64  65  66-
        // 68  69  70  71  72-
        // 75  76  77  78-
        // 81-
        // 83-
        // 92  93  94-
        // 96-
        // 104 105 106-
        // 111-
        // 115-

        if (id > 5) {
            if (id < 59) {
                if (id < 34) {
                    if (id < 26) {
                        if (id == 6) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        if (id < 29) {
                            return true;
                        } else {
                            if (id < 33 && id > 30) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                } else if (id > 34) {
                    if (id > 40) {
                        if (id > 49 && id < 52) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (id > 38) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else if (id > 59) {
                if (id < 83) {
                    if (id < 68) {
                        if (id > 62 && id < 67) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        if (id < 73) {
                            return true;
                        } else {
                            if (id > 74) {
                                if (id < 79 || id == 81) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    }
                } else if (id > 83) {
                    if (id < 96) {
                        if (id > 91 && id < 95) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (id > 96) {
                        if (id < 111) {
                            if (id > 103 && id < 107) {
                                return true;
                            } else {
                                return false;
                            }
                        } else if (id > 111) {
                            if (id == 115) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }

        // 6-
        // 26  27  28-
        // 31  32-
        // 34-
        // 37  38  39  40-
        // 50  51-
        // 59-
        // 63  64  65  66-
        // 68  69  70  71  72-
        // 75  76  77  78-
        // 81-
        // 83-
        // 92  93  94-
        // 96-
        // 104 105 106-
        // 111-
        // 115-
    }

    /**
     * Checks whether a block falls. Water, lava, sand, gravel etc.
     * 
     * @param id The id to be checked.
     * @return true if the block falls. Otherwise false
     */
    public static boolean falling(int id) {
        if ((id > 7 && id < 14) || id == 122) {
            return true;
        } else {
            return false;
        }
    }
}
