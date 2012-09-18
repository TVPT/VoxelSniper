package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * @author Giltwist
 */
public class TriangleBrush extends PerformBrush {
	private static int timesUsed = 0;
    private double[] coordsOne = new double[3]; // Three corners
    private double[] coordsTwo = new double[3];
    private double[] coordsThree = new double[3];
    private int cornernumber = 1;
    private double[] currentCoords = new double[3]; // For loop tracking
    private double[] vectorOne = new double[3]; // Point 1 to 2
    private double[] vectorTwo = new double[3]; // Point 1 to 3
    private double[] vectorThree = new double[3]; // Point 2 to 3, for area calculations
    private double[] normalVector = new double[3];

    /**
     * 
     */
    public TriangleBrush() {
        this.setName("Triangle");
    }

    private final void triangleA(final SnipeData v) {
        switch (this.cornernumber) {
        case 1:
            this.coordsOne[0] = this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()); // I hate you sometimes, Notch. Really? Every quadrant is
                                                                                                 // different?
            this.coordsOne[1] = this.getTargetBlock().getY() + .5;
            this.coordsOne[2] = this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ());
            this.cornernumber = 2;
            v.sendMessage(ChatColor.GRAY + "First Corner set.");
            break;
        case 2:
            this.coordsTwo[0] = this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()); // I hate you sometimes, Notch. Really? Every quadrant is
                                                                                                 // different?
            this.coordsTwo[1] = this.getTargetBlock().getY() + .5;
            this.coordsTwo[2] = this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ());
            this.cornernumber = 3;
            v.sendMessage(ChatColor.GRAY + "Second Corner set.");
            break;
        case 3:
            this.coordsThree[0] = this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()); // I hate you sometimes, Notch. Really? Every quadrant is
                                                                                                   // different?
            this.coordsThree[1] = this.getTargetBlock().getY() + .5;
            this.coordsThree[2] = this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ());
            this.cornernumber = 1;
            v.sendMessage(ChatColor.GRAY + "Third Corner set.");
            break;
        default:
            break;

        }

    }

    private final void triangleP(final SnipeData v) {
        double _lengthOne = 0;
        double _lengthTwo = 0;
        double _lengthThree = 0;
        double _heronBig = 0;

        // Calculate slope vectors
        for (int _i = 0; _i < 3; _i++) {
            this.vectorOne[_i] = this.coordsTwo[_i] - this.coordsOne[_i];
            this.vectorTwo[_i] = this.coordsThree[_i] - this.coordsOne[_i];
            this.vectorThree[_i] = this.coordsThree[_i] - this.coordsTwo[_i];
        }

        // Calculate the cross product of vectorone and vectortwo
        this.normalVector[0] = this.vectorOne[1] * this.vectorTwo[2] - this.vectorOne[2] * this.vectorTwo[1];
        this.normalVector[1] = this.vectorOne[2] * this.vectorTwo[0] - this.vectorOne[0] * this.vectorTwo[2];
        this.normalVector[2] = this.vectorOne[0] * this.vectorTwo[1] - this.vectorOne[1] * this.vectorTwo[0];

        // Calculate magnitude of slope vectors
        _lengthOne = Math.pow(Math.pow(this.vectorOne[0], 2) + Math.pow(this.vectorOne[1], 2) + Math.pow(this.vectorOne[2], 2), .5);
        _lengthTwo = Math.pow(Math.pow(this.vectorTwo[0], 2) + Math.pow(this.vectorTwo[1], 2) + Math.pow(this.vectorTwo[2], 2), .5);
        _lengthThree = Math.pow(Math.pow(this.vectorThree[0], 2) + Math.pow(this.vectorThree[1], 2) + Math.pow(this.vectorThree[2], 2), .5);

        // Bigger vector determines brush size
        final int _bSize = (int) Math.ceil((_lengthOne > _lengthTwo) ? _lengthOne : _lengthTwo);

        // Calculate constant term
        final double _planeConstant = this.normalVector[0] * this.coordsOne[0] + this.normalVector[1] * this.coordsOne[1] + this.normalVector[2]
                * this.coordsOne[2];

        // Calculate the area of the full triangle
        _heronBig = .25 * Math.pow(
                Math.pow(Math.pow(_lengthOne, 2) + Math.pow(_lengthTwo, 2) + Math.pow(_lengthThree, 2), 2) - 2
                        * (Math.pow(_lengthOne, 4) + Math.pow(_lengthTwo, 4) + Math.pow(_lengthThree, 4)), .5);

        if (_lengthOne == 0 || _lengthTwo == 0 || (this.coordsOne[0] == 0 && this.coordsOne[1] == 0 && this.coordsOne[2] == 0)
                || (this.coordsTwo[0] == 0 && this.coordsTwo[1] == 0 && this.coordsTwo[2] == 0)
                || (this.coordsThree[0] == 0 && this.coordsThree[1] == 0 && this.coordsThree[2] == 0)) {
            v.sendMessage(ChatColor.RED + "ERROR: Invalid corners, please try again.");
        } else {
            // Make the Changes
            final double[] _cVectorOne = new double[3];
            final double[] _cVectorTwo = new double[3];
            final double[] _cVectorThree = new double[3];
            
            for (int _y = -_bSize; _y <= _bSize; _y++) { // X DEPENDENT
                for (int _z = -_bSize; _z <= _bSize; _z++) {
                    this.currentCoords[1] = this.coordsOne[1] + _y;
                    this.currentCoords[2] = this.coordsOne[2] + _z;
                    this.currentCoords[0] = (_planeConstant - this.normalVector[1] * this.currentCoords[1] - this.normalVector[2] * this.currentCoords[2])
                            / this.normalVector[0];

                    // Area of triangle currentcoords, coordsone, coordstwo
                    for (int _i = 0; _i < 3; _i++) {
                        _cVectorOne[_i] = this.coordsTwo[_i] - this.coordsOne[_i];
                        _cVectorTwo[_i] = this.currentCoords[_i] - this.coordsOne[_i];
                        _cVectorThree[_i] = this.currentCoords[_i] - this.coordsTwo[_i];
                    }
                    double _cLengthOne = Math.pow(Math.pow(_cVectorOne[0], 2) + Math.pow(_cVectorOne[1], 2) + Math.pow(_cVectorOne[2], 2), .5);
                    double _cLengthTwo = Math.pow(Math.pow(_cVectorTwo[0], 2) + Math.pow(_cVectorTwo[1], 2) + Math.pow(_cVectorTwo[2], 2), .5);
                    double _cLengthThree = Math.pow(Math.pow(_cVectorThree[0], 2) + Math.pow(_cVectorThree[1], 2) + Math.pow(_cVectorThree[2], 2), .5);

                    final double _heronOne = .25 * Math.pow(
                            Math.pow(Math.pow(_cLengthOne, 2) + Math.pow(_cLengthTwo, 2) + Math.pow(_cLengthThree, 2), 2) - 2
                                    * (Math.pow(_cLengthOne, 4) + Math.pow(_cLengthTwo, 4) + Math.pow(_cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordstwo
                    for (int _i = 0; _i < 3; _i++) {
                        _cVectorOne[_i] = this.coordsTwo[_i] - this.coordsThree[_i];
                        _cVectorTwo[_i] = this.currentCoords[_i] - this.coordsThree[_i];
                        _cVectorThree[_i] = this.currentCoords[_i] - this.coordsTwo[_i];
                    }
                    _cLengthOne = Math.pow(Math.pow(_cVectorOne[0], 2) + Math.pow(_cVectorOne[1], 2) + Math.pow(_cVectorOne[2], 2), .5);
                    _cLengthTwo = Math.pow(Math.pow(_cVectorTwo[0], 2) + Math.pow(_cVectorTwo[1], 2) + Math.pow(_cVectorTwo[2], 2), .5);
                    _cLengthThree = Math.pow(Math.pow(_cVectorThree[0], 2) + Math.pow(_cVectorThree[1], 2) + Math.pow(_cVectorThree[2], 2), .5);
                    final double _heronTwo = .25 * Math.pow(
                            Math.pow(Math.pow(_cLengthOne, 2) + Math.pow(_cLengthTwo, 2) + Math.pow(_cLengthThree, 2), 2) - 2
                                    * (Math.pow(_cLengthOne, 4) + Math.pow(_cLengthTwo, 4) + Math.pow(_cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordsone
                    for (int _i = 0; _i < 3; _i++) {
                        _cVectorOne[_i] = this.coordsOne[_i] - this.coordsThree[_i];
                        _cVectorTwo[_i] = this.currentCoords[_i] - this.coordsThree[_i];
                        _cVectorThree[_i] = this.currentCoords[_i] - this.coordsOne[_i];
                    }
                    _cLengthOne = Math.pow(Math.pow(_cVectorOne[0], 2) + Math.pow(_cVectorOne[1], 2) + Math.pow(_cVectorOne[2], 2), .5);
                    _cLengthTwo = Math.pow(Math.pow(_cVectorTwo[0], 2) + Math.pow(_cVectorTwo[1], 2) + Math.pow(_cVectorTwo[2], 2), .5);
                    _cLengthThree = Math.pow(Math.pow(_cVectorThree[0], 2) + Math.pow(_cVectorThree[1], 2) + Math.pow(_cVectorThree[2], 2), .5);
                    final double _heronThree = .25 * Math.pow(
                            Math.pow(Math.pow(_cLengthOne, 2) + Math.pow(_cLengthTwo, 2) + Math.pow(_cLengthThree, 2), 2) - 2
                                    * (Math.pow(_cLengthOne, 4) + Math.pow(_cLengthTwo, 4) + Math.pow(_cLengthThree, 4)), .5);

                    final double _barycentric = (_heronOne + _heronTwo + _heronThree) / _heronBig;

                    if (_barycentric <= 1.1) {

                        this.current.perform(this.clampY((int) this.currentCoords[0], (int) this.currentCoords[1], (int) this.currentCoords[2]));

                    }

                }
            } // END X DEPENDENT

            for (int _x = -_bSize; _x <= _bSize; _x++) { // Y DEPENDENT
                for (int _z = -_bSize; _z <= _bSize; _z++) {

                    this.currentCoords[0] = this.coordsOne[0] + _x;
                    this.currentCoords[2] = this.coordsOne[2] + _z;
                    this.currentCoords[1] = (_planeConstant - this.normalVector[0] * this.currentCoords[0] - this.normalVector[2] * this.currentCoords[2])
                            / this.normalVector[1];

                    // Area of triangle currentcoords, coordsone, coordstwo
                    for (int i = 0; i < 3; i++) {
                        _cVectorOne[i] = this.coordsTwo[i] - this.coordsOne[i];
                        _cVectorTwo[i] = this.currentCoords[i] - this.coordsOne[i];
                        _cVectorThree[i] = this.currentCoords[i] - this.coordsTwo[i];
                    }
                    double _cLengthOne = Math.pow(Math.pow(_cVectorOne[0], 2) + Math.pow(_cVectorOne[1], 2) + Math.pow(_cVectorOne[2], 2), .5);
                    double _cLengthTwo = Math.pow(Math.pow(_cVectorTwo[0], 2) + Math.pow(_cVectorTwo[1], 2) + Math.pow(_cVectorTwo[2], 2), .5);
                    double _cLengthThree = Math.pow(Math.pow(_cVectorThree[0], 2) + Math.pow(_cVectorThree[1], 2) + Math.pow(_cVectorThree[2], 2), .5);

                    final double _heronOne = .25 * Math.pow(
                            Math.pow(Math.pow(_cLengthOne, 2) + Math.pow(_cLengthTwo, 2) + Math.pow(_cLengthThree, 2), 2) - 2
                                    * (Math.pow(_cLengthOne, 4) + Math.pow(_cLengthTwo, 4) + Math.pow(_cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordstwo
                    for (int i = 0; i < 3; i++) {
                        _cVectorOne[i] = this.coordsTwo[i] - this.coordsThree[i];
                        _cVectorTwo[i] = this.currentCoords[i] - this.coordsThree[i];
                        _cVectorThree[i] = this.currentCoords[i] - this.coordsTwo[i];
                    }
                    _cLengthOne = Math.pow(Math.pow(_cVectorOne[0], 2) + Math.pow(_cVectorOne[1], 2) + Math.pow(_cVectorOne[2], 2), .5);
                    _cLengthTwo = Math.pow(Math.pow(_cVectorTwo[0], 2) + Math.pow(_cVectorTwo[1], 2) + Math.pow(_cVectorTwo[2], 2), .5);
                    _cLengthThree = Math.pow(Math.pow(_cVectorThree[0], 2) + Math.pow(_cVectorThree[1], 2) + Math.pow(_cVectorThree[2], 2), .5);
                    final double _heronTwo = .25 * Math.pow(
                            Math.pow(Math.pow(_cLengthOne, 2) + Math.pow(_cLengthTwo, 2) + Math.pow(_cLengthThree, 2), 2) - 2
                                    * (Math.pow(_cLengthOne, 4) + Math.pow(_cLengthTwo, 4) + Math.pow(_cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordsone
                    for (int i = 0; i < 3; i++) {
                        _cVectorOne[i] = this.coordsOne[i] - this.coordsThree[i];
                        _cVectorTwo[i] = this.currentCoords[i] - this.coordsThree[i];
                        _cVectorThree[i] = this.currentCoords[i] - this.coordsOne[i];
                    }
                    _cLengthOne = Math.pow(Math.pow(_cVectorOne[0], 2) + Math.pow(_cVectorOne[1], 2) + Math.pow(_cVectorOne[2], 2), .5);
                    _cLengthTwo = Math.pow(Math.pow(_cVectorTwo[0], 2) + Math.pow(_cVectorTwo[1], 2) + Math.pow(_cVectorTwo[2], 2), .5);
                    _cLengthThree = Math.pow(Math.pow(_cVectorThree[0], 2) + Math.pow(_cVectorThree[1], 2) + Math.pow(_cVectorThree[2], 2), .5);
                    final double _heronThree = .25 * Math.pow(
                            Math.pow(Math.pow(_cLengthOne, 2) + Math.pow(_cLengthTwo, 2) + Math.pow(_cLengthThree, 2), 2) - 2
                                    * (Math.pow(_cLengthOne, 4) + Math.pow(_cLengthTwo, 4) + Math.pow(_cLengthThree, 4)), .5);

                    final double barycentric = (_heronOne + _heronTwo + _heronThree) / _heronBig;

                    if (barycentric <= 1.1) {

                        this.current.perform(this.clampY((int) this.currentCoords[0], (int) this.currentCoords[1], (int) this.currentCoords[2]));

                    }

                }
            } // END Y DEPENDENT
            for (int _x = -_bSize; _x <= _bSize; _x++) { // Z DEPENDENT
                for (int _y = -_bSize; _y <= _bSize; _y++) {
                    this.currentCoords[0] = this.coordsOne[0] + _x;
                    this.currentCoords[1] = this.coordsOne[1] + _y;
                    this.currentCoords[2] = (_planeConstant - this.normalVector[0] * this.currentCoords[0] - this.normalVector[1] * this.currentCoords[1])
                            / this.normalVector[2];

                    // Area of triangle currentcoords, coordsone, coordstwo
                    for (int _i = 0; _i < 3; _i++) {
                        _cVectorOne[_i] = this.coordsTwo[_i] - this.coordsOne[_i];
                        _cVectorTwo[_i] = this.currentCoords[_i] - this.coordsOne[_i];
                        _cVectorThree[_i] = this.currentCoords[_i] - this.coordsTwo[_i];
                    }
                    double _cLengthOne = Math.pow(Math.pow(_cVectorOne[0], 2) + Math.pow(_cVectorOne[1], 2) + Math.pow(_cVectorOne[2], 2), .5);
                    double _cLengthTwo = Math.pow(Math.pow(_cVectorTwo[0], 2) + Math.pow(_cVectorTwo[1], 2) + Math.pow(_cVectorTwo[2], 2), .5);
                    double _cLengthThree = Math.pow(Math.pow(_cVectorThree[0], 2) + Math.pow(_cVectorThree[1], 2) + Math.pow(_cVectorThree[2], 2), .5);

                    final double _heronOne = .25 * Math.pow(
                            Math.pow(Math.pow(_cLengthOne, 2) + Math.pow(_cLengthTwo, 2) + Math.pow(_cLengthThree, 2), 2) - 2
                                    * (Math.pow(_cLengthOne, 4) + Math.pow(_cLengthTwo, 4) + Math.pow(_cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordstwo
                    for (int _i = 0; _i < 3; _i++) {
                        _cVectorOne[_i] = this.coordsTwo[_i] - this.coordsThree[_i];
                        _cVectorTwo[_i] = this.currentCoords[_i] - this.coordsThree[_i];
                        _cVectorThree[_i] = this.currentCoords[_i] - this.coordsTwo[_i];
                    }
                    _cLengthOne = Math.pow(Math.pow(_cVectorOne[0], 2) + Math.pow(_cVectorOne[1], 2) + Math.pow(_cVectorOne[2], 2), .5);
                    _cLengthTwo = Math.pow(Math.pow(_cVectorTwo[0], 2) + Math.pow(_cVectorTwo[1], 2) + Math.pow(_cVectorTwo[2], 2), .5);
                    _cLengthThree = Math.pow(Math.pow(_cVectorThree[0], 2) + Math.pow(_cVectorThree[1], 2) + Math.pow(_cVectorThree[2], 2), .5);
                    final double _heronTwo = .25 * Math.pow(
                            Math.pow(Math.pow(_cLengthOne, 2) + Math.pow(_cLengthTwo, 2) + Math.pow(_cLengthThree, 2), 2) - 2
                                    * (Math.pow(_cLengthOne, 4) + Math.pow(_cLengthTwo, 4) + Math.pow(_cLengthThree, 4)), .5);

                    // Area of triangle currentcoords, coordsthree, coordsone
                    for (int _i = 0; _i < 3; _i++) {
                        _cVectorOne[_i] = this.coordsOne[_i] - this.coordsThree[_i];
                        _cVectorTwo[_i] = this.currentCoords[_i] - this.coordsThree[_i];
                        _cVectorThree[_i] = this.currentCoords[_i] - this.coordsOne[_i];
                    }
                    _cLengthOne = Math.pow(Math.pow(_cVectorOne[0], 2) + Math.pow(_cVectorOne[1], 2) + Math.pow(_cVectorOne[2], 2), .5);
                    _cLengthTwo = Math.pow(Math.pow(_cVectorTwo[0], 2) + Math.pow(_cVectorTwo[1], 2) + Math.pow(_cVectorTwo[2], 2), .5);
                    _cLengthThree = Math.pow(Math.pow(_cVectorThree[0], 2) + Math.pow(_cVectorThree[1], 2) + Math.pow(_cVectorThree[2], 2), .5);
                    final double _heronThree = .25 * Math.pow(
                            Math.pow(Math.pow(_cLengthOne, 2) + Math.pow(_cLengthTwo, 2) + Math.pow(_cLengthThree, 2), 2) - 2
                                    * (Math.pow(_cLengthOne, 4) + Math.pow(_cLengthTwo, 4) + Math.pow(_cLengthThree, 4)), .5);

                    final double _barycentric = (_heronOne + _heronTwo + _heronThree) / _heronBig;

                    // VoxelSniper.log.info("Bary: "+barycentric+", hb: "+heronbig+", h1: "+heronone+", h2: "+herontwo+", h3: "+heronthree);

                    if (_barycentric <= 1.1) {
                        this.current.perform(this.clampY((int) this.currentCoords[0], (int) this.currentCoords[1], (int) this.currentCoords[2]));
                    }

                }
            } // END Z DEPENDENT

            v.storeUndo(this.current.getUndo());

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
        this.triangleA(v);
    }

    @Override
    protected final void powder(final SnipeData v) { // Add a point
        this.triangleP(v);
    }
    

    @Override
    public final void info(final Message vm) { // Make the triangle
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD
                    + "Triangle Brush instructions: Select three corners with the arrow brush, then generate the triangle with the powder brush.");
        }
    }
    
    @Override
    public final int getTimesUsed() {
        return TriangleBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        TriangleBrush.timesUsed = tUsed;
    }
}
