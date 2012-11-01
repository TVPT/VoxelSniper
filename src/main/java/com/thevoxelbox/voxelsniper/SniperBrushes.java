package com.thevoxelbox.voxelsniper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.thevoxelbox.voxelsniper.brush.FillDownBrush;
import com.thevoxelbox.voxelsniper.brush.FlatOceanBrush;
import com.thevoxelbox.voxelsniper.brush.GenerateTreeBrush;
import com.thevoxelbox.voxelsniper.brush.HeatRayBrush;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.JaggedLineBrush;
import com.thevoxelbox.voxelsniper.brush.JockeyBrush;
import com.thevoxelbox.voxelsniper.brush.LightningBrush;
import com.thevoxelbox.voxelsniper.brush.LineBrush;
import com.thevoxelbox.voxelsniper.brush.MoveBrush;
import com.thevoxelbox.voxelsniper.brush.OceanBrush;
import com.thevoxelbox.voxelsniper.brush.OverlayBrush;
import com.thevoxelbox.voxelsniper.brush.PaintingBrush;
import com.thevoxelbox.voxelsniper.brush.PullBrush;
import com.thevoxelbox.voxelsniper.brush.PunishBrush;
import com.thevoxelbox.voxelsniper.brush.RandomErodeBrush;
import com.thevoxelbox.voxelsniper.brush.RegenerateChunkBrush;
import com.thevoxelbox.voxelsniper.brush.RingBrush;
import com.thevoxelbox.voxelsniper.brush.Rot2DBrush;
import com.thevoxelbox.voxelsniper.brush.Rot2DvertBrush;
import com.thevoxelbox.voxelsniper.brush.Rot3DBrush;
import com.thevoxelbox.voxelsniper.brush.RulerBrush;
import com.thevoxelbox.voxelsniper.brush.ScannerBrush;
import com.thevoxelbox.voxelsniper.brush.SetBrush;
import com.thevoxelbox.voxelsniper.brush.SetRedstoneFlipBrush;
import com.thevoxelbox.voxelsniper.brush.ShellBallBrush;
import com.thevoxelbox.voxelsniper.brush.ShellSetBrush;
import com.thevoxelbox.voxelsniper.brush.ShellVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.SignOverwriteBrush;
import com.thevoxelbox.voxelsniper.brush.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.SnowConeBrush;
import com.thevoxelbox.voxelsniper.brush.SpiralStaircaseBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterBallBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterDiscBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterOverlayBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.StencilBrush;
import com.thevoxelbox.voxelsniper.brush.StencilListBrush;
import com.thevoxelbox.voxelsniper.brush.ThreePointCircleBrush;
import com.thevoxelbox.voxelsniper.brush.TreeSnipeBrush;
import com.thevoxelbox.voxelsniper.brush.TriangleBrush;
import com.thevoxelbox.voxelsniper.brush.UnderlayBrush;
import com.thevoxelbox.voxelsniper.brush.VoltMeterBrush;
import com.thevoxelbox.voxelsniper.brush.VoxelBrush;
import com.thevoxelbox.voxelsniper.brush.VoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.VoxelDiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.WarpBrush;

/**
 * 
 * @author Voxel
 */
