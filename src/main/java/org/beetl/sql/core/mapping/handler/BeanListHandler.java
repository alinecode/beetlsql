package org.beetl.sql.core.mapping.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.mapping.BasicRowProcessor;
import org.beetl.sql.core.mapping.ResultSetHandler;
import org.beetl.sql.core.mapping.RowProcessor;

/**  
 *  将rs处理为Pojo集合  
 * @author: suxj  
 */
public class BeanListHandler<T> implements ResultSetHandler<List<T>> {

    private final Class<T> type;
    private final RowProcessor convert;

    public BeanListHandler(Class<T> type) {
        this(type, BeanHandler.BASIC_ROW_PROCESSOR);
    }
    
    public BeanListHandler(Class<T> type ,NameConversion nc) {
        this(type, new BasicRowProcessor(nc));
    }

    public BeanListHandler(Class<T> type, RowProcessor convert) {
        this.type = type;
        this.convert = convert;
    }

    @Override
    public List<T> handle(ResultSet rs) throws SQLException {
        return this.convert.toBeanList(rs, type);
    }
}
