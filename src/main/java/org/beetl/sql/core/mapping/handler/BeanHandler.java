package org.beetl.sql.core.mapping.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapping.BasicRowProcessor;
import org.beetl.sql.core.mapping.ResultSetHandler;
import org.beetl.sql.core.mapping.RowProcessor;

/**  
 * 将rs处理为Pojo
 * @author: suxj  
 */
public class BeanHandler<T> implements ResultSetHandler<T> {
	

	
	private final Class<T> type;
	private final RowProcessor convert;
	private boolean uniqueCheck = false ;

	
	public BeanHandler(Class<T> type ,NameConversion nc,SQLManager sm) {
        this(type, new BasicRowProcessor(nc,sm));
    }
	
	public BeanHandler(Class<T> type ,NameConversion nc,SQLManager sm,boolean uniqueCheck) {
        this(type, new BasicRowProcessor(nc,sm));
        this.uniqueCheck = uniqueCheck;
    }
	
	protected BeanHandler(Class<T> type, RowProcessor convert) {
        this.type = type;
        this.convert = convert;
    }

	@Override
	public T handle(ResultSet rs) throws SQLException {
		if(rs.next()){
			return this.convert.toBean(rs, this.type);
		}else{
			if(this.uniqueCheck){
				throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR, "unique查询，但数据库未找到结果集");
			}else{
				return null;
			}
		}
	}

}
