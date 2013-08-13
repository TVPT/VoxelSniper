package com.thevoxelbox.voxelsniper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;

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

/**
 * @author Voxel
 */
public class VoxelSniper extends JavaPlugin
{

    private static final String PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML = "plugins/VoxelSniper/SniperConfig.xml";
    private static final SniperPermissionHelper SNIPER_PERMISSION_HELPER = new SniperPermissionHelper();

    private final VoxelSniperListener voxelSniperListener = new VoxelSniperListener(this);
    private final ArrayList<Integer> liteRestricted = new ArrayList<Integer>();
    private int liteMaxBrush = 5;
    public static final Logger LOG = Logger.getLogger("Minecraft");
    protected static final Object ITEM_LOCK = new Object();
    private static VoxelSniper instance;

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
     *
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
     * @param liteMaxBrush
     */
    public final void setLiteMaxBrush(final int liteMaxBrush)
    {
        this.liteMaxBrush = liteMaxBrush;
    }
}
