package com.thevoxelbox.voxelsniper;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Voxel
 * 
 */
public class VoxelSniper extends JavaPlugin {

    private static final String ITEMS_TXT = "items.txt";
    private static final String PLUGINS_VOXEL_SNIPER_ITEMS_TXT = "plugins/VoxelSniper/items.txt";
    private final VoxelSniperListener voxelSniperListener = new VoxelSniperListener(this);
    private final VoxelSniperEntity voxelSniperEntity = new VoxelSniperEntity();
    public static final Logger LOG = Logger.getLogger("Minecraft");
    protected static final Object ITEM_LOCK = new Object();
    private static HashMap<String, Integer> items;

    /**
     * Get Item name from id.
     * 
     * @param id
     * @return String
     */
    public static String getItem(final int id) {
        synchronized (VoxelSniper.ITEM_LOCK) {
            for (final String _name : VoxelSniper.items.keySet()) {
                if (VoxelSniper.items.get(_name) == id) {
                    return _name;
                }
            }
        }
        return String.valueOf(id);
    }

    /**
     * Get Item id from name.
     * 
     * @param name
     * @return int
     */
    public static int getItem(final String name) {
        synchronized (VoxelSniper.ITEM_LOCK) {
            if (VoxelSniper.items.containsKey(name)) {
                return VoxelSniper.items.get(name);
            }
        }
        return -1;
    }

    /**
     * Validate if item id is valid.
     * 
     * @param itemId
     * @return boolean
     */
    public static boolean isValidItem(final int itemId) {
        return VoxelSniper.items.containsValue(itemId);
    }

    /**
     * Load items from Item List file.
     */
    public final void loadItems() {
        final String _location = VoxelSniper.PLUGINS_VOXEL_SNIPER_ITEMS_TXT;
        final File _f = new File(VoxelSniper.ITEMS_TXT);
        final File _nf = new File(VoxelSniper.PLUGINS_VOXEL_SNIPER_ITEMS_TXT);
        if (_f.exists() && !_nf.exists()) {
            _f.delete();
        }

        if (!_nf.exists()) {
            _nf.getParentFile().mkdirs();
            this.saveResource(VoxelSniper.ITEMS_TXT, false);
        }

        synchronized (VoxelSniper.ITEM_LOCK) {
            VoxelSniper.items = new HashMap<String, Integer>();
            try {
                final Scanner _scanner = new Scanner(_nf);
                while (_scanner.hasNextLine()) {
                    final String _line = _scanner.nextLine();
                    if (_line.startsWith("#")) {
                        continue;
                    }
                    if (_line.equals("")) {
                        continue;
                    }
                    final String[] _split = _line.split(":");
                    VoxelSniper.items.put(_split[0], Integer.parseInt(_split[1]));
                }
                _scanner.close();
            } catch (final Exception _e) {
                VoxelSniper.LOG.log(Level.SEVERE, "Exception while reading " + _location + " (Are you sure you formatted it correctly?)", _e);
            }
        }
    }

    @Override
    public final boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (sender instanceof Player) {
            final Player _p = (Player) sender;
            final String _comm = command.getName();
            if (args == null) {
                if (!VoxelSniperListener.onCommand(_p, new String[0], _comm)) {
                    if (_p.isOp()) {
                        _p.sendMessage(ChatColor.RED + "Your name is not listed on the snipers.txt or you haven't /reload 'ed the server yet.");
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                if (!VoxelSniperListener.onCommand(_p, args, _comm)) {
                    if (_p.isOp()) {
                        _p.sendMessage(ChatColor.RED + "Your name is not listed on the snipers.txt or you haven't /reload 'ed the server yet.");
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

    @Override
    public final void onEnable() {
        this.loadItems();
        this.voxelSniperListener.loadConfig();        
        
        final PluginManager _pm = Bukkit.getPluginManager();
        _pm.registerEvents(this.voxelSniperListener, this);
        if (VoxelSniperListener.isSmiteVoxelFoxOffenders()) {
            _pm.registerEvents(this.voxelSniperEntity, this);
            VoxelSniper.LOG.info("[VoxelSniper] Entity Damage Event registered.");
        }
    }
}
