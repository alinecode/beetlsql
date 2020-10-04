package org.beetl.sql.mapper.ready;

import org.beetl.sql.core.SQLBatchReady;
import org.beetl.sql.core.SQLManager;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 批量更新
 * <pre>{@code
 *
 * @Sql("update user set status=? where id=?)
 * public int[] update(List<Object[]> paras)
 *
 * }</pre>
 *
 * @author xiandafu
 */
public class BatchSqlReadyMI extends BaseSqlReadyMI {

    public BatchSqlReadyMI(String sql){
        this.sql = sql;
    }
    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        if(!(args[0] instanceof  List)){
            throw new UnsupportedOperationException("期望第一个参数是List<Object[]>类型");
        }
        List<Object[]> list = (List)args[0];
        SQLBatchReady sqlBatchReady = new SQLBatchReady(this.getSql(),list);
        return sm.executeBatchUpdate(sqlBatchReady);
    }
}
