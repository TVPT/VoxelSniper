package com.thevoxelbox.voxelsniper.brush;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.jsap.HelpJSAP;
import com.thevoxelbox.voxelsniper.jsap.NullableIntegerStringParser;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/VoxelSniper#The_Erosion_Brush
 *
 * @author Piotr
 * @author MikeMatrix
 */
public class ErodeBrush extends Brush
{
    private static final Vector[] FACES_TO_CHECK = { new Vector(0, 0, 1), new Vector(0, 0, -1), new Vector(0, 1, 0), new Vector(0, -1, 0), new Vector(1, 0, 0), new Vector(-1, 0, 0) };
    private static int timesUsed = 0;
    private final HelpJSAP parser = new HelpJSAP("/b e", "Brush for eroding landscape.", ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH);
    private ErosionPreset currentPreset = new ErosionPreset(0, 1, 0, 1);

    /**
     *
     */
    public ErodeBrush()
    {
        this.setName("Erode");

        try
        {
            this.parser.registerParameter(new UnflaggedOption("preset", EnumeratedStringParser.getParser(Preset.getValuesString(";"), false), null, false, false, "Preset options: " + Preset.getValuesString(", ")));
            this.parser.registerParameter(new FlaggedOption("fill", NullableIntegerStringParser.getParser(), null, false, 'f', "fill", "Surrounding blocks required to fill the block."));
            this.parser.registerParameter(new FlaggedOption("erode", NullableIntegerStringParser.getParser(), null, false, 'e', "erode", "Surrounding air required to erode the block."));
            this.parser.registerParameter(new FlaggedOption("fillrecursion", NullableIntegerStringParser.getParser(), null, false, 'F', "fillrecursion", "Repeated fill iterations."));
            this.parser.registerParameter(new FlaggedOption("eroderecursion", NullableIntegerStringParser.getParser(), null, false, 'E', "eroderecursion", "Repeated erode iterations."));
        }
        catch (JSAPException e)
        {
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.erosion(v, this.currentPreset);
    }

    private void erosion(final SnipeData v, final ErosionPreset erosionPreset)
    {

        final BlockChangeTracker _blockChangeTracker = new BlockChangeTracker(this.getTargetBlock().getWorld());

        final Vector _targetBlockVector = this.getTargetBlock().getLocation().toVector();

        for (int _i = 0; _i < erosionPreset.getErosionRecursion(); ++_i)
        {
            erosionIteration(v, erosionPreset, _blockChangeTracker, _targetBlockVector);
        }

        for (int _i = 0; _i < erosionPreset.getFillRecursion(); ++_i)
        {
            fillIteration(v, erosionPreset, _blockChangeTracker, _targetBlockVector);
        }

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        for (final BlockWrapper _blockWrapper : _blockChangeTracker.getAll())
        {
            _undo.put(_blockWrapper.getBlock());
            _blockWrapper.getBlock().setTypeIdAndData(_blockWrapper.getMaterial().getId(), _blockWrapper.getData(), true);
        }

        v.storeUndo(_undo);
    }

    private void fillIteration(final SnipeData v, final ErosionPreset erosionPreset, final BlockChangeTracker _blockChangeTracker, final Vector _targetBlockVector)
    {
        final int _currentIteration = _blockChangeTracker.nextIteration();
        for (int _x = this.getTargetBlock().getX() - v.getBrushSize(); _x <= this.getTargetBlock().getX() + v.getBrushSize(); ++_x)
        {
            for (int _z = this.getTargetBlock().getZ() - v.getBrushSize(); _z <= this.getTargetBlock().getZ() + v.getBrushSize(); ++_z)
            {
                for (int _y = this.getTargetBlock().getY() - v.getBrushSize(); _y <= this.getTargetBlock().getY() + v.getBrushSize(); ++_y)
                {
                    final Vector _currentPosition = new Vector(_x, _y, _z);
                    if (_currentPosition.isInSphere(_targetBlockVector, v.getBrushSize()))
                    {
                        final BlockWrapper _currentBlock = _blockChangeTracker.get(_currentPosition, _currentIteration);

                        if (!(_currentBlock.isEmpty() || _currentBlock.isLiquid()))
                        {
                            continue;
                        }

                        int _count = 0;

                        final Map<BlockWrapper, Integer> _blockCount = new HashMap<BlockWrapper, Integer>();

                        for (final Vector _vector : ErodeBrush.FACES_TO_CHECK)
                        {
                            final Vector _relativePosition = _currentPosition.clone().add(_vector);
                            final BlockWrapper _relativeBlock = _blockChangeTracker.get(_relativePosition, _currentIteration);

                            if (!(_relativeBlock.isEmpty() || _relativeBlock.isLiquid()))
                            {
                                _count++;
                                final BlockWrapper _typeBlock = new BlockWrapper(null, _relativeBlock.getMaterial(), _relativeBlock.getData());
                                if (_blockCount.containsKey(_typeBlock))
                                {
                                    _blockCount.put(_typeBlock, _blockCount.get(_typeBlock) + 1);
                                }
                                else
                                {
                                    _blockCount.put(_typeBlock, 1);
                                }
                            }
                        }

                        BlockWrapper _currentMaterial = new BlockWrapper(null, Material.AIR, (byte) 0);
                        int _amount = 0;

                        for (final BlockWrapper _wrapper : _blockCount.keySet())
                        {
                            final Integer _currentCount = _blockCount.get(_wrapper);
                            if (_amount <= _currentCount)
                            {
                                _currentMaterial = _wrapper;
                                _amount = _currentCount;
                            }
                        }

                        if (_count >= erosionPreset.getFillFaces())
                        {
                            _blockChangeTracker.put(_currentPosition, new BlockWrapper(_currentBlock.getBlock(), _currentMaterial.getMaterial(), _currentMaterial.getData()), _currentIteration);
                        }
                    }
                }
            }
        }
    }

    private void erosionIteration(final SnipeData v, final ErosionPreset erosionPreset, final BlockChangeTracker _blockChangeTracker, final Vector _targetBlockVector)
    {
        final int _currentIteration = _blockChangeTracker.nextIteration();
        for (int _x = this.getTargetBlock().getX() - v.getBrushSize(); _x <= this.getTargetBlock().getX() + v.getBrushSize(); ++_x)
        {
            for (int _z = this.getTargetBlock().getZ() - v.getBrushSize(); _z <= this.getTargetBlock().getZ() + v.getBrushSize(); ++_z)
            {
                for (int _y = this.getTargetBlock().getY() - v.getBrushSize(); _y <= this.getTargetBlock().getY() + v.getBrushSize(); ++_y)
                {
                    final Vector _currentPosition = new Vector(_x, _y, _z);
                    if (_currentPosition.isInSphere(_targetBlockVector, v.getBrushSize()))
                    {
                        final BlockWrapper _currentBlock = _blockChangeTracker.get(_currentPosition, _currentIteration);

                        if (_currentBlock.isEmpty() || _currentBlock.isLiquid())
                        {
                            continue;
                        }

                        int _count = 0;
                        for (final Vector _vector : ErodeBrush.FACES_TO_CHECK)
                        {
                            final Vector _relativePosition = _currentPosition.clone().add(_vector);
                            final BlockWrapper _relativeBlock = _blockChangeTracker.get(_relativePosition, _currentIteration);

                            if (_relativeBlock.isEmpty() || _relativeBlock.isLiquid())
                            {
                                _count++;
                            }
                        }

                        if (_count >= erosionPreset.getErosionFaces())
                        {
                            _blockChangeTracker.put(_currentPosition, new BlockWrapper(_currentBlock.getBlock(), Material.AIR, (byte) 0), _currentIteration);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.erosion(v, this.currentPreset.getInverted());
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(ChatColor.AQUA + "Erosion minimum exposed faces set to " + this.currentPreset.getErosionFaces());
        vm.custom(ChatColor.BLUE + "Fill minumum touching faces set to " + this.currentPreset.getFillFaces());
        vm.custom(ChatColor.DARK_BLUE + "Erosion recursion amount set to " + this.currentPreset.getErosionRecursion());
        vm.custom(ChatColor.DARK_GREEN + "Fill recursion amount set to " + this.currentPreset.getFillRecursion());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        JSAPResult result = this.parser.parse(Arrays.copyOfRange(par, 1, par.length));

        if (sendHelpOrErrorMessageToPlayer(result, v.owner().getPlayer(), this.parser))
        {
            return;
        }

        if (result.getString("preset") != null)
        {
            try
            {
                this.currentPreset = Preset.valueOf(result.getString("preset").toUpperCase()).getPreset();
                v.getVoxelMessage().brushMessage("Brush preset set to " + result.getString("preset"));
                return;
            }
            catch (final IllegalArgumentException _ex)
            {
                v.getVoxelMessage().brushMessage("No such preset.");
                return;
            }
        }

        ErosionPreset currentPresetBackup = this.currentPreset;

        if (result.getObject("fill") != null)
        {
            this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), this.currentPreset.getErosionRecursion(), result.getInt("fill"), this.currentPreset.getFillRecursion());
        }

        if (result.getObject("erode") != null)
        {
            this.currentPreset = new ErosionPreset(result.getInt("erode"), this.currentPreset.getErosionRecursion(), this.currentPreset.getFillFaces(), this.currentPreset.getFillRecursion());
        }

        if (result.getObject("fillrecursion") != null)
        {
            this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), this.currentPreset.getErosionRecursion(), this.currentPreset.getFillFaces(), result.getInt("fillrecursion"));
        }

        if (result.getObject("eroderecursion") != null)
        {
            this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), result.getInt("eroderecursion"), this.currentPreset.getFillFaces(), this.currentPreset.getFillRecursion());
        }

        if (!currentPreset.equals(currentPresetBackup))
        {
            if (currentPreset.getErosionFaces() != currentPresetBackup.getErosionFaces())
            {
                v.sendMessage(ChatColor.AQUA + "Erosion faces set to: " + ChatColor.WHITE + currentPreset.getErosionFaces());
            }
            if (currentPreset.getFillFaces() != currentPresetBackup.getFillFaces())
            {
                v.sendMessage(ChatColor.AQUA + "Fill faces set to: " + ChatColor.WHITE + currentPreset.getFillFaces());
            }
            if (currentPreset.getErosionRecursion() != currentPresetBackup.getErosionRecursion())
            {
                v.sendMessage(ChatColor.AQUA + "Erosion recursions set to: " + ChatColor.WHITE + currentPreset.getErosionRecursion());
            }
            if (currentPreset.getFillRecursion() != currentPresetBackup.getFillRecursion())
            {
                v.sendMessage(ChatColor.AQUA + "Fill recursions set to: " + ChatColor.WHITE + currentPreset.getFillRecursion());
            }
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return ErodeBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        ErodeBrush.timesUsed = tUsed;
    }

    /**
     * @param result
     * @param player
     * @param helpJSAP
     *
     * @return if a message was sent.
     */
    public static boolean sendHelpOrErrorMessageToPlayer(final JSAPResult result, final Player player, final HelpJSAP helpJSAP)
    {
        final List<String> output = helpJSAP.writeHelpOrErrorMessageIfRequired(result);
        if (!output.isEmpty())
        {
            for (final String string : output)
            {
                player.sendMessage(string);
            }
            return true;
        }
        return false;
    }

    /**
     * @author MikeMatrix
     */
    private enum Preset
    {
        MELT(new ErosionPreset(2, 1, 5, 1)), FILL(new ErosionPreset(5, 1, 2, 1)), SMOOTH(new ErosionPreset(3, 1, 3, 1)), LIFT(new ErosionPreset(6, 0, 1, 1)), FLOATCLEAN(new ErosionPreset(6, 1, 6, 1));
        ErosionPreset preset;

        Preset(final ErosionPreset preset)
        {
            this.preset = preset;
        }

        /**
         * Generates a concat string of all options.
         *
         * @param seperator
         *         Seperator for delimiting entries.
         *
         * @return
         */
        public static String getValuesString(String seperator)
        {
            String valuesString = "";

            boolean delimiterHelper = true;
            for (final Preset preset : Preset.values())
            {
                if (delimiterHelper)
                {
                    delimiterHelper = false;
                }
                else
                {
                    valuesString += seperator;
                }
                valuesString += preset.name();
            }
            return valuesString;
        }

        public ErosionPreset getPreset()
        {
            return this.preset;
        }


    }

    /**
     * @author MikeMatrix
     */
    private static final class BlockChangeTracker
    {
        private final Map<Integer, Map<Vector, BlockWrapper>> blockChanges;
        private final Map<Vector, BlockWrapper> flatChanges;
        private final World world;
        private int nextIterationId = 0;

        public BlockChangeTracker(final World world)
        {
            this.blockChanges = new HashMap<Integer, Map<Vector, BlockWrapper>>();
            this.flatChanges = new HashMap<Vector, BlockWrapper>();
            this.world = world;
        }

        public BlockWrapper get(final Vector position, final int iteration)
        {
            BlockWrapper _changedBlock = null;

            for (int _i = iteration - 1; _i >= 0; --_i)
            {
                if (this.blockChanges.containsKey(_i) && this.blockChanges.get(_i).containsKey(position))
                {
                    _changedBlock = this.blockChanges.get(_i).get(position);
                    return _changedBlock;
                }
            }

            if (_changedBlock == null)
            {
                _changedBlock = new BlockWrapper(position.toLocation(this.world).getBlock());
            }

            return _changedBlock;
        }

        public Collection<BlockWrapper> getAll()
        {
            return this.flatChanges.values();
        }

        public int nextIteration()
        {
            return this.nextIterationId++;
        }

        public void put(final Vector position, final BlockWrapper changedBlock, final int iteration)
        {
            if (!this.blockChanges.containsKey(iteration))
            {
                this.blockChanges.put(iteration, new HashMap<Vector, BlockWrapper>());
            }

            this.blockChanges.get(iteration).put(position, changedBlock);
            this.flatChanges.put(position, changedBlock);
        }
    }

    /**
     * @author MikeMatrix
     */
    private static final class BlockWrapper
    {

        private final Block block;
        private final Material material;
        private final byte data;

        public BlockWrapper(final Block block)
        {
            this.block = block;
            this.data = block.getData();
            this.material = block.getType();
        }

        public BlockWrapper(final Block block, final Material material, final byte data)
        {
            this.block = block;
            this.material = material;
            this.data = data;
        }

        /**
         * @return the block
         */
        public Block getBlock()
        {
            return this.block;
        }

        /**
         * @return the data
         */
        public byte getData()
        {
            return this.data;
        }

        /**
         * @return the material
         */
        public Material getMaterial()
        {
            return this.material;
        }

        /**
         * @return if the block is Empty.
         */
        public boolean isEmpty()
        {
            if (this.material == Material.AIR)
            {
                return true;
            }
            return false;
        }

        /**
         * @return if the block is a Liquid.
         */
        public boolean isLiquid()
        {
            switch (this.material)
            {
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
    private static final class ErosionPreset
    {
        private final int erosionFaces;
        private final int erosionRecursion;
        private final int fillFaces;
        private final int fillRecursion;

        public ErosionPreset(final int erosionFaces, final int erosionRecursion, final int fillFaces, final int fillRecursion)
        {
            this.erosionFaces = erosionFaces;
            this.erosionRecursion = erosionRecursion;
            this.fillFaces = fillFaces;
            this.fillRecursion = fillRecursion;
        }

        @Override
        public int hashCode()
        {
            int hash = 1;
            hash = hash * 17 + erosionFaces;
            hash = hash * 47 + erosionRecursion;
            hash = hash * 59 + fillFaces;
            hash = hash * 61 + fillRecursion;

            return hash;
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (obj instanceof ErosionPreset)
            {
                ErosionPreset other = (ErosionPreset) obj;

                return this.erosionFaces == other.erosionFaces && this.erosionRecursion == other.erosionRecursion && this.fillFaces == other.fillFaces && this.fillRecursion == other.fillRecursion;
            }
            return false;
        }

        /**
         * @return the erosionFaces
         */
        public int getErosionFaces()
        {
            return this.erosionFaces;
        }


        /**
         * @return the erosionRecursion
         */
        public int getErosionRecursion()
        {
            return this.erosionRecursion;
        }

        /**
         * @return the fillFaces
         */
        public int getFillFaces()
        {
            return this.fillFaces;
        }

        /**
         * @return the fillRecursion
         */
        public int getFillRecursion()
        {
            return this.fillRecursion;
        }

        public ErosionPreset getInverted()
        {
            return new ErosionPreset(this.fillFaces, this.fillRecursion, this.erosionFaces, this.erosionRecursion);
        }


    }
}
