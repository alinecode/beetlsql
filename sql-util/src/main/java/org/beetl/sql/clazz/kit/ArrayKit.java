package org.beetl.sql.clazz.kit;

import java.util.Arrays;

/**
 * 数组工具类
 *
 * @author liumin
 */
public class ArrayKit {

	/**
	 * 判断数组是否为空
	 *
	 * @param array 数组
	 * @return boolean
	 */
	public static boolean isEmpty(Object[] array) {
		return null == array || array.length == 0;
	}

	/**
	 * 判断数组是否不为空
	 *
	 * @param array 数组
	 * @return boolean
	 */
	public static boolean isNotEmpty(Object[] array) {
		return !isEmpty(array);
	}


	/**
	 * 将多个数组拼接成一个数组
	 *
	 * @param first 第一个数组
	 * @param rest  剩余数组
	 * @param <T>   数组类型
	 * @return 拼接后的数组
	 */
	public static <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/**
	 * 将多个数组拼接成一个数组
	 *
	 * @param first 第一个数组
	 * @param rest  剩余数组
	 * @return 拼接后的数组
	 */
	public static int[] concatAll(int[] first, int[]... rest) {
		int totalLength = first.length;
		for (int[] array : rest) {
			totalLength += array.length;
		}
		int[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (int[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
}
