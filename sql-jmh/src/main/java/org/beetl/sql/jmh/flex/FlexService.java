package org.beetl.sql.jmh.flex;

import org.beetl.sql.jmh.base.BaseService;

public class FlexService implements BaseService {
	@Override
	public void addEntity() {
		FlexInitializer.insert();
	}

	@Override
	public Object getEntity() {
		return FlexInitializer.selectOne();
	}

	@Override
	public void lambdaQuery() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void executeJdbcSql() {
		FlexInitializer.executeJdbcSql();
	}

	@Override
	public void executeTemplateSql() {
		FlexInitializer.executeTemplateSql();
	}

	@Override
	public void sqlFile() {
		FlexInitializer.sqlFile();
	}

	@Override
	public void one2Many() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void pageQuery() {
		FlexInitializer.paginate();
	}

	@Override
	public void complexMapping() {
		FlexInitializer.complexMapping();
	}

	@Override
	public void getAll() {
		FlexInitializer.getAll();
	}
}
