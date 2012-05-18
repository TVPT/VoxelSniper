/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;

/**
 * @author Giltwist
 */
public class Triangle extends PerformBrush {

    protected boolean first = true;
    protected double[] coordsone = new double[3]; //Three corners
    protected double[] coordstwo = new double[3];
    protected double[] coordsthree = new double[3];
    protected int cornernumber = 1;
    protected double[] currentcoords = new double[3]; //For loop tracking
    protected double[] vectorone = new double[3]; //Point 1 to 2
    protected double[] vectortwo = new double[3]; // Point 1 to 3
    protected double[] vectorthree = new double[3]; // Point 2 to 3, for area calculations    
    protected double[] normalvector = new double[3];

    public Triangle() {
        name = "Triangle";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {

        TriangleA(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {  //Add a point

        TriangleP(v);

    }

    @Override
    public void info(vMessage vm) { //Make the triangle
        vm.brushName(name);
        //vm.voxel();
        //vm.custom(ChatColor.GRAY + "First Corner: " + coordsone[0] + ", " + coordsone[1] + ", " + coordsone[2] + ")");
        //vm.custom(ChatColor.GRAY + "Second Corner: " + coordstwo[0] + ", " + coordstwo[1] + ", " + coordstwo[2] + ")");
        //vm.custom(ChatColor.GRAY + "Third Corner: " + coordsthree[0] + ", " + coordsthree[1] + ", " + coordsthree[2] + ")");
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Triangle Brush instructions: Select three corners with the arrow brush, then generate the triangle with the powder brush.");
        }
    }

    public void TriangleA(vData v) {
        switch (cornernumber) {
            case 1:
                coordsone[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX()); //I hate you sometimes, Notch.  Really? Every quadrant is different?
                coordsone[1] = tb.getY() + .5;
                coordsone[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());
                cornernumber = 2;
                v.sendMessage(ChatColor.GRAY + "First Corner set.");
                break;
            case 2:
                coordstwo[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX()); //I hate you sometimes, Notch.  Really? Every quadrant is different?
                coordstwo[1] = tb.getY() + .5;
                coordstwo[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());
                cornernumber = 3;
                v.sendMessage(ChatColor.GRAY + "Second Corner set.");
                break;
            case 3:
                coordsthree[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX()); //I hate you sometimes, Notch.  Really? Every quadrant is different?
                coordsthree[1] = tb.getY() + .5;
                coordsthree[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());
                cornernumber = 1;
                v.sendMessage(ChatColor.GRAY + "Third Corner set.");
                break;

        }

    }

    public void TriangleP(vData v) {
        w = v.owner().p.getWorld();
        int bId = v.voxelId;
        double lengthone = 0;
        double lengthtwo = 0;
        double lengththree = 0;
        double heronbig = 0;


        //Calculate slope vectors
        for (int i = 0; i < 3; i++) {
            vectorone[i] = coordstwo[i] - coordsone[i];
            vectortwo[i] = coordsthree[i] - coordsone[i];
            vectorthree[i] = coordsthree[i] - coordstwo[i];
        }

        //Calculate the cross product of vectorone and vectortwo
        normalvector[0] = vectorone[1] * vectortwo[2] - vectorone[2] * vectortwo[1];
        normalvector[1] = vectorone[2] * vectortwo[0] - vectorone[0] * vectortwo[2];
        normalvector[2] = vectorone[0] * vectortwo[1] - vectorone[1] * vectortwo[0];

        //Calculate magnitude of slope vectors
        lengthone = Math.pow(Math.pow(vectorone[0], 2) + Math.pow(vectorone[1], 2) + Math.pow(vectorone[2], 2), .5);
        lengthtwo = Math.pow(Math.pow(vectortwo[0], 2) + Math.pow(vectortwo[1], 2) + Math.pow(vectortwo[2], 2), .5);
        lengththree = Math.pow(Math.pow(vectorthree[0], 2) + Math.pow(vectorthree[1], 2) + Math.pow(vectorthree[2], 2), .5);

        //Bigger vector determines brush size
        int bsize = (int) Math.ceil((lengthone > lengthtwo) ? lengthone : lengthtwo);

        //Calculate constant term
        double planeconstant = normalvector[0] * coordsone[0] + normalvector[1] * coordsone[1] + normalvector[2] * coordsone[2];

        //Calculate the area of the full triangle
        heronbig = .25 * Math.pow(Math.pow(Math.pow(lengthone, 2) + Math.pow(lengthtwo, 2) + Math.pow(lengththree, 2), 2) - 2 * (Math.pow(lengthone, 4) + Math.pow(lengthtwo, 4) + Math.pow(lengththree, 4)), .5);


        if (lengthone == 0 || lengthtwo == 0 || (coordsone[0] == 0 && coordsone[1] == 0 && coordsone[2] == 0) || (coordstwo[0] == 0 && coordstwo[1] == 0 && coordstwo[2] == 0) || (coordsthree[0] == 0 && coordsthree[1] == 0 && coordsthree[2] == 0)) {
            v.sendMessage(ChatColor.RED + "ERROR: Invalid corners, please try again.");
        } else {
            //Make the Changes

            double[] cvectorone = new double[3];
            double[] cvectortwo = new double[3];
            double[] cvectorthree = new double[3];

            double clengthone = 0;
            double clengthtwo = 0;
            double clengththree = 0;

            double heronone;
            double herontwo;
            double heronthree;


            for (int y = -bsize; y <= bsize; y++) { //X DEPENDENT
                for (int z = -bsize; z <= bsize; z++) {


                    currentcoords[1] = coordsone[1] + y;
                    currentcoords[2] = coordsone[2] + z;
                    currentcoords[0] = (planeconstant - normalvector[1] * currentcoords[1] - normalvector[2] * currentcoords[2]) / normalvector[0];

                    //Area of triangle currentcoords, coordsone, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = coordstwo[i] - coordsone[i];
                        cvectortwo[i] = currentcoords[i] - coordsone[i];
                        cvectorthree[i] = currentcoords[i] - coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);



                    heronone = .25 * Math.pow(Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2 * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    //Area of triangle currentcoords, coordsthree, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = coordstwo[i] - coordsthree[i];
                        cvectortwo[i] = currentcoords[i] - coordsthree[i];
                        cvectorthree[i] = currentcoords[i] - coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    herontwo = .25 * Math.pow(Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2 * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);



                    //Area of triangle currentcoords, coordsthree, coordsone
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = coordsone[i] - coordsthree[i];
                        cvectortwo[i] = currentcoords[i] - coordsthree[i];
                        cvectorthree[i] = currentcoords[i] - coordsone[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    heronthree = .25 * Math.pow(Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2 * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);


                    double barycentric = (heronone + herontwo + heronthree) / heronbig;

                    //VoxelSniper.log.info("Bary: "+barycentric+", hb: "+heronbig+", h1: "+heronone+", h2: "+herontwo+", h3: "+heronthree);


                    if (barycentric <= 1.1) {


                        current.perform(clampY((int) currentcoords[0], (int) currentcoords[1], (int) currentcoords[2]));


                    }




                }
            } //END X DEPENDENT

            for (int x = -bsize; x <= bsize; x++) { //Y DEPENDENT
                for (int z = -bsize; z <= bsize; z++) {


                    currentcoords[0] = coordsone[0] + x;
                    currentcoords[2] = coordsone[2] + z;
                    currentcoords[1] = (planeconstant - normalvector[0] * currentcoords[0] - normalvector[2] * currentcoords[2]) / normalvector[1];

                    //Area of triangle currentcoords, coordsone, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = coordstwo[i] - coordsone[i];
                        cvectortwo[i] = currentcoords[i] - coordsone[i];
                        cvectorthree[i] = currentcoords[i] - coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);



                    heronone = .25 * Math.pow(Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2 * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    //Area of triangle currentcoords, coordsthree, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = coordstwo[i] - coordsthree[i];
                        cvectortwo[i] = currentcoords[i] - coordsthree[i];
                        cvectorthree[i] = currentcoords[i] - coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    herontwo = .25 * Math.pow(Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2 * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);



                    //Area of triangle currentcoords, coordsthree, coordsone
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = coordsone[i] - coordsthree[i];
                        cvectortwo[i] = currentcoords[i] - coordsthree[i];
                        cvectorthree[i] = currentcoords[i] - coordsone[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    heronthree = .25 * Math.pow(Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2 * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);


                    double barycentric = (heronone + herontwo + heronthree) / heronbig;

                    //VoxelSniper.log.info("Bary: "+barycentric+", hb: "+heronbig+", h1: "+heronone+", h2: "+herontwo+", h3: "+heronthree);


                    if (barycentric <= 1.1) {


                        current.perform(clampY((int) currentcoords[0], (int) currentcoords[1], (int) currentcoords[2]));


                    }




                }
            } //END Y DEPENDENT
            for (int x = -bsize; x <= bsize; x++) { //Z DEPENDENT
                for (int y = -bsize; y <= bsize; y++) {


                    currentcoords[0] = coordsone[0] + x;
                    currentcoords[1] = coordsone[1] + y;
                    currentcoords[2] = (planeconstant - normalvector[0] * currentcoords[0] - normalvector[1] * currentcoords[1]) / normalvector[2];

                    //Area of triangle currentcoords, coordsone, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = coordstwo[i] - coordsone[i];
                        cvectortwo[i] = currentcoords[i] - coordsone[i];
                        cvectorthree[i] = currentcoords[i] - coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);



                    heronone = .25 * Math.pow(Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2 * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);

                    //Area of triangle currentcoords, coordsthree, coordstwo
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = coordstwo[i] - coordsthree[i];
                        cvectortwo[i] = currentcoords[i] - coordsthree[i];
                        cvectorthree[i] = currentcoords[i] - coordstwo[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    herontwo = .25 * Math.pow(Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2 * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);



                    //Area of triangle currentcoords, coordsthree, coordsone
                    for (int i = 0; i < 3; i++) {
                        cvectorone[i] = coordsone[i] - coordsthree[i];
                        cvectortwo[i] = currentcoords[i] - coordsthree[i];
                        cvectorthree[i] = currentcoords[i] - coordsone[i];
                    }
                    clengthone = Math.pow(Math.pow(cvectorone[0], 2) + Math.pow(cvectorone[1], 2) + Math.pow(cvectorone[2], 2), .5);
                    clengthtwo = Math.pow(Math.pow(cvectortwo[0], 2) + Math.pow(cvectortwo[1], 2) + Math.pow(cvectortwo[2], 2), .5);
                    clengththree = Math.pow(Math.pow(cvectorthree[0], 2) + Math.pow(cvectorthree[1], 2) + Math.pow(cvectorthree[2], 2), .5);
                    heronthree = .25 * Math.pow(Math.pow(Math.pow(clengthone, 2) + Math.pow(clengthtwo, 2) + Math.pow(clengththree, 2), 2) - 2 * (Math.pow(clengthone, 4) + Math.pow(clengthtwo, 4) + Math.pow(clengththree, 4)), .5);


                    double barycentric = (heronone + herontwo + heronthree) / heronbig;

                    //VoxelSniper.log.info("Bary: "+barycentric+", hb: "+heronbig+", h1: "+heronone+", h2: "+herontwo+", h3: "+heronthree);


                    if (barycentric <= 1.1) {

                        current.perform(clampY((int) currentcoords[0], (int) currentcoords[1], (int) currentcoords[2]));


                    }




                }
            } //END Z DEPENDENT

            v.storeUndo(current.getUndo());



        }







        //RESET BRUSH
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
}
