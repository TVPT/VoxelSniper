package com.thevoxelbox.voxelsniper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thevoxelbox.voxelsniper.brush.AntiFreeze;
import com.thevoxelbox.voxelsniper.brush.Ball;
import com.thevoxelbox.voxelsniper.brush.Biome;
import com.thevoxelbox.voxelsniper.brush.BlendBall;
import com.thevoxelbox.voxelsniper.brush.BlendDisc;
import com.thevoxelbox.voxelsniper.brush.BlendVoxel;
import com.thevoxelbox.voxelsniper.brush.BlendVoxelDisc;
import com.thevoxelbox.voxelsniper.brush.Blob;
import com.thevoxelbox.voxelsniper.brush.BlockResetBrush;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.Canyon;
import com.thevoxelbox.voxelsniper.brush.CanyonSelection;
import com.thevoxelbox.voxelsniper.brush.ChunkCoords;
import com.thevoxelbox.voxelsniper.brush.CleanSnow;
import com.thevoxelbox.voxelsniper.brush.Clone;
import com.thevoxelbox.voxelsniper.brush.Comet;
import com.thevoxelbox.voxelsniper.brush.CopyPasta;
import com.thevoxelbox.voxelsniper.brush.Cylinder;
import com.thevoxelbox.voxelsniper.brush.Disc;
import com.thevoxelbox.voxelsniper.brush.DiscFace;
import com.thevoxelbox.voxelsniper.brush.Dome;
import com.thevoxelbox.voxelsniper.brush.Drain;
import com.thevoxelbox.voxelsniper.brush.Ellipse;
import com.thevoxelbox.voxelsniper.brush.Entity;
import com.thevoxelbox.voxelsniper.brush.EntityRemoval;
import com.thevoxelbox.voxelsniper.brush.Eraser;
import com.thevoxelbox.voxelsniper.brush.Erode;
import com.thevoxelbox.voxelsniper.brush.Extrude;
import com.thevoxelbox.voxelsniper.brush.Fertilize;
import com.thevoxelbox.voxelsniper.brush.FillDown;
import com.thevoxelbox.voxelsniper.brush.FlatOcean;
import com.thevoxelbox.voxelsniper.brush.ForceBrush;
import com.thevoxelbox.voxelsniper.brush.GavinSecret;
import com.thevoxelbox.voxelsniper.brush.GenerateChunk;
import com.thevoxelbox.voxelsniper.brush.GenerateTree;
import com.thevoxelbox.voxelsniper.brush.HeatRay;
import com.thevoxelbox.voxelsniper.brush.Jagged;
import com.thevoxelbox.voxelsniper.brush.Jockey;
import com.thevoxelbox.voxelsniper.brush.Lightning;
import com.thevoxelbox.voxelsniper.brush.Line;
import com.thevoxelbox.voxelsniper.brush.LoadChunk;
import com.thevoxelbox.voxelsniper.brush.Meteor;
import com.thevoxelbox.voxelsniper.brush.Move;
import com.thevoxelbox.voxelsniper.brush.Ocean;
import com.thevoxelbox.voxelsniper.brush.OceanSelection;
import com.thevoxelbox.voxelsniper.brush.Overlay;
import com.thevoxelbox.voxelsniper.brush.Painting;
import com.thevoxelbox.voxelsniper.brush.Pointless;
import com.thevoxelbox.voxelsniper.brush.PullTest;
import com.thevoxelbox.voxelsniper.brush.Punish;
import com.thevoxelbox.voxelsniper.brush.RandomErode;
import com.thevoxelbox.voxelsniper.brush.Ring;
import com.thevoxelbox.voxelsniper.brush.Rot2D;
import com.thevoxelbox.voxelsniper.brush.Rot2Dvert;
import com.thevoxelbox.voxelsniper.brush.Rot3D;
import com.thevoxelbox.voxelsniper.brush.Ruler;
import com.thevoxelbox.voxelsniper.brush.Savannah;
import com.thevoxelbox.voxelsniper.brush.Scanner;
import com.thevoxelbox.voxelsniper.brush.Set;
import com.thevoxelbox.voxelsniper.brush.SetRedstoneFlip;
import com.thevoxelbox.voxelsniper.brush.ShellBall;
import com.thevoxelbox.voxelsniper.brush.ShellSet;
import com.thevoxelbox.voxelsniper.brush.ShellVoxel;
import com.thevoxelbox.voxelsniper.brush.Snipe;
import com.thevoxelbox.voxelsniper.brush.SnowCone;
import com.thevoxelbox.voxelsniper.brush.SpiralStaircase;
import com.thevoxelbox.voxelsniper.brush.SplatterBall;
import com.thevoxelbox.voxelsniper.brush.SplatterDisc;
import com.thevoxelbox.voxelsniper.brush.SplatterOverlay;
import com.thevoxelbox.voxelsniper.brush.SplatterVoxel;
import com.thevoxelbox.voxelsniper.brush.SplatterVoxelDisc;
import com.thevoxelbox.voxelsniper.brush.Spline;
import com.thevoxelbox.voxelsniper.brush.Stencil;
import com.thevoxelbox.voxelsniper.brush.StencilList;
import com.thevoxelbox.voxelsniper.brush.ThreePointCircle;
import com.thevoxelbox.voxelsniper.brush.TreeRemover;
import com.thevoxelbox.voxelsniper.brush.TreeSnipe;
import com.thevoxelbox.voxelsniper.brush.Triangle;
import com.thevoxelbox.voxelsniper.brush.Underlay;
import com.thevoxelbox.voxelsniper.brush.VoltMeter;
import com.thevoxelbox.voxelsniper.brush.Voxel;
import com.thevoxelbox.voxelsniper.brush.VoxelDisc;
import com.thevoxelbox.voxelsniper.brush.VoxelDiscFace;
import com.thevoxelbox.voxelsniper.brush.WarpInStyle;

