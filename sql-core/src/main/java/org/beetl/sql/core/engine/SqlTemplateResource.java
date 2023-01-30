package org.beetl.sql.core.engine;

import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.loader.SQLLoader;

import java.io.IOException;
import java.io.Reader;

public class SqlTemplateResource extends Resource<SqlId> {

	int line = 0;

	public SqlTemplateResource(SqlId id, ResourceLoader loader) {
		super(id, loader);

	}

	@Override
	public Reader openReader() {
		StringSqlTemplateLoader l = (StringSqlTemplateLoader) this.resourceLoader;
		SQLLoader loader = l.getSqlLLoader();

		SQLSource newResource = loader.loadSQL(id);
		if(newResource==null){
			//loadSQL会返回空，但beetlsql调用到这里的时候，先调用了SQLManager.getScript,在那里先过滤一次了
			throw new IllegalArgumentException("should not hapeen here.");
		}
		this.line = newResource.getLine();
		return new NoneBlockStringReader(newResource.getTemplate());
	}

	@Override
	public boolean isModified() {

		StringSqlTemplateLoader l = (StringSqlTemplateLoader) this.resourceLoader;
		SQLLoader loader = l.getSqlLLoader();
		return loader.isModified(id);

	}

	public int getLine() {
		return this.line;
	}


}


