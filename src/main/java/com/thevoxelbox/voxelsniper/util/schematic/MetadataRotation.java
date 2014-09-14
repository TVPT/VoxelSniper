package com.thevoxelbox.voxelsniper.util.schematic;

import java.util.HashMap;
import java.util.Map;

public abstract class MetadataRotation
{
    
    private static final Map<Integer, MetadataRotation> standards = new HashMap<Integer, MetadataRotation>();
    
    private static final MetadataRotation STAIR_STANDARD = new StairRotation();
    private static final MetadataRotation DIODE_STANDARD = new FullSimpleRotation();
    private static final MetadataRotation BED_STANDARD = new BedRotation();
    private static final MetadataRotation SIMPLE_STANDARD = new SimpleRotation();
    private static final MetadataRotation MID_SWAP_STANDARD = new MidSwapRotation();
    private static final MetadataRotation WIERD_DIRECTIONAL_STANDARD = new WierdDirectionalRotation();
    private static final MetadataRotation PISTON_STANDARD = new PistonRotation();
    private static final MetadataRotation SIGN_STANDARD = new SignRotation();
    private static final MetadataRotation RAIL_STANDARD = new RailSpecialRotation();
    private static final MetadataRotation RAIL2_STANDARD = new RailNormalRotation();
    private static final MetadataRotation LEVER_STANDARD = new LeverRotation();
    private static final MetadataRotation TORCH_STANDARD = new TorchRotation();
    private static final MetadataRotation VINE_STANDARD = new VineRotation();
    
    static
    {
        standards.put(53, STAIR_STANDARD);
        standards.put(67, STAIR_STANDARD);
        standards.put(108, STAIR_STANDARD);
        standards.put(109, STAIR_STANDARD);
        standards.put(114, STAIR_STANDARD);
        standards.put(128, STAIR_STANDARD);
        standards.put(134, STAIR_STANDARD);
        standards.put(135, STAIR_STANDARD);
        standards.put(136, STAIR_STANDARD);
        standards.put(156, STAIR_STANDARD);
        standards.put(163, STAIR_STANDARD);
        standards.put(164, STAIR_STANDARD);

        standards.put(93, DIODE_STANDARD);
        standards.put(94, DIODE_STANDARD);
        standards.put(149, DIODE_STANDARD);
        standards.put(107, DIODE_STANDARD);
        standards.put(145, DIODE_STANDARD);

        standards.put(26, BED_STANDARD);

        standards.put(86, SIMPLE_STANDARD);
        standards.put(91, SIMPLE_STANDARD);
        standards.put(64, SIMPLE_STANDARD);
        standards.put(71, SIMPLE_STANDARD);

        standards.put(17, MID_SWAP_STANDARD);
        standards.put(162, MID_SWAP_STANDARD);

        standards.put(54, WIERD_DIRECTIONAL_STANDARD);
        standards.put(130, WIERD_DIRECTIONAL_STANDARD);
        standards.put(23, WIERD_DIRECTIONAL_STANDARD);
        standards.put(158, WIERD_DIRECTIONAL_STANDARD);
        standards.put(65, WIERD_DIRECTIONAL_STANDARD);
        standards.put(68, WIERD_DIRECTIONAL_STANDARD);

        standards.put(29, PISTON_STANDARD);
        standards.put(33, PISTON_STANDARD);

        standards.put(63, SIGN_STANDARD);

        standards.put(66, RAIL2_STANDARD);
        standards.put(27, RAIL_STANDARD);
        standards.put(28, RAIL_STANDARD);
        standards.put(157, RAIL_STANDARD);

        standards.put(69, LEVER_STANDARD);

        standards.put(50, TORCH_STANDARD);
        standards.put(75, TORCH_STANDARD);
        standards.put(76, TORCH_STANDARD);
        standards.put(77, TORCH_STANDARD);
        standards.put(143, TORCH_STANDARD);

        standards.put(106, VINE_STANDARD); 
    }
    
    protected MetadataRotation()
    {
        
    }
    
    public static byte getData(int id, byte current, int degrees)
    {
        degrees = degrees % 360;
        if(degrees < 0) degrees += 360;
        MetadataRotation rot = standards.get(id);
        if(rot == null) return current;
        for(int i = 0; i < ((degrees + 45)/90)%4; i++)
        {
            current = rot.getData(current);
        }
        return current;
    }
    
    protected abstract byte getData(byte current);
}

class StairRotation extends MetadataRotation
{
    @Override
    protected byte getData(byte current)
    {
        if(current == 0) current = 2;
        if(current == 1) current = 3;
        if(current == 2) current = 1;
        if(current == 3) current = 0;
        if(current == 4) current = 6;
        if(current == 5) current = 7;
        if(current == 6) current = 5;
        if(current == 7) current = 4;
        return current;
    }
}

