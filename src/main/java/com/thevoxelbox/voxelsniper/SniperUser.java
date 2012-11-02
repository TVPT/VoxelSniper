package com.thevoxelbox.voxelsniper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.thevoxelbox.voxelgunsmith.Brush;
import com.thevoxelbox.voxelgunsmith.MaterialData;
import com.thevoxelbox.voxelgunsmith.ToolConfiguration;
import com.thevoxelbox.voxelgunsmith.User;

/**
 * Implementation of the VoxelGunsmith User interface.
 * 
 * @author MikeMatrix
 * 
 */
public class SniperUser implements User {

    private final Player player;
    private final Map<String, ToolConfiguration> toolConfigurations = new HashMap<String, ToolConfiguration>();
    private final Map<String, List<Brush>> toolBrushInstances = new HashMap<String, List<Brush>>();
    private final Map<String, Brush> toolBrush = new HashMap<String, Brush>();
    private final Map<MaterialData, String> toolMappingArrow = new HashMap<MaterialData, String>();
    private final Map<MaterialData, String> toolMappingPowder = new HashMap<MaterialData, String>();

    /**
     * @param player
     */
    public SniperUser(final Player player) {
        this.player = player;
    }

    @Override
    public final Brush getActiveBrush() {
        final MaterialData _matData = new SniperMaterialData(this.player.getItemInHand().getType(), this.player.getItemInHand().getData().getData());
        if (this.toolMappingArrow.containsKey(_matData)) {
            return this.getBrush(this.toolMappingArrow.get(_matData));
        } else if (this.toolMappingPowder.containsKey(_matData)) {
            return this.getBrush(this.toolMappingPowder.get(_matData));
        }
        return null;
    }

    @Override
    public final ToolConfiguration getActiveToolConfiguration() {
        final MaterialData _matData = new SniperMaterialData(this.player.getItemInHand().getType(), this.player.getItemInHand().getData().getData());
        if (this.toolMappingArrow.containsKey(_matData)) {
            return this.getToolConfiguration(this.toolMappingArrow.get(_matData));
        } else if (this.toolMappingPowder.containsKey(_matData)) {
            return this.getToolConfiguration(this.toolMappingPowder.get(_matData));
        }
        return null;
    }

    @Override
    public final Brush getBrush(final String toolId) {
        return this.toolBrush.get(toolId);
    }

    @Override
    public final Player getPlayer() {
        return this.player;
    }

    @Override
    public final ToolConfiguration getToolConfiguration(final String toolId) {
        return this.toolConfigurations.get(toolId);
    }

    @Override
    public final void sendMessage(final String message) {
        this.player.sendMessage(message);
    }

}
