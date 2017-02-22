/*
 * This file is part of VoxelSniper, licensed under the MIT License (MIT).
 *
 * Copyright (c) The VoxelBox <http://thevoxelbox.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import com.thevoxelbox.voxelsniper.brush.shape.SnipeBrush;
import com.thevoxelbox.voxelsniper.event.sniper.ChangeBrushEvent;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.MutableClassToInstanceMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRay.BlockRayBuilder;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class Sniper {

    private final UUID player;
    private boolean enabled = true;
    private LinkedList<Undo> undoList = new LinkedList<Undo>();
    private Map<String, SniperTool> tools = Maps.newHashMap();

    public Sniper(Player player) {
        this.player = player.getUniqueId();
        SniperTool sniperTool = new SniperTool(this);
        sniperTool.assignAction(SnipeAction.ARROW, ItemTypes.ARROW);
        sniperTool.assignAction(SnipeAction.GUNPOWDER, ItemTypes.GUNPOWDER);
        this.tools.put(null, sniperTool);
    }

    public String getCurrentToolId() {
        // @Update should have some support for sniper tools in offhands
        return getToolId((getPlayer().getItemInHand(HandTypes.MAIN_HAND).isPresent()) ? getPlayer().getItemInHand(HandTypes.MAIN_HAND).get() : null);
    }

    // @Cleanup Just tear this whole tool system out and replace

    public String getToolId(ItemStack itemInHand) {
        if (itemInHand == null) {
            return null;
        }

        for (Map.Entry<String, SniperTool> entry : this.tools.entrySet()) {
            if (entry.getValue().hasToolAssigned(itemInHand.getItem())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public SniperTool getSniperTool(ItemStack itemInHand) {
        if (itemInHand == null) {
            return null;
        }

        for (SniperTool entry : this.tools.values()) {
            if (entry.hasToolAssigned(itemInHand.getItem())) {
                return entry;
            }
        }
        return null;
    }

    public Player getPlayer() {
        return Sponge.getServer().getPlayer(this.player).orElse(null);
    }

    /**
     * Sniper execution call.
     *
     * @param action Action player performed
     * @param itemInHand Item in hand of player
     * @return true if command visibly processed, false otherwise.
     */
    public boolean snipe(InteractionType action, ItemStack itemInHand) {
        SniperTool sniperTool = getSniperTool(itemInHand);
        Player player = getPlayer();
        // @Cleanup: invert this if statement
        if (sniperTool != null && sniperTool.hasToolAssigned(itemInHand.getItem())) {
            if (sniperTool.getCurrentBrush() == null) {
                player.sendMessage(VoxelSniperMessages.NO_BRUSH);
                return true;
            }

            if (!player.hasPermission(sniperTool.getCurrentBrush().getPermissionNode())) {
                player.sendMessage(VoxelSniperMessages.BRUSH_PERMISSION_ERROR.create(sniperTool.getCurrentBrush().getPermissionNode()));
                return true;
            }

            SnipeData snipeData = sniperTool.getSnipeData();
            if (player.get(Keys.IS_SNEAKING).orElse(false)) {
                Location<World> targetBlock = null;
                SnipeAction snipeAction = sniperTool.getActionAssigned(itemInHand.getItem());

                Predicate<BlockRayHit<World>> filter = BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1);
                BlockRayBuilder<World> rayBuilder = BlockRay.from(player).stopFilter(filter);
                if (snipeData.isRanged()) {
                    rayBuilder.distanceLimit(snipeData.getRange());
                }
                BlockRay<World> ray = rayBuilder.build();
                while (ray.hasNext()) {
                    targetBlock = ray.next().getLocation();
                }

                switch (action) {
                    case PRIMARY_MAINHAND:
                    case PRIMARY_OFFHAND:
                        switch (snipeAction) {
                            case ARROW:
//                                    int originalVoxel = snipeData.getVoxelId();
                                snipeData.setVoxelId(targetBlock.getBlockType().getDefaultState());
//                                    SniperMaterialChangedEvent event =
//                                            new SniperMaterialChangedEvent(this, toolId, new MaterialData(originalVoxel, snipeData.getData()),
//                                                    new MaterialData(snipeData.getVoxelId(), snipeData.getData()));
//                                    Bukkit.getPluginManager().callEvent(event);
                                snipeData.getVoxelMessage().voxel();
                                return true;
                            case GUNPOWDER:
//                                byte originalData = snipeData.getData();
                                snipeData.setVoxelId(targetBlock.getBlock());
//                                SniperMaterialChangedEvent event =
//                                        new SniperMaterialChangedEvent(this, toolId, new MaterialData(snipeData.getVoxelId(), originalData),
//                                                new MaterialData(snipeData.getVoxelId(), snipeData.getData()));
//                                Bukkit.getPluginManager().callEvent(event);
                                snipeData.getVoxelMessage().voxel();
                                return true;
                            default:
                                break;
                        }
                        break;
                    case SECONDARY_MAINHAND:
                    case SECONDARY_OFFHAND:
                        switch (snipeAction) {
                            case ARROW:
//                                int originalId = snipeData.getReplaceId();
                                snipeData.setReplaceId(targetBlock.getBlockType().getDefaultState());
//                                SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId,
//                                        new MaterialData(originalId, snipeData.getReplaceData()),
//                                        new MaterialData(snipeData.getReplaceId(), snipeData.getReplaceData()));
//                                Bukkit.getPluginManager().callEvent(event);
                                snipeData.getVoxelMessage().replace();
                                return true;
                            case GUNPOWDER:
//                                byte originalData = snipeData.getReplaceData();
                                snipeData.setReplaceId(targetBlock.getBlock());
//                                SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId,
//                                        new MaterialData(snipeData.getReplaceId(), originalData),
//                                        new MaterialData(snipeData.getReplaceId(), snipeData.getReplaceData()));
//                                Bukkit.getPluginManager().callEvent(event);
                                snipeData.getVoxelMessage().replace();
                                return true;
                            default:
                                break;
                        }
                        break;
                    default:
                        return false;
                }
            } else {
                Location<World> targetBlock = null;
                Location<World> lastBlock = null;
                SnipeAction snipeAction = sniperTool.getActionAssigned(itemInHand.getItem());

                if (action == InteractionType.PRIMARY_MAINHAND || action == InteractionType.PRIMARY_OFFHAND) {
                    return false;
                }

                Predicate<BlockRayHit<World>> filter = BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1);
                BlockRayBuilder<World> rayBuilder = BlockRay.from(player).stopFilter(filter);
                if (snipeData.isRanged()) {
                    rayBuilder.distanceLimit(snipeData.getRange());
                }
                BlockRay<World> ray = rayBuilder.build();
                while (ray.hasNext()) {
                    lastBlock = targetBlock;
                    targetBlock = ray.next().getLocation();
                }
                if (targetBlock == null) {
                    player.sendMessage(VoxelSniperMessages.SNIPE_TARGET_NOT_VISIBLE);
                    return false;
                }
                if (lastBlock == null) {
                    lastBlock = targetBlock;
                }

                try {
                    sniperTool.getCurrentBrush().perform(snipeAction, snipeData, targetBlock, lastBlock);
                } catch (Exception e) {
                    player.sendMessage(VoxelSniperMessages.BRUSH_ERROR);
                    VoxelSniper.getLogger().error("Error performing brush " + sniperTool.getCurrentBrush().getName());
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    public IBrush setBrush(String toolId, Class<? extends IBrush> brush) {
        if (!this.tools.containsKey(toolId)) {
            return null;
        }

        return this.tools.get(toolId).setCurrentBrush(brush);
    }

    public IBrush getBrush(String toolId) {
        if (!this.tools.containsKey(toolId)) {
            return null;
        }

        return this.tools.get(toolId).getCurrentBrush();
    }

    public IBrush previousBrush(String toolId) {
        if (!this.tools.containsKey(toolId)) {
            return null;
        }

        return this.tools.get(toolId).previousBrush();
    }

    public boolean setTool(String toolId, SnipeAction action, ItemType itemInHand) {
        for (Map.Entry<String, SniperTool> entry : this.tools.entrySet()) {
            if (entry.getKey() != toolId && entry.getValue().hasToolAssigned(itemInHand)) {
                return false;
            }
        }

        if (!this.tools.containsKey(toolId)) {
            SniperTool tool = new SniperTool(this);
            this.tools.put(toolId, tool);
        }
        this.tools.get(toolId).assignAction(action, itemInHand);
        return true;
    }

    public void removeTool(String toolId, ItemType itemInHand) {
        if (!this.tools.containsKey(toolId)) {
            SniperTool tool = new SniperTool(this);
            this.tools.put(toolId, tool);
        }
        this.tools.get(toolId).unassignAction(itemInHand);
    }

    public void removeTool(String toolId) {
        if (toolId == null) {
            return;
        }
        this.tools.remove(toolId);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void storeUndo(Undo undo) {
        if (VoxelSniperConfiguration.UNDO_CACHE_SIZE <= 0) {
            return;
        }
        if (undo != null && undo.getSize() > 0) {
            while (this.undoList.size() >= VoxelSniperConfiguration.UNDO_CACHE_SIZE) {
                this.undoList.pollLast();
            }
            this.undoList.push(undo);
        }
    }

    public void undo(int amount) {
        int sum = 0;
        if (this.undoList.isEmpty()) {
            getPlayer().sendMessage(VoxelSniperMessages.NOTHING_TO_UNDO);
        } else {
            for (int x = 0; x < amount && !this.undoList.isEmpty(); x++) {
                Undo undo = this.undoList.pop();
                if (undo != null) {
                    undo.undo();
                    sum += undo.getSize();
                } else {
                    break;
                }
            }
            getPlayer().sendMessage(VoxelSniperMessages.UNDO_SUCCESSFUL.create(sum + ""));
        }
    }

    public void reset(String toolId) {
        SniperTool backup = this.tools.remove(toolId);
        SniperTool newTool = new SniperTool(this);

        for (Map.Entry<SnipeAction, ItemType> entry : backup.getActionTools().entrySet()) {
            newTool.assignAction(entry.getKey(), entry.getValue());
        }
        this.tools.put(toolId, newTool);
    }

    public SnipeData getSnipeData(String toolId) {
        return this.tools.containsKey(toolId) ? this.tools.get(toolId).getSnipeData() : null;
    }

    public void displayInfo() {
        String currentToolId = getCurrentToolId();
        SniperTool sniperTool = this.tools.get(currentToolId);
        IBrush brush = sniperTool.getCurrentBrush();
        getPlayer().sendMessage(VoxelSniperMessages.CURRENT_TOOL.create((currentToolId != null) ? currentToolId : "Default Tool"));
        if (brush == null) {
            getPlayer().sendMessage(VoxelSniperMessages.NO_BRUSH);
            return;
        }
        brush.info(sniperTool.getMessageHelper());
        if (brush instanceof PerformBrush) {
            ((PerformBrush) brush).showInfo(sniperTool.getMessageHelper());
        }
    }

    public SniperTool getSniperTool(String toolId) {
        return this.tools.get(toolId);
    }

    public class SniperTool {

        private BiMap<SnipeAction, ItemType> actionTools = HashBiMap.create();
        private ClassToInstanceMap<IBrush> brushes = MutableClassToInstanceMap.create();
        private Class<? extends IBrush> currentBrush;
        private Class<? extends IBrush> previousBrush;
        private SnipeData snipeData;
        private Message messageHelper;

        SniperTool(Sniper owner) {
            this(SnipeBrush.class, new SnipeData(owner));
        }

        private SniperTool(Class<? extends IBrush> currentBrush, SnipeData snipeData) {
            this.snipeData = snipeData;
            this.messageHelper = new Message(snipeData);
            snipeData.setVoxelMessage(this.messageHelper);

            IBrush newBrushInstance = instanciateBrush(currentBrush);
            if (snipeData.owner().getPlayer().hasPermission(newBrushInstance.getPermissionNode())) {
                this.brushes.put(currentBrush, newBrushInstance);
                this.currentBrush = currentBrush;
            }
        }

        public boolean hasToolAssigned(ItemType material) {
            return this.actionTools.containsValue(material);
        }

        public SnipeAction getActionAssigned(ItemType itemInHand) {
            return this.actionTools.inverse().get(itemInHand);
        }

        public ItemType getToolAssigned(SnipeAction action) {
            return this.actionTools.get(action);
        }

        public void assignAction(SnipeAction action, ItemType itemInHand) {
            this.actionTools.forcePut(action, itemInHand);
        }

        public void unassignAction(ItemType itemInHand) {
            this.actionTools.inverse().remove(itemInHand);
        }

        public SnipeData getSnipeData() {
            return this.snipeData;
        }

        public Message getMessageHelper() {
            return this.messageHelper;
        }

        public IBrush getCurrentBrush() {
            if (this.currentBrush == null) {
                return null;
            }
            return this.brushes.getInstance(this.currentBrush);
        }

        public IBrush setCurrentBrush(Class<? extends IBrush> brush) {
            Preconditions.checkNotNull(brush, "Can't set brush to null.");
            IBrush brushInstance = this.brushes.get(brush);
            if (brushInstance == null) {
                brushInstance = instanciateBrush(brush);
                Preconditions.checkNotNull(brushInstance, "Could not instanciate brush class.");
                this.brushes.put(brush, brushInstance);
            }

            if (this.snipeData.owner().getPlayer().hasPermission(brushInstance.getPermissionNode())) {
                ChangeBrushEvent event = new ChangeBrushEvent(VoxelSniper.plugin_cause.with(NamedCause.source(this.snipeData.owner().getPlayer())),
                        this.snipeData, brushInstance);
                Sponge.getEventManager().post(event);
                this.previousBrush = this.currentBrush;
                this.currentBrush = brush;
                return brushInstance;
            }

            return null;
        }

        public IBrush previousBrush() {
            if (this.previousBrush == null) {
                return null;
            }
            return setCurrentBrush(this.previousBrush);
        }

        private IBrush instanciateBrush(Class<? extends IBrush> brush) {
            try {
                return brush.newInstance();
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        public BiMap<SnipeAction, ItemType> getActionTools() {
            return this.actionTools;
        }
    }
}
