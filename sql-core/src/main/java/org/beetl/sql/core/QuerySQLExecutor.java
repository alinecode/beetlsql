package org.beetl.sql.core;

import org.beetl.sql.core.db.KeyHolder;

import java.util.List;

/**
 * 用于查询引擎，只支持查询
 *
 * @author xiandafu
 * @see BaseStatementOnlySQLExecutor
 */
public class QuerySQLExecutor extends BaseSQLExecutor {

    public QuerySQLExecutor(ExecuteContext executeContext) {
        super(executeContext);
    }

    @Override
    public int insert(Class target, Object paras){
        throw new UnsupportedOperationException("Query only support");
    }


    @Override
    public List<Object[]> insert(Class target, Object paras, String[] cols){
        throw new UnsupportedOperationException("Query only support");
    }


    @Override
    public int update(Class target, Object obj){
        throw new UnsupportedOperationException("Query only support");
    }

    @Override
    public int[] updateBatch(List<?> list){
        throw new UnsupportedOperationException("Query only support");
    }

    @Override
    public int[] updateBatch(Class<?> target , List<?> list){
        throw new UnsupportedOperationException("Query only support");
    }

    @Override
    public int[] insertBatch(Class<?> target, List<?> list){
        throw new UnsupportedOperationException("Query only support");
    }


    @Override
    public int deleteById(Class<?> target, Object objId){
        throw new UnsupportedOperationException("Query only support");
    }

    @Override
    public int sqlReadyExecuteUpdate(SQLReady p){
        throw new UnsupportedOperationException("Query only support");
    }

    @Override
    public int[] sqlReadyBatchExecuteUpdate(SQLBatchReady batch){
        throw new UnsupportedOperationException("Query only support");
    }
}
