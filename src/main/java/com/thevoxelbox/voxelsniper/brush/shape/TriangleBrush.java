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
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Creates a triangle shape.
 */
@Brush.BrushInfo(
    name = "Triangle",
    aliases = {"tri", "triangle"},
    permission = "voxelsniper.brush.triangle",
    category = Brush.BrushCategory.SHAPE
)
public class TriangleBrush extends PerformBrush {

    private double[] coordsOne = new double[3]; // Three corners
    private double[] coordsTwo = new double[3];
    private double[] coordsThree = new double[3];
    private int cornernumber = 1;
    private double[] currentCoords = new double[3]; // For loop tracking
    private double[] vectorOne = new double[3]; // Point 1 to 2
    private double[] vectorTwo = new double[3]; // Point 1 to 3
    private double[] vectorThree = new double[3]; // Point 2 to 3, for area
                                                  // calculations
    private double[] normalVector = new double[3];

    public TriangleBrush() {
    }

    private void triangleA(final SnipeData v, Location<World> target) {
        switch (this.cornernumber) {
            case 1:
                this.coordsOne[0] = target.getX() + .5 * target.getX() / Math.abs(target.getX());
                this.coordsOne[1] = target.getY() + .5;
                this.coordsOne[2] = target.getZ() + .5 * target.getZ() / Math.abs(target.getZ());
                this.cornernumber = 2;
                v.sendMessage(TextColors.GRAY, "First Corner set.");
                break;
            case 2:
                this.coordsTwo[0] = target.getX() + .5 * target.getX() / Math.abs(target.getX());
                this.coordsTwo[1] = target.getY() + .5;
                this.coordsTwo[2] = target.getZ() + .5 * target.getZ() / Math.abs(target.getZ());
                this.cornernumber = 3;
                v.sendMessage(TextColors.GRAY, "Second Corner set.");
                break;
            case 3:
                this.coordsThree[0] = target.getX() + .5 * target.getX() / Math.abs(target.getX());
                this.coordsThree[1] = target.getY() + .5;
                this.coordsThree[2] = target.getZ() + .5 * target.getZ() / Math.abs(target.getZ());
                this.cornernumber = 1;
                v.sendMessage(TextColors.GRAY, "Third Corner set. Placing triangle.");
                triangleP(v);
                break;
            default:
                break;

        }

    }

