package com.thevoxelbox.voxelsniper.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Simple static class that provides a utility method to md5-hash a string.
 * 
 * @author Monofraps
 * 
 */
public final class HashHelperMD5 {

	/**
	 * Creates MD5 hash of a given string.
	 * 
	 * @param str
	 *            The string to hash
	 * @return the md5 hash as string
	 */
	public final static String hash(String str) {

		MessageDigest _md5 = null;
		StringBuffer _sbMD5sum = new StringBuffer();
		byte[] _digest = null;

		try {
			_md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException _ex) {
			_ex.printStackTrace();
			return "";
		}

		_md5.reset();
		_md5.update(str.getBytes());
		str = "";

		_digest = _md5.digest();

		for (byte b : _digest) {
			_sbMD5sum.append(Integer.toHexString((b & 0xFF) | 0x100).toLowerCase().substring(1, 3));
		}

		return _sbMD5sum.toString();
	}
}
