package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * @author Giltwist
 */
public class ThreePointCircle extends PerformBrush {

    /**
     * Enumeration on Tolerance values.
     * 
     * @author MikeMatrix
     * 
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

    private Vector coordsOne;
    private Vector coordsTwo;
    private Vector coordsThree;
    private Tolerance tolerance = Tolerance.DEFAULT;

    private static int timesUsed = 0;

    /**
     * Default Constructor.
     */
    public ThreePointCircle() {
        this.name = "3-Point Circle";
    }

    @Override
    public final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        if (this.coordsOne == null) {
            this.coordsOne = this.tb.getLocation().toVector();
            v.sendMessage(ChatColor.GRAY + "First Corner set.");
        } else if (this.coordsTwo == null) {
            this.coordsTwo = this.tb.getLocation().toVector();
            v.sendMessage(ChatColor.GRAY + "Second Corner set.");
        } else if (this.coordsThree == null) {
            this.coordsThree = this.tb.getLocation().toVector();
            v.sendMessage(ChatColor.GRAY + "Third Corner set.");
        } else {
            this.coordsOne = this.tb.getLocation().toVector();
            this.coordsTwo = null;
            this.coordsThree = null;
            v.sendMessage(ChatColor.GRAY + "First Corner set.");
        }
    }

    @Override
    public final int getTimesUsed() {
        return ThreePointCircle.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        switch (this.tolerance) {
        case ACCURATE:
            vm.custom(ChatColor.GOLD + "Mode: Accurate");
            break;
        case DEFAULT:
            vm.custom(ChatColor.GOLD + "Mode: Default");
            break;
        case SMOOTH:
            vm.custom(ChatColor.GOLD + "Mode: Smooth");
            break;
        default:
            vm.custom(ChatColor.GOLD + "Mode: Unknown");
            break;
        }

    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.YELLOW
                    + "3-Point Circle Brush instructions: Select three corners with the arrow brush, then generate the Circle with the powder brush.");
            String _toleranceOptions = "";
            for (final Tolerance _tolerance : Tolerance.values()) {
                if (!_toleranceOptions.isEmpty()) {
                    _toleranceOptions += "|";
                }
                _toleranceOptions += _tolerance.name().toLowerCase();
            }
            v.sendMessage(ChatColor.GOLD + "/b tpc " + _toleranceOptions + " -- Toggle the calculations to emphasize accuracy or smoothness");
            return;
        }

        for (int _i = 1; _i < par.length; _i++) {
            final String _string = par[_i].toUpperCase();
            try {
                this.tolerance = Tolerance.valueOf(_string);
                v.sendMessage(ChatColor.AQUA + "Brush set to " + this.tolerance.name().toLowerCase() + " tolerance.");
                return;
            } catch (final IllegalArgumentException _e) {
                v.vm.brushMessage("No such tolerance.");
            }
        }
    }

    @Override
    public final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.w = v.getWorld();

        if (this.coordsOne == null || this.coordsTwo == null || this.coordsThree == null) {
            return;
        }

        // Calculate triangle defining vectors
        final Vector _vectorOne = this.coordsTwo.clone();
        _vectorOne.subtract(this.coordsOne);
        final Vector _vectorTwo = this.coordsThree.clone();
        _vectorTwo.subtract(this.coordsOne);
        final Vector _vectorThree = this.coordsThree.clone();
        _vectorThree.subtract(_vectorTwo);

        // Redundant data check
        if (_vectorOne.length() == 0 || _vectorTwo.length() == 0 || _vectorThree.length() == 0 || _vectorOne.angle(_vectorTwo) == 0
                || _vectorOne.angle(_vectorThree) == 0 || _vectorThree.angle(_vectorTwo) == 0) {

            v.sendMessage(ChatColor.RED + "ERROR: Invalid points, try again.");
            this.coordsOne = null;
            this.coordsTwo = null;
            this.coordsThree = null;
            return;
        }

        // Calculate normal vector of the plane.
        final Vector _normalVector = _vectorOne.clone();
        _normalVector.crossProduct(_vectorTwo);

        // Calculate constant term of the plane.
        final double _planeConstant = _normalVector.getX() * this.coordsOne.getX() + _normalVector.getY() * this.coordsOne.getY() + _normalVector.getZ()
                * this.coordsOne.getZ();

        final Vector _midpointOne = this.coordsOne.getMidpoint(this.coordsTwo);
        final Vector _midpointTwo = this.coordsOne.getMidpoint(this.coordsThree);

        // Find perpendicular vectors to two sides in the plane
        final Vector _perpendicularOne = _normalVector.clone();
        _perpendicularOne.crossProduct(_vectorOne);
        final Vector _perpendicularTwo = _normalVector.clone();
        _perpendicularTwo.crossProduct(_vectorTwo);

        // determine value of parametric variable at intersection of two perpendicular bisectors
        final Vector _tNumerator = _midpointTwo.clone();
        _tNumerator.subtract(_midpointOne);
        _tNumerator.crossProduct(_perpendicularTwo);
        final Vector _tDenominator = _perpendicularOne.clone();
        _tDenominator.crossProduct(_perpendicularTwo);
        final double _t = _tNumerator.length() / _tDenominator.length();

        // Calculate Circumcenter and Brushcenter.
        final Vector _circumcenter = new Vector();
        _circumcenter.copy(_perpendicularOne);
        _circumcenter.multiply(_t);
        _circumcenter.add(_midpointOne);

        final Vector _brushcenter = new Vector(Math.round(_circumcenter.getX()), Math.round(_circumcenter.getY()), Math.round(_circumcenter.getZ()));

        // Calculate radius of circumcircle and determine brushsize
        final double _radius = _circumcenter.distance(new Vector(this.coordsOne.getX(), this.coordsOne.getY(), this.coordsOne.getZ()));
        final int _bSize = NumberConversions.ceil(_radius) + 1;

        for (int _x = -_bSize; _x <= _bSize; _x++) {
            for (int _y = -_bSize; _y <= _bSize; _y++) {
                for (int _z = -_bSize; _z <= _bSize; _z++) {
                    // Calculate distance from center
                    final double _tempDistance = Math.pow(Math.pow(_x, 2) + Math.pow(_y, 2) + Math.pow(_z, 2), .5);

                    // gets corner-on blocks
                    final double _cornerConstant = _normalVector.getX() * (_circumcenter.getX() + _x) + _normalVector.getY() * (_circumcenter.getY() + _y)
                            + _normalVector.getZ() * (_circumcenter.getZ() + _z);

                    // gets center-on blocks
                    final double _centerConstant = _normalVector.getX() * (_circumcenter.getX() + _x + .5) + _normalVector.getY()
                            * (_circumcenter.getY() + _y + .5) + _normalVector.getZ() * (_circumcenter.getZ() + _z + .5);

                    // Check if point is within sphere and on plane (some tolerance given)
                    if (_tempDistance <= _radius
                            && (Math.abs(_cornerConstant - _planeConstant) < this.tolerance.getValue() || Math.abs(_centerConstant - _planeConstant) < this.tolerance
                                    .getValue())) {
                        this.current.perform(this.clampY(_brushcenter.getBlockX() + _x, _brushcenter.getBlockY() + _y, _brushcenter.getBlockZ() + _z));
                    }

                }
            }
        }

        v.sendMessage(ChatColor.GREEN + "Done.");
        v.storeUndo(this.current.getUndo());

        // Reset Brush
        this.coordsOne = null;
        this.coordsTwo = null;
        this.coordsThree = null;

    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        ThreePointCircle.timesUsed = tUsed;
    }
}
