package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.perform.PerformerE;
import com.thevoxelbox.voxelsniper.voxelfood.CatapultCalzone;
import com.thevoxelbox.voxelsniper.voxelfood.DietDrSmurfy;
import com.thevoxelbox.voxelsniper.voxelfood.DobaCrackaz;
import com.thevoxelbox.voxelsniper.voxelfood.NinewerksCoffee;
import com.thevoxelbox.voxelsniper.voxelfood.OinkiesPorkSandwich;
import com.thevoxelbox.voxelsniper.voxelfood.PoisonVial;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class VoxelSniperListener implements Listener {

    public static TreeMap<String, vSniper> VoxelSnipers = new TreeMap<String, vSniper>();
    public static HashSet<String> snipers = new HashSet<String>();
    public static HashSet<String> liteSnipers = new HashSet<String>();
    public static HashSet<Integer> liteRestricted = new HashSet<Integer>();
    public static HashSet<Player> voxelFood = new HashSet<Player>();
    public static int LITE_MAX_BRUSH;
    public static boolean SMITE_VOXELFOX_OFFENDERS;
    public static boolean VOXEL_FOOD;
    //
    private static final int CONFIG_VERSION = 705;
    //
    //
    public static VoxelSniper plugin;
    //

    public VoxelSniperListener(VoxelSniper instance) {
        plugin = instance;
    }

    public void initSnipers() {
        readSnipers();
        readLiteSnipers();
        loadConfig();
        VoxelSnipers.clear();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (snipers.contains(p.getName())) {
                String playerName = p.getName();
                VoxelSnipers.put(playerName, new vSniper());
                VoxelSnipers.get(playerName).reset();
                VoxelSnipers.get(playerName).p = p;
                VoxelSnipers.get(playerName).loadAllPresets();
            }
            if (liteSnipers.contains(p.getName())) {
                String playerName = p.getName();
                VoxelSnipers.put(playerName, new liteSniper());
                VoxelSnipers.get(playerName).reset();
                VoxelSnipers.get(playerName).p = p;
                VoxelSnipers.get(playerName).loadAllPresets();
            }
        }
    }

    public static boolean isAdmin(String st) {
        return snipers.contains(st);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String pName = p.getName();
        if (isAdmin(pName)) {
            try {
                vSniper vs = VoxelSnipers.get(pName);
                vs.p = p;
                vs.info();
                return;
            } catch (Exception e) {
                vSniper vs = new vSniper();
                vs.reset();
                vs.p = p;
                vs.loadAllPresets();
                VoxelSnipers.put(pName, vs);
                p.sendMessage(ChatColor.RED + "Sniper added");
                p.sendMessage("" + ChatColor.RED + VoxelSnipers.get(pName).p.getName());
                vs.info();
                return;
            }
        }
        if (liteSnipers.contains(p.getName())) {
            try {
                vSniper vs = VoxelSnipers.get(pName);
                if (vs instanceof liteSniper) {
                    vs.p = p;
                    vs.info();
                    return;
                } else {
                    vSniper vSni = new liteSniper();
                    vSni.reset();
                    vSni.p = p;
                    vs.loadAllPresets();
                    VoxelSnipers.put(pName, vSni);
                    p.sendMessage(ChatColor.RED + "LiteSniper added");
                    p.sendMessage("" + VoxelSnipers.get(pName).p.getName());
                    VoxelSniper.log.info("[VoxelSniper] LiteSniper added! (" + pName + ")");
                    return;
                }
            } catch (Exception e) {
                vSniper vSni = new liteSniper();
                vSni.reset();
                vSni.p = p;
                VoxelSnipers.put(pName, vSni);
                p.sendMessage(ChatColor.RED + "LiteSniper added");
                p.sendMessage("" + VoxelSnipers.get(pName).p.getName());
                VoxelSniper.log.info("[VoxelSniper] LiteSniper added! (" + pName + ")");
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerPreprocessCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().contains("|")) {
            String[] commands = event.getMessage().split("\\|");
            
            for (String command : commands) {
                command = command.trim();
                if (command.charAt(0) == '/') {
                    event.getPlayer().chat(command);
                }
            }
            
            event.setCancelled(true);
        }
    }

    public static boolean onCommand(Player player, String[] split, String command) {
        if (command.equalsIgnoreCase("vchunk")) {
            player.getWorld().refreshChunk(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
            return true;
        }
        if (command.equalsIgnoreCase("paint")) {
            if (split.length == 1) {
                try {
                    vPainting.paint(player, false, false, Integer.parseInt(split[0]));
                    return true;
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Invalid input!");
                    return true;
                }
            } else {
                vPainting.paint(player, true, false, 0);
                return true;
            }
        }
        if (command.equalsIgnoreCase("addlitesniper") && (isAdmin(player.getName()) || player.isOp())) {
            if (VoxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            Player pl;
            String plName;
            try {
                pl = plugin.getServer().getPlayer(split[0]);
                plName = pl.getName();
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Invalid name. Player not found!");
                return true;
            }
            try {
                vSniper vs = VoxelSnipers.get(plName);
                vs.reset();
                player.sendMessage(ChatColor.GREEN + "Sniper was already on the list. His settings were reset to default.");
                return true;
            } catch (Exception e) {
                vSniper vSni = new liteSniper();
                vSni.reset();
                vSni.p = pl;
                vSni.loadAllPresets();
                VoxelSnipers.put(plName, vSni);
                liteSnipers.add(pl.getName());
                writeLiteSnipers();
                player.sendMessage(ChatColor.RED + "LiteSniper added");
                player.sendMessage("" + VoxelSnipers.get(plName).p.getName());
                pl.sendMessage(ChatColor.RED + "LiteSniper added");
                pl.sendMessage("" + VoxelSnipers.get(plName).p.getName());
                VoxelSniper.log.info("[VoxelSniper] LiteSniper added! (" + pl.getName() + ") by: (" + player.getName() + ")");
                return true;
            }
        }
        if (command.equalsIgnoreCase("addsniper") && (isAdmin(player.getName()) || player.isOp())) {
            if (VoxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            Player pl;
            String plName;
            try {
                pl = plugin.getServer().getPlayer(split[0]);
                plName = pl.getName();
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Invalid name. Player not found!");
                return true;
            }
            try {
                vSniper vs = VoxelSnipers.get(plName);
                vs.reset();
                player.sendMessage(ChatColor.GREEN + "Sniper was already on the list. His settings were reset to default.");
                return true;
            } catch (Exception e) {
                vSniper vSni = new vSniper();
                vSni.reset();
                vSni.p = pl;
                vSni.loadAllPresets();
                VoxelSnipers.put(plName, vSni);
                writeSnipers();
                player.sendMessage(ChatColor.RED + "Sniper added");
                player.sendMessage("" + VoxelSnipers.get(plName).p.getName());
                pl.sendMessage(ChatColor.RED + "Sniper added");
                pl.sendMessage("" + VoxelSnipers.get(plName).p.getName());
                VoxelSniper.log.info("[VoxelSniper] Sniper added! (" + pl.getName() + ") by: (" + player.getName() + ")");
                return true;
            }
        }
        if (command.equalsIgnoreCase("addlitesnipertemp") && (isAdmin(player.getName()) || player.isOp())) {
            if (VoxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            Player pl;
            String plName;
            try {
                pl = plugin.getServer().getPlayer(split[0]);
                plName = pl.getName();
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Invalid name. Player not found!");
                return true;
            }
            try {
                vSniper vs = VoxelSnipers.get(plName);
                vs.reset();
                player.sendMessage(ChatColor.GREEN + "Sniper was already on the list. His settings were reset to default.");
                return true;
            } catch (Exception e) {
                vSniper vSni = new liteSniper();
                vSni.reset();
                vSni.p = pl;
                vSni.loadAllPresets();
                VoxelSnipers.put(plName, vSni);
                liteSnipers.add(pl.getName());
                player.sendMessage(ChatColor.RED + "LiteSniper added");
                player.sendMessage("" + VoxelSnipers.get(plName).p.getName());
                pl.sendMessage(ChatColor.RED + "LiteSniper added");
                pl.sendMessage("" + VoxelSnipers.get(plName).p.getName());
                VoxelSniper.log.info("[VoxelSniper] LiteSniper added! (" + pl.getName() + ") by: (" + player.getName() + ")");
                return true;
            }
        }
        if (command.equalsIgnoreCase("addsnipertemp") && (isAdmin(player.getName()) || player.isOp())) {
            if (VoxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            Player pl;
            String plName;
            try {
                pl = plugin.getServer().getPlayer(split[0]);
                plName = pl.getName();
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Invalid name. Player not found!");
                return true;
            }
            try {
                vSniper vs = VoxelSnipers.get(plName);
                vs.reset();
                player.sendMessage(ChatColor.GREEN + "Sniper was already on the list. His settings were reset to default.");
                return true;
            } catch (Exception e) {
                vSniper vSni = new vSniper();
                vSni.reset();
                vSni.p = pl;
                vSni.loadAllPresets();
                VoxelSnipers.put(plName, vSni);
                player.sendMessage(ChatColor.RED + "Sniper added");
                player.sendMessage("" + VoxelSnipers.get(plName).p.getName());
                pl.sendMessage(ChatColor.RED + "Sniper added");
                pl.sendMessage("" + VoxelSnipers.get(plName).p.getName());
                VoxelSniper.log.info("[VoxelSniper] Sniper added! (" + pl.getName() + ") by: (" + player.getName() + ")");
                return true;
            }
        }
        if (command.equalsIgnoreCase("removesniper") && (isAdmin(player.getName()) || player.isOp())) {
            if (VoxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            try {
                vSniper vs = VoxelSnipers.remove(split[0]);
                Boolean success = removeSniper(split[0]);
                Boolean success2 = removeLiteSniper(split[0]);
                if (!success && !success2) {
                    player.sendMessage("Unsuccessful removal.");
                } else {
                    player.sendMessage("Sniper removed.");
                }
                vs.p.sendMessage(ChatColor.DARK_GREEN + "Sorry you were removed from the sniper list :(");
                return true;
            } catch (Exception e) {
                player.sendMessage(ChatColor.GRAY + "Unsuccessful removal.");
                return true;
            }
        }
        if (command.equalsIgnoreCase("goto") && isAdmin(player.getName())) {
            if (VoxelSnipers.get(player.getName()) instanceof liteSniper) {
                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                return true;
            }
            try {
                int x = Integer.parseInt(split[0]);
                int z = Integer.parseInt(split[1]);
                player.teleport(new Location(player.getWorld(), x, 115, z));
                player.sendMessage(ChatColor.GREEN + "Woosh!");
                return true;
            } catch (Exception e) {
                player.sendMessage("Wrong.");
                return true;
            }
        }
        if (VoxelSnipers.containsKey(player.getName())) {
            if (command.equalsIgnoreCase("uuu")) {
                try {
                    VoxelSnipers.get(split[0]).doUndo();
                    return true;
                } catch (Exception e) {
                    player.sendMessage(ChatColor.GREEN + "Player not found");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("uu")) {
                try {
                    VoxelSnipers.get(plugin.getServer().getPlayer(split[0]).getName()).doUndo();
                    return true;
                } catch (Exception e) {
                    player.sendMessage(ChatColor.GREEN + "Player not found");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("u")) {
                vSniper vs = VoxelSnipers.get(player.getName());
                try {
                    int r = Integer.parseInt(split[0]);
                    vs.doUndo(r);
                } catch (Exception e) {
                    vs.doUndo();
                }
                VoxelSniper.log.log(Level.INFO, "[VoxelSniper] Player \"" + player.getName() + "\" used /u");
                return true;
            }
            if (command.equalsIgnoreCase("d")) {
                try {
                    VoxelSnipers.get(player.getName()).reset();
                    player.sendMessage(ChatColor.GRAY + "Values reset.");
                    return true;
                } catch (Exception e) {
                    player.sendMessage("Not valid.");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vs")) {
                try {
                    if (split.length >= 1) {
                        if (split[0].equalsIgnoreCase("brushes")) {
                            VoxelSnipers.get(player.getName()).printBrushes();
                            return true;
                        } else if (split[0].equalsIgnoreCase("brusheslong")) {
                            VoxelSnipers.get(player.getName()).printBrushesLong();
                            return true;
                        } else if (split[0].equalsIgnoreCase("printout")) {
                            VoxelSnipers.get(player.getName()).togglePrintout();
                            return true;
                        } else if (split[0].equalsIgnoreCase("lightning")) {
                            if (VoxelSnipers.get(player.getName()) instanceof liteSniper) {
                                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                                return true;
                            }
                            VoxelSnipers.get(player.getName()).toggleLightning();
                            return true;
                        } else if (split[0].equalsIgnoreCase("weather")) {
                            if (VoxelSnipers.get(player.getName()) instanceof liteSniper) {
                                player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use this command.");
                                return true;
                            }
                            player.getWorld().setWeatherDuration(0);
                            player.getWorld().setStorm(false);
                            player.sendMessage(ChatColor.GREEN + "Begone weather!");
                            return true;
                        } else if (split[0].equalsIgnoreCase("clear")) {  //So you don't accidentally undo stuff you worked on an hour ago. Also frees RAM, I suspect -Giltwist
                            // Would I reset the stuff in vUndo.java or in vSniper.java or both?
                            //player.sendMessage(ChatColor.GREEN + "Undo cache cleared");
                            return true;
                        } else if (split[0].equalsIgnoreCase("s")) {
                            if (split.length == 1) {
                                player.sendMessage(ChatColor.RED + "You done goofed.");
                            }

                            String message = "";
                            for (int i = 1; i < split.length; i++) {
                                message += split[i] + " ";
                            }
                            player.sendMessage(message);
                            return true;
                        } else if (split[0].equalsIgnoreCase("range")) {
                            if (split.length == 2) {
                                double i = Double.parseDouble(split[1]);
                                if (VoxelSnipers.get(player.getName()) instanceof liteSniper && (i > 12 || i < -12)) {
                                    player.sendMessage(ChatColor.RED + "A liteSniper is not permitted to use ranges over 12.");
                                    return true;
                                }
                                VoxelSnipers.get(player.getName()).setRange(i);
                                return true;
                            } else {
                                VoxelSnipers.get(player.getName()).setRange(-1);
                                return true;
                            }
                        } else if (split[0].equalsIgnoreCase("sitall")) {
                            player.sendMessage("Sitting all the players...");
                            sitAll();
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
                    VoxelSnipers.get(player.getName()).info();
                    return true;
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "You are not allowed to use this command");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vc")) {
                try {
                    if (split.length == 0) {
                        VoxelSnipers.get(player.getName()).setCentroid(0);
                        return true;
                    }
                    VoxelSnipers.get(player.getName()).setCentroid(Integer.parseInt(split[0]));
                    return true;
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Invalid input");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vh")) {
                try {
                    VoxelSnipers.get(player.getName()).setHeigth(Integer.parseInt(split[0]));
                    return true;
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Invalid input");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vi")) {
                if (split.length == 0) {
                    HitBlox hb = new HitBlox(player, player.getWorld());
                    Block tb = hb.getTargetBlock();
                    try {
                        VoxelSnipers.get(player.getName()).setData(tb.getData());
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                }
                try {
                    VoxelSnipers.get(player.getName()).setData((byte) Integer.parseInt(split[0]));
                    return true;
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Invalid input, please enter a number.");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vir")) {
                if (split.length == 0) {
                    HitBlox hb = new HitBlox(player, player.getWorld());
                    Block tb = hb.getTargetBlock();
                    try {
                        VoxelSnipers.get(player.getName()).setReplaceData(tb.getData());
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                }
                try {
                    VoxelSnipers.get(player.getName()).setReplaceData((byte) Integer.parseInt(split[0]));
                    return true;
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Invalid input, please enter a number.");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("vr")) {
                if (split.length == 0) {
                    HitBlox hb = new HitBlox(player, player.getWorld());
                    Block tb = hb.getTargetBlock();
                    try {
                        VoxelSnipers.get(player.getName()).setReplace(tb.getTypeId());
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                }
                try {
                    vSniper ps = VoxelSnipers.get(player.getName());
                    int i = Integer.parseInt(split[0]);
                    if (VoxelSniper.isValidItem(i) && Material.getMaterial(i).isBlock()) {
                        ps.setReplace(i);
                        return true;
                    } else if (ps.replaceId != 0) {
                        player.sendMessage(ChatColor.RED + "Invalid item ID D:<");
                        return true;
                    }
                    return true;
                } catch (Exception e) {
                    try {
                        vSniper ps = VoxelSnipers.get(player.getName());
                        int derp = VoxelSniper.getItem(split[0]);
                        if (derp != 0) {
                            ps.setReplace(derp);
                            return true;
                        }
                        player.sendMessage("Not valid.");
                        return true;
                    } catch (Exception ex) {
                        return true;
                    }
                }
            }
            if (command.equalsIgnoreCase("vl")) {
                if (split.length == 0) {
                    HitBlox hb = new HitBlox(player, player.getWorld());
                    Block tb = hb.getTargetBlock();
                    try {
                        VoxelSnipers.get(player.getName()).addVoxelToList(tb.getTypeId());

                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                } else {
                    if (split[0].equalsIgnoreCase("clear")) {
                        VoxelSnipers.get(player.getName()).clearVoxelList();
                        return true;
                    }
                }

                vSniper ps = VoxelSnipers.get(player.getName());
                boolean rem = false;

                for (String str : split) {
                    String tmpint;
                    Integer xint;

                    try {
                        if (str.startsWith("-")) {
                            rem = true;
                            tmpint = str.replaceAll("-", "");
                        } else {
                            tmpint = str;
                        }

                        xint = Integer.parseInt(tmpint);

                        if (VoxelSniper.isValidItem(xint) && Material.getMaterial(xint).isBlock()) {
                            if (!rem) {
                                ps.addVoxelToList(xint);
                                continue;
                            } else {
                                ps.removeVoxelFromList(xint);
                                continue;
                            }
                        }

                    } catch (NumberFormatException e) {
                        try {
                            String tmpstr;
                            Integer xstr;
                            rem = false;

                            if (str.startsWith("-")) {
                                rem = true;
                                tmpstr = str.replaceAll("-", "");
                            } else {
                                tmpstr = str;
                            }

                            xstr = VoxelSniper.getItem(tmpstr);

                            if (!rem) {
                                ps.addVoxelToList(xstr);
                            } else {
                                ps.removeVoxelFromList(xstr);
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
                return true;
            }
            if (command.equalsIgnoreCase("v")) {
                if (split.length == 0) {
                    HitBlox hb = new HitBlox(player, player.getWorld());
                    Block tb = hb.getTargetBlock();
                    try {
                        VoxelSnipers.get(player.getName()).setVoxel(tb.getTypeId());
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                }
                if (split[0] != null) {
                    try {
                        vSniper ps = VoxelSnipers.get(player.getName());
                        int i = Integer.parseInt(split[0]);
                        if (VoxelSniper.isValidItem(i) && Material.getMaterial(i).isBlock()) {
                            ps.setVoxel(i);
                            return true;
                        } else if (ps.voxelId != 0) {
                            player.sendMessage(ChatColor.RED + "Invalid Item ID. D:<");
                            return true;
                        }
                        return true;
                    } catch (NumberFormatException n) {
                        try {
                            vSniper ps = VoxelSnipers.get(player.getName());
                            int derp = VoxelSniper.getItem(split[0]);
                            if (derp != 0) {
                                ps.setVoxel(derp);
                                return true;
                            }
                            player.sendMessage("" + split[0] + " is not valid. /v <bId>");
                            return true;
                        } catch (Exception ex) {
                            return true;
                        }
                    }
                } else {
                    player.sendMessage("Use /v <blockId> or /v <Monster name>");
                    return true;
                }
            }
            if (command.equalsIgnoreCase("b")) {
                try {
                    vSniper ps = VoxelSnipers.get(player.getName());
                    try {
                        if (split == null || split.length == 0) {
                            ps.previousBrush();
                            //player.sendMessage(ChatColor.RED + "Please input a brush size.");
                            return true;
                        } else {
                            ps.setBrushSize(Integer.parseInt(split[0]));
                            player.sendMessage(ChatColor.GREEN + "Voxel brush size set to " + ps.brushSize);
                            if (ps.brushSize >= 20) {
                                player.sendMessage(ChatColor.RED + "WARNING: Large brush size selected!");
                            }
                            return true;
                        }
                    } catch (Exception e) {
                        ps.fillPrevious();
                        ps.setBrush(split);
                        return true;
                    }
                } catch (Exception ex) {
                    VoxelSniper.log.log(Level.WARNING, "[VoxelSniper] Command error from " + player.getName());
                    ex.printStackTrace();
                    return true;
                }
            }
            if (command.equalsIgnoreCase("p")) {
                try {
                    vSniper ps = VoxelSnipers.get(player.getName());
                    if (split == null || split.length == 0) {
                        ps.setPerformer(new String[]{"", "m"});
                    } else {
                        ps.setPerformer(split);
                    }
                    return true;
                } catch (Exception ex) {
                    VoxelSniper.log.log(Level.WARNING, "[VoxelSniper] Command error from " + player.getName());
                    ex.printStackTrace();
                    return true;
                }
            }
            if (command.equalsIgnoreCase("bms")) {
                try {
                    vSniper ps = VoxelSnipers.get(player.getName());
                    ps.savePreset(Integer.parseInt(split[0]));
                    return true;
                } catch (Exception e) {
                    vSniper ps = VoxelSnipers.get(player.getName());
                    ps.savePreset(split[0]);
                    return true;
                }
            }
            if (command.equalsIgnoreCase("bml")) {
                try {
                    vSniper ps = VoxelSnipers.get(player.getName());
                    ps.loadPreset(Integer.parseInt(split[0]));
                    return true;
                } catch (Exception e) {
                    vSniper ps = VoxelSnipers.get(player.getName());
                    ps.loadPreset(split[0]);
                    return true;
                }
            }
        }
        return false;
    }

    private static void sitAll() {
        for (Player player : VoxelSniper.s.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet39AttachEntity(((CraftPlayer) player).getHandle(), ((CraftPlayer) player).getHandle()));
            for (Player p : player.getServer().getOnlinePlayers()) {
                if (!p.getName().equals(player.getName())) {
                    ((CraftPlayer) p).getHandle().netServerHandler.sendPacket(new Packet39AttachEntity(((CraftPlayer) player).getHandle(), ((CraftPlayer) player).getHandle()));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.isBlockInHand()) {
            return;
        }
        final Player player = e.getPlayer();
        if (VOXEL_FOOD) {
            switch (player.getItemInHand().getType()) {
                case INK_SACK:
                    switch (player.getItemInHand().getData().getData()) {
                        case 3:
                            if (!voxelFood.contains(player)) {
                                e.setCancelled(new NinewerksCoffee().perform(e.getAction(), player, player.getItemInHand(), e.getClickedBlock()));
                                voxelFood.add(player);
                                runFoodTimer(player);
                            } else {
                                player.sendMessage(ChatColor.RED + "You can't do that yet!");
                            }
                            break;
                        case 4:
                            if (!voxelFood.contains(player)) {
                                e.setCancelled(new DietDrSmurfy().perform(e.getAction(), player, player.getItemInHand(), e.getClickedBlock()));
                                voxelFood.add(player);
                                runFoodTimer(player);
                            } else {
                                player.sendMessage(ChatColor.RED + "You can't do that yet!");
                            }
                            break;
                        case 8:
                            if (!voxelFood.contains(player)) {
                                e.setCancelled(new DobaCrackaz().perform(e.getAction(), player, player.getItemInHand(), e.getClickedBlock()));
                                voxelFood.add(player);
                                runFoodTimer(player);
                            } else {
                                player.sendMessage(ChatColor.RED + "You can't do that yet!");
                            }
                            break;
                        case 10:
                            if (!voxelFood.contains(player)) {
                                e.setCancelled(new CatapultCalzone().perform(e.getAction(), player, player.getItemInHand(), e.getClickedBlock()));
                                voxelFood.add(player);
                                runFoodTimer(player);
                            } else {
                                player.sendMessage(ChatColor.RED + "You can't do that yet!");
                            }
                            break;
                        case 12:
                            if (!voxelFood.contains(player)) {
                                e.setCancelled(new OinkiesPorkSandwich().perform(e.getAction(), player, player.getItemInHand(), e.getClickedBlock()));
                                voxelFood.add(player);
                                runFoodTimer(player);
                            } else {
                                player.sendMessage(ChatColor.RED + "You can't do that yet!");
                            }
                            break;
                        case 13:
                            if (!voxelFood.contains(player)) {
                                e.setCancelled(new PoisonVial().perform(e.getAction(), player, player.getItemInHand(), e.getClickedBlock()));
                                voxelFood.add(player);
                                runFoodTimer(player);
                            } else {
                                player.sendMessage(ChatColor.RED + "You can't do that yet!");
                            }
                            break;
                    }
                    break;
            }
        }
        try {
            vSniper vs = VoxelSnipers.get(player.getName());
            if (vs == null) {
                return;
            } else if (vs.snipe(player, e.getAction(), e.getMaterial(), e.getClickedBlock(), e.getBlockFace())) {
                e.setCancelled(true);
            }
        } catch (Exception ex) {
            return;
        }
    }

    public static void writeSnipers() {
        try {
            PrintWriter pw = new PrintWriter(new File("plugins/VoxelSniper/snipers.txt"));
            for (String st : snipers) {
                pw.write(st + "\r\n");
            }
            pw.close();
        } catch (Exception e) {
        }
    }

    public static void writeLiteSnipers() {
        try {
            PrintWriter pw = new PrintWriter(new File("plugins/VoxelSniper/LiteSnipers.txt"));
            for (String st : liteSnipers) {
                pw.write(st + "\r\n");
            }
            pw.close();
        } catch (Exception e) {
        }
    }

    public static boolean removeLiteSniper(String toRemove) {
        try {
            liteSnipers.remove(toRemove);
            writeSnipers();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean removeSniper(String toRemove) {
        try {
            snipers.remove(toRemove);
            writeSnipers();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void readSnipers() {
        try {
            File f = new File("plugins/snipers.txt");
            File nf = new File("plugins/VoxelSniper/snipers.txt");
            if (f.exists()) {
                if (!nf.exists()) {
                    Scanner snr = new Scanner(f);
                    while (snr.hasNext()) {
                        String st = snr.nextLine();
                        snipers.add(st);
                    }
                    snr.close();

                    PrintWriter pw = new PrintWriter(nf);
                    for (String st : snipers) {
                        pw.write(st + "\r\n");
                    }
                    pw.close();

                    f.delete();

                    VoxelSniper.log.warning("[VoxelSniper] ==============================================");
                    VoxelSniper.log.warning("[VoxelSniper] ");
                    VoxelSniper.log.warning("[VoxelSniper] This is an automated message brough to you by");
                    VoxelSniper.log.warning("[VoxelSniper] the przlabs.");
                    VoxelSniper.log.warning("[VoxelSniper] Your snipers.txt has been moved into the");
                    VoxelSniper.log.warning("[VoxelSniper] plugins/VoxelSniper/  folder.");
                    VoxelSniper.log.warning("[VoxelSniper] ");
                    VoxelSniper.log.warning("[VoxelSniper] End of automated message.");
                    VoxelSniper.log.warning("[VoxelSniper] ");
                    VoxelSniper.log.warning("[VoxelSniper] ==============================================");
                } else {
                    f.delete();
                }
            }
            if (!nf.exists()) {
                VoxelSniper.log.warning("[VoxelSniper] Whoops! snipers.txt is missing or in a wrong place.");
                f.createNewFile();
                VoxelSniper.log.warning("[VoxelSniper] It's okay though, I created a new snipers.txt for you!");
                VoxelSniper.log.warning("[VoxelSniper] =======================================================");
                VoxelSniper.log.warning("[VoxelSniper] ");
                VoxelSniper.log.warning("[VoxelSniper] I created a sample snipers.txt file for you, it is");
                VoxelSniper.log.warning("[VoxelSniper] notepad friendly! ");
                VoxelSniper.log.warning("[VoxelSniper] ");
                VoxelSniper.log.warning("[VoxelSniper] The format of the snipers.txt is as follows:");
                VoxelSniper.log.warning("[VoxelSniper] ");
                VoxelSniper.log.warning("[VoxelSniper] przerwap");
                VoxelSniper.log.warning("[VoxelSniper] Ridgedog");
                VoxelSniper.log.warning("[VoxelSniper] R4nD0mNameWithCapitalLettering");
                VoxelSniper.log.warning("[VoxelSniper] Gavjenks");
                VoxelSniper.log.warning("[VoxelSniper] giltwist");
                VoxelSniper.log.warning("[VoxelSniper] ");
                VoxelSniper.log.warning("[VoxelSniper] #End of file");
                VoxelSniper.log.warning("[VoxelSniper] ");
                VoxelSniper.log.warning("[VoxelSniper] As you can see the names are case sensitive and appear");
                VoxelSniper.log.warning("[VoxelSniper] one per line.");
                VoxelSniper.log.warning("[VoxelSniper] ");
                VoxelSniper.log.warning("[VoxelSniper] End of automated message.");
                VoxelSniper.log.warning("[VoxelSniper] ");
                VoxelSniper.log.warning("[VoxelSniper] =======================================================");
                try {
                    PrintWriter pw = new PrintWriter(new File("plugins/snipers.txt"));

                    pw.write("przerwap" + "\r\n");
                    pw.write("Ridgedog" + "\r\n");
                    pw.write("R4nD0mNameWithCapitalLettering" + "\r\n");
                    pw.write("Gavjenks" + "\r\n");
                    pw.write("giltwist" + "\r\n");

                    pw.close();
                } catch (Exception e) {
                }
            }
            Scanner snr = new Scanner(nf);
            snipers.clear();
            while (snr.hasNext()) {
                String st = snr.nextLine();
                snipers.add(st);
            }
            snr.close();
        } catch (Exception e) {
            VoxelSniper.log.warning("[VoxelSniper] Error while loading snipers.txt");
        }
    }

    public void readLiteSnipers() {
        try {
            File f = new File("plugins/VoxelSniper/LiteSnipers.txt");
            if (f.exists()) {
                Scanner snr = new Scanner(f);
                while (snr.hasNext()) {
                    String st = snr.nextLine();
                    liteSnipers.add(st);
                }
                snr.close();
            } else {
                f.getParentFile().mkdirs();
                f.createNewFile();
                VoxelSniper.log.info("[VoxelSniper] plugins/VoxelSniper/LiteSnipers.txt was missing and was created.");
            }
        } catch (Exception e) {
            VoxelSniper.log.warning("[VoxelSniper] Error while loading plugins/VoxelSniper/LiteSnipers.txt");
        }
    }

    public void saveConfig() {
        try {
            File f = new File("plugins/VoxelSniper/SniperConfig.txt");
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            PrintWriter pw = new PrintWriter(f);
            pw.write("#VoxelSniper config file v4." + CONFIG_VERSION + "\r\n");
            pw.write("#Add BLOCK Id's seperated by a ' , ' like so:\r\n");
            pw.write("#SniperLiteUnusableIds:7,8,9,10,11\r\n");
            pw.write("SniperLiteUnusableIds:10,11\r\n");
            pw.write("MaxLiteBrushSize:5\r\n");
            pw.write("SmiteVoxelFOXoffenders=false\r\n");
            pw.write("EnableVoxelFood=false\r\n");
            pw.close();
            VoxelSniper.log.info("[VoxelSniper] Config saved");
            loadConfig();
        } catch (Exception e) {
            VoxelSniper.log.warning("[VoxelSniper] Error while saving SniperConfig.txt");
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        try {
            File f = new File("plugins/VoxelSniper/SniperConfig.txt");
            if (f.exists()) {
                Scanner snr = new Scanner(f);
                if (snr.hasNext()) {
                    String nl = snr.nextLine();
                    if (nl.contains("v4.")) {
                        int v = Integer.parseInt(nl.split("v4.")[1]);
                        if (v != CONFIG_VERSION) {
                            VoxelSniper.log.info("[VoxelSniper] Updating Config file");
                            saveConfig();
                            return;
                        }
                    } else {
                        VoxelSniper.log.info("[VoxelSniper] Updating Config file");
                        saveConfig();
                        return;
                    }
                }
                while (snr.hasNext()) {
                    String str = snr.nextLine();
                    if (str.startsWith("#")) {
                        continue;
                    }
                    if (str.startsWith("SniperLiteUnusableIds")) {
                        String[] sp = str.split(":")[1].split(",");
                        for (String st : sp) {
                            liteRestricted.add(Integer.parseInt(st));
                        }
                    }
                    if (str.startsWith("MaxLiteBrushSize")) {
                        LITE_MAX_BRUSH = Integer.parseInt(str.split(":")[1]);
                    }
                    if (str.startsWith("SmiteVoxelFOXoffenders")) {
                        SMITE_VOXELFOX_OFFENDERS = Boolean.parseBoolean(str.split("=")[1]);
                    }
                    if (str.startsWith("EnableVoxelFood")) {
                        VOXEL_FOOD = Boolean.parseBoolean(str.split("=")[1]);
                    }
                }
                if (SMITE_VOXELFOX_OFFENDERS) {
                    VoxelSniper.log.info("[VoxelSniper] VoxelFOX offender will be Smite with lightning!");
                }
                if (VOXEL_FOOD) {
                    VoxelSniper.log.info("[VoxelSniper] VoxelFood is activated!");
                }
                snr.close();
                VoxelSniper.log.info("[VoxelSniper] Config loaded");
            } else {
                saveConfig();
            }
        } catch (Exception e) {
            VoxelSniper.log.warning("[VoxelSniper] Error while loading SniperConfig.txt");
            e.printStackTrace();
        }
    }

    public void runFoodTimer(final Player p) {
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                if (voxelFood.contains(p)) {
                    voxelFood.remove(p);
                } else {
                    System.out.println("Fatal error has ocurred with VoxelFood.");
                }
            }
        }, 1800L);
    }
}
