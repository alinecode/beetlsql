package org.beetl.sql.springboot.threadlocal;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.ext.DebugInterceptor;

public class DatasourceDebugInterceptor extends DebugInterceptor {
    protected String formatSqlId(ExecuteContext executeContext){
        SqlId id = executeContext.sqlId;
        String str = id.toString();
        HikariDataSource source = (HikariDataSource)executeContext.sqlManager.getDs().getMasterSource();
        String  name = source.getPoolName();
        String sql = name+"->"+formatSql(str);
        if(sql.length()>50){
            return sql.substring(0,50)+"...";
        }else{
            return sql;
        }

    }
}
