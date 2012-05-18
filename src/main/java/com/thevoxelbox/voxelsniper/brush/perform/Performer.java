/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.vMessage;

/**
 *
 * @author Voxel
 */
public interface Performer {

    public void parse(String[] args, com.thevoxelbox.voxelsniper.vData v);

    public void showInfo(vMessage vm);
}