/**
 * 
 * @author Voxel
 */
public enum vBrushes {
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~przerwap~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    SNIPE(Snipe.class, "s", "snipe"), // [ 1 ] \\
    DISC(Disc.class, "d", "disc"), // [ 2 ] \\
    DISC_FACE(DiscFace.class, "df", "discface"), // [ 3 ] \\
    BALL(Ball.class, "b", "ball"), // [ 4 ] \\
    VOXEL(Voxel.class, "v", "voxel"), // [ 5 ] \\
    VOXEL_DISC(VoxelDisc.class, "vd", "voxeldisc"), // [ 6 ] \\
    VOXEL_DISC_FACE(VoxelDiscFace.class, "vdf", "voxeldiscface"), // [ 7 ] \\
    ENTITY(Entity.class, "en", "entity"), // [ 8 ] \\
    OCEAN(Ocean.class, "o", "ocean"), // [ 9 ] \\
    OCEAN_SELECTION(OceanSelection.class, "ocs", "oceanselection"), // [ 10 ] \\
    CLONE_STAMP(Clone.class, "cs", "clonestamp"), // [ 11 ] \\
    ERODE(Erode.class, "e", "erode"), // [ 12 ] \\
    SOFT_SELECT_TEST(PullTest.class, "pull", "pull"), // [ 13 ] \\
    PAINTING(Painting.class, "paint", "painting"), // [ 14 ] \\
    CANYON(Canyon.class, "ca", "canyon"), // [ 15 ] \\
    CANYON_SELECTION(CanyonSelection.class, "cas", "canyonselection"), // [ 16 ] \\
    TWO_D_ROTATION(Rot2D.class, "rot2", "rotation2D"), // [ 17 ] \\
    WARP_IN_STYLE(WarpInStyle.class, "w", "warpinstyle"), // [ 18 ] \\
    FILL_DOWN(FillDown.class, "fd", "filldown"), // [ 19 ] \\
    SET(Set.class, "set", "set"), // [ 20 ] \\
    JOCKEY(Jockey.class, "jockey", "jockey"), // [ 21 ] \\
    ENTITY_REMOVAL(EntityRemoval.class, "er", "entityremoval"), // [ 22 ] \\
    RING(Ring.class, "ri", "ring"), // [ 23 ] \\
    SHELL_SET(ShellSet.class, "shs", "shellset"), // [ 24 ] \\
    BIOME(Biome.class, "bio", "biome"), // [ 25 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~giltwist~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    SPLATTER_DISC(SplatterDisc.class, "sd", "splatdisc"), // [ 1 ] \\
    SPLATTER_VOXEL_DISC(SplatterVoxelDisc.class, "svd", "splatvoxeldisc"), // [ 2 ] \\
    SPLATTER_BALL(SplatterBall.class, "sb", "splatball"), // [ 3 ] \\
    SPLATTER_VOXEL(SplatterVoxel.class, "sv", "splatvoxel"), // [ 4 ] \\
    BLOB(Blob.class, "blob", "splatblob"), // [ 5 ] \\
    SPIRAL_STAIRCASE(SpiralStaircase.class, "sstair", "spiralstaircase"), // [ 6 ] \\
    SPLATTER_OVERLAY(SplatterOverlay.class, "sover", "splatteroverlay"), // [ 7 ] \\
    BLEND_VOXEL_DISC(BlendVoxelDisc.class, "bvd", "blendvoxeldisc"), // [ 8 ] \\
    BLEND_VOXEL(BlendVoxel.class, "bv", "blendvoxel"), // [ 9 ] \\
    BLEND_DISC(BlendDisc.class, "bd", "blenddisc"), // [ 10 ] \\
    BLEND_BALL(BlendBall.class, "bb", "blendball"), // [ 11 ] \\
    LINE(Line.class, "l", "line"), // [ 12 ] \\
    SNOW_CONE(SnowCone.class, "snow", "snowcone"), // [ 13 ] \\
    SHELL_BALL(ShellBall.class, "shb", "shellball"), // [ 14 ] \\
    SHELL_VOXEL(ShellVoxel.class, "shv", "shellvoxel"), // [ 15 ] \\
    RANDOM_ERODE(RandomErode.class, "re", "randomerode"), // [ 16 ] \\
    METEOR(Meteor.class, "met", "meteor"), // [ 17 ] \\
    LOAD_CHUNK(LoadChunk.class, "lc", "loadchunk"), // [ 18 ] \\
    TRIANGLE(Triangle.class, "tri", "triangle"), // [ 19 ] \\
    ERASER(Eraser.class, "erase", "eraser"), // [ 20 ] \\
    COPYPASTA(CopyPasta.class, "cp", "copypasta"), // [ 22 ] \\
    COMET(Comet.class, "com", "comet"), // [ 23 ] \\
    JAGGED(Jagged.class, "j", "jagged"), // [ 24 ] \\
    THREEPOINTCIRCLE(ThreePointCircle.class, "tpc", "threepointcircle"), // [ 25 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Ghost8700~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    GENERATE_TREE(GenerateTree.class, "gt", "generatetree"), // [ 1 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~DivineRage~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    GENERATE_CHUNK(GenerateChunk.class, "gc", "generatechunk"), // [ 1 ] \\ // No documentation. Fucks up client-sided. Still works though.
    TREE_GENERATE(TreeSnipe.class, "t", "treesnipe"), // [ 2 ] \\
    POINTLESS(Pointless.class, "drlolol", "pointlessbrush"), // [ 4 ] \\
    SCANNER(Scanner.class, "sc", "scanner"), // [ 5 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Gavjenks~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    HEAT_RAY(HeatRay.class, "hr", "heatray"), // [ 1 ] \\
    OVERLAY(Overlay.class, "over", "overlay"), // [ 4 ] \\
    DOME(Dome.class, "dome", "domebrush"), // [ 6 ] \\
    RULER(Ruler.class, "r", "ruler"), // [ 7 ] \\
    VOLT_METER(VoltMeter.class, "volt", "voltmeter"), // [ 8 ] \\
    LIGHTNING(Lightning.class, "light", "lightning"), // [ 9 ] \\
    DRAIN(Drain.class, "drain", "drain"), // [ 10 ] \\
    THREE_D_ROTATION(Rot3D.class, "rot3", "rotation3D"), // [ 11 ] \\
    FORCE(ForceBrush.class, "force", "force"), // [ 12 ] \\
    ANTI_FREEZE(AntiFreeze.class, "af", "antifreeze"), // [ 13 ] \\
    CHUNK_COORDS(ChunkCoords.class, "chc", "chunkcoords"), // [ 15 ] \\
    GAVIN_SECRET(GavinSecret.class, "gavsec", "gavinsecret"), // [ 20 ] \\
    TWO_D_ROTATION_EXP(Rot2Dvert.class, "rot2v", "rotation2Dvertical"), // [ 21 ] \\
    SAVANNAH(Savannah.class, "savannah", "savannah"), // [ 22 ] \\
    STENCIL(Stencil.class, "st", "stencil"), // [ 23 ] \\
    STENCILLIST(StencilList.class, "sl", "stencillist"), // [ 24 ] \\
    FLAT_OCEAN(FlatOcean.class, "fo", "flatocean"), // [ 25 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~psanker~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    ELLIPSE(Ellipse.class, "el", "ellipse"), // [ 1 ] \\
    SPLINE(Spline.class, "sp", "spline"), // [ 2 ] \\
    CLEAN_SNOW(CleanSnow.class, "cls", "cleansnow"), // [ 4 ] \\
    EXTRUDE(Extrude.class, "ex", "extrude"), // [ 5 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Deamon~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    SET_REDSTONE_FLIP(SetRedstoneFlip.class, "setrf", "setredstoneflip"), // [ 1 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Jmck95~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    UNDERLAY(Underlay.class, "under", "underlay"), // [ 1 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Kavukamari~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    CYLINDER(Cylinder.class, "c", "cylinder"),

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Baseball435~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    TREE_REMOVER(TreeRemover.class, "tr", "treeremover"),

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~geekygenius~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    FERTILIZE(Fertilize.class, "fert", "fertilize"), // [ 3 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Monofraps~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    PUNISH(Punish.class, "p", "punish"), // [ 1 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~MikeMatrix~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    MOVE(Move.class, "mv", "move"), // [1] \\
    BLOCK_RESET(BlockResetBrush.class, "brb", "blockresetbrush"); // [1] \\

