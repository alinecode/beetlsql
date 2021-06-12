package org.beetl.sql.annotation;

import org.beetl.sql.BaseTest;
import org.beetl.sql.mapper.DefaultMapperBuilder;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.mapper.proxy.MapperProxy;
import org.beetl.sql.mapper.proxy.MapperProxyExecutor;
import org.beetl.sql.mapper.proxy.MapperProxyConfigBuilder;
import org.beetl.sql.mapper.proxy.ProxyContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.annotation.*;

public class MapperWrapTest extends BaseTest {

    @BeforeClass
    public static void init() {
        initTable(testSqlFile);
    }

    @Test
    public void wrapperTest() {
		UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
		userMapper.selectById(1);

		userMapper.selectById2(1);

    }

	/**
	 * 能执行sql前执行Datasource指定的类
	 */
	public  static  interface  UserMapper<User>{

		@Sql("select * from sys_user1 where id=? ")
		@Datasource("crm1")
		User selectById(Integer id);


		@Sql("select * from sys_user where id=? ")
		@Log()
		User selectById2(Integer id);


	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.METHOD})
	@MapperProxy(DataSourceExecutor.class)
	public @interface Datasource {
		String value();
	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.METHOD})
	@MapperProxy(LogExecutor.class)
	public @interface Log {
		String value() default "";
	}



	public static class DataSourceExecutor implements MapperProxyExecutor {

		@Override
		public void before(ProxyContext context) {
			Datasource datasource = (Datasource)context.getConfig();
			System.out.println("sql数据源切换"+ datasource.value());
		}
	}

	public static class LogExecutor implements MapperProxyExecutor {

		@Override
		public Object after(ProxyContext context,Object ret) {
			System.out.println("log "+ret);
			return ret;
		}
	}




}