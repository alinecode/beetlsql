package org.beetl.sql.core.engine;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.loader.SQLLoader;

public class StringSqlTemplateLoader implements ResourceLoader<SqlId> {
	SQLLoader sqlLoader;
	boolean autoCheck = true;

	public StringSqlTemplateLoader(SQLLoader sqlLoader) {
		this.sqlLoader = sqlLoader;
	}

	@Override
	public Resource getResource(SqlId id) {
		SQLSource source = sqlLoader.querySQL(id);
		return new SqlTemplateResource(id, source, this);
	}

	@Override
	public boolean isModified(Resource key) {
		return key.isModified();

	}

	@Override
	public boolean exist(SqlId key) {
		return sqlLoader.exist(key);
	}

	@Override
	public void close() {
		//never use
	}

	@Override
	public void init(GroupTemplate gt) {
		//never use
	}

	@Override
	public SqlId getResourceId(Resource resource, SqlId key) {
		//never use
		return null;
	}


	protected SQLLoader getSqlLLoader() {
		return sqlLoader;
	}

	@Override
	public String getInfo() {
		return sqlLoader.toString();
	}


}