    private static final Map<String, vBrushes> BRUSHES;

    /**
     * @return HashMap<String, String>
     */
    public static HashMap<String, String> getBrushAlternates() {
        final HashMap<String, String> _temp = new HashMap<String, String>();

        for (final vBrushes _vb : vBrushes.BRUSHES.values()) {
            _temp.put(_vb.getLong(), _vb.getShort());
        }

        return _temp;
    }

    /**
     * @param name
     * @return Brush
     */
    public static Brush getBrushInstance(final String name) {
        if (vBrushes.BRUSHES.containsKey(name)) {
            return vBrushes.BRUSHES.get(name).getBrush();
        } else {
            for (final vBrushes _vb : vBrushes.BRUSHES.values()) {
                if (_vb.getLong().equalsIgnoreCase(name)) {
                    return _vb.getBrush();
                }
            }
        }
        return null;
    }

    /**
     * @param brush
     * @return String
     */
    public static String getName(final Brush brush) {
        for (final vBrushes _vbs : vBrushes.BRUSHES.values()) {
            if (brush.getClass().getName().equals(_vbs.brush.getName())) {
                return _vbs.longName;
            }
        }
        return null;
    }

    /**
     * @return HashMap<String, Brush>
     */
    public static HashMap<String, Brush> getSniperBrushes() {
        final HashMap<String, Brush> _temp = new HashMap<String, Brush>();

        for (final Entry<String, vBrushes> _set : vBrushes.BRUSHES.entrySet()) {
            _temp.put(_set.getKey(), _set.getValue().getBrush());
        }

        return _temp;
    }