    private void triangleP(final SnipeData v) {
        double lengthOne = 0;
        double lengthTwo = 0;
        double lengthThree = 0;
        double heronBig = 0;

        // Calculate slope vectors
        for (int i = 0; i < 3; i++) {
            this.vectorOne[i] = this.coordsTwo[i] - this.coordsOne[i];
            this.vectorTwo[i] = this.coordsThree[i] - this.coordsOne[i];
            this.vectorThree[i] = this.coordsThree[i] - this.coordsTwo[i];
        }

        // Calculate the cross product of vectorone and vectortwo
        this.normalVector[0] = this.vectorOne[1] * this.vectorTwo[2] - this.vectorOne[2] * this.vectorTwo[1];
        this.normalVector[1] = this.vectorOne[2] * this.vectorTwo[0] - this.vectorOne[0] * this.vectorTwo[2];
        this.normalVector[2] = this.vectorOne[0] * this.vectorTwo[1] - this.vectorOne[1] * this.vectorTwo[0];

        // Calculate magnitude of slope vectors
        lengthOne = Math.pow(Math.pow(this.vectorOne[0], 2) + Math.pow(this.vectorOne[1], 2) + Math.pow(this.vectorOne[2], 2), .5);
        lengthTwo = Math.pow(Math.pow(this.vectorTwo[0], 2) + Math.pow(this.vectorTwo[1], 2) + Math.pow(this.vectorTwo[2], 2), .5);
        lengthThree = Math.pow(Math.pow(this.vectorThree[0], 2) + Math.pow(this.vectorThree[1], 2) + Math.pow(this.vectorThree[2], 2), .5);

        // Bigger vector determines brush size
        final int brushSize = (int) Math.ceil((lengthOne > lengthTwo) ? lengthOne : lengthTwo);

        // Calculate constant term
        final double planeConstant =
                this.normalVector[0] * this.coordsOne[0] + this.normalVector[1] * this.coordsOne[1] + this.normalVector[2] * this.coordsOne[2];

        // Calculate the area of the full triangle
        heronBig = .25 * Math.pow(Math.pow(Math.pow(lengthOne, 2) + Math.pow(lengthTwo, 2) + Math.pow(lengthThree, 2), 2)
                - 2 * (Math.pow(lengthOne, 4) + Math.pow(lengthTwo, 4) + Math.pow(lengthThree, 4)), .5);

        // @Performance, no idea how to size this undo correctly
        this.undo = new Undo(16);
        if (lengthOne == 0 || lengthTwo == 0 || (this.coordsOne[0] == 0 && this.coordsOne[1] == 0 && this.coordsOne[2] == 0)
                || (this.coordsTwo[0] == 0 && this.coordsTwo[1] == 0 && this.coordsTwo[2] == 0)
                || (this.coordsThree[0] == 0 && this.coordsThree[1] == 0 && this.coordsThree[2] == 0)) {
            v.sendMessage(TextColors.RED, "ERROR: Invalid corners, please try again.");
        } else {
            // Make the Changes
            final double[] cVectorOne = new double[3];
            final double[] cVectorTwo = new double[3];
            final double[] cVectorThree = new double[3];

            for (int y = -brushSize; y <= brushSize; y++) { // X DEPENDENT
                for (int z = -brushSize; z <= brushSize; z++) {
                    this.currentCoords[1] = this.coordsOne[1] + y;
                    this.currentCoords[2] = this.coordsOne[2] + z;
                    this.currentCoords[0] =
                            (planeConstant - this.normalVector[1] * this.currentCoords[1] - this.normalVector[2] * this.currentCoords[2])
                                    / this.normalVector[0];

                    // Area of triangle currentcoords, coordsone, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cVectorOne[i] = this.coordsTwo[i] - this.coordsOne[i];
                        cVectorTwo[i] = this.currentCoords[i] - this.coordsOne[i];
                        cVectorThree[i] = this.currentCoords[i] - this.coordsTwo[i];
                    }
                    double cLengthOne = Math.pow(Math.pow(cVectorOne[0], 2) + Math.pow(cVectorOne[1], 2) + Math.pow(cVectorOne[2], 2), .5);
                    double cLengthTwo = Math.pow(Math.pow(cVectorTwo[0], 2) + Math.pow(cVectorTwo[1], 2) + Math.pow(cVectorTwo[2], 2), .5);
                    double cLengthThree = Math.pow(Math.pow(cVectorThree[0], 2) + Math.pow(cVectorThree[1], 2) + Math.pow(cVectorThree[2], 2), .5);

                    final double heronOne = .25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2)
                            - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cVectorOne[i] = this.coordsTwo[i] - this.coordsThree[i];
                        cVectorTwo[i] = this.currentCoords[i] - this.coordsThree[i];
                        cVectorThree[i] = this.currentCoords[i] - this.coordsTwo[i];
                    }
                    cLengthOne = Math.pow(Math.pow(cVectorOne[0], 2) + Math.pow(cVectorOne[1], 2) + Math.pow(cVectorOne[2], 2), .5);
                    cLengthTwo = Math.pow(Math.pow(cVectorTwo[0], 2) + Math.pow(cVectorTwo[1], 2) + Math.pow(cVectorTwo[2], 2), .5);
                    cLengthThree = Math.pow(Math.pow(cVectorThree[0], 2) + Math.pow(cVectorThree[1], 2) + Math.pow(cVectorThree[2], 2), .5);
                    final double heronTwo = .25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2)
                            - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordsone
                    for (int i = 0; i < 3; i++) {
                        cVectorOne[i] = this.coordsOne[i] - this.coordsThree[i];
                        cVectorTwo[i] = this.currentCoords[i] - this.coordsThree[i];
                        cVectorThree[i] = this.currentCoords[i] - this.coordsOne[i];
                    }
                    cLengthOne = Math.pow(Math.pow(cVectorOne[0], 2) + Math.pow(cVectorOne[1], 2) + Math.pow(cVectorOne[2], 2), .5);
                    cLengthTwo = Math.pow(Math.pow(cVectorTwo[0], 2) + Math.pow(cVectorTwo[1], 2) + Math.pow(cVectorTwo[2], 2), .5);
                    cLengthThree = Math.pow(Math.pow(cVectorThree[0], 2) + Math.pow(cVectorThree[1], 2) + Math.pow(cVectorThree[2], 2), .5);
                    final double heronThree =
                            .25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2)
                                    - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), .5);

                    final double barycentric = (heronOne + heronTwo + heronThree) / heronBig;

                    if (barycentric <= 1.1) {

                        perform(v, (int) this.currentCoords[0], (int) this.currentCoords[1], (int) this.currentCoords[2]);

                    }

                }
            } // END X DEPENDENT

            for (int x = -brushSize; x <= brushSize; x++) { // Y DEPENDENT
                for (int z = -brushSize; z <= brushSize; z++) {
                    this.currentCoords[0] = this.coordsOne[0] + x;
                    this.currentCoords[2] = this.coordsOne[2] + z;
                    this.currentCoords[1] =
                            (planeConstant - this.normalVector[0] * this.currentCoords[0] - this.normalVector[2] * this.currentCoords[2])
                                    / this.normalVector[1];

                    // Area of triangle currentcoords, coordsone, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cVectorOne[i] = this.coordsTwo[i] - this.coordsOne[i];
                        cVectorTwo[i] = this.currentCoords[i] - this.coordsOne[i];
                        cVectorThree[i] = this.currentCoords[i] - this.coordsTwo[i];
                    }
                    double cLengthOne = Math.pow(Math.pow(cVectorOne[0], 2) + Math.pow(cVectorOne[1], 2) + Math.pow(cVectorOne[2], 2), .5);
                    double cLengthTwo = Math.pow(Math.pow(cVectorTwo[0], 2) + Math.pow(cVectorTwo[1], 2) + Math.pow(cVectorTwo[2], 2), .5);
                    double cLengthThree = Math.pow(Math.pow(cVectorThree[0], 2) + Math.pow(cVectorThree[1], 2) + Math.pow(cVectorThree[2], 2), .5);

                    final double heronOne = .25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2)
                            - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cVectorOne[i] = this.coordsTwo[i] - this.coordsThree[i];
                        cVectorTwo[i] = this.currentCoords[i] - this.coordsThree[i];
                        cVectorThree[i] = this.currentCoords[i] - this.coordsTwo[i];
                    }
                    cLengthOne = Math.pow(Math.pow(cVectorOne[0], 2) + Math.pow(cVectorOne[1], 2) + Math.pow(cVectorOne[2], 2), .5);
                    cLengthTwo = Math.pow(Math.pow(cVectorTwo[0], 2) + Math.pow(cVectorTwo[1], 2) + Math.pow(cVectorTwo[2], 2), .5);
                    cLengthThree = Math.pow(Math.pow(cVectorThree[0], 2) + Math.pow(cVectorThree[1], 2) + Math.pow(cVectorThree[2], 2), .5);
                    final double heronTwo = .25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2)
                            - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordsone
                    for (int i = 0; i < 3; i++) {
                        cVectorOne[i] = this.coordsOne[i] - this.coordsThree[i];
                        cVectorTwo[i] = this.currentCoords[i] - this.coordsThree[i];
                        cVectorThree[i] = this.currentCoords[i] - this.coordsOne[i];
                    }
                    cLengthOne = Math.pow(Math.pow(cVectorOne[0], 2) + Math.pow(cVectorOne[1], 2) + Math.pow(cVectorOne[2], 2), .5);
                    cLengthTwo = Math.pow(Math.pow(cVectorTwo[0], 2) + Math.pow(cVectorTwo[1], 2) + Math.pow(cVectorTwo[2], 2), .5);
                    cLengthThree = Math.pow(Math.pow(cVectorThree[0], 2) + Math.pow(cVectorThree[1], 2) + Math.pow(cVectorThree[2], 2), .5);
                    final double heronThree =
                            .25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2)
                                    - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), .5);

                    final double barycentric = (heronOne + heronTwo + heronThree) / heronBig;

                    if (barycentric <= 1.1) {

                        perform(v, (int) this.currentCoords[0], (int) this.currentCoords[1], (int) this.currentCoords[2]);

                    }

                }
            } // END Y DEPENDENT
            for (int x = -brushSize; x <= brushSize; x++) { // Z DEPENDENT
                for (int y = -brushSize; y <= brushSize; y++) {
                    this.currentCoords[0] = this.coordsOne[0] + x;
                    this.currentCoords[1] = this.coordsOne[1] + y;
                    this.currentCoords[2] =
                            (planeConstant - this.normalVector[0] * this.currentCoords[0] - this.normalVector[1] * this.currentCoords[1])
                                    / this.normalVector[2];

                    // Area of triangle currentcoords, coordsone, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cVectorOne[i] = this.coordsTwo[i] - this.coordsOne[i];
                        cVectorTwo[i] = this.currentCoords[i] - this.coordsOne[i];
                        cVectorThree[i] = this.currentCoords[i] - this.coordsTwo[i];
                    }
                    double cLengthOne = Math.pow(Math.pow(cVectorOne[0], 2) + Math.pow(cVectorOne[1], 2) + Math.pow(cVectorOne[2], 2), .5);
                    double cLengthTwo = Math.pow(Math.pow(cVectorTwo[0], 2) + Math.pow(cVectorTwo[1], 2) + Math.pow(cVectorTwo[2], 2), .5);
                    double cLengthThree = Math.pow(Math.pow(cVectorThree[0], 2) + Math.pow(cVectorThree[1], 2) + Math.pow(cVectorThree[2], 2), .5);

                    final double heronOne = .25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2)
                            - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cVectorOne[i] = this.coordsTwo[i] - this.coordsThree[i];
                        cVectorTwo[i] = this.currentCoords[i] - this.coordsThree[i];
                        cVectorThree[i] = this.currentCoords[i] - this.coordsTwo[i];
                    }
                    cLengthOne = Math.pow(Math.pow(cVectorOne[0], 2) + Math.pow(cVectorOne[1], 2) + Math.pow(cVectorOne[2], 2), .5);
                    cLengthTwo = Math.pow(Math.pow(cVectorTwo[0], 2) + Math.pow(cVectorTwo[1], 2) + Math.pow(cVectorTwo[2], 2), .5);
                    cLengthThree = Math.pow(Math.pow(cVectorThree[0], 2) + Math.pow(cVectorThree[1], 2) + Math.pow(cVectorThree[2], 2), .5);
                    final double heronTwo = .25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2)
                            - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordsone
                    for (int i = 0; i < 3; i++) {
                        cVectorOne[i] = this.coordsOne[i] - this.coordsThree[i];
                        cVectorTwo[i] = this.currentCoords[i] - this.coordsThree[i];
                        cVectorThree[i] = this.currentCoords[i] - this.coordsOne[i];
                    }
                    cLengthOne = Math.pow(Math.pow(cVectorOne[0], 2) + Math.pow(cVectorOne[1], 2) + Math.pow(cVectorOne[2], 2), .5);
                    cLengthTwo = Math.pow(Math.pow(cVectorTwo[0], 2) + Math.pow(cVectorTwo[1], 2) + Math.pow(cVectorTwo[2], 2), .5);
                    cLengthThree = Math.pow(Math.pow(cVectorThree[0], 2) + Math.pow(cVectorThree[1], 2) + Math.pow(cVectorThree[2], 2), .5);
                    final double heronThree =
                            .25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2)
                                    - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), .5);

                    final double barycentric = (heronOne + heronTwo + heronThree) / heronBig;

                    // VoxelSniper.log.info("Bary: "+barycentric+", hb:
                    // "+heronbig+", h1: "+heronone+", h2: "+herontwo+", h3:
                    // "+heronthree);

                    if (barycentric <= 1.1) {
                        perform(v, (int) this.currentCoords[0], (int) this.currentCoords[1], (int) this.currentCoords[2]);
                    }
                }
            } // END Z DEPENDENT

            v.owner().storeUndo(this.undo);
            this.undo = null;
        }

        // RESET BRUSH
        this.coordsOne[0] = 0;
        this.coordsOne[1] = 0;
        this.coordsOne[2] = 0;
        this.coordsTwo[0] = 0;
        this.coordsTwo[1] = 0;
        this.coordsTwo[2] = 0;
        this.coordsThree[0] = 0;
        this.coordsThree[1] = 0;
        this.coordsThree[2] = 0;

        this.cornernumber = 1;

    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.triangleA(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.triangleA(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD,
                    "Triangle Brush instructions: Select three corners with the arrow or powder brush. The triangle will be placed after the third corner.");
        }
        this.cornernumber = 1;
    }
}
