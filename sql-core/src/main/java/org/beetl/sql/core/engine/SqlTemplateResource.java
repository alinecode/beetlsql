package org.beetl.sql.core.engine;

import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.loader.SQLFileVersion;
import org.beetl.sql.core.loader.SQLLoader;

import java.io.IOException;
import java.io.Reader;

public class SqlTemplateResource extends Resource<SqlId> {
	SQLFileVersion sqlFileVersion = null;

	public SqlTemplateResource(SqlId id, ResourceLoader loader) {
		super(id, loader);
	}

	@Override
	public Reader openReader() {
		SQLSource newResource = getSQLLoader().loadSQL(id);
		this.sqlFileVersion = newResource.getVersion();
		return new NoneBlockStringReader(newResource.getTemplate());
	}

	@Override
	public boolean isModified() {
		if(sqlFileVersion==null){
			return true;
		}
		SQLSource newResource = getSQLLoader().querySQL(id);
		boolean isModified =  newResource.getVersion().isModified(sqlFileVersion);
		return isModified;

	}

	public int getLine() {
		SQLSource newResource = getSQLLoader().querySQL(id);
		return newResource.line;
	}

	protected SQLLoader getSQLLoader(){
		StringSqlTemplateLoader l = (StringSqlTemplateLoader) this.resourceLoader;
		SQLLoader sqlLoader = l.getSqlLLoader();
		return sqlLoader;
	}


}


