package org.beetl.sql.core.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**  
 * rs处理器接口  
 * @author: suxj  
 */
public interface RowProcessor {
	
	<T> T toBean(ResultSet rs, Class<T> type) throws SQLException;
	
	<T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException;

	Map<String, Object> toMap(ResultSet rs, Class<?> type) throws SQLException;
}
