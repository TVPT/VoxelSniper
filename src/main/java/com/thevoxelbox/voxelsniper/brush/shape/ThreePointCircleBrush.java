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

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.matrix.Matrix4d;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector4d;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.text.format.TextColors;

@Brush.BrushInfo(
    name = "3-point Circle",
    aliases = {"tpc", "threepointcircle"},
    permission = "voxelsniper.brush.threepointcircle",
    category = Brush.BrushCategory.SHAPE
)
public class ThreePointCircleBrush extends PerformBrush {

    private Vector3d coordsOne;
    private Vector3d coordsTwo;
    private Vector3d coordsThree;

    /**
     * Default Constructor.
     */
    public ThreePointCircleBrush() {
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.coordsOne == null) {
            this.coordsOne = this.targetBlock.getPosition();
            v.sendMessage(TextColors.GRAY, "First Corner set.");
        } else if (this.coordsTwo == null) {
            this.coordsTwo = this.targetBlock.getPosition();
            v.sendMessage(TextColors.GRAY, "Second Corner set.");
        } else if (this.coordsThree == null) {
            this.coordsThree = this.targetBlock.getPosition();
            v.sendMessage(TextColors.GRAY, "Third Corner set.");
        } else {
            this.coordsOne = this.targetBlock.getPosition();
            this.coordsTwo = null;
            this.coordsThree = null;
            v.sendMessage(TextColors.GRAY, "First Corner set.");
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.coordsOne == null || this.coordsTwo == null || this.coordsThree == null) {
            v.sendMessage(TextColors.RED, "ERROR: Set all points before creating the circle");
            return;
        }

        // Calculate triangle defining vectors
        final Vector3d delta1 = this.coordsTwo.sub(this.coordsOne);
        final Vector3d delta2 = this.coordsThree.sub(this.coordsOne);
        final Vector3d normalVector = delta1.cross(delta2);

        if (normalVector.lengthSquared() < GenericMath.DBL_EPSILON) {
            v.sendMessage(TextColors.RED, "ERROR: Invalid points, try again.");
            return;
        }

        // Column vectors for the A matrix since we are trying to solve the equation
        // Ax = b where A is:
        // | x1, y1, z1, 1 |
        // | x2, y2, z2, 1 |
        // | x3, y3, z3, 1 |
        // | Nx, Ny, Nz, 0 |
        // Where xi, yi, zi represents the x, y, or z component of the ith coord and
        // Nx, Ny, and Nz are the components of the normal vector
        // and b is:
        // | -x1^2 - y1^2 - z1^2 |
        // | -x2^2 - y2^2 - z2^2 |
        // | -x3^2 - y3^2 - z3^2 |
        // | -2 * N * coordsOne  |
        //
        // The first three equations come from the equation of a sphere centered on the three
        // points we get while the fourth ensures the point is on the same plane as the other
        // three points in the circle.  This method is loosely based on method 1 from
        // https://www.qc.edu.hk/math/Advanced%20Level/circle%20given%203%20points.htm but has
        // been adapted for 3D.
        Vector4d A1 = new Vector4d(this.coordsOne.getX(), this.coordsTwo.getX(), this.coordsThree.getX(), normalVector.getX());
        Vector4d A2 = new Vector4d(this.coordsOne.getY(), this.coordsTwo.getY(), this.coordsThree.getY(), normalVector.getY());
        Vector4d A3 = new Vector4d(this.coordsOne.getZ(), this.coordsTwo.getZ(), this.coordsThree.getZ(), normalVector.getZ());
        Vector4d A4 = new Vector4d(1, 1, 1, 0);

        double u1 = - this.coordsOne.lengthSquared();
        double u2 = - this.coordsTwo.lengthSquared();
        double u3 = - this.coordsThree.lengthSquared();

        Vector4d b = new Vector4d(u1, u2, u3, -2 * normalVector.dot(this.coordsOne));

        // Use Cramer's rule to calculate the center point
        double detA = columnsToMatrix(A1, A2, A3, A4).determinant();
        double detA1 = columnsToMatrix(b, A2, A3, A4).determinant();
        double detA2 = columnsToMatrix(A1, b, A3, A4).determinant();
        double detA3 = columnsToMatrix(A1, A2, b, A4).determinant();

        Vector3d center = new Vector3d(
                (detA1 / detA) / -2,
                (detA2 / detA) / -2,
                (detA3 / detA) / -2
        );

        double radius = center.distance(this.coordsOne);

        // Create two normal vectors on the plane that can act as the x and y unit vectors for drawing the circle
        Vector3d xPrime = this.coordsOne.sub(center).normalize();
        Vector3d yPrime = normalVector.cross(xPrime).normalize();


        this.undo = new Undo((int) (Math.PI * radius * radius));

        // Use the parametric description of a circle with respect to theta and r to determine where to place the
        // next block
        for (double r = 0; r <= radius; r += .9) {
            double deltaTheta = .8 / r;

            for (double currentAngle = 0; currentAngle <= 2 * Math.PI * r; currentAngle += deltaTheta) {
                Vector3d blockPos = xPrime.mul(Math.cos(currentAngle))
                                            .add(yPrime.mul(Math.sin(currentAngle)))
                                            .mul(r).add(center);

                int x = (int) Math.round(blockPos.getX());
                int y = (int) Math.round(blockPos.getY());
                int z = (int) Math.round(blockPos.getZ());

                perform(v, x, y, z);
            }
        }

        v.sendMessage(TextColors.GREEN, "Done.");
        v.owner().storeUndo(this.undo);
        this.undo = null;

        // Reset Brush
        this.coordsOne = null;
        this.coordsTwo = null;
        this.coordsThree = null;

    }

    private Matrix4d columnsToMatrix(Vector4d a, Vector4d b, Vector4d c, Vector4d d) {
        return new Matrix4d(
                a.getX(), b.getX(), c.getX(), d.getX(),
                a.getY(), b.getY(), c.getY(), d.getY(),
                a.getZ(), b.getZ(), c.getZ(), d.getZ(),
                a.getW(), b.getW(), c.getW(), d.getW()
        );
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length == 1 && par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.YELLOW,
                    "3-Point Circle Brush instructions: Select three corners with the arrow brush, then generate the Circle with the powder brush.");
        } else {
            v.sendMessage(TextColors.RED, "Do /b tpc info for information on this brush");
        }
    }
}
