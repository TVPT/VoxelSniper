package com.thevoxelbox.voxelsniper;

import java.io.File;
import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.Sneak;
import com.thevoxelbox.voxelsniper.brush.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.perform.Performer;
import com.thevoxelbox.voxelsniper.brush.tool.BrushTool;
import com.thevoxelbox.voxelsniper.brush.tool.SneakBrushTool;

/**
 * 
 * @author Piotr
 */
public class Sniper {

    private static final int SAVE_ARRAY_SIZE = 8;
    private static final int SAVE_ARRAY_RANGE = 7;
    private static final int SAVE_ARRAY_REPLACE_DATA_VALUE = 6;
    private static final int SAVE_ARRAY_CENTROID = 5;
    private static final int SAVE_ARRAY_VOXEL_HEIGHT = 4;
    private static final int SAVE_ARRAY_BRUSH_SIZE = 3;
    private static final int SAVE_ARRAY_DATA_VALUE = 2;
    private static final int SAVE_ARRAY_REPLACE_VOXEL_ID = 1;
    private static final int SAVE_ARRAY_VOXEL_ID = 0;
    private static int undoChacheSize = 20;

    /**
     * @return int
     */
    public static int getUndoChacheSize() {
        return Sniper.undoChacheSize;
    }

    /**
     * @param undoChacheSize
     */
    public static void setUndoChacheSize(final int undoChacheSize) {
        Sniper.undoChacheSize = undoChacheSize;
    }

    private Brush readingBrush;
    private String readingString;

    private Player player;

    private SnipeData data = new SnipeData(this);

    private Message voxelMessage;

    private boolean lightning = false;
    /**
     * If false, will suppress many types of common, spammy vmessages.
     */
    private boolean printout = true;
    private boolean distRestrict = false;
    private double range = 5.0d;

    private final LinkedList<Undo> undoList = new LinkedList<Undo>();
    private HashMap<String, Brush> myBrushes;
    private HashMap<String, String> brushAlt;
    private final EnumMap<Material, BrushTool> brushTools = new EnumMap<Material, BrushTool>(Material.class);
    private final HashMap<Integer, Brush> brushPresets = new HashMap<Integer, Brush>();
    private final HashMap<Integer, int[]> brushPresetsParams = new HashMap<Integer, int[]>();
    private final HashMap<String, Brush> brushPresetsS = new HashMap<String, Brush>();
    private final HashMap<String, int[]> brushPresetsParamsS = new HashMap<String, int[]>();

    private Brush current = new SnipeBrush();
    private Brush previous = new SnipeBrush();
    private Brush twoBack = new SnipeBrush();
    private IBrush sneak = new Sneak();

    private Integer group;

