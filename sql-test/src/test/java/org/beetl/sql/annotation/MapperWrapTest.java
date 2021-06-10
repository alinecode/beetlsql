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


    }

	/**
	 * 能执行sql前执行Datasource指定的类
	 */
	public  static  interface  UserMapper<User>{
		@Sql("select * from sys_user where id=? ")
		@Datasource("crm1")
		User selectById(Integer id);
	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.METHOD})
	@MapperWrapper(DataSourceChange.class)
	public @interface Datasource {
		String value();
	}

	public static class DataSourceChange implements MapperWrapperExecutor{

		@Override
		public void before(SQLManager sqlManager,Annotation annotation) {
			Datasource datasource = (Datasource)annotation;
			System.out.println("sql数据源切换"+ datasource.value());
		}
	}




}