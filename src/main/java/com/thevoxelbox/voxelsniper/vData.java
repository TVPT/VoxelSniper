/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper;

import org.bukkit.World;

import com.thevoxelbox.voxelsniper.util.VoxelList;

/**
 * 
 * @author Piotr
 */
public class vData {

    public static final int DEFAULT_REPLACE_DATA_VALUE = 0;
    public static final int DEFAULT_CYLINDER_CENTER = 0;
    public static final int DEFAULT_VOXEL_HEIGHT = 1;
    public static final int DEFAULT_BRUSH_SIZE = 3;
    public static final int DEFAULT_DATA_VALUE = 0;
    public static final int DEFAULT_REPLACE_ID = 0;
    public static final int DEFAULT_VOXEL_ID = 0;

    private final vSniper owner;
    public vMessage vm;
    /**
     * Brush size -- set by /b #.
     */
    public int brushSize = DEFAULT_BRUSH_SIZE;
    /**
     * Voxel Id -- set by /v (#,name).
     */
    public int voxelId = DEFAULT_VOXEL_ID;
    /**
     * Voxel Replace Id -- set by /vr #.
     */
    public int replaceId = DEFAULT_REPLACE_ID;
    /**
     * Voxel 'ink' -- set by /vi #.
     */
    public byte data = DEFAULT_DATA_VALUE;
    /**
     * Voxel 'ink' Replace -- set by /vir #.
     */
    public byte replaceData = DEFAULT_REPLACE_DATA_VALUE;
    /**
     * Voxel List of ID's -- set by /vl # # # -#.
     */
    public VoxelList voxelList = new VoxelList();
    /**
     * Voxel 'heigth' -- set by /vh #.
     */
    public int voxelHeight = DEFAULT_VOXEL_HEIGHT;
    public int cCen = DEFAULT_CYLINDER_CENTER;

    /**
     * @param vs
     */
    public vData(final vSniper vs) {
        this.owner = vs;
    }

    /**
     * @return World
     */
    public final World getWorld() {
        return this.owner.p.getWorld();
    }

    /**
     * @return vSniper
     */
    public final vSniper owner() {
        return this.owner;
    }

    /**
     * Reset to default values.
     */
    public final void reset() {
        this.voxelId = vData.DEFAULT_VOXEL_ID;
        this.replaceId = vData.DEFAULT_REPLACE_ID;
        this.data = vData.DEFAULT_DATA_VALUE;
        this.brushSize = vData.DEFAULT_BRUSH_SIZE;
        this.voxelHeight = vData.DEFAULT_VOXEL_HEIGHT;
        this.cCen = vData.DEFAULT_CYLINDER_CENTER;
        this.replaceData = vData.DEFAULT_REPLACE_DATA_VALUE;
    }

    /**
     * @param message
     */
    public final void sendMessage(final String message) {
        this.owner.p.sendMessage(message);
    }

    /**
     * @param vundo
     */
    public final void storeUndo(final com.thevoxelbox.voxelsniper.undo.vUndo vundo) {
        this.owner.storeUndo(vundo);
    }
}
