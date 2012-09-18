package com.thevoxelbox.voxelsniper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thevoxelbox.voxelsniper.brush.AntiFreezeBrush;
import com.thevoxelbox.voxelsniper.brush.BallBrush;
import com.thevoxelbox.voxelsniper.brush.BiomeBrush;
import com.thevoxelbox.voxelsniper.brush.BlendBallBrush;
import com.thevoxelbox.voxelsniper.brush.BlendDiscBrush;
import com.thevoxelbox.voxelsniper.brush.BlendVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.BlendVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.BlobBrush;
import com.thevoxelbox.voxelsniper.brush.BlockResetBrush;
import com.thevoxelbox.voxelsniper.brush.BlockResetSurfaceBrush;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.CanyonBrush;
import com.thevoxelbox.voxelsniper.brush.CanyonSelectionBrush;
import com.thevoxelbox.voxelsniper.brush.CheckerVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.CleanSnowBrush;
import com.thevoxelbox.voxelsniper.brush.CloneStampBrush;
import com.thevoxelbox.voxelsniper.brush.CometBrush;
import com.thevoxelbox.voxelsniper.brush.CopyPastaBrush;
import com.thevoxelbox.voxelsniper.brush.CylinderBrush;
import com.thevoxelbox.voxelsniper.brush.DiscBrush;
import com.thevoxelbox.voxelsniper.brush.DiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.DomeBrush;
import com.thevoxelbox.voxelsniper.brush.DrainBrush;
import com.thevoxelbox.voxelsniper.brush.EllipseBrush;
import com.thevoxelbox.voxelsniper.brush.EntityBrush;
import com.thevoxelbox.voxelsniper.brush.EntityRemovalBrush;
import com.thevoxelbox.voxelsniper.brush.EraserBrush;
import com.thevoxelbox.voxelsniper.brush.ErodeBrush;
import com.thevoxelbox.voxelsniper.brush.ExtrudeBrush;
import com.thevoxelbox.voxelsniper.brush.FillDown;
import com.thevoxelbox.voxelsniper.brush.FlatOcean;
import com.thevoxelbox.voxelsniper.brush.FreezeRay;
import com.thevoxelbox.voxelsniper.brush.GenerateChunk;
import com.thevoxelbox.voxelsniper.brush.GenerateTree;
import com.thevoxelbox.voxelsniper.brush.HeatRay;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.Jagged;
import com.thevoxelbox.voxelsniper.brush.Jockey;
import com.thevoxelbox.voxelsniper.brush.Line;
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
public enum SniperBrushes {
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~przerwap~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    SNIPE(Snipe.class, "s", "snipe"), // [ 1 ] \\
    DISC(DiscBrush.class, "d", "disc"), // [ 2 ] \\
    DISC_FACE(DiscFaceBrush.class, "df", "discface"), // [ 3 ] \\
    BALL(BallBrush.class, "b", "ball"), // [ 4 ] \\
    VOXEL(Voxel.class, "v", "voxel"), // [ 5 ] \\
    VOXEL_DISC(VoxelDisc.class, "vd", "voxeldisc"), // [ 6 ] \\
    VOXEL_DISC_FACE(VoxelDiscFace.class, "vdf", "voxeldiscface"), // [ 7 ] \\
    ENTITY(EntityBrush.class, "en", "entity"), // [ 8 ] \\
    OCEAN(Ocean.class, "o", "ocean"), // [ 9 ] \\
    OCEAN_SELECTION(OceanSelection.class, "ocs", "oceanselection"), // [ 10 ] \\
    CLONE_STAMP(CloneStampBrush.class, "cs", "clonestamp"), // [ 11 ] \\
    ERODE(ErodeBrush.class, "e", "erode"), // [ 12 ] \\
    SOFT_SELECT_TEST(PullTest.class, "pull", "pull"), // [ 13 ] \\
    PAINTING(Painting.class, "paint", "painting"), // [ 14 ] \\
    CANYON(CanyonBrush.class, "ca", "canyon"), // [ 15 ] \\
    CANYON_SELECTION(CanyonSelectionBrush.class, "cas", "canyonselection"), // [ 16 ] \\
    TWO_D_ROTATION(Rot2D.class, "rot2", "rotation2D"), // [ 17 ] \\
    WARP_IN_STYLE(WarpInStyle.class, "world", "warpinstyle"), // [ 18 ] \\
    FILL_DOWN(FillDown.class, "fd", "filldown"), // [ 19 ] \\
    SET(Set.class, "set", "set"), // [ 20 ] \\
    JOCKEY(Jockey.class, "jockey", "jockey"), // [ 21 ] \\
    ENTITY_REMOVAL(EntityRemovalBrush.class, "er", "entityremoval"), // [ 22 ] \\
    RING(Ring.class, "ri", "ring"), // [ 23 ] \\
    SHELL_SET(ShellSet.class, "shs", "shellset"), // [ 24 ] \\
    BIOME(BiomeBrush.class, "bio", "biome"), // [ 25 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~giltwist~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    SPLATTER_DISC(SplatterDisc.class, "sd", "splatdisc"), // [ 1 ] \\
    SPLATTER_VOXEL_DISC(SplatterVoxelDisc.class, "svd", "splatvoxeldisc"), // [ 2 ] \\
    SPLATTER_BALL(SplatterBall.class, "sb", "splatball"), // [ 3 ] \\
    SPLATTER_VOXEL(SplatterVoxel.class, "sv", "splatvoxel"), // [ 4 ] \\
    BLOB(BlobBrush.class, "blob", "splatblob"), // [ 5 ] \\
    SPIRAL_STAIRCASE(SpiralStaircase.class, "sstair", "spiralstaircase"), // [ 6 ] \\
    SPLATTER_OVERLAY(SplatterOverlay.class, "sover", "splatteroverlay"), // [ 7 ] \\
    BLEND_VOXEL_DISC(BlendVoxelDiscBrush.class, "bvd", "blendvoxeldisc"), // [ 8 ] \\
    BLEND_VOXEL(BlendVoxelBrush.class, "bv", "blendvoxel"), // [ 9 ] \\
    BLEND_DISC(BlendDiscBrush.class, "bd", "blenddisc"), // [ 10 ] \\
    BLEND_BALL(BlendBallBrush.class, "bb", "blendball"), // [ 11 ] \\
    LINE(Line.class, "l", "line"), // [ 12 ] \\
    SNOW_CONE(SnowCone.class, "snow", "snowcone"), // [ 13 ] \\
    SHELL_BALL(ShellBall.class, "shb", "shellball"), // [ 14 ] \\
    SHELL_VOXEL(ShellVoxel.class, "shv", "shellvoxel"), // [ 15 ] \\
    RANDOM_ERODE(RandomErode.class, "re", "randomerode"), // [ 16 ] \\
    METEOR(Meteor.class, "met", "meteor"), // [ 17 ] \\
    TRIANGLE(Triangle.class, "tri", "triangle"), // [ 19 ] \\
    ERASER(EraserBrush.class, "erase", "eraser"), // [ 20 ] \\
    COPYPASTA(CopyPastaBrush.class, "cp", "copypasta"), // [ 22 ] \\
    COMET(CometBrush.class, "com", "comet"), // [ 23 ] \\
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
    DOME(DomeBrush.class, "dome", "domebrush"), // [ 6 ] \\
    RULER(Ruler.class, "r", "ruler"), // [ 7 ] \\
    VOLT_METER(VoltMeter.class, "volt", "voltmeter"), // [ 8 ] \\
    DRAIN(DrainBrush.class, "drain", "drain"), // [ 10 ] \\
    THREE_D_ROTATION(Rot3D.class, "rot3", "rotation3D"), // [ 11 ] \\
    ANTI_FREEZE(AntiFreezeBrush.class, "af", "antifreeze"), // [ 13 ] \\
    TWO_D_ROTATION_EXP(Rot2Dvert.class, "rot2v", "rotation2Dvertical"), // [ 21 ] \\
    STENCIL(Stencil.class, "st", "stencil"), // [ 23 ] \\
    STENCILLIST(StencilList.class, "sl", "stencillist"), // [ 24 ] \\
    BLOCK_RESET_SURFACE(BlockResetSurfaceBrush.class, "brbs", "blockresetbrushsurface"), // [25] \\
    FLAT_OCEAN(FlatOcean.class, "fo", "flatocean"), // [ 26 ] \\
    FREEZE_RAY(FreezeRay.class, "fr", "freezeray"),
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~psanker~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    ELLIPSE(EllipseBrush.class, "el", "ellipse"), // [ 1 ] \\
    SPLINE(Spline.class, "sp", "spline"), // [ 2 ] \\
    CLEAN_SNOW(CleanSnowBrush.class, "cls", "cleansnow"), // [ 4 ] \\
    EXTRUDE(ExtrudeBrush.class, "ex", "extrude"), // [ 5 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Deamon~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    SET_REDSTONE_FLIP(SetRedstoneFlip.class, "setrf", "setredstoneflip"), // [ 1 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Jmck95~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    UNDERLAY(Underlay.class, "under", "underlay"), // [ 1 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Kavukamari~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    CYLINDER(CylinderBrush.class, "c", "cylinder"),

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Monofraps~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    PUNISH(Punish.class, "p", "punish"), // [ 1 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~MikeMatrix~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    MOVE(Move.class, "mv", "move"), // [1] \\
    BLOCK_RESET(BlockResetBrush.class, "brb", "blockresetbrush"), // [1] \\
    CHECKER_VOXEL_DISC(CheckerVoxelDiscBrush.class, "cvd", "checkervoxeldisc"); // [1] \\

