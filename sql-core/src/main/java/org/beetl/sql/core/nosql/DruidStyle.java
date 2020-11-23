package org.beetl.sql.core.nosql;

import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.QuerySQLExecutor;
import org.beetl.sql.core.SQLExecutor;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.range.RangeSql;

/**
 * 不支持翻页，不支持PreparedStatement
 * @author xiandafu
 * @see " * https://druid.apache.org/docs/latest/querying/sql.html#jdbc"
 */
public class DruidStyle extends AbstractDBStyle {

    public DruidStyle() {
        super();

    }

    @Override
    public int getIdType(Class c, String idProperty) {
        //只支持assign
        return DBType.ID_ASSIGN;
    }

    @Override
    public String getName() {
        return "druid";
    }

    @Override
    public int getDBType() {
        return DBType.DB_DRUID;
    }


    @Override
    public boolean preparedStatementSupport() {
        return false;
    }


    @Override
    public RangeSql getRangeSql(){
        throw new UnsupportedOperationException("druid 不支持offset");
    }

    @Override
    public SQLExecutor buildExecutor(ExecuteContext executeContext){
        return new QuerySQLExecutor(executeContext);
    }
}