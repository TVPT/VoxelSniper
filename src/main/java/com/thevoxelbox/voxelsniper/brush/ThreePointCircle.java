/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * @author Giltwist
 */
public class ThreePointCircle extends PerformBrush {

	private boolean first = true;
	// ALL COORDINATES AND VECTORS ARE ORDERED TRIPLES (X,Y,Z)
	// Player provided points and a status tracker
	private double[] coordsOne = new double[3];
	private double[] coordsTwo = new double[3];
	private double[] coordsThree = new double[3];
	private int cornernumber = 1;

	public ThreePointCircle() {
		name = "3-Point Circle";
	}

	@Override
	public final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
		// Add a point
		switch (cornernumber) {
		case 1:
			coordsOne[0] = tb.getX(); // .5 * tb.getX() / Math.abs(tb.getX())
			coordsOne[1] = tb.getY(); // +.5
			coordsOne[2] = tb.getZ(); // + .5 * tb.getZ() / Math.abs(tb.getZ())
			cornernumber = 2;
			v.sendMessage(ChatColor.GRAY + "First Corner set.");
			break;
		case 2:
			coordsTwo[0] = tb.getX(); // OFFSET NEEDED?
			coordsTwo[1] = tb.getY();
			coordsTwo[2] = tb.getZ();
			cornernumber = 3;
			v.sendMessage(ChatColor.GRAY + "Second Corner set.");
			break;
		case 3:
			coordsThree[0] = tb.getX(); // OFFSET NEEDED?
			coordsThree[1] = tb.getY();
			coordsThree[2] = tb.getZ();
			cornernumber = 1;
			v.sendMessage(ChatColor.GRAY + "Third Corner set.");
			break;
		default:
			break;

		}

	}

	@Override
	public final void powder(final com.thevoxelbox.voxelsniper.vData v) { // Make the circle
		w = v.getWorld();

		// Initialization

		double[] vectorOne = new double[3]; // Point 1 to 2
		double[] vectorTwo = new double[3]; // Point 1 to 3
		double[] vectorThree = new double[3]; // Point 2 to 3
		double[] normalVector = new double[3]; // normal vector of plane

		double sidelengthOne = 0;// Point 1 to 2
		double sidelengthTwo = 0;// Point 1 to 3
		double sidelengthThree = 0; // Point 2 to 3

		double[] midpointOne = new double[3]; // Point 1 to 2
		double[] midpointTwo = new double[3]; // Point 1 to 3

		double[] perpendicularOne = new double[3];// Point 1 to 2
		double[] perpendicularTwo = new double[3];// Point 1 to 3

		double[] circumcenter = new double[3]; // Defined by three points
		int[] brushcenter = new int[3]; // Rounded circumcenter

		double radius = 0;
		int bsize = 0;
		// BEGIN DEFINING TRIANGLE

		// Calculate slope vectors for the plane defined by three points
		for (int i = 0; i < 3; i++) {
			vectorOne[i] = coordsTwo[i] - coordsOne[i];
			vectorTwo[i] = coordsThree[i] - coordsOne[i];
			vectorThree[i] = coordsThree[i] - coordsTwo[i];
		}

		// Calculate magnitude of slope vectors
		sidelengthOne = Math.pow(Math.pow(vectorOne[0], 2) + Math.pow(vectorOne[1], 2) + Math.pow(vectorOne[2], 2), .5);
		sidelengthTwo = Math.pow(Math.pow(vectorTwo[0], 2) + Math.pow(vectorTwo[1], 2) + Math.pow(vectorTwo[2], 2), .5);
		sidelengthThree = Math.pow(Math.pow(vectorThree[0], 2) + Math.pow(vectorThree[1], 2) + Math.pow(vectorThree[2], 2), .5);

		// END TRIANGLE

		// Confirm triangle is not degenerate before proceeding

		if (sidelengthOne == 0 || sidelengthTwo == 0 || sidelengthThree == 0) {
			v.sendMessage(ChatColor.RED + "ERROR: Invalid corners, please try again.");
		} else {

			// BEGIN DETERMINATION OF PLANE IDENTITY

			// Calculate the cross product of vectorone and vectortwo
			normalVector[0] = vectorOne[1] * vectorTwo[2] - vectorOne[2] * vectorTwo[1];
			normalVector[1] = vectorOne[2] * vectorTwo[0] - vectorOne[0] * vectorTwo[2];
			normalVector[2] = vectorOne[0] * vectorTwo[1] - vectorOne[1] * vectorTwo[0];

			// Calculate constant term of plane
			double planeconstant = normalVector[0] * coordsOne[0] + normalVector[1] * coordsOne[1] + normalVector[2] * coordsOne[2];

			// END PLANE IDENTITY

			// BEGIN DETERMINATION OF CIRCUMCENTER

			// Find Midpoints of two sides
			for (int i = 0; i < 3; i++) {
				// Midpoint from point 1 to point 2 (vectorone)
				midpointOne[i] = (coordsTwo[i] + coordsOne[i]) / 2;
				// Midpoint from point 1 to point 3 (vectortwo)
				midpointTwo[i] = (coordsThree[i] + coordsOne[i]) / 2;
			}

			// DEBUG CHECK: Correctly finding midpoints?
			// current.perform(clampY((int) Math.round(midpointOne[0]), (int) Math.round(midpointOne[1]), (int) Math.round(midpointOne[2])));
			// current.perform(clampY((int) Math.round(midpointTwo[0]), (int) Math.round(midpointTwo[1]), (int) Math.round(midpointTwo[2])));
			// Always works, within rounding error

			// Find perpendicular vectors to two sides in the plane

			// Calculate the cross product of normalvector and vectorone
			perpendicularOne[0] = normalVector[1] * vectorOne[2] - normalVector[2] * vectorOne[1];
			perpendicularOne[1] = normalVector[2] * vectorOne[0] - normalVector[0] * vectorOne[2];
			perpendicularOne[2] = normalVector[0] * vectorOne[1] - normalVector[1] * vectorOne[0];

			// Calculate the cross product of normalvector and vectortwo
			perpendicularTwo[0] = normalVector[1] * vectorTwo[2] - normalVector[2] * vectorTwo[1];
			perpendicularTwo[1] = normalVector[2] * vectorTwo[0] - normalVector[0] * vectorTwo[2];
			perpendicularTwo[2] = normalVector[0] * vectorTwo[1] - normalVector[1] * vectorTwo[0]; // derp, can't believe I forgot to change a "one" into a
																									// "two" here.

			// DEBUG CHECK: Correctly finding perpendicular?
			v.sendMessage(ChatColor.YELLOW + "<" + perpendicularOne[0] + ", " + perpendicularOne[1] + ", " + perpendicularOne[2] + ">");
			v.sendMessage(ChatColor.YELLOW + "<" + perpendicularTwo[0] + ", " + perpendicularTwo[1] + ", " + perpendicularTwo[2] + ">");
			// Looks like it, except direction seems to not be constant? Right hand rule being problematic?

			double t = 0;

			// determine value of parametric variable at intersection of two perpendicular bisectors
			if (perpendicularOne[0] == perpendicularTwo[0] || midpointOne[0] == midpointTwo[0]) {
				if (perpendicularOne[1] == perpendicularTwo[1] || midpointOne[1] == midpointTwo[1]) {
					t = Math.abs((midpointTwo[2] - midpointOne[2]) / (perpendicularOne[2] - perpendicularTwo[2]));

				} else {

					t = Math.abs((midpointTwo[1] - midpointOne[1]) / (perpendicularOne[1] - perpendicularTwo[1]));

				}
			} else {
				t = Math.abs((midpointTwo[0] - midpointOne[0]) / (perpendicularOne[0] - perpendicularTwo[0]));

			}

			// DEBUG: What the heck is t?
			// v.sendMessage(ChatColor.GOLD+"t: "+t);
			// Positive t's seem to always work. negative t's always seem to fail

			// Calculate Circumcenter and Brushcenter
			for (int i = 0; i < 3; i++) {
				circumcenter[i] = midpointOne[i] + perpendicularOne[i] * t;
				brushcenter[i] = (int) Math.round(circumcenter[i]);

				// DEBUG CHECK: Correctly determining t?
				// v.sendMessage(ChatColor.YELLOW+"CC: "+circumcenter[i]+" DBG: "+ (midpointTwo[i]+perpendicularTwo[i]*t));
				// Fixed.

			}
			// END CIRCUMCENTER

			// DEBUG CHECK: Correctly finding circumcenter?
			// current.perform(clampY(brushcenter[0], brushcenter[1], brushcenter[2]));
			// Should be working now

			// Calculate radius of circumcircle and determine brushsize
			radius = Math.pow(
					Math.pow((coordsOne[0] - circumcenter[0]), 2) + Math.pow((coordsOne[1] - circumcenter[1]), 2)
							+ Math.pow((coordsOne[2] - circumcenter[2]), 2), .5);
			bsize = (int) (Math.ceil(radius) + 1);

			// BEGIN DETERMINING BLOCKS TO CHANGE

			for (int x = -bsize; x <= bsize; x++) {
				for (int y = -bsize; y <= bsize; y++) {
					for (int z = -bsize; z <= bsize; z++) {
						// Calculate distance from center
						double tempdistance = Math.pow(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2), .5);
						double tempconstant = normalVector[0] * (circumcenter[0] + x) + normalVector[1] * (circumcenter[1] + y) + normalVector[2]
								* (circumcenter[2] + z);

						// Check if point is within sphere and on plane

						if (tempdistance <= radius && tempconstant == planeconstant) {

							// Make the changes

							current.perform(clampY(brushcenter[0] + x, brushcenter[1] + y, brushcenter[2] + z));
						}

					}
				}
			}

			// END BLOCKS

		}

		// Finalize Undo
		v.storeUndo(current.getUndo());

		// Notify player
		// v.sendMessage(ChatColor.AQUA+"Radius " + (int) (Math.round(radius)) +
		// "circle created at ("+brushcenter[0]+","+brushcenter[1]+","+brushcenter[2]+")");

		// RESET BRUSH
		coordsOne[0] = 0;
		coordsOne[1] = 0;
		coordsOne[2] = 0;
		coordsTwo[0] = 0;
		coordsTwo[1] = 0;
		coordsTwo[2] = 0;
		coordsThree[0] = 0;
		coordsThree[1] = 0;
		coordsThree[2] = 0;

		cornernumber = 1;

	}

	@Override
	public final void info(final vMessage vm) {
		vm.brushName(name);

	}

	@Override
	public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
		if (par[1].equalsIgnoreCase("info")) {
			v.sendMessage(ChatColor.GOLD
					+ "3-Point Circle Brush instructions: Select three corners with the arrow brush, then generate the Circle with the powder brush.");
			return;
		}

	}
}
