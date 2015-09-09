package org.beetl.sql.core.mapping;

/**  
 * 扩展：废弃
 * 二维数组映射  
 * @author: suxj  
 */
public class ArrayMatcher implements Matcher {
	
	private java.util.Map<String ,String> map = null;
	
	public ArrayMatcher(String[][] mappingArr){
		
		if(mappingArr == null) throw new IllegalArgumentException("[][] cannot null");
		
		map = new java.util.HashMap<String ,String>();
		for(int i=0 ;i<mappingArr.length ;i++){
			String columnName = mappingArr[i][0];
			if(columnName != null) map.put(columnName.toUpperCase(), mappingArr[i][1]);
		}
		
	}
	
	@Override
	public boolean match(String columnName, String propertyName) {

		if(columnName == null) return false;
		
		String pname = map.get(columnName.toUpperCase());
		if(pname == null) 
			return false;
		else
			return pname.equals(propertyName);
	}
	
//	public static void main(String[] args) {
//		String[][] arr = {{"tId" ,"id"},{"table_name","tableName"},{"table_age","tableAge"}};
//		
//		ArrayMatcher matcher = new ArrayMatcher(arr);
//		System.out.println(matcher.match("tId", "id"));
//	}

}
