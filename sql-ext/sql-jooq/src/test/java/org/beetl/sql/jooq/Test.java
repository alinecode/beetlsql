package org.beetl.sql.jooq;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.codegen.GenerationTool;
import org.jooq.impl.DSL;
import org.jooq.meta.jaxb.Configuration;

import javax.sql.DataSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.jooq.impl.DSL.field;


public class Test {
	public static void main(String[] args) throws Exception {
		SQLManager sqlManager = getSQLManager();
		DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");
		InputStream ins = Test.class.getResourceAsStream("/jooq-config.xml");

		Configuration configuration = new Configuration();
//
//		Configuration configuration = GenerationTool.load(ins);
//		configuration.getGenerator().getDatabase().wi
		DSLContext create = DSL.using(dataSource.getConnection(), SQLDialect.H2);
		Result<?> result = create.select()
			.from("department").where(field("id").eq(1))
			.fetch();
		System.out.println(result);
	}
	private  static SQLManager getSQLManager(){
		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style());
		builder.setProduct(false);
		SQLManager sqlManager = builder.build();
		return sqlManager;
	}

	static DataSource dataSource = datasource();
	private static DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDriverClassName("org.h2.Driver");

		return ds;
	}
}
