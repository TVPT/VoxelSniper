package com.thevoxelbox.voxelsniper;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Voxel
 */
public class HitBlox {

    private static final int MAXIMUM_WORLD_HEIGHT = 255;
    private static final double DEFAULT_PLAYER_VIEW_HEIGHT = 1.65;
    private static final double DEFAULT_LOCATION_VIEW_HEIGHT = 0;
    private static final double DEFAULT_STEP = 0.2;
    private static final int DEFAULT_RANGE = 250;
    private Location playerLoc;
    private double rotX, rotY, viewHeight, rotXSin, rotXCos, rotYSin, rotYCos;
    private double length, hLength, step;
    private double range;
    private double playerX, playerY, playerZ;
    private double xOffset, yOffset, zOffset;
    private int lastX, lastY, lastZ;
    private int targetX, targetY, targetZ;
    private World w;

    /**
     * Constructor requiring location, uses default values.
     * 
     * @param location
     */
    public HitBlox(final Location location) {
        this.init(location, HitBlox.DEFAULT_RANGE, HitBlox.DEFAULT_STEP, HitBlox.DEFAULT_LOCATION_VIEW_HEIGHT);
    }

    /**
     * Constructor requiring location, max range, and a stepping value.
     * 
     * @param location
     * @param range
     * @param step
     */
    public HitBlox(final Location location, final int range, final double step) {
        this.w = location.getWorld();
        this.init(location, range, step, HitBlox.DEFAULT_LOCATION_VIEW_HEIGHT);
    }

    /**
     * Constructor requiring player, max range, and a stepping value.
     * 
     * @param player
     * @param range
     * @param step
     */
    public HitBlox(final Player player, final int range, final double step) {
        this.init(player.getLocation(), range, step, HitBlox.DEFAULT_PLAYER_VIEW_HEIGHT);
    }

    /**
     * Constructor requiring player, uses default values.
     * 
     * @param player
     */
    public HitBlox(final Player player, final World world) {
        this.w = world;
        this.init(player.getLocation(), HitBlox.DEFAULT_RANGE, HitBlox.DEFAULT_STEP, HitBlox.DEFAULT_PLAYER_VIEW_HEIGHT);
        // values
    }

    /**
     * @param player
     * @param world
     * @param range
     */
    public HitBlox(final Player player, final World world, final double range) {
        this.w = world;
        this.init(player.getLocation(), range, HitBlox.DEFAULT_STEP, HitBlox.DEFAULT_PLAYER_VIEW_HEIGHT);
        this.fromOffworld();
    }