public enum SniperBrushes {
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~przerwap~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    SNIPE(SnipeBrush.class, "s", "snipe"), // [ 1 ] \\
    DISC(DiscBrush.class, "d", "disc"), // [ 2 ] \\
    DISC_FACE(DiscFaceBrush.class, "df", "discface"), // [ 3 ] \\
    BALL(BallBrush.class, "b", "ball"), // [ 4 ] \\
    VOXEL(VoxelBrush.class, "v", "voxel"), // [ 5 ] \\
    VOXEL_DISC(VoxelDiscBrush.class, "vd", "voxeldisc"), // [ 6 ] \\
    VOXEL_DISC_FACE(VoxelDiscFaceBrush.class, "vdf", "voxeldiscface"), // [ 7 ] \\
    ENTITY(EntityBrush.class, "en", "entity"), // [ 8 ] \\
    OCEAN(OceanBrush.class, "o", "ocean"), // [ 9 ] \\
    CLONE_STAMP(CloneStampBrush.class, "cs", "clonestamp"), // [ 11 ] \\
    ERODE(ErodeBrush.class, "e", "erode"), // [ 12 ] \\
    PULL(PullBrush.class, "pull", "pull"), // [ 13 ] \\
    PAINTING(PaintingBrush.class, "paint", "painting"), // [ 14 ] \\
    CANYON(CanyonBrush.class, "ca", "canyon"), // [ 15 ] \\
    CANYON_SELECTION(CanyonSelectionBrush.class, "cas", "canyonselection"), // [ 16 ] \\
    TWO_D_ROTATION(Rot2DBrush.class, "rot2", "rotation2D"), // [ 17 ] \\
    WARP(WarpBrush.class, "w", "warp"), // [ 18 ] \\
    FILL_DOWN(FillDownBrush.class, "fd", "filldown"), // [ 19 ] \\
    SET(SetBrush.class, "set", "set"), // [ 20 ] \\
    JOCKEY(JockeyBrush.class, "jockey", "jockey"), // [ 21 ] \\
    ENTITY_REMOVAL(EntityRemovalBrush.class, "er", "entityremoval"), // [ 22 ] \\
    RING(RingBrush.class, "ri", "ring"), // [ 23 ] \\
    SHELL_SET(ShellSetBrush.class, "shs", "shellset"), // [ 24 ] \\
    BIOME(BiomeBrush.class, "bio", "biome"), // [ 25 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~giltwist~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    SPLATTER_DISC(SplatterDiscBrush.class, "sd", "splatdisc"), // [ 1 ] \\
    SPLATTER_VOXEL_DISC(SplatterVoxelDiscBrush.class, "svd", "splatvoxeldisc"), // [ 2 ] \\
    SPLATTER_BALL(SplatterBallBrush.class, "sb", "splatball"), // [ 3 ] \\
    SPLATTER_VOXEL(SplatterVoxelBrush.class, "sv", "splatvoxel"), // [ 4 ] \\
    BLOB(BlobBrush.class, "blob", "splatblob"), // [ 5 ] \\
    SPIRAL_STAIRCASE(SpiralStaircaseBrush.class, "sstair", "spiralstaircase"), // [ 6 ] \\
    SPLATTER_OVERLAY(SplatterOverlayBrush.class, "sover", "splatteroverlay"), // [ 7 ] \\
    BLEND_VOXEL_DISC(BlendVoxelDiscBrush.class, "bvd", "blendvoxeldisc"), // [ 8 ] \\
    BLEND_VOXEL(BlendVoxelBrush.class, "bv", "blendvoxel"), // [ 9 ] \\
    BLEND_DISC(BlendDiscBrush.class, "bd", "blenddisc"), // [ 10 ] \\
    BLEND_BALL(BlendBallBrush.class, "bb", "blendball"), // [ 11 ] \\
    LINE(LineBrush.class, "l", "line"), // [ 12 ] \\
    SNOW_CONE(SnowConeBrush.class, "snow", "snowcone"), // [ 13 ] \\
    SHELL_BALL(ShellBallBrush.class, "shb", "shellball"), // [ 14 ] \\
    SHELL_VOXEL(ShellVoxelBrush.class, "shv", "shellvoxel"), // [ 15 ] \\
    RANDOM_ERODE(RandomErodeBrush.class, "re", "randomerode"), // [ 16 ] \\
    TRIANGLE(TriangleBrush.class, "tri", "triangle"), // [ 19 ] \\
    ERASER(EraserBrush.class, "erase", "eraser"), // [ 20 ] \\
    COPYPASTA(CopyPastaBrush.class, "cp", "copypasta"), // [ 22 ] \\
    COMET(CometBrush.class, "com", "comet"), // [ 23 ] \\
    JAGGED(JaggedLineBrush.class, "j", "jagged"), // [ 24 ] \\
    THREEPOINTCIRCLE(ThreePointCircleBrush.class, "tpc", "threepointcircle"), // [ 25 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Ghost8700~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    GENERATE_TREE(GenerateTreeBrush.class, "gt", "generatetree"), // [ 1 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~DivineRage~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    GENERATE_CHUNK(RegenerateChunkBrush.class, "gc", "generatechunk"), // [ 1 ] \\ // No documentation. Fucks up client-sided. Still works though.
    TREE_GENERATE(TreeSnipeBrush.class, "t", "treesnipe"), // [ 2 ] \\
    SCANNER(ScannerBrush.class, "sc", "scanner"), // [ 5 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Gavjenks~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    HEAT_RAY(HeatRayBrush.class, "hr", "heatray"), // [ 1 ] \\
    OVERLAY(OverlayBrush.class, "over", "overlay"), // [ 4 ] \\
    DOME(DomeBrush.class, "dome", "domebrush"), // [ 6 ] \\
    RULER(RulerBrush.class, "r", "ruler"), // [ 7 ] \\
    VOLT_METER(VoltMeterBrush.class, "volt", "voltmeter"), // [ 8 ] \\
    LIGHTNING(LightningBrush.class, "light", "lightning"), // [ 9 ] \\
    DRAIN(DrainBrush.class, "drain", "drain"), // [ 10 ] \\
    THREE_D_ROTATION(Rot3DBrush.class, "rot3", "rotation3D"), // [ 11 ] \\
    TWO_D_ROTATION_EXP(Rot2DvertBrush.class, "rot2v", "rotation2Dvertical"), // [ 21 ] \\
    STENCIL(StencilBrush.class, "st", "stencil"), // [ 23 ] \\
    STENCILLIST(StencilListBrush.class, "sl", "stencillist"), // [ 24 ] \\
    BLOCK_RESET_SURFACE(BlockResetSurfaceBrush.class, "brbs", "blockresetbrushsurface"), // [25] \\
    FLAT_OCEAN(FlatOceanBrush.class, "fo", "flatocean"), // [ 26 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~psanker~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    ELLIPSE(EllipseBrush.class, "el", "ellipse"), // [ 1 ] \\
    CLEAN_SNOW(CleanSnowBrush.class, "cls", "cleansnow"), // [ 4 ] \\
    EXTRUDE(ExtrudeBrush.class, "ex", "extrude"), // [ 5 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Deamon~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    SET_REDSTONE_FLIP(SetRedstoneFlipBrush.class, "setrf", "setredstoneflip"), // [ 1 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Jmck95~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    UNDERLAY(UnderlayBrush.class, "under", "underlay"), // [ 1 ] \\

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Kavukamari~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    CYLINDER(CylinderBrush.class, "c", "cylinder"),

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Monofraps~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    PUNISH(PunishBrush.class, "p", "punish"), // [ 1 ] \\
    SIGN_OVERWRITE(SignOverwriteBrush.class, "sio", "signoverwrite"),

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~MikeMatrix~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \\
    MOVE(MoveBrush.class, "mv", "move"), // [1] \\
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
