package org.beetl.sql.postgres;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.PostgresStyle;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;

public class PostgresTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new PostgresStyle());
        SQLManager sqlManager = builder.build();

		MyView view = sqlManager.single(MyView.class,"1");
		System.out.println(view);

    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://127.0.0.1:5455/postgres");
        ds.setUsername("postgresUser");
        ds.setPassword("postgresPW");
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }
}
