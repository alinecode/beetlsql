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

    public static void init() {
        DataSource dataSource = DataSourceHelper.ins();
        MybatisFlexBootstrap.getInstance()
                .setDataSource(dataSource)
                .addMapper(FlexUserMapper.class)
                .start();

        sqlSessionFactory = MybatisFlexBootstrap.getInstance().getSqlSessionFactory();
    }


    public static FlexSysUser selectOne() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FlexUserMapper mapper = sqlSession.getMapper(FlexUserMapper.class);
			return mapper.selectOneById(1);
        }
    }

	public static void insert() {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			FlexUserMapper mapper = sqlSession.getMapper(FlexUserMapper.class);
			FlexSysUser flexSysUser = new FlexSysUser();
			flexSysUser.setId(id.incrementAndGet());
			flexSysUser.setCode("abc");
			mapper.insert(flexSysUser);
		}
	}



    public static Page<FlexSysUser> paginate() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FlexUserMapper mapper = sqlSession.getMapper(FlexUserMapper.class);
			QueryWrapper queryWrapper = new QueryWrapper()
				.where(FLEX_SYS_USER.CODE.eq("用户一"));
            return mapper.paginate(1, 5, queryWrapper);
        }
    }

	public static List<FlexSysUser> lambdaQuery() {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			FlexUserMapper mapper = sqlSession.getMapper(FlexUserMapper.class);
			QueryWrapper queryWrapper = new QueryWrapper();
			queryWrapper.where(FLEX_SYS_USER.ID.eq(1));
			return mapper.selectListByQuery(queryWrapper);
		}
	}


	public static List<FlexSysUser> getAll() {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			FlexUserMapper mapper = sqlSession.getMapper(FlexUserMapper.class);
			return mapper.selectAll();
		}
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
