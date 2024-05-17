package org.beetl.sql.jmh.dbvisitor;

import lombok.SneakyThrows;
import net.hasor.dbvisitor.dal.mapper.BaseMapper;
import net.hasor.dbvisitor.dal.session.DalSession;
import net.hasor.dbvisitor.jdbc.core.JdbcTemplate;
import net.hasor.dbvisitor.lambda.LambdaTemplate;
import net.hasor.dbvisitor.page.Page;
import net.hasor.dbvisitor.page.PageObject;
import org.beetl.sql.jmh.base.BaseService;
import org.beetl.sql.jmh.base.DataSourceHelper;
import org.beetl.sql.jmh.beetl.vo.BeetlSQLSysUser;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DBVisitorService implements BaseService {
	JdbcTemplate jdbcTemplate;
	LambdaTemplate lambdaTemplate;

	private static AtomicInteger idGen = new AtomicInteger(1000);

	BaseMapper<DBVisitorSysUser> baseMapper;
	@SneakyThrows
	public DBVisitorService(){
		DataSource dataSource = DataSourceHelper.newDatasource();
		jdbcTemplate = new JdbcTemplate(dataSource);
		lambdaTemplate = new LambdaTemplate(dataSource);
		DalSession session = new DalSession(dataSource);
		baseMapper = session.createBaseMapper(DBVisitorSysUser.class);

	}
	@SneakyThrows
	@Override
	public void addEntity() {
		DBVisitorSysUser beetlSQLSysUser = new DBVisitorSysUser();
		beetlSQLSysUser.setId(idGen.getAndIncrement());
		beetlSQLSysUser.setCode("abc");
		beetlSQLSysUser.setCode1("abc");
		beetlSQLSysUser.setCode2("abc");
		beetlSQLSysUser.setCode3("abc");
		beetlSQLSysUser.setCode4("abc");
		beetlSQLSysUser.setCode5("abc");
		beetlSQLSysUser.setCode6("abc");
		beetlSQLSysUser.setCode7("abc");
		beetlSQLSysUser.setCode8("abc");
		beetlSQLSysUser.setCode9("abc");
		beetlSQLSysUser.setCode10("abc");
		beetlSQLSysUser.setCode11("abc");
		beetlSQLSysUser.setCode12("abc");
		beetlSQLSysUser.setCode13("abc");
		beetlSQLSysUser.setCode14("abc");
		beetlSQLSysUser.setCode15("abc");
		beetlSQLSysUser.setCode16("abc");
		beetlSQLSysUser.setCode17("abc");
		beetlSQLSysUser.setCode18("abc");
		beetlSQLSysUser.setCode19("abc");
		beetlSQLSysUser.setCode20("abc");
		lambdaTemplate.lambdaInsert(DBVisitorSysUser.class).applyEntity(beetlSQLSysUser).executeSumResult();
	}

	@Override
	public Object getEntity() {
		return baseMapper.selectById(1);
	}

	@Override
	@SneakyThrows
	public void lambdaQuery() {
		List<DBVisitorSysUser> list =lambdaTemplate.lambdaQuery(DBVisitorSysUser.class)
			.eq(DBVisitorSysUser::getId,1).queryForList();
	}

	@Override
	@SneakyThrows
	public void executeJdbcSql() {
		String sql =  "select * from sys_user where id = ?";
		Object[] queryArg = new Object[] { 1 };
		DBVisitorSysUser user = jdbcTemplate.queryForList(sql,queryArg,DBVisitorSysUser.class).get(0);
	}

	@Override
	@SneakyThrows
	public void executeTemplateSql() {
		String querySql = "select * from sys_user where id > :id";
		Map<String, Object> queryArg = Collections.singletonMap("id", 1);
		DBVisitorSysUser user = jdbcTemplate.queryForList(querySql,queryArg,DBVisitorSysUser.class).get(0);
	}

	@Override
	public void sqlFile() {
		//必须是spring环境才能用sqlfile？
		throw new UnsupportedOperationException();
	}

	@Override
	public void one2Many() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SneakyThrows
	public void pageQuery() {
		Page pageInfo = new PageObject();
		pageInfo.setPageSize(5);
		pageInfo.setCurrentPage(1);
		List<DBVisitorSysUser> pageData1 = lambdaTemplate.lambdaQuery(DBVisitorSysUser.class)
			.usePage(pageInfo)
			.queryForList();

	}

	@Override
	public void complexMapping() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SneakyThrows
	public void getAll() {
		//TODO 没有找到内置
		String querySql = "select * from sys_user ";
		List<DBVisitorSysUser> users = jdbcTemplate.queryForList(querySql,new Object[0],DBVisitorSysUser.class);

	}
}
