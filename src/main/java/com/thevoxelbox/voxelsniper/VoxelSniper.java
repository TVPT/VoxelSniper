package com.thevoxelbox.voxelsniper;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelpacket.server.VoxelPacketServer;
import com.thevoxelbox.voxelsniper.brush.*;
import com.thevoxelbox.voxelsniper.common.VoxelSniperCommon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private final SniperPermissionHelper sniperPermissionHelper = new SniperPermissionHelper();
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

    /**
     * @return {@link SniperPermissionHelper}
     */
    public SniperPermissionHelper getSniperPermissionHelper()
    {
        return sniperPermissionHelper;
    }

    /**
     * @return int
     * @deprecated Use {@link com.thevoxelbox.voxelsniper.VoxelSniperConfiguration#getLiteSniperMaxBrushSize()}
     */
    @Deprecated
    public int getLiteSniperMaxBrushSize()
    {
        return voxelSniperConfiguration.getLiteSniperMaxBrushSize();
    }

    /**
     * @param liteSniperMaxBrushSize
     * @deprecated Use {@link VoxelSniperConfiguration#setLiteSniperMaxBrushSize(int)}
     */
    @Deprecated
    public void setLiteSniperMaxBrushSize(int liteSniperMaxBrushSize)
    {
        this.voxelSniperConfiguration.setLiteSniperMaxBrushSize(liteSniperMaxBrushSize);
    }

    /**
     * @return ArrayList<Integer>
     * @deprecated Use {@link com.thevoxelbox.voxelsniper.VoxelSniperConfiguration#getLiteSniperRestrictedItems()}
     */
    @Deprecated
    public List<Integer> getLiteRestricted()
    {
        return voxelSniperConfiguration.getLiteSniperRestrictedItems();
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
        getLogger().info("Registered " + Brushes.registeredLiteSniperBrushes() + " LiteSniper Brushes with " + Brushes.registeredLiteSniperBrushHandles() + " handles.");

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
        Brushes.registerSniperBrush(BallBrush.class, Brushes.BrushAvailability.ALL, "b", "ball");
        Brushes.registerSniperBrush(BiomeBrush.class, Brushes.BrushAvailability.ALL, "bio", "biome");
        Brushes.registerSniperBrush(BlendBallBrush.class, Brushes.BrushAvailability.ALL, "bb", "blendball");
        Brushes.registerSniperBrush(BlendDiscBrush.class, Brushes.BrushAvailability.ALL, "bd", "blenddisc");
        Brushes.registerSniperBrush(BlendVoxelBrush.class, Brushes.BrushAvailability.ALL, "bv", "blendvoxel");
        Brushes.registerSniperBrush(BlendVoxelDiscBrush.class, Brushes.BrushAvailability.ALL, "bvd", "blendvoxeldisc");
        Brushes.registerSniperBrush(BlobBrush.class, Brushes.BrushAvailability.ALL, "blob", "splatblob");
        Brushes.registerSniperBrush(BlockResetBrush.class, Brushes.BrushAvailability.ALL, "brb", "blockresetbrush");
        Brushes.registerSniperBrush(BlockResetSurfaceBrush.class, Brushes.BrushAvailability.ALL, "brbs", "blockresetbrushsurface");
        Brushes.registerSniperBrush(CanyonBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "ca", "canyon");
        Brushes.registerSniperBrush(CanyonSelectionBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "cas", "canyonselection");
        Brushes.registerSniperBrush(CheckerVoxelDiscBrush.class, Brushes.BrushAvailability.ALL, "cvd", "checkervoxeldisc");
        Brushes.registerSniperBrush(CleanSnowBrush.class, Brushes.BrushAvailability.ALL, "cls", "cleansnow");
        Brushes.registerSniperBrush(CloneStampBrush.class, Brushes.BrushAvailability.ALL, "cs", "clonestamp");
        Brushes.registerSniperBrush(CometBrush.class, Brushes.BrushAvailability.ALL, "com", "comet");
        Brushes.registerSniperBrush(CopyPastaBrush.class, Brushes.BrushAvailability.ALL, "cp", "copypasta");
        Brushes.registerSniperBrush(CylinderBrush.class, Brushes.BrushAvailability.ALL, "c", "cylinder");
        Brushes.registerSniperBrush(DiscBrush.class, Brushes.BrushAvailability.ALL, "d", "disc");
        Brushes.registerSniperBrush(DiscFaceBrush.class, Brushes.BrushAvailability.ALL, "df", "discface");
        Brushes.registerSniperBrush(DomeBrush.class, Brushes.BrushAvailability.ALL, "dome", "domebrush");
        Brushes.registerSniperBrush(DrainBrush.class, Brushes.BrushAvailability.ALL, "drain");
        Brushes.registerSniperBrush(EllipseBrush.class, Brushes.BrushAvailability.ALL, "el", "ellipse");
        Brushes.registerSniperBrush(EllipsoidBrush.class, Brushes.BrushAvailability.ALL, "elo", "ellipsoid");
        Brushes.registerSniperBrush(EntityBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "en", "entity");
        Brushes.registerSniperBrush(EntityRemovalBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "er", "entityremoval");
        Brushes.registerSniperBrush(EraserBrush.class, Brushes.BrushAvailability.ALL, "erase", "eraser");
        Brushes.registerSniperBrush(ErodeBrush.class, Brushes.BrushAvailability.ALL, "e", "erode");
        Brushes.registerSniperBrush(ExtrudeBrush.class, Brushes.BrushAvailability.ALL, "ex", "extrude");
        Brushes.registerSniperBrush(FillDownBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "fd", "filldown");
        Brushes.registerSniperBrush(FlatOceanBrush.class, Brushes.BrushAvailability.ALL, "fo", "flatocean");
        Brushes.registerSniperBrush(GenerateTreeBrush.class, Brushes.BrushAvailability.ALL, "gt", "generatetree");
        Brushes.registerSniperBrush(HeatRayBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "hr", "heatray");
        Brushes.registerSniperBrush(JaggedLineBrush.class, Brushes.BrushAvailability.ALL, "j", "jagged");
        Brushes.registerSniperBrush(JockeyBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "jockey");
        Brushes.registerSniperBrush(LightningBrush.class, Brushes.BrushAvailability.ALL, "light", "lightning");
        Brushes.registerSniperBrush(LineBrush.class, Brushes.BrushAvailability.ALL, "l", "line");
        Brushes.registerSniperBrush(MoveBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "mv", "move");
        Brushes.registerSniperBrush(OceanBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "o", "ocean");
        Brushes.registerSniperBrush(OverlayBrush.class, Brushes.BrushAvailability.ALL, "over", "overlay");
        Brushes.registerSniperBrush(PaintingBrush.class, Brushes.BrushAvailability.ALL, "paint", "painting");
        Brushes.registerSniperBrush(PullBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "pull");
        Brushes.registerSniperBrush(PunishBrush.class, Brushes.BrushAvailability.ALL, "p", "punish");
        Brushes.registerSniperBrush(RandomErodeBrush.class, Brushes.BrushAvailability.ALL, "re", "randomerode");
        Brushes.registerSniperBrush(RegenerateChunkBrush.class, Brushes.BrushAvailability.ALL, "gc", "generatechunk");
        Brushes.registerSniperBrush(RingBrush.class, Brushes.BrushAvailability.ALL, "ri", "ring");
        Brushes.registerSniperBrush(Rot2DBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "rot2", "rotation2d");
        Brushes.registerSniperBrush(Rot2DvertBrush.class, Brushes.BrushAvailability.ALL, "rot2v", "rotation2dvertical");
        Brushes.registerSniperBrush(Rot3DBrush.class, Brushes.BrushAvailability.ALL, "rot3", "rotation3d");
        Brushes.registerSniperBrush(RulerBrush.class, Brushes.BrushAvailability.ALL, "r", "ruler");
        Brushes.registerSniperBrush(ScannerBrush.class, Brushes.BrushAvailability.ALL, "sc", "scanner");
        Brushes.registerSniperBrush(SetBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "set");
        Brushes.registerSniperBrush(SetRedstoneFlipBrush.class, Brushes.BrushAvailability.ALL, "setrf", "setredstoneflip");
        Brushes.registerSniperBrush(ShellBallBrush.class, Brushes.BrushAvailability.ALL, "shb", "shellball");
        Brushes.registerSniperBrush(ShellSetBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "shs", "shellset");
        Brushes.registerSniperBrush(ShellVoxelBrush.class, Brushes.BrushAvailability.ALL, "shv", "shellvoxel");
        Brushes.registerSniperBrush(SignOverwriteBrush.class, Brushes.BrushAvailability.ALL, "sio", "signoverwriter");
        Brushes.registerSniperBrush(SnipeBrush.class, Brushes.BrushAvailability.ALL, "s", "snipe");
        Brushes.registerSniperBrush(SnowConeBrush.class, Brushes.BrushAvailability.ALL, "snow", "snowcone");
        Brushes.registerSniperBrush(SpiralStaircaseBrush.class, Brushes.BrushAvailability.ALL, "sstair", "spiralstaircase");
        Brushes.registerSniperBrush(SplatterBallBrush.class, Brushes.BrushAvailability.ALL, "sb", "splatball");
        Brushes.registerSniperBrush(SplatterDiscBrush.class, Brushes.BrushAvailability.ALL, "sd", "splatdisc");
        Brushes.registerSniperBrush(SplatterOverlayBrush.class, Brushes.BrushAvailability.ALL, "sover", "splatteroverlay");
        Brushes.registerSniperBrush(SplatterVoxelBrush.class, Brushes.BrushAvailability.ALL, "sv", "splattervoxel");
        Brushes.registerSniperBrush(SplatterDiscBrush.class, Brushes.BrushAvailability.ALL, "svd", "splatvoxeldisc");
        Brushes.registerSniperBrush(SplineBrush.class, Brushes.BrushAvailability.ALL, "sp", "spline");
        Brushes.registerSniperBrush(StencilBrush.class, Brushes.BrushAvailability.ALL, "st", "stencil");
        Brushes.registerSniperBrush(StencilListBrush.class, Brushes.BrushAvailability.ALL, "sl", "stencillist");
        Brushes.registerSniperBrush(ThreePointCircleBrush.class, Brushes.BrushAvailability.ALL, "tpc", "threepointcircle");
        Brushes.registerSniperBrush(TreeSnipeBrush.class, Brushes.BrushAvailability.ALL, "t", "tree", "treesnipe");
        Brushes.registerSniperBrush(TriangleBrush.class, Brushes.BrushAvailability.ALL, "tri", "triangle");
        Brushes.registerSniperBrush(UnderlayBrush.class, Brushes.BrushAvailability.ALL, "under", "underlay");
        Brushes.registerSniperBrush(VoltMeterBrush.class, Brushes.BrushAvailability.ALL, "volt", "voltmeter");
        Brushes.registerSniperBrush(VoxelBrush.class, Brushes.BrushAvailability.ALL, "v", "voxel");
        Brushes.registerSniperBrush(VoxelDiscBrush.class, Brushes.BrushAvailability.ALL, "vd", "voxeldisc");
        Brushes.registerSniperBrush(VoxelDiscFaceBrush.class, Brushes.BrushAvailability.ALL, "vdf", "voxeldiscface");
        Brushes.registerSniperBrush(WarpBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "w", "warp");
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
