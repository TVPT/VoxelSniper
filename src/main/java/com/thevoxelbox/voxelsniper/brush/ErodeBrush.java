package com.thevoxelbox.voxelsniper.brush;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/VoxelSniper#The_Erosion_Brush
 * 
 * @author Piotr
 * @author MikeMatrix
 */
public class ErodeBrush extends Brush {
    /**
     * @author MikeMatrix
     */
    private static final class BlockChangeTracker {
        private final Map<Integer, Map<Vector, BlockWrapper>> blockChanges;
        private final Map<Vector, BlockWrapper> flatChanges;
        private final World world;
        private int nextIterationId = 0;

        public BlockChangeTracker(final World world) {
            this.blockChanges = new HashMap<Integer, Map<Vector, BlockWrapper>>();
            this.flatChanges = new HashMap<Vector, BlockWrapper>();
            this.world = world;
        }

        public BlockWrapper get(final Vector position, final int iteration) {
            BlockWrapper _changedBlock = null;

            for (int _i = iteration - 1; _i >= 0; --_i) {
                if (this.blockChanges.containsKey(_i) && this.blockChanges.get(_i).containsKey(position)) {
                    _changedBlock = this.blockChanges.get(_i).get(position);
                    return _changedBlock;
                }
            }

            if (_changedBlock == null) {
                _changedBlock = new BlockWrapper(position.toLocation(this.world).getBlock());
            }

            return _changedBlock;
        }

        public Collection<BlockWrapper> getAll() {
            return this.flatChanges.values();
        }

        public int nextIteration() {
            return this.nextIterationId++;
        }

        public void put(final Vector position, final BlockWrapper changedBlock, final int iteration) {
            if (!this.blockChanges.containsKey(iteration)) {
                this.blockChanges.put(iteration, new HashMap<Vector, BlockWrapper>());
            }

            this.blockChanges.get(iteration).put(position, changedBlock);
            this.flatChanges.put(position, changedBlock);
        }
    }

    /**
     * @author MikeMatrix
     */
    private static final class BlockWrapper {

        private final Block block;
        private final Material material;
        private final byte data;

        public BlockWrapper(final Block block) {
            this.block = block;
            this.data = block.getData();
            this.material = block.getType();
        }

        public BlockWrapper(final Block block, final Material material, final byte data) {
            this.block = block;
            this.material = material;
            this.data = data;
        }

        /**
         * @return the block
         */
        public Block getBlock() {
            return this.block;
        }

        /**
         * @return the data
         */
        public byte getData() {
            return this.data;
        }

        /**
         * @return the material
         */
        public Material getMaterial() {
            return this.material;
        }

        /**
         * @return if the block is Empty.
         */
        public boolean isEmpty() {
            if (this.material == Material.AIR) {
                return true;
            }
            return false;
        }

        /**
         * @return if the block is a Liquid.
         */
        public boolean isLiquid() {
            switch (this.material) {
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
                return true;
            default:
                return false;
            }
        }

    }

    /**
     * @author MikeMatrix
     */
    private static final class ErosionPreset {
        private final int erosionFaces;
        private final int erosionRecursion;
        private final int fillFaces;
        private final int fillRecursion;

        public ErosionPreset(final int erosionFaces, final int erosionRecursion, final int fillFaces, final int fillRecursion) {
            this.erosionFaces = erosionFaces;
            this.erosionRecursion = erosionRecursion;
            this.fillFaces = fillFaces;
            this.fillRecursion = fillRecursion;
        }

        /**
         * @return the erosionFaces
         */
        public int getErosionFaces() {
            return this.erosionFaces;
        }

        /**
         * @return the erosionRecursion
         */
        public int getErosionRecursion() {
            return this.erosionRecursion;
        }

        /**
         * @return the fillFaces
         */
        public int getFillFaces() {
            return this.fillFaces;
        }

        /**
         * @return the fillRecursion
         */
        public int getFillRecursion() {
            return this.fillRecursion;
        }

        public ErosionPreset getInverted() {
            return new ErosionPreset(this.fillFaces, this.fillRecursion, this.erosionFaces, this.erosionRecursion);
        }
    }

    /**
     * @author MikeMatrix
     */
    private enum Preset {
        MELT(new ErosionPreset(2, 1, 5, 1)), FILL(new ErosionPreset(5, 1, 2, 1)), SMOOTH(new ErosionPreset(3, 1, 3, 1)), LIFT(new ErosionPreset(6, 0, 1, 1)), FLOATCLEAN(
                new ErosionPreset(6, 1, 6, 1));

        ErosionPreset preset;

        Preset(final ErosionPreset preset) {
            this.preset = preset;
        }

        public ErosionPreset getPreset() {
            return this.preset;
        }
    }

