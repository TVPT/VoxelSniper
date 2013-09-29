/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Voxel
 */
public class VoxelList
{

    private List<int[]> col = new ArrayList<int[]>();

    /**
     * @param i
     */
    public final void add(final int[] i)
    {
    	if(i[1] == -1) {
    		if (!col.contains(i))
            {
	    		for(Iterator<int[]> it = col.iterator(); it.hasNext();) {
	    			int[] in = it.next();
	    			if(in[0] == i[0]) {
	    				it.remove();
	    			}
	    		}
	    		col.add(i);
            }
    	} else {
    		if (!col.contains(i))
            {
                col.add(i);
            }
    	}
    }

    /**
     * @param i
     *
     * @return
     */
    public final boolean removeValue(final int[] i)
    {
        if (col.isEmpty())
        {
            return false;
        }
        else
        {
        	boolean ret = false;
        	if(i[1] == -1) {
        		for(Iterator<int[]> it = col.iterator(); it.hasNext();) {
        			int[] in = it.next();
        			if(in[0] == i[0]) {
        				it.remove();
        				ret = true;
        			}
        		}
        	} else {
        		ret = col.remove(i);
        	}
            return ret;
        }
    }

    /**
     * @param i
     *
     * @return
     */
    public final boolean contains(final int[] i)
    {
    	for(Iterator<int[]> it = col.iterator(); it.hasNext();) {
    		int[] in = it.next();
    		if(in[0] == i[0] && (in[1] == i[1] || in[1] == -1)) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * clears the voxelList
     */
    public final void clear()
    {
        col.clear();
    }
    
	public boolean isEmpty() {
		return col.isEmpty();
	}

	public List<int[]> getList() {
		return col;
	}



}
