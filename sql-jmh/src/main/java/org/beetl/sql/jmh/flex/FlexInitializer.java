package org.beetl.sql.jmh.flex;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.beetl.sql.jmh.DataSourceHelper;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


import static org.beetl.sql.jmh.flex.table.Tables.FLEX_SYS_USER;

/**
 * 如果有疑问，欢迎PR，我对flex不熟
 */
public class FlexInitializer {

	private static SqlSessionFactory sqlSessionFactory;
	private static AtomicInteger id = new AtomicInteger(1000);


	private static FlexUserMapper flexUserMapper;

	public static void init() {
		DataSource dataSource = DataSourceHelper.ins();
		MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance().setDataSource(dataSource)
			.addMapper(FlexUserMapper.class).start();
		flexUserMapper = bootstrap.getMapper(FlexUserMapper.class);

	}


	public static FlexSysUser selectOne() {
		return flexUserMapper.selectOneById(1);
	}

	public static void insert() {

		FlexSysUser flexSysUser = new FlexSysUser();
		flexSysUser.setId(id.incrementAndGet());
		flexSysUser.setCode("abc");
		flexSysUser.setCode1("abc");
		flexSysUser.setCode2("abc");
		flexSysUser.setCode3("abc");
		flexSysUser.setCode4("abc");
		flexSysUser.setCode5("abc");
		flexSysUser.setCode6("abc");
		flexSysUser.setCode7("abc");
		flexSysUser.setCode8("abc");
		flexSysUser.setCode9("abc");
		flexSysUser.setCode10("abc");
		flexSysUser.setCode11("abc");
		flexSysUser.setCode12("abc");
		flexSysUser.setCode13("abc");
		flexSysUser.setCode14("abc");
		flexSysUser.setCode15("abc");
		flexSysUser.setCode16("abc");
		flexSysUser.setCode17("abc");
		flexSysUser.setCode18("abc");
		flexSysUser.setCode19("abc");
		flexSysUser.setCode20("abc");
		flexUserMapper.insert(flexSysUser);

	}


	public static Page<FlexSysUser> paginate() {
		QueryWrapper queryWrapper = new QueryWrapper().where(FLEX_SYS_USER.CODE.eq("用户一"));
		return flexUserMapper.paginate(1, 5, queryWrapper);
	}

	public static List<FlexSysUser> lambdaQuery() {
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.where(FLEX_SYS_USER.ID.eq(1));
		return flexUserMapper.selectListByQuery(queryWrapper);
	}


	public static List<FlexSysUser> getAll() {
		return flexUserMapper.selectAll();
	}


	public static void executeJdbcSql() {
		throw new UnsupportedOperationException();
	}


	public static void executeTemplateSql() {
		throw new UnsupportedOperationException();
	}


	public static void sqlFile() {
		throw new UnsupportedOperationException();
	}


	public static void one2Many() {
		throw new UnsupportedOperationException();
	}


	public static void complexMapping() {
		throw new UnsupportedOperationException();
	}


}
