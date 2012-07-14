/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;

/**
 * @author Giltwist
 */
public class ThreePointCircle extends PerformBrush {

	private boolean first = true;
	// ALL COORDINATES AND VECTORS ARE ORDERED TRIPLES (X,Y,Z)
	// Player provided points and a status tracker
	private double[] coordsone = new double[3];
	private double[] coordstwo = new double[3];
	private double[] coordsthree = new double[3];
	private int cornernumber = 1;

	public ThreePointCircle() {
		name = "3-Point Circle";
	}

	@Override
	public final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
		// Add a point
		switch (cornernumber) {
		case 1:
			coordsone[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX()); // I hate you sometimes, Notch. Really? Every quadrant is different?
			coordsone[1] = tb.getY() + .5;
			coordsone[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());
			cornernumber = 2;
			v.sendMessage(ChatColor.GRAY + "First Corner set.");
			break;
		case 2:
			coordstwo[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX()); // I hate you sometimes, Notch. Really? Every quadrant is different?
			coordstwo[1] = tb.getY() + .5;
			coordstwo[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());
			cornernumber = 3;
			v.sendMessage(ChatColor.GRAY + "Second Corner set.");
			break;
		case 3:
			coordsthree[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX()); // I hate you sometimes, Notch. Really? Every quadrant is different?
			coordsthree[1] = tb.getY() + .5;
			coordsthree[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());
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

		double[] vectorone = new double[3]; // Point 1 to 2
		double[] vectortwo = new double[3]; // Point 1 to 3
		double[] vectorthree = new double[3]; // Point 2 to 3
		double[] normalvector = new double[3]; // normal vector of plane

		double sidelengthone = 0;// Point 1 to 2
		double sidelengthtwo = 0;// Point 1 to 3
		double sidelengththree = 0; // Point 2 to 3

		double[] midpointone = new double[3]; // Point 1 to 2
		double[] midpointtwo = new double[3]; // Point 1 to 3

		double[] perpendicularone = new double[3];// Point 1 to 2
		double[] perpendiculartwo = new double[3];// Point 1 to 3

		double[] circumcenter = new double[3]; // Defined by three points
		int[] brushcenter = new int[3]; // Rounded circumcenter

		double radius = 0;
		int bsize = 0;
		// BEGIN DEFINING TRIANGLE

		// Calculate slope vectors for the plane defined by three points
		for (int i = 0; i < 3; i++) {
			vectorone[i] = coordstwo[i] - coordsone[i];
			vectortwo[i] = coordsthree[i] - coordsone[i];
			vectorthree[i] = coordsthree[i] - coordstwo[i];
		}

		// Calculate magnitude of slope vectors
		sidelengthone = Math.pow(Math.pow(vectorone[0], 2) + Math.pow(vectorone[1], 2) + Math.pow(vectorone[2], 2), .5);
		sidelengthtwo = Math.pow(Math.pow(vectortwo[0], 2) + Math.pow(vectortwo[1], 2) + Math.pow(vectortwo[2], 2), .5);
		sidelengththree = Math.pow(Math.pow(vectorthree[0], 2) + Math.pow(vectorthree[1], 2) + Math.pow(vectorthree[2], 2), .5);

		// END TRIANGLE

		// Confirm triangle is not degenerate before proceeding
		if (sidelengthone == 0
				|| sidelengthtwo == 0
				|| sidelengththree == 0
				|| coordsone[0] * (coordstwo[1] - coordsthree[1]) + coordstwo[0] * (coordsthree[1] - coordsone[1]) + coordsthree[0]
						* (coordsone[1] - coordstwo[1]) == 0) {
			v.sendMessage(ChatColor.RED + "ERROR: Invalid corners, please try again.");
		} else {

			// BEGIN DETERMINATION OF PLANE IDENTITY

			// Calculate the cross product of vectorone and vectortwo
			normalvector[0] = vectorone[1] * vectortwo[2] - vectorone[2] * vectortwo[1];
			normalvector[1] = vectorone[2] * vectortwo[0] - vectorone[0] * vectortwo[2];
			normalvector[2] = vectorone[0] * vectortwo[1] - vectorone[1] * vectortwo[0];

			// Calculate constant term of plane
			double planeconstant = normalvector[0] * coordsone[0] + normalvector[1] * coordsone[1] + normalvector[2] * coordsone[2];

			// END PLANE IDENTITY

			// BEGIN DETERMINATION OF CIRCUMCENTER

			// Find Midpoints of two sides
			for (int i = 0; i < 3; i++) {
				// Midpoint from point 1 to point 2 (vectorone)
				midpointone[i] = (coordstwo[i] + coordsone[i]) / 2;
				// Midpoint from point 1 to point 3 (vectortwo)
				midpointtwo[i] = (coordsthree[i] + coordsone[i]) / 2;
			}

			// Find perpendicular vectors to two sides in the plane

			// Calculate the cross product of normalvector and vectorone
			perpendicularone[0] = normalvector[1] * vectorone[2] - normalvector[2] * vectorone[1];
			perpendicularone[1] = normalvector[2] * vectorone[0] - normalvector[0] * vectorone[2];
			perpendicularone[2] = normalvector[0] * vectorone[1] - normalvector[1] * vectorone[0];

			// Calculate the cross product of normalvector and vectortwo
			perpendiculartwo[0] = normalvector[1] * vectortwo[2] - normalvector[2] * vectortwo[1];
			perpendiculartwo[1] = normalvector[2] * vectortwo[0] - normalvector[0] * vectortwo[2];
			perpendiculartwo[2] = normalvector[0] * vectortwo[1] - normalvector[1] * vectorone[0];

			// determine value of parametric variable at intersection of two perpendicular bisectors
			double t = (midpointtwo[0] - midpointone[0]) / (perpendicularone[0] - perpendiculartwo[0]);

			// Calculate Circumcenter and Brushcenter
			for (int i = 0; i < 3; i++) {
				circumcenter[i] = midpointone[i] + perpendicularone[i] * t;
				brushcenter[i] = (int) Math.round(circumcenter[i]);
			}
			// END CIRCUMCENTER

			// Calculate radius of circumcircle and determine brushsize
			radius = Math.pow(
					Math.pow((coordsone[0] - circumcenter[0]), 2) + Math.pow((coordsone[1] - circumcenter[1]), 2)
							+ Math.pow((coordsone[2] - circumcenter[2]), 2), .5);
			bsize = (int) (Math.ceil(radius) + 1);

			// BEGIN DETERMINING BLOCKS TO CHANGE

			for (int x = -bsize; x <= bsize; x++) {
				for (int y = -bsize; y <= bsize; y++) {
					for (int z = -bsize; z <= bsize; z++) {
						// Calculate distance from center, offset .5 to get "center" of block rather than corner
						double tempdistance = Math.pow(Math.pow(x + .5, 2) + Math.pow(y + .5, 2) + Math.pow(z + .5, 2), .5);
						double tempconstant = normalvector[0] * (circumcenter[0] + x + .5) + normalvector[1] * (circumcenter[1] + y + .5) + normalvector[2]
								* (circumcenter[2] + z + .5);

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

		// RESET BRUSH
		coordsone[0] = 0;
		coordsone[1] = 0; 
		coordsone[2] = 0;
		coordstwo[0] = 0;
		coordstwo[1] = 0;
		coordstwo[2] = 0;
		coordsthree[0] = 0;
		coordsthree[1] = 0;
		coordsthree[2] = 0;

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
