package org.beetl.sql.core.engine;

import java.io.Reader;
import java.io.StringReader;

import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLSource;

public class SqlTemplateResource extends Resource {

	String template = null;
	int line = 0;
	public SqlTemplateResource(String id, SQLSource source,ResourceLoader loader)
	{
		super(id,loader);
		this.template = source.getTemplate() ;
		this.line = source.getLine();
	}
	@Override
	public Reader openReader() {
		return new StringReader(template);
	}

	@Override
	public boolean isModified() {
		StringSqlTemplateLoader l = (StringSqlTemplateLoader)this.resourceLoader;
		SQLLoader loader = l.getSqlLLoader();
		return loader.isModified(id);
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}

}
