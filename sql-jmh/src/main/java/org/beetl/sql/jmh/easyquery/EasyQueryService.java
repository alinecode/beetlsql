package org.beetl.sql.jmh.easyquery;

import com.easy.query.api4j.client.DefaultEasyQuery;
import com.easy.query.api4j.client.EasyQuery;
import com.easy.query.core.api.client.EasyQueryClient;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.bootstrapper.EasyQueryBootstrapper;
import com.easy.query.core.configuration.nameconversion.NameConversion;
import com.easy.query.core.configuration.nameconversion.impl.DefaultNameConversion;
import com.easy.query.core.configuration.nameconversion.impl.UnderlinedNameConversion;
import com.easy.query.h2.config.H2DatabaseConfiguration;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.jmh.BaseService;
import org.beetl.sql.jmh.DataSourceHelper;
import org.beetl.sql.jmh.beetl.BeetlSQLUserMapper;
import org.beetl.sql.jmh.beetl.vo.BeetlSqlSysCustomerView;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * create time 2023/8/3 08:34
 * 文件说明
 *
 * @author xuejiaming
 */
public class EasyQueryService implements BaseService {
	EasyQueryClient easyQueryClient = null;
	EasyQuery easyQuery = null;
	AtomicInteger idGen = new AtomicInteger(1000);
	public void init(){
		DataSource dataSource = DataSourceHelper.ins();
		easyQueryClient = EasyQueryBootstrapper.defaultBuilderConfiguration()
			.setDefaultDataSource(dataSource)
			.optionConfigure(op -> {
				op.setPrintSql(false);
			})
			.useDatabaseConfigure(new H2DatabaseConfiguration())
			.replaceService(NameConversion.class, UnderlinedNameConversion.class)
			.build();
		easyQuery = new DefaultEasyQuery(easyQueryClient);
	}
	@Override
	public void addEntity() {
		EasyQuerySysUser easyQuerySysUser = new EasyQuerySysUser();
		easyQuerySysUser.setId(idGen.getAndIncrement());
		easyQuerySysUser.setCode("abc");
		easyQuery.insertable(easyQuerySysUser).executeRows();
	}

	@Override
	public Object getEntity() {
		return easyQueryClient.queryable(EasyQuerySysUser.class).whereById(1).firstOrNull();
	}

	@Override
	public void lambdaQuery() {

		List<EasyQuerySysUser> list = easyQuery.queryable(EasyQuerySysUser.class).whereById(1).toList();
	}

	@Override
	public void executeJdbcSql() {
		List<EasyQuerySysUser> easyQuerySysUsers = easyQuery.sqlQuery("select * from sys_user where id = ?", EasyQuerySysUser.class, Collections.singletonList(1));
	}

	@Override
	public void executeTemplateSql() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sqlFile() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void one2Many() {
		EasyQuerySysCustomer sysUser = easyQuery.queryable(EasyQuerySysCustomer.class)
			.whereById(1)
			.include(o -> o.many(EasyQuerySysCustomer::getOrder))
			.firstOrNull();
		int size = sysUser.getOrder().size();
	}

	@Override
	public void pageQuery() {
		EasyPageResult<EasyQuerySysUser> pageResult = easyQuery.queryable(EasyQuerySysUser.class)
			.where(o->o.eq(EasyQuerySysUser::getCode,"用户一"))
			.toPageResult(1, 5);
	}

	@Override
	public void complexMapping() {

		EasyQuerySysCustomer easyQuerySysCustomer = easyQuery.queryable(EasyQuerySysCustomer.class)
			.include(o -> o.many(EasyQuerySysCustomer::getOrder))
			.whereById(1)
			.select(EasyQuerySysCustomer.class, o -> o.columnAll().columnIncludeMany(EasyQuerySysCustomer::getOrder, EasyQuerySysCustomer::getOrder))
			.firstOrNull();
		easyQuerySysCustomer.getOrder().get(0);

	}
}
