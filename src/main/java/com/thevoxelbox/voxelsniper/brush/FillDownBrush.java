package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class FillDownBrush extends PerformBrush {

    private boolean fillLiquid = true;
    private boolean fromExisting = false;

    public FillDownBrush() {
        this.setName("Fill Down");
    }

    private void fillDown(SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;

        this.undo = new Undo(GenericMath.floor(4 * Math.PI * (brushSize + 1) * (brushSize + 1) * (brushSize + 1) / 3));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (minx - x) * (minx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (minz - z) * (minz - z);
                if (xs + zs < brushSizeSquared) {
                    int y = targetBlock.getBlockY();
                    if (this.fromExisting) {
                        for (int y0 = -v.getVoxelHeight(); y0 < v.getVoxelHeight(); y0++) {
                            if (this.world.getBlock(x, y + y0, z) != v.getReplaceIdState()) {
                                y += y0 - 1;
                                break;
                            }
                        }
                    }
                    for (; y >= 0; y--) {
                        if (this.replace != PerformerType.NONE) {
                            if (!perform(v, x, y, z)) {
                                break;
                            }
                        } else {
                            BlockState current = this.world.getBlock(x, y, z);
                            if (current.getType() == BlockTypes.AIR) {
                                perform(v, x, y, z);
                            } else if (this.fillLiquid) {
                                Optional<MatterProperty> matter = current.getProperty(MatterProperty.class);
                                if (matter.isPresent()) {
                                    Matter m = matter.get().getValue();
                                    if (m == Matter.LIQUID) {
                                        perform(v, x, y, z);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.fillDown(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.fillDown(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 1; i < par.length; i++) {
            if (par[i].equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GOLD, "Fill Down Parameters:");
                v.sendMessage(TextColors.AQUA, "/b fd some -- Fills only into air.");
                v.sendMessage(TextColors.AQUA, "/b fd all -- Fills into liquids as well. (Default)");
                v.sendMessage(TextColors.AQUA, "/b fd -e -- Fills only own from existing blocks. (Toggle)");
                return;
            } else if (par[i].equalsIgnoreCase("all")) {
                this.fillLiquid = true;
                v.sendMessage(TextColors.AQUA, "Now filling liquids as well as air.");
            } else if (par[i].equalsIgnoreCase("some")) {
                this.fillLiquid = false;
                v.sendMessage(TextColors.AQUA, "Now only filling air.");
            } else if (par[i].equalsIgnoreCase("-e")) {
                this.fromExisting = !this.fromExisting;
                v.sendMessage(TextColors.AQUA, "Now filling down from " + ((this.fromExisting) ? "existing" : "all") + " blocks.");
            } else {
                v.sendMessage(TextColors.RED, "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.filldown";
    }
}
