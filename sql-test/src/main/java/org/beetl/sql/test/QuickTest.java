package org.beetl.sql.test;


import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

/**
 * 内部测试新功能或者bug用，所有单元测试参考test目录
 * @author xiandafu
 *
 */

public class QuickTest {
	private static   DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDriverClassName("org.h2.Driver");
		return ds;
	}
	private  static SQLManager getSQLManager(){
		DataSource dataSource = datasource();
		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style());
		SQLManager sqlManager = builder.build();
		return sqlManager;
	}

    public static void main(String[] args) throws Exception {
        SQLManager sqlManager = getSQLManager();

        DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");
		Set<String> all =  sqlManager.getMetaDataManager().allTable();
		List<MyUser> myUsers = sqlManager.all(MyUser.class);
		System.out.println(myUsers.size());
    }

}
