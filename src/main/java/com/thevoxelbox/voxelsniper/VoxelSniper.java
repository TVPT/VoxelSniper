package com.thevoxelbox.voxelsniper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import org.bukkit.ChatColor;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VoxelSniper extends JavaPlugin {

    private final VoxelSniperListener VSPlayer = new VoxelSniperListener(this);
    private final VoxelSniperEntity VSEntity = new VoxelSniperEntity();
    public static final Logger log = Logger.getLogger("Minecraft");
    protected static final Object itemLock = new Object();
    public static HashMap<String, Integer> items;
    public static Server s;
    public static HashSet<File> plugins = new HashSet<File>();
    public static VoxelSniper instance;
    
    @Override
    public void onDisable() {
    	
    }

    @Override
    public void onEnable() {
    	instance = this;
        loadItems();
        s = getServer();
        VSPlayer.initSnipers();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(VSPlayer, this);
        if (VoxelSniperListener.SMITE_VOXELFOX_OFFENDERS) {
            pm.registerEvents(VSEntity, this);
            log.info("[VoxelSniper] Entity Damage Event registered.");
        }

        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled! Snipe away.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String comm = command.getName();
            if (args == null) {
                if (!VoxelSniperListener.onCommand(p, new String[0], comm)) {
                    if (p.isOp()) {
                        p.sendMessage(ChatColor.RED + "Your name is not listed on the snipers.txt or you haven't /reload 'ed the server yet.");
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                if (!VoxelSniperListener.onCommand(p, args, comm)) {
                    if (p.isOp()) {
                        p.sendMessage(ChatColor.RED + "Your name is not listed on the snipers.txt or you haven't /reload 'ed the server yet.");
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            }
        }

        System.out.println("Not instanceof Player!");

        return false;
    }

    public void loadItems() {
        String location = "plugins/VoxelSniper/items.txt";
        File f = new File("items.txt");
        File nf = new File("plugins/VoxelSniper/items.txt");
        if (f.exists() && !nf.exists()) {
            f.delete();
        }

        if (!nf.exists()) {
            nf.getParentFile().mkdirs();
            FileWriter writer = null;
            try {
                writer = new FileWriter(location);
                writer.write("#Add your items in here (When adding your entry DO NOT include #!)\r\n");
                writer.write("#The format is:\r\n");
                writer.write("#NAME:ID\r\n");
                writer.write("#Default Items:\r\n");
                writer.write("air:0\r\n");
                writer.write("rock:1\r\n");
                writer.write("stone:1\r\n");
                writer.write("grass:2\r\n");
                writer.write("dirt:3\r\n");
                writer.write("cobblestone:4\r\n");
                writer.write("cobble:4\r\n");
                writer.write("wood:5\r\n");
                writer.write("sapling:6\r\n");
                writer.write("adminium:7\r\n");
                writer.write("bedrock:7\r\n");
                writer.write("water:8\r\n");
                writer.write("stillwater:9\r\n");
                writer.write("swater:9\r\n");
                writer.write("lava:10\r\n");
                writer.write("stilllava:11\r\n");
                writer.write("slava:11\r\n");
                writer.write("sand:12\r\n");
                writer.write("gravel:13\r\n");
                writer.write("goldore:14\r\n");
                writer.write("column:14\r\n");
                writer.write("ironore:15\r\n");
                writer.write("granite:15\r\n");
                writer.write("coalore:16\r\n");
                writer.write("limestone:14\r\n");
                writer.write("tree:17\r\n");
                writer.write("log:17\r\n");
                writer.write("leaves:18\r\n");
                writer.write("sponge:19\r\n");
                writer.write("glass:20\r\n");
                writer.write("lapisore:21\r\n");
                writer.write("bricklime:21\r\n");
                writer.write("lapisblock:22\r\n");
                writer.write("lapislazuli:22\r\n");
                writer.write("dispenser:23\r\n");
                writer.write("sandstone:24\r\n");
                writer.write("noteblock:25\r\n");
                writer.write("bed:26\r\n");
                writer.write("powerrail:27\r\n");
                writer.write("triggerrail:28\r\n");
                writer.write("stickypiston:29\r\n");
                writer.write("sticky:29\r\n");
                writer.write("web:30\r\n");
                writer.write("cobweb:30\r\n");
                writer.write("spiderweb:30\r\n");
                writer.write("wildgrass:31\r\n"); //1.6
                writer.write("tallgrass:31\r\n"); //1.6
                writer.write("shrub:32\r\n"); //1.6
                writer.write("deadshrub:32\r\n"); //1.6
                writer.write("piston:33\r\n");
                writer.write("pistonhead:34\r\n");
                writer.write("cloth:35\r\n");
                writer.write("wool:35\r\n");
                writer.write("void:36\r\n");
                writer.write("flower:37\r\n");
                writer.write("rose:38\r\n");
                writer.write("brownmushroom:39\r\n");
                writer.write("redmushroom:40\r\n");
                writer.write("gold:41\r\n");
                writer.write("goldblock:41\r\n");
                writer.write("iron:42\r\n");
                writer.write("ironblock:42\r\n");
                writer.write("doublestair:43\r\n");
                writer.write("stair:44\r\n");
                writer.write("step:44\r\n");
                writer.write("brickblock:45\r\n");
                writer.write("brickwall:45\r\n");
                writer.write("tnt:46\r\n");
                writer.write("bookshelf:47\r\n");
                writer.write("bookcase:47\r\n");
                writer.write("mossycobblestone:48\r\n");
                writer.write("mossy:48\r\n");
                writer.write("obsidian:49\r\n");
                writer.write("torch:50\r\n");
                writer.write("fire:51\r\n");
                writer.write("mobspawner:52\r\n");
                writer.write("woodstairs:53\r\n");
                writer.write("chest:54\r\n");
                writer.write("redstonedust:55\r\n");
                writer.write("redstonewire:55\r\n");
                writer.write("diamondore:56\r\n");
                writer.write("diamondblock:57\r\n");
                writer.write("copper2:56\r\n");
                writer.write("ocopper:56\r\n");
                writer.write("workbench:58\r\n");
                writer.write("crop:59\r\n");
                writer.write("crops:59\r\n");
                writer.write("soil:60\r\n");
                writer.write("furnace:61\r\n");
                writer.write("litfurnace:62\r\n");
                writer.write("signblock:63\r\n");
                writer.write("wooddoorblock:64\r\n");
                writer.write("ladder:65\r\n");
                writer.write("rails:66\r\n");
                writer.write("rail:66\r\n");
                writer.write("track:66\r\n");
                writer.write("tracks:66\r\n");
                writer.write("cobblestonestair:67\r\n");
                writer.write("stair:67\r\n");
                writer.write("signblocktop:68\r\n");
                writer.write("wallsign:68\r\n");
                writer.write("lever:69\r\n");
                writer.write("rockplate:70\r\n");
                writer.write("stoneplate:70\r\n");
                writer.write("irondoorblock:71\r\n");
                writer.write("woodplate:72\r\n");
                writer.write("redstoneore:73\r\n");
                writer.write("copper:73\r\n");
                writer.write("redstoneorealt:74\r\n");
                writer.write("redstonetorchoff:75\r\n");
                writer.write("redstonetorchon:76\r\n");
                writer.write("button:77\r\n");
                writer.write("snow:78\r\n");
                writer.write("snowtile:78\r\n");
                writer.write("ice:79\r\n");
                writer.write("snowblock:80\r\n");
                writer.write("cactus:81\r\n");
                writer.write("clayblock:82\r\n");
                writer.write("reedblock:83\r\n");
                writer.write("jukebox:84\r\n");
                writer.write("fence:85\r\n");
                writer.write("pumpkin:86\r\n");
                writer.write("monitor:86\r\n");
                writer.write("netherstone:87\r\n");
                writer.write("magmastone:87\r\n");
                writer.write("slowsand:88\r\n");
                writer.write("sludge:88\r\n");
                writer.write("lightstone:89\r\n");
                writer.write("glowstone:89\r\n");
                writer.write("lightstone:89\r\n");
                writer.write("portal:90\r\n");
                writer.write("jackolantern:91\r\n");
                writer.write("jacko:91\r\n");
                writer.write("monitoron:56\r\n");
                writer.write("cake:92\r\n");
                writer.write("repeateroff:93\r\n");
                writer.write("repeater:94\r\n");
                writer.write("chestlocked:95\r\n");
                writer.write("hatch:96\r\n"); //1.6
                writer.write("silverfish:97\r\n"); //1.6
                writer.write("stonebrick:98\r\n"); //1.8
                writer.write("shroombrown:99\r\n"); //1.8
                writer.write("shroomred:100\r\n"); //1.8
                writer.write("ironbars:101\r\n"); //1.8
                writer.write("glasspane:102\r\n"); //1.8
                writer.write("melon:103\r\n"); //1.8
                writer.write("pumpkinstem:104\r\n"); //1.8
                writer.write("stempumpkin:104\r\n"); //1.8
                writer.write("melonstem:105\r\n"); //1.8
                writer.write("stemmelon:105\r\n"); //1.8
                writer.write("vine:106\r\n"); //1.8
                writer.write("vines:106\r\n"); //1.8
                writer.write("fencegate:107\r\n"); //1.8
                writer.write("gate:107\r\n"); //1.8
                writer.write("brickstair:108\r\n"); //1.8
                writer.write("stonestair:109\r\n"); //1.8
                writer.write("mycel:110\r\n"); //1.0
                writer.write("industrial:110\r\n"); //1.0
                writer.write("lilypad:111\r\n"); //1.0
                writer.write("netherbrick:112\r\n"); //1.0
                writer.write("netherfence:113\r\n"); //1.0
                writer.write("netherstair:114\r\n"); //1.0
                writer.write("netherwart:115\r\n"); //1.0
                writer.write("enchanttable:116\r\n"); //1.0
                writer.write("brewstand:117\r\n"); //1.0
                writer.write("cauldron:118\r\n"); //1.0
                writer.write("brickstair:119\r\n"); //1.0
                writer.write("endportalframe:120\r\n"); //1.0
                writer.write("endstone:121\r\n"); //1.0
                writer.write("dragonegg:122\r\n"); //1.0
                writer.write("redstonelampoff:123\r\n");
                writer.write("redstonelampon:123\r\n");

            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception while creating " + location, e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        log.log(Level.SEVERE, "Exception while closing writer for " + location, e);
                    }
                }
            }
        }

        synchronized (itemLock) {
            items = new HashMap<String, Integer>();
            try {
                Scanner scanner = new Scanner(nf);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("#")) {
                        continue;
                    }
                    if (line.equals("")) {
                        continue;
                    }
                    String[] split = line.split(":");
                    VoxelSniper.items.put(split[0], Integer.parseInt(split[1]));
                }
                scanner.close();
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception while reading " + location
                        + " (Are you sure you formatted it correctly?)", e);
            }
        }
    }

    public static int getItem(String name) {
        synchronized (itemLock) {
            if (items.containsKey(name)) {
                return items.get(name);
            }
        }
        return -1;
    }

    public static String getItem(int id) {
        synchronized (itemLock) {
            for (String name : items.keySet()) {
                if (items.get(name) == id) {
                    return name;
                }
            }
        }
        return String.valueOf(id);
    }

    public static boolean isValidItem(int itemId) {
        return items.containsValue(itemId);
    }
}
