package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeAction;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Abstract implementation of the {@link IBrush} interface.
 */
public abstract class Brush implements IBrush {

    protected static int WORLD_HEIGHT = Sponge.getServer().getChunkLayout().getSpaceMax().getY();

    public static Location<World> clampY(World world, int x, int y, int z) {
        if (y < 0) {
            y = 0;
        } else if (y > WORLD_HEIGHT) {
            y = WORLD_HEIGHT;
        }

        return new Location<>(world, x, y, z);
    }

    protected World world;
    protected Location<World> targetBlock;
    protected Location<World> lastBlock;
    protected Undo undo;
    private String name = "Undefined";

    @Override
    public boolean perform(SnipeAction action, SnipeData data, Location<World> targetBlock, Location<World> lastBlock) {
        this.world = targetBlock.getExtent();
        this.targetBlock = targetBlock;
        this.lastBlock = lastBlock;
        switch (action) {
            case ARROW:
                this.arrow(data);
                return true;
            case GUNPOWDER:
                this.powder(data);
                return true;
            default:
                return false;
        }
    }

    /**
     * The arrow action. Executed when a player RightClicks with an Arrow
     *
     * @param v Sniper caller
     */
    protected void arrow(final SnipeData v) {
    }

    /**
     * The powder action. Executed when a player RightClicks with Gunpowder
     *
     * @param v Sniper caller
     */
    protected void powder(final SnipeData v) {
    }

    @Override
    public abstract void info(Message vm);

    @Override
    public void parameters(final String[] par, final SnipeData v) {
        // @Usability support a --no-undo parameter flag
        v.sendMessage(TextColors.RED, "This brush does not accept additional parameters.");
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getBrushCategory() {
        return "General";
    }

    protected void setBlockType(int x, int y, int z, BlockType type) {
        if (this.undo != null) {
            this.undo.put(new Location<World>(this.world, x, y, z));
        }
        this.world.setBlockType(x, y, z, type);
    }

    protected void setBlockState(int x, int y, int z, BlockState type) {
        if (this.undo != null) {
            this.undo.put(new Location<World>(this.world, x, y, z));
        }
        this.world.setBlock(x, y, z, type);
    }
}
