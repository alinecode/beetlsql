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
 * 将rs处理为List&lt;Map&lt;String ,Object&gt;&gt;  
 * @author: suxj  
 */
public class MapListHandler implements ResultSetHandler<java.util.List<java.util.Map<String, Object>>> {
	
	private final RowProcessor convert;
	
	
	
	public MapListHandler(NameConversion nc,SQLManager sm) {
        this(new BasicRowProcessor(nc,sm));
    }
	
	protected  MapListHandler(RowProcessor convert) {
        super();
        this.convert = convert;
    }

	@Override
	public java.util.List<java.util.Map<String, Object>> handle(ResultSet rs) throws SQLException {
		
		java.util.List<java.util.Map<String, Object>> rows = new java.util.ArrayList<java.util.Map<String, Object>>();
        while (rs.next()) {
            rows.add(this.convert.toMap(rs,Map.class));
        }
        return rows;
        
	}

}
