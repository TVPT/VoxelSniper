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
 *
 */
public class VoxelSniperListener implements Listener {

    /**
     * @param player
     * @param split
     * @param command
     * @return boolean Success.
     */
    public static boolean onCommand(final Player player, final String[] split, final String command) {
	boolean success = false;
        if (command.equalsIgnoreCase("vchunk")) {
            success = VoxelSniperListener.commandVChunk(player);
        } else if (command.equalsIgnoreCase("paint")) {
            success = VoxelSniperListener.commandPaint(player, split);
        } else if (command.equalsIgnoreCase("goto") && VoxelSniper.getSniperPermissionHelper().isSniper(player)) {
            success = VoxelSniperListener.commandGoto(player, split);
        } else if (VoxelSniper.getSniperPermissionHelper().isSniper(player) || VoxelSniper.getSniperPermissionHelper().isLiteSniper(player)) {
            if (command.equalsIgnoreCase("btool")) {
                success = VoxelSniperListener.commandSniperBTool(player, split);
            } else if (command.equalsIgnoreCase("uuu")) {
                success = VoxelSniperListener.commandSniperUUU(player);
            } else if (command.equalsIgnoreCase("uu")) {
                success = VoxelSniperListener.commandSniperUU(player, split);
            } else if (command.equalsIgnoreCase("u")) {
                success = VoxelSniperListener.commandSniperU(player, split);
            } else if (command.equalsIgnoreCase("d")) {
                success = VoxelSniperListener.commandSniperD(player);
            } else if (command.equalsIgnoreCase("vs")) {
                success = VoxelSniperListener.commandSniperVs(player, split);
            } else if (command.equalsIgnoreCase("vc")) {
                success = VoxelSniperListener.commandSniperVc(player, split);
            } else if (command.equalsIgnoreCase("vh")) {
                success = VoxelSniperListener.commandSniperVh(player, split);
            } else if (command.equalsIgnoreCase("vi")) {
                success = VoxelSniperListener.commandSniperVi(player, split);
            } else if (command.equalsIgnoreCase("vir")) {
                success = VoxelSniperListener.commandSniperVir(player, split);
            } else if (command.equalsIgnoreCase("vr")) {
                success = VoxelSniperListener.commandSniperVr(player, split);
            } else if (command.equalsIgnoreCase("vl")) {
                success = VoxelSniperListener.commandSniperVl(player, split);
            } else if (command.equalsIgnoreCase("v")) {
                success = VoxelSniperListener.commandSniperV(player, split);
            } else if (command.equalsIgnoreCase("b")) {
                success = VoxelSniperListener.commandSniperB(player, split);
            } else if (command.equalsIgnoreCase("p")) {
                success = VoxelSniperListener.commandSniperP(player, split);
            } else if (command.equalsIgnoreCase("bms")) {
                success = VoxelSniperListener.commandSniperBms(player, split);
            } else if (command.equalsIgnoreCase("bml")) {
                success = VoxelSniperListener.commandSniperBml(player, split);
            }
        }
	
	if (success) {
	    VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).renameItemInfo();
	}
	
