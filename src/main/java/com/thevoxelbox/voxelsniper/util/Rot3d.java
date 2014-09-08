package com.thevoxelbox.voxelsniper.util;

/**
 * Implements a rotation matrix to assist with rotating points in 3d space with an arbitrary yaw, pitch, and roll.
 * 
 * @author Deamon
 */
public class Rot3d
{
    private double[][] r = new double[3][3];

    public Rot3d(double u, double v, double w)
    {
        double cos1 = Math.cos(u);
        double sin1 = Math.sin(u);

        double cos2 = Math.cos(v);
        double sin2 = Math.sin(v);

        double cos3 = Math.cos(w);
        double sin3 = Math.sin(w);

        r[0][0] = cos1 * cos2;
        r[0][1] = sin1 * sin3 - cos1 * cos3 * sin2;
        r[0][2] = cos3 * sin1 + cos1 * sin2 * sin3;
        r[1][0] = sin2;
        r[1][1] = cos2 * cos3;
        r[1][2] = -cos2 * sin3;
        r[2][0] = -cos2 * sin1;
        r[2][1] = cos1 * sin3 + cos3 * sin1 * sin2;
        r[2][2] = cos1 * cos3 - sin1 * sin2 * sin3;

    }

    /**
     * Performs the rotation operation on the given x, y, and z coordinates relative to the origin.
     * 
     * @param x
     * @param y
     * @param z
     * @return the final position
     */
    public double[] doRotation(double x, double y, double z)
    {
        double[] p = new double[3];
        p[0] = r[0][0] * x + r[0][1] * y + r[0][2] * z;
        p[1] = r[1][0] * x + r[1][1] * y + r[1][2] * z;
        p[2] = r[2][0] * x + r[2][1] * y + r[2][2] * z;
        return p;
    }

    /**
     * Performs the rotation operation on the given x, y, and z coordinates relative to the origin.
     * 
     * @param xyz
     * @return the final position
     */
    public double[] doRotation(double[] xyz)
    {
        double[] p = new double[3];
        p[0] = r[0][0] * xyz[0] + r[0][1] * xyz[1] + r[0][2] * xyz[2];
        p[1] = r[1][0] * xyz[0] + r[1][1] * xyz[1] + r[1][2] * xyz[2];
        p[2] = r[2][0] * xyz[0] + r[2][1] * xyz[1] + r[2][2] * xyz[2];
        return p;
    }
}
