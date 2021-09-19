package org.beetl.sql.ext;

import org.beetl.sql.core.IDAutoGen;

import java.util.UUID;

/**
 * uuid,基于版本uuid 版本3,但从36位压缩压到22位，如 b4YELwjlM7KN9Xd7WbxXV
 * <pre>{code
 * @AssingId("uuid22")
 * String id;
 * }</pre>
 *
 * 第三方类库提供了uuid time版本，比如
 * <pre>
 *     <dependency>
 *   	<groupId>com.fasterxml.uuid</groupId>
 *   	<artifactId>java-uuid-generator</artifactId>
 *   	<version>4.0.1</version>
 * 	</dependency>
 * </pre>
 *
 * <pre>
 *   NoArgGenerator timeBasedGenerator = Generators.timeBasedGenerator();
 *   String timeUUID = timeBasedGenerator.generate());
 * </pre>
 *
 * 你可以自定义一个基于时间的uuid 生成器
 *
 * 必须调用{@link org.beetl.sql.core.SQLManager#addIdAutoGen} 来使用
 * @see org.beetl.sql.annotation.entity.AssignID
 * @author xiandafu
 */
public class UUIDAutoGen22 implements IDAutoGen<String> {
	@Override
	public String nextID(String params) {

		String compressedUUID  = RadixUtil.randomUUID();
		return compressedUUID;
	}

	public static  void main(String[] args){
		System.out.println(new UUIDAutoGen22().nextID(""));
	}
}
class RadixUtil {


	final static char[] DIGIT = {
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9'
	};

	final static int RADIX = DIGIT.length;

	/**
	 * 将长整型数值转换为指定的进制数（最大支持62进制）
	 */
	public static String to62String(long i) {
		StringBuilder sb = new StringBuilder(32);
		while (i >= RADIX) {
			sb.append(DIGIT[(int) (i % RADIX)]);
			i = i / RADIX;
		}
		sb.append(DIGIT[(int) (i)]);
		return sb.reverse().toString();
	}

	public static String randomUUID(){
		UUID uuid = UUID.randomUUID();
		long most = Math.abs(uuid.getMostSignificantBits());
		long least = Math.abs(uuid.getLeastSignificantBits());
		StringBuilder sb24 = new StringBuilder();
		sb24.append(to62String(most)).append(to62String(least));
		return sb24.toString();
	}





}
