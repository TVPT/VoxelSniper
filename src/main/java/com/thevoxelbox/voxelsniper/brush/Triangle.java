package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * @author Giltwist
 */
public class Triangle extends PerformBrush {

    protected boolean first = true;
    protected double[] coordsone = new double[3]; // Three corners
    protected double[] coordstwo = new double[3];
    protected double[] coordsthree = new double[3];
    protected int cornernumber = 1;
    protected double[] currentcoords = new double[3]; // For loop tracking
    protected double[] vectorone = new double[3]; // Point 1 to 2
    protected double[] vectortwo = new double[3]; // Point 1 to 3
    protected double[] vectorthree = new double[3]; // Point 2 to 3, for area calculations
    protected double[] normalvector = new double[3];

    private static int timesUsed = 0;

    public Triangle() {
        this.name = "Triangle";
    }

    @Override
    public final int getTimesUsed() {
        return Triangle.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) { // Make the triangle
        vm.brushName(this.name);
        // vm.voxel();
        // vm.custom(ChatColor.GRAY + "First Corner: " + coordsone[0] + ", " + coordsone[1] + ", " + coordsone[2] + ")");
        // vm.custom(ChatColor.GRAY + "Second Corner: " + coordstwo[0] + ", " + coordstwo[1] + ", " + coordstwo[2] + ")");
        // vm.custom(ChatColor.GRAY + "Third Corner: " + coordsthree[0] + ", " + coordsthree[1] + ", " + coordsthree[2] + ")");
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD
                    + "Triangle Brush instructions: Select three corners with the arrow brush, then generate the triangle with the powder brush.");
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Triangle.timesUsed = tUsed;
    }

    public final void TriangleA(final vData v) {
        switch (this.cornernumber) {
        case 1:
            this.coordsone[0] = this.tb.getX() + .5 * this.tb.getX() / Math.abs(this.tb.getX()); // I hate you sometimes, Notch. Really? Every quadrant is
                                                                                                 // different?
            this.coordsone[1] = this.tb.getY() + .5;
            this.coordsone[2] = this.tb.getZ() + .5 * this.tb.getZ() / Math.abs(this.tb.getZ());
            this.cornernumber = 2;
            v.sendMessage(ChatColor.GRAY + "First Corner set.");
            break;
        case 2:
            this.coordstwo[0] = this.tb.getX() + .5 * this.tb.getX() / Math.abs(this.tb.getX()); // I hate you sometimes, Notch. Really? Every quadrant is
                                                                                                 // different?
            this.coordstwo[1] = this.tb.getY() + .5;
            this.coordstwo[2] = this.tb.getZ() + .5 * this.tb.getZ() / Math.abs(this.tb.getZ());
            this.cornernumber = 3;
            v.sendMessage(ChatColor.GRAY + "Second Corner set.");
            break;
        case 3:
            this.coordsthree[0] = this.tb.getX() + .5 * this.tb.getX() / Math.abs(this.tb.getX()); // I hate you sometimes, Notch. Really? Every quadrant is
                                                                                                   // different?
            this.coordsthree[1] = this.tb.getY() + .5;
            this.coordsthree[2] = this.tb.getZ() + .5 * this.tb.getZ() / Math.abs(this.tb.getZ());
            this.cornernumber = 1;
            v.sendMessage(ChatColor.GRAY + "Third Corner set.");
            break;
        default:
            break;

        }

    }

    public final void TriangleP(final vData v) {
        this.w = v.owner().getPlayer().getWorld();
        double lengthone = 0;
        double lengthtwo = 0;
        double lengththree = 0;
        double heronbig = 0;

        // Calculate slope vectors
        for (int i = 0; i < 3; i++) {
            this.vectorone[i] = this.coordstwo[i] - this.coordsone[i];
            this.vectortwo[i] = this.coordsthree[i] - this.coordsone[i];
            this.vectorthree[i] = this.coordsthree[i] - this.coordstwo[i];
        }

        // Calculate the cross product of vectorone and vectortwo
        this.normalvector[0] = this.vectorone[1] * this.vectortwo[2] - this.vectorone[2] * this.vectortwo[1];
        this.normalvector[1] = this.vectorone[2] * this.vectortwo[0] - this.vectorone[0] * this.vectortwo[2];
        this.normalvector[2] = this.vectorone[0] * this.vectortwo[1] - this.vectorone[1] * this.vectortwo[0];

        // Calculate magnitude of slope vectors
        lengthone = Math.pow(Math.pow(this.vectorone[0], 2) + Math.pow(this.vectorone[1], 2) + Math.pow(this.vectorone[2], 2), .5);
        lengthtwo = Math.pow(Math.pow(this.vectortwo[0], 2) + Math.pow(this.vectortwo[1], 2) + Math.pow(this.vectortwo[2], 2), .5);
        lengththree = Math.pow(Math.pow(this.vectorthree[0], 2) + Math.pow(this.vectorthree[1], 2) + Math.pow(this.vectorthree[2], 2), .5);

        // Bigger vector determines brush size
        final int bsize = (int) Math.ceil((lengthone > lengthtwo) ? lengthone : lengthtwo);

        // Calculate constant term
        final double planeconstant = this.normalvector[0] * this.coordsone[0] + this.normalvector[1] * this.coordsone[1] + this.normalvector[2]
                * this.coordsone[2];

        // Calculate the area of the full triangle
        heronbig = .25 * Math.pow(
                Math.pow(Math.pow(lengthone, 2) + Math.pow(lengthtwo, 2) + Math.pow(lengththree, 2), 2) - 2
                        * (Math.pow(lengthone, 4) + Math.pow(lengthtwo, 4) + Math.pow(lengththree, 4)), .5);

        if (lengthone == 0 || lengthtwo == 0 || (this.coordsone[0] == 0 && this.coordsone[1] == 0 && this.coordsone[2] == 0)
                || (this.coordstwo[0] == 0 && this.coordstwo[1] == 0 && this.coordstwo[2] == 0)
                || (this.coordsthree[0] == 0 && this.coordsthree[1] == 0 && this.coordsthree[2] == 0)) {
            v.sendMessage(ChatColor.RED + "ERROR: Invalid corners, please try again.");
        } else {
            // Make the Changes

            final double[] cvectorone = new double[3];
            final double[] cvectortwo = new double[3];
            final double[] cvectorthree = new double[3];

            double clengthone = 0;
            double clengthtwo = 0;
            double clengththree = 0;

            double heronone;
            double herontwo;
            double heronthree;

            for (int y = -bsize; y <= bsize; y++) { // X DEPENDENT
                for (int z = -bsize; z <= bsize; z++) {

                    this.currentcoords[1] = this.coordsone[1] + y;
                    this.currentcoords[2] = this.coordsone[2] + z;
                    this.currentcoords[0] = (planeconstant - this.normalvector[1] * this.currentcoords[1] - this.normalvector[2] * this.currentcoords[2])
                            / this.normalvector[0];

                    // Area of triangle currentcoords, coordsone, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = this.coordstwo[i] - this.coordsone[i];
                        cvectortwo[i] = this.currentcoords[i] - this.coordsone[i];
                        cvectorthree[i] = this.currentcoords[i] - this.coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);

                    heronone = .25 * Math.pow(
                            Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2
                                    * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = this.coordstwo[i] - this.coordsthree[i];
                        cvectortwo[i] = this.currentcoords[i] - this.coordsthree[i];
                        cvectorthree[i] = this.currentcoords[i] - this.coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    herontwo = .25 * Math.pow(
                            Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2
                                    * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordsone
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = this.coordsone[i] - this.coordsthree[i];
                        cvectortwo[i] = this.currentcoords[i] - this.coordsthree[i];
                        cvectorthree[i] = this.currentcoords[i] - this.coordsone[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    heronthree = .25 * Math.pow(
                            Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2
                                    * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    final double barycentric = (heronone + herontwo + heronthree) / heronbig;

                    // VoxelSniper.log.info("Bary: "+barycentric+", hb: "+heronbig+", h1: "+heronone+", h2: "+herontwo+", h3: "+heronthree);

                    if (barycentric <= 1.1) {

                        this.current.perform(this.clampY((int) this.currentcoords[0], (int) this.currentcoords[1], (int) this.currentcoords[2]));

                    }

                }
            } // END X DEPENDENT

            for (int x = -bsize; x <= bsize; x++) { // Y DEPENDENT
                for (int z = -bsize; z <= bsize; z++) {

                    this.currentcoords[0] = this.coordsone[0] + x;
                    this.currentcoords[2] = this.coordsone[2] + z;
                    this.currentcoords[1] = (planeconstant - this.normalvector[0] * this.currentcoords[0] - this.normalvector[2] * this.currentcoords[2])
                            / this.normalvector[1];

                    // Area of triangle currentcoords, coordsone, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = this.coordstwo[i] - this.coordsone[i];
                        cvectortwo[i] = this.currentcoords[i] - this.coordsone[i];
                        cvectorthree[i] = this.currentcoords[i] - this.coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);

                    heronone = .25 * Math.pow(
                            Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2
                                    * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = this.coordstwo[i] - this.coordsthree[i];
                        cvectortwo[i] = this.currentcoords[i] - this.coordsthree[i];
                        cvectorthree[i] = this.currentcoords[i] - this.coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    herontwo = .25 * Math.pow(
                            Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2
                                    * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordsone
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = this.coordsone[i] - this.coordsthree[i];
                        cvectortwo[i] = this.currentcoords[i] - this.coordsthree[i];
                        cvectorthree[i] = this.currentcoords[i] - this.coordsone[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    heronthree = .25 * Math.pow(
                            Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2
                                    * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    final double barycentric = (heronone + herontwo + heronthree) / heronbig;

                    // VoxelSniper.log.info("Bary: "+barycentric+", hb: "+heronbig+", h1: "+heronone+", h2: "+herontwo+", h3: "+heronthree);

                    if (barycentric <= 1.1) {

                        this.current.perform(this.clampY((int) this.currentcoords[0], (int) this.currentcoords[1], (int) this.currentcoords[2]));

                    }

                }
            } // END Y DEPENDENT
            for (int x = -bsize; x <= bsize; x++) { // Z DEPENDENT
                for (int y = -bsize; y <= bsize; y++) {

                    this.currentcoords[0] = this.coordsone[0] + x;
                    this.currentcoords[1] = this.coordsone[1] + y;
                    this.currentcoords[2] = (planeconstant - this.normalvector[0] * this.currentcoords[0] - this.normalvector[1] * this.currentcoords[1])
                            / this.normalvector[2];

                    // Area of triangle currentcoords, coordsone, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = this.coordstwo[i] - this.coordsone[i];
                        cvectortwo[i] = this.currentcoords[i] - this.coordsone[i];
                        cvectorthree[i] = this.currentcoords[i] - this.coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);

                    heronone = .25 * Math.pow(
                            Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2
                                    * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = this.coordstwo[i] - this.coordsthree[i];
                        cvectortwo[i] = this.currentcoords[i] - this.coordsthree[i];
                        cvectorthree[i] = this.currentcoords[i] - this.coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    herontwo = .25 * Math.pow(
                            Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2
                                    * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordsone
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = this.coordsone[i] - this.coordsthree[i];
                        cvectortwo[i] = this.currentcoords[i] - this.coordsthree[i];
                        cvectorthree[i] = this.currentcoords[i] - this.coordsone[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    heronthree = .25 * Math.pow(
                            Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2
                                    * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    final double barycentric = (heronone + herontwo + heronthree) / heronbig;

                    // VoxelSniper.log.info("Bary: "+barycentric+", hb: "+heronbig+", h1: "+heronone+", h2: "+herontwo+", h3: "+heronthree);

                    if (barycentric <= 1.1) {

                        this.current.perform(this.clampY((int) this.currentcoords[0], (int) this.currentcoords[1], (int) this.currentcoords[2]));

                    }

                }
            } // END Z DEPENDENT

            v.storeUndo(this.current.getUndo());

        }

        // RESET BRUSH
        this.coordsone[0] = 0;
        this.coordsone[1] = 0;
        this.coordsone[2] = 0;
        this.coordstwo[0] = 0;
        this.coordstwo[1] = 0;
        this.coordstwo[2] = 0;
        this.coordsthree[0] = 0;
        this.coordsthree[1] = 0;
        this.coordsthree[2] = 0;

        this.cornernumber = 1;

    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {

        this.TriangleA(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) { // Add a point

        this.TriangleP(v);

    }
}
