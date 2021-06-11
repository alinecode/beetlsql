package org.beetl.sql.annotation;

import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.entity.User;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.DefaultMapperBuilder;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.mapper.wrapper.MapperWrapper;
import org.beetl.sql.mapper.wrapper.MapperWrapperExecutor;
import org.beetl.sql.mapper.wrapper.WrapperConfigBuilder;
import org.beetl.sql.mapper.wrapper.WrapperContext;
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
		((DefaultMapperBuilder)sqlManager.getMapperBuilder()).setMapperConfig(new WrapperConfigBuilder());
		UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
		userMapper.selectById(1);

		userMapper.selectById2(1);

    }

	/**
	 * 能执行sql前执行Datasource指定的类
	 */
	public  static  interface  UserMapper<User>{

		@Sql("select * from sys_user where id=? ")
		@Datasource("crm1")
		User selectById(Integer id);


		@Sql("select * from sys_user where id=? ")
		@Log()
		User selectById2(Integer id);


	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.METHOD})
	@MapperWrapper(DataSourceExecutor.class)
	public @interface Datasource {
		String value();
	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.METHOD})
	@MapperWrapper(LogExecutor.class)
	public @interface Log {
		String value() default "";
	}



	public static class DataSourceExecutor implements MapperWrapperExecutor{

		@Override
		public void before(WrapperContext context) {
			Datasource datasource = (Datasource)context.getConfig();
			System.out.println("sql数据源切换"+ datasource.value());
		}
	}

	public static class LogExecutor implements MapperWrapperExecutor{

		@Override
		public void after(WrapperContext context,Object ret) {
			System.out.println("日志输出:"+ret);
		}
	}




}