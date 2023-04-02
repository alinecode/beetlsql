package org.beetl.sql.core.engine;

import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.loader.SQLLoader;

import java.io.IOException;
import java.io.Reader;

public class SqlTemplateResource extends Resource<SqlId> {

	public SqlTemplateResource(SqlId id, ResourceLoader loader) {
		super(id, loader);
	}

	@Override
	public Reader openReader() {

		StringSqlTemplateLoader l = (StringSqlTemplateLoader) resourceLoader;
		SQLLoader sqlLoader = l.getSqlLLoader();
		SQLSource newResource = sqlLoader.loadSQL(id);
		return new NoneBlockStringReader(newResource.getTemplate());
	}

	@Override
	public boolean isModified() {

		StringSqlTemplateLoader l = (StringSqlTemplateLoader) this.resourceLoader;
		SQLLoader loader = l.getSqlLLoader();
		boolean isModified =  loader.isModified(id);
		return isModified;

	}

	public int getLine() {
		StringSqlTemplateLoader l = (StringSqlTemplateLoader) resourceLoader;
		SQLLoader sqlLoader = l.getSqlLLoader();
		SQLSource newResource = sqlLoader.querySQL(id);
		return newResource.line;
	}


}


