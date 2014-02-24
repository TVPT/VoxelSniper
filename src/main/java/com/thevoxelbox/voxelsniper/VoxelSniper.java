package com.thevoxelbox.voxelsniper;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelpacket.server.VoxelPacketServer;
import com.thevoxelbox.voxelsniper.brush.*;
import com.thevoxelbox.voxelsniper.common.VoxelSniperCommon;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
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

        registerBrushes();
        getLogger().info("Registered " + Brushes.registeredSniperBrushes() + " Sniper Brushes with " + Brushes.registeredSniperBrushHandles() + " handles.");

        MetricsManager.getInstance().start();


        saveDefaultConfig();
        voxelSniperConfiguration = new VoxelSniperConfiguration(getConfig());
        loadSniperConfiguration();

        Bukkit.getPluginManager().registerEvents(this.voxelSniperListener, this);
        getLogger().info("Registered Sniper Listener.");

        Plugin voxelModPackPlugin = Bukkit.getPluginManager().getPlugin("VoxelModPackPlugin");
        if (voxelModPackPlugin != null && voxelModPackPlugin.isEnabled())
        {
            VoxelSniperGuiListener voxelSniperGuiListener = new VoxelSniperGuiListener(this);
            Bukkit.getPluginManager().registerEvents(voxelSniperGuiListener, this);
            VoxelPacketServer.getInstance().subscribe(voxelSniperGuiListener, VoxelSniperCommon.BRUSH_UPDATE_REQUEST_CHANNEL_SHORTCODE);
            getLogger().info("Registered VoxelSniperGUI Listener.");
        }
    }

    /**
     * Registers all brushes.
     */
    public void registerBrushes()
    {
        Brushes.registerSniperBrush(BallBrush.class, "b", "ball");
        Brushes.registerSniperBrush(BiomeBrush.class, "bio", "biome");
        Brushes.registerSniperBrush(BlendBallBrush.class, "bb", "blendball");
        Brushes.registerSniperBrush(BlendDiscBrush.class, "bd", "blenddisc");
        Brushes.registerSniperBrush(BlendVoxelBrush.class, "bv", "blendvoxel");
        Brushes.registerSniperBrush(BlendVoxelDiscBrush.class, "bvd", "blendvoxeldisc");
        Brushes.registerSniperBrush(BlobBrush.class, "blob", "splatblob");
        Brushes.registerSniperBrush(BlockResetBrush.class, "brb", "blockresetbrush");
        Brushes.registerSniperBrush(BlockResetSurfaceBrush.class, "brbs", "blockresetbrushsurface");
        Brushes.registerSniperBrush(CanyonBrush.class, "ca", "canyon");
        Brushes.registerSniperBrush(CanyonSelectionBrush.class, "cas", "canyonselection");
        Brushes.registerSniperBrush(CheckerVoxelDiscBrush.class, "cvd", "checkervoxeldisc");
        Brushes.registerSniperBrush(CleanSnowBrush.class, "cls", "cleansnow");
        Brushes.registerSniperBrush(CloneStampBrush.class, "cs", "clonestamp");
        Brushes.registerSniperBrush(CometBrush.class, "com", "comet");
        Brushes.registerSniperBrush(CopyPastaBrush.class, "cp", "copypasta");
        Brushes.registerSniperBrush(CylinderBrush.class, "c", "cylinder");
        Brushes.registerSniperBrush(DiscBrush.class, "d", "disc");
        Brushes.registerSniperBrush(DiscFaceBrush.class, "df", "discface");
        Brushes.registerSniperBrush(DomeBrush.class, "dome", "domebrush");
        Brushes.registerSniperBrush(DrainBrush.class, "drain");
        Brushes.registerSniperBrush(EllipseBrush.class, "el", "ellipse");
        Brushes.registerSniperBrush(EllipsoidBrush.class, "elo", "ellipsoid");
        Brushes.registerSniperBrush(EntityBrush.class, "en", "entity");
        Brushes.registerSniperBrush(EntityRemovalBrush.class, "er", "entityremoval");
        Brushes.registerSniperBrush(EraserBrush.class, "erase", "eraser");
        Brushes.registerSniperBrush(ErodeBrush.class, "e", "erode");
        Brushes.registerSniperBrush(ExtrudeBrush.class, "ex", "extrude");
        Brushes.registerSniperBrush(FillDownBrush.class, "fd", "filldown");
        Brushes.registerSniperBrush(FlatOceanBrush.class, "fo", "flatocean");
        Brushes.registerSniperBrush(GenerateTreeBrush.class, "gt", "generatetree");
        Brushes.registerSniperBrush(HeatRayBrush.class, "hr", "heatray");
        Brushes.registerSniperBrush(JaggedLineBrush.class, "j", "jagged");
        Brushes.registerSniperBrush(JockeyBrush.class, "jockey");
        Brushes.registerSniperBrush(LightningBrush.class, "light", "lightning");
        Brushes.registerSniperBrush(LineBrush.class, "l", "line");
        Brushes.registerSniperBrush(MoveBrush.class, "mv", "move");
        Brushes.registerSniperBrush(OceanBrush.class, "o", "ocean");
        Brushes.registerSniperBrush(OverlayBrush.class, "over", "overlay");
        Brushes.registerSniperBrush(PaintingBrush.class, "paint", "painting");
        Brushes.registerSniperBrush(PullBrush.class, "pull");
        Brushes.registerSniperBrush(PunishBrush.class, "p", "punish");
        Brushes.registerSniperBrush(RandomErodeBrush.class, "re", "randomerode");
        Brushes.registerSniperBrush(RegenerateChunkBrush.class, "gc", "generatechunk");
        Brushes.registerSniperBrush(RingBrush.class, "ri", "ring");
        Brushes.registerSniperBrush(Rot2DBrush.class, "rot2", "rotation2d");
        Brushes.registerSniperBrush(Rot2DvertBrush.class, "rot2v", "rotation2dvertical");
        Brushes.registerSniperBrush(Rot3DBrush.class, "rot3", "rotation3d");
        Brushes.registerSniperBrush(RulerBrush.class, "r", "ruler");
        Brushes.registerSniperBrush(ScannerBrush.class, "sc", "scanner");
        Brushes.registerSniperBrush(SetBrush.class, "set");
        Brushes.registerSniperBrush(SetRedstoneFlipBrush.class, "setrf", "setredstoneflip");
        Brushes.registerSniperBrush(ShellBallBrush.class, "shb", "shellball");
        Brushes.registerSniperBrush(ShellSetBrush.class, "shs", "shellset");
        Brushes.registerSniperBrush(ShellVoxelBrush.class, "shv", "shellvoxel");
        Brushes.registerSniperBrush(SignOverwriteBrush.class, "sio", "signoverwriter");
        Brushes.registerSniperBrush(SnipeBrush.class, "s", "snipe");
        Brushes.registerSniperBrush(SnowConeBrush.class, "snow", "snowcone");
        Brushes.registerSniperBrush(SpiralStaircaseBrush.class, "sstair", "spiralstaircase");
        Brushes.registerSniperBrush(SplatterBallBrush.class, "sb", "splatball");
        Brushes.registerSniperBrush(SplatterDiscBrush.class, "sd", "splatdisc");
        Brushes.registerSniperBrush(SplatterOverlayBrush.class, "sover", "splatteroverlay");
        Brushes.registerSniperBrush(SplatterVoxelBrush.class, "sv", "splattervoxel");
        Brushes.registerSniperBrush(SplatterDiscBrush.class, "svd", "splatvoxeldisc");
        Brushes.registerSniperBrush(SplineBrush.class, "sp", "spline");
        Brushes.registerSniperBrush(StencilBrush.class, "st", "stencil");
        Brushes.registerSniperBrush(StencilListBrush.class, "sl", "stencillist");
        Brushes.registerSniperBrush(ThreePointCircleBrush.class, "tpc", "threepointcircle");
        Brushes.registerSniperBrush(TreeSnipeBrush.class, "t", "tree", "treesnipe");
        Brushes.registerSniperBrush(TriangleBrush.class, "tri", "triangle");
        Brushes.registerSniperBrush(UnderlayBrush.class, "under", "underlay");
        Brushes.registerSniperBrush(VoltMeterBrush.class, "volt", "voltmeter");
        Brushes.registerSniperBrush(VoxelBrush.class, "v", "voxel");
        Brushes.registerSniperBrush(VoxelDiscBrush.class, "vd", "voxeldisc");
        Brushes.registerSniperBrush(VoxelDiscFaceBrush.class, "vdf", "voxeldiscface");
        Brushes.registerSniperBrush(WarpBrush.class, "w", "warp");
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
