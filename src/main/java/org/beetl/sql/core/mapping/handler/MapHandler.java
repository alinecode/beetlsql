package org.beetl.sql.core.mapping.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapping.BasicRowProcessor;
import org.beetl.sql.core.mapping.ResultSetHandler;
import org.beetl.sql.core.mapping.RowProcessor;


/**  
 * 将rs处理为Map&lt;String ,Object&gt;  
 * @author: suxj  
 */
public class MapHandler implements ResultSetHandler<java.util.Map<String ,Object>> {
	
	private final RowProcessor convert;
	
	
	
	public MapHandler(NameConversion nc,SQLManager sm){
		this(new BasicRowProcessor(nc,sm));
	}
	
	public MapHandler(RowProcessor convert){
		super();
		this.convert = convert;
	}

	@Override
	public java.util.Map<String, Object> handle(ResultSet rs) throws SQLException {
		return rs.next() ? this.convert.toMap(rs,Map.class) : null;
	}

}
