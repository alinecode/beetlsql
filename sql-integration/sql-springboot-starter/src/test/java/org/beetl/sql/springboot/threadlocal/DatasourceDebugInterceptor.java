package org.beetl.sql.springboot.threadlocal;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.ext.DebugInterceptor;

/**
 * 日志输出增加一个数据源前缀，以观察多数据据情况，ThreadLocalSqlManager使用了
 * 正确的数据源
 */
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