    private static final Vector[] FACES_TO_CHECK = { new Vector(0, 0, 1), new Vector(0, 0, -1), new Vector(0, 1, 0), new Vector(0, -1, 0), new Vector(1, 0, 0),
            new Vector(-1, 0, 0) };

    private static int timesUsed = 0;

    private ErosionPreset currentPreset = new ErosionPreset(0, 1, 0, 1);

    /**
     * 
     */
    public ErodeBrush() {
        this.setName("Erode");
    }

    @Override
    public final int getTimesUsed() {
        return ErodeBrush.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(ChatColor.AQUA + "Erosion minimum exposed faces set to " + this.currentPreset.getErosionFaces());
        vm.custom(ChatColor.BLUE + "Fill minumum touching faces set to " + this.currentPreset.getFillFaces());
        vm.custom(ChatColor.DARK_BLUE + "Erosion recursion amount set to " + this.currentPreset.getErosionRecursion());
        vm.custom(ChatColor.DARK_GREEN + "Fill recursion amount set to " + this.currentPreset.getFillRecursion());
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        for (int _i = 1; _i < par.length; _i++) {
            final String _param = par[_i];

            try {
                if (_param.equalsIgnoreCase("info")) {
                    v.sendMessage(ChatColor.GOLD + "Erode brush parameters");
                    v.sendMessage(ChatColor.AQUA + "e[number] (ex:  e3) Sets the number of minimum exposed faces to erode a block.");
                    v.sendMessage(ChatColor.BLUE + "f[number] (ex:  f5) Sets the number of minumum faces containing a block to place a block.");
                    v.sendMessage(ChatColor.DARK_BLUE + "re[number] (ex:  re3) Sets the number of recursions the brush will perform erosion.");
                    v.sendMessage(ChatColor.DARK_GREEN + "rf[number] (ex:  rf5) Sets the number of recursions the brush will perform filling.");
                    this.printPresets(v.getVoxelMessage());
                    return;
                } else if (_param.startsWith("rf")) {
                    this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), this.currentPreset.getErosionRecursion(),
                            this.currentPreset.getFillFaces(), Integer.parseInt(_param.replace("rf", "")));
                    v.sendMessage(ChatColor.BLUE + "Fill recursion amount set to " + this.currentPreset.getFillRecursion());
                    continue;
                } else if (_param.startsWith("re")) {
                    this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), Integer.parseInt(_param.replace("re", "")),
                            this.currentPreset.getFillFaces(), this.currentPreset.getFillRecursion());
                    v.sendMessage(ChatColor.AQUA + "Erosion recursion amount set to " + this.currentPreset.getErosionRecursion());
                    continue;
                } else if (_param.startsWith("f")) {
                    this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), this.currentPreset.getErosionRecursion(),
                            Integer.parseInt(_param.replace("f", "")), this.currentPreset.getFillRecursion());
                    v.sendMessage(ChatColor.BLUE + "Fill minumum touching faces set to " + this.currentPreset.getFillFaces());
                    continue;
                } else if (_param.startsWith("e")) {
                    this.currentPreset = new ErosionPreset(Integer.parseInt(_param.replace("e", "")), this.currentPreset.getErosionRecursion(),
                            this.currentPreset.getFillFaces(), this.currentPreset.getFillRecursion());
                    v.sendMessage(ChatColor.AQUA + "Erosion minimum exposed faces set to " + this.currentPreset.getErosionFaces());
                    continue;
                }
            } catch (final Exception _e) {
            }

            try {
                this.currentPreset = Preset.valueOf(_param.toUpperCase()).getPreset();
                v.getVoxelMessage().brushMessage("Brush preset set to " + _param.toLowerCase());
                return;
            } catch (final IllegalArgumentException _ex) {
                v.getVoxelMessage().brushMessage("No such preset.");
                return;
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        ErodeBrush.timesUsed = tUsed;
    }

    private void erosion(final SnipeData v, final ErosionPreset erosionPreset) {

        final BlockChangeTracker _blockChangeTracker = new BlockChangeTracker(this.getTargetBlock().getWorld());

        final Vector _targetBlockVector = this.getTargetBlock().getLocation().toVector();

        for (int _i = 0; _i < erosionPreset.getErosionRecursion(); ++_i) {
            final int _currentIteration = _blockChangeTracker.nextIteration();
            for (int _x = this.getTargetBlock().getX() - v.getBrushSize(); _x <= this.getTargetBlock().getX() + v.getBrushSize(); ++_x) {
                for (int _z = this.getTargetBlock().getZ() - v.getBrushSize(); _z <= this.getTargetBlock().getZ() + v.getBrushSize(); ++_z) {
                    for (int _y = this.getTargetBlock().getY() - v.getBrushSize(); _y <= this.getTargetBlock().getY() + v.getBrushSize(); ++_y) {
                        final Vector _currentPosition = new Vector(_x, _y, _z);
                        if (_currentPosition.isInSphere(_targetBlockVector, v.getBrushSize())) {
                            final BlockWrapper _currentBlock = _blockChangeTracker.get(_currentPosition, _currentIteration);

                            if (_currentBlock.isEmpty() || _currentBlock.isLiquid()) {
                                continue;
                            }

                            int _count = 0;
                            for (final Vector _vector : ErodeBrush.FACES_TO_CHECK) {
                                final Vector _relativePosition = _currentPosition.clone().add(_vector);
                                final BlockWrapper _relativeBlock = _blockChangeTracker.get(_relativePosition, _currentIteration);

                                if (_relativeBlock.isEmpty() || _relativeBlock.isLiquid()) {
                                    _count++;
                                }
                            }

                            if (_count >= erosionPreset.getErosionFaces()) {
                                _blockChangeTracker
                                        .put(_currentPosition, new BlockWrapper(_currentBlock.getBlock(), Material.AIR, (byte) 0), _currentIteration);
                            }
                        }
                    }
                }
            }
        }

        for (int _i = 0; _i < erosionPreset.getFillRecursion(); ++_i) {
            final int _currentIteration = _blockChangeTracker.nextIteration();
            for (int _x = this.getTargetBlock().getX() - v.getBrushSize(); _x <= this.getTargetBlock().getX() + v.getBrushSize(); ++_x) {
                for (int _z = this.getTargetBlock().getZ() - v.getBrushSize(); _z <= this.getTargetBlock().getZ() + v.getBrushSize(); ++_z) {
                    for (int _y = this.getTargetBlock().getY() - v.getBrushSize(); _y <= this.getTargetBlock().getY() + v.getBrushSize(); ++_y) {
                        final Vector _currentPosition = new Vector(_x, _y, _z);
                        if (_currentPosition.isInSphere(_targetBlockVector, v.getBrushSize())) {
                            final BlockWrapper _currentBlock = _blockChangeTracker.get(_currentPosition, _currentIteration);

                            if (!(_currentBlock.isEmpty() || _currentBlock.isLiquid())) {
                                continue;
                            }

                            int _count = 0;

                            final Map<BlockWrapper, Integer> _blockCount = new HashMap<BlockWrapper, Integer>();

                            for (final Vector _vector : ErodeBrush.FACES_TO_CHECK) {
                                final Vector _relativePosition = _currentPosition.clone().add(_vector);
                                final BlockWrapper _relativeBlock = _blockChangeTracker.get(_relativePosition, _currentIteration);

                                if (!(_relativeBlock.isEmpty() || _relativeBlock.isLiquid())) {
                                    _count++;
                                    final BlockWrapper _typeBlock = new BlockWrapper(null, _relativeBlock.getMaterial(), _relativeBlock.getData());
                                    if (_blockCount.containsKey(_typeBlock)) {
                                        _blockCount.put(_typeBlock, _blockCount.get(_typeBlock) + 1);
                                    } else {
                                        _blockCount.put(_typeBlock, 1);
                                    }
                                }
                            }

                            BlockWrapper _currentMaterial = new BlockWrapper(null, Material.AIR, (byte) 0);
                            int _amount = 0;

                            for (final BlockWrapper _wrapper : _blockCount.keySet()) {
                                final Integer _currentCount = _blockCount.get(_wrapper);
                                if (_amount <= _currentCount) {
                                    _currentMaterial = _wrapper;
                                    _amount = _currentCount;
                                }
                            }

                            if (_count >= erosionPreset.getFillFaces()) {
                                _blockChangeTracker.put(_currentPosition, new BlockWrapper(_currentBlock.getBlock(), _currentMaterial.getMaterial(),
                                        _currentMaterial.getData()), _currentIteration);
                            }
                        }
                    }
                }
            }
        }

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        for (final BlockWrapper _blockWrapper : _blockChangeTracker.getAll()) {
            _undo.put(_blockWrapper.getBlock());
            _blockWrapper.getBlock().setTypeIdAndData(_blockWrapper.getMaterial().getId(), _blockWrapper.getData(), true);
        }

        v.storeUndo(_undo);
    }

    private void printPresets(final Message vm) {
        String _printout = "";

        boolean _delimiterHelper = true;
        for (final Preset _treeType : Preset.values()) {
            if (_delimiterHelper) {
                _delimiterHelper = false;
            } else {
                _printout += ", ";
            }
            _printout += ChatColor.GRAY + _treeType.name().toLowerCase() + ChatColor.WHITE;
        }

        vm.custom(_printout);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.erosion(v, this.currentPreset);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.erosion(v, this.currentPreset.getInverted());
    }
}
