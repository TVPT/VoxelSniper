package com.thevoxelbox.voxelsniper;

import com.google.common.base.Joiner;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.Sneak;
import com.thevoxelbox.voxelsniper.brush.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.perform.Performer;
import com.thevoxelbox.voxelsniper.brush.tool.BrushTool;
import com.thevoxelbox.voxelsniper.brush.tool.SneakBrushTool;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.io.File;
import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * @author Piotr
 */
public class Sniper
{
    private static final int SAVE_ARRAY_SIZE = 8;
    private static final int SAVE_ARRAY_RANGE = 7;
    private static final int SAVE_ARRAY_REPLACE_DATA_VALUE = 6;
    private static final int SAVE_ARRAY_CENTROID = 5;
    private static final int SAVE_ARRAY_VOXEL_HEIGHT = 4;
    private static final int SAVE_ARRAY_BRUSH_SIZE = 3;
    private static final int SAVE_ARRAY_DATA_VALUE = 2;
    private static final int SAVE_ARRAY_REPLACE_VOXEL_ID = 1;
    private static final int SAVE_ARRAY_VOXEL_ID = 0;
    private static int undoCacheSize = 20;
    private final LinkedList<Undo> undoList = new LinkedList<Undo>();
    private final EnumMap<Material, BrushTool> brushTools = new EnumMap<Material, BrushTool>(Material.class);
    private final HashMap<Integer, IBrush> brushPresets = new HashMap<Integer, IBrush>();
    private final HashMap<Integer, int[]> brushPresetsParams = new HashMap<Integer, int[]>();
    private final HashMap<String, IBrush> brushPresetsS = new HashMap<String, IBrush>();
    private final HashMap<String, int[]> brushPresetsParamsS = new HashMap<String, int[]>();
    private IBrush readingBrush;
    private String readingString;
    private Player player;
    private SnipeData data = new SnipeData(this);
    private Message voxelMessage;
    private boolean lightning = false;
    private boolean enabled = true;
    /**
     * If false, will suppress many types of common, spammy vmessages.
     */
    private boolean printout = true;
    private boolean distRestrict = false;
    private double range = 5.0d;
    private Map<String, IBrush> myBrushes;
    private IBrush current = new SnipeBrush();
    private IBrush previous = new SnipeBrush();
    private IBrush twoBack = new SnipeBrush();
    private IBrush sneak = new Sneak();
    private Integer group;

