/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 *
 * @author psanker
 */
public class Extrude extends Brush {

    int level;
    double trueCircle;
    boolean awto;
    
    public Extrude() {
        name = "Extrude";
    }
    
    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.height();
        vm.voxelList();
        if (trueCircle == 0.5)
            vm.custom(ChatColor.AQUA + "True circle mode ON.");
        else
            vm.custom(ChatColor.AQUA + "True circle mode OFF.");
    }
    
    @Override
    public void parameters(String par[], vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Extrude brush Parameters:");
            v.p.sendMessage(ChatColor.AQUA + "/b ex true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ex false will switch back. (false is default)");
            return;
        }
        
        for (int i = 1; i < par.length; i++) {
            try {
                if (par[i].startsWith("true")) {
                    trueCircle = 0.5;
                    v.p.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                    continue;
                } else if (par[i].startsWith("false")) {
                    trueCircle = 0;
                    v.p.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                    continue;
                } else {
                    v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                    return;     
                }
            } catch (Exception e) {
                v.p.sendMessage(ChatColor.RED+"Incorrect parameter \""+par[i]+"\"; use the \"info\" parameter.");
            }
        }
    }
    
    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        awto = false;
        
        pre(v, tb.getFace(lb));
    }
    
    @Override
    public void powder(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        awto = true;
        
        pre(v, tb.getFace(lb));
    }
    
    private void pre(vSniper v, BlockFace bf) {
        if (bf == null)
            return;
        
        level = v.voxelHeight;
        
        if (level == 0) {
            return;
        } else if (!awto) {
            if (level > 0) {
                level = -1 * level;
            }
        } else if (awto) {
            if (level < 0) {
                level = -1 * level;
            }
        }
           
        
        switch (bf) {
            case NORTH:
                extrudeN(v);
                break;
                
            case SOUTH:
                extrudeS(v);
                break;

            case EAST:
                extrudeE(v);
                break;
                
            case WEST:
                extrudeW(v);
                break;

            case UP:
                extrudeU(v);
                break;
                
            case DOWN:
                extrudeD(v);
                break;

            default:
                break;
        }
    }

    private void extrudeS(vSniper v) {
        int bsize = v.brushSize;
        
        vUndo h = new vUndo(tb.getWorld().getName());

        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (awto) {
                        for (int i = 0; i <= level - 1; i++) {
                            h = perform(clampY(bx + i, by + x, bz + y), clampY(bx + i + 1, by + x, bz + y), v, h);
                            h = perform(clampY(bx + i, by + x, bz - y), clampY(bx + i + 1, by + x, bz - y), v, h);
                            h = perform(clampY(bx + i, by - x, bz + y), clampY(bx + i + 1, by - x, bz + y), v, h);
                            h = perform(clampY(bx + i, by - x, bz - y), clampY(bx + i + 1, by - x, bz - y), v, h);
                        }
                    } else {
                        for (int i = 0; i >= level + 1; i--) {
                            h = perform(clampY(bx + i, by + x, bz + y), clampY(bx + i - 1, by + x, bz + y), v, h);
                            h = perform(clampY(bx + i, by + x, bz - y), clampY(bx + i - 1, by + x, bz - y), v, h);
                            h = perform(clampY(bx + i, by - x, bz + y), clampY(bx + i - 1, by - x, bz + y), v, h);
                            h = perform(clampY(bx + i, by - x, bz - y), clampY(bx + i - 1, by - x, bz - y), v, h);
                        }
                    }
                }
            }
        }
        
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }
    
    private void extrudeN(vSniper v) {
        int bsize = v.brushSize;
        
        vUndo h = new vUndo(tb.getWorld().getName());

        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (awto) {
                        for (int i = 0; i <= level - 1; i++) {
                            h = perform(clampY(bx - i, by + x, bz + y), clampY(bx - i - 1, by + x, bz + y), v, h);
                            h = perform(clampY(bx - i, by + x, bz - y), clampY(bx - i - 1, by + x, bz - y), v, h);
                            h = perform(clampY(bx - i, by - x, bz + y), clampY(bx - i - 1, by - x, bz + y), v, h);
                            h = perform(clampY(bx - i, by - x, bz - y), clampY(bx - i - 1, by - x, bz - y), v, h);
                        }
                    } else {
                        for (int i = 0; i >= level + 1; i--) {
                            h = perform(clampY(bx - i, by + x, bz + y), clampY(bx - i + 1, by + x, bz + y), v, h);
                            h = perform(clampY(bx - i, by + x, bz - y), clampY(bx - i + 1, by + x, bz - y), v, h);
                            h = perform(clampY(bx - i, by - x, bz + y), clampY(bx - i + 1, by - x, bz + y), v, h);
                            h = perform(clampY(bx - i, by - x, bz - y), clampY(bx - i + 1, by - x, bz - y), v, h);
                        }
                    }
                }
            }
        }
        
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    private void extrudeW(vSniper v) {
        int bsize = v.brushSize;

        vUndo h = new vUndo(tb.getWorld().getName());
        
        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                   if (awto) {
                        for (int i = 0; i <= level - 1; i++) {     
                            h = perform(clampY(bx + x, by + y, bz + i), clampY(bx + x, by + y, bz + i + 1), v, h);
                            h = perform(clampY(bx + x, by - y, bz + i), clampY(bx + x, by - y, bz + i + 1), v, h);
                            h = perform(clampY(bx - x, by + y, bz + i), clampY(bx - x, by + y, bz + i + 1), v, h);
                            h = perform(clampY(bx - x, by - y, bz + i), clampY(bx - x, by - y, bz + i + 1), v, h);
                        }
                   } else {
                        for (int i = 0; i >= level + 1; i--) {     
                            h = perform(clampY(bx + x, by + y, bz + i), clampY(bx + x, by + y, bz + i - 1), v, h);
                            h = perform(clampY(bx + x, by - y, bz + i), clampY(bx + x, by - y, bz + i - 1), v, h);
                            h = perform(clampY(bx - x, by + y, bz + i), clampY(bx - x, by + y, bz + i - 1), v, h);
                            h = perform(clampY(bx - x, by - y, bz + i), clampY(bx - x, by - y, bz + i - 1), v, h);
                        }
                   }
                }
            }
        }
        
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }
    
    private void extrudeE(vSniper v) {
        int bsize = v.brushSize;

        vUndo h = new vUndo(tb.getWorld().getName());
        
        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                   if (awto) {
                        for (int i = 0; i <= level - 1; i++) {     
                            h = perform(clampY(bx + x, by + y, bz - i), clampY(bx + x, by + y, bz - i - 1), v, h);
                            h = perform(clampY(bx + x, by - y, bz - i), clampY(bx + x, by - y, bz - i - 1), v, h);
                            h = perform(clampY(bx - x, by + y, bz - i), clampY(bx - x, by + y, bz - i - 1), v, h);
                            h = perform(clampY(bx - x, by - y, bz - i), clampY(bx - x, by - y, bz - i - 1), v, h);
                        }
                   } else {
                        for (int i = 0; i >= level + 1; i--) {     
                            h = perform(clampY(bx + x, by + y, bz - i), clampY(bx + x, by + y, bz - i + 1), v, h);
                            h = perform(clampY(bx + x, by - y, bz - i), clampY(bx + x, by - y, bz - i + 1), v, h);
                            h = perform(clampY(bx - x, by + y, bz - i), clampY(bx - x, by + y, bz - i + 1), v, h);
                            h = perform(clampY(bx - x, by - y, bz - i), clampY(bx - x, by - y, bz - i + 1), v, h);
                        }
                   }
                }
            }
        }
        
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    private void extrudeU(vSniper v) {
        int bsize = v.brushSize;

        vUndo h = new vUndo(tb.getWorld().getName());
        
        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (awto) {
                        for (int i = 0; i <= level - 1; i++) {
                            h = perform(clampY(bx + x, by + i, bz + y), clampY(bx + x, by + i + 1, bz + y), v, h);
                            h = perform(clampY(bx + x, by + i, bz - y), clampY(bx + x, by + i + 1, bz - y), v, h);
                            h = perform(clampY(bx - x, by + i, bz + y), clampY(bx - x, by + i + 1, bz + y), v, h);
                            h = perform(clampY(bx - x, by + i, bz - y), clampY(bx - x, by + i + 1, bz - y), v, h);
                        }
                    } else {
                        for (int i = 0; i >= level + 1; i--) {
                            h = perform(clampY(bx + x, by + i, bz + y), clampY(bx + x, by + i - 1, bz + y), v, h);
                            h = perform(clampY(bx + x, by + i, bz - y), clampY(bx + x, by + i - 1, bz - y), v, h);
                            h = perform(clampY(bx - x, by + i, bz + y), clampY(bx - x, by + i - 1, bz + y), v, h);
                            h = perform(clampY(bx - x, by + i, bz - y), clampY(bx - x, by + i - 1, bz - y), v, h);
                        }
                    }
                }
            }
        }
        
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }
    
    private void extrudeD(vSniper v) {
        int bsize = v.brushSize;

        vUndo h = new vUndo(tb.getWorld().getName());
        
        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (awto) {
                        for (int i = 0; i <= level - 1; i++) {
                            h = perform(clampY(bx + x, by - i, bz + y), clampY(bx + x, by - i - 1, bz + y), v, h);
                            h = perform(clampY(bx + x, by - i, bz - y), clampY(bx + x, by - i - 1, bz - y), v, h);
                            h = perform(clampY(bx - x, by - i, bz + y), clampY(bx - x, by - i - 1, bz + y), v, h);
                            h = perform(clampY(bx - x, by - i, bz - y), clampY(bx - x, by - i - 1, bz - y), v, h);
                        }
                    } else {
                        for (int i = 0; i >= level + 1; i--) {
                            h = perform(clampY(bx + x, by - i, bz + y), clampY(bx + x, by - i + 1, bz + y), v, h);
                            h = perform(clampY(bx + x, by - i, bz - y), clampY(bx + x, by - i + 1, bz - y), v, h);
                            h = perform(clampY(bx - x, by - i, bz + y), clampY(bx - x, by - i + 1, bz + y), v, h);
                            h = perform(clampY(bx - x, by - i, bz - y), clampY(bx - x, by - i + 1, bz - y), v, h);
                        }
                    }
                }
            }
        }
        
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }
    
    private vUndo perform(Block b1, Block b2, vSniper v, vUndo h) {
        if ((b2.getY() > 128) || (b2.getY() < 0))
            return h;
        
        if (v.voxelList.contains(getBlockIdAt(b1.getX(), b1.getY(), b1.getZ()))) {
            h.put(b2);
            setBlockIdAt(getBlockIdAt(b1.getX(), b1.getY(), b1.getZ()), b2.getX(), b2.getY(), b2.getZ());
            clampY(b2.getX(), b2.getY(), b2.getZ()).setData(clampY(b1.getX(), b1.getY(), b1.getZ()).getData());
        }
        
        return h;
    }
}
