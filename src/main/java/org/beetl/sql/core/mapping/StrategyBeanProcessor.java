/**  
 * 文件名:    StrategyBeanProcessor.java  
 * 描述:      
 * 作者:      suxj
 * 版本:      1.0  
 * 创建时间:  2015年8月2日 下午2:29:30  
 *  
 * 修改历史:  
 * 日期                          作者           版本         描述  
 * ------------------------------------------------------------------  
 * 2015年8月2日        suxj     1.0     1.0 Version  
 */ 
package org.beetl.sql.core.mapping;

import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**  
 * 扩展：废弃
 *  策略模式的BeanProcessor，用来扩展Pojo字段和数据库字段的映射  
 * @author: suxj  
 */
public class StrategyBeanProcessor extends BeanProcessor {
	
	private Matcher matcher;
	
	//默认是驼峰映射
	public StrategyBeanProcessor(){
		matcher = new HumpMatcher();
	}
	
	public StrategyBeanProcessor(Matcher matcher){
		this.matcher = matcher;
	}

	@Override
	protected int[] mapColumnsToProperties(Class<?> c,ResultSetMetaData rsmd,
			PropertyDescriptor[] props) throws SQLException {

		if(matcher == null) throw new IllegalStateException("Matcher must be setted!");
		
		int cols = rsmd.getColumnCount();
		int[] columnToProperty = new int[cols + 1];
		java.util.Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);
		
		for (int col = 1; col <= cols; col++) {
			String columnName = rsmd.getColumnLabel(col);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(col);
			}
			
			for (int i = 0; i < props.length; i++) {
				if (matcher.match(columnName, props[i].getName())) {//这里是一个扩展点，用来扩展pojo属性到数据库字段的映射
					columnToProperty[col] = i;
					break;
				}
			}
		}
		return columnToProperty;
		
	}
	
	

}
