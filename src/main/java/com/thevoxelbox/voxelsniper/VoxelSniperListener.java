package com.thevoxelbox.voxelsniper;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.perform.Performer;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerE;
import com.thevoxelbox.voxelsniper.event.SniperBrushChangedEvent;
import com.thevoxelbox.voxelsniper.event.SniperBrushSizeChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Voxel
 */
public class VoxelSniperListener implements Listener
{

    private static final String SNIPER_PERMISSION = "voxelsniper.sniper";
    private final VoxelSniper plugin;

    /**
     * @param plugin
     */
    public VoxelSniperListener(final VoxelSniper plugin)
    {
        this.plugin = plugin;
        MetricsManager.setSnipeCounterInitTimeStamp(System.currentTimeMillis());
    }

    /**
     * @param player
     * @param split
     * @param command
     * @return boolean Success.
     */
    public boolean onCommand(final Player player, final String[] split, final String command)
    {
        if (command.equalsIgnoreCase("vchunk"))
        {
            return commandVChunk(player);
        }
        else if (command.equalsIgnoreCase("paint"))
        {
            return commandPaint(player, split);
        }
        else if (command.equalsIgnoreCase("goto") && player.hasPermission("voxelsniper.goto"))
        {
            return commandGoto(player, split);
        }
        else if (command.equalsIgnoreCase("btool"))
        {
            return commandSniperBTool(player, split);
        }
        else if (command.equalsIgnoreCase("uu"))
        {
            return commandSniperUU(player, split);
        }
        else if (command.equalsIgnoreCase("u"))
        {
            return commandSniperU(player, split);
        }
        else if (command.equalsIgnoreCase("d"))
        {
            return commandSniperD(player);
        }
        else if (command.equalsIgnoreCase("vs"))
        {
            return commandSniperVs(player, split);
        }
        else if (command.equalsIgnoreCase("vc"))
        {
            return commandSniperVc(player, split);
        }
        else if (command.equalsIgnoreCase("vh"))
        {
            return commandSniperVh(player, split);
        }
        else if (command.equalsIgnoreCase("vi"))
        {
            return commandSniperVi(player, split);
        }
        else if (command.equalsIgnoreCase("vir"))
        {
            return commandSniperVir(player, split);
        }
        else if (command.equalsIgnoreCase("vr"))
        {
            return commandSniperVr(player, split);
        }
        else if (command.equalsIgnoreCase("vl"))
        {
            return commandSniperVl(player, split);
        }
        else if (command.equalsIgnoreCase("v"))
        {
            return commandSniperV(player, split);
        }
        else if (command.equalsIgnoreCase("b"))
        {
            return commandSniperB(player, split);
        }
        else if (command.equalsIgnoreCase("p"))
        {
            return commandSniperP(player, split);
        }
        return false;
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandGoto(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        try
        {
            final int x = Integer.parseInt(split[0]);
            final int z = Integer.parseInt(split[1]);
            player.teleport(new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z), z));
            player.sendMessage(ChatColor.GREEN + "Woosh!");
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage(ChatColor.RED + "Invalid syntax.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandPaint(final Player player, final String[] split)
    {
        if (split.length == 1)
        {
            if (split[0].equalsIgnoreCase("back"))
            {
                PaintingWrapper.paint(player, true, true, 0);
                return true;
            }
            else
            {
                try
                {
                    PaintingWrapper.paint(player, false, false, Integer.parseInt(split[0]));
                    return true;
                }
                catch (final Exception exception)
                {
                    player.sendMessage(ChatColor.RED + "Invalid input.");
                    return true;
                }
            }
        }
        else
        {
            PaintingWrapper.paint(player, true, false, 0);
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperB(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
        String currentToolId = sniper.getCurrentToolId();
        SnipeData snipeData = sniper.getSnipeData(currentToolId);

        if (split == null || split.length == 0)
        {
            sniper.previousBrush(currentToolId);
            sniper.displayInfo();
            return true;
        }
        else if (split.length > 0)
        {
            try
            {
                int newBrushSize = Integer.parseInt(split[0]);
                if (!player.hasPermission("voxelsniper.ignorelimitations") && newBrushSize > plugin.getVoxelSniperConfiguration().getLiteSniperMaxBrushSize())
                {
                    player.sendMessage("Size is restricted to " + plugin.getVoxelSniperConfiguration().getLiteSniperMaxBrushSize() + " for you.");
                    newBrushSize = plugin.getVoxelSniperConfiguration().getLiteSniperMaxBrushSize();
                }
                int originalSize = snipeData.getBrushSize();
                snipeData.setBrushSize(newBrushSize);
                SniperBrushSizeChangedEvent event = new SniperBrushSizeChangedEvent(sniper, currentToolId, originalSize, snipeData.getBrushSize());
                Bukkit.getPluginManager().callEvent(event);
                snipeData.getVoxelMessage().size();
                return true;
            }
            catch (NumberFormatException ingored)
            {
            }

            Class<? extends IBrush> brush = plugin.getBrushManager().getBrushForHandle(split[0]);
            if (brush != null)
            {
                IBrush orignalBrush = sniper.getBrush(currentToolId);
                sniper.setBrush(currentToolId, brush);

                if (split.length > 1)
                {
                    IBrush currentBrush = sniper.getBrush(currentToolId);
                    if (currentBrush instanceof Performer)
                    {
                        String[] parameters = Arrays.copyOfRange(split, 1, split.length);
                        ((Performer) currentBrush).parse(parameters, snipeData);
                        return true;
                    }
                    else
                    {
                        String[] parameters = hackTheArray(Arrays.copyOfRange(split, 1, split.length));
                        currentBrush.parameters(parameters, snipeData);
                        return true;
                    }
                }
                SniperBrushChangedEvent event = new SniperBrushChangedEvent(sniper, currentToolId, orignalBrush, sniper.getBrush(currentToolId));
                sniper.displayInfo();
                return true;
            }
            else
            {
                player.sendMessage("Couldn't find Brush for brush handle \"" + split[0] + "\"");
                return true;
            }
        }
        return false;
    }

    /**
     * Padds an empty String to the front of the array.
     *
     * @param args Array to pad empty string in front of
     * @return padded array
     */
    private String[] hackTheArray(String[] args)
    {
        String[] returnValue = new String[args.length + 1];
        for (int i = 0, argsLength = args.length; i < argsLength; i++)
        {
            String arg = args[i];
            returnValue[i + 1] = arg;
        }
        return returnValue;
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperBTool(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        if (split != null && split.length > 0)
        {
            if (split[0].equalsIgnoreCase("assign"))
            {
                SnipeAction action;
                if (split[1].equalsIgnoreCase("arrow"))
                {
                    action = SnipeAction.ARROW;
                }
                else if (split[1].equalsIgnoreCase("powder"))
                {
                    action = SnipeAction.GUNPOWDER;
                }
                else
                {
                    player.sendMessage("/btool assign <arrow|powder> <toolid>");
                    return true;
                }

                if (split.length == 3 && split[2] != null && !split[2].isEmpty())
                {
                    Material itemInHand = (player.getItemInHand() != null) ? player.getItemInHand().getType() : null;
                    if (itemInHand == null)
                    {
                        player.sendMessage("/btool assign <arrow|powder> <toolid>");
                        return true;
                    }
                    if (sniper.setTool(split[2], action, itemInHand))
                    {
                        player.sendMessage(itemInHand.name() + " has been assigned to '" + split[2] + "' as action " + action.name() + ".");
                    }
                    else
                    {
                        player.sendMessage("Couldn't assign tool.");
                    }
                    return true;
                }
            }
            else if (split[0].equalsIgnoreCase("remove"))
            {
                if (split.length == 2 && split[1] != null && !split[1].isEmpty())
                {
                    sniper.removeTool(split[1]);
                    return true;
                }
                else
                {
                    Material itemInHand = (player.getItemInHand() != null) ? player.getItemInHand().getType() : null;
                    if (itemInHand == null)
                    {
                        player.sendMessage("Can't unassign empty hands.");
                        return true;
                    }
                    if (sniper.getCurrentToolId() == null)
                    {
                        player.sendMessage("Can't unassign default tool.");
                        return true;
                    }
                    sniper.removeTool(sniper.getCurrentToolId(), itemInHand);
                    return true;
                }
            }
        }
        player.sendMessage("/btool assign <arrow|powder> <toolid>");
        player.sendMessage("/btool remove [toolid]");
        return true;
    }

    /**
     * @param player
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperD(final Player player)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        sniper.reset(sniper.getCurrentToolId());
        player.sendMessage(ChatColor.AQUA + "Brush settings reset to their default values.");
        return true;
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperP(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        try
        {
            if (split == null || split.length == 0)
            {
                IBrush brush = sniper.getBrush(sniper.getCurrentToolId());
                if (brush instanceof Performer)
                {
                    ((Performer) brush).parse(new String[]{ "m" }, snipeData);
                }
                else
                {
                    player.sendMessage("This brush is not a performer brush.");
                }
            }
            else
            {
                IBrush brush = sniper.getBrush(sniper.getCurrentToolId());
                if (brush instanceof Performer)
                {
                    ((Performer) brush).parse(split, snipeData);
                }
                else
                {
                    player.sendMessage("This brush is not a performer brush.");
                }
            }
            return true;
        }
        catch (Exception exception)
        {
            plugin.getLogger().log(Level.WARNING, "Command error from " + player.getName(), exception);
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperU(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        if (split.length == 1)
        {
            try
            {
                int amount = Integer.parseInt(split[0]);
                sniper.undo(amount);
            }
            catch (NumberFormatException exception)
            {
                player.sendMessage("Error while parsing amount of undo. Number format exception.");
            }
        }
        else
        {
            sniper.undo();
        }
        plugin.getLogger().info("Player \"" + player.getName() + "\" used /u");
        return true;
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperUU(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        try
        {
            plugin.getSniperManager().getSniperForPlayer(Bukkit.getPlayer(split[0])).undo();
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage(ChatColor.GREEN + "Player not found.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    @SuppressWarnings("deprecation")
    private boolean commandSniperV(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        if (split.length == 0)
        {
            Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (targetBlock != null)
            {
                if (!player.hasPermission("voxelsniper.ignorelimitations") && plugin.getVoxelSniperConfiguration().getLiteSniperRestrictedItems().contains(targetBlock.getTypeId()))
                {
                    player.sendMessage("You are not allowed to use " + targetBlock.getType().name() + ".");
                    return true;
                }
                snipeData.setVoxelId(targetBlock.getTypeId());
                snipeData.getVoxelMessage().voxel();
            }
            return true;
        }

        Material material = Material.matchMaterial(split[0]);
        if (material != null && material.isBlock())
        {
            if (!player.hasPermission("voxelsniper.ignorelimitations") && plugin.getVoxelSniperConfiguration().getLiteSniperRestrictedItems().contains(material.getId()))
            {
                player.sendMessage("You are not allowed to use " + material.name() + ".");
                return true;
            }
            snipeData.setVoxelId(material.getId());
            snipeData.getVoxelMessage().voxel();
            return true;
        }
        else
        {
            player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperVc(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        try
        {
            int center = Integer.parseInt(split[0]);
            snipeData.setcCen(center);
            snipeData.getVoxelMessage().center();
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage(ChatColor.RED + "Invalid input.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperVh(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        try
        {
            int height = Integer.parseInt(split[0]);
            snipeData.setVoxelHeight(height);
            snipeData.getVoxelMessage().height();
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage(ChatColor.RED + "Invalid input.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    @SuppressWarnings("deprecation")
    private boolean commandSniperVi(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        byte dataValue;

        if (split.length == 0)
        {
            Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (targetBlock != null)
            {
                dataValue = targetBlock.getData();
            }
            else
            {
                return true;
            }
        }
        else
        {
            try
            {
                dataValue = Byte.parseByte(split[0]);
            }
            catch (NumberFormatException exception)
            {
                player.sendMessage("Couldn't parse input.");
                return true;
            }
        }

        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        snipeData.setData(dataValue);
        snipeData.getVoxelMessage().data();
        return true;
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    @SuppressWarnings("deprecation")
    private boolean commandSniperVir(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        byte dataValue;

        if (split.length == 0)
        {
            Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (targetBlock != null)
            {
                dataValue = targetBlock.getData();
            }
            else
            {
                return true;
            }
        }
        else
        {
            try
            {
                dataValue = Byte.parseByte(split[0]);
            }
            catch (NumberFormatException exception)
            {
                player.sendMessage("Couldn't parse input.");
                return true;
            }
        }

        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        snipeData.setReplaceData(dataValue);
        snipeData.getVoxelMessage().replaceData();
        return true;
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    @SuppressWarnings("deprecation")
    private boolean commandSniperVl(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        if (split.length == 0)
        {
            final RangeBlockHelper rangeBlockHelper = new RangeBlockHelper(player, player.getWorld());
            final Block targetBlock = rangeBlockHelper.getTargetBlock();
            snipeData.getVoxelList().add(new int[]{ targetBlock.getTypeId(), targetBlock.getData() });
            snipeData.getVoxelMessage().voxelList();
            return true;
        }
        else
        {
            if (split[0].equalsIgnoreCase("clear"))
            {
                snipeData.getVoxelList().clear();
                snipeData.getVoxelMessage().voxelList();
                return true;
            }
        }

        boolean remove = false;

        for (final String string : split)
        {
            String tmpint;
            Integer xint;
            Integer xdat;

            if (string.startsWith("-"))
            {
                remove = true;
                tmpint = string.replaceAll("-", "");
            }
            else
            {
                tmpint = string;
            }

            try
            {
                if (tmpint.contains(":"))
                {
                    String[] tempintsplit = tmpint.split(":");
                    xint = Integer.parseInt(tempintsplit[0]);
                    xdat = Integer.parseInt(tempintsplit[1]);
                }
                else
                {
                    xint = Integer.parseInt(tmpint);
                    xdat = -1;
                }

                if (Material.getMaterial(xint) != null && Material.getMaterial(xint).isBlock())
                {
                    if (!remove)
                    {
                        snipeData.getVoxelList().add(new int[]{ xint, xdat });
                        snipeData.getVoxelMessage().voxelList();
                    }
                    else
                    {
                        snipeData.getVoxelList().removeValue(new int[]{ xint, xdat });
                        snipeData.getVoxelMessage().voxelList();
                    }
                }

            }
            catch (NumberFormatException ignored)
            {
            }
        }
        return true;
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    @SuppressWarnings("deprecation")
    private boolean commandSniperVr(final Player player, final String[] split)
    {
        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        if (split.length == 0)
        {
            Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (targetBlock != null)
            {
                snipeData.setReplaceId(targetBlock.getTypeId());
                snipeData.getVoxelMessage().replace();
            }
            return true;
        }

        Material material = Material.matchMaterial(split[0]);
        if (material != null)
        {
            if (material.isBlock())
            {
                snipeData.setReplaceId(material.getId());
                snipeData.getVoxelMessage().replace();
                return true;
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
                return true;
            }
        }
        return false;
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperVs(final Player player, final String[] split)
    {
        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            player.sendMessage("Insufficient Permissions.");
            return true;
        }

        if (split.length >= 1)
        {
            if (split[0].equalsIgnoreCase("brushes"))
            {
                Multimap<Class<? extends IBrush>, String> registeredBrushesMultimap = plugin.getBrushManager().getRegisteredBrushesMultimap();
                List<String> allHandles = Lists.newLinkedList();
                for (Class<? extends IBrush> brushClass : registeredBrushesMultimap.keySet())
                {
                    allHandles.addAll(registeredBrushesMultimap.get(brushClass));
                }
                player.sendMessage(Joiner.on(", ").skipNulls().join(allHandles));
                return true;
            }
            else if (split[0].equalsIgnoreCase("range"))
            {
                SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
                if (split.length == 2)
                {
                    try
                    {
                        int range = Integer.parseInt(split[1]);
                        if (range < 0)
                        {
                            player.sendMessage("Negative values are not allowed.");
                        }
                        snipeData.setRange(range);
                        snipeData.setRanged(true);
                        snipeData.getVoxelMessage().toggleRange();

                    }
                    catch (NumberFormatException exception)
                    {
                        player.sendMessage("Can't parse number.");
                    }
                    return true;
                }
                else
                {
                    snipeData.setRanged(!snipeData.isRanged());
                    snipeData.getVoxelMessage().toggleRange();
                    return true;
                }
            }
            else if (split[0].equalsIgnoreCase("perf"))
            {
                player.sendMessage(ChatColor.AQUA + "Available performers (abbreviated):");
                player.sendMessage(PerformerE.performer_list_short);
                return true;
            }
            else if (split[0].equalsIgnoreCase("perflong"))
            {
                player.sendMessage(ChatColor.AQUA + "Available performers:");
                player.sendMessage(PerformerE.performer_list_long);
                return true;
            }
            else if (split[0].equalsIgnoreCase("enable"))
            {
                sniper.setEnabled(true);
                player.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
                return true;
            }
            else if (split[0].equalsIgnoreCase("disable"))
            {
                sniper.setEnabled(false);
                player.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
                return true;
            }
            else if (split[0].equalsIgnoreCase("toggle"))
            {
                sniper.setEnabled(!sniper.isEnabled());
                player.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
                return true;
            }
        }
        player.sendMessage(ChatColor.DARK_RED + "VoxelSniper - Current Brush Settings:");
        sniper.displayInfo();
        Sniper.SniperTool sniperTool = sniper.getSniperTool(sniper.getCurrentToolId());
        return true;
    }

    /**
     * @param player
     * @return true if command was processed, false otherwise
     */
    private boolean commandVChunk(final Player player)
    {
        player.getWorld().refreshChunk(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
        return true;
    }

    /**
     * @param event
     */
    @EventHandler(ignoreCancelled = false)
    public final void onPlayerInteract(final PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (!player.hasPermission(SNIPER_PERMISSION))
        {
            return;
        }

        try
        {
            Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
            if (sniper.isEnabled() && sniper.snipe(event.getAction(), event.getMaterial(), event.getClickedBlock(), event.getBlockFace()))
            {
                MetricsManager.increaseSnipeCounter();
                event.setCancelled(true);
            }
        }
        catch (final Exception ignored)
        {
        }
    }

    /**
     * @param event
     */
    @EventHandler
    public final void onPlayerJoin(final PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        if (player.hasPermission(SNIPER_PERMISSION) && plugin.getVoxelSniperConfiguration().isMessageOnLoginEnabled())
        {
            sniper.displayInfo();
        }
    }
}
