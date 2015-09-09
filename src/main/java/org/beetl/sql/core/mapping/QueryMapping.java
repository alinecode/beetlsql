package org.beetl.sql.core.mapping;

import java.sql.SQLException;

/**  
 * @author: suxj  
 */
public class QueryMapping {
	
	private QueryMapping(){}
	private static QueryMapping mapping = new QueryMapping();
	public static QueryMapping getInstance(){
		return mapping;
	}

	/**
	 * 过不同的处理器将rs处理为需要的类型  
	 * @param rs  rs 结果
	 * @param rsh rsh 结果处理器
	 * @return
	 */
	public <T> T query(java.sql.ResultSet rs ,ResultSetHandler<T> rsh){
		try {
			return rsh.handle(rs);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
