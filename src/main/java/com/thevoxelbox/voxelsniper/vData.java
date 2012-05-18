/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.util.VoxelList;
import org.bukkit.World;

/**
 *
 * @author Piotr
 */
public class vData {

    private vSniper owner;
    public vMessage vm;
    public int brushSize = 3;                       // Brush size  --  set by /b #
    public int voxelId;                             // Voxel Id   --   set by /v (#,name)
    public int replaceId;                           // Voxel Replace Id   --   set by /vr #
    public byte data;                               // Voxel 'ink'  --   set by /vi #
    public byte replaceData;                        // Voxel 'ink' Replace -- set by /vir #
    public VoxelList voxelList = new VoxelList();   // Voxel List of ID's -- set by /vl # # # -#
    public int voxelHeight;                         // Voxel 'heigth'   --  set by /vh #
    public int cCen;

    public vData(vSniper vs) {
        owner = vs;
    }

    public vSniper owner() {
        return owner;
    }

    public void reset() {
        voxelId = 0;
        replaceId = 0;
        data = 0;
        brushSize = 3;
        voxelHeight = 1;
        cCen = 0;
        replaceData = 0;
    }

    public void storeUndo(com.thevoxelbox.voxelsniper.undo.vUndo vundo) {
        owner.storeUndo(vundo);
    }

    public void sendMessage(String message) {
        owner.p.sendMessage(message);
    }
    
    public World getWorld() {
        return owner.p.getWorld();
    }
}
