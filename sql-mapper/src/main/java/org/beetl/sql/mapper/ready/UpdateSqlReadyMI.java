package org.beetl.sql.mapper.ready;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;

import java.lang.reflect.Method;

/**
 * <pre>{@code
 * @Sql("update user set status=? where id=?")
 * public void update(Integer status,Integer id)
 * }</pre>
 * @author xiandafu
 */
public class UpdateSqlReadyMI extends BaseSqlReadyMI {

    public UpdateSqlReadyMI(String sql){
        this.sql = sql;
    }
    @Override
    public Object call(SQLManager sm, Class entityClass,  Method m, Object[] args) {
        SQLReady sqlReady = new SQLReady(this.getSql(),args);
        return sm.executeUpdate(sqlReady);
    }
}
