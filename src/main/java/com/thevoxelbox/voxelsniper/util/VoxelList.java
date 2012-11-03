/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.util;

/**
 * 
 * @author Voxel
 */
public class VoxelList {

	private int[] col = new int[100];
	private int vir = 0;

	/**
	 * 
	 * @param i
	 */
	public final void add(final int i) {
		if (!contains(i)) {
			col[vir++] = i;
		}
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public final boolean removeValue(final int i) {
		if (isEmpty()) {
			return false;
		} else {
			return removeFrom(getIndexOf(i));
		}
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public final boolean removeFrom(final int i) {
		if (i >= 0 && i < vir) {
			for (int x = i; x < vir; x++) {
				col[x] = col[x + 1];
			}
			vir--;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public final boolean contains(final int i) {
		if (isEmpty()) {
			return false;
		} else {
			for (int x = 0; x < vir; x++) {
				if (col[x] == i) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public final boolean isEmpty() {
		return vir == 0;
	}

	/**
	 * 
	 */
	public final void clear() {
		vir = 0;
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public final int getIndexOf(final int i) {
		if (isEmpty()) {
			return -1;
		} else {
			for (int x = 0; x < vir; x++) {
				if (col[x] == i) {
					return x;
				}
			}
			return -1;
		}
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public final int getFrom(final int i) {
		if (i >= 0 && i < vir) {
			return col[i];
		} else {
			return -1;
		}
	}

	/**
	 * 
	 * @return
	 */
	public final VoxIterator getIterator() {
		return new VoxIterator(col, vir);
	}

	/**
	 * 
	 * @author Voxel
	 *
	 */
	public class VoxIterator {

		private int[] col;
		private int vir;
		private int cur = 0;

		/**
		 * 
		 * @param collection
		 * @param virtual
		 */
		public VoxIterator(int[] collection, int virtual) {
			col = collection;
			vir = virtual;
		}

		/**
		 * 
		 * @return
		 */
		public final boolean hasNext() {
			return cur < vir;
		}

		/**
		 * 
		 * @return
		 */
		public final int next() {
			return col[cur++];
		}
	}
}
