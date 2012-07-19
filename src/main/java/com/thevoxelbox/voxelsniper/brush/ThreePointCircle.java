/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

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

		// BEGIN DEFINING TRIANGLE

		// Calculate slope vectors for the plane defined by three points

		Vector vectorOne = new Vector(coordsTwo[0] - coordsOne[0], coordsTwo[1] - coordsOne[1], coordsTwo[2] - coordsOne[2]); // Point 1 to 2
		Vector vectorTwo = new Vector(coordsThree[0] - coordsOne[0], coordsThree[1] - coordsOne[1], coordsThree[2] - coordsOne[2]); // Point 1 to 3
		Vector vectorThree = new Vector(coordsThree[0] - coordsTwo[0], coordsThree[1] - coordsTwo[1], coordsThree[2] - coordsTwo[2]); // Point 2 to 3

		// Calculate magnitude of slope vectors

		double sidelengthOne = vectorOne.length();// Point 1 to 2
		double sidelengthTwo = vectorTwo.length();// Point 1 to 3
		double sidelengthThree = vectorThree.length(); // Point 2 to 3

		// END TRIANGLE

		// Confirm triangle is not degenerate before proceeding

		if (sidelengthOne == 0 || sidelengthTwo == 0 || sidelengthThree == 0 || vectorOne.angle(vectorTwo) == 0 || vectorOne.angle(vectorThree) == 0
				|| vectorThree.angle(vectorTwo) == 0) {

			v.sendMessage(ChatColor.RED + "ERROR: Invalid points, try again.");
		} else {

			// BEGIN DETERMINATION OF PLANE IDENTITY

			// Calculate the cross product of vectorone and vectortwo
			Vector normalVector = new Vector();
			normalVector.copy(vectorOne);
			normalVector.crossProduct(vectorTwo); // normal vector of plane

			// Calculate constant term of plane
			double planeconstant = normalVector.getX() * coordsOne[0] + normalVector.getY() * coordsOne[1] + normalVector.getZ() * coordsOne[2];

			// END PLANE IDENTITY

			// BEGIN DETERMINATION OF CIRCUMCENTER

			// Find Midpoints of two sides
			Vector midpointOne = new Vector((coordsOne[0] + coordsTwo[0]) / 2, (coordsOne[1] + coordsTwo[1]) / 2, (coordsOne[2] + coordsTwo[2]) / 2); // Point 1
																																						// to 2
			Vector midpointTwo = new Vector((coordsOne[0] + coordsThree[0]) / 2, (coordsOne[1] + coordsThree[1]) / 2, (coordsOne[2] + coordsThree[2]) / 2); // Point
																																							// 1
			// to
			// 3

			// Find perpendicular vectors to two sides in the plane
			Vector perpendicularOne = new Vector();
			perpendicularOne.copy(normalVector);
			perpendicularOne.crossProduct(vectorOne);// Point 1 to 2
			Vector perpendicularTwo = new Vector();
			perpendicularTwo.copy(normalVector);
			perpendicularTwo.crossProduct(vectorTwo);// Point 1 to 3

			// determine value of parametric variable at intersection of two perpendicular bisectors
			Vector tNumerator=new Vector();
			tNumerator.copy(midpointTwo);
			tNumerator.subtract(midpointOne);
			tNumerator.crossProduct(perpendicularTwo);
			Vector tDenominator=new Vector();
			tDenominator.copy(perpendicularOne);
			tDenominator.crossProduct(perpendicularTwo);
			double t = tNumerator.length() / tDenominator.length();

			// Calculate Circumcenter and Brushcenter
			Vector circumcenter = new Vector();
			circumcenter.copy(perpendicularOne);
			circumcenter.multiply(t);
			circumcenter.add(midpointOne);
			int[] brushcenter = new int[3]; // Rounded circumcenter
			brushcenter[0]=(int) Math.round(circumcenter.getX());
			brushcenter[1]=(int) Math.round(circumcenter.getY());
			brushcenter[2]=(int) Math.round(circumcenter.getZ());
			

			// END CIRCUMCENTER

			// Calculate radius of circumcircle and determine brushsize
			double radius = circumcenter.distance(new Vector(coordsOne[0], coordsOne[1], coordsOne[2]));
			int bsize = (int) Math.ceil(radius) + 1;

			// BEGIN DETERMINING BLOCKS TO CHANGE
			
			  for (int x = -bsize; x <= bsize; x++) {
				  for (int y = -bsize; y <= bsize; y++) { 
					  for (int z = -bsize; z <= bsize; z++) {
						  // Calculate distance from center 
					  double tempdistance = Math.pow(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2), .5); 
					  
					  //gets corner-on blocks
					  double cornerconstant = normalVector.getX() * (circumcenter.getX() + x) + normalVector.getY() * (circumcenter.getY() + y) + normalVector.getZ()*(circumcenter.getZ() + z);
					  
					  //gets center-on blocks
					  double centerconstant = normalVector.getX() * (circumcenter.getX() + x +.5) + normalVector.getY() * (circumcenter.getY() + y+.5) + normalVector.getZ()*(circumcenter.getZ() + z+.5);
			  
			  // Check if point is within sphere and on plane (some tolerance given)
			  
			  if (tempdistance <= radius && (Math.abs(cornerconstant-planeconstant)<.05||Math.abs(centerconstant-planeconstant)<.05)) {
			 
			  // Make the changes
			  
			  current.perform(clampY(brushcenter[0] + x, brushcenter[1] + y, brushcenter[2] + z)); }
			  
			  } } }
			 
			// END BLOCKS

		}

		// Finalize Undo
		v.storeUndo(current.getUndo());

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
