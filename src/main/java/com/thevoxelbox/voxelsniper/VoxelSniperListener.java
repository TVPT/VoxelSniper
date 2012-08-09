package com.thevoxelbox.voxelsniper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeMap;
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

import net.minecraft.server.Packet39AttachEntity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerE;
import com.thevoxelbox.voxelsniper.voxelfood.CatapultCalzone;
import com.thevoxelbox.voxelsniper.voxelfood.DietDrSmurfy;
import com.thevoxelbox.voxelsniper.voxelfood.DobaCrackaz;
import com.thevoxelbox.voxelsniper.voxelfood.NinewerksCoffee;
import com.thevoxelbox.voxelsniper.voxelfood.OinkiesPorkSandwich;
import com.thevoxelbox.voxelsniper.voxelfood.PoisonVial;

/**
 * @author Voxel
 * 
 */
public class VoxelSniperListener implements Listener {

    private static final String PLUGINS_SNIPERS_TXT = "plugins/VoxelSniper/snipers.txt";
    private static final String PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML = "plugins/VoxelSniper/SniperConfig.xml";
    private static final String PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_TXT = "plugins/VoxelSniper/SniperConfig.txt";
    private static final String PLUGINS_VOXEL_SNIPER_SNIPERS_TXT = "plugins/VoxelSniper/snipers.txt";
    private static final String PLUGINS_VOXEL_SNIPER_LITE_SNIPERS_TXT = "plugins/VoxelSniper/LiteSnipers.txt";
    private static TreeMap<String, vSniper> voxelSnipers = new TreeMap<String, vSniper>();
    private static HashSet<String> snipers = new HashSet<String>();
    private static HashSet<String> liteSnipers = new HashSet<String>();
    private static ArrayList<Integer> liteRestricted = new ArrayList<Integer>();
    private static HashSet<Player> voxelFood = new HashSet<Player>();
    private static int liteMaxBrush = 5;
    private static boolean smiteVoxelFoxOffenders = false;
    private static boolean voxelFoodEnabled = false;
    private static VoxelSniper plugin;

    /**
     * @return int
     */
    public static int getLiteMaxBrush() {
        return VoxelSniperListener.liteMaxBrush;
    }

    /**
     * @return ArrayList<Integer>
     */
    public static ArrayList<Integer> getLiteRestricted() {
        return VoxelSniperListener.liteRestricted;
    }

    /**
     * @return HashSet<String>
     */
    public static HashSet<String> getLiteSnipers() {
        return VoxelSniperListener.liteSnipers;
    }

    /**
     * @return VoxelSniper
     */
    public static VoxelSniper getPlugin() {
        return VoxelSniperListener.plugin;
    }

    /**
     * @return HashSet<String>
     */
    public static HashSet<String> getSnipers() {
        return VoxelSniperListener.snipers;
    }

    /**
     * @return HashSet<Player>
     */
    public static HashSet<Player> getVoxelFood() {
        return VoxelSniperListener.voxelFood;
    }

    /**
     * @return TreeMap<String, vSniper>
     */
    public static TreeMap<String, vSniper> getVoxelSnipers() {
        return VoxelSniperListener.voxelSnipers;
    }

    /**
     * @param st
     * @return boolean
     */
    public static boolean isAdmin(final String st) {
        return VoxelSniperListener.snipers.contains(st);
    }

    /**
     * @return boolean
     */
    public static boolean isSmiteVoxelFoxOffenders() {
        return VoxelSniperListener.smiteVoxelFoxOffenders;
    }

    /**
     * @return boolean
     */
    public static boolean isVoxelFoodEnabled() {
        return VoxelSniperListener.voxelFoodEnabled;
    }

