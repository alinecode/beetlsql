package org.beetl.sql.core.mapping;

/**  
 * 扩展：废弃
 * @author: suxj  
 */
public class HumpMatcher implements Matcher {

	@Override
	public boolean match(String columnName, String propertyName) {
		
		if(columnName == null) return false;
		
		columnName = columnName.toLowerCase();
		String[] array = columnName.split("_");
		StringBuilder sb = new StringBuilder();
		
		for(int i=0 ;i<array.length ;i++){
			String str = array[i];
			if(!"".equals(str) && i>0){
				StringBuilder sb1 = new StringBuilder();
				str = sb1.append(str.substring(0,1).toUpperCase()).append(str.substring(1)).toString();
			}
			sb.append(str);
		}
		return sb.toString().equals(propertyName);

	}
	
//	public static void main(String[] args) {
//		HumpMatcher hump = new HumpMatcher();
//		System.out.println(hump.match("table_name", "tableName"));
//	}

}
