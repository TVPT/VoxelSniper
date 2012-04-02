/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Voxel
 */
public enum vBrushes { // DO NOTE: PSANKER IS REWRITING BRUSH LOADING FOR VS6
                                                                                                                // What is this I don't even -- DR
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~przerwap~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  \\
    SNIPE(                              Snipe.class,                    "s",        "snipe"),                   //  [   1    ]  \\
    DISC(                               Disc.class,                     "d",        "disc"),                    //  [   2    ]  \\
    DISC_FACE(                          DiscFace.class,                 "df",       "discface"),                //  [   3    ]  \\
    BALL(                               Ball.class,                     "b",        "ball"),                    //  [   6    ]  \\
    VOXEL(                              Voxel.class,                    "v",        "voxel"),                   //  [   8    ]  \\
    VOXEL_DISC(                         VoxelDisc.class,                "vd",       "voxeldisc"),               //  [   9    ]  \\
    VOXEL_DISC_FACE(                    VoxelDiscFace.class,            "vdf",      "voxeldiscface"),           //  [   11   ]  \\
    MONSTER(                            Monster.class,                  "m",        "monster"),                 //  [   19   ]  \\
    OCEAN(                              Ocean.class,                    "o",        "ocean"),                   //  [   20   ]  \\
    OCEAN_SELECTION(                    OceanSelection.class,           "ocs",      "oceanselection"),          //  [   21   ]  \\
    CLONE_STAMP(                        Clone.class,                    "cs",       "clonestamp"),              //  [   22   ]  \\
    ERODE(                              Erode.class,                    "e",        "erode"),                   //  [   23   ]  \\
    SOFT_SELECT_TEST(                   PullTest.class,                 "pull",     "pull"),                    //  [   24   ]  \\
    PAINTING(                           Painting.class,                 "paint",    "painting"),                //  [   25   ]  \\
    CANYON(                             Canyon.class,                   "ca",       "canyon"),                  //  [   26   ]  \\
    CANYON_SELECTION(                   CanyonSelection.class,          "cas",      "canyonselection"),         //  [   27   ]  \\
    TWO_D_ROTATION(                     Rot2D.class,                    "rot2",     "rotation2D"),              //  [   28   ]  \\
    WARP_IN_STYLE(                      WarpInStyle.class,              "w",        "warpinstyle"),             //  [   32   ]  \\
    FILL_DOWN(                          FillDown.class,                 "fd",       "filldown"),                //  [   33   ]  \\
    SET(                                Set.class,                      "set",      "set"),                     //  [   34   ]  \\ derp Heeeerp
    NOUNDOVOXEL(                        noUndoVoxel.class,              "nv",       "noundovoxel"),             //  [   35   ]  \\
    JOCKEY(                             Jockey.class,                   "jockey",   "jockey"),                  //  [   38   ]  \\
    NOUNDOSET(                          noUndoSet.class,                "nset",     "noundoset"),               //  [   39   ]  \\
    ENTITY_REMOVAL(                     EntityRemoval.class,            "er",       "entityremoval"),           //  [   40   ]  \\
    RING(                               Ring.class,                     "ri",       "ring"),                    //  [   41   ]  \\
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~giltwist~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  \\
    SPLATTER_DISC(                      SplatterDisc.class,             "sd",       "splatdisc"),               //  [   1    ]  \\
    SPLATTER_VOXEL_DISC(                SplatterVoxelDisc.class,        "svd",      "splatvoxeldisc"),          //  [   2    ]  \\
    SPLATTER_BALL(                      SplatterBall.class,             "sb",       "splatball"),               //  [   3    ]  \\
    SPLATTER_VOXEL(                     SplatterVoxel.class,            "sv",          "splatvoxel"),          //  [   4    ]  \\
    BLOB(                               Blob.class,                     "blob",     "splatblob"),               //  [   5    ]  \\
    SPIRAL_STAIRCASE(                   SpiralStaircase.class,          "sstair",   "spiralstaircase"),         //  [   6    ]  \\
    SPLATTER_OVERLAY(                   SplatterOverlay.class,          "sover",    "splatteroverlay"),         //  [   7    ]  \\
    BLEND_VOXEL_DISC(                   BlendVoxelDisc.class,           "bvd",      "blendvoxeldisc"),          //  [   8    ]  \\
    BLEND_VOXEL(                        BlendVoxel.class,               "bv",       "blendvoxel"),              //  [   9    ]  \\
    BLEND_DISC(                         BlendDisc.class,                "bd",       "blenddisc"),               //  [   10   ]  \\
    BLEND_BALL(                         BlendBall.class,                "bb",       "blendball"),               //  [   11   ]  \\
    LINE(                               Line.class,                     "l",        "line"),                    //  [   12   ]  \\
    SNOW_CONE(                          SnowCone.class,                 "snow",     "snowcone"),                //  [   13   ]  \\
    SHELL_BALL(                         ShellBall.class,                "shb",      "shellball"),               //  [   14   ]  \\
    SHELL_VOXEL(                        ShellVoxel.class,               "shv",      "shellvoxel"),              //  [   15   ]  \\
    RANDOM_ERODE(                       RandomErode.class,              "re",       "randomerode"),             //  [   16   ]  \\
    METEOR(                             Meteor.class,                   "met",      "meteor"),                  //  [   17   ]  \\
    LOAD_CHUNK(                         LoadChunk.class,                "lc",       "loadchunk"),               //  [   18   ]  \\
    TRIANGLE(                           Triangle.class,                 "tri",      "triangle"),                //  [   19   ]  \\
    ERASER(                             Eraser.class,                   "erase",    "eraser"),                  //  [   20   ]  \\
    COPYPASTA(                          CopyPasta.class,                "cp",    "copypasta"),                  //  [   22   ]  \\
    COMET(                              Comet.class,                "com",    "comet"),                  //  [   24   ]  \\
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Ghost8700~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  \\
    GENERATE_TREE(                      GenerateTree.class,             "gt",       "generatetree"),            //  [   1    ]  \\
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~DivineRage~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  \\
    GENERATE_CHUNK(                     GenerateChunk.class,            "gc",       "generatechunk"),           //  [   1    ]  \\      // No documentation. Fucks up client-sided. Still works though.
    TREE_GENERATE(                      TreeSnipe.class,                "t",        "treesnipe"),               //  [   2    ]  \\
    POINTLESS(                          Pointless.class,                "drlolol",     "pointlessbrush"),          //  [   4    ]  \\
    SCANNER(                            Scanner.class,                  "sc",       "scanner"),                 //  [   5    ]  \\
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Gavjenks~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  \\
    HEAT_RAY(                           HeatRay.class,                  "hr",       "heatray"),                 //  [   1    ]  \\
    OVERLAY(                            Overlay.class,                  "over",     "overlay"),                 //  [   4    ]  \\
    DOME(                               Dome.class,                     "dome",     "domebrush"),               //  [   6    ]  \\
    RULER(                              Ruler.class,                    "r",        "ruler"),                   //  [   7    ]  \\
    VOLT_METER(                         VoltMeter.class,                "volt",     "voltmeter"),               //  [   8    ]  \\
    LIGHTNING(                          Lightning.class,                "light",    "lightning"),               //  [   9    ]  \\
    DRAIN(                              Drain.class,                    "drain",    "drain"),                   //  [   10   ]  \\
    THREE_D_ROTATION(                   Rot3D.class,                    "rot3",     "rotation3D"),              //  [   11   ]  \\
    FORCE(                              ForceBrush.class,               "force",    "force"),                   //  [   12   ]  \\
    ANTI_FREEZE(                        AntiFreeze.class,               "af",       "antifreeze"),              //  [   13   ]  \\
    CHUNK_COORDS(                       ChunkCoords.class,              "chc",      "chunkcoords"),             //  [   15   ]  \\
    GAVIN_SECRET(                       GavinSecret.class,              "gavsec",   "gavinsecret"),             //  [   20   ]  \\
    TWO_D_ROTATION_EXP(                 Rot2Dvert.class,                "rot2v",    "rotation2Dvertical"),      //  [   21   ]  \\
    SAVANNAH(                           Savannah.class,                 "savannah", "savannah"),                //  [   22   ]  \\
    STENCIL(                            Stencil.class,                  "st",       "stencil"),                 //  [   23   ]  \\
    STENCILLIST(                        StencilList.class,              "sl",       "stencillist"),             //  [   24   ]  \\
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~psanker~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ELLIPSE(                            Ellipse.class,                  "el",       "ellipse"    ),             //  [   1    ]  \\
    SPLINE(                             Spline.class,                   "sp",       "spline"     ),             //  [   2    ]  \\
    CLEAN_SNOW(                         CleanSnow.class,                "cls",      "cleansnow"  ),             //  [   4    ]  \\  NB: Removes snow (78) if the block under it is also 78 (removes the floating snow blocks)
    EXTRUDE(                            Extrude.class,                  "ex",       "extrude"    ),             //  [   5    ]  \\  NB: Extrudes a surface. Use with p[Ex/In]cludeMat for best results.
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Deamon~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    SET_REDSTONE_FLIP(                  SetRedstoneFlip.class,          "setrf",    "setredstoneflip"),         //  [   1    ]  \\
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~jmck95~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  \\
    UNDERLAY(                           Underlay.class,                 "under",    "underlay"),                //  [   1    ]  \\ 
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Kavukamari~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    CYLINDER(                           Cylinder.class,                 "c",        "cylinder"),
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~geekygenius~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    FERTILIZE(                           Fertilize.class,                 "fert",        "fertilize");          //  [   3    ]  \\
            
    private static final Map<String, vBrushes> brushes;
    private Class<? extends Brush> brush;
    private String short_name;
    private String long_name;

    private vBrushes(Class<? extends Brush> b, String shortName, String longName) {
        brush = b;
        short_name = shortName;
        long_name = longName;
    }

    private Brush getBrush() {
        Brush b;
        try {
            try {
                b = brush.getConstructor().newInstance();
                return b;
            } catch (InstantiationException ex) {
                Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String getShort() {
        return short_name;
    }

    private String getLong() {
        return long_name;
    }

    public static HashMap<String, Brush> getSniperBrushes() {
        HashMap<String, Brush> temp = new HashMap<String, Brush>();

        for (Entry<String, vBrushes> set : brushes.entrySet()) {
            temp.put(set.getKey(), set.getValue().getBrush());
        }

        return temp;
    }

    public static HashMap<String, String> getBrushAlternates() {
        HashMap<String, String> temp = new HashMap<String, String>();

        for (vBrushes vb : brushes.values()) {
            temp.put(vb.getLong(), vb.getShort());
        }

        return temp;
    }

    static {
        brushes = new HashMap<String, vBrushes>();

        for (vBrushes vb : values()) {
            brushes.put(vb.getShort(), vb);
        }
    }
}
