package org.beetl.sql.core.kit;

public class StringKit {
	
	// 首字母转小写
	public static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toLowerCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}

	// 首字母转大写
	public static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toUpperCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}

	// 大写字母前面加上下划线并转为全小写
	public static String enCodeUnderlined(String s) {
		char[] chars = toLowerCaseFirstOne(s).toCharArray();
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < chars.length; i++) {
			if(Character.isUpperCase(chars[i])){
				temp.append("_");
			}
			temp.append(Character.toLowerCase(chars[i]));
		}
		return temp.toString();
	}
	
	// 删除下划线并转把后一个字母转成大写
	public static String deCodeUnderlined(String str) {
		
		String[] splitArr = str.split("_");
		StringBuilder sb = new StringBuilder();
		
		for(int i=0 ;i<splitArr.length ;i++){
			if(i == 0){
				sb.append(splitArr[0].toLowerCase());
				continue;
			}
			
			sb.append(toUpperCaseFirstOne(splitArr[i].toLowerCase()));
		}
		
		return sb.toString();
	}
	
	
	/**
	 * 去空格 
	 * @param str
	 * @return
	 */
	public static String trimAllWhitespace(String str) {
		if (!((CharSequence) str != null && ((CharSequence) str).length() > 0)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		int index = 0;
		while (sb.length() > index) {
			if (Character.isWhitespace(sb.charAt(index))) {
				sb.deleteCharAt(index);
			}
			else {
				index++;
			}
		}
		return sb.toString();
	}
	
	
	
	
	public static void main(String[] args) {
//		System.out.println(deCodeUnderlined("USER_NAME"));
		System.out.println(trimAllWhitespace(" fsdfsd sdfds fsd "));
	}
	
}