    /**
     * 
     */
    public final void fromOffworld() {
        if (this.targetY > HitBlox.MAXIMUM_WORLD_HEIGHT) {
            while (this.targetY > HitBlox.MAXIMUM_WORLD_HEIGHT && this.length <= this.range) {
                this.lastX = this.targetX;
                this.lastY = this.targetY;
                this.lastZ = this.targetZ;

                do {
                    this.length += this.step;

                    this.hLength = (this.length * this.rotYCos);
                    this.yOffset = (this.length * this.rotYSin);
                    this.xOffset = (this.hLength * this.rotXCos);
                    this.zOffset = (this.hLength * this.rotXSin);

                    this.targetX = (int) Math.floor(this.xOffset + this.playerX);
                    this.targetY = (int) Math.floor(this.yOffset + this.playerY);
                    this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);

                } while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));
            }
        } else if (this.targetY < 0) {
            while (this.targetY < 0 && this.length <= this.range) {
                this.lastX = this.targetX;
                this.lastY = this.targetY;
                this.lastZ = this.targetZ;

                do {
                    this.length += this.step;

                    this.hLength = (this.length * this.rotYCos);
                    this.yOffset = (this.length * this.rotYSin);
                    this.xOffset = (this.hLength * this.rotXCos);
                    this.zOffset = (this.hLength * this.rotXSin);

                    this.targetX = (int) Math.floor(this.xOffset + this.playerX);
                    this.targetY = (int) Math.floor(this.yOffset + this.playerY);
                    this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);

                } while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));
            }
        } else {
            return;
        }
    }

    /**
     * Returns the current block along the line of vision.
     * 
     * @return Block
     */
    public final Block getCurBlock() {
        if (this.length > this.range || this.targetY > HitBlox.MAXIMUM_WORLD_HEIGHT || this.targetY < 0) {
            return null;
        } else {
            return this.w.getBlockAt(this.targetX, this.targetY, this.targetZ);
        }
    }

    /**
     * Returns the block attached to the face at the cursor, or null if out of range.
     * 
     * @return Block
     */
    public final Block getFaceBlock() {
        while ((this.getNextBlock() != null) && (this.getCurBlock().getTypeId() == 0)) {
        }

        if (this.getCurBlock() != null) {
            return this.getLastBlock();
        } else {
            return null;
        }
    }

    /**
     * Returns the previous block along the line of vision.
     * 
     * @return Block
     */
    public final Block getLastBlock() {
        if (this.lastY > HitBlox.MAXIMUM_WORLD_HEIGHT || this.lastY < 0) {
            return null;
        }
        return this.w.getBlockAt(this.lastX, this.lastY, this.lastZ);
    }

    /**
     * Returns STEPS forward along line of vision and returns block.
     * 
     * @return Block
     */
    public final Block getNextBlock() {
        this.lastX = this.targetX;
        this.lastY = this.targetY;
        this.lastZ = this.targetZ;

        do {
            this.length += this.step;

            this.hLength = (this.length * this.rotYCos);
            this.yOffset = (this.length * this.rotYSin);
            this.xOffset = (this.hLength * this.rotXCos);
            this.zOffset = (this.hLength * this.rotXSin);

            this.targetX = (int) Math.floor(this.xOffset + this.playerX);
            this.targetY = (int) Math.floor(this.yOffset + this.playerY);
            this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);

        } while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));

        if (this.length > this.range || this.targetY > HitBlox.MAXIMUM_WORLD_HEIGHT || this.targetY < 0) {
            return null;
        }

        return this.w.getBlockAt(this.targetX, this.targetY, this.targetZ);
    }

    /**
     * @return Block
     */
    public final Block getRangeBlock() {
        this.fromOffworld();
        if (this.length > this.range) {
            return null;
        } else {
            return this.getRange();
        }
    }

    /**
     * Returns the block at the cursor, or null if out of range.
     * 
     * @return Block
     */
    public final Block getTargetBlock() {
        this.fromOffworld();
        while ((this.getNextBlock() != null) && (this.getCurBlock().getTypeId() == 0)) {

        }
        return this.getCurBlock();
    }

    /**
     * Sets current block type id.
     * 
     * @param type
     */
    public final void setCurBlock(final int type) {
        if (this.getCurBlock() != null) {
            this.w.getBlockAt(this.targetX, this.targetY, this.targetZ).setTypeId(type);
        }
    }

    /**
     * Sets the type of the block attached to the face at the cursor.
     * 
     * @param type
     */
    public final void setFaceBlock(final int type) {
        while ((this.getNextBlock() != null) && (this.getCurBlock().getTypeId() == 0)) {
        }

        if (this.getCurBlock() != null) {
            this.w.getBlockAt(this.targetX, this.targetY, this.targetZ).setTypeId(type);
        }
    }

    /**
     * Sets previous block type id.
     * 
     * @param type
     */
    public final void setLastBlock(final int type) {
        if (this.getLastBlock() != null) {
            this.w.getBlockAt(this.lastX, this.lastY, this.lastZ).setTypeId(type);
        }
    }

    /**
     * Sets the type of the block at the cursor.
     * 
     * @param type
     */
    public final void setTargetBlock(final int type) {
        while ((this.getNextBlock() != null) && (this.getCurBlock().getTypeId() == 0)) {

        }
        if (this.getCurBlock() != null) {
            this.w.getBlockAt(this.targetX, this.targetY, this.targetZ).setTypeId(type);
        }
    }

    private Block getRange() {
        this.lastX = this.targetX;
        this.lastY = this.targetY;
        this.lastZ = this.targetZ;

        do {
            this.length += this.step;

            this.hLength = (this.length * this.rotYCos);
            this.yOffset = (this.length * this.rotYSin);
            this.xOffset = (this.hLength * this.rotXCos);
            this.zOffset = (this.hLength * this.rotXSin);

            this.targetX = (int) Math.floor(this.xOffset + this.playerX);
            this.targetY = (int) Math.floor(this.yOffset + this.playerY);
            this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);

        } while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));

        if (this.w.getBlockTypeIdAt(this.targetX, this.targetY, this.targetZ) != 0) {
            return this.w.getBlockAt(this.targetX, this.targetY, this.targetZ);
        }

        if (this.length > this.range || this.targetY > HitBlox.MAXIMUM_WORLD_HEIGHT || this.targetY < 0) {
            return this.w.getBlockAt(this.lastX, this.lastY, this.lastZ);
        } else {
            return this.getRange();
        }
    }

    private void init(final Location location, final double range, final double step, final double viewHeight) {
        this.playerLoc = location;
        this.viewHeight = viewHeight;
        this.playerX = this.playerLoc.getX();
        this.playerY = this.playerLoc.getY() + this.viewHeight;
        this.playerZ = this.playerLoc.getZ();
        this.range = range;
        this.step = step;
        this.length = 0;
        this.rotX = (this.playerLoc.getYaw() + 90) % 360;
        this.rotY = this.playerLoc.getPitch() * -1;
        this.rotYCos = Math.cos(Math.toRadians(this.rotY));
        this.rotYSin = Math.sin(Math.toRadians(this.rotY));
        this.rotXCos = Math.cos(Math.toRadians(this.rotX));
        this.rotXSin = Math.sin(Math.toRadians(this.rotX));

        this.targetX = (int) Math.floor(this.playerLoc.getX());
        this.targetY = (int) Math.floor(this.playerLoc.getY() + this.viewHeight);
        this.targetZ = (int) Math.floor(this.playerLoc.getZ());
        this.lastX = this.targetX;
        this.lastY = this.targetY;
        this.lastZ = this.targetZ;
    }
}
