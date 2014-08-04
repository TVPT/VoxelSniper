package com.thevoxelbox.voxelsniper;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelsniper.brush.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Bukkit extension point.
 */
public class VoxelSniper extends JavaPlugin
{
    private static final String PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML = "plugins/VoxelSniper/SniperConfig.xml";
    private static VoxelSniper instance;
    private SniperManager sniperManager = new SniperManager(this);
    private final VoxelSniperListener voxelSniperListener = new VoxelSniperListener(this);
    private VoxelSniperConfiguration voxelSniperConfiguration;

    /**
     * Returns {@link com.thevoxelbox.voxelsniper.Brushes} for current instance.
     *
     * @return Brush Manager for current instance.
     */
    public Brushes getBrushManager()
    {
        return brushManager;
    }

    private Brushes brushManager = new Brushes();

    /**
     * @return {@link VoxelSniper}
     */
    public static VoxelSniper getInstance()
    {
        return VoxelSniper.instance;
    }

    /**
     * Returns object for accessing global VoxelSniper options.
     *
     * @return {@link VoxelSniperConfiguration} object for accessing global VoxelSniper options.
     */
    public VoxelSniperConfiguration getVoxelSniperConfiguration()
    {
        return voxelSniperConfiguration;
    }

    /**
     * Returns {@link com.thevoxelbox.voxelsniper.SniperManager} for current instance.
     *
     * @return SniperManager
     */
    public SniperManager getSniperManager()
    {
        return sniperManager;
    }

