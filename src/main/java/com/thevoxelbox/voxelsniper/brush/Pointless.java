package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;

/**
 * 
 * @author Mick
 */
public class Pointless extends Brush {

    Server server = Bukkit.getServer();
    private boolean broadcastIt = false;
    private boolean loadedPrintouts = false;
    public static HashMap<String, String[]> printouts = new HashMap();
    private String selection = "voxelbox";
    private boolean password = false;

    public Pointless() {
        name = "Mind-Numbingly Pointless";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        if (password) {

            if (!loadedPrintouts) {
                addToPrintouts();
                // readPrintouts();
                loadedPrintouts = true;
            }

            printIt(v);
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Pointless brushiness! :D :");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].equals("broadcast")) {
                broadcastIt = !broadcastIt;
                v.sendMessage("Broadcast mode: " + broadcastIt);
                break;
            } else if (par[x].equals("pwderp")) {
                password = true;
                break;
            } else {
                selection = par[x];
            }
        }
    }

    /**
     * 
     * @param v
     */
    public void printIt(vData v) {
        if (printouts.containsKey(selection)) {
            for (String i : printouts.get(selection)) {
                printLine(v, i);
            }
        } else {
            v.sendMessage(ChatColor.RED + "Sorry, this printout does not exist.");
            // for(String i : printouts.get(selection)) {
            // server.broadcastMessage("- " + i);
            // }
            for (String i : printouts.keySet()) {
                server.broadcastMessage("--- " + i);
            }
        }
    }

    public void printLine(vData v, String line) {
        // This will eventually parse everything for the colors before printing. Now I just want to get it out there.
        if (broadcastIt) {
            server.broadcastMessage(line);
        } else {
            v.sendMessage(line);
        }
    }

    // !NameDerpLowercase
    // @ .( * .
    // @ . * . ) .
    // @ . . &6POOF&9 .* .
    // @ ' * . ( .) '
    // @ ` ( . *
    // #
    public void addToPrintouts() {
        printouts.put("dachshund",
                new String[] { "             .--.", " (_______(]6 `-,", " (   ____    /''\"`", " //\\\\   //\\\\", " \"\"  \"\"  \"\"  \"\"" });
        printouts.put("sunset", new String[] { "                           ~,  ^^                       |          ",
                "                           /|    ^^                  \\ _ /        ", "                          / |\\                    -=  ( )  =-     ",
                " ~^~ ^ ^~^~ ~^~ ~=====^~^~-~^~~^~^-=~=~=-~^~^~^~" });
        printouts.put("poof", new String[] { "      .( * .", "    . *  .  ) .", "   . . POOF .* .", "    ' * . (  .) '", "     ` ( . *" });
        printouts.put("dog", new String[] { "   |\\_/|", "   |^ ^|      /}", "   ( 0 )\"\"\"\\'", "  8===8     |", "   ||_/=\\\\__|" });
        printouts.put("voxelbox", new String[] { "                             §c_ _               ", "§a /\\   /\\§b___§6__  __§c___| | |__   _____  __",
                "§a \\ \\ / /§b  _ \\§6 \\/ /§c _ \\ | '_ \\ / _ \\ \\/ /", "§a  \\ V /§b  (_) §6>   <§c  __/ |   |_) | (_)  >   < ",
                "§a   \\_/ §b\\___/§6_/\\_\\§c__|_|_.__/ \\___/_/\\_\\" });
    }

    public void readPrintouts() {
        try {
            File f = new File("plugins/VoxelSniper/lulz.txt");
            if (!f.exists()) {
                // v.sendMessage("Sorry, no file to load from. Can't use this one."); // Should log it instead
                return;
            }
            Scanner scnnr = new Scanner(f);

            String curPrintout = new String();
            // ArrayList curLines = new ArrayList(2);
            String[] curLines = new String[1];

            while (scnnr.hasNext()) {
                String nextLine = scnnr.nextLine();
                if (nextLine == null) {
                    continue;
                }

                server.broadcastMessage("0");

                if (nextLine.startsWith("!")) {
                    // Printout name
                    curPrintout = nextLine.substring(1);
                    server.broadcastMessage("1");
                    continue;
                } else if (nextLine.startsWith("@")) {
                    // Printout lines
                    // curLines.add(nextLine.substring(1));
                    server.broadcastMessage("curLines[0]" + curLines[0]);
                    if (curLines[0] != null) {
                        server.broadcastMessage("if null - " + curPrintout);
                        String[] tempLines = new String[curLines.length + 1];
                        System.arraycopy(curLines, 0, tempLines, 0, curLines.length);
                        tempLines[curLines.length + 1] = nextLine.substring(1);
                        curLines = tempLines;
                    } else {
                        curLines[0] = nextLine.substring(1);
                    }
                    continue;
                    // } else if (nextLine.startsWith("#")) {
                } else {
                    // Close
                    server.broadcastMessage("derp - " + curPrintout);
                    for (String i : curLines) {
                        server.broadcastMessage("derpus - " + i);
                    }

                    printouts.put(curPrintout, (String[]) curLines);
                    curPrintout = "";
                    curLines = new String[1];
                    server.broadcastMessage("3");
                    continue;
                }
            }
            scnnr.close();
        } catch (Exception e) {
        }
    }
}
