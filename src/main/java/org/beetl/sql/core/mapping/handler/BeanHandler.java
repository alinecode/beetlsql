package org.beetl.sql.core.mapping.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.mapping.BasicRowProcessor;
import org.beetl.sql.core.mapping.ResultSetHandler;
import org.beetl.sql.core.mapping.RowProcessor;

/**  
 * 将rs处理为Pojo
 * @author: suxj  
 */
public class BeanHandler<T> implements ResultSetHandler<T> {
	
	//默认处理器
	static final RowProcessor BASIC_ROW_PROCESSOR = new BasicRowProcessor();
	
	private final Class<T> type;
	private final RowProcessor convert;
	
	public BeanHandler(Class<T> type) {
        this(type, BASIC_ROW_PROCESSOR);
    }
	
	public BeanHandler(Class<T> type ,NameConversion nc) {
        this(type, new BasicRowProcessor(nc));
    }
	
	public BeanHandler(Class<T> type, RowProcessor convert) {
        this.type = type;
        this.convert = convert;
    }

	@Override
	public T handle(ResultSet rs) throws SQLException {
		return rs.next() ? this.convert.toBean(rs, this.type) : null;
	}

}
