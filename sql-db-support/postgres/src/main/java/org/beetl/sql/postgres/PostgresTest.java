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
		sqlManager.deleteById(JsonDataEntity.class,"3");

		JsonDataEntity jsonDataEntity = new JsonDataEntity();
		jsonDataEntity.setId("3");
		Color color = new Color();
		color.setAb("3433");
		jsonDataEntity.setJsonData(color);
		sqlManager.updateTemplateById(jsonDataEntity);


		JsonDataEntity jsonDataEntity4 = new JsonDataEntity();
		jsonDataEntity.setId("4");
		jsonDataEntity.setJsonData(color);
		sqlManager.insertTemplate(jsonDataEntity);

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