        return success;
    }

    /**
     * @param player
     * @param split
     * @return
     */
    private static boolean commandGoto(final Player player, final String[] split) {
        if (VoxelSniper.getSniperPermissionHelper().isLiteSniper(player)) {
            player.sendMessage(ChatColor.RED + "A LiteSniper is not permitted to use this command.");
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
                PaintingWrapper.paint(player, false, false, Integer.parseInt(split[0]));
                return true;
            } catch (final Exception _e) {
                player.sendMessage(ChatColor.RED + "Invalid input!");
                return true;
            }
        } else {
            PaintingWrapper.paint(player, true, false, 0);
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
            final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);
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
            final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);
            _ps.loadPreset(Integer.parseInt(split[0]));
            return true;
        } catch (final Exception _e) {
            final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);
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
            final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);
            _ps.savePreset(Integer.parseInt(split[0]));
            return true;
        } catch (final Exception _e) {
            final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);
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
                        VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).addBrushTool(true);
                    } else if (split[1].equals("-powder")) {
                        VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).addBrushTool(false);
                    } else {
                        player.sendMessage(ChatColor.GREEN + "/btool add (-arrow|-powder) -- turns the item in your hand into a BrushTool");
                    }
                } else {
                    VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).addBrushTool();
                }
            } else if (split[0].equalsIgnoreCase("remove")) {
                VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).removeBrushTool();
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
            VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).reset();
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
            final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);
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
        final Sniper _vs = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);
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
            VoxelSniper.getSniperPermissionHelper().getSniperInstance(Bukkit.getPlayer(split[0]).getName()).doUndo();
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
            VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).doUndo();
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
            final Block _tb = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (_tb != null) {
                VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setVoxel(_tb.getTypeId());
            }
            return true;
        }

        if (VoxelSniper.getItem(split[0]) != -1) {
            final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);
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

            final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);

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
                VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setCentroid(0);
                return true;
            }
            VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setCentroid(Integer.parseInt(split[0]));
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
            VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setHeigth(Integer.parseInt(split[0]));
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
            final Block _tb = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (_tb != null) {
                VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setData(_tb.getData());
            }
            return true;
        }
        try {
            VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setData((byte) Integer.parseInt(split[0]));
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
            final Block _tb = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (_tb != null) {
                VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setReplaceData(_tb.getData());
            }
            return true;
        }
        try {
            VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setReplaceData((byte) Integer.parseInt(split[0]));
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
            final RangeBlockHelper _hb = new RangeBlockHelper(player, player.getWorld());
            final Block _tb = _hb.getTargetBlock();
            try {
                VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).addVoxelToList(_tb.getTypeId());

                return true;
            } catch (final Exception _e) {
                return true;
            }
        } else {
            if (split[0].equalsIgnoreCase("clear")) {
                VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).clearVoxelList();
                return true;
            }
        }

        final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);
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
            final Block _tb = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (_tb != null) {
                VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setReplace(_tb.getTypeId());
            }
            return true;
        }

        if (VoxelSniper.getItem(split[0]) != -1) {
            final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);
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

            final Sniper _ps = VoxelSniper.getSniperPermissionHelper().getSniperInstance(player);

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
                    VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).printBrushes();
                    return true;
                } else if (split[0].equalsIgnoreCase("brusheslong")) {
                    VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).printBrushesLong();
                    return true;
                } else if (split[0].equalsIgnoreCase("printout")) {
                    VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).togglePrintout();
                    return true;
                } else if (split[0].equalsIgnoreCase("lightning")) {
                    if (VoxelSniper.getSniperPermissionHelper().isLiteSniper(player)) {
                        player.sendMessage(ChatColor.RED + "A LiteSniper is not permitted to use this command.");
                        return true;
                    }
                    VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).toggleLightning();
                    return true;
                } else if (split[0].equalsIgnoreCase("weather")) {
                    if (VoxelSniper.getSniperPermissionHelper().isLiteSniper(player)) {
                        player.sendMessage(ChatColor.RED + "A LiteSniper is not permitted to use this command.");
                        return true;
                    }
                    player.getWorld().setWeatherDuration(0);
                    player.getWorld().setStorm(false);
                    player.sendMessage(ChatColor.GREEN + "Begone weather!");
                    return true;
                } else if (split[0].equalsIgnoreCase("range")) {
                    if (split.length == 2) {
                        final double _i = Double.parseDouble(split[1]);
                        if (VoxelSniper.getSniperPermissionHelper().isLiteSniper(player) && (_i > 12 || _i < -12)) {
                            player.sendMessage(ChatColor.RED + "A LiteSniper is not permitted to use ranges over 12.");
                            return true;
                        }
                        VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setRange(_i);
                        return true;
                    } else {
                        VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).setRange(-1);
                        return true;
                    }
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
            VoxelSniper.getSniperPermissionHelper().getSniperInstance(player).info();
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

    private final VoxelSniper plugin;

    /**
     * @param plugin
     */
    public VoxelSniperListener(final VoxelSniper plugin) {
        this.plugin = plugin;
        MetricsManager.setSnipeCounterInitTimeStamp(System.currentTimeMillis());
    }

    /**
     * @return VoxelSniper
     */
    public final VoxelSniper getPlugin() {
        return this.plugin;
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

        try {
            final Sniper _vs = VoxelSniper.getSniperPermissionHelper().getSniperInstance(_player);
            if (_vs == null) {
                return;
            } else if (_vs.snipe(_player, event.getAction(), event.getMaterial(), event.getClickedBlock(), event.getBlockFace())) {
                MetricsManager.increaseSnipeCounter();
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
        if (VoxelSniper.getSniperPermissionHelper().isSniper(_p)) {
            try {
                final Sniper _vs = VoxelSniper.getSniperPermissionHelper().getSniperInstance(_p);
                _vs.setPlayer(_p);
                _vs.info();
		_vs.renameItemInfo();
                return;
            } catch (final Exception _e) {
                return;
            }
        }
        if (VoxelSniper.getSniperPermissionHelper().isLiteSniper(_p)) {
            try {
                final Sniper _vs = VoxelSniper.getSniperPermissionHelper().getSniperInstance(_p);
                if (_vs instanceof LiteSniper) {
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
}
