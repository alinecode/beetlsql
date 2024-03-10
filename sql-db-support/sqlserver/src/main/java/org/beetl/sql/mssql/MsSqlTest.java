package org.beetl.sql.mssql;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.db.SqlServerStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;

public class MsSqlTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new SqlServerStyle());
        SQLManager sqlManager = builder.build();
		sqlManager.single(MsSqlUser.class,1);

    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:sqlserver://127.0.0.1:1433;databaseName=test;encrypt=false");
        ds.setUsername("sa");
        ds.setPassword("Xdf780214!");
//        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }
}
