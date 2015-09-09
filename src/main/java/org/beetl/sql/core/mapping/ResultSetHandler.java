package org.beetl.sql.core.mapping;

import java.sql.SQLException;

/**  
 * handler接口  
 * @author: suxj  
 */
public interface ResultSetHandler<T> {

	T handle(java.sql.ResultSet rs) throws SQLException;
}
