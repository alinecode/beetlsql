package org.beetl.sql.postgres;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.PostgresStyle;
import org.beetl.sql.ext.DBInitHelper;
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
		DBInitHelper.executeSqlScript(sqlManager,"create.sql");
		Set<String> set = sqlManager.getMetaDataManager().allTable();
		System.out.println(set);
		JsonDataEntity jsonDataEntity = new JsonDataEntity();
		jsonDataEntity.setId("2");
		jsonDataEntity.setCreateTs(System.currentTimeMillis());
		jsonDataEntity.setJsonData(new Color("a","b"));
		sqlManager.insert(jsonDataEntity);
		jsonDataEntity = sqlManager.unique(JsonDataEntity.class,"2");
		jsonDataEntity.setJsonData(null);
		sqlManager.updateById(jsonDataEntity);



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
