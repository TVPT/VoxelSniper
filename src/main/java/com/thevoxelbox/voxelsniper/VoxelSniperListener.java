package com.thevoxelbox.voxelsniper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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

    private static final String PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_XML = "plugins/VoxelSniper/SniperConfig.xml";
    private static final String PLUGINS_VOXEL_SNIPER_SNIPER_CONFIG_TXT = "plugins/VoxelSniper/SniperConfig.txt";
    private static final VoxelSniperPermissionHelper SNIPER_PERMISSION_HELPER = new VoxelSniperPermissionHelper();
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
     * @return VoxelSniper
     */
    public static VoxelSniper getPlugin() {
        return VoxelSniperListener.plugin;
    }

    /**
     * @return HashSet<Player>
     */
    public static HashSet<Player> getVoxelFood() {
        return VoxelSniperListener.voxelFood;
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
            return VoxelSniperListener.commandVChunk(player);
        } else if (command.equalsIgnoreCase("paint")) {
            return VoxelSniperListener.commandPaint(player, split);
        } else if (command.equalsIgnoreCase("goto") && VoxelSniperListener.SNIPER_PERMISSION_HELPER.isSniper(player)) {
            return VoxelSniperListener.commandGoto(player, split);
        } else if (VoxelSniperListener.SNIPER_PERMISSION_HELPER.isSniper(player) || VoxelSniperListener.SNIPER_PERMISSION_HELPER.isLiteSniper(player)) {
            if (command.equalsIgnoreCase("btool")) {
                return VoxelSniperListener.commandSniperBTool(player, split);
            } else if (command.equalsIgnoreCase("uuu")) {
                return VoxelSniperListener.commandSniperUUU(player);
            } else if (command.equalsIgnoreCase("uu")) {
                return VoxelSniperListener.commandSniperUU(player, split);
            } else if (command.equalsIgnoreCase("u")) {
                return VoxelSniperListener.commandSniperU(player, split);
            } else if (command.equalsIgnoreCase("d")) {
                return VoxelSniperListener.commandSniperD(player);
            } else if (command.equalsIgnoreCase("vs")) {
                return VoxelSniperListener.commandSniperVs(player, split);
            } else if (command.equalsIgnoreCase("vc")) {
                return VoxelSniperListener.commandSniperVc(player, split);
            } else if (command.equalsIgnoreCase("vh")) {
                return VoxelSniperListener.commandSniperVh(player, split);
            } else if (command.equalsIgnoreCase("vi")) {
                return VoxelSniperListener.commandSniperVi(player, split);
            } else if (command.equalsIgnoreCase("vir")) {
                return VoxelSniperListener.commandSniperVir(player, split);
            } else if (command.equalsIgnoreCase("vr")) {
                return VoxelSniperListener.commandSniperVr(player, split);
            } else if (command.equalsIgnoreCase("vl")) {
                return VoxelSniperListener.commandSniperVl(player, split);
            } else if (command.equalsIgnoreCase("v")) {
                return VoxelSniperListener.commandSniperV(player, split);
            } else if (command.equalsIgnoreCase("b")) {
                return VoxelSniperListener.commandSniperB(player, split);
            } else if (command.equalsIgnoreCase("p")) {
                return VoxelSniperListener.commandSniperP(player, split);
            } else if (command.equalsIgnoreCase("bms")) {
                return VoxelSniperListener.commandSniperBms(player, split);
            } else if (command.equalsIgnoreCase("bml")) {
                return VoxelSniperListener.commandSniperBml(player, split);
            }
        }
        return false;
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
     * @param player
     * @param split
     * @return
     */
    private static boolean commandGoto(final Player player, final String[] split) {
        if (VoxelSniperListener.SNIPER_PERMISSION_HELPER.isLiteSniper(player)) {
            player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
            return true;
        }
        try {
            final int _x = Integer.parseInt(split[0]);
            final int _z = Integer.parseInt(split[1]);
            player.teleport(new Location(player.getWorld(), _x, player.getWorld().getHighestBlockYAt(_x, _z), _z));
            player.sendMessage(ChatColor.GREEN + "Woosh!");
            return true;
        } catch (final Exception _e) {
            player.sendMessage("Wrong.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandPaint(final Player player, final String[] split) {
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

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperB(final Player player, final String[] split) {
        try {
            final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);
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

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperBml(final Player player, final String[] split) {
        try {
            final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);
            _ps.loadPreset(Integer.parseInt(split[0]));
            return true;
        } catch (final Exception _e) {
            final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);
            _ps.loadPreset(split[0]);
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperBms(final Player player, final String[] split) {
        try {
            final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);
            _ps.savePreset(Integer.parseInt(split[0]));
            return true;
        } catch (final Exception _e) {
            final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);
            _ps.savePreset(split[0]);
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperBTool(final Player player, final String[] split) {
        if (split != null && split.length > 0) {
            if (split[0].equalsIgnoreCase("add")) {
                if (split.length == 2) {
                    if (split[1].equals("-arrow")) {
                        VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).addBrushTool(true);
                    } else if (split[1].equals("-powder")) {
                        VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).addBrushTool(false);
                    } else {
                        player.sendMessage(ChatColor.GREEN + "/btool add (-arrow|-powder) -- turns the item in your hand into a BrushTool");
                    }
                } else {
                    VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).addBrushTool();
                }
            } else if (split[0].equalsIgnoreCase("remove")) {
                VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).removeBrushTool();
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

    /**
     * @param player
     * @return
     */
    private static boolean commandSniperD(final Player player) {
        try {
            VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).reset();
            player.sendMessage(ChatColor.GRAY + "Values reset.");
            return true;
        } catch (final Exception _e) {
            player.sendMessage("Not valid.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperP(final Player player, final String[] split) {
        try {
            final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);
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

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperU(final Player player, final String[] split) {
        final vSniper _vs = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);
        try {
            final int _r = Integer.parseInt(split[0]);
            _vs.doUndo(_r);
        } catch (final Exception _e) {
            _vs.doUndo();
        }
        VoxelSniper.LOG.log(Level.INFO, "[VoxelSniper] Player \"" + player.getName() + "\" used /u");
        return true;
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperUU(final Player player, final String[] split) {
        try {
            VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(Bukkit.getPlayer(split[0]).getName()).doUndo();
            return true;
        } catch (final Exception _e) {
            player.sendMessage(ChatColor.GREEN + "Player not found");
            return true;
        }
    }

    /**
     * @param player
     * @return
     */
    private static boolean commandSniperUUU(final Player player) {
        try {
            VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).doUndo();
            return true;
        } catch (final Exception _e) {
            player.sendMessage(ChatColor.GREEN + "Player not found");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperV(final Player player, final String[] split) {
        if (split.length == 0) {
            final Block _tb = new HitBlox(player, player.getWorld()).getTargetBlock();
            if (_tb != null) {
                VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setVoxel(_tb.getTypeId());
            }
            return true;
        }

        if (VoxelSniper.getItem(split[0]) != -1) {
            final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);
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

            final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);

            if (_mat.isBlock()) {
                _ps.setVoxel(_mat.getId());
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID!");
                return true;
            }
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperVc(final Player player, final String[] split) {
        try {
            if (split.length == 0) {
                VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setCentroid(0);
                return true;
            }
            VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setCentroid(Integer.parseInt(split[0]));
            return true;
        } catch (final Exception _e) {
            player.sendMessage(ChatColor.RED + "Invalid input");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperVh(final Player player, final String[] split) {
        try {
            VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setHeigth(Integer.parseInt(split[0]));
            return true;
        } catch (final Exception _e) {
            player.sendMessage(ChatColor.RED + "Invalid input");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperVi(final Player player, final String[] split) {
        if (split.length == 0) {
            final Block _tb = new HitBlox(player, player.getWorld()).getTargetBlock();
            if (_tb != null) {
                VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setData(_tb.getData());
            }
            return true;
        }
        try {
            VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setData((byte) Integer.parseInt(split[0]));
            return true;
        } catch (final Exception _e) {
            player.sendMessage(ChatColor.RED + "Invalid input, please enter a number.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperVir(final Player player, final String[] split) {
        if (split.length == 0) {
            final Block _tb = new HitBlox(player, player.getWorld()).getTargetBlock();
            if (_tb != null) {
                VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setReplaceData(_tb.getData());
            }
            return true;
        }
        try {
            VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setReplaceData((byte) Integer.parseInt(split[0]));
            return true;
        } catch (final Exception _e) {
            player.sendMessage(ChatColor.RED + "Invalid input, please enter a number.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperVl(final Player player, final String[] split) {
        if (split.length == 0) {
            final HitBlox _hb = new HitBlox(player, player.getWorld());
            final Block _tb = _hb.getTargetBlock();
            try {
                VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).addVoxelToList(_tb.getTypeId());

                return true;
            } catch (final Exception _e) {
                return true;
            }
        } else {
            if (split[0].equalsIgnoreCase("clear")) {
                VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).clearVoxelList();
                return true;
            }
        }

        final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);
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

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperVr(final Player player, final String[] split) {
        if (split.length == 0) {
            final Block _tb = new HitBlox(player, player.getWorld()).getTargetBlock();
            if (_tb != null) {
                VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setReplace(_tb.getTypeId());
            }
            return true;
        }

        if (VoxelSniper.getItem(split[0]) != -1) {
            final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);
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

            final vSniper _ps = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player);

            if (_mat.isBlock()) {
                _ps.setReplace(_mat.getId());
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID!");
                return true;
            }
        }
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandSniperVs(final Player player, final String[] split) {
        try {
            if (split.length >= 1) {
                if (split[0].equalsIgnoreCase("brushes")) {
                    VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).printBrushes();
                    return true;
                } else if (split[0].equalsIgnoreCase("brusheslong")) {
                    VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).printBrushesLong();
                    return true;
                } else if (split[0].equalsIgnoreCase("printout")) {
                    VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).togglePrintout();
                    return true;
                } else if (split[0].equalsIgnoreCase("lightning")) {
                    if (VoxelSniperListener.SNIPER_PERMISSION_HELPER.isLiteSniper(player)) {
                        player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                        return true;
                    }
                    VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).toggleLightning();
                    return true;
                } else if (split[0].equalsIgnoreCase("weather")) {
                    if (VoxelSniperListener.SNIPER_PERMISSION_HELPER.isLiteSniper(player)) {
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
                        if (VoxelSniperListener.SNIPER_PERMISSION_HELPER.isLiteSniper(player) && (_i > 12 || _i < -12)) {
                            player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use ranges over 12.");
                            return true;
                        }
                        VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setRange(_i);
                        return true;
                    } else {
                        VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).setRange(-1);
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
            VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(player).info();
            return true;
        } catch (final Exception _e) {
            player.sendMessage(ChatColor.RED + "You are not allowed to use this command");
            return true;
        }
    }

    /**
     * @param player
     * @return
     */
    private static boolean commandVChunk(final Player player) {
        player.getWorld().refreshChunk(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
        return true;
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
            final vSniper _vs = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(_player);
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
        _p.getName();
        if (VoxelSniperListener.SNIPER_PERMISSION_HELPER.isSniper(_p)) {
            try {
                final vSniper _vs = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(_p);
                _vs.setPlayer(_p);
                _vs.info();
                return;
            } catch (final Exception _e) {
                return;
            }
        }
        if (VoxelSniperListener.SNIPER_PERMISSION_HELPER.isLiteSniper(_p)) {
            try {
                final vSniper _vs = VoxelSniperListener.SNIPER_PERMISSION_HELPER.getSniperInstance(_p);
                if (_vs instanceof liteSniper) {
                    _vs.setPlayer(_p);
                    _vs.info();
                    return;
                } else {
                    return;
                }
            } catch (final Exception _e) {
                return;
            }
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
