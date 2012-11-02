package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#CopyPasta_Brush
 * @author giltwist
 */
public class CopyPastaBrush extends Brush {
	private static int timesUsed = 0;
	private static final int BLOCK_LIMIT = 10000;

    private boolean pasteAir = true; // False = no air, true = air
    private int points = 0; //
    private int numBlocks = 0;
    private int[] firstPoint = new int[3];
    private int[] secondPoint = new int[3];
    private int[] pastePoint = new int[3];
    private int[] minPoint = new int[3];
    private int[] offsetPoint = new int[3];
    private int[] blockArray;
    private byte[] dataArray;
    private int[] arraySize = new int[3];
    private int pivot = 0; // ccw degrees    

    /**
     * 
     */
    public CopyPastaBrush() {
        this.setName("CopyPasta");
    }

    private void doCopy(final SnipeData v) {
        for (int _i = 0; _i < 3; _i++) {
            this.arraySize[_i] = Math.abs(this.firstPoint[_i] - this.secondPoint[_i]) + 1;
            this.minPoint[_i] = Math.min(this.firstPoint[_i], this.secondPoint[_i]);
            this.offsetPoint[_i] = this.minPoint[_i] - this.firstPoint[_i]; // will always be negative or zero
        }
        
        this.numBlocks = (this.arraySize[0]) * (this.arraySize[1]) * (this.arraySize[2]);
        
        if (this.numBlocks > 0 && this.numBlocks < CopyPastaBrush.BLOCK_LIMIT) {
            this.blockArray = new int[this.numBlocks];
            this.dataArray = new byte[this.numBlocks];

            for (int _i = 0; _i < this.arraySize[0]; _i++) {
                for (int _j = 0; _j < this.arraySize[1]; _j++) {
                    for (int _k = 0; _k < this.arraySize[2]; _k++) {
                        final int _currentPos = _i + this.arraySize[0] * _j + this.arraySize[0] * this.arraySize[1] * _k;
                        this.blockArray[_currentPos] = this.getWorld().getBlockTypeIdAt(this.minPoint[0] + _i, this.minPoint[1] + _j, this.minPoint[2] + _k);
                        this.dataArray[_currentPos] = this.clampY(this.minPoint[0] + _i, this.minPoint[1] + _j, this.minPoint[2] + _k).getData();
                    }
                }
            }

            v.sendMessage(ChatColor.AQUA + "" + this.numBlocks + " blocks copied.");
        } else {
            v.sendMessage(ChatColor.RED + "Copy area too big: " + this.numBlocks + "(Limit: " + CopyPastaBrush.BLOCK_LIMIT + ")");
        }
    }

    private void doPasta(final SnipeData v) {
    	final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _i = 0; _i < this.arraySize[0]; _i++) {
            for (int _j = 0; _j < this.arraySize[1]; _j++) {
                for (int _k = 0; _k < this.arraySize[2]; _k++) {
                    final int _currentPos = _i + this.arraySize[0] * _j + this.arraySize[0] * this.arraySize[1] * _k;
                    Block _b = null;
                    
                    switch (this.pivot) {
                    case 180:
                        _b = this.clampY(this.pastePoint[0] - this.offsetPoint[0] - _i, this.pastePoint[1] + this.offsetPoint[1] + _j, this.pastePoint[2]
                                - this.offsetPoint[2] - _k);
                        break;
                    case 270:
                        _b = this.clampY(this.pastePoint[0] + this.offsetPoint[2] + _k, this.pastePoint[1] + this.offsetPoint[1] + _j, this.pastePoint[2]
                                - this.offsetPoint[0] - _i);
                        break;
                    case 90:
                        _b = this.clampY(this.pastePoint[0] - this.offsetPoint[2] - _k, this.pastePoint[1] + this.offsetPoint[1] + _j, this.pastePoint[2]
                                + this.offsetPoint[0] + _i);
                        break;
                    default: // assume no rotation
                        _b = this.clampY(this.pastePoint[0] + this.offsetPoint[0] + _i, this.pastePoint[1] + this.offsetPoint[1] + _j, this.pastePoint[2]
                                + this.offsetPoint[2] + _k);
                        break;
                    }

					if (!(this.blockArray[_currentPos] == 0 && !this.pasteAir)) {
						if (_b.getTypeId() != this.blockArray[_currentPos] || _b.getData() != this.dataArray[_currentPos]) {
							_undo.put(_b);
						}
                        _b.setTypeIdAndData(this.blockArray[_currentPos], this.dataArray[_currentPos], true);
                    }
                }
            }
        }
        v.sendMessage(ChatColor.AQUA + "" + this.numBlocks + " blocks pasted.");

        v.storeUndo(_undo);
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        switch (this.points) {
        case 0:
            this.firstPoint[0] = this.getTargetBlock().getX();
            this.firstPoint[1] = this.getTargetBlock().getY();
            this.firstPoint[2] = this.getTargetBlock().getZ();
            v.sendMessage(ChatColor.GRAY + "First point");
            this.points = 1;
            break;
        case 1:
            this.secondPoint[0] = this.getTargetBlock().getX();
            this.secondPoint[1] = this.getTargetBlock().getY();
            this.secondPoint[2] = this.getTargetBlock().getZ();
            v.sendMessage(ChatColor.GRAY + "Second point");
            this.points = 2;
            break;
        default:
            this.firstPoint = new int[3];
            this.secondPoint = new int[3];
            this.numBlocks = 0;
            this.blockArray = new int[1];
            this.dataArray = new byte[1];
            this.points = 0;
            v.sendMessage(ChatColor.GRAY + "Points cleared.");
            break;
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (this.points == 2) {
            if (this.numBlocks == 0) {
                this.doCopy(v);
            } else if (this.numBlocks > 0 && this.numBlocks < CopyPastaBrush.BLOCK_LIMIT) {
                this.pastePoint[0] = this.getTargetBlock().getX();
                this.pastePoint[1] = this.getTargetBlock().getY();
                this.pastePoint[2] = this.getTargetBlock().getZ();
                this.doPasta(v);
            } else {
                v.sendMessage(ChatColor.RED + "Error");
            }
        } else {
            v.sendMessage(ChatColor.RED + "You must select exactly two points.");
        }
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.custom(ChatColor.GOLD + "Paste air: " + this.pasteAir);
    	vm.custom(ChatColor.GOLD + "Pivot angle: " + this.pivot);
    }
    
    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
    	final String _param = par[1];
    	
    	if (_param.equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GOLD + "CopyPasta Parameters:");
    		v.sendMessage(ChatColor.AQUA + "/b cp air -- toggle include (default) or exclude  air during paste");
    		v.sendMessage(ChatColor.AQUA + "/b cp 0|90|180|270 -- toggle rotation (0 default)");
    		return;
    	}
    	
    	if (_param.equalsIgnoreCase("air")) {
    		this.pasteAir = !this.pasteAir;
    		
    		v.sendMessage(ChatColor.GOLD + "Paste air: " + this.pasteAir);
    		return;
    	}
    	
    	if (_param.equalsIgnoreCase("90") || _param.equalsIgnoreCase("180") || _param.equalsIgnoreCase("270") || _param.equalsIgnoreCase("0")) {
    		this.pivot = Integer.parseInt(_param);
    		v.sendMessage(ChatColor.GOLD + "Pivot angle: " + this.pivot);
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return CopyPastaBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	CopyPastaBrush.timesUsed = tUsed;
    }
}