    /**
     * Load and migrate legacy configuration.
     */
    public void loadSniperConfiguration()
    {
        File configurationFile = new File(VoxelSniper.PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML);

        if (!configurationFile.exists())
        {
            return;
        }

        JAXBContext context;
        Unmarshaller unmarshaller;
        try
        {
            context = JAXBContext.newInstance(LegacyConfigurationContainer.class);
            unmarshaller = context.createUnmarshaller();
        }
        catch (JAXBException exception)
        {
            getLogger().log(Level.SEVERE, "Couldn't create Unmarshaller, while attempting to load legacy configuration.", exception);
            return;
        }

        try
        {
            Object unmarshal = unmarshaller.unmarshal(configurationFile);
            Preconditions.checkState(unmarshal instanceof LegacyConfigurationContainer, "Unmarshalled object is not of expected type.");
            LegacyConfigurationContainer legacyConfiguration = (LegacyConfigurationContainer) unmarshal;
            voxelSniperConfiguration.setUndoCacheSize(legacyConfiguration.undoCacheSize);
            voxelSniperConfiguration.setLitesniperRestrictedItems(legacyConfiguration.litesniperRestrictedItems);
            voxelSniperConfiguration.setLiteSniperMaxBrushSize(legacyConfiguration.liteSniperMaxBrushSize);
        }
        catch (JAXBException exception)
        {
            getLogger().log(Level.SEVERE, "Couldn't unmarshall legacy configuration.", exception);
            return;
        }
        catch (IllegalStateException exception)
        {
            getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            return;
        }

        if (configurationFile.delete())
        {
            saveConfig();
            reloadConfig();
            getLogger().info("Migrated legacy configuration file.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
        if (sender instanceof Player)
        {
            String[] arguments = args;

            if (arguments == null)
            {
                arguments = new String[0];
            }

            return voxelSniperListener.onCommand((Player) sender, arguments, command.getName());
        }

        getLogger().info("Only Players can execute commands.");
        return true;
    }

    @Override
    public void onEnable()
    {
        VoxelSniper.instance = this;

    	CoreProtectUtils.init();
    	if (CoreProtectUtils.CoreProtectExists)
    		getLogger().info("CoreProtect found, enabling block logging.");

        registerBrushes();
        getLogger().info("Registered " + brushManager.registeredSniperBrushes() + " Sniper Brushes with " + brushManager.registeredSniperBrushHandles() + " handles.");

        MetricsManager.getInstance().start();


        saveDefaultConfig();
        voxelSniperConfiguration = new VoxelSniperConfiguration(getConfig());
        loadSniperConfiguration();

        Bukkit.getPluginManager().registerEvents(this.voxelSniperListener, this);
        getLogger().info("Registered Sniper Listener.");
    }

    /**
     * Registers all brushes.
     */
    public void registerBrushes()
    {
        brushManager.registerSniperBrush(BallBrush.class, "b", "ball");
        brushManager.registerSniperBrush(BiomeBrush.class, "bio", "biome");
        brushManager.registerSniperBrush(BlendBallBrush.class, "bb", "blendball");
        brushManager.registerSniperBrush(BlendDiscBrush.class, "bd", "blenddisc");
        brushManager.registerSniperBrush(BlendVoxelBrush.class, "bv", "blendvoxel");
        brushManager.registerSniperBrush(BlendVoxelDiscBrush.class, "bvd", "blendvoxeldisc");
        brushManager.registerSniperBrush(BlobBrush.class, "blob", "splatblob");
        brushManager.registerSniperBrush(BlockResetBrush.class, "brb", "blockresetbrush");
        brushManager.registerSniperBrush(BlockResetSurfaceBrush.class, "brbs", "blockresetbrushsurface");
        brushManager.registerSniperBrush(CanyonBrush.class, "ca", "canyon");
        brushManager.registerSniperBrush(CanyonSelectionBrush.class, "cas", "canyonselection");
        brushManager.registerSniperBrush(CheckerVoxelDiscBrush.class, "cvd", "checkervoxeldisc");
        brushManager.registerSniperBrush(CleanSnowBrush.class, "cls", "cleansnow");
        brushManager.registerSniperBrush(CloneStampBrush.class, "cs", "clonestamp");
        brushManager.registerSniperBrush(CometBrush.class, "com", "comet");
        brushManager.registerSniperBrush(CopyPastaBrush.class, "cp", "copypasta");
        brushManager.registerSniperBrush(CylinderBrush.class, "c", "cylinder");
        brushManager.registerSniperBrush(DiscBrush.class, "d", "disc");
        brushManager.registerSniperBrush(DiscFaceBrush.class, "df", "discface");
        brushManager.registerSniperBrush(DomeBrush.class, "dome", "domebrush");
        brushManager.registerSniperBrush(DrainBrush.class, "drain");
        brushManager.registerSniperBrush(EllipseBrush.class, "el", "ellipse");
        brushManager.registerSniperBrush(EllipsoidBrush.class, "elo", "ellipsoid");
        brushManager.registerSniperBrush(EntityBrush.class, "en", "entity");
        brushManager.registerSniperBrush(EntityRemovalBrush.class, "er", "entityremoval");
        brushManager.registerSniperBrush(EraserBrush.class, "erase", "eraser");
        brushManager.registerSniperBrush(ErodeBrush.class, "e", "erode");
        brushManager.registerSniperBrush(ExtrudeBrush.class, "ex", "extrude");
        brushManager.registerSniperBrush(FillDownBrush.class, "fd", "filldown");
        brushManager.registerSniperBrush(FlatOceanBrush.class, "fo", "flatocean");
        brushManager.registerSniperBrush(GenerateTreeBrush.class, "gt", "generatetree");
        brushManager.registerSniperBrush(HeatRayBrush.class, "hr", "heatray");
        brushManager.registerSniperBrush(JaggedLineBrush.class, "j", "jagged");
        brushManager.registerSniperBrush(JockeyBrush.class, "jockey");
        brushManager.registerSniperBrush(LightningBrush.class, "light", "lightning");
        brushManager.registerSniperBrush(LineBrush.class, "l", "line");
        brushManager.registerSniperBrush(MoveBrush.class, "mv", "move");
        brushManager.registerSniperBrush(OceanBrush.class, "o", "ocean");
        brushManager.registerSniperBrush(OverlayBrush.class, "over", "overlay");
        brushManager.registerSniperBrush(PaintingBrush.class, "paint", "painting");
        brushManager.registerSniperBrush(PullBrush.class, "pull");
        brushManager.registerSniperBrush(PunishBrush.class, "p", "punish");
        brushManager.registerSniperBrush(RandomErodeBrush.class, "re", "randomerode");
        brushManager.registerSniperBrush(RegenerateChunkBrush.class, "gc", "generatechunk");
        brushManager.registerSniperBrush(RingBrush.class, "ri", "ring");
        brushManager.registerSniperBrush(Rot2DBrush.class, "rot2", "rotation2d");
        brushManager.registerSniperBrush(Rot2DvertBrush.class, "rot2v", "rotation2dvertical");
        brushManager.registerSniperBrush(Rot3DBrush.class, "rot3", "rotation3d");
        brushManager.registerSniperBrush(RulerBrush.class, "r", "ruler");
        brushManager.registerSniperBrush(ScannerBrush.class, "sc", "scanner");
        brushManager.registerSniperBrush(SetBrush.class, "set");
        brushManager.registerSniperBrush(SetRedstoneFlipBrush.class, "setrf", "setredstoneflip");
        brushManager.registerSniperBrush(ShellBallBrush.class, "shb", "shellball");
        brushManager.registerSniperBrush(ShellSetBrush.class, "shs", "shellset");
        brushManager.registerSniperBrush(ShellVoxelBrush.class, "shv", "shellvoxel");
        brushManager.registerSniperBrush(SignOverwriteBrush.class, "sio", "signoverwriter");
        brushManager.registerSniperBrush(SnipeBrush.class, "s", "snipe");
        brushManager.registerSniperBrush(SnowConeBrush.class, "snow", "snowcone");
        brushManager.registerSniperBrush(SpiralStaircaseBrush.class, "sstair", "spiralstaircase");
        brushManager.registerSniperBrush(SplatterBallBrush.class, "sb", "splatball");
        brushManager.registerSniperBrush(SplatterDiscBrush.class, "sd", "splatdisc");
        brushManager.registerSniperBrush(SplatterOverlayBrush.class, "sover", "splatteroverlay");
        brushManager.registerSniperBrush(SplatterVoxelBrush.class, "sv", "splattervoxel");
        brushManager.registerSniperBrush(SplatterDiscBrush.class, "svd", "splatvoxeldisc");
        brushManager.registerSniperBrush(SplineBrush.class, "sp", "spline");
        brushManager.registerSniperBrush(StencilBrush.class, "st", "stencil");
        brushManager.registerSniperBrush(StencilListBrush.class, "sl", "stencillist");
        brushManager.registerSniperBrush(ThreePointCircleBrush.class, "tpc", "threepointcircle");
        brushManager.registerSniperBrush(TreeSnipeBrush.class, "t", "tree", "treesnipe");
        brushManager.registerSniperBrush(TriangleBrush.class, "tri", "triangle");
        brushManager.registerSniperBrush(UnderlayBrush.class, "under", "underlay");
        brushManager.registerSniperBrush(VoltMeterBrush.class, "volt", "voltmeter");
        brushManager.registerSniperBrush(VoxelBrush.class, "v", "voxel");
        brushManager.registerSniperBrush(VoxelDiscBrush.class, "vd", "voxeldisc");
        brushManager.registerSniperBrush(VoxelDiscFaceBrush.class, "vdf", "voxeldiscface");
        brushManager.registerSniperBrush(WarpBrush.class, "w", "warp");
    }

    @XmlRootElement(name = "VoxelSniper")
    private static class LegacyConfigurationContainer
    {
        private LegacyConfigurationContainer()
        {
        }

        @XmlElementWrapper(name = "LiteSniperBannedIDs", required = false)
        @XmlElement(name = "id", type = Integer.class)
        List<Integer> litesniperRestrictedItems = new ArrayList<Integer>();
        @XmlElement(name = "MaxLiteBrushSize", required = true, defaultValue = "5")
        int liteSniperMaxBrushSize = 5;
        @XmlElement(name = "SniperUndoCache", required = true, defaultValue = "20")
        int undoCacheSize = 20;
    }
}