    private static final Map<String, SniperBrushes> BRUSHES;

    /**
     * @return HashMap<String, String>
     */
    public static HashMap<String, String> getBrushAlternates() {
        final HashMap<String, String> _temp = new HashMap<String, String>();

        for (final SniperBrushes _vb : SniperBrushes.BRUSHES.values()) {
            _temp.put(_vb.getLong(), _vb.getShort());
        }

        return _temp;
    }

    /**
     * @param name
     * @return Brush
     */
    public static IBrush getBrushInstance(final String name) {
        if (SniperBrushes.BRUSHES.containsKey(name)) {
            return SniperBrushes.BRUSHES.get(name).getBrush();
        } else {
            for (final SniperBrushes _vb : SniperBrushes.BRUSHES.values()) {
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
        for (final SniperBrushes _vbs : SniperBrushes.BRUSHES.values()) {
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

        for (final Entry<String, SniperBrushes> _set : SniperBrushes.BRUSHES.entrySet()) {
            _temp.put(_set.getKey(), _set.getValue().getBrush());
        }

        return _temp;
    }

    /**
     * @param name
     * @return boolean
     */
    public static boolean hasBrush(final String name) {
        if (SniperBrushes.BRUSHES.containsKey(name)) {
            return true;
        } else {
            for (final SniperBrushes _vb : SniperBrushes.BRUSHES.values()) {
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
        BRUSHES = new HashMap<String, SniperBrushes>();

        for (final SniperBrushes _vb : SniperBrushes.values()) {
            SniperBrushes.BRUSHES.put(_vb.getShort(), _vb);
        }
    }

    private SniperBrushes(final Class<? extends Brush> brush, final String shortName, final String longName) {
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
                Logger.getLogger(SniperBrushes.class.getName()).log(Level.SEVERE, null, _ex);
            } catch (final IllegalAccessException _ex) {
                Logger.getLogger(SniperBrushes.class.getName()).log(Level.SEVERE, null, _ex);
            } catch (final IllegalArgumentException _ex) {
                Logger.getLogger(SniperBrushes.class.getName()).log(Level.SEVERE, null, _ex);
            } catch (final InvocationTargetException _ex) {
                Logger.getLogger(SniperBrushes.class.getName()).log(Level.SEVERE, null, _ex);
            }
        } catch (final NoSuchMethodException _ex) {
            Logger.getLogger(SniperBrushes.class.getName()).log(Level.SEVERE, null, _ex);
        } catch (final SecurityException _ex) {
            Logger.getLogger(SniperBrushes.class.getName()).log(Level.SEVERE, null, _ex);
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
