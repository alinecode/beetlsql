package org.beetl.sql.core.engine;

import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.loader.SQLLoader;

import java.io.IOException;
import java.io.Reader;

public class SqlTemplateResource extends Resource<SqlId> {

	SQLSource newResource;

	public SqlTemplateResource(SqlId id, ResourceLoader loader) {
		super(id, loader);
		StringSqlTemplateLoader l = (StringSqlTemplateLoader) loader;
		SQLLoader sqlLoader = l.getSqlLLoader();
		newResource = sqlLoader.loadSQL(id);

	}

	@Override
	public Reader openReader() {

		if(newResource==null){
			//loadSQL会返回空，但beetlsql调用到这里的时候，先调用了SQLManager.getScript,在那里先过滤一次了
			throw new IllegalArgumentException("should not happen here.");
		}
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
		return newResource!=null?newResource.line:0;
	}


}


