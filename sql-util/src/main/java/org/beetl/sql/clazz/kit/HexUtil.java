package org.beetl.sql.clazz.kit;

public class HexUtil {
	/**
	 * 将十六进制字符串解码为byte[]
	 *
	 * @param hexStr 十六进制String
	 * @return byte[]
	 */
	public static byte[] decodeHex(String hexStr) {
		return decodeHex((CharSequence) hexStr);
	}
	
	/**
	 * 将十六进制字符数组转换为字节数组
	 *
	 * @param hexData 十六进制字符串
	 * @return byte[]
	 * @throws UtilException 如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
	 * @since 5.6.6
	 */
	public static byte[] decodeHex(CharSequence hexData) {
		return Base16Codec.CODEC_LOWER.decode(hexData);
	}
}