    /**
     * Default constructor.
     */
    public Sniper() {
        this.myBrushes = SniperBrushes.getSniperBrushes();
        this.brushAlt = SniperBrushes.getBrushAlternates();

        this.voxelMessage = new Message(this.data);
        this.data.setVoxelMessage(this.voxelMessage);

        final int[] _currentP = new int[Sniper.SAVE_ARRAY_SIZE];
        _currentP[Sniper.SAVE_ARRAY_VOXEL_ID] = 0;
        _currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] = 0;
        _currentP[Sniper.SAVE_ARRAY_DATA_VALUE] = 0;
        _currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE] = 3;
        _currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] = 1;
        _currentP[Sniper.SAVE_ARRAY_CENTROID] = 0;
        this.brushPresetsParamsS.put("current@", _currentP);
        this.brushPresetsParamsS.put("previous@", _currentP);
        this.brushPresetsParamsS.put("twoBack@", _currentP);
        this.brushPresetsS.put("current@", this.myBrushes.get("s"));
        this.brushPresetsS.put("previous@", this.myBrushes.get("s"));
        this.brushPresetsS.put("twoBack@", this.myBrushes.get("s"));
    }

    /**
     * 
     */
    public final void addBrushTool() {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            this.player.sendMessage(ChatColor.DARK_GREEN + "Brush tool already exists!");
        } else {
            this.brushTools.put(this.player.getItemInHand().getType(), new BrushTool(this));
            this.player.sendMessage(ChatColor.GOLD + "Brush tool has been added.");
        }
    }

    /**
     * @param arrow
     */
    public final void addBrushTool(final boolean arrow) {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            this.player.sendMessage(ChatColor.DARK_GREEN + "Brush tool already exists!");
        } else {
            this.brushTools.put(this.player.getItemInHand().getType(), new SneakBrushTool(this, arrow));
            this.player.sendMessage(ChatColor.GOLD + "Brush tool has been added.");
        }
    }

    /**
     * @param i
     */
    public final void addVoxelToList(final int i) {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.data.getVoxelList().add(i);
            _bt.data.getVoxelMessage().voxelList();
        } else {
            this.data.getVoxelList().add(i);
            this.voxelMessage.voxelList();
        }
    }

    /**
     * 
     */
    public final void clearVoxelList() {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.data.getVoxelList().clear();
            _bt.data.getVoxelMessage().voxelList();
        } else {
            this.data.getVoxelList().clear();
            this.voxelMessage.voxelList();
        }
    }

    /**
     * 
     */
    public final void doUndo() {
        this.doUndo(1);
    }

    /**
     * @param num
     */
    public final void doUndo(final int num) {
        int _sum = 0;
        if (this.undoList.isEmpty()) {
            this.player.sendMessage(ChatColor.GREEN + "Nothing to undo");
        } else {
            for (int _x = 0; _x < num; _x++) {
                final Undo _undo = this.undoList.pollLast();
                if (_undo != null) {
                    _undo.undo();
                    _sum += _undo.getSize();
                } else {
                    break;
                }
            }
            this.player.sendMessage(ChatColor.GREEN + "Undo succesfull!  " + ChatColor.RED + _sum + ChatColor.GREEN + "  Blocks have been replaced.");
        }
    }

    /**
     * Writes parameters to the current key in the {@link HashMap}.
     */
    public final void fillCurrent() {
        final int[] _currentP = new int[Sniper.SAVE_ARRAY_SIZE];
        _currentP[Sniper.SAVE_ARRAY_VOXEL_ID] = this.data.getVoxelId();
        _currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] = this.data.getReplaceId();
        _currentP[Sniper.SAVE_ARRAY_DATA_VALUE] = this.data.getData();
        _currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE] = this.data.getBrushSize();
        _currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] = this.data.getVoxelHeight();
        _currentP[Sniper.SAVE_ARRAY_CENTROID] = this.data.getcCen();
        _currentP[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] = this.data.getReplaceData();
        _currentP[Sniper.SAVE_ARRAY_RANGE] = (int) this.range;
        this.brushPresetsParamsS.put("current@", _currentP);
    }

    /**
     * Writes parameters of the last brush you were working with to the previous key in the {@link HashMap}.
     */
    public final void fillPrevious() {
        final int[] _currentP = new int[Sniper.SAVE_ARRAY_SIZE];
        _currentP[Sniper.SAVE_ARRAY_VOXEL_ID] = this.data.getVoxelId();
        _currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] = this.data.getReplaceId();
        _currentP[Sniper.SAVE_ARRAY_DATA_VALUE] = this.data.getData();
        _currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE] = this.data.getBrushSize();
        _currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] = this.data.getVoxelHeight();
        _currentP[Sniper.SAVE_ARRAY_CENTROID] = this.data.getcCen();
        _currentP[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] = this.data.getReplaceData();
        _currentP[Sniper.SAVE_ARRAY_RANGE] = (int) this.range;
        this.brushPresetsParamsS.put("previous@", _currentP);
    }

    public HashMap<String, String> getBrushAlt() {
        return this.brushAlt;
    }

    public HashMap<Integer, Brush> getBrushPresets() {
        return this.brushPresets;
    }

    public HashMap<Integer, int[]> getBrushPresetsParams() {
        return this.brushPresetsParams;
    }

    public HashMap<String, int[]> getBrushPresetsParamsS() {
        return this.brushPresetsParamsS;
    }

    public HashMap<String, Brush> getBrushPresetsS() {
        return this.brushPresetsS;
    }

    public EnumMap<Material, BrushTool> getBrushTools() {
        return this.brushTools;
    }

    public IBrush getCurrent() {
        return this.current;
    }

    public SnipeData getData() {
        return this.data;
    }

    public Integer getGroup() {
        return this.group;
    }

    public HashMap<String, Brush> getMyBrushes() {
        return this.myBrushes;
    }

    public Player getPlayer() {
        return this.player;
    }

    public IBrush getPrevious() {
        return this.previous;
    }

    public double getRange() {
        return this.range;
    }

    public IBrush getReadingBrush() {
        return this.readingBrush;
    }

    public String getReadingString() {
        return this.readingString;
    }

    public IBrush getSneak() {
        return this.sneak;
    }

    public IBrush getTwoBack() {
        return this.twoBack;
    }

    public LinkedList<Undo> getUndoList() {
        return this.undoList;
    }

    public Message getVoxelMessage() {
        return this.voxelMessage;
    }

    /**
     * 
     */
    public final void info() {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.info();
        } else {
            this.current.info(this.voxelMessage);
            if (this.current instanceof Performer) {
                ((Performer) this.current).showInfo(this.voxelMessage);
            }
        }
    }

    public boolean isDistRestrict() {
        return this.distRestrict;
    }

    public boolean isLightning() {
        return this.lightning;
    }

    public boolean isPrintout() {
        return this.printout;
    }

    /**
     * 
     */
    public final void loadAllPresets() {
        try {
            final File _f = new File("plugins/VoxelSniper/presetsBySniper/" + this.player.getName() + ".txt");
            if (_f.exists()) {
                final Scanner _snr = new Scanner(_f);
                final int[] _presetsHolder = new int[Sniper.SAVE_ARRAY_SIZE];
                while (_snr.hasNext()) {
                    try {
                        this.readingString = _snr.nextLine();
                        final int _key = Integer.parseInt(this.readingString);
                        this.readingBrush = this.myBrushes.get(_snr.nextLine());
                        this.brushPresets.put(_key, this.readingBrush);
                        _presetsHolder[Sniper.SAVE_ARRAY_VOXEL_ID] = Integer.parseInt(_snr.nextLine());
                        _presetsHolder[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] = Integer.parseInt(_snr.nextLine());
                        _presetsHolder[Sniper.SAVE_ARRAY_DATA_VALUE] = Byte.parseByte(_snr.nextLine());
                        _presetsHolder[Sniper.SAVE_ARRAY_BRUSH_SIZE] = Integer.parseInt(_snr.nextLine());
                        _presetsHolder[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] = Integer.parseInt(_snr.nextLine());
                        _presetsHolder[Sniper.SAVE_ARRAY_CENTROID] = Integer.parseInt(_snr.nextLine());
                        _presetsHolder[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] = Byte.parseByte(_snr.nextLine());
                        _presetsHolder[Sniper.SAVE_ARRAY_RANGE] = Integer.parseInt(_snr.nextLine());
                        this.brushPresetsParams.put(_key, _presetsHolder);
                    } catch (final NumberFormatException _e) {
                        boolean _first = true;
                        while (_snr.hasNext()) {
                            String _keyS;
                            if (_first) {
                                _keyS = this.readingString;
                                _first = false;
                            } else {
                                _keyS = _snr.nextLine();
                            }
                            this.readingBrush = this.myBrushes.get(_snr.nextLine());
                            this.brushPresetsS.put(_keyS, this.readingBrush);
                            _presetsHolder[Sniper.SAVE_ARRAY_VOXEL_ID] = Integer.parseInt(_snr.nextLine());
                            _presetsHolder[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] = Integer.parseInt(_snr.nextLine());
                            _presetsHolder[Sniper.SAVE_ARRAY_DATA_VALUE] = Byte.parseByte(_snr.nextLine());
                            _presetsHolder[Sniper.SAVE_ARRAY_BRUSH_SIZE] = Integer.parseInt(_snr.nextLine());
                            _presetsHolder[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] = Integer.parseInt(_snr.nextLine());
                            _presetsHolder[Sniper.SAVE_ARRAY_CENTROID] = Integer.parseInt(_snr.nextLine());
                            _presetsHolder[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] = Byte.parseByte(_snr.nextLine());
                            _presetsHolder[Sniper.SAVE_ARRAY_RANGE] = Integer.parseInt(_snr.nextLine());
                            this.brushPresetsParamsS.put(_keyS, _presetsHolder);

                        }
                    }
                }
                _snr.close();
            }
        } catch (final Exception _e) {
            _e.printStackTrace();
        }
    }

    /**
     * @param slot
     */
    public final void loadPreset(final int slot) {
        try {
            final int[] _paramArray = this.brushPresetsParams.get(slot);

            final Brush _temp = this.brushPresets.get(slot);
            if (_temp != this.current) {
                this.twoBack = this.previous;
                this.previous = this.current;
                this.current = _temp;
            }
            this.fillPrevious();
            this.data.setVoxelId(_paramArray[Sniper.SAVE_ARRAY_VOXEL_ID]);
            this.data.setReplaceId(_paramArray[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID]);
            this.data.setData((byte) _paramArray[Sniper.SAVE_ARRAY_DATA_VALUE]);
            this.data.setBrushSize(_paramArray[Sniper.SAVE_ARRAY_BRUSH_SIZE]);
            this.data.setVoxelHeight(_paramArray[Sniper.SAVE_ARRAY_VOXEL_HEIGHT]);
            this.data.setcCen(_paramArray[Sniper.SAVE_ARRAY_CENTROID]);
            this.data.setReplaceData((byte) _paramArray[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE]);
            this.range = _paramArray[Sniper.SAVE_ARRAY_RANGE];
            this.setPerformer(new String[] { "", "m" });

            this.player.sendMessage("Preset loaded.");
        } catch (final Exception _e) {
            this.player.sendMessage(ChatColor.RED + "Preset is empty.  Cannot load.");
            _e.printStackTrace();
        }
    }

    /**
     * @param slot
     */
    public final void loadPreset(final String slot) {
        try {
            final int[] _paramArray = this.brushPresetsParamsS.get(slot);

            final Brush _temp = this.brushPresetsS.get(slot);
            if (_temp != this.current) {
                this.twoBack = this.previous;
                this.previous = this.current;
                this.current = _temp;
            }
            this.fillPrevious();
            this.data.setVoxelId(_paramArray[Sniper.SAVE_ARRAY_VOXEL_ID]);
            this.data.setReplaceId(_paramArray[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID]);
            this.data.setData((byte) _paramArray[Sniper.SAVE_ARRAY_DATA_VALUE]);
            this.data.setBrushSize(_paramArray[Sniper.SAVE_ARRAY_BRUSH_SIZE]);
            this.data.setVoxelHeight(_paramArray[Sniper.SAVE_ARRAY_VOXEL_HEIGHT]);
            this.data.setcCen(_paramArray[Sniper.SAVE_ARRAY_CENTROID]);
            this.data.setReplaceData((byte) _paramArray[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE]);
            this.range = _paramArray[Sniper.SAVE_ARRAY_RANGE];
            this.setPerformer(new String[] { "", "m" });

            this.player.sendMessage("Preset loaded.");
        } catch (final Exception _e) {
            this.player.sendMessage(ChatColor.RED + "Preset is empty.  Cannot load.");
            _e.printStackTrace();
        }
    }

    /**
     * 
     */
    public final void previousBrush() {
        final Brush _temp = this.current;
        this.current = this.previous;
        this.previous = _temp;

        this.fillCurrent();
        this.readPrevious();
        this.brushPresetsParamsS.put("previous@", this.brushPresetsParamsS.get("current@"));
        this.fillCurrent();
        this.info();
    }

    /**
     * 
     */
    public final void printBrushes() {
        String _msg = ChatColor.GREEN + "Available brush short-names: /b ";
        for (final String _brushName : this.myBrushes.keySet()) {
            _msg += ChatColor.GREEN + " | " + ChatColor.BLUE + _brushName;
        }
        this.player.sendMessage(_msg);
    }

    /**
     * 
     */
    public final void printBrushesLong() {
        String _msg = ChatColor.GREEN + "Available brush long-names: /b ";
        for (final String _brushName : this.brushAlt.keySet()) {
            _msg += ChatColor.GREEN + " | " + ChatColor.BLUE + _brushName;
        }
        this.player.sendMessage(_msg);
    }

    /**
     * 
     */
    public final void removeBrushTool() {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            this.brushTools.remove(this.player.getItemInHand().getType());
            this.player.sendMessage(ChatColor.GOLD + "Brush tool has been removed.");
        } else {
            this.player.sendMessage(ChatColor.DARK_GREEN + "Brush tool is non-existant!");
        }
    }

    /**
     * @param i
     */
    public final void removeVoxelFromList(final int i) {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.data.getVoxelList().removeValue(i);
            _bt.data.getVoxelMessage().voxelList();
        } else {
            this.data.getVoxelList().removeValue(i);
            this.voxelMessage.voxelList();
        }
    }

    /**
     * 
     */
    public final void reset() {
        if (this instanceof LiteSniper) {
            this.myBrushes = LiteSniperBrushes.getSniperBrushes();
        } else {
            this.myBrushes = SniperBrushes.getSniperBrushes();
        }

        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.setBrush(new SnipeBrush());

            _bt.data.setVoxelId(0);
            _bt.data.setReplaceId(0);
            _bt.data.setData((byte) 0);
            _bt.data.setBrushSize(3);
            _bt.data.setVoxelHeight(1);
            _bt.data.setcCen(0);
            _bt.data.setReplaceData((byte) 0);
        } else {
            this.current = new SnipeBrush();

            this.fillPrevious();
            this.data.setVoxelId(0);
            this.data.setReplaceId(0);
            this.data.setData((byte) 0);
            this.data.setBrushSize(3);
            this.data.setVoxelHeight(1);
            this.data.setcCen(0);
            this.data.setReplaceData((byte) 0);
            this.range = 1;
        }
        this.fillCurrent();
    }

    /**
     * 
     */
    public final void saveAllPresets() {
        final String _location = "plugins/VoxelSniper/presetsBySniper/" + this.player.getName() + ".txt";
        final File _nf = new File(_location);

        _nf.getParentFile().mkdirs();
        PrintWriter _writer = null;
        try {
            _writer = new PrintWriter(_location);
            int[] _presetsHolder = new int[Sniper.SAVE_ARRAY_SIZE];
            Iterator<?> _it = this.brushPresets.keySet().iterator();
            if (!this.brushPresets.isEmpty()) {
                while (_it.hasNext()) {
                    final int _i = (Integer) _it.next();
                    _writer.write(_i + "\r\n" + this.brushPresets.get(_i).getName() + "\r\n");
                    _presetsHolder = this.brushPresetsParams.get(_i);
                    _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_VOXEL_ID] + "\r\n");
                    _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] + "\r\n");
                    _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_DATA_VALUE] + "\r\n");
                    _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_BRUSH_SIZE] + "\r\n");
                    _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] + "\r\n");
                    _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_CENTROID] + "\r\n");
                    _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] + "\r\n");
                    _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_RANGE] + "\r\n");
                }
            }
            _it = this.brushPresetsS.keySet().iterator();
            if (!this.brushPresetsS.isEmpty()) {
                while (_it.hasNext()) {
                    final String _key = (String) _it.next();
                    if (!_key.startsWith("current") && !_key.startsWith("previous") && !_key.startsWith("twoBack")) {
                        _writer.write(_key + "\r\n" + this.brushPresetsS.get(_key).getName() + "\r\n");
                        _presetsHolder = this.brushPresetsParamsS.get(_key);
                        _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_VOXEL_ID] + "\r\n");
                        _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] + "\r\n");
                        _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_DATA_VALUE] + "\r\n");
                        _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_BRUSH_SIZE] + "\r\n");
                        _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] + "\r\n");
                        _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_CENTROID] + "\r\n");
                        _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] + "\r\n");
                        _writer.write(_presetsHolder[Sniper.SAVE_ARRAY_RANGE] + "\r\n");
                    }
                }
            }
            _writer.close();
        } catch (final Exception _e) {
            _e.printStackTrace();
        }
    }

    /**
     * @param slot
     */
    public final void savePreset(final int slot) {
        this.brushPresets.put(slot, this.current);
        this.fillCurrent();
        this.brushPresetsParams.put(slot, this.brushPresetsParamsS.get("current@"));
        this.saveAllPresets();
        this.player.sendMessage(ChatColor.AQUA + "Preset saved in slot " + slot);
    }

    /**
     * @param slot
     */
    public final void savePreset(final String slot) { // string version
        this.brushPresetsS.put(slot, this.current);
        this.fillCurrent();
        this.brushPresetsParamsS.put(slot, this.brushPresetsParamsS.get("current@"));
        this.saveAllPresets();
        this.player.sendMessage(ChatColor.AQUA + "Preset saved in slot " + slot);
    }

    /**
     * @param args
     * @return boolean
     */
    public final boolean setBrush(final String[] args) {
        try {
            if (args == null || args.length == 0) {
                this.player.sendMessage(ChatColor.RED + "Invalid input!");
                return false;
            }
            if (this.myBrushes.containsKey(args[0])) {
                if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
                    final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
                    _bt.setBrush(SniperBrushes.getBrushInstance(args[0]));
                } else {
                    this.brushPresetsParamsS.put("twoBack@", this.brushPresetsParamsS.get("previous@"));
                    this.fillPrevious();

                    this.twoBack = this.previous;
                    this.previous = this.current;
                    this.current = this.myBrushes.get(args[0]);
                }
            } else if (this.brushAlt.containsKey(args[0])) {
                if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
                    final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
                    _bt.setBrush(SniperBrushes.getBrushInstance(args[0]));
                } else {
                    this.brushPresetsParamsS.put("twoBack@", this.brushPresetsParamsS.get("previous@"));
                    this.fillPrevious();

                    this.twoBack = this.previous;
                    this.previous = this.current;
                    this.current = this.myBrushes.get(this.brushAlt.get(args[0]));
                }
            } else {
                this.player.sendMessage(ChatColor.LIGHT_PURPLE + "No such brush.");
                return false;
            }

            final String[] _argsParsed = this.parseParams(args);

            if (_argsParsed.length > 1) {
                try {
                    if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
                        final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
                        _bt.parse(_argsParsed);
                    } else {
                        if (this.current instanceof Performer) {
                            ((Performer) this.current).parse(_argsParsed, this.data);
                        } else {
                            this.current.parameters(_argsParsed, this.data);
                        }
                    }
                    return true;
                } catch (final Exception _e) {
                    this.player.sendMessage(ChatColor.RED + "Invalid parameters! (Parameter error)");
                    this.player.sendMessage(ChatColor.DARK_PURPLE + "" + this.fromArgs(_argsParsed));
                    this.player.sendMessage(ChatColor.RED + "Is not a valid statement");
                    this.player.sendMessage(ChatColor.DARK_BLUE + "" + _e.getMessage());
                    VoxelSniper.LOG.warning("[VoxelSniper] Exception while receiving parameters: \n(" + this.player.getName() + " " + this.current.getName()
                            + ") par[ " + this.fromArgs(_argsParsed) + "]");
                    VoxelSniper.LOG.log(Level.SEVERE, null, _e);
                    return false;
                }
            }
            this.info();
            return true;
        } catch (final ArrayIndexOutOfBoundsException _e) {
            this.player.sendMessage(ChatColor.RED + "Invalid input!");
            _e.printStackTrace();
            return false;
        }
    }

    public void setBrushAlt(final HashMap<String, String> brushAlt) {
        this.brushAlt = brushAlt;
    }

    /**
     * @param size
     */
    public void setBrushSize(final int size) {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.data.setBrushSize(size);
            _bt.data.getVoxelMessage().size();
        } else {
            this.data.setBrushSize(size);
            this.voxelMessage.size();
        }
    }

    /**
     * @param centroid
     */
    public final void setCentroid(final int centroid) {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.data.setcCen(centroid);
            _bt.data.getVoxelMessage().center();
        } else {
            this.data.setcCen(centroid);
            this.voxelMessage.center();
        }
    }

    public void setCurrent(final Brush current) {
        this.current = current;
    }

    /**
     * @param dat
     */
    public final void setData(final byte dat) {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.data.setData(dat);
            _bt.data.getVoxelMessage().data();
        } else {
            this.data.setData(dat);
            this.voxelMessage.data();
        }
    }

    public void setData(final SnipeData data) {
        this.data = data;
    }

    public void setDistRestrict(final boolean distRestrict) {
        this.distRestrict = distRestrict;
    }

    public void setGroup(final Integer group) {
        this.group = group;
    }

    /**
     * @param heigth
     */
    public void setHeigth(final int heigth) {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.data.setVoxelHeight(heigth);
            _bt.data.getVoxelMessage().height();
        } else {
            this.data.setVoxelHeight(heigth);
            this.voxelMessage.height();
        }
    }

    public void setLightning(final boolean lightning) {
        this.lightning = lightning;
    }

    public void setMyBrushes(final HashMap<String, Brush> myBrushes) {
        this.myBrushes = myBrushes;
    }

    /**
     * @param args
     */
    public final void setPerformer(final String[] args) {
        final String[] _derp = new String[args.length + 1];
        _derp[0] = "";
        System.arraycopy(args, 0, _derp, 1, args.length);
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.setPerformer(_derp);
        } else {
            if (this.current instanceof Performer) {
                ((Performer) this.current).parse(_derp, this.data);
            } else {
                this.voxelMessage.custom(ChatColor.GOLD + "This brush is not a Performer brush!");
            }
        }
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }

    public void setPrevious(final Brush previous) {
        this.previous = previous;
    }

    public void setPrintout(final boolean printout) {
        this.printout = printout;
    }

    /**
     * @param rng
     */
    public void setRange(final double rng) {
        if (rng > -1) {
            this.range = rng;
            this.distRestrict = true;
            this.voxelMessage.toggleRange();
        } else {
            this.distRestrict = !this.distRestrict;
            this.voxelMessage.toggleRange();
        }
    }

    public void setReadingBrush(final Brush readingBrush) {
        this.readingBrush = readingBrush;
    }

    public void setReadingString(final String readingString) {
        this.readingString = readingString;
    }

    /**
     * @param replace
     */
    public void setReplace(final int replace) {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.data.setReplaceId(replace);
            _bt.data.getVoxelMessage().replace();
        } else {
            this.data.setReplaceId(replace);
            this.voxelMessage.replace();
        }
    }

    /**
     * @param dat
     */
    public final void setReplaceData(final byte dat) {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.data.setReplaceData(dat);
            _bt.data.getVoxelMessage().replaceData();
        } else {
            this.data.setReplaceData(dat);
            this.voxelMessage.replaceData();
        }
    }

    public void setSneak(final IBrush sneak) {
        this.sneak = sneak;
    }

    public void setTwoBack(final Brush twoBack) {
        this.twoBack = twoBack;
    }

    /**
     * @param voxel
     */
    public void setVoxel(final int voxel) {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
            final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
            _bt.data.setVoxelId(voxel);
            _bt.data.getVoxelMessage().voxel();
        } else {
            this.data.setVoxelId(voxel);
            this.voxelMessage.voxel();
        }
    }

    public void setVoxelMessage(final Message voxelMessage) {
        this.voxelMessage = voxelMessage;
    }

    /**
     * @param playr
     * @param action
     * @param itemInHand
     * @param clickedBlock
     * @param clickedFace
     * @return boolean Success.
     */
    public final boolean snipe(final Player playr, final Action action, final Material itemInHand, final Block clickedBlock, final BlockFace clickedFace) {
        boolean _success = false;
        try {
            this.player = playr;
            if (this.brushTools.containsKey(this.player.getItemInHand().getType())) {
                final BrushTool _bt = this.brushTools.get(this.player.getItemInHand().getType());
                _success = _bt.snipe(playr, action, itemInHand, clickedBlock, clickedFace);
            } else {
                if (this.player.isSneaking()) {
                    _success = this.sneak.perform(action, this.data, itemInHand, clickedBlock, clickedFace);
                    return _success;
                }

                _success = this.current.perform(action, this.data, itemInHand, clickedBlock, clickedFace);
            }
        } catch (final Exception _e) {
            this.player.sendMessage(ChatColor.RED + "An Exception has occured! (Sniping error)");
            this.player.sendMessage(ChatColor.RED + "" + _e.toString());
            final StackTraceElement[] _ste = _e.getStackTrace();
            for (final StackTraceElement _se : _ste) {
                this.player.sendMessage(ChatColor.DARK_GRAY + _se.getClassName() + ChatColor.DARK_GREEN + " : " + ChatColor.DARK_GRAY + _se.getLineNumber());
            }
            VoxelSniper.LOG.warning("[VoxelSniper] Exception while sniping: (" + this.player.getName() + " " + this.current.getName() + ")");
            VoxelSniper.LOG.log(Level.SEVERE, null, _e);
            return false;
        }
        return _success;
    }

    /**
     * @param undo
     */
    public final void storeUndo(final Undo undo) {
        if (Sniper.undoChacheSize <= 0) {
            return;
        }
        if (undo != null && undo.getSize() > 0) {
            while (this.undoList.size() > Sniper.undoChacheSize) {
                this.undoList.pop();
            }
            this.undoList.add(undo);
        }
    }

    /**
     * 
     */
    public void toggleLightning() {
        this.lightning = !this.lightning;
        this.voxelMessage.toggleLightning();
    }

    /**
     * 
     */
    public final void togglePrintout() {
        this.printout = !this.printout;
        this.voxelMessage.togglePrintout();
    }

    /**
     * 
     */
    public final void twoBackBrush() {
        this.fillCurrent();
        final Brush _temp = this.current;
        final Brush _tempTwo = this.previous;
        this.current = this.twoBack;
        this.previous = _temp;
        this.twoBack = _tempTwo;

        this.fillCurrent();
        this.readTwoBack();
        this.brushPresetsParamsS.put("twoBack@", this.brushPresetsParamsS.get("previous@"));
        this.brushPresetsParamsS.put("previous@", this.brushPresetsParamsS.get("current@"));
        this.fillCurrent();

        this.info();
    }

    private String fromArgs(final String[] args) {
        String _str = "";
        for (final String _st : args) {
            _str += _st + " ";
        }
        return _str;
    }

    private String[] parseParams(final String[] args) {
        final boolean[] _toremove = new boolean[args.length];
        if (args.length > 1) {
            for (int _x = 1; _x < args.length; _x++) {
                final String _str = args[_x];
                if (_str.startsWith("-") && _str.length() > 1) {
                    switch (_str.charAt(1)) {

                    case 'b':
                        try {
                            final int _i = Integer.parseInt(_str.substring(2));
                            this.setBrushSize(_i);
                            _toremove[_x] = true;
                        } catch (final Exception _e) {
                            this.player.sendMessage(ChatColor.RED + args[_x] + " is Not a valid parameter!");
                        }
                        break;

                    case 'r':
                        try {
                            if (_str.length() == 2) {
                                this.setRange(-1);
                            } else {
                                this.setRange(Double.parseDouble(_str.substring(2)));
                            }
                            _toremove[_x] = true;
                        } catch (final Exception _e) {
                            this.player.sendMessage(ChatColor.RED + args[_x] + " is Not a valid parameter!");
                        }
                        break;

                    case 'l':
                        this.toggleLightning();
                        _toremove[_x] = true;
                        break;

                    case 'e':
                        this.player.chat("/ve " + _str.substring(2));
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        int _i = 0;
        for (final boolean _b : _toremove) {
            if (_b) {
                _i++;
            }
        }
        if (_i == 0) {
            return args;
        }
        final String[] _temp = new String[args.length - _i];
        _i = 0;
        for (int _x = 0; _x < args.length; _x++) {
            if (!_toremove[_x]) {
                _temp[_i++] = args[_x];
            }
        }
        return _temp;
    }

    /**
     * Reads parameters from the current key in the {@link HashMap}.
     */
    private void readCurrent() {
        final int[] _currentP = this.brushPresetsParamsS.get("current@");
        this.data.setVoxelId(_currentP[Sniper.SAVE_ARRAY_VOXEL_ID]);
        this.data.setReplaceId(_currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID]);
        this.data.setData((byte) _currentP[Sniper.SAVE_ARRAY_DATA_VALUE]);
        this.data.setBrushSize(_currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE]);
        this.data.setVoxelHeight(_currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT]);
        this.data.setcCen(_currentP[Sniper.SAVE_ARRAY_CENTROID]);
        this.data.setReplaceData((byte) _currentP[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE]);
        this.range = _currentP[Sniper.SAVE_ARRAY_RANGE];
    }

    /**
     * reads parameters from the previous key in the {@link HashMap}.
     */
    private void readPrevious() {
        final int[] _currentP = this.brushPresetsParamsS.get("previous@");
        this.data.setVoxelId(_currentP[Sniper.SAVE_ARRAY_VOXEL_ID]);
        this.data.setReplaceId(_currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID]);
        this.data.setData((byte) _currentP[Sniper.SAVE_ARRAY_DATA_VALUE]);
        this.data.setBrushSize(_currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE]);
        this.data.setVoxelHeight(_currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT]);
        this.data.setcCen(_currentP[Sniper.SAVE_ARRAY_CENTROID]);
        this.data.setReplaceData((byte) _currentP[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE]);
        this.range = _currentP[Sniper.SAVE_ARRAY_RANGE];
    }

    private void readTwoBack() {
        final int[] _currentP = this.brushPresetsParamsS.get("twoBack@");
        this.data.setVoxelId(_currentP[Sniper.SAVE_ARRAY_VOXEL_ID]);
        this.data.setReplaceId(_currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID]);
        this.data.setData((byte) _currentP[Sniper.SAVE_ARRAY_DATA_VALUE]);
        this.data.setBrushSize(_currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE]);
        this.data.setVoxelHeight(_currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT]);
        this.data.setcCen(_currentP[Sniper.SAVE_ARRAY_CENTROID]);
        this.data.setReplaceData((byte) _currentP[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE]);
        this.range = _currentP[Sniper.SAVE_ARRAY_RANGE];
    }
}
