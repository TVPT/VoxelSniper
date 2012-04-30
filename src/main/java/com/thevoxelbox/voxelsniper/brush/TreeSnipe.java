package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;

/**
 *
 * @author Mick
 */
public class TreeSnipe extends Brush {
    private TreeType treeType = TreeType.TREE;
    
    public TreeSnipe() {
        name = "Tree Snipe";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        by = getLocation(v);
        single(v);
    }

    @Override
    public void powder(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        single(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        printTreeType(vm);
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Tree snipe brush:");
            v.p.sendMessage(ChatColor.AQUA + "/b ts treetype");
            printTreeType(v.vm);
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].equals("bigtree")) {
                treeType = TreeType.BIG_TREE;
                printTreeType(v.vm);
                break;
            }
            if (par[x].equals("birch")) {
                treeType = TreeType.BIRCH;
                printTreeType(v.vm);
                break;
            }
            if (par[x].equals("redwood")) {
                treeType = TreeType.REDWOOD;
                printTreeType(v.vm);
                break;
            }
            if (par[x].equals("tallredwood")) {
                treeType = TreeType.TALL_REDWOOD;
                printTreeType(v.vm);
                break;
            }
            if (par[x].equals("tree")) {
                treeType = TreeType.TREE;
                printTreeType(v.vm);
                break;
            }
            if (par[x].equals("jungle")) {
                treeType = TreeType.JUNGLE;
                printTreeType(v.vm);
                break;
            }
            if (par[x].equals("redmushroom")) {
                treeType = TreeType.RED_MUSHROOM;
                printTreeType(v.vm);
                break;
            }
            if (par[x].equals("brownmushroom")) {
                treeType = TreeType.BROWN_MUSHROOM;
                printTreeType(v.vm);
                break;
            }
        }

    }

    public void single(vSniper v) {
        try {
            //int id = clampY(bx,by,bz).getTypeId();
            if (clampY(bx,tb.getY(),bz).getTypeId()!= 18) {clampY(bx,tb.getY(),bz).setTypeId(3);} //makes its own dirt so it can go anywhere
            w.generateTree(new Location(w, (double) bx, (double) by, (double) bz), treeType);
            //s.getBlockAt(bx,by,bz).setTypeId(id); //replace the original block that was there.
        }
        catch (Exception e) {
            v.p.sendMessage("Nope");
        }
    }

    private int getLocation(vSniper v) {
        for (int i = 1; i < (256 - by); i++) {
            if (clampY(bx, by+i, bz).getType() == Material.AIR) { // Dont you mean != AIR ?  -- prz
                return by+i;                                            // But why are you even grabbing the highest Y ?
            }
        }
        return by;
    }
    
    private void printTreeType(vMessage vm) {
        switch (treeType) {
            case BIG_TREE:
                vm.custom(ChatColor.GRAY + "bigtree " + ChatColor.DARK_GRAY + "birch " + "redwood " + "tallredwood " + "tree" + "jungle" + "redmushroom" + "brownmushroom");
                break;

            case BIRCH:
                vm.custom(ChatColor.DARK_GRAY + "bigtree " + ChatColor.GRAY + "birch " + ChatColor.DARK_GRAY + "redwood " + "tallredwood " + "tree" + "jungle" + "redmushroom" + "brownmushroom");
                break;

            case REDWOOD:
                vm.custom(ChatColor.DARK_GRAY + "bigtree " + "birch " + ChatColor.GRAY + "redwood " + ChatColor.DARK_GRAY + "tallredwood " + "tree" + "jungle" + "redmushroom" + "brownmushroom");
                break;

            case TALL_REDWOOD:
                vm.custom(ChatColor.DARK_GRAY + "bigtree " + "birch " + "redwood " + ChatColor.GRAY + "tallredwood " + ChatColor.DARK_GRAY + "tree" + "jungle" + "redmushroom" + "brownmushroom");
                break;

            case TREE:
                vm.custom(ChatColor.DARK_GRAY + "bigtree " + "birch " + "redwood " + "tallredwood " + ChatColor.GRAY + "tree");
                break;
                
            case JUNGLE:
                vm.custom(ChatColor.DARK_GRAY + "bigtree " + "birch " + "redwood " + "tallredwood " + "tree" + ChatColor.GRAY + "jungle" + ChatColor.DARK_GRAY + "redmushroom" + "brownmushroom");
                break;    
                
            case RED_MUSHROOM:
                vm.custom(ChatColor.DARK_GRAY + "bigtree " + "birch " + "redwood " + "tallredwood " + "tree" +  "jungle" + ChatColor.GRAY + "redmushroom" + ChatColor.DARK_GRAY  + "brownmushroom");
                break;        
            
           case BROWN_MUSHROOM:
                vm.custom(ChatColor.DARK_GRAY + "bigtree " + "birch " + "redwood " + "tallredwood " + "tree" +  "jungle" + "redmushroom" + ChatColor.GRAY  + "brownmushroom");
                break;             
        }
    }
}
