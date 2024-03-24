package org.beetl.sql.ymtraix;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.PostgresStyle;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.util.Set;

public class PostgresTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new PostgresStyle());
        SQLManager sqlManager = builder.build();
		Set<String> set = sqlManager.getMetaDataManager().allTable();
		System.out.println(set);


//		sqlManager.updateById(jsonDataEntity);

//		JsonDataEntity jsonDataEntity = sqlManager.single(JsonDataEntity.class,"a");

    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://127.0.0.1:5432/postgres");
        ds.setUsername("postgres");
        ds.setPassword("12345678");
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }
}
