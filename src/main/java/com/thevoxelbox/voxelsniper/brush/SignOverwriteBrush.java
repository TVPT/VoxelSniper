package com.thevoxelbox.voxelsniper.brush;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.VoxelSniper;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

/**
 * Overwrites signs. (Wiki:
 * http://www.voxelwiki.com/minecraft/VoxelSniper#Sign_Overwrite_Brush)
 *
 * @author Monofraps
 */
public class SignOverwriteBrush extends Brush
{
    private static final int MAX_SIGN_LINE_LENGTH = 15;
    private static final int NUM_SIGN_LINES = 4;

    // these are no array indices
    private static final int SIGN_LINE_1 = 1;
    private static final int SIGN_LINE_2 = 2;
    private static final int SIGN_LINE_3 = 3;
    private static final int SIGN_LINE_4 = 4;

    private static int timesUsed = 0;

    private String[] signTextLines = new String[NUM_SIGN_LINES];
    private boolean[] signLinesEnabled = new boolean[NUM_SIGN_LINES];
    private boolean rangedMode = false;

    /**
     *
     */
    public SignOverwriteBrush()
    {
        this.setName("Sign Overwrite Brush");

        clearBuffer();
        resetStates();
    }

    /**
     * Sets the text of a given sign.
     *
     * @param sign
     */
    private void setSignText(final Sign sign)
    {
        for (int _i = 0; _i < this.signTextLines.length; _i++)
        {
            if (this.signLinesEnabled[_i])
            {
                sign.setLine(_i, this.signTextLines[_i]);
            }
        }

        sign.update();
    }

    /**
     * Sets the text of the target sign if the target block is a sign.
     *
     * @param v
     */
    private void setSingle(final SnipeData v)
    {
        if (this.getTargetBlock().getState() instanceof Sign)
        {
            setSignText((Sign) this.getTargetBlock().getState());
        }
        else
        {
            v.sendMessage(ChatColor.RED + "Target block is not a sign.");
            return;
        }
    }