    /**
     * @param player
     * @param split
     * @param command
     * @return boolean Success.
     */
    public static boolean onCommand(final Player player, final String[] split, final String command) {
        if (command.equalsIgnoreCase("vchunk")) {
            player.getWorld().refreshChunk(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
            return true;
        }
        if (command.equalsIgnoreCase("paint")) {
            if (split.length == 1) {
                try {
                    vPainting.paint(player, false, false, Integer.parseInt(split[0]));
                    return true;
                } catch (final Exception _e) {
                    player.sendMessage(ChatColor.RED + "Invalid input!");
                    return true;
                }
            } else {
                vPainting.paint(player, true, false, 0);
                return true;
            }
        }
        if (command.equalsIgnoreCase("addlitesniper") && (VoxelSniperListener.isAdmin(player.getName()) || player.isOp())) {
            if (VoxelSniperListener.voxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            Player _pl;
            String _plName;
            try {
                _pl = VoxelSniperListener.plugin.getServer().getPlayer(split[0]);
                _plName = _pl.getName();
            } catch (final Exception _e) {
                player.sendMessage(ChatColor.RED + "Invalid name. Player not found!");
                return true;
            }
            try {
                final vSniper _vs = VoxelSniperListener.voxelSnipers.get(_plName);
                _vs.reset();
                player.sendMessage(ChatColor.GREEN + "Sniper was already on the list. His settings were reset to default.");
                return true;
            } catch (final Exception _e) {
                final vSniper _vSni = new liteSniper();
                _vSni.setPlayer(_pl);
                _vSni.reset();
                _vSni.loadAllPresets();
                VoxelSniperListener.voxelSnipers.put(_plName, _vSni);
                VoxelSniperListener.liteSnipers.add(_pl.getName());
                VoxelSniperListener.writeLiteSnipers();
                player.sendMessage(ChatColor.RED + "LiteSniper added");
                player.sendMessage("" + VoxelSniperListener.voxelSnipers.get(_plName).getPlayer().getName());
                _pl.sendMessage(ChatColor.RED + "LiteSniper added");
                _pl.sendMessage("" + VoxelSniperListener.voxelSnipers.get(_plName).getPlayer().getName());
                VoxelSniper.LOG.info("[VoxelSniper] LiteSniper added! (" + _pl.getName() + ") by: (" + player.getName() + ")");
                return true;
            }
        }
        if (command.equalsIgnoreCase("addsniper") && (VoxelSniperListener.isAdmin(player.getName()) || player.isOp())) {
            if (VoxelSniperListener.voxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            Player _pl;
            String _plName;
            try {
                _pl = VoxelSniperListener.plugin.getServer().getPlayer(split[0]);
                _plName = _pl.getName();
            } catch (final Exception _e) {
                player.sendMessage(ChatColor.RED + "Invalid name. Player not found!");
                return true;
            }
            try {
                final vSniper _vs = VoxelSniperListener.voxelSnipers.get(_plName);
                _vs.reset();
                player.sendMessage(ChatColor.GREEN + "Sniper was already on the list. His settings were reset to default.");
                return true;
            } catch (final Exception _e) {
                final vSniper _vSni = new vSniper();
                _vSni.setPlayer(_pl);
                _vSni.reset();
                _vSni.loadAllPresets();
                VoxelSniperListener.voxelSnipers.put(_plName, _vSni);
                VoxelSniperListener.writeSnipers();
                player.sendMessage(ChatColor.RED + "Sniper added");
                player.sendMessage("" + VoxelSniperListener.voxelSnipers.get(_plName).getPlayer().getName());
                _pl.sendMessage(ChatColor.RED + "Sniper added");
                _pl.sendMessage("" + VoxelSniperListener.voxelSnipers.get(_plName).getPlayer().getName());
                VoxelSniper.LOG.info("[VoxelSniper] Sniper added! (" + _pl.getName() + ") by: (" + player.getName() + ")");
                return true;
            }
        }
        if (command.equalsIgnoreCase("addlitesnipertemp") && (VoxelSniperListener.isAdmin(player.getName()) || player.isOp())) {
            if (VoxelSniperListener.voxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            Player _pl;
            String _plName;
            try {
                _pl = VoxelSniperListener.plugin.getServer().getPlayer(split[0]);
                _plName = _pl.getName();
            } catch (final Exception _e) {
                player.sendMessage(ChatColor.RED + "Invalid name. Player not found!");
                return true;
            }
            try {
                final vSniper _vs = VoxelSniperListener.voxelSnipers.get(_plName);
                _vs.reset();
                player.sendMessage(ChatColor.GREEN + "Sniper was already on the list. His settings were reset to default.");
                return true;
            } catch (final Exception _e) {
                final vSniper _vSni = new liteSniper();
                _vSni.setPlayer(_pl);
                _vSni.reset();
                _vSni.loadAllPresets();
                VoxelSniperListener.voxelSnipers.put(_plName, _vSni);
                VoxelSniperListener.liteSnipers.add(_pl.getName());
                player.sendMessage(ChatColor.RED + "LiteSniper added");
                player.sendMessage("" + VoxelSniperListener.voxelSnipers.get(_plName).getPlayer().getName());
                _pl.sendMessage(ChatColor.RED + "LiteSniper added");
                _pl.sendMessage("" + VoxelSniperListener.voxelSnipers.get(_plName).getPlayer().getName());
                VoxelSniper.LOG.info("[VoxelSniper] LiteSniper added! (" + _pl.getName() + ") by: (" + player.getName() + ")");
                return true;
            }
        }
        if (command.equalsIgnoreCase("addsnipertemp") && (VoxelSniperListener.isAdmin(player.getName()) || player.isOp())) {
            if (VoxelSniperListener.voxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            Player _pl;
            String _plName;
            try {
                _pl = VoxelSniperListener.plugin.getServer().getPlayer(split[0]);
                _plName = _pl.getName();
            } catch (final Exception _e) {
                player.sendMessage(ChatColor.RED + "Invalid name. Player not found!");
                return true;
            }
            try {
                final vSniper _vs = VoxelSniperListener.voxelSnipers.get(_plName);
                _vs.reset();
                player.sendMessage(ChatColor.GREEN + "Sniper was already on the list. His settings were reset to default.");
                return true;
            } catch (final Exception _e) {
                final vSniper _vSni = new vSniper();
                _vSni.setPlayer(_pl);
                _vSni.reset();
                _vSni.loadAllPresets();
                VoxelSniperListener.voxelSnipers.put(_plName, _vSni);
                player.sendMessage(ChatColor.RED + "Sniper added");
                player.sendMessage("" + VoxelSniperListener.voxelSnipers.get(_plName).getPlayer().getName());
                _pl.sendMessage(ChatColor.RED + "Sniper added");
                _pl.sendMessage("" + VoxelSniperListener.voxelSnipers.get(_plName).getPlayer().getName());
                VoxelSniper.LOG.info("[VoxelSniper] Sniper added! (" + _pl.getName() + ") by: (" + player.getName() + ")");
                return true;
            }
        }
        if (command.equalsIgnoreCase("removesniper") && (VoxelSniperListener.isAdmin(player.getName()) || player.isOp())) {
            if (VoxelSniperListener.voxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            try {
                final vSniper _vs = VoxelSniperListener.voxelSnipers.remove(split[0]);
                final Boolean _success = VoxelSniperListener.removeSniper(split[0]);
                final Boolean _success2 = VoxelSniperListener.removeLiteSniper(split[0]);
                if (!_success && !_success2) {
                    player.sendMessage("Unsuccessful removal.");
                } else {
                    player.sendMessage("Sniper removed.");
                }
                _vs.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Sorry you were removed from the sniper list :(");
                return true;
            } catch (final Exception _e) {
                player.sendMessage(ChatColor.GRAY + "Unsuccessful removal.");
                return true;
            }
        }
        if (command.equalsIgnoreCase("goto") && VoxelSniperListener.isAdmin(player.getName())) {
            if (VoxelSniperListener.voxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            try {
                final int _x = Integer.parseInt(split[0]);
                final int _z = Integer.parseInt(split[1]);
                player.teleport(new Location(player.getWorld(), _x, 115, _z));
                player.sendMessage(ChatColor.GREEN + "Woosh!");
                return true;
            } catch (final Exception _e) {
                player.sendMessage("Wrong.");
                return true;
            }
        }
        if (VoxelSniperListener.voxelSnipers.containsKey(player.getName())) {
            if (command.equalsIgnoreCase("btool")) {
                if (split != null && split.length > 0) {
                    if (split[0].equalsIgnoreCase("add")) {
                        if (split.length == 2) {
                            if (split[1].equals("-arrow")) {
                                VoxelSniperListener.voxelSnipers.get(player.getName()).addBrushTool(true);
                            } else if (split[1].equals("-powder")) {
                                VoxelSniperListener.voxelSnipers.get(player.getName()).addBrushTool(false);
                            } else {
                                player.sendMessage(ChatColor.GREEN + "/btool add (-arrow|-powder) -- turns the item in your hand into a BrushTool");
                            }
                        } else {
                            VoxelSniperListener.voxelSnipers.get(player.getName()).addBrushTool();
                        }
                    } else if (split[0].equalsIgnoreCase("remove")) {
                        VoxelSniperListener.voxelSnipers.get(player.getName()).removeBrushTool();
                    } else {
                        player.sendMessage(ChatColor.GREEN + "/btool add (-arrow|-powder) -- turns the item in your hand into a BrushTool");
                        player.sendMessage(ChatColor.GRAY + "/btool remove -- turns the BrushTool in your hand into a regular item");
                    }
                } else {
                    player.sendMessage(ChatColor.GREEN + "/btool add (-arrow|-powder) -- turns the item in your hand into a BrushTool");
                    player.sendMessage(ChatColor.GRAY + "/btool remove -- turns the BrushTool in your hand into a regular item");
                }
                return true;
            }
            if (command.equalsIgnoreCase("uuu")) {
                try {
                    VoxelSniperListener.voxelSnipers.get(split[0]).doUndo();
                    return true;
                } catch (final Exception _e) {
                    player.sendMessage(ChatColor.GREEN + "Player not found");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("uu")) {
                try {
                    VoxelSniperListener.voxelSnipers.get(VoxelSniperListener.plugin.getServer().getPlayer(split[0]).getName()).doUndo();
                    return true;
                } catch (final Exception _e) {
                    player.sendMessage(ChatColor.GREEN + "Player not found");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("u")) {
                final vSniper _vs = VoxelSniperListener.voxelSnipers.get(player.getName());
                try {
                    final int _r = Integer.parseInt(split[0]);
                    _vs.doUndo(_r);
                } catch (final Exception _e) {
                    _vs.doUndo();
                }
                VoxelSniper.LOG.log(Level.INFO, "[VoxelSniper] Player \"" + player.getName() + "\" used /u");
                return true;
            }
            if (command.equalsIgnoreCase("d")) {
                try {
                    VoxelSniperListener.voxelSnipers.get(player.getName()).reset();
                    player.sendMessage(ChatColor.GRAY + "Values reset.");
                    return true;
                } catch (final Exception _e) {
                    player.sendMessage("Not valid.");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vs")) {
                try {
                    if (split.length >= 1) {
                        if (split[0].equalsIgnoreCase("brushes")) {
                            VoxelSniperListener.voxelSnipers.get(player.getName()).printBrushes();
                            return true;
                        } else if (split[0].equalsIgnoreCase("brusheslong")) {
                            VoxelSniperListener.voxelSnipers.get(player.getName()).printBrushesLong();
                            return true;
                        } else if (split[0].equalsIgnoreCase("printout")) {
                            VoxelSniperListener.voxelSnipers.get(player.getName()).togglePrintout();
                            return true;
                        } else if (split[0].equalsIgnoreCase("lightning")) {
                            if (VoxelSniperListener.voxelSnipers.get(player.getName()) instanceof liteSniper) {
                                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                                return true;
                            }
                            VoxelSniperListener.voxelSnipers.get(player.getName()).toggleLightning();
                            return true;
                        } else if (split[0].equalsIgnoreCase("weather")) {
                            if (VoxelSniperListener.voxelSnipers.get(player.getName()) instanceof liteSniper) {
                                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                                return true;
                            }
                            player.getWorld().setWeatherDuration(0);
                            player.getWorld().setStorm(false);
                            player.sendMessage(ChatColor.GREEN + "Begone weather!");
                            return true;
                        } else if (split[0].equalsIgnoreCase("clear")) { // So you don't accidentally undo stuff you worked on an hour ago. Also frees RAM, I
                                                                         // suspect -Giltwist
                            // Would I reset the stuff in vUndo.java or in vSniper.java or both?
                            // player.sendMessage(ChatColor.GREEN + "Undo cache cleared");
                            return true;
                        } else if (split[0].equalsIgnoreCase("range")) {
                            if (split.length == 2) {
                                final double _i = Double.parseDouble(split[1]);
                                if (VoxelSniperListener.voxelSnipers.get(player.getName()) instanceof liteSniper && (_i > 12 || _i < -12)) {
                                    player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use ranges over 12.");
                                    return true;
                                }
                                VoxelSniperListener.voxelSnipers.get(player.getName()).setRange(_i);
                                return true;
                            } else {
                                VoxelSniperListener.voxelSnipers.get(player.getName()).setRange(-1);
                                return true;
                            }
                        } else if (split[0].equalsIgnoreCase("sitall")) {
                            player.sendMessage("Sitting all the players...");
                            VoxelSniperListener.sitAll();
                            player.sendMessage("Done!");
                            return true;
                        } else if (split[0].equalsIgnoreCase("perf")) {
                            player.sendMessage(ChatColor.AQUA + "The aviable performers are:");
                            player.sendMessage(PerformerE.performer_list_short);
                            return true;
                        } else if (split[0].equalsIgnoreCase("perflong")) {
                            player.sendMessage(ChatColor.AQUA + "The aviable performers are:");
                            player.sendMessage(PerformerE.performer_list_long);
                            return true;
                        }
                    }
                    player.sendMessage(ChatColor.DARK_RED + "VoxelSniper current settings:");
                    VoxelSniperListener.voxelSnipers.get(player.getName()).info();
                    return true;
                } catch (final Exception _e) {
                    player.sendMessage(ChatColor.RED + "You are not allowed to use this command");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vc")) {
                try {
                    if (split.length == 0) {
                        VoxelSniperListener.voxelSnipers.get(player.getName()).setCentroid(0);
                        return true;
                    }
                    VoxelSniperListener.voxelSnipers.get(player.getName()).setCentroid(Integer.parseInt(split[0]));
                    return true;
                } catch (final Exception _e) {
                    player.sendMessage(ChatColor.RED + "Invalid input");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vh")) {
                try {
                    VoxelSniperListener.voxelSnipers.get(player.getName()).setHeigth(Integer.parseInt(split[0]));
                    return true;
                } catch (final Exception _e) {
                    player.sendMessage(ChatColor.RED + "Invalid input");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vi")) {
                if (split.length == 0) {
                    final Block _tb = new HitBlox(player, player.getWorld()).getTargetBlock();
                    if (_tb != null) {
                        VoxelSniperListener.voxelSnipers.get(player.getName()).setData(_tb.getData());
                    }
                    return true;
                }
                try {
                    VoxelSniperListener.voxelSnipers.get(player.getName()).setData((byte) Integer.parseInt(split[0]));
                    return true;
                } catch (final Exception _e) {
                    player.sendMessage(ChatColor.RED + "Invalid input, please enter a number.");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vir")) {
                if (split.length == 0) {
                    final Block _tb = new HitBlox(player, player.getWorld()).getTargetBlock();
                    if (_tb != null) {
                        VoxelSniperListener.voxelSnipers.get(player.getName()).setReplaceData(_tb.getData());
                    }
                    return true;
                }
                try {
                    VoxelSniperListener.voxelSnipers.get(player.getName()).setReplaceData((byte) Integer.parseInt(split[0]));
                    return true;
                } catch (final Exception _e) {
                    player.sendMessage(ChatColor.RED + "Invalid input, please enter a number.");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vr")) {
                if (split.length == 0) {
                    final Block _tb = new HitBlox(player, player.getWorld()).getTargetBlock();
                    if (_tb != null) {
                        VoxelSniperListener.voxelSnipers.get(player.getName()).setReplace(_tb.getTypeId());
                    }
                    return true;
                }

                if (VoxelSniper.getItem(split[0]) != -1) {
                    final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());
                    final int _i = VoxelSniper.getItem(split[0]);
                    if (Material.getMaterial(_i) != null && Material.getMaterial(_i).isBlock()) {
                        _ps.setReplace(_i);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID!");
                        return true;
                    }
                } else {
                    final Material _mat = Material.matchMaterial(split[0]);
                    if (_mat == null) {
                        player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID!");
                        return true;
                    }

                    final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());

                    if (_mat.isBlock()) {
                        _ps.setReplace(_mat.getId());
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID!");
                        return true;
                    }
                }
            }
            if (command.equalsIgnoreCase("vl")) {
                if (split.length == 0) {
                    final HitBlox _hb = new HitBlox(player, player.getWorld());
                    final Block _tb = _hb.getTargetBlock();
                    try {
                        VoxelSniperListener.voxelSnipers.get(player.getName()).addVoxelToList(_tb.getTypeId());

                        return true;
                    } catch (final Exception _e) {
                        return true;
                    }
                } else {
                    if (split[0].equalsIgnoreCase("clear")) {
                        VoxelSniperListener.voxelSnipers.get(player.getName()).clearVoxelList();
                        return true;
                    }
                }

                final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());
                boolean _rem = false;

                for (final String _str : split) {
                    String _tmpint;
                    Integer _xint;

                    try {
                        if (_str.startsWith("-")) {
                            _rem = true;
                            _tmpint = _str.replaceAll("-", "");
                        } else {
                            _tmpint = _str;
                        }

                        _xint = Integer.parseInt(_tmpint);

                        if (VoxelSniper.isValidItem(_xint) && Material.getMaterial(_xint).isBlock()) {
                            if (!_rem) {
                                _ps.addVoxelToList(_xint);
                                continue;
                            } else {
                                _ps.removeVoxelFromList(_xint);
                                continue;
                            }
                        }

                    } catch (final NumberFormatException _e) {
                        try {
                            String _tmpstr;
                            Integer _xstr;
                            _rem = false;

                            if (_str.startsWith("-")) {
                                _rem = true;
                                _tmpstr = _str.replaceAll("-", "");
                            } else {
                                _tmpstr = _str;
                            }

                            _xstr = VoxelSniper.getItem(_tmpstr);

                            if (!_rem) {
                                _ps.addVoxelToList(_xstr);
                            } else {
                                _ps.removeVoxelFromList(_xstr);
                            }
                        } catch (final Exception _ex) {
                        }
                    }
                }
                return true;
            }
            if (command.equalsIgnoreCase("v")) {
                if (split.length == 0) {
                    final Block _tb = new HitBlox(player, player.getWorld()).getTargetBlock();
                    if (_tb != null) {
                        VoxelSniperListener.voxelSnipers.get(player.getName()).setVoxel(_tb.getTypeId());
                    }
                    return true;
                }

                if (VoxelSniper.getItem(split[0]) != -1) {
                    final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());
                    final int _i = VoxelSniper.getItem(split[0]);
                    if (Material.getMaterial(_i) != null && Material.getMaterial(_i).isBlock()) {
                        _ps.setVoxel(_i);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID!");
                        return true;
                    }
                } else {
                    final Material _mat = Material.matchMaterial(split[0]);
                    if (_mat == null) {
                        player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID!");
                        return true;
                    }

                    final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());

                    if (_mat.isBlock()) {
                        _ps.setVoxel(_mat.getId());
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID!");
                        return true;
                    }
                }
            }
            if (command.equalsIgnoreCase("b")) {
                try {
                    final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());
                    try {
                        if (split == null || split.length == 0) {
                            _ps.previousBrush();
                            // player.sendMessage(ChatColor.RED + "Please input a brush size.");
                            return true;
                        } else {
                            _ps.setBrushSize(Integer.parseInt(split[0]));
                            return true;
                        }
                    } catch (final Exception _e) {
                        _ps.fillPrevious();
                        _ps.setBrush(split);
                        return true;
                    }
                } catch (final Exception _ex) {
                    VoxelSniper.LOG.log(Level.WARNING, "[VoxelSniper] Command error from " + player.getName());
                    _ex.printStackTrace();
                    return true;
                }
            }
            if (command.equalsIgnoreCase("p")) {
                try {
                    final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());
                    if (split == null || split.length == 0) {
                        _ps.setPerformer(new String[] { "", "m" });
                    } else {
                        _ps.setPerformer(split);
                    }
                    return true;
                } catch (final Exception _ex) {
                    VoxelSniper.LOG.log(Level.WARNING, "[VoxelSniper] Command error from " + player.getName());
                    _ex.printStackTrace();
                    return true;
                }
            }
            if (command.equalsIgnoreCase("bms")) {
                try {
                    final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());
                    _ps.savePreset(Integer.parseInt(split[0]));
                    return true;
                } catch (final Exception _e) {
                    final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());
                    _ps.savePreset(split[0]);
                    return true;
                }
            }
            if (command.equalsIgnoreCase("bml")) {
                try {
                    final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());
                    _ps.loadPreset(Integer.parseInt(split[0]));
                    return true;
                } catch (final Exception _e) {
                    final vSniper _ps = VoxelSniperListener.voxelSnipers.get(player.getName());
                    _ps.loadPreset(split[0]);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param toRemove
     * @return boolean Success.
     */
    public static boolean removeLiteSniper(final String toRemove) {
        try {
            VoxelSniperListener.liteSnipers.remove(toRemove);
            VoxelSniperListener.writeSnipers();
            return true;
        } catch (final Exception _e) {
            return false;
        }
    }

    /**
     * @param toRemove
     * @return boolean Success.
     */
    public static boolean removeSniper(final String toRemove) {
        try {
            VoxelSniperListener.snipers.remove(toRemove);
            VoxelSniperListener.writeSnipers();
            return true;
        } catch (final Exception _e) {
            return false;
        }
    }

    /**
     * @param liteMaxBrush
     */
    public static void setLiteMaxBrush(final int liteMaxBrush) {
        VoxelSniperListener.liteMaxBrush = liteMaxBrush;
    }

    /**
     * @param smiteVoxelFoxOffenders
     */
    public static void setSmiteVoxelFoxOffenders(final boolean smiteVoxelFoxOffenders) {
        VoxelSniperListener.smiteVoxelFoxOffenders = smiteVoxelFoxOffenders;
    }

    /**
     * @param voxelFoodEnabled
     */
    public static void setVoxelFoodEnabled(final boolean voxelFoodEnabled) {
        VoxelSniperListener.voxelFoodEnabled = voxelFoodEnabled;
    }

    /**
     * Write all litesnipers to file.
     */
    public static void writeLiteSnipers() {
        try {
            final PrintWriter _pw = new PrintWriter(new File(VoxelSniperListener.PLUGINS_VOXEL_SNIPER_LITE_SNIPERS_TXT));
            for (final String _st : VoxelSniperListener.liteSnipers) {
                _pw.write(_st + "\r\n");
            }
            _pw.close();
        } catch (final Exception _e) {
        }
    }

    /**
     * Write all snipers to file.
     */
    public static void writeSnipers() {
        try {
            final PrintWriter _pw = new PrintWriter(new File(VoxelSniperListener.PLUGINS_VOXEL_SNIPER_SNIPERS_TXT));
            for (final String _st : VoxelSniperListener.snipers) {
                _pw.write(_st + "\r\n");
            }
            _pw.close();
        } catch (final Exception _e) {
        }
    }

    private static void sitAll() {
        for (final Player _player : Bukkit.getServer().getOnlinePlayers()) {
            ((CraftPlayer) _player).getHandle().netServerHandler.sendPacket(new Packet39AttachEntity(((CraftPlayer) _player).getHandle(),
                    ((CraftPlayer) _player).getHandle()));
            for (final Player _p : _player.getServer().getOnlinePlayers()) {
                if (!_p.getName().equals(_player.getName())) {
                    ((CraftPlayer) _p).getHandle().netServerHandler.sendPacket(new Packet39AttachEntity(((CraftPlayer) _player).getHandle(),
                            ((CraftPlayer) _player).getHandle()));
                }
            }
        }
    }

    /**
     * @param instance
     */
    public VoxelSniperListener(final VoxelSniper instance) {
        VoxelSniperListener.plugin = instance;
    }

    /**
     * Initialize snipers.
     */
    public final void initSnipers() {
        this.readSnipers();
        this.readLiteSnipers();
        VoxelSniperListener.liteRestricted.add(10);
        VoxelSniperListener.liteRestricted.add(11);
        this.loadConfig();
        VoxelSniperListener.voxelSnipers.clear();
        for (final Player _p : VoxelSniperListener.plugin.getServer().getOnlinePlayers()) {
            if (VoxelSniperListener.snipers.contains(_p.getName())) {
                final String _playerName = _p.getName();
                VoxelSniperListener.voxelSnipers.put(_playerName, new vSniper());
                VoxelSniperListener.voxelSnipers.get(_playerName).setPlayer(_p);
                VoxelSniperListener.voxelSnipers.get(_playerName).reset();
                VoxelSniperListener.voxelSnipers.get(_playerName).loadAllPresets();
            }
            if (VoxelSniperListener.liteSnipers.contains(_p.getName())) {
                final String _playerName = _p.getName();
                VoxelSniperListener.voxelSnipers.put(_playerName, new liteSniper());
                VoxelSniperListener.voxelSnipers.get(_playerName).setPlayer(_p);
                VoxelSniperListener.voxelSnipers.get(_playerName).reset();
                VoxelSniperListener.voxelSnipers.get(_playerName).loadAllPresets();
            }
        }
    }

    /**
     * Load configuration.
     */
    public final void loadConfig() {
        try {
            final File _oldConfig = new File(VoxelSniperListener.PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_TXT);
            if (_oldConfig.exists()) {
                this.loadOldConfig();
                _oldConfig.delete();
                if (VoxelSniperListener.liteRestricted.isEmpty()) {
                    VoxelSniperListener.liteRestricted.add(10);
                    VoxelSniperListener.liteRestricted.add(11);
                }
                this.saveConfig();
                VoxelSniper.LOG.info("[VoxelSniper] Configuration has been converted to new format!");
                return;
            }

            final File _f = new File(VoxelSniperListener.PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML);

            if (!_f.exists()) {
                this.saveConfig();
            }

            final DocumentBuilderFactory _docFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder _docBuilder = _docFactory.newDocumentBuilder();
            final Document _doc = _docBuilder.parse(_f);
            _doc.normalize();
            final Node _root = _doc.getFirstChild();
            final NodeList _rnodes = _root.getChildNodes();
            for (int _x = 0; _x < _rnodes.getLength(); _x++) {
                final Node _n = _rnodes.item(_x);

                if (!_n.hasChildNodes()) {
                    continue;
                }

                if (_n.getNodeName().equals("LiteSniperBannedIDs")) {
                    VoxelSniperListener.liteRestricted.clear();
                    final NodeList _idn = _n.getChildNodes();
                    for (int _y = 0; _y < _idn.getLength(); _y++) {
                        if (_idn.item(_y).getNodeName().equals("id")) {
                            if (_idn.item(_y).hasChildNodes()) {
                                VoxelSniperListener.liteRestricted.add(Integer.parseInt(_idn.item(_y).getFirstChild().getNodeValue()));
                            }
                        }
                    }
                } else if (_n.getNodeName().equals("MaxLiteBrushSize")) {
                    VoxelSniperListener.liteMaxBrush = Integer.parseInt(_n.getFirstChild().getNodeValue());
                } else if (_n.getNodeName().equals("SmiteVoxelFox")) {
                    VoxelSniperListener.smiteVoxelFoxOffenders = Boolean.parseBoolean(_n.getFirstChild().getNodeValue());
                } else if (_n.getNodeName().equals("VoxelFood")) {
                    VoxelSniperListener.voxelFoodEnabled = Boolean.parseBoolean(_n.getFirstChild().getNodeValue());
                } else if (_n.getNodeName().equals("SniperUndoCache")) {
                    vSniper.setUndoChacheSize(Integer.parseInt(_n.getFirstChild().getNodeValue()));
                }
            }
        } catch (final SAXException _ex) {
            Logger.getLogger(VoxelSniperListener.class.getName()).log(Level.SEVERE, null, _ex);
        } catch (final IOException _ex) {
            Logger.getLogger(VoxelSniperListener.class.getName()).log(Level.SEVERE, null, _ex);
        } catch (final ParserConfigurationException _ex) {
            Logger.getLogger(VoxelSniperListener.class.getName()).log(Level.SEVERE, null, _ex);
        }
    }

    /**
     * @param event
     */
    @EventHandler
    public final void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.isBlockInHand()) {
            return;
        }
        final Player _player = event.getPlayer();
        if (VoxelSniperListener.voxelFoodEnabled) {
            switch (_player.getItemInHand().getType()) {
            case INK_SACK:
                switch (_player.getItemInHand().getData().getData()) {
                case 3:
                    if (!VoxelSniperListener.voxelFood.contains(_player)) {
                        event.setCancelled(new NinewerksCoffee().perform(event.getAction(), _player, _player.getItemInHand(), event.getClickedBlock()));
                        VoxelSniperListener.voxelFood.add(_player);
                        this.runFoodTimer(_player);
                    } else {
                        _player.sendMessage(ChatColor.RED + "You can't do that yet!");
                    }
                    break;
                case 4:
                    if (!VoxelSniperListener.voxelFood.contains(_player)) {
                        event.setCancelled(new DietDrSmurfy().perform(event.getAction(), _player, _player.getItemInHand(), event.getClickedBlock()));
                        VoxelSniperListener.voxelFood.add(_player);
                        this.runFoodTimer(_player);
                    } else {
                        _player.sendMessage(ChatColor.RED + "You can't do that yet!");
                    }
                    break;
                case 8:
                    if (!VoxelSniperListener.voxelFood.contains(_player)) {
                        event.setCancelled(new DobaCrackaz().perform(event.getAction(), _player, _player.getItemInHand(), event.getClickedBlock()));
                        VoxelSniperListener.voxelFood.add(_player);
                        this.runFoodTimer(_player);
                    } else {
                        _player.sendMessage(ChatColor.RED + "You can't do that yet!");
                    }
                    break;
                case 10:
                    if (!VoxelSniperListener.voxelFood.contains(_player)) {
                        event.setCancelled(new CatapultCalzone().perform(event.getAction(), _player, _player.getItemInHand(), event.getClickedBlock()));
                        VoxelSniperListener.voxelFood.add(_player);
                        this.runFoodTimer(_player);
                    } else {
                        _player.sendMessage(ChatColor.RED + "You can't do that yet!");
                    }
                    break;
                case 12:
                    if (!VoxelSniperListener.voxelFood.contains(_player)) {
                        event.setCancelled(new OinkiesPorkSandwich().perform(event.getAction(), _player, _player.getItemInHand(), event.getClickedBlock()));
                        VoxelSniperListener.voxelFood.add(_player);
                        this.runFoodTimer(_player);
                    } else {
                        _player.sendMessage(ChatColor.RED + "You can't do that yet!");
                    }
                    break;
                case 13:
                    if (!VoxelSniperListener.voxelFood.contains(_player)) {
                        event.setCancelled(new PoisonVial().perform(event.getAction(), _player, _player.getItemInHand(), event.getClickedBlock()));
                        VoxelSniperListener.voxelFood.add(_player);
                        this.runFoodTimer(_player);
                    } else {
                        _player.sendMessage(ChatColor.RED + "You can't do that yet!");
                    }
                    break;
                default:
                    break;
                }
                break;
            default:
                break;
            }
        }
        try {
            final vSniper _vs = VoxelSniperListener.voxelSnipers.get(_player.getName());
            if (_vs == null) {
                return;
            } else if (_vs.snipe(_player, event.getAction(), event.getMaterial(), event.getClickedBlock(), event.getBlockFace())) {
                event.setCancelled(true);
            }
        } catch (final Exception _ex) {
            return;
        }
    }

    /**
     * @param event
     */
    @EventHandler
    public final void onPlayerJoin(final PlayerJoinEvent event) {
        final Player _p = event.getPlayer();
        final String _pName = _p.getName();
        if (VoxelSniperListener.isAdmin(_pName)) {
            try {
                final vSniper _vs = VoxelSniperListener.voxelSnipers.get(_pName);
                _vs.setPlayer(_p);
                _vs.info();
                return;
            } catch (final Exception _e) {
                final vSniper _vs = new vSniper();
                _vs.setPlayer(_p);
                _vs.reset();
                _vs.loadAllPresets();
                VoxelSniperListener.voxelSnipers.put(_pName, _vs);
                _p.sendMessage(ChatColor.RED + "Sniper added");
                _p.sendMessage("" + ChatColor.RED + VoxelSniperListener.voxelSnipers.get(_pName).getPlayer().getName());
                _vs.info();
                return;
            }
        }
        if (VoxelSniperListener.liteSnipers.contains(_p.getName())) {
            try {
                final vSniper _vs = VoxelSniperListener.voxelSnipers.get(_pName);
                if (_vs instanceof liteSniper) {
                    _vs.setPlayer(_p);
                    _vs.info();
                    return;
                } else {
                    final vSniper _vSni = new liteSniper();
                    _vSni.setPlayer(_p);
                    _vSni.reset();
                    _vs.loadAllPresets();
                    VoxelSniperListener.voxelSnipers.put(_pName, _vSni);
                    _p.sendMessage(ChatColor.RED + "LiteSniper added");
                    _p.sendMessage("" + VoxelSniperListener.voxelSnipers.get(_pName).getPlayer().getName());
                    VoxelSniper.LOG.info("[VoxelSniper] LiteSniper added! (" + _pName + ")");
                    return;
                }
            } catch (final Exception _e) {
                final vSniper _vSni = new liteSniper();
                _vSni.setPlayer(_p);
                _vSni.reset();
                VoxelSniperListener.voxelSnipers.put(_pName, _vSni);
                _p.sendMessage(ChatColor.RED + "LiteSniper added");
                _p.sendMessage("" + VoxelSniperListener.voxelSnipers.get(_pName).getPlayer().getName());
                VoxelSniper.LOG.info("[VoxelSniper] LiteSniper added! (" + _pName + ")");
                return;
            }
        }
    }

    /**
     * Read litesnipers from file.
     */
    public final void readLiteSnipers() {
        try {
            final File _f = new File(VoxelSniperListener.PLUGINS_VOXEL_SNIPER_LITE_SNIPERS_TXT);
            if (_f.exists()) {
                final Scanner _snr = new Scanner(_f);
                while (_snr.hasNext()) {
                    final String _st = _snr.nextLine();
                    VoxelSniperListener.liteSnipers.add(_st);
                }
                _snr.close();
            } else {
                _f.getParentFile().mkdirs();
                _f.createNewFile();
                VoxelSniper.LOG.info("[VoxelSniper] plugins/VoxelSniper/LiteSnipers.txt was missing and was created.");
            }
        } catch (final Exception _e) {
            VoxelSniper.LOG.warning("[VoxelSniper] Error while loading plugins/VoxelSniper/LiteSnipers.txt");
        }
    }

    /**
     * Read Snipers from file.
     */
    public final void readSnipers() {
        try {
            final File _f = new File(VoxelSniperListener.PLUGINS_SNIPERS_TXT);
            final File _nf = new File(VoxelSniperListener.PLUGINS_VOXEL_SNIPER_SNIPERS_TXT);
            if (_f.exists()) {
                if (!_nf.exists()) {
                    final Scanner _snr = new Scanner(_f);
                    while (_snr.hasNext()) {
                        final String _st = _snr.nextLine();
                        VoxelSniperListener.snipers.add(_st);
                    }
                    _snr.close();

                    final PrintWriter _pw = new PrintWriter(_nf);
                    for (final String _st : VoxelSniperListener.snipers) {
                        _pw.write(_st + "\r\n");
                    }
                    _pw.close();

                    _f.delete();

                    VoxelSniper.LOG.warning("[VoxelSniper] ==============================================");
                    VoxelSniper.LOG.warning("[VoxelSniper] ");
                    VoxelSniper.LOG.warning("[VoxelSniper] This is an automated message brough to you by");
                    VoxelSniper.LOG.warning("[VoxelSniper] the przlabs.");
                    VoxelSniper.LOG.warning("[VoxelSniper] Your snipers.txt has been moved into the");
                    VoxelSniper.LOG.warning("[VoxelSniper] plugins/VoxelSniper/  folder.");
                    VoxelSniper.LOG.warning("[VoxelSniper] ");
                    VoxelSniper.LOG.warning("[VoxelSniper] End of automated message.");
                    VoxelSniper.LOG.warning("[VoxelSniper] ");
                    VoxelSniper.LOG.warning("[VoxelSniper] ==============================================");
                } else {
                    _f.delete();
                }
            }
            if (!_nf.exists()) {
                VoxelSniper.LOG.warning("[VoxelSniper] Whoops! snipers.txt is missing or in a wrong place.");
                _f.createNewFile();
                VoxelSniper.LOG.warning("[VoxelSniper] It's okay though, I created a new snipers.txt for you!");
                VoxelSniper.LOG.warning("[VoxelSniper] =======================================================");
                VoxelSniper.LOG.warning("[VoxelSniper] ");
                VoxelSniper.LOG.warning("[VoxelSniper] I created a sample snipers.txt file for you, it is");
                VoxelSniper.LOG.warning("[VoxelSniper] notepad friendly! ");
                VoxelSniper.LOG.warning("[VoxelSniper] ");
                VoxelSniper.LOG.warning("[VoxelSniper] The format of the snipers.txt is as follows:");
                VoxelSniper.LOG.warning("[VoxelSniper] ");
                VoxelSniper.LOG.warning("[VoxelSniper] przerwap");
                VoxelSniper.LOG.warning("[VoxelSniper] Ridgedog");
                VoxelSniper.LOG.warning("[VoxelSniper] R4nD0mNameWithCapitalLettering");
                VoxelSniper.LOG.warning("[VoxelSniper] Gavjenks");
                VoxelSniper.LOG.warning("[VoxelSniper] giltwist");
                VoxelSniper.LOG.warning("[VoxelSniper] ");
                VoxelSniper.LOG.warning("[VoxelSniper] #End of file");
                VoxelSniper.LOG.warning("[VoxelSniper] ");
                VoxelSniper.LOG.warning("[VoxelSniper] As you can see the names are case sensitive and appear");
                VoxelSniper.LOG.warning("[VoxelSniper] one per line.");
                VoxelSniper.LOG.warning("[VoxelSniper] ");
                VoxelSniper.LOG.warning("[VoxelSniper] End of automated message.");
                VoxelSniper.LOG.warning("[VoxelSniper] ");
                VoxelSniper.LOG.warning("[VoxelSniper] =======================================================");
                try {
                    final PrintWriter _pw = new PrintWriter(new File(VoxelSniperListener.PLUGINS_SNIPERS_TXT));

                    _pw.write("przerwap" + "\r\n");
                    _pw.write("Ridgedog" + "\r\n");
                    _pw.write("R4nD0mNameWithCapitalLettering" + "\r\n");
                    _pw.write("Gavjenks" + "\r\n");
                    _pw.write("giltwist" + "\r\n");

                    _pw.close();
                } catch (final Exception _e) {
                }
            }
            final Scanner _snr = new Scanner(_nf);
            VoxelSniperListener.snipers.clear();
            while (_snr.hasNext()) {
                final String _st = _snr.nextLine();
                VoxelSniperListener.snipers.add(_st);
            }
            _snr.close();
        } catch (final Exception _e) {
            VoxelSniper.LOG.warning("[VoxelSniper] Error while loading snipers.txt");
        }
    }

    /**
     * @param player
     */
    public final void runFoodTimer(final Player player) {
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(VoxelSniperListener.plugin, new Runnable() {

            @Override
            public void run() {
                if (VoxelSniperListener.voxelFood.contains(player)) {
                    VoxelSniperListener.voxelFood.remove(player);
                } else {
                    System.out.println("Fatal error has ocurred with VoxelFood.");
                }
            }
        }, 1800L);
    }

    /**
     * Save configuration.
     */
    public final void saveConfig() {
        try {
            VoxelSniper.LOG.info("[VoxelSniper] Saving Configuration.....");

            final File _f = new File(VoxelSniperListener.PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML);
            _f.getParentFile().mkdirs();

            final DocumentBuilderFactory _docFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder _docBuilder = _docFactory.newDocumentBuilder();
            final Document _doc = _docBuilder.newDocument();
            final Element _vsElement = _doc.createElement("VoxelSniper");

            final Element _liteUnusable = _doc.createElement("LiteSniperBannedIDs");
            if (!VoxelSniperListener.liteRestricted.isEmpty()) {
                for (int _x = 0; _x < VoxelSniperListener.liteRestricted.size(); _x++) {
                    final int _id = VoxelSniperListener.liteRestricted.get(_x);
                    final Element _ide = _doc.createElement("id");
                    _ide.appendChild(_doc.createTextNode(_id + ""));
                    _liteUnusable.appendChild(_ide);
                }
            }
            _vsElement.appendChild(_liteUnusable);

            final Element _liteBrushSize = _doc.createElement("MaxLiteBrushSize");
            _liteBrushSize.appendChild(_doc.createTextNode(VoxelSniperListener.liteMaxBrush + ""));
            _vsElement.appendChild(_liteBrushSize);

            final Element _smiteFox = _doc.createElement("SmiteVoxelFox");
            _smiteFox.appendChild(_doc.createTextNode(VoxelSniperListener.smiteVoxelFoxOffenders + ""));
            _vsElement.appendChild(_smiteFox);

            final Element _vFood = _doc.createElement("VoxelFood");
            _vFood.appendChild(_doc.createTextNode(VoxelSniperListener.voxelFoodEnabled + ""));
            _vsElement.appendChild(_vFood);

            final Element _undoCache = _doc.createElement("SniperUndoCache");
            _undoCache.appendChild(_doc.createTextNode(vSniper.getUndoChacheSize() + ""));
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
        } catch (final TransformerException _ex) {
            Logger.getLogger(VoxelSniperListener.class.getName()).log(Level.SEVERE, null, _ex);
        } catch (final ParserConfigurationException _ex) {
            Logger.getLogger(VoxelSniperListener.class.getName()).log(Level.SEVERE, null, _ex);
        }
    }

    private void loadOldConfig() {
        try {
            final File _f = new File(VoxelSniperListener.PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_TXT);
            if (_f.exists()) {
                final Scanner _snr = new Scanner(_f);
                while (_snr.hasNext()) {
                    final String _str = _snr.nextLine();
                    if (_str.startsWith("#")) {
                        continue;
                    }
                    if (_str.startsWith("SniperLiteUnusableIds")) {
                        VoxelSniperListener.liteRestricted.clear();
                        final String[] _sp = _str.split(":")[1].split(",");
                        for (final String _st : _sp) {
                            VoxelSniperListener.liteRestricted.add(Integer.parseInt(_st));
                        }
                    }
                    if (_str.startsWith("MaxLiteBrushSize")) {
                        VoxelSniperListener.liteMaxBrush = Integer.parseInt(_str.split(":")[1]);
                    }
                    if (_str.startsWith("SmiteVoxelFOXoffenders")) {
                        VoxelSniperListener.smiteVoxelFoxOffenders = Boolean.parseBoolean(_str.split("=")[1]);
                    }
                    if (_str.startsWith("EnableVoxelFood")) {
                        VoxelSniperListener.voxelFoodEnabled = Boolean.parseBoolean(_str.split("=")[1]);
                    }
                }
                _snr.close();
                VoxelSniper.LOG.info("[VoxelSniper] Config loaded");
            }
        } catch (final Exception _e) {
            VoxelSniper.LOG.warning("[VoxelSniper] Error while loading SniperConfig.txt");
            _e.printStackTrace();
        }
    }
}
