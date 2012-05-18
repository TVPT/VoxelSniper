package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.Sneak;
import com.thevoxelbox.voxelsniper.brush.Snipe;
import com.thevoxelbox.voxelsniper.brush.perform.Performer;
import com.thevoxelbox.voxelsniper.brush.tool.BrushTool;
import com.thevoxelbox.voxelsniper.brush.tool.SneakBrushTool;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

/**
 *
 * @author Piotr
 */
public class vSniper {

    public static int UNDO_CACHE_SIZE = 20;
    public Brush readingBrush;
    public String readingString;
    public int pieceSize = 5000; //for CPU throttling purposes.  Needs to be set by the config file later (NOT the sniper himself), but just testing for now.
    public Player p;
    // 
    protected vData data = new vData(this);
    public vMessage vm;
    //
    //
    public boolean lightning = false;
    public boolean printout = true; //if false, will suppress many types of common, spammy vmessages. -Gav
    public boolean distRestrict = false;
    public double range = 5.0D;
    //
    //
    protected LinkedList<vUndo> undoList = new LinkedList<vUndo>();
    protected HashMap<String, Brush> myBrushes;
    protected HashMap<String, String> brushAlt;
    protected EnumMap<Material, BrushTool> brushTools = new EnumMap<Material, BrushTool>(Material.class);
    protected HashMap<Integer, Brush> brushPresets = new HashMap();
    protected HashMap<Integer, int[]> brushPresetsParams = new HashMap();
    protected HashMap<String, Brush> brushPresetsS = new HashMap();
    protected HashMap<String, int[]> brushPresetsParamsS = new HashMap();
    //activated
    protected Brush current = new Snipe();
    protected Brush previous = new Snipe();
    protected Brush twoBack = new Snipe();
    protected Brush sneak = new Sneak();
    // VOXELGUEST HOOKS
    protected Integer group;

    public vSniper() {
        myBrushes = vBrushes.getSniperBrushes();
        brushAlt = vBrushes.getBrushAlternates();

        vm = new vMessage(data);
        data.vm = vm;
        //defaults
        int[] currentP = new int[8];
        currentP[0] = 0;
        currentP[1] = 0;
        currentP[2] = 0;
        currentP[3] = 3;
        currentP[4] = 1;
        currentP[5] = 0;
        brushPresetsParamsS.put("current@", currentP);
        brushPresetsParamsS.put("previous@", currentP);
        brushPresetsParamsS.put("twoBack@", currentP);
        brushPresetsS.put("current@", myBrushes.get("s"));
        brushPresetsS.put("previous@", myBrushes.get("s"));
        brushPresetsS.put("twoBack@", myBrushes.get("s"));
    }

