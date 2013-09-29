package com.thevoxelbox.voxelsniper;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import com.thevoxelbox.voxelsniper.brush.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private final ArrayList<Integer> liteRestricted = new ArrayList<Integer>();
    private int liteSniperMaxBrushSize = 5;

    /**
     * @return {@link VoxelSniper}
     */
    public static VoxelSniper getInstance()
    {
        return VoxelSniper.instance;
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
     */
    public int getLiteSniperMaxBrushSize()
    {
        return liteSniperMaxBrushSize;
    }

    /**
     * @param liteSniperMaxBrushSize
     */
    public void setLiteSniperMaxBrushSize(int liteSniperMaxBrushSize)
    {
        this.liteSniperMaxBrushSize = liteSniperMaxBrushSize;
    }

    /**
     * @return ArrayList<Integer>
     */
    public ArrayList<Integer> getLiteRestricted()
    {
        return liteRestricted;
    }

    /**
     * Load configuration.
     */
    public void loadSniperConfiguration()
    {
        try
        {
            File configurationFile = new File(VoxelSniper.PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML);

            if (!configurationFile.exists())
            {
                saveSniperConfig();
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(configurationFile);
            document.normalize();
            Node root = document.getFirstChild();
            NodeList rootChildNodes = root.getChildNodes();
            for (int x = 0; x < rootChildNodes.getLength(); x++)
            {
                Node n = rootChildNodes.item(x);

                if (!n.hasChildNodes())
                {
                    continue;
                }

                if (n.getNodeName().equals("LiteSniperBannedIDs"))
                {
                    liteRestricted.clear();
                    NodeList idn = n.getChildNodes();
                    for (int y = 0; y < idn.getLength(); y++)
                    {
                        if (idn.item(y).getNodeName().equals("id"))
                        {
                            if (idn.item(y).hasChildNodes())
                            {
                                liteRestricted.add(Integer.parseInt(idn.item(y).getFirstChild().getNodeValue()));
                            }
                        }
                    }
                }
                else if (n.getNodeName().equals("MaxLiteBrushSize"))
                {
                    liteSniperMaxBrushSize = Integer.parseInt(n.getFirstChild().getNodeValue());
                }
                else if (n.getNodeName().equals("SniperUndoCache"))
                {
                    Sniper.setUndoCacheSize(Integer.parseInt(n.getFirstChild().getNodeValue()));
                }
            }
        }
        catch (SAXException exception)
        {
            getLogger().log(Level.SEVERE, "Error during configuration load.", exception);
        }
        catch (IOException exception)
        {
            getLogger().log(Level.SEVERE, "Error during configuration load.", exception);
        }
        catch (ParserConfigurationException exception)
        {
            getLogger().log(Level.SEVERE, "Error during configuration load.", exception);
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args)
    {
        if (sender instanceof Player)
        {
            final Player player = (Player) sender;
            final String commandName = command.getName();
            if (args == null)
            {
                if (!voxelSniperListener.onCommand(player, new String[0], commandName))
                {
                    if (player.isOp())
                    {
                        player.sendMessage(ChatColor.RED + "Command failed.");
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return true;
                }
            }
            else
            {
                if (!voxelSniperListener.onCommand(player, args, commandName))
                {
                    if (player.isOp())
                    {
                        player.sendMessage(ChatColor.RED + "Command failed.");
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return true;
                }
            }
        }

        getLogger().info("Only Players can execute commands.");
        return false;
    }

    @Override
    public void onEnable()
    {
        VoxelSniper.instance = this;

        registerBrushes();
        getLogger().info("Registered " + Brushes.registeredSniperBrushes() + " Sniper Brushes with " + Brushes.registeredSniperBrushHandles() + " handles.");
        getLogger().info("Registered " + Brushes.registeredLiteSniperBrushes() + " LiteSniper Brushes with " + Brushes.registeredLiteSniperBrushHandles() + " handles.");

        MetricsManager.getInstance().start();

        loadSniperConfiguration();

        Bukkit.getPluginManager().registerEvents(this.voxelSniperListener, this);
    }

    /**
     * Save configuration.
     */
    public void saveSniperConfig()
    {
        try
        {
            getLogger().info("Saving Configuration...");

            File file = new File(VoxelSniper.PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML);
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            {
                getLogger().severe("Could not create parent directories for configuration file.");
                return;
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            Element voxelSniperElement = document.createElement("VoxelSniper");

            Element liteUnusable = document.createElement("LiteSniperBannedIDs");
            if (!liteRestricted.isEmpty())
            {
                for (Integer liteRestrictedElement : this.liteRestricted)
                {
                    int id = liteRestrictedElement;
                    Element idElement = document.createElement("id");
                    idElement.appendChild(document.createTextNode(id + ""));
                    liteUnusable.appendChild(idElement);
                }
            }
            voxelSniperElement.appendChild(liteUnusable);

            Element maxLiteBrushSize = document.createElement("MaxLiteBrushSize");
            maxLiteBrushSize.appendChild(document.createTextNode(this.liteSniperMaxBrushSize + ""));
            voxelSniperElement.appendChild(maxLiteBrushSize);

            Element sniperUndoCache = document.createElement("SniperUndoCache");
            sniperUndoCache.appendChild(document.createTextNode(Sniper.getUndoCacheSize() + ""));
            voxelSniperElement.appendChild(sniperUndoCache);
            voxelSniperElement.normalize();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
            DOMSource source = new DOMSource(voxelSniperElement);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            getLogger().info("Configuration Saved!");
        }
        catch (TransformerException exception)
        {
            getLogger().log(Level.SEVERE, "Error during configuration save.", exception);
        }
        catch (ParserConfigurationException exception)
        {
            getLogger().log(Level.SEVERE, "Error during configuration save.", exception);
        }
    }

    /**
     * Registers all brushes.
     */
    public void registerBrushes()
    {
        Brushes.registerSniperBrush(BallBrush.class, Brushes.BrushAvailability.ALL, "b", "ball");
        Brushes.registerSniperBrush(BiomeBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "bio", "biome");
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
        Brushes.registerSniperBrush(HeatRayBrush.class, Brushes.BrushAvailability.ALL, "hr", "heatray");
        Brushes.registerSniperBrush(JaggedLineBrush.class, Brushes.BrushAvailability.ALL, "j", "jagged");
        Brushes.registerSniperBrush(JockeyBrush.class, Brushes.BrushAvailability.SNIPER_ONLY, "joceky");
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
}
