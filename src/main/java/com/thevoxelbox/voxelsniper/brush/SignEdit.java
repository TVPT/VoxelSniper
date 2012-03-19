package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 *
 * @author Razorcane
 */
public class SignEdit extends Brush{
    
    String[] lines;
    
    public SignEdit() {
        name = "Sign Edit";
    }
    
    @Override
    public void arrow(vSniper v){
        init();
    }
    
    @Override
    public void powder(vSniper v){
        init();
    }
    
    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }
    
    public void init(){
        Block b = tb;
        if(b.getType() != Material.SIGN_POST && b.getType() != Material.WALL_SIGN){
            return;
        }
        
        if(b.getType() == Material.SIGN_POST){
            Sign targetState = (Sign) tb.getState();
            lines = targetState.getLines();
            b.setType(Material.SIGN_POST);
            for(int i = 1; i < 4; i++){
                targetState.setLine(i, lines[i-1]);
            }
            targetState.update(true);
        }
        
        if(b.getType() == Material.WALL_SIGN){
            Sign targetState = (Sign) tb.getState();
            lines = targetState.getLines();
            b.setType(Material.WALL_SIGN);
            for(int i = 1; i < 4; i++){
                targetState.setLine(i, lines[i-1]);
            }
            targetState.update(true);
        }
    }

}