    public void setBrushSize(int size) {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.data.brushSize = size;
            bt.data.vm.size();
        } else {
            data.brushSize = size;
            vm.size();
        }
    }

    public void setVoxel(int voxel) {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.data.voxelId = voxel;
            bt.data.vm.voxel();
        } else {
            data.voxelId = voxel;
            vm.voxel();
        }
    }

    public void setReplace(int replace) {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.data.replaceId = replace;
            bt.data.vm.replace();
        } else {
            data.replaceId = replace;
            vm.replace();
        }
    }

    public void setData(byte dat) {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.data.data = dat;
            bt.data.vm.data();
        } else {
            data.data = dat;
            vm.data();
        }
    }

    public void setReplaceData(byte dat) {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.data.replaceData = dat;
            bt.data.vm.replaceData();
        } else {
            data.replaceData = dat;
            vm.replaceData();
        }
    }

    public void addVoxelToList(int i) {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.data.voxelList.add(i);
            bt.data.vm.voxelList();
        } else {
            data.voxelList.add(i);
            vm.voxelList();
        }
    }

    public void removeVoxelFromList(int i) {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.data.voxelList.removeValue(i);
            bt.data.vm.voxelList();
        } else {
            data.voxelList.removeValue(i);
            vm.voxelList();
        }
    }

    public void clearVoxelList() {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.data.voxelList.clear();
            bt.data.vm.voxelList();
        } else {
            data.voxelList.clear();
            vm.voxelList();
        }
    }

    public void setHeigth(int heigth) {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.data.voxelHeight = heigth;
            bt.data.vm.height();
        } else {
            data.voxelHeight = heigth;
            vm.height();
        }
    }

    public void setCentroid(int centroid) {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.data.cCen = centroid;
            bt.data.vm.center();
        } else {
            data.cCen = centroid;
            vm.center();
        }
    }

    public void setRange(double rng) {
        if (rng > -1) {
            range = rng;
            distRestrict = true;
            vm.toggleRange();
        } else {
            distRestrict = !distRestrict;
            vm.toggleRange();
        }
    }

    public void toggleLightning() {
        lightning = !lightning;
        vm.toggleLightning();
    }

    public void togglePrintout() {
        printout = !printout;
        vm.togglePrintout();
    }

    public void addBrushTool() {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            p.sendMessage(ChatColor.DARK_GREEN + "Brush tool already exists!");
        } else {
            brushTools.put(p.getItemInHand().getType(), new BrushTool(this));
            p.sendMessage(ChatColor.GOLD + "Brush tool has been added.");
        }
    }

    public void addBrushTool(boolean arrow) {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            p.sendMessage(ChatColor.DARK_GREEN + "Brush tool already exists!");
        } else {
            brushTools.put(p.getItemInHand().getType(), new SneakBrushTool(this, arrow));
            p.sendMessage(ChatColor.GOLD + "Brush tool has been added.");
        }
    }

    public void removeBrushTool() {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            brushTools.remove(p.getItemInHand().getType());
            p.sendMessage(ChatColor.GOLD + "Brush tool has been removed.");
        } else {
            p.sendMessage(ChatColor.DARK_GREEN + "Brush tool is non-existant!");
        }
    }

    public void setPerformer(String[] args) {
        String[] derp = new String[args.length + 1];
        derp[0] = "";
        System.arraycopy(args, 0, derp, 1, args.length);
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.setPerformer(derp);
        } else {
            if (current instanceof Performer) {
                ((Performer) current).parse(derp, data);
            } else {
                vm.custom(ChatColor.GOLD + "This brush is not a Performer brush!");
            }
        }
    }

    public boolean setBrush(String[] args) {
        try {
            if (args == null || args.length == 0) {
                p.sendMessage(ChatColor.RED + "Invalid input!");  //This was spamming the console and people were complaining.  Fixed below with a catch instead.  -GJ
                return false;
            }
            if (myBrushes.containsKey(args[0])) {
                if (brushTools.containsKey(p.getItemInHand().getType())) {
                    BrushTool bt = brushTools.get(p.getItemInHand().getType());
                    bt.setBrush(vBrushes.getBrushInstance(args[0]));
                } else {
                    //parameters:
                    brushPresetsParamsS.put("twoBack@", brushPresetsParamsS.get("previous@"));
                    fillPrevious(); //there are no current parameters yet, you just declared the brush / haven't input your new params.

                    twoBack = previous;
                    previous = current;
                    current = myBrushes.get(args[0]);
                }
            } else if (brushAlt.containsKey(args[0])) {
                if (brushTools.containsKey(p.getItemInHand().getType())) {
                    BrushTool bt = brushTools.get(p.getItemInHand().getType());
                    bt.setBrush(vBrushes.getBrushInstance(args[0]));
                } else {
                    //parameters:
                    brushPresetsParamsS.put("twoBack@", brushPresetsParamsS.get("previous@"));
                    fillPrevious();

                    twoBack = previous;
                    previous = current;
                    current = myBrushes.get(brushAlt.get(args[0]));
                }
            } else {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "No such brush.");
                return false;
            }

            args = parseParams(args);

            if (args.length > 1) {
                try {
                    if (brushTools.containsKey(p.getItemInHand().getType())) {
                        BrushTool bt = brushTools.get(p.getItemInHand().getType());
                        bt.parse(args);
                    } else {
                        if (current instanceof Performer) {
                            ((Performer) current).parse(args, data);
                        } else {
                            current.parameters(args, data);
                        }
                    }
                    return true;
                } catch (Exception e) {
                    p.sendMessage(ChatColor.RED + "Invalid parameters! (Parameter error)");
                    p.sendMessage(ChatColor.DARK_PURPLE + "" + fromArgs(args));
                    p.sendMessage(ChatColor.RED + "Is not a valid statement");
                    p.sendMessage(ChatColor.DARK_BLUE + "" + e.getMessage());
                    VoxelSniper.log.warning("[VoxelSniper] Exception while receiving parameters: \n(" + p.getName() + " " + current.name + ") par[ " + fromArgs(args) + "]");
                    VoxelSniper.log.log(Level.SEVERE, null, e);
                    return false;
                }
            }
            info();
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            p.sendMessage(ChatColor.RED + "Invalid input!");
            e.printStackTrace();
            return false;
        }
    }

    private String fromArgs(String[] args) {
        String str = "";
        for (String st : args) {
            str += st + " ";
        }
        return str;
    }

    private String[] parseParams(String[] args) {
        boolean[] toremove = new boolean[args.length];
        if (args.length > 1) {
            for (int x = 1; x < args.length; x++) {
                String str = args[x];
                if (str.startsWith("-") && str.length() > 1) {
                    switch (str.charAt(1)) {

                        case 'b':
                            try {
                                int i = Integer.parseInt(str.substring(2));
                                setBrushSize(i);
                                toremove[x] = true;
                            } catch (Exception e) {
                                p.sendMessage(ChatColor.RED + args[x] + " is Not a valid parameter!");
                            }
                            break;

                        case 'r':
                            try {
                                if (str.length() == 2) {
                                    setRange(-1);
                                } else {
                                    setRange(Double.parseDouble(str.substring(2)));
                                }
                                toremove[x] = true;
                            } catch (Exception e) {
                                p.sendMessage(ChatColor.RED + args[x] + " is Not a valid parameter!");
                            }
                            break;

                        case 'l':
                            toggleLightning();
                            toremove[x] = true;
                            break;

                        case 'e':
                            p.chat("/ve " + str.substring(2));
                            break;
                    }
                }
            }
        }
        int i = 0;
        for (boolean b : toremove) {
            if (b) {
                i++;
            }
        }
        if (i == 0) {
            return args;
        }
        String[] temp = new String[args.length - i];
        i = 0;
        for (int x = 0; x < args.length; x++) {
            if (!toremove[x]) {
                temp[i++] = args[x];
            }
        }
        return temp;
    }

    //writes parameters to the current key in the hashmap
    public void fillCurrent() {
        int[] currentP = new int[8];
        currentP[0] = data.voxelId;
        currentP[1] = data.replaceId;
        currentP[2] = data.data;
        currentP[3] = data.brushSize;
        currentP[4] = data.voxelHeight;
        currentP[5] = data.cCen;
        currentP[6] = data.replaceData;
        currentP[7] = (int) range;
        brushPresetsParamsS.put("current@", currentP);
    }

    //writes parameters of the last brush you were working with to the previous key in the hashmap
    public void fillPrevious() {
        int[] currentP = new int[8];
        currentP[0] = data.voxelId;
        currentP[1] = data.replaceId;
        currentP[2] = data.data;
        currentP[3] = data.brushSize;
        currentP[4] = data.voxelHeight;
        currentP[5] = data.cCen;
        currentP[6] = data.replaceData;
        currentP[7] = (int) range;
        brushPresetsParamsS.put("previous@", currentP);
    }

    //reads parameters from the current key in the hashmap
    private void readCurrent() {
        int[] currentP = brushPresetsParamsS.get("current@");
        data.voxelId = currentP[0];
        data.replaceId = currentP[1];
        data.data = (byte) currentP[2];
        data.brushSize = currentP[3];
        data.voxelHeight = currentP[4];
        data.cCen = currentP[5];
        data.replaceData = (byte) currentP[6];
        range = currentP[7];
    }

    //reads parameters from the previous key in the hashmap
    private void readPrevious() {
        int[] currentP = brushPresetsParamsS.get("previous@");
        data.voxelId = currentP[0];
        data.replaceId = currentP[1];
        data.data = (byte) currentP[2];
        data.brushSize = currentP[3];
        data.voxelHeight = currentP[4];
        data.cCen = currentP[5];
        data.replaceData = (byte) currentP[6];
        range = currentP[7];
    }

    private void readTwoBack() {
        int[] currentP = brushPresetsParamsS.get("twoBack@");
        data.voxelId = currentP[0];
        data.replaceId = currentP[1];
        data.data = (byte) currentP[2];
        data.brushSize = currentP[3];
        data.voxelHeight = currentP[4];
        data.cCen = currentP[5];
        data.replaceData = (byte) currentP[6];
        this.range = currentP[7];
    }

    public boolean snipe(Player playr, Action action, Material itemInHand, Block clickedBlock, BlockFace clickedFace) {
        boolean bool = false;
        try {
            p = playr;
            if (brushTools.containsKey(p.getItemInHand().getType())) {
                BrushTool bt = brushTools.get(p.getItemInHand().getType());
                bool = bt.snipe(playr, action, itemInHand, clickedBlock, clickedFace);
            } else {
                if (p.isSneaking()) {
                    bool = sneak.perform(action, data, itemInHand, clickedBlock, clickedFace);
                    return bool;
                }

                bool = current.perform(action, data, itemInHand, clickedBlock, clickedFace);
            }
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "An Exception has occured! (Sniping error)");
            p.sendMessage(ChatColor.RED + "" + e.toString());
            StackTraceElement[] ste = e.getStackTrace();
            for (StackTraceElement se : ste) {
                p.sendMessage(ChatColor.DARK_GRAY + se.getClassName() + ChatColor.DARK_GREEN + " : " + ChatColor.DARK_GRAY + se.getLineNumber());
            }
            VoxelSniper.log.warning("[VoxelSniper] Exception while sniping: (" + p.getName() + " " + current.name + ")");
            VoxelSniper.log.log(Level.SEVERE, null, e);
            return false;
        }
        return bool;
    }

    public void storeUndo(vUndo undo) {
        if (UNDO_CACHE_SIZE <= 0) {
            return;
        }
        if (undo != null && undo.getSize() > 0) {
            while (undoList.size() > UNDO_CACHE_SIZE) {
                undoList.pop();
            }
            undoList.add(undo);
        }
    }

    public void doUndo() {
        if (undoList.isEmpty()) {
            p.sendMessage(ChatColor.GREEN + "Nothing to undo");
        } else {
            vUndo undo = undoList.pollLast();
            if (undo != null) {
                undo.undo();
                p.sendMessage(ChatColor.GREEN + "Undo succesfull!  " + ChatColor.RED + undo.getSize() + ChatColor.GREEN + "  Blocks have been replaced.");
            } else {
                p.sendMessage(ChatColor.GREEN + "Nothing to undo");
            }
        }
    }

    public void doUndo(int num) {
        int sum = 0;
        if (undoList.isEmpty()) {
            p.sendMessage(ChatColor.GREEN + "Nothing to undo");
        } else {
            for (int x = 0; x < num; x++) {
                vUndo undo = undoList.pollLast();
                if (undo != null) {
                    undo.undo();
                    sum += undo.getSize();
                } else {
                    break;
                }
            }
            p.sendMessage(ChatColor.GREEN + "Undo succesfull!  " + ChatColor.RED + sum + ChatColor.GREEN + "  Blocks have been replaced.");
        }
    }

    public void info() {
        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.info();
        } else {
            current.info(vm);
            if (current instanceof Performer) {
                ((Performer) current).showInfo(vm);
            }
        }
    }

    public void printBrushes() {
        String msg = ChatColor.GREEN + "Available brush short-names: /b ";
        for (String brushName : myBrushes.keySet()) {
            msg += ChatColor.GREEN + " | " + ChatColor.BLUE + brushName;
        }
        p.sendMessage(msg);
    }

    public void printBrushesLong() {
        String msg = ChatColor.GREEN + "Available brush long-names: /b ";
        for (String brushName : brushAlt.keySet()) {
            msg += ChatColor.GREEN + " | " + ChatColor.BLUE + brushName;
        }
        p.sendMessage(msg);
    }

    public void previousBrush() {
        Brush temp = current;
        current = previous;
        previous = temp;

        fillCurrent();
        readPrevious();
        brushPresetsParamsS.put("previous@", brushPresetsParamsS.get("current@"));
        fillCurrent();
        info();
    }

    public void twoBackBrush() {
        fillCurrent();
        Brush temp = current;
        Brush tempTwo = previous;
        current = twoBack;
        previous = temp;
        twoBack = tempTwo;

        fillCurrent();
        readTwoBack();
        brushPresetsParamsS.put("twoBack@", brushPresetsParamsS.get("previous@"));
        brushPresetsParamsS.put("previous@", brushPresetsParamsS.get("current@"));
        fillCurrent();

        info();
    }

    public void savePreset(int slot) {
        brushPresets.put(slot, current);
        fillCurrent();
        brushPresetsParams.put(slot, brushPresetsParamsS.get("current@"));
        saveAllPresets();
        p.sendMessage(ChatColor.AQUA + "Preset saved in slot " + slot);
    }

    public void savePreset(String slot) { //string version
        brushPresetsS.put(slot, current);
        fillCurrent();
        brushPresetsParamsS.put(slot, brushPresetsParamsS.get("current@"));
        saveAllPresets();
        p.sendMessage(ChatColor.AQUA + "Preset saved in slot " + slot);
    }

    public void loadPreset(int slot) {
        try {
            int[] paramArray = brushPresetsParams.get(slot);

            Brush temp = brushPresets.get(slot);
            if (temp != current) {
                twoBack = previous;
                previous = current;
                current = temp;
            }
            fillPrevious();
            data.voxelId = paramArray[0];
            data.replaceId = paramArray[1];
            data.data = (byte) paramArray[2];
            data.brushSize = paramArray[3];
            data.voxelHeight = paramArray[4];
            data.cCen = paramArray[5];
            data.replaceData = (byte) paramArray[6];
            range = paramArray[7];
            setPerformer(new String[]{"", "m"});

            p.sendMessage("Preset loaded.");
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Preset is empty.  Cannot load.");
            e.printStackTrace();
        }
    }

    public void loadPreset(String slot) {
        try {
            int[] paramArray = brushPresetsParamsS.get(slot);

            Brush temp = brushPresetsS.get(slot);
            if (temp != current) {
                twoBack = previous;
                previous = current;
                current = temp;
            }
            fillPrevious();
            data.voxelId = paramArray[0];
            data.replaceId = paramArray[1];
            data.data = (byte) paramArray[2];
            data.brushSize = paramArray[3];
            data.voxelHeight = paramArray[4];
            data.cCen = paramArray[5];
            data.replaceData = (byte) paramArray[6]; // Noticed this was missing - Giltwist
            range = paramArray[7];
            setPerformer(new String[]{"", "m"});

            p.sendMessage("Preset loaded.");
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Preset is empty.  Cannot load.");
            e.printStackTrace();
        }
    }

    public void reset() {
        if (this instanceof liteSniper) {
            myBrushes = liteBrushes.getSniperBrushes();
        } else {
            myBrushes = vBrushes.getSniperBrushes();
        }

        if (brushTools.containsKey(p.getItemInHand().getType())) {
            BrushTool bt = brushTools.get(p.getItemInHand().getType());
            bt.setBrush(new Snipe());

            bt.data.voxelId = 0;
            bt.data.replaceId = 0;
            bt.data.data = 0;
            bt.data.brushSize = 3;
            bt.data.voxelHeight = 1;
            bt.data.cCen = 0;
            bt.data.replaceData = 0;
        } else {
            current = new Snipe();

            fillPrevious();
            data.voxelId = 0;
            data.replaceId = 0;
            data.data = 0;
            data.brushSize = 3;
            data.voxelHeight = 1;
            data.cCen = 0;
            data.replaceData = 0; // Noticed this was missing - Giltwist
            range = 1;

            //Is it possible to set the performer of every brush simultaneously or would
            //there need to be a for loop? - Giltwist
        }
        fillCurrent();
    }

    public void saveAllPresets() {
        String location = "plugins/VoxelSniper/presetsBySniper/" + p.getName() + ".txt";
        File nf = new File(location);
        //if (!nf.exists()) {
        nf.getParentFile().mkdirs();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(location);
            int[] presetsHolder = new int[8];
            Iterator it = brushPresets.keySet().iterator();
            if (!brushPresets.isEmpty()) {
                while (it.hasNext()) {
                    int i = (Integer) it.next();
                    writer.write(i + "\r\n" + brushPresets.get(i).name + "\r\n");
                    presetsHolder = brushPresetsParams.get(i);
                    writer.write(presetsHolder[0] + "\r\n");
                    writer.write(presetsHolder[1] + "\r\n");
                    writer.write(presetsHolder[2] + "\r\n");
                    writer.write(presetsHolder[3] + "\r\n");
                    writer.write(presetsHolder[4] + "\r\n");
                    writer.write(presetsHolder[5] + "\r\n");
                    writer.write(presetsHolder[6] + "\r\n");
                    writer.write(presetsHolder[7] + "\r\n");
                }
            }
            it = brushPresetsS.keySet().iterator();
            if (!brushPresetsS.isEmpty()) {
                while (it.hasNext()) {
                    String key = (String) it.next();
                    if (!key.startsWith("current") && !key.startsWith("previous") && !key.startsWith("twoBack")) {
                        writer.write(key + "\r\n" + brushPresetsS.get(key).name + "\r\n");
                        presetsHolder = brushPresetsParamsS.get(key);
                        writer.write(presetsHolder[0] + "\r\n");
                        writer.write(presetsHolder[1] + "\r\n");
                        writer.write(presetsHolder[2] + "\r\n");
                        writer.write(presetsHolder[3] + "\r\n");
                        writer.write(presetsHolder[4] + "\r\n");
                        writer.write(presetsHolder[5] + "\r\n");
                        writer.write(presetsHolder[6] + "\r\n");
                        writer.write(presetsHolder[7] + "\r\n");
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
    }

    public void loadAllPresets() {
        try {
            File f = new File("plugins/VoxelSniper/presetsBySniper/" + p.getName() + ".txt");
            if (f.exists()) {
                Scanner snr = new Scanner(f);
                int[] presetsHolder = new int[8];
                while (snr.hasNext()) {
                    try {
                        readingString = snr.nextLine();
                        int key = Integer.parseInt(readingString);
                        readingBrush = myBrushes.get(snr.nextLine());
                        brushPresets.put(key, readingBrush);
                        presetsHolder[0] = Integer.parseInt(snr.nextLine());
                        presetsHolder[1] = Integer.parseInt(snr.nextLine());
                        presetsHolder[2] = Byte.parseByte(snr.nextLine());
                        presetsHolder[3] = Integer.parseInt(snr.nextLine());
                        presetsHolder[4] = Integer.parseInt(snr.nextLine());
                        presetsHolder[5] = Integer.parseInt(snr.nextLine());
                        presetsHolder[6] = Byte.parseByte(snr.nextLine());
                        presetsHolder[7] = Integer.parseInt(snr.nextLine());
                        brushPresetsParams.put(key, presetsHolder);
                    } catch (NumberFormatException e) {
                        boolean first = true;
                        while (snr.hasNext()) {
                            String keyS;
                            if (first) {
                                keyS = readingString;
                                first = false;
                            } else {
                                keyS = snr.nextLine();
                            }
                            readingBrush = myBrushes.get(snr.nextLine());
                            brushPresetsS.put(keyS, readingBrush);
                            presetsHolder[0] = Integer.parseInt(snr.nextLine());
                            presetsHolder[1] = Integer.parseInt(snr.nextLine());
                            presetsHolder[2] = Byte.parseByte(snr.nextLine());
                            presetsHolder[3] = Integer.parseInt(snr.nextLine());
                            presetsHolder[4] = Integer.parseInt(snr.nextLine());
                            presetsHolder[5] = Integer.parseInt(snr.nextLine());
                            presetsHolder[6] = Byte.parseByte(snr.nextLine());
                            presetsHolder[7] = Integer.parseInt(snr.nextLine());
                            brushPresetsParamsS.put(keyS, presetsHolder);

                        }
                    }
                }
                snr.close();
            }
        } catch (Exception f) {
            f.printStackTrace();
        }
    }
}
