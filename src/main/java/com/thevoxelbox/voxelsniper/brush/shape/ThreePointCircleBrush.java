/*
 * This file is part of VoxelSniper, licensed under the MIT License (MIT).
 *
 * Copyright (c) The VoxelBox <http://thevoxelbox.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.thevoxelbox.voxelsniper.brush.shape;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.text.format.TextColors;

public class ThreePointCircleBrush extends PerformBrush {

    private Vector3d coordsOne;
    private Vector3d coordsTwo;
    private Vector3d coordsThree;
    private Tolerance tolerance = Tolerance.DEFAULT;

    /**
     * Default Constructor.
     */
    public ThreePointCircleBrush() {
        this.setName("3-Point Circle");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.coordsOne == null) {
            this.coordsOne = this.targetBlock.getPosition().add(0.5, 0.5, 0.5);
            v.sendMessage(TextColors.GRAY, "First Corner set.");
        } else if (this.coordsTwo == null) {
            this.coordsTwo = this.targetBlock.getPosition().add(0.5, 0.5, 0.5);
            v.sendMessage(TextColors.GRAY, "Second Corner set.");
        } else if (this.coordsThree == null) {
            this.coordsThree = this.targetBlock.getPosition().add(0.5, 0.5, 0.5);
            v.sendMessage(TextColors.GRAY, "Third Corner set.");
        } else {
            this.coordsOne = this.targetBlock.getPosition().add(0.5, 0.5, 0.5);
            this.coordsTwo = null;
            this.coordsThree = null;
            v.sendMessage(TextColors.GRAY, "First Corner set.");
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.coordsOne == null || this.coordsTwo == null || this.coordsThree == null) {
            return;
        }
        v.sendMessage(TextColors.RED, "Unfortunately the three-point circle is currently disabled as it does not work :(");
        // Calculate triangle defining vectors
//        final Vector3d vectorOne = this.coordsTwo.sub(this.coordsOne);
//        final Vector3d vectorTwo = this.coordsThree.sub(this.coordsOne);
//        final Vector3d vectorThree = this.coordsThree.sub(vectorTwo);
//
//        // Redundant data check
//        if (vectorOne.length() == 0 || vectorTwo.length() == 0 || vectorThree.length() == 0) {
//            v.sendMessage(TextColors.RED, "ERROR: Invalid points, try again.");
//            this.coordsOne = null;
//            this.coordsTwo = null;
//            this.coordsThree = null;
//            return;
//        }
//
//        // Calculate normal vector of the plane.
//        final Vector3d normalVector = vectorOne.cross(vectorTwo);
//
//        // Calculate constant term of the plane.
//        final double planeConstant = normalVector.getX() * this.coordsOne.getX() + normalVector.getY() * this.coordsOne.getY()
//                + normalVector.getZ() * this.coordsOne.getZ();
//
//        final Vector3d midpointOne = this.coordsOne.add(this.coordsTwo.sub(this.coordsOne).mul(0.5));
//        final Vector3d midpointTwo = this.coordsOne.add(this.coordsThree.sub(this.coordsOne).mul(0.5));
//
//        // Find perpendicular vectors to two sides in the plane
//        final Vector3d perpendicularOne = normalVector.cross(vectorOne);
//        final Vector3d perpendicularTwo = normalVector.cross(vectorTwo);
//
//        // determine value of parametric variable at intersection of two
//        // perpendicular bisectors
//        final Vector3d tNumerator = midpointTwo.sub(midpointOne).cross(perpendicularTwo);
//        final Vector3d tDenominator = perpendicularOne.cross(perpendicularTwo);
//        final double t = tNumerator.length() / tDenominator.length();
//
//        // Calculate Circumcenter and Brushcenter.
//        final Vector3d circumcenter = perpendicularOne.mul(t);
//        circumcenter.add(midpointOne);
//
//        final Vector3d brushCenter = new Vector3d(Math.round(circumcenter.getX()), Math.round(circumcenter.getY()), Math.round(circumcenter.getZ()));
//
//        // Calculate radius of circumcircle and determine brushsize
//        final double radius = circumcenter.distance(this.coordsOne);
//        final int brushSize = (int) (Math.ceil(radius) + 1);
//        this.undo = new Undo((int) (4 * v.getBrushSize() * v.getBrushSize()));
//        for (int x = -brushSize; x <= brushSize; x++) {
//            for (int y = -brushSize; y <= brushSize; y++) {
//                for (int z = -brushSize; z <= brushSize; z++) {
//                    // Calculate distance from center
//                    final double tempDistance = Math.pow(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2), .5);
//
//                    // gets corner-on blocks
//                    final double cornerConstant = normalVector.getX() * (circumcenter.getX() + x) + normalVector.getY() * (circumcenter.getY() + y)
//                            + normalVector.getZ() * (circumcenter.getZ() + z);
//
//                    // gets center-on blocks
//                    final double centerConstant = normalVector.getX() * (circumcenter.getX() + x + .5)
//                            + normalVector.getY() * (circumcenter.getY() + y + .5) + normalVector.getZ() * (circumcenter.getZ() + z + .5);
//
//                    // Check if point is within sphere and on plane (some
//                    // tolerance given)
//                    if (tempDistance <= radius && (Math.abs(cornerConstant - planeConstant) < this.tolerance.getValue()
//                            || Math.abs(centerConstant - planeConstant) < this.tolerance.getValue())) {
//                        perform(v, brushCenter.getFloorX() + x, brushCenter.getFloorY() + y, brushCenter.getFloorZ() + z);
//                    }
//
//                }
//            }
//        }
//
//        v.sendMessage(TextColors.GREEN, "Done.");
//        v.owner().storeUndo(this.undo);
//        this.undo = null;
//
//        // Reset Brush
//        this.coordsOne = null;
//        this.coordsTwo = null;
//        this.coordsThree = null;

    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        switch (this.tolerance) {
            case ACCURATE:
                vm.custom(TextColors.GOLD, "Mode: Accurate");
                break;
            case DEFAULT:
                vm.custom(TextColors.GOLD, "Mode: Default");
                break;
            case SMOOTH:
                vm.custom(TextColors.GOLD, "Mode: Smooth");
                break;
            default:
                vm.custom(TextColors.GOLD, "Mode: Unknown");
                break;
        }

    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length > 0 && par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.YELLOW,
                    "3-Point Circle Brush instructions: Select three corners with the arrow brush, then generate the Circle with the powder brush.");
            String toleranceOptions = "";
            for (final Tolerance tolerance : Tolerance.values()) {
                if (!toleranceOptions.isEmpty()) {
                    toleranceOptions += "|";
                }
                toleranceOptions += tolerance.name().toLowerCase();
            }
            v.sendMessage(TextColors.GOLD, "/b tpc " + toleranceOptions + " -- Toggle the calculations to emphasize accuracy or smoothness");
            return;
        }

        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i].toUpperCase();
            try {
                this.tolerance = Tolerance.valueOf(parameter);
                v.sendMessage(TextColors.AQUA, "Brush set to " + this.tolerance.name().toLowerCase() + " tolerance.");
                return;
            } catch (final IllegalArgumentException exception) {
                v.getVoxelMessage().brushMessage("No such tolerance.");
            }
        }
    }

    /**
     * Enumeration on Tolerance values.
     *
     * @author MikeMatrix
     */
    private enum Tolerance {
        DEFAULT(1000), ACCURATE(10), SMOOTH(2000);

        private int value;

        Tolerance(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.threepointcircle";
    }
}
