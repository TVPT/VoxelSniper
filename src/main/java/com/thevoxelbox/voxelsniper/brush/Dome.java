/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;

/**
 *
 * @author Gavjenks
 */
public class Dome extends Brush {

    boolean fsa = true;
    boolean powder = false;
    
    public Dome() {
        name = "Dome";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        powder = false;
        pre(v, tb.getFace(lb));
    }

    @Override
    public void powder(vSniper v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        powder = true;
        pre(v, tb.getFace(lb));
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.voxel();
        vm.height();
                
    }

    //should only have the truecircle option this time.
    //param is just height -- will start with the face disc and instead of making the actual disc, will make a dome of that height.
    //defaul height = diameter of the disc.  Other heights would make parabolic domes, thus I am developing this in concurrence with parabola brush.  It will be able to do everything this can, but would be harder to use.
    //for simplicity for this brush also, arrow will do half block accuracy, powder full block - will work eve for sideways dome, though maybe limited usefulness there.  If /v is not a half block, override to full block accuracy.  If it is, make full double step stuff for all the blocks underneath the top curve, in same material.

    double height = 1024; //just avoiding initiating bsize yet;
    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Dome brush Parameters:");
            v.p.sendMessage(ChatColor.AQUA + "/b dome h[number] -- set a custom dome height.  Default is the radius of the brush.  Anything else will make it a parabolic dome with circular base.  Cannot be negative.");
            v.p.sendMessage(ChatColor.BLUE + "/b dome acc [or inacc] -- set brush to half (acc) or full step (inacc) accuracy.  if /v is anything other than 44, will override you and force full step accuracy.");

            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("h")) {
                height = Double.parseDouble(par[x].replace("h", ""));
                v.p.sendMessage(ChatColor.AQUA + "Dome height set to: " + height);
                continue;
            }
            else if(par[x].startsWith("inacc")) {
                fsa = true;
                v.p.sendMessage(ChatColor.BLUE + "Full step accuracy.");
                continue;
            } else if (par[x].startsWith("acc")) {

                if (v.voxelId != 44) {
                    fsa = true;
                    v.p.sendMessage(ChatColor.BLUE + "Full step accuracy. (overridden since you don't have half steps selected)");
                } else {
                    fsa = false;
                    v.p.sendMessage(ChatColor.BLUE + "Half step accuracy.");

                }
                continue;
            } else {
                v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }

    }

    private void pre(vSniper v, BlockFace bf) {
        if(bf == null) {
            return;
        }
        switch (bf) {
            case NORTH:
            case SOUTH:
                dome(v); //would be domeNS later
                break;

            case EAST:
            case WEST:
                dome(v); //would be domeEW later
                break;

            case UP:
            case DOWN:
                dome(v);
                break;

            default:
                break;
        }
    }

    public void dome(vSniper v) {
        int bsize = v.brushSize;
        int bId = v.voxelId;

        vUndo h = new vUndo(tb.getWorld().getName());

        double bpow = Math.pow(bsize +0.5, 2);
        //double curvature = 1; //actually not necessary if base is circular.
        if (height == 1024) {
            height = bsize + 0.5;
        }
        double curvature = height / (bsize + 0.5);
        double yManip = 0.5;
        double centerRef = 0;
        if (fsa || bId != 44) { //override half block accuracy if /v not set to a half block.
            yManip = 0.5; //whole block accuracy
            centerRef = 0;
        } else {
            yManip = 0.5; //half block accuracy
            centerRef = -0.25;
        }

        
        int[][] heightmap = new int[bsize+2][bsize+2];
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int z = bsize; z >= 0; z--) {
                double zpow = Math.pow(z, 2);
                for (int y = (int)height; y >= 0; y--) {
                    double ypowminus = Math.pow(y - yManip + centerRef, 2);
                    double ypow = Math.pow(y + centerRef,2);
                    if ((xpow + (ypowminus / Math.pow(curvature, 2)) + zpow) <= bpow) { //If within the ellipse
                        double ypowplus = Math.pow(y + yManip + centerRef, 2);
                        if ((xpow + (ypowplus / Math.pow(curvature, 2)) + zpow) > bpow) { //If nothing else further out (i.e. if on the surface)
                            h.put(clampY(bx + x, by + y, bz + z));
                            h.put(clampY(bx + x, by + y, bz - z));
                            h.put(clampY(bx - x, by + y, bz + z)); //only want top of dome.  So only 4 of these.
                            h.put(clampY(bx - x, by + y, bz - z));

                            h.put(clampY(bx + x, by + y -1, bz + z));
                            h.put(clampY(bx + x, by + y -1, bz - z)); //blocks right underneath each
                            h.put(clampY(bx - x, by + y -1, bz + z));
                            h.put(clampY(bx - x, by + y -1, bz - z));
                            
                            
                            if (!fsa && ((xpow + (ypow / Math.pow(curvature, 2)) + zpow) > bpow)) { //if half block accuracy is being used AND this is a portion of the curve that is closer to matching a half block than a full block...
                                setBlockIdAt(44, bx + x, by + y, bz + z); //set to a half block
                                setBlockIdAt(44, bx + x, by + y, bz - z);
                                setBlockIdAt(44, bx - x, by + y, bz + z);
                                setBlockIdAt(44, bx - x, by + y, bz - z);
                                //AND place a full double step underneath to prevent gaps (might be slightly bulkier than could be possible... but much simpler to code)
                                heightmap[x][z] = y - 1;
                                setBlockIdAt(43, bx + x, by + y - 1, bz + z);
                                setBlockIdAt(43, bx + x, by + y - 1, bz - z);
                                setBlockIdAt(43, bx - x, by + y - 1, bz + z);
                                setBlockIdAt(43, bx - x, by + y - 1, bz - z);
                            } else {
                                if (bId == 44) { //if half block accuracy, but this particular position conforms better to a full block
                                    heightmap[x][z] = y;
                                    setBlockIdAt(43, bx + x, by + y, bz + z); //set to a full double step
                                    setBlockIdAt(43, bx + x, by + y, bz - z);
                                    setBlockIdAt(43, bx - x, by + y, bz + z);
                                    setBlockIdAt(43, bx - x, by + y, bz - z);
                                } else { //if full block accuracy
                                    heightmap[x][z] = y;
                                    setBlockIdAt(bId, bx + x, by + y, bz + z); //set to a full block of whatever /v is.
                                    setBlockIdAt(bId, bx + x, by + y, bz - z);
                                    setBlockIdAt(bId, bx - x, by + y, bz + z);
                                    setBlockIdAt(bId, bx - x, by + y, bz - z);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int x = 0; x <= bsize; x++) {
            for (int z = 0; z <= bsize; z++) {
                for (int i = heightmap[x][z]-1; i >= 0; i--) {
                     if (!powder && heightmap[x][z + 1] < i || heightmap[x + 1][z] < i) { //if annoying air gap in wall in x or z direction
                        h.put(clampY(bx + x, by + i, bz + z));
                        h.put(clampY(bx + x, by + i, bz - z));
                        h.put(clampY(bx - x, by + i, bz + z));
                        h.put(clampY(bx - x, by + i, bz - z));

                        if (bId == 44) {
                            setBlockIdAt(43, bx + x, by + i, bz + z);
                            setBlockIdAt(43, bx + x, by + i, bz - z);
                            setBlockIdAt(43, bx - x, by + i, bz + z);
                            setBlockIdAt(43, bx - x, by + i, bz - z);
                        } else {
                            setBlockIdAt(bId, bx + x, by + i, bz + z);
                            setBlockIdAt(bId, bx + x, by + i, bz - z);
                            setBlockIdAt(bId, bx - x, by + i, bz + z);
                            setBlockIdAt(bId, bx - x, by + i, bz - z);
                        }
                    }
                    if (powder) { //fill in solid.
                        h.put(clampY(bx + x, by + i, bz + z));
                        h.put(clampY(bx + x, by + i, bz - z));
                        h.put(clampY(bx - x, by + i, bz + z));
                        h.put(clampY(bx - x, by + i, bz - z));

                        if (bId == 44) {
                            setBlockIdAt(43, bx + x, by + i, bz + z);
                            setBlockIdAt(43, bx + x, by + i, bz - z);
                            setBlockIdAt(43, bx - x, by + i, bz + z);
                            setBlockIdAt(43, bx - x, by + i, bz - z);
                        } else {
                            setBlockIdAt(bId, bx + x, by + i, bz + z);
                            setBlockIdAt(bId, bx + x, by + i, bz - z);
                            setBlockIdAt(bId, bx - x, by + i, bz + z);
                            setBlockIdAt(bId, bx - x, by + i, bz - z);
                        }
                    }

                }
            }
        }

        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    //###### nothing below here fixed yet.  Copy above code, but switch out all the y'w for whatever other axis... BUT complication: the checks for half block accuracy must always still be y-oriented...  harder.
    
   /* public void domeNS(vSniper v) {
        int bsize = v.brushSize;
        int bId = v.voxelId;
        w = w;


        vUndo h = new vUndo(tb.getWorld().getName());

        double bpow = Math.pow(bsize +0.5, 2);
        //double curvature = 1; //actually not necessary if base is circular.
        if (height == 1024) {
            height = bsize + 0.5;
        }
        double curvature = height / (bsize + 0.5);
        double yManip = 0.5;
        double centerRef = 0;
        if (fsa || bId != 44) { //override half block accuracy if /v not set to a half block.
            yManip = 0.5; //whole block accuracy
            centerRef = 0;
        } else {
            yManip = 0.5; //half block accuracy
            centerRef = -0.25;
        }


        int[][] heightmap = new int[(int)height+2][bsize+2];
        for (int x = (int)height; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int z = bsize; z >= 0; z--) {
                double zpow = Math.pow(z, 2);
                for (int y = bsize; y >= 0; y--) {
                    double ypowminus = Math.pow(y - yManip + centerRef, 2);
                    double ypow = Math.pow(y + centerRef,2);
                    if ((xpow/Math.pow(curvature, 2) + ypowminus + zpow) <= bpow) { //If within the ellipse
                        double ypowplus = Math.pow(y + yManip + centerRef, 2);
                        if ((xpow/Math.pow(curvature, 2) + ypowplus + zpow) > bpow) { //If nothing else further out (i.e. if on the surface) #### or nothing below (surface on bottom)
                            h.put(clampY(bx + x, by + y, bz + z));
                            h.put(clampY(bx + x, by + y, bz - z));
                            h.put(clampY(bx + x, by - y+1, bz + z)); //only want top of dome.  So only 4 of these.
                            h.put(clampY(bx + x, by - y+1, bz - z));

                            h.put(clampY(bx + x, by + y -1, bz + z));
                            h.put(clampY(bx + x, by + y -1, bz - z)); //blocks right underneath each
                            h.put(clampY(bx + x, by - y , bz + z));
                            h.put(clampY(bx + x, by - y , bz - z));


                            if (!fsa && ((xpow + (ypow / Math.pow(curvature, 2)) + zpow) > bpow)) { //if half block accuracy is being used AND this is a portion of the curve that is closer to matching a half block than a full block...
                                setBlockIdAt(44, bx + x, by + y, bz + z); //set to a half block
                                setBlockIdAt(44, bx + x, by + y, bz - z);
                                setBlockIdAt(44, bx + x, by - y+1, bz + z);
                                setBlockIdAt(44, bx + x, by - y+1, bz - z);
                                //AND place a full double step underneath to prevent gaps (might be slightly bulkier than could be possible... but much simpler to code)
                                heightmap[x][z] = y - 1;
                                setBlockIdAt(43, bx + x, by + y - 1, bz + z);
                                setBlockIdAt(43, bx + x, by + y - 1, bz - z);
                                setBlockIdAt(43, bx + x, by - y , bz + z);
                                setBlockIdAt(43, bx + x, by - y , bz - z);
                            } else {
                                if (bId == 44) { //if half block accuracy, but this particular position conforms better to a full block
                                    heightmap[x][z] = y;
                                    setBlockIdAt(43, bx + x, by + y, bz + z); //set to a full double step
                                    setBlockIdAt(43, bx + x, by + y, bz - z);
                                    setBlockIdAt(43, bx + x, by - y+1, bz + z);
                                    setBlockIdAt(43, bx + x, by - y+1, bz - z);
                                } else { //if full block accuracy
                                    heightmap[x][z] = y;
                                    setBlockIdAt(bId, bx + x, by + y, bz + z); //set to a full block of whatever /v is.
                                    setBlockIdAt(bId, bx + x, by + y, bz - z);
                                    setBlockIdAt(bId, bx + x, by - y+1, bz + z);
                                    setBlockIdAt(bId, bx + x, by - y+1, bz - z);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int x = 0; x <= bsize; x++) {
            for (int z = 0; z <= bsize; z++) {
                for (int i = heightmap[x][z]-1; i >= 0; i--) { // zero should be bottom of curve for top blocks, and top of curve with reverse counting up for the bottom
                     if (!powder && heightmap[x][z + 1] < i || heightmap[x + 1][z] < i) { //if annoying air gap in wall in x or z direction
                        h.put(clampY(bx + x, by + i, bz + z));
                        h.put(clampY(bx + x, by + i, bz - z));
                        h.put(clampY(bx + x, by + i, bz + z));
                        h.put(clampY(bx + x, by + i, bz - z));

                        if (bId == 44) {
                            setBlockIdAt(43, bx + x, by + i, bz + z);
                            setBlockIdAt(43, bx + x, by + i, bz - z);
                            setBlockIdAt(43, bx + x, by + i, bz + z);
                            setBlockIdAt(43, bx + x, by + i, bz - z);
                        } else {
                            setBlockIdAt(bId, bx + x, by + i, bz + z);
                            setBlockIdAt(bId, bx + x, by + i, bz - z);
                            setBlockIdAt(bId, bx + x, by + i, bz + z);
                            setBlockIdAt(bId, bx + x, by + i, bz - z);
                        }
                    }
                    if (powder) { //fill in solid.
                        h.put(clampY(bx + x, by + i, bz + z));
                        h.put(clampY(bx + x, by + i, bz - z));
                        h.put(clampY(bx + x, by + i, bz + z));
                        h.put(clampY(bx + x, by + i, bz - z));

                        if (bId == 44) {
                            setBlockIdAt(43, bx + x, by + i, bz + z);
                            setBlockIdAt(43, bx + x, by + i, bz - z);
                            setBlockIdAt(43, bx + x, by + i, bz + z);
                            setBlockIdAt(43, bx + x, by + i, bz - z);
                        } else {
                            setBlockIdAt(bId, bx + x, by + i, bz + z);
                            setBlockIdAt(bId, bx + x, by + i, bz - z);
                            setBlockIdAt(bId, bx + x, by + i, bz + z);
                            setBlockIdAt(bId, bx + x, by + i, bz - z);
                        }
                    }

                }
            }
        }

        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    public void domeEW(vSniper v) {
        int bsize = v.brushSize;
        int bId = v.voxelId;
        w = w;

        vUndo h = new vUndo(tb.getWorld().getName());

        double bpow = Math.pow(bsize+0.5, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (getBlockIdAt(bx, by + x, bz + y) != bId) {
                        h.put(clampY(bx, by + x, bz + y));
                    }
                    if (getBlockIdAt(bx, by + x, bz - y) != bId) {
                        h.put(clampY(bx, by + x, bz - y));
                    }
                    if (getBlockIdAt(bx, by - x, bz + y) != bId) {
                        h.put(clampY(bx, by - x, bz + y));
                    }
                    if (getBlockIdAt(bx, by - x, bz - y) != bId) {
                        h.put(clampY(bx, by - x, bz - y));
                    }
                    setBlockIdAt(bId, bx, by + x, bz + y);
                    setBlockIdAt(bId, bx, by + x, bz - y);
                    setBlockIdAt(bId, bx, by - x, bz + y);
                    setBlockIdAt(bId, bx, by - x, bz - y);
                }
            }
        }
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }
    
    */
}
