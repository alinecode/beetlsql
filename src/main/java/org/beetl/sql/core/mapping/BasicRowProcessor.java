/**  
 * 文件名:    BasicRowProcessor.java  
 * 描述:      
 * 作者:      suxj
 * 版本:      1.0  
 * 创建时间:  2015年8月2日 上午12:35:15  
 *  
 * 修改历史:  
 * 日期                          作者           版本         描述  
 * ------------------------------------------------------------------  
 * 2015年8月2日        suxj     1.0     1.0 Version  
 */ 
package org.beetl.sql.core.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLManager;

/**  
 * 基础rs处理器  
 * @author: suxj  
 */
public class BasicRowProcessor implements RowProcessor {
	
//	private static final BeanProcessor defaultConvert = new BeanProcessor();
	private final BeanProcessor convert;
	
//	public BasicRowProcessor(){
//		this(defaultConvert);
//	}
	
	public BasicRowProcessor(NameConversion nc,SQLManager sm){
		this(new BeanProcessor(nc,sm));
	}
	
	public BasicRowProcessor(BeanProcessor convert){
		super();
		this.convert = convert;
	}

	@Override
	public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
		return this.convert.toBean(rs, type);
	}

	@Override
	public <T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException {
		return this.convert.toBeanList(rs, type);
	}

	@Override
	public Map<String, Object> toMap(ResultSet rs,Class<?> type) throws SQLException {
        return this.convert.toMap(type,rs);
	}
	
}
