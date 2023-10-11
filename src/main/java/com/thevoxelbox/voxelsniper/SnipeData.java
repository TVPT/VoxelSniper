package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.util.Inker;
import com.thevoxelbox.voxelsniper.util.VoxelList;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/**
 * @author Piotr
 */
public class SnipeData
{

    public static final int DEFAULT_CYLINDER_CENTER = 0;
    public static final int DEFAULT_VOXEL_HEIGHT = 1;
    public static final int DEFAULT_BRUSH_SIZE = 3;
    public static final Material DEFAULT_MATERIAL = Material.AIR;
    public static final String DEFAULT_INK = "";

    private final Sniper owner;
    private Message voxelMessage;
    /**
     * Brush size -- set blockPositionY /b #.
     */
    private int brushSize = SnipeData.DEFAULT_BRUSH_SIZE;
    /**
     * Voxel List of ID's -- set blockPositionY /vl # # # -#.
     */
    private VoxelList voxelList = new VoxelList();
    /**
     * Voxel 'heigth' -- set blockPositionY /vh #.
     */
    private int voxelHeight = SnipeData.DEFAULT_VOXEL_HEIGHT;
    /**
     * Voxel centroid -- set Cylynder center /vc #.
     */
    private int cCen = SnipeData.DEFAULT_CYLINDER_CENTER;
    private int range = 0;
    private boolean ranged = false;
    private boolean lightning = false;
    private Material voxelMat = SnipeData.DEFAULT_MATERIAL;
    private String voxelInk = SnipeData.DEFAULT_INK;
    private BlockData voxelData = null;
    private Material replaceMat = SnipeData.DEFAULT_MATERIAL;
    private String replaceInk = SnipeData.DEFAULT_INK;
    private BlockData replaceData = null;
    private Tag<Material> tag = null;

    /**
     * @param vs
     */
    public SnipeData(final Sniper vs)
    {
        this.owner = vs;
    }

    /**
     * @return the brushSize
     */
    public final int getBrushSize()
    {
        return this.brushSize;
    }

    /**
     * @return the cCen
     */
    public final int getcCen()
    {
        return this.cCen;
    }

    /**
     * @return the voxelHeight
     */
    public final int getVoxelHeight()
    {
        return this.voxelHeight;
    }

    /**
     * @return the voxelList
     */
    public final VoxelList getVoxelList()
    {
        return this.voxelList;
    }

    /**
     * @return the voxelMessage
     */
    public final Message getVoxelMessage()
    {
        return this.voxelMessage;
    }

    /**
     * @return World
     */
    public final World getWorld()
    {
        return this.owner.getPlayer().getWorld();
    }

    /**
     * @return Sniper
     */
    public final Sniper owner()
    {
        return this.owner;
    }

    /**
     * Reset to default values.
     */
    public final void reset()
    {
        this.voxelMat = SnipeData.DEFAULT_MATERIAL;
        this.voxelInk = SnipeData.DEFAULT_INK;
        this.voxelData = null;
        this.replaceMat = SnipeData.DEFAULT_MATERIAL;
        this.replaceInk = SnipeData.DEFAULT_INK;
        this.replaceData = null;
        this.tag = null;
        this.brushSize = SnipeData.DEFAULT_BRUSH_SIZE;
        this.voxelHeight = SnipeData.DEFAULT_VOXEL_HEIGHT;
        this.cCen = SnipeData.DEFAULT_CYLINDER_CENTER;
        this.voxelList = new VoxelList();
    }

    /**
     * @param message
     */
    public final void sendMessage(final String message)
    {
        this.owner.getPlayer().sendMessage(message);
    }

    /**
     * @param brushSize
     *         the brushSize to set
     */
    public final void setBrushSize(final int brushSize)
    {
        this.brushSize = brushSize;
    }

    /**
     * @param cCen
     *         the cCen to set
     */
    public final void setcCen(final int cCen)
    {
        this.cCen = cCen;
    }

    /**
     * @param voxelHeight
     *         the voxelHeight to set
     */
    public final void setVoxelHeight(final int voxelHeight)
    {
        this.voxelHeight = voxelHeight;
    }

    /**
     * @param voxelList
     *         the voxelList to set
     */
    public final void setVoxelList(final VoxelList voxelList)
    {
        this.voxelList = voxelList;
    }

    /**
     * @param voxelMessage
     *         the voxelMessage to set
     */
    public final void setVoxelMessage(final Message voxelMessage)
    {
        this.voxelMessage = voxelMessage;
    }

    public int getRange()
    {
        return range;
    }

    public void setRange(int range)
    {
        this.range = range;
    }

    public boolean isRanged()
    {
        return ranged;
    }

    public void setRanged(boolean ranged)
    {
        this.ranged = ranged;
    }

    public boolean isLightningEnabled()
    {
        return lightning;
    }

    public void setLightningEnabled(boolean lightning)
    {
        this.lightning = lightning;
    }

    public BlockData getVoxelData()
    {
        if(this.voxelData == null) {
            this.voxelData = Inker.inkMat(voxelMat, voxelInk);
        }

        return voxelData;
    }

    public Material getVoxelMat()
    {
        return voxelMat;
    }

    public String getVoxelInk()
    {
        return voxelInk;
    }

    public BlockData getReplaceData()
    {
        if (this.replaceData == null) {
            this.replaceData = Inker.inkMat(replaceMat, replaceInk);
        }

        return replaceData;
    }

    public Material getReplaceMat()
    {
        return replaceMat;
    }

    public String getReplaceInk()
    {
        return replaceInk;
    }

    public Tag<Material> getTag()
    {
        return tag;
    }

    public void setVoxelMat(Material voxelMat)
    {
        this.voxelData = null;
        this.voxelMat = voxelMat;
    }

    public void setVoxelInk(String voxelInk)
    {
        this.voxelData = null;
        this.voxelInk = voxelInk;
    }

    public void setReplaceMat(Material replaceMat)
    {
        this.replaceData = null;
        this.replaceMat = replaceMat;
    }

    public void setReplaceInk(String replaceInk)
    {
        this.replaceData = null;
        this.replaceInk = replaceInk;
    }

    public void setTag(Tag<Material> tag)
    {
        this.tag = tag;
    }
}