    /**
     * Sets all signs in a range of box{x=z=brushSize*2+1 ; z=voxelHeight*2+1}.
     *
     * @param v
     */
    private void setRanged(final SnipeData v)
    {
        final int _minX = getTargetBlock().getX() - v.getBrushSize();
        final int _maxX = getTargetBlock().getX() + v.getBrushSize();
        final int _minY = getTargetBlock().getY() - v.getVoxelHeight();
        final int _maxY = getTargetBlock().getY() + v.getVoxelHeight();
        final int _minZ = getTargetBlock().getZ() - v.getBrushSize();
        final int _maxZ = getTargetBlock().getZ() + v.getBrushSize();

        boolean _signFound = false; // indicates whether or not a sign was set

        for (int _x = _minX; _x <= _maxX; _x++)
        {
            for (int _y = _minY; _y <= _maxY; _y++)
            {
                for (int _z = _minZ; _z <= _maxZ; _z++)
                {
                    BlockState _blockState = this.getWorld().getBlockAt(_x, _y, _z).getState();
                    if (_blockState instanceof Sign)
                    {
                        setSignText((Sign) _blockState);
                        _signFound = true;
                    }
                }
            }
        }

        if (!_signFound)
        {
            v.sendMessage(ChatColor.RED + "Did not found any sign in selection box.");
            return;
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        if (this.rangedMode)
        {
            setRanged(v);
        }
        else
        {
            setSingle(v);
        }
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        if (this.getTargetBlock().getState() instanceof Sign)
        {
            Sign _sign = (Sign) this.getTargetBlock().getState();

            for (int _i = 0; _i < this.signTextLines.length; _i++)
            {
                if (this.signLinesEnabled[_i])
                {
                    this.signTextLines[_i] = _sign.getLine(_i);
                }
            }

            displayBuffer(v);
        }
        else
        {
            v.sendMessage(ChatColor.RED + "Target block is not a sign.");
            return;
        }
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        boolean _textChanged = false;

        for (int _i = 0; _i < par.length; _i++)
        {
            String _param = par[_i];

            try
            {
                if (_param.equalsIgnoreCase("info"))
                {
                    v.sendMessage(ChatColor.AQUA + "Sign Overwrite Brush Powder/Arrow:");
                    v.sendMessage(ChatColor.BLUE + "The arrow writes the internal line buffer to the tearget sign.");
                    v.sendMessage(ChatColor.BLUE + "The powder reads the text of the target sign into the internal buffer.");
                    v.sendMessage(ChatColor.AQUA + "Sign Overwrite Brush Parameters:");
                    v.sendMessage(ChatColor.GREEN + "-1[:(enabled|disabled)] ... " + ChatColor.BLUE + "-- Sets the text of the first sign line. (e.g. -1 Blah Blah)");
                    v.sendMessage(ChatColor.GREEN + "-2[:(enabled|disabled)] ... " + ChatColor.BLUE + "-- Sets the text of the second sign line. (e.g. -2 Blah Blah)");
                    v.sendMessage(ChatColor.GREEN + "-3[:(enabled|disabled)] ... " + ChatColor.BLUE + "-- Sets the text of the third sign line. (e.g. -3 Blah Blah)");
                    v.sendMessage(ChatColor.GREEN + "-4[:(enabled|disabled)] ... " + ChatColor.BLUE + "-- Sets the text of the fourth sign line. (e.g. -4 Blah Blah)");
                    v.sendMessage(ChatColor.GREEN + "-clear " + ChatColor.BLUE + "-- Clears the line buffer. (Alias: -c)");
                    v.sendMessage(ChatColor.GREEN + "-clearall " + ChatColor.BLUE + "-- Clears the line buffer and sets all lines back to enabled. (Alias: -ca)");
                    v.sendMessage(ChatColor.GREEN + "-multiple [on|off] " + ChatColor.BLUE + "-- Enables or disables ranged mode. (Alias: -m) (see Wiki for more information)");
                    v.sendMessage(ChatColor.GREEN + "-save (name) " + ChatColor.BLUE + "-- Save you buffer to a file named [name]. (Alias: -s)");
                    v.sendMessage(ChatColor.GREEN + "-open (name) " + ChatColor.BLUE + "-- Loads a buffer from a file named [name]. (Alias: -o)");
                    continue;
                }
                else if (_param.startsWith("-1"))
                {
                    _textChanged = true;
                    _i = parseSignLineFromParam(par, SIGN_LINE_1, v, _i);
                    continue;
                }
                else if (_param.startsWith("-2"))
                {
                    _textChanged = true;
                    _i = parseSignLineFromParam(par, SIGN_LINE_2, v, _i);
                    continue;
                }
                else if (_param.startsWith("-3"))
                {
                    _textChanged = true;
                    _i = parseSignLineFromParam(par, SIGN_LINE_3, v, _i);
                    continue;
                }
                else if (_param.startsWith("-4"))
                {
                    _textChanged = true;
                    _i = parseSignLineFromParam(par, SIGN_LINE_4, v, _i);
                    continue;
                }
                else if (_param.equalsIgnoreCase("-clear") || _param.equalsIgnoreCase("-c"))
                {
                    clearBuffer();
                    v.sendMessage(ChatColor.BLUE + "Internal text buffer cleard.");
                    continue;
                }
                else if (_param.equalsIgnoreCase("-clearall") || _param.equalsIgnoreCase("-ca"))
                {
                    clearBuffer();
                    resetStates();
                    v.sendMessage(ChatColor.BLUE + "Internal text buffer cleard and states back to enabled.");
                    continue;
                }
                else if (_param.equalsIgnoreCase("-multiple") || _param.equalsIgnoreCase("-m"))
                {
                    if ((_i + 1) >= par.length)
                    {
                        v.sendMessage(ChatColor.RED + String.format("Missing parameter after %s.", _param));
                        continue;
                    }

                    rangedMode = (par[++_i].equalsIgnoreCase("on") || par[++_i].equalsIgnoreCase("yes"));
                    v.sendMessage(ChatColor.BLUE + String.format("Ranged mode is %s", ChatColor.GREEN + (rangedMode ? "enabled" : "disabled")));
                    if (this.rangedMode)
                    {
                        v.sendMessage(ChatColor.GREEN + "Brush size set to " + ChatColor.RED + v.getBrushSize());
                        v.sendMessage(ChatColor.AQUA + "Brush height set to " + ChatColor.RED + v.getVoxelHeight());
                    }
                    continue;
                }
                else if (_param.equalsIgnoreCase("-save") || _param.equalsIgnoreCase("-s"))
                {
                    if ((_i + 1) >= par.length)
                    {
                        v.sendMessage(ChatColor.RED + String.format("Missing parameter after %s.", _param));
                        continue;
                    }

                    String _fileName = par[++_i];
                    saveBufferToFile(_fileName, v);
                }
                else if (_param.equalsIgnoreCase("-open") || _param.equalsIgnoreCase("-o"))
                {
                    if ((_i + 1) >= par.length)
                    {
                        v.sendMessage(ChatColor.RED + String.format("Missing parameter after %s.", _param));
                        continue;
                    }

                    String _fileName = par[++_i];
                    loadBufferFromFile(_fileName, "", v);
                    _textChanged = true;
                }
            }
            catch (Exception _ex)
            {
                v.sendMessage(ChatColor.RED + String.format("Error while parsing parameter %s", _param));
                _ex.printStackTrace();
            }
        }

        if (_textChanged)
        {
            displayBuffer(v);
        }
    }

    /**
     * Parses parameter input text of line [param:lineNumber].
     * Iterates though the given array until the next top level param (a parameter which starts
     * with a dash -) is found.
     *
     * @param params
     * @param lineNumber
     * @param v
     * @param i
     *
     * @return
     */
    private int parseSignLineFromParam(final String[] params, final int lineNumber, final SnipeData v, int i)
    {
        final int _lineIndex = lineNumber - 1;
        final String _param = params[i];

        boolean _statusSet = false;

        if (_param.contains(":"))
        {
            this.signLinesEnabled[_lineIndex] = _param.substring(_param.indexOf(":")).equalsIgnoreCase(":enabled");
            v.sendMessage(ChatColor.BLUE + "Line " + lineNumber + " is " + ChatColor.GREEN + (this.signLinesEnabled[_lineIndex] ? "enabled" : "disabled"));
            _statusSet = true;
        }

        if ((i + 1) >= params.length)
        {
            // return if the user just wanted to set the status
            if (_statusSet)
            {
                return i;
            }

            v.sendMessage(ChatColor.RED + "Warning: No text after -" + lineNumber + ". Setting buffer text to \"\" (empty string)");
            signTextLines[_lineIndex] = "";
            return i;
        }

        String _newText = "";

        // go through the array until the next top level parameter is found
        for (i++; i < params.length; i++)
        {
            final String _currentParam = params[i];

            if (_currentParam.startsWith("-"))
            {
                i--;
                break;
            }
            else
            {
                _newText += _currentParam + " ";
            }
        }

        _newText = ChatColor.translateAlternateColorCodes('&', _newText);

        // remove last space or return if the string is empty and the user just wanted to set the status
        if (!_newText.isEmpty() && _newText.endsWith(" "))
        {
            _newText = _newText.substring(0, _newText.length() - 1);
        }
        else if (_newText.isEmpty())
        {
            if (_statusSet)
            {
                return i;
            }
            v.sendMessage(ChatColor.RED + "Warning: No text after -" + lineNumber + ". Setting buffer text to \"\" (empty string)");
        }

        // check the line length and cut the text if needed
        if (_newText.length() > MAX_SIGN_LINE_LENGTH)
        {
            v.sendMessage(ChatColor.RED + "Warning: Text on line " + lineNumber + " exceeds the maximum line length of " + MAX_SIGN_LINE_LENGTH + " characters. Your text will be cut.");
            _newText = _newText.substring(0, MAX_SIGN_LINE_LENGTH);
        }

        this.signTextLines[_lineIndex] = _newText;
        return i;
    }

    private final void displayBuffer(final SnipeData v)
    {
        v.sendMessage(ChatColor.BLUE + "Buffer text set to: ");
        for (int _i = 0; _i < this.signTextLines.length; _i++)
        {
            v.sendMessage((this.signLinesEnabled[_i] ? ChatColor.GREEN + "(E): " : ChatColor.RED + "(D): ") + ChatColor.BLACK + this.signTextLines[_i]);
        }
    }

    /**
     * Saves the buffer to file.
     *
     * @param fileName
     * @param v
     */
    private void saveBufferToFile(final String fileName, final SnipeData v)
    {
        final File _store = new File(VoxelSniper.getInstance().getDataFolder() + "/" + fileName + ".vsign");
        if (_store.exists())
        {
            v.sendMessage("This file already exists.");
            return;
        }

        try
        {
            _store.createNewFile();
            FileWriter _outFile = new FileWriter(_store);
            BufferedWriter _outStream = new BufferedWriter(_outFile);

            for (int _i = 0; _i < this.signTextLines.length; _i++)
            {
                _outStream.write(String.valueOf(this.signLinesEnabled[_i]) + "\n");
                _outStream.write(this.signTextLines[_i] + "\n");
            }

            _outStream.close();
            _outFile.close();

            v.sendMessage(ChatColor.BLUE + "File saved successfully.");
        }
        catch (IOException _e)
        {
            v.sendMessage(ChatColor.RED + "Failed to save file. " + _e.getMessage());
            _e.printStackTrace();
        }
    }

    /**
     * Loads a buffer from a file.
     *
     * @param fileName
     * @param userDomain
     * @param v
     */
    private void loadBufferFromFile(final String fileName, final String userDomain, final SnipeData v)
    {
        final File _store = new File(VoxelSniper.getInstance().getDataFolder() + "/" + fileName + ".vsign");
        if (!_store.exists())
        {
            v.sendMessage("This file does not exist.");
            return;
        }

        try
        {
            FileReader _inFile = new FileReader(_store);
            BufferedReader _inStream = new BufferedReader(_inFile);

            for (int _i = 0; _i < this.signTextLines.length; _i++)
            {
                this.signLinesEnabled[_i] = Boolean.valueOf(_inStream.readLine());
                this.signTextLines[_i] = _inStream.readLine();
            }

            _inStream.close();
            _inFile.close();

            v.sendMessage(ChatColor.BLUE + "File loaded successfully.");
        }
        catch (IOException _e)
        {
            v.sendMessage(ChatColor.RED + "Failed to load file. " + _e.getMessage());
            _e.printStackTrace();
        }
    }

    /**
     * Clears the internal text buffer. (Sets it to empty strings)
     */
    private void clearBuffer()
    {
        for (int _i = 0; _i < this.signTextLines.length; _i++)
        {
            this.signTextLines[_i] = "";
        }
    }

    /**
     * Resets line enabled states to enabled.
     */
    private void resetStates()
    {
        for (int _i = 0; _i < this.signLinesEnabled.length; _i++)
        {
            this.signLinesEnabled[_i] = true;
        }
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName("Sign Overwrite Brush");

        vm.custom(ChatColor.BLUE + "Buffer text: ");
        for (int _i = 0; _i < this.signTextLines.length; _i++)
        {
            vm.custom((this.signLinesEnabled[_i] ? ChatColor.GREEN + "(E): " : ChatColor.RED + "(D): ") + ChatColor.BLACK + this.signTextLines[_i]);
        }

        vm.custom(ChatColor.BLUE + String.format("Ranged mode is %s", ChatColor.GREEN + (rangedMode ? "enabled" : "disabled")));
        if (rangedMode)
        {
            vm.size();
            vm.height();
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return SignOverwriteBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int timesUsed)
    {
        SignOverwriteBrush.timesUsed = timesUsed;
    }

}
