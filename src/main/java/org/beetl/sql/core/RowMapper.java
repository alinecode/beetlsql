package org.beetl.sql.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/** 用来映射额外属性，如一对一
 * @author xiandafu
 *
 */
public interface RowMapper<T> {
	
	/**
	 * 
	 * @MethodName: mapRow   
	 * @Description: 扩展处理  
	 * @param  obj 正常处理后的对象
	 * @param  rs 结果集
	 * @param  rowNum 处理的记录位置(第几条记录)：可以只针对某一条记录做特殊处理
	 * @param 
	 * @throws SQLException  
	 * @return T  
	 */
	 T mapRow(Object obj,ResultSet rs, int rowNum) throws SQLException;
}