    /**
     * Default constructor.
     */
    public Sniper()
    {
        this.myBrushes = Brushes.getNewSniperBrushInstances();

        this.voxelMessage = new Message(this.data);
        this.data.setVoxelMessage(this.voxelMessage);

        final int[] currentP = new int[Sniper.SAVE_ARRAY_SIZE];
        currentP[Sniper.SAVE_ARRAY_VOXEL_ID] = 0;
        currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] = 0;
        currentP[Sniper.SAVE_ARRAY_DATA_VALUE] = 0;
        currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE] = 3;
        currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] = 1;
        currentP[Sniper.SAVE_ARRAY_CENTROID] = 0;
        this.brushPresetsParamsS.put("current@", currentP);
        this.brushPresetsParamsS.put("previous@", currentP);
        this.brushPresetsParamsS.put("twoBack@", currentP);
        this.brushPresetsS.put("current@", this.myBrushes.get("s"));
        this.brushPresetsS.put("previous@", this.myBrushes.get("s"));
        this.brushPresetsS.put("twoBack@", this.myBrushes.get("s"));
    }

    /**
     * @return int
     */
    public static int getUndoCacheSize()
    {
        return Sniper.undoCacheSize;
    }

    /**
     * @param undoChacheSize
     */
    public static void setUndoCacheSize(final int undoChacheSize)
    {
        Sniper.undoCacheSize = undoChacheSize;
    }

    /**
     * Check if Sniper currently is processing snipes.
     *
     * @return true if enabled, false otherwise.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Set if Sniper currently is processing snipes.
     *
     * @param enabled true for enabling snipe processing.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     *
     */
    public final void addBrushTool()
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            this.player.sendMessage(ChatColor.DARK_GREEN + "That brush tool already exists!");
        }
        else
        {
            this.brushTools.put(this.player.getItemInHand().getType(), new BrushTool(this));
            this.player.sendMessage(ChatColor.GOLD + "Brush tool has been added.");
        }
    }

    /**
     * @param arrow
     */
    public final void addBrushTool(final boolean arrow)
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            this.player.sendMessage(ChatColor.DARK_GREEN + "That brush tool already exists!");
        }
        else
        {
            this.brushTools.put(this.player.getItemInHand().getType(), new SneakBrushTool(this, arrow));
            this.player.sendMessage(ChatColor.GOLD + "Brush tool has been added.");
        }
    }

    /**
     * @param i
     */
    public final void addVoxelToList(final int[] i)
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.data.getVoxelList().add(i);
            brushTool.data.getVoxelMessage().voxelList();
        }
        else
        {
            this.data.getVoxelList().add(i);
            this.voxelMessage.voxelList();
        }
    }

    /**
     *
     */
    public final void clearVoxelList()
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.data.getVoxelList().clear();
            brushTool.data.getVoxelMessage().voxelList();
        }
        else
        {
            this.data.getVoxelList().clear();
            this.voxelMessage.voxelList();
        }
    }

    /**
     *
     */
    public final void doUndo()
    {
        this.doUndo(1);
    }

    /**
     * @param num
     */
    public final void doUndo(final int num)
    {
        int sum = 0;
        if (this.undoList.isEmpty())
        {
            this.player.sendMessage(ChatColor.GREEN + "There's nothing to undo.");
        }
        else
        {
            for (int x = 0; x < num; x++)
            {
                final Undo undo = this.undoList.pollLast();
                if (undo != null)
                {
                    undo.undo();
                    sum += undo.getSize();
                }
                else
                {
                    break;
                }
            }
            this.player.sendMessage(ChatColor.GREEN + "Undo successful:  " + ChatColor.RED + sum + ChatColor.GREEN + " blocks have been replaced.");
        }
    }

    /**
     * Writes parameters to the current key in the {@link HashMap}.
     */
    public final void fillCurrent()
    {
        final int[] currentP = new int[Sniper.SAVE_ARRAY_SIZE];
        currentP[Sniper.SAVE_ARRAY_VOXEL_ID] = this.data.getVoxelId();
        currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] = this.data.getReplaceId();
        currentP[Sniper.SAVE_ARRAY_DATA_VALUE] = this.data.getData();
        currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE] = this.data.getBrushSize();
        currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] = this.data.getVoxelHeight();
        currentP[Sniper.SAVE_ARRAY_CENTROID] = this.data.getcCen();
        currentP[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] = this.data.getReplaceData();
        currentP[Sniper.SAVE_ARRAY_RANGE] = (int) this.range;
        this.brushPresetsParamsS.put("current@", currentP);
    }

    /**
     * Writes parameters of the last brush you were working with to the previous key in the {@link HashMap}.
     */
    public final void fillPrevious()
    {
        final int[] currentP = new int[Sniper.SAVE_ARRAY_SIZE];
        currentP[Sniper.SAVE_ARRAY_VOXEL_ID] = this.data.getVoxelId();
        currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] = this.data.getReplaceId();
        currentP[Sniper.SAVE_ARRAY_DATA_VALUE] = this.data.getData();
        currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE] = this.data.getBrushSize();
        currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] = this.data.getVoxelHeight();
        currentP[Sniper.SAVE_ARRAY_CENTROID] = this.data.getcCen();
        currentP[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] = this.data.getReplaceData();
        currentP[Sniper.SAVE_ARRAY_RANGE] = (int) this.range;
        this.brushPresetsParamsS.put("previous@", currentP);
    }

    public HashMap<Integer, IBrush> getBrushPresets()
    {
        return this.brushPresets;
    }

    public HashMap<Integer, int[]> getBrushPresetsParams()
    {
        return this.brushPresetsParams;
    }

    public HashMap<String, int[]> getBrushPresetsParamsS()
    {
        return this.brushPresetsParamsS;
    }

    public HashMap<String, IBrush> getBrushPresetsS()
    {
        return this.brushPresetsS;
    }

    public EnumMap<Material, BrushTool> getBrushTools()
    {
        return this.brushTools;
    }

    public IBrush getCurrent()
    {
        return this.current;
    }

    public void setCurrent(final Brush current)
    {
        this.current = current;
    }

    public SnipeData getData()
    {
        return this.data;
    }

    /**
     * @param dat
     */
    public final void setData(final byte dat)
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.data.setData(dat);
            brushTool.data.getVoxelMessage().data();
        }
        else
        {
            this.data.setData(dat);
            this.voxelMessage.data();
        }
    }

    public void setData(final SnipeData data)
    {
        this.data = data;
    }

    public Integer getGroup()
    {
        return this.group;
    }

    public void setGroup(final Integer group)
    {
        this.group = group;
    }

    public Map<String, IBrush> getMyBrushes()
    {
        return this.myBrushes;
    }

    public void setMyBrushes(final Map<String, IBrush> myBrushes)
    {
        this.myBrushes = myBrushes;
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public void setPlayer(final Player player)
    {
        this.player = player;
    }

    public IBrush getPrevious()
    {
        return this.previous;
    }

    public void setPrevious(final Brush previous)
    {
        this.previous = previous;
    }

    public double getRange()
    {
        return this.range;
    }

    /**
     * @param rng
     */
    public void setRange(final double rng)
    {
        if (rng > -1)
        {
            this.range = rng;
            this.distRestrict = true;
            this.voxelMessage.toggleRange();
        }
        else
        {
            this.distRestrict = !this.distRestrict;
            this.voxelMessage.toggleRange();
        }
    }

    public IBrush getReadingBrush()
    {
        return this.readingBrush;
    }

    public void setReadingBrush(final Brush readingBrush)
    {
        this.readingBrush = readingBrush;
    }

    public String getReadingString()
    {
        return this.readingString;
    }

    public void setReadingString(final String readingString)
    {
        this.readingString = readingString;
    }

    public IBrush getSneak()
    {
        return this.sneak;
    }

    public void setSneak(final IBrush sneak)
    {
        this.sneak = sneak;
    }

    public IBrush getTwoBack()
    {
        return this.twoBack;
    }

    public void setTwoBack(final Brush twoBack)
    {
        this.twoBack = twoBack;
    }

    public LinkedList<Undo> getUndoList()
    {
        return this.undoList;
    }

    public Message getVoxelMessage()
    {
        return this.voxelMessage;
    }

    public void setVoxelMessage(final Message voxelMessage)
    {
        this.voxelMessage = voxelMessage;
    }

    /**
     *
     */
    public final void info()
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.info();
        }
        else
        {
            this.current.info(this.voxelMessage);
            if (this.current instanceof Performer)
            {
                ((Performer) this.current).showInfo(this.voxelMessage);
            }
        }
    }

    public boolean isDistRestrict()
    {
        return this.distRestrict;
    }

    public void setDistRestrict(final boolean distRestrict)
    {
        this.distRestrict = distRestrict;
    }

    public boolean isLightning()
    {
        return this.lightning;
    }

    public void setLightning(final boolean lightning)
    {
        this.lightning = lightning;
    }

    public boolean isPrintout()
    {
        return this.printout;
    }

    public void setPrintout(final boolean printout)
    {
        this.printout = printout;
    }

    /**
     *
     */
    public final void loadAllPresets()
    {
        try
        {
            final File file = new File("plugins/VoxelSniper/presetsBySniper/" + this.player.getName() + ".txt");
            if (file.exists())
            {
                final Scanner scanner = new Scanner(file);
                final int[] presetsHolder = new int[Sniper.SAVE_ARRAY_SIZE];
                while (scanner.hasNext())
                {
                    try
                    {
                        this.readingString = scanner.nextLine();
                        final int key = Integer.parseInt(this.readingString);
                        this.readingBrush = this.myBrushes.get(scanner.nextLine());
                        this.brushPresets.put(key, this.readingBrush);
                        presetsHolder[Sniper.SAVE_ARRAY_VOXEL_ID] = Integer.parseInt(scanner.nextLine());
                        presetsHolder[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] = Integer.parseInt(scanner.nextLine());
                        presetsHolder[Sniper.SAVE_ARRAY_DATA_VALUE] = Byte.parseByte(scanner.nextLine());
                        presetsHolder[Sniper.SAVE_ARRAY_BRUSH_SIZE] = Integer.parseInt(scanner.nextLine());
                        presetsHolder[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] = Integer.parseInt(scanner.nextLine());
                        presetsHolder[Sniper.SAVE_ARRAY_CENTROID] = Integer.parseInt(scanner.nextLine());
                        presetsHolder[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] = Byte.parseByte(scanner.nextLine());
                        presetsHolder[Sniper.SAVE_ARRAY_RANGE] = Integer.parseInt(scanner.nextLine());
                        this.brushPresetsParams.put(key, presetsHolder);
                    }
                    catch (final NumberFormatException exception)
                    {
                        boolean first = true;
                        while (scanner.hasNext())
                        {
                            String keyS;
                            if (first)
                            {
                                keyS = this.readingString;
                                first = false;
                            }
                            else
                            {
                                keyS = scanner.nextLine();
                            }
                            this.readingBrush = this.myBrushes.get(scanner.nextLine());
                            this.brushPresetsS.put(keyS, this.readingBrush);
                            presetsHolder[Sniper.SAVE_ARRAY_VOXEL_ID] = Integer.parseInt(scanner.nextLine());
                            presetsHolder[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] = Integer.parseInt(scanner.nextLine());
                            presetsHolder[Sniper.SAVE_ARRAY_DATA_VALUE] = Byte.parseByte(scanner.nextLine());
                            presetsHolder[Sniper.SAVE_ARRAY_BRUSH_SIZE] = Integer.parseInt(scanner.nextLine());
                            presetsHolder[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] = Integer.parseInt(scanner.nextLine());
                            presetsHolder[Sniper.SAVE_ARRAY_CENTROID] = Integer.parseInt(scanner.nextLine());
                            presetsHolder[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] = Byte.parseByte(scanner.nextLine());
                            presetsHolder[Sniper.SAVE_ARRAY_RANGE] = Integer.parseInt(scanner.nextLine());
                            this.brushPresetsParamsS.put(keyS, presetsHolder);
                        }
                    }
                }
                scanner.close();
            }
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * @param slot
     */
    public final void loadPreset(final int slot)
    {
        try
        {
            final int[] parameterArray = this.brushPresetsParams.get(slot);

            final IBrush temp = this.brushPresets.get(slot);
            if (temp != this.current)
            {
                this.twoBack = this.previous;
                this.previous = this.current;
                this.current = temp;
            }
            this.fillPrevious();
            this.data.setVoxelId(parameterArray[Sniper.SAVE_ARRAY_VOXEL_ID]);
            this.data.setReplaceId(parameterArray[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID]);
            this.data.setData((byte) parameterArray[Sniper.SAVE_ARRAY_DATA_VALUE]);
            this.data.setBrushSize(parameterArray[Sniper.SAVE_ARRAY_BRUSH_SIZE]);
            this.data.setVoxelHeight(parameterArray[Sniper.SAVE_ARRAY_VOXEL_HEIGHT]);
            this.data.setcCen(parameterArray[Sniper.SAVE_ARRAY_CENTROID]);
            this.data.setReplaceData((byte) parameterArray[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE]);
            this.range = parameterArray[Sniper.SAVE_ARRAY_RANGE];
            this.setPerformer(new String[]{"", "m"});

            this.player.sendMessage("Preset loaded.");
        }
        catch (final Exception exception)
        {
            this.player.sendMessage(ChatColor.RED + "Preset is empty. Cannot load.");
            exception.printStackTrace();
        }
    }

    /**
     * @param slot
     */
    public final void loadPreset(final String slot)
    {
        try
        {
            final int[] parameterArray = this.brushPresetsParamsS.get(slot);

            final IBrush temp = this.brushPresetsS.get(slot);
            if (temp != this.current)
            {
                this.twoBack = this.previous;
                this.previous = this.current;
                this.current = temp;
            }
            this.fillPrevious();
            this.data.setVoxelId(parameterArray[Sniper.SAVE_ARRAY_VOXEL_ID]);
            this.data.setReplaceId(parameterArray[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID]);
            this.data.setData((byte) parameterArray[Sniper.SAVE_ARRAY_DATA_VALUE]);
            this.data.setBrushSize(parameterArray[Sniper.SAVE_ARRAY_BRUSH_SIZE]);
            this.data.setVoxelHeight(parameterArray[Sniper.SAVE_ARRAY_VOXEL_HEIGHT]);
            this.data.setcCen(parameterArray[Sniper.SAVE_ARRAY_CENTROID]);
            this.data.setReplaceData((byte) parameterArray[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE]);
            this.range = parameterArray[Sniper.SAVE_ARRAY_RANGE];
            this.setPerformer(new String[]{"", "m"});

            this.player.sendMessage("Preset loaded.");
        }
        catch (final Exception exception)
        {
            this.player.sendMessage(ChatColor.RED + "Preset is empty. Cannot load.");
            exception.printStackTrace();
        }
    }

    /**
     *
     */
    public final void previousBrush()
    {
        final IBrush temp = this.current;
        this.current = this.previous;
        this.previous = temp;

        this.fillCurrent();
        this.readPrevious();
        this.brushPresetsParamsS.put("previous@", this.brushPresetsParamsS.get("current@"));
        this.fillCurrent();
        this.info();
    }

    /**
     *
     */
    public final void printBrushes()
    {
        player.sendMessage("Available brushes:");
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(ChatColor.BLUE);
        Joiner joiner = Joiner.on(ChatColor.GREEN + " | " + ChatColor.BLUE).skipNulls();
        resultBuilder.append(joiner.join(myBrushes.keySet()));
        this.player.sendMessage(resultBuilder.toString());
    }

    /**
     *
     */
    public final void removeBrushTool()
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            this.brushTools.remove(this.player.getItemInHand().getType());
            this.player.sendMessage(ChatColor.GOLD + "Brush tool has been removed.");
        }
        else
        {
            this.player.sendMessage(ChatColor.DARK_GREEN + "That brush tool doesn't exist!");
        }
    }

    /**
     * @param i
     */
    public final void removeVoxelFromList(final int[] i)
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.data.getVoxelList().removeValue(i);
            brushTool.data.getVoxelMessage().voxelList();
        }
        else
        {
            this.data.getVoxelList().removeValue(i);
            this.voxelMessage.voxelList();
        }
    }

    /**
     *
     */
    public final void reset()
    {
        if (this instanceof LiteSniper)
        {
            this.myBrushes = Brushes.getNewLiteSniperBrushInstances();
        }
        else
        {
            this.myBrushes = Brushes.getNewSniperBrushInstances();
        }

        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.setBrush(new SnipeBrush());
            brushTool.data.reset();
        }
        else
        {
            this.current = new SnipeBrush();
            this.fillPrevious();
            this.data.reset();
            this.range = 1;
            this.distRestrict = false;

        }
        this.fillCurrent();
    }

    /**
     *
     */
    public final void saveAllPresets()
    {
        final String location = "plugins/VoxelSniper/presetsBySniper/" + this.player.getName() + ".txt";
        final File file = new File(location);

        file.getParentFile().mkdirs();
        PrintWriter writer;
        try
        {
            writer = new PrintWriter(location);
            int[] presetsHolder = new int[Sniper.SAVE_ARRAY_SIZE];
            Iterator<?> iterator = this.brushPresets.keySet().iterator();
            if (!this.brushPresets.isEmpty())
            {
                while (iterator.hasNext())
                {
                    final int i = (Integer) iterator.next();
                    writer.write(i + "\r\n" + this.brushPresets.get(i).getName() + "\r\n");
                    presetsHolder = this.brushPresetsParams.get(i);
                    writer.write(presetsHolder[Sniper.SAVE_ARRAY_VOXEL_ID] + "\r\n");
                    writer.write(presetsHolder[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] + "\r\n");
                    writer.write(presetsHolder[Sniper.SAVE_ARRAY_DATA_VALUE] + "\r\n");
                    writer.write(presetsHolder[Sniper.SAVE_ARRAY_BRUSH_SIZE] + "\r\n");
                    writer.write(presetsHolder[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] + "\r\n");
                    writer.write(presetsHolder[Sniper.SAVE_ARRAY_CENTROID] + "\r\n");
                    writer.write(presetsHolder[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] + "\r\n");
                    writer.write(presetsHolder[Sniper.SAVE_ARRAY_RANGE] + "\r\n");
                }
            }
            iterator = this.brushPresetsS.keySet().iterator();
            if (!this.brushPresetsS.isEmpty())
            {
                while (iterator.hasNext())
                {
                    final String key = (String) iterator.next();
                    if (!key.startsWith("current") && !key.startsWith("previous") && !key.startsWith("twoBack"))
                    {
                        writer.write(key + "\r\n" + this.brushPresetsS.get(key).getName() + "\r\n");
                        presetsHolder = this.brushPresetsParamsS.get(key);
                        writer.write(presetsHolder[Sniper.SAVE_ARRAY_VOXEL_ID] + "\r\n");
                        writer.write(presetsHolder[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID] + "\r\n");
                        writer.write(presetsHolder[Sniper.SAVE_ARRAY_DATA_VALUE] + "\r\n");
                        writer.write(presetsHolder[Sniper.SAVE_ARRAY_BRUSH_SIZE] + "\r\n");
                        writer.write(presetsHolder[Sniper.SAVE_ARRAY_VOXEL_HEIGHT] + "\r\n");
                        writer.write(presetsHolder[Sniper.SAVE_ARRAY_CENTROID] + "\r\n");
                        writer.write(presetsHolder[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE] + "\r\n");
                        writer.write(presetsHolder[Sniper.SAVE_ARRAY_RANGE] + "\r\n");
                    }
                }
            }
            writer.close();
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * @param slot
     */
    public final void savePreset(final int slot)
    {
        this.brushPresets.put(slot, this.current);
        this.fillCurrent();
        this.brushPresetsParams.put(slot, this.brushPresetsParamsS.get("current@"));
        this.saveAllPresets();
        this.player.sendMessage(ChatColor.AQUA + "Preset saved in slot " + slot);
    }

    /**
     * @param slot
     */
    public final void savePreset(final String slot)
    { // string version
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
    public final boolean setBrush(final String[] args)
    {
        try
        {
            if (args == null || args.length == 0)
            {
                this.player.sendMessage(ChatColor.RED + "Invalid input.");
                return false;
            }
            if (this.myBrushes.containsKey(args[0]))
            {
                if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
                {
                    final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
                    brushTool.setBrush(Brushes.getNewSniperBrushInstance(args[0]));
                }
                else
                {
                    this.brushPresetsParamsS.put("twoBack@", this.brushPresetsParamsS.get("previous@"));
                    this.fillPrevious();

                    this.twoBack = this.previous;
                    this.previous = this.current;
                    this.current = this.myBrushes.get(args[0]);
                }
            }
            else
            {
                this.player.sendMessage(ChatColor.LIGHT_PURPLE + "That brush does not exist.");
                return false;
            }

            final String[] argumentsParsed = this.parseParams(args);

            if (argumentsParsed.length > 1)
            {
                try
                {
                    if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
                    {
                        final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
                        brushTool.parse(argumentsParsed);
                    }
                    else
                    {
                        if (this.current instanceof Performer)
                        {
                            ((Performer) this.current).parse(argumentsParsed, this.data);
                        }
                        else
                        {
                            this.current.parameters(argumentsParsed, this.data);
                        }
                    }
                    return true;
                }
                catch (Exception exception)
                {
                    this.player.sendMessage(ChatColor.RED + "Invalid parameters! (Parameter error)");
                    this.player.sendMessage(ChatColor.DARK_PURPLE + "" + this.fromArgs(argumentsParsed));
                    this.player.sendMessage(ChatColor.RED + "Is not a valid statement");
                    this.player.sendMessage(ChatColor.DARK_BLUE + "" + exception.getMessage());
                    VoxelSniper.getInstance().getLogger().log(Level.SEVERE, "Exception while receiving parameters: (" + this.player.getName() + " " + this.current.getName() + ") par[ " + this.fromArgs(argumentsParsed) + "]", exception);
                    return false;
                }
            }
            this.info();
            return true;
        }
        catch (final ArrayIndexOutOfBoundsException exception)
        {
            this.player.sendMessage(ChatColor.RED + "Invalid input.");
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * @param size
     */
    public void setBrushSize(final int size)
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.data.setBrushSize(size);
            brushTool.data.getVoxelMessage().size();
        }
        else
        {
            this.data.setBrushSize(size);
            this.voxelMessage.size();
        }
    }

    /**
     * @param centroid
     */
    public final void setCentroid(final int centroid)
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.data.setcCen(centroid);
            brushTool.data.getVoxelMessage().center();
        }
        else
        {
            this.data.setcCen(centroid);
            this.voxelMessage.center();
        }
    }

    /**
     * @param heigth
     */
    public void setHeigth(final int heigth)
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.data.setVoxelHeight(heigth);
            brushTool.data.getVoxelMessage().height();
        }
        else
        {
            this.data.setVoxelHeight(heigth);
            this.voxelMessage.height();
        }
    }

    /**
     * @param args
     */
    public final void setPerformer(final String[] args)
    {
        final String[] parameters = new String[args.length + 1];
        parameters[0] = "";
        System.arraycopy(args, 0, parameters, 1, args.length);
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.setPerformer(parameters);
        }
        else
        {
            if (this.current instanceof Performer)
            {
                ((Performer) this.current).parse(parameters, this.data);
            }
            else
            {
                this.voxelMessage.custom(ChatColor.GOLD + "This brush is not a performer brush.");
            }
        }
    }

    /**
     * @param replace
     */
    public void setReplace(final int replace)
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.data.setReplaceId(replace);
            brushTool.data.getVoxelMessage().replace();
        }
        else
        {
            this.data.setReplaceId(replace);
            this.voxelMessage.replace();
        }
    }

    /**
     * @param dat
     */
    public final void setReplaceData(final byte dat)
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.data.setReplaceData(dat);
            brushTool.data.getVoxelMessage().replaceData();
        }
        else
        {
            this.data.setReplaceData(dat);
            this.voxelMessage.replaceData();
        }
    }

    /**
     * @param voxel
     */
    public void setVoxel(final int voxel)
    {
        if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
        {
            final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
            brushTool.data.setVoxelId(voxel);
            brushTool.data.getVoxelMessage().voxel();
        }
        else
        {
            this.data.setVoxelId(voxel);
            this.voxelMessage.voxel();
        }
    }

    /**
     * @param playr
     * @param action
     * @param itemInHand
     * @param clickedBlock
     * @param clickedFace
     * @return boolean Success.
     */
    public final boolean snipe(final Player playr, final Action action, final Material itemInHand, final Block clickedBlock, final BlockFace clickedFace)
    {
        boolean success = false;
        try
        {
            this.player = playr;
            if (this.brushTools.containsKey(this.player.getItemInHand().getType()))
            {
                final BrushTool brushTool = this.brushTools.get(this.player.getItemInHand().getType());
                success = brushTool.snipe(playr, action, itemInHand, clickedBlock, clickedFace);
            }
            else
            {
                if (this.player.isSneaking())
                {
                    success = this.sneak.perform(action, this.data, itemInHand, clickedBlock, clickedFace);
                    return success;
                }

                success = this.current.perform(action, this.data, itemInHand, clickedBlock, clickedFace);
            }
        }
        catch (Exception exception)
        {
            player.sendMessage(ChatColor.RED + "An Exception has been caught while trying to execute snipe. (Details in server log)");
            VoxelSniper.getInstance().getLogger().log(Level.SEVERE, "Exception while sniping: (" + player.getName() + " " + current.getName() + ")", exception);
            return false;
        }
        return success;
    }

    /**
     * @param undo
     */
    public final void storeUndo(final Undo undo)
    {
        if (Sniper.undoCacheSize <= 0)
        {
            return;
        }
        if (undo != null && undo.getSize() > 0)
        {
            while (this.undoList.size() > Sniper.undoCacheSize)
            {
                this.undoList.pop();
            }
            this.undoList.add(undo);
        }
    }

    /**
     *
     */
    public void toggleLightning()
    {
        this.lightning = !this.lightning;
        this.voxelMessage.toggleLightning();
    }

    /**
     *
     */
    public final void togglePrintout()
    {
        this.printout = !this.printout;
        this.voxelMessage.togglePrintout();
    }

    /**
     *
     */
    public final void twoBackBrush()
    {
        this.fillCurrent();
        final IBrush temp = this.current;
        final IBrush tempTwo = this.previous;
        this.current = this.twoBack;
        this.previous = temp;
        this.twoBack = tempTwo;

        this.fillCurrent();
        this.readTwoBack();
        this.brushPresetsParamsS.put("twoBack@", this.brushPresetsParamsS.get("previous@"));
        this.brushPresetsParamsS.put("previous@", this.brushPresetsParamsS.get("current@"));
        this.fillCurrent();

        this.info();
    }

    private String fromArgs(final String[] args)
    {
        String string = "";
        for (final String argument : args)
        {
            string += argument + " ";
        }
        return string;
    }

    private String[] parseParams(final String[] args)
    {
        final boolean[] toRemove = new boolean[args.length];
        if (args.length > 1)
        {
            for (int x = 1; x < args.length; x++)
            {
                final String arg = args[x];
                if (arg.startsWith("-") && arg.length() > 1)
                {
                    switch (arg.charAt(1))
                    {

                        case 'b':
                            try
                            {
                                final int i = Integer.parseInt(arg.substring(2));
                                this.setBrushSize(i);
                                toRemove[x] = true;
                            }
                            catch (final Exception exception)
                            {
                                this.player.sendMessage(ChatColor.RED + args[x] + " is not a valid parameter!");
                            }
                            break;

                        case 'r':
                            try
                            {
                                if (arg.length() == 2)
                                {
                                    this.setRange(-1);
                                }
                                else
                                {
                                    this.setRange(Double.parseDouble(arg.substring(2)));
                                }
                                toRemove[x] = true;
                            }
                            catch (final Exception exception)
                            {
                                this.player.sendMessage(ChatColor.RED + args[x] + " is not a valid parameter!");
                            }
                            break;

                        case 'l':
                            this.toggleLightning();
                            toRemove[x] = true;
                            break;

                        case 'e':
                            this.player.chat("/ve " + arg.substring(2));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        int i = 0;
        for (final boolean b : toRemove)
        {
            if (b)
            {
                i++;
            }
        }
        if (i == 0)
        {
            return args;
        }
        final String[] temp = new String[args.length - i];
        i = 0;
        for (int x = 0; x < args.length; x++)
        {
            if (!toRemove[x])
            {
                temp[i++] = args[x];
            }
        }
        return temp;
    }

    /**
     * Reads parameters from the current key in the {@link HashMap}.
     */
    @SuppressWarnings("unused")
	private void readCurrent()
    {
        final int[] currentP = this.brushPresetsParamsS.get("current@");
        this.data.setVoxelId(currentP[Sniper.SAVE_ARRAY_VOXEL_ID]);
        this.data.setReplaceId(currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID]);
        this.data.setData((byte) currentP[Sniper.SAVE_ARRAY_DATA_VALUE]);
        this.data.setBrushSize(currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE]);
        this.data.setVoxelHeight(currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT]);
        this.data.setcCen(currentP[Sniper.SAVE_ARRAY_CENTROID]);
        this.data.setReplaceData((byte) currentP[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE]);
        this.range = currentP[Sniper.SAVE_ARRAY_RANGE];
    }

    /**
     * reads parameters from the previous key in the {@link HashMap}.
     */
    private void readPrevious()
    {
        final int[] currentP = this.brushPresetsParamsS.get("previous@");
        this.data.setVoxelId(currentP[Sniper.SAVE_ARRAY_VOXEL_ID]);
        this.data.setReplaceId(currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID]);
        this.data.setData((byte) currentP[Sniper.SAVE_ARRAY_DATA_VALUE]);
        this.data.setBrushSize(currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE]);
        this.data.setVoxelHeight(currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT]);
        this.data.setcCen(currentP[Sniper.SAVE_ARRAY_CENTROID]);
        this.data.setReplaceData((byte) currentP[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE]);
        this.range = currentP[Sniper.SAVE_ARRAY_RANGE];
    }

    private void readTwoBack()
    {
        final int[] currentP = this.brushPresetsParamsS.get("twoBack@");
        this.data.setVoxelId(currentP[Sniper.SAVE_ARRAY_VOXEL_ID]);
        this.data.setReplaceId(currentP[Sniper.SAVE_ARRAY_REPLACE_VOXEL_ID]);
        this.data.setData((byte) currentP[Sniper.SAVE_ARRAY_DATA_VALUE]);
        this.data.setBrushSize(currentP[Sniper.SAVE_ARRAY_BRUSH_SIZE]);
        this.data.setVoxelHeight(currentP[Sniper.SAVE_ARRAY_VOXEL_HEIGHT]);
        this.data.setcCen(currentP[Sniper.SAVE_ARRAY_CENTROID]);
        this.data.setReplaceData((byte) currentP[Sniper.SAVE_ARRAY_REPLACE_DATA_VALUE]);
        this.range = currentP[Sniper.SAVE_ARRAY_RANGE];
    }
}
