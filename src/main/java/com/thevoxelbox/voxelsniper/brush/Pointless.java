package com.thevoxelbox.voxelsniper.brush;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

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

    private static int timesUsed = 0;

    public Pointless() {
        this.setName("Mind-Numbingly Pointless");
    }

    // !NameDerpLowercase
    // @ .( * .
    // @ . * . ) .
    // @ . . &6POOF&9 .* .
    // @ ' * . ( .) '
    // @ ` ( . *
    // #
    public final void addToPrintouts() {
        Pointless.printouts.put("dachshund", new String[] { "             .--.", " (_______(]6 `-,", " (   ____    /''\"`", " //\\\\   //\\\\",
                " \"\"  \"\"  \"\"  \"\"" });
        Pointless.printouts.put("sunset", new String[] { "                           ~,  ^^                       |          ",
                "                           /|    ^^                  \\ _ /        ", "                          / |\\                    -=  ( )  =-     ",
                " ~^~ ^ ^~^~ ~^~ ~=====^~^~-~^~~^~^-=~=~=-~^~^~^~" });
        Pointless.printouts.put("poof", new String[] { "      .( * .", "    . *  .  ) .", "   . . POOF .* .", "    ' * . (  .) '", "     ` ( . *" });
        Pointless.printouts.put("dog", new String[] { "   |\\_/|", "   |^ ^|      /}", "   ( 0 )\"\"\"\\'", "  8===8     |", "   ||_/=\\\\__|" });
        Pointless.printouts.put("voxelbox", new String[] { "                             §c_ _               ",
                "§a /\\   /\\§b___§6__  __§c___| | |__   _____  __", "§a \\ \\ / /§b  _ \\§6 \\/ /§c _ \\ | '_ \\ / _ \\ \\/ /",
                "§a  \\ V /§b  (_) §6>   <§c  __/ |   |_) | (_)  >   < ", "§a   \\_/ §b\\___/§6_/\\_\\§c__|_|_.__/ \\___/_/\\_\\" });
    }

    @Override
    public final int getTimesUsed() {
        return Pointless.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Pointless brushiness! :D :");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].equals("broadcast")) {
                this.broadcastIt = !this.broadcastIt;
                v.sendMessage("Broadcast mode: " + this.broadcastIt);
                break;
            } else if (par[x].equals("pwderp")) {
                this.password = true;
                break;
            } else {
                this.selection = par[x];
            }
        }
    }

    /**
     * 
     * @param v
     */
    public final void printIt(final SnipeData v) {
        if (Pointless.printouts.containsKey(this.selection)) {
            for (final String i : Pointless.printouts.get(this.selection)) {
                this.printLine(v, i);
            }
        } else {
            v.sendMessage(ChatColor.RED + "Sorry, this printout does not exist.");
            // for(String i : printouts.get(selection)) {
            // server.broadcastMessage("- " + i);
            // }
            for (final String i : Pointless.printouts.keySet()) {
                this.server.broadcastMessage("--- " + i);
            }
        }
    }

    public final void printLine(final SnipeData v, final String line) {
        // This will eventually parse everything for the colors before printing. Now I just want to get it out there.
        if (this.broadcastIt) {
            this.server.broadcastMessage(line);
        } else {
            v.sendMessage(line);
        }
    }

    public final void readPrintouts() {
        try {
            final File f = new File("plugins/VoxelSniper/lulz.txt");
            if (!f.exists()) {
                // v.sendMessage("Sorry, no file to load from. Can't use this one."); // Should log it instead
                return;
            }
            final Scanner scnnr = new Scanner(f);

            String curPrintout = new String();
            // ArrayList curLines = new ArrayList(2);
            String[] curLines = new String[1];

            while (scnnr.hasNext()) {
                final String nextLine = scnnr.nextLine();
                if (nextLine == null) {
                    continue;
                }

                this.server.broadcastMessage("0");

                if (nextLine.startsWith("!")) {
                    // Printout name
                    curPrintout = nextLine.substring(1);
                    this.server.broadcastMessage("1");
                    continue;
                } else if (nextLine.startsWith("@")) {
                    // Printout lines
                    // curLines.add(nextLine.substring(1));
                    this.server.broadcastMessage("curLines[0]" + curLines[0]);
                    if (curLines[0] != null) {
                        this.server.broadcastMessage("if null - " + curPrintout);
                        final String[] tempLines = new String[curLines.length + 1];
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
                    this.server.broadcastMessage("derp - " + curPrintout);
                    for (final String i : curLines) {
                        this.server.broadcastMessage("derpus - " + i);
                    }

                    Pointless.printouts.put(curPrintout, curLines);
                    curPrintout = "";
                    curLines = new String[1];
                    this.server.broadcastMessage("3");
                    continue;
                }
            }
            scnnr.close();
        } catch (final Exception e) {
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Pointless.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (this.password) {

            if (!this.loadedPrintouts) {
                this.addToPrintouts();
                // readPrintouts();
                this.loadedPrintouts = true;
            }

            this.printIt(v);
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.arrow(v);
    }
}
