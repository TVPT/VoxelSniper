/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Gavjenks
 * Brush creates 3d mazes in a hardcoded architectural style using a perfect Prim'w algorithm.
 */
public class GavinSecret extends Brush {

    private int i;
    private Block b = null;
    private vUndo h;
    private boolean passCorrect = false;
    private int height = 2;
    private int xlen = 2;
    private int zlen = 2;
    private int[][][] wilderness;
    private int[][][] frontiers;
    private Random generator = new Random();
    private int sumFronts = 1;
    private List<int[]> FrontsList = new ArrayList();

    //private Iterator HashIt;
    public GavinSecret() {
        name = "GavinSecret";
    }

    @Override
    protected void arrow(vSniper v) {

        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        maze(tb, v);
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    @Override
    protected void powder(vSniper v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        b = null;
        vm.brushName(name);
        vm.voxel();
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.AQUA + "This brush requires a password to function.");
            v.p.sendMessage(ChatColor.BLUE + "Include password and also size in each dimension, e.g. x5 y3 z10.");
            v.p.sendMessage(ChatColor.BLUE + "Will begin from -x -y -z corner from target block. (x3 each dim + 1)");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("Z3!*uw")) {
                passCorrect = true;
                continue;
            } else if (par[x].startsWith("x")) {
                xlen = Integer.parseInt(par[x].replace("x", ""));
            } else if (par[x].startsWith("y")) {
                height = Integer.parseInt(par[x].replace("y", ""));
            } else if (par[x].startsWith("z")) {
                zlen = Integer.parseInt(par[x].replace("z", ""));
            }
        }
    }

    private void maze(Block bl, vSniper v) {
        FrontsList.clear();
        if (passCorrect) {
            h = new vUndo(tb.getWorld().getName()); // line was wrong -prz
            for (int x = 0; x < xlen * 3 + 1; x++) { //virtually every block will change, so put everything in undo.
                for (int z = 0; z < zlen * 3 + 1; z++) {
                    for (int y = 0; y < height * 3 + 1; y++) {
                        h.put(clampY(bx + x, by + z, bz + y));
                    }
                }
            }

            int[][][] blueprint = blueprint(v, xlen, zlen, height);  //design the maze
            for (int X = 0; X < xlen; X++) { //build the maze in minecraft
                for (int Z = 0; Z < zlen; Z++) {
                    for (int H = 0; H < height; H++) {
                        junction(clampY(bx + X * 3, by + H * 3, bz + Z * 3), blueprint[X][H][Z], v);
                    }
                }
            }
        } else {
            v.p.sendMessage(ChatColor.RED + "Incorrect password.");
        }
    }

    private void junction(Block bl, int openings, vSniper v) { //Builds a junction.  bl is at lowest XYZ corner of module
        //hard-coded design.
        v.p.sendMessage("openingscode@junction " + openings);
        //build floor
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                setBlockIdAt(7, bl.getX() + x, bl.getY(), bl.getZ() + z);
            }
        }
        setBlockIdAt(89, bl.getX() + 1, bl.getY(), bl.getZ() + 1); //lighting
        //build ceiling
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                setBlockIdAt(7, bl.getX() + x, bl.getY() + 3, bl.getZ() + z);
            }
        }
        setBlockIdAt(89, bl.getX() + 1, bl.getY() + 3, bl.getZ() + 1); //lighting
        //walls
        setBlockIdAt(7, bl.getX(), bl.getY() + 1, bl.getZ());
        setBlockIdAt(7, bl.getX(), bl.getY() + 1, bl.getZ() + 2);
        setBlockIdAt(7, bl.getX() + 2, bl.getY() + 1, bl.getZ());
        setBlockIdAt(7, bl.getX(), bl.getY() + 1, bl.getZ() + 3);
        setBlockIdAt(7, bl.getX() + 3, bl.getY() + 1, bl.getZ());
        setBlockIdAt(7, bl.getX() + 3, bl.getY() + 1, bl.getZ() + 2);
        setBlockIdAt(7, bl.getX() + 2, bl.getY() + 1, bl.getZ() + 3);
        setBlockIdAt(7, bl.getX() + 3, bl.getY() + 1, bl.getZ() + 3);

        setBlockIdAt(7, bl.getX(), bl.getY() + 2, bl.getZ());
        setBlockIdAt(7, bl.getX(), bl.getY() + 2, bl.getZ() + 2);
        setBlockIdAt(7, bl.getX() + 2, bl.getY() + 2, bl.getZ());
        setBlockIdAt(7, bl.getX(), bl.getY() + 2, bl.getZ() + 3);
        setBlockIdAt(7, bl.getX() + 3, bl.getY() + 2, bl.getZ());
        setBlockIdAt(7, bl.getX() + 3, bl.getY() + 2, bl.getZ() + 2);
        setBlockIdAt(7, bl.getX() + 2, bl.getY() + 2, bl.getZ() + 3);
        setBlockIdAt(7, bl.getX() + 3, bl.getY() + 2, bl.getZ() + 3);

        //two perma-ladders
        setBlockIdAt(65, bl.getX() + 2, bl.getY() + 1, bl.getZ() + 2);
        clampY(bl.getX() + 2, bl.getY() + 1, bl.getZ() + 2).setData((byte) 2);
        setBlockIdAt(65, bl.getX() + 2, bl.getY() + 2, bl.getZ() + 2);
        clampY(bl.getX() + 2, bl.getY() + 2, bl.getZ() + 2).setData((byte) 2);

        //ceiling floor holes overwrite if needed
        if (openings % 13 == 0) {
            setBlockIdAt(65, bl.getX() + 2, bl.getY() + 3, bl.getZ() + 2);
            clampY(bl.getX() + 2, bl.getY() + 3, bl.getZ() + 2).setData((byte) 2);
        }
        if (openings % 11 == 0) {
            setBlockIdAt(65, bl.getX() + 2, bl.getY(), bl.getZ() + 2);
            clampY(bl.getX() + 2, bl.getY(), bl.getZ() + 2).setData((byte) 2);
        }

        //fill in walls if needed.
        if (openings % 7 > 0) {
            setBlockIdAt(7, bl.getX() + 1, bl.getY() + 1, bl.getZ() + 3);
            setBlockIdAt(7, bl.getX() + 1, bl.getY() + 2, bl.getZ() + 3);
        }
        if (openings % 5 > 0) {
            setBlockIdAt(7, bl.getX() + 1, bl.getY() + 1, bl.getZ());
            setBlockIdAt(7, bl.getX() + 1, bl.getY() + 2, bl.getZ());
        }
        if (openings % 3 > 0) {
            setBlockIdAt(7, bl.getX(), bl.getY() + 1, bl.getZ() + 1);
            setBlockIdAt(7, bl.getX(), bl.getY() + 2, bl.getZ() + 1);
        }


        if (openings % 2 > 0) {
            setBlockIdAt(7, bl.getX() + 3, bl.getY() + 1, bl.getZ() + 1);
            setBlockIdAt(7, bl.getX() + 3, bl.getY() + 2, bl.getZ() + 1);
        }

        //2 = -X   1
        //3 = X    2
        //5 = -Z   3
        //7 = Z    4
        //11 = -Y  5
        //13 = Y   6
    }

    private int[][][] blueprint(vSniper v, int X, int Z, int Y) { //generates the design for a maze XYZ in size.
        int[] toInclude = new int[4];
        int[][][] blueprintM = new int[X][Y][Z];
        frontiers = new int[X][Y][Z];
        wilderness = new int[X][Y][Z];
        for (int x = 0; x < X; x++) { //set all cells to wilderness status at first.
            for (int y = 0; y < Y; y++) {
                for (int z = 0; z < Z; z++) {
                    wilderness[x][y][z] = 1;
                }
            }
        }
        for (int x = 0; x < X; x++) { //set all cells in blueprint to 1 so when we multiply them by stuff later they wont stay zero...
            for (int y = 0; y < Y; y++) {
                for (int z = 0; z < Z; z++) {
                    blueprintM[x][y][z] = 1;
                }
            }
        }

        //special case, first cell (special since there are no frontier cells yet)
        int randX = generator.nextInt(X);
        int randY = generator.nextInt(Y);
        int randZ = generator.nextInt(Z);
        int passage = generator.nextInt(6) + 1;
        sumFronts = 1;
        v.p.sendMessage("passage" + passage);
        if (passage == 1) {
            if (randX > 0) {
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 2;
                blueprintM[randX - 1][randY][randZ] = blueprintM[randX - 1][randY][randZ] * 3;
                toInclude[0] = randX - 1;
                toInclude[1] = randY;
                toInclude[2] = randZ;
                include(v, frontiers, wilderness, toInclude);
            } else { //boundary condition, dig other direction instead.
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 3;
                blueprintM[randX + 1][randY][randZ] = blueprintM[randX + 1][randY][randZ] * 2;
                toInclude[0] = randX + 1;
                toInclude[1] = randY;
                toInclude[2] = randZ;
                include(v, frontiers, wilderness, toInclude);
            }
        }
        if (passage == 2) {
            if (randX < X - 1) {
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 3;
                blueprintM[randX + 1][randY][randZ] = blueprintM[randX + 1][randY][randZ] * 2;
                toInclude[0] = randX + 1;
                toInclude[1] = randY;
                toInclude[2] = randZ;
                include(v, frontiers, wilderness, toInclude);
            } else { //boundary condition, dig other direction instead.
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 2;
                blueprintM[randX - 1][randY][randZ] = blueprintM[randX - 1][randY][randZ] * 3;
                toInclude[0] = randX - 1;
                toInclude[1] = randY;
                toInclude[2] = randZ;
                include(v, frontiers, wilderness, toInclude);
            }
        }
        if (passage == 3) {
            if (randZ > 0) {
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 5;
                blueprintM[randX][randY][randZ - 1] = blueprintM[randX][randY][randZ - 1] * 7;
                toInclude[0] = randX;
                toInclude[1] = randY;
                toInclude[2] = randZ - 1;
                include(v, frontiers, wilderness, toInclude);
            } else { //boundary condition, dig other direction instead.
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 7;
                blueprintM[randX][randY][randZ + 1] = blueprintM[randX][randY][randZ + 1] * 5;
                toInclude[0] = randX;
                toInclude[1] = randY;
                toInclude[2] = randZ + 1;
                include(v, frontiers, wilderness, toInclude);
            }
        }
        if (passage == 4) {
            if (randZ < Z - 1) {
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 7;
                blueprintM[randX][randY][randZ + 1] = blueprintM[randX][randY][randZ + 1] * 5;
                toInclude[0] = randX;
                toInclude[1] = randY;
                toInclude[2] = randZ + 1;
                include(v, frontiers, wilderness, toInclude);
            } else { //boundary condition, dig other direction instead.
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 5;
                blueprintM[randX][randY][randZ - 1] = blueprintM[randX][randY][randZ - 1] * 7;
                toInclude[0] = randX;
                toInclude[1] = randY;
                toInclude[2] = randZ - 1;
                include(v, frontiers, wilderness, toInclude);
            }
        }
        if (passage == 5) {
            if (randY > 0) {
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 11;
                blueprintM[randX][randY - 1][randZ] = blueprintM[randX][randY - 1][randZ] * 13;
                toInclude[0] = randX;
                toInclude[1] = randY - 1;
                toInclude[2] = randZ;
                include(v, frontiers, wilderness, toInclude);
            } else { //boundary condition, dig other direction instead.
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 13;
                blueprintM[randX][randY + 1][randZ] = blueprintM[randX][randY + 1][randZ] * 11;
                toInclude[0] = randX;
                toInclude[1] = randY + 1;
                toInclude[2] = randZ;
                include(v, frontiers, wilderness, toInclude);
            }
        }
        if (passage == 6) {
            if (randY < Y - 1) {
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 13;
                blueprintM[randX][randY + 1][randZ] = blueprintM[randX][randY + 1][randZ] * 11;
                toInclude[0] = randX;
                toInclude[1] = randY + 1;
                toInclude[2] = randZ;
                include(v, frontiers, wilderness, toInclude);
            } else { //boundary condition, dig other direction instead.
                blueprintM[randX][randY][randZ] = blueprintM[randX][randY][randZ] * 11;
                blueprintM[randX][randY - 1][randZ] = blueprintM[randX][randY - 1][randZ] * 13;
                toInclude[0] = randX;
                toInclude[1] = randY - 1;
                toInclude[2] = randZ;
                include(v, frontiers, wilderness, toInclude);
            }
        }
        v.p.sendMessage("sumfronts" + sumFronts);


        while (sumFronts > 0) { //build the rest of the blueprint
            //some sort of hashset of frontier cells
            int ListSize = FrontsList.size();
            int ListRand = generator.nextInt(ListSize);
            int[] frontierCell = (int[]) FrontsList.get(ListRand); //seriously, this cast works?  Gets a frontier cell at random
            v.p.sendMessage("frontssize " + ListSize);
            v.p.sendMessage("just before search");
            v.p.sendMessage("frontier " + frontierCell[0] + " " + frontierCell[1] + " " + frontierCell[2]);

            List<int[]> searchResults = search(v, frontiers, wilderness, frontierCell);

            int searchRand;
            if (searchResults.size() > 0) {
                searchRand = generator.nextInt(searchResults.size());
            } else {
                searchRand = generator.nextInt(1);
            }
            v.p.sendMessage("searchSize " + searchResults.size());
            if (searchResults.isEmpty()) {
                v.p.sendMessage("On second thought, I think it's this one that's empty...");
                sumFronts--;
                continue;
            }
            int[] inCell = searchResults.get(searchRand); //finds a cell in the maze at random next to this frontier cell
            if (inCell.length == 0) {
                v.p.sendMessage("Gavin the error was due to empty array");
                sumFronts--;
                continue;
            }
            include(v, frontiers, wilderness, frontierCell);
            v.p.sendMessage("in " + inCell[0] + " " + inCell[1] + " " + inCell[2]);
            if (frontierCell[0] < inCell[0]) { //carve the actual passages between the two cells
                blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] = blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] * 2;
                blueprintM[inCell[0]][inCell[1]][inCell[2]] = blueprintM[inCell[0]][inCell[1]][inCell[2]] * 3;
            } else if (frontierCell[0] > inCell[0]) {
                blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] = blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] * 3;
                blueprintM[inCell[0]][inCell[1]][inCell[2]] = blueprintM[inCell[0]][inCell[1]][inCell[2]] * 2;
            } else if (frontierCell[1] < inCell[1]) {
                blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] = blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] * 13;
                blueprintM[inCell[0]][inCell[1]][inCell[2]] = blueprintM[inCell[0]][inCell[1]][inCell[2]] * 11;
            } else if (frontierCell[1] > inCell[1]) {
                blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] = blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] * 11;
                blueprintM[inCell[0]][inCell[1]][inCell[2]] = blueprintM[inCell[0]][inCell[1]][inCell[2]] * 13;
            } else if (frontierCell[2] < inCell[2]) {
                blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] = blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] * 7;
                blueprintM[inCell[0]][inCell[1]][inCell[2]] = blueprintM[inCell[0]][inCell[1]][inCell[2]] * 5;
            } else if (frontierCell[2] > inCell[2]) {
                blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] = blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]] * 5;
                blueprintM[inCell[0]][inCell[1]][inCell[2]] = blueprintM[inCell[0]][inCell[1]][inCell[2]] * 7;
            }
            v.p.sendMessage("blueprintcode" + blueprintM[frontierCell[0]][frontierCell[1]][frontierCell[2]]);
        }

        return blueprintM;
    }

    private void include(vSniper v, int[][][] frontiers, int[][][] wilderness, int[] targetCell) { //adds a frontier cell to the maze, and then adds any wilderness cells next to it to the list of frontier cells
        v.p.sendMessage("SF before" + sumFronts);
        v.p.sendMessage("including: " + targetCell[0] + " " + targetCell[1] + " " + targetCell[2]);
        int[] refCell = targetCell;
        int[] WTF = targetCell;
        frontiers[targetCell[0]][targetCell[1]][targetCell[2]] = 0;
        wilderness[targetCell[0]][targetCell[1]][targetCell[2]] = 0;
        int[] otherCell = targetCell;
        sumFronts--;
        try { //first cell never was a frontier cell, and general glitch catching.
            FrontsList.remove(targetCell); //remove from hashset
        } catch (Exception e) {
        }
        try {
            if (wilderness[targetCell[0] + 1][targetCell[1]][targetCell[2]] == 1) {
                wilderness[targetCell[0] + 1][targetCell[1]][targetCell[2]] = 0;
                frontiers[targetCell[0] + 1][targetCell[1]][targetCell[2]] = 1;
                sumFronts++;
                v.p.sendMessage("Ref before being referenced: " + refCell[0] + " " + refCell[1] + " " + refCell[2]);
                //v.p.sendMessage("WTF which is not ref'd AT ALL: " + WTF[0] + " " + WTF[1] + " " + WTF[2]);

                otherCell[0] = refCell[0];
                otherCell[1] = refCell[1];
                otherCell[2] = refCell[2];
                v.p.sendMessage("Other right after restting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);

                otherCell[0] = otherCell[0] + 1; //add new frontier cell to hashset
                v.p.sendMessage("Other right after adjusting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);

                FrontsList.add(otherCell);
                v.p.sendMessage("New Frontier: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);
            }
        } catch (Exception e) {
            //v.p.sendMessage("EXCEPTIONTYPE" + e.toString());
            //v.p.sendMessage("EXCEPTION");//nothing  This is just a cheap and easy way to not run specific boudnary checks.
        }

        try {
            if (wilderness[targetCell[0] - 1][targetCell[1]][targetCell[2]] == 1) {
                wilderness[targetCell[0] - 1][targetCell[1]][targetCell[2]] = 0;
                frontiers[targetCell[0] - 1][targetCell[1]][targetCell[2]] = 1;
                sumFronts++;
                v.p.sendMessage("Ref before being referenced: " + refCell[0] + " " + refCell[1] + " " + refCell[2]);
                //v.p.sendMessage("WTF which is not ref'd AT ALL: " + WTF[0] + " " + WTF[1] + " " + WTF[2]);
                otherCell[0] = refCell[0];
                otherCell[1] = refCell[1];
                otherCell[2] = refCell[2];
                v.p.sendMessage("Other right after restting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);

                otherCell[0] = otherCell[0] - 1;
                v.p.sendMessage("Other right after adjusting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);

                FrontsList.add(otherCell);
                v.p.sendMessage("New Frontier: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);
            }
        } catch (Exception e) {
            //nothing  This is just a cheap and easy way to not run specific boudnary checks.
        }

        try {
            if (wilderness[targetCell[0]][targetCell[1] + 1][targetCell[2]] == 1) {
                wilderness[targetCell[0]][targetCell[1] + 1][targetCell[2]] = 0;
                frontiers[targetCell[0]][targetCell[1] + 1][targetCell[2]] = 1;
                sumFronts++;
                v.p.sendMessage("Ref before being referenced: " + refCell[0] + " " + refCell[1] + " " + refCell[2]);
                //v.p.sendMessage("WTF which is not ref'd AT ALL: " + WTF[0] + " " + WTF[1] + " " + WTF[2]);
                otherCell[0] = refCell[0];
                otherCell[1] = refCell[1];
                otherCell[2] = refCell[2];
                v.p.sendMessage("Other right after restting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);

                otherCell[1] = otherCell[1] + 1;
                v.p.sendMessage("Other right after adjusting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);

                FrontsList.add(otherCell);
                v.p.sendMessage("New Frontier: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);
            }
        } catch (Exception e) {
            //nothing  This is just a cheap and easy way to not run specific boudnary checks.
        }

        try {
            if (wilderness[targetCell[0]][targetCell[1] - 1][targetCell[2]] == 1) {
                wilderness[targetCell[0]][targetCell[1] - 1][targetCell[2]] = 0;
                frontiers[targetCell[0]][targetCell[1] - 1][targetCell[2]] = 1;
                sumFronts++;
                v.p.sendMessage("Ref before being referenced: " + refCell[0] + " " + refCell[1] + " " + refCell[2]);
                //v.p.sendMessage("WTF which is not ref'd AT ALL: " + WTF[0] + " " + WTF[1] + " " + WTF[2]);
                otherCell[0] = refCell[0];
                otherCell[1] = refCell[1];
                otherCell[2] = refCell[2];
                v.p.sendMessage("Other right after restting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);
                otherCell[1] = otherCell[1] - 1;
                v.p.sendMessage("Other right after adjusting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);
                FrontsList.add(otherCell);
                v.p.sendMessage("New Frontier: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);
            }
        } catch (Exception e) {
            //nothing  This is just a cheap and easy way to not run specific boudnary checks.
        }

        try {
            if (wilderness[targetCell[0]][targetCell[1]][targetCell[2] + 1] == 1) {
                wilderness[targetCell[0]][targetCell[1]][targetCell[2] + 1] = 0;
                frontiers[targetCell[0]][targetCell[1]][targetCell[2] + 1] = 1;
                sumFronts++;
                v.p.sendMessage("Ref before being referenced: " + refCell[0] + " " + refCell[1] + " " + refCell[2]);
                v.p.sendMessage("WTF which is not ref'd AT ALL: " + WTF[0] + " " + WTF[1] + " " + WTF[2]);
                otherCell[0] = refCell[0];
                otherCell[1] = refCell[1];
                otherCell[2] = refCell[2];
                v.p.sendMessage("Other right after restting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);

                otherCell[2] = otherCell[2] + 1;
                v.p.sendMessage("Other right after adjusting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);

                FrontsList.add(otherCell);
                v.p.sendMessage("New Frontier: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);
            }
        } catch (Exception e) {
            //nothing  This is just a cheap and easy way to not run specific boudnary checks.
        }

        try {
            if (wilderness[targetCell[0]][targetCell[1]][targetCell[2] - 1] == 1) {
                wilderness[targetCell[0]][targetCell[1]][targetCell[2] - 1] = 0;
                frontiers[targetCell[0]][targetCell[1]][targetCell[2] - 1] = 1;
                sumFronts++;
                v.p.sendMessage("Ref before being referenced: " + refCell[0] + " " + refCell[1] + " " + refCell[2]);
                v.p.sendMessage("WTF which is not ref'd AT ALL: " + WTF[0] + " " + WTF[1] + " " + WTF[2]);
                otherCell[0] = refCell[0];
                otherCell[1] = refCell[1];
                otherCell[2] = refCell[2];
                v.p.sendMessage("Other right after restting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);

                otherCell[2] = otherCell[2] - 1;
                v.p.sendMessage("Other right after adjusting: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);

                FrontsList.add(otherCell);
                v.p.sendMessage("New Frontier: " + otherCell[0] + " " + otherCell[1] + " " + otherCell[2]);
            }
        } catch (Exception e) {
            //nothing  This is just a cheap and easy way to not run specific boudnary checks.
        }
        v.p.sendMessage("SF after" + sumFronts);
    }

    private ArrayList<int[]> search(vSniper v, int[][][] frontiers, int[][][] wilderness, int[] targetCell) { //returns a list of all the "in" cells adjacent to any given frontier cell.
        //int[][] frontiersList = new int[6][3];
        v.p.sendMessage("got to search");
        ArrayList<int[]> inList = new ArrayList();
        inList.clear();
        int[] temp;
        v.p.sendMessage("target" + targetCell[0] + targetCell[1] + targetCell[2]);

        try {
            if (frontiers[targetCell[0] + 1][targetCell[1]][targetCell[2]] == 0 && wilderness[targetCell[0] + 1][targetCell[1]][targetCell[2]] == 0) {
                temp = targetCell;
                temp[0] = temp[0] + 1;
                inList.add(temp);
                v.p.sendMessage("yes");
            }
        } catch (Exception e) {
            v.p.sendMessage("no");
        }
        v.p.sendMessage("second check in search");
        try {
            if (frontiers[targetCell[0] - 1][targetCell[1]][targetCell[2]] == 0 && wilderness[targetCell[0] - 1][targetCell[1]][targetCell[2]] == 0) {
                temp = targetCell;
                temp[0] = temp[0] - 1;
                inList.add(temp);
                v.p.sendMessage("yes");
            }
        } catch (Exception e) {
            v.p.sendMessage("no");
        }
        v.p.sendMessage("third check in search");
        try {
            if (frontiers[targetCell[0]][targetCell[1] + 1][targetCell[2]] == 0 && wilderness[targetCell[0]][targetCell[1] + 1][targetCell[2]] == 0) {
                temp = targetCell;
                temp[0] = temp[1] + 1;
                inList.add(temp);
                v.p.sendMessage("yes");
            }
        } catch (Exception e) {
            v.p.sendMessage("no");
        }
        v.p.sendMessage("fourth check in search");
        try {
            if (frontiers[targetCell[0]][targetCell[1] - 1][targetCell[2]] == 0 && wilderness[targetCell[0]][targetCell[1] - 1][targetCell[2]] == 0) {
                temp = targetCell;
                temp[0] = temp[1] - 1;
                inList.add(temp);
                v.p.sendMessage("yes");
            }
        } catch (Exception e) {
            v.p.sendMessage("no");
        }
        v.p.sendMessage("fifth check in search");
        try {
            if (frontiers[targetCell[0]][targetCell[1]][targetCell[2] + 1] == 0 && wilderness[targetCell[0]][targetCell[1]][targetCell[2] + 1] == 0) {
                temp = targetCell;
                temp[0] = temp[2] + 1;
                inList.add(temp);
                v.p.sendMessage("yes");
            }
        } catch (Exception e) {
            v.p.sendMessage("no");
        }
        v.p.sendMessage("sixth check in search");
        try {
            if (frontiers[targetCell[0]][targetCell[1]][targetCell[2] - 1] == 0 && wilderness[targetCell[0]][targetCell[1]][targetCell[2] - 1] == 0) {
                temp = targetCell;
                temp[0] = temp[2] - 1;
                inList.add(temp);
                v.p.sendMessage("yes");
            }
        } catch (Exception e) {
            v.p.sendMessage("no");
        }
        return inList;
    }
}
