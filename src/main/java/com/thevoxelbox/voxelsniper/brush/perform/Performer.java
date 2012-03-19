/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;

/**
 *
 * @author Voxel
 */
public interface Performer {

    public void parse(String[] args, vSniper v);

    public void showInfo(vMessage vm);
}
