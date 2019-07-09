package org.beetl.sql.core.engine;

import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLSource;

import java.io.Reader;
import java.io.StringReader;

public class SqlTemplateResource extends Resource {


	SQLSource source;

	public SqlTemplateResource(String id, SQLSource source, ResourceLoader loader) {
		super(id, loader);
		this.source = source;

	}

	@Override
	public Reader openReader() {
		return new StringReader(source.getTemplate());
	}

	@Override
	public boolean isModified() {
		StringSqlTemplateLoader l = (StringSqlTemplateLoader) this.resourceLoader;
		SQLLoader loader = l.getSqlLLoader();
		SQLSource newSource = loader.getSQL(source.getId());
		if (newSource == null) {
			return true;
		}
		return source.getVersion().isModified(newSource.getVersion());
	}

	public String getTemplate() {
		return source.getTemplate();
	}

	public int getLine() {
		return source.getLine();
	}


}