class SimpleRotation extends MetadataRotation
{
    @Override
    protected byte getData(byte current)
    {
        if (current == 2) current = 3;
        if (current == 3) current = 0;
        if (current == 0) current = 1;
        if (current == 1) current = 2;
        return current;
    }
}

class BedRotation extends SimpleRotation
{
    @Override
    protected byte getData(byte current)
    {
        current = super.getData(current);
        if (current == 8) current = 9;
        if (current == 9) current = 10;
        if (current == 10) current = 11;
        if (current == 11) current = 8;
        return current;
    }
}

class FullSimpleRotation extends BedRotation
{
    @Override
    protected byte getData(byte current)
    {
        current = super.getData(current);
        if (current == 4) current = 5;
        if (current == 5) current = 6;
        if (current == 6) current = 7;
        if (current == 7) current = 4;
        if (current == 12) current = 13;
        if (current == 13) current = 14;
        if (current == 14) current = 15;
        if (current == 15) current = 12;
        return current;
    }
}

class MidSwapRotation extends MetadataRotation
{
    @Override
    protected byte getData(byte current)
    {
        if (current == 8) current = 4;
        if (current == 4) current = 8;
        if (current == 9) current = 5;
        if (current == 5) current = 9;
        if (current == 10) current = 6;
        if (current == 6) current = 10;
        if (current == 11) current = 7;
        if (current == 7) current = 11;
        return current;
    }
}

class WierdDirectionalRotation extends MetadataRotation
{
    @Override
    protected byte getData(byte current)
    {
        if (current == 2) current = 5;
        if (current == 5) current = 3;
        if (current == 3) current = 4;
        if (current == 4) current = 2;
        return current;
    }
}

class PistonRotation extends WierdDirectionalRotation
{
    @Override
    protected byte getData(byte current)
    {
        current = super.getData(current);
        if (current == 13) current = 11;
        if (current == 11) current = 12;
        if (current == 12) current = 10;
        if (current == 10) current = 13;
        return current;
    }
}

class SignRotation extends MetadataRotation
{
    @Override
    protected byte getData(byte current)
    {
        current = (byte) ((current + 4) % 16);
        return current;
    }
}

class RailSpecialRotation extends WierdDirectionalRotation
{
    @Override
    protected byte getData(byte current)
    {
        current = super.getData(current);
        if (current == 0) current = 1;
        if (current == 1) current = 0;
        if (current == 6) current = 7;
        if (current == 7) current = 6;
        
        if (current == 10) current = 13;
        if (current == 13) current = 11;
        if (current == 11) current = 12;
        if (current == 12) current = 10;

        if (current == 8) current = 9;
        if (current == 9) current = 8;
        if (current == 14) current = 15;
        if (current == 15) current = 14;
        return current;
    }
}

class RailNormalRotation extends MetadataRotation
{
    @Override
    protected byte getData(byte current)
    {
        if (current == 6) current = 7;
        if (current == 7) current = 8;
        if (current == 8) current = 9;
        if (current == 9) current = 6;
        if (current == 0) current = 1;
        if (current == 1) current = 0;
        return current;
    }
}

class LeverRotation extends MetadataRotation
{
    @Override
    protected byte getData(byte current)
    {
        if (current == 9) current = 11;
        if (current == 11) current = 10;
        if (current == 10) current = 12;
        if (current == 12) current = 9;
        if (current == 2) current = 4;
        if (current == 4) current = 1;
        if (current == 1) current = 3;
        if (current == 3) current = 2;
        if (current == 5) current = 14;
        if (current == 14) current = 13;
        if (current == 13) current = 6;
        if (current == 6) current = 5;
        if (current == 7) current = 8;
        if (current == 8) current = 15;
        if (current == 15) current = 0;
        if (current == 0) current = 7;
        return current;
    }
}

class TorchRotation extends MetadataRotation
{
    @Override
    protected byte getData(byte current)
    {
        if (current == 2) current = 4;
        if (current == 4) current = 1;
        if (current == 1) current = 3;
        if (current == 3) current = 2;
        return current;
    }
}

class VineRotation extends MetadataRotation
{
    @Override
    protected byte getData(byte current)
    {
        current = (byte) (((current << 1) & 0xE) | ((current & 0x8) >> 3));
        return current;
    }
}

class HatchRotation extends MetadataRotation
{
    @Override
    protected byte getData(byte current)
    {
        if (current == 3) current = 1;
        if (current == 1) current = 2;
        if (current == 2) current = 0;
        if (current == 0) current = 3;
        if (current == 10) current = 8;
        if (current == 8) current = 11;
        if (current == 11) current = 9;
        if (current == 9) current = 10;
        return current;
    }
}
