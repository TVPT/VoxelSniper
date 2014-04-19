package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * @author Sushen117
 * 
 * This brush behaves just like an ErodeBrush, immediately followed by a BlendBallBrush.
 * It's result is much nicer than the ErodeBrush alone, and allows a very good sculpting experience. 
 */

public class ErodeExtendedBrush extends Brush {

	private BlendBallBrush blendBallBrush;
	private ErodeBrush erodeBrush;
	
	public ErodeExtendedBrush() 
	{
		this.setName("Erode Extended");
		
		//prepare our internal brushes
		blendBallBrush = new BlendBallBrush();
		erodeBrush = new ErodeBrush();
		erodeBrush.setName(this.getName());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.erode";
	}

	@Override
	public void info(Message vm) {
		erodeBrush.info(vm);
	}
	
	@Override
    public final void parameters(final String[] par, final SnipeData v)
    {
		erodeBrush.parameters(par, v);
		blendBallBrush.parameters(par, v);
    }
	
	
	@Override
    protected final void powder(final SnipeData v)
    {
		//apply Erode first
		erodeBrush.setTargetBlock(this.getTargetBlock());
		erodeBrush.powder(v);
		
		//then BlendBall
		blendBallBrush.setTargetBlock(this.getTargetBlock());
		blendBallBrush.arrow(v); //not an error
    }
	
	@Override
    protected final void arrow(final SnipeData v)
    {
		//apply Erode first
		erodeBrush.setTargetBlock(this.getTargetBlock());
		erodeBrush.arrow(v);
		
		//then BlendBall
		blendBallBrush.setTargetBlock(this.getTargetBlock());
		blendBallBrush.arrow(v);
    }
	
}
