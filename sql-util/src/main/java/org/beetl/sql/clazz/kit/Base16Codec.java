package org.beetl.sql.clazz.kit;

public class Base16Codec {
	public static final Base16Codec CODEC_LOWER = new Base16Codec(true);
	public static final Base16Codec CODEC_UPPER = new Base16Codec(false);
	
	private final char[] alphabets;

	/**
	 * 构造
	 *
	 * @param lowerCase 是否小写
	 */
	public Base16Codec(boolean lowerCase) {
		this.alphabets = (lowerCase ? "0123456789abcdef" : "0123456789ABCDEF").toCharArray();
	}
	
	public byte[] decode(CharSequence encoded) {
		if (StringKit.isEmpty(encoded)) {
			return null;
		}

		encoded = StringKit.trim(encoded);
		int len = encoded.length();

		if ((len & 0x01) != 0) {
			// 如果提供的数据是奇数长度，则前面补0凑偶数
			encoded = "0" + encoded;
			len = encoded.length();
		}

		final byte[] out = new byte[len >> 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(encoded.charAt(j), j) << 4;
			j++;
			f = f | toDigit(encoded.charAt(j), j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}

		return out;
	}
	
	/**
	 * 将十六进制字符转换成一个整数
	 *
	 * @param ch    十六进制char
	 * @param index 十六进制字符在字符数组中的位置
	 * @return 一个整数
	 * @throws UtilException 当ch不是一个合法的十六进制字符时，抛出运行时异常
	 */
	private static int toDigit(char ch, int index) {
		int digit = Character.digit(ch, 16);
		if (digit < 0) {
			throw new RuntimeException("Illegal hexadecimal character "+ch+"at index "+index);
		}
		return digit;
	}
}