    /**
     * @param name
     * @return boolean
     */
    public static boolean hasBrush(final String name) {
        if (vBrushes.BRUSHES.containsKey(name)) {
            return true;
        } else {
            for (final vBrushes _vb : vBrushes.BRUSHES.values()) {
                if (_vb.getLong().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Class<? extends Brush> brush;

    private String shortName;

    private String longName;

    static {
        BRUSHES = new HashMap<String, vBrushes>();

        for (final vBrushes _vb : vBrushes.values()) {
            vBrushes.BRUSHES.put(_vb.getShort(), _vb);
        }
    }

    private vBrushes(final Class<? extends Brush> brush, final String shortName, final String longName) {
        this.brush = brush;
        this.shortName = shortName;
        this.longName = longName;
    }

    private Brush getBrush() {
        Brush _brush;
        try {
            try {
                _brush = this.brush.getConstructor().newInstance();
                return _brush;
            } catch (final InstantiationException _ex) {
                Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, _ex);
            } catch (final IllegalAccessException _ex) {
                Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, _ex);
            } catch (final IllegalArgumentException _ex) {
                Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, _ex);
            } catch (final InvocationTargetException _ex) {
                Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, _ex);
            }
        } catch (final NoSuchMethodException _ex) {
            Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, _ex);
        } catch (final SecurityException _ex) {
            Logger.getLogger(vBrushes.class.getName()).log(Level.SEVERE, null, _ex);
        }
        return null;
    }

    private String getLong() {
        return this.longName;
    }

    private String getShort() {
        return this.shortName;
    }
}
