package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.perform.PerformerE;
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

import java.util.logging.Level;

/**
 * @author Voxel
 */
public class VoxelSniperListener implements Listener
{

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
        else if (command.equalsIgnoreCase("goto") && VoxelSniper.getInstance().getSniperPermissionHelper().isSniper(player))
        {
            return commandGoto(player, split);
        }
        else if (VoxelSniper.getInstance().getSniperPermissionHelper().isSniper(player) || VoxelSniper.getInstance().getSniperPermissionHelper().isLiteSniper(player))
        {
            if (command.equalsIgnoreCase("btool"))
            {
                return commandSniperBTool(player, split);
            }
            else if (command.equalsIgnoreCase("uuu"))
            {
                return commandSniperUUU(player);
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
            else if (command.equalsIgnoreCase("bms"))
            {
                return commandSniperBms(player, split);
            }
            else if (command.equalsIgnoreCase("bml"))
            {
                return commandSniperBml(player, split);
            }
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
        if (VoxelSniper.getInstance().getSniperPermissionHelper().isLiteSniper(player))
        {
            player.sendMessage(ChatColor.RED + "LiteSnipers are not permitted to use this command.");
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
        try
        {
            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
            try
            {
                if (split == null || split.length == 0)
                {
                    sniper.previousBrush();
                    // player.sendMessage(ChatColor.RED + "Please input a brush size.");
                    return true;
                }
                else
                {
                    sniper.setBrushSize(Integer.parseInt(split[0]));
                    return true;
                }
            }
            catch (final Exception exception)
            {
                sniper.fillPrevious();
                sniper.setBrush(split);
                return true;
            }
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
    private boolean commandSniperBml(final Player player, final String[] split)
    {
        try
        {
            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
            sniper.loadPreset(Integer.parseInt(split[0]));
            return true;
        }
        catch (final Exception exception)
        {
            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
            sniper.loadPreset(split[0]);
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperBms(final Player player, final String[] split)
    {
        try
        {
            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
            sniper.savePreset(Integer.parseInt(split[0]));
            return true;
        }
        catch (final Exception exception)
        {
            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
            sniper.savePreset(split[0]);
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperBTool(final Player player, final String[] split)
    {
        if (split != null && split.length > 0)
        {
            if (split[0].equalsIgnoreCase("add"))
            {
                if (split.length == 2)
                {
                    if (split[1].equals("-arrow"))
                    {
                        VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).addBrushTool(true);
                    }
                    else if (split[1].equals("-powder"))
                    {
                        VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).addBrushTool(false);
                    }
                    else
                    {
                        player.sendMessage(ChatColor.GREEN + "/btool add (-arrow|-powder) -- turns the item in your hand into a BrushTool");
                    }
                }
                else
                {
                    VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).addBrushTool();
                }
            }
            else if (split[0].equalsIgnoreCase("remove"))
            {
                VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).removeBrushTool();
            }
            else
            {
                player.sendMessage(ChatColor.GREEN + "/btool add (-arrow|-powder) -- turns the item in your hand into a BrushTool");
                player.sendMessage(ChatColor.GRAY + "/btool remove -- turns the BrushTool in your hand into a regular item");
            }
        }
        else
        {
            player.sendMessage(ChatColor.GREEN + "/btool add (-arrow|-powder) -- turns the item in your hand into a BrushTool");
            player.sendMessage(ChatColor.GRAY + "/btool remove -- turns the BrushTool in your hand into a regular item");
        }
        return true;
    }

    /**
     * @param player
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperD(final Player player)
    {
        try
        {
            VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).reset();
            player.sendMessage(ChatColor.AQUA + "Brush settings reset to their default values.");
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage("Not valid.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperP(final Player player, final String[] split)
    {
        try
        {
            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
            if (split == null || split.length == 0)
            {
                sniper.setPerformer(new String[]{"", "m"});
            }
            else
            {
                sniper.setPerformer(split);
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
        final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
        try
        {
            final int r = Integer.parseInt(split[0]);
            sniper.doUndo(r);
        }
        catch (final Exception exception)
        {
            sniper.doUndo();
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
        try
        {
            VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(Bukkit.getPlayer(split[0]).getName()).doUndo();
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
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperUUU(final Player player)
    {
        try
        {
            VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).doUndo();
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
        if (split.length == 0)
        {
            final Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (targetBlock != null)
            {
                VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setVoxel(targetBlock.getTypeId());
            }
            return true;
        }

        Material material = Material.matchMaterial(split[0]);
        if (material != null)
        {
            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
            if (material.isBlock())
            {
                sniper.setVoxel(material.getId());
                return true;
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
                return true;
            }
        }
        else
        {
            final Material material1 = Material.matchMaterial(split[0]);
            if (material1 == null)
            {
                player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
                return true;
            }

            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);

            if (material1.isBlock())
            {
                sniper.setVoxel(material1.getId());
                return true;
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
                return true;
            }
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperVc(final Player player, final String[] split)
    {
        try
        {
            if (split.length == 0)
            {
                VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setCentroid(0);
                return true;
            }
            VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setCentroid(Integer.parseInt(split[0]));
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
        try
        {
            VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setHeigth(Integer.parseInt(split[0]));
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
        if (split.length == 0)
        {
            final Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (targetBlock != null)
            {
                VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setData(targetBlock.getData());
            }
            return true;
        }
        try
        {
            VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setData((byte) Integer.parseInt(split[0]));
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage(ChatColor.RED + "Invalid input, please enter a number.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    @SuppressWarnings("deprecation")
	private boolean commandSniperVir(final Player player, final String[] split)
    {
        if (split.length == 0)
        {
            final Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (targetBlock != null)
            {
                VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setReplaceData(targetBlock.getData());
            }
            return true;
        }
        try
        {
            VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setReplaceData((byte) Integer.parseInt(split[0]));
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage(ChatColor.RED + "Invalid input, please enter a number.");
            return true;
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    @SuppressWarnings("deprecation")
	private boolean commandSniperVl(final Player player, final String[] split)
    {
        if (split.length == 0)
        {
            final RangeBlockHelper rangeBlockHelper = new RangeBlockHelper(player, player.getWorld());
            final Block targetBlock = rangeBlockHelper.getTargetBlock();
            try
            {
                VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).addVoxelToList(new int[] {targetBlock.getTypeId(), targetBlock.getData()});

                return true;
            }
            catch (final Exception exception)
            {
                return true;
            }
        }
        else
        {
            if (split[0].equalsIgnoreCase("clear"))
            {
                VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).clearVoxelList();
                return true;
            }
        }

        final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
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
                        sniper.addVoxelToList(new int[]{xint, xdat});
                    }
                    else
                    {
                        sniper.removeVoxelFromList(new int[]{xint, xdat});
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
        if (split.length == 0)
        {
            final Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (targetBlock != null)
            {
                VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setReplace(targetBlock.getTypeId());
            }
            return true;
        }

        Material material = Material.matchMaterial(split[0]);
        if (material != null)
        {
            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
            if (material.isBlock())
            {
                sniper.setReplace(material.getId());
                return true;
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
                return true;
            }
        }
        else
        {
            final Material material1 = Material.matchMaterial(split[0]);
            if (material1 == null)
            {
                player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
                return true;
            }

            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);

            if (material1.isBlock())
            {
                sniper.setReplace(material1.getId());
                return true;
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
                return true;
            }
        }
    }

    /**
     * @param player
     * @param split
     * @return true if command was processed, false otherwise
     */
    private boolean commandSniperVs(final Player player, final String[] split)
    {
        try
        {
            if (split.length >= 1)
            {
                if (split[0].equalsIgnoreCase("brushes"))
                {
                    VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).printBrushes();
                    return true;
                }
                else if (split[0].equalsIgnoreCase("printout"))
                {
                    VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).togglePrintout();
                    return true;
                }
                else if (split[0].equalsIgnoreCase("lightning"))
                {
                    if (VoxelSniper.getInstance().getSniperPermissionHelper().isLiteSniper(player))
                    {
                        player.sendMessage(ChatColor.RED + "LiteSnipers are not permitted to use this command.");
                        return true;
                    }
                    VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).toggleLightning();
                    return true;
                }
                else if (split[0].equalsIgnoreCase("weather"))
                {
                    if (VoxelSniper.getInstance().getSniperPermissionHelper().isLiteSniper(player))
                    {
                        player.sendMessage(ChatColor.RED + "LiteSnipers are not permitted to use this command.");
                        return true;
                    }
                    player.getWorld().setWeatherDuration(0);
                    player.getWorld().setStorm(false);
                    player.sendMessage(ChatColor.GREEN + "Begone weather!");
                    return true;
                }
                else if (split[0].equalsIgnoreCase("range"))
                {
                    if (split.length == 2)
                    {
                        final double i = Double.parseDouble(split[1]);
                        if (VoxelSniper.getInstance().getSniperPermissionHelper().isLiteSniper(player) && (i > 12 || i < -12))
                        {
                            player.sendMessage(ChatColor.RED + "LiteSnipers are not permitted to use ranges over 12.");
                            return true;
                        }
                        VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setRange(i);
                        return true;
                    }
                    else
                    {
                        VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).setRange(-1);
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
                    Sniper sniperInstance = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
                    sniperInstance.setEnabled(true);
                    sniperInstance.getVoxelMessage().brushMessage("VoxelSniper is " + (sniperInstance.isEnabled() ? "enabled" : "disabled"));
                    return true;
                }
                else if (split[0].equalsIgnoreCase("disable"))
                {
                    Sniper sniperInstance = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
                    sniperInstance.setEnabled(false);
                    sniperInstance.getVoxelMessage().brushMessage("VoxelSniper is " + (sniperInstance.isEnabled() ? "enabled" : "disabled"));
                    return true;
                }
                else if (split[0].equalsIgnoreCase("toggle"))
                {
                    Sniper sniperInstance = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
                    sniperInstance.setEnabled(!sniperInstance.isEnabled());
                    sniperInstance.getVoxelMessage().brushMessage("VoxelSniper is " + (sniperInstance.isEnabled() ? "enabled" : "disabled"));
                    return true;
                }
            }
            player.sendMessage(ChatColor.DARK_RED + "VoxelSniper - Current Brush Settings:");
            VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player).info();
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage(ChatColor.RED + "You are not allowed to use this command.");
            return true;
        }
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
     * @return VoxelSniper
     */
    public final VoxelSniper getPlugin()
    {
        return this.plugin;
    }

    /**
     * @param event
     */
    @EventHandler
    public final void onPlayerInteract(final PlayerInteractEvent event)
    {
        if (event.isBlockInHand())
        {
            return;
        }
        final Player player = event.getPlayer();

        try
        {
            final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
            if (sniper != null)
            {
                if (sniper.isEnabled() && sniper.snipe(player, event.getAction(), event.getMaterial(), event.getClickedBlock(), event.getBlockFace()))
                {
                    MetricsManager.increaseSnipeCounter();
                    event.setCancelled(true);
                }
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
        final Player player = event.getPlayer();
        if (VoxelSniper.getInstance().getSniperPermissionHelper().isSniper(player))
        {
            try
            {
                final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
                sniper.setPlayer(player);
                sniper.info();
                return;
            }
            catch (Exception ignored)
            {
                return;
            }
        }
        if (VoxelSniper.getInstance().getSniperPermissionHelper().isLiteSniper(player))
        {
            try
            {
                final Sniper sniper = VoxelSniper.getInstance().getSniperPermissionHelper().getSniperInstance(player);
                if (sniper instanceof LiteSniper)
                {
                    sniper.setPlayer(player);
                    sniper.info();
                }
            }
            catch (Exception ignored)
            {
            }
        }
    }
}
