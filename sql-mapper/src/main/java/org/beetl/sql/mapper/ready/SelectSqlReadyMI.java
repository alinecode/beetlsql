package org.beetl.sql.mapper.ready;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;

import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * <pre>{@code
 * @Sql("select * from user where name=?")
 * public List<User> queryName(String name);
 * }
 * </pre>
 * @author xiandafu
 */
public class SelectSqlReadyMI extends BaseSqlReadyMI {
    boolean isSingle = false;
    public SelectSqlReadyMI(String sql, Class targetType, boolean isSingle){
        this.sql = sql;
        this.targetType = targetType;
        this.isSingle = isSingle;
    }

    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        SQLReady sqlReady = new SQLReady(getSqId(sm,entityClass,m),this.getSql(),args);
        List list =  sm.execute(sqlReady,this.getTargetType());
        if(isSingle){
            return list.isEmpty()?null:list.get(0);
        }else{
            return list;
        }
    }
}
