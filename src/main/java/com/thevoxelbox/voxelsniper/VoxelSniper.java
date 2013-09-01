package com.thevoxelbox.voxelsniper;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import com.thevoxelbox.voxelsniper.brush.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
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
import java.util.logging.Logger;

/**
 * @author Voxel
 */
public class VoxelSniper extends JavaPlugin
{

    public static final Logger LOG = Logger.getLogger("Minecraft");
    protected static final Object ITEM_LOCK = new Object();
    private static final String PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML = "plugins/VoxelSniper/SniperConfig.xml";
    private static final SniperPermissionHelper SNIPER_PERMISSION_HELPER = new SniperPermissionHelper();
    private static VoxelSniper instance;
    private final VoxelSniperListener voxelSniperListener = new VoxelSniperListener(this);
    private final ArrayList<Integer> liteRestricted = new ArrayList<Integer>();
    private int liteMaxBrush = 5;

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
    public static SniperPermissionHelper getSniperPermissionHelper()
    {
        return VoxelSniper.SNIPER_PERMISSION_HELPER;
    }

    /**
     * Validate if item id is valid.
     *
     * @param itemId
     * @return boolean
     */
    public static boolean isValidItem(final int itemId)
    {
        return Material.getMaterial(itemId) != null;
    }

    /**
     * @return int
     */
    public final int getLiteMaxBrush()
    {
        return this.liteMaxBrush;
    }

    /**
     * @param liteMaxBrush
     */
    public final void setLiteMaxBrush(final int liteMaxBrush)
    {
        this.liteMaxBrush = liteMaxBrush;
    }

    /**
     * @return ArrayList<Integer>
     */
    public final ArrayList<Integer> getLiteRestricted()
    {
        return this.liteRestricted;
    }

    /**
     * Load configuration.
     */
    public final void loadSniperConfiguration()
    {
        try
        {
            final File _configurationFile = new File(VoxelSniper.PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML);

            if (!_configurationFile.exists())
            {
                this.saveSniperConfig();
            }

            final DocumentBuilderFactory _docFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder _docBuilder = _docFactory.newDocumentBuilder();
            final Document _doc = _docBuilder.parse(_configurationFile);
            _doc.normalize();
            final Node _root = _doc.getFirstChild();
            final NodeList _rnodes = _root.getChildNodes();
            for (int _x = 0; _x < _rnodes.getLength(); _x++)
            {
                final Node _n = _rnodes.item(_x);

                if (!_n.hasChildNodes())
                {
                    continue;
                }

                if (_n.getNodeName().equals("LiteSniperBannedIDs"))
                {
                    this.liteRestricted.clear();
                    final NodeList _idn = _n.getChildNodes();
                    for (int _y = 0; _y < _idn.getLength(); _y++)
                    {
                        if (_idn.item(_y).getNodeName().equals("id"))
                        {
                            if (_idn.item(_y).hasChildNodes())
                            {
                                this.liteRestricted.add(Integer.parseInt(_idn.item(_y).getFirstChild().getNodeValue()));
                            }
                        }
                    }
                }
                else if (_n.getNodeName().equals("MaxLiteBrushSize"))
                {
                    this.liteMaxBrush = Integer.parseInt(_n.getFirstChild().getNodeValue());
                }
                else if (_n.getNodeName().equals("SniperUndoCache"))
                {
                    Sniper.setUndoCacheSize(Integer.parseInt(_n.getFirstChild().getNodeValue()));
                }
            }
        }
        catch (final SAXException _ex)
        {
            this.getLogger().log(Level.SEVERE, null, _ex);
        }
        catch (final IOException _ex)
        {
            this.getLogger().log(Level.SEVERE, null, _ex);
        }
        catch (final ParserConfigurationException _ex)
        {
            this.getLogger().log(Level.SEVERE, null, _ex);
        }
    }

    @Override
    public final boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args)
    {
        if (sender instanceof Player)
        {
            final Player _p = (Player) sender;
            final String _comm = command.getName();
            if (args == null)
            {
                if (!VoxelSniperListener.onCommand(_p, new String[0], _comm))
                {
                    if (_p.isOp())
                    {
                        _p.sendMessage(ChatColor.RED + "Your name is not listed on the snipers.txt or you haven't /reload 'ed the server yet.");
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
                if (!VoxelSniperListener.onCommand(_p, args, _comm))
                {
                    if (_p.isOp())
                    {
                        _p.sendMessage(ChatColor.RED + "Your name is not listed on the snipers.txt or you haven't /reload 'ed the server yet.");
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

        System.out.println("Not instanceof Player!");

        return false;
    }

    @Override
    public final void onEnable()
    {
        VoxelSniper.instance = this;

        registerBrushes();
        getLogger().info("Registered " + Brushes.registeredSniperBrushes() + " Sniper Brushes with " + Brushes.registeredSniperBrushHandles() + " handles.");
        getLogger().info("Registered " + Brushes.registeredLiteSniperBrushes() + " LiteSniper Brushes with " + Brushes.registeredLiteSniperBrushHandles() + " handles.");

        MetricsManager.getInstance().start();

        this.loadSniperConfiguration();

        final PluginManager _pm = Bukkit.getPluginManager();
        _pm.registerEvents(this.voxelSniperListener, this);
    }

    /**
     * Save configuration.
     */
    public final void saveSniperConfig()
    {
        try
        {
            VoxelSniper.LOG.info("[VoxelSniper] Saving Configuration.....");

            final File _f = new File(VoxelSniper.PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML);
            _f.getParentFile().mkdirs();

            final DocumentBuilderFactory _docFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder _docBuilder = _docFactory.newDocumentBuilder();
            final Document _doc = _docBuilder.newDocument();
            final Element _vsElement = _doc.createElement("VoxelSniper");

            final Element _liteUnusable = _doc.createElement("LiteSniperBannedIDs");
            if (!this.liteRestricted.isEmpty())
            {
                for (int _x = 0; _x < this.liteRestricted.size(); _x++)
                {
                    final int _id = this.liteRestricted.get(_x);
                    final Element _ide = _doc.createElement("id");
                    _ide.appendChild(_doc.createTextNode(_id + ""));
                    _liteUnusable.appendChild(_ide);
                }
            }
            _vsElement.appendChild(_liteUnusable);

            final Element _liteBrushSize = _doc.createElement("MaxLiteBrushSize");
            _liteBrushSize.appendChild(_doc.createTextNode(this.liteMaxBrush + ""));
            _vsElement.appendChild(_liteBrushSize);

            final Element _undoCache = _doc.createElement("SniperUndoCache");
            _undoCache.appendChild(_doc.createTextNode(Sniper.getUndoCacheSize() + ""));
            _vsElement.appendChild(_undoCache);
            _vsElement.normalize();

            final TransformerFactory _transformerFactory = TransformerFactory.newInstance();
            _transformerFactory.setAttribute("indent-number", 4);
            final Transformer _transformer = _transformerFactory.newTransformer();
            _transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            _transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            _transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
            final DOMSource _source = new DOMSource(_vsElement);
            final StreamResult _result = new StreamResult(_f);
            _transformer.transform(_source, _result);

            VoxelSniper.LOG.info("[VoxelSniper] Configuration Saved!!");
        }
        catch (final TransformerException _ex)
        {
            Logger.getLogger(VoxelSniperListener.class.getName()).log(Level.SEVERE, null, _ex);
        }
        catch (final ParserConfigurationException _ex)
        {
            Logger.getLogger(VoxelSniperListener.class.getName()).log(Level.SEVERE, null, _ex);
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
